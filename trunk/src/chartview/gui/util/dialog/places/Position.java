package chartview.gui.util.dialog.places;

public class Position 
{
  Latitude L;
  Longitude G;
  String gpsMnemo = "";
  
  public Position(Latitude L, Longitude G)
  {
    this(L, G, "");
  }
  public Position(Latitude L, Longitude G, String gps)
  {
    this.L = L;
    this.G = G;
    this.gpsMnemo = gps;
  }
  public Latitude getLat()
  { return L; }
  public Longitude getLong()
  { return G; }
  public String getGpsMnemo()
  { return gpsMnemo; }
}