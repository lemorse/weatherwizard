package chartview.routing.enveloppe.custom;

import astro.calc.GeoPoint;
import astro.calc.GreatCircle;

import chart.components.ui.ChartPanel;

import chartview.util.grib.GribHelper;

import chartview.gui.toolbar.controlpanels.LoggingPanel;

import chartview.routing.polars.PolarHelper;

import chartview.util.WWGnlUtilities;
import chartview.ctx.WWContext;

import chartview.gui.right.CommandPanel;
import chartview.gui.util.dialog.WhatIfRoutingPanel;

import chartview.util.grib.GribHelper.GribCondition;
import chartview.util.grib.GribHelper.GribConditionData;

import chartview.util.progress.ProgressMonitor;

import java.awt.Point;
import java.awt.Polygon;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import java.util.List;

import javax.swing.JOptionPane;

public class RoutingUtil
{
  public static final int REAL_ROUTING    = 0;
  public static final int WHAT_IF_ROUTING = 1;
  
  private static RoutingPoint finalDestination      = null;
  private static GribHelper.GribConditionData[] wgd = null;
  private static double timeStep                    = 0D;
  
  private static GreatCircle gc = new GreatCircle();
  private static RoutingPoint closest      = null;
  private static RoutingPoint finalClosest = null;
  
  private static int brg = 0;
  
  private static double smallestDist = Double.MAX_VALUE;
  
  private static boolean interruptRouting = false;

  private static int getBearing(RoutingPoint center)
  {
    int brg = 0;
    gc.setStart(new GeoPoint(Math.toRadians(center.getPosition().getL()),
                             Math.toRadians(center.getPosition().getG())));
    gc.setArrival(new GeoPoint(Math.toRadians(finalDestination.getPosition().getL()),
                               Math.toRadians(finalDestination.getPosition().getG())));
//  gc.calculateGreatCircle(10);
//  double gcDistance = Math.toDegrees(gc.getDistance() * 60D);
    gc.calculateRhumLine();
    double rlZ = gc.getRhumbLineRoute();
    brg = (int)Math.round(Math.toDegrees(rlZ));
    return brg;
  }

