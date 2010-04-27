package chartview.gui;


import astro.calc.GeoPoint;

import chartview.ctx.ApplicationEventListener;
import chartview.ctx.WWContext;

import chartview.gui.left.FileTypeHolder;
import chartview.gui.right.CommandPanel;
import chartview.gui.right.Panel3D;
import chartview.gui.toolbar.controlpanels.ChartCommandPanelToolBar;
import chartview.gui.toolbar.controlpanels.ChartControlPane;
import chartview.gui.toolbar.controlpanels.LoggingPanel;
import chartview.gui.util.dialog.AutoDownloadTablePanel;
import chartview.gui.util.dialog.ContactPanel;
import chartview.gui.util.dialog.FaxType;
import chartview.gui.util.dialog.InternetFax;
import chartview.gui.util.dialog.InternetGRIB;
import chartview.gui.util.dialog.LoadAtStartupPanel;
import chartview.gui.util.dialog.PositionInputPanel;
import chartview.gui.util.dialog.PredefZonesTablePanel;
import chartview.gui.util.dialog.WeatherDataInputPanel;
import chartview.gui.util.param.CategoryPanel;
import chartview.gui.util.param.ParamData;
import chartview.gui.util.param.ParamPanel;

import chartview.util.WWGnlUtilities;
import chartview.util.grib.GribHelper;
import chartview.util.http.HTTPClient;

import coreutilities.Utilities;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import main.ChartAdjust;

import main.help.AboutBox;

import oracle.xml.parser.v2.DOMParser;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;
import oracle.xml.parser.v2.XMLParser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import user.util.TimeUtil;


public class AdjustFrame
  extends JFrame
{
  private static final String FRAME_BASE_TITLE = WWGnlUtilities.buildMessage("product-name");
  private static final SimpleDateFormat sdfForTitle = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
  
  private BorderLayout borderLayout;
  private JTabbedPane tabbedPane = new JTabbedPane();
  private CommandPanel commandPanel;
  private JPanel commandPanelHolder;
  private Panel3D threeDGRIBPanel;
  
  private FileTypeHolder allJTrees = new FileTypeHolder();

  private JPanel statusPanel = new JPanel();
  private JProgressBar progressBar = new JProgressBar(0, 100);
  private JLabel statusLabel = new JLabel("...");

  private JMenu menuFile = new JMenu();
//private JMenuItem menuFileOpen = new JMenuItem();
  private JMenu menuDownload = new JMenu();
  private JMenuItem menuDownloadFaxFromNet = new JMenuItem();
  private JMenuItem menuDownloadGRIBFromNet = new JMenuItem();
  private JMenuItem menuSetupAutoDownload = new JMenuItem();
  private JMenuItem menuStartAutoDownload = new JMenuItem();
  
  private JMenuItem menuFilePrint = new JMenuItem();
  private JMenuItem menuGenImage = new JMenuItem();

  private JMenuItem menuGoogle      = new JMenu();
  private JMenuItem menuGoogleMap   = new JMenuItem();
  private JMenuItem menuGoogleEarth = new JMenuItem();
  
//private JMenuItem menuFileExit = new JMenuItem();
//private JMenuItem menuFileStore = new JMenuItem();
  private JMenuItem menuFileRestore = new JMenuItem();
  private JMenuItem menuFileRestoreFromURL = new JMenuItem();

  private JMenuItem menuFileCreatePattern = new JMenuItem();
  private JMenuItem menuFileLoadFromPattern = new JMenuItem();

  private JMenu menuTools = new JMenu();
  private JCheckBoxMenuItem showGRIBPointData = new JCheckBoxMenuItem();
  private JMenu menuCharts = new JMenu();
  private JMenuItem managePredefinedZones = new JMenuItem();

  private JMenu menuRouting = new JMenu();
  private JMenuItem menuToolsRouting = new JMenuItem();
  private JMenuItem whatIfMenuItem = new JMenuItem();
  
  private JMenuItem menuToolsGeostrophicWind = new JMenuItem();
  private JMenuItem menuToolsRetryNetwork = new JMenuItem();
  private JMenuItem menuToolsPlaces = new JMenuItem();
  private JMenuItem menuToolsPreferences = new JMenuItem();
  
  private JMenu menuAdmin = new JMenu();
  private JMenuItem menuAdminGenImages = new JMenuItem();
  private JMenuItem menuDetectUnusedFiles = new JMenuItem();
  private JMenuItem menuCleanOldBackups = new JMenuItem();
  private JMenuItem menuManageUE = new JMenuItem();
  
  private JMenu nmeaMenu = new JMenu();
  private JCheckBoxMenuItem menuStartNMEA = new JCheckBoxMenuItem();
  private JMenuItem manualNMEA = new JMenuItem();

  private JMenu menuHelp = new JMenu();
  private JMenuItem menuHelpAbout = new JMenuItem();
//private JMenuItem menuHelpContent = new JMenuItem();
  private JMenuItem menuHelpContact = new JMenuItem();
  private JMenuItem menuCheckForUpdate = new JMenuItem();
  private JMenuBar menuBar = new JMenuBar();

  private JSplitPane jSplitPane = null;
  private ChartCommandPanelToolBar commandPanelToolbar = new ChartCommandPanelToolBar();
  
  private WeatherDataInputPanel inputPanel = null;
  private PositionInputPanel pip = null;

  protected AdjustFrame instance = this;
  
  private JPanel chartPanelControlPaneHolder = new JPanel(new BorderLayout()); 
  private JScrollPane controlPaneScrollPane = new JScrollPane();
  private ChartControlPane ccp = new ChartControlPane();

  public AdjustFrame()
  {
    borderLayout = new BorderLayout();
//  commandPanel = new CommandPanel(this.mainZoomPanel);
    commandPanel = new CommandPanel(ccp.getMainZoomPanel());
    commandPanelHolder = new JPanel(new BorderLayout());
    commandPanelHolder.add(commandPanel, BorderLayout.CENTER);    
    commandPanelHolder.add(commandPanelToolbar, BorderLayout.NORTH);        
    chartPanelControlPaneHolder.add(controlPaneScrollPane, BorderLayout.CENTER);
    controlPaneScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    controlPaneScrollPane.getViewport().add(ccp, null);
    
    commandPanelHolder.add(chartPanelControlPaneHolder, BorderLayout.EAST);    
    chartPanelControlPaneHolder.setVisible(false);
    threeDGRIBPanel = new Panel3D();

    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      WWContext.getInstance().fireExceptionLogging(e);
      e.printStackTrace();
    }
//    synchronized (toolBar)
//    {
//      WWContext.getInstance().fireToolbarElementResized();        
//    }
    // Any composite to load at startup?
    final String compositeName = ((ParamPanel.DataFile) ParamPanel.data[ParamData.LOAD_COMPOSITE_STARTUP][1]).toString();
    if (compositeName.trim().length() > 0)
    {
      askAndWaitForLoadAtStartup(compositeName, 30); //  TODO, the 30 as a preference
    }
  }

  private void askAndWaitForLoadAtStartup(final String compositeName, int timeout)
  {
    final LoadAtStartupPanel specialPanel = new LoadAtStartupPanel(compositeName);
    final WWGnlUtilities.SpecialBool imDone = new WWGnlUtilities.SpecialBool(false);
    
    final Thread me = Thread.currentThread();  
    Thread thread = new Thread()
      {
        public void run()
        {
          try
          {
            int resp = JOptionPane.showConfirmDialog(instance, 
                                                     specialPanel, WWGnlUtilities.buildMessage("load-composite-at-startup"), 
                                                     JOptionPane.YES_NO_OPTION, 
                                                     JOptionPane.QUESTION_MESSAGE);
            imDone.setValue(true);
            synchronized (me) { me.notify(); }
            if (resp == JOptionPane.YES_OPTION)
            {
              WWContext.getInstance().fireLoadDynamicComposite(compositeName);
            }
          }
          catch (Exception e)
          {
            e.printStackTrace();
          }
        }
        
        public void interrupt()
        {
          super.interrupt();
//        System.out.println("Wait is canceled.");
        }
      };
    thread.start();
    
    synchronized (me)
    {
      try
      {
        for (int i=0; i<timeout && !imDone.valueOf(); i++)
        {
          specialPanel.setLineFiveMessage(WWGnlUtilities.buildMessage("you-said-5", new String[] { Integer.toString(timeout - i) }));
          me.wait(1000L);
        }
        if (thread.isAlive())
        {
          thread.interrupt();
//        System.out.println("Wait is over.");
        }
//      System.out.println("Back in main thread.");
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }    
  }
  
  private void setLoadingProgresssBar(boolean b)
  {
    setLoadingProgresssBar(b, WWGnlUtilities.buildMessage("loading"));
  }
  
  private int nbLoad = 0;
  OscillateThread oscillate = null;
  
  private void setLoadingProgresssBar(boolean b, final String s)
  {
    if (b) nbLoad += 1;
    if (!b) nbLoad -= 1;
    
    final boolean x = (nbLoad > 0);
    
    oscillate = new OscillateThread(s, x);
    oscillate.start();
  }

  public void stopAnyOscillatingThread()
  {
    if (oscillate != null)
      oscillate.abort();
  }
  
  private void jbInit() throws Exception
  {
    WWContext.getInstance().setMasterTopFrame(this);
    
    this.setIconImage(new ImageIcon(this.getClass().getResource("img/paperboat.png")).getImage());
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    WWContext.getInstance().addApplicationListener(new ApplicationEventListener()
        {
          public void collapseExpandToolBar(boolean b) 
          {
            chartPanelControlPaneHolder.setVisible(b);             
          }
          
          public void setCompositeRequested()
          {
            setupComposite();             
          }
          
          public void gribUnloaded()
          {
            if (tabbedPane.getSelectedIndex() == 1)
              tabbedPane.setSelectedIndex(0);
            tabbedPane.setEnabledAt(1, false);
            tabbedPane.setSelectedIndex(0);
            WWContext.getInstance().setGribFile(null);
          }

          public void enable3DTab(boolean b)
          {
            tabbedPane.setEnabledAt(1, b);
          }
          
          public void gribFileOpen(String str)
          {
            setupComposite(null, str);
          }

          public void faxFileOpen(String str)
          {
            setupComposite(str, null);
          }

          public void setLoading(boolean b)
          {
//          System.out.println("Set loading... " + b);
            setLoadingProgresssBar(b);
          }

          public void setLoading(boolean b, String s)
          {
//          System.out.println("Set loading... " + b);
            setLoadingProgresssBar(b, s);
          }
          
          public void stopAnyLoadingProgressBar()
          {
            stopAnyOscillatingThread();  
          }
          
          public void setStatus(String s)
          {
            setStatusLabel(s);
          }
          
          public void networkOK(boolean b)
          {
//          System.out.println("Network access:" + (b?"":" not") + " OK");
            menuToolsRetryNetwork.setEnabled(!b);
          }

          public void gribForward()
          {            
            threeDGRIBPanel.getThreeDPanel().setPanelLabel(commandPanel.getGribData()[commandPanel.getGribIndex()].getDate().toString());
          }
          
          public void gribBackward() 
          {
            threeDGRIBPanel.getThreeDPanel().setPanelLabel(commandPanel.getGribData()[commandPanel.getGribIndex()].getDate().toString());
          }
          
          public void gribDataPanelClosed() 
          {
            showGRIBPointData.setSelected(false);
          }
        });
    this.setJMenuBar(menuBar);
//  menuFile.add(menuFileOpen);
    menuFile.add(new OpenAction()).setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
    menuFile.add(new JSeparator());
    menuFile.add(menuDownload);
    menuDownload.add(menuDownloadFaxFromNet);    
    menuDownload.add(menuDownloadGRIBFromNet);
    menuDownload.add(menuSetupAutoDownload);
    menuDownload.add(menuStartAutoDownload);
    
    menuFile.add(new JSeparator());
    menuFile.add(menuFilePrint);
    menuFile.add(menuGenImage);
    menuFile.add(new JSeparator());
    menuFile.add(menuGoogle);
    menuGoogle.add(menuGoogleMap);
    menuGoogle.add(menuGoogleEarth);
    menuFile.add(new JSeparator());
//  menuFile.add(menuFileStore);
    menuFile.add(new SaveCompositeAction()).setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
    menuFile.add(menuFileRestore);
    menuFile.add(menuFileRestoreFromURL);
    menuFile.add(new JSeparator());
    menuFile.add(menuFileCreatePattern);
    menuFile.add(menuFileLoadFromPattern);
    menuFile.add(new JSeparator());
//  menuFile.add(menuFileExit);
    menuFile.add(new ExitAction()).setAccelerator(KeyStroke.getKeyStroke("alt F4"));
    
    menuFile.setMnemonic('F'); // TODO Same for everyone
    
    menuBar.add(menuFile);
    menuFile.setText(WWGnlUtilities.buildMessage("file"));
//    menuFileOpen.setText(WWGnlUtilities.buildMessage("set-composite"));
//    menuFileOpen.setToolTipText(WWGnlUtilities.buildMessage("fax-grib-charts"));
//    menuFileOpen.setIcon(new ImageIcon(this.getClass().getResource("img/composite.png")));
//    menuFileOpen.addActionListener(new ActionListener()
//        {
//          public void actionPerformed(ActionEvent ae)
//          {
//            fileOpen_ActionPerformed(ae);
//          }
//        });
    menuDownload.setText(WWGnlUtilities.buildMessage("download"));
    menuDownload.setIcon(new ImageIcon(this.getClass().getResource("img/download.png")));
    menuDownloadFaxFromNet.setText(WWGnlUtilities.buildMessage("download-fax-from-internet"));
    menuDownloadFaxFromNet.setToolTipText(WWGnlUtilities.buildMessage("from-predefined"));
    menuDownloadFaxFromNet.setIcon(new ImageIcon(this.getClass().getResource("img/script.png")));
    menuDownloadFaxFromNet.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            downloadFaxFromInternet();
          }
        });
    menuDownloadGRIBFromNet.setText(WWGnlUtilities.buildMessage("download-grib-from-net"));
    menuDownloadGRIBFromNet.setToolTipText(WWGnlUtilities.buildMessage("from-predefined"));
    menuDownloadGRIBFromNet.setIcon(new ImageIcon(this.getClass().getResource("img/grib.png")));
    menuDownloadGRIBFromNet.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            downloadGRIBFromInternet();
          }
        });
    
    menuSetupAutoDownload.setText(WWGnlUtilities.buildMessage("setup-auto-download"));
    menuSetupAutoDownload.setIcon(new ImageIcon(this.getClass().getResource("img/filter.png")));
    menuSetupAutoDownload.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            setupDownload();
          }
        });
    menuStartAutoDownload.setText(WWGnlUtilities.buildMessage("start-auto-download"));
    menuStartAutoDownload.setIcon(new ImageIcon(this.getClass().getResource("img/controller.png")));
    menuStartAutoDownload.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            startAutoDownload();
          }
        });
