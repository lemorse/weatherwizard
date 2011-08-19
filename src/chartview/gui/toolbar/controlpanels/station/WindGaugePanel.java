package chartview.gui.toolbar.controlpanels.station;

import java.awt.Color;
import java.awt.Dimension;

import java.awt.GradientPaint;
import java.awt.Graphics;

import java.awt.Graphics2D;

import javax.swing.JPanel;

public class WindGaugePanel
  extends JPanel
{
  float tws = 0f;
  
  public WindGaugePanel()
  {
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
    this.setLayout( null );
    this.setSize(new Dimension(30, 200));
  }
  
  final private static Color[] colorfield = new Color[]
  {
    Color.white,             // 0-5
    new Color(21, 200, 232), // Blue 5-10
    new Color(19, 234, 186), // Lighter blue 10-15
    new Color(48, 232, 21),  // Green 15-20
    new Color(211, 239, 14), // Yellow 20-25
    new Color(232, 180, 21), // Orange 25-30
    new Color(232, 100, 21), // Darker Orange 30-35
    new Color(180, 8, 0),    // Red 35-40
    new Color(147, 4, 0),    // Dark red 40-45
    new Color(148, 4, 161)   // Purple 45-
  };
  
  public static Color getWindColor(float w)
  {
    int i = (int)w;
    int colorIdx = (int)(i / 5d);
    if (colorIdx > colorfield.length - 1) colorIdx = colorfield.length - 1;
    return colorfield[colorIdx];
  }
  
  public void paintComponent(Graphics gr)
  {
    Graphics2D g2d = (Graphics2D)gr;
    // Gauge background 
//  g2d.setColor(Color.black); 
    Color startColor = Color.black; // new Color(255, 255, 255);
    Color endColor   = Color.gray; // new Color(102, 102, 102);
    GradientPaint gradient = new GradientPaint(0, this.getHeight(), startColor, 0, 0, endColor); // vertical, upside down
    (g2d).setPaint(gradient);
    int gaugeHeight = this.getHeight();
    g2d.fillRect(0, 
                 0, 
                 this.getWidth(), 
                 gaugeHeight);
    // Data
    final int MAX_RANGE = 60;
    final int STEP      = 5;
    
    int w = this.getWidth();
    int h = (int)((STEP - 2) * ((float)gaugeHeight / (float)MAX_RANGE));
    int i = 0;
    int y = 0;
    boolean last = false;
    boolean go   = true;
    while (tws > 0f && go)
    {
      int colorIdx = (int)(i / 5d);
      if (colorIdx > colorfield.length - 1) colorIdx = colorfield.length - 1;
      Color c = colorfield[colorIdx];
//      if (i > 5)
//        c = Color.yellow;
//      if (i > 25)
//        c = Color.orange;
//      if (i > 40)
//        c = Color.red;
//      if (i > 50)
//        c = new Color(125, 2, 15); // Dark red
      gr.setColor(c);
      gr.fillRoundRect(1,
                       gaugeHeight - y - STEP - 1,
                       w - 2, 
                       h,
                       2, 2);
      i += STEP;
      y += (h + 2);
      if (i > tws) 
      {        
        if (!last)
          last = true;
        else
          go = false;
        go = false;
      }
    }
  }

  public void setTws(float tws)
  {
    this.tws = tws;
  }

  public float getTws()
  {
    return tws;
  }
}