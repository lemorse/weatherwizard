package chartview.gui.toolbar.controlpanels;

import chartview.ctx.ApplicationEventListener;
import chartview.ctx.WWContext;

import chartview.gui.util.param.ParamData;
import chartview.gui.util.param.ParamPanel;

import chartview.util.WWGnlUtilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;

public class ChartCommandPanelToolBar
  extends JToolBar
{
  private JButton zoomInButton  = new JButton();
  private JButton zoomOutButton = new JButton();
  private JButton reloadButton  = new JButton();

  private JPanel extraComponentHolder = new JPanel(new BorderLayout());
  private JPanel radioButtonHolder    = new JPanel(new FlowLayout());
  private JPanel expandCollapseHolder = new JPanel(new FlowLayout());
  
  private ButtonGroup buttonGroup       = new ButtonGroup();
  private JRadioButton ddRadioButton    = new JRadioButton();
  private JRadioButton grabRadioButton  = new JRadioButton();
  private JRadioButton crossRadioButton = new JRadioButton();
  private JRadioButton arrowRadioButton = new JRadioButton();
  
  private JButton expandCollapseControlButton = new JButton();
  private JButton scrollThruOpenTabsButton = new JButton();
  private boolean controlExpanded = false;

  public static final int DD_ZOOM           = 0;
  public static final int GRAB_SCROLL       = 1;
  public static final int CROSS_HAIR_CURSOR = 2;
  public static final int REGULAR_CURSOR    = 3; // Keep this one the last one

  private int grab = DD_ZOOM;

  private transient ApplicationEventListener ael = new ApplicationEventListener()
     {
       public String toString()
       {
         return "from ChartCommandPanelToolBar.";
       }
       public void toggleGrabScroll(int shape) 
       {
         grab = shape;
         switch (shape)
         {
           case DD_ZOOM:
             ddRadioButton.setSelected(true);
             break;
           case GRAB_SCROLL:
             grabRadioButton.setSelected(true);
             break;
           case CROSS_HAIR_CURSOR:  
             crossRadioButton.setSelected(true);
             break;
           case REGULAR_CURSOR:
             arrowRadioButton.setSelected(true);
             break;
           default:
             break;
         }
       }
       public void setOpenTabNum(int i) 
       {
         scrollThruOpenTabsButton.setEnabled(i > 1);
       }
     };
  
  public ChartCommandPanelToolBar()
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
    this.repaint();
  }

  public void removeListener()
  {
    WWContext.getInstance().removeApplicationListener(ael);  
  }
  
  private void jbInit()
    throws Exception
  {
    WWContext.getInstance().addApplicationListener(ael);
    zoomInButton.setIcon(new ImageIcon(this.getClass().getResource("img/zoomin.gif")));
    zoomInButton.setToolTipText(WWGnlUtilities.buildMessage("fax-zoom-in"));
    zoomInButton.setActionCommand("zoomIn");
    zoomInButton.setPreferredSize(new Dimension(24, 24));
    zoomInButton.setBorderPainted(false);
    zoomInButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          zoomInButton_actionPerformed(e);
        }
      });
    zoomOutButton.setIcon(new ImageIcon(this.getClass().getResource("img/zoomout.gif")));
    zoomOutButton.setToolTipText(WWGnlUtilities.buildMessage("fax-zoom-out"));
    zoomOutButton.setPreferredSize(new Dimension(24, 24));
    zoomOutButton.setBorderPainted(false);
    zoomOutButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          zoomOutButton_actionPerformed(e);
        }
      });
    reloadButton.setIcon(new ImageIcon(this.getClass().getResource("img/refresh.png")));
    reloadButton.setToolTipText(WWGnlUtilities.buildMessage("load-reload-defaut"));
    reloadButton.setPreferredSize(new Dimension(24, 24));
    reloadButton.setBorderPainted(false);
    final String compositeName = ((ParamPanel.DataFile) ParamPanel.data[ParamData.LOAD_COMPOSITE_STARTUP][1]).toString();
    reloadButton.setEnabled(compositeName.trim().length() > 0); // TODO Event, if that name is modified
    reloadButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          WWContext.getInstance().fireLoadDynamicComposite(compositeName);
        }
      });

    expandCollapseControlButton.setIcon(new ImageIcon(this.getClass().getResource("img/monitors.png")));
    expandCollapseControlButton.setToolTipText(WWGnlUtilities.buildMessage("collapse"));
    expandCollapseControlButton.setPreferredSize(new Dimension(24, 24));
    expandCollapseControlButton.setBorderPainted(false);
    expandCollapseControlButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          controlExpanded = !controlExpanded;
          if (controlExpanded)
            expandCollapseControlButton.setToolTipText(WWGnlUtilities.buildMessage("collapse"));
          else
            expandCollapseControlButton.setToolTipText(WWGnlUtilities.buildMessage("expand"));
          expandCollapseButton_actionPerformed(e);
        }
      });
    
    scrollThruOpenTabsButton.setIcon(new ImageIcon(this.getClass().getResource("img/clustering.png")));
    scrollThruOpenTabsButton.setToolTipText(WWGnlUtilities.buildMessage("scroll-thru-tabs"));
    scrollThruOpenTabsButton.setPreferredSize(new Dimension(24, 24));
    scrollThruOpenTabsButton.setBorderPainted(false);
    scrollThruOpenTabsButton.setEnabled(false);
    scrollThruOpenTabsButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          WWContext.getInstance().fireScrollThroughTabs();
        }
      });
      
    this.add(zoomInButton);
    this.add(zoomOutButton);
    this.add(reloadButton);
    
    this.add(extraComponentHolder);
    extraComponentHolder.add(radioButtonHolder, BorderLayout.WEST);
    expandCollapseHolder.add(scrollThruOpenTabsButton);
    expandCollapseHolder.add(expandCollapseControlButton);
    extraComponentHolder.add(expandCollapseHolder, BorderLayout.EAST);
    
    radioButtonHolder.add(ddRadioButton, null);
    radioButtonHolder.add(grabRadioButton, null);
    radioButtonHolder.add(crossRadioButton, null);
    radioButtonHolder.add(arrowRadioButton, null);

    buttonGroup.add(ddRadioButton);
    buttonGroup.add(grabRadioButton);
    buttonGroup.add(crossRadioButton);
    buttonGroup.add(arrowRadioButton);

    ddRadioButton.setSelected(true);
    grabRadioButton.setSelected(false);
    crossRadioButton.setSelected(false);
    arrowRadioButton.setSelected(false);

    ddRadioButton.setText("<html><img src='" + this.getClass().getResource("img/ddz.png").toString() + "'></html>");
    grabRadioButton.setText("<html><img src='" + this.getClass().getResource("img/grab.png").toString() + "'></html>");
    crossRadioButton.setText("<html><img src='" + this.getClass().getResource("img/ch.png").toString() + "'></html>");
    arrowRadioButton.setText("<html><img src='" + this.getClass().getResource("img/arrow.png").toString() + "'></html>");

    ddRadioButton.setToolTipText(WWGnlUtilities.buildMessage("set-to-dd"));
    grabRadioButton.setToolTipText(WWGnlUtilities.buildMessage("set-to-gs"));
    crossRadioButton.setToolTipText(WWGnlUtilities.buildMessage("set-to-ch"));
    arrowRadioButton.setToolTipText(WWGnlUtilities.buildMessage("set-to-rp"));

    ddRadioButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          newCursorSelected();
        }
      });
    grabRadioButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          newCursorSelected();
        }
      });
    crossRadioButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          newCursorSelected();
        }
      });
    arrowRadioButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          newCursorSelected();
        }
      });

    this.validate();
  }

  private void zoomInButton_actionPerformed(ActionEvent e)
  {
    //  System.out.println("ZoomIn requested");
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.allLayerZoomIn();
    }
  }

  private void zoomOutButton_actionPerformed(ActionEvent e)
  {
    //  System.out.println("ZoomOut requested");
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.allLayerZoomOut();
    }
  }
  
  private void expandCollapseButton_actionPerformed(ActionEvent e)
  {
    //  System.out.println("ZoomOut requested");
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.collapseExpandToolBar(controlExpanded);
    }
  }
    
  private void newCursorSelected()
  {
    if (ddRadioButton.isSelected())
      grab = DD_ZOOM;
    else if (grabRadioButton.isSelected())
      grab = GRAB_SCROLL;
    else if (crossRadioButton.isSelected())
      grab = CROSS_HAIR_CURSOR;
    else if (arrowRadioButton.isSelected())
      grab = REGULAR_CURSOR;

    WWContext.getInstance().fireSetCursor(grab);
  }
}
