package main;


import chartview.ctx.WWContext;

import chartview.gui.AdjustFrame;
import chartview.gui.util.dialog.UpdatePanel;
import chartview.gui.util.param.ParamData;
import chartview.gui.util.param.ParamPanel;

import chartview.util.WWGnlUtilities;

import coreutilities.CheckForUpdateThread;
import coreutilities.NotificationCheck;
import coreutilities.Utilities;

import coreutilities.ctx.CoreContext;
import coreutilities.ctx.CoreEventListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


/**
 * Main GUI entry point
 */ 
public class ChartAdjust
{
  protected final AdjustFrame frame;
  
  public ChartAdjust()
  {
//  System.out.println("ClassLoader:" + this.getClass().getClassLoader().getClass().getName());
    // Cleanup from previous session if necessary
    WWGnlUtilities.deleteNow();
    // Find the compilation date...
    String lastModified = "";
    String fullPath2Class = this.getClass().getName();
    // try manifest first
    // Count number of dots
    int nbdots = 0;
    int i = 0;
    String str = fullPath2Class;
    while (i != -1)
    {
      i = str.indexOf(".", i);
      if (i != -1)
      {
        nbdots += 1;
        str = str.substring(i + 1);
      }
    }
    //  System.out.println("Found " + nbdots + " dot(s)");
    String resource = "";
    for (i=0; i<nbdots; i++)
      resource += (".." + "/");
    resource += ("meta-inf" + "/" + "Manifest.mf");

    String className = this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1) + ".class";
    URL me = this.getClass().getResource(className);
    //  System.out.println("Resource:" + me);
    String strURL = me.toString();
    
