package chartview.gui.right;

import astro.calc.GeoPoint;
import astro.calc.GreatCircle;

import chart.components.ui.ChartPanel;
import chart.components.ui.ChartPanelInterface;
import chart.components.ui.ChartPanelParentInterface;
import chart.components.ui.ChartPanelParentInterface_II;
import chart.components.util.World;

import chartview.ctx.ApplicationEventListener;

import chartview.util.grib.GRIBDataUtil;
import chartview.util.grib.GribHelper;

import chartview.gui.toolbar.controlpanels.MainZoomPanel;
import chartview.gui.toolbar.controlpanels.LoggingPanel;
import chartview.gui.util.dialog.StartRoutingPanel;
import chartview.gui.util.dialog.FaxPatternTablePanel;
import chartview.gui.util.dialog.FaxPatternType;
import chartview.gui.util.dialog.FaxType;
import chartview.gui.util.param.ParamData;
import chartview.gui.util.param.ParamPanel;
import chartview.gui.util.print.print.PrintUtilities;

import chartview.util.http.HTTPClient;

import chartview.routing.enveloppe.custom.RoutingClientInterface;
import chartview.routing.enveloppe.custom.RoutingPoint;
import chartview.routing.enveloppe.custom.RoutingUtil;

import chartview.util.CurveUtil;
import chartview.util.WWGnlUtilities;
import chartview.util.ImageUtil;
import chartview.util.RelativePath;
import chartview.ctx.WWContext;

import chartview.gui.toolbar.controlpanels.ChartCommandPanelToolBar;
import chartview.gui.util.dialog.GRIBSlicePanel;
import chartview.gui.util.dialog.PrintOptionsPanel;
import chartview.gui.util.dialog.RoutingOutputFlavorPanel;
import chartview.gui.util.dialog.TwoFilePanel;
import chartview.gui.util.dialog.places.PlacesTablePanel;

import chartview.gui.util.panels.GRIBVisualPanel;
import chartview.gui.util.transparent.TransparentFrame;

import chartview.gui.util.transparent.TransparentPanel;

import chartview.routing.DatedGribCondition;

import chartview.util.DynFaxUtil;
import chartview.util.GoogleUtil;
import chartview.util.SearchUtil;
import chartview.util.progress.ProgressMonitor;
import chartview.util.progress.ProgressUtil;

import coreutilities.Utilities;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import java.net.URL;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.EventObject;
import java.util.GregorianCalendar;
import java.util.Iterator;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jgrib.GribFile;

import oracle.xml.parser.v2.DOMParser;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import user.util.GeomUtil;

@SuppressWarnings("serial")
public class CommandPanel 
     extends JPanel
  implements ChartPanelParentInterface_II, 
             RoutingClientInterface
{
  private long id = 0L;
  private final static Color CUSTOM_LIGHT_BLUE = new Color(85, 115, 170);
  
  private CompositeTabbedPane parent = null;
  private CommandPanel instance = this;
  
  private Cursor backupCursor = null;
  private Cursor cursorOverWayPoint = null;
  private Cursor cusorDraggingWayPoint = null;
  
  private int defaultWindOption = Integer.parseInt(((ParamPanel.WindOptionList)(ParamPanel.data[ParamData.PREFERRED_WIND_DISPLAY][ParamData.VALUE_INDEX])).getStringIndex());
  
  private BorderLayout borderLayout1;
  private JScrollPane chartPanelScrollPane;  
  private JSplitPane jSplitPane;
  private JPanel dummyGribSlicePlaceHolder;
  protected ChartPanel chartPanel;
  
  protected transient FaxImage[] faxImage = null;
  private int currentFaxIndex = -1;
  
  private String currentComment = "";
  
  private JPanel bottomPanel;
  private JPanel bottomLeftPanel;
  private JPanel bottomRightPanel;
  private JPanel rightVerticalPanel;
  private JPanel rightBottomPanel;
  private JPanel blurSharpPanel;
  private JRadioButton blurRadioButton;
  private JRadioButton noChangeRadioButton;
  private JRadioButton sharpRadioButton;
  private ButtonGroup blurSharpGroup = new ButtonGroup();
  
  private int previousBlurSharpOption = ImageUtil.NO_CHANGE;
  private int blurSharpOption = ImageUtil.NO_CHANGE;
  
  private JPanel checkBoxCompositePanel;
  private JPanel checkBoxPanelHolder;
  private JScrollPane checkBoxPanelScrollPane;
  private JButton cbPanelScrollUpButton;
  private JButton cbPanelScrollDownButton;
  
  private JLabel displayLabel       = new JLabel(WWGnlUtilities.buildMessage("display"));
  private JComboBox displayComboBox = new JComboBox();
  private JLabel boundariesLabel    = new JLabel(" - ");
  
  private JSlider faxOpacitySlider     = new JSlider();  
  private float faxUserOpacity         = ((Float) ParamPanel.data[ParamData.FAX_TRANSPARENCY][ParamData.VALUE_INDEX]).floatValue();

  private JSlider gribOpacitySlider     = new JSlider();  
  private float gribUserOpacity         = 0.75f; // ((Float) ParamPanel.data[ParamData.FAX_TRANSPARENCY][ParamData.VALUE_INDEX]).floatValue();

  protected JCheckBox[] compositeCheckBox = null;
  protected JRadioButton[] compositeRadioButton = null;
  protected ButtonGroup buttonGroup = new ButtonGroup();
  protected JCheckBox[] contourCheckBox = null;
  
  public final static int CHECKBOX_OPTION    = 0;
  public final static int RADIOBUTTON_OPTION = 1;
  private int checkBoxPanelOption = CHECKBOX_OPTION;
  
  protected static Color initialGribWindBaseColor = (Color) ParamPanel.data[ParamData.GRIB_WIND_COLOR][ParamData.VALUE_INDEX];
  protected static Color initialGribCurrentBaseColor = (Color) ParamPanel.data[ParamData.GRIB_CURRENT_COLOR][ParamData.VALUE_INDEX];
  
  // Indexes of the above
  private final static int WIND_SPEED    = 0; // Always displayed.
  private final static int PRMSL         = 1;
  private final static int HGT500        = 2;
  private final static int TEMPERATURE   = 3;
  private final static int WAVES         = 4;
  private final static int RAIN          = 5;
  private final static int CURRENT_SPEED = 6;
  
  private static int temperatureUnit = Integer.parseInt(((ParamPanel.TemperatureUnitList)(ParamPanel.data[ParamData.TEMPERATURE_UNIT][ParamData.VALUE_INDEX])).getStringIndex());
  private final static String[] dataLabels = { "WIND", "PRMSL", "500HGT", "AIRTMP", "WAVES", "RAIN",  "CURRENT" };
  private final static String[] units      = { " kts", " mb",   " m",     
                                                                          ParamPanel.TemperatureUnitList.getLabel(temperatureUnit), // was "°C"    
                                                                                    " m",    " mm/h", " kts" };
  private double[][] boundaries = new double[dataLabels.length][2];
  
  private JLabel statusLabel = new JLabel("");

  private MainZoomPanel dataPanel = null;
  
  private double lgInc = ((Double) ParamPanel.data[ParamData.DEFAULT_CHART_INC_VALUE][ParamData.VALUE_INDEX]).doubleValue();  
  private int faxInc   = ((Integer) ParamPanel.data[ParamData.DEFAULT_FAX_INC_VALUE][ParamData.VALUE_INDEX]).intValue();  
  
  protected String gribFileName = "";
  protected String gribRequest = "";
  protected transient GribHelper.GribConditionData wgd[] = null;
  protected transient GribHelper.GribConditionData originalWgd[] = null;
  
  private int gribIndex = 0;
  protected boolean drawHeavyDot = false;
  protected boolean drawWindColorBackground = false;
  private int smooth = 1;
  private int timeSmooth = 1;
  private boolean alreadyAskedAboutGRIB = false;
  
  private boolean drawChart = true;
  private boolean drawIsochrons = ((Boolean)ParamPanel.data[ParamData.SHOW_ISOCHRONS][ParamData.VALUE_INDEX]).booleanValue();

  private boolean drawBestRoute = true;
  private boolean drawGRIB = true;
  private boolean enableGRIBSlice = false;
  
  private boolean settingGRIBInfo = false;

  private CommandPanelPopup popup = null;
  private CheckBoxPanelPopup cbpPopup = null;
  
  private String tooltipMess = null;
  private boolean replace = false;
  
  private transient GeoPoint fromGRIBSlice = null, toGRIBSlice = null;
  private double gribSliceInfo = -1D;
  
  protected transient GeoPoint from = null, to = null, closest = null;
  protected transient boolean insertRoutingWP = false;
  protected List<GeoPoint> intermediateRoutingWP = null;
  
  private transient RoutingPoint closestPoint = null;
  private List<RoutingPoint> bestRoute = null;
  private transient GeoPoint boatPosition = null;
  private int boatHeading = -1;
  private transient GeoPoint wp2highlight = null;
  private transient GeoPoint wpBeingDragged = null;
  
  private int nmeaPollingInterval = ((Integer) ParamPanel.data[ParamData.NMEA_POLLING_FREQ][ParamData.VALUE_INDEX]).intValue();
  private boolean goNmea          = false;
  private transient Thread nmeaThread       = null;
  
  protected boolean routingMode         = false;
  protected boolean routingForecastMode = false;
  protected boolean routingOnItsWay     = false;
  private boolean postitOnRoute         = ((Boolean)ParamPanel.data[ParamData.SHOW_ROUTING_LABELS][ParamData.VALUE_INDEX]).booleanValue();;

//private double timeInterval         = ((Double) ParamPanel.data[ParamData.ROUTING_TIME_INTERVAL][ParamData.VALUE_INDEX]).doubleValue(); // 6.0;
//private static int routingForkWidth = ((Integer) ParamPanel.data[ParamData.ROUTING_FORK_WIDTH][ParamData.VALUE_INDEX]).intValue(); // 50;
//private static int routingStep      = ((Integer) ParamPanel.data[ParamData.ROUTING_STEP][ParamData.VALUE_INDEX]).intValue(); // 10;
    
  protected transient List<List<RoutingPoint>> allCalculatedIsochrons = new ArrayList<List<RoutingPoint>>();
  
  private boolean displayPageSize = false;
  
  private transient GeoPoint routingPoint = null; // Routing Boat position
  private int routingHeading    = -1;
  
  protected double whRatio = 1D;
  private boolean plotNadir = false;
  private boolean displayWindSpeedValue = false;
  private boolean useThickWind = false;
  
  private boolean windOnly = false;

  private boolean displayTws   = true;
  private boolean displayPrmsl = true;
  private boolean display500mb = true;
  private boolean displayWaves = true;
  private boolean displayTemperature = true;
  private boolean displayRain  = true;
  
  private boolean displayContourLines = false; // default
  
  private boolean displayContourTWS   = false;
  private boolean displayContourPRMSL = false;
  private boolean displayContour500mb = false;
  private boolean displayContourWaves = false;
  private boolean displayContourTemp  = false;
  private boolean displayContourPrate = false;
  
  private boolean display3DTws   = false;
  private boolean display3DPrmsl = false;
  private boolean display3D500mb = false;
  private boolean display3DWaves = false;
  private boolean display3DTemperature = false;
  private boolean display3DRain  = false;  
  
  private boolean thereIsWind = true;
  private boolean thereIsPrmsl = true;
  private boolean thereIs500mb = true;
  private boolean thereIsWaves = true;
  private boolean thereIsTemperature = true;
  private boolean thereIsRain = true;
  private boolean thereIsCurrent = true;

  private static boolean coloredWind = true;
  private static boolean coloredCurrent = true;
  
  private transient List<List<List<GeoPoint>>> islandsPressure = null,
                                               islands500mb    = null,
                                               islandsTemp     = null,
                                               islandsWave     = null,
                                               islandsTws      = null,
                                               islandsPrate    = null;

  private boolean displayGribPRMSLContour  = true,
                  displayGrib500HGTContour = true,
                  displayGribTWSContour    = true,
                  displayGribWavesContour  = true,
                  displayGribTempContour   = true,
                  displayGribPrateContour  = true;

  private transient List<CurveUtil.GeoBump> twsBumps = null;
  private transient List<CurveUtil.GeoBump> prmslBumps = null;
  private transient List<CurveUtil.GeoBump> hgt500Bumps = null;
  private transient List<CurveUtil.GeoBump> tempBumps = null;
  private transient List<CurveUtil.GeoBump> wavesBumps = null;
  private transient List<CurveUtil.GeoBump> prateBumps = null;
  
  private transient IsoPointsThread isoPointsThread = null;
  
  private boolean showPlaces = true,
                  showSMStations = false,
                  showWeatherStations = false;
  
  private transient AnimateThread animateThread = null;
  
  private Font dataFont  = new Font("Tahoma", Font.BOLD, 12);
  private Font titleFont = new Font("Tahoma", Font.BOLD, 12);
  private final int ALT_WINDOW_HEADER_SIZE        =  30;
  private final int ALT_WINDOW_BORDER_SIZE        =   5;
  private final int ALT_WINDOW_DATA_OFFSET_SIZE   =   2;
  private final int ALT_WINDOW_MIN_HEIGHT         = 100;
  private final int ALT_WINDOW_MIN_WIDTH          = 100;
  private final int ALT_WINDOW_MIN_NUM_LINES      =   8;
  private final int ALT_WINDOW_TITLE_MIN_BASELINE =  20;
  private final int ALT_WINDOW_TITLE_OFFSET       =  10;
  
  private final int ALT_WINDOW_3BUTTON_WIDTH = 80;
  
  private int altTooltipX = 10;
  private int altTooltipY = 10;
  private int altTooltipW = ALT_WINDOW_MIN_WIDTH;
  private int altTooltipH = ALT_WINDOW_MIN_HEIGHT;

  private ImageIcon closeImage   = new ImageIcon(this.getClass().getResource("close.gif"));
  private ImageIcon zoomInImage  = new ImageIcon(this.getClass().getResource("zoomexpand.gif"));
  private ImageIcon zoomOutImage = new ImageIcon(this.getClass().getResource("zoomshrink.gif"));
  private final int buttonWidth = 15;
  
  private boolean altToooltipWindowBeingDragged = false;
  private int dragStartX = 0, dragStartY = 0;
  private boolean displayAltTooltip = false;
  
  private final static int CLOSE_IMAGE       = 1;
  private final static int ZOOMEXPAND_IMAGE  = 2;
  private final static int ZOOMSHRINK_IMAGE  = 3;
  
  private List<GeoPoint> gpxData = null;
  
  public boolean isBusy() // Is there a Composite in the panel?
  {
    return (wgd != null || faxImage != null);
  }
  
  private void setDisplayAltTooltip(Graphics graphics, String winTitle, String dataString)
  {
    int imageWidth = 24;
    Color endColor   = new Color(0.0f, 0.0f, 0.05f, 0.75f);
    Color startColor = new Color(0.0f, 0.0f, 0.75f, 0.25f);

    altTooltipW = ALT_WINDOW_MIN_WIDTH;
    altTooltipH = ALT_WINDOW_MIN_HEIGHT;
    // Measure dimensions, based on the title and the data to display.
    if (winTitle != null)
    {
      int strWidth = graphics.getFontMetrics(titleFont).stringWidth(winTitle);
      if ((strWidth + ALT_WINDOW_TITLE_OFFSET + ALT_WINDOW_3BUTTON_WIDTH) > altTooltipW)
        altTooltipW = strWidth + ALT_WINDOW_TITLE_OFFSET + ALT_WINDOW_3BUTTON_WIDTH;
    }
    if (dataString != null)
    {
      graphics.setFont(dataFont);
      String[] dataLine = dataString.split("\n");
      int strHeight = dataFont.getSize();
      int progressWidth = 0;
      for (int i=0; i<dataLine.length; i++)
      {
        int strWidth = graphics.getFontMetrics(dataFont).stringWidth(dataLine[i]);
        if (strWidth > progressWidth)
          progressWidth = strWidth;
      }
      if ((progressWidth + (2 * ALT_WINDOW_DATA_OFFSET_SIZE) + (2 * ALT_WINDOW_BORDER_SIZE)) > altTooltipW)
        altTooltipW = (progressWidth + (2 * ALT_WINDOW_DATA_OFFSET_SIZE) + (2 * ALT_WINDOW_BORDER_SIZE));

      int nl = dataLine.length;
      if (nl < ALT_WINDOW_MIN_NUM_LINES)
        nl = ALT_WINDOW_MIN_NUM_LINES;
      if (((nl * (strHeight + 2)) + ALT_WINDOW_HEADER_SIZE + (2 * ALT_WINDOW_BORDER_SIZE)) > altTooltipH)
        altTooltipH = ((nl * (strHeight + 2)) + ALT_WINDOW_HEADER_SIZE + (2 * ALT_WINDOW_BORDER_SIZE));
    }    
  //  System.out.println("Repainting AltWin:" + altTooltipX + ", " + altTooltipY);

    int x = (int)chartPanel.getVisibleRect().getX();
    int y = (int)chartPanel.getVisibleRect().getY();    
    
    GradientPaint gradient = new GradientPaint(x + altTooltipX, y + altTooltipY, startColor, x + altTooltipX + altTooltipH, y + altTooltipY + altTooltipW, endColor); // Diagonal, top-left to bottom-right
  //  GradientPaint gradient = new GradientPaint(x + altTooltipX, x + altTooltipX + altTooltipH, startColor, y + altTooltipY + altTooltipW, y + altTooltipY, endColor); // Horizontal
  //  GradientPaint gradient = new GradientPaint(x + altTooltipX, y + altTooltipY, startColor, x + altTooltipX, x + altTooltipX + altTooltipH, endColor); // vertical
  //  GradientPaint gradient = new GradientPaint(x + altTooltipX, x + altTooltipX + altTooltipH, startColor, x + altTooltipX, y + altTooltipY, endColor); // vertical, upside down
    ((Graphics2D)graphics).setPaint(gradient);

  //  Color bgColor = new Color(0.0f, 0.0f, 0.75f, 0.55f);
  //  graphics.setColor(bgColor);
    graphics.fillRoundRect(x + altTooltipX, y + altTooltipY, altTooltipW, altTooltipH, 10, 10);

    int xi = altTooltipX + altTooltipW - (imageWidth);
    int yi = altTooltipY;
    graphics.drawImage(closeImage.getImage(), x + xi, y + yi, null);

    xi = altTooltipX + altTooltipW - (2 * imageWidth);
    yi = altTooltipY;
    graphics.drawImage(zoomInImage.getImage(), x + xi, y + yi, null);

    xi = altTooltipX + altTooltipW - (3 * imageWidth);
    yi = altTooltipY;
    graphics.drawImage(zoomOutImage.getImage(), x + xi, y + yi, null);
    
    // The data frame (area)
    int xs = x + altTooltipX + ALT_WINDOW_BORDER_SIZE;
    int ys = y + altTooltipY + ALT_WINDOW_HEADER_SIZE;
    graphics.setColor(new Color(1f, 1f, 1f, 0.5f));
    graphics.fillRoundRect(xs, 
                           ys, 
                           altTooltipW - (2 * ALT_WINDOW_BORDER_SIZE), 
                           altTooltipH - ((2 * ALT_WINDOW_BORDER_SIZE) + ALT_WINDOW_HEADER_SIZE), 
                           10, 10);
    
    chartPanel.setPositionToolTipEnabled(false);
    // Win Title here
    if (winTitle != null)
    {
      graphics.setFont(titleFont);
      graphics.setColor(Color.white);
      int baseLine = ALT_WINDOW_TITLE_MIN_BASELINE;
      if ((titleFont.getSize() + 2) > baseLine)
        baseLine = titleFont.getSize() + 2;
      graphics.drawString(winTitle, x + altTooltipX + ALT_WINDOW_TITLE_OFFSET, y + altTooltipY + baseLine);
    }
    // Draw Data Here
    if (dataString != null)
    {
      graphics.setFont(dataFont);
      String[] dataLine = dataString.split("\n");
      graphics.setColor(Color.blue);
      for (int i=0; i<dataLine.length; i++)
      {
        graphics.drawString(dataLine[i], 
                            xs + ALT_WINDOW_DATA_OFFSET_SIZE, 
                            ys + dataFont.getSize());
        ys += (dataFont.getSize() + 2);
      }
    }
  }
  
  private boolean isMouseInAltWindow(MouseEvent me)
  {
    boolean resp = false;
    if (displayAltTooltip)
    {
      int x = me.getX() - (int)chartPanel.getVisibleRect().getX(), 
          y = me.getY() - (int)chartPanel.getVisibleRect().getY();
      if (x > altTooltipX &&
          y > altTooltipY &&
          x < (altTooltipX + altTooltipW) &&
          y < (altTooltipY + altTooltipH))
        resp = true;
    }
    return resp;
  }
  
  private int isMouseOnAltWindowButton(MouseEvent me)
  {
    int button = 0;
    if (!displayAltTooltip)
      return button;
    int x = me.getX() - (int)chartPanel.getVisibleRect().getX(), 
        y = me.getY() - (int)chartPanel.getVisibleRect().getY();
  //  System.out.println("X:" + x + ", Y:" + y + " (winY:" + altTooltipY + ", winX:" + altTooltipX + ", winW:" + altTooltipW);
    if (y > altTooltipY + 7 &&
        y < altTooltipY + 21)
    {
      if (x < (altTooltipX + altTooltipW - 3) && 
          x > (altTooltipX + altTooltipW - (3 + buttonWidth)))
      {
  //    System.out.println("Close");
        button = CLOSE_IMAGE;
      }
      else if (x < (altTooltipX + altTooltipW - 30) && 
               x > (altTooltipX + altTooltipW - (30 + buttonWidth)))
      {
  //    System.out.println("Expand");
        button = ZOOMEXPAND_IMAGE;
      }
      else if (x < (altTooltipX + altTooltipW - 50) && 
               x > (altTooltipX + altTooltipW - (50 + buttonWidth)))
      {
  //    System.out.println("Shrink");
        button = ZOOMSHRINK_IMAGE;
      }
    }
    return button;
  }
  
  public long getID()
  {
    return id;
  }
  
  public CommandPanel(CompositeTabbedPane caller, MainZoomPanel dp)
  {
    this.id = (long)(Math.random() + Long.MAX_VALUE);
    
    this.parent = caller;
    dataPanel = dp;
    borderLayout1 = new BorderLayout();
//  borderLayout2 = new BorderLayout();
    chartPanelScrollPane = new JScrollPane();
    chartPanel = new ChartPanel(this);
    
    dummyGribSlicePlaceHolder = new JPanel();
    JLabel dummyLabel = new JLabel("Dummy GRIB Slice place holder");
    dummyLabel.setEnabled(false);
    dummyGribSlicePlaceHolder.add(dummyLabel, null);
    jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dummyGribSlicePlaceHolder, chartPanelScrollPane);
    jSplitPane.setContinuousLayout(true);
    jSplitPane.setOneTouchExpandable(true);
    jSplitPane.setDividerSize(6);
    jSplitPane.setDividerLocation(0);
            
    bottomPanel = new JPanel();
    bottomLeftPanel = new JPanel();
    bottomRightPanel = new JPanel();
    rightVerticalPanel = new JPanel();
    rightBottomPanel = new JPanel();
    blurSharpPanel = new JPanel();
    blurSharpPanel.setLayout(new GridBagLayout());
    blurRadioButton = new JRadioButton("");
    noChangeRadioButton = new JRadioButton("");
    sharpRadioButton = new JRadioButton("");
    blurRadioButton.setToolTipText(WWGnlUtilities.buildMessage("fax-blur"));
    noChangeRadioButton.setToolTipText(WWGnlUtilities.buildMessage("fax-no-change"));
    sharpRadioButton.setToolTipText(WWGnlUtilities.buildMessage("fax-sharp"));
    blurSharpGroup.add(blurRadioButton);
    blurSharpGroup.add(noChangeRadioButton);
    blurSharpGroup.add(sharpRadioButton);
    {
      int blurIndex = Integer.parseInt(((ParamPanel.FaxBlurList)(ParamPanel.data[ParamData.DEFAULT_FAX_BLUR][ParamData.VALUE_INDEX])).getStringIndex());
      blurRadioButton.setSelected(blurIndex == -1);
      noChangeRadioButton.setSelected(blurIndex == 0);
      sharpRadioButton.setSelected(blurIndex == 1);
      if (blurIndex == -1)
        blurSharpOption = ImageUtil.BLUR;
      else if (blurIndex == 0)
        blurSharpOption = ImageUtil.NO_CHANGE;
      else if (blurIndex == 1)
        blurSharpOption = ImageUtil.SHARPEN;
      previousBlurSharpOption = blurSharpOption;
    }
//  JSeparator sep1 = new JSeparator();
//  sep1.setOrientation(JSeparator.HORIZONTAL);
    
    int vpos = 0;
//  blurSharpPanel.add(sep1,
//                     new GridBagConstraints(0, vpos++, 1, 1, 0.0, 0.0, 
//                                            GridBagConstraints.CENTER, 
//                                            GridBagConstraints.HORIZONTAL, 
//                                            new Insets(5, 5, 5, 5), 0, 0));
    blurSharpPanel.add(blurRadioButton,
                       new GridBagConstraints(0, vpos++, 1, 1, 0.0, 0.0, 
                                              GridBagConstraints.CENTER, 
                                              GridBagConstraints.NONE, 
                                              new Insets(5, 5, 5, 5), 0, 0));
    blurSharpPanel.add(noChangeRadioButton,
                       new GridBagConstraints(0, vpos++, 1, 1, 0.0, 0.0, 
                                              GridBagConstraints.CENTER, 
                                              GridBagConstraints.NONE, 
                                              new Insets(5, 5, 5, 5), 0, 0));
    blurSharpPanel.add(sharpRadioButton,
                       new GridBagConstraints(0, vpos++, 1, 1, 0.0, 0.0, 
                                              GridBagConstraints.CENTER, 
                                              GridBagConstraints.NONE, 
                                              new Insets(5, 5, 5, 5), 0, 0));    
    blurRadioButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            blurSharpOption = ImageUtil.BLUR;     
            if (getFaxes() != null)
              adjustFuzziness();
          }
        });          
    noChangeRadioButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            blurSharpOption = ImageUtil.NO_CHANGE;
            if (getFaxes() != null)
              adjustFuzziness();
          }
        });          
    sharpRadioButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            blurSharpOption = ImageUtil.SHARPEN;
            if (getFaxes() != null)
              adjustFuzziness();
          }
        });          
    
    checkBoxCompositePanel = new JPanel();
    checkBoxPanelHolder = new JPanel();
    checkBoxPanelHolder.setLayout(new BorderLayout());
    cbPanelScrollUpButton = new JButton("");
    cbPanelScrollUpButton.setToolTipText("Scroll Up");
    cbPanelScrollDownButton = new JButton("");
    cbPanelScrollDownButton.setToolTipText("Scroll Down");
    checkBoxPanelScrollPane = new JScrollPane();
    
    checkBoxPanelScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    checkBoxPanelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    
    checkBoxPanelScrollPane.getViewport().add(checkBoxCompositePanel, null);    

    checkBoxPanelHolder.add(cbPanelScrollUpButton, BorderLayout.NORTH);
    checkBoxPanelHolder.add(checkBoxPanelScrollPane, BorderLayout.CENTER);
    checkBoxPanelHolder.add(cbPanelScrollDownButton, BorderLayout.SOUTH);
    
    cbPanelScrollUpButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          int y = checkBoxPanelScrollPane.getViewport().getViewPosition().y;
//        System.out.println("Y:"+ y);
          int newy = y - 10;
          if (newy < 0) newy = 0;
          checkBoxPanelScrollPane.getViewport().setViewPosition(new Point(0, newy));
        }
      });
    
    cbPanelScrollDownButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          int y = checkBoxPanelScrollPane.getViewport().getViewPosition().y;
//        System.out.println("Y:"+ y);
          int newy = y + 10;
          if (newy + checkBoxPanelScrollPane.getViewport().getHeight() > checkBoxCompositePanel.getHeight())
          {
//          System.out.println("Rheu!");
//          System.out.println("Panel H:" + panelThatScroll.getHeight());
//          System.out.println("View H :" + jScrollPane1.getViewport().getHeight());
            newy = checkBoxCompositePanel.getHeight() - checkBoxPanelScrollPane.getViewport().getHeight() + 1;
          }
          checkBoxPanelScrollPane.getViewport().setViewPosition(new Point(0, newy));
        }
      });    
    
    checkBoxCompositePanel.setLayout(new GridBagLayout());
    checkBoxCompositePanel.setToolTipText(WWGnlUtilities.buildMessage("check-box-panel-tooltip"));
    checkBoxCompositePanel.addMouseListener(new MouseAdapter()
      {
        @Override
        public void mouseClicked(MouseEvent me)
        {
//        super.mouseClicked(me);
          int mask = me.getModifiers();
          if ((mask & MouseEvent.BUTTON2_MASK) != 0 || (mask & MouseEvent.BUTTON3_MASK) != 0) // Right click
          {
            {
              cbpPopup = new CheckBoxPanelPopup(instance, me.getX(), me.getY());
            }
            cbpPopup.show(checkBoxCompositePanel, me.getX(), me.getY());
            me.consume();
          }
        }
      });
    bottomPanel.setLayout(new BorderLayout());
    rightVerticalPanel.setLayout(new BorderLayout());
    
    JPanel sliderHolder = new JPanel();
    sliderHolder.add(faxOpacitySlider, null);
    sliderHolder.add(gribOpacitySlider, null);
    
    rightVerticalPanel.add(sliderHolder, BorderLayout.NORTH);    
    rightBottomPanel.setLayout(new BorderLayout());
//  rightBottomPanel.add(checkBoxCompositePanel, BorderLayout.NORTH);
//  rightVerticalPanel.add(rightBottomPanel, BorderLayout.CENTER);
    rightVerticalPanel.add(checkBoxPanelHolder, BorderLayout.CENTER);
    rightVerticalPanel.add(blurSharpPanel, BorderLayout.SOUTH);    
    
    faxOpacitySlider.setMaximum(0);
    faxOpacitySlider.setMaximum(100);
    faxOpacitySlider.setToolTipText(WWGnlUtilities.buildMessage("fax-opacity"));
    faxOpacitySlider.setOrientation(JSlider.VERTICAL);
    faxOpacitySlider.setValue((int)(faxUserOpacity * 100F));
    faxOpacitySlider.setEnabled(false);
    faxOpacitySlider.addChangeListener(new ChangeListener() 
    {
      public void stateChanged(ChangeEvent evt) 
      {
        JSlider slider = (JSlider)evt.getSource();
    
        if (!slider.getValueIsAdjusting()) 
        {
          // Get new value
          float value = (float)slider.getValue() / 100F;
          faxUserOpacity = value;
          faxOpacitySlider.setToolTipText("<html>" + WWGnlUtilities.buildMessage("fax-opacity") + ":<br>" + Float.toString(value) + "</html>");
          chartPanel.repaint();
        }
      }
    });
    gribOpacitySlider.setMaximum(0);
    gribOpacitySlider.setMaximum(100);
    gribOpacitySlider.setToolTipText(WWGnlUtilities.buildMessage("grib-opacity"));
    gribOpacitySlider.setOrientation(JSlider.VERTICAL);
    gribOpacitySlider.setValue((int)(gribUserOpacity * 100F));
    gribOpacitySlider.setEnabled(false);
    gribOpacitySlider.addChangeListener(new ChangeListener() 
    {
      public void stateChanged(ChangeEvent evt) 
      {
        JSlider slider = (JSlider)evt.getSource();
    
        if (!slider.getValueIsAdjusting()) 
        {
          // Get new value
          float value = (float)slider.getValue() / 100F;
          gribUserOpacity = value;
          gribOpacitySlider.setToolTipText("<html>" + WWGnlUtilities.buildMessage("grib-opacity") + ":<br>" + Float.toString(value) + "</html>");
          chartPanel.repaint();
        }
      }
    });
    
    bottomPanel.add(bottomLeftPanel, BorderLayout.WEST);
    bottomPanel.add(bottomRightPanel, BorderLayout.CENTER);
    
    if (defaultWindOption == ParamPanel.WindOptionList.SMALL_DOT)
    {
      drawHeavyDot = false;
      drawWindColorBackground = false;
    }
    else if (defaultWindOption == ParamPanel.WindOptionList.HEAVY_DOT)
    {
      drawHeavyDot = true;
      drawWindColorBackground = false;
    }
    else if (defaultWindOption == ParamPanel.WindOptionList.BACKGROUND)
    {
      drawHeavyDot = false;
      drawWindColorBackground = true;
    }
    
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      WWContext.getInstance().fireExceptionLogging(e);
      e.printStackTrace();
    }
  }

  private static double resetDir(double dir)
  {
    dir = 180D - dir;
    while (dir > 360D) dir -= 360D;
    while (dir < 0D) dir += 360D;
    return dir;
  }

  private void adjustFuzziness()
  {
    Thread adjustThread = new Thread("fuzziness-adjuster")
      {
        public void run()
        {
          WWContext.getInstance().fireSetLoading(true, WWGnlUtilities.buildMessage("adjusting"));
          blurRadioButton.setEnabled(false);
          noChangeRadioButton.setEnabled(false);
          sharpRadioButton.setEnabled(false);
          setFaxes(getFaxes());
          chartPanel.repaint();
          blurRadioButton.setEnabled(true);
          noChangeRadioButton.setEnabled(true);
          sharpRadioButton.setEnabled(true);
          WWContext.getInstance().fireSetLoading(false, WWGnlUtilities.buildMessage("adjusting"));
        }
      };
    adjustThread.start();
  }

  public int getProjection()
  { return chartPanel.getProjection(); }
  
  public FaxType[] getFaxes()
  {
    if (faxImage == null || faxImage.length == 0)
      return null;
    FaxType[] ft = null;
    try
    {
      ft = new FaxType[faxImage.length];
      
      for (int i=0; faxImage!=null && i<faxImage.length; i++)
      {
        if (faxImage[i] != null)
        {
          ft[i] = new FaxType(faxImage[i].fileName, 
                              faxImage[i].color, 
                              faxImage[i].show,
                              faxImage[i].transparent,
                              faxImage[i].imageRotationAngle,
                              faxImage[i].faxOrigin,
                              faxImage[i].faxTitle,
                              faxImage[i].colorChange);
          ft[i].setComment(faxImage[i].comment);
        }
      }
    }
    catch (Exception exp) 
    {
      exp.printStackTrace();
    }
    return ft;
  }
  
  public void print()
  {
    PrintOptionsPanel pop = new PrintOptionsPanel();
    int resp = JOptionPane.showConfirmDialog(this, pop, WWGnlUtilities.buildMessage("insert-title"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (resp == JOptionPane.OK_OPTION)
    {
      String title = pop.getTitle();
      Component view = null;
      if (pop.getAllCompositeOption())
        view = chartPanel;  // All the composite
      else
        view = chartPanelScrollPane.getViewport(); // Just visible part
      if (title.trim().length() > 0)
        PrintUtilities.printComponent(view, title, Color.black, new Font("Verdana", Font.BOLD | Font.ITALIC, 16), 10, 20);
      else
        PrintUtilities.printComponent(view);
    }
  }
  
  public int[] genImage()
  {
    int[] ret = null;
    String fName = WWGnlUtilities.chooseFile(this, JFileChooser.FILES_ONLY, 
                                           new String[] { "jpg", "jpeg", "png" }, WWGnlUtilities.buildMessage("image-files"),
                                           ".", WWGnlUtilities.buildMessage("save"), WWGnlUtilities.buildMessage("generate-image"));
    if (fName.trim().length() > 0)
    {
      String prefix = "";
      String suffix = "";
      if (fName.trim().indexOf(".") == -1)
      {
        JOptionPane.showMessageDialog(this, WWGnlUtilities.buildMessage("please-provide-extension"), WWGnlUtilities.buildMessage("generate-image"), 
                                      JOptionPane.ERROR_MESSAGE); 
      }
      else
      {
        prefix = fName.trim().substring(0, fName.trim().indexOf("."));
        suffix = fName.trim().substring(fName.trim().indexOf(".") + 1);
        if (!suffix.toLowerCase().equals("jpg") &&
            !suffix.toLowerCase().equals("jpeg") &&
            !suffix.toLowerCase().equals("png"))
        {
          JOptionPane.showMessageDialog(this, WWGnlUtilities.buildMessage("bad-extension", new String[] { suffix.toLowerCase() }), WWGnlUtilities.buildMessage("generate-image"), 
                                        JOptionPane.ERROR_MESSAGE); 
        }
        else
        {
          ret = chartPanel.genImage(prefix, suffix.toLowerCase());
          JOptionPane.showMessageDialog(this, WWGnlUtilities.buildMessage("file-generated", new String[] { fName }), WWGnlUtilities.buildMessage("generate-image"), 
                                        JOptionPane.INFORMATION_MESSAGE);
        }
      }
    }
    return ret;
  }
  
  private int faxIndex(String str)
  {
    int idx = -1;
    if (faxImage != null)
    {
      for (int i=0; faxImage!=null && i<faxImage.length; i++)
      {
        if (faxImage[i].fileName.equals(str))
        {
          idx = i;
          break;
        }
      }
    }
    return idx;
  }
  
  public boolean isRoutingBoatDisplayed()
  {
    return (routingPoint != null);
  }
  
  public void eraseRoutingBoat()
  {
    routingPoint = null;
    chartPanel.repaint();  
  }
  
  public boolean isBoatPositionDisplayed()
  {
    return (boatPosition != null);  
  }
  
  public void resetBoatPosition()
  {
    boatPosition = null;
    boatHeading = -1;
  }
  
  public void interruptRouting()
  {
//  System.out.println("Routing interrupted.");
    RoutingUtil.interruptRoutingCalculation();
  }
  
  public void removeCompositeCheckBoxes()
  {
    faxOpacitySlider.setEnabled(false);
    gribOpacitySlider.setEnabled(false);
    if (checkBoxCompositePanel != null)
    {
      if (compositeCheckBox != null) // Remove before adding new ones
      {
        for (int i=0; i<compositeCheckBox.length; i++)
        {
          if (compositeCheckBox[i] != null)
            checkBoxCompositePanel.remove(compositeCheckBox[i]);
        }
      }
      if (compositeRadioButton != null)
      {
        for (int i=0; i<compositeRadioButton.length; i++)
        {
          if (compositeRadioButton[i] != null)
            checkBoxCompositePanel.remove(compositeRadioButton[i]);
        }
      }
      checkBoxCompositePanel.repaint();
    }
  }
  
  public void removeContourCheckBoxes()
  {
    if (contourCheckBox != null)
    {
      for (int i=0; i<contourCheckBox.length; i++)
        checkBoxCompositePanel.remove(contourCheckBox[i]);       
    }
    checkBoxCompositePanel.repaint();
  }
  
  public void setCheckBoxes()
  {
    setCheckBoxes(getFaxes());  
    setContourCheckBoxes();
    checkBoxCompositePanel.repaint();
  }
  
  private final int EXTRA_CHECK_BOXES = 5; // 4: Chart, Grid, Places, SailMail, Weather.
  
  public void setCheckBoxes(FaxType[] faxes)
  {        
    removeCompositeCheckBoxes();
    compositeCheckBox = null;
    int nbFaxes = 0;
    if (faxes != null)
    {
      faxOpacitySlider.setEnabled(true);
      nbFaxes = faxes.length;
    }
    gribOpacitySlider.setEnabled(wgd != null);

    if (checkBoxPanelOption == CHECKBOX_OPTION)
    {
      compositeCheckBox = new JCheckBox[nbFaxes + EXTRA_CHECK_BOXES + (wgd!=null?1:0)]; 
    }
    if (checkBoxPanelOption == RADIOBUTTON_OPTION)
    {
      compositeRadioButton = new JRadioButton[nbFaxes];
      compositeCheckBox = new JCheckBox[EXTRA_CHECK_BOXES + (wgd!=null?1:0)]; 
    }
    
    for (int i=0; faxes != null && faxes.length > 0 && i<faxes.length; i++)
    {
      if (faxes[i] == null)
        continue;
      if (checkBoxPanelOption == CHECKBOX_OPTION)
      {
        compositeCheckBox[i] = new JCheckBox("");
        compositeCheckBox[i].setBackground(faxes[i].getColor());
  //    compositeCheckBox[i].setForeground(faxes[i].getColor());
  //    compositeCheckBox[i].setBorderPaintedFlat(false);
        compositeCheckBox[i].setSelected(faxes[i].isShow());
        final int fIdx = i;
        compositeCheckBox[i].addActionListener(new ActionListener()
            {
              public void actionPerformed(ActionEvent e)
              {
  //            System.out.println("Fax[] " + (faxCheckBox[fIdx].isSelected()?"show":"hide"));
                faxImage[fIdx].show = compositeCheckBox[fIdx].isSelected();
                chartPanel.repaint();
              }
            });          
        String tooltip = faxes[i].getTitle();
        if (tooltip == null || tooltip.trim().length() == 0)
          tooltip = faxes[i].getComment();
        if (tooltip.trim().length() == 0)
          tooltip = faxes[i].getValue();
        if (tooltip.indexOf("/") > -1)
          tooltip = tooltip.substring(tooltip.lastIndexOf("/") + 1);
        compositeCheckBox[i].setToolTipText(tooltip);
        checkBoxCompositePanel.add(compositeCheckBox[i],
                             new GridBagConstraints(0, i, 1, 1, 0.0, 0.0, 
                                                    GridBagConstraints.CENTER, 
                                                    GridBagConstraints.NONE, 
                                                    new Insets(5, 5, 5, 5), 0, 0));
      }
      if (checkBoxPanelOption == RADIOBUTTON_OPTION)
      {
        compositeRadioButton[i] = new JRadioButton("");
        compositeRadioButton[i].setBackground(faxes[i].getColor());
      //    compositeCheckBox[i].setForeground(faxes[i].getColor());
      //    compositeCheckBox[i].setBorderPaintedFlat(false);
        compositeRadioButton[i].setSelected(i == 0);
        faxImage[i].show = (i == 0);
        final int fIdx = i;
        compositeRadioButton[i].addActionListener(new ActionListener()
            {
              public void actionPerformed(ActionEvent e)
              {
      //        System.out.println("Fax[] " + (faxCheckBox[fIdx].isSelected()?"show":"hide"));
      //        faxImage[fIdx].show = compositeRadioButton[fIdx].isSelected(); 
                for (int i = 0; i<faxImage.length; i++)
                  faxImage[i].show = compositeRadioButton[i].isSelected();
                chartPanel.repaint();
              }
            });          
        String tooltip = faxes[i].getTitle();
        if (tooltip == null || tooltip.trim().length() == 0)
          tooltip = faxes[i].getComment();
        if (tooltip.trim().length() == 0)
          tooltip = faxes[i].getValue();
        if (tooltip.indexOf("/") > -1)
          tooltip = tooltip.substring(tooltip.lastIndexOf("/") + 1);
        compositeRadioButton[i].setToolTipText(tooltip);
        checkBoxCompositePanel.add(compositeRadioButton[i],
                             new GridBagConstraints(0, i, 1, 1, 0.0, 0.0, 
                                                    GridBagConstraints.CENTER, 
                                                    GridBagConstraints.NONE, 
                                                    new Insets(5, 5, 5, 5), 0, 0));
        buttonGroup.add(compositeRadioButton[i]);
      }
    }
    if (checkBoxPanelOption == RADIOBUTTON_OPTION)
    {
      for (int i=0; i<compositeRadioButton.length; i++)
        compositeRadioButton[i].setSelected(i == 0);
    }
    int i = (faxes==null?0:(checkBoxPanelOption == CHECKBOX_OPTION?faxes.length:0));
    int pos = (faxes==null?0:faxes.length);
    if (wgd != null) // One for the GRIBs
    {
      final int cbIdx = i;
      if (compositeCheckBox == null)
        compositeCheckBox = new JCheckBox[EXTRA_CHECK_BOXES + 1]; // + 1 pour le GRIB
      compositeCheckBox[i] = new JCheckBox("");
      compositeCheckBox[i].setSelected(this.isDrawChart());
      compositeCheckBox[i].setBackground((Color) ParamPanel.data[ParamData.GRIB_WIND_COLOR][ParamData.VALUE_INDEX]);
      compositeCheckBox[i].addActionListener(new ActionListener()
          {
            public void actionPerformed(ActionEvent e)
            {
              setDrawGRIB(compositeCheckBox[cbIdx].isSelected());
              chartPanel.repaint();
            }
          });
      String tooltip = WWGnlUtilities.buildMessage("show-grib");
      compositeCheckBox[i].setToolTipText(tooltip);
      compositeCheckBox[i].setToolTipText("GRIB");
      checkBoxCompositePanel.add(compositeCheckBox[i],
                           new GridBagConstraints(0, pos++, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                                  new Insets(5, 5, 5, 5), 0, 0));      
    }
    // Add 5, for the chart, and for the grid, places, sailmail stations, weather stations
    if (compositeCheckBox == null)
      compositeCheckBox = new JCheckBox[EXTRA_CHECK_BOXES];
    i = (faxes==null?0:(checkBoxPanelOption == CHECKBOX_OPTION?faxes.length:0)) + (wgd!=null?1:0);
    { // Chart
      final int cbIdx = i;
      compositeCheckBox[i] = new JCheckBox("");
      compositeCheckBox[i].setSelected(this.isDrawChart());
      compositeCheckBox[i].setBackground((Color) ParamPanel.data[ParamData.CHART_COLOR][ParamData.VALUE_INDEX]);
      compositeCheckBox[i].addActionListener(new ActionListener()
          {
            public void actionPerformed(ActionEvent e)
            {
              setDrawChart(compositeCheckBox[cbIdx].isSelected());
              chartPanel.repaint();
            }
          });
      String tooltip = WWGnlUtilities.buildMessage("show-chart");
      compositeCheckBox[i].setToolTipText(tooltip);
      checkBoxCompositePanel.add(compositeCheckBox[i],
                           new GridBagConstraints(0, pos++, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                                  new Insets(5, 5, 5, 5), 0, 0));
    }
    i++;
    { // Grid
      final int cbIdx = i;
      compositeCheckBox[i] = new JCheckBox("");
      compositeCheckBox[i].setSelected(chartPanel.isWithGrid());
      compositeCheckBox[i].setBackground((Color) ParamPanel.data[ParamData.GRID_COLOR][ParamData.VALUE_INDEX]);
      compositeCheckBox[i].addActionListener(new ActionListener()
          {
            public void actionPerformed(ActionEvent e)
            {
              chartPanel.setWithGrid(compositeCheckBox[cbIdx].isSelected());
              chartPanel.repaint();
            }
          });
      String tooltip = WWGnlUtilities.buildMessage("show-grid");
      compositeCheckBox[i].setToolTipText(tooltip);
      checkBoxCompositePanel.add(compositeCheckBox[i],
                           new GridBagConstraints(0, pos++, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                                  new Insets(5, 5, 5, 5), 0, 0));
    }
    i++;
    { // Places
      final int cbIdx = i;
      compositeCheckBox[i] = new JCheckBox("");
      compositeCheckBox[i].setSelected(isShowPlaces());
//    compositeCheckBox[i].setBackground((Color) ParamPanel.data[ParamData.GRID_COLOR][ParamData.VALUE_INDEX]);
      compositeCheckBox[i].addActionListener(new ActionListener()
          {
            public void actionPerformed(ActionEvent e)
            {
              setShowPlaces(compositeCheckBox[cbIdx].isSelected());
              repaint();
            }
          });
      String tooltip = WWGnlUtilities.buildMessage("show-places");
      compositeCheckBox[i].setToolTipText(tooltip);
      checkBoxCompositePanel.add(compositeCheckBox[i],
                           new GridBagConstraints(0, pos++, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                                  new Insets(5, 5, 5, 5), 0, 0));
    }
    i++;
    { // SailMail
      final int cbIdx = i;
      compositeCheckBox[i] = new JCheckBox("");
      compositeCheckBox[i].setSelected(isShowSMStations());
//    compositeCheckBox[i].setBackground((Color) ParamPanel.data[ParamData.GRID_COLOR][ParamData.VALUE_INDEX]);
      compositeCheckBox[i].addActionListener(new ActionListener()
          {
            public void actionPerformed(ActionEvent e)
            {
              setShowSMStations(compositeCheckBox[cbIdx].isSelected());
              repaint();
            }
          });
      String tooltip = WWGnlUtilities.buildMessage("show-sailmail");
      compositeCheckBox[i].setToolTipText(tooltip);
      checkBoxCompositePanel.add(compositeCheckBox[i],
                           new GridBagConstraints(0, pos++, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                                  new Insets(5, 5, 5, 5), 0, 0));
    }
    i++;
    { // Weather Stations
      final int cbIdx = i;
      compositeCheckBox[i] = new JCheckBox("");
      compositeCheckBox[i].setSelected(isShowSMStations());
//    compositeCheckBox[i].setBackground((Color) ParamPanel.data[ParamData.GRID_COLOR][ParamData.VALUE_INDEX]);
      compositeCheckBox[i].addActionListener(new ActionListener()
          {
            public void actionPerformed(ActionEvent e)
            {
              setShowWeathertations(compositeCheckBox[cbIdx].isSelected());
              repaint();
            }
          });
      String tooltip = WWGnlUtilities.buildMessage("show-weather-station");
      compositeCheckBox[i].setToolTipText(tooltip);
      checkBoxCompositePanel.add(compositeCheckBox[i],
                           new GridBagConstraints(0, pos++, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                                  new Insets(5, 5, 5, 5), 0, 0));
    }
  }

  public void setContourCheckBoxes()
  {
    removeContourCheckBoxes();
    int nbContour = 0;
    final List<Integer> contourList = new ArrayList<Integer>();
    
    if (gribData != null && isDisplayContourTWS())   contourList.add(new Integer(GRIBDataUtil.TYPE_TWS));
    if (gribData != null && isTherePrmsl() && isDisplayContourPRMSL()) contourList.add(new Integer(GRIBDataUtil.TYPE_PRMSL));
    if (gribData != null && isThere500mb() && isDisplayContour500mb()) contourList.add(new Integer(GRIBDataUtil.TYPE_500MB));
    if (gribData != null && isThereWaves() && isDisplayContourWaves()) contourList.add(new Integer(GRIBDataUtil.TYPE_WAVE));
    if (gribData != null && isThereTemperature() && isDisplayContourTemp()) contourList.add(new Integer(GRIBDataUtil.TYPE_TMP));
    if (gribData != null && isThereRain() && isDisplayContourPrate()) contourList.add(new Integer(GRIBDataUtil.TYPE_RAIN));
    
    nbContour = contourList.size();
    
    displayGribPRMSLContour = true;
    displayGrib500HGTContour = true;
    displayGribTWSContour = true;
    displayGribWavesContour = true;
    displayGribTempContour = true;
    displayGribPrateContour = true;
    
    if (nbContour > 0)
    {
      contourCheckBox = new JCheckBox[nbContour];
      // Checkboxes already on panel
      int startWith = (compositeCheckBox==null)?0:compositeCheckBox.length;
      for (int i=0; i<nbContour; i++)
      {
        contourCheckBox[i] = new JCheckBox("");
        final int dataType = contourList.get(i);
        String tooltip = "";
        switch (dataType) // LOCALIZE tooltips
        {
          case GRIBDataUtil.TYPE_TWS:
            contourCheckBox[i].setBackground((Color) ParamPanel.data[ParamData.GRIB_WIND_COLOR][ParamData.VALUE_INDEX]);
            tooltip = "GRIB Contour TWS";
            contourCheckBox[i].setToolTipText(tooltip);
            break;
          case GRIBDataUtil.TYPE_PRMSL:
            contourCheckBox[i].setBackground((Color) ParamPanel.data[ParamData.PRMSL_CONTOUR][ParamData.VALUE_INDEX]);
            tooltip = "GRIB Contour PRMSL";
            contourCheckBox[i].setToolTipText(tooltip);
            break;
          case GRIBDataUtil.TYPE_500MB:
            contourCheckBox[i].setBackground((Color) ParamPanel.data[ParamData.MB500_CONTOUR][ParamData.VALUE_INDEX]);
            tooltip = "GRIB Contour 500mb";
            contourCheckBox[i].setToolTipText(tooltip);
            break;
          case GRIBDataUtil.TYPE_WAVE:
            contourCheckBox[i].setBackground((Color) ParamPanel.data[ParamData.WAVES_CONTOUR][ParamData.VALUE_INDEX]);
            tooltip = "GRIB Contour WAVES";
            contourCheckBox[i].setToolTipText(tooltip);
            break;
          case GRIBDataUtil.TYPE_TMP:
            contourCheckBox[i].setBackground((Color) ParamPanel.data[ParamData.TEMP_CONTOUR][ParamData.VALUE_INDEX]);
            tooltip = "GRIB Contour TEMP";
            contourCheckBox[i].setToolTipText(tooltip);
            break;
          case GRIBDataUtil.TYPE_RAIN:
            contourCheckBox[i].setBackground((Color) ParamPanel.data[ParamData.PRATE_CONTOUR][ParamData.VALUE_INDEX]);
            tooltip = "GRIB Contour PRATE";
            contourCheckBox[i].setToolTipText(tooltip);
            break;
          default:
            break;
        }
        contourCheckBox[i].setSelected(true);
        final int fIdx = i;
        contourCheckBox[i].addActionListener(new ActionListener()
          {
            public void actionPerformed(ActionEvent e)
            {
              switch (contourList.get(fIdx))
              {
                case GRIBDataUtil.TYPE_TWS:
                  displayGribTWSContour = contourCheckBox[fIdx].isSelected();
                  break;
                case GRIBDataUtil.TYPE_PRMSL:
                  displayGribPRMSLContour = contourCheckBox[fIdx].isSelected();
                  break;
                case GRIBDataUtil.TYPE_500MB:
                  displayGrib500HGTContour = contourCheckBox[fIdx].isSelected();
                  break;
                case GRIBDataUtil.TYPE_WAVE:
                  displayGribWavesContour = contourCheckBox[fIdx].isSelected();
                  break;
                case GRIBDataUtil.TYPE_TMP:
                  displayGribTempContour = contourCheckBox[fIdx].isSelected();
                  break;
                case GRIBDataUtil.TYPE_RAIN:
                  displayGribPrateContour = contourCheckBox[fIdx].isSelected();
                  break;
                default:
                  break;
              }
              chartPanel.repaint();
            }
          });          
        checkBoxCompositePanel.add(contourCheckBox[i],
                                   new GridBagConstraints(0, i + startWith, 1, 1, 0.0, 0.0, 
                                                          GridBagConstraints.CENTER, 
                                                          GridBagConstraints.NONE, 
                                                          new Insets(5, 5, 5, 5), 0, 0));
      }
    }
  }
  
  public void setFaxes(FaxType[] faxes)
  {
    ZipFile waz = null;
    FaxImage[] newFaxImage = new FaxImage[faxes.length];
    for (int i=0; i<faxes.length; i++)
    {
      String fileName = faxes[i].getValue();
      Image faxImg = null;
      try
      {
        if (fileName.startsWith(WWContext.WAZ_PROTOCOL_PREFIX)) // From archive. Used for example when color is to be changed by user.
        {
          waz = new ZipFile(WWContext.getInstance().getCurrentComposite());
          String fName = fileName.substring(WWContext.WAZ_PROTOCOL_PREFIX.length());
          InputStream is = waz.getInputStream(waz.getEntry(fName));
          boolean tif = fileName.toUpperCase().endsWith(".TIFF") || fileName.toUpperCase().endsWith(".TIF");
          faxImg = ImageUtil.readImage(is, tif);     
          is.close();
        }
        else
          faxImg = ImageUtil.readImage(fileName);
        if (faxImg != null)
        {
          if (faxes[i].isTransparent())
          {
            if (faxes[i].isChangeColor() || previousBlurSharpOption != blurSharpOption)
            {
              if (faxImage != null && 
                  faxImage.length > i && 
                  faxes[i].getColor().equals(faxImage[i].color) && // Same color
                  previousBlurSharpOption == blurSharpOption)      // Same fuzziness
                faxImg = faxImage[i].faxImage;
              else
              {
            //  faxImg = ImageUtil.makeTransparentImage(this, faxImg, faxes[i].getColor());
                if (ImageUtil.countColors(faxImg) > 2)
                  faxImg = ImageUtil.switchAnyColorAndMakeColorTransparent(faxImg, faxes[i].getColor(), ImageUtil.mostUsedColor(faxImg), blurSharpOption);
                else
                  faxImg = ImageUtil.switchColorAndMakeColorTransparent(faxImg, Color.black, faxes[i].getColor(), Color.white, blurSharpOption);
              }
            }
            else // Leave colors as they are, make the white transparent
              faxImg = ImageUtil.makeColorTransparent(faxImg, Color.white, blurSharpOption);
          }
          newFaxImage[i] = new FaxImage();
          newFaxImage[i].faxImage    = faxImg;
          newFaxImage[i].fileName    = faxes[i].getValue();
          newFaxImage[i].color       = faxes[i].getColor();
          newFaxImage[i].comment     = faxes[i].getComment();   
          newFaxImage[i].show        = faxes[i].isShow();
          newFaxImage[i].transparent = faxes[i].isTransparent();
          newFaxImage[i].colorChange = faxes[i].isChangeColor();
          newFaxImage[i].faxTitle    = faxes[i].getTitle();
          newFaxImage[i].faxOrigin   = faxes[i].getOrigin();
          int idx = faxIndex(fileName); // Is FileName already displayed? 
          if (idx > -1) // if yes, keep offsets, scale and rotation
          {
            newFaxImage[i].imageHOffset = faxImage[idx].imageHOffset;
            newFaxImage[i].imageVOffset = faxImage[idx].imageVOffset;
            newFaxImage[i].imageScale   = faxImage[idx].imageScale;
            newFaxImage[i].imageRotationAngle = faxImage[idx].imageRotationAngle;
          }
        }
      }
      catch (Exception ex)
      {
        WWContext.getInstance().fireExceptionLogging(ex);
        ex.printStackTrace();
      }
    }
    faxImage = newFaxImage;
    setCheckBoxes(faxes);
    if (faxes.length > 0)
    {
      WWContext.getInstance().fireFaxLoaded();
      WWContext.getInstance().fireFaxesLoaded(faxes);
    }
    previousBlurSharpOption = blurSharpOption;
  }
  
  private double nLat  =   65d; // 82D;
  private double sLat  =  -65d; //-76D;
  private double wLong = -180D;
  private double eLong =  180D;
  
  private double tmpNLat  = Double.MAX_VALUE;
  private double tmpSLat  = Double.MAX_VALUE;
  private double tmpWLong = Double.MAX_VALUE;
  private double tmpELong = Double.MAX_VALUE;
  
  private static GeoPoint[] gpa = null;
  private static String[] ptLabels = null;
  private static Boolean[] showPlacesArray = null;

  private transient List<WWGnlUtilities.SailMailStation> sma = null;
  private transient List<WWGnlUtilities.WeatherStation> wsta = null;

  private ImageIcon greenFlagImage = null;
  
  private transient ApplicationEventListener ael = null;
  
  private void jbInit()
        throws Exception
  {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    String imgFileName = "PanOpenHand32x32.png";
    Image image = toolkit.getImage(ChartPanel.class.getResource(imgFileName));
    cursorOverWayPoint = toolkit.createCustomCursor(image , new Point(15,15), imgFileName);
    imgFileName = "PanClosedHand32x32.png";
    image = toolkit.getImage(ChartPanel.class.getResource(imgFileName));
    cusorDraggingWayPoint = toolkit.createCustomCursor(image , new Point(15,15), imgFileName);
    
    ael = new ApplicationEventListener()
      {
        public String toString()
        {
          return "{" + Long.toString(id) + "} from CommandPanel.";
        }
        public void imageUp()
        {
         if (parent != null && parent.isVisible())
          {
            if (faxImage != null && faxImage.length > 0)
            {
              try
              {
                if (currentFaxIndex == -1)
                {
                  for (int i=0; faxImage!=null && i<faxImage.length; i++)
                    faxImage[i].imageVOffset -= dataPanel.getFaxInc();
                }
                else
                  faxImage[currentFaxIndex].imageVOffset -= dataPanel.getFaxInc();
              }
              catch (Exception ex) 
              {
                WWContext.getInstance().fireExceptionLogging(ex);
                ex.printStackTrace(); 
              }
            }
            displayStatus();
            repaint();
          }
        }
        public void imageDown() 
        {
         if (parent != null && parent.isVisible())
          {
            if (faxImage != null && faxImage.length > 0)
            {
              try
              {
                if (currentFaxIndex == -1)
                {
                  for (int i=0; faxImage!=null && i<faxImage.length; i++)
                    faxImage[i].imageVOffset += dataPanel.getFaxInc();
                }
                else
                  faxImage[currentFaxIndex].imageVOffset += dataPanel.getFaxInc();
              }
              catch (Exception e)
              {
                WWContext.getInstance().fireExceptionLogging(e);
                e.printStackTrace();
              }
            }
            displayStatus();
            repaint();
          }
        }
        public void imageLeft() 
        {
         if (parent != null && parent.isVisible())
          {
            if (faxImage != null && faxImage.length > 0)
            {
              try
              {
                if (currentFaxIndex == -1)
                {
                  for (int i=0; faxImage!=null && i<faxImage.length; i++)
                    faxImage[i].imageHOffset -= dataPanel.getFaxInc();
                }
                else
                  faxImage[currentFaxIndex].imageHOffset -= dataPanel.getFaxInc();
              }
              catch (Exception e)
              {
                WWContext.getInstance().fireExceptionLogging(e);
                e.printStackTrace();
              }
            }
            displayStatus();
            repaint();
          }
        }
        public void imageRight() 
        {
         if (parent != null && parent.isVisible())
          {
            if (faxImage != null && faxImage.length > 0)
            {
              try
              {
                if (currentFaxIndex == -1)
                {
                  for (int i=0; faxImage!=null && i<faxImage.length; i++)
                    faxImage[i].imageHOffset += dataPanel.getFaxInc();
                }
                else
                  faxImage[currentFaxIndex].imageHOffset += dataPanel.getFaxInc();
              }
              catch (Exception e)
              {
                WWContext.getInstance().fireExceptionLogging(e);
                e.printStackTrace();
              }
            }
            displayStatus();
            repaint();
          }
        }
        public void imageZoomin() 
        {
         if (parent != null && parent.isVisible())
          {
            if (faxImage != null && faxImage.length > 0)
            {
              if (currentFaxIndex == -1)
              {
                for (int i=0; faxImage!=null && i<faxImage.length; i++)
                  faxImage[i].imageScale *= dataPanel.getZoomFactor();
              }
              else
                faxImage[currentFaxIndex].imageScale *= dataPanel.getZoomFactor();
            }
            displayStatus();
            repaint();
          }
        }
        public void imageZoomout() 
        {
         if (parent != null && parent.isVisible())
          {
            if (faxImage != null && faxImage.length > 0)
            {
              if (currentFaxIndex == -1)
              {
                for (int i=0; faxImage!=null && i<faxImage.length; i++)
                  faxImage[i].imageScale /= dataPanel.getZoomFactor();
              }
              else
                faxImage[currentFaxIndex].imageScale /= dataPanel.getZoomFactor();
            }
            displayStatus();
            repaint();
          }
        }

        public void toggleGrabScroll(int cursorType)
        {
         if (parent != null && parent.isVisible())
          {
            switch (cursorType)
            {
              case ChartCommandPanelToolBar.REGULAR_CURSOR:
                chartPanel.setMouseDraggedEnabled(false);
                chartPanel.setMouseDraggedType(ChartPanel.MOUSE_DRAG_ZOOM);
                break;
              case ChartCommandPanelToolBar.DD_ZOOM:
                chartPanel.setMouseDraggedEnabled(true);
                chartPanel.setMouseDraggedType(ChartPanel.MOUSE_DRAG_ZOOM);
                break;
              case ChartCommandPanelToolBar.GRAB_SCROLL:
                chartPanel.setMouseDraggedEnabled(true);
                chartPanel.setMouseDraggedType(ChartPanel.MOUSE_DRAG_GRAB_SCROLL);
                break;
              case ChartCommandPanelToolBar.CROSS_HAIR_CURSOR:
                chartPanel.setMouseDraggedEnabled(true);
                chartPanel.setMouseDraggedType(ChartPanel.MOUSE_DRAW_LINE_ON_CHART);
                break;
              default:
                break;
            }
          }
        }
        
        public void ddZoomConfirmChanged(boolean b) 
        {
         if (parent != null && parent.isVisible())
            chartPanel.setConfirmDDZoom(b);
        }
        
        public void rotate(double d)
        {
         if (parent != null && parent.isVisible())
          {
            if (faxImage != null && faxImage.length > 0)
            {
              if (currentFaxIndex == -1)
              {
                for (int i=0; faxImage!=null && i<faxImage.length; i++)
                  faxImage[i].imageRotationAngle = d;
              }
              else
                faxImage[currentFaxIndex].imageRotationAngle = d;
            }
            repaint();
          }
        }
        
        public void chartUp()
        {
         if (parent != null && parent.isVisible())
          {
            try
            {
              if (chartPanel.getProjection() == ChartPanel.GLOBE_VIEW)
              {
                double foreAft = chartPanel.getGlobeViewForeAftRotation() - dataPanel.getLatLongInc();
                chartPanel.setGlobeViewForeAftRotation(foreAft);
                WWContext.getInstance().fireSetGlobeParameters(foreAft, 
                                                                   chartPanel.getGlobeViewLngOffset());
              }
              else if (chartPanel.getProjection() == ChartPanel.SATELLITE_VIEW)
              {
                double satLat = chartPanel.getSatelliteLatitude() - dataPanel.getLatLongInc();
                chartPanel.setSatelliteLatitude(satLat);
                WWContext.getInstance().fireSetSatelliteParameters(chartPanel.getSatelliteLatitude(), chartPanel.getSatelliteLongitude(), chartPanel.getSatelliteAltitude(), !chartPanel.isTransparentGlobe());
              }
              else
              {
                nLat -= dataPanel.getLatLongInc();
                sLat -= dataPanel.getLatLongInc();
              }
            }
            catch (Exception e)
            {
              WWContext.getInstance().fireExceptionLogging(e);
              e.printStackTrace();
            }
            if (chartPanel.getProjection() != ChartPanel.GLOBE_VIEW && 
                chartPanel.getProjection() != ChartPanel.SATELLITE_VIEW)
              chartPanel.setWidthFromChart(nLat, sLat, wLong, eLong);
  //        eLong = chartPanel.calculateEastG(nLat, sLat, wLong);
            chartPanel.setEastG(eLong);
            chartPanel.setWestG(wLong);
            chartPanel.setNorthL(nLat);
            chartPanel.setSouthL(sLat);
            chartPanel.repaint();  
            displayStatus();
          }
        }
        public void chartDown()
        {
         if (parent != null && parent.isVisible())
          {
            try
            {
              if (chartPanel.getProjection() == ChartPanel.GLOBE_VIEW)
              {
                double foreAft = chartPanel.getGlobeViewForeAftRotation() + dataPanel.getLatLongInc();
                chartPanel.setGlobeViewForeAftRotation(foreAft);
                WWContext.getInstance().fireSetGlobeParameters(foreAft, 
                                                                   chartPanel.getGlobeViewLngOffset());
              }                                                                   
              else if (chartPanel.getProjection() == ChartPanel.SATELLITE_VIEW)
              {
                double satLat = chartPanel.getSatelliteLatitude() + dataPanel.getLatLongInc();
                chartPanel.setSatelliteLatitude(satLat);
                WWContext.getInstance().fireSetSatelliteParameters(chartPanel.getSatelliteLatitude(), chartPanel.getSatelliteLongitude(), chartPanel.getSatelliteAltitude(), !chartPanel.isTransparentGlobe());
              }                                                                   
              else
              {
                nLat += dataPanel.getLatLongInc();
                sLat += dataPanel.getLatLongInc();
              }
            }
            catch (Exception e)
            {
              WWContext.getInstance().fireExceptionLogging(e);
              e.printStackTrace();
            }
            if (chartPanel.getProjection() != ChartPanel.GLOBE_VIEW &&
                chartPanel.getProjection() != ChartPanel.SATELLITE_VIEW)
              chartPanel.setWidthFromChart(nLat, sLat, wLong, eLong);
  //        eLong = chartPanel.calculateEastG(nLat, sLat, wLong);
            chartPanel.setEastG(eLong);
            chartPanel.setWestG(wLong);
            chartPanel.setNorthL(nLat);
            chartPanel.setSouthL(sLat);
            chartPanel.repaint();    
            displayStatus();
          }
        }
        public void chartLeft()
        {
         if (parent != null && parent.isVisible())
          {
            try
            {
              if (chartPanel.getProjection() == ChartPanel.GLOBE_VIEW)
              {
                double leftRight = chartPanel.getGlobeViewLngOffset() + dataPanel.getLatLongInc();
                chartPanel.setGlobeViewLngOffset(leftRight);
                WWContext.getInstance().fireSetGlobeParameters(chartPanel.getGlobeViewForeAftRotation(),
                                                                   leftRight);
              }                                                                   
              else if (chartPanel.getProjection() == ChartPanel.SATELLITE_VIEW)
              {
                double satLng = chartPanel.getSatelliteLongitude() + dataPanel.getLatLongInc();
                chartPanel.setSatelliteLongitude(satLng);
                WWContext.getInstance().fireSetSatelliteParameters(chartPanel.getSatelliteLatitude(), chartPanel.getSatelliteLongitude(), chartPanel.getSatelliteAltitude(), !chartPanel.isTransparentGlobe());
              }                                                                   
              else
              {
                wLong += dataPanel.getLatLongInc();
                eLong += dataPanel.getLatLongInc();
              }
            }
            catch (Exception e)
            {
              WWContext.getInstance().fireExceptionLogging(e);
              e.printStackTrace();
            }
            if (chartPanel.getProjection() != ChartPanel.GLOBE_VIEW &&
                chartPanel.getProjection() != ChartPanel.SATELLITE_VIEW)
              chartPanel.setWidthFromChart(nLat, sLat, wLong, eLong);
  //        eLong = chartPanel.calculateEastG(nLat, sLat, wLong);
            chartPanel.setEastG(eLong);
            chartPanel.setWestG(wLong);
            chartPanel.setNorthL(nLat);
            chartPanel.setSouthL(sLat);
            chartPanel.repaint();     
            displayStatus();
          }
        }
        public void chartRight()
        {
         if (parent != null && parent.isVisible())
          {
            try
            {
              if (chartPanel.getProjection() == ChartPanel.GLOBE_VIEW)
              {
                double currOffset = chartPanel.getGlobeViewLngOffset();
                double newOffset  = currOffset - dataPanel.getLatLongInc();
                chartPanel.setGlobeViewLngOffset(newOffset);
                WWContext.getInstance().fireSetGlobeParameters(chartPanel.getGlobeViewForeAftRotation(), 
                                                                   newOffset);
              }
              else if (chartPanel.getProjection() == ChartPanel.SATELLITE_VIEW)
              {
                double satLng = chartPanel.getSatelliteLongitude() - dataPanel.getLatLongInc();
                chartPanel.setSatelliteLongitude(satLng);
                WWContext.getInstance().fireSetSatelliteParameters(chartPanel.getSatelliteLatitude(), chartPanel.getSatelliteLongitude(), chartPanel.getSatelliteAltitude(), !chartPanel.isTransparentGlobe());
              }                                                                   
              else
              {
                wLong -= dataPanel.getLatLongInc();
                eLong -= dataPanel.getLatLongInc();
              }
            }
            catch (Exception e)
            {
              WWContext.getInstance().fireExceptionLogging(e);
              e.printStackTrace();
            }
  //        eLong = chartPanel.calculateEastG(nLat, sLat, wLong);
            if (chartPanel.getProjection() != ChartPanel.GLOBE_VIEW &&
                chartPanel.getProjection() != ChartPanel.SATELLITE_VIEW)
              chartPanel.setWidthFromChart(nLat, sLat, wLong, eLong);
            chartPanel.setEastG(eLong);
            chartPanel.setWestG(wLong);
            chartPanel.setNorthL(nLat);
            chartPanel.setSouthL(sLat);
            chartPanel.repaint();    
            displayStatus();
          }
        }
        public void chartZoomin() 
        {
         if (parent != null && parent.isVisible())
          {
            chartPanel.setZoomFactor(dataPanel.getZoomFactor());
            chartPanel.zoomIn();
            displayStatus();
          }
        }
        public void chartZoomout() 
        {
         if (parent != null && parent.isVisible())
          {
            chartPanel.setZoomFactor(dataPanel.getZoomFactor());
            chartPanel.zoomOut();
            displayStatus();
          }
        }
        public void allLayerZoomIn() 
        {
         if (parent != null && parent.isVisible())
          {
            double f = dataPanel.getZoomFactor();
            chartPanel.setZoomFactor(f);
            chartPanel.zoomIn();
            for (int i=0; faxImage!=null && i<faxImage.length; i++)
              faxImage[i].imageScale *= f;
            repaint();
          }
        }

        public void allLayerZoomOut() 
        {
         if (parent != null && parent.isVisible())
          {
            double f = dataPanel.getZoomFactor();
            chartPanel.setZoomFactor(f);
            chartPanel.zoomOut();
            for (int i=0; faxImage!=null && i<faxImage.length; i++)
              faxImage[i].imageScale /= f;
            repaint();
          }
        }
        
        public void store() // Save, and update
        {
          if (parent != null && parent.isVisible())
          {
            boolean ok2go = true;
            int nbWaz = 0;
            boolean update = false;
            
            for (int i=0; faxImage!=null && i<faxImage.length; i++)
            {
              if (faxImage[i].fileName.startsWith(WWContext.WAZ_PROTOCOL_PREFIX))
              {
                nbWaz++;
              }
            }
            if (faxImage != null)
            {
              if (nbWaz > 0 && nbWaz != faxImage.length)
              {
                ok2go = false; // At least one fax does not come from the archive
              }
              if (ok2go && nbWaz > 0)
                update = true;
            }
            
            if (ok2go)
            {
              // Check if file name has changed!
              if (gribFileName != null && gribFileName.startsWith(WWContext.WAZ_PROTOCOL_PREFIX))
              {
                update = true;
                if (faxImage != null && nbWaz != faxImage.length)
                  ok2go = false;
              }
            }
            if (!ok2go)
            {
              String compositeName = WWContext.getInstance().getCurrentComposite();
              String newCompositeName = compositeName.substring(0, compositeName.indexOf(".waz")) + ".new.waz";
              JOptionPane.showMessageDialog(instance, 
                                            WWGnlUtilities.buildMessage("no-save-from-archive", new String[] { newCompositeName }), 
                                            WWGnlUtilities.buildMessage("save-composite"),
                                            JOptionPane.WARNING_MESSAGE);
              update = true;
              ok2go = true;
              try
              {
                ZipOutputStream out = new ZipOutputStream(new FileOutputStream(newCompositeName));
                // Duplicate current content
                ZipInputStream in = new ZipInputStream(new FileInputStream(compositeName));
                boolean go = true;
                while (go)
                {
                  ZipEntry ze = in.getNextEntry();
                  if (ze == null)
                    go = false;
                  else
                  {
                    System.out.println("Duplicating " + ze.getName());                  
                    WWGnlUtilities.writeToArchive(out, in, ze.getName()); 
                  }
                }
                // Add new files to the existing composite (file://, not waz://)
                // 1 - Fax(es)
                for (int i=0; faxImage!=null && i<faxImage.length; i++)
                {
                  if (!faxImage[i].fileName.startsWith(WWContext.WAZ_PROTOCOL_PREFIX))
                  {
                    String newFileName = faxImage[i].fileName;
                    File f = new File(newFileName);
                    String zipEntryName = "faxes/" + f.getName();
                    faxImage[i].fileName = WWContext.WAZ_PROTOCOL_PREFIX + zipEntryName; // TODO Suggest to delete the newly added files?
                    WWGnlUtilities.writeToArchive(out, f.getAbsolutePath(), zipEntryName);                  
                  }
                }                
                // 2 - GRIB
                if (gribFileName != null && !gribFileName.startsWith(WWContext.WAZ_PROTOCOL_PREFIX))
                {
                  String newFileName = gribFileName;
                  File f = new File(newFileName);
                  String zipEntryName = "grib/" + f.getName();
                  gribFileName = WWContext.WAZ_PROTOCOL_PREFIX + zipEntryName; // TODO Suggest to delete the newly added files?
                  WWGnlUtilities.writeToArchive(out, f.getAbsolutePath(), zipEntryName);                  
                }
                // 3 - Update composite.xml (taken care of later)
                in.close();
                out.close();
                // TODO Swap pointers to archive
//              System.out.println("Saved as " + newCompositeName);
                compositeName = newCompositeName;
                WWContext.getInstance().setCurrentComposite(compositeName);
              }
              catch (Exception ex)
              {
                JOptionPane.showMessageDialog(null, ex.toString(), "Updating archive", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
                ok2go = false;
              }
            }
            if (update)
            {
              String compositeName = WWContext.getInstance().getCurrentComposite();
           // JOptionPane.showMessageDialog(instance, "Update not implemented yet...", "Updating", JOptionPane.WARNING_MESSAGE);
           // ok2go = false;
              System.out.println("Updating " + compositeName);
            }
            // A comment for this composite?
            if (ok2go && currentComment.trim().length() > 0)
            {
              Object message = WWGnlUtilities.buildMessage("confirm-comment", 
                                                           new String[] { currentComment });
              String title = WWGnlUtilities.buildMessage("composite-comment");
              int nbCR = WWGnlUtilities.nbOccurs(currentComment.trim(), '\n');
              if (nbCR > 5) // then create an editor pane
              {
                JScrollPane jScrollPane = new JScrollPane();
                JTextArea jta = new JTextArea(currentComment.trim());
                jta.setEditable(false);
                jScrollPane.getViewport().add(jta, null);
                jScrollPane.setPreferredSize(new Dimension(400, 200));
                message = jScrollPane;
                title = WWGnlUtilities.buildMessage("confirm-comment-2");
              }
              int resp = JOptionPane.showConfirmDialog(instance, 
                                                       message, 
                                                       title,
                                                       JOptionPane.YES_NO_CANCEL_OPTION, 
                                                       JOptionPane.QUESTION_MESSAGE);
              if (resp == JOptionPane.NO_OPTION)                                                            
                currentComment = "";
              else if (resp == JOptionPane.CANCEL_OPTION)
                ok2go = false;
            }
            if (ok2go)
            {
              final boolean updateComposite = update;
//            Runnable heavyRunnable = new Runnable() // Show progress bar
              Thread heavyRunnable = new Thread("progress-bar") // Show progress bar
              {
  //            ProgressMonitor monitor = null;
                
                public void run()
                {
//                WWContext.getInstance().fireSetLoading(true, WWGnlUtilities.buildMessage("gathering-storing"));
                  if (!updateComposite)
                  {                    
                    WWContext.getInstance().setMonitor(ProgressUtil.createModalProgressMonitor(WWContext.getInstance().getMasterTopFrame(), 1, true, true));
                    ProgressMonitor monitor = WWContext.getInstance().getMonitor();
                    if (monitor != null)
                    {
                      synchronized (monitor)
                      {
                        monitor.start(WWGnlUtilities.buildMessage("gathering-storing"));
                      }
                    }
                    WWContext.getInstance().setAel4monitor(new ApplicationEventListener()
                    {
                      public String toString()
                      {
                        return "{" + Long.toString(id) + "} from Runnable in CommandPanel (1).";
                      }
                      public void progressing(String mess)
                      {
                        ProgressMonitor monitor = WWContext.getInstance().getMonitor();
                        if (monitor != null)
                        {
                          synchronized (monitor)
                          {
                            monitor.setCurrent(mess, WWContext.getInstance().getMonitor().getCurrent());
                          }
                        }
                      }                      
                      public void interruptProgress()
                      {
                        System.out.println("Interruption requested (1)...");
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
                      }
                    });
                    WWContext.getInstance().addApplicationListener(WWContext.getInstance().getAel4monitor());
                  }
                  try
                  { 
                    if (!updateComposite)
                      runStorageThread(); 
                    else
                      runStorageThread(updateComposite, WWContext.getInstance().getCurrentComposite());
                  }
                  finally
                  {
//                  WWContext.getInstance().fireSetLoading(false, WWGnlUtilities.buildMessage("gathering-storing"));
                    if (!updateComposite)
                    {
                      // to ensure that progress dlg is closed in case of any exception
                      ProgressMonitor monitor = WWContext.getInstance().getMonitor();
                      if (monitor != null)
                      {
                        synchronized (monitor)
                        {
                          try
                          {
                            if (monitor.getCurrent() != monitor.getTotal())
                              monitor.setCurrent(null, monitor.getTotal());
                            WWContext.getInstance().removeApplicationListener(WWContext.getInstance().getAel4monitor());
                            WWContext.getInstance().setAel4monitor(null);
                            WWContext.getInstance().setMonitor(null);
                          }
                          catch (Exception ex)
                          {
                            ex.printStackTrace();
                          }
                        }
                      }
                    }
                  }
                }
              };
//            new Thread(heavyRunnable).start();
              heavyRunnable.start();
            }
          }
        }
        
        public void storeAs() // Save as...
        {
          String compositeName = WWContext.getInstance().getCurrentComposite();
          final String newFileName = WWGnlUtilities.chooseFile(instance, 
                                                               JFileChooser.FILES_ONLY, 
                                                               new String[] { "xml", "waz" }, 
                                                              "Composites", 
                                                              ParamPanel.data[ParamData.COMPOSITE_ROOT_DIR][ParamData.VALUE_INDEX].toString(),
                                                              "Save as", 
                                                              "Save Composite as");
          if (newFileName.trim().length() > 0)
          {
  //        System.out.println("Saving " + compositeName + " as " + newFileName);
            try
            {
              FileInputStream in   = new FileInputStream(compositeName);
              FileOutputStream out = new FileOutputStream(newFileName);
              Utilities.copy(in, out);
              WWContext.getInstance().setCurrentComposite(newFileName);
              in.close();
              out.close();
              store();
            }
            catch (FileNotFoundException fnfe)
            {
              JOptionPane.showMessageDialog(instance, fnfe.toString(), "Save as...", JOptionPane.ERROR_MESSAGE);
              fnfe.printStackTrace();
            }
            catch (Exception ex)
            {
              ex.printStackTrace();          
            }
          }
        }
        
        public void googleMapRequested()
        {
         if (parent != null && parent.isVisible())
            generateGoogleFiles(GoogleUtil.OPTION_MAP);
        }
        
        public void googleEarthRequested()
        {
         if (parent != null && parent.isVisible())
            generateGoogleFiles(GoogleUtil.OPTION_EARTH);
        }
        
        public void generatePattern() 
        {
         if (parent != null && parent.isVisible())
          {
            XMLDocument pattern = new XMLDocument();
            XMLElement root = (XMLElement)pattern.createElement("pattern");
            pattern.appendChild(root);
            
            XMLElement faxcollection = (XMLElement)pattern.createElement("fax-collection");
            root.appendChild(faxcollection);
            // Ask for a hint for each fax
            FaxPatternTablePanel fptp = new FaxPatternTablePanel();
            if (faxImage == null)
            {
              JOptionPane.showMessageDialog(instance, WWGnlUtilities.buildMessage("no-fax-loaded"), WWGnlUtilities.buildMessage("pattern-generation"), JOptionPane.WARNING_MESSAGE);
              return;
            }
            int nbGrib = (gribFileName.trim().length() == 0?0:1);
            Object[][] data = new Object[faxImage.length + nbGrib][2];
            for (int i=0; faxImage!=null && i<faxImage.length; i++)
            {
              String s = Integer.toString(i+1) + " - " + faxImage[i].fileName.substring(faxImage[i].fileName.lastIndexOf(File.separator) + 1);
              Color c  = faxImage[i].color;
              data[i][0] = new FaxPatternType(s, c);
              data[i][ParamData.VALUE_INDEX] = "";
            }
            if (nbGrib == 1)
            {
              data[faxImage.length][0] = "GRIB Zone";
              data[faxImage.length][ParamData.VALUE_INDEX] = "";
            }
            fptp.setData(data);
            int resp = JOptionPane.showConfirmDialog(instance, fptp, WWGnlUtilities.buildMessage("pattern-generation"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (resp != JOptionPane.OK_OPTION)
              return;
            
            data =  fptp.getData();
            
            for (int i=0; data!=null && i<data.length - nbGrib; i++)
            {
              XMLElement fax = (XMLElement)pattern.createElement("fax");
              faxcollection.appendChild(fax);
              fax.setAttribute("hint", (String)data[i][ParamData.VALUE_INDEX]); // store the hint here
              fax.setAttribute("color", WWGnlUtilities.colorToString(faxImage[i].color));
              if (whRatio != 1D)
                fax.setAttribute("wh-ratio", Double.toString(whRatio));
              fax.setAttribute("transparent", Boolean.toString(faxImage[i].transparent));
              fax.setAttribute("color-change", Boolean.toString(faxImage[i].colorChange));
          
              XMLElement faxScale = (XMLElement)pattern.createElement("faxScale");
              fax.appendChild(faxScale);
              Text faxScaleText = pattern.createTextNode("#text");
              faxScaleText.setNodeValue(Double.toString(faxImage[i].imageScale));
              faxScale.appendChild(faxScaleText);
              
              XMLElement faxXoffset = (XMLElement)pattern.createElement("faxXoffset");
              fax.appendChild(faxXoffset);
              Text faxXoffsetText = pattern.createTextNode("#text");
              faxXoffsetText.setNodeValue(Integer.toString(faxImage[i].imageHOffset));
              faxXoffset.appendChild(faxXoffsetText);
              
              XMLElement faxYoffset = (XMLElement)pattern.createElement("faxYoffset");
              fax.appendChild(faxYoffset);
              Text faxYoffsetText = pattern.createTextNode("#text");
              faxYoffsetText.setNodeValue(Integer.toString(faxImage[i].imageVOffset));
              faxYoffset.appendChild(faxYoffsetText);
              
              XMLElement faxRotation = (XMLElement)pattern.createElement("faxRotation");
              fax.appendChild(faxRotation);
              Text faxRotationText = pattern.createTextNode("#text");
              faxRotationText.setNodeValue(Double.toString(faxImage[i].imageRotationAngle));
              faxRotation.appendChild(faxRotationText);
            }
            XMLElement grib = (XMLElement)pattern.createElement("grib");
  
  //        grib.setAttribute("wind-only", Boolean.toString(windOnly));
  //        grib.setAttribute("with-contour", Boolean.toString(displayContourLines));
            
            grib.setAttribute("display-TWS-Data",    Boolean.toString(displayTws));
            grib.setAttribute("display-PRMSL-Data",  Boolean.toString(displayPrmsl));
            grib.setAttribute("display-500HGT-Data", Boolean.toString(display500mb));
            grib.setAttribute("display-WAVES-Data",  Boolean.toString(displayWaves));
            grib.setAttribute("display-TEMP-Data",   Boolean.toString(displayTemperature));
            grib.setAttribute("display-PRATE-Data",  Boolean.toString(displayRain));
            
            grib.setAttribute("display-TWS-contour",    Boolean.toString(displayContourTWS));
            grib.setAttribute("display-PRMSL-contour",  Boolean.toString(displayContourPRMSL));
            grib.setAttribute("display-500HGT-contour", Boolean.toString(displayContour500mb));
            grib.setAttribute("display-WAVES-contour",  Boolean.toString(displayContourWaves));
            grib.setAttribute("display-TEMP-contour",   Boolean.toString(displayContourTemp));
            grib.setAttribute("display-PRATE-contour",  Boolean.toString(displayContourPrate));
  
            grib.setAttribute("display-TWS-3D",    Boolean.toString(display3DTws));
            grib.setAttribute("display-PRMSL-3D",  Boolean.toString(display3DPrmsl));
            grib.setAttribute("display-500HGT-3D", Boolean.toString(display3D500mb));
            grib.setAttribute("display-WAVES-3D",  Boolean.toString(display3DWaves));
            grib.setAttribute("display-TEMP-3D",   Boolean.toString(display3DTemperature));
            grib.setAttribute("display-PRATE-3D",  Boolean.toString(display3DRain));
  
            root.appendChild(grib);
            Text gribText = pattern.createTextNode("#text");
            String gribHint = "";
            if (nbGrib == 1)
              gribHint = (String)data[faxImage.length][ParamData.VALUE_INDEX];
            gribText.setNodeValue(gribHint);
            grib.appendChild(gribText);
           
            XMLElement projection = (XMLElement)pattern.createElement("projection");
            root.appendChild(projection);
            switch (getProjection())
            {
              case ChartPanel.MERCATOR:
                projection.setAttribute("type", MERCATOR);
                break;
              case ChartPanel.ANAXIMANDRE:
                projection.setAttribute("type", ANAXIMANDRE);
                break;
              case ChartPanel.STEREOGRAPHIC:
                projection.setAttribute("type", STEREO);
                break;
              case ChartPanel.POLAR_STEREOGRAPHIC:
                projection.setAttribute("type", POLAR_STEREO);
                break;
              case ChartPanel.LAMBERT:
                projection.setAttribute("type", LAMBERT);
                projection.setAttribute("contact-parallel", Double.toString(chartPanel.getContactParallel()));
                break;
              case ChartPanel.CONIC_EQUIDISTANT:
                projection.setAttribute("type", CONIC_EQU);
                projection.setAttribute("contact-parallel", Double.toString(chartPanel.getContactParallel()));
                break;
              case ChartPanel.GLOBE_VIEW:
                projection.setAttribute("type", GLOBE);
                break;
              case ChartPanel.SATELLITE_VIEW:
                projection.setAttribute("type", SATELLITE);
                projection.setAttribute("nadir-latitude", Double.toString(chartPanel.getSatelliteLatitude()));              
                projection.setAttribute("nadir-longitude", Double.toString(chartPanel.getSatelliteLongitude()));              
                projection.setAttribute("altitude", Double.toString(chartPanel.getSatelliteAltitude()));              
                projection.setAttribute("opaque", Boolean.toString(!chartPanel.isTransparentGlobe()));              
               break;
              default:
                projection.setAttribute("type", MERCATOR);
                break;
            }
           
            XMLElement north = (XMLElement)pattern.createElement("north");
            root.appendChild(north);
            Text northText = pattern.createTextNode("#text");
            northText.setNodeValue(Double.toString(nLat));
            north.appendChild(northText);
  
            XMLElement south = (XMLElement)pattern.createElement("south");
            root.appendChild(south);
            Text southText = pattern.createTextNode("#text");
            southText.setNodeValue(Double.toString(sLat));
            south.appendChild(southText);
            
            XMLElement east = (XMLElement)pattern.createElement("east");
            root.appendChild(east);
            Text eastText = pattern.createTextNode("#text");
            eastText.setNodeValue(Double.toString(eLong));
            east.appendChild(eastText);
           
            XMLElement west = (XMLElement)pattern.createElement("west");
            root.appendChild(west);
            Text westText = pattern.createTextNode("#text");
            westText.setNodeValue(Double.toString(wLong));
            west.appendChild(westText);
           
            XMLElement chartwidth = (XMLElement)pattern.createElement("chartwidth");
            root.appendChild(chartwidth);
            Text chartwidthText = pattern.createTextNode("#text");
            chartwidthText.setNodeValue(Integer.toString(chartPanel.getWidth()));
            chartwidth.appendChild(chartwidthText);
           
            XMLElement chartheight = (XMLElement)pattern.createElement("chartheight");
            root.appendChild(chartheight);
            Text chartheightText = pattern.createTextNode("#text");
            chartheightText.setNodeValue(Integer.toString(chartPanel.getHeight()));
            chartheight.appendChild(chartheightText);
            
            String fileName = WWGnlUtilities.chooseFile(instance, 
                                                      JFileChooser.FILES_ONLY, 
                                                      new String[] { "ptrn" }, 
                                                      "Patterns", 
                                                      ParamPanel.data[ParamData.PATTERN_DIR][ParamData.VALUE_INDEX].toString(), 
                                                      "Save", 
                                                      "Create Pattern");
            if (fileName != null && fileName.trim().length() > 0)
            {
              fileName = Utilities.makeSureExtensionIsOK(fileName, ".ptrn");
              try
              {
                // Check file existence first
                File patt = new File(fileName);
                boolean ok = true;
                if (patt.exists())
                {
                  int rsp = JOptionPane.showConfirmDialog(instance, WWGnlUtilities.buildMessage("pattern-already-exist", new String[] { fileName }), WWGnlUtilities.buildMessage("store-pattern"), 
                                                          JOptionPane.YES_NO_OPTION, 
                                                          JOptionPane.WARNING_MESSAGE);
                  if (rsp == JOptionPane.NO_OPTION)
                    ok = false;
                }
                if (ok)
                {
  //              pattern.print(System.out);
                  pattern.print(new FileOutputStream(patt));
                  WWContext.getInstance().fireReloadPatternTree();
                }
              }
              catch (Exception e)
              {
                WWContext.getInstance().fireExceptionLogging(e);
                e.printStackTrace();
              }
            }
          }
        }

        public void restore() 
        {
         if (parent != null && parent.isVisible())
          {
            // Read data
            final String fileName = WWGnlUtilities.chooseFile(instance, 
                                                            JFileChooser.FILES_ONLY, 
                                                            new String[] { "xml", "waz" }, 
                                                            "Composites", 
                                                            ParamPanel.data[ParamData.COMPOSITE_ROOT_DIR][ParamData.VALUE_INDEX].toString(),
                                                            "Open", 
                                                            "Open Composite");
            if (fileName != null && fileName.trim().length() > 0)
            {
              Thread loader = new Thread("composite-loader")
              {
                public void run()
                {
    //            System.out.println("Loader top");
                  WWContext.getInstance().fireSetLoading(true);
                  restoreComposite(fileName);
                  WWContext.getInstance().fireSetLoading(false);
    //            System.out.println("Loader bottom");
                }
              };
              loader.start();
            }
            // Apply values
            if (chartPanel.getProjection() != ChartPanel.GLOBE_VIEW &&
                chartPanel.getProjection() != ChartPanel.SATELLITE_VIEW)
              chartPanel.setWidthFromChart(nLat, sLat, wLong, eLong);
            chartPanel.setEastG(eLong);
            chartPanel.setWestG(wLong);
            chartPanel.setNorthL(nLat);
            chartPanel.setSouthL(sLat);
            chartPanel.repaint();    
            displayStatus();
          }
        }
        
        public void loadWithPattern() 
        {
         if (parent != null && parent.isVisible())
          {
            // Read data
            final String fileName = WWGnlUtilities.chooseFile(instance, 
                                                              JFileChooser.FILES_ONLY, 
                                                              new String[] { "ptrn" }, 
                                                              "Patterns", 
                                                              ParamPanel.data[ParamData.PATTERN_DIR][ParamData.VALUE_INDEX].toString(), 
                                                              "Open", 
                                                              "Use Pattern");
            loadWithPattern(fileName);
          }
        }

        public void addFaxImage(CommandPanel.FaxImage fi)
        {
          if (parent != null && parent.isVisible())
          {
		        FaxImage[] newFaxImage;
            int nbFax = 0;
            if (faxImage == null)
              newFaxImage = new FaxImage[1];
            else
            {
              newFaxImage = new FaxImage[faxImage.length + 1];
              nbFax = faxImage.length;
            }
            int i, j = 0;
            for (i=0, j=0; i<nbFax; i++)
            {
              try
              {
                if (faxImage[i] != null)
                  newFaxImage[j++] = faxImage[i].clone();
                else
                  System.out.println("Found null faxImage idx " + i);
              }
              catch (Exception ex)
              {
                ex.printStackTrace();
              }
            }
//          newFaxImage[nbFax] = fi;
            try
            {
              newFaxImage[j] = fi;
            }
            catch (Exception e)
            {
              e.printStackTrace();
            }
            faxImage = newFaxImage; 
            // FaxTypes (for the checkBoxes)
            FaxType[] ft = new FaxType[faxImage.length];
            for (i=0; i<ft.length; i++)
            {
              ft[i] = new FaxType(faxImage[i].fileName, 
                                  faxImage[i].color, 
                                  Boolean.valueOf(true), 
                                  faxImage[i].transparent, 
                                  faxImage[i].imageRotationAngle, 
                                  faxImage[i].faxOrigin, 
                                  faxImage[i].faxTitle, 
                                  faxImage[i].colorChange);
              ft[i].setRank(i+1);
              ft[i].setComment(faxImage[i].comment);
              ft[i].setShow(true);
              ft[i].setTransparent(faxImage[i].transparent);
              repaint(); // Repaint between each fax
            }
            setCheckBoxes(ft);
            if (ft.length > 0)
            {
              WWContext.getInstance().fireFaxLoaded();
              WWContext.getInstance().fireFaxesLoaded(ft);
            }
            setCheckBoxes(ft);
            repaint();
          }
        }
        
        public void loadWithPattern(final String fileName) 
        {
          if (parent != null && parent.isVisible())
          {
            if (fileName != null && fileName.trim().length() > 0)
            {
              // Reset comment
              currentComment = "";
              boolean dyn = WWGnlUtilities.isPatternDynamic(fileName); // TODO http:// protocol
              if (dyn)
              {
//              Runnable heavyRunnable = new Runnable()
                Thread heavyRunnable = new Thread("pattern-loader")
                {
  //              ProgressMonitor monitor = null;
  
                  public void run()
                  {
                    WWContext.getInstance().setMonitor(ProgressUtil.createModalProgressMonitor(WWContext.getInstance().getMasterTopFrame(), 1, true, true));
                    ProgressMonitor monitor = WWContext.getInstance().getMonitor();
                    if (monitor != null)
                    {
                      synchronized (monitor)
                      {
                        monitor.start(WWGnlUtilities.buildMessage("loading-with-pattern"));
                      }
                    }
                    if (WWContext.getInstance().getAel4monitor() != null)
                    {
                      System.out.println("Warning!!! AELMonitor != null !! (2, in " + this.getClass().getName() + ")" );  
                    }
                    WWContext.getInstance().setAel4monitor(new ApplicationEventListener()
                      {
                        public String toString()
                        {
                          return "{" + Long.toString(id) + "} from Runnable in CommandPanel (2).";
                        }
                        public void progressing(String mess)
                        {
                          ProgressMonitor monitor = WWContext.getInstance().getMonitor();
                          if (monitor != null)
                          {
                            synchronized (monitor)
                            {
                              monitor.setCurrent(mess, monitor.getCurrent());
                            }
                          }
                        }
  
  //                    public void interruptProcess()
  //                    {
  //                      System.out.println("Interrupting Pattern Loading.");
  //                    }
                      });
                    WWContext.getInstance().addApplicationListener(WWContext.getInstance().getAel4monitor());
  
                    try
                    {
                      WWContext.getInstance().fireSetLoading(true);
                      createFromPattern(fileName);
                      WWContext.getInstance().fireSetLoading(false);
                    }
                    finally
                    {
  //                  System.out.println("End of Progress Monitor");
                      // to ensure that progress dlg is closed in case of any exception
                   // ProgressMonitor pm = WWContext.getInstance().getMonitor();
                      if (WWContext.getInstance().getMonitor() != null)
                      {
                        synchronized (WWContext.getInstance().getMonitor())
                        {
                          try
                          {
                            if (WWContext.getInstance().getMonitor().getCurrent() != WWContext.getInstance().getMonitor().getTotal())
                              WWContext.getInstance().getMonitor().setCurrent(null, WWContext.getInstance().getMonitor().getTotal());
  //                        WWContext.getInstance().removeApplicationListener(WWContext.getInstance().getAel4monitor());
                            WWContext.getInstance().setAel4monitor(null);
                            WWContext.getInstance().setMonitor(null);
                          }
                          catch (Exception ex)
                          {
                            ex.printStackTrace();
                          }
                        }
                      }
                    }
                  }
                };
//              new Thread(heavyRunnable).start();
                heavyRunnable.start();
              }
              else
              {
                Thread loader = new Thread("non-dynamic-pattern-loader")
                {
                  public void run()
                  {
      //            System.out.println("Loader top");
                    WWContext.getInstance().fireSetLoading(true);
                    createFromPattern(fileName); // TODO http protocol
                    WWContext.getInstance().fireSetLoading(false);
      //            System.out.println("Loader bottom");
                  }
                };
                loader.start();
              }
            }
            // Apply values
            if (chartPanel.getProjection() != ChartPanel.GLOBE_VIEW &&
                chartPanel.getProjection() != ChartPanel.SATELLITE_VIEW)
              chartPanel.setWidthFromChart(nLat, sLat, wLong, eLong);
            chartPanel.setEastG(eLong);
            chartPanel.setWestG(wLong);
            chartPanel.setNorthL(nLat);
            chartPanel.setSouthL(sLat);
            chartPanel.repaint();    
            displayStatus();
          }
        }
        
        public void setProjection(int p)
        {
         if (parent != null && parent.isVisible())
          {
  //        System.out.println("Setting projection to " + p);
            chartPanel.setProjection(p);
  
            if (p != ChartPanel.GLOBE_VIEW && tmpNLat != Double.MAX_VALUE &&
                                              tmpSLat != Double.MAX_VALUE &&
                                              tmpELong != Double.MAX_VALUE &&
                                              tmpWLong != Double.MAX_VALUE)
            {
              // Restore to where it was
              nLat = tmpNLat;
              sLat = tmpSLat;
              eLong = tmpELong;
              wLong = tmpWLong;
              chartPanel.setNorthL(nLat);
              chartPanel.setSouthL(sLat);
              chartPanel.setEastG(eLong);
              chartPanel.setWestG(wLong);
            }
  //        if (p == ChartPanel.ANAXIMANDRE)
  //          chartPanel.setWidthFromChart(nLat, sLat, eLong, wLong);
  //        if (p != ChartPanel.LAMBERT)
              
            chartPanel.repaint();            
          }
        }
        
        public void setContactParallel(double d)
        {
         if (parent != null && parent.isVisible())
          {
            chartPanel.setContactParallel(d);
            chartPanel.repaint();
          }
        }
        
        public void setGlobeProjPrms(double lat, double lng, double tilt, boolean b) 
        {
         if (parent != null && parent.isVisible())
          {
            // Temporary
            tmpNLat = nLat;
            tmpSLat = sLat;
            tmpELong = eLong;
            tmpWLong = wLong;
            
            nLat =  90D;
            sLat = -90D;
            eLong =  180D;
            wLong = -180D;
            chartPanel.setNorthL(nLat);
            chartPanel.setSouthL(sLat);
            chartPanel.setEastG(eLong);
            chartPanel.setWestG(wLong);
            
            chartPanel.setGlobeViewLngOffset(lng);
            chartPanel.setGlobeViewForeAftRotation(lat);
            chartPanel.setGlobeViewRightLeftRotation(tilt);
            chartPanel.setTransparentGlobe(!b);
  
            chartPanel.repaint();
          }
        }
        
        public void setSatelliteProjPrms(double lat, double lng, double alt, boolean b) 
        {
         if (parent != null && parent.isVisible())
          {
            // Temporary
            tmpNLat = nLat;
            tmpSLat = sLat;
            tmpELong = eLong;
            tmpWLong = wLong;
            
            nLat =  90D;
            sLat = -90D;
            eLong =  180D;
            wLong = -180D;
            chartPanel.setNorthL(nLat);
            chartPanel.setSouthL(sLat);
            chartPanel.setEastG(eLong);
            chartPanel.setWestG(wLong);
            
            chartPanel.setSatelliteAltitude(alt);
            chartPanel.setSatelliteLatitude(lat);
            chartPanel.setSatelliteLongitude(lng);
            chartPanel.setTransparentGlobe(!b);
  
            chartPanel.repaint();
          }
        }
        
        public void gribForward()
        {
         if (parent != null && parent.isVisible())
          {
            gribIndex++;
            if (gribIndex > (wgd.length - 1))
              gribIndex = 0;
            smoothingRequired = true;
            updateGRIBDisplay();
          }
        }
        
        public void gribBackward()
        {
         if (parent != null && parent.isVisible())
          {
            gribIndex--;
            if (gribIndex < 0)
              gribIndex = (wgd.length - 1);
            smoothingRequired = true;
            updateGRIBDisplay();
          }
        }
        
        public void setGribIndex(int idx) 
        {
         if (parent != null && parent.isVisible())
          {
            if (gribIndex != idx)
            {
              gribIndex = idx;
              smoothingRequired = true;
              updateGRIBDisplay();
            }
          }
        }

        public void gribAnimate()
        {
         if (parent != null && parent.isVisible())
          {
            if (animateThread == null)
            {
              boolean go = true;
              if ((instance.isDisplay3DPrmsl() && instance.isTherePrmsl())|| 
                  (instance.isDisplay3D500mb() && instance.isThere500mb()) || 
                  (instance.isDisplay3DTemperature() && instance.isThereTemperature()) ||
                  (instance.isDisplay3DWaves() && instance.isThereWaves()) /* || 
                  instance.displayContourLines */)
              {
                int resp = JOptionPane.showConfirmDialog(instance, WWGnlUtilities.buildMessage("might-take-time"), WWGnlUtilities.buildMessage("animate"), 
                                                         JOptionPane.YES_NO_OPTION, 
                                                         JOptionPane.QUESTION_MESSAGE);
                go = (resp == JOptionPane.YES_OPTION);
              }
              if (go)
              {
                animateThread = new AnimateThread();
//              System.out.println("New Animated Thread");
                animateThread.start();
                WWContext.getInstance().fireStartGRIBAnimation();
              }
            }
            else
            {
              animateThread.freeze();
              animateThread = null;
//            System.out.println("Resting Animated Thread...");
              WWContext.getInstance().fireStopGRIBAnimation();
            }
          }
        }
        
        public void showWindInGoogle()
        {
         if (parent != null && parent.isVisible())
          {
  //        System.out.println("Showing Wind in Google");  
            GribHelper.GribConditionData gribData = null;
            if (wgd != null)
            {
              gribData = wgd[gribIndex];
              if (gribData != null)
              {              
                try
                {
                  Date gribDate = gribData.getDate(); // TimeUtil.getGMT(gribData.getDate());
                  Calendar cal = new GregorianCalendar();
                  cal.setTime(gribDate);
                  
                  int year    = cal.get(Calendar.YEAR);
                  int month   = cal.get(Calendar.MONTH);
                  int day     = cal.get(Calendar.DAY_OF_MONTH);
                  int hours   = cal.get(Calendar.HOUR_OF_DAY);
                  int minutes = cal.get(Calendar.MINUTE);
                  int seconds = cal.get(Calendar.SECOND);
                  
                  String dateStr = Integer.toString(year) + "-" + WWGnlUtilities.MONTH[month] + "-" + WWGnlUtilities.DF2.format(day) + " " + WWGnlUtilities.DF2.format(hours) + ":" + WWGnlUtilities.DF2.format(minutes) + ":" + WWGnlUtilities.DF2.format(seconds) + " GMT";
                  Utilities.makeSureTempExists();                                 
                  BufferedWriter bw = new BufferedWriter(new FileWriter("temp" + File.separator + "googlewind.js"));
                  bw.write("GRIBdate='" + dateStr + "';\n");
                  double _gRight  = gribData.getELng();
                  double _gLeft   = gribData.getWLng();
                  double _lTop    = gribData.getNLat();
                  double _lBottom = gribData.getSLat();
                  
                  if (Utilities.sign(_gLeft) != Utilities.sign(_gRight))
                    _gLeft -= 360;
  
                  double centerLat = (_lTop + _lBottom) / 2D;
                  double centerLng = (_gRight + _gLeft) / 2D;
                  
                  bw.write("centerlatitude=" + Double.toString(centerLat) + ";\n");
                  bw.write("centerlongitude=" + Double.toString(centerLng) + ";\n\n");                
                  
                  String header = "var windpoint = new Array(\n";
                  String trailer = ");";
                  bw.write(header);
                  boolean first = true;
                  
                  // Do the job here.
                  for (int h=0; gribData.getGribPointData() != null && h<gribData.getGribPointData().length; h++)
                  {
                    for (int w=0; w<gribData.getGribPointData()[h].length; w++)
                    {
                      float x = gribData.getGribPointData()[h][w].getU();
                      float y = -gribData.getGribPointData()[h][w].getV();
                      double lat = gribData.getGribPointData()[h][w].getLat();
                      double lng = gribData.getGribPointData()[h][w].getLng();
    
                      double speed = Math.sqrt(x * x + y * y);
                      speed *= 3.60D;
                      speed /= 1.852D;
                      double dir = WWGnlUtilities.getDir(x, y);
                       
                      // Write the data here
                      if (!first)
                        bw.write(",\n");
                      else                    
                        first = false;
                      String str = 
                      "{lat:" + Double.toString(lat) + ",\n" +
                      " lng:" + Double.toString(lng) + ",\n" +
                      " speed:" + WWGnlUtilities.XXX12.format(speed) + ",\n" +
                      " dir:" + WWGnlUtilities.XXX12.format(resetDir(dir)) + " }\n";                    
                      bw.write(str);
                    }
                  }
                  bw.write(trailer);
                  bw.close();
                  // Show.
                  try
                  {
                    Utilities.openInBrowser("googlewind.html"); 
                  } 
                  catch (Exception exception) 
                  {
                    WWContext.getInstance().fireExceptionLogging(exception);
                    exception.printStackTrace(); 
                  }                
                }
                catch (IOException e)
                {
                  WWContext.getInstance().fireExceptionLogging(e);
                  e.printStackTrace();
                }
              }
            }       
          }
        }

        public void compositeFileOpen(final String fileName)
        {
//          System.out.println("compositeFileOpen: parent is " + (parent==null?"":"not ") + "null");
//          if (parent != null)
//            System.out.println("compositeFileOpen: parent is " + (parent.isVisible()?"":"not ") + "visible");
          if (parent != null && parent.isVisible())
          {
            // Progress bar
            Runnable heavyRunnable = new Runnable()
            {              
              public void run()
              {
                WWContext.getInstance().setMonitor(ProgressUtil.createModalProgressMonitor(WWContext.getInstance().getMasterTopFrame(), 1, true, true));
                ProgressMonitor pm = WWContext.getInstance().getMonitor();
                synchronized (pm)
                {
                  pm.start(WWGnlUtilities.buildMessage("restoring-wait")); 
                }
                WWContext.getInstance ().setAel4monitor(new ApplicationEventListener()
                  {
                    public String toString()
                    {
                      return "{" + Long.toString(id) + "} from Runnable in CommandPanel (3).";
                    }
                    public void progressing(String mess)
                    {
                      try
                      {
                        ProgressMonitor pm = WWContext.getInstance().getMonitor();
                        synchronized (pm)
                        {
                          pm.setCurrent(mess, WWContext.getInstance().getMonitor().getCurrent());
                        }
                      }
                      catch (Exception ex)
                      {
                        System.out.println(" ... progessing:" + ex.toString());
                        ex.printStackTrace();
                      }
                    }
                    public void interruptProgress()
                    {
                      System.out.println("Interruption requested (3)...");
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
                    }
                  });
                WWContext.getInstance().addApplicationListener(WWContext.getInstance().getAel4monitor());
                WWContext.getInstance().fireSetLoading(true);
                try
                {
                  restoreComposite(fileName); 
                }
                catch (ConcurrentModificationException cme)
                {
                  System.err.println("...ConcurrentModificationException");    
                  System.err.println("===================");
                  cme.printStackTrace();
                  System.err.println("===================");
                  WWContext.getInstance().fireInterruptProcess();
                }
                catch (RuntimeException rte)
                {
                  String mess = rte.getMessage();
                  if (mess.startsWith("DataArray (width) size mismatch"))
                    if (WWContext.getDebugLevel() >= 1) System.out.println(mess);
                  else
                    throw rte;
                }
                finally
                {
//                ProgressMonitor pm = WWContext.getInstance().getMonitor();
                  synchronized (pm)
                  {
                    try
                    {
                      if (pm.getCurrent() != pm.getTotal())
                        pm.setCurrent(null, pm.getTotal());
                      WWContext.getInstance().removeApplicationListener(WWContext.getInstance().getAel4monitor());
                      WWContext.getInstance().setAel4monitor(null);
                      WWContext.getInstance().setMonitor(null);
                    }
                    catch (Exception ex)
                    {
                      ex.printStackTrace();
                    }
                  }
                }
                WWContext.getInstance().fireSetLoading(false);
              }
            };
            new Thread(heavyRunnable, "composite-restorer").start();
  
            // Apply values
            if (chartPanel.getProjection() != ChartPanel.GLOBE_VIEW &&
                chartPanel.getProjection() != ChartPanel.SATELLITE_VIEW)
              chartPanel.setWidthFromChart(nLat, sLat, wLong, eLong);
            chartPanel.setEastG(eLong);
            chartPanel.setWestG(wLong);
            chartPanel.setNorthL(nLat);
            chartPanel.setSouthL(sLat);
            chartPanel.repaint();    
            displayStatus();    
          }
        }
        
        public void patternFileOpen(String str) 
        {
          if (parent != null && parent.isVisible())
            loadWithPattern(str);
        }
        
        public void predfinedFaxOpen(String str) 
        {
          if (parent != null && parent.isVisible())
          {
            DynFaxUtil.PreDefFax pdf = DynFaxUtil.findPredefFax(str);
//          System.out.println(pdf);
            try
            {
              File f = File.createTempFile("predef-fax_", ".png");
              String fileName = f.getAbsolutePath(); //f.getName();  //  
              String dir = f.getParent();
//		        System.out.println("Generating " + fileName + " in " + dir);
              Image img = null;
              if (pdf.getOrigin().toLowerCase().startsWith("http://"))
                img = HTTPClient.getChart(pdf.getOrigin(), dir, fileName, true);
              else if (pdf.getOrigin().startsWith(SearchUtil.SEARCH_PROTOCOL)) // (local search, for SailMail)               
              {
                // Parse Expression, like search:chartview.util.SearchUtil.findMostRecentFax(pattern, rootPath)
                String faxName = SearchUtil.dynamicSearch(pdf.getOrigin());
//              System.out.println("For [" + pdf.getOrigin() + "], search: found [" + faxName + "]");
                if (faxName != null)
                {
                  img = ImageUtil.readImage(faxName);
                }
              }
              else                  
                JOptionPane.showMessageDialog(instance, "Protocol for " + pdf.getOrigin() + " not there yet...");
              if (pdf.isTransparent() && img != null)
              {
                if (pdf.changeColor())
                  img = ImageUtil.switchColorAndMakeColorTransparent(img, Color.black, pdf.getColor(), Color.white, blurSharpOption);
                else
                  img = ImageUtil.makeColorTransparent(img, Color.white, blurSharpOption);                  
              }
//			        System.out.println("Done.");
              WWContext.getInstance().fireAddFaxImage(DynFaxUtil.getFaxImage(pdf, img, f.getAbsolutePath(), chartPanel));
            }
            catch (Exception ex)
            {
              ex.printStackTrace();
            }
          }
        }

        public void chartRepaintRequested()
        {
          if (parent != null && parent.isVisible())
          {
            buildPlaces();
            chartPanel.repaint();
          }
        }
        
        public void setNMEAAcquisition(boolean b)
        {
         if (parent != null && parent.isVisible())
          {
            if (b) // Start
            {
              nmeaPollingInterval = ((Integer) ParamPanel.data[ParamData.NMEA_POLLING_FREQ][ParamData.VALUE_INDEX]).intValue();
              final boolean KEEP_LOOPING = false;
              nmeaThread = new Thread("nmea-data-thread")
              {
                public void run()
                {
                  synchronized(this)
                  {
                    WWContext.getInstance().fireLogging("Starting NMEA Thread\n");
                    while (goNmea)
                    {
                      WWGnlUtilities.BoatPosition bp = null;
                      // Read here
                      try
                      {
                        try
                        {
                          bp = WWGnlUtilities.getHTTPBoatPosition(); // Try HTTP port
                        }
                        catch (HTTPClient.NMEAServerException nse)
                        {
                          System.out.println("NMEA HTTP Server must be down...");   
                          try
                          {
                            bp = WWGnlUtilities.getSerialBoatPosition(); // Try Serial port
                          }
                          catch (Exception serialEx)
                          {
                            System.err.println("Cannot read Serial port either.\nStopping Serial Port reading, trying TCP");
                            try
                            {
                              bp = WWGnlUtilities.getTCPBoatPosition(); // Try TCP
                            }
                            catch (Exception tcpEx)
                            {
                              System.err.println("Cannot read TCP port either.\nStopping TCP Port reading, trying UDP");
                              try
                              {
                                bp = WWGnlUtilities.getUDPBoatPosition(); // Try UDP
                              }
                              catch (Exception udpEx)
                              {
                                String mess = "Cannot read UDP port either.\nStopping UDP Port reading, switch to manual entry";
                                System.err.println(mess);
//                              mess = "HTTP, Serial, TCP, UDP ports are not responding.\nSwitching to manual entry.";
//                              JOptionPane.showMessageDialog(null, mess, "Position Acquisition", JOptionPane.WARNING_MESSAGE);
                                WWGnlUtilities.getManualBoatPosition();
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
                      catch (Exception e)
                      {
                        WWContext.getInstance().fireExceptionLogging(e);
                        e.printStackTrace();
                      }
                      if (bp != null)
                      {
                        boatPosition = bp.getPos();
                        boatHeading = bp.getHeading();
                        chartPanel.repaint();
                        WWGnlUtilities.storeBoatPosAndHeading(boatPosition.getL(), 
                                                              boatPosition.getG(), 
                                                              boatHeading, 
                                                              new File("." + File.separator + "config" + File.separator + WWContext.MANUAL_POSITION_FILE));                        
                      }
                      else
                      {
                        // TODO If everything has failed, switch to Manual input
                        System.out.println("Manual Position Input");
                      }
                      
                      if (KEEP_LOOPING) // Wait and re-read
                      {
                        try { this.wait((long)nmeaPollingInterval * 1000L); }
                        catch (Exception ignore)
                        {
                          WWContext.getInstance().fireLogging("oops\n"); 
                          System.out.println(ignore.getMessage());
                        }
                      }
                      else
                        goNmea = false; // then exit, instead of re-looping

                    }
                    WWContext.getInstance().fireLogging("End of NMEA Thread\n");
                  }
                }
              };
              goNmea = true;
              nmeaThread.start();
            }
            else
            {
              goNmea = false;
              boatPosition = null;
              chartPanel.repaint();
              synchronized (nmeaThread)
              {
                nmeaThread.notify();
              }
            }
          }
        }
        
        public void activeFaxChanged(FaxType ft) 
        {
          if (parent != null && parent.isVisible())
          {
            for (int i=0; faxImage!=null && i<faxImage.length; i++)
            {
              if (faxImage[i] == null)
              {
                System.out.println("faxImage[" + i + "] is null");
                continue;
              }
              if (faxImage[i].comment.equals(ft.toString()))
              {
                currentFaxIndex = i;
                break;
              }
            }
          }
        }
        
        public void allFaxesSelected() 
        {
          if (parent != null && parent.isVisible())
          {
            System.out.println("All faxes selected");
            currentFaxIndex = -1;
          }
        }
        
        public void plotBoatAt(GeoPoint gp, int hdg) 
        {
          if (parent != null && parent.isVisible())
          {
            routingPoint   = gp;
            routingHeading = hdg;
            chartPanel.repaint();
          }
        }
        
        public void highlightWayPoint(GeoPoint gp) 
        {
          wp2highlight = gp;
          chartPanel.repaint();
        }
        
        public void manuallyEnterBoatPosition(GeoPoint gp, int hdg) 
        {
          if (parent != null && parent.isVisible())
          {
            boatPosition = gp;
            boatHeading = hdg;
            chartPanel.repaint(); 
          }
        }
        
        public void setGribSmoothing(int i) 
        {
         if (parent != null && parent.isVisible())
          {
            if (smooth != i)
            {
              smoothingRequired = true;
              smooth = i;
              if (wgd != null)
              {
                if (gribIndex == -1) gribIndex = 0;
                gribData = wgd[gribIndex]; 
                if (smooth > 1 && gribData != null && smoothingRequired)
                {
                  gribData = GribHelper.smoothGribData(gribData, smooth); // Rebuilds all the GRIB Data
                  chartPanel.repaint();
                  smoothingRequired = false;
                  // Re-generate 3D?
                  generateAll3DData();
                }              
              }
            }
          }
        }

        public void setGribTimeSmoothing(int i) 
        {
         if (parent != null && parent.isVisible())
          {
            if (i != timeSmooth)
            {
              timeSmooth = i;
              gribIndex = 0;
              if (i == 1)
              {
                if (originalWgd != null)
                {
                  wgd = originalWgd;
                  originalWgd = null;
                  smoothingRequired = true;
                  updateGRIBDisplay();
                }
              }
              else
              {
                if (originalWgd == null)
                  originalWgd = wgd;
                
                wgd = GribHelper.smoothGRIBinTime(originalWgd, i);
                smoothingRequired = true;
                updateGRIBDisplay();
              }
            }
          }
        }
       
        public void gribLoaded()
        {
         if (parent != null && parent.isVisible())
          {
            gribData = null; 
            if (wgd != null)
            {
              originalWgd = null;
              gribIndex = 0;
  //          if (gribIndex == -1) gribIndex = 0;
              smoothingRequired = true;
              gribData = wgd[gribIndex]; 
              if (smooth > 1 && gribData != null && smoothingRequired)
              {
                gribData = GribHelper.smoothGribData(gribData, smooth); // Rebuilds all the GRIB Data
                smoothingRequired = false;
              }
  //            int m = 10000;
  //            
  //            int x = gribData.getGribPointData().length;
  //            int y = gribData.getGribPointData()[0].length;
  //            
  //            m = x * y * 10;
  //            
  //            System.out.println("X:" + x + ", Y:" + y);
  //            System.out.println("Chart W:" + chartPanel.getWidth() + ", chart H:" + chartPanel.getHeight());
              
              if (gribData != null)
              {
                if (displayContourLines)
                {
                  WWContext.getInstance().fireProgressing(WWGnlUtilities.buildMessage("computing-contour-lines"));
                  if (isoPointsThread != null && isoPointsThread.isAlive())
                    isoPointsThread.interrupt();
                  isoPointsThread = new IsoPointsThread(gribData);
                  isoPointsThread.start();
                }
                applyBoundariesChanges();
                displayComboBox.setEnabled(true);
                boundaries[WIND_SPEED] = GRIBDataUtil.getWindSpeedBoundaries(gribData); // TODO Look into this
                
                String u = units[WIND_SPEED];
                boundariesLabel.setText(WWGnlUtilities.buildMessage("from-to", new String[] { WWGnlUtilities.XX22.format(boundaries[0][0]) + u, WWGnlUtilities.XX22.format(boundaries[0][1]) + u }));            
                boundariesLabel.setEnabled(true);
                
                if (WWGnlUtilities.isIn(dataLabels[WIND_SPEED], displayComboBox))
                  boundaries[WIND_SPEED] = GRIBDataUtil.getWindSpeedBoundaries(gribData);
                if (WWGnlUtilities.isIn(dataLabels[PRMSL], displayComboBox))
                  boundaries[PRMSL] = GRIBDataUtil.getPRMSLBoundaries(gribData);
                if (WWGnlUtilities.isIn(dataLabels[HGT500], displayComboBox))
                  boundaries[HGT500] = GRIBDataUtil.get500MbBoundaries(gribData);
                if (WWGnlUtilities.isIn(dataLabels[TEMPERATURE], displayComboBox))
                  boundaries[TEMPERATURE] = GRIBDataUtil.getAirTempBoundaries(gribData);
                if (WWGnlUtilities.isIn(dataLabels[WAVES], displayComboBox))
                  boundaries[WAVES] = GRIBDataUtil.getWaveHgtBoundaries(gribData);
                if (WWGnlUtilities.isIn(dataLabels[RAIN], displayComboBox))
                  boundaries[RAIN] = GRIBDataUtil.getRainBoundaries(gribData);
                if (WWGnlUtilities.isIn(dataLabels[CURRENT_SPEED], displayComboBox))
                  boundaries[CURRENT_SPEED] = GRIBDataUtil.getCurrentSpeedBoundaries(gribData);
  
                boolean displaySomething = display3DTws || display3DPrmsl || display3D500mb || display3DWaves || display3DTemperature || display3DRain;
                if (displaySomething)
                {
                  WWContext.getInstance().fireProgressing(WWGnlUtilities.buildMessage("computing-3D-data"));
                  WWContext.getInstance().fireSetLoading(true, WWGnlUtilities.buildMessage("computing-3D-data"));
                  generateAll3DData();
                  WWContext.getInstance().fireSetLoading(false, WWGnlUtilities.buildMessage("computing-3D-data"));
                }
        //      System.out.println(gribData.getDate().toString());
                Date gribDate = gribData.getDate(); // TimeUtil.getGMT(gribData.getDate());
                Calendar cal = new GregorianCalendar();
                cal.setTime(gribDate);
                
                int dow     = cal.get(Calendar.DAY_OF_WEEK);
                int year    = cal.get(Calendar.YEAR);
                int month   = cal.get(Calendar.MONTH);
                int day     = cal.get(Calendar.DAY_OF_MONTH);
                int hours   = cal.get(Calendar.HOUR_OF_DAY);
                int minutes = cal.get(Calendar.MINUTE);
                int seconds = cal.get(Calendar.SECOND);
                                            
                String gribInfo2 = Integer.toString(year) + "-" + WWGnlUtilities.MONTH[month] + "-" + WWGnlUtilities.DF2.format(day) + " " + WWGnlUtilities.WEEK[dow - 1] + " " + WWGnlUtilities.DF2.format(hours) + ":" + WWGnlUtilities.DF2.format(minutes) + ":" + WWGnlUtilities.DF2.format(seconds) + " GMT";
                String gribInfo3 = GeomUtil.decToSex(gribData.getNLat(), GeomUtil.SWING, GeomUtil.NS) + " " + 
                                   GeomUtil.decToSex(gribData.getWLng(), GeomUtil.SWING, GeomUtil.EW);
                String gribInfo4 = GeomUtil.decToSex(gribData.getSLat(), GeomUtil.SWING, GeomUtil.NS) + " " + 
                                   GeomUtil.decToSex(gribData.getELng(), GeomUtil.SWING, GeomUtil.EW);
  
                // Proceed for variation with gribFile
                GribFile gf = WWContext.getInstance().getGribFile();
                setThereIsWind(GRIBDataUtil.thereIsVariation(gf, GRIBDataUtil.TYPE_TWS));
//              setThereIsPrmsl(GRIBDataUtil.thereIsVariation(gribData, GRIBDataUtil.TYPE_PRMSL));
                setThereIsPrmsl(GRIBDataUtil.thereIsVariation(gf, GRIBDataUtil.TYPE_PRMSL));
//              setThereIs500mb(GRIBDataUtil.thereIsVariation(gribData, GRIBDataUtil.TYPE_500MB));
                setThereIs500mb(GRIBDataUtil.thereIsVariation(gf, GRIBDataUtil.TYPE_500MB));
//              setThereIsTemperature(GRIBDataUtil.thereIsVariation(gribData, GRIBDataUtil.TYPE_TMP));
                setThereIsTemperature(GRIBDataUtil.thereIsVariation(gf, GRIBDataUtil.TYPE_TMP));
//              setThereIsWaves(GRIBDataUtil.thereIsVariation(gribData, GRIBDataUtil.TYPE_WAVE));
                setThereIsWaves(GRIBDataUtil.thereIsVariation(gf, GRIBDataUtil.TYPE_WAVE));
//              setThereIsRain(GRIBDataUtil.thereIsVariation(gribData, GRIBDataUtil.TYPE_RAIN));
                setThereIsRain(GRIBDataUtil.thereIsVariation(gf, GRIBDataUtil.TYPE_RAIN));
                setThereIsCurrent(GRIBDataUtil.thereIsVariation(gf, GRIBDataUtil.TYPE_CURRENT));
                WWContext.getInstance().fireGribInfo(gribIndex, 
                                                     wgd.length, 
                                                     gribInfo2, 
                                                     gribInfo3, 
                                                     gribInfo4,
                                                     windOnly,
                                                     isThereWind(), // gribData.wind,
                                                     isTherePrmsl(),
                                                     isThere500mb(),
                                                     isThereTemperature(),
                                                     isThereWaves(),
                                                     isThereRain(),
                                                     isThereCurrent(),
                                                     gribData.wind,
                                                     windOnly?false:gribData.prmsl,
                                                     windOnly?false:gribData.hgt,
                                                     windOnly?false:gribData.temp,
                                                     windOnly?false:gribData.wave,
                                                     windOnly?false:gribData.rain);
              }
            }
            setCheckBoxes();
          }
        }
        
        public void gribUnloaded()
        {
         if (parent != null && parent.isVisible())
          {
            gribIndex = 0;
  
            displayComboBox.setEnabled(false);
            boundariesLabel.setText("--- ---");
            boundariesLabel.setEnabled(false);
            WWContext.getInstance().fireNoTWSObj();
            WWContext.getInstance().fireNo500mbObj();
            WWContext.getInstance().fireNoPrmslObj();
            WWContext.getInstance().fireNoTmpObj();
            WWContext.getInstance().fireNoWaveObj();
            WWContext.getInstance().fireNoRainObj();
            WWContext.getInstance().fireGribInfo(0, 0, "", "", "", false, false, false, false, false, false, false, false, false, false, false, false, false, false);
            
            setCheckBoxes();
            fromGRIBSlice = null;
            toGRIBSlice   = null;
            WWContext.getInstance().setGribFile(null);
          }
        }
        
        public void setGribInfo(int currentIndex, 
                                int maxIndex, 
                                String s2, 
                                String s3, 
                                String s4,
                                boolean windOnly,
                                boolean thereIsWind, 
                                boolean thereIsPrmsl, 
                                boolean thereIsHgt500, 
                                boolean thereIsTemp, 
                                boolean thereIsWaves,
                                boolean thereIsRain,
                                boolean thereIsCurrent,
                                boolean displayWind,
                                boolean display3DPrmsl,
                                boolean display3D500hgt,
                                boolean display3DTemp,
                                boolean display3Dwaves,
                                boolean display3DRain) 
        {
         if (parent != null && parent.isVisible())
         {
            settingGRIBInfo = true;
            
            synchronized (displayComboBox)
            {
              // To keep the possible already selected option
              String currentSelection = (String)displayComboBox.getSelectedItem();
              
              displayComboBox.removeAllItems();
              displayComboBox.setEnabled(false);
  //          if (thereIs3D)
              displayComboBox.addItem("- None -"); // LOCALIZE
              if (thereIsWind)                       displayComboBox.addItem(dataLabels[WIND_SPEED]); 
              if (thereIsPrmsl && displayPrmsl)      displayComboBox.addItem(dataLabels[PRMSL]);
              if (thereIsHgt500 && display500mb)     displayComboBox.addItem(dataLabels[HGT500]);
              if (thereIsTemp && displayTemperature) displayComboBox.addItem(dataLabels[TEMPERATURE]);
              if (thereIsWaves && displayWaves)      displayComboBox.addItem(dataLabels[WAVES]);
              if (thereIsRain && displayRain)        displayComboBox.addItem(dataLabels[RAIN]);
                            
              boolean displayCurrent = true; // TODO Parameters
              
              if (thereIsCurrent && displayCurrent) displayComboBox.addItem(dataLabels[CURRENT_SPEED]); 
              settingGRIBInfo = false;
              
              displayComboBox.setEnabled((thereIsPrmsl && displayPrmsl) || 
                                         (thereIsHgt500 && display500mb) || 
                                         (thereIsTemp && displayTemperature) || 
                                         (thereIsWaves && displayWaves) || 
                                         (thereIsRain && displayRain) ||
                                         (thereIsCurrent && displayCurrent));
              
              // Reset current selection if available
              if (currentSelection != null && currentSelection.trim().length() > 0)
              {
                boolean contains = false;
                for (int i=0; i<displayComboBox.getItemCount(); i++)
                {
                  if (currentSelection.equals(displayComboBox.getItemAt(i)))
                  {
                    contains = true;
                    break;
                  }
                }
                if (contains)
                  displayComboBox.setSelectedItem(currentSelection);
                else
                {
                  if (displayComboBox.isEnabled() && displayComboBox.getItemCount() >= 1)
                    displayComboBox.setSelectedIndex(1);
                }
              }
              
              setThereIsPrmsl(thereIsPrmsl);
              setThereIs500mb(thereIsHgt500);
              setThereIsTemperature(displayTemperature);
              setThereIsWaves(thereIsWaves);
              setThereIsRain(thereIsRain);
            }
          }
        }

        public void syncGribWithDate(Date date)
        {
         if (parent != null && parent.isVisible())
          {
            System.out.println("Synchronizing with GRIB data (" + date.toString() + ")");
            // Find the GRIB frame for the date
            long datetime = date.getTime();
            long smallest = Long.MAX_VALUE;
            int closestGribIndex = 0;
            for (int i=0; i<wgd.length; i++)
            {
              long gribtime = wgd[i].getDate().getTime();
              long diff = Math.abs(datetime - gribtime);
              if (diff < smallest)
              {
                smallest = diff;
                closestGribIndex = i;
              }
              else
                break;
            }
  //        System.out.println("Closest date is " + wgd[closestGribIndex].getDate());
            gribIndex = closestGribIndex;
            smoothingRequired = true;
            updateGRIBDisplay();
          }
        }
        
        public void chartLineColorChanged(Color c) 
        {
          chartPanel.setChartColor(c);
          chartPanel.repaint();
        }
        public void chartLineThicknessChanged(int i) 
        {
          chartPanel.repaint();
        }
        public void chartBackgroundColorChanged(Color c) 
        {
          chartPanel.setChartBackGround(c);
          chartPanel.repaint();
        }
        public void ddzColorChanged(Color c)
        {
          chartPanel.setDdRectColor(c);
          chartPanel.repaint();
        }
        public void gridColorChanged(Color c) 
        {
          chartPanel.setGridColor(c);
          chartPanel.repaint();
        }

        public void GRIBSliceInfoRequested(double d) 
        {
         if (parent != null && parent.isVisible())
          {
            gribSliceInfo = d;
            chartPanel.repaint();
          }
        }

        public void GRIBSliceInfoRequestStop() 
        {
         if (parent != null && parent.isVisible())
          {
            gribSliceInfo = -1D;
            chartPanel.repaint();
          }
        }
      };
    
    WWContext.getInstance().addApplicationListener(ael);
    setLayout(borderLayout1);
    
    this.setBackground(new Color(177, 220, 216));
    if (chartPanel.getProjection() != ChartPanel.GLOBE_VIEW &&
        chartPanel.getProjection() != ChartPanel.SATELLITE_VIEW)
      chartPanel.setWidthFromChart(nLat, sLat, wLong, eLong);
//  eLong = chartPanel.calculateEastG(nLat, sLat, wLong);
    chartPanel.setEastG(eLong);
    chartPanel.setWestG(wLong);
    chartPanel.setNorthL(nLat);
    chartPanel.setSouthL(sLat);
    
    chartPanel.setChartColor((Color) ParamPanel.data[ParamData.CHART_COLOR][ParamData.VALUE_INDEX]);
    chartPanel.setGridColor((Color) ParamPanel.data[ParamData.GRID_COLOR][ParamData.VALUE_INDEX]);
    chartPanel.setChartBackGround((Color) ParamPanel.data[ParamData.CHART_BG_COLOR][ParamData.VALUE_INDEX]);
    chartPanel.setHorizontalGridInterval(10D);
    chartPanel.setVerticalGridInterval(10D);
    chartPanel.setWithScale(false);
    chartPanel.setMouseDraggedEnabled(true);
    chartPanel.setMouseDraggedType(ChartPanel.MOUSE_DRAG_ZOOM);
    chartPanel.setPositionToolTipEnabled(true);
    chartPanel.setConfirmDDZoom(((Boolean) ParamPanel.data[ParamData.CONFIRM_DD_ZOOM][ParamData.VALUE_INDEX]).booleanValue());
    chartPanel.setDdRectColor((Color) ParamPanel.data[ParamData.DD_ZOOM_COLOR][ParamData.VALUE_INDEX]);
    chartPanel.setMouseEdgeProximityDetectionEnabled(((Boolean) ParamPanel.data[ParamData.CLICK_SCROLL][ParamData.VALUE_INDEX]).booleanValue());
    
    chartPanel.setOpaque(false);
    chartPanel.setCleanFirst(true);
    
    add(jSplitPane, BorderLayout.CENTER);
    
    chartPanelScrollPane.getViewport().setBackground(CUSTOM_LIGHT_BLUE);
    bottomLeftPanel.add(displayLabel, null);
    displayComboBox.removeAllItems();
    displayComboBox.addItem("WIND");
    displayComboBox.addItem("PRMSL");
    displayComboBox.addItem("500HGT");
    displayComboBox.addItem("AIRTMP");
    displayComboBox.addItem("WAVES");
    displayComboBox.addItem("RAIN");
    displayComboBox.addItem("CURRENT");
    
    displayComboBox.setEnabled(false);
    displayComboBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          if (displayComboBox.isEnabled() && !settingGRIBInfo)
          {
//          StaticObjects.getInstance().fireLogging("Selected Data: " + (String)displayComboBox.getSelectedItem());
            
            double[] bndr = boundaries[getDataIndexOf((String)displayComboBox.getSelectedItem())];
            String u = units[getDataIndexOf((String)displayComboBox.getSelectedItem())];
            if ("AIRTMP".equals((String)displayComboBox.getSelectedItem()))
              boundariesLabel.setText(WWGnlUtilities.buildMessage("from-to", new String[] { WWGnlUtilities.XX22.format(WWGnlUtilities.convertTemperatureFromCelcius(bndr[0], temperatureUnit)) + u, 
                                                                                            WWGnlUtilities.XX22.format(WWGnlUtilities.convertTemperatureFromCelcius(bndr[1], temperatureUnit)) + u }));            
            else
              boundariesLabel.setText(WWGnlUtilities.buildMessage("from-to", new String[] { WWGnlUtilities.XX22.format(bndr[0]) + u, WWGnlUtilities.XX22.format(bndr[1]) + u }));            
            chartPanel.repaint();
          }
        }
      });
    
    bottomLeftPanel.add(displayComboBox, null);
    displayComboBox.setToolTipText(WWGnlUtilities.buildMessage("grib-combo-hint"));
    boundariesLabel.setText("--- ---");
    boundariesLabel.setEnabled(false);
    bottomLeftPanel.add(boundariesLabel, null);
    bottomRightPanel.add(statusLabel, null);
    add(bottomPanel, BorderLayout.SOUTH);
    add(rightVerticalPanel, BorderLayout.EAST);
    
    dataPanel.setLatLongInc(lgInc);
    dataPanel.setFaxInc(faxInc);
    double z = ((Double) ParamPanel.data[ParamData.DEFAULT_ZOOM_VALUE][ParamData.VALUE_INDEX]).doubleValue();  
    chartPanel.setZoomFactor(z);
    dataPanel.setZoomFactor(chartPanel.getZoomFactor());
    chartPanelScrollPane.getViewport().add(chartPanel, null);
    
    displayStatus();
    buildPlaces();
    buildSailMailStations();
    buildWeatherStations();
    
    setCheckBoxes();
  }

  public void removeListener()
  {
    WWContext.getInstance().removeApplicationListener(ael);  
  }
  
  private void runStorageThread()
  {
    runStorageThread(false, null);
  }
  
  private void runStorageThread(boolean update, String compositeName)
  {
    WWContext.getInstance().fireSetLoading(true, "Please wait, storing..."); // LOCALIZE
    XMLDocument storage = new XMLDocument();
    
    XMLElement root = (XMLElement)storage.createElement("storage");
    storage.appendChild(root);
    
    if (currentComment.length() > 0)
    {
      XMLElement compositeComment = (XMLElement)storage.createElement("composite-comment");
      root.appendChild(compositeComment);
      CDATASection cdata = storage.createCDATASection(currentComment);
      compositeComment.appendChild(cdata);
    }
    
    XMLElement faxcollection = (XMLElement)storage.createElement("fax-collection");
    root.appendChild(faxcollection);
    
    for (int i=0; faxImage!=null && i<faxImage.length; i++)
    {
      XMLElement fax = (XMLElement)storage.createElement("fax");
      faxcollection.appendChild(fax);
      if (update)
        fax.setAttribute("file", faxImage[i].fileName);
      else
        fax.setAttribute("file", RelativePath.getRelativePath(System.getProperty("user.dir").replace(File.separatorChar, '/'), faxImage[i].fileName).replace(File.separatorChar, '/'));
      fax.setAttribute("color", WWGnlUtilities.colorToString(faxImage[i].color));
      if (whRatio != 1D)
        fax.setAttribute("wh-ratio", Double.toString(whRatio));
      fax.setAttribute("transparent", Boolean.toString(faxImage[i].transparent));
      fax.setAttribute("color-change", Boolean.toString(faxImage[i].colorChange));

      XMLElement faxScale = (XMLElement)storage.createElement("faxScale");
      fax.appendChild(faxScale);
      Text faxScaleText = storage.createTextNode("#text");
      faxScaleText.setNodeValue(Double.toString(faxImage[i].imageScale));
      faxScale.appendChild(faxScaleText);
      
      XMLElement faxXoffset = (XMLElement)storage.createElement("faxXoffset");
      fax.appendChild(faxXoffset);
      Text faxXoffsetText = storage.createTextNode("#text");
      faxXoffsetText.setNodeValue(Integer.toString(faxImage[i].imageHOffset));
      faxXoffset.appendChild(faxXoffsetText);
      
      XMLElement faxYoffset = (XMLElement)storage.createElement("faxYoffset");
      fax.appendChild(faxYoffset);
      Text faxYoffsetText = storage.createTextNode("#text");
      faxYoffsetText.setNodeValue(Integer.toString(faxImage[i].imageVOffset));
      faxYoffset.appendChild(faxYoffsetText);
                  
      XMLElement faxRotation = (XMLElement)storage.createElement("faxRotation");
      fax.appendChild(faxRotation);
      Text faxRotationText = storage.createTextNode("#text");
      faxRotationText.setNodeValue(Double.toString(faxImage[i].imageRotationAngle));
      faxRotation.appendChild(faxRotationText);
                  
      XMLElement faxOrigin = (XMLElement)storage.createElement("faxOrigin");
      fax.appendChild(faxOrigin);
      Text faxOriginText = storage.createTextNode("#text");
      faxOriginText.setNodeValue(faxImage[i].faxOrigin);
      faxOrigin.appendChild(faxOriginText);
                  
      XMLElement faxTitle = (XMLElement)storage.createElement("faxTitle");
      fax.appendChild(faxTitle);
      Text faxTitleText = storage.createTextNode("#text");
      faxTitleText.setNodeValue(faxImage[i].faxTitle);
      faxTitle.appendChild(faxTitleText);
    }
    XMLElement grib = (XMLElement)storage.createElement("grib");

    // Obsolete - 13-Oct-2009
//  grib.setAttribute("wind-only", Boolean.toString(windOnly));
//  grib.setAttribute("with-contour", Boolean.toString(displayContourLines));
    
    grib.setAttribute("display-TWS-Data",    Boolean.toString(displayTws));
    grib.setAttribute("display-PRMSL-Data",  Boolean.toString(displayPrmsl));
    grib.setAttribute("display-500HGT-Data", Boolean.toString(display500mb));
    grib.setAttribute("display-WAVES-Data",  Boolean.toString(displayWaves));
    grib.setAttribute("display-TEMP-Data",   Boolean.toString(displayTemperature));
    grib.setAttribute("display-PRATE-Data",  Boolean.toString(displayRain));
    
    grib.setAttribute("display-TWS-contour",    Boolean.toString(displayContourTWS));
    grib.setAttribute("display-PRMSL-contour",  Boolean.toString(displayContourPRMSL));
    grib.setAttribute("display-500HGT-contour", Boolean.toString(displayContour500mb));
    grib.setAttribute("display-WAVES-contour",  Boolean.toString(displayContourWaves));
    grib.setAttribute("display-TEMP-contour",   Boolean.toString(displayContourTemp));
    grib.setAttribute("display-PRATE-contour",  Boolean.toString(displayContourPrate));

    grib.setAttribute("display-TWS-3D",    Boolean.toString(display3DTws));
    grib.setAttribute("display-PRMSL-3D",  Boolean.toString(display3DPrmsl));
    grib.setAttribute("display-500HGT-3D", Boolean.toString(display3D500mb));
    grib.setAttribute("display-WAVES-3D",  Boolean.toString(display3DWaves));
    grib.setAttribute("display-TEMP-3D",   Boolean.toString(display3DTemperature));
    grib.setAttribute("display-PRATE-3D",  Boolean.toString(display3DRain));

//  System.out.println("GRIB Request :" + gribRequest);
//  System.out.println("GRIB File Name :" + gribFileName);  
    grib.setAttribute("grib-request", gribRequest);
    root.appendChild(grib);
    
//  System.out.println("GRIB File Name:" + gribFileName);
    boolean inLineGRIBContent = false;
    if (!update)
    {
      File gf = new File(gribFileName);
      if (!gf.exists())
        inLineGRIBContent = true;
    }
    if (update || !inLineGRIBContent)  
    {
      Text gribText = storage.createTextNode("#text");
      gribText.setNodeValue(gribFileName.replace(File.separatorChar, '/'));
      grib.appendChild(gribText);
    }
    else
    {
      grib.setAttribute("in-line", "true");
      grib.setAttribute("in-line-request", gribFileName);
      if (wgd != null)           
      {
        try
        {
          boolean inLineOption = false;
          if (inLineOption)
          {
           // Might generate OutOfMemoryError
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLEncoder encoder = new XMLEncoder(baos);
            // TODO Write down the GribFile
//            Object o = WWContext.getInstance().getGribFile();
//            if (o == null)
//              o = wgd;
            encoder.writeObject(wgd);
            encoder.close();
            String gribContent = baos.toString();
            StringReader sr = new StringReader(gribContent);              
            DOMParser parser = WWContext.getInstance().getParser();
            XMLDocument doc = null;
            synchronized (parser)
            {
              parser.setValidationMode(DOMParser.NONVALIDATING);
              parser.parse(sr);
              doc = parser.getDocument();
            }
            Node docRoot = doc.getDocumentElement();
            Node newNode = storage.adoptNode(docRoot);
            grib.appendChild(newNode);
          }
          else // File approach
          {
            String gribDir = ParamPanel.data[ParamData.GRIB_FILES_LOC][ParamData.VALUE_INDEX].toString().split(File.pathSeparator)[0] + File.separator + "inline-requests";
            String gribFileName = WWGnlUtilities.SDF.format(new Date()) + ".in-line-grb";
            File dir = new File(gribDir);
            if (!dir.exists())
              dir.mkdirs();
            FileOutputStream fos = new FileOutputStream(new File(gribDir, gribFileName));
            XMLEncoder encoder = new XMLEncoder(fos);
            // TODO Write down the GribFile
//            Object o = WWContext.getInstance().getGribFile();
//            if (o == null)
//              o = wgd;
            encoder.writeObject(wgd);
            encoder.close();
            Text gribText = storage.createTextNode("#text");
            gribText.setNodeValue((gribDir + File.separator + gribFileName).replace(File.separatorChar, '/'));
            grib.appendChild(gribText);                  
          }
        }
        catch (Exception pe)
        {
          pe.printStackTrace();
        }
      }
      else
      {
        try { Thread.sleep(1000L); } catch (Exception ignore) {}
      }
    }
    if (gpxData != null)
    {
      XMLElement gpxDataNode = (XMLElement)storage.createElement("gpx-data");
      root.appendChild(gpxDataNode);  
      // Loop on the GPX Data content
      for (GeoPoint gpxPoint : gpxData)
      {
        double l = gpxPoint.getL();
        double g = gpxPoint.getG();
        XMLElement gpxNode = (XMLElement)storage.createElement("gpx-point");
        gpxNode.setAttribute("lat", Double.toString(l));
        gpxNode.setAttribute("lng", Double.toString(g));
        gpxDataNode.appendChild(gpxNode);              
      }
    }
    
    XMLElement projection = (XMLElement)storage.createElement("projection");
    root.appendChild(projection);
    switch (getProjection())
    {
      case ChartPanel.MERCATOR:
        projection.setAttribute("type", MERCATOR);
        break;
      case ChartPanel.ANAXIMANDRE:
        projection.setAttribute("type", ANAXIMANDRE);
        break;
      case ChartPanel.POLAR_STEREOGRAPHIC:
        projection.setAttribute("type", POLAR_STEREO);
        break;
      case ChartPanel.STEREOGRAPHIC:
        projection.setAttribute("type", STEREO);
        break;
      case ChartPanel.LAMBERT:
        projection.setAttribute("type", LAMBERT);
        projection.setAttribute("contact-parallel", Double.toString(chartPanel.getContactParallel()));
        break;
      case ChartPanel.CONIC_EQUIDISTANT:
        projection.setAttribute("type", CONIC_EQU);
        projection.setAttribute("contact-parallel", Double.toString(chartPanel.getContactParallel()));
        break;
      case ChartPanel.GLOBE_VIEW:
        projection.setAttribute("type", GLOBE);
        break;
      case ChartPanel.SATELLITE_VIEW:
        projection.setAttribute("type", SATELLITE);
        projection.setAttribute("nadir-latitude", Double.toString(chartPanel.getSatelliteLatitude()));              
        projection.setAttribute("nadir-longitude", Double.toString(chartPanel.getSatelliteLongitude()));              
        projection.setAttribute("altitude", Double.toString(chartPanel.getSatelliteAltitude()));              
        projection.setAttribute("opaque", Boolean.toString(!chartPanel.isTransparentGlobe()));              
        break;
      default:
        projection.setAttribute("type", MERCATOR);
        break;
    }
   
    XMLElement north = (XMLElement)storage.createElement("north");
    root.appendChild(north);
    Text northText = storage.createTextNode("#text");
    northText.setNodeValue(Double.toString(nLat));
    north.appendChild(northText);

    XMLElement south = (XMLElement)storage.createElement("south");
    root.appendChild(south);
    Text southText = storage.createTextNode("#text");
    southText.setNodeValue(Double.toString(sLat));
    south.appendChild(southText);
    
    XMLElement east = (XMLElement)storage.createElement("east");
    root.appendChild(east);
    Text eastText = storage.createTextNode("#text");
    eastText.setNodeValue(Double.toString(eLong));
    east.appendChild(eastText);
   
    XMLElement west = (XMLElement)storage.createElement("west");
    root.appendChild(west);
    Text westText = storage.createTextNode("#text");
    westText.setNodeValue(Double.toString(wLong));
    west.appendChild(westText);
   
    XMLElement chartwidth = (XMLElement)storage.createElement("chartwidth");
    root.appendChild(chartwidth);
    Text chartwidthText = storage.createTextNode("#text");
    chartwidthText.setNodeValue(Integer.toString(chartPanel.getWidth()));
    chartwidth.appendChild(chartwidthText);
   
    XMLElement chartheight = (XMLElement)storage.createElement("chartheight");
    root.appendChild(chartheight);
    Text chartheightText = storage.createTextNode("#text");
    chartheightText.setNodeValue(Integer.toString(chartPanel.getHeight()));
    chartheight.appendChild(chartheightText);

    XMLElement scroll = (XMLElement)storage.createElement("scroll");    
    root.appendChild(scroll);
    scroll.setAttribute("x", Integer.toString(chartPanelScrollPane.getViewport().getViewPosition().x));
    scroll.setAttribute("y", Integer.toString(chartPanelScrollPane.getViewport().getViewPosition().y));
    
    XMLElement faxOption = (XMLElement)storage.createElement("fax-option");    
    root.appendChild(faxOption);
    faxOption.setAttribute("value", (getCheckBoxPanelOption() == CHECKBOX_OPTION?"CHECKBOX":"RADIOBUTTON"));

    // Boat position?
    if (boatPosition != null)
    {
      XMLElement boatLocation = (XMLElement)storage.createElement("boat-position");
      root.appendChild(boatLocation);
      boatLocation.setAttribute("lat", Double.toString(boatPosition.getL()));
      boatLocation.setAttribute("lng", Double.toString(boatPosition.getG()));
      boatLocation.setAttribute("hdg", Integer.toString(boatHeading));
    }
    
    String fileName = "";    
    if (!update && compositeName == null)
      fileName = WWGnlUtilities.chooseFile(instance, 
                                           JFileChooser.FILES_ONLY, 
                                           new String[] { "waz" }, // , "xml" }, 
                                           "Composites", 
                                           ParamPanel.data[ParamData.COMPOSITE_ROOT_DIR][ParamData.VALUE_INDEX].toString(), 
                                           "Save", 
                                           "Composite File");
    else
      fileName = compositeName;
    if (!update && fileName != null && fileName.trim().length() > 0)
    {
      boolean archiveRequired = false;
      String archiveName = null;
      fileName = Utilities.makeSureExtensionIsOK(fileName, new String[] { ".xml", ".waz" }, ".waz");
      if (fileName.toUpperCase().endsWith(".WAZ")) // Archive required
      {
        archiveRequired = true;
        archiveName = fileName;
        fileName = fileName.substring(0, fileName.trim().length() - WWContext.WAZ_EXTENSION.length());
      }
      fileName = Utilities.makeSureExtensionIsOK(fileName, ".xml");  // On purpose. There is a boolean (archiveRequired)
      try
      {
        // Check file existence first
        File composite = new File(fileName);
        boolean ok = true;
        if (composite.exists())
        {
          int resp = JOptionPane.showConfirmDialog(instance, 
                                                   WWGnlUtilities.buildMessage("composite-already-exist", new String[] { fileName }), 
                                                   WWGnlUtilities.buildMessage("store-composite"), 
                                                   JOptionPane.YES_NO_OPTION, 
                                                   JOptionPane.WARNING_MESSAGE);
          if (resp == JOptionPane.NO_OPTION)
            ok = false;
        }
        if (ok)
        {
          OutputStream os = new FileOutputStream(composite);
          storage.print(os);
          os.close();
          if (!archiveRequired)
            WWContext.getInstance().fireReloadCompositeTree();
        }
      }
      catch (Exception e)
      {
        WWContext.getInstance().fireExceptionLogging(e);
        e.printStackTrace();
      }
      if (archiveRequired) // Archive here if necessary
      {
//      System.out.println("Archiving " + fileName);
        boolean autoDownloadAndSave = fileName != null && !update;
        WWGnlUtilities.archiveComposite(fileName, autoDownloadAndSave); 
        WWContext.getInstance().fireReloadCompositeTree();
        WWContext.getInstance().fireReloadFaxTree();
        WWContext.getInstance().fireReloadGRIBTree();
        // Finally, reload composite from archive, 
        // to have the faxes and GRIBs pointing in the archive
        WWContext.getInstance().fireProgressing(WWGnlUtilities.buildMessage("swapping-pointers")); 
        restoreComposite(archiveName);
      }
    }
    else if (update && fileName != null && fileName.trim().length() > 0)
    {
      // Update the composite.xml in the waz file
      if (fileName.toUpperCase().endsWith(".WAZ")) // Archive required
      {
//      WWContext.getInstance().fireInterruptProcess();
        int resp = JOptionPane.showConfirmDialog(instance, 
                                                 WWGnlUtilities.buildMessage("updating-composite", new String[] { fileName }), 
                                                 WWGnlUtilities.buildMessage("store-composite"), 
                                                 JOptionPane.YES_NO_OPTION, 
                                                 JOptionPane.WARNING_MESSAGE);
        if (resp == JOptionPane.YES_OPTION)
        {
          try
          {
            storage.print(System.out);
            WWGnlUtilities.updateComposite(fileName, storage);
          }
          catch (Exception ex)
          {
            ex.printStackTrace();
          }
          // Finally
          WWContext.getInstance().fireReloadCompositeTree();
        }
      }
      else
        System.out.println("Ooops!");
    }
    WWContext.getInstance().fireSetCompositeFileName(fileName);  
    WWContext.getInstance().fireSetLoading(false, "Please wait...");               
  }
  
  private void warnFor3D()
  {
    JOptionPane.showMessageDialog(this, WWGnlUtilities.buildMessage("warn-for-3d"), 
                                  "3D Data", 
                                  JOptionPane.INFORMATION_MESSAGE);
  }
  
  private void generateAll3DData()
  {
    boolean displaySomething = display3DTws || display3DPrmsl || display3D500mb || display3DWaves || display3DTemperature || display3DRain;
    if (displaySomething)
    {
      WWContext.getInstance().fireEnable3DTab(displaySomething);
      GRIBDataUtil.generateChart3dFile(gribData, 1.0, chartPanel);
      if (display3DTws)
      {
        if (smooth > 1 && !alreadyAskedAboutGRIB)
        {
          warnFor3D();
          alreadyAskedAboutGRIB = true;
        }
      /*  boundaries[WIND_SPEED] = */ GRIBDataUtil.generate3dFile(gribData, GRIBDataUtil.TYPE_TWS, 1.0, 15.0, chartPanel);
 //     boundaries[WIND_SPEED][0] /= 10D;
 //     boundaries[WIND_SPEED][ParamData.VALUE_INDEX] /= 10D;
      }
      else
      {
        WWContext.getInstance().fireNoTWSObj();
      //  boundaries[2][0] = boundaries[2][ParamData.VALUE_INDEX] = 0D;
      }
      // 500mb and other data.
      if (display3DPrmsl)
      {
        if (smooth > 1 && !alreadyAskedAboutGRIB)
        {
          warnFor3D();
          alreadyAskedAboutGRIB = true;
        }
        boundaries[PRMSL] = GRIBDataUtil.generate3dFile(gribData, GRIBDataUtil.TYPE_PRMSL, 1.0,  1.0, chartPanel);
        boundaries[PRMSL][0] /= 10D;
        boundaries[PRMSL][ParamData.VALUE_INDEX] /= 10D;
      }
      else
      {
        WWContext.getInstance().fireNoPrmslObj();
    //  boundaries[1][0] = boundaries[1][ParamData.VALUE_INDEX] = 0D;
      }
      if (display3D500mb)
      {
        if (smooth > 1 && !alreadyAskedAboutGRIB)
        {
          warnFor3D();
          alreadyAskedAboutGRIB = true;
        }
        boundaries[HGT500] = GRIBDataUtil.generate3dFile(gribData, GRIBDataUtil.TYPE_500MB, 1.0,  1.0, chartPanel);
        boundaries[HGT500][0] /= 10D;
        boundaries[HGT500][ParamData.VALUE_INDEX] /= 10D;
      }
      else
      {
        WWContext.getInstance().fireNo500mbObj();
    //  boundaries[2][0] = boundaries[2][ParamData.VALUE_INDEX] = 0D;
      }
      if (display3DTemperature)
      {
        if (smooth > 1 && !alreadyAskedAboutGRIB)
        {
          warnFor3D();
          alreadyAskedAboutGRIB = true;
        }
        boundaries[TEMPERATURE] = GRIBDataUtil.generate3dFile(gribData, GRIBDataUtil.TYPE_TMP,   1.0, 10.0, chartPanel);
        boundaries[TEMPERATURE][0] -= 273.6D;
        boundaries[TEMPERATURE][ParamData.VALUE_INDEX] -= 273.6D;
      }
      else
      {
        WWContext.getInstance().fireNoTmpObj();
    //  boundaries[3][0] = boundaries[3][ParamData.VALUE_INDEX] = 0D;
      }
      if (display3DWaves)
      {
        if (smooth > 1 && !alreadyAskedAboutGRIB)
        {
          warnFor3D();
          alreadyAskedAboutGRIB = true;
        }
        boundaries[WAVES] = GRIBDataUtil.generate3dFile(gribData, GRIBDataUtil.TYPE_WAVE,  1.0, 10.0, chartPanel);
        boundaries[WAVES][0] /= 10D;
        boundaries[WAVES][ParamData.VALUE_INDEX] /= 10D;
      }
      else
      {
        WWContext.getInstance().fireNoWaveObj();
      }
      if (display3DRain) // TODO Make sure boundaries are OK...
      {
        if (smooth > 1 && !alreadyAskedAboutGRIB)
        {
          warnFor3D();
          alreadyAskedAboutGRIB = true;
        }
        boundaries[RAIN] = GRIBDataUtil.generate3dFile(gribData, GRIBDataUtil.TYPE_RAIN,  1.0, 50.0, chartPanel);
    //  boundaries[RAIN][0] *= 3600D;
    //  boundaries[RAIN][ParamData.VALUE_INDEX] *= 3600D;
      }
      else
      {
        WWContext.getInstance().fireNoRainObj();
    //  boundaries[RAIN4][0] = boundaries[RAIN][ParamData.VALUE_INDEX] = 0D;
      }
    }
  }

  private static int getDataIndexOf(String str)
  {
    int idx = 0;
    for (int i=0; i<dataLabels.length; i++)
    {
      if (str.equals(dataLabels[i]))
      {
        idx = i;
        break;
      }
    }
    return idx;
  }
  
  private void buildPlaces()
  {
    String placesFileName = PlacesTablePanel.PLACES_FILE_NAME;
    DOMParser parser = WWContext.getInstance().getParser();
    try
    {
      XMLDocument doc = null;
      synchronized(parser)
      {
        parser.setValidationMode(DOMParser.NONVALIDATING);
        parser.parse(new File(placesFileName).toURI().toURL());
        doc = parser.getDocument();
      }
      NodeList place = doc.selectNodes("//place");
      List<GeoPoint> alPos  = new ArrayList<GeoPoint>(place.getLength());
      List<String> alName = new ArrayList<String>(place.getLength());
      List<Boolean> alShow = new ArrayList<Boolean>(place.getLength());
      for (int i=0; i<place.getLength(); i++)
      {
        GeoPoint gp = null;
        XMLElement xe = (XMLElement)place.item(i);
        String placeName = xe.getAttribute("name");
        String show      = xe.getAttribute("show");
        if (show.trim().length() == 0)
          show = "true";
        String degL = ((XMLElement)xe.getElementsByTagName("latitude").item(0)).getAttribute("deg");
        String minL = ((XMLElement)xe.getElementsByTagName("latitude").item(0)).getAttribute("min");
        String sgnL = ((XMLElement)xe.getElementsByTagName("latitude").item(0)).getAttribute("sign");
        String degG = ((XMLElement)xe.getElementsByTagName("longitude").item(0)).getAttribute("deg");
        String minG = ((XMLElement)xe.getElementsByTagName("longitude").item(0)).getAttribute("min");
        String sgnG = ((XMLElement)xe.getElementsByTagName("longitude").item(0)).getAttribute("sign");
        double l = GeomUtil.sexToDec(degL, minL);
        if (sgnL.equals("S")) l = -l;
        double g = GeomUtil.sexToDec(degG, minG);
        if (sgnG.equals("W")) g = -g;
        gp = new GeoPoint(l, g);
        alPos.add(gp);
        alName.add(placeName);
        alShow.add(new Boolean(show));
      }
      gpa = /*(GeoPoint[])*/alPos.toArray(new GeoPoint[alPos.size()]);
      ptLabels = /*(String[])*/alName.toArray(new String[alName.size()]);
      showPlacesArray = alShow.toArray(new Boolean[alShow.size()]);
    }
    catch (FileNotFoundException fne)
    {
      WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("places-not-found") + "\n");
    }
    catch (Exception ex)
    {
      WWContext.getInstance().fireExceptionLogging(ex);
      ex.printStackTrace();
    }
  }
  
  private void buildSailMailStations()
  {
    sma = new ArrayList<WWGnlUtilities.SailMailStation>();
    String placesFileName = WWContext.SAILMAIL_STATIONS;
    DOMParser parser = WWContext.getInstance().getParser();
    try
    {
      XMLDocument doc = null;
      synchronized(parser)
      {
        parser.setValidationMode(DOMParser.NONVALIDATING);
        parser.parse(new File(placesFileName).toURI().toURL());
        doc = parser.getDocument();
      }
      NodeList place = doc.selectNodes("//place");
//    List<GeoPoint> alPos  = new ArrayList<GeoPoint>(place.getLength());
//    List<String> alName = new ArrayList<String>(place.getLength());
//    List<Boolean> alShow = new ArrayList<Boolean>(place.getLength());
      for (int i=0; i<place.getLength(); i++)
      {
        GeoPoint gp = null;
        XMLElement xe = (XMLElement)place.item(i);
        String placeName = xe.getAttribute("name");
        String show      = xe.getAttribute("show");
        if (show.trim().length() == 0)
          show = "true";
        String degL = ((XMLElement)xe.getElementsByTagName("latitude").item(0)).getAttribute("deg");
        String minL = ((XMLElement)xe.getElementsByTagName("latitude").item(0)).getAttribute("min");
        String sgnL = ((XMLElement)xe.getElementsByTagName("latitude").item(0)).getAttribute("sign");
        String degG = ((XMLElement)xe.getElementsByTagName("longitude").item(0)).getAttribute("deg");
        String minG = ((XMLElement)xe.getElementsByTagName("longitude").item(0)).getAttribute("min");
        String sgnG = ((XMLElement)xe.getElementsByTagName("longitude").item(0)).getAttribute("sign");
        double l = GeomUtil.sexToDec(degL, minL);
        if (sgnL.equals("S")) l = -l;
        double g = GeomUtil.sexToDec(degG, minG);
        if (sgnG.equals("W")) g = -g;
        gp = new GeoPoint(l, g);
        WWGnlUtilities.SailMailStation sms = new WWGnlUtilities.SailMailStation(gp, placeName);
        sma.add(sms);
      }
    }
    catch (FileNotFoundException fne)
    {
      WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("places-not-found") + "\n");
    }
    catch (Exception ex)
    {
      WWContext.getInstance().fireExceptionLogging(ex);
      ex.printStackTrace();
    }
  }
  
  private void buildWeatherStations()
  {
    wsta = new ArrayList<WWGnlUtilities.WeatherStation>();
    String placesFileName = WWContext.NOAA_STATIONS;
    DOMParser parser = WWContext.getInstance().getParser();
    try
    {
      XMLDocument doc = null;
      synchronized(parser)
      {
        parser.setValidationMode(DOMParser.NONVALIDATING);
        parser.parse(new File(placesFileName).toURI().toURL());
        doc = parser.getDocument();
      }
      NodeList place = doc.selectNodes("//place");
//    List<GeoPoint> alPos  = new ArrayList<GeoPoint>(place.getLength());
//    List<String> alName = new ArrayList<String>(place.getLength());
//    List<Boolean> alShow = new ArrayList<Boolean>(place.getLength());
      for (int i=0; i<place.getLength(); i++)
      {
        GeoPoint gp = null;
        XMLElement xe = (XMLElement)place.item(i);
        String placeName = xe.getAttribute("name");
        String show      = xe.getAttribute("show");
        if (show.trim().length() == 0)
          show = "true";
        String degL = ((XMLElement)xe.getElementsByTagName("latitude").item(0)).getAttribute("deg");
        String minL = ((XMLElement)xe.getElementsByTagName("latitude").item(0)).getAttribute("min");
        String sgnL = ((XMLElement)xe.getElementsByTagName("latitude").item(0)).getAttribute("sign");
        String degG = ((XMLElement)xe.getElementsByTagName("longitude").item(0)).getAttribute("deg");
        String minG = ((XMLElement)xe.getElementsByTagName("longitude").item(0)).getAttribute("min");
        String sgnG = ((XMLElement)xe.getElementsByTagName("longitude").item(0)).getAttribute("sign");
        double l = GeomUtil.sexToDec(degL, minL);
        if (sgnL.equals("S")) l = -l;
        double g = GeomUtil.sexToDec(degG, minG);
        if (sgnG.equals("W")) g = -g;
        gp = new GeoPoint(l, g);
        WWGnlUtilities.WeatherStation ws = new WWGnlUtilities.WeatherStation(gp, placeName);
        wsta.add(ws);
      }
    }
    catch (FileNotFoundException fne)
    {
      WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("places-not-found") + "\n");
    }
    catch (Exception ex)
    {
      WWContext.getInstance().fireExceptionLogging(ex);
      ex.printStackTrace();
    }
  }
  
  private void updateGRIBDisplay()
  {
    chartPanel.repaint();
    gribData = null; 
    if (wgd != null)
    {
      if (gribIndex == -1) gribIndex = 0;
      gribData = wgd[gribIndex]; 
//    System.out.println("smooth:" + smooth + ", required:" + smoothingRequired);
      if (smooth > 1 && gribData != null && smoothingRequired)
      {
        gribData = GribHelper.smoothGribData(gribData, smooth); // Rebuilds all the GRIB Data
        smoothingRequired = false;
        // Re-generate 3D?
//      generateAll3DData();
      }              
      if (gribData != null)
      {
        if (fromGRIBSlice != null && toGRIBSlice != null)
          displayGRIBSlice();

        if (displayContourLines)
        {              
          if (isoPointsThread != null && isoPointsThread.isAlive())
            isoPointsThread.interrupt();
          isoPointsThread = new IsoPointsThread(gribData);
          isoPointsThread.start();
        }
        boundaries[WIND_SPEED] = GRIBDataUtil.getWindSpeedBoundaries(gribData);
        String u = units[WIND_SPEED];
        boundariesLabel.setText(WWGnlUtilities.buildMessage("from-to", new String[] { WWGnlUtilities.XX22.format(boundaries[0][0]) + u, WWGnlUtilities.XX22.format(boundaries[0][ParamData.VALUE_INDEX]) + u }));            
        boundariesLabel.setEnabled(true);
        if (!windOnly)
        {
          // 500mb and other data.
           if (display3D500mb)
            GRIBDataUtil.generate3dFile(gribData, GRIBDataUtil.TYPE_500MB, 1.0,  1.0, chartPanel);
           if (display3DPrmsl)
            GRIBDataUtil.generate3dFile(gribData, GRIBDataUtil.TYPE_PRMSL, 1.0,  1.0, chartPanel);
           if (display3DTemperature)
            GRIBDataUtil.generate3dFile(gribData, GRIBDataUtil.TYPE_TMP,   1.0, 10.0, chartPanel);
           if (display3DWaves)
            GRIBDataUtil.generate3dFile(gribData, GRIBDataUtil.TYPE_WAVE,  1.0, 10.0, chartPanel);
          if (display3DRain)
            GRIBDataUtil.generate3dFile(gribData, GRIBDataUtil.TYPE_RAIN,  1.0, 50.0, chartPanel);
          if (display3DTws)
            GRIBDataUtil.generate3dFile(gribData, GRIBDataUtil.TYPE_TWS,  1.0, 15.0, chartPanel);
        }
    //      System.out.println(gribData.getDate().toString());
        Date gribDate = gribData.getDate(); // TimeUtil.getGMT(gribData.getDate());
        Calendar cal = new GregorianCalendar();
        cal.setTime(gribDate);
        
        int dow     = cal.get(Calendar.DAY_OF_WEEK);
        int year    = cal.get(Calendar.YEAR);
        int month   = cal.get(Calendar.MONTH);
        int day     = cal.get(Calendar.DAY_OF_MONTH);
        int hours   = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        int seconds = cal.get(Calendar.SECOND);
        
        String gribInfo2 = Integer.toString(year) + "-" + WWGnlUtilities.MONTH[month] + "-" + WWGnlUtilities.DF2.format(day) + " " + WWGnlUtilities.WEEK[dow - 1] + " " + WWGnlUtilities.DF2.format(hours) + ":" + WWGnlUtilities.DF2.format(minutes) + ":" + WWGnlUtilities.DF2.format(seconds) + " UTC";
        String gribInfo3 = GeomUtil.decToSex(gribData.getNLat(), GeomUtil.SWING, GeomUtil.NS) + " " + 
                           GeomUtil.decToSex(gribData.getWLng(), GeomUtil.SWING, GeomUtil.EW);
        String gribInfo4 = GeomUtil.decToSex(gribData.getSLat(), GeomUtil.SWING, GeomUtil.NS) + " " + 
                           GeomUtil.decToSex(gribData.getELng(), GeomUtil.SWING, GeomUtil.EW);

        GribFile gf = WWContext.getInstance().getGribFile();
        setThereIsWind(GRIBDataUtil.thereIsVariation(gf, GRIBDataUtil.TYPE_TWS));
//      setThereIsPrmsl((gribData.getGribPointData()[0][0].getPrmsl() > 0));
//      setThereIsPrmsl(GRIBDataUtil.thereIsVariation(gribData, GRIBDataUtil.TYPE_PRMSL));
        setThereIsPrmsl(GRIBDataUtil.thereIsVariation(gf, GRIBDataUtil.TYPE_PRMSL));
//      setThereIs500mb((gribData.getGribPointData()[0][0].getHgt() > 0));
//      setThereIs500mb(GRIBDataUtil.thereIsVariation(gribData, GRIBDataUtil.TYPE_500MB));
        setThereIs500mb(GRIBDataUtil.thereIsVariation(gf, GRIBDataUtil.TYPE_500MB));
//      setThereIsTemperature((gribData.getGribPointData()[0][0].getTmp() > 0));
//      setThereIsTemperature(GRIBDataUtil.thereIsVariation(gribData, GRIBDataUtil.TYPE_TMP));
        setThereIsTemperature(GRIBDataUtil.thereIsVariation(gf, GRIBDataUtil.TYPE_TMP));
//      setThereIsWaves(GRIBDataUtil.thereIsVariation(gribData, GRIBDataUtil.TYPE_WAVE));
        setThereIsWaves(GRIBDataUtil.thereIsVariation(gf, GRIBDataUtil.TYPE_WAVE));
//      setThereIsRain(GRIBDataUtil.thereIsVariation(gribData, GRIBDataUtil.TYPE_RAIN));
        setThereIsRain(GRIBDataUtil.thereIsVariation(gf, GRIBDataUtil.TYPE_RAIN));
        setThereIsCurrent(GRIBDataUtil.thereIsVariation(gf, GRIBDataUtil.TYPE_CURRENT));

        WWContext.getInstance().fireGribInfo(gribIndex, 
                                             wgd.length, 
                                             gribInfo2, 
                                             gribInfo3, 
                                             gribInfo4,
                                             windOnly,
                                             gribData.wind,
                                             isTherePrmsl(),
                                             isThere500mb(),
                                             isThereTemperature(),
                                             isThereWaves(),
                                             isThereRain(),
                                             isThereCurrent(),
                                             isThereWind(),
                                             windOnly?false:gribData.prmsl,
                                             windOnly?false:gribData.hgt,
                                             windOnly?false:gribData.temp,
                                             windOnly?false:gribData.wave,
                                             windOnly?false:gribData.rain);
      }
    }              
  }
  
  
  public void generateGoogleFiles(final int option)
  {
    if (getProjection() != ChartPanel.MERCATOR &&
        getProjection() != ChartPanel.ANAXIMANDRE)
    {
      JOptionPane.showMessageDialog(instance, WWGnlUtilities.buildMessage("mercator-only"), WWGnlUtilities.buildMessage("google"), JOptionPane.WARNING_MESSAGE);
    }
    else
    {
      if ((gribFileName == null || gribFileName.trim().length() == 0) && (faxImage == null || faxImage.length == 0))
      {
        JOptionPane.showMessageDialog(instance, WWGnlUtilities.buildMessage("nothing-to-display"), WWGnlUtilities.buildMessage("google"), JOptionPane.WARNING_MESSAGE);
      }
      else
      {              
        Thread googleThread = new Thread("google-thread")
        {
          public void run()
          {
            GoogleUtil.generateGoogleFile(chartPanel, 
                                          option, 
                                          faxImage, 
                                          instance, 
                                          coloredWind, 
                                          gribFileName, 
                                          drawGRIB, 
                                          wgd, 
                                          gribIndex, 
                                          displayComboBox, 
                                          displayAltTooltip);
          }
        };
        googleThread.start();
      }
    }
  }
  
  private final static String MERCATOR     = "MERCATOR";
  private final static String ANAXIMANDRE  = "ANAXIMANDRE";
  private final static String POLAR_STEREO = "POLAR_STEREO";
  private final static String STEREO       = "STEREO";
  private final static String LAMBERT      = "LAMBERT";
  private final static String CONIC_EQU    = "CONIC_EQU";
  private final static String GLOBE        = "GLOBE";
  private final static String SATELLITE    = "SATELLITE";
  
  public void restoreComposite(String fileName)
  {
    try
    {
      restoreComposite(fileName, TwoFilePanel.EVERY_THING, true);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "Restoring Composite", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  public int restoreComposite(String fileName, String option, boolean withBoatAndTrack)
  {
    return restoreComposite(fileName, option, null, withBoatAndTrack);
  }
  
  public int restoreComposite(String fileName, String option, Pattern faxPattern, boolean withBoatAndTrack)
  {
    int nbComponents = 0;
    boolean fromArchive = false;
    ZipFile waz = null;
    XMLDocument doc = null;
    DOMParser parser = WWContext.getInstance().getParser();
    synchronized (parser)
    {    
      parser.setValidationMode(DOMParser.NONVALIDATING);
      WWContext.getInstance().setCurrentComposite(fileName);  
      try
      {
        if (fileName.endsWith(WWContext.WAZ_EXTENSION))
        {
          fromArchive = true;
          waz = new ZipFile(fileName);
          ZipEntry composite = waz.getEntry("composite.xml");
          if (composite != null)
          {
            InputStream is = waz.getInputStream(composite);
            parser.parse(is);
          }
          else
            System.out.println("composite.xml not found :(");
        }
        else if (fileName.endsWith(".xml"))
        {
          if (!(new File(fileName).isDirectory()))
            parser.parse(new File(fileName).toURI().toURL());
          else
          {
            JOptionPane.showMessageDialog(instance, fileName + " is a directory", "Restoring Composite", JOptionPane.ERROR_MESSAGE);
            return 0;
          }
        }
        doc = parser.getDocument();
  
        if (doc.selectNodes("//composite-comment").getLength() > 0)
        {
          currentComment = Utilities.superTrim(doc.selectNodes("//composite-comment").item(0).getFirstChild().getNodeValue());
          WWContext.getInstance().fireLogging("Comment:" + currentComment + "\n");
        }
        else
        {
          WWContext.getInstance().fireLogging("Reseting Comment...\n");
          currentComment = "";
        }
        
        String projType = "";
        try { projType = ((XMLElement)doc.selectNodes("//projection").item(0)).getAttribute("type"); } catch (Exception ignore) { projType = MERCATOR; }
        if (projType.equals(MERCATOR))
        {
          chartPanel.setProjection(ChartPanel.MERCATOR);
          WWContext.getInstance().fireSetProjection(ChartPanel.MERCATOR);
        }
        else if  (projType.equals(ANAXIMANDRE))
        {
          chartPanel.setProjection(ChartPanel.ANAXIMANDRE);
          WWContext.getInstance().fireSetProjection(ChartPanel.ANAXIMANDRE);
        }
        else if  (projType.equals(LAMBERT))
        {
          chartPanel.setProjection(ChartPanel.LAMBERT);
          double cp = Double.parseDouble(((XMLElement)doc.selectNodes("//projection").item(0)).getAttribute("contact-parallel"));
          chartPanel.setContactParallel(cp);
          WWContext.getInstance().fireSetProjection(ChartPanel.LAMBERT);
          WWContext.getInstance().fireSetContactParallel(cp);
        }
        else if  (projType.equals(CONIC_EQU))
        {
          chartPanel.setProjection(ChartPanel.CONIC_EQUIDISTANT);
          double cp = Double.parseDouble(((XMLElement)doc.selectNodes("//projection").item(0)).getAttribute("contact-parallel"));
          chartPanel.setContactParallel(cp);
          WWContext.getInstance().fireSetProjection(ChartPanel.CONIC_EQUIDISTANT);
          WWContext.getInstance().fireSetContactParallel(cp);
        }
        else if  (projType.equals(GLOBE))
        {
          chartPanel.setProjection(ChartPanel.GLOBE_VIEW);
          WWContext.getInstance().fireSetProjection(ChartPanel.GLOBE_VIEW);
  //      StaticObjects.getInstance().fireSetGlobeParameters();
        }
        else if  (projType.equals(STEREO))
        {
          chartPanel.setProjection(ChartPanel.STEREOGRAPHIC);
          WWContext.getInstance().fireSetProjection(ChartPanel.STEREOGRAPHIC);
        }
        else if  (projType.equals(POLAR_STEREO))
        {
          chartPanel.setProjection(ChartPanel.POLAR_STEREOGRAPHIC);
          WWContext.getInstance().fireSetProjection(ChartPanel.POLAR_STEREOGRAPHIC);
        }
        else if (projType.equals(SATELLITE))
        {
          chartPanel.setProjection(ChartPanel.SATELLITE_VIEW);
          WWContext.getInstance().fireSetProjection(ChartPanel.SATELLITE_VIEW);
          double nl  = Double.parseDouble(((XMLElement)doc.selectNodes("//projection").item(0)).getAttribute("nadir-latitude"));
          double ng  = Double.parseDouble(((XMLElement)doc.selectNodes("//projection").item(0)).getAttribute("nadir-longitude"));
          double alt = Double.parseDouble(((XMLElement)doc.selectNodes("//projection").item(0)).getAttribute("altitude"));
          boolean opaque = new Boolean(((XMLElement)doc.selectNodes("//projection").item(0)).getAttribute("opaque")).booleanValue();
          chartPanel.setSatelliteAltitude(alt);
          chartPanel.setSatelliteLatitude(nl);
          chartPanel.setSatelliteLongitude(ng);
          chartPanel.setTransparentGlobe(!opaque);
          WWContext.getInstance().fireSetSatelliteParameters(nl, ng, alt, opaque);
        }
        try { nLat = Double.parseDouble(doc.selectNodes("//north").item(0).getFirstChild().getNodeValue()); } catch (Exception ex) {}
        try { sLat = Double.parseDouble(doc.selectNodes("//south").item(0).getFirstChild().getNodeValue()); } catch (Exception ex) {}
        try { wLong = Double.parseDouble(doc.selectNodes("//west").item(0).getFirstChild().getNodeValue()); } catch (Exception ex) {}
        try { eLong = Double.parseDouble(doc.selectNodes("//east").item(0).getFirstChild().getNodeValue()); } catch (Exception ex) {}
        int w = 0;
        try { w = Integer.parseInt(doc.selectNodes("//chartwidth").item(0).getFirstChild().getNodeValue()); } catch (Exception ex) {}
        int h = 0;
        try { h = Integer.parseInt(doc.selectNodes("//chartheight").item(0).getFirstChild().getNodeValue()); } catch (Exception ex) {}
        
        int xScroll = 0, yScroll = 0;
        try
        {
          XMLElement scroll = (XMLElement)(doc.selectNodes("//scroll").item(0));
          xScroll = Integer.parseInt(scroll.getAttribute("x"));
          yScroll = Integer.parseInt(scroll.getAttribute("y"));
        }
        catch (Exception ignore) 
        {
          ignore.printStackTrace();
          doc.print(System.err);
        }
        try
        {
          XMLElement faxOption = (XMLElement)(doc.selectNodes("//fax-option").item(0));
          String opt = faxOption.getAttribute("value");
          if (opt.equals("CHECKBOX"))
            setCheckBoxPanelOption(CHECKBOX_OPTION);
          else if (opt.equals("RADIOBUTTON"))
            setCheckBoxPanelOption(RADIOBUTTON_OPTION);
          else
            System.out.println("Unknown option [" + opt + "]");
        }
        catch (Exception ignore) 
        {
          ignore.printStackTrace();
          doc.print(System.err);
        }
        
        // Boat Position?
        if ((withBoatAndTrack || option.equals(TwoFilePanel.EVERY_THING)) && doc.selectNodes("//boat-position").getLength() == 1)
        {
          XMLElement bp = (XMLElement)doc.selectNodes("//boat-position").item(0);
          double l   = Double.parseDouble(bp.getAttribute("lat"));
          double g   = Double.parseDouble(bp.getAttribute("lng"));
          int hdg    = Integer.parseInt(bp.getAttribute("hdg"));
          boatPosition = new GeoPoint(l, g);
          boatHeading  = hdg;
        }
        
        if (chartPanel.getProjection() != ChartPanel.GLOBE_VIEW && 
            chartPanel.getProjection() != ChartPanel.SATELLITE_VIEW)
          chartPanel.setWidthFromChart(nLat, sLat, wLong, eLong);
  //    eLong = chartPanel.calculateEastG(nLat, sLat, wLong);
        chartPanel.setEastG(eLong);
        chartPanel.setWestG(wLong);
        chartPanel.setNorthL(nLat);
        chartPanel.setSouthL(sLat);

        chartPanel.setW(w);
        chartPanel.setH(h);
        chartPanel.setBounds(0, 0, w, h);

        chartPanel.repaint();  
  
        if (option.equals(TwoFilePanel.EVERY_THING) || option.equals(TwoFilePanel.JUST_FAXES))
        {
          try 
          { 
            NodeList faxes = doc.selectNodes("//fax-collection/fax");
            faxImage = new FaxImage[faxes.getLength()];
            FaxType[] ft = new FaxType[faxes.getLength()];
            for (int i=0; i<faxes.getLength(); i++)
            {
              WWContext.getInstance().fireProgressing(WWGnlUtilities.buildMessage("restoring-fax", new String[] {Integer.toString(i+1),
                                                                                                                 Integer.toString(faxes.getLength())}));
              XMLElement fax = (XMLElement)faxes.item(i);
              String faxName = fax.getAttribute("file");
              if (faxPattern != null)
              {
                Matcher m = faxPattern.matcher(faxName);
                if (!m.find())
                  continue;
              }
              nbComponents++;
              Color c = WWGnlUtilities.buildColor(fax.getAttribute("color"));
              String strRatio = fax.getAttribute("wh-ratio");
              if (strRatio.trim().length() > 0)
              {
                try { whRatio = Double.parseDouble(strRatio); } 
                catch (Exception ignore) { ignore.printStackTrace(); }
              }
              else
                whRatio = 1D;
              String strTransparent = fax.getAttribute("transparent");
              String strColorChange = fax.getAttribute("color-change");
              double imageScale = Double.parseDouble(fax.selectNodes("./faxScale").item(0).getFirstChild().getNodeValue());
              int imageHOffset  = Integer.parseInt(fax.selectNodes("./faxXoffset").item(0).getFirstChild().getNodeValue());
              int imageVOffset  = Integer.parseInt(fax.selectNodes("./faxYoffset").item(0).getFirstChild().getNodeValue());
              double imageRotation = 0D;
              try { imageRotation = Double.parseDouble(fax.selectNodes("./faxRotation").item(0).getFirstChild().getNodeValue()); } 
              catch (Exception ignore) { ignore.printStackTrace(); }
              // New items (15-sep-2009)
              String faxTitle = "";
              String faxOrigin = "";
              try { faxTitle = fax.selectNodes("./faxTitle").item(0).getFirstChild().getNodeValue(); } 
              catch (Exception ignore) { /* ignore.printStackTrace(); */ }
              try { faxOrigin = fax.selectNodes("./faxOrigin").item(0).getFirstChild().getNodeValue(); } 
              catch (Exception ignore) { /* ignore.printStackTrace(); */ }
              
              faxImage[i] = new FaxImage();
              faxImage[i].fileName = faxName;
              faxImage[i].color = c;
              faxImage[i].imageScale   = imageScale;
              faxImage[i].imageHOffset = imageHOffset;
              faxImage[i].imageVOffset = imageVOffset;
              faxImage[i].imageRotationAngle = imageRotation;
              faxImage[i].comment = WWGnlUtilities.getHeader(faxName);
              faxImage[i].show = true;
              if (strTransparent == null || strTransparent.trim().length() == 0)
                faxImage[i].transparent = true;
              else
                faxImage[i].transparent = strTransparent.equals("true");
              if (strColorChange == null || strColorChange.trim().length() == 0)
                faxImage[i].colorChange = true;
              else
                faxImage[i].colorChange = strColorChange.equals("true");
              faxImage[i].faxTitle = faxTitle;
              faxImage[i].faxOrigin = faxOrigin;
              try
              {
                if (fromArchive)
                {
                  if (faxName.startsWith(WWContext.WAZ_PROTOCOL_PREFIX))
                    faxName = faxName.substring(WWContext.WAZ_PROTOCOL_PREFIX.length());
                  InputStream is = waz.getInputStream(waz.getEntry(faxName));
                  boolean tif = faxName.toUpperCase().endsWith(".TIFF") || faxName.toUpperCase().endsWith(".TIF");
                  if (faxImage[i].transparent)
                  {
                    if (faxImage[i].colorChange)
                    {
                //    faxImage[i].faxImage = ImageUtil.makeTransparentImage(this, ImageUtil.readImage(is, tif), c);
                   // faxImage[i].faxImage = ImageUtil.switchColorAndMakeColorTransparent(ImageUtil.readImage(is, tif), Color.black, c, Color.white, blurSharpOption);
                      Image faxImg = ImageUtil.readImage(is, tif); //ImageUtil.readImage(faxName);
                      if (ImageUtil.countColors(faxImg) > 2)
                        faxImg = ImageUtil.switchAnyColorAndMakeColorTransparent(faxImg, c, ImageUtil.mostUsedColor(faxImg), blurSharpOption);
                      else
                        faxImg = ImageUtil.switchColorAndMakeColorTransparent(faxImg, Color.black, c, Color.white, blurSharpOption);
                      faxImage[i].faxImage = faxImg;
                    }
                    else
                      faxImage[i].faxImage = ImageUtil.makeColorTransparent(ImageUtil.readImage(is, tif), Color.white, blurSharpOption);
                  }
                  else
                    faxImage[i].faxImage = ImageUtil.readImage(is, tif);     
                  is.close();
                }
                else
                {
                  if (faxImage[i].transparent)
                  {
                    if (faxImage[i].colorChange)
                  //  faxImage[i].faxImage = ImageUtil.makeTransparentImage(this, ImageUtil.readImage(faxName), c);
                      faxImage[i].faxImage = ImageUtil.switchColorAndMakeColorTransparent(ImageUtil.readImage(faxName), Color.black, c, Color.white, blurSharpOption);
                    else
                      faxImage[i].faxImage = ImageUtil.makeColorTransparent(ImageUtil.readImage(faxName), Color.white, blurSharpOption);
                  }
                  else
                    faxImage[i].faxImage = ImageUtil.readImage(faxName);
                }
              }
              catch (Exception e)
              {
                WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("file-not-found", new String[] { faxName }) + "\n");
                throw e;
              }
              ft[i] = new FaxType(faxName, c, Boolean.valueOf(true), Boolean.valueOf(true), imageRotation, faxOrigin, faxTitle, faxImage[i].colorChange);
              ft[i].setRank(i+1);
              ft[i].setComment(faxImage[i].comment);
              ft[i].setShow(true);
              ft[i].setTransparent(true);
              repaint(); // Repaint between each fax
            }
            if (faxPattern == null)
            {
              setCheckBoxes(ft);
              if (faxes.getLength() > 0)
              {
                WWContext.getInstance().fireFaxLoaded();
                WWContext.getInstance().fireFaxesLoaded(ft);
              }
              setCheckBoxes(ft);
            }
          }
          catch (Exception ex) 
          {
            WWContext.getInstance().fireExceptionLogging(ex);
            ex.printStackTrace();
            unsetFaxImage();
          }
        }
        // TODO IF there is a GRIB...
        if (option.equals(TwoFilePanel.EVERY_THING) || option.equals(TwoFilePanel.JUST_GRIBS))
        {
          try 
          { 
            XMLElement gribNode = (XMLElement)doc.selectNodes("//grib").item(0);

            String twsData = gribNode.getAttribute("display-TWS-Data");
            if (twsData.trim().length() > 0) // New version
            {
              displayTws = "true".equals(twsData);
              displayPrmsl = "true".equals(gribNode.getAttribute("display-PRMSL-Data"));
              display500mb = "true".equals(gribNode.getAttribute("display-500HGT-Data"));
              displayWaves = "true".equals(gribNode.getAttribute("display-WAVES-Data"));
              displayTemperature = "true".equals(gribNode.getAttribute("display-TEMP-Data"));
              displayRain = "true".equals(gribNode.getAttribute("display-PRATE-Data"));
              
              displayContourTWS = "true".equals(gribNode.getAttribute("display-TWS-contour"));
              displayContourPRMSL = "true".equals(gribNode.getAttribute("display-PRMSL-contour"));
              displayContour500mb = "true".equals(gribNode.getAttribute("display-500HGT-contour"));
              displayContourWaves = "true".equals(gribNode.getAttribute("display-WAVES-contour"));
              displayContourTemp = "true".equals(gribNode.getAttribute("display-TEMP-contour"));
              displayContourPrate = "true".equals(gribNode.getAttribute("display-PRATE-contour"));
  
              display3DTws = "true".equals(gribNode.getAttribute("display-TWS-3D"));
              display3DPrmsl = "true".equals(gribNode.getAttribute("display-PRMSL-3D"));
              display3D500mb = "true".equals(gribNode.getAttribute("display-500HGT-3D"));
              display3DWaves = "true".equals(gribNode.getAttribute("display-WAVES-3D"));
              display3DTemperature = "true".equals(gribNode.getAttribute("display-TEMP-3D"));
              display3DRain = "true".equals(gribNode.getAttribute("display-PRATE-3D"));
            }
            String wo = gribNode.getAttribute("wind-only"); // deprecated
            if (wo.trim().length() > 0)
              windOnly = Boolean.valueOf(wo);
            String wc = gribNode.getAttribute("with-contour"); // deprecated
            if (wc.trim().length() > 0)
              displayContourLines = Boolean.valueOf(wc);
            
            String inLine = gribNode.getAttribute("in-line");
            if (inLine != null && inLine.trim().equals("true"))
            {
              // Deserialize
    //        String gribRequest = gribNode.getAttribute("in-line-request");
    //        XMLElement gribContent = (XMLElement)gribNode.getChildrenByTagName("java").item(0);
    //        StringWriter sw = new StringWriter();
    //        gribContent.print(sw);
              
              gribFileName = gribNode.getFirstChild().getNodeValue(); 
              WWContext.getInstance().fireProgressing(WWGnlUtilities.buildMessage("restoring-grib"));
              String displayFileName = gribFileName;
              InputStream is = null;
              if (fromArchive)
              {
                if (gribFileName.startsWith(WWContext.WAZ_PROTOCOL_PREFIX))
                  gribFileName = gribFileName.substring(WWContext.WAZ_PROTOCOL_PREFIX.length());
                is = waz.getInputStream(waz.getEntry(gribFileName));
              }
              else
                is = new FileInputStream(new File(gribFileName));
              
    //        XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(sw.toString().getBytes()));
              XMLDecoder decoder = new XMLDecoder(is);
              Object o = decoder.readObject();
              if (o instanceof GribFile)
              {
                GribFile gf = (GribFile)o;
                WWContext.getInstance().setGribFile(gf);
                List<GribHelper.GribConditionData> agcd = GribHelper.dumper(gf, displayFileName);
                GribHelper.GribConditionData wgd[] = agcd.toArray(new GribHelper.GribConditionData[agcd.size()]);
                setGribData(wgd, displayFileName);
              }
              else // For backward compatibility. 27-Feb-2009
              {
                GribHelper.GribConditionData wgd[] = (GribHelper.GribConditionData[])o;
      //        setGribData(wgd, gribRequest);
                setGribData(wgd, displayFileName);
              }
            }
            else
            {
              gribFileName = gribNode.getFirstChild().getNodeValue(); 
              WWContext.getInstance().fireProgressing(WWGnlUtilities.buildMessage("restoring-grib"));
              String displayFileName = gribFileName;
              GribHelper.GribConditionData wgd[] = null;
              if (fromArchive)
              {
                if (gribFileName.startsWith(WWContext.WAZ_PROTOCOL_PREFIX))
                  gribFileName = gribFileName.substring(WWContext.WAZ_PROTOCOL_PREFIX.length());
                synchronized (this)
                {
                  InputStream is = waz.getInputStream(waz.getEntry(gribFileName));       
                  try
                  {
                    wgd = GribHelper.getGribData(is, gribFileName);
                  }
                  catch (RuntimeException rte)
                  {
                    String mess = rte.getMessage();
//                  System.out.println("RuntimeException getMessage(): [" + mess + "]");
                    if (mess.startsWith("DataArray (width) size mismatch"))
                      System.out.println(mess);
                    else
                      throw rte;
                  }
                  is.close();           
                }
              }
              else
                wgd = GribHelper.getGribData(gribFileName, true);
              
              setGribData(wgd, displayFileName);
            }
            nbComponents++;
          }
          catch (Exception ex)  
          { 
    //      StaticObjects.getInstance().fireLogging("No GRIB node..."); 
            unsetGribData();
          }
        }
        setWindOnly(windOnly);
        // Now done above.
//        chartPanel.setW(w);
//        chartPanel.setH(h);
//        chartPanel.setBounds(0,0,w,h);
        
        // GPX Data?
        if (withBoatAndTrack || option.equals(TwoFilePanel.EVERY_THING))
        {
          try 
          { 
            NodeList gpxList = doc.selectNodes("//gpx-data/gpx-point");
            int nl = gpxList.getLength();
            if (nl == 0)
              gpxData = null;
            else
            {
              gpxData = new ArrayList<GeoPoint>(nl);
              for (int i=0; i<nl; i++)
              {
                XMLElement gpx = (XMLElement)gpxList.item(i);
                GeoPoint gp = new GeoPoint(Double.parseDouble(gpx.getAttribute("lat")),
                                           Double.parseDouble(gpx.getAttribute("lng")));
                gpxData.add(gp);
              }
              nbComponents++;
            }            
          }
          catch (Exception ex)
          {
            ex.printStackTrace();   
          }
        }
        
        if (xScroll != 0 || yScroll != 0)
        {
          chartPanelScrollPane.getViewport().setViewPosition(new Point(xScroll, yScroll));
        }
      }
      catch (Exception e)
      {
        WWContext.getInstance().fireExceptionLogging(e);
        e.printStackTrace();
      }    
    }
    return nbComponents;
  }
  
  public void createFromPattern(String fileName)
  {
    try
    {
      DOMParser parser = WWContext.getInstance().getParser();
      synchronized(parser)
      {
        parser.setValidationMode(DOMParser.NONVALIDATING);
        URL patternURL = null;
        try
        {
          if (fileName.startsWith("http://"))
            patternURL = new URL(fileName);
          else
            patternURL = new File(fileName).toURI().toURL();
          parser.parse(patternURL); 
        }
        catch (FileNotFoundException fnfe) // Possibly happens at startup?
        {
          JOptionPane.showMessageDialog(instance, fnfe.toString(), "Loading Pattern", JOptionPane.ERROR_MESSAGE);
          return;
        }
        catch (Exception other)
        {
          System.out.println("restoreFromPattern:" + other.toString());
          other.printStackTrace();
          // Dump the file
          File f = new File(fileName);
          if (f.exists())
          {
            try { System.out.println(f.toURI().toString()); }
            catch (Exception e) { System.out.println("...No URI " + e.toString()); }
            try { System.out.println(f.toURI().toURL().toString()); }
            catch (Exception e) { System.out.println("...No URL " + e.toString()); }
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = "";
            while ((line = br.readLine()) != null)
            {
              System.out.println(line);
            }
          }
          else
            System.out.println("File [" + fileName + "] does not exist...");
          System.out.println("Cancelling...");
          WWContext.getInstance().fireInterruptProcess();
          JOptionPane.showMessageDialog(instance, other.toString(), "Loading Pattern", JOptionPane.ERROR_MESSAGE);
          return;
        }
        XMLDocument doc = parser.getDocument();
        String projType = ((XMLElement)doc.selectNodes("//projection").item(0)).getAttribute("type");
        if (projType.equals(MERCATOR))
        {
          chartPanel.setProjection(ChartPanel.MERCATOR);
          WWContext.getInstance().fireSetProjection(ChartPanel.MERCATOR);
        }
        else if (projType.equals(ANAXIMANDRE))
        {
          chartPanel.setProjection(ChartPanel.ANAXIMANDRE);
          WWContext.getInstance().fireSetProjection(ChartPanel.ANAXIMANDRE);
        }
        else if (projType.equals(LAMBERT))
        {
          chartPanel.setProjection(ChartPanel.LAMBERT);
          double cp = Double.parseDouble(((XMLElement)doc.selectNodes("//projection").item(0)).getAttribute("contact-parallel"));
          chartPanel.setContactParallel(cp);
          WWContext.getInstance().fireSetProjection(ChartPanel.LAMBERT);
          WWContext.getInstance().fireSetContactParallel(cp);
        }
        else if (projType.equals(GLOBE))
        {
          chartPanel.setProjection(ChartPanel.GLOBE_VIEW);
          WWContext.getInstance().fireSetProjection(ChartPanel.GLOBE_VIEW);
          // FIXME Globe parameters
  //      StaticObjects.getInstance().fireSetGlobeParameters();
        }
        else if (projType.equals(SATELLITE))
        {
          chartPanel.setProjection(ChartPanel.SATELLITE_VIEW);
          WWContext.getInstance().fireSetProjection(ChartPanel.SATELLITE_VIEW);
          double nl  = Double.parseDouble(((XMLElement)doc.selectNodes("//projection").item(0)).getAttribute("nadir-latitude"));
          double ng  = Double.parseDouble(((XMLElement)doc.selectNodes("//projection").item(0)).getAttribute("nadir-longitude"));
          double alt = Double.parseDouble(((XMLElement)doc.selectNodes("//projection").item(0)).getAttribute("altitude"));
          boolean opaque = new Boolean(((XMLElement)doc.selectNodes("//projection").item(0)).getAttribute("opaque")).booleanValue();
          chartPanel.setSatelliteAltitude(alt);
          chartPanel.setSatelliteLatitude(nl);
          chartPanel.setSatelliteLongitude(ng);
          chartPanel.setTransparentGlobe(!opaque);
          WWContext.getInstance().fireSetSatelliteParameters(nl, ng, alt, opaque);
        }
        else if  (projType.equals(STEREO))
        {
          chartPanel.setProjection(ChartPanel.STEREOGRAPHIC);
          WWContext.getInstance().fireSetProjection(ChartPanel.STEREOGRAPHIC);
        }
        else if  (projType.equals(POLAR_STEREO))
        {
          chartPanel.setProjection(ChartPanel.POLAR_STEREOGRAPHIC);
          WWContext.getInstance().fireSetProjection(ChartPanel.POLAR_STEREOGRAPHIC);
        }
        nLat = Double.parseDouble(doc.selectNodes("//north").item(0).getFirstChild().getNodeValue());
        sLat = Double.parseDouble(doc.selectNodes("//south").item(0).getFirstChild().getNodeValue());
        wLong = Double.parseDouble(doc.selectNodes("//west").item(0).getFirstChild().getNodeValue());
        eLong = Double.parseDouble(doc.selectNodes("//east").item(0).getFirstChild().getNodeValue());
        int w = Integer.parseInt(doc.selectNodes("//chartwidth").item(0).getFirstChild().getNodeValue());
        int h = Integer.parseInt(doc.selectNodes("//chartheight").item(0).getFirstChild().getNodeValue());

        int xScroll = 0, yScroll = 0;
        try
        {
          xScroll = Integer.parseInt(((XMLElement)doc.selectNodes("//scroll").item(0)).getAttribute("x"));
          yScroll = Integer.parseInt(((XMLElement)doc.selectNodes("//scroll").item(0)).getAttribute("y"));
        }
        catch (Exception ignore) { }
        
        try
        {
          XMLElement faxOption = (XMLElement)(doc.selectNodes("//fax-option").item(0));
          String opt = faxOption.getAttribute("value");
          if (opt.equals("CHECKBOX"))
            setCheckBoxPanelOption(CHECKBOX_OPTION);
          else if (opt.equals("RADIOBUTTON"))
            setCheckBoxPanelOption(RADIOBUTTON_OPTION);
          else
            System.out.println("Unknown option [" + opt + "]");
        }
        catch (Exception ignore) 
        {
        }        
        
        if (chartPanel.getProjection() != ChartPanel.GLOBE_VIEW &&
            chartPanel.getProjection() != ChartPanel.SATELLITE_VIEW)
          chartPanel.setWidthFromChart(nLat, sLat, wLong, eLong);
  //    eLong = chartPanel.calculateEastG(nLat, sLat, wLong);
        chartPanel.setEastG(eLong);
        chartPanel.setWestG(wLong);
        chartPanel.setNorthL(nLat);
        chartPanel.setSouthL(sLat);

        chartPanel.setW(w);
        chartPanel.setH(h);
        chartPanel.setBounds(0,0,w,h);

        chartPanel.repaint();  
        
        try 
        { 
          NodeList faxes = doc.selectNodes("//fax-collection/fax");
          faxImage = new FaxImage[faxes.getLength()];
          FaxType[] ft = new FaxType[faxes.getLength()];
          for (int i=0; i<faxes.getLength(); i++)
          {
            XMLElement fax = (XMLElement)faxes.item(i);
            // Dynamic pattern?
            XMLElement dynamic = null;
            try { dynamic = (XMLElement)fax.selectNodes("dynamic-resource").item(0); }
            catch (Exception ignore) { ignore.printStackTrace(); }
            String faxName = "";
            String hintName = fax.getAttribute("hint");
            if (dynamic != null)
            {
              final String url = dynamic.getAttribute("url");
              if (url.toUpperCase().startsWith("HTTP://"))
              {
                boolean ok2go = WWContext.getInstance().isOnLine();
                if (!ok2go)
                {
    //            String mess = GnlUtilities.buildMessage("you-are-not-on-line", new String[] { url });
                  int resp = JOptionPane.showConfirmDialog(this, 
                                                           WWGnlUtilities.buildMessage("you-are-not-on-line", 
                                                                                        new String[] { url }), 
                                                           WWGnlUtilities.buildMessage("dynamic-pattern"), 
                                                           JOptionPane.YES_NO_OPTION, 
                                                           JOptionPane.QUESTION_MESSAGE);                            
                  if (resp == JOptionPane.YES_OPTION)
                    ok2go = true;
                }
                if (ok2go)
                {
                  WWContext.getInstance().fireProgressing("Loading Fax " + hintName);
                  String dir = dynamic.getAttribute("dir");
                  String prefix = dynamic.getAttribute("prefix");
                  String pattern = dynamic.getAttribute("pattern");
                  String ext = dynamic.getAttribute("extension");
                  Date now = new Date();
                  dir = WWGnlUtilities.translatePath(dir, now).replace('/', File.separatorChar);
                  SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                  faxName = dir + File.separator + prefix + sdf.format(now) + "." + ext;
                  
                  File faxDir = new File(dir);
                  if (!faxDir.exists())
                    faxDir.mkdirs();
                  try
                  {
                    WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("loading2", new String[] { url }) + "\n", LoggingPanel.WHITE_STYLE);
                    HTTPClient.getChart(url, dir, faxName, true);
                    WWContext.getInstance().fireReloadFaxTree();
                  }
                  catch (Exception ex)
                  {
  //                System.out.println("HTTPClient.getChart interrupted..., returning.");
                    WWContext.getInstance().fireInterruptProcess();
                    return;
                  }
                }
              }
              else // Non HTTP protocols
              {
                // Non http protocols! 
                if (url.startsWith(SearchUtil.SEARCH_PROTOCOL)) // (local search, for SailMail)               
                {
                  // Parse Expression, like search:chartview.util.SearchUtil.findMostRecentFax(pattern, rootPath)
                  faxName = SearchUtil.dynamicSearch(url);
                  // System.out.println("For " + hintName + ", search: found [" + faxName + "]");
                }
                else if (url.startsWith(WWContext.INTERNAL_RESOURCE_PREFIX)) // like Backgrounds
                {
                  String internStr = url.substring(WWContext.INTERNAL_RESOURCE_PREFIX.length());
                  if (internStr.equals(WWContext.BG_MERCATOR_GREENWICH_CENTERED_ALIAS))
                    internStr = WWContext.BG_MERCATOR_GREENWICH_CENTERED; 
                  else if (internStr.equals(WWContext.BG_MERCATOR_ANTIMERIDIAN_CENTERED_ALIAS))
                    internStr = WWContext.BG_MERCATOR_ANTIMERIDIAN_CENTERED; 
                  else if (internStr.equals(WWContext.BG_MERCATOR_NE_ATLANTIC_ALIAS))
                    internStr = WWContext.BG_MERCATOR_NE_ATLANTIC; 
                  URL intern = new URL(internStr);
                  Image image = null;
                  try
                  {
                    image = ImageIO.read(intern);
                    File temp = File.createTempFile("resource.bg.", ".png");
                    ImageIO.write(ImageUtil.toBufferedImage(image), "png", temp);  
                    faxName = temp.getAbsolutePath();
                    temp.deleteOnExit();
                  }
                  catch(Exception e)
                  {
                    System.err.println("For URL: [" + url + "] => [" + internStr + "]");
                    WWContext.getInstance().fireExceptionLogging(e);
                    e.printStackTrace();
                  }                 
                }
              }
            }
            else // Non dynamic, prompt the user.
            {
              // Prompt for the file          
              String firstDir = ((ParamPanel.DataPath) ParamPanel.data[ParamData.FAX_FILES_LOC][ParamData.VALUE_INDEX]).toString().split(File.pathSeparator)[0];
              faxName = WWGnlUtilities.chooseFile(instance, JFileChooser.FILES_ONLY, 
                                                  new String[] { "gif", "jpg", "jpeg", "tif", "tiff", "png" }, 
                                                  hintName, 
                                                  firstDir, 
                                                  WWGnlUtilities.buildMessage("open"),
                                                  hintName,
                                                  true);
            }             
            // Fax identified, loading.
            if (faxName.trim().length() > 0)
            {
              Color c = WWGnlUtilities.buildColor(fax.getAttribute("color"));
              if (false)
                System.out.println("For " + faxName + ", color=" + WWGnlUtilities.colorToString(c));
              String strRatio = fax.getAttribute("wh-ratio");
              String transparentStr = fax.getAttribute("transparent");
              String colorChangeStr = fax.getAttribute("color-change");
              if (strRatio.trim().length() > 0)
              {
                try { whRatio = Double.parseDouble(strRatio); } 
                catch (Exception ignore) { ignore.printStackTrace(); }
              }
              else
                whRatio = 1D;
              double imageScale = Double.parseDouble(fax.selectNodes("./faxScale").item(0).getFirstChild().getNodeValue());
              int imageHOffset  = Integer.parseInt(fax.selectNodes("./faxXoffset").item(0).getFirstChild().getNodeValue());
              int imageVOffset  = Integer.parseInt(fax.selectNodes("./faxYoffset").item(0).getFirstChild().getNodeValue());
              double imageRotation  = 0D;
              try { imageRotation = Double.parseDouble(fax.selectNodes("./faxRotation").item(0).getFirstChild().getNodeValue()); } 
              catch (Exception ignore) { System.err.println("Rotation:" + ignore.getLocalizedMessage()); }
              faxImage[i] = new FaxImage();
              faxImage[i].fileName = faxName;
              faxImage[i].color = c;
              faxImage[i].imageScale   = imageScale;
              faxImage[i].imageHOffset = imageHOffset;
              faxImage[i].imageVOffset = imageVOffset;
              faxImage[i].imageRotationAngle = imageRotation;
              faxImage[i].comment = WWGnlUtilities.getHeader(faxName);
              String faxOrigin = "";
              try { faxOrigin = ((XMLElement)fax.selectNodes("./dynamic-resource").item(0)).getAttribute("url") ; } 
              catch (Exception ignore) { ignore.printStackTrace(); }
              faxImage[i].faxOrigin = faxOrigin;
              if (faxImage[i].comment.equals(faxName))
              {
                String hint = fax.getAttribute("hint");
                if (hint.trim().length() > 0)
                {
                  faxImage[i].comment = hint;
                  faxImage[i].faxTitle = hint;
                }
              }
              if (faxImage[i].comment.trim().length() > 0 && (faxImage[i].faxTitle == null || faxImage[i].faxTitle.trim().length() == 0))
                faxImage[i].faxTitle = faxImage[i].comment;
              
              faxImage[i].show = true;
              if (transparentStr == null || transparentStr.trim().length() == 0)
                faxImage[i].transparent = true;
              else
                faxImage[i].transparent = transparentStr.equals("true");
              
              if (colorChangeStr == null || colorChangeStr.trim().length() == 0)
                faxImage[i].colorChange = true;
              else
                faxImage[i].colorChange = colorChangeStr.equals("true");
              
              try
              {
                if (faxImage[i].transparent)
                {
                  if (faxImage[i].colorChange)
                  {
               //   faxImage[i].faxImage = ImageUtil.makeTransparentImage(this, ImageUtil.readImage(faxName), c);
               //   faxImage[i].faxImage = ImageUtil.switchColorAndMakeColorTransparent(ImageUtil.readImage(faxName), Color.black, c, Color.white, blurSharpOption);
                    Image faxImg = ImageUtil.readImage(faxName);
                    if (ImageUtil.countColors(faxImg) > 2)
                      faxImg = ImageUtil.switchAnyColorAndMakeColorTransparent(faxImg, c, ImageUtil.mostUsedColor(faxImg), blurSharpOption);
                    else
                      faxImg = ImageUtil.switchColorAndMakeColorTransparent(faxImg, Color.black, c, Color.white, blurSharpOption);
                    faxImage[i].faxImage = faxImg;
                  }
                  else
                    faxImage[i].faxImage = ImageUtil.makeColorTransparent(ImageUtil.readImage(faxName), Color.white, blurSharpOption);
                }
                else
                  faxImage[i].faxImage = ImageUtil.readImage(faxName);
              }
              catch (Exception e)
              {
                WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("file-not-found", new String[] { faxName }) + "\n");
                WWContext.getInstance().fireInterruptProcess();
                return;
              }
              ft[i] = new FaxType(faxName, c, Boolean.valueOf(true), Boolean.valueOf(true), imageRotation, faxOrigin, faxName);
              ft[i].setRank(i+1);
              ft[i].setComment(faxImage[i].comment);
              repaint();
            }
            else
              return; // Bye!
          }
          setCheckBoxes(ft);
          WWContext.getInstance().fireFaxLoaded();
          WWContext.getInstance().fireFaxesLoaded(ft);
        }
        catch (Exception ex) 
        {
          WWContext.getInstance().fireExceptionLogging(ex);
          ex.printStackTrace();
          unsetFaxImage();
        }
        try 
        { 
          XMLElement gribNode = (XMLElement)doc.selectNodes("//grib").item(0);
          
          String twsData = gribNode.getAttribute("display-TWS-Data");
          if (twsData.trim().length() > 0) // New version
          {
            displayTws = "true".equals(twsData);
            displayPrmsl = "true".equals(gribNode.getAttribute("display-PRMSL-Data"));
            display500mb = "true".equals(gribNode.getAttribute("display-500HGT-Data"));
            displayWaves = "true".equals(gribNode.getAttribute("display-WAVES-Data"));
            displayTemperature = "true".equals(gribNode.getAttribute("display-TEMP-Data"));
            displayRain = "true".equals(gribNode.getAttribute("display-PRATE-Data"));
            
            displayContourTWS = "true".equals(gribNode.getAttribute("display-TWS-contour"));
            displayContourPRMSL = "true".equals(gribNode.getAttribute("display-PRMSL-contour"));
            displayContour500mb = "true".equals(gribNode.getAttribute("display-500HGT-contour"));
            displayContourWaves = "true".equals(gribNode.getAttribute("display-WAVES-contour"));
            displayContourTemp = "true".equals(gribNode.getAttribute("display-TEMP-contour"));
            displayContourPrate = "true".equals(gribNode.getAttribute("display-PRATE-contour"));
          
            display3DTws = "true".equals(gribNode.getAttribute("display-TWS-3D"));
            display3DPrmsl = "true".equals(gribNode.getAttribute("display-PRMSL-3D"));
            display3D500mb = "true".equals(gribNode.getAttribute("display-500HGT-3D"));
            display3DWaves = "true".equals(gribNode.getAttribute("display-WAVES-3D"));
            display3DTemperature = "true".equals(gribNode.getAttribute("display-TEMP-3D"));
            display3DRain = "true".equals(gribNode.getAttribute("display-PRATE-3D"));
          }
          String wo = gribNode.getAttribute("wind-only"); // deprecated
          if (wo.trim().length() > 0)
          {
            setWindOnly(Boolean.valueOf(wo));
            if (!isWindOnly())
            {
              setDisplayPrmsl(true);
              setDisplay500mb(true);
              setDisplayWaves(true);
              setDisplayTemperature(true);
              setDisplayRain(true);
            }
          }
          String wc = gribNode.getAttribute("with-contour"); // deprecated
          if (wc.trim().length() > 0)
            displayContourLines = Boolean.valueOf(wc);
          gribRequest = "";
          if (gribNode.selectNodes("dynamic-grib").getLength() == 0)
          {
            String gribHint = gribNode.getFirstChild().getNodeValue();
            if (gribHint.trim().length() > 0)
            {
              String firstDir = ((ParamPanel.DataPath) ParamPanel.data[ParamData.GRIB_FILES_LOC][ParamData.VALUE_INDEX]).toString().split(File.pathSeparator)[0];
              String grib = WWGnlUtilities.chooseFile(instance, JFileChooser.FILES_ONLY, 
                                                    new String[] { "grb", "grib" }, gribHint, 
                                                    firstDir, WWGnlUtilities.buildMessage("open"), 
                                                    gribHint);
              if (grib != null && grib.trim().length() > 0)
              {
                gribFileName = grib;
                GribHelper.GribConditionData wgd[] = GribHelper.getGribData(gribFileName, true);
                setGribData(wgd, gribFileName);
              }
              else
                gribFileName = "";
            }
          }
          else // GRIB from Saildocs
          {
            WWContext.getInstance().fireProgressing(WWGnlUtilities.buildMessage("loading-grib"));
            XMLElement saildocs = (XMLElement)gribNode.selectNodes("dynamic-grib").item(0);
            String request      = saildocs.getAttribute("request");
            gribRequest = request;
            if (gribRequest.startsWith(SearchUtil.SEARCH_PROTOCOL)) // From the disc             
            {
              // Parse Expression, like search:chartview.util.SearchUtil.findMostRecentFax(pattern, rootPath)
              gribFileName = SearchUtil.dynamicSearch(gribRequest);
              // System.out.println("For " + hintName + ", search: found [" + gribFileName + "]");
              if (gribFileName != null && gribFileName.trim().length() > 0)
              {
                GribHelper.GribConditionData wgd[] = GribHelper.getGribData(gribFileName, true);
                setGribData(wgd, gribFileName);
              }
              else
                gribFileName = "";
            }
            else // From the Web
            {
              String gribDir     = saildocs.getAttribute("dir");
              String girbPrefix  = saildocs.getAttribute("prefix");
              String gribPattern = saildocs.getAttribute("pattern");
              String gribExt     = saildocs.getAttribute("extension");
              Date now = new Date();
              gribDir = WWGnlUtilities.translatePath(gribDir, now);
              SimpleDateFormat sdf = new SimpleDateFormat(gribPattern);
              gribFileName = gribDir + File.separator + girbPrefix + sdf.format(now) + "." + gribExt;
  
              WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("loading2", new String[] { request }) + "\n", LoggingPanel.WHITE_STYLE);
              File dir = new File(gribDir);
              if (!dir.exists())
                dir.mkdirs();
              try
              {
                byte[] gribContent = HTTPClient.getGRIB(WWGnlUtilities.generateGRIBRequest(request), gribDir, gribFileName, true);
                WWContext.getInstance().fireReloadGRIBTree();
                GribHelper.GribConditionData wgd[] = GribHelper.getGribData(new ByteArrayInputStream(gribContent), request);
                setGribData(wgd, gribFileName); 
              }
              catch (Exception ex)
              {
                ex.printStackTrace();
                WWContext.getInstance().fireInterruptProcess();
                return;
              }
            }
          }
        }
        catch (Exception ex)  
        { 
  //      StaticObjects.getInstance().fireLogging("No GRIB node..."); 
          unsetGribData();
          gribRequest = "";
        }
        // Now done first
//        chartPanel.setW(w);
//        chartPanel.setH(h);
//        chartPanel.setBounds(0,0,w,h);

        if (xScroll != 0 || yScroll != 0)
        {
          chartPanelScrollPane.getViewport().setViewPosition(new Point(xScroll, yScroll));
        }
        // Done. Should we save it?
        System.out.println("-- Composite [" + fileName + "] created. Saving?");
//      System.out.println("Default Composite: [" + ((ParamPanel.DataFile) ParamPanel.data[ParamData.LOAD_COMPOSITE_STARTUP][ParamData.VALUE_INDEX]).toString() + "]");
        boolean autoSaveDefaultComposite = ((String)ParamPanel.data[ParamData.AUTO_SAVE_DEFAULT_COMPOSITE][ParamData.VALUE_INDEX]).trim().length() > 0;
        if (((ParamPanel.DataFile) ParamPanel.data[ParamData.LOAD_COMPOSITE_STARTUP][ParamData.VALUE_INDEX]).toString().equals(fileName) && autoSaveDefaultComposite)
        {
          try
          {
            System.out.println("-- Created from [" + fileName + "]. Saving!");
            String compositeDir = ((ParamPanel.DataDirectory)ParamPanel.data[ParamData.COMPOSITE_ROOT_DIR][ParamData.VALUE_INDEX]).toString();
            String bigPattern = ((String)ParamPanel.data[ParamData.AUTO_SAVE_DEFAULT_COMPOSITE][ParamData.VALUE_INDEX]);
            String[] patternElements = bigPattern.split("\\|");
            String dir = compositeDir + patternElements[0].trim(); // "/yyyy/MM-MMM";
            String prefix = patternElements[1].trim();             // "Auto_";
            String pattern =patternElements[2].trim();             // "yyyy_MM_dd_HH_mm_ss_z";
            String ext = patternElements[3].trim();                // "waz";
            Date now = new Date();
            dir = WWGnlUtilities.translatePath(dir, now).replace('/', File.separatorChar);
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            String saveAsName = dir + File.separator + prefix + sdf.format(now) + "." + ext;
            
            File faxDir = new File(dir);
            if (!faxDir.exists())
              faxDir.mkdirs();
            
            runStorageThread(false, saveAsName);
            System.out.println("-- Saved as [" + saveAsName + "]");
          }
          catch (Exception ex)
          {
            String message = "Error: " + ex.getLocalizedMessage() + "\nfor [" + 
                            ((String)ParamPanel.data[ParamData.AUTO_SAVE_DEFAULT_COMPOSITE][ParamData.VALUE_INDEX]) + "]";
            JOptionPane.showMessageDialog(this, message, "Auto-save", JOptionPane.ERROR_MESSAGE);            
          }
        }
      }
    }
    catch (Exception e)
    {
      WWContext.getInstance().fireExceptionLogging(e);
      e.printStackTrace();
    }    
  }
  
  private void displayStatus()
  {
    String mess = "";
    mess = "N:" + WWGnlUtilities.XX14.format(nLat) + " " +
           "S:" + WWGnlUtilities.XX14.format(sLat) + " " +
           "W:" + WWGnlUtilities.XX14.format(wLong) + " " +
           "E:" + WWGnlUtilities.XX14.format(eLong) + " " +
           "w=" + chartPanel.getWidth() + " " + 
           "h=" + chartPanel.getHeight() + "    topleft:" +
           "x=" + chartPanelScrollPane.getViewport().getViewPosition().x + 
         ", y=" + chartPanelScrollPane.getViewport().getViewPosition().y;

    statusLabel.setText(mess);    
  }
  
  private boolean isVisible(double l, double g)
  {
    boolean plot = true;
    if (chartPanel.getProjection() == ChartPanelInterface.GLOBE_VIEW)
    {
      if (!chartPanel.isTransparentGlobe() && chartPanel.isBehind(l, g - chartPanel.getGlobeViewLngOffset()))
        plot = false;
    }
    else if (chartPanel.getProjection() == ChartPanelInterface.SATELLITE_VIEW)
    {
      if (!chartPanel.isTransparentGlobe() && chartPanel.isBehind(l, g))
        plot = false;
    }
    else if (chartPanel.getProjection() == ChartPanelInterface.POLAR_STEREOGRAPHIC)
      plot = chartPanel.contains(new GeoPoint(l, g));
    return plot;
  }
  
  private final boolean doItAfter = true;
  private boolean smoothingRequired = true;
  // GRIB Data
  private transient GribHelper.GribConditionData gribData = null;
  
  public void chartPanelPaintComponentFeature(final Graphics gr)
  {
    ((Graphics2D)gr).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);      
    ((Graphics2D)gr).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                      RenderingHints.VALUE_ANTIALIAS_ON);      
//  System.out.println("chartPanelPaintComponentFeature");
//  Color before = gr.getColor();
//  gr.setColor(this.getBackground());
//  gr.fillRect(0, 0, this.getWidth(), this.getHeight());
//  gr.setColor(before);
    
    // Set Wind Options
    if (WWContext.getInstance().getUseColorRangeForWindSpeed().booleanValue())
    {
      drawWindColorBackground = true;
    }
    
    // No Faxes, No GRIB
    if ((getFaxes() == null || getFaxes().length == 0) && (wgd == null || wgd.length == 0))
    {
      Color origColor = gr.getColor();
      Font origFont = gr.getFont();
      // Display the banner
      Point pt = new Point(5, chartPanel.getVisibleRect().height / 2);
      // Transparency
      float alpha = 0.75f;
      ((Graphics2D)gr).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
      gr.setColor(Color.orange);
      Font font = gr.getFont();
      gr.setFont(new Font(font.getName(), Font.BOLD | Font.ITALIC, 30));
      int y = pt.y - 2, x = 15;
      gr.drawString(WWGnlUtilities.buildMessage("welcome"), 
                    x, 
                    y);
      gr.setFont(new Font(font.getName(), Font.BOLD | Font.ITALIC, 14));
      y += 16;
      gr.drawString(WWGnlUtilities.buildMessage("more-welcome-1"), 
                    x, 
                    y);
      y += 16;
      // etc...    
      
      gr.setColor(origColor);
      gr.setFont(origFont);
    }
    
//  double ratio = (double)chartPanel.getWidth() / (double)this.getWidth();
//  System.out.println("Ratio:" + ratio);
    
    Graphics2D g2d = null;
    if (gr instanceof Graphics2D)
      g2d = (Graphics2D)gr;
    
    // Transparency
    float alpha = faxUserOpacity; // ((Float) ParamPanel.data[ParamData.FAX_TRANSPARENCY][ParamData.VALUE_INDEX]).floatValue();
//  System.out.println("Transparency set to " + Float.toString(alpha));
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

    // Faxes - Non transparent only
    AffineTransform origTx = g2d.getTransform();
    for (int i=0; faxImage != null && i<faxImage.length; i++)
    {
      AffineTransform tx = new AffineTransform();
      Image img = null;
      boolean show = true;
      boolean transparentFax = true;
      try 
      { 
        img  = faxImage[i].faxImage; 
        show = faxImage[i].show;
        transparentFax = faxImage[i].transparent;
        if (transparentFax)
          continue;
//      System.out.println("Image:" + (img==null?"NULL":"ok") + " show:" + show);
      } 
      catch (NullPointerException npe)
      {
        if (false) System.out.print("-npe[1] (i=" + i + ")-");;
      }
      catch (Exception ex) 
      {
        WWContext.getInstance().fireLogging("Oops - 2:" + ex.getClass().getName() + "\n");
      }
      if (img != null && show)
      {
        int h = img.getHeight(this);
        int w = img.getWidth(this);
        
        double add2HOffset = 0D, add2VOffset = 0D;
    //  double scale = chartPanel.getPreferredSize().getWidth() / (double)weatherFaxImage.getWidth(null);
    //  layer.setPreferredSize(chartPanel.getPreferredSize());
        tx.scale(faxImage[i].imageScale * whRatio, faxImage[i].imageScale);
        tx.translate((double)faxImage[i].imageHOffset + (add2HOffset * faxImage[i].imageScale), 
                     (double)faxImage[i].imageVOffset + (add2VOffset * faxImage[i].imageScale));
        // IMPORTANT: Rotation is the *last* transformation
        if (faxImage[i].imageRotationAngle != 0D)
        {
          double radians = Math.toRadians(faxImage[i].imageRotationAngle);
          tx.rotate(radians, w/2, h/2);
        }

//      tx.scale(1D, 1D);
        if (!faxImage[i].transparent) // Then reset transparency
          g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.drawImage(img, tx, this);
        if (!faxImage[i].transparent) // Then set it back
          g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        // If fax is *not* transparent, set it to false      
        chartPanel.setCleanFirst(false); 
        g2d.setTransform(origTx);
      }
    }
    // Reset transparency
    alpha = 1.0f;
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

    chartPanel.setCleanFirst(true);
     
    // If there was any opaque faxes, then draw the grid again 
    if (thereIsAnOpaqueFax() && chartPanel.isWithGrid()) 
      chartPanel.redrawGrid(gr);
        
    if (wgd != null && drawGRIB)
    {
//    System.out.println("GRIBIndex:" + gribIndex);
      if (gribIndex == -1) 
      {
        gribIndex = 0;
        smoothingRequired = true;
      }
//    System.out.println("Before: GribIndex:" + gribIndex + ", gribData is " + (gribData==null?"":"not ") + "null, smootingRequired " + smoothingRequired + ", smooth:" + smooth);
      if (gribData == null || smoothingRequired)
      {
        gribData = wgd[gribIndex]; 
      }
      if (smooth > 1 && gribData != null && smoothingRequired)
      {
        gribData = GribHelper.smoothGribData(gribData, smooth); // Rebuilds all the GRIB Data
        smoothingRequired = false;
      }
      if (gribData != null) // Display the grib file
      {
        if (displayContourLines)
        {
          // Reset transparency for the contour lines
          g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
          
  //      System.out.println("After: GribIndex:" + gribIndex + ", gribData is " + (gribData==null?"":"not ") + "null, smootingRequired " + smoothingRequired + ", smooth:" + smooth);
          if (displayGribTWSContour && islandsTws != null && displayContourTWS)
          {
            WWGnlUtilities.drawIsoPoints(gr, chartPanel, islandsTws, (Color)ParamPanel.data[ParamData.GRIB_WIND_COLOR][ParamData.VALUE_INDEX], ((ParamPanel.ContourLinesList)ParamPanel.data[ParamData.ISO_TWS_LIST][ParamData.VALUE_INDEX]).getBoldIndexes()); 
       //   WWGnlUtilities.drawBumps(gr, chartPanel, twsBumps);
          }
          if (displayGribPRMSLContour && islandsPressure != null && displayContourPRMSL)
          {
            WWGnlUtilities.drawIsoPoints(gr, chartPanel, islandsPressure, (Color)ParamPanel.data[ParamData.PRMSL_CONTOUR][ParamData.VALUE_INDEX], ((ParamPanel.ContourLinesList)ParamPanel.data[ParamData.ISOBARS_LIST][ParamData.VALUE_INDEX]).getBoldIndexes()); 
            WWGnlUtilities.drawBumps(gr, chartPanel, prmslBumps); // Labels
          }
          if (displayGribWavesContour && islandsWave != null && displayContourWaves)
            WWGnlUtilities.drawIsoPoints(gr, chartPanel, islandsWave, (Color)ParamPanel.data[ParamData.WAVES_CONTOUR][ParamData.VALUE_INDEX], ((ParamPanel.ContourLinesList)ParamPanel.data[ParamData.ISOHEIGHTWAVES_LIST][ParamData.VALUE_INDEX]).getBoldIndexes()); 
          if (displayGrib500HGTContour && islands500mb != null && displayContour500mb)
          {
            WWGnlUtilities.drawIsoPoints(gr, chartPanel, islands500mb, (Color)ParamPanel.data[ParamData.MB500_CONTOUR][ParamData.VALUE_INDEX], ((ParamPanel.ContourLinesList)ParamPanel.data[ParamData.ISOHEIGHT500_LIST][ParamData.VALUE_INDEX]).getBoldIndexes());
            WWGnlUtilities.drawBumps(gr, chartPanel, hgt500Bumps);  // Labels
          }
          if (displayGribTempContour && islandsTemp != null && displayContourTemp)
            WWGnlUtilities.drawIsoPoints(gr, chartPanel, islandsTemp, (Color)ParamPanel.data[ParamData.TEMP_CONTOUR][ParamData.VALUE_INDEX], ((ParamPanel.ContourLinesList)ParamPanel.data[ParamData.ISOTEMP_LIST][ParamData.VALUE_INDEX]).getBoldIndexes());
          if (displayGribPrateContour && islandsPrate != null && displayContourPrate)
            WWGnlUtilities.drawIsoPoints(gr, chartPanel, islandsPrate, (Color)ParamPanel.data[ParamData.PRATE_CONTOUR][ParamData.VALUE_INDEX], ((ParamPanel.ContourLinesList)ParamPanel.data[ParamData.ISOPRATE_LIST][ParamData.VALUE_INDEX]).getBoldIndexes());
        }
        // Real points - from the GRIB
        String dataOption = (String)displayComboBox.getSelectedItem();
        if (dataOption == null) dataOption = "WIND";
        
//      System.out.println("-- GRIB Dimensions: h=" + gribData.getGribPointData().length + ", w=" + gribData.getGribPointData()[0].length);
                
        double gribStepX = gribData.getStepX(), gribStepY = gribData.getStepY();
//      System.out.println("GRIB Rendering: stepX=" + gribStepX + ", stepY=" + gribStepY);
        try
        {
          for (int h=0; gribData.getGribPointData() != null && h<gribData.getGribPointData().length; h++)
          {
            try
            {
              // TASK It seems that the next lines throws an NPE some times, or an IndexOutOfBoundsException...
              for (int w=0; gribData.getGribPointData()[h] != null && w<gribData.getGribPointData()[h].length; w++)
              {
                if (gribData.getGribPointData()[h][w] != null) // Border of the smoothed frame
                {
                  double lat = gribData.getGribPointData()[h][w].getLat();
                  double lng = gribData.getGribPointData()[h][w].getLng();
                  Point gp = chartPanel.getPanelPoint(lat, lng);
                  // Wind
                  double speed = 0D, dir = 0D;
                  if (gribData.getGribPointData()[h][w].getTwd() != -1D &&
                      gribData.getGribPointData()[h][w].getTws() != -1D)
                  {
                    speed = gribData.getGribPointData()[h][w].getTws();
                    dir = gribData.getGribPointData()[h][w].getTwd();
                  }
                  else
                  {
                    float x = gribData.getGribPointData()[h][w].getU();
                    float y = -gribData.getGribPointData()[h][w].getV();
                    speed = Math.sqrt(x * x + y * y);
                    speed *= 3.60D;
                    speed /= 1.852D;
                    dir = WWGnlUtilities.getDir(x, y);
                  }
                  // Current
                  double cSpeed = 0D, cDir = 0D;
                  if (gribData.getGribPointData()[h][w].getCdr() != -1D &&
                      gribData.getGribPointData()[h][w].getCsp() != -1D)
                  {
                    cSpeed = gribData.getGribPointData()[h][w].getCsp();
                    cDir = gribData.getGribPointData()[h][w].getCdr();
                  }
                  else
                  {
                    float x = gribData.getGribPointData()[h][w].getUOgrd();
                    float y = -gribData.getGribPointData()[h][w].getVOgrd();
                    cSpeed = Math.sqrt(x * x + y * y);
                    cSpeed *= 3.60D;
                    cSpeed /= 1.852D;
                    cDir = WWGnlUtilities.getDir(x, y);
                  }
      //          gr.setColor(getWindColor(speed));
                  // We have speed, direction, wind color
                  if ("WIND".equals(dataOption))
                  {
      //            gr.setColor(GnlUtilities.getWindColor(coloredWind, initialGribWindBaseColor, speed, false));
                    if (drawWindColorBackground)
                    {
                      double stpY = (gribStepY); // / (double)smooth);
                      double stpX = (gribStepX); //  / (double)smooth);
      
                      double topLeftLat = lat + (stpY / 2D);
                      double topLeftLng = lng - (stpX / 2D);
                      double bottomRightLat = topLeftLat - stpY;
                      double bottomRightLng = topLeftLng + stpX;
                      // TopLeft
                      Point tl = chartPanel.getPanelPoint(topLeftLat, topLeftLng);
                      // Bottom Right
                      Point br = chartPanel.getPanelPoint(bottomRightLat, bottomRightLng);
    
                      Point tr = null; // Top Right
                      Point bl = null; // Bottom Left
                      if (chartPanel.getProjection() != ChartPanel.ANAXIMANDRE &&
                          chartPanel.getProjection() != ChartPanel.MERCATOR)
                      {
                        tr = chartPanel.getPanelPoint(topLeftLat, bottomRightLng);
                        bl = chartPanel.getPanelPoint(bottomRightLat, topLeftLng);
                      }
    
                      WWGnlUtilities.drawWind(gr, 
                                              gp.x, 
                                              gp.y,                                       
                                              speed, 
                                              dir, 
                                              coloredWind, 
                                              initialGribWindBaseColor, 
                                              drawHeavyDot, 
                                              drawWindColorBackground, 
                                              displayWindSpeedValue, 
                                              useThickWind,
                                              tl, 
                                              br,
                                              tr,
                                              bl,
                                              gribUserOpacity);
                    }
                    else
                      WWGnlUtilities.drawWind(gr, 
                                            gp.x, 
                                            gp.y,                                       
                                            speed, 
                                            dir, 
                                            coloredWind, 
                                            initialGribWindBaseColor, 
                                            drawHeavyDot, 
                                            drawWindColorBackground, 
                                            displayWindSpeedValue,
                                            useThickWind,
                                            gribUserOpacity); 
                  }
                  else if ("AIRTMP".equals(dataOption))
                  {
                    double temperature = /*(double)*/(gribData.getGribPointData()[h][w].getAirtmp() - 273.6D);
                    gr.setColor(WWGnlUtilities.getTemperatureColor(temperature, boundaries[TEMPERATURE][1], boundaries[TEMPERATURE][0]));
                    // 
                    double topLeftLat = lat + (gribStepY / 2D);
                    double topLeftLng = lng - (gribStepX / 2D);
                    double bottomRightLat = topLeftLat - (gribStepY);
                    double bottomRightLng = topLeftLng + (gribStepX);
                    Point tl = chartPanel.getPanelPoint(topLeftLat, topLeftLng);
                    Point br = chartPanel.getPanelPoint(bottomRightLat, bottomRightLng);
                    Point tr = null; // Top Right
                    Point bl = null; // Bottom Left
                    if (chartPanel.getProjection() != ChartPanel.ANAXIMANDRE &&
                        chartPanel.getProjection() != ChartPanel.MERCATOR)
                    {
                      tr = chartPanel.getPanelPoint(topLeftLat, bottomRightLng);
                      bl = chartPanel.getPanelPoint(bottomRightLat, topLeftLng);
                    }
    
                    WWGnlUtilities.drawGRIBData(gr, gp.x, gp.y, tl, br, tr, bl, gribUserOpacity); 
                  }
                  else if ("500HGT".equals(dataOption))
                  {
                    double altitude = /*(double)*/(gribData.getGribPointData()[h][w].getHgt());
                    gr.setColor(WWGnlUtilities.get500mbColor(altitude, boundaries[HGT500][1], boundaries[HGT500][0]));
                    // 
                    double topLeftLat = lat + (gribStepY / 2D);
                    double topLeftLng = lng - (gribStepX / 2D);
                    double bottomRightLat = topLeftLat - (gribStepY);
                    double bottomRightLng = topLeftLng + (gribStepX);
                    Point tl = chartPanel.getPanelPoint(topLeftLat, topLeftLng);
                    Point br = chartPanel.getPanelPoint(bottomRightLat, bottomRightLng);
                    Point tr = null; // Top Right
                    Point bl = null; // Bottom Left
                    if (chartPanel.getProjection() != ChartPanel.ANAXIMANDRE &&
                        chartPanel.getProjection() != ChartPanel.MERCATOR)
                    {
                      tr = chartPanel.getPanelPoint(topLeftLat, bottomRightLng);
                      bl = chartPanel.getPanelPoint(bottomRightLat, topLeftLng);
                    }
    
                    WWGnlUtilities.drawGRIBData(gr, gp.x, gp.y, tl, br, tr, bl, gribUserOpacity);
                  }
                  else if ("WAVES".equals(dataOption))
                  {
                    // TASK What if array size is noyte is not the same?
                    double height = /*(double)*/(gribData.getGribPointData()[h][w].getWHgt());
                    gr.setColor(WWGnlUtilities.getWavesColor(height / 100D, boundaries[WAVES][1], boundaries[WAVES][0]));
                    // 
                    double topLeftLat = lat + (gribStepY / 2D);
                    double topLeftLng = lng - (gribStepX / 2D);
                    double bottomRightLat = topLeftLat - (gribStepY);
                    double bottomRightLng = topLeftLng + (gribStepX);
                    Point tl = chartPanel.getPanelPoint(topLeftLat, topLeftLng);
                    Point br = chartPanel.getPanelPoint(bottomRightLat, bottomRightLng);
                    Point tr = null; // Top Right
                    Point bl = null; // Bottom Left
                    if (chartPanel.getProjection() != ChartPanel.ANAXIMANDRE &&
                        chartPanel.getProjection() != ChartPanel.MERCATOR)
                    {
                      tr = chartPanel.getPanelPoint(topLeftLat, bottomRightLng);
                      bl = chartPanel.getPanelPoint(bottomRightLat, topLeftLng);
                    }
    
                    WWGnlUtilities.drawGRIBData(gr, gp.x, gp.y, tl, br, tr, bl, gribUserOpacity); 
                  }
                  else if ("RAIN".equals(dataOption)) 
                  {
                    double height = /*(double)*/(gribData.getGribPointData()[h][w].getRain() * 3600D);
                    gr.setColor(WWGnlUtilities.getRainColor(height, boundaries[RAIN][1], boundaries[RAIN][0]));
                    // 
                    double topLeftLat = lat + ((gribStepY) / 2D);
                    double topLeftLng = lng - ((gribStepX) / 2D);
                    double bottomRightLat = topLeftLat - (gribStepY);
                    double bottomRightLng = topLeftLng + (gribStepX);
                    Point tl = chartPanel.getPanelPoint(topLeftLat, topLeftLng);
                    Point br = chartPanel.getPanelPoint(bottomRightLat, bottomRightLng);
                    Point tr = null; // Top Right
                    Point bl = null; // Bottom Left
                    if (chartPanel.getProjection() != ChartPanel.ANAXIMANDRE &&
                        chartPanel.getProjection() != ChartPanel.MERCATOR)
                    {
                      tr = chartPanel.getPanelPoint(topLeftLat, bottomRightLng);
                      bl = chartPanel.getPanelPoint(bottomRightLat, topLeftLng);
                    }
    
                    WWGnlUtilities.drawGRIBData(gr, gp.x, gp.y, tl, br, tr, bl, gribUserOpacity);
                  }
                  else if ("PRMSL".equals(dataOption))
                  {
                    double pressure = (double)gribData.getGribPointData()[h][w].getPrmsl() / 100D;
                    gr.setColor(WWGnlUtilities.getPressureColor(pressure, boundaries[PRMSL][1], boundaries[PRMSL][0]));
                    // 
                    double topLeftLat = lat + ((gribStepY) / 2D);
                    double topLeftLng = lng - ((gribStepX) / 2D);
                    double bottomRightLat = topLeftLat - (gribStepY);
                    double bottomRightLng = topLeftLng + (gribStepX);
                    Point tl = chartPanel.getPanelPoint(topLeftLat, topLeftLng);
                    Point br = chartPanel.getPanelPoint(bottomRightLat, bottomRightLng);
                    Point tr = null; // Top Right
                    Point bl = null; // Bottom Left
                    if (chartPanel.getProjection() != ChartPanel.ANAXIMANDRE &&
                        chartPanel.getProjection() != ChartPanel.MERCATOR)
                    {
                      tr = chartPanel.getPanelPoint(topLeftLat, bottomRightLng);
                      bl = chartPanel.getPanelPoint(bottomRightLat, topLeftLng);
                    }
    
                    WWGnlUtilities.drawGRIBData(gr, gp.x, gp.y, tl, br, tr, bl, gribUserOpacity);
                  }
                  else if ("CURRENT".equals(dataOption))
                  {
    //              gr.setColor(GnlUtilities.getWindColor(coloredWind, initialGribWindBaseColor, speed, false));
                    initialGribCurrentBaseColor = (Color) ParamPanel.data[ParamData.GRIB_CURRENT_COLOR][ParamData.VALUE_INDEX];
                    if (drawWindColorBackground) 
                    {
                      double stpY = (gribStepY); // / (double)smooth);
                      double stpX = (gribStepX); //  / (double)smooth);
                  
                      double topLeftLat = lat + (stpY / 2D);
                      double topLeftLng = lng - (stpX / 2D);
                      double bottomRightLat = topLeftLat - stpY;
                      double bottomRightLng = topLeftLng + stpX;
                      // TopLeft
                      Point tl = chartPanel.getPanelPoint(topLeftLat, topLeftLng);
                      // Bottom Right
                      Point br = chartPanel.getPanelPoint(bottomRightLat, bottomRightLng);
    
                      Point tr = null; // Top Right
                      Point bl = null; // Bottom Left
                      if (chartPanel.getProjection() != ChartPanel.ANAXIMANDRE &&
                          chartPanel.getProjection() != ChartPanel.MERCATOR)
                      {
                        tr = chartPanel.getPanelPoint(topLeftLat, bottomRightLng);
                        bl = chartPanel.getPanelPoint(bottomRightLat, topLeftLng);
                      }
    
                      WWGnlUtilities.drawCurrent(gr, 
                                                 gp.x, 
                                                 gp.y,                                       
                                                 cSpeed, // * 10, 
                                                 cDir, 
                                                 coloredCurrent, 
                                                 initialGribCurrentBaseColor, 
                                                 drawHeavyDot, 
                                                 drawWindColorBackground, 
                                                 displayWindSpeedValue, 
                                                 useThickWind,
                                                 tl, 
                                                 br,
                                                 tr,
                                                 bl,
                                                 gribUserOpacity);
                    }
                    else
                      WWGnlUtilities.drawCurrent(gr, 
                                                 gp.x, 
                                                 gp.y,                                       
                                                 cSpeed, // * 10, 
                                                 cDir, 
                                                 coloredCurrent, 
                                                 initialGribCurrentBaseColor, 
                                                 drawHeavyDot, 
                                                 drawWindColorBackground, 
                                                 displayWindSpeedValue,
                                                 useThickWind,
                                                 gribUserOpacity); 
                  }
                }
    //          else
    //            System.out.println("Null DataPoint for [" + h + ", " + w + "]");
              }
            }
            catch (Exception ex1)
            {
              System.err.println("---------------------------------------------");
              System.err.println("From thread [" + Thread.currentThread().getName() + "]");
              System.err.println("gribData.getGribPointData():" + 
                                 (gribData.getGribPointData()==null?"":"not ") + "null, h=" + 
                                 Integer.toString(h) + ", gribData.getGribPointData().length:" + 
                                 Integer.toString(gribData.getGribPointData().length));
              ex1.printStackTrace();
              System.err.println("---------------------------------------------");
            }
          }
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
        }
      } // gribData != null
//    else
//      System.out.println("No GRIB Data");
    } 
    
    // Transparent faxes
    // Transparency
    alpha = faxUserOpacity; // ((Float) ParamPanel.data[ParamData.FAX_TRANSPARENCY][ParamData.VALUE_INDEX]).floatValue();
    //  System.out.println("Transparency set to " + Float.toString(alpha));
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

    // Faxes
    origTx = g2d.getTransform();
    for (int i=0; faxImage != null && i<faxImage.length; i++)
    {
      AffineTransform tx = new AffineTransform();
      Image img = null;
      boolean show = true;
      boolean transparentFax = true;
      try 
      { 
        img  = faxImage[i].faxImage; 
        show = faxImage[i].show;
        transparentFax = faxImage[i].transparent;

        if ("true".equals(System.getProperty("display.fax.coordinates", "false")))
        {
          int top    = (int)(faxImage[i].imageVOffset * faxImage[i].imageScale);
          int bottom = (int)(faxImage[i].imageVOffset * faxImage[i].imageScale) + (int)(img.getHeight(null) * faxImage[i].imageScale);
          int left   = (int)(faxImage[i].imageHOffset * faxImage[i].imageScale);
          int right  = (int)(faxImage[i].imageHOffset * faxImage[i].imageScale) + (int)(img.getWidth(null) * faxImage[i].imageScale);
          if (faxImage[i].imageRotationAngle != 0)
          {
            int xFaxCenter = left + ((right - left) / 2);
            int yFaxCenter = top + ((bottom - top) / 2);
            if (false)
            {
              // Original settings
              g2d.setColor(Color.BLACK);
              g2d.drawRect(left, 
                           top,  
                           (int)(img.getWidth(null) * faxImage[i].imageScale), // width 
                           (int)(img.getHeight(null) * faxImage[i].imageScale)); // height
        //          System.out.println("Fax Center:" + xFaxCenter + ", " + yFaxCenter + " (panel:" + chartPanel.getWidth() + "x" + chartPanel.getHeight() + ")");
              g2d.fillOval(xFaxCenter - 4, yFaxCenter - 4, 8, 8);
            }
            // Rotation centree sur le centre du fax.
            Point center = new Point(xFaxCenter, yFaxCenter);
        //          g2d.setColor(Color.BLUE);
        //          g2d.fillOval(right - 4, top - 4, 8, 8);
        //          g2d.drawString("1", right + 5, top);
            Point rotatedOne = WWGnlUtilities.rotate(new Point(right, 
                                                               top), 
                                                     center,
                                                     faxImage[i].imageRotationAngle);            
        //          g2d.fillOval(rotatedOne.x - 4, rotatedOne.y - 4, 8, 8);
        //          g2d.drawString("2", rotatedOne.x + 5, rotatedOne.y);
        //          g2d.drawLine(right, top, rotatedOne.x, rotatedOne.y);
        //          g2d.drawLine(right, top, xFaxCenter, yFaxCenter);
        //          g2d.drawLine(xFaxCenter, yFaxCenter, rotatedOne.x, rotatedOne.y);

        //          g2d.setColor(Color.RED);
        //          g2d.fillOval(left - 4, bottom - 4, 8, 8);
        //          g2d.drawString("1", left, bottom - 10);
            
            Point rotatedTwo = WWGnlUtilities.rotate(new Point(left, 
                                                               bottom), 
                                                     center,
                                                     faxImage[i].imageRotationAngle);
        //          g2d.fillOval(rotatedTwo.x - 4, rotatedTwo.y - 4, 8, 8);
        //          g2d.drawString("2", rotatedTwo.x, rotatedTwo.y - 10);
        //          g2d.drawLine(left, bottom, rotatedTwo.x, rotatedTwo.y);
        //          g2d.drawLine(left, bottom, xFaxCenter, yFaxCenter);
        //          g2d.drawLine(xFaxCenter, yFaxCenter, rotatedTwo.x, rotatedTwo.y);
            
            top    = rotatedOne.y; // top
            left   = rotatedOne.x; // left
            bottom = rotatedTwo.y; // bottom
            right  = rotatedTwo.x; // right
        //          System.out.println("Top:" + top + ", bottom:" + bottom + ", left:" + left + ", right:" + right);
          }          
          GeoPoint topLeft     = chartPanel.getGeoPos(left, top);
          GeoPoint bottomRight = chartPanel.getGeoPos(right, bottom);
        //        System.out.println("TopLeft    :" + topLeft.toString());
        //        System.out.println("BottomRight:" + bottomRight.toString());
          // Display where the fax is set
          System.out.println("<dyn-fax title=\"" + faxImage[i].faxTitle + "\"\n" + 
                             "         rotation=\"" + faxImage[i].imageRotationAngle + "\"\n" +
                             "         origin=\"" + faxImage[i].faxOrigin + "\"\n" +
                             "         top=\"" + topLeft.getL() + "\"\n" + 
                             "         left=\"" + topLeft.getG() + "\"\n" +
                             "         bottom=\"" + bottomRight.getL() + "\"\n" +
                             "         right=\"" + bottomRight.getG() + "\"\n" +
                             "         transparent=\"" + faxImage[i].transparent + "\"\n" +
                             "         change-color=\"" + faxImage[i].colorChange + "\"/>");
          
          // Frame around the fax.
          g2d.setColor(faxImage[i].colorChange ? faxImage[i].color : Color.BLACK);
          float[] dashPattern = { 5, 5, 5, 5 };
          Stroke origStroke = g2d.getStroke();
          g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
          g2d.drawRect(left, 
                       top,  
                       (right - left), // (int)(img.getWidth(null) * faxImage[i].imageScale), // width 
                       (bottom - top)); //(int)(img.getHeight(null) * faxImage[i].imageScale)); // height
          g2d.setStroke(origStroke);
        }

        if (!transparentFax)
          continue;
  //      System.out.println("Image:" + (img==null?"NULL":"ok") + " show:" + show);
      } 
      catch (NullPointerException npe)
      {
        if (false) System.out.print("-npe[2] (i=" + i + ")-");;
      }
      catch (Exception ex) 
      {
        WWContext.getInstance().fireLogging("Oops - 2:" + ex.getClass().getName() + "\n");
      }
      if (img != null && show)
      {
        int h = img.getHeight(this);
        int w = img.getWidth(this);
        
        double add2HOffset = 0D, add2VOffset = 0D;
    //  double scale = chartPanel.getPreferredSize().getWidth() / (double)weatherFaxImage.getWidth(null);
    //  layer.setPreferredSize(chartPanel.getPreferredSize());
        tx.scale(faxImage[i].imageScale * whRatio, faxImage[i].imageScale);
        tx.translate((double)faxImage[i].imageHOffset + (add2HOffset * faxImage[i].imageScale), 
                     (double)faxImage[i].imageVOffset + (add2VOffset * faxImage[i].imageScale));
        /*
         * tx.shear(shx, shy) actually applies the following matrix:
         * 
         * |  1  shx  0  |
         * | shy  1   0  |
         * |  0   0   1  |
         */
//      tx.shear(-Math.sin(Math.toRadians(30D)), 
//               Math.sin(Math.toRadians(10D)));

        // IMPORTANT: Rotation is the last transformation
        if (faxImage[i].imageRotationAngle != 0D)
        {
          double radians = Math.toRadians(faxImage[i].imageRotationAngle);
          tx.rotate(radians, w/2, h/2);
        }

    //  tx.scale(1D, 1D);
        if (!faxImage[i].transparent) // Then reset transparency
          g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.drawImage(img, tx, this); // Fax is drawn here
        if (!faxImage[i].transparent) // Then set it back
          g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        // If fax is *not* transparent, set it to false      
        chartPanel.setCleanFirst(true); 
        g2d.setTransform(origTx);
      }
    }
    // Reset transparency
    alpha = 1.0f;
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    
    // The Chart
    gr.setColor(chartPanel.getChartColor());
    Stroke originalStroke = null;
    if (gr instanceof Graphics2D)
    {
      originalStroke = g2d.getStroke();
      Stroke stroke =  new BasicStroke(((Integer) ParamPanel.data[ParamData.CHART_LINE_THICK][ParamData.VALUE_INDEX]).intValue(), 
                                       BasicStroke.CAP_BUTT,
                                       BasicStroke.JOIN_BEVEL);
      g2d.setStroke(stroke);  
    }
    
    // Chart itself
    if (drawChart)
    {
//      Point pt = chartPanel.getPanelPoint(65D, -130D) ;
//      System.out.println("y top:" + pt.y);
//    if (chartPanel.getProjection() == ChartPanel.LAMBERT)
//      System.out.println("Lambert detected");
      World.drawChart(chartPanel, gr); 
    }
      
    // Globe view: draw the eye nadir
    if (chartPanel.getProjection() == ChartPanel.GLOBE_VIEW && plotNadir)
    {
      Point gp = chartPanel.getPanelPoint(chartPanel.getGlobeViewForeAftRotation(), 
                                          chartPanel.getGlobeViewLngOffset());
      Color orig = gr.getColor();
      gr.setColor(Color.blue);
      gr.drawOval(gp.x - 5, gp.y - 5, 10, 10);
      gr.drawOval(gp.x - 3, gp.y - 3, 6, 6);
      gr.setColor(orig);
    }
    else if (chartPanel.getProjection() == ChartPanel.SATELLITE_VIEW && plotNadir)
    {
      Point gp = chartPanel.getPanelPoint(chartPanel.getSatelliteLatitude(), 
                                          chartPanel.getSatelliteLongitude());
      Color orig = gr.getColor();
      gr.setColor(Color.blue);
      gr.drawOval(gp.x - 5, gp.y - 5, 10, 10);
      gr.drawOval(gp.x - 3, gp.y - 3, 6, 6);
      gr.setColor(orig);
    }
//  layer.repaint();
//  imgHolder.repaint();
    
    if (gr instanceof Graphics2D)
      g2d.setStroke(originalStroke);  
    
    if (from != null)
    {
      Point gp = chartPanel.getPanelPoint(from.getL(), from.getG());
      String prefix = "";
      if (intermediateRoutingWP != null && intermediateRoutingWP.size() > 0)
        prefix = "1. ";
      chartPanel.postit(gr, prefix + WWGnlUtilities.buildMessage("origin"), gp.x + 5, gp.y + 5, Color.yellow);
      Color orig = gr.getColor();
      gr.setColor(Color.black);
      gr.fillOval(gp.x - 3, gp.y - 3, 6, 6);
      if (greenFlagImage == null)
//      greenFlagImage = new ImageIcon(this.getClass().getResource("greenflag.png")); //.getImage();      
        greenFlagImage = new ImageIcon(this.getClass().getResource("pushpin_25x25.gif")); //.getImage();      
      
//    gr.drawImage(greenFlagImage.getImage(), gp.x - 2, gp.y - greenFlagImage.getImage().getHeight(null), null);
      gr.drawImage(greenFlagImage.getImage(), gp.x - 25, gp.y - greenFlagImage.getImage().getHeight(null), null);
      gr.setColor(orig);
    }
    if (to != null)
    {
      Point gp = chartPanel.getPanelPoint(to.getL(), to.getG());
      String prefix = "";
      if (intermediateRoutingWP != null && intermediateRoutingWP.size() > 0)
        prefix = Integer.toString(intermediateRoutingWP.size() + 2) + ". ";
      chartPanel.postit(gr, prefix + WWGnlUtilities.buildMessage("destination"), gp.x + 5, gp.y, Color.yellow);
      Color orig = gr.getColor();
      gr.setColor(Color.black);
      gr.fillOval(gp.x - 3, gp.y - 3, 6, 6);
      if (greenFlagImage == null)
//      greenFlagImage = new ImageIcon(this.getClass().getResource("greenflag.png")); //.getImage();      
        greenFlagImage = new ImageIcon(this.getClass().getResource("pushpin_25x25.gif")); //.getImage();      
//    gr.drawImage(greenFlagImage.getImage(), gp.x - 2, gp.y - greenFlagImage.getImage().getHeight(null), null);
      gr.drawImage(greenFlagImage.getImage(), gp.x - 25, gp.y - greenFlagImage.getImage().getHeight(null), null);
      gr.setColor(orig);
    }
    if (intermediateRoutingWP != null && intermediateRoutingWP.size() > 0)
    {
      int nbIntPt = 0;
      for (GeoPoint gp : intermediateRoutingWP)
      {
        Point pt = chartPanel.getPanelPoint(gp.getL(), gp.getG());
        chartPanel.postit(gr, Integer.toString(2 + (nbIntPt++)) + ".", pt.x + 5, pt.y, Color.yellow);
        Color orig = gr.getColor();
        gr.setColor(Color.black);
        gr.fillOval(pt.x - 3, pt.y - 3, 6, 6);
        if (greenFlagImage == null)
//        greenFlagImage = new ImageIcon(this.getClass().getResource("greenflag.png")); //.getImage();      
          greenFlagImage = new ImageIcon(this.getClass().getResource("pushpin_25x25.gif")); //.getImage();      
//      gr.drawImage(greenFlagImage.getImage(), pt.x - 2, pt.y - greenFlagImage.getImage().getHeight(null), null);
        gr.drawImage(greenFlagImage.getImage(), pt.x - 25, pt.y - greenFlagImage.getImage().getHeight(null), null);
        gr.setColor(orig);        
      }
    }
    
    // Some plots - places.xml
    for (int i=0; showPlaces && drawChart && gpa != null && i<gpa.length; i++)
    {
      Point gp = chartPanel.getPanelPoint(gpa[i].getL(), gpa[i].getG());
      if (gp != null && isVisible(gpa[i].getL(), gpa[i].getG()))
      {
        if (showPlacesArray[i].booleanValue())
        {
          chartPanel.postit(gr, ptLabels[i].replace("\\n", "\n"), gp.x, gp.y, Color.yellow);
          Color orig = gr.getColor();
          gr.setColor(Color.red);
          gr.drawOval(gp.x - 5, gp.y - 5, 10, 10);
          gr.drawOval(gp.x - 3, gp.y - 3, 6, 6);
          gr.setColor(orig);
        }
      }
    }
    
    // SailMail Stations
    if (showSMStations && drawChart && sma != null)
    {
      for (WWGnlUtilities.SailMailStation sms : sma)
      {
        Point pt = chartPanel.getPanelPoint(sms.getGp());
        chartPanel.postit(gr, sms.getStationName().replace(" - ", "\n"), pt.x, pt.y, Color.blue);
        Color orig = gr.getColor();
        gr.setColor(Color.blue);
        gr.drawOval(pt.x - 5, pt.y - 5, 10, 10);
        gr.drawOval(pt.x - 3, pt.y - 3, 6, 6);
        gr.setColor(orig);
      }
    }
    
    // Weather Stations
    if (showWeatherStations && drawChart && wsta != null)
    {
      for (WWGnlUtilities.WeatherStation ws : wsta)
      {
        Point pt = chartPanel.getPanelPoint(ws.getGp());
        chartPanel.postit(gr, ws.getStationName(), pt.x, pt.y, Color.blue);
        Color orig = gr.getColor();
        gr.setColor(Color.blue);
        gr.drawOval(pt.x - 5, pt.y - 5, 10, 10);
        gr.drawOval(pt.x - 3, pt.y - 3, 6, 6);
        gr.setColor(orig);
      }
    }    
    // Routing
    if (from != null && 
        wgd != null && 
        (routingForecastMode || (routingMode && to != null)) && 
        (drawIsochrons || drawBestRoute))
    {
      if (routingForecastMode || allCalculatedIsochrons != null)
      {
        gr.setColor(Color.black);
        int nbIsochron = 0;
        Color colors[] = { Color.orange, 
                           Color.darkGray, 
                           Color.green, 
                           Color.red, 
                           Color.blue, 
                           Color.pink, 
                           Color.gray,
                           Color.yellow,
                           Color.magenta, 
                           Color.cyan, 
                           Color.black };
    
        // Draw Routing
        boolean isochronLine = true;   // TODO Preference
        boolean drawRadii    = true;  // TODO Preference
        boolean plotPoints   = false;  // TODO Preference
        if (allCalculatedIsochrons != null && drawIsochrons)
        {
          try
          {
            Iterator<List<RoutingPoint>> dimOne = allCalculatedIsochrons.iterator();
            while (dimOne.hasNext())
            {
              List<RoutingPoint> curve = dimOne.next();
              Iterator<RoutingPoint> dimTwo = curve.iterator();
              Point previous = null;
              int colorIndex = (nbIsochron % colors.length);
              while (dimTwo.hasNext())
              {
                RoutingPoint p = dimTwo.next();
                Point pp = chartPanel.getPanelPoint(p.getPosition());
                Point ancestor = null;
                try { ancestor = chartPanel.getPanelPoint(p.getAncestor().getPosition()); } catch (Exception ex) {}
                
                if (isochronLine && plotPoints)
                  gr.fillOval(pp.x - 2, pp.y - 2, 4, 4);
                
                if (ancestor != null)
                {
                  if (drawRadii)
                  {
                    Color c = gr.getColor();
                    gr.setColor((Color) ParamPanel.data[ParamData.OLD_ISOCHRONS_COLOR][ParamData.VALUE_INDEX]);
                    gr.drawLine(ancestor.x, ancestor.y, pp.x, pp.y);
                    gr.setColor(c);
                  }
                }
                if (!isochronLine)
                {
                  if (plotPoints)
                  {
                    Color c = gr.getColor();
                    if (p.isGribTooOld())
                      gr.setColor((Color) ParamPanel.data[ParamData.OLD_ISOCHRONS_COLOR][ParamData.VALUE_INDEX]); 
                    else
                      gr.setColor(colors[colorIndex]);
                    gr.fillOval(pp.x - 2, pp.y - 2, 4, 4);
                    gr.setColor(c);
                  }
                }
                else
                {
                  if (previous != null)
                  {
                    Color c = gr.getColor();
                    if (p.isGribTooOld())
                      gr.setColor((Color) ParamPanel.data[ParamData.OLD_ISOCHRONS_COLOR][ParamData.VALUE_INDEX]);
                    else
                      gr.setColor(colors[colorIndex]);
                    gr.drawLine(previous.x, previous.y, pp.x, pp.y);
                    gr.setColor(c);
                  }
                  previous = pp;
                }
              }
              nbIsochron++;
            }  
            if (from != null)
            {
              Point center = chartPanel.getPanelPoint(from);
              int xc = center.x; // this.getWidth() / 2;
              int yc = center.y; // this.getHeight();
              // Plot center
              Color c = gr.getColor();
              gr.setColor(Color.green);
//            gr.fillOval(xc - 3, yc - 3, 6, 6);
              gr.fillOval(xc - 5, yc - 5, 10, 10); 
              gr.setColor(c);
            }
          }
          catch (ConcurrentModificationException cme)
          {
            System.out.println("ConcurrentModificationException (A)...");
          }
        }        
        if (closestPoint != null && drawBestRoute)
        {
          Graphics2D g2 = (Graphics2D)gr;
          g2.setColor((Color) ParamPanel.data[ParamData.ROUTE_COLOR][ParamData.VALUE_INDEX]);
//        g2.setColor(Color.blue); // Route Color
          originalStroke = g2.getStroke();
          Stroke stroke = new BasicStroke(4f, 0, 2);
          g2.setStroke(stroke);
          alpha = 0.3f;
          g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

          Point fromPt = chartPanel.getPanelPoint(closestPoint.getPosition());
          RoutingPoint thisPoint = closestPoint; // LAST point of the route
          // A cross
          gr.drawLine(fromPt.x - 10, fromPt.y, fromPt.x + 10, fromPt.y);
          gr.drawLine(fromPt.x, fromPt.y - 10, fromPt.x, fromPt.y + 10);
//        ((Graphics2D)gr).setStroke(originalStroke);     
          
          // Draw Wind at that point
          Color c = g2.getColor();
          Stroke strk = g2.getStroke();
          g2.setStroke(new BasicStroke(1f,
                                       BasicStroke.CAP_BUTT,
                                       BasicStroke.JOIN_MITER));
          
//        System.out.println("1 - Drawing Wind :" + WWGnlUtilities.XXX12.format(thisPoint.getTws()) + " kts @ " + Integer.toString(thisPoint.getTwd()) + "t");
          g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
          WWGnlUtilities.drawWind(gr, 
                                  fromPt.x, 
                                  fromPt.y, 
                                  thisPoint.getTws(), 
                                  180 - thisPoint.getTwd(), 
                                  false,
                                  Color.blue, 
                                  false, 
                                  false, 
                                  false, 
                                  false);          
          g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
          g2.setColor(c);
          g2.setStroke(strk);
          // Draw the best route..., from destination to origin.
          boolean go = drawBestRoute;
          if (go)
            bestRoute = new ArrayList<RoutingPoint>();
          while (go)
          {
            RoutingPoint next = thisPoint.getAncestor();
            if (next == null)
              go = false;
            else
            {
              bestRoute.add(next);
              Point toPoint = chartPanel.getPanelPoint(next.getPosition());
              // The route segment
              gr.drawLine(fromPt.x, fromPt.y, toPoint.x, toPoint.y);
              gr.fillOval(toPoint.x - 4, toPoint.y - 4, 8, 8); // A dot
              // Draw Wind at that point
              c = g2.getColor();
              strk = g2.getStroke();
              g2.setStroke(new BasicStroke(1f,
                                           BasicStroke.CAP_BUTT,
                                           BasicStroke.JOIN_MITER));
              int twd    = thisPoint.getTwd();
              double tws = thisPoint.getTws();
//            System.out.println("2 - Drawing Wind :" + WWGnlUtilities.XXX12.format(tws) + " kts @ " + Integer.toString(twd) + "t");
              g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
              WWGnlUtilities.drawWind(gr, 
                                      toPoint.x, 
                                      toPoint.y, 
                                      tws, 
                                      180 - twd, 
                                      false,
                                      Color.blue, 
                                      false, 
                                      false, 
                                      false, 
                                      false);          
              g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
              g2.setColor(c);
              g2.setStroke(strk);
              if (postitOnRoute)
              {
                String postit = WWGnlUtilities.SDF_UT_3.format(thisPoint.getDate());
                postit += ("\n" + WWGnlUtilities.XXX12.format(tws) + " kts @ " + Integer.toString(twd) + "t");
                chartPanel.postit(gr, postit, toPoint.x, toPoint.y, Color.yellow);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
              }
              // Move on, swap.
              fromPt = toPoint;
              thisPoint = next;
            }
          }
          // TODO Wind for last point (origin)          
          alpha = 1f;
          g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
          g2.setStroke(originalStroke);
        }
//      else
//        System.out.println("Closest is null");
      }
      else
        if (routingMode) System.out.println("No isochron yet");
    } // End Of Routing
    
    // GPX ?
    if (gpxData != null)
    {
      GeoPoint previous = null;
      for (GeoPoint gp : gpxData)
      {
        if (previous != null)
        {
          Point _from = chartPanel.getPanelPoint(previous);          
          Point _to   = chartPanel.getPanelPoint(gp);
          gr.drawLine(_from.x, _from.y, _to.x, _to.y);
        }
        previous = gp;
      }
    }
    
    if (wp2highlight != null)
    {
      Point wp = chartPanel.getPanelPoint(wp2highlight);
      ((Graphics2D)gr).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
      gr.setColor(Color.green);
      gr.fillOval(wp.x - 8, wp.y - 8, 16, 16);        
      ((Graphics2D)gr).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
    
    if (wpBeingDragged != null) // Postit
    {
      Point p = chartPanel.getPanelPoint(wpBeingDragged);
      chartPanel.bubble(gr, 
                        wpBeingDragged.toString(), 
                        p.x,                              
                        p.y, 
                        Color.yellow, 
                        Color.red, 
                        0.40f);
    }
    
    if (boatPosition != null) 
    {
      WWGnlUtilities.drawBoat((Graphics2D)gr, 
                            (Color)ParamPanel.data[ParamData.GPS_BOAT_COLOR][ParamData.VALUE_INDEX], 
                            chartPanel.getPanelPoint(boatPosition.getL(), 
                                                     boatPosition.getG()), 
                            30, 
                            boatHeading,
                            0.50f,
                            WWGnlUtilities.CIRCLE);
    }
    
    // Routing Element
    if (routingPoint != null)
    {      
      WWGnlUtilities.drawBoat((Graphics2D)gr, 
                             (Color)ParamPanel.data[ParamData.ROUTING_BOAT_COLOR][ParamData.VALUE_INDEX],
                             chartPanel.getPanelPoint(routingPoint.getL(), 
                                                      routingPoint.getG()), 
                             30, 
                             routingHeading,
                             0.50f);      
      // Add big bubble here 
      // Find routing point for routing boat position.
      RoutingPoint thisPoint = closestPoint; // LAST point of the route
//    System.out.println("Searching " + routingPoint.toString());
      boolean go = true;
      RoutingPoint furtherDown = null;
      while (go)
      {
        if (thisPoint.getPosition().equals(routingPoint))
        {
          go = false;
          if (furtherDown != null)
            thisPoint = furtherDown;
//        System.out.println("Found match at " + thisPoint.getPosition().toString());
        }
        else
        {
          furtherDown = thisPoint;
          RoutingPoint next = thisPoint.getAncestor();
          if (next == null)
            go = false;
          else
            thisPoint = next;
//        System.out.println("ThisPoint now " + thisPoint.getPosition().toString());
        }
      }    
      RoutingPoint previous = thisPoint.getAncestor();
//      try { System.out.println("ThisPoint:" + WWGnlUtilities.SDF_UT_3.format(thisPoint.getDate()) + ", Next:" + WWGnlUtilities.SDF_UT_3.format(thisPoint.getAncestor().getDate())); }
//      catch (Exception ex)
//      { ex.printStackTrace(); }
      String postit = "";
      if (previous != null)
      {
        postit += (WWGnlUtilities.buildMessage("from") + ":" + WWGnlUtilities.SDF_UT_3.format(previous.getDate()) + "\n" +
                   WWGnlUtilities.buildMessage("to")   + ":");
      }
      postit += WWGnlUtilities.SDF_UT_3.format(thisPoint.getDate());
      postit += ("\nWind:" + WWGnlUtilities.XXX12.format(thisPoint.getTws()) + " kts @ " + Integer.toString(thisPoint.getTwd()) + "\272t");
      postit += ("\nBSP :" + WWGnlUtilities.XXX12.format(thisPoint.getBsp()) + " kts");
      postit += ("\nHDG :" + Integer.toString(thisPoint.getHdg()) + "\272t");
      chartPanel.bubble(gr, 
                        postit, 
                        chartPanel.getPanelPoint(routingPoint.getL(), 
                                                 routingPoint.getG()).x,                              
                        chartPanel.getPanelPoint(routingPoint.getL(), 
                                                 routingPoint.getG()).y, 
                        Color.cyan, 
                        Color.blue, 
                        0.40f);
    }
    
    if (fromGRIBSlice != null && toGRIBSlice != null)
    {
      Graphics2D g2 = (Graphics2D)gr;
      originalStroke = g2.getStroke();
      Stroke stroke = new BasicStroke(4f, 0, 2);
      g2.setStroke(stroke);
      g2.setColor(Color.darkGray);
      Point f = chartPanel.getPanelPoint(fromGRIBSlice);
      Point t = chartPanel.getPanelPoint(toGRIBSlice);
      g2.drawLine(f.x, f.y, t.x, t.y);
      
      if (gribSliceInfo != -1D)
      {
        int infoX = f.x + (int)(gribSliceInfo * (double)(t.x - f.x));
        int infoY = f.y + (int)(gribSliceInfo * (double)(t.y - f.y));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g2.setColor(Color.red);
        g2.fillOval(infoX - 8, infoY - 8, 16, 16);        
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
      }
      g2.setStroke(originalStroke);
    }
    if (gribSliceInfo != -1D && fromGRIBSlice == null && toGRIBSlice == null) // Routing GRIB Slice
    {
//    System.out.println("GribSliceInfo at Point D:" + gribSliceInfo);      
      int[] xy = gsp.getPointFromD(gribSliceInfo);
      if (gribData != null)
      {
//      System.out.println("gribSliceInfo:" + gribSliceInfo);
        GeoPoint gp = null;
        if (bestRoute != null)
        {
          int len = bestRoute.size();
          int index = (int)Math.round(len * (1 - gribSliceInfo)) - 1; // Last point is first in the route
//        System.out.println("Len:" + len + ", gribSliceInfo:" + gribSliceInfo + ", index:" + index);
          if (index < 0) index = 0;
          if (index > len - 1) index = len - 1;
          try
          {
            RoutingPoint rp = bestRoute.get(index);
            gp = new GeoPoint(rp.getPosition().getL(), rp.getPosition().getG());
          }
          catch (Exception ex)
          {
            System.out.println(ex.toString());
            System.out.println("Len:" + len + ", gribSliceInfo:" + gribSliceInfo + ", index:" + index);
          }
        }
        else
        {
          GribHelper.GribPointData gpd = gribData.getGribPointData()[xy[0]][xy[1]];
          gp = new GeoPoint(gpd.getLat(), gpd.getLng());
  //      System.out.println("X:" + xy[0] + ", Y:" + xy[1] + " -> " + gp.toString());
        }
        
        Point pt = chartPanel.getPanelPoint(gp);
        ((Graphics2D)gr).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        gr.setColor(Color.red);
        gr.fillOval(pt.x - 8, pt.y - 8, 16, 16);        
        ((Graphics2D)gr).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
      }
    }
    
    if (displayPageSize)
    {
      // Print / Page boundaries
      PrinterJob printJob = PrinterJob.getPrinterJob();
      PageFormat pf = printJob.defaultPage();
//    if (jScrollPane1.getViewport().getSize().width > jScrollPane1.getViewport().getSize().height)
      if (chartPanel.getSize().width > chartPanel.getSize().height)
        pf.setOrientation(PageFormat.LANDSCAPE);
      else
        pf.setOrientation(PageFormat.PORTRAIT);
        
      double pageHeight = pf.getImageableHeight();
      double pageWidth = pf.getImageableWidth();
      originalStroke = null;
      if (gr instanceof Graphics2D)
      {
        originalStroke = ((Graphics2D)gr).getStroke();
        float miterLimit = 10F;
        float[] dashPattern = {10F};
        float dashPhase = 5F;
        Stroke stroke =  new BasicStroke(1, 
                                         BasicStroke.CAP_BUTT,
                                         BasicStroke.JOIN_MITER,
                                         miterLimit,
                                         dashPattern,
                                         dashPhase);
        ((Graphics2D)gr).setStroke(stroke);  
      }
      gr.setColor(Color.black);
      int viewX = chartPanelScrollPane.getViewport().getViewPosition().x;
      int viewY = chartPanelScrollPane.getViewport().getViewPosition().y;
      for (int x = (int)pageWidth; x<chartPanelScrollPane.getViewport().getSize().width; x+=(int)pageWidth)
        gr.drawLine(viewX + x + 1, 0, viewX + x + 1, chartPanel.getSize().height);
      for (int y = (int)pageHeight; y<chartPanelScrollPane.getViewport().getSize().height; y+=(int)pageHeight)
        gr.drawLine(0, viewY + y + 1, chartPanel.getSize().width, viewY + y + 1);
      if (originalStroke != null)
        ((Graphics2D)gr).setStroke(originalStroke);  
    }  
    // And finally    
    displayStatus();
//  setCheckBoxes();       
  }

  private boolean resizeSplitPane = true;
  public void chartPanelPaintComponent(final Graphics gr)
  {
    if (resizeSplitPane)
    {
      if (enableGRIBSlice && fromGRIBSlice != null && toGRIBSlice != null)
      {
        int loc = 190; // 190, datapanel height
        jSplitPane.setDividerLocation(loc);
      }
      else
        jSplitPane.setDividerLocation(0D);
      resizeSplitPane = false;
    }
    if (!doItAfter)
      chartPanelPaintComponentFeature(gr);
  }

  public void chartPanelPaintComponentAfter(final Graphics gr)
  {
    if (doItAfter)
      chartPanelPaintComponentFeature(gr);
    // Draw Alternate Windows
    if (displayAltTooltip)
    {
//    displayAltWindow(gr, WWGnlUtilities.buildMessage("alt-win-title"), tooltipMess);
      setDisplayAltTooltip(gr, WWGnlUtilities.buildMessage("alt-win-title"), tooltipMess);
    }    
  }

  public void whatIfRouting() 
  {
    List<RoutingPoint> route = RoutingUtil.whatIfRouting(this, this.getFrom(), wgd);
    if (route != null)
    {
      routingForecastMode = true;
      routingMode = false;
      closestPoint = route.get(0);
      WWContext.getInstance().fireRoutingForecastAvailable(true, route);
    }
  }
  
  public void calculateRouting()
  {
    routingMode = true;
    routingForecastMode = false;
    routingOnItsWay = true;
    GribHelper.setAlreadySaidTooOld(false);
    startIsochronComputation();
  }

  private StartRoutingPanel startRoutingPanel = null;

  private transient GeoPoint isoFrom;
  private transient GeoPoint isoTo;

  private double timeInterval  = ((Double) ParamPanel.data[ParamData.ROUTING_TIME_INTERVAL][ParamData.VALUE_INDEX]).doubleValue(); // 6.0;
  private int routingForkWidth = ((Integer) ParamPanel.data[ParamData.ROUTING_FORK_WIDTH][ParamData.VALUE_INDEX]).intValue();      // 50;
  private int routingStep      = ((Integer) ParamPanel.data[ParamData.ROUTING_STEP][ParamData.VALUE_INDEX]).intValue();            // 10;

  public void startIsochronComputation()
  {
    fromGRIBSlice = null;
    toGRIBSlice = null;
    
    boolean init = true;
    TimeZone.setDefault(TimeZone.getTimeZone("127"));

    int borderTWS           = ((Integer) ParamPanel.data[ParamData.AVOID_TWS_GT][ParamData.VALUE_INDEX]).intValue();            // -1;
    int borderTWA           = ((Integer) ParamPanel.data[ParamData.AVOID_TWA_LT][ParamData.VALUE_INDEX]).intValue();            // -1;
    boolean stopOnExhausted = ((Boolean) ParamPanel.data[ParamData.STOP_ROUTING_ON_EXHAUSTED_GRIB][ParamData.VALUE_INDEX]).booleanValue();
    double polarFactor = 1.0;

    Date gribFrom    = wgd[0].getDate();
    Date gribTo      = wgd[wgd.length - 1].getDate();
    Date currentDate = new Date(); // TimeUtil.getGMT();
     
    Calendar cal = new GregorianCalendar();
//  System.out.println("Isochrons - TimeZone:" + TimeZone.getDefault().toString());
    cal.setTime(gribFrom);
     
    int year    = cal.get(Calendar.YEAR);
    int month   = cal.get(Calendar.MONTH);
    int day     = cal.get(Calendar.DAY_OF_MONTH);
    int hours   = cal.get(Calendar.HOUR_OF_DAY);
    int minutes = cal.get(Calendar.MINUTE);
    int seconds = cal.get(Calendar.SECOND);
     
    String dateOne = Integer.toString(year) + "-" + WWGnlUtilities.MONTH[month] + "-" + WWGnlUtilities.DF2.format(day) + " " + WWGnlUtilities.DF2.format(hours) + ":" + WWGnlUtilities.DF2.format(minutes) + ":" + WWGnlUtilities.DF2.format(seconds) + " UT";
    
    cal.setTime(gribTo);
     
    year    = cal.get(Calendar.YEAR);
    month   = cal.get(Calendar.MONTH);
    day     = cal.get(Calendar.DAY_OF_MONTH);
    hours   = cal.get(Calendar.HOUR_OF_DAY);
    minutes = cal.get(Calendar.MINUTE);
    seconds = cal.get(Calendar.SECOND);
     
    String dateTwo = Integer.toString(year) + "-" + WWGnlUtilities.MONTH[month] + "-" + WWGnlUtilities.DF2.format(day) + " " + WWGnlUtilities.DF2.format(hours) + ":" + WWGnlUtilities.DF2.format(minutes) + ":" + WWGnlUtilities.DF2.format(seconds) + " UT";
    
    String mess = WWGnlUtilities.buildMessage("grib-from-to", new String[] { dateOne, dateTwo });
    
    if (startRoutingPanel == null)
      startRoutingPanel = new StartRoutingPanel();
    else
      init = false;
    startRoutingPanel.setGribFrom(gribFrom);
    // Distance from to
    GreatCircle gcCalc = WWContext.getInstance().getGreatCircle();
    gcCalc.setStart(new GeoPoint(Math.toRadians(from.getL()), Math.toRadians(from.getG())));
    gcCalc.setArrival(new GeoPoint(Math.toRadians(to.getL()), Math.toRadians(to.getG())));
//  gcCalc.calculateGreatCircle(20);
    double gcDist = Math.toDegrees(gcCalc.getDistance()) * 60.0;
    
    startRoutingPanel.setDistanceLabel(WWGnlUtilities.buildMessage("great-circle", new String[] { WWGnlUtilities.XX22.format(gcDist) }));
    startRoutingPanel.setMess(mess);
    
    if (init) 
    {
      startRoutingPanel.setDate(currentDate);

      startRoutingPanel.setTimeInterval((int)timeInterval);
      startRoutingPanel.setAngularStep(routingStep);
      startRoutingPanel.setForkWidth(routingForkWidth);
      startRoutingPanel.setMaxTWS(borderTWS);
      startRoutingPanel.setMinTWA(borderTWA);
      startRoutingPanel.setStopRoutingOnExhaustedGRIB(stopOnExhausted);
      startRoutingPanel.setPolarFactor(((Double) ParamPanel.data[ParamData.POLAR_SPEED_FACTOR][ParamData.VALUE_INDEX]).doubleValue());
    }
    
    int resp = JOptionPane.showConfirmDialog(this, 
                                             startRoutingPanel, 
                                             WWGnlUtilities.buildMessage("routing"), 
                                             JOptionPane.OK_CANCEL_OPTION);
    if (resp == JOptionPane.OK_OPTION)
    {
      currentDate = startRoutingPanel.getDate();
      timeInterval = startRoutingPanel.getTimeInterval();
      routingForkWidth = startRoutingPanel.getForkWidth();
      routingStep = startRoutingPanel.getAngularStep();
      borderTWS = startRoutingPanel.getMaxTWS();
      borderTWA = startRoutingPanel.getMinTWA();
      stopOnExhausted = startRoutingPanel.isStopRoutingOnExhaustedGRIB();
      polarFactor = startRoutingPanel.getPolarFactor();
      
      if (borderTWS != -1 || borderTWA != -1)
      {
        JOptionPane.showMessageDialog(this, 
                                      WWGnlUtilities.buildMessage("routing-warning"), 
                                      WWGnlUtilities.buildMessage("routing"),
                                      JOptionPane.INFORMATION_MESSAGE);
      }
    }
    else
      return; // Cancel
      
    final int limitTWS = borderTWS;
    final int limitTWA = borderTWA;    
    final Date now = currentDate;
    final boolean stopGRIB = stopOnExhausted;
    final double pf = polarFactor;
    Thread isochronThread = null;
//  if (isoFrom == null || from != isoFrom || isoTo == null || isoTo != to)
    {
      final boolean showProgressMonitor = true;
      // the Routing
      isochronThread = new Thread("routing-thread")
      {
        public void run()
        {
          ProgressMonitor pm = null;
          if (showProgressMonitor)
          {
            WWContext.getInstance().setMonitor(ProgressUtil.createModalProgressMonitor(WWContext.getInstance().getMasterTopFrame(), 1, true, true, "interrupt-routing"));
            pm = WWContext.getInstance().getMonitor();
            if (pm != null)
            {
              synchronized (pm)
              {
                pm.start(WWGnlUtilities.buildMessage("routing-dot")); 
              }
            }
            WWContext.getInstance ().setAel4monitor(new ApplicationEventListener()
              {
                public String toString()
                {
                  return "{" + Long.toString(id) + "} from Runnable in CommandPanel (4).";
                }
                public void progressing(String mess)
                {
                  try
                  {
                    ProgressMonitor pm = WWContext.getInstance().getMonitor();
                    if (pm != null)
                    {
                      synchronized (pm)
                      {
                        pm.setCurrent(mess, WWContext.getInstance().getMonitor().getCurrent());
                      }
                    }
                  }
                  catch (Exception ex)
                  {
                    System.out.println(" ... progessing:" + ex.toString());
                    ex.printStackTrace();
                  }
                }
                public void interruptProgress()
                {
                  System.out.println("Interruption requested (4)...");
                  RoutingUtil.interruptRoutingCalculation();
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
                }
              });
            WWContext.getInstance().addApplicationListener(WWContext.getInstance().getAel4monitor());
          }
          WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("computing-isochrones") + "\n");
          WWContext.getInstance().fireSetLoading(true, WWGnlUtilities.buildMessage("routing"));
          boolean stopIfTooOld = stopGRIB;
          long before = System.currentTimeMillis();
          closest = null;
          isoFrom = from;
          isoTo   = to;
          System.out.println("Routing from " + isoFrom.toString() + "\nto " + isoTo.toString());
          int i = 0;
          Point point = chartPanel.getPanelPoint(isoFrom);
          RoutingPoint center = new RoutingPoint(point);
          center.setPosition(from);
          point = chartPanel.getPanelPoint(to);
          RoutingPoint destination = new RoutingPoint(point);
          destination.setPosition(to);

          List<RoutingPoint> interWP = null;
          if (intermediateRoutingWP != null && intermediateRoutingWP.size() > 0)
          {
            interWP = new ArrayList<RoutingPoint>(intermediateRoutingWP.size());
            for (GeoPoint gp : intermediateRoutingWP)
            {
              RoutingPoint rp = new RoutingPoint(chartPanel.getPanelPoint(gp));
              rp.setPosition(gp);
              interWP.add(rp);
            }
          }
          allCalculatedIsochrons = RoutingUtil.calculateIsochrons(instance, 
                                                                  chartPanel,
                                                                  center, 
                                                                  destination, 
                                                                  interWP,
                                                                  now, 
                                                                  wgd,
                                                                  timeInterval,
                                                                  routingForkWidth,
                                                                  routingStep,
                                                                  limitTWS, 
                                                                  limitTWA,
                                                                  stopIfTooOld,
                                                                  pf);
          
          int clipboardOption = Integer.parseInt(((ParamPanel.RoutingOutputList)(ParamPanel.data[ParamData.ROUTING_OUTPUT_FLAVOR][ParamData.VALUE_INDEX])).getStringIndex());
          String fileOutput = null;
          
          i = allCalculatedIsochrons.size();
          long after = System.currentTimeMillis();
          routingOnItsWay = false;
          WWContext.getInstance().fireSetLoading(false, WWGnlUtilities.buildMessage("routing"));
          WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("isochrones-calculated", new String[] { Integer.toString(i), Long.toString(after - before) }) + "\n");          

          if (showProgressMonitor) // Then dispose progress bar
          {
            if (pm != null)
            {
              synchronized (pm)
              {
                try
                {
                  if (pm.getCurrent() != pm.getTotal())
                    pm.setCurrent(null, pm.getTotal());
                  WWContext.getInstance().removeApplicationListener(WWContext.getInstance().getAel4monitor());
                  WWContext.getInstance().setAel4monitor(null);
                  WWContext.getInstance().setMonitor(null);
                }
                catch (Exception ex)
                {
                  ex.printStackTrace();
                }
              }
            }
          }

          if (clipboardOption == ParamPanel.RoutingOutputList.ASK)
          {
            try { Thread.sleep(500L); } catch (InterruptedException ie) {} // Pas joli...
            RoutingOutputFlavorPanel rofp = new RoutingOutputFlavorPanel();              
            JOptionPane.showMessageDialog(instance, rofp, "Routing output", JOptionPane.QUESTION_MESSAGE);
            clipboardOption = rofp.getSelectedOption();
            fileOutput = rofp.getFileOutput();
          }

          // Reverse, for the clipboard
          boolean generateGPXRoute = true;
          String clipboardContent = "";
          if (clipboardOption == ParamPanel.RoutingOutputList.CSV)
            clipboardContent = "L;(dec L);G;(dec G);Date;UTC;TWS;TWD;BSP;HDG\n";
          else if (clipboardOption == ParamPanel.RoutingOutputList.GPX)
          {
            clipboardContent = 
            "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" + 
            "<gpx version=\"1.1\" \n" + 
            "     creator=\"OpenCPN\" \n" + 
            "	 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" + 
            "	 xmlns=\"http://www.topografix.com/GPX/1/1\" \n" + 
            "	 xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\" \n" + 
            "	 xmlns:opencpn=\"http://www.opencpn.org\">\n";
            if (generateGPXRoute)
            {
              Date d = new Date();
              clipboardContent += ("  <rte>\n" +
                                   "    <name>Weather Wizard route (" + WWGnlUtilities.SDF_DMY.format(d) + ")</name>\n" + 
                                   "    <type>Routing</type>\n" +
                                   "    <desc>Routing from Weather Wizard (generated " + d.toString() + ")</desc>\n" +
                                   "    <number>" + (d.getTime()) + "</number>\n");
            }
          }
          else if (clipboardOption == ParamPanel.RoutingOutputList.TXT)
          {
            Date d = new Date();
            clipboardContent += ("Weather Wizard route (" + WWGnlUtilities.SDF_DMY.format(d) + ") generated " + d.toString() + ")\n");
          }

          if (closestPoint != null && allCalculatedIsochrons != null)
          {
            Calendar cal = new GregorianCalendar();
            List<RoutingPoint> bestRoute = new ArrayList<RoutingPoint>(allCalculatedIsochrons.size());
            boolean go = true;
            RoutingPoint start = closestPoint;
            bestRoute.add(start);
            while (go)
            {
              RoutingPoint next = start.getAncestor();
              if (next == null)
                go = false;
              else
              {
                bestRoute.add(next);
                start = next;
              }
            }
            int routesize = bestRoute.size();
            String date = "", time = "";
            RoutingPoint rp = null;
            RoutingPoint ic = null; // Isochron Center
//          for (int r=0; r<routesize; r++) // 0 is the closest point, the last calculated
            for (int r=routesize - 1; r>=0; r--) // 0 is the closest point, the last calculated
            {
              rp = bestRoute.get(r);
              if (r == 0) // Last one
                ic = rp;
              else
                ic = bestRoute.get(r-1);
                
              if (rp.getDate() == null)
                date = time = "";
              else
              {
                cal.setTime(rp.getDate());
                    
                int year    = cal.get(Calendar.YEAR);
                int month   = cal.get(Calendar.MONTH);
                int day     = cal.get(Calendar.DAY_OF_MONTH);
                int hours   = cal.get(Calendar.HOUR_OF_DAY);
                int minutes = cal.get(Calendar.MINUTE);
                int seconds = cal.get(Calendar.SECOND);
                if (clipboardOption == ParamPanel.RoutingOutputList.CSV)
                {
                  date = WWGnlUtilities.DF2.format(month + 1) + "/" + WWGnlUtilities.DF2.format(day) + "/" + Integer.toString(year);
                  time = WWGnlUtilities.DF2.format(hours) + ":" + WWGnlUtilities.DF2.format(minutes);
                }
                else if (clipboardOption == ParamPanel.RoutingOutputList.GPX)
                {
                  date = Integer.toString(year) + "-" + 
                         WWGnlUtilities.DF2.format(month + 1) + "-" + 
                         WWGnlUtilities.DF2.format(day) + "T" +
                         WWGnlUtilities.DF2.format(hours) + ":" + 
                         WWGnlUtilities.DF2.format(minutes) + ":" +
                         WWGnlUtilities.DF2.format(seconds) + "Z";
                }
                else if (clipboardOption == ParamPanel.RoutingOutputList.TXT)
                {
                  date = rp.getDate().toString();
                }
              }    
              if (clipboardOption == ParamPanel.RoutingOutputList.CSV)
              {
                String lat = GeomUtil.decToSex(rp.getPosition().getL(), GeomUtil.SWING, GeomUtil.NS);
                String lng = GeomUtil.decToSex(rp.getPosition().getG(), GeomUtil.SWING, GeomUtil.EW);
                String tws = WWGnlUtilities.XX22.format(ic.getTws());
                String twd = Integer.toString(ic.getTwd());
                String bsp = WWGnlUtilities.XX22.format(ic.getBsp());
                String hdg = Integer.toString(ic.getHdg());
                    
                clipboardContent += (lat + ";" + 
                                     Double.toString(rp.getPosition().getL()) + ";" +
                                     lng + ";" + 
                                     Double.toString(rp.getPosition().getG()) + ";" +
                                     date + ";" + 
                                     time + ";" + 
                                     tws + ";" +
                                     twd + ";" + 
                                     bsp + ";" + 
                                     hdg + "\n");
              }
              else if (clipboardOption == ParamPanel.RoutingOutputList.GPX)
              {
                if (generateGPXRoute)
                {
                  NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);      
                  nf.setMaximumFractionDigits(2);
                  clipboardContent +=
                    ("       <rtept lat=\"" + rp.getPosition().getL() + "\" lon=\"" + rp.getPosition().getG() + "\">\n" + 
                    "            <name>" + WWGnlUtilities.DF3.format(routesize - r) + "_WW</name>\n" + 
                    "            <desc>Waypoint " + Integer.toString(routesize - r) + ";VMG=" + nf.format(ic.getBsp()) + ";</desc>\n" +
                //  "            <sym>triangle</sym>\n" + 
                    "            <sym>empty</sym>\n" + 
                    "            <type>WPT</type>\n" + 
                    "            <extensions>\n" + 
                    "                <opencpn:prop>A,0,1,1,1</opencpn:prop>\n" + 
                    "                <opencpn:viz>1</opencpn:viz>\n" + 
                    "                <opencpn:viz_name>0</opencpn:viz_name>\n" +
                    "            </extensions>\n" + 
                    "        </rtept>\n");
                }
                else
                {
                  clipboardContent +=
                    ("  <wpt lat=\"" + rp.getPosition().getL() + "\" lon=\"" + rp.getPosition().getG() + "\">\n" + 
                     "    <time>" + date + "</time>\n" + 
                     "    <name>" + WWGnlUtilities.DF3.format(r) + "_WW</name>\n" + 
                     "    <sym>triangle</sym>\n" + 
                     "    <type>WPT</type>\n" + 
                     "    <extensions>\n" + 
                     "            <opencpn:guid>142646-1706866-1264115693</opencpn:guid>\n" + 
                     "            <opencpn:viz>1</opencpn:viz>\n" + 
                     "            <opencpn:viz_name>1</opencpn:viz_name>\n" + 
                     "            <opencpn:shared>1</opencpn:shared>\n" + 
                     "    </extensions>\n" +
                     "  </wpt>\n");
                }
              }
              else if (clipboardOption == ParamPanel.RoutingOutputList.TXT)
              {
                String tws = WWGnlUtilities.XX22.format(ic.getTws());
                String twd = Integer.toString(ic.getTwd());
                String bsp = WWGnlUtilities.XX22.format(ic.getBsp());
                String hdg = Integer.toString(ic.getHdg());
                clipboardContent +=
                  (rp.getPosition().toString() + " : " + date + ", tws:" + tws + ", twd:" + twd + ", bsp:" + bsp + ", hdg:" + hdg + "\n");
              }
            }
            if (clipboardOption == ParamPanel.RoutingOutputList.GPX)
            {
              if (generateGPXRoute)
                clipboardContent += "  </rte>\n";
              clipboardContent +=
               ("</gpx>");
            }

//          commandPanelInstance.repaint();
            if (fileOutput != null && fileOutput.trim().length() > 0)            
            {
              try
              {
                BufferedWriter bw = new BufferedWriter(new FileWriter(fileOutput));              
                bw.write(clipboardContent + "\n");
                bw.close();
              }
              catch (Exception ex)
              {
                ex.printStackTrace();
              }
              WWContext.getInstance().fireSetStatus(WWGnlUtilities.buildMessage("routing-in-file", new String[] { fileOutput }));
            }
            else
            {
              Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
              StringSelection stringSelection = new StringSelection(clipboardContent);
              clipboard.setContents(stringSelection, null);    
  //          JOptionPane.showMessageDialog(null, "Routing is in the clipboard\n(Ctrl+V in any editor...)", "Routing completed", JOptionPane.INFORMATION_MESSAGE);
              WWContext.getInstance().fireSetStatus(WWGnlUtilities.buildMessage("routing-in-clip"));
            }

            WWContext.getInstance().fireRoutingAvailable(true, bestRoute);                        
            displayGRIBSlice(bestRoute);
            
            // End of the Routing.
            
  //        routingMode = false;
  //        JOptionPane.showMessageDialog(null, "Isochron Calculation completed", "Routing", 1);
//          System.out.println("RoutingMode:" + routingMode + ", from:" + (from==null?"":"not ") + "null" +
//                                                            ", to:" + (to==null?"":"not ") + "null" +
//                                                            ", allIsochrons:" + (allIsochrons==null?"":"not ") + "null");
          }
          repaint();
        }
      };
      isochronThread.setPriority(1);
      isochronThread.start();
//    JOptionPane.showMessageDialog(this, "Isochrons calculation is on its way...", "Routing", 1);
    } 
  }
   
  private boolean mouseDragDropOverriden = false;
  private boolean fromBeingDragged = false;
  private boolean toBeingDragged = false;
  private int intermediatePointDragged = -1;
  
  public boolean onEvent(EventObject e, int type)
  {
    MouseEvent me = (MouseEvent)e;
    int mask = me.getModifiers();
    
    if (type == ChartPanel.MOUSE_CLICKED)
    {
      int button = 0;
      if (isMouseInAltWindow((MouseEvent)e) && (button = isMouseOnAltWindowButton((MouseEvent)e)) != 0)
      {
      //      System.out.println("Button:" + button);
        if (button == CLOSE_IMAGE)
        {
          displayAltTooltip = false;
          System.setProperty("tooltip.option", "none");
        }
        else if (button == ZOOMEXPAND_IMAGE)
        {                                     
          altTooltipW *= 1.1;  
          altTooltipH *= 1.1;  
          dataFont = dataFont.deriveFont(dataFont.getSize() * 1.1f);
        }
        else if (button == ZOOMSHRINK_IMAGE)  
        {                                     
          altTooltipW /= 1.1;  
          altTooltipH /= 1.1;  
          dataFont = dataFont.deriveFont(dataFont.getSize() / 1.1f);
        }
        return false;
      }
      else
      {
        if (chartPanel.getMouseEdgeProximity() == ChartPanel.MOUSE_AWAY_FROM_EDGES)
        {  
          if ((mask & MouseEvent.BUTTON2_MASK) != 0 || (mask & MouseEvent.BUTTON3_MASK) != 0) // Right click
          {
    //      if (popup == null)
            {
              popup = new CommandPanelPopup(this, me.getX(), me.getY());
            }
            popup.show(chartPanel, me.getX(), me.getY());
            me.consume();
          }
          else if ((mask & MouseEvent.BUTTON1_MASK) != 0) // Left Click
          {
            int x = me.getX();
            int y = me.getY();
            
            GeoPoint here = chartPanel.getGeoPos(x, y);
    //      boatPosition = here;
    //      boatHeading = 45;
            if (boatPosition != null && ((Boolean) ParamPanel.data[ParamData.ROUTING_FROM_CURR_LOC][ParamData.VALUE_INDEX]).booleanValue())
            {
              if (allCalculatedIsochrons != null && !insertRoutingWP) // reset
                shutOffRouting(); // Caution: this one resets from & to.         
              from = boatPosition;
              if (!insertRoutingWP)
                to = null;
            }
            if (from == null && to == null)
              from = here;
            else if (from != null && to == null)
              to = here;
            else if (from != null && to != null)
            {
              if (!insertRoutingWP)
              {
                if (allCalculatedIsochrons != null) // reset
                  shutOffRouting(); // Caution: this one resets from & to.         
                from = here;
              }
              else
              {
                if (intermediateRoutingWP == null)
                  intermediateRoutingWP = new ArrayList<GeoPoint>();
                intermediateRoutingWP.add(here);
              }
            }
          }
        }
      }
    }
    else if (type == ChartPanel.MOUSE_MOVED)    
    {
      int x = me.getX();
      int y = me.getY();
      GeoPoint gp = chartPanel.getGeoPos(x, y);
      
      if (from != null)
      {
        Point pt = chartPanel.getPanelPoint(from);
        if (pt.x >= x-1 && pt.x <= x+1 && pt.y >= y-1 && pt.y <= y+1)
        {
//        System.out.println("On the from Point.");
          backupCursor = chartPanel.getCursor();
//        System.out.println("BackupCursor is" + (backupCursor!=null?" not":"") +  " null.");
          chartPanel.setCursor(cursorOverWayPoint);
        }
        else
        {
          if (backupCursor != null)
          {
            chartPanel.setCursor(backupCursor);
            chartPanel.setDefaultCursor();
          }
          backupCursor = null;
        }
      } 
      if (to != null && backupCursor == null)
      {
        Point pt = chartPanel.getPanelPoint(to);
        if (pt.x >= x-1 && pt.x <= x+1 && pt.y >= y-1 && pt.y <= y+1)
        {
//        System.out.println("On the to Point.");
          backupCursor = chartPanel.getCursor();
//        System.out.println("BackupCursor is" + (backupCursor!=null?" not":"") +  " null.");
          chartPanel.setCursor(cursorOverWayPoint);
        }
        else
        {
          if (backupCursor != null)
          {
            chartPanel.setCursor(backupCursor);
            chartPanel.setDefaultCursor();
          }
          backupCursor = null;
        }
      }
      if (intermediateRoutingWP != null && intermediateRoutingWP.size() > 0 && backupCursor == null)
      {
        int ptIdx = 0;
        for (GeoPoint gpt : intermediateRoutingWP)
        {
          Point pt = chartPanel.getPanelPoint(gpt);
          if (pt.x >= x-1 && pt.x <= x+1 && pt.y >= y-1 && pt.y <= y+1)
          {
//          System.out.println("Pressed on the Point[" + ptIdx + "], dragging !!");
            backupCursor = chartPanel.getCursor();
            chartPanel.setCursor(cursorOverWayPoint);
            intermediatePointDragged = ptIdx;
            break;
          }
          else
          {
            if (backupCursor != null)
            {
              chartPanel.setCursor(backupCursor);
              chartPanel.setDefaultCursor();
            }
            backupCursor = null;
          }
          ptIdx++;
        }
      }
      
      String header = displayAltTooltip?"":"<html>";
      String footer = displayAltTooltip?"":"</html>";
      String br = displayAltTooltip?"\n":"<br>";
      String mess = header;
      if (displayAltTooltip)
      {
        mess += (GeomUtil.decToSex(gp.getL(), GeomUtil.SWING, GeomUtil.NS) + "\n");
        mess += (GeomUtil.decToSex(gp.getG(), GeomUtil.SWING, GeomUtil.EW) + "\n");
      }
      if (wgd != null)
      {
        if (gribData == null || smoothingRequired)
        {
          gribData = wgd[gribIndex]; 
        }
        GribHelper.GribCondition gribPoint = null;
        try { gribPoint = GribHelper.gribLookup(gp, wgd, gribData.getDate()); }
        catch (Exception ignore) 
        
        {
          WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("wind-lookup", new String[] { gribData.getDate().toString(), ignore.getMessage() }) + "\n"); }

//        List<Integer> ar = gribData.getDataPointsAround(gp);   
//        if (ar != null)
//        {          
//          int yIdx = ar.get(0).intValue();
//          int xIdx = ar.get(1).intValue();
//          System.out.println(gp.toString() + " is between:");
//          System.out.println( gribData.getWindPointData()[yIdx][xIdx].toString() );
//          System.out.println( gribData.getWindPointData()[yIdx][xIdx + 1].toString() );
//          System.out.println( gribData.getWindPointData()[yIdx + 1][xIdx].toString() );
//          System.out.println( gribData.getWindPointData()[yIdx + 1][xIdx + 1].toString() );
//          System.out.println("------------------");
//        }
        
        if (gribPoint != null)
        {
          if (chartPanel.getProjection() != ChartPanelInterface.GLOBE_VIEW &&
              chartPanel.getProjection() != ChartPanelInterface.SATELLITE_VIEW)
          {     
//          Context.getInstance().fireGRIBWindValue(gribPoint.winddir, gribPoint.windspeed);
//          Context.getInstance().fireGRIBPRMSLValue(gribPoint.prmsl);
//          Context.getInstance().fireGRIB500HGTValue(gribPoint.hgt500);
//          Context.getInstance().fireGRIBWaveHeight(gribPoint.waves);
//          Context.getInstance().fireGRIBTemp(gribPoint.temp);
//          Context.getInstance().fireGRIBprate(gribPoint.rain);

            WWContext.getInstance().fireSetGRIBData(gribPoint.winddir, 
                                                    gribPoint.windspeed, 
                                                    gribPoint.prmsl, 
                                                    gribPoint.hgt500, 
                                                    gribPoint.waves, 
                                                    gribPoint.temp, 
                                                    gribPoint.rain);
            mess += ((isThereWind()?("wind:" + Math.round(gribPoint.windspeed) + "kts@" + gribPoint.winddir + " (" + (!displayAltTooltip?"<b>":"") + "F " + WWGnlUtilities.getBeaufort(gribPoint.windspeed) + (!displayAltTooltip?"</b>":"") + ")"):"") +
                    ((gribPoint.prmsl>0)?br  + "prmsl:" + WWGnlUtilities.DF2.format((gribPoint.prmsl / 100F)) + units[PRMSL]:"") +
                    ((gribPoint.waves>0)?br  + "waves:" + WWGnlUtilities.XXX12.format((gribPoint.waves / 100F)) + units[WAVES]:"") +
                    ((gribPoint.temp>0)?br   + "temp:" + WWGnlUtilities.XX22.format(WWGnlUtilities.convertTemperatureFromKelvin(gribPoint.temp, temperatureUnit)) + units[TEMPERATURE]:"") + // Originally in Kelvin
                    ((gribPoint.hgt500>0)?br + "500mb:" + WWGnlUtilities.DF2.format(gribPoint.hgt500) + units[HGT500]:"") + 
                    ((gribPoint.rain>0)?br   + "prate:" + WWGnlUtilities.XX22.format(gribPoint.rain * 3600F) + units[RAIN]:"") +
                     ((gribPoint.currentspeed>0)?br + "current:" + WWGnlUtilities.XXX12.format(gribPoint.currentspeed) + "kts@" + gribPoint.currentdir:""));
            if (!displayAltTooltip)
            {
              mess += "<hr>" +
                      "<center><small>x:" + gribPoint.horIdx + ", y:" + gribPoint.vertIdx + "</small></center>" +
                      "<hr>" + 
                      "<small>" + WWGnlUtilities.getSolarTimeTooltip(gp) + "</small>";
            }
            else
              mess += ((mess.trim().length() > 0?"\n":"") + WWGnlUtilities.getSolarTimeTooltip(gp));
            mess += footer;
            tooltipMess = mess;
  //        System.out.println(mess);
  //        System.out.println("TooltipEnabled:" + chartPanel.isPositionToolTipEnabled());
          }
          else
          {
            tooltipMess = mess.trim() + (mess.trim().length() > 0?"\n":"") + WWGnlUtilities.getSolarTimeTooltip(gp) + footer;
          }
        } 
        else
        {
          tooltipMess = null;
//        System.out.println("No Wind");
          tooltipMess = mess.trim() + (mess.trim().length() > 0?"\n":"") + WWGnlUtilities.getSolarTimeTooltip(gp) + footer;
        }
      } 
      else
      {
        tooltipMess = null;        
//      System.out.println("No GRIB Data");
        tooltipMess = mess.trim() + (mess.trim().length() > 0?"\n":"") + WWGnlUtilities.getSolarTimeTooltip(gp) + footer;
        // System.out.println("mess:" + mess + ", tooltip:" + tooltipMess);
      }
      if (displayAltTooltip)
        chartPanel.repaint();
    }
    else if (type == ChartPanel.MOUSE_DRAGGED)    
    {
//      System.out.println("Mouse Dragged");
//      MouseEvent me = (MouseEvent)e;
      if (altToooltipWindowBeingDragged)
      {
      //      System.out.println("Mouse in AltWin, " + ((MouseEvent)e).getX() + ", " + ((MouseEvent)e).getY());
        int x = ((MouseEvent)e).getX();
        int y = ((MouseEvent)e).getY();
        altTooltipX += (x - dragStartX);
        altTooltipY += (y - dragStartY);
        dragStartX = x;
        dragStartY = y;
        chartPanel.repaint();
        return false;
      }
      
      if (fromBeingDragged)
      {
        int x = ((MouseEvent)e).getX();
        int y = ((MouseEvent)e).getY();
        from = chartPanel.getGeoPos(x, y);
        wpBeingDragged = from;
        chartPanel.repaint();
        return false;
      }
      if (toBeingDragged)
      {
        int x = ((MouseEvent)e).getX();
        int y = ((MouseEvent)e).getY();
        to = chartPanel.getGeoPos(x, y);
        wpBeingDragged = to;
        chartPanel.repaint();
        return false;
      }
      if (intermediatePointDragged != -1)
      {
        int x = ((MouseEvent)e).getX();
        int y = ((MouseEvent)e).getY();
        GeoPoint gp = chartPanel.getGeoPos(x, y);
        intermediateRoutingWP.set(intermediatePointDragged, gp);
        wpBeingDragged = gp;
        chartPanel.repaint();
        return false;
      }
    }  
    else if (type == ChartPanel.MOUSE_RELEASED)
    {
      altToooltipWindowBeingDragged = false;
      fromBeingDragged              = false;
      toBeingDragged                = false;
      intermediatePointDragged      = -1;
      wpBeingDragged = null;
      if (backupCursor != null)
      {
        chartPanel.setCursor(backupCursor);
        chartPanel.setDefaultCursor();
      }
      backupCursor = null;
      
      // Proceed if left clicked, and mouse was dragged.
      if ((mask & MouseEvent.BUTTON2_MASK) == 0 && 
          (mask & MouseEvent.BUTTON3_MASK) == 0 &&
          chartPanel.getDraggedFromX() != -1 && 
          chartPanel.getDraggedFromY() != -1 &&
          chartPanel.getMouseDraggedType() == ChartPanel.MOUSE_DRAW_LINE_ON_CHART &&
          enableGRIBSlice)
      {
        if (wgd != null)
        {
//        System.out.println("Mouse Released, GRIB Slice...");    
          int fromX = chartPanel.getDraggedFromX();
          int fromY = chartPanel.getDraggedFromY();
          int toX = me.getX();
          int toY = me.getY();
  //      System.out.println("Slice boundaries: from (" + fromX + "/" + fromY + ") to (" + toX + "/" + toY + ")");
          if (gribData != null)
          {
            fromGRIBSlice = chartPanel.getGeoPos(fromX, fromY);
            toGRIBSlice   = chartPanel.getGeoPos(toX, toY);
            resizeSplitPane = true;
            displayGRIBSlice();
            if (true) // Disable defaut behavior
            {
              return false;
            }
          }
          else
            JOptionPane.showMessageDialog(this, "No GRIB Data to display...", "GRIB Slice", JOptionPane.WARNING_MESSAGE);
        }
      }
    }
    else if (type == ChartPanel.MOUSE_PRESSED)
    {
//    System.out.println("Pressed!");
      if (isMouseInAltWindow((MouseEvent)e))
      {
        altToooltipWindowBeingDragged = true;
        dragStartX = ((MouseEvent)e).getX();
        dragStartY = ((MouseEvent)e).getY();
      }
      
      if (from != null)
      {
        int x = ((MouseEvent)e).getX();
        int y = ((MouseEvent)e).getY();
        Point pt = chartPanel.getPanelPoint(from);
//      System.out.println("...and here.");
        if (pt.x >= x-1 && pt.x <= x+1 && pt.y >= y-1 && pt.y <= y+1)
        {
//        System.out.println("Pressed on the from Point, dragging !!");
          chartPanel.setCursor(cusorDraggingWayPoint);
          fromBeingDragged = true;
          return false;
        }
      }
      if (to != null)
      {
        int x = ((MouseEvent)e).getX();
        int y = ((MouseEvent)e).getY();
        Point pt = chartPanel.getPanelPoint(to);
//      System.out.println("...and here.");
        if (pt.x >= x-1 && pt.x <= x+1 && pt.y >= y-1 && pt.y <= y+1)
        {
//        System.out.println("Pressed on the to Point, dragging !!");
          chartPanel.setCursor(cusorDraggingWayPoint);
          toBeingDragged = true;
          return false;
        }
      }
      if (intermediateRoutingWP != null && intermediateRoutingWP.size() > 0)
      {
        int x = ((MouseEvent)e).getX();
        int y = ((MouseEvent)e).getY();
        int ptIdx = 0;
        for (GeoPoint gp : intermediateRoutingWP)
        {
          Point pt = chartPanel.getPanelPoint(gp);
          if (pt.x >= x-1 && pt.x <= x+1 && pt.y >= y-1 && pt.y <= y+1)
          {
//          System.out.println("Pressed on the Point[" + ptIdx + "], dragging !!");
            chartPanel.setCursor(cusorDraggingWayPoint);
            intermediatePointDragged = ptIdx;
            return false;
          }
          ptIdx++;
        }
      }
      
      if (chartPanel.getMouseDraggedEnabled() &&                            // Mouse Drag Enabled
          chartPanel.getMouseDraggedType() == ChartPanel.MOUSE_DRAG_ZOOM && // Set to Drag & Drop zoom
          (mask & MouseEvent.BUTTON1_MASK) != 0 &&                          // Left button
          chartPanel.getMouseEdgeProximity() == ChartPanel.MOUSE_AWAY_FROM_EDGES)
      {
        mouseDragDropOverriden = true;
        /*
        if ((mask & MouseEvent.SHIFT_MASK) != 0)
          System.out.println("Shift Down");
        else
          System.out.println("Shift Up");
        if ((mask & MouseEvent.CTRL_MASK) != 0)
          System.out.println("Control Down");
        else
          System.out.println("Control Up");
        */
        // Shift : Grab Scroll
        if ((mask & MouseEvent.SHIFT_MASK) != 0)
          chartPanel.setMouseDraggedType(ChartPanel.MOUSE_DRAG_GRAB_SCROLL);
        
        // Ctrl : Draw Line 
        if ((mask & MouseEvent.CTRL_MASK) != 0)
          chartPanel.setMouseDraggedType(ChartPanel.MOUSE_DRAW_LINE_ON_CHART);

        // Shift + Ctrl : Just Pointer
        if ((mask & MouseEvent.SHIFT_MASK) != 0 && (mask & MouseEvent.CTRL_MASK) != 0)
          chartPanel.setMouseDraggedEnabled(false);
      }
    }
    return true;
  }

  public void afterEvent(EventObject e, int type)
  {
    MouseEvent me = (MouseEvent)e;
    int mask = me.getModifiers();

    if (type == ChartPanel.MOUSE_RELEASED)
    {
      if (mouseDragDropOverriden && (mask & MouseEvent.BUTTON1_MASK) != 0)
      {
        mouseDragDropOverriden = false;
        chartPanel.setMouseDraggedType(ChartPanel.MOUSE_DRAG_ZOOM); 
      }
    }
  }

  private GRIBSlicePanel gsp = null;
  
  private void displayGRIBSlice()
  {
    displayGRIBSlice(null);
  }
  
  private void displayGRIBSlice(List<RoutingPoint> bestRoute)
  {
//  System.out.println("displayGRIBSlice...");
//  List<GribHelper.GribCondition> data2plot = new ArrayList<GribHelper.GribCondition>();
    List<DatedGribCondition> data2plot = new ArrayList<DatedGribCondition>();
    
    List<Double> bsp = null;
    List<Integer> hdg = null;
    List<Integer> twa = null;
    int fw = GRIBSlicePanel.DEFAULT_FORK_WIDTH; // That's for routing
    int dataOption = GRIBSlicePanel.GRIB_SLICE_OPTION;
    if (fromGRIBSlice != null && toGRIBSlice != null) // GRIB Slice
    {
//    fw = GRIBSlicePanel.DEFAULT_FORK_WIDTH;
      int nbSteps = 1000;
      for (int idx=0; idx <= nbSteps; idx++)
      {
        Point from = chartPanel.getPanelPoint(fromGRIBSlice);
        Point to   = chartPanel.getPanelPoint(toGRIBSlice);
        int x = from.x + (int)((idx * ((float)to.x - (float)from.x)) / (float)nbSteps);
        int y = from.y + (int)((idx * ((float)to.y - (float)from.y)) / (float)nbSteps);
//      System.out.println("x:" + x + ", y:" + y);
        GeoPoint gp = chartPanel.getGeoPos(x, y);
//      GribHelper.GribCondition gribPoint = null;
        DatedGribCondition gribPoint = null;
        try 
        { 
          gribPoint = new DatedGribCondition(GribHelper.gribLookup(gp, gribData)); 
//        System.out.println("1 - Adding to slice:" + (gribPoint.rain * 3600f));
          data2plot.add(gribPoint);
        }
        catch (Exception ignore) 
        {
          ignore.printStackTrace();
        }          
      }
    }
    else // This is a routing
    {
      dataOption = GRIBSlicePanel.ROUTING_OPTION;
      bsp = new ArrayList<Double>();
      hdg = new ArrayList<Integer>();
      twa = new ArrayList<Integer>();
      // Route is upside down, reverse it.
      int routeSize = bestRoute.size();
//    for (RoutingPoint rp : bestRoute)
      for (int i=routeSize - 1; i >= 0; i--)
      {
        RoutingPoint rp = bestRoute.get(i);
        GeoPoint gp = rp.getPosition();
//      GribHelper.GribCondition gribPoint = null;
        DatedGribCondition gribPoint = null;
        try 
        { 
          if (wgd != null)
          {
            gribPoint = new DatedGribCondition(GribHelper.gribLookup(gp, wgd, rp.getDate())); 
            gribPoint.setDate(rp.getDate());
          }
          else
          {
            System.out.println("Warning:" + this.getClass().getName() + ": wgd is null.");
            gribPoint = new DatedGribCondition(GribHelper.gribLookup(gp, gribData)); 
            gribPoint.setDate(rp.getDate());
          }
//        System.out.println("2 - Adding to slice:" + (gribPoint.rain * 3600f));
          data2plot.add(gribPoint); 
          bsp.add((i==(routeSize - 1))?new Double(bestRoute.get(i-1).getBsp()):new Double(rp.getBsp()));
          hdg.add((i==(routeSize - 1))?new Integer(bestRoute.get(i-1).getHdg()):new Integer(rp.getHdg()));
          twa.add((i==(routeSize - 1))?new Integer(bestRoute.get(i-1).getTwa()):new Integer(rp.getTwa()));
        }
        catch (Exception ignore) 
        {
          ignore.printStackTrace();
        }          
      }
    }
    if (gsp == null)
      gsp = new GRIBSlicePanel(data2plot, bsp, hdg, twa, dataOption, fw);
    else
    {
      gsp.setData(data2plot, bsp, hdg, twa, dataOption);
      gsp.setForkWidth(fw);
    }
//  gsp.setDataOption(dataOption);
//  gsp.setBsp(bsp);
    
    jSplitPane.setLeftComponent(gsp);
  }
  
  public String getMessForTooltip()
  {
    return tooltipMess;
  }

  public boolean replaceMessForTooltip()
  {
    return replace;
  }

  public void videoCompleted() {}
  public void videoFrameCompleted(Graphics g, Point p) {}

  public void setNLat(double nLat)
  {
    this.nLat = nLat;
  }

  public double getNLat()
  {
    return nLat;
  }

  public void setSLat(double sLat)
  {
    this.sLat = sLat;
  }

  public double getSLat()
  {
    return sLat;
  }

  public void setWLong(double wLong)
  {
    this.wLong = wLong;
  }

  public double getWLong()
  {
    return wLong;
  }

  public void setELong(double eLong)
  {
    this.eLong = eLong;
  }

  public double getELong()
  {
    return eLong;
  }

  public void setGribData(GribHelper.GribConditionData[] gd, String gribFileName)
  {
    if (gd == null)
    {
//    System.out.println("Stopping any progress bar");
      WWContext.getInstance().fireStopAnyLoading();
    }
    else
    {
      this.gribFileName = gribFileName;
      wgd = gd;
      alreadyAskedAboutGRIB = false;
      WWContext.getInstance().fireGribLoaded(gribFileName);
      gribIndex = 0;
      if (wgd != null && wgd.length > 1)
        WWContext.getInstance().fireMoreThanOneGrib();
    }    
    setCheckBoxes();       
  }

  public String getGRIBDataName()
  {
    return gribFileName;
  }

  public GribHelper.GribConditionData[] getGribData()
  { return wgd; }
  
  public void unsetGribData()
  {
    this.gribFileName = "";
    WWContext.getInstance().setGribFile(null);
    routingMode = false;
    routingForecastMode = false;    
    removeContourCheckBoxes();
    wgd = null;
    System.gc();
    WWContext.getInstance().fireGribUnloaded();    
    displayComboBox.setEnabled(false);
  }
  
  public void unsetFaxImage()
  {
    faxImage = null;
    removeCompositeCheckBoxes();
    removeContourCheckBoxes();
    System.gc();
    WWContext.getInstance().fireFaxUnloaded();
  }
  
  public GeoPoint getFrom()
  { return from; }
  public GeoPoint getTo()
  { return to; }
  
  public boolean thereIsFax2Display()
  { return (faxImage != null); }
  
  public boolean thereIsGRIB2Display()
  { return (wgd != null); }

  public synchronized void applyBoundariesChanges(double north,
                                                  double south,
                                                  double west,
                                                  double east)
  {
    nLat = north;
    sLat = south;
    wLong = west;
    eLong = east;
    applyBoundariesChanges();
  }
  
  public synchronized void applyBoundariesChanges()
  {
    if (chartPanel.getProjection() != ChartPanel.GLOBE_VIEW &&
        chartPanel.getProjection() != ChartPanel.SATELLITE_VIEW)
    chartPanel.setWidthFromChart(nLat, sLat, wLong, eLong);
    chartPanel.setEastG(eLong);
    chartPanel.setWestG(wLong);
    chartPanel.setNorthL(nLat);
    chartPanel.setSouthL(sLat);
    chartPanel.repaint();    
    displayStatus();
  }

  public void zoomFactorHasChanged(double d)
  {
//  System.out.println("Zooming to " + d);
    for (int i=0; faxImage!=null && i<faxImage.length; i++)
      faxImage[i].imageScale *= d;
  }

  public void chartDDZ(double top, double bottom, double left, double right){}

  public void setDrawGRIB(boolean b)
  {
    this.drawGRIB = b;   
  }
  
  public boolean isDrawGRIB()
  {
    return this.drawGRIB;   
  }
  
  public void setDrawChart(boolean drawChart)
  {
    this.drawChart = drawChart;
  }

  public boolean isDrawChart()
  {
    return drawChart;
  }

  private boolean thereIsAnOpaqueFax()
  {
    boolean b = false;
    for (int i=0; faxImage != null && i<faxImage.length; i++)
    {
      if (faxImage[i] != null && !faxImage[i].transparent)
      {
        b = true;
        break;
      }
    }
    return b;
  }
  
  public void setDisplayPageSize(boolean displayPageSize)
  {
    this.displayPageSize = displayPageSize;
    chartPanel.repaint();
  }

  public boolean isDisplayPageSize()
  {
    return displayPageSize;
  }

  public void setDrawIsochrons(boolean b)
  {
    this.drawIsochrons = b;
  }

  public boolean isDrawIsochrons()
  {
    return drawIsochrons;
  }

  public void setDrawBestRoute(boolean b)
  {
    this.drawBestRoute = b;
  }

  public boolean isDrawBestRoute()
  {
    return drawBestRoute;
  }

  public void setPostitOnRoute(boolean postitOnRoute)
  {
    this.postitOnRoute = postitOnRoute;
    repaint();
  }

  public boolean isPostitOnRoute()
  {
    return postitOnRoute;
  }

  public void setWHRatio(double whRatio)
  {
    WWContext.getInstance().fireLogging("Setting ratio to " + whRatio + "\n");
    this.whRatio = whRatio;
  }

  public double getWHRatio()
  {
    return whRatio;
  }

  /**
   * @deprecated
   * @param windOnly
   */
  @Deprecated
  private void setWindOnly(boolean windOnly)
  {
    this.windOnly = windOnly;
    if (this.windOnly)
    {
      this.displayPrmsl = false;
      this.display500mb = false;
      this.displayWaves = false;
      this.displayTemperature = false;
      
      this.displayContourLines = false;
    }
  }

  /**
   * @deprecated
   * @return
   */
  @Deprecated
  private boolean isWindOnly()
  {
    return windOnly;
  }

  private void setDisplayContour(boolean b)
  {
    this.displayContourLines = b;
  }
  
  public void setPlotNadir(boolean plotNadir)
  {
    this.plotNadir = plotNadir;
  }

  public boolean isPlotNadir()
  {
    return plotNadir;
  }

  public void setDisplayPrmsl(boolean displayPrmsl)
  {
    this.displayPrmsl = displayPrmsl;
  }

  public boolean isDisplayPrmsl()
  {
    return displayPrmsl;
  }

  public void setDisplay500mb(boolean display500mb)
  {
    this.display500mb = display500mb;
  }

  public boolean isDisplay500mb()
  {
    return display500mb;
  }

  public void setDisplayWaves(boolean displayWaves)
  {
    this.displayWaves = displayWaves;
  }

  public boolean isDisplayWaves()
  {
    return displayWaves;
  }

  public void setDisplayTemperature(boolean displayTemperature)
  {
    this.displayTemperature = displayTemperature;
  }

  public boolean isDisplayTemperature()
  {
    return displayTemperature;
  }

  public void setDisplayRain(boolean b)
  {
    this.displayRain = b;
  }

  public boolean isDisplayRain()
  {
    return displayRain;
  }

  public void setCurrentComment(String currentComment)
  {
    this.currentComment = currentComment;
  }

  public String getCurrentComment()
  {
    return currentComment;
  }

  public void setDisplayWindSpeedValue(boolean dwsv)
  {
    this.displayWindSpeedValue = dwsv;
  }

  public boolean isDisplayWindSpeedValue()
  {
    return displayWindSpeedValue;
  }

  public void routingNotification(List<List<RoutingPoint>> o, RoutingPoint close)
  {
    allCalculatedIsochrons = o;
    closestPoint = close;
    chartPanel.repaint();
  }

  public void setShowPlaces(boolean showPlaces)
  {
    this.showPlaces = showPlaces;
  }

  public boolean isShowPlaces()
  {
    return showPlaces;
  }

  public void setThereIsWind(boolean thereIsWind)
  {
    this.thereIsWind = thereIsWind;
  }

  public boolean isThereWind()
  {
    return thereIsWind;
  }

  public void setThereIsPrmsl(boolean thereIsPrmsl)
  {
    this.thereIsPrmsl = thereIsPrmsl;
  }

  public boolean isTherePrmsl()
  {
    return thereIsPrmsl;
  }

  public void setThereIs500mb(boolean thereIs500mb)
  {
    this.thereIs500mb = thereIs500mb;
  }

  public boolean isThere500mb()
  {
    return thereIs500mb;
  }

  public void setThereIsWaves(boolean thereIsWaves)
  {
    this.thereIsWaves = thereIsWaves;
  }

  public boolean isThereWaves()
  {
    return thereIsWaves;
  }

  public void setThereIsTemperature(boolean thereIsTemperature)
  {
    this.thereIsTemperature = thereIsTemperature;
  }

  public boolean isThereTemperature()
  {
    return thereIsTemperature;
  }

  public void setThereIsRain(boolean b)
  {
    this.thereIsRain = b;
  }

  public boolean isThereRain()
  {
    return thereIsRain;
  }

  public void setThereIsCurrent(boolean b)
  {
    this.thereIsCurrent = b;
  }

  public boolean isThereCurrent()
  {
    return thereIsCurrent;
  }

  public int getGribIndex()
  {
    return gribIndex;
  }

  public ChartPanel getChartPanel()
  {
    return chartPanel;
  }

  public void setEnableGRIBSlice(boolean enableGRIBSlice)
  {
    this.enableGRIBSlice = enableGRIBSlice;
    if (!enableGRIBSlice)
    {
      fromGRIBSlice = null;
      toGRIBSlice = null;
    }
    else
    {
      // Set the mouse
      WWContext.getInstance().fireSetCursor(ChartCommandPanelToolBar.CROSS_HAIR_CURSOR);
    }
    from = null;
    to = null;
    closest = null;
    allCalculatedIsochrons = null;
    bestRoute = null;
    eraseRoutingBoat();
    WWContext.getInstance().fireRoutingAvailable(false, null);

    resizeSplitPane = true;
  }

  public boolean isEnableGRIBSlice()
  {
    return enableGRIBSlice;
  }

  public void setUseThickWind(boolean useThickWind)
  {
    this.useThickWind = useThickWind;
  }

  public boolean isUseThickWind()
  {
    return useThickWind;
  }

  public void setDisplayTws(boolean displayTws)
  {
    this.displayTws = displayTws;
  }

  public boolean isDisplayTws()
  {
    return displayTws;
  }

  public CommandPanel.FaxImage[] getFaxImage()
  {
    return faxImage;
  }

  public void setShowSMStations(boolean showSMStations)
  {
    this.showSMStations = showSMStations;
  }

  public boolean isShowSMStations()
  {
    return showSMStations;
  }

  public void setShowWeathertations(boolean showWeatherStations)
  {
    this.showWeatherStations = showWeatherStations;
  }

  public boolean isShowWeatherStations()
  {
    return showWeatherStations;
  }

  public void setGribRequest(String gribRequest)
  {
    this.gribRequest = gribRequest;
  }

  public String getGribRequest()
  {
    return gribRequest;
  }

  public JScrollPane getChartPanelScrollPane()
  {
    return chartPanelScrollPane;
  }

  public void setDisplayAltTooltip(boolean displayAltTooltip)
  {
    this.displayAltTooltip = displayAltTooltip;
  }

  public boolean isDisplayAltTooltip()
  {
    return displayAltTooltip;
  }

  public void setDisplayContourTWS(boolean displayContourTWS)
  {
    this.displayContourTWS = displayContourTWS;
    setDisplayContour(displayContourTWS || 
                      displayContourPRMSL ||
                      displayContour500mb || 
                      displayContourWaves ||
                      displayContourTemp ||
                      displayContourPrate);
  }

  public void setDisplayContourPRMSL(boolean displayContourPRMSL)
  {
    this.displayContourPRMSL = displayContourPRMSL;
    setDisplayContour(displayContourTWS || 
                      displayContourPRMSL ||
                      displayContour500mb || 
                      displayContourWaves ||
                      displayContourTemp ||
                      displayContourPrate);
  }

  public boolean isDisplayContourPRMSL()
  {
    return displayContourPRMSL;
  }

  public void setDisplayContour500mb(boolean displayContour500mb)
  {
    this.displayContour500mb = displayContour500mb;
    setDisplayContour(displayContourTWS || 
                      displayContourPRMSL ||
                      displayContour500mb || 
                      displayContourWaves ||
                      displayContourTemp ||
                      displayContourPrate);
  }

  public boolean isDisplayContour500mb()
  {
    return displayContour500mb;
  }

  public void setDisplayContourWaves(boolean displayContourWaves)
  {
    this.displayContourWaves = displayContourWaves;
    setDisplayContour(displayContourTWS || 
                      displayContourPRMSL ||
                      displayContour500mb || 
                      displayContourWaves ||
                      displayContourTemp ||
                      displayContourPrate);
  }

  public boolean isDisplayContourWaves()
  {
    return displayContourWaves;
  }

  public void setDisplayContourTemp(boolean displayContourTemp)
  {
    this.displayContourTemp = displayContourTemp;
    setDisplayContour(displayContourPRMSL ||
                      displayContour500mb || 
                      displayContourWaves ||
                      displayContourTemp ||
                      displayContourPrate);
  }

  public boolean isDisplayContourTemp()
  {
    return displayContourTemp;
  }

  public void setDisplayContourPrate(boolean displayContourPrate)
  {
    this.displayContourPrate = displayContourPrate;
    setDisplayContour(displayContourTWS || 
                      displayContourPRMSL ||
                      displayContour500mb || 
                      displayContourWaves ||
                      displayContourTemp ||
                      displayContourPrate);
  }

  public boolean isDisplayContourPrate()
  {
    return displayContourPrate;
  }

  public void setDisplay3DTws(boolean display3DTws)
  {
    this.display3DTws = display3DTws;
  }

  public boolean isDisplay3DTws()
  {
    return display3DTws;
  }

  public void setDisplay3DPrmsl(boolean display3DPrmsl)
  {
    this.display3DPrmsl = display3DPrmsl;
  }

  public boolean isDisplay3DPrmsl()
  {
    return display3DPrmsl;
  }

  public void setDisplay3D500mb(boolean display3D500mb)
  {
    this.display3D500mb = display3D500mb;
  }

  public boolean isDisplay3D500mb()
  {
    return display3D500mb;
  }

  public void setDisplay3DWaves(boolean display3DWaves)
  {
    this.display3DWaves = display3DWaves;
  }

  public boolean isDisplay3DWaves()
  {
    return display3DWaves;
  }

  public void setDisplay3DTemperature(boolean display3DTemperature)
  {
    this.display3DTemperature = display3DTemperature;
  }

  public boolean isDisplay3DTemperature()
  {
    return display3DTemperature;
  }

  public void setDisplay3DRain(boolean display3DRain)
  {
    this.display3DRain = display3DRain;
  }

  public boolean isDisplay3DRain()
  {
    return display3DRain;
  }

  public boolean isDisplayContourTWS()
  {
    return displayContourTWS;
  }

  public List<List<RoutingPoint>> getAllCalculatedIsochrons()
  {
    return allCalculatedIsochrons;
  }
  
  public void shutOffRouting()
  {
    from = null;
    to = null;
    allCalculatedIsochrons = null;
    bestRoute = null;
    fromGRIBSlice = null;
    toGRIBSlice = null;
    WWContext.getInstance().fireRoutingAvailable(false, null);
    setEnableGRIBSlice(false);
    eraseRoutingBoat();
    jSplitPane.setLeftComponent(dummyGribSlicePlaceHolder);
    jSplitPane.setDividerLocation(0);
  }

  public void setGPXData(List<GeoPoint> gpxData)
  {
    this.gpxData = gpxData;
  }

  public List<GeoPoint> getGPXData()
  {
    return gpxData;
  }

  public int getBlurSharpOption()
  {
    return blurSharpOption;
  }
  
  public void removeComposite()
  {
    WWContext.getInstance().setCurrentComposite("");

    this.unsetFaxImage();
    this.unsetGribData();
    this.setNLat(65D);
    this.setSLat(-65D);
    this.setWLong(-180D);
    this.setELong(180D);
    this.applyBoundariesChanges();
    this.faxImage = null;
    this.wgd = null;
    this.gribFileName = "";
    this.chartPanel.repaint();    
  }

  public void setCheckBoxPanelOption(int checkBoxPanelOption)
  {
    this.checkBoxPanelOption = checkBoxPanelOption;
    setCheckBoxes();
  }

  public int getCheckBoxPanelOption()
  {
    return checkBoxPanelOption;
  }

  public static class FaxImage implements Cloneable
  {
    public Image faxImage;
    public String fileName;
    public String faxTitle; // From the template, like "North Pacific Surface"
    public String faxOrigin; // From template, in case it's dynamic, contains the URL.
    public String comment;
    public Color color;
    public boolean show;
    public boolean transparent;
    public boolean colorChange;
    public double imageScale = 1.0;
    public int imageHOffset = 0;
    public int imageVOffset = 0;
    public double imageRotationAngle = 0D;
    
    protected FaxImage clone() throws CloneNotSupportedException
    {
      return (FaxImage)super.clone();
    }
  }

  /**
   * Contour lines detection in the GRIBs
   */
  class IsoPointsThread extends Thread
  {
    public IsoPointsThread(GribHelper.GribConditionData gd)
    {
      super("contour-line-detector");
      this.gribData = gd;
    }
    GribHelper.GribConditionData gribData = null;
    
    public void setGribData(GribHelper.GribConditionData gd)
    { this.gribData = gd; }
    
    public void run()
    {
      long before = System.currentTimeMillis();
      if (animateThread == null)
        WWContext.getInstance().fireSetLoading(true, WWGnlUtilities.buildMessage("calculating"));
//    List<ArrayList<GeoPoint>> isopoints = null;
      // TWS
      if (gribData != null && displayContourTWS)
        islandsTws = GRIBDataUtil.generateIsoTWS(gribData, ((ParamPanel.ContourLinesList)ParamPanel.data[ParamData.ISO_TWS_LIST][ParamData.VALUE_INDEX]).getIntValues()); 
      else
        islandsTws = null;
      // Pressure
      if (isTherePrmsl() && displayContourPRMSL)
      {
        islandsPressure = GRIBDataUtil.generateIsobars(gribData, ((ParamPanel.ContourLinesList)ParamPanel.data[ParamData.ISOBARS_LIST][ParamData.VALUE_INDEX]).getIntValues());
        prmslBumps = CurveUtil.getBumps(gribData, CurveUtil.PRMSL);
      }
      else
        islandsPressure = null;
      // 500mb  
      if (isThere500mb() && displayContour500mb)
      {                                       
        islands500mb = GRIBDataUtil.generateIso500(gribData, ((ParamPanel.ContourLinesList)ParamPanel.data[ParamData.ISOHEIGHT500_LIST][ParamData.VALUE_INDEX]).getIntValues());
        hgt500Bumps = CurveUtil.getBumps(gribData, CurveUtil.HGT500);
      }
      else
        islands500mb = null;
      // Waves  
      if (isThereWaves() && displayContourWaves)
        islandsWave = GRIBDataUtil.generateIsowaves(gribData, ((ParamPanel.ContourLinesList)ParamPanel.data[ParamData.ISOHEIGHTWAVES_LIST][ParamData.VALUE_INDEX]).getIntValues());
      else
        islandsWave = null;
      // Temperature  
      if (isThereTemperature() && displayContourTemp)
        islandsTemp = GRIBDataUtil.generateIsotherm(gribData, ((ParamPanel.ContourLinesList)ParamPanel.data[ParamData.ISOTEMP_LIST][ParamData.VALUE_INDEX]).getIntValues());
      else
        islandsTemp = null;
      //Prate
      if (isThereRain() && displayContourPrate)
        islandsPrate = GRIBDataUtil.generateIsorain(gribData, ((ParamPanel.ContourLinesList)ParamPanel.data[ParamData.ISOPRATE_LIST][ParamData.VALUE_INDEX]).getIntValues());
      else
        islandsPrate = null;
      long after = System.currentTimeMillis();
      WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("all-calculated-in", new String[] { Long.toString(after - before) }) + "\n");
      if (animateThread == null)
        WWContext.getInstance().fireSetLoading(false, WWGnlUtilities.buildMessage("calculating"));
      chartPanel.repaint();
    }
  }
  
  class AnimateThread extends Thread
  {
    public AnimateThread()
    {
      super("grib-animator");
//    originalwgd = wgd;
//    wgd = GribHelper.smoothGRIBinTime(wgd, 12); 
    }
    
//  private GribHelper.GribConditionData originalwgd[] = null;

    private boolean go = true;
    public void run()
    {
      while (go)
      {
        gribIndex++;
        if (gribIndex >= wgd.length)
          gribIndex = 0;
        smoothingRequired = true;
        updateGRIBDisplay();
        try { Thread.sleep(500L); } // was 2000
        catch (Exception ignore) { System.out.println("Interrupted"); }        
      }
    }
    
    public void freeze()
    { 
      go = false; 
//    wgd = originalwgd; // Restore
//    gribIndex = 0;
//    updateGRIBDisplay();
    }
  }
}