//  menuSetupAutoDownload.setEnabled(false);
//  menuStartAutoDownload.setEnabled(false);
        
    menuFilePrint.setText(WWGnlUtilities.buildMessage("print"));
    menuFilePrint.setIcon(new ImageIcon(this.getClass().getResource("img/print.png")));
    menuFilePrint.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            filePrint_ActionPerformed(ae);
          }
        });
    menuGenImage.setText(WWGnlUtilities.buildMessage("generate-image"));
    menuGenImage.setIcon(new ImageIcon(this.getClass().getResource("img/snapshot.png")));
    menuGenImage.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            genImage_actionPerformed(ae);
          }
        });
//  menuFileStore.setText(GnlUtilities.buildMessage("save-composite"));
//  menuFileStore.setIcon(new ImageIcon(this.getClass().getResource("img/save.png")));
//  menuFileStore.addActionListener(new ActionListener()
//      {
//        public void actionPerformed(ActionEvent ae)
//        {
//          store();
//        }
//      });

    menuGoogle.setText(WWGnlUtilities.buildMessage("view-google"));
    menuGoogle.setIcon(new ImageIcon(this.getClass().getResource("img/google.png")));

    menuGoogleMap.setText(WWGnlUtilities.buildMessage("view-google-map"));
    menuGoogleMap.setIcon(new ImageIcon(this.getClass().getResource("img/google.png")));
    menuGoogleMap.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            gMap();
          }
        });
//  menuGoogleMap.setEnabled(false);
    menuGoogleEarth.setText(WWGnlUtilities.buildMessage("view-google-earth"));
    menuGoogleEarth.setIcon(new ImageIcon(this.getClass().getResource("img/ge.png")));
    menuGoogleEarth.addActionListener(new ActionListener()
       {
         public void actionPerformed(ActionEvent ae)
         {
           gEarth();
         }
       });
//  menuGoogleEarth.setEnabled(false);
        
    menuFileRestore.setText(WWGnlUtilities.buildMessage("load-composite"));
    menuFileRestore.setIcon(new ImageIcon(this.getClass().getResource("img/open.png")));
    menuFileRestore.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            restore();
          }
        });
    menuFileRestoreFromURL.setText(WWGnlUtilities.buildMessage("load-composite-from-url"));
    menuFileRestoreFromURL.setIcon(new ImageIcon(this.getClass().getResource("img/open.png")));
    menuFileRestoreFromURL.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            downloadFromTheWebAndDisplayComposite(null);
          }
        });
        
    menuFileCreatePattern.setText(WWGnlUtilities.buildMessage("create-pattern"));
    menuFileCreatePattern.setIcon(new ImageIcon(this.getClass().getResource("img/pattern.png")));
    menuFileCreatePattern.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            generatePattern();
          }
        });
    menuFileLoadFromPattern.setText(WWGnlUtilities.buildMessage("load-with-pattern"));
    menuFileLoadFromPattern.setIcon(new ImageIcon(this.getClass().getResource("img/pattern.png")));
    menuFileLoadFromPattern.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            loadWithPattern();
          }
        });
