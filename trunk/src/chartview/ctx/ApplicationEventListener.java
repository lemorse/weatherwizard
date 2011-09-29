package chartview.ctx;


import astro.calc.GeoPoint;

import chartview.gui.util.dialog.FaxType;

import chartview.routing.enveloppe.custom.RoutingPoint;

import chartview.util.WWGnlUtilities;

import java.awt.Color;
import java.awt.Point;

import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;

public abstract class ApplicationEventListener implements EventListener 
{
  public void applicationLoaded() {}
  public void collapseExpandToolBar(boolean b) {}
  public void imageUp() {}
  public void imageDown() {}
  public void imageLeft() {}
  public void imageRight() {}
  public void imageZoomin() {}
  public void imageZoomout() {}

  public void chartUp() {}
  public void chartDown() {}
  public void chartLeft() {}
  public void chartRight() {}
  public void chartZoomin() {}
  public void chartZoomout() {}
  
  public void allLayerZoomIn() {}
  public void allLayerZoomOut() {}
  
  public void chartLineColorChanged(Color c) {}
  public void chartLineThicknessChanged(int i) {}
  public void chartBackgroundColorChanged(Color c) {}
  public void ddzColorChanged(Color c) {}
  public void gridColorChanged(Color c) {}
  
  public void lookAndFeelChanged(String laf) {}
  
  public void store() {}
  public void storeAs() {}
  public void restore() {}
  
  public void generatePattern() {}
  public void loadWithPattern() {}
  public void loadWithPattern(String patternName) {}
  
  public void setCompositeRequested() {}
  
  public void setProjection(int p) {}
  public void setContactParallel(double d) {}
  public void setGlobeProjPrms(double lat, double lng, double tilt, boolean b) {}
  public void setGlobeProjPrms(double lat, double lng) {}
  public void setSatelliteProjPrms(double lat, double lng, double alt, boolean b) {}
  
  public void setSatellitePrms(double l, double g, double a, boolean b) {}

  public void rotate(double d) {}

  public void toggleGrabScroll(int i) {}
  
  public void setGribIndex(int idx) {}
  public void gribForward() {}
  public void gribBackward() {}
  public void gribAnimate() {}
  public void startGribAnimation() {}
  public void stopGribAnimation() {}
  public void changeAnimationSpeed(int interval) {}
  
  public void thereIsMoreThanOneGrib() {}
  
  public void gribLoaded() {}
  public void gribUnloaded() {}
  
  public void faxLoaded() {}
  public void faxUnloaded() {}
  
  public void faxesLoaded(FaxType[] ft) {}
  public void activeFaxChanged(FaxType ft) {}
  public void allFaxesSelected() {}
  
  public void setGribInfo(int currentIndex, 
                          int maxIndex, 
                          String gribInfo2, 
                          String gribInfo3, 
                          String gribInfo4,
                          boolean windOnly,
                          boolean wind, 
                          boolean prmsl,
                          boolean hgt500, 
                          boolean temp,
                          boolean waves,
                          boolean rain,
                          boolean displayWind,
                          boolean display3DPRMSL,
                          boolean display3D500hgt,
                          boolean display3DTemp,
                          boolean display3DWaves,
                          boolean display3DRain) {}
  
  public void gribFileOpen(String str) {}
  
  public void syncGribWithDate(Date date) {}
  
  public void faxFileOpen(String str) {}
  public void compositeFileOpen(String str) {}
  public void patternFileOpen(String str) {}
  
  public void chartRepaintRequested() {}
  public void setNMEAAcquisition(boolean b) {}
  
  public void plotBoatAt(GeoPoint gp, int hdg) {}
  public void manuallyEnterBoatPosition(GeoPoint gp, int hdg) {}
  
  public void setLoading(boolean b) { setLoading(b, WWGnlUtilities.buildMessage("loading")); }
  public void setLoading(boolean b, String mess) {}
  public void stopAnyLoadingProgressBar() {}
  public void setStatus(String str) {}
  public void setGribSmoothing(int i) {}
  public void setGribTimeSmoothing(int i) {}
  public void new500mbObj(ArrayList<Point> al) {}
  public void no500mbObj() {}
  public void newPrmslObj(ArrayList<Point> al) {}
  public void noPrmslObj() {}
  public void newTmpObj(ArrayList<Point> al) {}
  public void noTmpObj() {}
  public void newWaveObj(ArrayList<Point> al) {}
  public void noWaveObj() {}
  public void newRainObj(ArrayList<Point> al) {}
  public void noRainObj() {}
  
  public void newTWSObj(ArrayList<Point> al) {}
  public void noTWSObj() {}

  public void setTWSDisplayed(boolean b) {}
  public void setPRMSLDisplayed(boolean b) {}
  public void set500MBDisplayed(boolean b) {}
  public void setWAVESDisplayed(boolean b) {}
  public void setTEMPDisplayed(boolean b) {}
  public void setRAINDisplayed(boolean b) {}

  public void setZoom3D(double d) {}
  public void showWindInGoogle() {}
  
  public void googleMapRequested() {}
  public void googleEarthRequested() {}
  
  public void readFax() {}
  public void readGRIB() {}
  public void readPattern() {}
  public void readComposite() {}
  
  public void log(String str) {}
  public void log(String str, int idx) {}
  
  public void faxSelectedForPreview(String s) {}
  public void ddZoomConfirmChanged(boolean b) {}
  
  public void routingAvailable(boolean b, ArrayList<RoutingPoint> bestRoute) {}
  public void routingForecastAvailable(boolean b, ArrayList<RoutingPoint> route) {}
  
  public void reloadFaxTree() {}
  public void reloadGRIBTree() {}
  public void reloadCompositeTree() {}
  public void reloadPatternTree() {}
  
  public void networkOK(boolean b) {}  
  public void enable3DTab(boolean b) {}
  
  public void progressing() {}
  public void progressing(String mess) {}
  public void interruptProgress() {}
  
  public void GRIBSliceInfoRequested(double d) {}
  public void GRIBSliceInfoRequestStop() {}
  
  public void setGRIBWindValue(int twd, float tws) {}
  public void setGRIBPRMSLValue(float prmsl) {}
  public void setGRIB500HGTValue(float hgt500) {}
  public void setGRIBWaveHeight(float wh) {}
  public void setGRIBTemp(float t) {}
  public void setGRIBprate(float prate) {}
  
  public void setGRIBData(int twd, float tws, float prmsl, float hgt500, float wh, float t, float prate) {}
  public void gribDataPanelClosed() {}
}
