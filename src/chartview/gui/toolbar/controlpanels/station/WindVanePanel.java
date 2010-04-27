package chartview.gui.toolbar.controlpanels.station;

import chartview.ctx.WWContext;

import chartview.util.WWGnlUtilities;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;

import java.awt.Graphics;

import java.awt.Graphics2D;

import java.awt.Point;

import java.awt.Stroke;

import javax.swing.JPanel;

public class WindVanePanel
  extends JPanel
{
  private int windDir = 0;
  
  public WindVanePanel()
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
    this.setSize(new Dimension(224, 224));
  }
  
  private final static int KNOB_DIAMETER = 10; // Make it even
  
  public void paintComponent(Graphics g)
  {
    Point center = new Point(this.getWidth() / 2, this.getWidth() / 2);
    Graphics2D g2d = (Graphics2D)g;
    // Background
    g2d.setColor(Color.black);
//  g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
    g2d.fillOval(0, 0, this.getWidth(), this.getHeight());
    // Boat
    g2d.setColor(Color.white);
    int boatLength = this.getHeight() - 30;
    WWGnlUtilities.drawBoat(g2d, 
                           Color.white, 
                           center, 
                           boatLength, 
                           0,
                           1.0f);
    
    // Hand shadow
    g2d.setColor(Color.gray);
    float alpha = 0.3f;
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    Stroke originalStroke = g2d.getStroke();
    Stroke stroke =  new BasicStroke(5, 
                                     BasicStroke.CAP_ROUND,
                                     BasicStroke.JOIN_BEVEL);
    g2d.setStroke(stroke);  
    int handLength = (this.getWidth() / 2) - 10;
    int shadowOffset = 5;
    g2d.drawLine(center.x + shadowOffset, 
                 center.y + shadowOffset, 
                 center.x + shadowOffset + (int)(handLength * Math.sin(Math.toRadians((double)windDir))),
                 center.y + shadowOffset - (int)(handLength * Math.cos(Math.toRadians((double)windDir))));
    g2d.fillOval(center.x + shadowOffset - (KNOB_DIAMETER / 2),
                 center.y + shadowOffset - (KNOB_DIAMETER / 2),
                 KNOB_DIAMETER, KNOB_DIAMETER);                 
    // Reset Transparency
    alpha = 1.0f;
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));      
    // Hand
    g2d.drawLine(center.x, 
                 center.y, 
                 center.x + (int)(handLength * Math.sin(Math.toRadians((double)windDir))),
                 center.y - (int)(handLength * Math.cos(Math.toRadians((double)windDir))));
    g2d.fillOval(center.x - (KNOB_DIAMETER / 2),
                 center.y - (KNOB_DIAMETER / 2),
                 KNOB_DIAMETER, KNOB_DIAMETER);                 
    
    g2d.setStroke(originalStroke);  
  }

  public void setWindDir(int windDir)
  {
    this.windDir = windDir;
  }

  public int getWindDir()
  {
    return windDir;
  }
}
