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

import java.awt.Point;
import java.awt.Polygon;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

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

  public static ArrayList<ArrayList<RoutingPoint>> calculateIsochrons(RoutingClientInterface caller, 
                                                                      ChartPanel chartPanel,
                                                                      RoutingPoint center,
                                                                      RoutingPoint destination,
                                                                      Date fromDate,
                                                                      GribHelper.GribConditionData[] gribData,
                                                                      double timeInterval,
                                                                      int routingForkWidth,
                                                                      int routingStep,
                                                                      int maxTWS,
                                                                      int minTWA,
                                                                      boolean stopIfGRIB2old)
  {
    return calculateIsochrons(caller, 
                              chartPanel,
                              center,
                              destination,
                              fromDate,
                              gribData,
                              timeInterval,
                              routingForkWidth,
                              routingStep,
                              maxTWS,
                              minTWA,
                              stopIfGRIB2old,
                              1D);
  }
  
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

  public static ArrayList<ArrayList<RoutingPoint>> calculateIsochrons(RoutingClientInterface caller, 
                                                                      ChartPanel chartPanel,
                                                                      RoutingPoint center,
                                                                      RoutingPoint destination,
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
    finalDestination = destination;
    timeStep         = timeInterval;
    closest          = null;
    finalClosest     = null;
    
    double gcDistance = 0D;
    
//  System.out.println("Starting routing from " + center.getPosition().toString() + " to " + destination.getPosition().toString());

    // Calcutate bearing to detination (from start)
    brg = getBearing(center);
    
    ArrayList<ArrayList<RoutingPoint>> allIsochrons = new ArrayList<ArrayList<RoutingPoint>>();
    
    // Initialization
    interruptRouting = false;
    timer = System.currentTimeMillis();
    
    smallestDist = Double.MAX_VALUE;
    ArrayList<ArrayList<RoutingPoint>> data = new ArrayList<ArrayList<RoutingPoint>>(1);
    ArrayList<RoutingPoint> one = new ArrayList<RoutingPoint>(1);
    center.setDate(fromDate);
    GribHelper.GribCondition wind = GribHelper.gribLookup(center.getPosition(), wgd, fromDate);
    boolean keepLooping = true;
    if (wind != null && wind.comment != null && wind.comment.equals("TOO_OLD"))
    {
      center.setGribTooOld(true);
//    System.out.println("Stop if GRIB too old:" + stopIfGRIB2old);
      if (stopIfGRIB2old)
      {
        keepLooping = false;
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
        ArrayList<ArrayList<RoutingPoint>> temp = new ArrayList<ArrayList<RoutingPoint>>();
        Iterator<ArrayList<RoutingPoint>> dimOne = data.iterator();
        while (!interruptRouting && dimOne.hasNext() && keepLooping)
        {
//        timer = logDiffTime(timer, "Milestone 2");
          ArrayList<RoutingPoint> curve = dimOne.next();
          Iterator<RoutingPoint> dimTwo = curve.iterator();
          while (!interruptRouting && keepLooping && dimTwo.hasNext())
          {
//          timer = logDiffTime(timer, "Milestone 3");
            RoutingPoint newCurveCenter = dimTwo.next();
            ArrayList<RoutingPoint> oneCurve = new ArrayList<RoutingPoint>(10);
            
            wind = GribHelper.gribLookup(newCurveCenter.getPosition(), wgd, currentDate);
            if (wind != null && wind.comment != null && wind.comment.equals("TOO_OLD"))
            {
              center.setGribTooOld(true);
//            System.out.println("Stop if GRIB too old:" + stopIfGRIB2old);
              if (stopIfGRIB2old)
              {
                keepLooping = false;
                WWContext.getInstance().fireLogging("Routing aborted. GRIB exhausted (preference).\n", LoggingPanel.YELLOW_STYLE);
              }
            }
//          timer = logDiffTime(timer, "Milestone 4");
            
            brg = getBearing(newCurveCenter); // 7-apr-2010
            
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
                  continue;
                }
              }
              double speed = 0D;
              if (minTWA > -1 && twa < minTWA || twa > (360 - minTWA))
              {
//              Context.getInstance().fireLogging("Avoiding too close wind (" + Integer.toString(twa) + " below " + Integer.toString(minTWA) + ")\n");
//              WWContext.getInstance().fireLogging(".", LoggingPanel.RED_STYLE); // Takes a long time!
                speed = 0D;
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
                double dist = timeInterval * speed;
                arrivalDate = new Date(currentDate.getTime() + (long)(timeStep * 3600D * 1000D));
                GeoPoint dr = GreatCircle.dr(new GeoPoint(Math.toRadians(newCurveCenter.getPosition().getL()), 
                                                          Math.toRadians(newCurveCenter.getPosition().getG())), 
                                             dist, 
                                             bearing);
                GeoPoint forecast = new GeoPoint(Math.toDegrees(dr.getL()), Math.toDegrees(dr.getG()));
                Point forecastPoint = chartPanel.getPanelPoint(forecast);
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
        // Start from the finalCurve, the previous enveloppe, for the next calculation
        // Flip data
//      timer = logDiffTime(timer, "Milestone 8");
        data = temp;  
        ArrayList<RoutingPoint> finalCurve = null;
        if (!interruptRouting)
        {
//        timer = logDiffTime(timer, "Milestone 8-bis");
          finalCurve = calculateEnveloppe(data, center);          
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
          WWContext.getInstance().fireLogging("Still progressing...\n");                  
        }
        else if (localSmallOne == smallestDist)
        {
          // Not progressing
          keepLooping = false;
          WWContext.getInstance().fireLogging("Not progressing (stuck at " + WWGnlUtilities.XXXX12.format(smallestDist) + " nm), aborting.\n", LoggingPanel.RED_STYLE);                  
          System.out.println("Not progressing (stuck at " + WWGnlUtilities.XXXX12.format(smallestDist) + " nm), aborting.");                  
          JOptionPane.showMessageDialog(null, "Routing aborted, not progressing", "Routing", JOptionPane.WARNING_MESSAGE);
        }
        else
        {
          keepLooping = false;
          if (localSmallOne != Double.MAX_VALUE)
            WWContext.getInstance().fireLogging("Finished (" + WWGnlUtilities.XXXX12.format(smallestDist) + " vs " + WWGnlUtilities.XXXX12.format(localSmallOne) + ").\n", LoggingPanel.YELLOW_STYLE);                  
        }
        
//      timer = logDiffTime(timer, "Milestone 12");
        if (keepLooping)
        {
          allIsochrons.add(finalCurve);
          data = new ArrayList<ArrayList<RoutingPoint>>();
          data.add(finalCurve);
          currentDate = arrivalDate;
        }
        WWContext.getInstance().fireLogging("Isochrone # " + Integer.toString(allIsochrons.size()) + ", smallest distance:" + WWGnlUtilities.XXXX12.format(smallestDist) + ". Processing:" + keepLooping + "\n");          
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
     
  private static ArrayList<RoutingPoint> calculateEnveloppe(ArrayList<ArrayList<RoutingPoint>> bulkPoints, RoutingPoint center)
  {
    ArrayList<RoutingPoint> returnCurve = new ArrayList<RoutingPoint>();
    long before = System.currentTimeMillis();
    // Put ALL the points in the finalCurve
     Iterator<ArrayList<RoutingPoint>> dimOne = bulkPoints.iterator();
     while (!interruptRouting && dimOne.hasNext())
     {
       ArrayList<RoutingPoint> curve = dimOne.next();
       Iterator<RoutingPoint> dimTwo = curve.iterator();
       while (!interruptRouting && dimTwo.hasNext())
       {
         RoutingPoint newPoint = dimTwo.next();
         returnCurve.add(newPoint);
       }
    }
    String mess = "From " + returnCurve.size() + " point(s)... ";
    // Calculate final curve - Here is the skill
    dimOne = bulkPoints.iterator();
    while (!interruptRouting && dimOne.hasNext())
    {
      Polygon currentPolygon = new Polygon();
      currentPolygon.addPoint(center.getPoint().x, center.getPoint().y); // center
      ArrayList<RoutingPoint> curve = dimOne.next();
      Iterator<RoutingPoint> dimTwo = curve.iterator();
      while (!interruptRouting && dimTwo.hasNext())
      {
        RoutingPoint newPoint = dimTwo.next();
        currentPolygon.addPoint(newPoint.getPoint().x, 
                                newPoint.getPoint().y);
      }
      currentPolygon.addPoint(center.getPoint().x, center.getPoint().y); // close
      
      Iterator<ArrayList<RoutingPoint>> dimOneBis = bulkPoints.iterator();
      while (!interruptRouting && dimOneBis.hasNext())
      {
        ArrayList<RoutingPoint> curveBis = dimOneBis.next();
        if (curveBis.equals(curve)) continue;
        Iterator<RoutingPoint> dimTwoBis = curveBis.iterator();
        while (!interruptRouting && dimTwoBis.hasNext())
        {
          RoutingPoint isop = dimTwoBis.next();
          if (currentPolygon.contains(isop.getPoint())) 
          {
            // Remove from the final Curve if it's inside (and not removed already)
            if (returnCurve.contains(isop.getPoint()))
            {
              returnCurve.remove(isop.getPoint());
  //          System.out.println("Removing point, len now " + returnCurve.size());
            }
          }
        }
      }
    }
    long after = System.currentTimeMillis();
    WWContext.getInstance().fireLogging(mess + "to " + returnCurve.size() + " point(s), curve calculated in " + Long.toString(after - before) + " ms");
    
    return returnCurve;
  }
  
  public static void interruptRoutingCalculation()
  {
    System.out.println("Interrupting the routing.");
    timer = System.currentTimeMillis();
    interruptRouting = true;
  }
  
  private static WhatIfRoutingPanel wirp = null;
  
  public static ArrayList<RoutingPoint> whatIfRouting(CommandPanel cp, GeoPoint fromPt, GribHelper.GribConditionData[] gribData) 
  {
    if (wirp == null)
      wirp = new WhatIfRoutingPanel();
    wirp.setFromPos(fromPt);
    
    ArrayList<RoutingPoint> route = null;
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
        hdg = wirp.getHeading();
      else
        twa = wirp.getTWA();
      
      startNow = wirp.isNowSelected();
      
      if (wirp.isDuringSelected())
        nbd = wirp.getNbDays();
            
      long timeStep      = 6L;   // TODO Step, in hours
      double polarFactor = 0.9f; // TODO Polar Factor
      
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
      ArrayList<RoutingPoint> route2 = new ArrayList<RoutingPoint>(route.size());
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
