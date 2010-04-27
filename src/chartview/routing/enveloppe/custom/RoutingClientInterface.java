package chartview.routing.enveloppe.custom;

import java.util.ArrayList;

public interface RoutingClientInterface
{
  public void routingNotification(ArrayList<ArrayList<RoutingPoint>> all, RoutingPoint closest);
  
}
