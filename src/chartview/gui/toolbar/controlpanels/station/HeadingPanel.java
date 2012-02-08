package chartview.gui.toolbar.controlpanels.station;

import java.awt.Color;
import java.awt.Dimension;

import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;

import java.awt.Graphics2D;

import java.awt.RenderingHints;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class HeadingPanel
  extends JPanel
{
  private int hdg = 0;
  
  public HeadingPanel()
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
    this.setSize(new Dimension(190, 30));
  }
  
  public void paintComponent(Graphics gr)
  {    
    ((Graphics2D)gr).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);      
    ((Graphics2D)gr).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                      RenderingHints.VALUE_ANTIALIAS_ON);      
    int w = this.getWidth();
    int h = this.getHeight();
    final int FONT_SIZE = 12;
//  g2d.setColor(Color.black); 
    Color startColor = Color.black; // new Color(255, 255, 255);
    Color endColor   = Color.gray; // new Color(102, 102, 102);
    GradientPaint gradient = new GradientPaint(0, this.getHeight(), startColor, 0, 0, endColor); // vertical, upside down
    ((Graphics2D)gr).setPaint(gradient);
    gr.fillRect(0, 0, w, h);
    // Width: 30 on each side = 60
    gr.setColor(Color.white);
    float oneDegree = (float)w / 60f;
    // One graduation every 1 & 5, one label every 15
    for (int rose=hdg-30; rose<=hdg+30; rose++)
    {
      int roseToDisplay = rose;
      while (roseToDisplay >= 360) roseToDisplay -= 360;
      while (roseToDisplay < 0) roseToDisplay += 360;
      int abscisse = Math.round((float)(rose + 30 - hdg) * oneDegree);
//    System.out.println("(w=" + w + ") Abscisse for " + rose + "=" + abscisse);
      gr.drawLine(abscisse, 0, abscisse, 2);
      gr.drawLine(abscisse, h - 2, abscisse, h);
      if (rose % 5 == 0)
      {
        gr.drawLine(abscisse, 0, abscisse, 5);
        gr.drawLine(abscisse, h - 5, abscisse, h);
      }
      if (rose % 15 == 0)
      {
        Font f = gr.getFont();
        gr.setFont(new Font("Arial", Font.BOLD, FONT_SIZE));
        String roseStr = Integer.toString(Math.round(roseToDisplay));
        if (roseToDisplay == 0)
          roseStr = "N";
        else if (roseToDisplay == 180)
          roseStr = "S";    
        else if (roseToDisplay == 90)
          roseStr = "E";    
        else if (roseToDisplay == 270)
          roseStr = "W";    
        else if (roseToDisplay == 45)
          roseStr = "NE";    
        else if (roseToDisplay == 135)
          roseStr = "SE";    
        else if (roseToDisplay == 225)
          roseStr = "SW";    
        else if (roseToDisplay == 315)
          roseStr = "NW";    
//      System.out.println("String:" + roseStr);
        int strWidth  = gr.getFontMetrics(gr.getFont()).stringWidth(roseStr);
        gr.drawString(roseStr, abscisse - strWidth / 2, (h / 2) + (FONT_SIZE / 2) );
        gr.setFont(f);        
      }
    }    
    gr.setColor(Color.red);
    gr.drawLine(w/2, 0, w/2, h);

    this.setToolTipText(Integer.toString(hdg) + "\272");
  }

  public void setHdg(int hdg)
  {
    this.hdg = hdg;
  }

  public int getHdg()
  {
    return hdg;
  }
}