  public static List<List<RoutingPoint>> calculateIsochrons(RoutingClientInterface caller, 
                                                            ChartPanel chartPanel,
                                                            RoutingPoint startFrom,
                                                            RoutingPoint destination,
                                                            List<RoutingPoint> intermediateWP,
                                                            Date fromDate,
                                                            GribHelper.GribConditionData[] gribData,
                                                            double timeInterval,
                                                            int routingForkWidth,
                                                            int routingStep,
                                                            int maxTWS,
                                                            int minTWA,
                                                            boolean stopIfGRIB2old,
                                                            double speedCoeff)
  {
    wgd              = gribData;
    finalDestination = destination; // By default
    timeStep         = timeInterval;
    closest          = null;
    finalClosest     = null;
    
    RoutingPoint center = startFrom;
    
    int nbIntermediateIndex = 0;
    if (intermediateWP != null)
      finalDestination = intermediateWP.get(nbIntermediateIndex++);
    
    double gcDistance = 0D;
    
//  System.out.println("Starting routing from " + center.getPosition().toString() + " to " + destination.getPosition().toString());

    // Calcutate bearing to detination (from start)
    brg = getBearing(center);
    
    List<List<RoutingPoint>> allIsochrons = new ArrayList<List<RoutingPoint>>();
    
    // Initialization
    interruptRouting = false;
    timer = System.currentTimeMillis();
    
    smallestDist = Double.MAX_VALUE;
    List<List<RoutingPoint>> data = new ArrayList<List<RoutingPoint>>(1);
    ArrayList<RoutingPoint> one = new ArrayList<RoutingPoint>(1);
    center.setDate(fromDate);
    GribHelper.GribCondition wind = GribHelper.gribLookup(center.getPosition(), wgd, fromDate);
    boolean keepLooping = true;
    boolean interruptedBecauseTooOld = false;
    if (wind != null && wind.comment != null && wind.comment.equals("TOO_OLD"))
    {
      center.setGribTooOld(true);
//    System.out.println("Stop if GRIB too old:" + stopIfGRIB2old);
      if (stopIfGRIB2old)
      {
        keepLooping = false;
        interruptedBecauseTooOld = true;
        WWContext.getInstance().fireLogging("Routing aborted. GRIB exhausted (preference).\n", LoggingPanel.YELLOW_STYLE);
      }
    }
    one.add(center); // Initialize data with the center. One point only.
    data.add(one);
    
    Date currentDate = fromDate; // new Date(fromDate.getTime() + (long)(timeStep * 3600D * 1000D));
    Date arrivalDate = null;
//  synchronized (allIsochrons)
    {
      // Start from "center"
      while (keepLooping && !interruptRouting)
      {
//      timer = logDiffTime(timer, "Milestone 1");
        double localSmallOne = Double.MAX_VALUE;
        List<List<RoutingPoint>> temp = new ArrayList<List<RoutingPoint>>();
        Iterator<List<RoutingPoint>> dimOne = data.iterator();
        int nbNonZeroSpeed = 0;
        boolean allowOtherRoute = false;
        long before = System.currentTimeMillis();
        while (!interruptRouting && dimOne.hasNext() && keepLooping)
        {
//        timer = logDiffTime(timer, "Milestone 2");
          List<RoutingPoint> curve = dimOne.next();
          Iterator<RoutingPoint> dimTwo = curve.iterator();
          nbNonZeroSpeed = 0;
          while (!interruptRouting && keepLooping && dimTwo.hasNext())
          {
//          timer = logDiffTime(timer, "Milestone 3");
            RoutingPoint newCurveCenter = dimTwo.next();
            List<RoutingPoint> oneCurve = new ArrayList<RoutingPoint>(10);
            
            wind = GribHelper.gribLookup(newCurveCenter.getPosition(), wgd, currentDate);
            if (wind != null && wind.comment != null && wind.comment.equals("TOO_OLD"))
            {
              center.setGribTooOld(true);
//            System.out.println("Stop if GRIB too old:" + stopIfGRIB2old);
              if (stopIfGRIB2old)
              {
                keepLooping = false;
                interruptedBecauseTooOld = true;
                WWContext.getInstance().fireLogging("Routing aborted. GRIB exhausted (preference).\n", LoggingPanel.YELLOW_STYLE);
              }
            }
//          timer = logDiffTime(timer, "Milestone 4");
            
            brg = getBearing(newCurveCenter); // 7-apr-2010
            nbNonZeroSpeed = 0;
            // Calculate isochron from center
            for (int bearing=brg - routingForkWidth / 2; keepLooping && !interruptRouting && bearing<=brg + routingForkWidth / 2; bearing += routingStep)
            {
//            timer = logDiffTime(timer, "Milestone 5");
              int windDir = 0;
              if (wind != null)
                windDir = wind.winddir;
              else
              {
//              Context.getInstance().fireLogging("Wind is null..., aborting (out of the GRIB)\n");
//              System.out.println("Aborting routing from " + center.getPosition().toString() + " to " + destination.getPosition().toString()+ ", wind is null.");
        //      keepLooping = false;
                continue;
              }
              int twa;
              for (twa = bearing - windDir; twa < 0; twa += 360);
              double wSpeed = 0.0D;
              if (wind != null) // Should be granted already...
                wSpeed = wind.windspeed;
              // In case user said to avoid TWS > xxx                
              if (maxTWS > -1)
              {
                if (wSpeed > maxTWS)
                {
//                Context.getInstance().fireLogging("Avoiding too much wind (" + GnlUtilities.XXXX12.format(wSpeed) + " over " + Integer.toString(maxTWS) + ")\n");
//                WWContext.getInstance().fireLogging(".", LoggingPanel.RED_STYLE); // Takes a long time!
                  wSpeed = 0;
                  allowOtherRoute = true;
                  continue;
                }
              }
              double speed = 0D;
              if (minTWA > -1 && twa < minTWA || twa > (360 - minTWA))
              {
//              Context.getInstance().fireLogging("Avoiding too close wind (" + Integer.toString(twa) + " below " + Integer.toString(minTWA) + ")\n");
//              WWContext.getInstance().fireLogging(".", LoggingPanel.RED_STYLE); // Takes a long time!
                speed = 0D;
                allowOtherRoute = true;
                continue; // Added 22-Jun-2009
              }              
              else
              {
//              if (minTWA > -1)
//                WWContext.getInstance().fireLogging(".", LoggingPanel.GREEN_STYLE); // Takes a long time!
                speed = PolarHelper.getSpeed(wSpeed, twa, speedCoeff);
              }
              
              if (speed < 0.0D)
                speed = 0.0D;
              
              if (speed > 0D)
              {
                nbNonZeroSpeed++;
                double dist = timeInterval * speed;
                arrivalDate = new Date(currentDate.getTime() + (long)(timeStep * 3600D * 1000D));
                GeoPoint dr = GreatCircle.dr(new GeoPoint(Math.toRadians(newCurveCenter.getPosition().getL()), 
                                                          Math.toRadians(newCurveCenter.getPosition().getG())), 
                                             dist, 
                                             bearing);
                GeoPoint forecast = new GeoPoint(Math.toDegrees(dr.getL()), Math.toDegrees(dr.getG()));
                Point forecastPoint = null;
                if (chartPanel != null)
                  forecastPoint = chartPanel.getPanelPoint(forecast);
                else
                  forecastPoint = new Point((int)Math.round(forecast.getG() * 1000), (int)Math.round(forecast.getL() * 1000));
                RoutingPoint ip = new RoutingPoint(forecastPoint);
                
                // Add to Data
                ip.setPosition(forecast);
                ip.setAncestor(newCurveCenter);
                ip.setBsp(speed);        // Speed from the center
                ip.setHdg(bearing);      // Bearing from the center
                ip.setTwa(twa);          // twa fron center
                ip.setTws(wSpeed);       // tws from center
                ip.setTwd(windDir);      // twd from center
                ip.setDate(arrivalDate); // arrival date at this point
                if (wind != null && wind.comment != null && wind.comment.equals("TOO_OLD"))
                  ip.setGribTooOld(true);
                oneCurve.add(ip);        
              }
//            timer = logDiffTime(timer, "Milestone 6");

            }
//          timer = logDiffTime(timer, "Milestone 7");
            if (!interruptRouting)
              temp.add(oneCurve);
          }
        }      
        long after = System.currentTimeMillis();
        System.out.println("Isochron calculated in " + Long.toString(after - before) + " ms.");
        // Start from the finalCurve, the previous enveloppe, for the next calculation
        // Flip data
//      timer = logDiffTime(timer, "Milestone 8");
        data = temp;  
        List<RoutingPoint> finalCurve = null;
        if (!interruptRouting)
        {
//        timer = logDiffTime(timer, "Milestone 8-bis");
//        WWContext.getInstance().fireLogging("Reducing...");
//        System.out.print("Reducing...");
//        before = System.currentTimeMillis();
          finalCurve = calculateEnveloppe(data, center);          
//        WWContext.getInstance().fireLogging("Reducing completed in " + Long.toString(System.currentTimeMillis() - before) + " ms\n");
//        System.out.println(" completed in " + Long.toString(System.currentTimeMillis() - before) + " ms\n");
        }
        // Calculate distance to destination, from the final curve
        Iterator<RoutingPoint> finalIterator = null;
//      timer = logDiffTime(timer, "Milestone 9");
        if (finalCurve != null)
        {
          try { finalIterator = finalCurve.iterator(); }
          catch (Exception ex) 
          {  
            if (!interruptRouting)
            {
              ex.printStackTrace();
            }
          }
        }
        while (!interruptRouting && finalIterator != null && finalIterator.hasNext())
        {
//        timer = logDiffTime(timer, "Milestone 10");
          RoutingPoint forecast = finalIterator.next();
          gc.setStart(new GeoPoint(Math.toRadians(forecast.getPosition().getL()),
                                   Math.toRadians(forecast.getPosition().getG())));
          gc.setArrival(new GeoPoint(Math.toRadians(finalDestination.getPosition().getL()),
                                     Math.toRadians(finalDestination.getPosition().getG())));
          gc.calculateGreatCircle(10);                                       
          gcDistance = Math.toDegrees(gc.getDistance() * 60D);
          if (gcDistance < localSmallOne)
          {
            localSmallOne = gcDistance;
            closest = forecast;
          }
        }
//      timer = logDiffTime(timer, "Milestone 11");
//      System.out.println("Local:" + localSmallOne + ", Smallest:" + smallestDist);
        if (localSmallOne < smallestDist)
        {
          smallestDist = localSmallOne;
          finalClosest = closest;
//        WWContext.getInstance().fireLogging("Still progressing...\n");                  
        }
        else if (localSmallOne == smallestDist)
        {
          // Not progressing
          keepLooping = false;
          WWContext.getInstance().fireLogging("Not progressing (stuck at " + WWGnlUtilities.XXXX12.format(smallestDist) + " nm), aborting.\n", LoggingPanel.RED_STYLE);                  
          System.out.println("Not progressing (stuck at " + WWGnlUtilities.XXXX12.format(smallestDist) + " nm), aborting.");                  
          ProgressMonitor monitor = WWContext.getInstance().getMonitor();
          if (monitor != null)
          {
            synchronized (monitor)
            {
              int total = monitor.getTotal();
              int current = monitor.getCurrent();
              if (current != total)
                monitor.setCurrent(null, total);
            }
          }
          JOptionPane.showMessageDialog(null, "Routing aborted, not progressing (dead-end),\nat isochron #" + Integer.toString(allIsochrons.size()), "Routing", JOptionPane.WARNING_MESSAGE);
        }
        else
        {
          keepLooping = false;
          if (allowOtherRoute && nbNonZeroSpeed == 0)
          {
            smallestDist = localSmallOne;
            keepLooping = true; // Try again, even if the distance was not shrinking
          }
          if (localSmallOne != Double.MAX_VALUE)
          {
            if (intermediateWP != null)
            {
              smallestDist = Double.MAX_VALUE; // Reset, for the next leg
              keepLooping = true;                     
              finalCurve = new ArrayList<RoutingPoint>();
              finalCurve.add(closest);
              center = closest;
              center.setDate(currentDate);

              if (nbIntermediateIndex < intermediateWP.size())
                finalDestination = intermediateWP.get(nbIntermediateIndex++);                
              else
              {
                if (!finalDestination.getPosition().equals(destination.getPosition()))
                  finalDestination = destination;
                else
                  keepLooping = false;
              }
            }
            if (!keepLooping)
              WWContext.getInstance().fireLogging("Finished (" + WWGnlUtilities.XXXX12.format(smallestDist) + " vs " + WWGnlUtilities.XXXX12.format(localSmallOne) + ").\n(Non Zero Speed:" + nbNonZeroSpeed+ ")\n", LoggingPanel.YELLOW_STYLE);                  
            if (nbNonZeroSpeed == 0)
            {
              ProgressMonitor monitor = WWContext.getInstance().getMonitor();
              if (monitor != null)
              {
                synchronized (monitor)
                {
                  int total = monitor.getTotal();
                  int current = monitor.getCurrent();
                  if (current != total)
                      monitor.setCurrent(null, total);
                }
              }
              if (interruptedBecauseTooOld)
                JOptionPane.showMessageDialog(null, "Routing aborted (GRIB exhausted),\nat isochron #" + Integer.toString(allIsochrons.size()), "Routing", JOptionPane.WARNING_MESSAGE);
              else
                JOptionPane.showMessageDialog(null, "Routing aborted, not progressing (dead-end),\nat isochron #" + Integer.toString(allIsochrons.size()), "Routing", JOptionPane.WARNING_MESSAGE);
            }
          }
          else
          {
            ProgressMonitor monitor = WWContext.getInstance().getMonitor();
            if (monitor != null)
            {
              synchronized (monitor)
              {
                int total = monitor.getTotal();
                int current = monitor.getCurrent();
                if (current != total)
                    monitor.setCurrent(null, total);
              }
            }
            if (interruptedBecauseTooOld)
              JOptionPane.showMessageDialog(null, "Routing aborted (GRIB exhausted),\nat isochron #" + Integer.toString(allIsochrons.size()), "Routing", JOptionPane.WARNING_MESSAGE);
            else
              JOptionPane.showMessageDialog(null, "Routing aborted, not progressing (dead-end),\nat isochron #" + Integer.toString(allIsochrons.size()), "Routing", JOptionPane.WARNING_MESSAGE);
          }
        }
        allowOtherRoute = false;
        
//      timer = logDiffTime(timer, "Milestone 12");
        if (keepLooping)
        {
          allIsochrons.add(finalCurve);
          data = new ArrayList<List<RoutingPoint>>();
          data.add(finalCurve);
          currentDate = arrivalDate;
        }
        WWContext.getInstance().fireLogging("Isochrone # " + Integer.toString(allIsochrons.size()) + ", smallest distance to arrival:" + WWGnlUtilities.XXXX12.format(smallestDist) + " nm. Still processing:" + keepLooping + "\n");          
        WWContext.getInstance().fireProgressing("Isochrone # " + Integer.toString(allIsochrons.size()) + "...");

        if (caller != null)
          caller.routingNotification(allIsochrons, finalClosest);
//      timer = logDiffTime(timer, "Milestone 13");
      }
      if (interruptRouting)
      {
        logDiffTime(timer, "Routing interrupted.");
//      System.out.println("Routing interrupted.");
        WWContext.getInstance().fireLogging("Routing aborted on user's request.\n", LoggingPanel.YELLOW_STYLE);          
      }
    }
//  timer = logDiffTime(timer, "Milestone 14");
    return allIsochrons;
  }
  
