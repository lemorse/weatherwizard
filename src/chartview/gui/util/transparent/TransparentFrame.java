package chartview.gui.util.transparent;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class TransparentFrame extends JFrame
{
  Robot robot;
  BufferedImage screenImg;
  Rectangle screenRect;
  TransparentPanel contentPanel = null;
  boolean userActivate = false;

  public TransparentFrame(TransparentPanel tp)
  {
    super();
    createScreenImage();
    contentPanel = tp;
    this.setContentPane(contentPanel);

    this.addComponentListener(new ComponentAdapter()
      {
        public void componentHidden(ComponentEvent e)
        {
        }

        public void componentMoved(ComponentEvent e)
        {
          resetUnderImg();
          repaint();
        }

        public void componentResized(ComponentEvent e)
        {
          resetUnderImg();
          repaint();
        }

        public void componentShown(ComponentEvent e)
        {
        }
      });

    this.addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
        }

        public void windowClosed(WindowEvent e)
        {
        }

        public void windowOpened(WindowEvent e)
        {
        }

        public void windowIconified(WindowEvent e)
        {
        }

        public void windowDeiconified(WindowEvent e)
        {
        }

        public void windowActivated(WindowEvent e)
        {
          if (userActivate)
          {
            userActivate = false;
            TransparentFrame.this.setVisible(false);
            createScreenImage();
            resetUnderImg();
            TransparentFrame.this.setVisible(true);
          }
          else
          {
            userActivate = true;
          }
        }

        public void windowDeactivated(WindowEvent e)
        {
        }
      });
  }

  protected void createScreenImage()
  {
    try
    {
      if (robot == null)
        robot = new Robot();
    }
    catch (AWTException ex)
    {
      ex.printStackTrace();
    }
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    screenRect = new Rectangle(0, 0, screenSize.width, screenSize.height);
    screenImg = robot.createScreenCapture(screenRect);
  }
  public void resetUnderImg()
  {
    if (robot != null && screenImg != null)
    {
      Rectangle frameRect = getBounds();
      int x = frameRect.x + 4;
      contentPanel.paintX = 0;
      contentPanel.paintY = 0;
      if (x < 0)
      {
        contentPanel.paintX = -x;
        x = 0;
      }
      int y = frameRect.y + 23;
      if (y < 0)
      {
        contentPanel.paintY = -y;
        y = 0;
      }
      int w = frameRect.width - 10;
      if (x + w > screenImg.getWidth())
        w = screenImg.getWidth() - x;
      int h = frameRect.height - 23 - 5;
      if (y + h > screenImg.getHeight())
        h = screenImg.getHeight() - y;
      contentPanel.underFrameImg = screenImg.getSubimage(x, y, w, h);
    }
  }
}

