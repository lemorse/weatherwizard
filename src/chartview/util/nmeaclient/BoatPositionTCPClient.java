package chartview.util.nmeaclient;

import chartview.gui.util.param.ParamData;
import chartview.gui.util.param.ParamPanel;

import chartview.util.WWGnlUtilities;

import nmea.server.datareader.CustomNMEAClient;

public class BoatPositionTCPClient implements BoatPositionClient
{
  WWGnlUtilities.BoatPosition bPos = null;
  private boolean ok = true;
  private Throwable problemCause = null;
  
  public BoatPositionTCPClient()
  {
    final String tcpPortNumber = (ParamPanel.data[ParamData.TCP_PORT][1]).toString();
    final WW_NMEAReader reader = new WW_NMEAReader(false, this, CustomNMEAClient.TCP_OPTION, tcpPortNumber,  "");     
//  reader.setParent(this);
    
    Thread t = new Thread("tcp-port-reader")
      {
        public void run()
        {
          while (bPos == null)
          {
            try { Thread.sleep(1000L); } 
            catch (Exception ex) 
            {
              System.err.println("Reading TCP port:" + tcpPortNumber + ":");
              ex.printStackTrace();
            }
          }
          try { reader.stopReader(); } catch (Exception ex) {}
        }
      };
    t.start();
  }
  
  public void setBoatPosition(WWGnlUtilities.BoatPosition bp)
  {
    bPos = bp;  
  }
  
  public WWGnlUtilities.BoatPosition getBoatPosition()
  {
    return bPos;
  }
  
  public void manageError(Throwable t) 
  {
    System.out.println("TCP Error!!!:" + t.toString());
    problemCause = t;
    ok = false;
  }
  
  public boolean allIsOk()
  {
    return ok;
  }
  public Throwable getProblemCause()
  {
    return problemCause;
  }
}
