package chartview.ctx;

import astro.calc.GeoPoint;
import astro.calc.GreatCircle;

import chartview.gui.util.dialog.FaxType;

import chartview.gui.toolbar.controlpanels.LoggingPanel;
import chartview.gui.util.param.ParamData;
import chartview.gui.util.param.ParamPanel;

import chartview.routing.enveloppe.custom.RoutingPoint;

import chartview.util.progress.ProgressMonitor;

import java.awt.Color;
import java.awt.Point;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import oracle.xml.parser.v2.DOMParser;
import java.util.ArrayList;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import jgrib.GribFile;

import oracle.xml.parser.v2.XMLParser;

/**
 * A singleton to hold the contextual data
 * across all the application
 */
public class WWContext
{
  public final static String VERSION_NUMBER = "0.9.0.5";
  public final static String PRODUCT_ID     = "weather_assistant.0.9.0.5";
  
  private static int debugLevel = 0;
  
  public final static String WAZ_PROTOCOL_PREFIX = "waz://";
  public final static String WAZ_EXTENSION       = ".waz";
  
  public final static String SAILMAIL_STATIONS = "sailmail-stations.xml";
  public final static String NOAA_STATIONS     = "noaa-stations.xml";
  
  public final static String CONFIG_PROPERTIES_FILE = "ww-config.properties";
  
  public final static String MANUAL_POSITION_FILE   = "manualposition.xml";
  
  private static WWContext context = null;

  private String compiled = "- unknown -";
  private Date currentUTC = null;
  
  private DOMParser parser = null;
  private GreatCircle greatCircle = null;
  private transient ArrayList<ApplicationEventListener> applicationListeners = null;
  private ProgressMonitor monitor = null;
  private ApplicationEventListener ael4monitor = null;
  
  private String lookAndFeel = "";
  private String currentGribFileName = "";
  private GribFile gribFile = null;
  
  private boolean onLine = true;
  private Boolean useGRIBWindSpeedTransparency = null;
  private Boolean useColorRangeForWindSpeed = null;
  
  private String currentComposite = "";
  
  private JFrame masterTopFrame = null;
    
  private WWContext()
  {
    parser = new DOMParser();
    parser.setValidationMode(XMLParser.NONVALIDATING);

    greatCircle = new GreatCircle();
    applicationListeners = new ArrayList<ApplicationEventListener>(2); // 2: Initial Capacity
  }
  
  public static synchronized WWContext getInstance()
  {
    if (context == null)
      context = new WWContext();
    return context;
  }
  
  public void release()
  {
    parser = null;
    context = null;
    System.gc();
  }

  public ArrayList<ApplicationEventListener> getListeners()
  {
    return applicationListeners;
  }
  
  public synchronized void addApplicationListener(ApplicationEventListener l)
  {
    if (!this.getListeners().contains(l))
    {      
      this.getListeners().add(l);
//    System.out.println("Now having " + Integer.toString(this.getListeners().size()) + " listener(s) - Just added [" + l.toString() + "]");
    }
  }

  public synchronized void removeApplicationListener(ApplicationEventListener l)
  {
    this.getListeners().remove(l);
//  System.out.println("Now having " + Integer.toString(this.getListeners().size()) + " listener(s) - Just removed [" + l.toString() + "]");
  }

  public void fireLoadDynamicComposite(String compositeName)
  {
    for (int i=0; i < WWContext.getInstance().getListeners().size(); i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.patternFileOpen(compositeName);
    }
  }