//    menuFileExit.setText("Exit");
//    menuFileExit.setToolTipText("Quit");
//    menuFileExit.addActionListener(new ActionListener()
//        {
//          public void actionPerformed(ActionEvent ae)
//          {
//            fileExit_ActionPerformed(ae);
//          }
//        });

    menuBar.add(menuTools);
    menuTools.setText(WWGnlUtilities.buildMessage("tools"));
    menuTools.add(showGRIBPointData);
    menuTools.add(new JSeparator());
    menuTools.add(menuCharts); // Externalized
    menuTools.add(new JSeparator());
    menuTools.add(menuRouting);
    menuRouting.add(menuToolsRouting);
    menuRouting.add(whatIfMenuItem);
    menuTools.add(menuToolsGeostrophicWind);
    
    menuTools.add(nmeaMenu);
    nmeaMenu.add(menuStartNMEA);
    nmeaMenu.add(manualNMEA);
    
    menuTools.add(new JSeparator());
    menuAdmin.setText(WWGnlUtilities.buildMessage("admin-menu"));
    menuAdmin.setIcon(new ImageIcon(instance.getClass().getResource("img/greydot.png")));
    menuTools.add(menuAdmin);
    menuAdmin.add(menuAdminGenImages);
    menuAdminGenImages.setText(WWGnlUtilities.buildMessage("admin-images"));
    menuAdminGenImages.setIcon(new ImageIcon(this.getClass().getResource("img/snapshot.png")));
    menuAdminGenImages.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            String compositeDir = ((ParamPanel.DataDirectory)ParamPanel.data[ParamData.CTX_FILES_LOC][1]).toString();
            WWGnlUtilities.generateImagesFromComposites(compositeDir, commandPanel);
          }
        });
    menuAdmin.add(menuDetectUnusedFiles);
    menuDetectUnusedFiles.setText(WWGnlUtilities.buildMessage("detect-unused"));
    menuDetectUnusedFiles.setIcon(new ImageIcon(this.getClass().getResource("img/findfiles.png")));
    menuDetectUnusedFiles.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            Thread t = new Thread()
              {
                public void run()
                {
              WWGnlUtilities.detectUnusedDocuments();
              WWContext.getInstance().fireSetLoading(false, "Detecting...");
                }
              };
          WWContext.getInstance().fireSetLoading(true, "Detecting...");
            t.start();
          }
        });
    menuAdmin.add(menuCleanOldBackups);
    menuCleanOldBackups.setText(WWGnlUtilities.buildMessage("cleanup-backup"));
    menuCleanOldBackups.setIcon(new ImageIcon(this.getClass().getResource("img/smrtdata.png")));
    menuCleanOldBackups.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
          WWGnlUtilities.cleanupBackups();
          }
        });
       
    menuAdmin.add(menuToolsPlaces);
    menuToolsPlaces.setText(WWGnlUtilities.buildMessage("places-mgmt"));
    menuToolsPlaces.setIcon(new ImageIcon(instance.getClass().getResource("img/places.png")));
    menuToolsPlaces.setSelected(true);
    menuToolsPlaces.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ae)
      {
        WWGnlUtilities.placesMgmt(instance);
      }
    });
    
    menuAdmin.add(menuManageUE);
    menuManageUE.setText(WWGnlUtilities.buildMessage("manage-ue"));
    menuManageUE.setIcon(new ImageIcon(this.getClass().getResource("img/help.png")));
    menuManageUE.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            WWGnlUtilities.manageUE(instance);
          }
        });
      
    menuTools.add(new JSeparator());
    menuToolsRetryNetwork.setText(WWGnlUtilities.buildMessage("retry-network"));
    menuToolsRetryNetwork.setIcon(new ImageIcon(this.getClass().getResource("img/network.png")));
    menuTools.add(menuToolsRetryNetwork);
    menuToolsRetryNetwork.setSelected(false);
    menuToolsRetryNetwork.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            ChartAdjust.checkForUpdate();
          }
        });
    
    menuTools.add(new JSeparator());
    menuTools.add(menuToolsPreferences);
    
    showGRIBPointData.setText(WWGnlUtilities.buildMessage("show-grib-datapoint"));
    showGRIBPointData.setIcon(new ImageIcon(instance.getClass().getResource("img/greydot.png")));
    showGRIBPointData.setSelected(commandPanel.isShowGRIBPointPanel());
    showGRIBPointData.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            commandPanel.setShowGRIBPointPanel(showGRIBPointData.isSelected());
          }
        });

    menuCharts.setText(WWGnlUtilities.buildMessage("predefined-zones"));
    menuCharts.setIcon(new ImageIcon(instance.getClass().getResource("img/greydot.png")));
    buildChartMenu();

    menuToolsPreferences.setText(WWGnlUtilities.buildMessage("preferences"));
    menuToolsPreferences.setIcon(new ImageIcon(instance.getClass().getResource("img/tools.png")));
    menuToolsPreferences.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            CategoryPanel cp = new CategoryPanel();
            int opt = 
              JOptionPane.showConfirmDialog(WWContext.getInstance().getMasterTopFrame(), 
                                            cp, 
                                            "Application Parameters", 
                                            JOptionPane.OK_CANCEL_OPTION, 
                                            JOptionPane.DEFAULT_OPTION);
            if (opt == JOptionPane.OK_OPTION)
              cp.finalPrmUpdate();
          }
        });
    menuRouting.setText(WWGnlUtilities.buildMessage("routing"));
    menuRouting.setIcon(new ImageIcon(instance.getClass().getResource("img/navigation.png")));
    menuToolsRouting.setText(WWGnlUtilities.buildMessage("routing-dot"));
    menuToolsRouting.setIcon(new ImageIcon(instance.getClass().getResource("img/navigation.png")));
    menuToolsRouting.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            if (commandPanel.getFrom() != null && 
                commandPanel.getTo() != null && 
                commandPanel.getGribData() != null)
            {
  //          int resp = JOptionPane.showConfirmDialog(null, "Start routing computation?", "Routing", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
  //          if (resp == JOptionPane.OK_OPTION)
              {
                commandPanel.calculateRouting();
              }
            }
            else
            {
              JOptionPane.showMessageDialog(instance, WWGnlUtilities.buildMessage("orig-dest-routing"), WWGnlUtilities.buildMessage("routing"), 
                                            JOptionPane.WARNING_MESSAGE);
            }
          }
        });
    whatIfMenuItem.setText(WWGnlUtilities.buildMessage("what-if")); // Reverse Routing.
    whatIfMenuItem.setIcon(new ImageIcon(instance.getClass().getResource("img/navigation.png")));
    whatIfMenuItem.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            if (commandPanel.getFrom() != null && 
//              commandPanel.getTo() != null && 
                commandPanel.getGribData() != null)
            {
  //          int resp = JOptionPane.showConfirmDialog(null, "Start routing computation?", "Routing", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
  //          if (resp == JOptionPane.OK_OPTION)
              {
                commandPanel.whatIfRouting();
              }
            }
            else
            {
              JOptionPane.showMessageDialog(instance, 
                                            WWGnlUtilities.buildMessage("orig-dest-routing"), // TODO One point required only
                                            WWGnlUtilities.buildMessage("routing"), 
                                            JOptionPane.WARNING_MESSAGE);
            }
          }
        });
    menuToolsGeostrophicWind.setText(WWGnlUtilities.buildMessage("geostrophic-wind"));
    menuToolsGeostrophicWind.setIcon(new ImageIcon(this.getClass().getResource("img/greenflag.png")));
    menuToolsGeostrophicWind.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            if (commandPanel.getFrom() != null && 
                commandPanel.getTo() != null)
            {
              if (Math.abs(commandPanel.getFrom().getL()) < 30D || 
                  Math.abs(commandPanel.getTo().getL()) < 30D)
              {
                 JOptionPane.showMessageDialog(instance, WWGnlUtilities.buildMessage("under-30"), WWGnlUtilities.buildMessage("geostrophic-wind"), 
                                               JOptionPane.ERROR_MESSAGE); 
              }
              else
              {
                // Get the distance
              WWContext.getInstance().getGreatCircle().setStart(new GeoPoint(Math.toRadians(commandPanel.getFrom().getL()), Math.toRadians(commandPanel.getFrom().getG())));
              WWContext.getInstance().getGreatCircle().setArrival(new GeoPoint(Math.toRadians(commandPanel.getTo().getL()), Math.toRadians(commandPanel.getTo().getG())));                
                double gcDist = Math.toDegrees(WWContext.getInstance().getGreatCircle().getDistance()) * 60.0;

                int interval = ((Integer) ParamPanel.data[ParamData.INTERVAL_BETWEEN_ISOBARS][1]).intValue();
                double gws = WWGnlUtilities.getGeostrophicWindSpeed(gcDist, Math.abs((commandPanel.getFrom().getL() + commandPanel.getTo().getL())/ 2D), interval);
                // Display result
                String result = WWGnlUtilities.buildMessage("geostrophic-wind-speed") + WWGnlUtilities.DF2.format(gws)       + " kts.\n" +
                WWGnlUtilities.buildMessage("with-friction") + WWGnlUtilities.DF2.format(gws * 0.7) + " kts.";
                
                JOptionPane.showMessageDialog(instance, 
                                              result, WWGnlUtilities.buildMessage("geostrophic-wind"), 
                                              JOptionPane.INFORMATION_MESSAGE);
              }
            }
            else
            {
              JOptionPane.showMessageDialog(instance, WWGnlUtilities.buildMessage("need-2-points"), WWGnlUtilities.buildMessage("geostrophic-wind"), 
                                            JOptionPane.WARNING_MESSAGE);
            }
          }
        });
        
    nmeaMenu.setText(WWGnlUtilities.buildMessage("position-acquisition"));
    nmeaMenu.setIcon(new ImageIcon(instance.getClass().getResource("img/greydot.png")));
    menuStartNMEA.setText(WWGnlUtilities.buildMessage("from-gps"));
    menuStartNMEA.setIcon(new ImageIcon(instance.getClass().getResource("img/gps.png")));
    menuStartNMEA.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
          WWContext.getInstance().fireNMEAAcquisition(menuStartNMEA.isSelected());
          }
        });
    manualNMEA.setText(WWGnlUtilities.buildMessage("manual"));       
    manualNMEA.setIcon(new ImageIcon(this.getClass().getResource("img/grab.png")));
    manualNMEA.addActionListener(new ActionListener()
        {
          File manualPositionFile = new File("." + File.separator + "config" + File.separator + "manualposition.xml");

          public void actionPerformed(ActionEvent ae)
          {
            if (pip == null)
              pip = new PositionInputPanel();
            // Manual position already exist?
            if (manualPositionFile.exists())
            {
              // Read and feed
               DOMParser parser = WWContext.getInstance().getParser();
               try
               {
                 XMLDocument doc = null;
                 synchronized(parser)
                 {
                   parser.parse(manualPositionFile.toURI().toURL());
                   doc = parser.getDocument();
                 }
                 double lat = Double.parseDouble(((XMLElement)doc.getDocumentElement()).getAttribute("latitude"));
                 double lng = Double.parseDouble(((XMLElement)doc.getDocumentElement()).getAttribute("longitude"));
                 int hdg = Integer.parseInt(((XMLElement)doc.getDocumentElement()).getAttribute("heading"));
                 pip.setData(lat, lng, hdg);
               }
               catch (Exception ex)
               {
                 ex.printStackTrace();
               }
            }
            int resp = JOptionPane.showConfirmDialog(WWContext.getInstance().getMasterTopFrame(), pip, WWGnlUtilities.buildMessage("position-manual-entry"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (resp == JOptionPane.OK_OPTION)
            {
              double l = pip.getL();
              double g = pip.getG();
              int heading = pip.getHeading();
            WWContext.getInstance().fireManuallyEnterBoatPosition(new GeoPoint(l, g), heading);
              // Write to file for next time
              XMLDocument doc = new XMLDocument();
              XMLElement root = (XMLElement)doc.createElement("root");
              doc.appendChild(root);
              root.setAttribute("latitude", Double.toString(l));
              root.setAttribute("longitude", Double.toString(g));
              root.setAttribute("heading", Integer.toString(heading));
              try { doc.print(new FileOutputStream(manualPositionFile)); } catch (Exception ioe) { ioe.printStackTrace(); }
            }
          }
        });

    menuBar.add(menuHelp);
    menuHelp.setText(WWGnlUtilities.buildMessage("help"));
    menuHelp.add(menuHelpAbout);
    menuHelpAbout.setText(WWGnlUtilities.buildMessage("about"));
    menuHelpAbout.setIcon(new ImageIcon(this.getClass().getResource("img/help.png")));
    menuHelpAbout.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            JOptionPane.showMessageDialog(instance, new AboutBox(), 
                                          "GRIB, Weather Faxes, Charts", 
                                          JOptionPane.PLAIN_MESSAGE);
          }
        });
