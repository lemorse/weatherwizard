package chartview.util.grib;

import astro.calc.GeoPoint;

import chart.components.ui.ChartPanel;

import chartview.util.WWGnlUtilities;
import chartview.ctx.WWContext;

import chartview.gui.util.dialog.GRIBDetailPanel;

import coreutilities.Utilities;

import java.awt.Point;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.io.InputStream;

import java.io.PrintStream;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TimeZone;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import java.util.HashMap;

import java.util.Set;

import javax.swing.JOptionPane;
import jgrib.GribFile;
import jgrib.GribRecordGDS;
import jgrib.NotSupportedException;
import jgrib.NoValidGribException;
import jgrib.GribRecord;
import jgrib.GribRecordBDS;
import jgrib.GribRecordPDS;

public class GribHelper
{
  private static boolean alreadySaidTooOld;

  public static void setAlreadySaidTooOld(boolean b)
  {
    alreadySaidTooOld = b;
  }

  /**
   * 2D smoothing (area, no time0
   * 
   * @param gribData Original GRIB Data
   * @param smooth   smooth factor.
   * @return         Smoothed data
   */
  public static GribConditionData smoothGribData(GribConditionData gribData, int smooth)
  {
//  System.out.println("Smoothing..., factor " + smooth);
    GribConditionData newGribData = null;

    double _w = gribData.getWLng();
    double _e = gribData.getELng();
    double _n = gribData.getNLat();
    double _s = gribData.getSLat();
    
    double stepX = gribData.getStepX() / (double)smooth;
    double stepY = gribData.getStepY() / (double)smooth;
    
    newGribData = new GribConditionData();
    newGribData.setDate(gribData.getDate());
    newGribData.setELng(_e);
    newGribData.setWLng(_w);
    newGribData.setNLat(_n);
    newGribData.setSLat(_s);
    newGribData.setStepX(stepX);
    newGribData.setStepY(stepY);

    int newH = gribData.getGribPointData().length * smooth,
        newW = gribData.getGribPointData()[0].length * smooth;

    GribPointData[][] newGribPointData = new GribPointData[newH][newW];
    
    if (Utilities.sign(_w) != Utilities.sign(_e) && _w > 0D) // Around Antemeridian
      _w -= 360D;
    
    int h = 0;
    for (double _Lat = _s; _Lat <= _n; _Lat += stepY)
    {
      int w = 0;
      for (double _Lng = _w; _Lng <= _e; _Lng += stepX)
      {
        ArrayList<Integer> ar = gribData.getDataPointsAround(new GeoPoint(_Lat, _Lng));   
        if (ar != null)
        {          
          try
          {
            int yIdx = ar.get(0).intValue();
            int xIdx = ar.get(1).intValue();
            DataPoint[] dp = new DataPoint[4];
    
            float u =  gribData.getGribPointData()[yIdx][xIdx].getU();
            float v = -gribData.getGribPointData()[yIdx][xIdx].getV();
            double lat = gribData.getGribPointData()[yIdx][xIdx].getLat();
            double lng = gribData.getGribPointData()[yIdx][xIdx].getLng();
            if (_Lng < -180 && lng > 0)
              lng -= 360D;
            double speed = Math.sqrt(u * u + v * v);
            speed *= 3.60D;
            speed /= 1.852D;
            double dir = WWGnlUtilities.getDir(u, v);
            double prmsl  = gribData.getGribPointData()[yIdx][xIdx].getPrmsl();
            double hgt500 = gribData.getGribPointData()[yIdx][xIdx].getHgt();
            double temp   = gribData.getGribPointData()[yIdx][xIdx].getTmp();
            double whgt   = gribData.getGribPointData()[yIdx][xIdx].getWHgt();
            double rain   = gribData.getGribPointData()[yIdx][xIdx].getRain();
            
            // Reset
            u = gribData.getGribPointData()[yIdx][xIdx].getU();
            v = gribData.getGribPointData()[yIdx][xIdx].getV();
    
            dp[0] = new DataPoint(lng, lat, u, v, dir, speed, prmsl, hgt500, temp, whgt, rain);                 
                
            u =  gribData.getGribPointData()[yIdx][xIdx + 1].getU();
            v = -gribData.getGribPointData()[yIdx][xIdx + 1].getV();
            lat = gribData.getGribPointData()[yIdx][xIdx + 1].getLat();
            lng = gribData.getGribPointData()[yIdx][xIdx + 1].getLng();
            if (_Lng < -180 && lng > 0)
              lng -= 360D;
            speed = Math.sqrt(u * u + v * v);
            speed *= 3.60D;
            speed /= 1.852D;
            dir = WWGnlUtilities.getDir(u, v);
            prmsl  = gribData.getGribPointData()[yIdx][xIdx + 1].getPrmsl();
            hgt500 = gribData.getGribPointData()[yIdx][xIdx + 1].getHgt();
            temp   = gribData.getGribPointData()[yIdx][xIdx + 1].getTmp();
            whgt   = gribData.getGribPointData()[yIdx][xIdx + 1].getWHgt();
            rain   = gribData.getGribPointData()[yIdx][xIdx + 1].getRain();
    
            u = gribData.getGribPointData()[yIdx][xIdx + 1].getU();
            v = gribData.getGribPointData()[yIdx][xIdx + 1].getV();

            dp[1] = new DataPoint(lng, lat, u, v, dir, speed, prmsl, hgt500, temp, whgt, rain);                 
    
            u =  gribData.getGribPointData()[yIdx + 1][xIdx].getU();
            v = -gribData.getGribPointData()[yIdx + 1][xIdx].getV();
            lat = gribData.getGribPointData()[yIdx + 1][xIdx].getLat();
            lng = gribData.getGribPointData()[yIdx + 1][xIdx].getLng();
            if (_Lng < -180 && lng > 0)
              lng -= 360D;
            speed = Math.sqrt(u * u + v * v);
            speed *= 3.60D;
            speed /= 1.852D;
            dir = WWGnlUtilities.getDir(u, v);
            prmsl  = gribData.getGribPointData()[yIdx + 1][xIdx].getPrmsl();
            hgt500 = gribData.getGribPointData()[yIdx + 1][xIdx].getHgt();
            temp   = gribData.getGribPointData()[yIdx + 1][xIdx].getTmp();
            whgt   = gribData.getGribPointData()[yIdx + 1][xIdx].getWHgt();
            rain   = gribData.getGribPointData()[yIdx + 1][xIdx].getRain();
    
            u = gribData.getGribPointData()[yIdx + 1][xIdx].getU();
            v = gribData.getGribPointData()[yIdx + 1][xIdx].getV();
            
            dp[2] = new DataPoint(lng, lat, u, v, dir, speed, prmsl, hgt500, temp, whgt, rain);                 
    
            u =  gribData.getGribPointData()[yIdx + 1][xIdx + 1].getU();
            v = -gribData.getGribPointData()[yIdx + 1][xIdx + 1].getV();
            lat = gribData.getGribPointData()[yIdx + 1][xIdx + 1].getLat();
            lng = gribData.getGribPointData()[yIdx + 1][xIdx + 1].getLng();
            if (_Lng < -180 && lng > 0)
              lng -= 360D;
            speed = Math.sqrt(u * u + v * v);
            speed *= 3.60D;
            speed /= 1.852D;
            dir = WWGnlUtilities.getDir(u, v);
            prmsl  = gribData.getGribPointData()[yIdx + 1][xIdx + 1].getPrmsl();
            hgt500 = gribData.getGribPointData()[yIdx + 1][xIdx + 1].getHgt();
            temp   = gribData.getGribPointData()[yIdx + 1][xIdx + 1].getTmp();
            whgt   = gribData.getGribPointData()[yIdx + 1][xIdx + 1].getWHgt();
            rain   = gribData.getGribPointData()[yIdx + 1][xIdx + 1].getRain();
    
            u = gribData.getGribPointData()[yIdx + 1][xIdx + 1].getU();
            v = gribData.getGribPointData()[yIdx + 1][xIdx + 1].getV();
            
            dp[3] = new DataPoint(lng, lat, u, v, dir, speed, prmsl, hgt500, temp, whgt, rain);                 
    
            boolean right  = false;
            boolean left   = false;
            boolean top    = false;
            boolean bottom = false;
            for (int i=0; i<dp.length; i++)
            {
              if (dp[i].d < 180) right = true;
              if (dp[i].d > 180) left = true;
              if (dp[i].d > 270 || dp[i].d < 90) top = true;
              if (dp[i].d < 270 && dp[i].d > 90) bottom = true;
            }
            if (right && left && top)
            {
              for (int i=0; i<dp.length; i++)
              {
                if (dp[i].d < 180) 
                  dp[i].d += 360;
              }
            }
            // Smooth
            ArrayList<Double> _ar = Smoothing.calculate(dp, _Lng, _Lat);
            if (_ar != null)
            {
              double _dir    = _ar.get(0).doubleValue();
              double _speed  = _ar.get(1).doubleValue();
              double _prmsl  = _ar.get(2).doubleValue();
              double _500hgt = _ar.get(3).doubleValue();
              double _temp   = _ar.get(4).doubleValue();
              double _whgt   = _ar.get(5).doubleValue();
              double _rain   = _ar.get(6).doubleValue();
              
              int _u = (int)(_ar.get(7).doubleValue());
              int _v = (int)(_ar.get(8).doubleValue());
              
              GribPointData gpd = new GribPointData();
              gpd.setHgt((int)_500hgt);
              gpd.setLat(_Lat);
              double newLng = _Lng;
              if (Math.abs(newLng) > 180D)
              {
                if (newLng < 0D) newLng += 360D;
                else newLng -= 360D;
              }
              gpd.setLng(newLng);
              gpd.setPrmsl((int)_prmsl);
              gpd.setTmp((int)_temp);
              gpd.setRain((float)_rain);
              gpd.setWHgt((int)_whgt);
              gpd.setTwd(_dir);
              gpd.setTws(_speed);
              
              gpd.setU(_u);
              gpd.setV(_v);
              
              newGribPointData[h][w] = gpd;
//            System.out.println("New GribPointData set at [" + h + ", " + w + "]");
            } // _ar != null             
          }
          catch (ArrayIndexOutOfBoundsException aioobe)
          {
            System.out.println(aioobe.toString());
            aioobe.printStackTrace();
          }
          catch (Exception ex)
          {
            ex.printStackTrace();
          }
        } // ar != null (points around)
        w++;
      } // for _Lng
      h++;
    } // for _Lat
    newGribData.setGribPointData(newGribPointData);
    
    return newGribData;    
  }
    