    String jarIdentifier = ".jar!/";
    if (strURL.indexOf(jarIdentifier) > -1)
    {
      try 
      { 
        String jarFileURL = strURL.substring(0, strURL.indexOf(jarIdentifier) + jarIdentifier.length()); // Must end with ".jar!/"
    //      System.out.println("Trying to reach [" + jarFileURL + "]");
        URL jarURL = new URL(jarFileURL);
        JarFile myJar = ((JarURLConnection)jarURL.openConnection()).getJarFile();
        Manifest manifest = myJar.getManifest();
        Attributes attributes = manifest.getMainAttributes();
        lastModified = attributes.getValue("Compile-Date");
        System.out.println("Compile-Date found in manifest:[" + lastModified + "]");
        WWContext.getInstance().setCompiled(lastModified);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
    
    if (lastModified == null || lastModified.trim().length() == 0)
    {
      strURL = strURL.substring(0, strURL.lastIndexOf(className));
      strURL += resource;
      try { me = new URL(strURL); } catch (Exception ex) { System.err.println(ex.toString()); }
      System.out.println("URL:" + me);
  
      try
      {
        URLConnection con = null;
        try { con = me.openConnection(); }
        catch (Exception ex)
        {
          System.out.println("Will try the class...");
        }
        if (con == null)
        {
          me = this.getClass().getResource(className);
          con = me.openConnection();
        }
        lastModified = con.getHeaderField("Last-modified");
        if (lastModified == null)
        {
//        System.out.println("Manifest not found");
          me = this.getClass().getResource(className);
          con = me.openConnection();
          lastModified = con.getHeaderField("Last-modified");
        }      
//      else
//        System.out.println("Found manifest");
  //    System.out.println(me.toExternalForm() + ", Last Modified:[" + lastModified + "]");
        if (lastModified != null)
        {
          // like Tue, "21 Sep 2004 13:37:32 GMT"
     //   DateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.US);
     //   Date age = df.parse(lastModified);
     //   long modified = age.getTime();
          WWContext.getInstance().setCompiled(lastModified);
        }
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
    
    ParamPanel.setUserValues();
/*  final JFrame */ frame = new AdjustFrame();
    
    boolean positioned = false;
    File propFile = new File("ww_position.properties");
    if (propFile.exists())
    {
      try
      {
        Properties props = new Properties();
        props.load(new FileReader(propFile));
        int w  = Integer.parseInt(props.getProperty("frame.width"));
        int h  = Integer.parseInt(props.getProperty("frame.height"));
        int x  = Integer.parseInt(props.getProperty("frame.x.pos"));
        int y  = Integer.parseInt(props.getProperty("frame.y.pos"));
        int dl = Integer.parseInt(props.getProperty("divider.location", "175"));
        frame.setSize(w, h);
        frame.setLocation(x, y);
        frame.setDividerLocation(dl);
        positioned = true;
      }
      catch (Exception forgetit) 
      { System.err.println(forgetit.toString()); }
    }
    
    if (!positioned)
    {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = frame.getSize();
      if(frameSize.height > screenSize.height)
        frameSize.height = screenSize.height;
      if(frameSize.width > screenSize.width)
        frameSize.width = screenSize.width;
      frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    }
    frame.addWindowListener(new WindowAdapter() 
      {
        public void windowClosing(WindowEvent e)
        {
          WWGnlUtilities.doOnExit(frame);
        }
      });
//  frame.setUndecorated(true);
    frame.setVisible(true);
    checkForUpdate();
    // lastModified, like Thu 02/16/2012 18:11:14.08
    Date compiledDate = null;
    try
    {
      SimpleDateFormat sdf = new SimpleDateFormat("E MM/dd/yyyy HH:mm:ss.SS", Locale.ENGLISH);
      sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
      compiledDate = sdf.parse(lastModified);
    }
    catch (ParseException pe)
    {
      // From the class ? like Sun, 19 Feb 2012 03:21:22 GMT 
      try
      {
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        compiledDate = sdf.parse(lastModified);        
      }
      catch (ParseException pe2)
      {
        try
        {
          SimpleDateFormat sdf = new SimpleDateFormat("E MM/dd/yyyy HH:mm:ss", Locale.ENGLISH); // Compiled on Linux
          sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
          compiledDate = sdf.parse(lastModified);        
        }
        catch (ParseException pe3)
        {
          // Give up...
          System.err.println(pe3.getLocalizedMessage());
        }
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    
//    Date now = new Date();
//    compiledDate = new Date(now.getTime() - (30L * 24L * 3600L * 1000L)); // 30 days before now
//    System.out.println("Compiled Date:" + compiledDate);
    
    checkForNotification(compiledDate);
    startMemoryProbe();
  }
  
  private static boolean proceed = false;
  
  public static void checkForUpdate()
  {
    // Checking for update
    proceed = ((Boolean)ParamPanel.data[ParamData.AUTO_UPDATES][ParamData.VALUE_INDEX]).booleanValue();
    Thread checkForUpdate = // new CheckForUpdateThread("weather_assistant");
      new CheckForUpdateThread(WWContext.PRODUCT_ID, 
                               WWContext.getInstance().getParser(), 
                               WWGnlUtilities.STRUCTURE_FILE_NAME, 
                               proceed);
    // Add listener
    CoreContext.getInstance().addApplicationListener(new CoreEventListener()
     {
       public void updateCompleted(List<String> fList)
       {
         System.out.println("Update Completed by the Core Context");
         if (fList != null && fList.size() > 0)
         {
           String downloadMess = "";
           for (String s : fList)
             downloadMess += (s + "\n");
           // Display file list
           UpdatePanel updatePanel = new UpdatePanel();
           sendPing("Software update requested for:\n" + downloadMess);
           WWContext.getInstance().fireSetStatus(WWGnlUtilities.buildMessage("soft-update-available"));
 
           if (proceed)
             updatePanel.setTopLabel(WWGnlUtilities.buildMessage("following-updated"));
           else
             updatePanel.setTopLabel(WWGnlUtilities.buildMessage("update-available"));
           updatePanel.setFileList(downloadMess);
           JOptionPane.showMessageDialog(WWContext.getInstance().getMasterTopFrame(), updatePanel, WWGnlUtilities.buildMessage("automatic-updates"), JOptionPane.INFORMATION_MESSAGE);
         }
         // Send Ping, whatever update has been done.
         Thread thread = new Thread("ping-thread")
           {
             public void run()
             {
               sendPing();
             }
           };
         thread.start();
         // TODO Remove CoreContext listener
       }
       public void networkOk(boolean b) 
       {
         WWContext.getInstance().fireSetStatus(WWGnlUtilities.buildMessage("network-connection-status", new String[] { Boolean.toString(b) }));
         WWContext.getInstance().fireNetworkOK(b);
         // TODO Remove CoreContext listener if !b
       }
     });
    checkForUpdate.start();
  }

  private final static String NOTIFICATION_PROP_FILE_NAME = "notification_" + WWContext.PRODUCT_KEY + ".properties";
  private final static SimpleDateFormat SDF = new SimpleDateFormat("E dd MMM yyyy, HH:mm:ss z");
  
  public static void checkForNotification(final Date manifestDate)
  {
    // Checking for notification
    proceed = ((Boolean)ParamPanel.data[ParamData.SHOW_NOTIFICATIONS][ParamData.VALUE_INDEX]).booleanValue(); 
    if (proceed)
    {
      Thread checkForNotification = new Thread()
        {
          public void run()
          {
            String notificationDate = "";
            Date providedDate = manifestDate;
            
            Properties props = new Properties();
            try
            {
              FileInputStream fis = new FileInputStream(NOTIFICATION_PROP_FILE_NAME);
              props.load(fis);
              fis.close();
              notificationDate = props.getProperty("date"); // UTC date
            }
            catch (Exception ex)
            {
              System.out.println("Properties file [" + NOTIFICATION_PROP_FILE_NAME + "] not found");
            }    
            try
            {
              if (providedDate != null)
              {
                Date propertiesDate = null;
                try
                {
                  propertiesDate = NotificationCheck.getDateFormat().parse(notificationDate);
                }
                catch (ParseException pe)
                {
                  if (notificationDate.trim().length() > 0)
                    System.err.println(pe.getLocalizedMessage());                  
                }
//              System.out.println("Properties Date:" + propertiesDate.toString() + ", Provided Date:" + providedDate.toString());
                if (notificationDate == null || notificationDate.trim().length() == 0 || propertiesDate.before(providedDate))
                  notificationDate = NotificationCheck.getDateFormat().format(providedDate);          
              }
              NotificationCheck nc = new NotificationCheck(WWContext.PRODUCT_KEY, notificationDate);
              Map<Date, String> map = nc.check();
              String productName = nc.getProductName();
              // Display Notification Here.
              if (map.size() > 0)
              {
                String title = "Notifications";
                if (productName.trim().length() > 0)
                  title += (" for " + productName); // LOCALIZE
                int resp = displayNotification(title, nc);
                if (resp == JOptionPane.OK_OPTION)
                {
                  props.setProperty("date", NotificationCheck.getDateFormat().format(new Date())); // Write UTC date
                  FileOutputStream fos = new FileOutputStream(NOTIFICATION_PROP_FILE_NAME);
                  props.store(fos, "Last notification date");
                  fos.close();
                }
              }
            }
            catch (Exception ex)
            {
              ex.printStackTrace();
            }    
          }
        };
      checkForNotification.start();           
    }
  }

  private static int displayNotification(String title, NotificationCheck nc)
  {    
    String result = 
    "<html><head><style type='text/css'>" +
    "body { background : #efefef; color : #000000; font-size: 10pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
    "h1 { color: white; font-style: italic; font-size: 14pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular; background-color: black; padding-left: 5pt }\n" + 
    "h2 { font-size: 12pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
    "h3 { font-style: italic; font-weight: bold; font-size: 10pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular; bold:  }\n" + 
    "li { font-size: 10pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
    "p { font-size: 10pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
    "td { font-size: 10pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
    "small { font-size: 8pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
    "blockquote{ font-style: italic; font-size: 10pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }-->\n" + 
    "em { font-size: 10pt; font-style: italic; font-weight: bold; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
    "pre { font-size: 9pt; font-family: Courier New, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
    "address { font-size: 8pt; font-family: Arial, Helvetica, Geneva, Swiss, SunSans-Regular }\n" + 
    "a:link { color : #000000} \n" + 
    "a:active { color: #000000} \n" + 
    "a:visited { color : #000000}\n" +
    "</style></head><body>\n";
    Map<Date, String> map = nc.check();
    // Display Notification Here.
    if (map.size() > 0)
    {
      Set<Date> keys = map.keySet();
      Date[] da = keys.toArray(new Date[keys.size()]);
      Arrays.sort(da);
      for (Date d : da)
      {
        String mess = map.get(d);
        result += ("<br><i><b>" + SDF.format(d) + "</b></i><br>" + mess + "<br>"); 
      }
    }
    // Display list
    result += ("</body></html>");
    // Produce clickable list here
    JPanel notificationPanel = new JPanel();
    notificationPanel.setPreferredSize(new Dimension(500, 300));
    JEditorPane jEditorPane = new JEditorPane();
    JScrollPane jScrollPane = new JScrollPane();
    notificationPanel.setLayout(new BorderLayout());
    jEditorPane.setEditable(false);
    jEditorPane.setFocusable(false);
    jEditorPane.setFont(new Font("Verdana", 0, 10));
    jEditorPane.setBackground(Color.lightGray);
    jScrollPane.getViewport().add(jEditorPane, null);
    jEditorPane.addHyperlinkListener(new HyperlinkListener()
      {
        public void hyperlinkUpdate(HyperlinkEvent he)
        {
          try
          {
            if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            {
    //            System.out.println("URL Activated:" + he.getURL().toString());
              String activatedURL = he.getURL().toString();
              String value = activatedURL;
              if (!value.startsWith("http://"))
                value = value.substring(value.indexOf("http://"));
              if (value.startsWith("http://"))
              {
                if (value.endsWith(".html/"))
                  value = value.substring(0, value.length() - 1);
                Utilities.openInBrowser(value);
              }
            }
          }
          catch (Exception ioe)
          {
            ioe.printStackTrace();
          }
        }
      });

    try
    {
      File tempFile = File.createTempFile("data", ".html");
      tempFile.deleteOnExit();
      BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
      bw.write(result);
      bw.flush();
      bw.close();              
      jEditorPane.setPage(tempFile.toURI().toURL());
      jEditorPane.repaint();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    notificationPanel.add(jScrollPane, BorderLayout.CENTER);
//  JLabel nbDayLabel = new JLabel();
//  nbDayLabel.setText("Blah-blah-blah.");
//  notificationPanel.add(nbDayLabel, BorderLayout.SOUTH);

    int resp = JOptionPane.showConfirmDialog(null, notificationPanel, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

    return resp;
  }

  private void startMemoryProbe()
  {
    Thread memoryProbeThread = new Thread()
      {
        public void run()
        {
          boolean ok = true;
          while (ok)
          {
            try
            {
              long mem = WWGnlUtilities.memoryProbe();
              String memMess = "Memory used:" + WWGnlUtilities.formatMem(mem); // LOCALIZE
              WWContext.getInstance().fireSetStatus(memMess);
              try { Thread.sleep(5000L); } catch (Exception ignore) {}
            }
            catch (NoClassDefFoundError ncdfe)
            {
              ok = false;
              System.err.println("--------------------------------------------------------------------------------------");
              System.err.println("** Warning: You must add tools.jar in your classpath for the memory probe to work...\n");
              System.err.println("** Your java.home is [" + System.getProperty("java.home") + "]");
              System.err.println("--------------------------------------------------------------------------------------");
            }
            catch (Exception ex)
            {
              ok = false;
              ex.printStackTrace();
            }
          }
        }
      };
    memoryProbeThread.start();
  }
  
  public static void main(String args[])
  {
    System.out.println("=======\nIn the main, " + args.length + " arguments:");
    String displayComposite = "";
    for (int i=0; i<args.length; i++)
    {
      System.out.println("arg[" + i + "]=" + args[i]);
      if ("-display-composite".equals(args[i]))
        displayComposite = args[i+1];
      if ("-debug-level".equals(args[i]))
      {
        int debugLevel = Integer.parseInt(args[i+1]);
        if (debugLevel < 0 || debugLevel > 5)
          throw new RuntimeException("Debug Level must be in [0, 5]");
        else
          WWContext.setDebugLevel(debugLevel);
      }
    }
    System.out.println("=======");
    if (displayComposite.trim().length() > 0)
    {
      System.out.println("Composite to display:" + displayComposite);
      System.setProperty("display.composite", displayComposite);
    }
    // Read config properties file
    File configFile = new File(WWContext.CONFIG_PROPERTIES_FILE);
    if (configFile.exists())
    {
      try
      {
        Properties props = new Properties();
        props.load(new FileInputStream(configFile));
        // Assign as System properties
        System.setProperty("tooltip.option", props.getProperty("tooltip.option", "on-chart")); // on-chart, none, tt-window        
        System.setProperty("composite.sort", props.getProperty("composite.sort", "date.desc")); // date, name, asc, desc
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
    // Start the UI
    String lnf = System.getProperty("swing.defaultlaf");
//  System.out.println("LnF:" + lnf);
    if (lnf == null) // Let the -Dswing.defaultlaf do the job.
    {
//    WWGnlUtilities.installLookAndFeel();
      try
      {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception e)
      {
        WWContext.getInstance().fireExceptionLogging(e);
        e.printStackTrace();
      }
    }
    JFrame.setDefaultLookAndFeelDecorated(true);
    if (false)
    {
      UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
      for (int i = 0; i < info.length; i++)
        System.out.println(info[i].getName() + ":" + info[i].getClassName());
    }
    try { new ChartAdjust(); }
    catch (Exception e)
    {
      System.err.println("Cought from the main:");
      System.err.println("---------------------");
      e.printStackTrace();
      System.err.println("---------------------");
    }
  }

  public static void sendPing()
  {
    sendPing(null);
  }
  
  public static void sendPing(String extra)
  {
  //    Properties properties = System.getProperties();
  //    Enumeration keys = properties.keys();
  //    while (keys.hasMoreElements())
  //    {
  //      String key = (String) keys.nextElement();
  //      String value = (String) properties.getProperty(key);
  //      System.out.println(key + "=" + value);
  //    }
    String mac = "";
    try 
    { 
      mac = Utilities.getMacAddress(); 
//    System.out.println("Physical Address:" + mac);
    } 
    catch (Exception e) 
    {
      e.printStackTrace();
    }
    final String messToSend = "Weather Wizard usage detected:\n"        +
      "date:" + new Date().toString() + ",\n" +
      "user.country:" + System.getProperty("user.country")                 + ",\n" + 
      "sun.os.patch.level:" + System.getProperty("sun.os.patch.level")     + ",\n" + 
      "java.runtime.version:" + System.getProperty("java.runtime.version") + ",\n" + 
      "os.arch:" + System.getProperty("os.arch")                           + ",\n" + 
      "os.name:" + System.getProperty("os.name")                           + ",\n" + 
      "os.version:" + System.getProperty("os.version")                     + ",\n" + 
      "user.name:" + System.getProperty("user.name")                       + ",\n" + 
      "user.language:" + System.getProperty("user.language")               + ",\n" + 
      "sun.desktop:" + System.getProperty("sun.desktop")                   + ",\n" + 
      "sun.cpu.isalist:" + System.getProperty("sun.cpu.isalist")           + ",\n" +
      "MAC Addr:" + mac                                                    + ",\n" + 
      "Compiled:" + WWContext.getInstance().getCompiled();
    final String userMess = extra;      
    
    final String username = System.getProperty("user.name");
    final String macaddress = mac;
    final String productname = "weather_assistant";
    
  //  System.out.println(messToSend);
    // Posting the message
    Thread ping = new Thread("updater")
    {
      public void run()
      {
        try
        {
          String data = "";
          URL url = null;
          if (userMess != null)
          {
            // Construct data, "message" parameter
            data = URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(messToSend + (userMess!=null?("\n\n" + userMess):""), "UTF-8");
            // Send data
            url = new URL("http://donpedro.lediouris.net/software/mail/sendMail.php");
          }
          else
          {
            // Construct data, "message" parameter
            data = URLEncoder.encode("product_name", "UTF-8") + "=" + URLEncoder.encode(productname, "UTF-8") + "&" +
                   URLEncoder.encode("mac_address",  "UTF-8") + "=" + URLEncoder.encode(macaddress,  "UTF-8") + "&" +
                   URLEncoder.encode("user_name",    "UTF-8") + "=" + URLEncoder.encode(username,    "UTF-8") + "&" +
                   URLEncoder.encode("misc_fields",  "UTF-8") + "=" + URLEncoder.encode(messToSend,  "UTF-8");
            // Send data
            url = new URL("http://donpedro.lediouris.net/software/mail/productUsage.php");
          }
          URLConnection conn = url.openConnection();
          conn.setDoOutput(true);
          OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
          wr.write(data);
          wr.flush();
          // Get the response, even if it is empty
          BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          String line;
          while ((line = rd.readLine()) != null) 
          {
            // Process line... Validation would occur here
//          System.out.println("\nReturned by the ping: [" + line + "]");
          }
//        JOptionPane.showMessageDialog(null, mess, "From OlivSoft", JOptionPane.INFORMATION_MESSAGE);
          wr.close();
          rd.close();
          WWContext.getInstance().fireNetworkOK(true);
        }
        catch (Exception e)
        {
//        System.out.println("Not on line (ping)");
          WWContext.getInstance().setOnLine(false);
          WWContext.getInstance().fireNetworkOK(false);
//        e.printStackTrace();
        }
      }
    };    
    ping.start();  
  }
}