//    menuHelp.add(menuHelpContent);
//    menuHelpContent.setText("Content");
//    menuHelpContent.addActionListener(new ActionListener()
//        {
//          public void actionPerformed(ActionEvent ae)
//          {
//            showHelp();
//          }
//        });
    menuHelp.add(new HelpOpenAction()).setAccelerator(KeyStroke.getKeyStroke("F1"));
    
    menuHelpContact.setText(WWGnlUtilities.buildMessage("contact-dev-team-dot"));
    menuHelpContact.setIcon(new ImageIcon(this.getClass().getResource("img/onecamel.png")));
    menuHelpContact.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            if (WWContext.getInstance().isOnLine())
            {
              final ContactPanel cp = new ContactPanel();
              boolean fineByMe = false;
              while (!fineByMe)
              {
                int resp = JOptionPane.showConfirmDialog(null, cp, WWGnlUtilities.buildMessage("contact-dev-team"), 
                                              JOptionPane.OK_CANCEL_OPTION,
                                              JOptionPane.PLAIN_MESSAGE);
                if (resp == JOptionPane.OK_OPTION)
                {
                  String name    = cp.getName();
                  String email   = cp.getEmail();
                  String message = cp.getMessage();
                  
                  if (name.trim().length() == 0 || email.trim().length() == 0 || message.trim().length() == 0)
                  {
                    // This should not happen
                    System.out.println("A problem...");
                    JOptionPane.showMessageDialog(instance, WWGnlUtilities.buildMessage("please-all-fields"), WWGnlUtilities.buildMessage("contact-dev-team"), JOptionPane.ERROR_MESSAGE);
                  }
                  else
                  {
                    fineByMe = true;
                    String userMessage = "From:" + name + "\n" +
                                         "email:" + email + "\n" +
                                         message + "\n\n";
                    ChartAdjust.sendPing(userMessage);                                       
                  }
                }
                else
                  fineByMe = true;
              }
            }
            else
            {
              JOptionPane.showMessageDialog(instance, "You're not on line,\nor your Internet connection is not accessible.\nThere is currently not way to send a message...", "Contact", JOptionPane.WARNING_MESSAGE);
            }
          }
        });
    menuHelp.add(menuHelpContact);
    
    
    menuCheckForUpdate.setText(WWGnlUtilities.buildMessage("check-for-update-menu"));
    menuCheckForUpdate.setIcon(new ImageIcon(this.getClass().getResource("img/download.png")));
    menuCheckForUpdate.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            if (WWContext.getInstance().isOnLine())
            {
              ChartAdjust.checkForUpdate();
            }
            else
            {
              JOptionPane.showMessageDialog(instance, "You're not on line,\nor your Internet connection is not accessible.\nThere is currently not way to send a message...", "Contact", JOptionPane.WARNING_MESSAGE);
            }
          }
        });
   menuHelp.add(new JSeparator());
   menuHelp.add(menuCheckForUpdate);
    
    getContentPane().setLayout(borderLayout);
    setSize(new Dimension(1000, 700));

    Thread pendule = new Thread()
    {
      public void run()
      {
        while (true)
        {
          sdfForTitle.setTimeZone(TimeZone.getDefault());
          Date ut = TimeUtil.getGMT();
          String title = FRAME_BASE_TITLE + " - UTC: " + sdfForTitle.format(ut);
//        System.out.println("Setting time to " + title);
          setTitle(title);
          try { Thread.sleep(1000L); } catch (Exception ignore) {}
        }
      }
    };
    pendule.start();

    this.getContentPane().add(statusPanel, BorderLayout.SOUTH);
    statusPanel.setLayout(new BorderLayout());
    statusPanel.add(progressBar, BorderLayout.EAST);
    statusPanel.add(statusLabel, BorderLayout.WEST);
    //  progressBar.setIndeterminate(false);
    progressBar.setValue(0);
    progressBar.setString(WWGnlUtilities.buildMessage("loading"));
//  progressBar.setStringPainted(true);
    progressBar.setEnabled(false);

//  jSplitPane.setRightComponent(commandPanel);
//  jSplitPane.setLeftComponent(allJTrees);
//  jSplitPane.add(commandPanel, JSplitPane.RIGHT);
//  jSplitPane.add(allJTrees, JSplitPane.LEFT);
    tabbedPane.add(WWGnlUtilities.buildMessage("chart"), commandPanelHolder);
    tabbedPane.add("3D GRIB Data", threeDGRIBPanel);
    tabbedPane.setEnabledAt(1, false);

    WWGnlUtilities.setTreeConfig(allJTrees);
    jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, allJTrees, tabbedPane);
    jSplitPane.setContinuousLayout(true);
    jSplitPane.setOneTouchExpandable(true);
