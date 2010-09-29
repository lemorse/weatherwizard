package chartview.gui.util.dialog;


import chartview.ctx.WWContext;

import chartview.util.grib.GribHelper;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.text.DecimalFormat;

import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class GRIBSlicePanel
     extends JPanel
{
  private transient ArrayList<GribHelper.GribCondition> data2plot;
  private transient ArrayList<GribHelper.GribCondition> smoothedData;
  private JPanel checkBoxPanel = new JPanel();
  private JPanel checkBoxTopPanel = new JPanel();
  private JPanel smoothFactorPanel = new JPanel();
  
  public final static int DEFAULT_FORK_WIDTH = 75;
  private int forkWidth = DEFAULT_FORK_WIDTH;
  
  private GRIBSliceDataPanel dataPanel = new GRIBSliceDataPanel();
  
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JLabel dataLabel = new JLabel();
  private JCheckBox prmslCheckBox = new JCheckBox();
  private JCheckBox hgt500CheckBox = new JCheckBox();
  private JCheckBox twsCheckBox = new JCheckBox();
  private JCheckBox wavesCheckBox = new JCheckBox();
  private JCheckBox tempCheckBox = new JCheckBox();
  private JCheckBox rainCheckBox = new JCheckBox();
  
  private boolean displayTWS = true,
                  displayPRMSL = false,
                  displayHGT500 = false,
                  displayWAVES = false,
                  displayTEMP = false,
                  displayRAIN = false;
  private JLabel jLabel1 = new JLabel();
  private GridBagLayout gridBagLayout2 = new GridBagLayout();
  private JTextField smoothTextField = new JTextField();

  private DecimalFormat twsFormat    = new DecimalFormat("##0.0 'kts'");
  private DecimalFormat prmslFormat  = new DecimalFormat("##0.0 'mb'");
  private DecimalFormat hgt500Format = new DecimalFormat("##0 'm'");
  private DecimalFormat wavesFormat  = new DecimalFormat("##0.0 'm'"); 
  private DecimalFormat tempFormat   = new DecimalFormat("##0'°C'");
  private DecimalFormat prateFormat  = new DecimalFormat("##0.00 'mm/h'");

  public GRIBSlicePanel(ArrayList<GribHelper.GribCondition> data, int fw)
  {
    if (fw % 2 == 0)
    {
   // throw new RuntimeException("Fork Width must be odd");
      JOptionPane.showMessageDialog(this, "Fork width must be odd.\nUsing " + Integer.toString(fw + 1) + " instead of " + Integer.toString(fw), "Smoothing", JOptionPane.ERROR_MESSAGE);    
      forkWidth = fw + 1;
    }
    else
      forkWidth = fw;
    data2plot = data;
    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public GRIBSlicePanel(ArrayList<GribHelper.GribCondition> data)
  {
    data2plot = data;
    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void setData(ArrayList<GribHelper.GribCondition> data)
  {
    data2plot = data;
    computeData();
  }
  
  private transient GribHelper.GribCondition gribMini = null;
  private transient GribHelper.GribCondition gribMaxi = null;

  public int[] getPointFromD(double d) // d goes from 0 to 1
  {
    int size = data2plot.size();
    double dIdx = d * size;
    if (dIdx < 0) dIdx = 0;
    if (dIdx > data2plot.size() - 1) dIdx = data2plot.size() - 1;
    int x = data2plot.get((int)dIdx).vertIdx;
    int y = data2plot.get((int)dIdx).horIdx;
    
    return new int[] { x, y };
  }

  private void jbInit()
    throws Exception
  {
    this.setLayout(new BorderLayout());

    this.setSize(new Dimension(700, 190));
    this.setPreferredSize(new Dimension(700, 190));
    dataPanel.setLayout(null);
    dataPanel.setBackground(Color.white);
    dataPanel.setSize(new Dimension(640, 280));
    dataPanel.setPreferredSize(new Dimension(640, 280));

    dataLabel.setText("Data");
    dataLabel.setFont(new Font("Tahoma", 1, 11));
    prmslCheckBox.setText("PRMSL");
    prmslCheckBox.setBackground(Color.red);
    prmslCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          prmslCheckBox_actionPerformed(e);
        }
      });
    hgt500CheckBox.setText("HGT500");
    hgt500CheckBox.setBackground(Color.cyan);
    hgt500CheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          hgt500CheckBox_actionPerformed(e);
        }
      });
    twsCheckBox.setText("TWS");
    twsCheckBox.setBackground(Color.blue);
    twsCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          twsCheckBox_actionPerformed(e);
        }
      });
    wavesCheckBox.setText("WAVES");
    wavesCheckBox.setBackground(Color.green);
    wavesCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          wavesCheckBox_actionPerformed(e);
        }
      });
    tempCheckBox.setText("TEMP");
    tempCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          tempCheckBox_actionPerformed(e);
        }
      });
    rainCheckBox.setText("RAIN");
    rainCheckBox.setBackground(Color.gray);
    rainCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          rainCheckBox_actionPerformed(e);
        }
      });
    
    twsCheckBox.setSelected(displayTWS);
    prmslCheckBox.setSelected(displayPRMSL);
    hgt500CheckBox.setSelected(displayHGT500);
    wavesCheckBox.setSelected(displayWAVES);
    tempCheckBox.setSelected(displayTEMP);
    rainCheckBox.setSelected(displayRAIN);

    jLabel1.setText("Smooth");
    smoothTextField.setSize(new Dimension(30, 20));
    smoothTextField.setText(Integer.toString(forkWidth));
    smoothTextField.setPreferredSize(new Dimension(30, 20));
    smoothTextField.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
