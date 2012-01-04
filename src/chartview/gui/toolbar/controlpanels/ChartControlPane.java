package chartview.gui.toolbar.controlpanels;


import chartview.ctx.ApplicationEventListener;
import chartview.ctx.WWContext;

import chartview.routing.enveloppe.custom.RoutingPoint;
import chartview.routing.enveloppe.custom.RoutingUtil;

import chartview.util.WWGnlUtilities;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.util.ArrayList;

import java.util.List;

import javax.swing.JPanel;


public class ChartControlPane
  extends JPanel
{
  public final static int WIDTH = 220;
  
  JPanel componentHolder = new JPanel(new GridBagLayout());

  private MainZoomPanel mainZoomPanel = new MainZoomPanel();
  private SelectFaxPanel selectFaxPanel = new SelectFaxPanel();
  private ButtonCommandPanel imageCommandPanel = new ButtonCommandPanel()
    {
      public void fireUp()
      {
        imageUp();
      }

      public void fireDown()
      {
        imageDown();
      }

      public void fireLeft()
      {
        imageLeft();
      }

      public void fireRight()
      {
        imageRight();
      }

      public void fireZoomIn()
      {
        imageZoomin();
      }

      public void fireZoomOut()
      {
        imageZoomout();
      }
    };
  private ButtonCommandPanel chartCommandPanel = new ButtonCommandPanel()
    {
      public void fireUp()
      {
        chartUp();
      }

      public void fireDown()
      {
        chartDown();
      }

      public void fireLeft()
      {
        chartLeft();
      }

      public void fireRight()
      {
        chartRight();
      }

      public void fireZoomIn()
      {
        chartZoomin();
      }

      public void fireZoomOut()
      {
        chartZoomout();
      }
    };
  private ProjectionPanel projectionPanel = new ProjectionPanel();
  private RotationPanel rotationPanel     = new RotationPanel();
  private GribPanel gribPanel             = new GribPanel();
  private LoggingPanel loggingPanel       = new LoggingPanel();
  private FaxPreviewPanel faxPreviewPanel = new FaxPreviewPanel();
  private RoutingPanel routingPanel       = new RoutingPanel();  

  private final JPanel chartControlPanelHolder = new JPanel(new BorderLayout());
  private final JPanel faxControlPanelHolder = new JPanel(new BorderLayout());
  private final JPanel gribControlPanelHolder = new JPanel(new BorderLayout());
  private final JPanel projectionControlPanelHolder = new JPanel(new BorderLayout());
  private final JPanel routingControlPanelHolder = new JPanel(new BorderLayout());
  private final JPanel faxPreviewControlPanelHolder = new JPanel(new BorderLayout());
  private final JPanel logControlPanelHolder = new JPanel(new BorderLayout());
  
  private SingleControlPane faxControl = null;
  private SingleControlPane chartControl = null;
  private SingleControlPane gribControl = null;
  private SingleControlPane projectionControl = null;
  private SingleControlPane faxPreviewControl = null;
  private SingleControlPane routingPreviewControl = null;
  private SingleControlPane longControl = null;

  private transient ApplicationEventListener ael = new ApplicationEventListener()
        {
          public String toString()
          {
            return "from ChartControlPane.";
          }
          public void faxLoaded()
          {
            imageCommandPanel.setEnabled(true);
            selectFaxPanel.setEnabled(true);
            rotationPanel.setEnabled(true);
            faxControl.setEnabled(true);
          }

          public void faxUnloaded()
          {
            imageCommandPanel.setEnabled(false);
            selectFaxPanel.setEnabled(false);
            rotationPanel.setEnabled(false);
            faxControl.setEnabled(false);
          }

          public void gribLoaded() 
          {
            gribControl.setEnabled(true);
          }

          public void gribUnloaded()
          {
            gribControl.setEnabled(false);
            WWContext.getInstance().setGribFile(null);
          }

          public void routingAvailable(boolean b, List<RoutingPoint> bestRoute)
          {
//          System.out.println("Routing is " + (b?"":"not ") + "available");
            routingPreviewControl.setEnabled(b);
            routingPanel.setBestRoute(bestRoute, RoutingUtil.REAL_ROUTING);
          }
          
          public void routingForecastAvailable(boolean b, List<RoutingPoint> route)
          {
            routingPreviewControl.setEnabled(b);
            routingPanel.setBestRoute(route, RoutingUtil.WHAT_IF_ROUTING);
          }
        };
  
  public ChartControlPane()
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
    this.setLayout(new BorderLayout());
//  this.setSize(new Dimension(238, 300));
    componentHolder.add(mainZoomPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));    

    // Chart Controls
    JPanel intermediatePanel = new JPanel(new GridBagLayout());
    intermediatePanel.add(chartCommandPanel, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    
//  chartControlPanelHolder.add(chartCommandPanel, BorderLayout.CENTER);
    chartControlPanelHolder.add(intermediatePanel, BorderLayout.CENTER);
    chartControl = new SingleControlPane(WWGnlUtilities.buildMessage("chart-control"), chartControlPanelHolder, false);
    componentHolder.add(chartControl, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

    // Fax Controls
    JPanel intermediateFaxPanel = new JPanel();
    intermediateFaxPanel.setLayout(new BorderLayout());
    intermediateFaxPanel.add(selectFaxPanel, BorderLayout.NORTH);
    selectFaxPanel.setEnabled(false);
    imageCommandPanel.setSize(110, 80);
    JPanel imageCommandPanelHolder = new JPanel();
    imageCommandPanelHolder.setLayout(new GridBagLayout());
    imageCommandPanelHolder.add(imageCommandPanel, 
                                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    
    intermediateFaxPanel.add(imageCommandPanelHolder, BorderLayout.CENTER);
    faxControlPanelHolder.add(intermediateFaxPanel, BorderLayout.CENTER);
    imageCommandPanel.setEnabled(false);
    rotationPanel.setEnabled(false);
    faxControlPanelHolder.add(rotationPanel, BorderLayout.SOUTH);
    faxControl = new SingleControlPane(WWGnlUtilities.buildMessage("fax-control"), faxControlPanelHolder, false);
    componentHolder.add(faxControl, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    faxControl.setEnabled(false);

    // GRIB Controls
    gribControlPanelHolder.add(gribPanel, BorderLayout.CENTER);
    gribControl = new SingleControlPane(WWGnlUtilities.buildMessage("grib-control"), gribControlPanelHolder, false);
    componentHolder.add(gribControl, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    gribControl.setEnabled(false);

    // Projection Controls
    projectionControlPanelHolder.add(projectionPanel, BorderLayout.CENTER);
    projectionControl = new SingleControlPane(WWGnlUtilities.buildMessage("projection-control"), projectionControlPanelHolder, false);
    componentHolder.add(projectionControl, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

    // Fax Preview
    faxPreviewControlPanelHolder.add(faxPreviewPanel, BorderLayout.CENTER);
    faxPreviewControl = new SingleControlPane(WWGnlUtilities.buildMessage("fax-preview"), faxPreviewControlPanelHolder, false);
    componentHolder.add(faxPreviewControl, new GridBagConstraints(0, 5, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));        
    
    // Routing Controls
    routingControlPanelHolder.add(routingPanel, BorderLayout.CENTER);
    routingPreviewControl = new SingleControlPane(WWGnlUtilities.buildMessage("routing-details"), routingControlPanelHolder, false);
    componentHolder.add(routingPreviewControl, new GridBagConstraints(0, 6, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));    
    routingPreviewControl.setEnabled(false);

    // Logging Controls
    logControlPanelHolder.add(loggingPanel, BorderLayout.CENTER);
    longControl = new SingleControlPane(WWGnlUtilities.buildMessage("log-control"), logControlPanelHolder, false);
    componentHolder.add(longControl, new GridBagConstraints(0, 7, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));        
    // Done with the toolbar
    this.add(componentHolder, BorderLayout.NORTH);
    
    WWContext.getInstance().addApplicationListener(ael);
  }

  public void removeListener()
  {
    WWContext.getInstance().removeApplicationListener(ael);  
  }
  
  public MainZoomPanel getMainZoomPanel()
  {
    return mainZoomPanel;
  }

  private void imageUp()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); 
         i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.imageUp();
    }
  }

  private void imageDown()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); 
         i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.imageDown();
    }
  }

  private void imageLeft()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); 
         i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.imageLeft();
    }
  }

  private void imageRight()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); 
         i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.imageRight();
    }
  }

  private void imageZoomin()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); 
         i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.imageZoomin();
    }
  }

  private void imageZoomout()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); 
         i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.imageZoomout();
    }
  }

  private void chartUp()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); 
         i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.chartUp();
    }
  }

  private void chartDown()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); 
         i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.chartDown();
    }
  }

  private void chartLeft()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); 
         i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.chartLeft();
    }
  }

  private void chartRight()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); 
         i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.chartRight();
    }
  }

  private void chartZoomin()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); 
         i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.chartZoomin();
    }
  }

  private void chartZoomout()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); 
         i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.chartZoomout();
    }
  }

  public ProjectionPanel getProjectionPanel()
  {
    return projectionPanel;
  }
}