  public static GribConditionData[] smoothGRIBinTime(GribConditionData[] original, int nbsteps)
  {
    GribConditionData[] newData = new GribConditionData[((original.length - 1) * nbsteps) + 1];
    for (int i=0; i<original.length - 1; i++)    
    {
      for (int j=0; j<nbsteps; j++)
      {
        int idx = (i * nbsteps) + j;
//      System.out.println("i=" + i + ", j=" + j + ", idx=" + idx);
        // Smoothing here
        if (j == 0)
          newData[idx] = original[i];
        else
        {
          newData[idx] = new GribConditionData();
          Date newDate = new Date((long)getIntermediateValue(original[i].getDate().getTime(), original[i + 1].getDate().getTime(), nbsteps, j));
//        System.out.println("New Date for " + idx + ":" + newDate.toString());
          newData[idx].setDate(newDate);
          newData[idx].setELng(original[i].getELng());
          newData[idx].setWLng(original[i].getWLng());
          newData[idx].setNLat(original[i].getNLat());
          newData[idx].setSLat(original[i].getSLat());
          newData[idx].setStepX(original[i].getStepX());
          newData[idx].setStepY(original[i].getStepY());
          newData[idx].hgt   = original[i].hgt;
          newData[idx].prmsl = original[i].prmsl;
          newData[idx].temp  = original[i].temp;
          newData[idx].rain  = original[i].rain;
          newData[idx].wave  = original[i].wave;
          newData[idx].wind  = original[i].wind;
          GribPointData[][] gpd1 = original[i].getGribPointData();
          GribPointData[][] gpd2 = original[i + 1].getGribPointData();
          GribPointData[][] gpd = new GribPointData[gpd1.length][gpd1[0].length];
          for (int h=0; h<gpd.length; h++)
          {
            for (int w=0; w<gpd[h].length; w++)
            {
              gpd[h][w] = new GribPointData();
              gpd[h][w].setLat(gpd1[h][w].getLat());
              gpd[h][w].setLng(gpd1[h][w].getLng());
              gpd[h][w].setHgt((float)getIntermediateValue(gpd1[h][w].getHgt(), gpd2[h][w].getHgt(), nbsteps, j));
              gpd[h][w].setPrmsl((float)getIntermediateValue(gpd1[h][w].getPrmsl(), gpd2[h][w].getPrmsl(), nbsteps, j));
              gpd[h][w].setRain((float)getIntermediateValue(gpd1[h][w].getRain(), gpd2[h][w].getRain(), nbsteps, j));
              gpd[h][w].setTmp((float)getIntermediateValue(gpd1[h][w].getTmp(), gpd2[h][w].getTmp(), nbsteps, j));
              gpd[h][w].setTwd(getIntermediateValue(gpd1[h][w].getTwd(), gpd2[h][w].getTwd(), nbsteps, j));
              gpd[h][w].setTws(getIntermediateValue(gpd1[h][w].getTws(), gpd2[h][w].getTws(), nbsteps, j));
              gpd[h][w].setU((float)getIntermediateValue(gpd1[h][w].getU(), gpd2[h][w].getU(), nbsteps, j));
              gpd[h][w].setV((float)getIntermediateValue(gpd1[h][w].getV(), gpd2[h][w].getV(), nbsteps, j));
              gpd[h][w].setWHgt((float)getIntermediateValue(gpd1[h][w].getWHgt(), gpd2[h][w].getWHgt(), nbsteps, j));
            }
          }
          newData[idx].setGribPointData(gpd);
        }
      }
    }
    // Last one
    newData[(original.length - 1) * nbsteps] = original[original.length - 1];
    
    return newData;
  }
  