  private static long timer = 0L;
  private static long logDiffTime(long before, String mess)
  {
    long after = System.currentTimeMillis();
    System.out.println(mess + " (" + Long.toString(after - before) + " ms)");
    return after;
  }
     
  // TASK Needs optimization   
  private static List<RoutingPoint> calculateEnveloppe(List<List<RoutingPoint>> bulkPoints, RoutingPoint center)
  {
    List<RoutingPoint> returnCurve = new ArrayList<RoutingPoint>();
    long before = System.currentTimeMillis();
    // Put ALL the points in the finalCurve
     Iterator<List<RoutingPoint>> dimOne = bulkPoints.iterator();
     while (!interruptRouting && dimOne.hasNext())
     {
       List<RoutingPoint> curve = dimOne.next();
       Iterator<RoutingPoint> dimTwo = curve.iterator();
       while (!interruptRouting && dimTwo.hasNext())
       {
         RoutingPoint newPoint = dimTwo.next();
         returnCurve.add(newPoint);
       }
    }
    String mess = "Reducing from " + returnCurve.size() + " point(s)... ";
    // Calculate final curve - Here is the skill
    dimOne = bulkPoints.iterator();
    while (!interruptRouting && dimOne.hasNext())
    {
      Polygon currentPolygon = new Polygon();
      currentPolygon.addPoint(center.getPoint().x, center.getPoint().y); // center
      List<RoutingPoint> curve = dimOne.next();
      Iterator<RoutingPoint> dimTwo = curve.iterator();
      while (!interruptRouting && dimTwo.hasNext())
      {
        RoutingPoint newPoint = dimTwo.next();
        currentPolygon.addPoint(newPoint.getPoint().x, 
                                newPoint.getPoint().y);
      }
      currentPolygon.addPoint(center.getPoint().x, center.getPoint().y); // close
      Iterator<List<RoutingPoint>> dimOneBis = bulkPoints.iterator();
      while (!interruptRouting && dimOneBis.hasNext())
      {
        List<RoutingPoint> curveBis = dimOneBis.next();
        if (curveBis.equals(curve)) continue;
        Iterator<RoutingPoint> dimTwoBis = curveBis.iterator();
        while (!interruptRouting && dimTwoBis.hasNext())
        {
          RoutingPoint isop = dimTwoBis.next();
          if (currentPolygon.contains(isop.getPoint())) 
          {
            // Remove from the final Curve if it's inside (and not removed already)
//          if (returnCurve.contains(isop.getPoint())) // Demanding...
            {
              returnCurve.remove(isop.getPoint());
  //          System.out.println("Removing point, len now " + returnCurve.size());
            }
          }
        }
      }
    }
    long after = System.currentTimeMillis();
    WWContext.getInstance().fireLogging(mess + "to " + returnCurve.size() + " point(s), curve reducing calculated in " + Long.toString(after - before) + " ms");
    System.out.println(mess + "to " + returnCurve.size() + " point(s), curve reducing calculated in " + Long.toString(after - before) + " ms");
    
    return returnCurve;
  }
  