  public void fireSetCompositeRequested()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setCompositeRequested();
    }    
  }

  public void fireLogging(String str)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.log(str, LoggingPanel.GREEN_STYLE);
    }    
  }

  public void fireReloadFaxTree()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.reloadFaxTree();
    }    
  }
  public void fireReloadGRIBTree()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.reloadGRIBTree();
    }    
  }
  public void fireReloadCompositeTree()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.reloadCompositeTree();
    }    
  }
  public void fireReloadPatternTree()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.reloadPatternTree();
    }    
  }
    
  public void fireLogging(String str, int idx)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.log(str, idx);
    }    
  }
  
  public void fireFaxSelectedForPreview(String str)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.faxSelectedForPreview(str);
    }    
  }
  
  public void fireExceptionLogging(Exception ex)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      StringWriter sw = new StringWriter();
      ex.printStackTrace(new PrintWriter(sw));
      l.log(new String(sw.getBuffer()), LoggingPanel.RED_STYLE);
    }    
  }
  
  public void fireChartRepaint() 
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.chartRepaintRequested();
    }    
  }
  
  public void fireNMEAAcquisition(boolean b)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setNMEAAcquisition(b);
    }    
  }

  public void fireFaxesLoaded(FaxType[] ft)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.faxesLoaded(ft);
    }    
  }
  
  public void fireActiveFaxChanged(FaxType ft)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.activeFaxChanged(ft);
    }    
  }
  
  public void fireAllFaxesSelected()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.allFaxesSelected();
    }    
  }

  public void fireSetCursor(int shape)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.toggleGrabScroll(shape);
    }    
  }

  public void fireGribInfo(int currentIndex, 
                           int maxIndex, 
                           String gribInfo2, 
                           String gribInfo3, 
                           String gribInfo4,
                           boolean displayWindOnly,
                           boolean thereIsWind, 
                           boolean thereIsPrmsl,
                           boolean thereIsHgt500, 
                           boolean thereIsTemp,
                           boolean thereIsWaves,
                           boolean thereIsRain,
                           boolean displayWind,
                           boolean display3DPRMSL,
                           boolean display3D500hgt,
                           boolean display3DTemp,
                           boolean display3DWaves,
                           boolean display3DRain)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setGribInfo(currentIndex, 
                    maxIndex, 
                    gribInfo2, 
                    gribInfo3, 
                    gribInfo4, 
                    displayWindOnly, 
                    thereIsWind, 
                    thereIsPrmsl, 
                    thereIsHgt500, 
                    thereIsTemp, 
                    thereIsWaves,
                    thereIsRain,
                    displayWind,
                    display3DPRMSL,
                    display3D500hgt,
                    display3DTemp,
                    display3DWaves,
                    display3DRain);
    }    
  }
  
  public void fireGribIndex(int idx)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setGribIndex(idx);
    }    
  }
  
  
  public void fireSyncGribWithDate(Date date)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.syncGribWithDate(date);
    }    
  }
  
  public void fireFaxLoaded()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.faxLoaded();
    }    
  }
  
  public void fireFaxUnloaded()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.faxUnloaded();
    }    
  }
  
  public void fireGribLoaded(String gribFileName)
  {
    currentGribFileName = gribFileName;
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.gribLoaded();
    }    
  }
  
  public void fireGribUnloaded()
  {
    currentGribFileName = "";
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.gribUnloaded();
    }    
  }

  public void fireMoreThanOneGrib()
  {
//  currentGribFile = "";
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.thereIsMoreThanOneGrib();
    }    
  }

  public void fireEnable3DTab(boolean b)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.enable3DTab(b);
    }    
  }
  
  public void fireStartGRIBAnimation()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.startGribAnimation();
    }    
  }
  
  public void fireStopGRIBAnimation()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.stopGribAnimation();
    }    
  }
  
  public void firePlotBoatAt(GeoPoint gp, int hdg)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.plotBoatAt(gp, hdg);
    }    
  }
    
  public void fireManuallyEnterBoatPosition(GeoPoint gp, int hdg)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.manuallyEnterBoatPosition(gp, hdg);
    }    
  }
    
  public void fireSetLoading(boolean b)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setLoading(b);
    }    
  }
  
  public void fireSetLoading(boolean b, String s)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setLoading(b, s);
    }    
  }
  
  public void fireStopAnyLoading()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.stopAnyLoadingProgressBar();
    }    
  }
      
  public void fireSetStatus(String str)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setStatus(str);
    }    
  }
  
  public void fireChartLineColorChanged(Color c)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.chartLineColorChanged(c);
    }    
  }
  
  public void fireChartBackgroundColorChanged(Color c)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.chartBackgroundColorChanged(c);
    }    
  }
  
  public void fireDDZColorChanged(Color c)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.ddzColorChanged(c);
    }    
  }
  
  public void fireGribBackward()
  {
    for (int i=0; i < WWContext.getInstance().getListeners().size(); i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.gribBackward();
    }    
  }

  public void fireGribForward()
  {
    for (int i=0; i < WWContext.getInstance().getListeners().size(); i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.gribForward();
    }    
  }

  public void fireGridColorChanged(Color c)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.gridColorChanged(c);
    }    
  }
  
  public void fireLookAndFeelChanged(String laf)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.lookAndFeelChanged(laf);
    }    
  }
  
  public void fireDDZoomConfirmChanged(boolean b)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.ddZoomConfirmChanged(b);
    }    
  }  
  
  public void fireChartLineThicknessChanged(int th)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.chartLineThicknessChanged(th);
    }    
  }
  
  public void fireGribSmoothing(int smooth)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setGribSmoothing(smooth);
    }    
  }
  
  public void fireGribTimeSmoothing(int smooth)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setGribTimeSmoothing(smooth);
    }    
  }
  
  public void fireNew500mbObj(ArrayList<Point> al)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.new500mbObj(al);
    }    
  }
  
  public void fireNo500mbObj()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.no500mbObj();
    }    
  }
  
  public void fireNewRainObj(ArrayList<Point> al)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.newRainObj(al);
    }    
  }
  
  public void fireNoRainObj()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.noRainObj();
    }    
  }
  public void fireNewPrmslObj(ArrayList<Point> al)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.newPrmslObj(al);
    }    
  }
  
  public void fireNoPrmslObj()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.noPrmslObj();
    }    
  }
  
  public void fireNewTmpObj(ArrayList<Point> al)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.newTmpObj(al);
    }    
  }
  
  public void fireNoTmpObj()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.noTmpObj();
    }    
  }
  
  public void fireNewWaveObj(ArrayList<Point> al)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.newWaveObj(al);
    }    
  }
  
  public void fireNoWaveObj()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.noWaveObj();
    }    
  }

  public void fireNewTWSObj(ArrayList<Point> al)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.newTWSObj(al);
    }    
  }
  
  public void fireNoTWSObj()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.noTWSObj();
    }    
  }

  public void fireTWSDisplayed(boolean b)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setTWSDisplayed(b);
    }    
  }

  public void firePRMSLDisplayed(boolean b)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setPRMSLDisplayed(b);
    }    
  }

  public void fire500MBDisplayed(boolean b)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.set500MBDisplayed(b);
    }    
  }

  public void fireWAVESDisplayed(boolean b)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setWAVESDisplayed(b);
    }    
  }

  public void fireTEMPDisplayed(boolean b)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setTEMPDisplayed(b);
    }    
  }

  public void fireRAINDisplayed(boolean b)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setRAINDisplayed(b);
    }    
  }

  public void fireZoom3D(double d)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setZoom3D(d);
    }    
  }
  
  public void fireGeneratePattern()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.generatePattern();
    }    
  }
  
  public void fireLoadWithPattern()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.loadWithPattern();
    }    
  }
  
  public void fireSetProjection(int p)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setProjection(p);
    }    
  }
  
  public void fireSetGlobeParameters(double lat, double lng, double tilt, boolean b)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setGlobeProjPrms(lat, lng, tilt, b);
    }    
  }
  
  public void fireSetGlobeParameters(double lat, double lng)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setGlobeProjPrms(lat, lng);
    }    
  }
  
  public void fireSetContactParallel(double p)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setContactParallel(p);
    }    
  }

  public void fireSetSatelliteParameters(double l, double g, double a, boolean b)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener lsnr = this.getListeners().get(i);
      lsnr.setSatellitePrms(l, g, a, b);
    }    
  }

  public void fireWindInGoogle()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.showWindInGoogle();
    }        
  }
  
  public void fireRoutingAvailable(boolean b, ArrayList<RoutingPoint> bestRoute)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.routingAvailable(b, bestRoute);
    }        
  }
  
  public void fireRoutingForecastAvailable(boolean b, ArrayList<RoutingPoint> route)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.routingForecastAvailable(b, route);
    }        
  }
  
  public void fireReadFax()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.readFax();
    }        
  }
  
  public void fireReadGRIB()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.readGRIB();
    }        
  }
  
  public void fireReadPattern()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.readPattern();
    }        
  }
  
  public void fireReadComposite()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.readComposite();
    }        
  }

  public void fireNetworkOK(boolean b)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.networkOK(b);
    }        
  }

  public void fireProgressing()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.progressing();
    }        
  }

  public void fireProgressing(String str)
  {
    for (int i = 0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.progressing(str);
    }
  }

  public void fireGRIBSliceInfoRequested(double d)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.GRIBSliceInfoRequested(d);
    }        
  }
  
  public void fireGRIBSliceInfoRequestStop()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.GRIBSliceInfoRequestStop();
    }        
  }

  public synchronized void fireInterruptProcess()
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.interruptProgress();
    }        
  }

  public void fireGRIBWindValue(int twd, float tws)
  {    
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setGRIBWindValue(twd, tws);
    }        
  }

  public void fireGRIBPRMSLValue(float prmsl)
  {    
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setGRIBPRMSLValue(prmsl);
    }        
  }

  public void fireGRIB500HGTValue(float hgt500)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setGRIB500HGTValue(hgt500);
    }        
  }

  public void fireGRIBWaveHeight(float wh)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setGRIBWaveHeight(wh);
    }        
  }

  public void fireGRIBTemp(float t)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setGRIBTemp(t);
    }        
  }

  public void fireGRIBprate(float prate)
  {
    for (int i=0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setGRIBprate(prate);
    }        
  }

  public void fireSetGRIBData(int twd, float tws, float prmsl, float hgt500, float wh, float t, float prate)
  {
    for (int i = 0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.setGRIBData(twd, tws, prmsl, hgt500, wh, t, prate);
    }
  }

  public void fireGribDataPanelClosed()
  {
    for (int i = 0; i < this.getListeners().size(); i++)
    {
      ApplicationEventListener l = this.getListeners().get(i);
      l.gribDataPanelClosed();
    }
  }

  public void fireGoogleMapRequested()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.googleMapRequested();
    }    
  }

  public void fireGoogleEarthRequested()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.googleEarthRequested();
    }    
  }

  public void setUseGRIBWindSpeedTransparency(Boolean useGRIBWindSpeedTransparency)
  {
    this.useGRIBWindSpeedTransparency = useGRIBWindSpeedTransparency;
  }

  public Boolean getUseGRIBWindSpeedTransparency()
  {
    if (useGRIBWindSpeedTransparency == null)
      useGRIBWindSpeedTransparency = ((Boolean)ParamPanel.data[ParamData.USE_TRANSPARENT_GRIB_WIND][1]);
    return useGRIBWindSpeedTransparency;
  }

  public void setCurrentComposite(String currentComposite)
  {
    this.currentComposite = currentComposite;
  }

  public String getCurrentComposite()
  {
    return currentComposite;
  }

  public void setUseColorRangeForWindSpeed(Boolean useColorRangeForWindSpeed)
  {
    this.useColorRangeForWindSpeed = useColorRangeForWindSpeed;
  }

  public Boolean getUseColorRangeForWindSpeed()
  {
    if (useColorRangeForWindSpeed == null)
      useColorRangeForWindSpeed = ((Boolean)ParamPanel.data[ParamData.COLOR_RANGE][1]);
    return useColorRangeForWindSpeed;
  }

  public void setMasterTopFrame(JFrame masterTopFrame)
  {
    this.masterTopFrame = masterTopFrame;
  }

  public JFrame getMasterTopFrame()
  {
    return masterTopFrame;
  }

  public void setCompiled(String compiled)
  {
    this.compiled = compiled;
  }

  public String getCompiled()
  {
    return compiled;
  }

  public void setParser(DOMParser parser)
  {
    this.parser = parser;
  }

  public DOMParser getParser()
  {
    return parser;
  }

  public void setGreatCircle(GreatCircle greatCircle)
  {
    this.greatCircle = greatCircle;
  }

  public GreatCircle getGreatCircle()
  {
    return greatCircle;
  }

  public void setApplicationListeners(ArrayList<ApplicationEventListener> applicationListeners)
  {
    this.applicationListeners = applicationListeners;
  }

  public /* synchronized */ void setMonitor(ProgressMonitor monitor)
  {
    synchronized (this)
    {
      this.monitor = monitor;
    }
  }

  public /* synchronized */ ProgressMonitor getMonitor()
  {
    synchronized (this)
    {
      return monitor;
    }
  }

  public synchronized void setAel4monitor(ApplicationEventListener ael4monitor)
  {
    synchronized (this)
    {
      this.ael4monitor = ael4monitor;
    }
  }

  public synchronized ApplicationEventListener getAel4monitor()
  {
    synchronized (this)
    {    
      return ael4monitor;
    }
  }

  public void setLookAndFeel(String lookAndFeel)
  {
    this.lookAndFeel = lookAndFeel;
  }

  public String getLookAndFeel()
  {
    return lookAndFeel;
  }

  public void setCurrentGribFileName(String currentGribFile)
  {
    this.currentGribFileName = currentGribFile;
  }

  public String getCurrentGribFileName()
  {
    return currentGribFileName;
  }

  public void setOnLine(boolean onLine)
  {
    this.onLine = onLine;
  }

  public boolean isOnLine()
  {
    return onLine;
  }

  public void setGribFile(GribFile gribFile)
  {
//  System.out.println("SetGribFile");
    this.gribFile = gribFile;
  }

  public GribFile getGribFile()
  {
//  System.out.println("getGribFile is " + (gribFile==null?"":"not ") + "null");
    return gribFile;
  }

  public void setCurrentUTC(Date currentUTC)
  {
    this.currentUTC = currentUTC;
  }

  public Date getCurrentUTC()
  {
    return currentUTC;
  }

  public static void setDebugLevel(int debugLevel)
  {
    WWContext.debugLevel = debugLevel;
  }

  public static int getDebugLevel()
  {
    return debugLevel;
  }

  public static class ToolFileFilter extends FileFilter
  {

    public boolean accept(File f)
    {
      if (f != null)
      {
        if (f.isDirectory())
          return true;
        String extension = getExtension(f);
        if (filters == null)
          return true;
        if (extension != null && filters.get(getExtension(f)) != null)
          return true;
      }
      return false;
    }

    public String getExtension(File f)
    {
      if(f != null)
      {
        String filename = f.getName();
        int i = filename.lastIndexOf('.');
        if(i > 0 && i < filename.length() - 1)
          return filename.substring(i + 1).toLowerCase();
      }
      return null;
    }

    public void addExtension(String extension)
    {
      if (filters == null)
        filters = new Hashtable<String, Object>(5);
      filters.put(extension.toLowerCase(), this);
      fullDescription = null;
    }

    public String getDescription()
    {
      if (fullDescription == null)
      {
        if(description == null || isExtensionListInDescription())
        {
          if (description != null)
            fullDescription = description;
          if (filters != null)
          {
            fullDescription += " (";
            Enumeration extensions = filters.keys();
            if (extensions != null)
              for (fullDescription += "." + (String)extensions.nextElement(); extensions.hasMoreElements(); fullDescription += ", " + (String)extensions.nextElement());
            fullDescription += ")";
          }
          else
            fullDescription = "";
        } 
        else
        {
          fullDescription = description;
        }
      }
      return fullDescription;
    }

    public void setDescription(String description)
    {
      this.description = description;
      fullDescription = null;
    }

    public void setExtensionListInDescription(boolean b)
    {
      useExtensionsInDescription = b;
      fullDescription = null;
    }

    public boolean isExtensionListInDescription()
    {
      return useExtensionsInDescription;
    }

    private String TYPE_UNKNOWN;
    private String HIDDEN_FILE;
    private Hashtable<String, Object> filters;
    private String description;
    private String fullDescription;
    private boolean useExtensionsInDescription;

    public ToolFileFilter()
    {
      this((String)null, null);
    }

    public ToolFileFilter(String extension)
    {
      this(extension, null);
    }

    public ToolFileFilter(String extension, String description)
    {
      this(new String[] { extension }, description);
    }

    public ToolFileFilter(String filters[])
    {
      this(filters, null);
    }

    public ToolFileFilter(String filter[], String description)
    {
      TYPE_UNKNOWN = "Type Unknown";
      HIDDEN_FILE = "Hidden File";
      this.filters = null;
      this.description = null;
      fullDescription = null;
      useExtensionsInDescription = true;
      if (filter != null)
      {
        this.filters = new Hashtable<String, Object>(filter.length);
        for (int i = 0; i < filter.length; i++)
          addExtension(filter[i]);
      }
      setDescription(description);
    }
  }
}
