package chartview.gui;

import astro.calc.GeoPoint;

import chartview.ctx.ApplicationEventListener;
import chartview.ctx.WWContext;

import chartview.gui.left.FileTypeHolder;
import chartview.gui.right.CommandPanel;
import chartview.gui.right.CompositeTabComponent;
import chartview.gui.right.CompositeTabbedPane;
import chartview.gui.toolbar.controlpanels.LoggingPanel;
import chartview.gui.util.dialog.AutoDownloadTablePanel;
import chartview.gui.util.dialog.CompositeDetailsInputPanel;
import chartview.gui.util.dialog.ContactPanel;
import chartview.gui.util.dialog.FaxType;
import chartview.gui.util.dialog.InternetFax;
import chartview.gui.util.dialog.InternetGRIB;
import chartview.gui.util.dialog.LoadAtStartupPanel;
import chartview.gui.util.dialog.PositionInputPanel;
import chartview.gui.util.dialog.PredefZonesTablePanel;
import chartview.gui.util.param.CategoryPanel;
import chartview.gui.util.param.ParamData;
import chartview.gui.util.param.ParamPanel;
import chartview.gui.util.tree.JTreeFilePanel;

import chartview.util.GPXUtil;
import chartview.util.WWGnlUtilities;
import chartview.util.grib.GribHelper;
import chartview.util.http.HTTPClient;

import coreutilities.Utilities;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
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
  @SuppressWarnings("compatibility:6828784390015466233")
  private static final long serialVersionUID = -6756364686697947626L;
  
  private static final String FRAME_BASE_TITLE = WWGnlUtilities.buildMessage("product-name");
  private static final SimpleDateFormat SDF_FOR_TITLE = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
  
  private BorderLayout borderLayout;

  private FileTypeHolder allJTrees = new FileTypeHolder();
  
  private JLayeredPane layers = new JLayeredPane()
    {
      @Override
      public void paint(Graphics g)
      {
        super.paint(g);
        masterTabPane.setBounds(0, 0, this.getWidth(), this.getHeight());
        grayTransparentPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
      }
    };

  private JTabbedPane masterTabPane = new JTabbedPane();
  private String message2Display = "";
  private JPanel grayTransparentPanel = new JPanel()
    {
      @Override
      public void paintComponent(Graphics g)
      {
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                         RenderingHints.VALUE_TEXT_ANTIALIAS_ON);      
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                         RenderingHints.VALUE_ANTIALIAS_ON);      
        this.setOpaque(false);      
        this.setSize(masterTabPane.getSize());
        ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
        
//      g.setColor(Color.LIGHT_GRAY);
//      g.setColor(Color.GRAY);
        Color startColor = Color.GRAY; // new Color(255, 255, 255);
        Color endColor   = Color.BLACK;  // new Color(102, 102, 102);
        GradientPaint gradient = new GradientPaint(0, this.getHeight(), startColor, 0, 0, endColor); // vertical, upside down
        ((Graphics2D)g).setPaint(gradient);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        
//      g.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 50));
        g.setFont(g.getFont().deriveFont(Font.ITALIC | Font.BOLD, 50f));
        String str = message2Display; // WWGnlUtilities.buildMessage("loading");
        int strWidth = g.getFontMetrics(g.getFont()).stringWidth(str);
        g.setColor(Color.GRAY);
        g.drawString(str, (this.getWidth() / 2) - (strWidth / 2) + 3, 20 + g.getFont().getSize() + 3); // Shadow
        g.setColor(Color.RED);
        g.drawString(str, (this.getWidth() / 2) - (strWidth / 2), 20 + g.getFont().getSize());         // Text
      }
    };

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
  private SaveCompositeAsAction scaa = null;
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
//private JCheckBoxMenuItem menuStartNMEA = new JCheckBoxMenuItem();
  private JMenuItem menuStartNMEA = new JMenuItem();
  private JMenuItem manualNMEA = new JMenuItem();

  private JMenu menuHelp = new JMenu();
  private JMenuItem menuHelpAbout = new JMenuItem();
