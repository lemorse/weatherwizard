package chartview.routing.polars;

import chartview.ctx.WWContext;
import chartview.gui.util.param.ParamData;
import chartview.gui.util.param.ParamPanel;
import java.io.File;

import oracle.xml.parser.v2.DOMParser;
import oracle.xml.parser.v2.NSResolver;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLParser;

public class PolarHelper
{
  private static double coeff[][] = null;

  public static void refreshCoeffs()
  {
    String fName = ((ParamPanel.DataFile) ParamPanel.data[ParamData.POLAR_FILE_LOC][1]).toString();
    WWContext.getInstance().fireLogging("Parsing " + fName);
    DOMParser parser = WWContext.getInstance().getParser();
    try
    {
      synchronized (parser)
      {
        parser.setValidationMode(XMLParser.NONVALIDATING);
        parser.parse(new File(fName).toURI().toURL());
        XMLDocument doc = parser.getDocument();
        NSResolver nsr = new NSResolver()
          {
            public String resolveNamespacePrefix(String string)
            {
              return "http://www.logisail.com/polars";
            }
          };
        
        int w = Integer.parseInt(doc.selectNodes("/pol:polar-coeff-function/@polar-degree", nsr).item(0).getNodeValue()) + 1;
        int h = Integer.parseInt(doc.selectNodes("/pol:polar-coeff-function/@polar-coeff-degree", nsr).item(0).getNodeValue()) + 1;
  //    System.out.println("H:" + h + ", W:"+ w);
        coeff = new double[w][h];
        for (int i=0; i<w; i++)
        {
          for (int j=0; j<h; j++)
          {
            coeff[i][j] = Double.parseDouble(doc.selectNodes("/pol:polar-coeff-function/pol:polar-coeff[@degree=" + (w - i - 1) + "]/pol:coeff[@degree=" + (h - j - 1) + "]", nsr).item(0).getFirstChild().getNodeValue());
          }
        }
      }
    }
    catch (Exception ex)
    {
      WWContext.getInstance().fireExceptionLogging(ex);
      ex.printStackTrace();
    }
  }
  
  public static double getSpeed(double tws, double twa)
  {
    return getSpeed(tws, twa, 1D);
  }
  
  public static double getSpeed(double tws, double twa, double speedCoeff)
  {
    if (coeff == null)
      refreshCoeffs();
      
    double speed = 0.0D;
    double[] metaCoeff = new double[coeff.length];
    for (int i=0; i<coeff.length; i++)
    {
      metaCoeff[i] = f(tws, coeff[i]);
    }
    double angle = twa;
    if (angle > 180D)
      angle = 360D - angle;
    speed = f(angle, metaCoeff) * speedCoeff;
    
    return speed;
  }
  
  private static double f(double x, double[] coeff)
  {
    double y = 0D;
    for (int i=0; i<coeff.length; i++)
      y += (Math.pow(x, (coeff.length - 1 - i)) * coeff[i]);
    return y;
  }

  public static void main(String args[])
  {
    new ParamPanel();
    refreshCoeffs();
    
    System.out.println("Speed for TWS:6 , TWA=52 = " + getSpeed(6D, 52D));
    System.out.println("Speed for TWS:10, TWA=52 = " + getSpeed(10D, 52D));
    System.out.println("Speed for TWS:20, TWA=52 = " + getSpeed(20D, 52D));
    System.out.println("Speed for TWS:6 , TWA=90 = " + getSpeed(6D, 90D));
    System.out.println("Speed for TWS:10, TWA=90 = " + getSpeed(10D, 90D));
    System.out.println("Speed for TWS:20, TWA=90 = " + getSpeed(20D, 90D));
    System.out.println("Speed for TWS:6 , TWA=150 = " + getSpeed(6D, 150D));
    System.out.println("Speed for TWS:10, TWA=150 = " + getSpeed(10D, 150D));
    System.out.println("Speed for TWS:20, TWA=150 = " + getSpeed(20D, 150D));
    System.out.println("Speed for TWS:20, TWA=250 = " + getSpeed(20D, 250D));
  }
}
