package chartview.gui.util.transparent;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class TransparentPanel extends JPanel
{
  Color bgColor = null;
  
  BufferedImage underFrameImg;
  int paintX = 0;
  int paintY = 0;

  public TransparentPanel()
  {
    super();
    setOpaque(true);
  }

  public void paint(Graphics g)
  {
    super.paint(g);
  }

  protected void paintComponent(Graphics g)
  {
    ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                     RenderingHints.VALUE_TEXT_ANTIALIAS_ON);      
    ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                     RenderingHints.VALUE_ANTIALIAS_ON);      
    super.paintComponent(g);
    g.drawImage(underFrameImg, paintX, paintY, null);
    if (bgColor != null)
    {
      g.setColor(bgColor);
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }
  }

  public void setBgColor(Color bgColor)
  {
    this.bgColor = bgColor;
  }

  public Color getBgColor()
  {
    return bgColor;
  }
}
