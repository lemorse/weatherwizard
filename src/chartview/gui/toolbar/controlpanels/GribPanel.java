package chartview.gui.toolbar.controlpanels;

import chartview.ctx.WWContext;
import chartview.ctx.ApplicationEventListener;

import chartview.util.WWGnlUtilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GribPanel
     extends JPanel
{
  private JButton forwardButton = new JButton();
  private JButton backwardButton = new JButton();
  private JButton animateButton = new JButton();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JLabel gribInfoLineTwo = new JLabel();
  private JLabel gribInfoLineThree = new JLabel();
  private JLabel gribInfoLineFour = new JLabel();
  private JPanel topPanel = new JPanel();

  private JPanel gribSmoothingPanel = new JPanel();
  private JTextField smoothValue = new JTextField("1");
  private JButton smoothButton = new JButton();
  private JLabel smoothLabel = new JLabel(WWGnlUtilities.buildMessage("grib-smooth"));
  private JPanel dataOptionsPanel = new JPanel();
  
  private JLabel windLabel  = new JLabel("WIND");
  private JLabel prmslLabel = new JLabel("PRMSL");
  private JLabel hgtLabel   = new JLabel("HGT");
  private JLabel tmpLabel   = new JLabel("AIRTMP");
  private JLabel waveLabel  = new JLabel("WAVES");
  private JLabel rainLabel  = new JLabel("RAIN");
  private JButton googleButton = new JButton();
  private JSlider gribSlider = new JSlider();
  
  private int sliderValue = 1;
  private JLabel googleLabel = new JLabel();
  private GridBagLayout gridBagLayout2 = new GridBagLayout();
  private JLabel smoothTimeLabel = new JLabel(WWGnlUtilities.buildMessage("grib-time-smooth"));
  private JTextField smoothTimeValue = new JTextField();
  private JButton smoothTimeButton = new JButton();

  public GribPanel()
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
    WWContext.getInstance().addApplicationListener(new ApplicationEventListener()
      {
        public String toString()
        {
          return "from GribPanel.";
        }
        public void gribLoaded()
        {
          googleButton.setEnabled(true);
          backwardButton.setEnabled(true);
//        animateButton.setEnabled(true); 
//        dustletLoopButton.setEnabled(true);
          forwardButton.setEnabled(true);
          gribInfoLineTwo.setEnabled(true);
          gribInfoLineThree.setEnabled(true);
          gribInfoLineFour.setEnabled(true);
          smoothLabel.setEnabled(true);
          smoothValue.setEnabled(true);
          smoothButton.setEnabled(true);
          smoothTimeLabel.setEnabled(true);
          smoothTimeValue.setEnabled(true);
          smoothTimeButton.setEnabled(true);
          gribSlider.setEnabled(true);
          googleLabel.setEnabled(true);
          smoothTimeValue.setText("1");
        }
        
        public void gribUnloaded()
        {          
          googleButton.setEnabled(false);
          backwardButton.setEnabled(false);
          animateButton.setEnabled(false);
//        dustletLoopButton.setEnabled(false);
          forwardButton.setEnabled(false);
          gribInfoLineTwo.setEnabled(false);
          gribInfoLineThree.setEnabled(false);
          gribInfoLineFour.setEnabled(false);
          smoothLabel.setEnabled(false);
          smoothValue.setEnabled(false);
          smoothButton.setEnabled(false);
          smoothTimeLabel.setEnabled(false);
          smoothTimeValue.setEnabled(false);
          smoothTimeButton.setEnabled(false);
          gribSlider.setEnabled(false);
          googleLabel.setEnabled(false);
          WWContext.getInstance().setGribFile(null);
        }
        
        public void setGribInfo(int currentIndex, 
                                int maxIndex, 
                                String s2, 
                                String s3, 
                                String s4,
                                boolean windOnly,
                                boolean b1, 
                                boolean b2, 
                                boolean b3, 
                                boolean b4, 
                                boolean b5,
                                boolean b6,
                                boolean displayWind,
                                boolean display3DPrmsl,
                                boolean display3D500hgt,
                                boolean display3DTemp,
                                boolean display3Dwaves,
                                boolean display3DRain) 
        {
          setGribInfoLabel(currentIndex, maxIndex, s2, s3, s4, b1, b2, b3, b4, b5, b6);
          sliderValue = currentIndex + 1;
          gribSlider.setMaximum(maxIndex);  
          gribSlider.setValue(sliderValue); 
        }
        
        public void startGribAnimation() 
        {
          forwardButton.setEnabled(false);
          backwardButton.setEnabled(false);
          animateButton.setToolTipText("Stop Animation");
        }
        
        public void stopGribAnimation() 
        {
          forwardButton.setEnabled(true);
          backwardButton.setEnabled(true);
          animateButton.setToolTipText("Start Animation");
        }
        
        public void thereIsMoreThanOneGrib() 
        {
          animateButton.setEnabled(true);
//        dustletLoopButton.setEnabled(true);          
        }
        
      });
    this.setSize(new Dimension(ChartControlPane.WIDTH, 270));
    this.setPreferredSize(new Dimension(ChartControlPane.WIDTH, 270));
    this.setMinimumSize(new Dimension(ChartControlPane.WIDTH, 270));
    this.setMaximumSize(new Dimension(1000, 1000));
    this.setLayout(gridBagLayout1);
    this.setEnabled(false);
    forwardButton.setIcon(new ImageIcon(this.getClass().getResource("img/panright.gif")));
    forwardButton.setPreferredSize(new Dimension(24, 24));
    forwardButton.setBorderPainted(false);
    forwardButton.setMaximumSize(new Dimension(24, 24));
    forwardButton.setMinimumSize(new Dimension(24, 24));
    forwardButton.setMargin(new Insets(1, 1, 1, 1));
    forwardButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            forwardButton_actionPerformed(e);
          }
        });
    backwardButton.setIcon(new ImageIcon(this.getClass().getResource("img/panleft.gif")));
    backwardButton.setPreferredSize(new Dimension(24, 24));
    backwardButton.setBorderPainted(false);
    backwardButton.setMaximumSize(new Dimension(24, 24));
    backwardButton.setMinimumSize(new Dimension(24, 24));
    backwardButton.setMargin(new Insets(1, 1, 1, 1));
    backwardButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            backwardButton_actionPerformed(e);
          }
        });
    animateButton.setIcon(new ImageIcon(this.getClass().getResource("img/refresh.png")));
    animateButton.setPreferredSize(new Dimension(24, 24));
    animateButton.setBorderPainted(false);
    animateButton.setToolTipText("Start Animation");
    animateButton.setMaximumSize(new Dimension(24, 24));
    animateButton.setMinimumSize(new Dimension(24, 24));
    animateButton.setMargin(new Insets(1, 1, 1, 1));
    animateButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            animateButton_actionPerformed(e);
          }
        });
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(backwardButton, null);
    buttonPanel.add(forwardButton, null);
    buttonPanel.add(animateButton, null);


    this.add(topPanel, 
             new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, 
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(gribSmoothingPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));

    this.add(buttonPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 4, 1, 0), 0, 0));
    this.add(gribInfoLineTwo, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(5, 5, 0, 0), 0, 0));
    this.add(gribInfoLineThree,
             new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(0, 5, 0, 0), 0, 0));
    this.add(gribInfoLineFour, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(0, 5, 10, 0), 0, 0));
    this.add(dataOptionsPanel, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));

    gribSlider.setMinimum(1);
    this.add(gribSlider, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 2, 0), 0, 0));
    gribSlider.addChangeListener(new ChangeListener()
        {
          public void stateChanged(ChangeEvent evt)
          {
            JSlider slider = (JSlider) evt.getSource();

            if (!slider.getValueIsAdjusting())
            {
              int sv = slider.getValue();
              if (sv != sliderValue)
              {
                sliderValue = sv;
//              System.out.println("Slider Value:" + sliderValue);
                updateSliderData();
              }
              slider.setToolTipText("Frame #" + Integer.toString(sv));
            }
          }
        });
                                    
    dataOptionsPanel.add(windLabel, null);
    dataOptionsPanel.add(prmslLabel, null);
    dataOptionsPanel.add(hgtLabel, null);
    dataOptionsPanel.add(tmpLabel, null);
    dataOptionsPanel.add(waveLabel, null);
    dataOptionsPanel.add(rainLabel, null);

    windLabel.setEnabled(false);
    prmslLabel.setEnabled(false);
    hgtLabel.setEnabled(false);
    tmpLabel.setEnabled(false);
    waveLabel.setEnabled(false);
    rainLabel.setEnabled(false);

    //  googleButton.setText("Google");
    googleButton.setIcon(new ImageIcon(this.getClass().getResource("img/google.png")));
    googleButton.setActionCommand("googleMap");
    googleButton.setToolTipText(WWGnlUtilities.buildMessage("show-wind-in-google-map"));
    googleButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            googleButton_actionPerformed(e);
          }
        });
    googleButton.setEnabled(false);
    googleButton.setMaximumSize(new Dimension(24, 24));
    googleButton.setMinimumSize(new Dimension(24, 24));
    googleButton.setPreferredSize(new Dimension(24, 24));
    googleButton.setMargin(new Insets(1, 1, 1, 1));

    gribSlider.setPaintTicks(true);
    gribSlider.setValue(1); // Was 0
    gribSlider.setMaximum(10);
    gribSlider.setMajorTickSpacing(1);
    gribSlider.setEnabled(false);
    gribSlider.setMinimum(1); // Was 0
    gribSlider.setSnapToTicks(true);
    googleLabel.setText(WWGnlUtilities.buildMessage("wind-in-google-map"));
    googleLabel.setEnabled(false);
    smoothTimeLabel.setEnabled(false);
    smoothTimeValue.setPreferredSize(new Dimension(30, 20));
    smoothTimeValue.setText("1");
    smoothTimeValue.setToolTipText(WWGnlUtilities.buildMessage("grib-time-smooth-tooltip"));
    smoothTimeValue.setHorizontalAlignment(JTextField.CENTER);
    smoothTimeValue.setEnabled(false);
    smoothTimeButton.setText("...");
    smoothTimeButton.setToolTipText(WWGnlUtilities.buildMessage("apply-time-smooth"));
    smoothTimeButton.setPreferredSize(new Dimension(30, 20));
    smoothTimeButton.setEnabled(false);
    smoothTimeButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          smoothTimeButton_actionPerformed(e);
        }
      });
    gribSmoothingPanel.setLayout(gridBagLayout2);
    gribSmoothingPanel.add(smoothLabel,
                           new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    smoothValue.setPreferredSize(new Dimension(30, 20));
    smoothValue.setToolTipText(WWGnlUtilities.buildMessage("grib-smooth-tooltip"));
    smoothValue.setHorizontalAlignment(JTextField.CENTER);
    gribSmoothingPanel.add(smoothValue,
                           new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    smoothButton.setText("...");
    smoothButton.setToolTipText(WWGnlUtilities.buildMessage("apply-smooth"));
    smoothButton.setPreferredSize(new Dimension(30, 20));
    smoothButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            smoothButton_actionPerformed(e);
          }
        });
    gribSmoothingPanel.add(smoothButton,
                           new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 5, 0, 0), 0, 0));
    gribSmoothingPanel.add(smoothTimeLabel,
                           new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    gribSmoothingPanel.add(smoothTimeValue,
                           new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    gribSmoothingPanel.add(smoothTimeButton,
                           new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 5, 0, 0), 0, 0));
    smoothLabel.setEnabled(false);
    smoothValue.setEnabled(false);
    smoothButton.setEnabled(false);

    gribInfoLineTwo.setText("GRIB Info - 2");
    gribInfoLineThree.setText("GRIB Info - 3");
    backwardButton.setEnabled(false);
    forwardButton.setEnabled(false);
    animateButton.setEnabled(false);
    gribInfoLineTwo.setEnabled(false);
    gribInfoLineTwo.setPreferredSize(new Dimension(170, 14));
    gribInfoLineTwo.setFont(new Font("Tahoma", 1, 9));
    gribInfoLineTwo.setForeground(Color.blue);
    gribInfoLineThree.setEnabled(false);
    gribInfoLineThree.setPreferredSize(new Dimension(170, 14));
    gribInfoLineThree.setFont(new Font("Tahoma", 0, 9));
    gribInfoLineFour.setText("GRIB Info - 4");
    gribInfoLineFour.setEnabled(false);
    gribInfoLineFour.setPreferredSize(new Dimension(170, 14));
    gribInfoLineFour.setFont(new Font("Tahoma", 0, 9));

    topPanel.add(googleLabel, null);
    topPanel.add(googleButton, null);
  }

  private void updateSliderData()
  {
    WWContext.getInstance().fireGribIndex(sliderValue - 1);
  }
  
  public void setWind(boolean b) { windLabel.setEnabled(b); }
  public void setPrmsl(boolean b) { prmslLabel.setEnabled(b); }
  public void setHgt(boolean b) { hgtLabel.setEnabled(b); }
  public void setTemp(boolean b) { tmpLabel.setEnabled(b); }
  public void setWaves(boolean b) { waveLabel.setEnabled(b); }
  public void setRain(boolean b) { rainLabel.setEnabled(b); }

  private void forwardButton_actionPerformed(ActionEvent e)
  {
    WWContext.getInstance().fireGribForward();        
  }

  private void backwardButton_actionPerformed(ActionEvent e)
  {
    WWContext.getInstance().fireGribBackward();        
  }  
  
  private void animateButton_actionPerformed(ActionEvent e)
  {
    for (int i=0; i < WWContext.getInstance().getListeners().size(); i++)
    {
      ApplicationEventListener l = WWContext.getInstance().getListeners().get(i);
      l.gribAnimate();
    }    
  }  
  
  private void setGribInfoLabel(int index, 
                                int max, 
                                String s2, 
                                String s3, 
                                String s4, 
                                boolean b1, 
                                boolean b2, 
                                boolean b3, 
                                boolean b4, 
                                boolean b5,
                                boolean b6)
  {
    String gribInfo1 = WWGnlUtilities.buildMessage("index-of", new String[] { Integer.toString(index),
                                                                            Integer.toString(max) });
    gribInfoLineTwo.setText(s2);
    gribInfoLineThree.setText(s3);
    gribInfoLineFour.setText(s4);
    
    setWind(b1);
    setPrmsl(b2);
    setHgt(b3);
    setTemp(b4);
    setWaves(b5);
    setRain(b6);
  }

  private void smoothButton_actionPerformed(ActionEvent e)
  {
    int smooth = 1;
    try { smooth = Integer.parseInt(smoothValue.getText()); } catch (Exception ignore) {}
    WWContext.getInstance().fireGribSmoothing(smooth);
  }

  private void googleButton_actionPerformed(ActionEvent e)
  {
    WWContext.getInstance().fireWindInGoogle();
  }

  private void smoothTimeButton_actionPerformed(ActionEvent e)
  {
    int smooth = 1;
    try { smooth = Integer.parseInt(smoothTimeValue.getText()); } catch (Exception ignore) {}
    WWContext.getInstance().fireGribTimeSmoothing(smooth);
  }
}
