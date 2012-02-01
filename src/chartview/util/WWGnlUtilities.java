package chartview.util;

import astro.calc.GeoPoint;

import chart.components.ui.ChartPanel;

import chartview.ctx.ApplicationEventListener;
import chartview.ctx.WWContext;

//import chartview.ctx.WWContext.ToolFileFilter;

import chartview.gui.AdjustFrame;
import chartview.gui.left.FileTypeHolder;
import chartview.gui.right.CommandPanel;
import chartview.gui.util.dialog.ExitPanel;
import chartview.gui.util.dialog.OneColumnTablePanel;
import chartview.gui.util.dialog.PositionInputPanel;
import chartview.gui.util.dialog.TwoFilePanel;
import chartview.gui.util.dialog.UserExitTablePanel;
import chartview.gui.util.dialog.WazUrlPanel;
import chartview.gui.util.dialog.places.PlacesTablePanel;
import chartview.gui.util.param.ParamData;
import chartview.gui.util.param.ParamPanel;

//import chartview.util.WWGnlUtilities.BoatPosition;
import chartview.util.http.HTTPClient;
import chartview.util.local.WeatherAssistantResourceBundle;

//import chartview.util.progress.ProgressMonitor;

import chartview.util.progress.ProgressUtil;

import chartview.util.nmeaclient.BoatPositionSerialClient;
//import chartview.util.serial.WWNMEAReader;

import chartview.util.nmeaclient.BoatPositionTCPClient;

import chartview.util.nmeaclient.BoatPositionUDPClient;

import coreutilities.Utilities;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;

import java.awt.Stroke;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

//import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
//import java.io.ByteArrayInputStream;
import java.io.File;

import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;

import java.net.URL;
import java.net.URLConnection;

import java.security.MessageDigest;

import java.text.DecimalFormat;

import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
//import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import oracle.xml.parser.v2.DOMParser;
import oracle.xml.parser.v2.NSResolver;
import oracle.xml.parser.v2.XMLDocument;

import oracle.xml.parser.v2.XMLElement;