  public static void interruptRoutingCalculation()
  {
    System.out.println("Interrupting the routing.");
    timer = System.currentTimeMillis();
    interruptRouting = true;
  }
  
  private static WhatIfRoutingPanel wirp = null;
  
  public static List<RoutingPoint> whatIfRouting(CommandPanel cp, GeoPoint fromPt, GribHelper.GribConditionData[] gribData) 
  {
    if (wirp == null)
      wirp = new WhatIfRoutingPanel();
    wirp.setFromPos(fromPt);
    
    List<RoutingPoint> route = null;
    int resp = JOptionPane.showConfirmDialog(cp, 
                                             wirp, 
                                             "Routing", 
                                             JOptionPane.OK_CANCEL_OPTION, 
                                             JOptionPane.PLAIN_MESSAGE);
    if (resp == JOptionPane.OK_OPTION)
    {
      int hdg = 0, twa = 0, nbd = 0;
      boolean startNow = false;

      if (wirp.isHeadingSelected())
      {
        try { hdg = wirp.getHeading(); }
        catch (Exception ex)
        {
          JOptionPane.showMessageDialog(cp, ex.toString(), "Heading", JOptionPane.ERROR_MESSAGE); 
        }
      }
      else
      {
        try { twa = wirp.getTWA(); }
        catch (Exception ex)
        {
          JOptionPane.showMessageDialog(cp, ex.toString(), "TWA", JOptionPane.ERROR_MESSAGE); 
        }
      }
      startNow = wirp.isNowSelected();
      
      if (wirp.isDuringSelected())
        nbd = wirp.getNbDays();
            
      long timeStep      = 24;
      try { timeStep = wirp.getRoutingStep(); } catch (Exception ex) { JOptionPane.showMessageDialog(cp, ex.toString(), "Time Step", JOptionPane.ERROR_MESSAGE); }
      double polarFactor = 1.0;
      try { polarFactor = wirp.getPolarFactor(); } catch (Exception ex) { JOptionPane.showMessageDialog(cp, ex.toString(), "Polar Factor", JOptionPane.ERROR_MESSAGE); }
      
      route = new ArrayList<RoutingPoint>(2);
      Date fromDate = gribData[0].getDate();
      if (startNow)
        fromDate = new Date();
      Date toDate = gribData[gribData.length - 1].getDate();
      if (wirp.isDuringSelected())
        toDate = new Date(fromDate.getTime() + (nbd * (24L * 3600L * 1000L)));
      System.out.println("Forecast ends at " + toDate.toString());
      
      Date currentDate = fromDate;
      GeoPoint currentPt = fromPt;
      RoutingPoint ancestor = null;
      long before = System.currentTimeMillis();
      while (currentDate.compareTo(toDate) <= 0)
      {
        Point panelPoint = cp.getChartPanel().getPanelPoint(currentPt);
        RoutingPoint rpt = new RoutingPoint(panelPoint);
        rpt.setAncestor(ancestor);
        GribHelper.GribCondition wind = GribHelper.gribLookup(currentPt, gribData, currentDate);        
        int windDir = 0;
        if (wind != null)
          windDir = wind.winddir;
        
        if (wirp.isHeadingSelected())
          twa = windDir - hdg;
        else
          hdg = windDir - twa;

        while (twa < 0) twa += 360;
        while (hdg < 0) hdg += 360;
        double wSpeed = 0.0D;
        if (wind != null)
          wSpeed = wind.windspeed;
        double speed = PolarHelper.getSpeed(wSpeed, twa, polarFactor);
        if (speed < 0.0D)
          speed = 0.0D;
        
        rpt.setTwa(-twa);
        rpt.setTwd(windDir);
        rpt.setTws(wSpeed);
        rpt.setBsp(speed);
        rpt.setHdg(hdg);
        rpt.setPosition(currentPt);
        rpt.setDate(currentDate);
        route.add(rpt);
        
        double dist = timeStep * speed;
        currentDate = new Date(currentDate.getTime() + (long)(timeStep * 3600D * 1000D));
        GeoPoint dr = GreatCircle.dr(new GeoPoint(Math.toRadians(currentPt.getL()), 
                                                  Math.toRadians(currentPt.getG())), 
                                     dist, 
                                     hdg);
        currentPt = new GeoPoint(Math.toDegrees(dr.getL()), Math.toDegrees(dr.getG()));                
        ancestor = rpt;
        
  //    System.out.println("Reaching " + currentDate.toString() + ", " + 
  //                        currentPt.toString() + " TWA:" + twa +
  //                       " TWD:" + windDir + 
  //                       " TWS:" + wSpeed + 
  //                       " BSP:" + speed + 
  //                       " HDG:" + hdg);
      }  
      long after = System.currentTimeMillis();
      System.out.println("Created " + route.size() + " Routing Points between " + fromDate.toString() + " and " + toDate.toString() + " in " + Long.toString(after - before) + " ms.");
      // Turn the route upside down, to do like the routing backtracking
      List<RoutingPoint> route2 = new ArrayList<RoutingPoint>(route.size());
      for (int i=0; i<route.size(); i++)
      {
        RoutingPoint rp = route.get(route.size() - (i + 1));
        route2.add(rp);
      }
      // TODO Fix that mess...
//    route2.add(route.get(0)); // Trick. For the route to look like the routing one (backtracking).
//    int size = route2.size();
      for (int i=0; false && i<route2.size(); i++)
      {
        RoutingPoint rp   = route2.get(i);
        RoutingPoint prev = null;
        try { route2.get(i + 1); } catch (IndexOutOfBoundsException ioobe) {}
        rp.setAncestor(prev);
        if (prev != null)
          rp.setPosition(prev.getPosition());
      }
      route = route2;
    }
    return route;
  }  
}
