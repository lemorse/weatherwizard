package chartview.gui.util.transparent;

import java.awt.Color;
import java.awt.Graphics;
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