import oracle.xml.parser.v2.XMLParser;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class WWGnlUtilities
{
  public final static String REGEXPR_PROPERTIES_FILE = "regexpr.properties";
  public final static String COMPOSITE_FILTER        = "composite.filter";
  public final static String FAX_NAME_FILTER         = "fax.filter";
  
  public final static DecimalFormat XX14   = new DecimalFormat("##0.0000");
  public final static DecimalFormat DF2    = new DecimalFormat("00");
  public final static DecimalFormat DF3    = new DecimalFormat("000");
  public final static DecimalFormat XX22   = new DecimalFormat("##00.00");
  public final static DecimalFormat XXX12  = new DecimalFormat("###0.00");
  public final static DecimalFormat XXXX12 = new DecimalFormat("####0.00");

  public final static DecimalFormat BIG_DOUBLE = new DecimalFormat("####0.0000000000");
  static
  {
    DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.ENGLISH);              
    BIG_DOUBLE.setDecimalFormatSymbols(dfs);    
  }
  
  public final static SimpleDateFormat SDF_UT   = new SimpleDateFormat("'UTC' d MMM yyyy HH:mm");
  public final static SimpleDateFormat SDF_UT_bis = new SimpleDateFormat("'UTC' EEE d MMM yyyy HH:mm");
  public final static SimpleDateFormat SDF_UT_day = new SimpleDateFormat("'UTC' d MMM yyyy HH:mm (EEE)");
  public final static SimpleDateFormat SDF_UT_2 = new SimpleDateFormat("yy MMM d (HH:mm 'UTC')");
  public final static SimpleDateFormat SDF_UT_3 = new SimpleDateFormat("d-MMM-yyyy (HH:mm 'UTC')");
  public final static SimpleDateFormat SDF_     = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_z");
  public final static SimpleDateFormat SDF      = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_S_z");
  public final static SimpleDateFormat SDF_2    = new SimpleDateFormat("yyyy MMM d (HH:mm z)");
  public final static SimpleDateFormat SDF_SOLAR = new SimpleDateFormat("d MMM yyyy HH:mm");
  public final static SimpleDateFormat SDF_DMY  = new SimpleDateFormat("d MMM yyyy");

  public final static Color PURPLE = new Color(165, 0, 165);
  
  public final static String USEREXITS_FILE_NAME = "." + File.separator + "config" + File.separator + "user-exit.xml";
  public final static String STRUCTURE_FILE_NAME = "." + File.separator + "config" + File.separator + "structure.xml";
  public final static String VISUAL_CONFIG_FILE_NAME = "." + File.separator + "config" + File.separator + "visual-config.xml";  
  
  public final static String[] MONTH =
  { 
    WWGnlUtilities.buildMessage("jan"), 
    WWGnlUtilities.buildMessage("feb"), 
    WWGnlUtilities.buildMessage("mar"), 
    WWGnlUtilities.buildMessage("apr"), 
    WWGnlUtilities.buildMessage("may"), 
    WWGnlUtilities.buildMessage("jun"), 
    WWGnlUtilities.buildMessage("jul"), 
    WWGnlUtilities.buildMessage("aug"), 
    WWGnlUtilities.buildMessage("sep"), 
    WWGnlUtilities.buildMessage("oct"), 
    WWGnlUtilities.buildMessage("nov"), 
    WWGnlUtilities.buildMessage("dec") 
  };

  public final static String[] WEEK =
  { 
    WWGnlUtilities.buildMessage("sunday"), 
    WWGnlUtilities.buildMessage("monday"), 
    WWGnlUtilities.buildMessage("tuesday"), 
    WWGnlUtilities.buildMessage("wednesday"), 
    WWGnlUtilities.buildMessage("thursday"), 
    WWGnlUtilities.buildMessage("friday"), 
    WWGnlUtilities.buildMessage("saturday") 
  };

  private final static int[][] WIND_COLOR_VALUES =
  {
   {255, 255, 255 },
   {208, 244, 250 },
   {161, 233, 246 },
   {115, 222, 241 },
   {68, 211, 237 },
   {21, 200, 232 },
   {21, 207, 223 },
   {20, 214, 214 },
   {20, 220, 204 },
   {19, 227, 195 },
   {19, 234, 186 },
   {25, 234, 153 },
   {31, 233, 120 },
   {36, 233, 87 },
   {42, 232, 54 },
   {48, 232, 21 },
   {81, 233, 20 },
   {113, 235, 18 },
   {146, 236, 17 },
   {178, 238, 15 },
   {211, 239, 14 },
   {215, 227, 15 },
   {219, 215, 17 },
   {224, 204, 18 },
   {228, 192, 20 },
   {232, 180, 21 },
   {232, 164, 21 },
   {232, 148, 21 },
   {232, 132, 21 },
   {232, 116, 21 },
   {232, 100, 21 },
   {222, 82, 17 },
   {211, 63, 13 },
   {201, 45, 8 },
   {190, 26, 4 },
   {180, 8, 0 },
   {173, 7, 0 },
   {167, 6, 0 },
   {160, 6, 0 },
   {154, 5, 0 },
   {147, 4, 0 },
   {147, 4, 32 },
   {147, 4, 64 },
   {148, 4, 97 },
   {148, 4, 129 },
   {148, 4, 161 },
   {118, 3, 129 },
   {89, 2, 97 },
   {59, 2, 64 },
   {30, 1, 32 },
   {0, 0, 0 }
  };
  
  public static String truncateBigFileName(String orig)
  {
    String trunc = orig;
    if (orig != null && orig.length() > 6)
      trunc = orig.substring(0, 6);
    trunc += "...";
    if (orig.indexOf(File.separator) > -1)
      trunc += (orig.substring(orig.lastIndexOf(File.separator)));
    else
      trunc += (orig.substring(orig.lastIndexOf("/")));
    return trunc;
  }

  public static void installLookAndFeel() 
  {
    try
    {
      String ui = "config" + File.separator + "UI.xml";
      DOMParser parser = WWContext.getInstance().getParser();
      synchronized (parser)
      {
        parser.setValidationMode(XMLParser.NONVALIDATING);
        parser.parse(new File(ui).toURI().toURL());
        XMLDocument doc = parser.getDocument();
        NodeList nl = doc.selectNodes("/lafs/laf");
        if (nl != null)
        {
          for (int i = 0; i < nl.getLength(); i++)
          {
            Node n = nl.item(i);
            String name = ((Element) n).getAttribute("name");
            String file = ((Element) n).getAttribute("file");
            String className = ((Element) n).getAttribute("class");
            WWContext.getInstance().fireLogging("Installing [" + name + "] Look & Feel from " + ui);
            UIManager.installLookAndFeel(name, className);
            Utilities.addURLToClassPath(new File(file).toURI().toURL());
          }
        }
      }
    }
    catch (FileNotFoundException fnfe)
    {
      WWContext.getInstance().fireLogging("No Look & Feel file");
    }
    catch (Exception ex)
    {
      WWContext.getInstance().fireExceptionLogging(ex);
      ex.printStackTrace();
    }
  }

  /**
   * Get the direction
   * @param x horizontal displacement
   * @param y vertical displacement
   * @return the angle, in degrees
   */
  public static double getDir(float x, float y)
  {
    double dir = 0.0D;
    if (y != 0)
      dir = Math.toDegrees(Math.atan((double)x / (double)y));
    if (x <= 0 || y <= 0)
    {
      if (x > 0 && y < 0)
        dir += 180D;
      else if (x < 0 && y > 0)
        dir += 360D;
      else if (x < 0 && y < 0)
        dir += 180D;
      else if (x == 0)
      {
        if (y > 0)
          dir = 0.0D;
        else
          dir = 180D;
      } 
      else if (y == 0)
      {
        if (x > 0)
          dir = 90D;
        else
          dir = 270D;
      }
    }
    dir += 180D;
    while (dir >= 360D) dir -= 360D;
    return dir;
  }

  public static Font tryToLoadFont(String fontName, Object parent) 
  {
    final String RESOURCE_PATH = "resources" + "/";
    try 
    {
      String fontRes = RESOURCE_PATH + fontName;
      InputStream fontDef = parent.getClass().getResourceAsStream(fontRes);
      if (fontDef == null) 
      {
        throw new NullPointerException("Could not find font resource \"" + fontName +
                                       "\"\n\t\tin \"" + fontRes +
                                       "\"\n\t\tfor \"" + parent.getClass().getName() +
                                       "\"\n\t\ttry: " + parent.getClass().getResource(fontRes));
      } 
      else
        return Font.createFont(Font.TRUETYPE_FONT, fontDef);
    } 
    catch (FontFormatException e) 
    {
      System.err.println("getting font " + fontName);
      e.printStackTrace();
    } 
    catch (IOException e) 
    {
      System.err.println("getting font " + fontName);
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * Builds a regular message based on the id in the resource bundle.
   */
  public static String buildMessage(String id)
  {
    String translated = id;
    try { translated = WeatherAssistantResourceBundle.getWeatherAssistantResourceBundle().getString(id); }
    catch (Exception ex) 
    { 
      translated = "Not found in resource bundle:" + id; 
      ex.printStackTrace();
    }
    return translated; 
  }

  /**
   * Builds a patched message.
   * The id is the id of the message in the resource bundle, which must look like
   * "akeu coucou {$1} larigou {$2}"
   * In that case, data must be 2 entries long, the first entry will patch {$1},
   * the second one {$2}.
   * 
   * Note:
   * If there is a string like "xx {$1} and {$1}, {$2}",
   * data needs to be only 2 entries big.
   */
  public static String buildMessage(String id, String[] data)
  {
    String mess = id;
    try
    { 
      mess = WeatherAssistantResourceBundle.getWeatherAssistantResourceBundle().getString(id); 
      for (int i=0; i<data.length; i++)
      {
        String toReplace = "{$" + Integer.toString(i+1) + "}";
  //    System.out.println("Replacing " + toReplace + " with " + data[i] + " in " + mess);
        mess = Utilities.replaceString(mess, toReplace, data[i]);
      }
    }
    catch (Exception ex)
    { 
      mess = "Not found in resource bundle:" + id; 
      ex.printStackTrace();
    }
    return mess;
  }
  
  public static void setLabelAndMnemonic(String labelKey, JMenuItem mi)
  {
    String s = buildMessage(labelKey);
    int ampIdx = s.indexOf('&');
    if (ampIdx > -1)
    {
      char m = s.charAt(ampIdx + 1);
      mi.setMnemonic(m);
      s = s.replace(new String(new char[] { s.charAt(ampIdx) }), "");
    }    
    mi.setText(s);
  }
    
  /**
   * For SailMail requests
   * 
   * @param gribRequest
   * @return
   */
  public static String generateGRIBRequest(String gribRequest)
  {
    String request = "";
    try
    {
      String inputString = gribRequest + " LeDiouris/6ce9Ci7t";
      
      MessageDigest digest = MessageDigest.getInstance("MD5");
      byte[] buff = new byte[inputString.length()];
      for (int i=0; i<inputString.length(); i++)
        buff[i] = (byte)inputString.charAt(i);
      digest.update(buff, 0, inputString.length());
      
      String s = "";
      byte[] md5encoded = digest.digest();
  //    System.out.println("Final len:" + md5encoded.length);
      for (int i=0; i<md5encoded.length; i++)
      {
        char c = (char)md5encoded[i];
        String str = Integer.toString(c, 16);
        if (str.length() == 4)
          str = str.substring(2, 4);
        else if (str.length() == 1)
          str = "0" + str;
  //      System.out.print(str + " ");
        
        s += str;
      }
  //    System.out.println();
  //    System.out.println("Java MD5 [" + s + "]");
  //    System.out.println("10 first chars [" + s.substring(0, 10) + "]");
  
  //    System.out.println("Final Request: [http://saildocs.com/fetch?" + gribRequest + "&3=" + s.substring(0, 10) + "&u]" );
      request = "http://saildocs.com/fetch?" + gribRequest + "&3=" + s.substring(0, 10) + "&u";
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }    
    return request;
  }

  public static String chooseFile(int mode, String flt, String desc)
  {
    return WWGnlUtilities.chooseFile(mode, new String[]{ flt }, desc);
  }

  public static String chooseFile(int mode, String[] flt, String desc)
  {
    return WWGnlUtilities.chooseFile(mode, flt, desc, ".");
  }

  public static String chooseFile(int mode, String[] flt, String desc, String where)
  {
    return WWGnlUtilities.chooseFile(mode, flt, desc, where, null, null);
  }
  public static String chooseFile(int mode, String[] flt, String desc, String where, String buttonLabel, String dialogLabel)
  {
    return WWGnlUtilities.chooseFile(null, mode, flt, desc, where, buttonLabel, dialogLabel);
  }

  private static FilePreviewer previewer;
  
  public static String chooseFile(Component parent, 
                                  int mode, 
                                  String[] flt, 
                                  String desc, 
                                  String where, 
                                  String buttonLabel, 
                                  String dialogLabel)
  {
    return WWGnlUtilities.chooseFile(parent, mode, flt, desc, where, buttonLabel, dialogLabel, false);
  }

  public static String chooseFile(Component parent, 
                                  int mode, 
                                  String[] flt, 
                                  String desc, 
                                  String where, 
                                  String buttonLabel, 
                                  String dialogLabel,
                                  boolean withPreviewer)
  {
    String fileName = "";
    JFileChooser chooser = new JFileChooser();
    if (withPreviewer)
    {
      previewer = new FilePreviewer(chooser);
      chooser.setAccessory(previewer);
    }
    // TODO Sort the file by date, most recent on top. If possible... :(
    WWContext.ToolFileFilter filter = new WWContext.ToolFileFilter(flt, desc);
    chooser.addChoosableFileFilter(filter);
    chooser.setFileFilter(filter);
    chooser.setFileSelectionMode(mode);

    if (buttonLabel != null)
      chooser.setApproveButtonText(buttonLabel);
    if (dialogLabel != null)
      chooser.setDialogTitle(dialogLabel);

    File ff = new File(where);
    if (ff.isDirectory())
      chooser.setCurrentDirectory(ff);
    else
    {
      File f = new File(".");
      String currPath = f.getAbsolutePath();
      f = new File(currPath.substring(0, currPath.lastIndexOf(File.separator)));
      chooser.setCurrentDirectory(f);
    }
    int retval = chooser.showOpenDialog(parent);
    switch (retval)
    {
      case JFileChooser.APPROVE_OPTION:
        fileName = chooser.getSelectedFile().toString();
        break;
      case JFileChooser.CANCEL_OPTION:
        break;
      case JFileChooser.ERROR_OPTION:
        break;
    }
    return fileName;
  }

  private static TwoFilePanel twoFilePanel = null;
  
  public static String[] chooseTwoFiles(Component parent, 
                                        int modeOne, 
                                        String[] fltOne, 
                                        String descOne, 
                                        String whereOne, 
                                        int modeTwo, 
                                        String[] fltTwo, 
                                        String descTwo, 
                                        String whereTwo, 
                                        String dialogLabel)
  {
    String regExp = ""; 
    String fileNameOne = "";
    String fileNameTwo = "";
    String displayOpt = null;
    String withBoatStr = "true";
    String faxNameFilter = null;

    if (twoFilePanel == null)
    {
      twoFilePanel = new TwoFilePanel();
      twoFilePanel.getLeftLabel().setText(descOne);
      twoFilePanel.getRightLabel().setText(descTwo);
      twoFilePanel.getPatternLabel().setText(WWGnlUtilities.buildMessage("reg-expr-composite"));
      
      // From
      WWContext.ToolFileFilter filterOne = new WWContext.ToolFileFilter(fltOne, descOne);
      twoFilePanel.getLeftChooser().addChoosableFileFilter(filterOne);
      twoFilePanel.getLeftChooser().setFileFilter(filterOne);
      twoFilePanel.getLeftChooser().setFileSelectionMode(modeOne);
      twoFilePanel.getLeftChooser().setControlButtonsAreShown(false);
      File ffOne = new File(whereOne);
      if (ffOne.isDirectory())
      {
        twoFilePanel.getLeftChooser().setCurrentDirectory(ffOne);
        twoFilePanel.getLeftChooser().setSelectedFile(ffOne);
      }
      else
      {
        File f = new File(".");
        String currPath = f.getAbsolutePath();
        f = new File(currPath.substring(0, currPath.lastIndexOf(File.separator)));
        twoFilePanel.getLeftChooser().setCurrentDirectory(f);
        twoFilePanel.getLeftChooser().setSelectedFile(f);
      }
      // To
      WWContext.ToolFileFilter filterTwo = new WWContext.ToolFileFilter(fltTwo, descTwo);
      twoFilePanel.getRightChooser().addChoosableFileFilter(filterTwo);
      twoFilePanel.getRightChooser().setFileFilter(filterTwo);
      twoFilePanel.getRightChooser().setFileSelectionMode(modeTwo);
      twoFilePanel.getRightChooser().setControlButtonsAreShown(false);
  
      File ffTwo = new File(whereTwo);
      if (ffTwo.isDirectory())
      {
        twoFilePanel.getRightChooser().setCurrentDirectory(ffTwo);
        twoFilePanel.getRightChooser().setSelectedFile(ffTwo);
      }
      else
      {
        File f = new File(".");
        String currPath = f.getAbsolutePath();
        f = new File(currPath.substring(0, currPath.lastIndexOf(File.separator)));
        twoFilePanel.getRightChooser().setCurrentDirectory(f);
        twoFilePanel.getRightChooser().setSelectedFile(f);
      }
    }
    int resp = JOptionPane.showConfirmDialog(parent, 
                                             twoFilePanel, 
                                             dialogLabel, 
                                             JOptionPane.OK_CANCEL_OPTION,
                                             JOptionPane.PLAIN_MESSAGE);
    if (resp == JOptionPane.OK_OPTION)
    {
      fileNameOne = twoFilePanel.getLeftChooser().getSelectedFile().toString();
      fileNameTwo = twoFilePanel.getRightChooser().getSelectedFile().toString();
      regExp = twoFilePanel.getRegExprPatternTextField().getText();
      displayOpt = twoFilePanel.getDisplayOption();
      withBoatStr = (twoFilePanel.withBoatAndTrack()?"true":"false");
      faxNameFilter = twoFilePanel.getFaxNameRegExpr();
    }
    return new String[] { fileNameOne, fileNameTwo, regExp, displayOpt, twoFilePanel.getPDFTitle(), withBoatStr, faxNameFilter };
  }
  
  public final static int NOTHING  = 0;
  public final static int CIRCLE   = 1;
  public final static int RETICULE = 2;
  
  public static void drawBoat(Graphics2D g2, Color c, Point pt, int boatLength, int trueHeading, float alpha)
  {
    drawBoat(g2, c, pt, boatLength, trueHeading, alpha, NOTHING);
  }
  public static void drawBoat(Graphics2D g2, Color c, Point pt, int boatLength, int trueHeading, float alpha, int option)
  {
    // Transparency
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    
    double[] x = // Half
      new double[]
      { 0, boatLength / 7, (2 * boatLength) / 7, (2 * boatLength) / 7, (1.5 * boatLength) / 7, -(1.5 * boatLength) / 7, 
        -(2 * boatLength) / 7, -(2 * boatLength) / 7, -boatLength / 7 };
    double[] y = // Half
      new double[]
      { -(4 * boatLength) / 7, -(3 * boatLength) / 7, -(boatLength) / 7, boatLength / 7, (3 * boatLength) / 7, 
        (3 * boatLength) / 7, boatLength / 7, -(boatLength) / 7, -(3 * boatLength) / 7 };
    int[] xpoints = new int[x.length];
    int[] ypoints = new int[y.length];

    // Rotation matrix:
    // | cos(alpha)  -sin(alpha) |
    // | sin(alpha)   cos(alpha) |
    for (int i = 0; i < x.length; i++)
    {
      double dx = x[i] * Math.cos(Math.toRadians(trueHeading)) + (y[i] * (-Math.sin(Math.toRadians(trueHeading))));
      double dy = x[i] * Math.sin(Math.toRadians(trueHeading)) + (y[i] * Math.cos(Math.toRadians(trueHeading)));
      xpoints[i] = (int) (pt.x + dx);
      ypoints[i] = (int) (pt.y + dy);
    }
    Polygon p = new Polygon(xpoints, ypoints, xpoints.length);
    Color before = g2.getColor();
    g2.setColor(c);
    g2.fillPolygon(p);
    // Reset Transparency
    alpha = 1.0f;
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));      
    // Line around the boat
    for (int i = 0; i < x.length-1; i++)
      g2.drawLine(xpoints[i], ypoints[i], xpoints[i+1], ypoints[i+1]);
    g2.drawLine(xpoints[x.length-1], ypoints[x.length-1], xpoints[0], ypoints[0]);
    
    // option ?
    switch (option)
    {
      case CIRCLE:
        int radius = ((3 * boatLength) / 7) / 2;
        g2.drawOval(pt.x - radius, pt.y - radius, 2 * radius, 2 * radius);
        g2.drawLine(pt.x, pt.y, pt.x, pt.y);
        break;
      case RETICULE:
        break;
      default:
        break;
    }
    
    g2.setColor(before);
  }

  private final static int KNOB_DIAMETER = 10; // Make it even
  
  private final static int SIMPLE_HAND_OPTION = 1;
  private final static int ARROW_HAND_OPTION  = 2;
  private final static int BIG_HAND_OPTION    = 3;
  // TODO A preference for the hand flavor
  private final static int HAND_OPTION = BIG_HAND_OPTION;
  
  public static void drawTWAOverBoat(Graphics2D g2d, int hLength, Point center, int twa)
  {
    // Hand shadow
    g2d.setColor(Color.gray);
    float alpha = 0.3f;
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    Stroke originalStroke = g2d.getStroke();
    Stroke stroke =  new BasicStroke(5, 
                                     BasicStroke.CAP_ROUND,
                                     BasicStroke.JOIN_BEVEL);
    g2d.setStroke(stroke);  
    int handLength = hLength;
    int shadowOffset = 5;
    if (HAND_OPTION == SIMPLE_HAND_OPTION)
    {
      g2d.drawLine(center.x + shadowOffset, 
                   center.y + shadowOffset, 
                   center.x + shadowOffset + (int)(handLength * Math.sin(Math.toRadians((double)twa))),
                   center.y + shadowOffset - (int)(handLength * Math.cos(Math.toRadians((double)twa))));
    }
    else if (HAND_OPTION == ARROW_HAND_OPTION)
    {
      Point from = new Point(center.x + shadowOffset + (int)((handLength) * Math.sin(Math.toRadians((double)twa))),
                             center.y + shadowOffset - (int)((handLength) * Math.cos(Math.toRadians((double)twa))));
      drawAnemometerArrow(g2d, from, center, 20, BACKWARD, null);      
    }
    else if (HAND_OPTION == BIG_HAND_OPTION)
    {
      Point to   = new Point(center.x + shadowOffset + (int)(handLength * Math.sin(Math.toRadians((double)twa))),
                             center.y + shadowOffset - (int)(handLength * Math.cos(Math.toRadians((double)twa))));
      Point from = new Point(center.x + shadowOffset - (int)(handLength * Math.sin(Math.toRadians((double)twa))),
                             center.y + shadowOffset + (int)(handLength * Math.cos(Math.toRadians((double)twa))));
      drawAnemometerArrow(g2d, from, to, 30, FORWARD, null);      
    }
    g2d.fillOval(center.x + shadowOffset - (KNOB_DIAMETER / 2),
                 center.y + shadowOffset - (KNOB_DIAMETER / 2),
                 KNOB_DIAMETER, KNOB_DIAMETER);                 
    // Reset Transparency
    alpha = 1.0f;
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));      
    // Hand
    if (HAND_OPTION == SIMPLE_HAND_OPTION)
    {
      g2d.drawLine(center.x, 
                   center.y, 
                   center.x + (int)(handLength * Math.sin(Math.toRadians((double)twa))),
                   center.y - (int)(handLength * Math.cos(Math.toRadians((double)twa))));
    }
    else if (HAND_OPTION == ARROW_HAND_OPTION)
    {
      Point from = new Point(center.x + (int)((handLength) * Math.sin(Math.toRadians((double)twa))),
                             center.y - (int)((handLength) * Math.cos(Math.toRadians((double)twa))));
      drawAnemometerArrow(g2d, from, center, 20, BACKWARD, null);      
    }
    else if (HAND_OPTION == BIG_HAND_OPTION)
    {
      Point to   = new Point(center.x + (int)(handLength * Math.sin(Math.toRadians((double)twa))),
                             center.y - (int)(handLength * Math.cos(Math.toRadians((double)twa))));
      Point from = new Point(center.x - (int)(handLength * Math.sin(Math.toRadians((double)twa))),
                             center.y + (int)(handLength * Math.cos(Math.toRadians((double)twa))));
      Color c = new Color(19, 234, 186);
      drawAnemometerArrow(g2d, from, to, 30, FORWARD, c, c);      
    }
    g2d.fillOval(center.x - (KNOB_DIAMETER / 2),
                 center.y - (KNOB_DIAMETER / 2),
                 KNOB_DIAMETER, KNOB_DIAMETER);                 
    
    g2d.setStroke(originalStroke);      
  }

  public static void drawAnemometerArrow(Graphics2D g, Point from, Point to, int headLength, int option, Color c)
  {
    drawAnemometerArrow(g, from, to, headLength, option, c, null);
  }
  
  public final static int FORWARD  = 1;
  public final static int BACKWARD = 2;

  public static void drawAnemometerArrow(Graphics2D g, Point from, Point to, int headLength, int option, Color handColor, Color arrowColor)
  {
    Color orig = null;
    if (g != null) orig = g.getColor();
    if (handColor != null) g.setColor(handColor);
//  int headLength = 30; // 17; // (int)(Math.sqrt( Math.pow((from.x - to.x), 2) + Math.pow((from.y - to.y), 2)) / 3d);
    double headHalfAngle = 15D;
    
    Point middlePoint = null;
    if (option == FORWARD)
      middlePoint = new Point(from.x - ((from.x - to.x) / 2), from.y - ((from.y - to.y) / 2));
    else
      middlePoint = new Point(from.x + ((from.x - to.x) / 6), from.y + ((from.y - to.y) / 6));
    
    double dir = getDir((float)(from.x - to.x), (float)(to.y - from.y));
  //  System.out.println("Dir:" + dir);
    Point left = null, right = null;
    if (option == FORWARD)
    {
      left = new Point((int)(middlePoint.x - (headLength * Math.cos(Math.toRadians(dir - 90 + headHalfAngle)))),
                       (int)(middlePoint.y - (headLength * Math.sin(Math.toRadians(dir - 90 + headHalfAngle)))));
      right = new Point((int)(middlePoint.x - (headLength * Math.cos(Math.toRadians(dir - 90 - headHalfAngle)))),
                        (int)(middlePoint.y - (headLength * Math.sin(Math.toRadians(dir - 90 - headHalfAngle)))));
    }
    else if (option == BACKWARD)
    {
      left = new Point((int)(middlePoint.x + (headLength * Math.cos(Math.toRadians(dir - 90 + headHalfAngle)))),
                       (int)(middlePoint.y + (headLength * Math.sin(Math.toRadians(dir - 90 + headHalfAngle)))));
      right = new Point((int)(middlePoint.x + (headLength * Math.cos(Math.toRadians(dir - 90 - headHalfAngle)))),
                        (int)(middlePoint.y + (headLength * Math.sin(Math.toRadians(dir - 90 - headHalfAngle)))));      
    }
    g.drawLine(from.x, from.y, to.x, to.y);
    Polygon head = new Polygon(new int[] { middlePoint.x, left.x, right.x }, new int[] { middlePoint.y, left.y, right.y }, 3);
    if (arrowColor != null)
      g.setColor(arrowColor);
    g.fillPolygon(head);
    
    if (g != null) g.setColor(orig);
  }
  
  public static void drawWind(Graphics gr, 
                              int x, 
                              int y, 
                              double speed, 
                              double dir,
                              boolean coloredWind, 
                              Color initialGribWindBaseColor)
  {
    WWGnlUtilities.drawWind(gr, x, y, speed, dir, coloredWind, initialGribWindBaseColor, false, false, false);
  }

  public static void drawWind(Graphics gr, 
                              int x, 
                              int y, 
                              double speed, 
                              double dir, 
                              boolean coloredWind, 
                              Color initialGribWindBaseColor, 
                              boolean drawHeavyDot, 
                              boolean drawWindColorBackground, 
                              boolean displayWindSpeed)
  {
    WWGnlUtilities.drawWind(gr, x, y, speed, dir, coloredWind, initialGribWindBaseColor, drawHeavyDot, drawWindColorBackground, displayWindSpeed, false);  
  }

  public static void drawWind(Graphics gr, 
                              int x, 
                              int y, 
                              double speed, 
                              double dir, 
                              boolean coloredWind, 
                              Color initialGribWindBaseColor, 
                              boolean drawHeavyDot, 
                              boolean drawWindColorBackground, 
                              boolean displayWindSpeed,
                              boolean useThickWind)
  {
    float alpha = 0.75f;
    WWGnlUtilities.drawWind(gr, x, y, speed, dir, coloredWind, initialGribWindBaseColor, drawHeavyDot, drawWindColorBackground, displayWindSpeed, useThickWind, alpha);  
  }

  public static void drawWind(Graphics gr, 
                              int x, 
                              int y, 
                              double speed, 
                              double dir, 
                              boolean coloredWind, 
                              Color initialGribWindBaseColor, 
                              boolean drawHeavyDot, 
                              boolean drawWindColorBackground, 
                              boolean displayWindSpeed,
                              boolean useThickWind,
                              float alpha)
  {
    WWGnlUtilities.drawWind(gr, x, y, speed, dir, coloredWind, initialGribWindBaseColor, drawHeavyDot, drawWindColorBackground, displayWindSpeed, useThickWind, null, null, null, null, alpha);  
  }

  public static void drawWind(Graphics gr, 
                              int x, 
                              int y, 
                              double speed, 
                              double dir, 
                              boolean coloredWind, 
                              Color initialGribWindBaseColor, 
                              boolean drawHeavyDot, 
                              boolean drawWindColorBackground, 
                              boolean displayWindSpeed,
                              Point tl, 
                              Point br,
                              Point tr,
                              Point bl,
                              float alpha)
  {
    WWGnlUtilities.drawWind(gr, x, y, speed, dir, coloredWind, initialGribWindBaseColor, drawHeavyDot, drawWindColorBackground, displayWindSpeed, false, tl, br, tr, bl, alpha);    
  }
  
  public static void drawWind(Graphics gr, 
                              int x, 
                              int y, 
                              double speed, 
                              double dir, 
                              boolean coloredWind, 
                              Color initialGribWindBaseColor, 
                              boolean drawHeavyDot, 
                              boolean drawWindColorBackground, 
                              boolean displayWindSpeed,
                              boolean useThickWind,
                              Point tl, 
                              Point br,
                              Point tr,
                              Point bl)
  {
    float alpha = 0.75f;
    WWGnlUtilities.drawWind(gr, x, y, speed, dir, coloredWind, initialGribWindBaseColor, drawHeavyDot, drawWindColorBackground, displayWindSpeed, useThickWind, tl, br, tr, bl, alpha);    
  }
  public static void drawWind(Graphics gr, 
                              int x, 
                              int y, 
                              double speed, 
                              double dir, 
                              boolean coloredWind, 
                              Color initialGribWindBaseColor, 
                              boolean drawHeavyDot, 
                              boolean drawWindColorBackground, 
                              boolean displayWindSpeed,
                              boolean useThickWind,
                              Point tl, 
                              Point br,
                              Point tr,
                              Point bl,
                              float alpha)
  {
    boolean displayWindDirWithWindColorRange = ((Boolean)ParamPanel.data[ParamData.DISPLAY_WIND_WITH_COLOR_WIND_RANGE][1]).booleanValue();
    
    int discSize = 4; // Smallest
    if (drawHeavyDot)
      discSize = (int) Math.round(speed / (double) 2);
    if (drawWindColorBackground)
    {      
      gr.setColor(getWindColor(coloredWind, initialGribWindBaseColor, speed, true)); // Set transparency anyway
      int w = Math.abs(br.x - tl.x);
      int h = Math.abs(br.y - tl.y);
      ((Graphics2D) gr).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
      // Form non-square projections, use polygon
      if (tr != null && bl != null)
      {
        Polygon plg = new Polygon(new int[] {tl.x, bl.x, br.x, tr.x},
                                  new int[] {tl.y, bl.y, br.y, tr.y},
                                  4);
        gr.fillPolygon(plg);
      }
      else
        gr.fillRect(tl.x, tl.y, w, h);
//    gr.setColor(Color.black);
//    gr.drawRect(tl.x, tl.y, w, h);
    }
    else
    {
      gr.setColor(getWindColor(coloredWind, initialGribWindBaseColor, speed, false));  
      gr.fillOval(x - discSize / 2, y - discSize / 2, discSize, discSize);
    }
    Stroke originalStroke = ((Graphics2D) gr).getStroke();
    if (drawWindColorBackground)
    {
      ((Graphics2D) gr).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    }
    if (useThickWind)
    {
      if (gr instanceof Graphics2D)
      {
        Stroke stroke =  new BasicStroke(((int)(speed / 10d) + 1), 
                                         BasicStroke.CAP_BUTT,
                                         BasicStroke.JOIN_BEVEL);
        ((Graphics2D) gr).setStroke(stroke);  
      }
    }
    int arrowLength = 20;
    double dTWD = Math.toRadians(dir);
    int arrowX = (int) ((double) arrowLength * Math.sin(dTWD));
    int arrowY = (int) ((double) arrowLength * Math.cos(dTWD));
   
    if (!WWContext.getInstance().getUseColorRangeForWindSpeed().booleanValue())
      gr.setColor(getWindColor(coloredWind, initialGribWindBaseColor, speed, false)); // Sets transparency if required (coloredWind = true)
    else if (displayWindDirWithWindColorRange)
      gr.setColor(Color.black);
    
    if (!WWContext.getInstance().getUseColorRangeForWindSpeed().booleanValue() || displayWindDirWithWindColorRange)
      gr.drawLine(x, y, x + arrowX, y + arrowY);
    
    if (displayWindSpeed)
    {
      Font f = gr.getFont();
      gr.setFont(new Font("Arial", Font.PLAIN, 10));
      String speedStr = Integer.toString((int) Math.round(speed));
      int strWidth = gr.getFontMetrics(gr.getFont()).stringWidth(speedStr);
      Color c = gr.getColor();
      gr.setColor(Color.black);
      gr.drawString(speedStr, x - strWidth / 2, y + 5);
      gr.setColor(c);
      gr.setFont(f);
    }
    // Fethers
    if (speed > 0.0D && (!WWContext.getInstance().getUseColorRangeForWindSpeed().booleanValue() || displayWindDirWithWindColorRange))
    {
      if (useThickWind)
      {
        if (gr instanceof Graphics2D)
        {
          Stroke stroke =  new BasicStroke(1, // Reset to 1 for the fethers
                                           BasicStroke.CAP_BUTT,
                                           BasicStroke.JOIN_BEVEL);
          ((Graphics2D) gr).setStroke(stroke);  
        }
      }
      int iTws = (int) Math.round(speed);
      int origin;
      for (origin = arrowLength; iTws >= 50; origin -= 5)
      {
        iTws -= 50;
        int featherStartX = x + (int) ((double) origin * Math.sin(dTWD));
        int featherStartY = y + (int) ((double) origin * Math.cos(dTWD));
        int featherEndX = featherStartX + (int) (10D * Math.sin(dTWD + Math.toRadians(60D)));
        int featherEndY = featherStartY + (int) (10D * Math.cos(dTWD + Math.toRadians(60D)));
        int featherStartX2 = x + (int) ((double) (origin - 5) * Math.sin(dTWD));
        int featherStartY2 = y + (int) ((double) (origin - 5) * Math.cos(dTWD));

        gr.fillPolygon(new Polygon(new int[]{ featherStartX, featherEndX, featherStartX2 }, 
                                   new int[]{ featherStartY, featherEndY, featherStartY2 }, 
                                   3));
      }
      while (iTws >= 10)
      {
        iTws -= 10;
        int featherStartX = x + (int) ((double) origin * Math.sin(dTWD));
        int featherStartY = y + (int) ((double) origin * Math.cos(dTWD));
        int featherEndX = featherStartX + (int) (7D * Math.sin(dTWD + Math.toRadians(60D)));
        int featherEndY = featherStartY + (int) (7D * Math.cos(dTWD + Math.toRadians(60D)));
        gr.drawLine(featherStartX, featherStartY, featherEndX, featherEndY);
        origin -= 3;
      }
      if (iTws >= 5)
      {
        int featherStartX = x + (int) ((double) origin * Math.sin(dTWD));
        int featherStartY = y + (int) ((double) origin * Math.cos(dTWD));
        int featherEndX = featherStartX + (int) (4D * Math.sin(dTWD + Math.toRadians(60D)));
        int featherEndY = featherStartY + (int) (4D * Math.cos(dTWD + Math.toRadians(60D)));
        gr.drawLine(featherStartX, featherStartY, featherEndX, featherEndY);
      }
    }
    if (drawWindColorBackground || useThickWind)
    {
      if (gr instanceof Graphics2D)
      {
        ((Graphics2D) gr).setStroke(originalStroke);
      }
    }
  }

  public static Color getWindColor(double speed)
  {
    return getWindColor(Color.black, speed, false);
  }
  
  public static Color getWindColor(Color initialGribWindBaseColor, 
                                   double speed, 
                                   boolean setAnyway)
  {
    return getWindColor(true, initialGribWindBaseColor, speed, setAnyway);
  }

  public static Color getWindColor(boolean coloredWind, 
                                   Color initialGribWindBaseColor, 
                                   double speed, 
                                   boolean setAnyway)
  {
    if (!coloredWind)
      return Color.black;
      
    Color c = null;
    
    if (!WWContext.getInstance().getUseColorRangeForWindSpeed().booleanValue())
    {
      float speedF = (float)speed;
      if (speedF > 50F) speedF = 50F;
      Color igwbc = initialGribWindBaseColor; // Base color 

      if (setAnyway || WWContext.getInstance().getUseGRIBWindSpeedTransparency())
      {
        int alpha = (int) (255 * speedF / 50F);
        c = new Color(igwbc.getRed(), igwbc.getGreen(), igwbc.getBlue(), alpha);
      }
      else
        c = igwbc;
    }
    else
    {
//    c = WindGaugePanel.getWindColor((float)speed);
      int intWS = Math.round((float)speed);
      if (intWS > WIND_COLOR_VALUES.length - 1)
        intWS = WIND_COLOR_VALUES.length - 1;
      c = new Color(WIND_COLOR_VALUES[intWS][0],
                    WIND_COLOR_VALUES[intWS][1],
                    WIND_COLOR_VALUES[intWS][2]);
      /*
      if (speed < (double)5)
        c = new Color(225, 183, 183);
      else if (speed >= (double)5 && speed < (double)10)
        c = new Color(224, 142, 142);
      else if (speed >= (double)10 && speed < (double)15)
        c = new Color(223, 105, 105);
      else if (speed >= (double)15 && speed < (double)20)
        c = new Color(225, 45, 45);
      else  if (speed >= (double)20 && speed < (double)25)
        c = new Color(204, 18, 18);
      else if (speed >= (double)25 && speed < (double)30)
        c = new Color(155, 9, 9);
      else if (speed >= (double)30 && speed < (double)35)
        c = new Color(115, 5, 5);
      else if (speed >= (double)35 && speed < (double)40)
        c = new Color(83, 3, 3);
      else if (speed >= (double)40 && speed < (double)45)
        c = new Color(50, 2, 2);
      else if (speed >= (double)45 && speed < (double)50)
        c = new Color(37, 1, 1);
      else
        c = new Color(0, 0, 0);
      */
    }
    return c;
  }

  public static Color getTemperatureColor(double temperature)
  {
      return getTemperatureColor(temperature, 40D, -20D);
  }
  
  public static Color getTemperatureColor(double temperature, double top, double bottom)
  {
    Color c = null;
    // Boundaries -20, 40
//    double bottom = -20D;
//    double top    =  40D;
    double amplitude = top - bottom;
    
    double t = temperature;
    if (t < bottom) t = bottom;
    if (t > top)    t = top;
    
    if (true)
    {
      Color c1 = new Color(14, 241, 236); // Color.blue; // From
      Color c2 = Color.red;   // To
      float ratio = (float) (t - bottom) / (float) amplitude;
      int red   = (int) (c2.getRed() * ratio + c1.getRed() * (1 - ratio));
      int green = (int) (c2.getGreen() * ratio + c1.getGreen() * (1 - ratio));
      int blue  = (int) (c2.getBlue() * ratio + c1.getBlue() * (1 - ratio));
      c   = new Color(red, green, blue);
    }
    else
    {
      int r = 0, g = 0, b = 0;
      r = (int)(255 * (t - bottom) / amplitude);
      b = 255 - (int)(255 * (t - bottom) / amplitude);
      if (r<0 || b<0)
        System.out.print("[Temp color for " + temperature +  ":" + r + "," + g + "," + b + "]");
      if (r < 0) r = 0;
      if (b < 0) b = 0;
      c = new Color(r, g, b);
    }
    return c;
  }

  public static Color getWavesColor(double height)
  {
     return getWavesColor(height, 10D, 0D);
  }
  
  public static Color getWavesColor(double height, double top, double bottom)
  {
    Color c = null;
    // Boundaries 0, 10
    int r = 0, g = 0, b = 255;
//    double bottom = 0D;
//    double top    = 10D;
    double amplitude = top - bottom;
    
    double h = height;
    if (h < bottom) h = bottom;
    if (h > top)    h = top;
    
    r = 255 - (int)(255 * (h - bottom) / amplitude);
    g = 255 - (int)(255 * (h - bottom) / amplitude);
    if (r<0 || g<0)
      System.out.print("[Waves color for " + height +  ":" + r + "," + g + "," + b + "]");
    if (r < 0) r = 0;
    if (g < 0) g = 0;
    c = new Color(r, g, b);
    return c;
  }

  public static Color getRainColor(double height)
  {
     return getRainColor(height, 100D, 0D);
  }
  
  public static Color getRainColor(double height, double top, double bottom)
  {
//  System.out.println("Finding color for " + XXX12.format(height) + " between " + XXX12.format(top) + " and " + XXX12.format(bottom));
    Color c = null;
    // Boundaries 0, 10
    int r = 0, g = 0, b = 0;
  //    double bottom = 0D;
  //    double top    = 10D;
    double amplitude = top - bottom;
    
    double h = top - height;
    if (h < bottom) h = bottom;
    if (h > top)    h = top;
    
    r = (int)(255 * (h - bottom) / amplitude);
    g = (int)(255 * (h - bottom) / amplitude);
    b = (int)(255 * (h - bottom) / amplitude);
    if (r<0 || g<0 || b<0)
      System.out.print("[Rain color for " + height +  ":" + r + "," + g + "," + b + "]");
    if (r < 0) r = 0;
    if (g < 0) g = 0;
    if (b < 0) b = 0;
    c = new Color(r, g, b);
    return c;
  }

  public static Color getPressureColor(double pressure)
  {
    return getPressureColor(pressure, 1030D, 970D);
  }
  
  public static Color getPressureColor(double pressure, double top, double bottom)
  {
    Color c = null;
    // Boundaries 0, 10
    int r = 0, g = 0, b = 0;
//    double bottom =  970D;
//    double top    = 1030D;
    double amplitude = top - bottom;
    
    double p = pressure;
    if (p < bottom) p = bottom;
    if (p > top)    p = top;
    
    r = (int)(255 * (p - bottom) / amplitude);
    g = (int)(255 * (p - bottom) / amplitude);
    b = (int)(255 * (p - bottom) / amplitude);
    if (r<0 || g<0 || b<0)
      System.out.print("[Pressure color for " + pressure +  ":" + r + "," + g + "," + b + "]");
    if (r < 0) r = 0;
    if (g < 0) g = 0;
    if (b < 0) b = 0;
    c = new Color(r, g, b);
    return c;
  }

  public static Color get500mbColor(double alt)
  {
    return get500mbColor(alt, 6000D, 5000D);
  }
  
  public static Color get500mbColor(double alt, double top, double bottom)
  {
    Color c = null;
    // Boundaries 0, 10
    int r = 0, g = 0, b = 0;
//    double bottom = 5000D;
//    double top    = 6000D;
    double amplitude = top - bottom;
    
    double p = alt;
    if (p < bottom) p = bottom;
    if (p > top)    p = top;
    
    r = (int)(255 * (p - bottom) / amplitude);
    g = (int)(255 * (p - bottom) / amplitude);
    b = (int)(255 * (p - bottom) / amplitude);
    if (r<0 || g<0 || b<0)
      System.out.print("[Pressure color for " + alt +  ":" + r + "," + g + "," + b + "]");
    if (r < 0) r = 0;
    if (g < 0) g = 0;
    if (b < 0) b = 0;
    c = new Color(r, g, b);
    return c;
  }

  public static void drawGRIBData(Graphics gr, int x, int y, Point tl, Point br, Point tr, Point bl)
  {
    float alpha = 0.75f;
    drawGRIBData(gr, x, y, tl, br, tr, bl, alpha);
  }
  
  public static void drawGRIBData(Graphics gr, int x, int y, Point tl, Point br, Point tr, Point bl, float alpha)
  {
    int w = Math.abs(br.x - tl.x);
    int h = Math.abs(br.y - tl.y);
    ((Graphics2D) gr).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    if (tr != null && bl != null)
    {
      Polygon plg = new Polygon(new int[] {tl.x, bl.x, br.x, tr.x},
                                new int[] {tl.y, bl.y, br.y, tr.y},
                                4);
      gr.fillPolygon(plg);
    }
    else
      gr.fillRect(tl.x, tl.y, w, h);
    alpha = 1.0f;
    ((Graphics2D) gr).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
  }

  public static Color buildColor(String str)
  {
    String[] st = str.split(";");
    int b = 0, g = 0, r = 0;
    if (st.length != 3)
      throw new RuntimeException("Bad color definition: [" + str + "]");
    r = Integer.parseInt(st[0]);
    g = Integer.parseInt(st[1]);
    b = Integer.parseInt(st[2]);
    Color c = new Color(r, g, b);
    return c;
  }

  public static String colorToString(Color c)
  {
    String s = Integer.toString(c.getRed()) + ";" + Integer.toString(c.getGreen()) + ";" + Integer.toString(c.getBlue());
    return s;
  }
  
  public static String getHeaderFileName(String faxName)
  {
    String hdr = "";
    hdr = faxName.substring(0, faxName.lastIndexOf(".")) + ".hdr";

    return hdr;
  }

  public static boolean headerFileExists(String headerName)
  {
    return (new File(headerName)).exists();
  }
  /**
   * For the faxes downloaded by GetFax
   * 
   * @param fax
   * @return
   */
  public static String getHeader(String fax)
  {
    String comment = fax;
    String headerName = getHeaderFileName(fax);
//  System.out.println("Header " + headerName);
    File h = new File(headerName);
    if (h.exists())
    {
      Properties p = new Properties();
      try
      {
        p.load(new FileInputStream(h));
      }
      catch (FileNotFoundException fnfe)
      {
        fnfe.printStackTrace();
      }
      catch (IOException ioe)
      {
        ioe.printStackTrace();
      }
      comment = p.getProperty("Comment", "") + " " + fax.substring(fax.lastIndexOf(File.separator) + 1);
    }
    return comment;
  }

  public static void doOnExit(JFrame frame)
  {
    boolean confirm = ((Boolean)ParamPanel.data[ParamData.CONFIRM_ON_EXIT][1]).booleanValue();
    boolean go = false;
    if (confirm)
    {
      ExitPanel exitPanel = new ExitPanel();      
      int resp = JOptionPane.showConfirmDialog(WWContext.getInstance().getMasterTopFrame(), 
                                               exitPanel, WWGnlUtilities.buildMessage("exit-no-acc"), 
                                               JOptionPane.YES_NO_OPTION, 
                                               JOptionPane.QUESTION_MESSAGE);
      if (resp == JOptionPane.YES_OPTION)
      {
        go = true;
        if (exitPanel.shutUpNextTime())
        {
          ParamPanel.data[ParamData.CONFIRM_ON_EXIT][1] = Boolean.valueOf(false);
          ParamPanel.saveParameters();
        }
      }
    }
    else
      go = true;
    
    if (go)
    {
      try
      {
        Properties props = new Properties();
        props.setProperty("frame.width", Integer.toString(frame.getWidth()));
        props.setProperty("frame.height", Integer.toString(frame.getHeight()));
        props.setProperty("frame.x.pos", Integer.toString(frame.getX()));
        props.setProperty("frame.y.pos", Integer.toString(frame.getY()));
        props.store(new FileWriter("ww_position.properties"), "Generated " + new Date().toString());
//      props.storeToXML(new FileOutputStream("ww_position.properties"), "Generated " + new Date().toString());
        
        props = new Properties();
        props.setProperty("tooltip.option", System.getProperty("tooltip.option", "on-chart"));
        props.setProperty("composite.sort", System.getProperty("composite.sort", "date.desc"));
        props.store(new FileWriter(WWContext.CONFIG_PROPERTIES_FILE), "Generated " + new Date().toString());
      }
      catch (Exception forgetit) 
      {
        forgetit.printStackTrace();
      }

      File updateFile = new File("update" + File.separator + "update.xml");
      if (updateFile.exists())
      {
        try
        {
          DOMParser parser = WWContext.getInstance().getParser();
          synchronized (parser)
          {
            parser.setValidationMode(XMLParser.NONVALIDATING);
            parser.parse(updateFile.toURI().toURL());
            XMLDocument doc = WWContext.getInstance().getParser().getDocument();
            NodeList nl = doc.selectNodes("//update");
            for (int i=0; i<nl.getLength(); i++)
            {
              XMLElement update = (XMLElement)nl.item(i);
              String destination = update.getAttribute("destination");
              String origin = update.getAttribute("origin");
              // Backup, copy
              System.out.println("Copying " + origin + " to " + destination);
    
              OutputStream os = new FileOutputStream(new File(destination));
              File fis = new File(origin);
              InputStream is = new FileInputStream(fis);
              Utilities.copy(is, os);
              os.close();
              is.close();
              fis.delete();
            }
          }
        }
        catch (Exception ex)
        {
          System.out.println("Exiting...");
  //      ex.printStackTrace();
        }
        finally
        {
          // Delete if successful
          updateFile.delete();
        }
      }
      Object topFrame = WWContext.getInstance().getMasterTopFrame();
      if (topFrame instanceof AdjustFrame)
      {
        try
        {
          AdjustFrame af = (AdjustFrame) topFrame;
          FileTypeHolder fth = af.getAllJTrees();
          XMLDocument visualConfig = new XMLDocument();
          XMLElement root = (XMLElement) visualConfig.createElement("visual-config");
          visualConfig.appendChild(root);
          for (int i = 0; i < fth.getNbTree(); i++)
          {
            XMLElement e = (XMLElement) visualConfig.createElement("tree-pane");
            root.appendChild(e);
            e.setAttribute("id", Integer.toString(i));
            e.setAttribute("expanded", Boolean.toString(fth.isTreeExpanded(i)));
          }
          FileOutputStream fos =  new FileOutputStream(VISUAL_CONFIG_FILE_NAME);
          visualConfig.print(fos);
          fos.close();
        }
        catch (DOMException e)
        {
          e.printStackTrace();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }        
      }
      
      System.exit(0);
    }
    else
      System.out.println("Not exiting...");
  }

  public static void setTreeConfig(FileTypeHolder fth)
  {
    try
    {
      File visualConfig = new File(VISUAL_CONFIG_FILE_NAME);
      DOMParser parser = WWContext.getInstance().getParser();
      synchronized (parser)
      {
        parser.parse(visualConfig.toURI().toURL());
        XMLDocument doc = parser.getDocument();
        NodeList nl = doc.selectNodes("/visual-config/tree-pane");
        for (int i=0; i<nl.getLength(); i++)
        {
          XMLElement tp = (XMLElement)nl.item(i);
          int idx = Integer.parseInt(tp.getAttribute("id"));
          boolean expanded = Boolean.parseBoolean(tp.getAttribute("expanded"));
          fth.setExpanded(idx, expanded);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static double getGeostrophicWindSpeed(double distanceInNM, double latitudeInDegrees)
  {
    return getGeostrophicWindSpeed(distanceInNM, latitudeInDegrees, 4);
  }
  
  public static double getGeostrophicWindSpeed(double distanceInNM, double latitudeInDegrees, int betweenIsobars)
  {
    double distance = (distanceInNM * ( 5D / (double)betweenIsobars ) ) * 1.852; // Km, between  isobars with 5mb difference
    double gradient = 1D / distance;
    double omega_rho = 0.0001D;
    double phi = Math.toRadians(latitudeInDegrees);
    double v = gradient / (2 * omega_rho * Math.sin(phi));
//  System.out.println("WS:" + v);        
    return v;    
  }

  public static String escapeXML(String str)
  {
    return str.replaceAll("'", "&apos;").replaceAll("\"", "&quot;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
  }

  public static String unescapeXML(String str)
  {
    return str.replaceAll("&apos;", "'").replaceAll("&quot;", "\"").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
  }

  public static void archiveCompositeDirectory(final String compositeDirectoryName)
  {
    // 1 - Ask: Copy or Move
    int resp = JOptionPane.showConfirmDialog(WWContext.getInstance().getMasterTopFrame(), WWGnlUtilities.buildMessage("do-you-wish-to-delete"), WWGnlUtilities.buildMessage("archive-composite"), 
                                             JOptionPane.YES_NO_CANCEL_OPTION, 
                                             JOptionPane.QUESTION_MESSAGE);
    final Boolean deleteWhenDone = Boolean.valueOf(resp == JOptionPane.YES_OPTION);
    if (resp == JOptionPane.CANCEL_OPTION)
      return;
    // Loop and archive
    Thread archiver = new Thread("archiver")
    {
      public void run()
      {
        File dir = new File(compositeDirectoryName);
        WWContext.getInstance().fireSetLoading(true, WWGnlUtilities.buildMessage("archiving"));
        int nb = drillDownAndArchive(dir, deleteWhenDone);
        WWContext.getInstance().fireSetLoading(false, WWGnlUtilities.buildMessage("archiving"));
        JOptionPane.showMessageDialog(WWContext.getInstance().getMasterTopFrame(), WWGnlUtilities.buildMessage("archived-x-composites", new String[] { Integer.toString(nb) }), WWGnlUtilities.buildMessage("archive-composite"), 
                                      JOptionPane.INFORMATION_MESSAGE);
      }
    };
    archiver.start();
  }

  private static int drillDownAndGenerateImage(File dir, 
                                               File imgDir, 
                                               CommandPanel cp, 
                                               final Pattern pattern, 
                                               final Pattern faxPattern, 
                                               boolean countOnly,
                                               String displayOption,
                                               BufferedWriter corellation,
                                               boolean withBoatAndTrack)
  {
    int howMany = 0;
    
//  System.out.println("drillDownAndGenerateImage for " + dir.toString());

    if (!dir.exists() || !dir.isDirectory())
      throw new RuntimeException("[" + dir.getName() + "] not found, or is not a directory");
    else
    {
      File[] flist = dir.listFiles(new FileFilter()
          {
            public boolean accept(File pathname)
            {
              boolean ok = pathname.isDirectory() || (!pathname.isDirectory() && 
                                                      (pathname.toString().endsWith(".xml") || 
                                                       pathname.toString().endsWith(WWContext.WAZ_EXTENSION)));
//            System.out.println("Filtering: [" + pathname.toString() + "] :" + ok);
              if (ok && !pathname.isDirectory() && pattern != null)
              {
                Matcher m = pattern.matcher(pathname.toString());
                System.out.println("Checking if [" + pathname.toString() + "] matches [" + pattern + "]");
                if (!m.matches())
                  ok = false;
              }
              return ok;
            }
          });
//    Arrays.sort(flist, Collections.reverseOrder());
      for (int i=0; i<flist.length && keepWorking.valueOf(); i++)
      {
        if (flist[i].isDirectory())
          howMany += drillDownAndGenerateImage(flist[i], imgDir, cp, pattern, faxPattern, countOnly, displayOption, corellation, withBoatAndTrack);
        else
        {
          String fName = flist[i].getAbsolutePath();
          if (!countOnly)
          {
            System.out.println("Generating Image for Composite [" + fName + "]");
            // That's here !!
            int nbc = cp.restoreComposite(flist[i].getAbsolutePath(), displayOption, faxPattern, withBoatAndTrack);
            if (nbc == 0)
            {
              System.out.println(">>>>>> No component for " + fName + ", skipping.");
              continue;
            }
//          else
//            System.out.println(">>> " + nbc + " component(s) for " + fName);
            String compositeComment = cp.getCurrentComment();            
            String imgfName = fName.substring(fName.lastIndexOf(File.separator) + 1) + ".png"; 
            if (corellation != null)
            {
              String xmlCorellation = "  <data file='" + imgfName + "'>" +
                                          "<![CDATA[" + compositeComment + "]]>" +
                                        "</data>\n";
              try { corellation.write(xmlCorellation); } catch (Exception ignore) {}
            }
            imgfName = imgDir.toString() + File.separator + imgfName;
            String prefix = imgfName.trim().substring(0, imgfName.trim().lastIndexOf("."));
            String suffix = imgfName.trim().substring(imgfName.trim().lastIndexOf(".") + 1);        
//          System.out.print("Generating image from composite (" + imgfName + ")...");
            int[] ret = cp.getChartPanel().genImage(prefix, suffix.toLowerCase());
//          System.out.println("Ok.");
            WWContext.getInstance().fireProgressing();
          }
          howMany++;
        } 
      }      
    }
    return howMany;
  }

  private static int drillDownAndArchive(File dir, Boolean b)
  {
    int howMany = 0;
    if (!dir.exists() || !dir.isDirectory())
      throw new RuntimeException("[" + dir.getName() + "] not found, or is not a directory");
    else
    {
      File[] flist = dir.listFiles(new FileFilter()
          {
            public boolean accept(File pathname)
            {
              boolean ok = pathname.isDirectory() || (!pathname.isDirectory() && pathname.toString().endsWith(".xml"));
//            System.out.println("Filtering: [" + pathname.toString() + "] :" + ok);
              return ok;
            }
          });
      Arrays.sort(flist, Collections.reverseOrder());
      for (int i=0; i<flist.length; i++)
      {
        if (flist[i].isDirectory())
          howMany += drillDownAndArchive(flist[i], b);
        else
        {
//        System.out.println("Archiving " + flist[i].getAbsolutePath());
          archiveComposite(flist[i].getAbsolutePath(), b);
          howMany++;
        } 
      }      
    }
    return howMany;
  }
              
  public static String archiveComposite(String compositeName)
  {
    Boolean deleteWhenDone = null;
    // 1 - Ask: Copy or Move
    int resp = JOptionPane.showConfirmDialog(WWContext.getInstance().getMasterTopFrame(), 
                                             WWGnlUtilities.buildMessage("do-you-wish-to-delete"), 
                                             WWGnlUtilities.buildMessage("archive-composite"), 
                                             JOptionPane.YES_NO_CANCEL_OPTION, 
                                             JOptionPane.QUESTION_MESSAGE);
    deleteWhenDone = Boolean.valueOf(resp == JOptionPane.YES_OPTION);
    if (resp == JOptionPane.CANCEL_OPTION)
      return "Cancelled";
      
    return archiveComposite(compositeName, deleteWhenDone);
  }  
  
  public static String archiveComposite(String compositeName, Boolean deleteOnceDone)
  {
    List<String> filesToDelete = null;
    if (deleteOnceDone.booleanValue())
    {
      filesToDelete = new ArrayList<String>(1);
      filesToDelete.add(compositeName); // TODO Make sure... It will be deleted anyway.
    }
    String archiveName = "";
    String wazFileName = "";
    try
    {
      System.out.println("Archiving " + compositeName);
      archiveName = compositeName.substring(compositeName.lastIndexOf(File.separator) + 1);
      wazFileName = findUnusedFileName(compositeName.substring(0, compositeName.lastIndexOf(".xml")), WWContext.WAZ_EXTENSION);
      // Create archive output stream.
      
      // Create temp dir
      String tempDir = "./temp/" + archiveName;
      File archiveDir = new File(tempDir);
      if (archiveDir.exists())
        archiveDir.delete();
      archiveDir.mkdirs();
      
      // 1 - Parse Composite XML doc
      DOMParser parser = WWContext.getInstance().getParser();
      try
      {
        synchronized (parser)
        {
          parser.setValidationMode(XMLParser.NONVALIDATING);
          parser.parse(new File(compositeName).toURI().toURL());
          XMLDocument composite = parser.getDocument();
          // 2 - Copy to temp directory
          // a - faxes
          NodeList faxes = composite.selectNodes("/storage/fax-collection/fax");
          for (int i=0; i<faxes.getLength(); i++)
          {
            WWContext.getInstance().fireProgressing(WWGnlUtilities.buildMessage("archiving-fax-num", new String[] { Integer.toString(i+1) }));
            XMLElement fax = (XMLElement)faxes.item(i);
            String faxName = fax.getAttribute("file");
    //      System.out.println("FileName:" + faxName);
            String faxDir = tempDir + "/faxes";
            File faxDirectory = new File(faxDir);
            if (!faxDirectory.exists())
              faxDirectory.mkdirs();
            String newFax = "";
            if (faxName.indexOf("/") > -1)
              newFax = faxName.substring(faxName.lastIndexOf("/") + 1);
            else
             newFax = faxName.substring(faxName.lastIndexOf(File.separator) + 1); // Legacy
    //      System.out.println("Copying " + faxName + " into " + newFax);
            FileInputStream fis = null;
            FileOutputStream fos = null;
            try
            {
              fis = new FileInputStream(faxName);
              fos = new FileOutputStream(new File(faxDir, newFax));
              Utilities.copy(fis, fos);
              fis.close();
              fos.close();
            }
            catch (Exception ex)
            {
              ex.printStackTrace();
            }
            finally
            {
              if (fis != null) fis.close();
              if (fos != null) fos.close();
            }
            // SailMail header file?
            if (faxName.toUpperCase().endsWith(".TIF") || faxName.toUpperCase().endsWith(".TIFF"))
            {
              String headerFileName = WWGnlUtilities.getHeaderFileName(faxName);
              if (WWGnlUtilities.headerFileExists(headerFileName))
              {
                try
                {
                  String newFaxHeader = "";
                  if (headerFileName.indexOf("/") > -1)
                    newFaxHeader = headerFileName.substring(headerFileName.lastIndexOf("/") + 1);
                  else
                    newFaxHeader = headerFileName.substring(headerFileName.lastIndexOf(File.separator) + 1);  // Legacy

                  System.out.println(" ---> " + headerFileName + " becomes " + newFaxHeader);
                  
                  fis = new FileInputStream(headerFileName);
                  fos = new FileOutputStream(new File(faxDir, newFaxHeader));
                  Utilities.copy(fis, fos);
                  fis.close();
                  fos.close();
                }
                catch (Exception ex)
                {
                  ex.printStackTrace();
                }
                finally
                {
                  if (fis != null)
                    fis.close();
                  if (fos != null)
                    fos.close();
                }
                if (deleteOnceDone.booleanValue())
                  filesToDelete.add(headerFileName);
              }
            }
            fax.setAttribute("file", WWContext.WAZ_PROTOCOL_PREFIX + "faxes/" + newFax);
            if (deleteOnceDone.booleanValue())
              filesToDelete.add(faxName);
          }
          // b - grib
    //    NodeList gribs = composite.selectNodes("/storage/grib[not (@in-line = 'true')]");
          NodeList gribs = composite.selectNodes("/storage/grib");
          for (int i=0; i<gribs.getLength(); i++)
          {
            WWContext.getInstance().fireProgressing(WWGnlUtilities.buildMessage("archiving-grib"));
            XMLElement grib = (XMLElement)gribs.item(i);
            Text gribNameNode = (Text)grib.getFirstChild();
            if (gribNameNode != null)
            {
              String gribName = gribNameNode.getNodeValue();
      //      System.out.println("FileName:" + faxName);
              String gribDir = tempDir + "/grib";
              File gribDirectory = new File(gribDir);
              if (!gribDirectory.exists())
                gribDirectory.mkdirs();
              String newGrib = "";
              if (gribName.indexOf("/") > -1)
                newGrib = gribName.substring(gribName.lastIndexOf("/") + 1);
              else
               newGrib = gribName.substring(gribName.lastIndexOf(File.separator) + 1); // Legacy
      //      System.out.println("Copying " + faxName + " into " + newFax);
              FileInputStream fis = null;
              FileOutputStream fos = null;
              try
              {
                fis = new FileInputStream(gribName);
                fos = new FileOutputStream(new File(gribDir, newGrib));
                Utilities.copy(fis, fos);
                fis.close();
                fos.close();
              }
              catch (Exception ex)
              {
                ex.printStackTrace();
              }
              finally
              {
                if (fis != null)
                  try { fis.close(); } catch (Exception ignore) {}
                if (fos != null)
                  try { fos.close(); } catch (Exception ignore) {}
              }
              gribNameNode.setNodeValue(WWContext.WAZ_PROTOCOL_PREFIX + "grib/" + newGrib);
              if (deleteOnceDone.booleanValue())
                filesToDelete.add(gribName);
            }
          }
          // c - GPX Data file
          // TODO GPX Data File
          
          // 3 - Spit out new XML Doc
          composite.print(new FileOutputStream(new File(tempDir, "composite.xml")));      
          // 4 - Archive all temp dir
          // a - create achive
          ZipOutputStream waz = new ZipOutputStream(new FileOutputStream(wazFileName));       
          // b - copy files into it. Loop and write
          recurseFile(archiveDir, waz, archiveDir);
          waz.close();
          // 5 - Delete temp dir
          boolean success = deleteDir(archiveDir);
          System.out.println("Deleting temp dir " + archiveDir.toString() + ":" + success);
          if (!success)
            deleteLater(archiveDir.toString());
          // 6 - Delete originals if necessary
          // Always delete the xml file when archive is requested
          File xml = new File(compositeName);
          if (!xml.exists() || !xml.canWrite())
            System.out.print("...does not exist or is write protected");
          boolean b = xml.delete();
          System.out.println(", done:" + b);
          if (!b) // Write in file to delete later
            deleteLater(compositeName);
          // Now the other guys.
          if (deleteOnceDone.booleanValue())
          {
            WWContext.getInstance().fireProgressing(WWGnlUtilities.buildMessage("deleting-from-filesystem"));
            Iterator<String> fd = filesToDelete.iterator();
            while(fd.hasNext())
            {
              String str = fd.next();
              System.out.print("Deleting " + str);
              File f = new File(str);
              if (!f.exists() || !f.canWrite())
                System.out.print("...does not exist or is write protected");
              b = f.delete();
              System.out.println(", done:" + b);
              if (!b) // Write in file to delete later
                deleteLater(str);
            }
          }
        }
      }
      catch (Exception ex)
      {
        JOptionPane.showMessageDialog(WWContext.getInstance().getMasterTopFrame(), 
                                      "Composite " + compositeName + "\n" + ex.toString(), WWGnlUtilities.buildMessage("archive-composite"), 
                                      JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();                                    
      }
    }
    catch (Exception ex)
    {
      System.err.println("Archiving " + compositeName);      
      ex.printStackTrace();
    }
    return wazFileName;
  }

  public static void updateComposite(String compositeName, XMLDocument newComposite)
  {
    try
    {
//    newComposite.print(System.out);
      String wazFileName = compositeName;
      try
      {
        File updated = File.createTempFile("WeatherWizard_tx_", ".");
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(updated));
        ZipFile zip = new ZipFile(wazFileName);

        Enumeration<? extends ZipEntry> ez = zip.entries();
        while (ez.hasMoreElements())
        {
          ZipEntry ze = ez.nextElement();
          if (!ze.getName().equals("composite.xml"))
          {
            zos.putNextEntry(ze);
            Utilities.copy(zip.getInputStream(ze), zos);
          }
          else
          {
            ZipEntry composite =  new ZipEntry("composite.xml");
            if (composite != null)
            {
              zos.putNextEntry(composite);
              newComposite.print(zos);
            }
            zos.closeEntry();            
          }
        }
        zos.close();
        zip.close();
        // Delete original file
        new File(wazFileName).delete();
        // Rename temp one
        boolean ok = updated.renameTo(new File(wazFileName));
        if (!ok)
        { // If it failed, copy old into new
          FileInputStream fis = new FileInputStream(updated);
          FileOutputStream fos = new FileOutputStream(wazFileName);
          Utilities.copy(fis, fos);
          fis.close();
          fos.close();
        }
        updated.deleteOnExit();
      }
      catch (Exception ex)
      {
        JOptionPane.showMessageDialog(WWContext.getInstance().getMasterTopFrame(), 
                                      "Update Composite " + compositeName + "\n" + ex.toString(), WWGnlUtilities.buildMessage("archive-composite"), 
                                      JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();                                    
      }
    }
    catch (Exception ex)
    {
      System.err.println("Updating " + compositeName);      
      ex.printStackTrace();
    }
  }

  public static String unarchiveComposite(String archiveName)
  {
    String generatedDirectory = null;
    generatedDirectory = archiveName.substring(0, archiveName.lastIndexOf(WWContext.WAZ_EXTENSION)) + "-" + "unarchived";
    File dir = new File(generatedDirectory);
    if (!dir.exists())
      dir.mkdirs();
    try
    {
      ZipFile waz = new ZipFile(archiveName);
      ZipEntry composite = waz.getEntry("composite.xml");
      if (composite != null)
      {
        InputStream is = waz.getInputStream(composite);
        File compositeFile = new File(dir, "composite.xml");
        FileOutputStream fos = new FileOutputStream(compositeFile);
        Utilities.copy(is, fos);
        fos.close();
        DOMParser parser = WWContext.getInstance().getParser();
        XMLDocument doc = null;
        synchronized (parser)
        {
          parser.setValidationMode(XMLParser.NONVALIDATING);
          parser.parse(compositeFile.toURI().toURL());
          doc = parser.getDocument();
        }
        if (doc != null)
        {
          // Faxes
          NodeList nl = doc.selectNodes("/storage/fax-collection/fax");
          for (int i=0; i<nl.getLength(); i++)
          {
            XMLElement fax = (XMLElement)nl.item(i);
            
            String faxName = fax.getAttribute("file");
            faxName = faxName.substring(WWContext.WAZ_PROTOCOL_PREFIX.length());
            InputStream fis = waz.getInputStream(waz.getEntry(faxName));      
            File faxDir = new File(dir, "faxes");
            if (!faxDir.exists())
              faxDir.mkdirs();
            String newFaxName = faxName.substring(faxName.lastIndexOf("/") + 1);
            File newFax = new File(faxDir, newFaxName);
            FileOutputStream faxOut = new FileOutputStream(newFax);
            Utilities.copy(fis, faxOut);
            faxOut.close();
            fax.setAttribute("file", newFax.getAbsolutePath());
          }
          // GRIB ?
          nl = doc.selectNodes("/storage/grib");
          for (int i=0; i<nl.getLength(); i++)
          {
            XMLElement grib = (XMLElement)nl.item(i);
            String gribName = grib.getFirstChild().getNodeValue();
            gribName = gribName.substring(WWContext.WAZ_PROTOCOL_PREFIX.length());
            InputStream fis = waz.getInputStream(waz.getEntry(gribName));      
            File gribDir = new File(dir, "grib");
            if (!gribDir.exists())
              gribDir.mkdirs();
            String newGribName = gribName.substring(gribName.lastIndexOf("/") + 1);
            File newGrib = new File(gribDir, newGribName);
            FileOutputStream gribOut = new FileOutputStream(newGrib);
            Utilities.copy(fis, gribOut);
            gribOut.close();
            grib.getFirstChild().setNodeValue(newGrib.getAbsolutePath());
          }
        }
        // Re-spit the patched composite.xml
        FileOutputStream fComposite = new FileOutputStream(compositeFile);
        doc.print(fComposite);
        fComposite.close();
      }
      else
        System.out.println("Bizarre...");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return generatedDirectory;
  }

  private static List<String> recurseDirectory(File dir, List<String> list, FilenameFilter filter)
  {
    if (dir.isDirectory())
    {
      File[] fa = dir.listFiles(filter);
      for (int i=0; i<fa.length; i++)
        list = recurseDirectory(fa[i], list, filter);
    }
    else
      list.add(dir.getAbsolutePath());
    return list;
  }
  
  public static void detectUnusedDocuments()
  {
    Component component = WWContext.getInstance().getMasterTopFrame();
     
    List<String> gribList = null;    
    List<String> faxList = null;    
    List<String> compositeList = null;    
    // Scan GRIBs
    FilenameFilter gribFilter = new FilenameFilter()
      {
        public boolean accept(File dir, String name)
        {
          File f = new File(dir, name);
          return f.isDirectory() ||
                 (!f.isDirectory() &&
                  (name.toUpperCase().endsWith(".GRB") ||
                   name.toUpperCase().endsWith(".GRIB")));
        }
      };
    gribList = new ArrayList<String>();
    String gribPath = ((ParamPanel.DataPath)ParamPanel.data[ParamData.GRIB_FILES_LOC][1]).toString();
    String[] pe = gribPath.split(File.pathSeparator);
    for (int i=0; i<pe.length; i++)
    {
      File gribDir = new File(pe[i]);
      gribList = recurseDirectory(gribDir, gribList, gribFilter);
    }
//  System.out.println("Found " + gribList.size() + " GRIB file(s)");
    // Scan faxes, full path
    FilenameFilter faxFilter = new FilenameFilter()
      {
        public boolean accept(File dir, String name)
        {
          File f = new File(dir, name);
          return f.isDirectory() ||
                 (!f.isDirectory() &&
                  (name.toUpperCase().endsWith(".TIF") ||
                   name.toUpperCase().endsWith(".TIFF") ||
                   name.toUpperCase().endsWith(".PNG") ||
                   name.toUpperCase().endsWith(".JPG") ||
                   name.toUpperCase().endsWith(".JPEG") ||
                   name.toUpperCase().endsWith(".GIF")));
        }
      };
    faxList = new ArrayList<String>();
    String faxPath = ((ParamPanel.DataPath)ParamPanel.data[ParamData.FAX_FILES_LOC][1]).toString();
    pe = faxPath.split(File.pathSeparator);
    for (int i=0; i<pe.length; i++)
    {
      File faxDir = new File(pe[i]);
      faxList = recurseDirectory(faxDir, faxList, faxFilter);
    }
//  System.out.println("Found " + faxList.size() + " Fax(es)");
    // Scan Composites (xml, not waz)
    String compositeDir = ((ParamPanel.DataDirectory)ParamPanel.data[ParamData.CTX_FILES_LOC][1]).toString();    
    FilenameFilter compositeFilter = new FilenameFilter()
      {
        public boolean accept(File dir, String name)
        {
          File f = new File(dir, name);
          return f.isDirectory() ||
                 (!f.isDirectory() && name.toUpperCase().endsWith(".XML"));
        }
      };
    compositeList = new ArrayList<String>();
    compositeList = recurseDirectory(new File(compositeDir), compositeList, compositeFilter);

    int origFaxNumber = faxList.size();
    int origGribNumber = gribList.size();
    
    // Now drilling into the composites...
    for (String comp : compositeList)
    {
      Composite composite = new Composite(comp);
//    System.out.println("Composite " + comp + " contains:");
      for (String c : composite.getFaxList())
      {
//      System.out.println(" Fax:" + c);
        File fax = new File(c);
        if (!fax.exists())
        {
          System.out.println("Composite [" + comp + "] mentions a non-existing fax [" + c + "]");
        }
        else
        {
          // Is it in the fax list?
          if (faxList.contains(fax.getAbsolutePath()))
          {
//          System.out.println("*** Removing from list [" + fax.getAbsolutePath() + "]");
            faxList.remove(fax.getAbsolutePath());
          }
          else
            System.out.println("Composite [" + comp + "] fax [" + fax.getAbsolutePath() + "] not in list...");
        }
      }
      String gfName = composite.getGribFileName();
      if (gfName != null && gfName.trim().length() > 0)
      {
        File grib = new File(gfName);
        if (gribList.contains(grib.getAbsolutePath()))
        {
          gribList.remove(grib.getAbsolutePath());
        }
        else
          System.out.println("Composite [" + comp + "] grib [" + grib.getAbsolutePath() + "] not in list...");
      }
    }
    Collections.sort(gribList);
    Collections.sort(faxList);
    
    OneColumnTablePanel gribPanel = new OneColumnTablePanel(WWGnlUtilities.buildMessage("splash-grib"));
    gribPanel.setData(gribList);
    OneColumnTablePanel faxPanel = new OneColumnTablePanel(WWGnlUtilities.buildMessage("splash-faxes"));
    faxPanel.setData(faxList);
    
    JPanel tabPanel = new JPanel();
    tabPanel.setLayout(new BorderLayout());
    JTabbedPane tabs = new JTabbedPane();
    tabPanel.add(tabs, BorderLayout.CENTER);
    tabs.add(WWGnlUtilities.buildMessage("unused-faxes", new String[] { Integer.toString(faxList.size()), Integer.toString(origFaxNumber) }), faxPanel);
    tabs.add(WWGnlUtilities.buildMessage("unused-gribs", new String[] { Integer.toString(gribList.size()), Integer.toString(origGribNumber) }), gribPanel);
    if (false)
    {
      String mess = "Found " + gribList.size() + "/" + origGribNumber + " unused GRIB file(s)\n" + 
                    "Found " + faxList.size() + "/" + origFaxNumber + " unused fax(es)\n" + 
                    "From " + compositeList.size() + " xml composite(s)\n" +
                    "Work in progress...";
      System.out.println(mess);
    }
    JOptionPane.showMessageDialog(component, tabPanel, WWGnlUtilities.buildMessage("detect-unused"), JOptionPane.INFORMATION_MESSAGE);    
  }
  
  public static void placesMgmt(Component comp)
  {
    PlacesTablePanel ptp = new PlacesTablePanel();  
    int resp = JOptionPane.showConfirmDialog(comp, ptp, WWGnlUtilities.buildMessage("places-mgmt"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (resp == JOptionPane.OK_OPTION)
    {
      // Save the work here
      ptp.saveData();
      // Repaint chart
      WWContext.getInstance().fireChartRepaint();     
    }
  }
  
  public static void manageUE(Component comp)
  {
    DOMParser parser = WWContext.getInstance().getParser();
    try
    {
      XMLDocument doc = null;
      synchronized(parser)
      {
        parser.parse(new File(USEREXITS_FILE_NAME).toURI().toURL());
        doc = parser.getDocument();
      }
      UserExitTablePanel uetp = new UserExitTablePanel(doc);
      int resp = JOptionPane.showConfirmDialog(comp, uetp, "User-Exits", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
      if (resp == JOptionPane.OK_OPTION)
      {
        // Save data
        uetp.saveData();
      }
    }
    catch (Exception ex)
    {
      
    }
  }
  
  public static void cleanupBackups()
  {
    List<String> toDelete = new ArrayList<String>();
    File from = new File(".." + File.separator + "all-libs");
    String ptrn = "\\.jar_[0-9]+$";
    final Pattern pattern = Pattern.compile(ptrn);
    FilenameFilter fnf = new FilenameFilter()
      {
        public boolean accept(File dir, String name)
        {
          File f = new File(dir, name);
          boolean accept = false;
          if (f.isDirectory())
            accept = true;
          else
          {
            Matcher m = pattern.matcher(name);
            if (m.find())
              accept = true;
          }
//        System.out.println("name [" + name + "] is " + (accept?"":"not ") + "matching");
          return accept;
        }
        
      };
    toDelete = recurseDirectory(from, toDelete, fnf);
    if (toDelete.size() > 0)
    {
      String mess = WWGnlUtilities.buildMessage("will-delete") + "\n";
      for (String s : toDelete)
        mess += (s + "\n");
      mess += ("\n" +
          WWGnlUtilities.buildMessage("proceed"));
      int resp = JOptionPane.showConfirmDialog(WWContext.getInstance().getMasterTopFrame(), 
                                               mess, WWGnlUtilities.buildMessage("cleanup-backup"), 
                                               JOptionPane.YES_NO_OPTION,
                                               JOptionPane.QUESTION_MESSAGE);
      if (resp == JOptionPane.YES_OPTION)
      {
        for (String s : toDelete)
        {
          File f = new File(s);
          f.delete();
        }
      }
    }
    else
      JOptionPane.showMessageDialog(WWContext.getInstance().getMasterTopFrame(), WWGnlUtilities.buildMessage("nothing-to-delete"), WWGnlUtilities.buildMessage("cleanup-backup"), 
                                    JOptionPane.INFORMATION_MESSAGE);
  }
  
  private static String findUnusedFileName(String radical, String extension)
  {
    String fileName = radical + extension;
    int i = 0;
    while (new File(fileName).exists())
    {
      fileName = radical + "__" + Integer.toString(i++) + extension;
    }
    return fileName;
  }
  
  public static void deleteLater(String fName)
  {
    try
    {
      String deleteFileName = "files2delete.txt";
      BufferedWriter bw = new BufferedWriter(new FileWriter(new File(deleteFileName), true)); // true: append
      bw.write(fName + "\n");
      bw.flush();
      bw.close();      
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public static void deleteNow()
  {
    try
    {
      String deleteFileName = "files2delete.txt";
      File f = new File(deleteFileName);
      if (f.exists())
      {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        while ((line = br.readLine()) != null)
        {
          System.out.println("Deleting " + line);
          boolean b = deleteDir(new File(line), false);
          if (!b)
            System.out.println("No way to delete " + line + "...");
        }
        br.close();      
        f.delete();
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
  
  public static boolean deleteDir(File dir)
  {
    return deleteDir(dir, true);
  }
  
  public static boolean deleteDir(File dir, boolean laterIfFailed)
  {
    if (dir.isDirectory())
    {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++)
      {
        System.out.println("Deleting " + children[i]);
        boolean success = deleteDir(new File(dir, children[i]), laterIfFailed);
        if (!success)
        {
          return false;
        }
      }
    }
    boolean b = false;
    if (!dir.exists())
      System.out.println(dir.toString() + ": not found.");
    else if (!dir.canWrite())
      System.out.println(dir.toString() + ": write protected.");
    else
    {
    // The directory is now empty so delete it
      b = dir.delete();
      if (!b && laterIfFailed)
        deleteLater(dir.toString());
    }
    return b;
  }

  private static void recurseFile(File f, ZipOutputStream zip, File rootDir) throws Exception
  {
    if (f.isDirectory())
    {
//    System.out.println("Looking into:" + f.getAbsolutePath());
      File[] toArchive = f.listFiles();
      for (int i=0; i<toArchive.length; i++)
        recurseFile(toArchive[i], zip, rootDir);
    }
    else
    {
      String fullFileName = f.getAbsolutePath();
      if (fullFileName.startsWith(rootDir.getAbsolutePath()))
        fullFileName = fullFileName.substring(rootDir.getAbsolutePath().length() + 1);
//    System.out.println("Archiving:" + f.getAbsolutePath() + " as " + fullFileName);
      writeToArchive(zip, f.getAbsolutePath(), Utilities.replaceString(fullFileName, File.separator, "/"));
    }
  }
  
  public static void writeToArchive(ZipOutputStream zip, String entryOriginLocation, String entryName) throws Exception
  {
    ZipEntry ze = new ZipEntry(entryName);                     
    zip.putNextEntry(ze);
    FileInputStream fin = new FileInputStream(entryOriginLocation);
    Utilities.copy(fin, zip);
    zip.closeEntry();
    fin.close();    
  }
  
  public static void writeToArchive(ZipOutputStream zip, InputStream is, String entryName) throws Exception
  {
    ZipEntry ze = new ZipEntry(entryName);                     
    zip.putNextEntry(ze);
    Utilities.copy(is, zip);
    zip.closeEntry();
  }
  
  public static String translatePath(String origDir, Date date)
  {
    String translated = "";
    String[] pathElem = origDir.split("/");
    for (int pe=0; pe<pathElem.length; pe++)
    {
      SimpleDateFormat sdfTest = null;
      try { sdfTest = new SimpleDateFormat(pathElem[pe]); } catch (Exception fmtEx) {} 
      if (sdfTest != null)
      {
        String s = sdfTest.format(date);
        pathElem[pe] = s;
      }
    }
    for (int pe=0; pe<pathElem.length; pe++)
      translated += ((translated.length()==0?"":"/") + pathElem[pe]);
    return translated;
  }
  
  private final static WWGnlUtilities.SpecialBool keepWorking = new WWGnlUtilities.SpecialBool(true);
  
  public static void generateImagesFromComposites(String fromDir, final CommandPanel cp)
  {
    String[] fromTo = chooseTwoFiles(cp, 
                                     JFileChooser.DIRECTORIES_ONLY, 
                                     new String[] { "" }, 
                                     WWGnlUtilities.buildMessage("composite-directories"),
                                     fromDir.toString(), 
                                     JFileChooser.DIRECTORIES_ONLY, 
                                     new String[] { "" }, 
                                     WWGnlUtilities.buildMessage("image-directory"), 
                                     ".", 
                                     WWGnlUtilities.buildMessage("gen-image-prompt"));
    final String startFrom  = fromTo[0];
    final String generateIn = fromTo[1];
    String regExpPattern    = fromTo[2]; 
    final String displayOpt = fromTo[3];
    final String pdfTitle   = fromTo[4];
    final boolean withBoatAndTrack = "true".equals(fromTo[5]);
    final String faxNameFilter = fromTo[6];
    
    Pattern pattern = null;
    Pattern faxNamePattern = null;
    
    if (startFrom.trim().length() > 0 && generateIn.trim().length() > 0)
    {
      boolean ok2go = true;
      if (regExpPattern != null && regExpPattern.trim().length() > 0)
      {
        // 0 - Validate pattern
        try { pattern = Pattern.compile(regExpPattern); }
        catch (PatternSyntaxException pse)
        {
          ok2go = false;
          String mess = pse.toString() + "\n" +
                        WWGnlUtilities.buildMessage("invalid-pattern"); 
          int resp = JOptionPane.showConfirmDialog(WWContext.getInstance().getMasterTopFrame(), 
                                                   mess, 
                                                   WWGnlUtilities.buildMessage("pattern-validation"), 
                                                   JOptionPane.OK_CANCEL_OPTION, 
                                                   JOptionPane.WARNING_MESSAGE);
          if (resp == JOptionPane.OK_OPTION)
          {
            ok2go = true;
            pattern = null;
          }
        }
      }
      if (faxNameFilter != null && faxNameFilter.trim().length() > 0)
      {
        try { faxNamePattern = Pattern.compile(faxNameFilter); }
        catch (PatternSyntaxException pse)
        {
          ok2go = false;
          String mess = pse.toString() + "\n" +
                        WWGnlUtilities.buildMessage("invalid-pattern"); 
          int resp = JOptionPane.showConfirmDialog(WWContext.getInstance().getMasterTopFrame(), 
                                                   mess, WWGnlUtilities.buildMessage("pattern-validation"), 
                                                   JOptionPane.OK_CANCEL_OPTION, 
                                                   JOptionPane.WARNING_MESSAGE);
          if (resp == JOptionPane.OK_OPTION)
          {
            ok2go = true;
            faxNamePattern = null;
          }
        }
      }
      if (ok2go) // Means pattern validated
      {
        // Store patterns
        try
        {
          Properties props = new Properties();
          props.setProperty(COMPOSITE_FILTER, regExpPattern);
          props.setProperty(FAX_NAME_FILTER, faxNameFilter);
          props.store(new FileWriter(REGEXPR_PROPERTIES_FILE), "Last Regular Expressions");
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
        }
        // 1 - Count        
        final int howMany = drillDownAndGenerateImage(new File(startFrom), new File(generateIn), cp, pattern, faxNamePattern, true, displayOpt, null, withBoatAndTrack);       
        int resp = JOptionPane.showConfirmDialog(cp, 
                                                 WWGnlUtilities.buildMessage("image-gen-prompt", 
                                                                             new String[] { Integer.toString(howMany) }), 
                                                 WWGnlUtilities.buildMessage("image-generation"), 
                                                 JOptionPane.OK_CANCEL_OPTION, 
                                                 JOptionPane.QUESTION_MESSAGE);
        if (resp == JOptionPane.OK_OPTION)
        {
          final Pattern ptrn = pattern;
          final Pattern faxPtrn = faxNamePattern;
          Runnable heavyRunnable = new Runnable() // Show progress bar
          {
//          ProgressMonitor monitor = null;
            
            public void run()
            {
              keepWorking.setValue(true);
              WWContext.getInstance().setMonitor(ProgressUtil.createModalProgressMonitor(WWContext.getInstance().getMasterTopFrame(), howMany, false));
              WWContext.getInstance().getMonitor().start(WWGnlUtilities.buildMessage("generating-image", new String[] { "1", Integer.toString(howMany)}));

              if (WWContext.getInstance().getAel4monitor() != null)
              {
                System.out.println("Warning!!! AELMonitor != null !! (1, in " + this.getClass().getName() + ")" );  
              }
              
              WWContext.getInstance().setAel4monitor(new ApplicationEventListener()
                {
                  public String toString()
                  {
                    return "from Runnable in WWGnlUtilities (1).";
                  }
                  public void progressing() 
                  {
                    int newValue = WWContext.getInstance().getMonitor().getCurrent() + 1;
                    String mess = WWGnlUtilities.buildMessage("generating-image", new String[] { Integer.toString(newValue + 1), Integer.toString(howMany)});
    //              System.out.println(mess);
                    WWContext.getInstance().getMonitor().setCurrent(mess, newValue);
                  }
                  
                  public void interruptProgress()
                  {
                    System.out.println("Interrupting image processing.");
                    keepWorking.setValue(false);
                  }
                });
              WWContext.getInstance().addApplicationListener(WWContext.getInstance().getAel4monitor());
              
              try
              {
                // A file (xml) for the comments
                BufferedWriter xc = new BufferedWriter(new FileWriter("image-comments.xml"));
                xc.write("<root pattern='" + ptrn + "'>\n");
                // Process Image Here
                int howMuch = drillDownAndGenerateImage(new File(startFrom), 
                                                        new File(generateIn), 
                                                        cp, 
                                                        ptrn, 
                                                        faxPtrn,
                                                        false,
                                                        displayOpt,
                                                        xc,
                                                        withBoatAndTrack); 
                xc.write("</root>\n");
                xc.close();
                if (howMuch > 0 && pdfTitle != null && pdfTitle.trim().length() > 0)
                {
                  JOptionPane.showMessageDialog(null, 
                                                "Will now generate pdf here [" + pdfTitle + "], from user.dir [" + System.getProperty("user.dir") + "]", 
                                                "Images Generation", 
                                                JOptionPane.INFORMATION_MESSAGE);
                  try
                  {
                    generatePDFDataFromImages(generateIn, pdfTitle);
                    // Generate pdf
                    try
                    {
                      // TASK Other systems
//                    String cmd = "cmd /k start /min ." + File.separator + "publish-journal.bat \"journal.xml\" \"journal.pdf\"";
                      String cmd = "cmd /k start ." + File.separator + "publish.pdf.images.bat";
                      Runtime.getRuntime().exec(cmd);
                    }
                    catch (Exception e)
                    {
                      e.printStackTrace();
                    }
                  }
                  catch (Exception ex)
                  {
                    ex.printStackTrace();
                  }
                }
                
                cp.removeComposite();

                String mess = WWGnlUtilities.buildMessage("after-img-gen", // Show on File System after generation?
                                                          new String[] {Integer.toString(howMuch)});
                int resp = JOptionPane.showConfirmDialog(cp, 
                                                         mess, WWGnlUtilities.buildMessage("image-generation"), 
                                                         JOptionPane.YES_NO_OPTION, 
                                                         JOptionPane.QUESTION_MESSAGE);
                if (resp == JOptionPane.YES_OPTION)
                {
                  try { Utilities.showFileSystem(generateIn); } 
                  catch (Exception ex)
                  { JOptionPane.showMessageDialog(cp, ex.toString(), "Oops...", JOptionPane.ERROR_MESSAGE); }
                }
              }
              catch (IOException ioe)
              {
                ioe.printStackTrace();
              }
              finally
              {
                // to ensure that progress dlg is closed in case of any exception
                if (WWContext.getInstance().getMonitor().getCurrent() != WWContext.getInstance().getMonitor().getTotal())
                  WWContext.getInstance().getMonitor().setCurrent(null, WWContext.getInstance().getMonitor().getTotal());
                WWContext.getInstance().removeApplicationListener(WWContext.getInstance().getAel4monitor());
                WWContext.getInstance().setAel4monitor(null);
                WWContext.getInstance().setMonitor(null);
              }
            }
          };
          new Thread(heavyRunnable, "image-generator").start();
        }
        else
          System.out.println("Image generation canceled by user.");
      }
    }
  }
  
  public static void generatePDFDataFromImages(String imgLoc, String pdfTitle) throws Exception
  {
    String[] month = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
    
  // PrintStream out = System.out;
    OutputStream os = new FileOutputStream("." + File.separator + "data4pdf.xml");
    PrintStream out = new PrintStream(os) ;
    
    File root = new File(imgLoc);
    File[] files = root.listFiles(new FilenameFilter()
      {
        public boolean accept(File dir, String name)
        {
          return name.endsWith(".waz.png");
        }
      });
    
    DOMParser parser = new DOMParser();
    XMLDocument doc = null;
    try
    {
      File f = new File("." + File.separator + "image-comments.xml");
      if (!f.exists())
        System.out.println("Tiens?");
      URL url = f.toURI().toURL();
      System.out.println("Parsing " + url.toString());
      parser.parse(new FileInputStream(f));
      doc = parser.getDocument();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    
    out.println("<root>");
    
    for (int i=0; i<files.length; i++)
    {
      String fullPath = files[i].getAbsolutePath();
      String fileName = fullPath.substring(fullPath.lastIndexOf(File.separator) + 1);
      String comment = "";
      if (doc != null)
      {
        try { comment = doc.selectNodes("//data[@file='" + fileName + "']").item(0).getTextContent(); }
        catch (Exception ex) {}
      }
      String date = fileName.substring(0, 4) + "-" +
                    month[Integer.parseInt(fileName.substring(5, 7)) - 1] + "-" +
                    fileName.substring(8, 10) + " " +
                    fileName.substring(11, 13) + ":00 UTC";
      out.println("  <file date='" + date + "' path='" + fullPath + "' comment='" + comment.replace('\n', ' ').replace('\r', ' ').replaceAll("&", "&amp;") + "'/>");      
    } 
    out.println("</root>");
    out.flush();
    out.close();
    os.close();
    
    // parameters file
    String prmFileName = "scalable.cfg";
    File prmFile = new File(".", prmFileName);
    if (prmFile.exists())
    {
      parser.parse(prmFile.toURI().toURL());
      doc = parser.getDocument();
      NodeList nl = doc.selectNodes("//xop:property[@name='xslt.doc-title']", new NSResolver()
        {
          public String resolveNamespacePrefix(String prefix)
          {
            return "http://xmlns.oracle.com/oxp/config/";
          }
        });
      if (nl.getLength() == 0)
      {
        System.out.println("doc-title not found...");
        doc.print(System.out);
      }
      else
      {
        nl.item(0).setTextContent("'" + pdfTitle + "'");
        PrintWriter pw = new PrintWriter(prmFile);
        doc.print(pw);
        pw.close();
      }
    }
    else
      System.out.println("Prm file not found: scalable.cfg");
  }
  
  public static boolean isPatternDynamic(String fileName)
  {
    boolean resp = false;
    try
    {
      XMLDocument doc = null;
      DOMParser parser = WWContext.getInstance().getParser();
      URL url = null;
      if (fileName.startsWith("http://"))
        url = new URL(fileName);
      else
        url = new File(fileName).toURI().toURL();
      synchronized (parser)
      {
        parser.setValidationMode(XMLParser.NONVALIDATING);
        parser.parse(url);
        doc = parser.getDocument();
      }
      NodeList one = doc.selectNodes("//fax/dynamic-resource");
      NodeList two = doc.selectNodes("//grib/dynamic-grib");
      resp = one.getLength() > 0 || two.getLength() > 0;
    }
    catch (Exception ex)
    {
      System.out.println("Exception Checking if [" + fileName + "] is dynamic");
      ex.printStackTrace();
    }
    return resp;
  }

  public static void drawIsoPoints(Graphics gr, ChartPanel cp, List<List<List<GeoPoint>>> data, Color lineColor)
  {
    drawIsoPoints(gr, cp, data, lineColor, new int[] { -1 } );
  }

  public static void drawIsoPoints(Graphics gr, 
                                   ChartPanel chartPanel, 
                                   List<List<List<GeoPoint>>> data, 
                                   Color lineColor, 
                                   int[] doubleThick)
  {
    gr.setColor(lineColor);
    Stroke originalStroke = null;
    Graphics2D g2d = null;
    if (gr instanceof Graphics2D)
    {
      g2d = (Graphics2D)gr;
      originalStroke = g2d.getStroke();
      Stroke stroke =  new BasicStroke(2, 
                                       BasicStroke.CAP_BUTT,
                                       BasicStroke.JOIN_BEVEL);
      g2d.setStroke(stroke);  
    }
    int BEFORE_AFTER = 3; // TASK Externalize that one, width for smoothing. But 3 sounds good.   
    if (data == null)
      return;
      
    int i = 0;
    for (List<List<GeoPoint>> level : data)
    {
      if (level == null)
        continue;

      if (isIn(i, doubleThick))      
        g2d.setStroke(new BasicStroke(4, 
                                      BasicStroke.CAP_BUTT,
                                      BasicStroke.JOIN_BEVEL));  
        
//    Iterator<ArrayList<GeoPoint>> islandIterator = level.iterator(); 
//    Iterator<GeoPoint> it = null;
      for (List<GeoPoint> curve : level)
//    while (islandIterator.hasNext())
      {
        GeoPoint firstPt = null;
        GeoPoint current = null;
        Point previous = null;
        Point firstPanelPoint = null;
//      List<GeoPoint> ap = islandIterator.next();
//      System.out.println("Curve Size:" + curve.size());
        if (curve.size() > BEFORE_AFTER) // Arbitraire
        {
          GeoPoint _first = curve.get(0);
          GeoPoint _last  = curve.get(curve.size() - 1);
//        double closeLoopDistance = _first.orthoDistanceBetween(_last);
          double closeLoopDistance = _first.loxoDistanceBetween(_last);
          
//        it = ap.iterator();
          int nb = 0;
          for (GeoPoint gp : curve)
//        while (it.hasNext())
          {
            current = gp;
            
//          Point plot = chartPanel.getPanelPoint(gp);
//          gr.fillOval(plot.x - 2, plot.y - 2, 4, 4);
            
            double X = 0D, Y = 0D;
      
            int bi = nb - BEFORE_AFTER;
            int ai = nb + BEFORE_AFTER;
            for (int idx = bi; idx<=ai; idx++)
            {
              int _idx = idx;                    
              if (idx < 0) 
              {
                if (closeLoopDistance > 300D)
                  _idx = 0; 
                else
                  _idx = curve.size() + idx; 
              }
              if (idx > curve.size() - 1) 
              {
                if (closeLoopDistance > 300D)
                  _idx = curve.size() - 1; 
                else
                  _idx = idx - curve.size();
              }
              Point _p = chartPanel.getPanelPoint(curve.get(_idx));
              X += _p.x;
              Y += _p.y;
            }
            int abs = (int)Math.round(X / (2 * BEFORE_AFTER + 1));
            int ord = (int)Math.round(Y / (2 * BEFORE_AFTER + 1));
  //        gr.fillOval(abs - 2, ord - 2, 4, 4); // Draw the point
            if (previous != null)
              gr.drawLine(previous.x, previous.y, abs, ord); // draw the line
            previous = new Point(abs, ord);
            if (firstPt == null)
            {
              firstPt = current; 
              firstPanelPoint = new Point(abs, ord);
            }
            nb++;
          }
          double dist = 0D;
//        if (firstPt != null && (dist = firstPt.orthoDistanceBetween(current)) < 300D) // Close the loop
          if (firstPt != null && (dist = firstPt.loxoDistanceBetween(current)) < 300D) // Close the loop
          {
    //                System.out.println("Distance between First & last:" + dist + "nm");
            gr.drawLine(firstPanelPoint.x, firstPanelPoint.y, previous.x, previous.y);
          }
        }
      }
      if (isIn(i, doubleThick))      
        g2d.setStroke(new BasicStroke(2, 
                                      BasicStroke.CAP_BUTT,
                                      BasicStroke.JOIN_BEVEL));  
      i++;
    }
    if (gr instanceof Graphics2D)
      g2d.setStroke(originalStroke);     
      
  }

  public static void drawBumps(Graphics gr, ChartPanel chartPanel, List<CurveUtil.GeoBump> bumps)
  {
    if (bumps != null)
    {
      Font f = gr.getFont();
      Font f2 = new Font(f.getName(), Font.BOLD, 20);
      gr.setFont(f2);
      Iterator<CurveUtil.GeoBump> iterator = bumps.iterator();
      while (iterator.hasNext())
      {
        CurveUtil.GeoBump gb = iterator.next();
        Point p = chartPanel.getPanelPoint(gb.getGeoPoint());
        String symbol = (gb.getType() == CurveUtil.GeoBump.L? WWGnlUtilities.buildMessage("low"): WWGnlUtilities.buildMessage("high"));
        int l = gr.getFontMetrics(f2).stringWidth(symbol);             
        gr.drawString(symbol, p.x - l/2, p.y + 10);
      }
      gr.setFont(f);
    }
  }
  
  public static boolean isIn(int i, int[] ia)
  {
    boolean b = false;
    for (int x=0; x<ia.length; x++)
    {
      if (i == ia[x])
      {
        b = true;
        break;
      }
    }
    return b;
  }

  public static boolean isIn(String str, JComboBox box)
  {
  //  System.out.print("Looking for " + str + " in the displayComboBox...");
    boolean ok;
    ok = false;
    for (int i=0; i<box.getItemCount(); i++)
    {
      if (box.getItemAt(i).equals(str))
      {
        ok = true;
        break;
      }
    }
  //  System.out.println(", found:" + ok);
    return ok;
  }
  
  public static void readCompositeFromURL(String url)
  {
    String urlToFetch = url;
    if (urlToFetch == null)
    {
      // Prompt user
  //  urlToFetch = "http://donpedro.lediouris.net/weather/waz/2009-08-23_18-AllPacific.waz";
      WazUrlPanel wup = new WazUrlPanel();
      int resp = JOptionPane.showConfirmDialog(WWContext.getInstance().getMasterTopFrame(),
                                               wup,
                                               WWGnlUtilities.buildMessage("reading-from-web"),
                                               JOptionPane.OK_CANCEL_OPTION,
                                               JOptionPane.QUESTION_MESSAGE);
      if (resp == JOptionPane.OK_OPTION)
        urlToFetch = wup.getURL().trim();
      else
        urlToFetch = null;
    }
    else
      urlToFetch = url;
    final String urlStr = urlToFetch; 
    if (urlStr != null && urlStr.length() > 0)
    {
      Runnable heavyRunnable = new Runnable()
      {
  //    ProgressMonitor monitor = null;
        public void run()
        {
          WWContext.getInstance().setMonitor(ProgressUtil.createModalProgressMonitor(WWContext.getInstance().getMasterTopFrame(), 1, true, true));
          WWContext.getInstance().getMonitor().start(WWGnlUtilities.buildMessage("reading-from-web"));
          if (WWContext.getInstance().getAel4monitor() != null)
          {
            System.out.println("Warning!!! AELMonitor != null !! (2, in " + this.getClass().getName() + ")" );  
          }
          
          WWContext.getInstance().setAel4monitor(new ApplicationEventListener()
            {
              public String toString()
              {
                return "from Runnable in WWGnlUtilities (2).";
              }
              public void progressing(String mess)
              {
                WWContext.getInstance().getMonitor().setCurrent(mess, WWContext.getInstance().getMonitor().getCurrent());
              }
  
  //          public void interruptProcess()
  //          {
  //            System.out.println("Interrupting Pattern Loading.");
  //          }
            });
          WWContext.getInstance().addApplicationListener(WWContext.getInstance().getAel4monitor());
  
          try
          {
            WWContext.getInstance().fireSetLoading(true);
            restoreFromURL(urlStr);
            WWContext.getInstance().fireSetLoading(false);
          }
          finally
          {
      //    System.out.println("End of Progress Monitor");
            // to ensure that progress dlg is closed in case of any exception
            if (WWContext.getInstance().getMonitor().getCurrent() != WWContext.getInstance().getMonitor().getTotal())
              WWContext.getInstance().getMonitor().setCurrent(null, WWContext.getInstance().getMonitor().getTotal());
//          WWContext.getInstance().removeApplicationListener(WWContext.getInstance().getAel4monitor());
            WWContext.getInstance().setAel4monitor(null);
            WWContext.getInstance().setMonitor(null);
          }
        }
      };
      new Thread(heavyRunnable, "web-downloader").start();    
    }
  }
    
  private static void restoreFromURL(String urlStr)
  {
    try
    {
      // 1 - Download the file
      File tempFile = null;
      URL wazUrl = new URL(urlStr);
      URLConnection connection = wazUrl.openConnection();
      connection.connect();
      InputStream dis = connection.getInputStream();
      tempFile = File.createTempFile("temp_waz_", ".waz");
      tempFile.deleteOnExit();
      FileOutputStream fos = new FileOutputStream(tempFile);
      Utilities.copy(dis, fos);        
      fos.flush();
      fos.close();
//    System.out.println("Download completed");
      // 2 - Open it!
      ((AdjustFrame)WWContext.getInstance().getMasterTopFrame()).getCommandPanel().restoreComposite(tempFile.getAbsolutePath());
    }
    catch (Exception ex)
    {
      System.err.println("Oops");
      ex.printStackTrace();
    }
  }

  public static String getSolarTimeTooltip(GeoPoint gp)
  {
    String str = "Solar Time: XX:XX:XX"; // Default
    try
    {
      Date ut = WWContext.getInstance().getCurrentUTC(); // TimeUtil.getGMT();
      long longUT = ut.getTime();
      long solarTime = longUT + (long)((gp.getG() / 15D) * 3600000D);
      Date solarDate = new Date(solarTime);
      SDF_SOLAR.setTimeZone(TimeZone.getDefault());
      str = buildMessage("solar") + ":" + SDF_SOLAR.format(solarDate);    
      if (false)
      {
        System.out.println("At " + gp.toString() + ":" + SDF_UT.format(ut) + ", Solar:" +  SDF_SOLAR.format(solarDate) + " (" + solarDate.toString() + ")");
      }
    }
    catch (Exception ignore) {}
    return str;
  }
  
  public static int nbOccurs(String in, char of)
  {
    int nb = 0;
    char[] ca = in.toCharArray();
    for (int i=0; i<ca.length; i++)
    {
      if (ca[i] == of)
        nb++;
    }
    return nb;
  }
  
  public static class FilePreviewer
              extends JComponent
           implements PropertyChangeListener
  {
    ImageIcon thumbnail = null;
    
    private final static int DEFAULT_WIDTH  = 300;
    private final static int DEFAULT_HEIGHT = 200;

    public FilePreviewer(JFileChooser fc)
    {
      setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
      fc.addPropertyChangeListener(this);
    }

    public void loadImage(File f)
    {
      if (f == null)
      {
        thumbnail = null;
      }
      else
      {
        try
        {
//        ImageIcon tmpIcon = new ImageIcon(f.getPath());        
          ImageIcon tmpIcon = new ImageIcon(ImageUtil.readImage(f.getPath()));        
          if (tmpIcon.getIconWidth() > (DEFAULT_WIDTH * 2))
          {
            thumbnail = new ImageIcon(tmpIcon.getImage().getScaledInstance((DEFAULT_WIDTH * 2), -1, Image.SCALE_DEFAULT));
          }
          else
          {
            thumbnail = tmpIcon;
          }
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
        }
      }
    }

    public void propertyChange(PropertyChangeEvent e)
    {
      String prop = e.getPropertyName();
      if (prop == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
      {
        if (isShowing())
        {
          loadImage((File) e.getNewValue());
          repaint();
        }
      }
    }

    public void paint(Graphics g)
    {
      if (thumbnail != null)
      {
        int x = getWidth() / 2 - thumbnail.getIconWidth() / 2;
        int y = getHeight() / 2 - thumbnail.getIconHeight() / 2;
        if (y < 0) y = 0;
        if (x < 5) x = 5;
        thumbnail.paintIcon(this, g, x, y);
      }
    }
  }
  
  public static class SpecialBool
  {
    private boolean b = true;
    public SpecialBool(boolean b)
    {
      this.b = b;
    }
    public boolean valueOf() { return this.b; }
    public void setValue(boolean b) { this.b = b; }
  }
  
  public static class Composite
  {
    List<String> faxList = null;
    String gribFileName = null;
    // TODO Other parameters
    public Composite(String fileName)
    {
      DOMParser parser = WWContext.getInstance().getParser();
      XMLDocument doc = null;
      synchronized (parser)
      {
        try
        {
          parser.setValidationMode(XMLParser.NONVALIDATING);
          parser.parse(new File(fileName).toURI().toURL());
          doc = parser.getDocument();
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
        }
      }
      // Now, the data
      try
      {
        NodeList nl = doc.selectNodes("/storage/fax-collection/fax");
        faxList = new ArrayList<String>(nl.getLength());
        for (int i=0; i<nl.getLength(); i++)
          faxList.add(((XMLElement)nl.item(i)).getAttribute("file"));
        nl = doc.selectNodes("/storage/grib");
        if (nl.getLength() > 0)
        {
          XMLElement grib = (XMLElement)nl.item(0);
          if (!"true".equals(grib.getAttribute("in-line")))
          {  
            try { gribFileName = grib.getFirstChild().getNodeValue(); }
            catch (NullPointerException npe) {}
          }
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }

    public List<String> getFaxList()
    {
      return faxList;
    }

    public String getGribFileName()
    {
      return gribFileName;
    }
  }
  
  /**
   *
   * @param c Original Color
   * @param transpency 0-255
   * @return the transparent Color
   */
  public static Color makeTransparentColor(Color c, int transpency)
  {
    return new Color(c.getRed(), c.getGreen(), c.getBlue(), transpency);
  }
  
  // TODO Implement...
  public static void drawBigBubble(Graphics gr,
                                   Color bgColor,
                                   Color fgColor,
                                   Point pt,
                                   String text)
  {
    String[] line = text.split("\n");
  }
  
  public static BoatPosition getSerialBoatPosition() throws Exception
  {
    BoatPositionSerialClient bpsc = new BoatPositionSerialClient();
    while (bpsc.getBoatPosition() == null && bpsc.allIsOk())
    {
      try { Thread.sleep(1000L); } catch (Exception ex) {} 
    }
    if (bpsc.allIsOk())
      return bpsc.getBoatPosition();
    else
      throw new RuntimeException(bpsc.getProblemCause());
  }
  
  public static BoatPosition getTCPBoatPosition() throws Exception
  {
    BoatPositionTCPClient bptc = new BoatPositionTCPClient();
    while (bptc.getBoatPosition() == null && bptc.allIsOk())
    {
      try { Thread.sleep(1000L); } catch (Exception ex) {}
    }
    if (bptc.allIsOk())
      return bptc.getBoatPosition();
    else
      throw new RuntimeException(bptc.getProblemCause());
  }
  
  public static BoatPosition getUDPBoatPosition() throws Exception
  {
    BoatPositionUDPClient bpuc = new BoatPositionUDPClient();
    while (bpuc.getBoatPosition() == null && bpuc.allIsOk())
    {
      try { Thread.sleep(1000L); } catch (Exception ex) {}
    }
    if (bpuc.allIsOk())
      return bpuc.getBoatPosition();
    else
      throw new RuntimeException(bpuc.getProblemCause());
  }
  
  public static BoatPosition getHTTPBoatPosition() throws Exception
  {
    BoatPosition bp = null;
    try
    {
      String nmeaPayload = HTTPClient.getContent((String) ParamPanel.data[ParamData.NMEA_SERVER_URL][1]);
      StringReader sr = new StringReader(nmeaPayload);
      DOMParser parser = WWContext.getInstance().getParser();
      XMLDocument doc = null;
      synchronized (parser)
      {
        parser.setValidationMode(DOMParser.NONVALIDATING);
        parser.parse(sr);
        doc = parser.getDocument();
      }
      try
      {
        double lat = 0d;
        try { lat = Double.parseDouble(doc.selectNodes("/data/lat[1]").item(0).getFirstChild().getNodeValue()); } catch (Exception ignore) {}
        double lng = 0d;
        try { lng = Double.parseDouble(doc.selectNodes("/data/lng[1]").item(0).getFirstChild().getNodeValue()); } catch (Exception ignore) {}
        GeoPoint gp = new GeoPoint(lat, lng);
        int hdg = 0;
        try { hdg = (int)Math.round(Double.parseDouble(doc.selectNodes("/data/cog").item(0).getFirstChild().getNodeValue())); } catch (Exception ignore) {}
        bp = new BoatPosition(gp, hdg);
      }
      catch (Exception ex)
      {
        WWContext.getInstance().fireExceptionLogging(ex);
        ex.printStackTrace();
      }
    }
    catch (HTTPClient.NMEAServerException nse)
    {
      throw nse;
    }
    catch (Exception e)
    {
      WWContext.getInstance().fireExceptionLogging(e);
      e.printStackTrace();
      throw e;
    }
    return bp;
  }
  
  public static void storeBoatPosAndHeading(double l, double g, int heading, File manualPositionFile)
  {
    // Write to file for next time
    XMLDocument doc = new XMLDocument();
    XMLElement root = (XMLElement)doc.createElement("root");
    doc.appendChild(root);
    root.setAttribute("latitude", Double.toString(l));
    root.setAttribute("longitude", Double.toString(g));
    root.setAttribute("heading", Integer.toString(heading));
    try { doc.print(new FileOutputStream(manualPositionFile)); } catch (Exception ioe) { ioe.printStackTrace(); }    
  }
  
  public static void getManualBoatPosition()
  {
    File manualPositionFile = new File("." + File.separator + "config" + File.separator + WWContext.MANUAL_POSITION_FILE);
    PositionInputPanel pip = new PositionInputPanel();
    // Manual position already exist?
    if (manualPositionFile.exists())
    {
      // Read and feed
       DOMParser parser = WWContext.getInstance().getParser();
       try
       {
         XMLDocument doc = null;
         synchronized(parser)
         {
           parser.parse(manualPositionFile.toURI().toURL());
           doc = parser.getDocument();
         }
         double lat = Double.parseDouble(((XMLElement)doc.getDocumentElement()).getAttribute("latitude"));
         double lng = Double.parseDouble(((XMLElement)doc.getDocumentElement()).getAttribute("longitude"));
         int hdg = Integer.parseInt(((XMLElement)doc.getDocumentElement()).getAttribute("heading"));
         pip.setData(lat, lng, hdg);
       }
       catch (Exception ex)
       {
         ex.printStackTrace();
       }
    }
    int resp = JOptionPane.showConfirmDialog(WWContext.getInstance().getMasterTopFrame(), pip, WWGnlUtilities.buildMessage("position-manual-entry"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (resp == JOptionPane.OK_OPTION)
    {
      double l = pip.getL();
      double g = pip.getG();
      int heading = pip.getHeading();
      WWContext.getInstance().fireManuallyEnterBoatPosition(new GeoPoint(l, g), heading);
      WWGnlUtilities.storeBoatPosAndHeading(l, g, heading, manualPositionFile);
    }
  }

  // Beaufort Scale                               0   1   2   3    4    5    6    7    8    9   10   11   12
  protected final static double[] BEAUFORT_SCALE = { 0d, 1d, 4d, 7d, 11d, 16d, 22d, 28d, 34d, 41d, 48d, 56d, 64d };

  public static int getBeaufort(double d)
  {
    int b = 0;
    for (int i=0; i<BEAUFORT_SCALE.length; i++)
    {
      if (d < BEAUFORT_SCALE[i])
      {
        b = i - 1;
        break;
      }
      else
        b = i;
    }
    return b;
  }
  
  /**
   * get the angle in radians, based on its sin & cos.
   * 
   * @param sin
   * @param cos
   * @return angle in radians 0-2PI
   */
  public static double getAngle(double sin, double cos)
  {
    double angle = 0;
    angle = Math.asin(sin);
    if (cos < 0)
      angle = Math.PI - angle;
    
    return angle;
  }
  
  /**
   *
   * @param p point to rotate
   * @param center rotation center
   * @param angle In degrees, counter clockwise
   * @return the rotated point
   */
  public static Point rotate(Point p, Point center, double angle)
  {
    int relativeX = p.x - center.x;
    int relativeY = p.y - center.y;
    
    int rotatedX = (int)((relativeX *  Math.cos(Math.toRadians(angle))) + (relativeY * Math.sin(Math.toRadians(angle))));
    int rotatedY = (int)((relativeX * -Math.sin(Math.toRadians(angle))) + (relativeY * Math.cos(Math.toRadians(angle))));
    
    Point r = new Point(center.x + rotatedX, 
                        center.y + rotatedY);
    return r;
  }

  public static void main(String[] args)
  {
    Point center = new Point(200, 100);
    Point toRotate = new Point(50, 50);
    Point rotated = rotate(toRotate, center, 90D);
    System.out.println("Rotated:" + rotated.x + ", " + rotated.y);
  }
  
  public static void main0(String[] args)
  {
    DecimalFormat df = new DecimalFormat("##0.00");
    for (int i=0; i<=360; i++)
    {
      double sin = Math.sin(Math.toRadians(i));
      double cos = Math.cos(Math.toRadians(i));
      double rad = getAngle(sin, cos);
      System.out.println("For:" + i + " => " + df.format(Math.toDegrees(rad)));
    }
  }
  
  public static void main2(String[] args)
  {
    String before = "\r\r\n  Akeu coucou  \r\n  \n";
    String after  = Utilities.superTrim(before);
    
    System.out.println("[" + after + "]");
    
    System.out.println("Archive test");;
    String ar = archiveComposite("C:\\_myWork\\_ForExport\\dev-corner\\olivsoft\\all-scripts\\composites\\2008\\01 - jan\\2008-01-17-AllPacific-GRIB.xml");
    System.out.println("Returned:" + ar);
  }
  
  public static void main1(String[] args)
  {
//  String ptrn = "jar_[0-9]+";
//  String ptrn = ".jar_[0-9]+";
    String ptrn = "\\.jar_[0-9]+$"; // + means one or many
//  String ptrn = "\\.jar_[0-9]$";
//  String ptrn = "\\.jar_[0-9]+\\>";
    
    System.out.println("Pattern: " + ptrn);
    String[] names = {"akeu.jar", "akeu.jar_1", "akeu.jar_2", "akeu.jar_02", "akeu_03", "akeu.jar_2 ", "akeujar_3" };
    Pattern pattern = Pattern.compile(ptrn);
    for (int i=0; i<names.length; i++)
    {
      Matcher m = pattern.matcher(names[i]);
      System.out.println("[" + names[i] + "] " + (m.find()?"matches":"does not match"));
    }
  }
  
  public static void main3(String[] args)
  {
    String comment = "This is a comment\non several Lines\rcontaing special character '&' to be replaced";
    System.out.println("Before:" + comment);
    System.out.println("After:" + comment.replace('\n', ' ').replace('\r', ' ').replaceAll("&", "&amp;"));
  }
  
  
  public static class SailMailStation
  {
    GeoPoint gp = null;
    String stationName = "";
    
    public SailMailStation(GeoPoint gp, String name)
    {
      this.gp = gp;
      this.stationName = name;
    }

    public void setGp(GeoPoint gp)
    {
      this.gp = gp;
    }

    public GeoPoint getGp()
    {
      return gp;
    }

    public void setStationName(String stationName)
    {
      this.stationName = stationName;
    }

    public String getStationName()
    {
      return stationName;
    }
  }
  
  public static class WeatherStation
  {
    GeoPoint gp = null;
    String stationName = "";
    
    public WeatherStation(GeoPoint gp, String name)
    {
      this.gp = gp;
      this.stationName = name;
    }

    public void setGp(GeoPoint gp)
    {
      this.gp = gp;
    }

    public GeoPoint getGp()
    {
      return gp;
    }

    public void setStationName(String stationName)
    {
      this.stationName = stationName;
    }

    public String getStationName()
    {
      return stationName;
    }
  }
  
  public static class BoatPosition
  {
    GeoPoint pos = null;
    int heading = -1;
    
    public BoatPosition()
    {
    }

    public BoatPosition(GeoPoint gp, int h)
    {
      pos = gp;
      heading = h;
    }

    public void setPos(GeoPoint pos)
    {
      this.pos = pos;
    }

    public GeoPoint getPos()
    {
      return pos;
    }

    public void setHeading(int heading)
    {
      this.heading = heading;
    }

    public int getHeading()
    {
      return heading;
    }
  }
}
