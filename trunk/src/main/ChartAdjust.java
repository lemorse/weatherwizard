package main;

import chartview.gui.AdjustFrame;

import chartview.gui.util.param.ParamData;

import chartview.util.WWGnlUtilities;
import chartview.ctx.WWContext;

import chartview.gui.util.dialog.UpdatePanel;
import chartview.gui.util.param.ParamPanel;

import coreutilities.CheckForUpdateThread;

import coreutilities.Utilities;

import coreutilities.ctx.CoreContext;

import coreutilities.ctx.CoreEventListener;

import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import java.util.jar.Manifest;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/** 
 * Main GUI entry point
 */ 
public class ChartAdjust
{
  public ChartAdjust()
  {
//  System.out.println("ClassLoader:" + this.getClass().getClassLoader().getClass().getName());
    // Cleanup from previous session if necessary
    WWGnlUtilities.deleteNow();
    // Find the compilation date...
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
    
    String lastModified = "";
    
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
      try { me = new URL(strURL); } catch (Exception ex) {}
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

    final JFrame frame = new AdjustFrame();
    
    boolean positioned = false;
    File propFile = new File("ww_position.properties");
    if (propFile.exists())
    {
      try
      {
        Properties props = new Properties();
        props.load(new FileReader(propFile));
        int w = Integer.parseInt(props.getProperty("frame.width"));
        int h = Integer.parseInt(props.getProperty("frame.height"));
        int x = Integer.parseInt(props.getProperty("frame.x.pos"));
        int y = Integer.parseInt(props.getProperty("frame.y.pos"));
        frame.setSize(w, h);
        frame.setLocation(x, y);
        positioned = true;
      }
      catch (Exception forgetit) { }
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
  }
  
  private static boolean proceed = false;
  
  public static void checkForUpdate()
  {
    // Checking for update
    proceed = ((Boolean)ParamPanel.data[ParamData.AUTO_UPDATES][1]).booleanValue();
    Thread checkForUpdate = // new CheckForUpdateThread("weather_assistant");
      new CheckForUpdateThread("weather_assistant", 
                               WWContext.getInstance().getParser(), 
                               WWGnlUtilities.STRUCTURE_FILE_NAME, 
                               proceed);
    // Add listener
    CoreContext.getInstance().addApplicationListener(new CoreEventListener()
     {
       public void updateCompleted(ArrayList<String> fList)
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
         Thread thread = new Thread()
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

  public static void main(String args[])
  {
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
    Thread ping = new Thread()
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
