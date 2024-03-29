package chartview.util.http;

import chartview.util.WWGnlUtilities;
import chartview.ctx.WWContext;

import chartview.util.ImageUtil;
import chartview.util.gifutil.GIFInputStream;
import chartview.util.gifutil.GIFOutputStream;

import chartview.util.gifutil.Gif;

import coreutilities.Utilities;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;

import javax.swing.JOptionPane;

public class HTTPClient
{

  public HTTPClient()
  {
  }

  public static String getContent(String url)
    throws Exception
  {
    String ret = null;
    try
    {
      byte content[] = readURL(new URL(url));
      if (content == null)
      {
        throw new NMEAServerException("NMEA HTTP Server not found.");
      }
      ret = new String(content);
    }
    catch(Exception e)
    {
      throw e;
    }
    return ret;
  }

  public static byte[] readURL(URL url) throws Exception
  {
    byte content[] = null;
    try
    {
      URLConnection newURLConn = url.openConnection();
      InputStream is = newURLConn.getInputStream();
      boolean finished = false;
      int available = 0;
      byte aByte[] = new byte[2];
      int nBytes;
      long started = System.currentTimeMillis();
      int nbLoop = 1;
      while((nBytes = is.read(aByte, 0, 1)) != -1) 
      {
        content = Utilities.appendByte(content, aByte[0]);
        if (content.length > (nbLoop * 1000))
        {
          long now = System.currentTimeMillis();
          long delta = now - started;
          double rate = (double)content.length / ((double)delta / 1000D);
          System.out.println("Size: " + content.length + " byte(s), downloading at " + Math.round(rate) + " bytes per second..."); 
          WWContext.getInstance().fireSetStatus("Downloading at " + rate + " bytes per second..."); // LOCALIZE
          nbLoop++;
        }
      }
    }
    catch (SocketException se)
    {
      // Fall Through
    }
    catch(IOException e)
    {
      System.err.println("ReadURL for " + url.toString() + "\nnewURLConn failed :\n" + e);
      throw e;
    }
    catch(Exception e)
    {
      System.err.println("Exception for: " + url.toString());
    }
    return content;
  }

  public static Image getChart(File img)
  {
    Image image = null;
    try
    {
      image = ImageIO.read(img.toURI().toURL());
    }
    catch(Exception e)
    {
      WWContext.getInstance().fireExceptionLogging(e);
      e.printStackTrace();
    }
    return image;
  }

  private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_z");

  public static Image getChart(String urlString, String dir, boolean verbose) throws Exception
  {
    return getChart(urlString, dir, null, verbose);
  }
  
  public static Image getChart(String urlString, String dir, String fileName, boolean verbose) throws Exception
  {
    String retFile = "";
    /**
     * If there is a ';' in the url, url is before, 
     * after, this is the output directory.
     */
    String outputdir = dir;
    String urlStr = urlString;
    if (urlStr.indexOf(";") > -1)
    {
      urlStr = urlStr.substring(0, urlStr.indexOf(";"));
      outputdir += (File.separator + urlString.substring(urlString.indexOf(";") + 1));
    }
    Image image = null;
    Gif gifImage = null;
    try
    {
      long before = System.currentTimeMillis();
      if (verbose) System.out.println("...reading (1) " + urlStr);
      String fName = fileName;
      if (fName == null) 
        fName = outputdir + File.separator + "Weather_" + sdf.format(new Date()) + ".jpg";
      try
      {
        URL chartUrl = new URL(urlStr);
        if (fName.endsWith(".gif"))
        {
          URLConnection urlConn = chartUrl.openConnection();
          gifImage = new Gif();
          gifImage.init(new GIFInputStream(urlConn.getInputStream()));
        }
        else
        {
          boolean tif = urlStr.toUpperCase().endsWith(".TIFF") || urlStr.toUpperCase().endsWith(".TIF");
          if (tif)
          {
            InputStream is = chartUrl.openStream();
            image = ImageUtil.readImage(is, tif);     
          }
          else
            image = ImageIO.read(chartUrl);
        }
      }
      catch (Exception e)
      {
        WWContext.getInstance().fireStopAnyLoading();
        WWContext.getInstance().fireInterruptProcess();
        if (fileName != null) // assume interactif
          JOptionPane.showMessageDialog(WWContext.getInstance().getMasterTopFrame(), 
                                        e.toString(), WWGnlUtilities.buildMessage("downloading-fax"),
                                        JOptionPane.ERROR_MESSAGE);
//      System.out.println("Exception from getChart..., rethrowing.");
        throw e;
      }
      File f = new File(fName);
      if (new File(outputdir).canWrite())
      {
        if (fName.endsWith(".jpg"))
          ImageIO.write((RenderedImage)image, "jpg", f);
        else if (fName.endsWith(".gif"))
          gifImage.write(new GIFOutputStream(new FileOutputStream(f)));
        else if (fName.endsWith(".png"))
          ImageIO.write((RenderedImage)image, "png", f);
        else
          System.out.println("Extension not supported (" + fName + ")");
        long diff = System.currentTimeMillis() - before;
        retFile = f.getAbsolutePath();
        if (verbose) System.out.println("New Fax available " + retFile + " [" + Long.toString(diff) + " ms]");
      }
      else
        throw new CannotWriteException("Cannot write in " + outputdir);
    }
    catch (Exception e)
    {
      WWContext.getInstance().fireExceptionLogging(e);
      throw e;
    }
    return image;
  }

