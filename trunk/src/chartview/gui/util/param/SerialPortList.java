package chartview.gui.util.param;


import java.util.ArrayList;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;

public class SerialPortList
{
  public static String[] listSerialPorts()
  {
    ArrayList<String> portList = new ArrayList<String>();                                  
    CommPortIdentifier portId;
    Enumeration en = CommPortIdentifier.getPortIdentifiers();
    while (en.hasMoreElements())
    {
      portId = (CommPortIdentifier) en.nextElement();
      if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
      {
        portList.add(portId.getName());
      }
    }
    String[] sa = portList.toArray(new String[portList.size()]);
    if (sa.length == 0) // Just in case...
    {
      sa = new String[] { "COM1", "COM2", "COM3" };
    }
    return sa;
  }
}