  private static double getIntermediateValue(double a, double b, int nbInterval, int currInterval)
  {
    double d = 0;
    d = a + (currInterval * ((b - a) / nbInterval));
    return d;
  }
    
  public static void displayGRIBDetails(String gribName)
  {    
    try
    {
      GribHelper.GribConditionData[] thisGRIB = GribHelper.getGribData(gribName);
      GribFile gf = new GribFile(gribName);
      String mess = "Contains " + thisGRIB.length + " frame(s).";
      displayGRIBDetails(gf, mess);
    }
    catch (NoValidGribException e)
    {
      e.printStackTrace();
    }
    catch (NotSupportedException e)
    {
      e.printStackTrace();
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  // TODO Localize
  public static void displayGRIBDetails(GribFile gf, String mess)
  {    
    try
    {
      String[] types = gf.getTypeNames();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrintStream ps = new PrintStream(baos);
      gf.listRecords(ps);
      mess += ("\n" +  baos.toString());
      
  //      if (false)
  //      {
  //        mess += ("\nCell: " + Double.toString(gf.getGrids()[0].getGridDX()) + " x " + Double.toString(gf.getGrids()[0].getGridDY()));
  //        mess += ("\n" + new GeoPoint(thisGRIB[0].getNLat(), thisGRIB[0].getWLng()).toString() + " to " + new GeoPoint(thisGRIB[0].getSLat(), thisGRIB[0].getELng()).toString());
  //        mess += "\nData:";
  //        for (int i=0; i<types.length; i++)
  //        {
  //          GribRecordGDS[]  grg = gf.getGridsForType(types[i]);
  //          int unit = gf.getZunitsForTypeGrid(types[i], grg[0])[0];
  //          String level = gf.getLevelsForTypeGridUnit(types[i], grg[0], unit)[0].getLevel();
  //          String description = gf.getDescriptionForType(types[i]);
  //          mess += ("\n " + Integer.toString(i+1) + ". " + types[i] + " (" + description + ", " + level + ")");
  //        }
  //        for (int i=0; i<thisGRIB.length; i++)
  //        {
  //          GribHelper.GribConditionData data = thisGRIB[i];
  //          mess += ("\nDate:" + data.getDate().toString());
  //        }
  //      }
      GRIBDetailPanel gdp = new GRIBDetailPanel();
      gdp.setText(mess);
      JOptionPane.showMessageDialog(WWContext.getInstance().getMasterTopFrame(), gdp, "GRIB Display", JOptionPane.PLAIN_MESSAGE);    
    }
    catch (NoValidGribException e)
    {
      e.printStackTrace();
    }
    catch (NotSupportedException e)
    {
      e.printStackTrace();
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static class GribCondition
  {
    public float windspeed;
    public int winddir;
    public float hgt500;
    public int horIdx;
    public int vertIdx;
    public float prmsl;
    public float waves;
    public float temp;
    public float rain;
    
    public String comment = null;

    public GribCondition()
    {
    }
  }

  public static class GribConditionData
  {
    public boolean wind  = true; // true by default
    public boolean prmsl = false;
    public boolean temp  = false;
    public boolean hgt   = false;
    public boolean wave  = false;
    public boolean rain  = false;
    
    private Date date;
    private double wLng;
    private double eLng;
    private double nLat;
    private double sLat;
    private double stepX;
    private double stepY;
    private GribPointData gribPointData[][];

    public GribConditionData()
    {
    }

    public void setDate(Date d)
    {
      date = d;
    }

    public void setWLng(double d)
    {
      wLng = d;
    }

    public void setELng(double d)
    {
      eLng = d;
    }

    public void setNLat(double d)
    {
      nLat = d;
    }

    public void setSLat(double d)
    {
      sLat = d;
    }

    public void setStepX(double d)
    {
      stepX = d;
    }

    public void setStepY(double d)
    {
      stepY = d;
    }

    public void setGribPointData(GribPointData gpd[][])
    {
      gribPointData = gpd;
    }

    public Date getDate()
    {
      return date;
    }

    public double getWLng()
    {
      return wLng;
    }

    public double getELng()
    {
      return eLng;
    }

    public double getNLat()
    {
      return nLat;
    }

    public double getSLat()
    {
      return sLat;
    }

    public double getStepX()
    {
      return stepX;
    }

    public double getStepY()
    {
      return stepY;
    }

    public GribPointData[][] getGribPointData()
    {
      return gribPointData;
    }
    /**
     * 
     * @param pt The position to find the points around for.
     * @return indexes (2 integers) of the <b>bottom left</b> grib data point
     * 
     * get the two Integers from the returned ArrayList, Y, and X
     * The 4 points we are interested in will eventually be:
     * 
     * bottom-left : gribPointData[Y][X]
     * bottom-right: gribPointData[Y][X+1]
     * top-right   : gribPointData[Y+1][X+1]
     * top-left    : gribPointData[Y+1][X]
     */
    public ArrayList<Integer> getDataPointsAround(GeoPoint pt)
    {
      ArrayList<Integer> array = null;
      double l = pt.getL();
      double g = pt.getG();
      // G same sign...
      double _wLng = gribPointData[0][0].getLng(), _eLng = eLng;
      double _sLat = gribPointData[0][0].getLat(), _nLat = nLat;
      if (Utilities.sign(_wLng) != Utilities.sign(_eLng) && _wLng > 0) // Around Ante meridian
      {
        _wLng -= 360D;
        if (g > 0) g -= 360D;
      }
      if (isBetween(l, _nLat, _sLat) && isBetween(g, _wLng, _eLng))
      {
        double deltaX = g - _wLng;
        double deltaY = l - _sLat;
        int idxX = (int)Math.floor(deltaX / stepX);
        int idxY = (int)Math.floor(deltaY / stepY);
        // Warning/Reminder: [0][0] bottom left
//        System.out.println(pt.toString() + " is between:");
//        System.out.println( windPointData[idxY][idxX].toString() );
//        System.out.println( windPointData[idxY][idxX + 1].toString() );
//        System.out.println( windPointData[idxY + 1][idxX].toString() );
//        System.out.println( windPointData[idxY + 1][idxX + 1].toString() );
//        System.out.println("------------------");
        array = new ArrayList<Integer>(2);
        // Indexes of the bottom left point.
        array.add(new Integer(idxY)); 
        array.add(new Integer(idxX));
      }      
      return array;  
    }
  }

  public static class GribPointData
  {
    private double lat;
    private double lng;
    private float u;
    private float v;
    private float hgt;
    private float prmsl;
    private float tmp;
    private float whgt;
    private float rain;
    
    private double tws = -1D;
    private double twd = -1D;

    public GribPointData()
    {
    }
    

    public void setLat(double d)
    {
      lat = d;
    }

    public void setLng(double d)
    {
      lng = d;
    }

    public void setU(float i)
    {
      u = i;
    }

    public void setV(float i)
    {
      v = i;
    }

    public void setHgt(float i)
    {
      hgt = i;
    }

    public void setPrmsl(float i)
    {
      prmsl = i;
    }

    public void setTmp(float i)
    {
      tmp = i;
    }

    public void setWHgt(float i)
    {
      whgt = i;
    }
    
    public void setRain(float f)
    {
      rain = f;
    }

    public double getLat()
    {
      return lat;
    }

    public double getLng()
    {
      return lng;
    }

    public float getU()
    {
      return u;
    }

    public float getV()
    {
      return v;
    }

    public float getHgt()
    {
      return hgt;
    }

    public float getPrmsl()
    {
      return prmsl;
    }

    public float getTmp()
    {
      return tmp;
    }

    public float getWHgt()
    {
      return whgt;
    }
    
    public float getRain()
    {
      return rain;
    }

    public String toString()
    {
      return new GeoPoint(lat, lng).toString();
    }

    public void setTws(double tws)
    {
      this.tws = tws;
    }

    public double getTws()
    {
      return tws;
    }

    public void setTwd(double twd)
    {
      this.twd = twd;
    }

    public double getTwd()
    {
      return twd;
    }
  }

  private static class TempGribData
  {
    protected Date date;
    protected int width, height;
    protected double top, bottom, left, right;
    protected double stepX, stepY;
    protected String type;
    protected String description;
    protected float[][] data;
  }
  
  public GribHelper()
  {
  }

  public static GribConditionData[] getGribData(InputStream stream, String name)
  {
    ArrayList<GribConditionData> wgd = null;
    try
    {
      TimeZone tz = TimeZone.getTimeZone("127"); // "GMT + 0"
      TimeZone.setDefault(tz);
    }
    catch(Exception e)
    {
      WWContext.getInstance().fireExceptionLogging(e);
      e.printStackTrace();
    }
    try
    {
//    System.err.println("Managing [" + name +"]");
      GribFile gribFile = new GribFile(stream);
      WWContext.getInstance().setGribFile(gribFile);
      stream.close();
      int recordCount = gribFile.getRecordCount();
//    System.out.println("Found " + recordCount + " GRIB record(s)");
      wgd = dumper(gribFile, name);
    }
    catch(IOException ioError)
    {
      System.err.println("For [" + name + "], IOException : " + ioError);
      JOptionPane.showMessageDialog(null, ioError.toString(), "For [" + name + "]", JOptionPane.ERROR_MESSAGE);
    }
    catch(NoValidGribException noGrib)
    {
      System.err.println("For [" + name + "], NoValidGribException : " + noGrib);
      JOptionPane.showMessageDialog(null, noGrib.toString(), "For [" + name + "]", JOptionPane.ERROR_MESSAGE);
    }
    catch(NotSupportedException noSupport)
    {
      WWContext.getInstance().fireExceptionLogging(noSupport);
      System.err.println("For [" + name + "], NotSupportedException : " + noSupport);
      JOptionPane.showMessageDialog(null, noSupport.toString(), "For [" + name + "]", JOptionPane.ERROR_MESSAGE);
    }
    GribConditionData[] gcd = null;
    if (wgd != null)
      gcd = new GribConditionData[wgd.size()];
    return (gcd!=null?wgd.toArray(gcd):null);
  }

  public static GribConditionData[] getGribData(String fileName)
  {
    ArrayList<GribConditionData> wgd = null;
    try
    {
      TimeZone tz = TimeZone.getTimeZone("127");
      TimeZone.setDefault(tz);
    }
    catch(Exception e)
    {
      WWContext.getInstance().fireExceptionLogging(e);
      e.printStackTrace();
    }
    try
    {
//    System.err.println("Managing [" + fileName +"]");
      GribFile gribFile = new GribFile(fileName);
      WWContext.getInstance().setGribFile(gribFile);
      int recordCount = gribFile.getRecordCount();
      wgd = dumper(gribFile, fileName);
    }
    catch(FileNotFoundException noFileError)
    {
      WWContext.getInstance().fireExceptionLogging(noFileError);
      System.err.println("For [" + fileName + "], " + "FileNotFoundException : " + noFileError);
      noFileError.printStackTrace();
      JOptionPane.showMessageDialog(WWContext.getInstance().getMasterTopFrame(), "GRIB File Not Found (must have been moved)\n" + noFileError.toString(), "GRIB Display", JOptionPane.ERROR_MESSAGE);
    }
    catch(IOException ioError)
    {
      System.err.println("For [" + fileName + "], " + "IOException : " + ioError);
      JOptionPane.showMessageDialog(null, ioError.toString(), "For [" + fileName + "]", JOptionPane.ERROR_MESSAGE);
    }
    catch(NoValidGribException noGrib)
    {
      System.err.println("For [" + fileName + "], " + "NoValidGribException : " + noGrib);
      JOptionPane.showMessageDialog(null, noGrib.toString(), "For [" + fileName + "]", JOptionPane.ERROR_MESSAGE);
    }
    catch(NotSupportedException noSupport)
    {
      WWContext.getInstance().fireExceptionLogging(noSupport);
      System.err.println("For [" + fileName + "], " + "NotSupportedException : " + noSupport);
      JOptionPane.showMessageDialog(null, noSupport.toString(), "For [" + fileName + "]", JOptionPane.ERROR_MESSAGE);
    }
    GribConditionData[] gcd = null;
    if (wgd != null)
      gcd = new GribConditionData[wgd.size()];
    return (gcd!=null?wgd.toArray(gcd):null);
  }

  public static ArrayList<GribConditionData> dumper(GribFile gribFile, String fileName)
  {
    ArrayList<String> unrecognized = new ArrayList<String>();

    ArrayList<GribConditionData> wgd = null;
    Map<String, Map<Date, TempGribData>> map = new HashMap<String, Map<Date, TempGribData>>();
    
    TimeZone tz = TimeZone.getTimeZone("127"); // "GMT + 0"
//  TimeZone.setDefault(tz);
    WWGnlUtilities.SDF.setTimeZone(tz);
  
    for (int i = 0; i < gribFile.getLightRecords().length; i++)
    {        
      try
      {
        GribRecord gr = new GribRecord(gribFile.getLightRecords()[i]);
        GribRecordPDS grpds = gr.getPDS(); // Headers and Data
        
        GribRecordGDS grgds = gr.getGDS(); // Boundaries and Steps
        GribRecordBDS grbds = gr.getBDS(); // Min/Max TASK Use those ones

         TempGribData tgd = new TempGribData();
         tgd.date = grpds.getGMTForecastTime().getTime(); 
         tgd.width = grgds.getGridNX();
         tgd.height = grgds.getGridNY();
         tgd.stepX = grgds.getGridDX();
         tgd.stepY = grgds.getGridDY();
         tgd.top = Math.max(grgds.getGridLat1(), grgds.getGridLat2());
         tgd.bottom = Math.min(grgds.getGridLat1(), grgds.getGridLat2());
         tgd.left = Math.min(grgds.getGridLon1(), grgds.getGridLon2());
         tgd.right = Math.max(grgds.getGridLon1(), grgds.getGridLon2());
         
         tgd.type = grpds.getType();
         tgd.description = grpds.getDescription();
        
         tgd.data = new float[tgd.height][tgd.width];
         float val = 0F;
         for (int col=0; col<tgd.width; col++)
         {
           for (int row=0; row<tgd.height; row++)
           {
             try
             {
               val = gr.getValue(col, row);
               if (val > 200000F)
                 val = 0.0F;
               if (tgd.type.equals("htsgw"))
                 val *= 100F;
                                   
               tgd.data[row][col] = val;
             }
             catch (Exception ex)
             {
               ex.printStackTrace();
             }
           }
         }
         Map<Date, TempGribData> mapForType = map.get(tgd.type);
         if (mapForType == null)
         {
           mapForType = new TreeMap<Date, TempGribData>();
           map.put(tgd.type, mapForType);
         }
         mapForType.put(tgd.date, tgd);
         
      }
      catch (NoValidGribException e)
      {
        e.printStackTrace();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
      catch (NotSupportedException e)
      {
        e.printStackTrace();
      }
    }
    if (false)
    {
      // Dump, for tests
      Set<String> keys = map.keySet();
      Iterator<String> itStr =  keys.iterator();
      while (itStr.hasNext())
      {
        String key = itStr.next();
        System.out.println("KEY: " + key);
        Map<Date, TempGribData> mapForType = map.get(key);
        Set<Date> dateKeys = mapForType.keySet();
        Iterator<Date> itDate = dateKeys.iterator();
        while (itDate.hasNext())
        {
          Date date = itDate.next();
          TempGribData tgd2 = mapForType.get(date);
          System.out.println("\t " + tgd2.type + " " + date.toString() + ", " + tgd2.date.toString() + " " + tgd2.top + "/" + tgd2.bottom + " " + tgd2.left + "/" + tgd2.right + " " + tgd2.stepX + "-" + tgd2.stepY + " " + tgd2.width + "x" + tgd2.height);
        }
      }
    }
    // Sort them by date!! 
    // All GRIB Records should have the same structure in term of dates and dimensions.
    // Data (GribConditionData) are added to the ArrayList, by date.        
    Map<Date, GribConditionData> tMap = new TreeMap<Date, GribConditionData>();
    Set<String> keys = map.keySet();
    Iterator<String> itStr =  keys.iterator();
    while (itStr.hasNext())
    {
      String key = itStr.next(); // That is the type
      Map<Date, TempGribData> mapForType = map.get(key);
      Set<Date> dateKeys = mapForType.keySet();
      Iterator<Date> itDate = dateKeys.iterator();
      while (itDate.hasNext())
      {
        Date date = itDate.next();
        TempGribData tgd2 = mapForType.get(date);
        
        GribConditionData gcd = tMap.get(date);
        if (gcd == null)
        {
          gcd = new GribConditionData();
          tMap.put(date, gcd);
          gcd.date = date;
          gcd.setStepX(tgd2.stepX);
          gcd.setStepY(tgd2.stepY);
          double east = Math.max(tgd2.left, tgd2.right);
          double west = Math.min(tgd2.left, tgd2.right);
          if (Math.abs(west - east) > 180D)
          {
            double tmp = east;
            east = west;
            west = tmp;
          }
          gcd.setWLng(west);
          gcd.setELng(east);
          gcd.setNLat(tgd2.top);
          gcd.setSLat(tgd2.bottom);
        }
//        else
//          System.out.println("Found the GribConditionData from the map");
        try
        {
          int arrayW = tgd2.width;
          int arrayH = tgd2.height;
          GribPointData wpd[][] = gcd.getGribPointData();
          if (wpd == null)
          {
            wpd = new GribPointData[arrayH][arrayW];
            gcd.setGribPointData(wpd);
          }
          else
          {
//            System.out.println("GribPointData array already exists.");
            if (wpd.length != arrayH)
            {
              throw new RuntimeException("DataArray (height) size mismatch in " + fileName);
            }
            else
            {
              if (wpd[0].length != arrayW)
              {
                throw new RuntimeException("DataArray (width) size mismatch in " + fileName);
              }
            }
          }
          // Good to go
          for (int i = 0; i < arrayH; i++)
          {
            for (int j = 0; j < arrayW; j++)
            {
              if (wpd[i][j] == null)
              {
                wpd[i][j] = new GribPointData();
                double l = gcd.getSLat() + (gcd.stepY / (double)2 + (double)i * gcd.stepY);
                double g = gcd.getWLng() + (gcd.stepX / (double)2 + (double)j * gcd.stepX);
                if(g > 180D)
                  g -= 360;
                wpd[i][j].setLat(l);
                wpd[i][j].setLng(g);
              }
//            else
//              System.out.println("Point[" + i + "][" + j + "] already exists");
                
              try
              {
                // FIXME All data are float
                if (key.equals("ugrd"))
                {
//                wpd[i][j].setX((int)Math.round(tgd2.data[i][j]));
                  wpd[i][j].setU(tgd2.data[i][j]);
                }
                else if (key.equals("vgrd"))
                {
                  wpd[i][j].setV(tgd2.data[i][j]);
                }
                else if (key.equals("prmsl"))
                {
                  wpd[i][j].setPrmsl(tgd2.data[i][j]);
                }
                else if (key.equals("hgt"))
                {
                  wpd[i][j].setHgt(tgd2.data[i][j]);
                }
                else if (key.equals("htsgw"))
                {
                  wpd[i][j].setWHgt(tgd2.data[i][j]);
                }
                else if (key.equals("tmp"))
                {
                  wpd[i][j].setTmp(tgd2.data[i][j]);  
                }
                else if (key.equals("prate"))
                {
                  // Unit is Kg x m-2 x s-1, which is 1mm.s-1
                  wpd[i][j].setRain(tgd2.data[i][j]);  
                }
                else
                {
                  if (!unrecognized.contains(key))
                  {
                    unrecognized.add(key);
                    System.err.println("Type [" + key + "] not recognized : [" + tgd2.type + "] " + tgd2.description);                    
                  }
                  System.out.println(key + " value: " + Float.toString(tgd2.data[i][j]));
                }
              }
              catch (Exception exx)
              {
                exx.printStackTrace();
              }
            }
          }
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
        }
      }
    } 
    // Now populate the output
    Set<Date> gribDates = tMap.keySet();
    Iterator<Date> dateIterator = gribDates.iterator();
    while (dateIterator.hasNext())
    {
      GribConditionData cd = tMap.get(dateIterator.next());
      if (wgd == null)
        wgd = new ArrayList<GribConditionData>(gribDates.size()); 
      wgd.add(cd);
    }
    return wgd;
  }
  
  public static boolean isBetween(double value, double one, double two)
  {
    return (value <= Math.max(one, two) && value >= Math.min(one, two));
  }

  public static GribCondition gribLookup(GeoPoint gp, GribConditionData wgdArray[], Date date)
  {
    GribCondition gribCond = null;
    GribConditionData wgd = null;
    long refDate = date.getTime();
    long interval = 0L;
    for (int i = 0; i < wgdArray.length; i++)
    {
      if (i < wgdArray.length - 1)
      {
        interval = Math.abs(wgdArray[i].getDate().getTime() - wgdArray[i + 1].getDate().getTime());
        if(refDate < wgdArray[i].getDate().getTime() || refDate >= wgdArray[i + 1].getDate().getTime())
          continue;
        wgd = wgdArray[i];
        break;
      }
      wgd = wgdArray[i];
      if (Math.abs(wgdArray[i].getDate().getTime() - refDate) > interval)
      {
        if (!alreadySaidTooOld)
        {
          System.out.println("Last GRIB record might be too old, using it anyway...");
          alreadySaidTooOld = true;
        }
      } 
      else
      {
        alreadySaidTooOld = false;
      }
    }

    double pointLng = gp.getG();
    double pointLat = gp.getL();
    double stepx = wgd.getStepX();
    double stepy = wgd.getStepY();
    GribPointData wpd[][] = wgd.getGribPointData();
    // TODO smoothing, space and time... 
    // Use the isBetween function
    for (int l=0; l<wpd.length; l++)
    {
      for (int g=0; g<wpd[l].length; g++)
      {
        double diff_01 = Math.abs(pointLng - wpd[l][g].getLng());
        if (diff_01 > 180.0)
          diff_01 = 360 - diff_01;
        double diff_02 = Math.abs(pointLat - wpd[l][g].getLat());
        
        if (diff_01 <= (stepx / 2D) && diff_02 <= (stepy / 2D))
        {
          gribCond = new GribCondition();
          float x = wpd[l][g].getU();
          float y = wpd[l][g].getV();
          double speed = Math.sqrt(x * x + y * y); // m/s
          speed *= 3.600D; // km/h
          speed /= 1.852D; // knots
          double dir = WWGnlUtilities.getDir(x, y);
          gribCond.winddir = (int)Math.round(dir);
          gribCond.windspeed = (float)speed;
          gribCond.hgt500 = wpd[l][g].getHgt();
          gribCond.horIdx = g;
          gribCond.vertIdx = l;
          gribCond.prmsl = wpd[l][g].getPrmsl();
          gribCond.waves = wpd[l][g].getWHgt();
          gribCond.temp = wpd[l][g].getTmp();
          gribCond.rain = wpd[l][g].getRain();
          if (alreadySaidTooOld) 
            gribCond.comment = "TOO_OLD";

          return gribCond;
        }
      }
    }
//  System.out.println(GeomUtil.decToSex(gp.getL()) + "/" + GeomUtil.decToSex(gp.getG()) + " is not in that grid...");
    return gribCond;
  }
  
  public static GribCondition gribLookup(GeoPoint gp, GribConditionData wgd)
  {
    GribCondition gribCond = null;

    double pointLng = gp.getG();
    double pointLat = gp.getL();
    double stepx = wgd.getStepX();
    double stepy = wgd.getStepY();
    GribPointData wpd[][] = wgd.getGribPointData();
    // Use the isBetween function
    for (int l=0; l<wpd.length; l++)
    {
      for (int g=0; g<wpd[l].length; g++)
      {
        if (wpd[l][g] != null)
        {
          double diff_01 = Math.abs(pointLng - wpd[l][g].getLng());
          if (diff_01 > 180.0)
            diff_01 = 360 - diff_01;
          double diff_02 = Math.abs(pointLat - wpd[l][g].getLat());
          
          if (diff_01 <= (stepx / 2D) && diff_02 <= (stepy / 2D))
          {
            gribCond = new GribCondition();
            double speed = 0D, dir = 0D;
            if (wpd[l][g].getTwd() != -1D && 
                wpd[l][g].getTws() != -1D)
            {
              speed = wpd[l][g].getTws();
              dir = wpd[l][g].getTwd();
            }
            else
            {
              float x = wpd[l][g].getU();
              float y = wpd[l][g].getV();
              speed = Math.sqrt(x * x + y * y); // m/s
              speed *= 3.600D; // km/h
              speed /= 1.852D; // knots
              dir = WWGnlUtilities.getDir(x, y);
            }
            gribCond.winddir = (int)Math.round(dir);
            gribCond.windspeed = (float)speed;
            gribCond.hgt500 = wpd[l][g].getHgt();
            gribCond.horIdx = g;
            gribCond.vertIdx = l;
            gribCond.prmsl = wpd[l][g].getPrmsl();
            gribCond.waves = wpd[l][g].getWHgt();
            gribCond.temp = wpd[l][g].getTmp();
            gribCond.rain = wpd[l][g].getRain();
            return gribCond;
          }
        }
      }
    }
  //  System.out.println(GeomUtil.decToSex(gp.getL()) + "/" + GeomUtil.decToSex(gp.getG()) + " is not in that grid...");
    return gribCond;
  }
  
  public static String formatGMTDateTime(Date date)
  {
    Date gribDate = date;
    Calendar cal = new GregorianCalendar();
    cal.setTime(gribDate);
    
    int year    = cal.get(Calendar.YEAR);
    int month   = cal.get(Calendar.MONTH);
    int day     = cal.get(Calendar.DAY_OF_MONTH);
    int hours   = cal.get(Calendar.HOUR_OF_DAY);
    int minutes = cal.get(Calendar.MINUTE);
    int seconds = cal.get(Calendar.SECOND);
    
    String formatted = Integer.toString(year) + "-" + WWGnlUtilities.MONTH[month] + "-" + WWGnlUtilities.DF2.format(day) + " " + WWGnlUtilities.DF2.format(hours) + ":" + WWGnlUtilities.DF2.format(minutes) + ":" + WWGnlUtilities.DF2.format(seconds) + " GMT";
    return formatted;
  }
  
  public static void main1(String[] args)
  {
    System.out.println("123 is" + (isBetween(123,    0,  200)?" ":" not") + " between " + 0 + " and " + 200);
    System.out.println("123 is" + (isBetween(123,    0, -200)?" ":" not") + " between " + 0 + " and " + -200);
    System.out.println("123 is" + (isBetween(123, -100,  200)?" ":" not") + " between " + -100 + " and " + 200);
  }
  
}
