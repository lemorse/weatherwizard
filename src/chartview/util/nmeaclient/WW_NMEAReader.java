package chartview.util.nmeaclient;

import astro.calc.GeoPoint;

import chartview.util.WWGnlUtilities;

import java.io.File;

// import gnu.io.NoSuchPortException;

import nmea.server.NMEAEventManager;
import nmea.server.datareader.CustomNMEAClient;

import ocss.nmea.api.NMEAEvent;
import ocss.nmea.parser.RMC;
import ocss.nmea.parser.StringParsers;

public class WW_NMEAReader
  implements NMEAEventManager
{
  private boolean verbose = false;
  private String serial = null;
  private int br = 0;
  private String tcp = "";
  private String udp = "";
  private int option = -1;
  private String hostName = "localhost"; 
  
  private WWGnlUtilities.BoatPosition bp = null;
  private BoatPositionClient parent = null;
  
  CustomNMEAClient nmeaClient = null;

  public WW_NMEAReader(boolean v,
                       BoatPositionClient parent,
                       String serial,
                       int br)
  {
    this.verbose = v;
    this.parent = parent;
    this.serial = serial;
    this.br = br;
    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public WW_NMEAReader(boolean v,
                       BoatPositionClient parent,
                       int option,
                       String port,
                       String hostName)
  {
    this.verbose = v;
    this.parent = parent;
    this.option = option;
    this.hostName = hostName;
    if (option == CustomNMEAClient.UDP_OPTION)      
      this.udp = port;
    if (option == CustomNMEAClient.TCP_OPTION)
      this.tcp = port;

    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  private void jbInit()
    throws Exception
  {
    try
    {
      if (tcp != null && tcp.trim().length() > 0)
      {
        int tcpport = Integer.parseInt(tcp);
        read(tcpport, hostName, CustomNMEAClient.TCP_OPTION); // TCP
      } 
      else if (udp != null && udp.trim().length() > 0)
      {
        int udpport = Integer.parseInt(udp);
        read(udpport, hostName, CustomNMEAClient.UDP_OPTION); // UDP
      } 
      else if (serial != null & serial.trim().length() > 0)
      {
        read(serial, br);                           // Serial port
      }
      else
      {
        System.out.println("Nothing to read, exiting.");
        System.exit(1);
      }
      
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private void read(String port, int br)
  {
    System.out.println("Reading Serial port...");
    long timeout = 5000L; // TODO Config Parameter
    nmeaClient = new CustomNMEAClient(this, port, br, timeout)
      {
        public void manageNMEAError(Throwable t)
        {
          System.err.println("Serial Port Error:" + t.toString());
//        if (t instanceof NoSuchPortException)
          {
            parent.manageError(t);
          }
        }
      };
  }

  private void read(int port, String host, int option)
  {
    if (option == CustomNMEAClient.TCP_OPTION)
    {
      System.out.println("Reading TCP...");
      nmeaClient = new CustomNMEAClient(this, CustomNMEAClient.TCP_OPTION, host, port)
      {
        public void manageNMEAError(Throwable t)
        {
          parent.manageError(t);
//        throw new RuntimeException(t);
        }
      };
    }
    else if (option == CustomNMEAClient.UDP_OPTION)
    {
      System.out.println("Reading UDP...");
      long timeout = 5000L; // TODO Config Parameter
      nmeaClient = new CustomNMEAClient(this, CustomNMEAClient.UDP_OPTION, host, port, timeout)
      {
        public void manageNMEAError(Throwable t)
        {
          parent.manageError(t);
//        throw new RuntimeException(t);
        }
      };
    }
    else
    {
      System.out.println("Unknown option [" + option + "]");
    }
  }

  private void read(File f)
  {
    System.out.println("Reading Data File...");
    nmeaClient = new CustomNMEAClient(this, f)
      {
        public void manageNMEAError(Throwable t)
        {
          throw new RuntimeException(t);
        }
      };
  }
  
  public boolean verbose()
  {
    return verbose;
  }

  public void manageDataEvent(String payload)
  {
    if (verbose)
      System.out.println("Read from NMEA :[" + payload + "]");
    
    String chainID = payload.substring(3, 6);
    if (chainID.equals("RMC")) // TODO More Strings here, like GLL...
    {
      if (bp == null)
        bp = new WWGnlUtilities.BoatPosition();
      RMC rmc = StringParsers.parseRMC(payload);
      bp.setHeading((int)Math.round(rmc.getCog()));
      bp.setPos(new GeoPoint(rmc.getGp().lat, rmc.getGp().lng));      
      
      try { stopReader(); }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
    try
    {
      if (parent != null && bp != null)
      {
//      System.out.println("Sending result back to top.");
        parent.setBoatPosition(bp);
      }
    }
    catch (Exception ex)
    {
      System.err.println(ex.toString());
    }
  }
  
  public void stopReader() throws Exception 
  {
    if ("true".equals(System.getProperty("verbose", "false")))
      System.out.println(this.getClass().getName() + ": Stop Reading requested.");
    nmeaClient.stopReading();
  }
  
  @Deprecated
  public void setParent(BoatPositionClient p)
  {
    this.parent = p;
  }
}
