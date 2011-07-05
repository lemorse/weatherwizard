package chartview.gui.util.dialog;

import astro.calc.GeoPoint;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;

import javax.swing.JTextField;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import user.util.GeomUtil;

public class WhatIfRoutingPanel
  extends JPanel
{
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JPanel jPanelOne = new JPanel();
  private GridBagLayout gridBagLayout2 = new GridBagLayout();
  private JLabel fromLabel = new JLabel();
  private JLabel latLabel = new JLabel();
  private JLabel lngLabel = new JLabel();
  private JLabel whatIfLabel = new JLabel();
  private JPanel jPanelTwo = new JPanel();
  private JPanel jPanelThree = new JPanel();
  private JPanel jPanelFour = new JPanel();
  private JLabel iKeepSailingLabel = new JLabel();
  private JLabel startingLabel = new JLabel();
  private GridBagLayout gridBagLayout4 = new GridBagLayout();
  private GridBagLayout gridBagLayout5 = new GridBagLayout();
  private GridBagLayout gridBagLayout6 = new GridBagLayout();
  private JRadioButton nowRadioButton = new JRadioButton();
  private JRadioButton withTheGRIBRadioButton = new JRadioButton();
  private JRadioButton headingRadioButton = new JRadioButton();
  private JRadioButton twaRadioButton = new JRadioButton();
  private JRadioButton gribExhaustedRadioButton = new JRadioButton();
  private JRadioButton duringRadioButton = new JRadioButton();
  
  private ButtonGroup groupOne   = new ButtonGroup();
  private ButtonGroup groupTwo   = new ButtonGroup();
  private ButtonGroup groupThree = new ButtonGroup();
  
  HeadingPanel        hp = new HeadingPanel();
  TWAPanel            tp = new TWAPanel();
  DurationPanel       dp = new DurationPanel();
  GRIBExhaustedPanel gep = new GRIBExhaustedPanel();

  public WhatIfRoutingPanel()
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
    groupOne.add(headingRadioButton);
    groupOne.add(twaRadioButton);
    headingRadioButton.setSelected(true);
    
    groupTwo.add(nowRadioButton);
    groupTwo.add(withTheGRIBRadioButton);
    nowRadioButton.setSelected(true);
    
    groupThree.add(gribExhaustedRadioButton);
    groupThree.add(duringRadioButton);
    gribExhaustedRadioButton.setSelected(true);
    
    this.setLayout(gridBagLayout1);
    jPanelOne.setLayout(gridBagLayout2);
    fromLabel.setText("From:");
    latLabel.setText("XX*XX.XX N");
    lngLabel.setText("XXX*XX.XX W");
    whatIfLabel.setText("What if...");
    whatIfLabel.setFont(new Font("Tahoma", 3, 12));
    jPanelTwo.setLayout(gridBagLayout4);
    jPanelThree.setLayout(gridBagLayout5);
    jPanelFour.setLayout(gridBagLayout6);
    iKeepSailingLabel.setText("I keep sailing");
    startingLabel.setText("Starting");
    nowRadioButton.setText("Now");
    withTheGRIBRadioButton.setText("With the GRIB");
    headingRadioButton.setText("Heading");
    headingRadioButton.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
          hp.setEnabled(headingRadioButton.isSelected());
        }
      });
    twaRadioButton.setText("TWA");
    twaRadioButton.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
          tp.setEnabled(twaRadioButton.isSelected());
        }
      });
    tp.setEnabled(false);
    gribExhaustedRadioButton.setText("Until GRIB exhausted");
    duringRadioButton.setText("During ");
    duringRadioButton.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
          dp.setEnabled(duringRadioButton.isSelected());
        }
      });
    this.add(jPanelOne, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    jPanelOne.add(latLabel,
                  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
                                                                                                                            0,
                                                                                                                            0,
                                                                                                                            0),
                                         0, 0));
    jPanelOne.add(lngLabel,
                  new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
                                                                                                                            0,
                                                                                                                            0,
                                                                                                                            0),
                                         0, 0));
    jPanelOne.add(fromLabel,
                  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
                                                                                                                            0,
                                                                                                                            0,
                                                                                                                            10),
                                         0, 0));
    jPanelOne.add(whatIfLabel, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(5, 0, 10, 0), 0, 0));
    jPanelTwo.add(iKeepSailingLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 10), 0, 0));
    jPanelTwo.add(hp /*headingRadioButton*/, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    jPanelTwo.add(tp /*twaRadioButton*/, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(jPanelTwo, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    jPanelTwo.add(startingLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 10), 0, 0));
    jPanelTwo.add(nowRadioButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    jPanelTwo.add(withTheGRIBRadioButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    jPanelTwo.add(gep /*gribExhaustedRadioButton*/, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    jPanelTwo.add(dp /*duringRadioButton*/, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    dp.setEnabled(false);    
  }
  
  public void setFromPos(GeoPoint gp)
  {
    latLabel.setText(GeomUtil.decToSex(gp.getL(), GeomUtil.SWING, GeomUtil.NS));
    lngLabel.setText(GeomUtil.decToSex(gp.getG(), GeomUtil.SWING, GeomUtil.EW));
  }
  
  class HeadingPanel extends JPanel
  {
    JTextField headingField = new JTextField();
    JLabel trueLabel = new JLabel(" true");
    
    public HeadingPanel()
    {
      init();
    }
    
    private void init()
    {
      headingField.setPreferredSize(new Dimension(40, 20));
      headingField.setHorizontalAlignment(JTextField.RIGHT);
      this.add(headingRadioButton, null);
      this.add(headingField, null);
      this.add(trueLabel);
    }
    
    public void setEnabled(boolean b)
    {
      headingField.setEnabled(b);
      trueLabel.setEnabled(b);
      this.repaint();
    }
    
    public int getHeading()
    {
      int hdg = 0;
      try
      {
        hdg = Integer.parseInt(headingField.getText());
      }
      catch (NumberFormatException nfe)
      {
        throw new RuntimeException(nfe);
      }
      return hdg;
    }
  }
  
  class TWAPanel extends JPanel
  {
    JTextField twaField = new JTextField();
    JRadioButton starboardRadioButton = new JRadioButton("Stbd");
    JRadioButton portRadioButton      = new JRadioButton("Port");
    ButtonGroup grp = new ButtonGroup();

    public TWAPanel()
    {
      init();
    }
    
    private void init()
    {
      grp.add(starboardRadioButton);
      grp.add(portRadioButton);
      starboardRadioButton.setSelected(true);
      twaField.setPreferredSize(new Dimension(40, 20));
      twaField.setHorizontalAlignment(JTextField.RIGHT);
      
      this.add(twaRadioButton, null);
      this.add(twaField, null);
      this.add(starboardRadioButton, null);
      this.add(portRadioButton, null);
    }

    public void setEnabled(boolean b)
    {
      starboardRadioButton.setEnabled(b);
      portRadioButton.setEnabled(b);
      twaField.setEnabled(b);
    }
    
    public int getTWA()
    {
      int twa = 0;
      try
      {
        twa = Integer.parseInt(twaField.getText());
        if (portRadioButton.isSelected())
          twa = -twa;
      }
      catch (NumberFormatException nfe)
      {
        throw new RuntimeException(nfe);
      }
      return twa;
    }
  }
  
  class DurationPanel extends JPanel
  {
    JTextField numField = new JTextField();
    JComboBox unitList = new JComboBox();
    
    public DurationPanel()
    {
      init();
    }
    
    private void init()
    {
      numField.setPreferredSize(new Dimension(40, 20));
      numField.setHorizontalAlignment(JTextField.RIGHT);
      
      unitList.removeAllItems();
      unitList.addItem("Day(s)");
      unitList.addItem("Week(s)");
      
      this.add(duringRadioButton, null);
      this.add(numField, null);
      this.add(unitList, null);
    }
    
    public void setEnabled(boolean b)
    {
      numField.setEnabled(b);
      unitList.setEnabled(b);
    }
    
    public int getNbDays()
    {
      int nbd = 0;
      try
      {
        nbd = Integer.parseInt(numField.getText());
        if (unitList.getSelectedIndex() == 1)
          nbd *= 7;
      }
      catch (NumberFormatException nfe)
      {
        throw new RuntimeException(nfe);
      }
      return nbd;
    }
  }
  
  class GRIBExhaustedPanel extends JPanel
  {
    public GRIBExhaustedPanel()
    {
      init();
    }
    
    private void init()
    {
      this.add(gribExhaustedRadioButton, null);
    }
  }
  
  public boolean isHeadingSelected()
  { return headingRadioButton.isSelected(); }
  
  public boolean isNowSelected()
  { return nowRadioButton.isSelected(); }
  
  public boolean isDuringSelected()
  { return duringRadioButton.isSelected(); }
  
  public int getHeading()
  {
    int hdg = 0;
    try
    {
      hdg = hp.getHeading();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return hdg;
  }
  
  public int getTWA()
  {
    int twa = 0;
    try
    {
      twa = tp.getTWA();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return twa;
  }
  
  public int getNbDays()
  {
    int nbd = 0;
    try
    {
      nbd = dp.getNbDays();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return nbd;
  }
}