//private JMenuItem menuHelpContent = new JMenuItem();
  private JMenuItem menuHelpContact = new JMenuItem();
  private JMenuItem menuCheckForUpdate = new JMenuItem();
  private JMenuBar menuBar = new JMenuBar();

  private JSplitPane jSplitPane = null;
  
  private CompositeDetailsInputPanel inputPanel = null;
  private PositionInputPanel pip = null;

  protected AdjustFrame instance = this;
  
  public AdjustFrame()
  {
    borderLayout = new BorderLayout();
    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      WWContext.getInstance().fireExceptionLogging(e);
      e.printStackTrace();
    }
    // Dispose the splash screen here
    WWContext.getInstance().fireApplicationLoaded();
    
    // Any composite to load at startup?
    String displayComposite = System.getProperty("display.composite", "");
    if (displayComposite.trim().length() == 0)
    {
      final String compositeName = ((ParamPanel.DataFile) ParamPanel.data[ParamData.LOAD_COMPOSITE_STARTUP][1]).toString();
      if (compositeName.trim().length() > 0)
      {
        askAndWaitForLoadAtStartup(compositeName, 30); //  TODO, the 30 as a preference
      }
    }
    else
    {
      // Display Composite from File System
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().restoreComposite(displayComposite);
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
  private transient OscillateThread oscillate = null;
  
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
  
  private transient Object grayLayerIndex = new Integer(2);
  
  private void jbInit() throws Exception
  {
    WWContext.getInstance().setMasterTopFrame(this);
    
    layers.add(masterTabPane, new Integer(1));
//  layers.add(grayTransparentPanel, grayLayerIndex);
    
    this.setIconImage(new ImageIcon(this.getClass().getResource("img/paperboat.png")).getImage());
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    final CompositeTabbedPane firstTab = new CompositeTabbedPane();
    masterTabPane.add("Composite", firstTab);  // The first tab
//  masterTabPane.setToolTipTextAt(0, "Right-click to close");
    masterTabPane.setTabComponentAt(0, new CompositeTabComponent("Composite", "right/remove_composite.png")
      {
        public void onClose()
        {
          if (masterTabPane.getTabCount() > 2)
          {
            masterTabPane.remove(0);          
            firstTab.removeListener();
            int newIndex = 0 - 1;
            while (newIndex < 0) newIndex++;
            masterTabPane.setSelectedIndex(newIndex);
          }
        }
        public boolean ok2Close()
        {
          return masterTabPane.getTabCount() > 2;
        }
      });
    
    Icon plus = new ImageIcon(this.getClass().getResource("img/plus.png"));
    masterTabPane.add(new JPanel(), plus); // The tab that adds tabs   
    masterTabPane.setToolTipTextAt(1, WWGnlUtilities.buildMessage("click-to-add"));
    
    masterTabPane.addMouseListener(new MouseAdapter()
     {
       public void mouseClicked(MouseEvent mouseEvent)
       {
         int mask = mouseEvent.getModifiers();       
    //       System.out.println("Mouse clicked on the tabbedPane");
         if (mouseEvent.isConsumed())
         {
           return;
         }
         if (mouseEvent.getClickCount() == 2)
         {
           System.out.println("Exploding!");
         }
         if ((mask & MouseEvent.BUTTON2_MASK) != 0 || (mask & MouseEvent.BUTTON3_MASK) != 0) // Right click
         {
           System.out.println("Right-Click on Tab " + ((JTabbedPane)mouseEvent.getSource()).getSelectedIndex());
           mouseEvent.consume(); // Trap
           // Remove the tab
           if (false)
           {
             int selectedIndex = ((JTabbedPane)mouseEvent.getSource()).getSelectedIndex();
             if (((JTabbedPane)mouseEvent.getSource()).getTabCount() > 2 && selectedIndex < ((JTabbedPane)mouseEvent.getSource()).getTabCount() - 1)
             {
               JTabbedPane tp = (JTabbedPane)mouseEvent.getSource();             
               ((CompositeTabbedPane)tp.getSelectedComponent()).removeListener();
               tp.remove(selectedIndex);          
               int newIndex = selectedIndex - 1;
               while (newIndex < 0) newIndex++;
               tp.setSelectedIndex(newIndex);
             }
             else
               ((JTabbedPane)mouseEvent.getSource()).setSelectedIndex(0);
           }
         }
         else // Usual left-click
         {
    //     System.out.println("Click on Tab " + ((JTabbedPane)mouseEvent.getSource()).getSelectedIndex());
           if (((JTabbedPane)mouseEvent.getSource()).getSelectedIndex() == ((JTabbedPane)mouseEvent.getSource()).getTabCount() - 1) // Last
           {
             addCompositeTab();
           }
         }
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
    scaa = new SaveCompositeAsAction();
    menuFile.add(scaa).setAccelerator(KeyStroke.getKeyStroke("alt S"));
    scaa.setEnabled(false);
    menuFile.add(menuFileRestore);
    menuFile.add(menuFileRestoreFromURL);
    menuFile.add(new JSeparator());
    menuFile.add(menuFileCreatePattern);
    menuFile.add(menuFileLoadFromPattern);
    menuFile.add(new JSeparator());
//  menuFile.add(menuFileExit);
    menuFile.add(new ExitAction()).setAccelerator(KeyStroke.getKeyStroke("alt F4"));
    
    menuBar.add(menuFile);
    WWGnlUtilities.setLabelAndMnemonic("file", menuFile);
//  String label = WWGnlUtilities.buildMessage("file");
//  menuFile.setText(label);
//  menuFile.setMnemonic('F');
    
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
    
//  menuDownload.setText(WWGnlUtilities.buildMessage("download"));
    WWGnlUtilities.setLabelAndMnemonic("download", menuDownload);
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
//  menuTools.setText(WWGnlUtilities.buildMessage("tools"));
    WWGnlUtilities.setLabelAndMnemonic("tools", menuTools);
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
            WWGnlUtilities.generateImagesFromComposites(compositeDir, ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel());
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
              WWContext.getInstance().fireSetLoading(false, "Detecting..."); // LOCALIZE
                }
              };
          WWContext.getInstance().fireSetLoading(true, "Detecting..."); // LOCALIZE
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
    showGRIBPointData.setSelected(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isShowGRIBPointPanel());
    showGRIBPointData.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setShowGRIBPointPanel(showGRIBPointData.isSelected());
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
            if (((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getFrom() != null && 
                ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getTo() != null && 
                ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getGribData() != null)
            {
  //          int resp = JOptionPane.showConfirmDialog(null, "Start routing computation?", "Routing", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
  //          if (resp == JOptionPane.OK_OPTION)
              {
                ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().calculateRouting();
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
            if (((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getFrom() != null && 
//              ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getTo() != null && 
                ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getGribData() != null)
            {
  //          int resp = JOptionPane.showConfirmDialog(null, "Start routing computation?", "Routing", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
  //          if (resp == JOptionPane.OK_OPTION)
              {
                ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().whatIfRouting();
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
            if (((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getFrom() != null && 
                ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getTo() != null)
            {
              if (Math.abs(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getFrom().getL()) < 30D || 
                  Math.abs(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getTo().getL()) < 30D)
              {
                 JOptionPane.showMessageDialog(instance, WWGnlUtilities.buildMessage("under-30"), WWGnlUtilities.buildMessage("geostrophic-wind"), 
                                               JOptionPane.ERROR_MESSAGE); 
              }
              else
              {
                // Get the distance
                WWContext.getInstance().getGreatCircle().setStart(new GeoPoint(Math.toRadians(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getFrom().getL()), Math.toRadians(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getFrom().getG())));
                WWContext.getInstance().getGreatCircle().setArrival(new GeoPoint(Math.toRadians(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getTo().getL()), Math.toRadians(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getTo().getG())));                
                double gcDist = Math.toDegrees(WWContext.getInstance().getGreatCircle().getDistance()) * 60.0;

                int interval = ((Integer) ParamPanel.data[ParamData.INTERVAL_BETWEEN_ISOBARS][1]).intValue();
                double gws = WWGnlUtilities.getGeostrophicWindSpeed(gcDist, Math.abs((((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getFrom().getL() + ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getTo().getL())/ 2D), interval);
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
//          WWContext.getInstance().fireNMEAAcquisition(menuStartNMEA.isSelected());
            WWContext.getInstance().fireNMEAAcquisition(true);
          }
        });
    manualNMEA.setText(WWGnlUtilities.buildMessage("manual"));       
    manualNMEA.setIcon(new ImageIcon(this.getClass().getResource("img/grab.png")));
    manualNMEA.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent ae)
          {
            WWGnlUtilities.getManualBoatPosition();
          }
        });

    menuBar.add(menuHelp);
//  menuHelp.setText(WWGnlUtilities.buildMessage("help"));
    WWGnlUtilities.setLabelAndMnemonic("help", menuHelp);
    menuHelp.add(menuHelpAbout);
//  menuHelpAbout.setText(WWGnlUtilities.buildMessage("about"));
    WWGnlUtilities.setLabelAndMnemonic("about", menuHelpAbout);
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
          SDF_FOR_TITLE.setTimeZone(TimeZone.getDefault());
          Date ut = TimeUtil.getGMT();
          WWContext.getInstance().setCurrentUTC(ut);
          String title = FRAME_BASE_TITLE + " - UTC: " + SDF_FOR_TITLE.format(ut);
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

//  jSplitPane.setRightComponent(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel());
//  jSplitPane.setLeftComponent(allJTrees);
//  jSplitPane.add(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel(), JSplitPane.RIGHT);
//  jSplitPane.add(allJTrees, JSplitPane.LEFT);

    WWGnlUtilities.setTreeConfig(allJTrees);
    
//  jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, allJTrees, masterTabPane);
//  layers.setPreferredSize(new Dimension(800, 600));
    jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, allJTrees, layers);
    jSplitPane.setContinuousLayout(true);
    jSplitPane.setOneTouchExpandable(true);
//  jSplitPane.setOneTouchExpandable(false);
    jSplitPane.setDividerSize(8);
    jSplitPane.setDividerLocation(175);

    this.getContentPane().add(jSplitPane, BorderLayout.CENTER);
    
    WWContext.getInstance().addApplicationListener(new ApplicationEventListener()
      {                                                     
        public String toString()
        {
          return "from AdjustFrame.";
        }
        
        @Override
        public void compositeFileOpen(final String fileName) // fileName : full path to the file
        {
          String justFileName = fileName; 
          if (fileName.lastIndexOf(File.separatorChar) > 0)
            justFileName = justFileName.substring(fileName.lastIndexOf(File.separatorChar) + 1);        
//        System.out.println("JustFileName:" + justFileName + " (idx:" + fileName.lastIndexOf(File.separatorChar) + ")");
          masterTabPane.setTitleAt(masterTabPane.getSelectedIndex(), justFileName);
          ((CompositeTabComponent)masterTabPane.getTabComponentAt(masterTabPane.getSelectedIndex())).setTabTitle(justFileName);
//        ((CompositeTabComponent)masterTabPane.getTabComponentAt(masterTabPane.getSelectedIndex())).setToolTipText(JTreeFilePanel.getCompositeBubble(justFileName, fileName));
          masterTabPane.setToolTipTextAt(masterTabPane.getSelectedIndex(), JTreeFilePanel.getCompositeBubble(justFileName, fileName));
          // Activate save as menu item
          scaa.setEnabled(true);
        }
        
        @Override
        public void store()
        {
          // Activate save as menu item
          scaa.setEnabled(true);
        }

        @Override
        public void setStatus(String str)
        {
          setStatusLabel(str);
        }

        @Override
        public void setLoading(boolean b, String mess)
        {
          message2Display = mess;
          setLoadingProgresssBar(b, mess);
          if (b)
            layers.add(grayTransparentPanel, grayLayerIndex); // Add gray layer
          else
            layers.remove(grayTransparentPanel);              // remove gray layer
          layers.repaint();
        }
        
        @Override
        public void progressing(String mess)
        {
//        System.out.println("... Progressing : " + mess + ", grayTransparentPanel is " + (grayTransparentPanel.isVisible()?"":"not ") + "visible");
          message2Display = mess;
          if (!grayTransparentPanel.isVisible())
            layers.add(grayTransparentPanel, grayLayerIndex); // Add gray layer
          layers.repaint();
        }
      });
  }

  @Override
  public void repaint()
  {
    layers.repaint();
  }
  
  public void addCompositeTab()
  {
    final CompositeTabbedPane nctp = new CompositeTabbedPane();
    masterTabPane.add(nctp, "Composite (" + masterTabPane.getTabCount() + ")", masterTabPane.getTabCount() - 1);
    masterTabPane.setTabComponentAt(masterTabPane.getTabCount() - 2, new CompositeTabComponent("Composite (" + Integer.toString(masterTabPane.getTabCount() - 1) + ")", "right/remove_composite.png")
      {
        public void onClose()
        {
          if (masterTabPane.getTabCount() > 2)
          {
            nctp.removeListener();
            masterTabPane.remove(nctp);          
            int newIndex = masterTabPane.getTabCount() - 2;
            while (newIndex < 0) newIndex++;
            masterTabPane.setSelectedIndex(newIndex);
          }
        }
        public boolean ok2Close()
        {
          return masterTabPane.getTabCount() > 2;
        }
      });
    // masterTabPane.setToolTipTextAt(masterTabPane.getTabCount() - 2, "Right-click to close");
    masterTabPane.setSelectedIndex(masterTabPane.getTabCount() - 2);    
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
                ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().applyBoundariesChanges(pz.top, pz.bottom, pz.left, pz.right);
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

  // TODO Merge that one with the same method in CompositeTabbedPane
  private void setupComposite(String faxFile, String gribFile)
  {
    if (inputPanel == null)
      inputPanel = new CompositeDetailsInputPanel();

    FaxType[] faxarray = ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getFaxes();
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
    inputPanel.setPRMSL(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isDisplayPrmsl() && ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isTherePrmsl());
    inputPanel.set500mb(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isDisplay500mb() && ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isThere500mb());
    inputPanel.setWaves(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isDisplayWaves() && ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isThereWaves());
    inputPanel.setTemp(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isDisplayTemperature() && ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isThereTemperature());
    inputPanel.setPrate(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isDisplayRain() && ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isThereRain());
    
    inputPanel.set3DTWS(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isDisplay3DTws());
    inputPanel.set3DPRMSL(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isDisplay3DPrmsl());
    inputPanel.set3D500hgt(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isDisplay3D500mb());
    inputPanel.set3DWaves(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isDisplay3DWaves());
    inputPanel.set3DTemp(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isDisplay3DTemperature());
    inputPanel.set3DRain(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isDisplay3DRain());
    
    inputPanel.setPRMSLContour(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isDisplayContourPRMSL());
    inputPanel.set500mbContour(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isDisplayContour500mb());
    inputPanel.setWavesContour(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isDisplayContourWaves());
    inputPanel.setTempContour(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isDisplayContourTemp());
    inputPanel.setPrateContour(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().isDisplayContourPrate());
    
    inputPanel.setComment(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getCurrentComment());

    inputPanel.setTopLat(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getNLat());
    inputPanel.setBottomLat(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getSLat());
    inputPanel.setLeftLong(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getWLong());
    inputPanel.setRightLong(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getELong());
    int resp = JOptionPane.showConfirmDialog(this, 
                                             inputPanel, WWGnlUtilities.buildMessage("set-data-context"), 
                                             JOptionPane.OK_CANCEL_OPTION, 
                                             JOptionPane.PLAIN_MESSAGE);
    if (resp == JOptionPane.OK_OPTION)
    {
      final boolean gribChanged =  haveGRIBOtpionsChanged(inputPanel, ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel());
      
      boolean[] gribOptions = inputPanel.getGRIBOptions();

      boolean atLeastOne3D = gribOptions[CompositeDetailsInputPanel.TWS_3D] ||
                             gribOptions[CompositeDetailsInputPanel.PRMSL_3D] ||
                             gribOptions[CompositeDetailsInputPanel.MB500_3D] ||
                             gribOptions[CompositeDetailsInputPanel.WAVES_3D] ||
                             gribOptions[CompositeDetailsInputPanel.TEMP_3D] ||
                             gribOptions[CompositeDetailsInputPanel.PRATE_3D];
      boolean atLeastOneContour = gribOptions[CompositeDetailsInputPanel.TWS_CONTOUR] ||
                                  gribOptions[CompositeDetailsInputPanel.PRMSL_CONTOUR] ||
                                  gribOptions[CompositeDetailsInputPanel.MB500_CONTOUR] ||
                                  gribOptions[CompositeDetailsInputPanel.WAVES_CONTOUR] ||
                                  gribOptions[CompositeDetailsInputPanel.TEMP_CONTOUR] ||
                                  gribOptions[CompositeDetailsInputPanel.PRATE_CONTOUR];
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
          gribOptions[CompositeDetailsInputPanel.TWS_CONTOUR] = false;
          gribOptions[CompositeDetailsInputPanel.PRMSL_CONTOUR] = false;
          gribOptions[CompositeDetailsInputPanel.MB500_CONTOUR] = false;
          gribOptions[CompositeDetailsInputPanel.WAVES_CONTOUR] = false;
          gribOptions[CompositeDetailsInputPanel.TEMP_CONTOUR]  = false;
          gribOptions[CompositeDetailsInputPanel.PRATE_CONTOUR] = false;
        }
      }
//    ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplayContour(withContourLines);
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setCurrentComment(inputPanel.getComment());
      
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplayPrmsl(gribOptions[CompositeDetailsInputPanel.PRMSL_DATA]);
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplay500mb(gribOptions[CompositeDetailsInputPanel.MB500_DATA]);
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplayWaves(gribOptions[CompositeDetailsInputPanel.WAVES_DATA]);
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplayTemperature(gribOptions[CompositeDetailsInputPanel.TEMP_DATA]);
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplayRain(gribOptions[CompositeDetailsInputPanel.PRATE_DATA]);
      
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplay3DTws(gribOptions[CompositeDetailsInputPanel.TWS_3D]);
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplay3DPrmsl(gribOptions[CompositeDetailsInputPanel.PRMSL_3D]);
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplay3D500mb(gribOptions[CompositeDetailsInputPanel.MB500_3D]);
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplay3DWaves(gribOptions[CompositeDetailsInputPanel.WAVES_3D]);
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplay3DTemperature(gribOptions[CompositeDetailsInputPanel.TEMP_3D]);
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplay3DRain(gribOptions[CompositeDetailsInputPanel.PRATE_3D]);
      if (atLeastOne3D)
      {
        ((JTabbedPane)masterTabPane.getSelectedComponent()).setEnabledAt(1, true);  
        if (((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getGribData() != null)
          ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getThreeDGRIBPanel().getThreeDPanel().setPanelLabel(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getGribData()[((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getGribIndex()].getDate().toString());
      }
      else
      {
        if (((JTabbedPane)masterTabPane.getSelectedComponent()).getSelectedIndex() == 1)
          ((JTabbedPane)masterTabPane.getSelectedComponent()).setSelectedIndex(0);
        ((JTabbedPane)masterTabPane.getSelectedComponent()).setEnabledAt(1, false);
      }
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplayContourTWS(gribOptions[CompositeDetailsInputPanel.TWS_CONTOUR]);
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplayContourPRMSL(gribOptions[CompositeDetailsInputPanel.PRMSL_CONTOUR]);
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplayContour500mb(gribOptions[CompositeDetailsInputPanel.MB500_CONTOUR]);
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplayContourWaves(gribOptions[CompositeDetailsInputPanel.WAVES_CONTOUR]);
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplayContourTemp(gribOptions[CompositeDetailsInputPanel.TEMP_CONTOUR]);
      ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setDisplayContourPrate(gribOptions[CompositeDetailsInputPanel.PRATE_CONTOUR]);

      // GPX Data?
      if (inputPanel.thereIsGPXData())
      {
        try
        {
          String gpxDataFileName = inputPanel.getGPXFileName();
          long to = -1L;
          Date date = inputPanel.getUpToDate();
          if (date != null)
            to = date.getTime();
          ArrayList<GeoPoint> algp = GPXUtil.parseGPXData(new File(gpxDataFileName).toURI().toURL(), -1L, to);
          ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setGPXData(algp);
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
        }
      }
      else
        ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setGPXData(null);
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
              ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setNLat(inputPanel.getTopLat());
              ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setSLat(inputPanel.getBottomLat());
              ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setWLong(inputPanel.getLeftLong());
              ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setELong(inputPanel.getRightLong());
              ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().applyBoundariesChanges();
            }
            if (grib.trim().length() > 0)
            {    
              boolean keepGoing = true;
              GribHelper.GribConditionData[] wgd = null;
              if (((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getGribData() != null && 
                  inputPanel.isGRIBRequestSelected() &&
                  grib.equals(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getGRIBDataName()))
              {
                // Ask if we reload the GRIB (if the GRIB does not come from a waz)
                String ccName = WWContext.getInstance().getCurrentComposite();
                if (ccName != null && ccName.endsWith(WWContext.WAZ_EXTENSION) && !gribChanged)
                {
                  wgd = ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getGribData();
                  keepGoing = false;
                }
                if (keepGoing)
                {
                  if (ccName != null && ccName.endsWith(WWContext.WAZ_EXTENSION) && gribChanged)
                  {
                    wgd = ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getGribData();
                    keepGoing = false;
                  }
                  else
                  {
                    int resp = JOptionPane.showConfirmDialog(((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel(), 
                                                             WWGnlUtilities.buildMessage("reload-grib-data"), 
                                                             WWGnlUtilities.buildMessage("grib-download"), 
                                                             JOptionPane.YES_NO_OPTION, 
                                                             JOptionPane.QUESTION_MESSAGE);
                    if (resp == JOptionPane.NO_OPTION)
                    {
                      wgd = ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getGribData();
                      keepGoing = false;
                    }
                  }
                }
              }
              if (keepGoing)
              {
                if (inputPanel.isGRIBRequestSelected()) // Then we assume it is to be reached through http
                {
                  ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setGribRequest(grib); 
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
                {
                  try
                  {
                    wgd = GribHelper.getGribData(grib);  // From a File
                  }
                  catch (RuntimeException rte)
                  {
                    String mess = rte.getMessage();
//                  System.out.println("RuntimeException getMessage(): [" + mess + "]");
                    if (mess.startsWith("DataArray (width) size mismatch"))
                      System.out.println(mess);
                    else
                      throw rte;
                  }
                  catch (Exception e)
                  {
                    e.printStackTrace();
                  }
                }
              }  
              if (inputPanel.isSizeFromGRIB() && wgd != null)
              {
                ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setNLat(wgd[0].getNLat());
                ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setSLat(wgd[0].getSLat());
                ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setWLong(wgd[0].getWLng());
                ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setELong(wgd[0].getELng());
              }
              ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().applyBoundariesChanges();
              ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setGribData(wgd, 
                                       grib); // Event sent, generates obj, etc.
            }
  //        System.out.println("Setting command panel boundaries:" + ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getNLat() + " to " + ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getSLat() + ", and " + ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getWLong() + " to " + ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().getELong());

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
              ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().setFaxes(faxes);
            }

  //        System.out.println("End of loader thread.");
          WWContext.getInstance().fireSetLoading(false);
          }
        };
      loader.start();
    }
  }

  private boolean haveGRIBOtpionsChanged(CompositeDetailsInputPanel ip, CommandPanel cp)
  {
    boolean[] gribOptions = ip.getGRIBOptions();
    return (gribOptions[CompositeDetailsInputPanel.PRMSL_DATA] != cp.isDisplayPrmsl() ||
            gribOptions[CompositeDetailsInputPanel.MB500_DATA] != cp.isDisplay500mb() ||
            gribOptions[CompositeDetailsInputPanel.WAVES_DATA] != cp.isDisplayWaves() ||
            gribOptions[CompositeDetailsInputPanel.TEMP_DATA] != cp.isDisplayTemperature() ||
            gribOptions[CompositeDetailsInputPanel.PRATE_DATA] != cp.isDisplayRain() ||
            gribOptions[CompositeDetailsInputPanel.TWS_3D] != cp.isDisplay3DTws() ||
            gribOptions[CompositeDetailsInputPanel.PRMSL_3D] != cp.isDisplay3DPrmsl() || 
            gribOptions[CompositeDetailsInputPanel.MB500_3D] != cp.isDisplay3D500mb() ||
            gribOptions[CompositeDetailsInputPanel.WAVES_3D] != cp.isDisplay3DWaves() ||
            gribOptions[CompositeDetailsInputPanel.TEMP_3D] != cp.isDisplay3DTemperature() ||
            gribOptions[CompositeDetailsInputPanel.PRATE_3D] != cp.isDisplay3DRain() ||
            gribOptions[CompositeDetailsInputPanel.TWS_CONTOUR] != cp.isDisplayContourTWS() ||
            gribOptions[CompositeDetailsInputPanel.PRMSL_CONTOUR] != cp.isDisplayContourPRMSL() ||
            gribOptions[CompositeDetailsInputPanel.MB500_CONTOUR] != cp.isDisplayContour500mb() ||
            gribOptions[CompositeDetailsInputPanel.WAVES_CONTOUR] != cp.isDisplayContourWaves() ||
            gribOptions[CompositeDetailsInputPanel.TEMP_CONTOUR] != cp.isDisplayContourTemp() ||
            gribOptions[CompositeDetailsInputPanel.PRATE_CONTOUR] != cp.isDisplayContourPrate());
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
    ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().print();
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
    ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel().genImage();
  }

  void fileExit_ActionPerformed(ActionEvent e)
  {
    WWGnlUtilities.doOnExit(this);
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

  private void storeAs()
  {
    for (int i = 0; i < WWContext.getInstance().getListeners().size(); 
         i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.storeAs();
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
    return ((CompositeTabbedPane)masterTabPane.getSelectedComponent()).getCommandPanel();
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
      super(WWGnlUtilities.buildMessage("exit-no-acc"), new ImageIcon(instance.getClass().getResource("img/dummy.png")));      
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

  public class SaveCompositeAction extends AbstractAction
  {
    public SaveCompositeAction()
    {
      super(WWGnlUtilities.buildMessage("save-composite"), new ImageIcon(instance.getClass().getResource("img/save.png")));
    }

    public void actionPerformed(ActionEvent ae)
    {
      store();
    }
  }

  public class SaveCompositeAsAction extends AbstractAction
  {
    public SaveCompositeAsAction()
    {
      super(WWGnlUtilities.buildMessage("save-as-composite"), new ImageIcon(instance.getClass().getResource("img/save.png")));
    }

    public void actionPerformed(ActionEvent ae)
    {
      storeAs();
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