//  jSplitPane.setOneTouchExpandable(false);
    jSplitPane.setDividerSize(8);
    jSplitPane.setDividerLocation(175);

    this.getContentPane().add(jSplitPane, BorderLayout.CENTER);

    ccp.getProjectionPanel().setSelectedProjection(commandPanel.getProjection());
    if (false) // Obsolete
    {
      // Look and Feel
      String laf = ((ParamPanel.ListOfLookAndFeel)ParamPanel.data[ParamData.LOOK_AND_FEEL][1]).getCurrentValue();
      String currentLaf = UIManager.getLookAndFeel().getName();
      
  //  System.out.println("Comparing " + laf + " to " + currentLaf);
      
      if (!currentLaf.equals(laf))
        setLandF(laf);   
    }
  }

  private void buildChartMenu()
  {
    menuCharts.removeAll();
    try
    {
      ArrayList<PredefZone> pdfz = getPredefZones(); // Read the file
      for (final PredefZone pz : pdfz)
      {
        if (pz.name.equals("[SEPARATOR]"))
        {
          menuCharts.add(new JSeparator());
        }
        else
        {
          JMenuItem mni = new JMenuItem(pz.name);
          mni.addActionListener(new ActionListener()
            {
              public void actionPerformed(ActionEvent ae)
              {
                commandPanel.applyBoundariesChanges(pz.top, pz.bottom, pz.left, pz.right);
              }
            });
          menuCharts.add(mni);
        }
      }
      menuCharts.add(new JSeparator());
      managePredefinedZones.setText(WWGnlUtilities.buildMessage("manage-predefined-zones"));
      menuCharts.add(managePredefinedZones);
      managePredefinedZones.addActionListener(new ActionListener()
          {
            public void actionPerformed(ActionEvent e)
            {
              PredefZonesTablePanel pdztp = new PredefZonesTablePanel();
              DOMParser parser = WWContext.getInstance().getParser();
              try
              {
                synchronized(parser)
                {
                  parser.parse(new File("config" + File.separator + "predefined-zones.xml").toURI().toURL());
                  pdztp.setData(parser.getDocument());
                }
                int resp = JOptionPane.showConfirmDialog (instance, pdztp, WWGnlUtilities.buildMessage("predefined-zones"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (resp == JOptionPane.OK_OPTION)
                {
                  XMLDocument predefDoc = pdztp.getData();
                  predefDoc.print(new FileOutputStream(new File("config" + File.separator + "predefined-zones.xml")));
                  buildChartMenu();
                }
              }
              catch (Exception ex)
              {
                JOptionPane.showMessageDialog(instance, ex.toString(), ":)", JOptionPane.ERROR_MESSAGE);  
                ex.printStackTrace();
              }                            
            }
          });
    }
    catch (Exception ex)
    {
      WWContext.getInstance().fireExceptionLogging(ex);
      ex.printStackTrace();
    }    
  }
  
  public void setStatusLabel(String s)
  {
    statusLabel.setText(s);  
  }
  
  private void generatePattern()
  {
    WWContext.getInstance().fireGeneratePattern();
  }

  private void loadWithPattern()
  {
    WWContext.getInstance().fireLoadWithPattern();
  }

  private void downloadFromTheWebAndDisplayComposite(String url)
  {
    WWGnlUtilities.readCompositeFromURL(url);
  }
  
  private void setLandF(ActionEvent e)
  {
    String lnf = e.getActionCommand();
    setLandF(lnf);
    WWContext.getInstance().setLookAndFeel(lnf);
  }

  private void setLandF(String lnf)
  {
    UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
    if (info != null)
    {
      for (int i = 0; i < info.length; i++)
      {
        if (info[i].getName().equals(lnf))
        {
          try
          {
            UIManager.setLookAndFeel(info[i].getClassName());
            SwingUtilities.updateComponentTreeUI(this);
          }
          catch (Exception ex)
          {
            WWContext.getInstance().fireExceptionLogging(ex);
            ex.printStackTrace();
          }
          break;
        }
      }
    }
  }

  private void setupComposite()
  {
    setupComposite(null, WWContext.getInstance().getCurrentGribFileName());
  }

  private void setupComposite(String faxFile, String gribFile)
  {
    if (inputPanel == null)
      inputPanel = new WeatherDataInputPanel();

    FaxType[] faxarray = commandPanel.getFaxes();
//  if (faxarray != null)
      inputPanel.setFaxes(faxarray);    
    
    if (faxFile != null)
      inputPanel.addNewFaxFileInTable(faxFile);

    if (faxFile == null && faxarray == null) // then it might be a GRIB
      inputPanel.setSizeFromGRIB(true);

    if (gribFile != null && gribFile.trim().length() > 0)
    {
      inputPanel.setGribFileName(gribFile);
      File gf = new File(gribFile);
      inputPanel.setGRIBRequestSelected(!gf.exists()); // If file not found, assume GRIB Request
    }
    inputPanel.setPRMSL(commandPanel.isDisplayPrmsl() && commandPanel.isTherePrmsl());
    inputPanel.set500mb(commandPanel.isDisplay500mb() && commandPanel.isThere500mb());
    inputPanel.setWaves(commandPanel.isDisplayWaves() && commandPanel.isThereWaves());
    inputPanel.setTemp(commandPanel.isDisplayTemperature() && commandPanel.isThereTemperature());
    inputPanel.setPrate(commandPanel.isDisplayRain() && commandPanel.isThereRain());
    
    inputPanel.set3DTWS(commandPanel.isDisplay3DTws());
    inputPanel.set3DPRMSL(commandPanel.isDisplay3DPrmsl());
    inputPanel.set3D500hgt(commandPanel.isDisplay3D500mb());
    inputPanel.set3DWaves(commandPanel.isDisplay3DWaves());
    inputPanel.set3DTemp(commandPanel.isDisplay3DTemperature());
    inputPanel.set3DRain(commandPanel.isDisplay3DRain());
    
    inputPanel.setPRMSLContour(commandPanel.isDisplayContourPRMSL());
    inputPanel.set500mbContour(commandPanel.isDisplayContour500mb());
    inputPanel.setWavesContour(commandPanel.isDisplayContourWaves());
    inputPanel.setTempContour(commandPanel.isDisplayContourTemp());
    inputPanel.setPrateContour(commandPanel.isDisplayContourPrate());
    
    inputPanel.setComment(commandPanel.getCurrentComment());

    inputPanel.setTopLat(commandPanel.getNLat());
    inputPanel.setBottomLat(commandPanel.getSLat());
    inputPanel.setLeftLong(commandPanel.getWLong());
    inputPanel.setRightLong(commandPanel.getELong());
    int resp = JOptionPane.showConfirmDialog(this, 
                                             inputPanel, WWGnlUtilities.buildMessage("set-data-context"), 
                                             JOptionPane.OK_CANCEL_OPTION, 
                                             JOptionPane.PLAIN_MESSAGE);
    if (resp == JOptionPane.OK_OPTION)
    {
      final boolean gribChanged =  haveGRIBOtpionsChanged(inputPanel, commandPanel);
      
      boolean[] gribOptions = inputPanel.getGRIBOptions();

      boolean atLeastOne3D = gribOptions[WeatherDataInputPanel.TWS_3D] ||
                             gribOptions[WeatherDataInputPanel.PRMSL_3D] ||
                             gribOptions[WeatherDataInputPanel.MB500_3D] ||
                             gribOptions[WeatherDataInputPanel.WAVES_3D] ||
                             gribOptions[WeatherDataInputPanel.TEMP_3D] ||
                             gribOptions[WeatherDataInputPanel.PRATE_3D];
      boolean atLeastOneContour = gribOptions[WeatherDataInputPanel.TWS_CONTOUR] ||
                                  gribOptions[WeatherDataInputPanel.PRMSL_CONTOUR] ||
                                  gribOptions[WeatherDataInputPanel.MB500_CONTOUR] ||
                                  gribOptions[WeatherDataInputPanel.WAVES_CONTOUR] ||
                                  gribOptions[WeatherDataInputPanel.TEMP_CONTOUR] ||
                                  gribOptions[WeatherDataInputPanel.PRATE_CONTOUR];
      if (((faxFile != null && faxFile.trim().length() > 0) || (faxarray != null && faxarray.length > 0)) && 
        /* inputPanel.getGribFileName().trim().length() > 0 && */
           atLeastOneContour) // Confirm Faxes + GRIB contour lines
      {
        int response = JOptionPane.showConfirmDialog(this, 
                                                     WWGnlUtilities.buildMessage("confirm-fax-plus-cl"), 
                                                     WWGnlUtilities.buildMessage("weather-data"), 
                                                     JOptionPane.YES_NO_OPTION, 
                                                     JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.NO_OPTION)
        {
          System.out.println("Canceling contour lines");
          gribOptions[WeatherDataInputPanel.TWS_CONTOUR] = false;
          gribOptions[WeatherDataInputPanel.PRMSL_CONTOUR] = false;
          gribOptions[WeatherDataInputPanel.MB500_CONTOUR] = false;
          gribOptions[WeatherDataInputPanel.WAVES_CONTOUR] = false;
          gribOptions[WeatherDataInputPanel.TEMP_CONTOUR]  = false;
          gribOptions[WeatherDataInputPanel.PRATE_CONTOUR] = false;
        }
      }
//    commandPanel.setDisplayContour(withContourLines);
      commandPanel.setCurrentComment(inputPanel.getComment());
      
      commandPanel.setDisplayPrmsl(gribOptions[WeatherDataInputPanel.PRMSL_DATA]);
      commandPanel.setDisplay500mb(gribOptions[WeatherDataInputPanel.MB500_DATA]);
      commandPanel.setDisplayWaves(gribOptions[WeatherDataInputPanel.WAVES_DATA]);
      commandPanel.setDisplayTemperature(gribOptions[WeatherDataInputPanel.TEMP_DATA]);
      commandPanel.setDisplayRain(gribOptions[WeatherDataInputPanel.PRATE_DATA]);
      
      commandPanel.setDisplay3DTws(gribOptions[WeatherDataInputPanel.TWS_3D]);
      commandPanel.setDisplay3DPrmsl(gribOptions[WeatherDataInputPanel.PRMSL_3D]);
      commandPanel.setDisplay3D500mb(gribOptions[WeatherDataInputPanel.MB500_3D]);
      commandPanel.setDisplay3DWaves(gribOptions[WeatherDataInputPanel.WAVES_3D]);
      commandPanel.setDisplay3DTemperature(gribOptions[WeatherDataInputPanel.TEMP_3D]);
      commandPanel.setDisplay3DRain(gribOptions[WeatherDataInputPanel.PRATE_3D]);
      if (atLeastOne3D)
      {
        tabbedPane.setEnabledAt(1, true);  
        if (commandPanel.getGribData() != null)
          threeDGRIBPanel.getThreeDPanel().setPanelLabel(commandPanel.getGribData()[commandPanel.getGribIndex()].getDate().toString());
      }
      else
      {
        if (tabbedPane.getSelectedIndex() == 1)
          tabbedPane.setSelectedIndex(0);
        tabbedPane.setEnabledAt(1, false);
      }
      commandPanel.setDisplayContourTWS(gribOptions[WeatherDataInputPanel.TWS_CONTOUR]);
      commandPanel.setDisplayContourPRMSL(gribOptions[WeatherDataInputPanel.PRMSL_CONTOUR]);
      commandPanel.setDisplayContour500mb(gribOptions[WeatherDataInputPanel.MB500_CONTOUR]);
      commandPanel.setDisplayContourWaves(gribOptions[WeatherDataInputPanel.WAVES_CONTOUR]);
      commandPanel.setDisplayContourTemp(gribOptions[WeatherDataInputPanel.TEMP_CONTOUR]);
      commandPanel.setDisplayContourPrate(gribOptions[WeatherDataInputPanel.PRATE_CONTOUR]);

//    System.out.println("InputPanel OK");
      WWContext.getInstance().fireSetLoading(true);
      Thread loader = new Thread()
        {
          public void run()
          {
//          System.out.println("Top of loader thread");
            String grib = inputPanel.getGribFileName();
//          if (!(grib.trim().length() > 0 && inputPanel.isSizeFromGRIB()))
            {
              commandPanel.setNLat(inputPanel.getTopLat());
              commandPanel.setSLat(inputPanel.getBottomLat());
              commandPanel.setWLong(inputPanel.getLeftLong());
              commandPanel.setELong(inputPanel.getRightLong());
              commandPanel.applyBoundariesChanges();
            }
            if (grib.trim().length() > 0)
            {    
              boolean keepGoing = true;
              GribHelper.GribConditionData[] wgd = null;
              if (commandPanel.getGribData() != null && 
                  inputPanel.isGRIBRequestSelected() &&
                  grib.equals(commandPanel.getGRIBDataName()))
              {
                // Ask if we reload the GRIB (if the GRIB does not come from a waz)
                String ccName = WWContext.getInstance().getCurrentComposite();
                if (ccName != null && ccName.endsWith(WWContext.WAZ_EXTENSION) && !gribChanged)
                {
                  wgd = commandPanel.getGribData();
                  keepGoing = false;
                }
                if (keepGoing)
                {
                  if (ccName != null && ccName.endsWith(WWContext.WAZ_EXTENSION) && gribChanged)
                  {
                    wgd = commandPanel.getGribData();
                    keepGoing = false;
                  }
                  else
                  {
                    int resp = JOptionPane.showConfirmDialog(commandPanel, 
                                                             WWGnlUtilities.buildMessage("reload-grib-data"), 
                                                             WWGnlUtilities.buildMessage("grib-download"), 
                                                             JOptionPane.YES_NO_OPTION, 
                                                             JOptionPane.QUESTION_MESSAGE);
                    if (resp == JOptionPane.NO_OPTION)
                    {
                      wgd = commandPanel.getGribData();
                      keepGoing = false;
                    }
                  }
                }
              }
              if (keepGoing)
              {
                if (inputPanel.isGRIBRequestSelected()) // Then we assume it is to be reached through http
                {
                  commandPanel.setGribRequest(grib); 
                  String gribRequest = WWGnlUtilities.generateGRIBRequest(grib);
                  try
                  {                
                    System.out.println(gribRequest);
                    URL saildocs = new URL(gribRequest);
                    URLConnection connection = saildocs.openConnection();
                    connection.connect();
              //    DataInputStream dis = new DataInputStream(connection.getInputStream());
                    InputStream dis = connection.getInputStream();
              
                    long waiting = 0L;
                    while (dis.available() == 0 && waiting < 30L) // 30s Timeout...
                    {
                      Thread.sleep(1000L);
                      waiting += 1L;
                    }
  //                System.out.println("Waiting: " + waiting);
  //                System.out.println("Available:" + dis.available());
  
                    if (true)
                    {
                      final int BUFFER_SIZE = 65536;
                      byte aByte[] = new byte[BUFFER_SIZE];
                      byte[] content = null;
                      int nBytes;
                      while((nBytes = dis.read(aByte, 0, BUFFER_SIZE)) != -1) 
                        content = Utilities.appendByteArrays(content, aByte, nBytes);

                      WWContext.getInstance().fireSetStatus("Read " + content.length + " bytes of GRIB data.");
                      System.out.println("Read " + content.length + " bytes.");
                      dis.close();
                      ByteArrayInputStream bais = new ByteArrayInputStream(content);                    
                      dis = bais; // switch
                    }
                    wgd = GribHelper.getGribData(dis, grib); // From an InputStream             
                  }
                  catch (Exception ex)
                  {
                    ex.printStackTrace();
                  }
                }
                else
                  wgd = GribHelper.getGribData(grib);  // From a File
              }  
              if (inputPanel.isSizeFromGRIB() && wgd != null)
              {
                commandPanel.setNLat(wgd[0].getNLat());
                commandPanel.setSLat(wgd[0].getSLat());
                commandPanel.setWLong(wgd[0].getWLng());
                commandPanel.setELong(wgd[0].getELng());
              }
              commandPanel.applyBoundariesChanges();
              commandPanel.setGribData(wgd, 
                                       grib); // Event sent, generates obj, etc.
            }
  //        System.out.println("Setting command panel boundaries:" + commandPanel.getNLat() + " to " + commandPanel.getSLat() + ", and " + commandPanel.getWLong() + " to " + commandPanel.getELong());

            FaxType[] faxes = inputPanel.getFaxes();
            if (faxes != null)
            {
              for (int i = 0; i < faxes.length; i++)
              {
      //        System.out.println("Rank: " + (i+1) + ", " + faxes[i].getValue() + ", " + faxes[i].getColor());
                String comment = WWGnlUtilities.getHeader(faxes[i].getValue());
                faxes[i].setComment(comment);
                appendFrameTitle(comment);
              }
              commandPanel.setFaxes(faxes);
            }

  //        System.out.println("End of loader thread.");
          WWContext.getInstance().fireSetLoading(false);
          }
        };
      loader.start();
    }
  }

  private boolean haveGRIBOtpionsChanged(WeatherDataInputPanel ip, CommandPanel cp)
  {
    boolean[] gribOptions = ip.getGRIBOptions();
    return (gribOptions[WeatherDataInputPanel.PRMSL_DATA] != cp.isDisplayPrmsl() ||
            gribOptions[WeatherDataInputPanel.MB500_DATA] != cp.isDisplay500mb() ||
            gribOptions[WeatherDataInputPanel.WAVES_DATA] != cp.isDisplayWaves() ||
            gribOptions[WeatherDataInputPanel.TEMP_DATA] != cp.isDisplayTemperature() ||
            gribOptions[WeatherDataInputPanel.PRATE_DATA] != cp.isDisplayRain() ||
            gribOptions[WeatherDataInputPanel.TWS_3D] != cp.isDisplay3DTws() ||
            gribOptions[WeatherDataInputPanel.PRMSL_3D] != cp.isDisplay3DPrmsl() || 
            gribOptions[WeatherDataInputPanel.MB500_3D] != cp.isDisplay3D500mb() ||
            gribOptions[WeatherDataInputPanel.WAVES_3D] != cp.isDisplay3DWaves() ||
            gribOptions[WeatherDataInputPanel.TEMP_3D] != cp.isDisplay3DTemperature() ||
            gribOptions[WeatherDataInputPanel.PRATE_3D] != cp.isDisplay3DRain() ||
            gribOptions[WeatherDataInputPanel.TWS_CONTOUR] != cp.isDisplayContourTWS() ||
            gribOptions[WeatherDataInputPanel.PRMSL_CONTOUR] != cp.isDisplayContourPRMSL() ||
            gribOptions[WeatherDataInputPanel.MB500_CONTOUR] != cp.isDisplayContour500mb() ||
            gribOptions[WeatherDataInputPanel.WAVES_CONTOUR] != cp.isDisplayContourWaves() ||
            gribOptions[WeatherDataInputPanel.TEMP_CONTOUR] != cp.isDisplayContourTemp() ||
            gribOptions[WeatherDataInputPanel.PRATE_CONTOUR] != cp.isDisplayContourPrate());
  }
  
  private void appendFrameTitle(String s)
  {
    String str = this.getTitle();
    str += (" - " + s);
    this.setTitle(str);
  }

  void fileOpen_ActionPerformed(ActionEvent e)
  {
    setupComposite(null, WWContext.getInstance().getCurrentGribFileName());
  }

  void filePrint_ActionPerformed(ActionEvent e)
  {
    commandPanel.print();
  }

  void downloadFaxFromInternet()
  {
    final InternetFax inf = new InternetFax();
    int resp = JOptionPane.showConfirmDialog(this, inf, WWGnlUtilities.buildMessage("download-from-internet"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (resp == JOptionPane.OK_OPTION && inf.getFaxLocalFile().trim().length() > 0)
    {
      File f = new File(inf.getFaxLocalFile());
      boolean go = true;      
      if (f.exists())
      {
        int r = JOptionPane.showConfirmDialog(WWContext.getInstance().getMasterTopFrame(), WWGnlUtilities.buildMessage("already-exists-override", new String[] {inf.getFaxLocalFile()}), WWGnlUtilities.buildMessage("download"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.YES_OPTION)
          go = false;
      }
      if (go)
      {
        Thread downLoadThread = new Thread()
        {
          public void run()
          {
            String saveAs = inf.getFaxLocalFile();
            WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("loading2", new String[] { inf.getFaxStrURL() }) + "\n", LoggingPanel.WHITE_STYLE);
            try
            {
              HTTPClient.getChart(inf.getFaxStrURL(), ".", saveAs, true);
              JOptionPane.showMessageDialog(instance, WWGnlUtilities.buildMessage("is-ready", new String[] { saveAs }), WWGnlUtilities.buildMessage("fax-download"), JOptionPane.INFORMATION_MESSAGE);
  //          allJTrees.refreshFaxTree();
              WWContext.getInstance().fireReloadFaxTree();          
            }
            catch (Exception ex)
            {
              ex.printStackTrace();
            }
          }
        };
        downLoadThread.start();
      }
    }
  }
  
  void downloadGRIBFromInternet()
  {
    final InternetGRIB ing = new InternetGRIB();
    int resp = JOptionPane.showConfirmDialog(this, 
                                             ing, WWGnlUtilities.buildMessage("download-from-internet"), 
                                             JOptionPane.OK_CANCEL_OPTION, 
                                             JOptionPane.PLAIN_MESSAGE);
    if (resp == JOptionPane.OK_OPTION && ing.getGRIBLocalFile().trim().length() > 0)
    {
      File f = new File(ing.getGRIBLocalFile());
      boolean go = true;      
      if (f.exists())
      {
        int r = JOptionPane.showConfirmDialog(WWContext.getInstance().getMasterTopFrame(), WWGnlUtilities.buildMessage("already-exists-override", 
                                                                        new String[] {ing.getGRIBLocalFile()}), WWGnlUtilities.buildMessage("download"), 
                                              JOptionPane.YES_NO_OPTION, 
                                              JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.YES_OPTION)
          go = false;
      }
      if (go)
      {
        Thread downLoadThread = new Thread()
        {
          public void run()
          {
            String saveAs = ing.getGRIBLocalFile();
            WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("loading2", new String[] { ing.getGRIBLocalFile() }) + "\n", LoggingPanel.WHITE_STYLE);
            HTTPClient.getGRIB(WWGnlUtilities.generateGRIBRequest(ing.getGRIBRequest()), ".", saveAs, true);
            JOptionPane.showMessageDialog(instance, WWGnlUtilities.buildMessage("is-ready", new String[] { saveAs }), WWGnlUtilities.buildMessage("grib-download"), JOptionPane.INFORMATION_MESSAGE);
//          allJTrees.refreshGribTree();
            WWContext.getInstance().fireReloadGRIBTree();
          }
        };
        downLoadThread.start();
      }
    }
  }
  
  private final static String AUTO_DOWNLOAD_CONFIG_FILE_NAME = "config" + File.separator + "autodownload.xml";

  private void setupDownload()
  {
    // Build the object
    // fax - url - dir - pattern
    Object data[][] = null;
    File autoDownloadConfigFile = new File(AUTO_DOWNLOAD_CONFIG_FILE_NAME);
    if (autoDownloadConfigFile.exists())
    {
      DOMParser parser = WWContext.getInstance().getParser();
      try
      {
        XMLDocument doc = null;
        synchronized(parser)
        {
          parser.setValidationMode(XMLParser.NONVALIDATING);
          parser.parse(autoDownloadConfigFile.toURI().toURL());
          doc = parser.getDocument();
        }
        NodeList documents = doc.selectNodes("/fax-collection/*");
        data = new Object[documents.getLength()][6];
        for (int i=0; i<documents.getLength(); i++)
        {
          XMLElement document = (XMLElement)documents.item(i);
          if (document.getNodeName().equals("fax"))
          {
            String faxName    = document.getAttribute("name");
            String faxUrl     = document.getAttribute("url");
            String faxDir     = document.getAttribute("dir");
            String faxPrefix  = document.getAttribute("prefix");
            String faxPattern = document.getAttribute("pattern");
            String faxExt     = document.getAttribute("extension");
            data[i][0] = faxName;
            data[i][1] = faxUrl;
            data[i][2] = new ParamPanel.DataDirectory("Faxes", faxDir);
            data[i][3] = faxPrefix;
            data[i][4] = faxPattern;
            data[i][5] = faxExt;
          }
          else if (document.getNodeName().equals("grib"))
          {
            String gribName    = document.getAttribute("name");
            String gribRequest = document.getAttribute("request");
            String gribDir     = document.getAttribute("dir");
            String gribPrefix  = document.getAttribute("prefix");
            String gribPattern = document.getAttribute("pattern");
            String gribExt     = document.getAttribute("extension");
            data[i][0] = gribName;
            data[i][1] = gribRequest;
            data[i][2] = new ParamPanel.DataDirectory("GRIBs", gribDir);
            data[i][3] = gribPrefix;
            data[i][4] = gribPattern;
            data[i][5] = gribExt;
          }
        }
      }
      catch (Exception ex)
      {
        WWContext.getInstance().fireExceptionLogging(ex);
        ex.printStackTrace();
      }
    }
    else
      WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("does-not-exist", 
                                                                  new String[] { autoDownloadConfigFile.getAbsolutePath() }) + "\n");
    // Display JTable
    if (data != null)
      WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("found-faxes", new String[] { Integer.toString(data.length) }) + "\n");
    AutoDownloadTablePanel autoTablePanel = new AutoDownloadTablePanel();
    autoTablePanel.setData(data);
    int resp = JOptionPane.showConfirmDialog(this, 
                                             autoTablePanel, WWGnlUtilities.buildMessage("automatic-download"), 
                                             JOptionPane.OK_CANCEL_OPTION, 
                                             JOptionPane.PLAIN_MESSAGE);
    // Write the file back
    if (resp == JOptionPane.OK_OPTION)
    {
      WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("validating") + "\n");
      data = autoTablePanel.getData();
      XMLDocument doc = new XMLDocument();
      Element root = doc.createElement("fax-collection");
      doc.appendChild(root);
      for (int i=0; i<data.length; i++)
      {
        String urlOrRequest = (String)data[i][1];
        if (urlOrRequest.startsWith("http://"))
        {
          XMLElement fax = (XMLElement)doc.createElement("fax");
          fax.setAttribute("name", (String)data[i][0]);
          fax.setAttribute("url", (String)data[i][1]);
          fax.setAttribute("dir", ((ParamPanel.DataDirectory)data[i][2]).toString());
          fax.setAttribute("prefix", (String)data[i][3]);
          fax.setAttribute("pattern", (String)data[i][4]);
          fax.setAttribute("extension", (String)data[i][5]);
          root.appendChild(fax);
        }
        else
        {
          XMLElement grib = (XMLElement)doc.createElement("grib");
          grib.setAttribute("name", (String)data[i][0]);
          grib.setAttribute("request", (String)data[i][1]);
          grib.setAttribute("dir", ((ParamPanel.DataDirectory)data[i][2]).toString());
          grib.setAttribute("prefix", (String)data[i][3]);
          grib.setAttribute("pattern", (String)data[i][4]);
          grib.setAttribute("extension", (String)data[i][5]);
          root.appendChild(grib);
        }
      }
      try
      {
        FileOutputStream fos = new FileOutputStream(AUTO_DOWNLOAD_CONFIG_FILE_NAME);
        doc.print(fos);
        fos.close();
      }
      catch (Exception e)
      {
        WWContext.getInstance().fireExceptionLogging(e);
        e.printStackTrace();
      }
    }
  }
  
  void startAutoDownload()
  {
    File autoDownloadConfigFile = new File(AUTO_DOWNLOAD_CONFIG_FILE_NAME);
    if (!autoDownloadConfigFile.exists())
    {
      JOptionPane.showMessageDialog(this, WWGnlUtilities.buildMessage("auto-download-not-configured"), WWGnlUtilities.buildMessage("automatic-download"), 
                                    JOptionPane.WARNING_MESSAGE);
      return;
    }
    else
    {
      DOMParser parser = WWContext.getInstance().getParser();
      try
      {
        XMLDocument doc = null;
        synchronized(parser)
        {
          parser.setValidationMode(XMLParser.NONVALIDATING);
          parser.parse(autoDownloadConfigFile.toURI().toURL());
          doc = parser.getDocument();
        }
        final NodeList downloadableDocuments = doc.selectNodes("/fax-collection/*");
        Thread autoDownload = new Thread()
        {
          public void run()
          {
            String finalMess = WWGnlUtilities.buildMessage("your-faxes"); 
            int fax = 0, grib = 0;
            for (int i=0; i<downloadableDocuments.getLength(); i++)
            {
              XMLElement document    = (XMLElement)downloadableDocuments.item(i);
              if (document.getNodeName().equals("fax"))
              {
                fax++;
                String faxUrl     = document.getAttribute("url");
                String faxDir     = document.getAttribute("dir");
                String faxPrefix  = document.getAttribute("prefix");
                String faxPattern = document.getAttribute("pattern");
                String faxExt     = document.getAttribute("extension");
                SimpleDateFormat sdf = new SimpleDateFormat(faxPattern);
                
                Date now = new Date();
                faxDir = WWGnlUtilities.translatePath(faxDir, now);

                String fileName = faxDir + File.separator + faxPrefix + sdf.format(now) + "." + faxExt;

                WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("loading2", new String[] { faxUrl }) + "\n", LoggingPanel.WHITE_STYLE);
                File fDir = new File(faxDir);
                if (!fDir.exists())
                  fDir.mkdirs();
                try
                {
                  HTTPClient.getChart(faxUrl, faxDir, fileName, true);    
                  finalMess += ("- " + fileName + "\n");
                }
                catch (Exception ex)
                {
                  ex.printStackTrace();
                }
              }
              else if (document.getNodeName().equals("grib"))
              {
                grib++;
                String request     = document.getAttribute("request");
                String gribDir     = document.getAttribute("dir");
                String girbPrefix  = document.getAttribute("prefix");
                String gribPattern = document.getAttribute("pattern");
                String gribExt     = document.getAttribute("extension");
                SimpleDateFormat sdf = new SimpleDateFormat(gribPattern);
                Date now = new Date();
                gribDir = WWGnlUtilities.translatePath(gribDir, now);
                File gDir = new File(gribDir);
                if (!gDir.exists())
                  gDir.mkdirs();
                String fileName = gribDir + File.separator + girbPrefix + sdf.format(new Date()) + "." + gribExt;

                WWContext.getInstance().fireLogging(WWGnlUtilities.buildMessage("loading2", new String[] { request }) + "\n", LoggingPanel.WHITE_STYLE);
                HTTPClient.getGRIB(WWGnlUtilities.generateGRIBRequest(request), gribDir, fileName, true);
                finalMess += ("- " + fileName + "\n");
              }
            }
            finalMess += WWGnlUtilities.buildMessage("are-ready");
            JOptionPane.showMessageDialog(instance, finalMess, WWGnlUtilities.buildMessage("automatic-download"), JOptionPane.INFORMATION_MESSAGE);
            if (fax > 0)
//            allJTrees.refreshFaxTree();
              WWContext.getInstance().fireReloadFaxTree();

            if (grib > 0)
//            allJTrees.refreshGribTree();
              WWContext.getInstance().fireReloadGRIBTree();
          }
        };
        autoDownload.start();
      }
      catch (Exception ex)
      {
        WWContext.getInstance().fireExceptionLogging(ex);
        ex.printStackTrace();
      }      
    }
  }
  
  void genImage_actionPerformed(ActionEvent e)
  {
    commandPanel.genImage();
  }

  void fileExit_ActionPerformed(ActionEvent e)
  {
    WWGnlUtilities.doOnExit();
  }

  private void store()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); 
         i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.store();
    }
  }

  private void restore()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.restore();
    }
  }
  
  private void gMap()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.googleMapRequested();
    }
  }
  
  private void gEarth()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.googleEarthRequested();
    }
  }
  
  private void showHelp()
  {
    try
    {
      // String lang = Locale.getDefault().getLanguage();
      // System.out.println("I speak " + lang);
      // String docFileName = System.getProperty("user.dir") + File.separator + "doc" + File.separator + "weather" + File.separator + "index.html";
      String docFileName = 
        "." + File.separator + "doc" + File.separator + "weather" + 
        File.separator + "index.html";
      Utilities.openInBrowser(docFileName);
    }
    catch (Exception e)
    {
      WWContext.getInstance().fireExceptionLogging(e);
      e.printStackTrace();
    }
  }
  
  private ArrayList<PredefZone> getPredefZones() throws Exception
  {
    ArrayList<PredefZone> pdfz = new ArrayList<PredefZone>(5);
    DOMParser parser = WWContext.getInstance().getParser();
    XMLDocument doc = null;
    synchronized(parser)
    {
      parser.setValidationMode(XMLParser.NONVALIDATING);
      parser.parse(new File("config" + File.separator + "predefined-zones.xml").toURI().toURL());
      doc = parser.getDocument();
    }
    NodeList items = doc.selectNodes("/pre-def-zones/*");
    for (int i=0; i<items.getLength(); i++)
    {
      XMLElement node = (XMLElement)items.item(i);
      String nodeName = node.getNodeName();
      if (nodeName.equals("item"))
      {
        pdfz.add(new PredefZone(node.getAttribute("name"),
                                Double.parseDouble(node.getAttribute("top")),
                                Double.parseDouble(node.getAttribute("bottom")),
                                Double.parseDouble(node.getAttribute("left")),
                                Double.parseDouble(node.getAttribute("right"))));
      }
      else if (nodeName.equals("separator"))
      {
        pdfz.add(new PredefZone("[SEPARATOR]", 0D, 0D, 0D, 0D));
      }
    }    
    return pdfz;
  }

  public CommandPanel getCommandPanel()
  {
    return commandPanel;
  }

  public FileTypeHolder getAllJTrees()
  {
    return allJTrees;
  }

  public class HelpOpenAction extends AbstractAction
  {
    public HelpOpenAction()
    {
      super(WWGnlUtilities.buildMessage("content"),
            new ImageIcon(instance.getClass().getResource("img/comment.png")));
    }

    public void actionPerformed(ActionEvent ae)
    {
      showHelp();
    }
  }
  
  public class ExitAction extends AbstractAction
  {
    public ExitAction()
    {
      super(WWGnlUtilities.buildMessage("exit"),
            new ImageIcon(instance.getClass().getResource("img/dummy.png")));
    }

    public void actionPerformed(ActionEvent ae)
    {
      fileExit_ActionPerformed(ae);
    }        
  }

  public class OpenAction extends AbstractAction
  {
    public OpenAction()
    {
      super(WWGnlUtilities.buildMessage("set-composite"),
            new ImageIcon(instance.getClass().getResource("img/composite.png")));
    }

    public void actionPerformed(ActionEvent ae)
    {
      fileOpen_ActionPerformed(ae);
    }        
  }

  public class SaveCompositeAction
    extends AbstractAction
  {
    public SaveCompositeAction()
    {
      super(WWGnlUtilities.buildMessage("save-composite"),
            new ImageIcon(instance.getClass().getResource("img/save.png")));
    }

    public void actionPerformed(ActionEvent ae)
    {
      store();
    }
  }

  class PredefZone
  {
    protected String name   = "";
    protected double top    = 0D;
    protected double bottom = 0D;
    protected double left   = 0D;
    protected double right  = 0D;
    
    public PredefZone(String s, double d1, double d2, double d3, double d4)
    {
      this.name   = s;
      this.top    = d1;
      this.bottom = d2;
      this.left   = d3;
      this.right  = d4;
    }
  }
  
  public class OscillateThread extends Thread
  { 
    String txt = "";
    boolean b = true;
    
    public OscillateThread(String txt, boolean b)
    {
      super();
      this.txt = txt;
      this.b = b;
    }
    
    public void run()
    {
      progressBar.setIndeterminate(b);
      progressBar.setString(txt);
      progressBar.setStringPainted(b);
      progressBar.setEnabled(b);
      progressBar.repaint();
    }
    
    public void abort()
    {
      progressBar.setIndeterminate(false);
      progressBar.setString("");
      progressBar.setStringPainted(false);
      progressBar.repaint();
    }
  }
}