//        System.out.println("Action performed!");
          smoothTextField_actionPerformed(e);
        }
      });
    checkBoxPanel.setLayout(new BorderLayout());
    checkBoxTopPanel.setLayout(gridBagLayout1);
    smoothFactorPanel.setLayout(gridBagLayout2);
    checkBoxPanel.add(checkBoxTopPanel, BorderLayout.NORTH);
    smoothFactorPanel.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    smoothFactorPanel.add(smoothTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    checkBoxPanel.add(smoothFactorPanel, BorderLayout.SOUTH);

    this.add(dataPanel, BorderLayout.CENTER);
    this.add(checkBoxPanel, BorderLayout.EAST);

    checkBoxTopPanel.add(dataLabel,
                         new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                                                new Insets(0, 3, 3, 0), 0, 0));
    checkBoxTopPanel.add(twsCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(0, 3, 0, 0), 0, 0));
    checkBoxTopPanel.add(prmslCheckBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(0, 3, 0, 0), 0, 0));
    checkBoxTopPanel.add(hgt500CheckBox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(0, 3, 0, 0), 0, 0));
    checkBoxTopPanel.add(wavesCheckBox, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(0, 3, 0, 0), 0, 0));
    checkBoxTopPanel.add(tempCheckBox, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(0, 3, 0, 0), 0, 0));
    checkBoxTopPanel.add(rainCheckBox, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(0, 3, 0, 0), 0, 0));
    
    computeData();
  }

  private void computeData()
  {
    // Detect mini maxi
    gribMini = new GribHelper.GribCondition();
    gribMaxi = new GribHelper.GribCondition();
    gribMini.windspeed = Float.MAX_VALUE;  gribMaxi.windspeed = 0f;
    gribMini.winddir = 360;                gribMaxi.winddir = 0;
    gribMini.prmsl = Integer.MAX_VALUE;    gribMaxi.prmsl = 0;
    gribMini.waves = Integer.MAX_VALUE;    gribMaxi.waves = 0;
    gribMini.temp = Integer.MAX_VALUE;     gribMaxi.temp = Integer.MIN_VALUE;
    gribMini.hgt500 = Integer.MAX_VALUE;   gribMaxi.hgt500 = 0;
    gribMini.rain = Float.MAX_VALUE;       gribMaxi.rain = 0f;
    int nbNull = 0;
    for (GribHelper.GribCondition ghgc : data2plot) // Mini/Maxi
    {
      if (ghgc != null)
      {
        if (ghgc.windspeed < gribMini.windspeed) gribMini.windspeed = ghgc.windspeed;
        if (ghgc.winddir < gribMini.winddir) gribMini.winddir = ghgc.winddir;
        if (ghgc.prmsl < gribMini.prmsl) gribMini.prmsl = ghgc.prmsl;
        if (ghgc.waves < gribMini.waves) gribMini.waves = ghgc.waves;
        if (ghgc.temp < gribMini.temp) gribMini.temp = ghgc.temp;
        if (ghgc.hgt500 < gribMini.hgt500) gribMini.hgt500 = ghgc.hgt500;
        if (ghgc.rain < gribMini.rain) gribMini.rain = ghgc.rain;
        
        if (ghgc.windspeed > gribMaxi.windspeed) gribMaxi.windspeed = ghgc.windspeed;
        if (ghgc.winddir > gribMaxi.winddir) gribMaxi.winddir = ghgc.winddir;
        if (ghgc.prmsl > gribMaxi.prmsl) gribMaxi.prmsl = ghgc.prmsl;
        if (ghgc.waves > gribMaxi.waves) gribMaxi.waves = ghgc.waves;
        if (ghgc.temp > gribMaxi.temp) gribMaxi.temp = ghgc.temp;
        if (ghgc.hgt500 > gribMaxi.hgt500) gribMaxi.hgt500 = ghgc.hgt500;
        if (ghgc.rain > gribMaxi.rain) gribMaxi.rain = ghgc.rain;
      }
      else
        nbNull++;
    }
    //  System.out.println("Will plot " + data2plot.size() + " point(s), " + nbNull + " null(s).");
    smooth(forkWidth); 
  }
  
  private void smooth(int fork)  
  {
    if ((fork % 2) != 1)
    {
      JOptionPane.showMessageDialog(this, "Fork width must be odd", "Smoothing", JOptionPane.ERROR_MESSAGE);      
      throw new RuntimeException("Fork must be odd.");
    }
    smoothedData = new ArrayList<GribHelper.GribCondition>(data2plot.size());

    for (GribHelper.GribCondition cond : data2plot) // Clone the array
    {
      smoothedData.add(new GribHelper.GribCondition(cond.windspeed,
                                                    cond.winddir,
                                                    cond.hgt500,
                                                    cond.horIdx,
                                                    cond.vertIdx,
                                                    cond.prmsl,
                                                    cond.waves,
                                                    cond.temp,
                                                    cond.rain));
    }
    int halfFork = ((fork-1) / 2);
    for (int i=0; i<data2plot.size(); i++)    
    {
      double tws    = 0D;      
      double hgt500 = 0D;
      double prmsl  = 0D;
      double waves  = 0D;
      double temp   = 0D;
      double rain   = 0D;
      for (int j=(i-halfFork); j<=(i+halfFork); j++)
      {
        int _j = j;
        if (_j<0) 
          _j = 0;
        if (_j>=data2plot.size()) 
          _j = data2plot.size() - 1;
        tws    += data2plot.get(_j).windspeed;
        hgt500 += data2plot.get(_j).hgt500;
        prmsl  += data2plot.get(_j).prmsl;
        waves  += data2plot.get(_j).waves;
        temp   += data2plot.get(_j).temp;
        rain   += data2plot.get(_j).rain;
      }
      tws = tws / fork;
      hgt500 = hgt500 / fork;
      prmsl = prmsl / fork;
      waves = waves / fork;
      temp = temp / fork;
      rain = rain / fork;

      smoothedData.get(i).windspeed = (float)tws;      
      smoothedData.get(i).hgt500    = (float)hgt500;      
      smoothedData.get(i).prmsl     = (int)prmsl;      
      smoothedData.get(i).waves     = (int)waves;      
      smoothedData.get(i).temp      = (float)temp;      
      smoothedData.get(i).rain      = (float)rain;      
    }
//  return smoothed;    
  }
  
  private void twsCheckBox_actionPerformed(ActionEvent e)
  {
    displayTWS = twsCheckBox.isSelected();
    repaint();
  }

  private void prmslCheckBox_actionPerformed(ActionEvent e)
  {
    displayPRMSL = prmslCheckBox.isSelected();
    repaint();
  }

  private void hgt500CheckBox_actionPerformed(ActionEvent e)
  {
    displayHGT500 = hgt500CheckBox.isSelected();
    repaint();
  }

  private void wavesCheckBox_actionPerformed(ActionEvent e)
  {
    displayWAVES = wavesCheckBox.isSelected();
    repaint();
  }

  private void tempCheckBox_actionPerformed(ActionEvent e)
  {
    displayTEMP = tempCheckBox.isSelected();
    repaint();
  }

  private void rainCheckBox_actionPerformed(ActionEvent e)
  {
    displayRAIN = rainCheckBox.isSelected();
    repaint();
  }

  private void smoothTextField_actionPerformed(ActionEvent e)
  {
    try
    {
      int fw = Integer.parseInt(smoothTextField.getText());
      if (fw % 2 == 1)
      {
        forkWidth = fw;
        smooth(forkWidth);
        repaint();
      }
      else
      {
        JOptionPane.showMessageDialog(this, "Fork width must be odd", "Smoothing", JOptionPane.ERROR_MESSAGE);
        smoothTextField.setText(Integer.toString(forkWidth));
      }
    }
    catch (NumberFormatException nfe)
    {
      JOptionPane.showMessageDialog(this, nfe.toString(), "Smoothing", JOptionPane.ERROR_MESSAGE);
    }
  }

  public void setForkWidth(int forkWidth)
  {
    System.out.println("Setting forkWidth to " + forkWidth);
    if (forkWidth % 2 == 1)
    {
      this.forkWidth = forkWidth;
      smooth(forkWidth);
      repaint();
    }
    else
    {
      JOptionPane.showMessageDialog(this, "Fork width must be odd", "Smoothing", JOptionPane.ERROR_MESSAGE);      
    }
    smoothTextField.setText(Integer.toString(this.forkWidth));
    smoothTextField.repaint();
  }

  class GRIBSliceDataPanel extends JPanel
  {
    private int infoX = -1;
    
    public GRIBSliceDataPanel()
    {
      super();
      this.addMouseMotionListener(new MouseMotionListener()
        {
          public void mouseDragged(MouseEvent e)
          {
            int x = e.getPoint().x;
            infoX = x;
            double pos = (double)x / (double)getWidth();
            WWContext.getInstance().fireGRIBSliceInfoRequested(pos);
            repaint();
          }

          public void mouseMoved(MouseEvent e)
          {
          }
        });
      this.addMouseListener(new MouseListener()
        {
          public void mouseClicked(MouseEvent e)
          {
          }

          public void mousePressed(MouseEvent e)
          {
            int x = e.getPoint().x;
            infoX = x;
            double pos = (double)x / (double)getWidth();
            WWContext.getInstance().fireGRIBSliceInfoRequested(pos);
            repaint();
          }

          public void mouseReleased(MouseEvent e)
          {
            infoX = -1;
            WWContext.getInstance().fireGRIBSliceInfoRequestStop();
            repaint();
          }

          public void mouseEntered(MouseEvent e)
          {
          }

          public void mouseExited(MouseEvent e)
          {
          }
        });
    }
    
    public void paintComponent(Graphics gr)
    {
      gr.setColor(Color.white);
      gr.fillRect(0, 0, this.getWidth(), this.getHeight());
      // Horizontal lines
      gr.setColor(Color.lightGray);
      for (int i=0; i<this.getHeight(); i+=(this.getHeight()/10))
        gr.drawLine(0, i, this.getWidth(), i);

      Stroke origStroke = null;
      if (gr instanceof Graphics2D)
      {
        Graphics2D g2d = (Graphics2D)gr;
        origStroke = g2d.getStroke();
        Stroke stroke =  new BasicStroke(3, 
                                         BasicStroke.CAP_BUTT,
                                         BasicStroke.JOIN_BEVEL);
        g2d.setStroke(stroke);  
      }
      // Calculate scales...
      float windscale   = (float)this.getHeight() / gribMaxi.windspeed;
      float prmslscale  = (float)this.getHeight() / ((gribMaxi.prmsl / 100f) - (gribMini.prmsl / 100f));
      float hgt500scale = (float)this.getHeight() / ((gribMaxi.hgt500) - (gribMini.hgt500));
      float wavescale   = (float)this.getHeight() / (gribMaxi.waves / 100f);
      float tempscale   = (float)this.getHeight() / ((gribMaxi.temp) - (gribMini.temp));
      float rainscale   = (float)this.getHeight() / (gribMaxi.rain * 3600f);
      
      // Plot
      int gribIdx = 0, gribSize = data2plot.size();
      
      int prevXtws    = -1, prevYtws    = -1;
      int prevXprmsl  = -1, prevYprmsl  = -1;
      int prevXhgt500 = -1, prevYhgt500 = -1;
      int prevXwaves  = -1, prevYwaves  = -1;
      int prevXtemp   = -1, prevYtemp   = -1;
      int prevXrain   = -1, prevYrain   = -1;    
      
      for (GribHelper.GribCondition gribPoint : smoothedData)
      {
        if (gribPoint != null)
        {
          float tws   = gribPoint.windspeed;
          int twd     = gribPoint.winddir;
          float prmsl = gribPoint.prmsl / 100f; // hPa
          float hgt500  = gribPoint.hgt500; // m
          float waves = gribPoint.waves / 100F; // m
          float temp    = gribPoint.temp - 273; // Celcius
          float rain  = gribPoint.rain * 3600f; // kg/m^2/h => mm/h
          int x, y;
          x = (int)((float)gribIdx * (float)this.getWidth() / (float)gribSize);
          // TWS
          if (displayTWS)
          {
      //    System.out.println("Idx:" + gribIdx + ", x:" + x + " for w:" + this.getWidth() + " and gSize:" + gribSize);
            y = (int)(this.getHeight() - (tws * windscale));
            gr.setColor(Color.blue);
            if (prevXtws > -1 && prevYtws > -1)
              gr.drawLine(prevXtws, prevYtws, x, y);
            prevXtws = x;
            prevYtws = y;
          }
          // PRMSL
          if (displayPRMSL)
          {
            y = (int)(this.getHeight() - ((prmsl - (gribMini.prmsl / 100f))* prmslscale));
            gr.setColor(Color.red);
            if (prevXprmsl > -1 && prevYprmsl > -1)
              gr.drawLine(prevXprmsl, prevYprmsl, x, y);
            prevXprmsl = x;
            prevYprmsl = y;
          }
          // HGT500
          if (displayHGT500)
          {
            y = (int)(this.getHeight() - ((hgt500 - (gribMini.hgt500))* hgt500scale));
            gr.setColor(Color.cyan);
            if (prevXhgt500 > -1 && prevYhgt500 > -1)
              gr.drawLine(prevXhgt500, prevYhgt500, x, y);
            prevXhgt500 = x;
            prevYhgt500 = y;        
          }
          // WAVES
          if (displayWAVES)
          {
            y = (int)(this.getHeight() - (waves * wavescale));
            gr.setColor(Color.green);
            if (prevXwaves > -1 && prevYwaves > -1)
              gr.drawLine(prevXwaves, prevYwaves, x, y);
            prevXwaves = x;
            prevYwaves = y;        
          }
          // TEMP
          if (displayTEMP)
          {
            y = (int)(this.getHeight() - ((temp - (gribMini.temp - 273))* tempscale));
            gr.setColor(Color.black);
            if (prevXtemp > -1 && prevYtemp > -1)
              gr.drawLine(prevXtemp, prevYtemp, x, y);
            prevXtemp = x;
            prevYtemp = y;                
          }
          // RAIN
          if (displayRAIN)
          {
            y = (int)(this.getHeight() - ((rain - (gribMini.rain * 3600f))* rainscale));
            gr.setColor(Color.gray);
            if (prevXrain > -1 && prevYrain > -1)
              gr.drawLine(prevXrain, prevYrain, x, y);
            prevXrain = x;
            prevYrain = y;                
          }
        }            
        gribIdx++;
      }   
      if (infoX != -1)
      {
        gr.setColor(Color.gray);
        gr.drawLine(infoX, 0, infoX, this.getHeight());

        int dataIdx = (int)((float)infoX * (float)gribSize / (float)this.getWidth());
//      System.out.println("GRIB Idx:" + dataIdx + " pour x:" + infoX + " / " + this.getWidth());
//      GribHelper.GribCondition gribPoint = data2plot.get(dataIdx);
        if (dataIdx > smoothedData.size() - 1) dataIdx = smoothedData.size() - 1;
        if (dataIdx < 0) dataIdx = 0;
        GribHelper.GribCondition gribPoint = smoothedData.get(dataIdx);
        if (displayTWS)
        {
          int y = (int)(this.getHeight() - (gribPoint.windspeed * windscale));
          postit(gr, " " + twsFormat.format(gribPoint.windspeed), infoX, y, Color.yellow, Color.blue, 0.75f);
        }
        if (displayPRMSL)
        {
          int y = (int)(this.getHeight() - (((gribPoint.prmsl / 100f) - (gribMini.prmsl / 100f))* prmslscale));
          postit(gr, " " + prmslFormat.format(gribPoint.prmsl / 100f), infoX, y, Color.yellow, Color.red, 0.75f);
        }
        if (displayHGT500)
        {
          int y = (int)(this.getHeight() - ((gribPoint.hgt500 - (gribMini.hgt500))* hgt500scale));
          postit(gr, " " + hgt500Format.format(gribPoint.hgt500), infoX, y, Color.yellow, Color.cyan, 0.75f);
        }
        if (displayWAVES)
        {
          int y = (int)(this.getHeight() - ((gribPoint.waves / 100f) * wavescale));
          postit(gr, " " + wavesFormat.format(gribPoint.waves / 100f), infoX, y, Color.yellow, Color.green, 0.75f);
        }
        if (displayTEMP)
        {
          int y = (int)(this.getHeight() - (((gribPoint.temp - 273) - (gribMini.temp - 273))* tempscale));
          postit(gr, " " + tempFormat.format(gribPoint.temp - 273), infoX, y, Color.yellow, Color.black, 0.75f);
        }
        if (displayRAIN)
        {
          int y = (int)(this.getHeight() - (((gribPoint.rain * 3600f) - (gribMini.rain * 3600f))* rainscale));
          postit(gr, " " + prateFormat.format(gribPoint.rain * 3600f), infoX, y, Color.yellow, Color.gray, 0.75f);
        }
      }

      if (origStroke != null)
        ((Graphics2D)gr).setStroke(origStroke);  
    }

    public void postit(Graphics g, String s, int x, int y, Color bgcolor, Color fgcolor, Float transp)
    {
      int bevel = 2;
      int postitOffset = 5;
      
      int startX = x;
      int startY = y;
      
      Color origin = g.getColor();
      g.setColor(fgcolor);
      Font f = g.getFont();
      int nbCr = 0;
      int crOffset;
      for (crOffset = 0; (crOffset = s.indexOf("\n", crOffset) + 1) > 0;)
        nbCr++;

      String txt[] = new String[nbCr + 1];
      int i = 0;
      crOffset = 0;
      for (i = 0; i < nbCr; i++)
        txt[i] = s.substring(crOffset, (crOffset = s.indexOf("\n", crOffset) + 1) - 1);

      txt[i] = s.substring(crOffset);
      int strWidth = 0;
      for (i = 0; i < nbCr + 1; i++)
      {
        if (g.getFontMetrics(f).stringWidth(txt[i]) > strWidth)
          strWidth = g.getFontMetrics(f).stringWidth(txt[i]);
      }
      Color c = g.getColor();
      g.setColor(bgcolor);
      if (g instanceof Graphics2D)
      {
        // Transparency
        Graphics2D g2 = (Graphics2D)g;
        float alpha = (transp!=null?transp.floatValue():0.3f);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
      }
      // left or right, up or down...
      Point topRightExtremity      = new Point(x + postitOffset + strWidth + (2 * bevel), (y - f.getSize()) + 1);
      Point bottomRightExtremity   = new Point(x + postitOffset + strWidth + (2 * bevel), (nbCr + 1) * f.getSize());
      Point bottomLeftExtremity    = new Point(x, y + ((nbCr + 1) * f.getSize()));
      
      if (!this.getVisibleRect().contains(topRightExtremity) && !this.getVisibleRect().contains(bottomRightExtremity))   
      {
        // This display left
        startX = x - strWidth - (2 * bevel) - (2 * postitOffset);
      }
      if ((startY - f.getSize()) < 0)   
      {
        // This display down
    //  startY = y - ((nbCr + 1) * f.getSize());
        startY = y + ((nbCr + 1) * f.getSize());
    //  System.out.println("Up, y [" + y + "] becomes [" + startY + "]");
      }
      
      g.fillRect(startX + postitOffset, (startY - f.getSize()) + 1, strWidth + (2 * bevel), (nbCr + 1) * f.getSize());
      if (g instanceof Graphics2D)
      {
        // Reset Transparency
        Graphics2D g2 = (Graphics2D)g;
        float alpha = 1.0f;
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
      }
      if (fgcolor != null)
        g.setColor(fgcolor);
      else
        g.setColor(c);
      
      for(i = 0; i < nbCr + 1; i++)
        g.drawString(txt[i], startX + bevel + postitOffset, startY + (i * f.getSize()));

      g.setColor(origin);
    }
  }
}
