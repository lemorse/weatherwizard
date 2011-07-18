package chartview.gui.toolbar.controlpanels.station;

import chartview.ctx.WWContext;

import chartview.util.WWGnlUtilities;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;

import java.awt.GradientPaint;
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
  
  public void paintComponent(Graphics g)
  {
    Point center = new Point(this.getWidth() / 2, this.getWidth() / 2);
    Graphics2D g2d = (Graphics2D)g;
    // Background
//  g2d.setColor(Color.black); 
    Color startColor = Color.black; // new Color(255, 255, 255);
    Color endColor   = Color.gray; // new Color(102, 102, 102);
    GradientPaint gradient = new GradientPaint(0, this.getHeight(), startColor, 0, 0, endColor); // vertical, upside down
    (g2d).setPaint(gradient);
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
    WWGnlUtilities.drawTWAOverBoat(g2d, 
                                   (this.getWidth() / 2) - 10, 
                                   center,
                                   windDir);
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
