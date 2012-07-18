package chartview.gui.toolbar.controlpanels;

import chart.components.ui.ChartPanel;

import chartview.ctx.ApplicationEventListener;

import chartview.ctx.WWContext;

import chartview.gui.AdjustFrame;
import chartview.gui.right.CompositeTabbedPane;

import chartview.util.ImageUtil;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.geom.AffineTransform;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class ThumbnailPanel
  extends JPanel
{
  private BorderLayout borderLayout = new BorderLayout();
  private JPanel instance = this;
  
  private double zoom = 1D;
  
  public ThumbnailPanel()
  {
    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      WWContext.getInstance().fireExceptionLogging(e);
      e.printStackTrace();
    }
  }

  
  private void jbInit()
    throws Exception
  {
    this.setLayout(borderLayout);
    this.setBackground(Color.white);

    this.setPreferredSize(new Dimension(ControlPane.WIDTH, 200));
    this.setMinimumSize(new Dimension(ControlPane.WIDTH, 200));
    this.setSize(new Dimension(ControlPane.WIDTH, 200));
    WWContext.getInstance().addApplicationListener(new ApplicationEventListener()
        {
          public String toString()
          {
            return "from ThumnailPanel.";
          }
          
          public void chartRepaint() 
          {
            try
            {
              displayImage();
            }
            catch (Exception ex)
            {
              ex.printStackTrace();
            }
          }
        });
  }
  
  public void paintComponent(Graphics gr)
  {
    Graphics2D g2d = (Graphics2D)gr;
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                         RenderingHints.VALUE_TEXT_ANTIALIAS_ON);      
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                         RenderingHints.VALUE_ANTIALIAS_ON);      
    //  ((AdjustFrame)WWContext.getInstance().getMasterTopFrame()).getCommandPanel().getChartPanelScrollPane().getViewportBorderBounds();
    ChartPanel cp = ((AdjustFrame)WWContext.getInstance().getMasterTopFrame()).getCommandPanel().getChartPanel();
    RenderedImage ri = cp.createChartImage(cp.getWidth(), cp.getHeight());
    //  System.out.println("ri is a " + ri.getClass().getName());
        
    final Image tnImg = (BufferedImage)ri;
    final int w = tnImg.getWidth(null);
    final int h = tnImg.getHeight(null);
    double wFact = w / instance.getSize().getWidth();
    double hFact = h / instance.getSize().getHeight();
    final double imgRatio = Math.max(wFact, hFact);
    final AffineTransform tx = new AffineTransform();
    double _tx = (instance.getSize().getWidth() - (w / imgRatio)) / 2d;
    double _ty = (instance.getSize().getHeight() - (h / imgRatio)) / 2d;
//  System.out.println("Translation:" + _tx + ", " + _ty);    
    tx.translate(_tx, _ty);
    tx.scale(1 / imgRatio, 
             1 / imgRatio);
    
//  Dimension dim = new Dimension((int)(w / imgRatio), (int)(h / imgRatio));
//  this.setPreferredSize(dim);
    g2d.drawImage(tnImg, tx, this);    
    // The actual view on the chart
    Point topLeft = ((AdjustFrame)WWContext.getInstance().getMasterTopFrame()).getCommandPanel().getChartPanel().getVisibleRect().getLocation();
    Rectangle rect = ((AdjustFrame)WWContext.getInstance().getMasterTopFrame()).getCommandPanel().getChartPanel().getVisibleRect();
//    g2d.setColor(Color.blue);
//    g2d.drawRect((int)(_tx + (topLeft.x / imgRatio)), 
//                 (int)(_ty + (topLeft.y / imgRatio)),
//                 (int)(rect.width / imgRatio), 
//                 (int)(rect.height / imgRatio));
    Shape big, small;    
    big   = new Rectangle2D.Double(0, 0, instance.getSize().getWidth(), instance.getSize().getHeight());
    small = new Rectangle2D.Double((_tx + (topLeft.x / imgRatio)), (_ty + (topLeft.y / imgRatio)), (rect.width / imgRatio), (rect.height / imgRatio));
    Area bigArea = new Area(big);
    Area smallArea = new Area(small);
    bigArea.exclusiveOr(smallArea);
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
    g2d.setPaint(Color.gray);
    g2d.fill(bigArea);
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
  }
  
  private final void displayImage() throws Exception
  {
    repaint();
  }
}
