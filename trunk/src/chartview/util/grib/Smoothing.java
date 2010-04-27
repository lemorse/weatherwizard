package chartview.util.grib;

import java.util.ArrayList;

public class Smoothing
{
  // Thank you Pythagore
  public static double distance(double fromX, 
                                double fromY, 
                                double toX,   
                                double toY)
  {    
    return Math.sqrt(Math.pow(fromX - toX, 2D) + Math.pow(fromY - toY, 2D)); 
  }

  /**
   * 
   * @param dp array of DataPoint
   * @param x abscisse of the point to calculate for
   * @param y ordinate of the point to calculate for
   * @return Direction & Speed, as Doubles
   */
  public static ArrayList<Double> calculate(DataPoint[] dp, double x, double y)
  {
    ArrayList<Double> data = null;
    double totalcoeff   = 0;
    double totalangle   = 0;
    double totalspeed   = 0;
    double totalprmsl   = 0D;
    double total500htg  = 0D;
    double totaltemp    = 0D;
    double totalwheight = 0D;
    double totalrain    = 0D;
    double totalu = 0, totalv = 0;
    
    double finalangle   = 0D;
    double finalspeed   = 0D;
    double finalprmsl   = 0D;
    double final500htg  = 0D;
    double finaltemp    = 0D;
    double finalwheight = 0D;
    double finalrain    = 0D;
    float finalu = 0, finalv = 0;
    
    boolean stuck = false;

    for (int i=0; i<dp.length; i++)
    {
      double dist = Smoothing.distance(dp[i].x, dp[i].y, x, y);
      if (dist == 0D)
      {
        finalangle = dp[i].d;
        finalspeed = dp[i].s;
        finalprmsl = dp[i].prmsl;
        final500htg = dp[i].hgt500;
        finaltemp = dp[i].temp;
        finalwheight = dp[i].whgt;
        finalrain = dp[i].rain;
        finalu = dp[i].u;
        finalv = dp[i].v;
        stuck = true;
        break;
      }
      else
      {
        double coeff = 1.0 / (dist * dist);                                     
        totalcoeff += coeff;
        totalangle += (coeff * dp[i].d);
        totalspeed += (coeff * dp[i].s);
        totalprmsl += (coeff * dp[i].prmsl);
        total500htg += (coeff * dp[i].hgt500);
        totaltemp += (coeff * dp[i].temp);
        totalwheight += (coeff * dp[i].whgt);
        totalrain += (coeff * dp[i].rain);
        totalu += (coeff * dp[i].u);
        totalv += (coeff * dp[i].v);
      }
    }
    if (!stuck)
    {
      finalangle = totalangle / totalcoeff;
      finalspeed = totalspeed / totalcoeff;
      finalprmsl = totalprmsl / totalcoeff;
      final500htg = total500htg / totalcoeff;
      finaltemp = totaltemp / totalcoeff;
      finalwheight = totalwheight / totalcoeff;
      finalrain = totalrain / totalcoeff;
      finalu = (int)Math.round(totalu / totalcoeff);
      finalv = (int)Math.round(totalv / totalcoeff);
    }
    while (finalangle > 360) finalangle -= 360;
    
    data = new ArrayList<Double>(2);
    data.add(new Double(finalangle));
    data.add(new Double(finalspeed));
    data.add(new Double(finalprmsl));
    data.add(new Double(final500htg));
    data.add(new Double(finaltemp));
    data.add(new Double(finalwheight));
    data.add(new Double(finalrain));
    
    data.add(new Double(finalu));
    data.add(new Double(finalv));

    return data;
  }  
}