  public static byte[] getGRIB(String urlString, String dir, String fileName, boolean verbose) throws Exception
  {
    byte[] content = null;
    String retFile = "";
    String outputdir = dir;
    String urlStr = urlString;
    if (urlStr.indexOf(";") > -1)
    {
      urlStr = urlStr.substring(0, urlStr.indexOf(";"));
      outputdir += (File.separator + urlString.substring(urlString.indexOf(";") + 1));
    }
    try
    {
      long before = System.currentTimeMillis();
      if (verbose) System.out.println("...reading (2) " + urlStr);
      String fName = fileName;
      if (fName == null) 
        fName = outputdir + File.separator + "GRIB" + sdf.format(new Date()) + ".grb";
      try
      {
//      System.out.println(request);
        URL saildocs = new URL(urlString);
        URLConnection connection = saildocs.openConnection();
        connection.connect();
  //    DataInputStream dis = new DataInputStream(connection.getInputStream());
        InputStream dis = connection.getInputStream();
         
        long waiting = 0L;
        while (dis.available() == 0 && waiting < 30L) // 30s Timeout...
        {
          Thread.sleep(1000L);
          waiting += 1L;
        }

        final int BUFFER_SIZE = 65536;
        byte aByte[] = new byte[BUFFER_SIZE];
        int nBytes;
        while((nBytes = dis.read(aByte, 0, BUFFER_SIZE)) != -1) 
        {
//        System.out.println("Read " + nBytes + " more bytes.");
          content = Utilities.appendByteArrays(content, aByte, nBytes);
        }
        System.out.println("Read " + content.length + " bytes.");
        WWContext.getInstance().fireSetStatus("Read " + content.length + " bytes of GRIB data.");
        dis.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(content);                    
        dis = bais; // switch
      }
      catch(Exception e)
      {
        if (fileName != null) // assume interactif
          JOptionPane.showMessageDialog(WWContext.getInstance().getMasterTopFrame(), e.toString(), "Downloading GRIB", JOptionPane.ERROR_MESSAGE);
        WWContext.getInstance().fireStopAnyLoading();
        WWContext.getInstance().fireExceptionLogging(e);
        e.printStackTrace();
      }
      File f = new File(fName); 
      if (new File(outputdir).canWrite())
      {
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(content);
        fos.close();
        long diff = System.currentTimeMillis() - before;
        retFile = f.getAbsolutePath();
        if (verbose) System.out.println("New GRIB available " + retFile + " [" + Long.toString(diff) + " ms]");
      }
      else
        throw new CannotWriteException("Nannot write in " + outputdir);
    }
    catch(Exception e)
    {
      WWContext.getInstance().fireExceptionLogging(e);
//    e.printStackTrace();
      throw e;
    }
    return content;
  }
  
  @SuppressWarnings("serial")
  public static class NMEAServerException extends Exception
  {
    public NMEAServerException(Throwable cause)
    {
      super(cause);
    }

    public NMEAServerException(String message, Throwable cause)
    {
      super(message, cause);
    }

    public NMEAServerException(String message)
    {
      super(message);
    }

    public NMEAServerException()
    {
      super();
    }
  }
  
  public static class CannotWriteException extends IOException
  {
    public CannotWriteException(Throwable cause)
    {
      super(cause);
    }

    public CannotWriteException(String message, Throwable cause)
    {
      super(message, cause);
    }

    public CannotWriteException(String message)
    {
      super(message);
    }

    public CannotWriteException()
    {
      super();
    }
  }
}
