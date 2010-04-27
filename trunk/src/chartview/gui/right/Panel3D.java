package chartview.gui.right;

import chartview.ctx.ApplicationEventListener;

import chartview.gui.right.threed.ThreeDPanel;

import chartview.gui.right.threed.ZoomPanel;

import chartview.ctx.WWContext;

import chartview.gui.right.threed.ThreeDPoint;
import chartview.gui.util.param.ParamData;
import chartview.gui.util.param.ParamPanel;

import chartview.util.grib.GRIBDataUtil;

import java.awt.BorderLayout;

import java.awt.Color;

import java.awt.Dimension;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class Panel3D
  extends JPanel
{
  private BorderLayout borderLayout1 = new BorderLayout();
  private ThreeDPanel threeDPanel = new ThreeDPanel(null, 
                                                    1.0f, 
                                                    new Color(240, 240, 245), 
                                                    (Color)ParamPanel.data[ParamData.CHART_COLOR][1], 
                                                    Color.blue, // Text (N, S, E, W)
                                                    Color.red); // Other points (not used for now)
  private ZoomPanel zoompanel = new ZoomPanel();
  private JCheckBox twsCheckBox = new JCheckBox();
  private JCheckBox prmslCheckBox = new JCheckBox();
  private JCheckBox hgt500CheckBox = new JCheckBox();
  private JCheckBox wavesCheckBox = new JCheckBox();
  private JCheckBox temperatureCheckBox = new JCheckBox();
  private JCheckBox rainCheckBox = new JCheckBox();

  public Panel3D()
  {
    WWContext.getInstance().addApplicationListener(new ApplicationEventListener()
        {
          public void newTWSObj(ArrayList<Point> al)
          {
            twsCheckBox.setEnabled(true);
          }
          
          public void new500mbObj(ArrayList<Point> al)
          {
            prmslCheckBox.setEnabled(true);
          }
          
          public void newPrmslObj(ArrayList<Point> al)
          {
            hgt500CheckBox.setEnabled(true);
          }
          
          public void newTmpObj(ArrayList<Point> al)
          {
            temperatureCheckBox.setEnabled(true);;
          }
          
          public void newWaveObj(ArrayList<Point> al)
          {
            wavesCheckBox.setEnabled(true);
          }
          
          public void newRainObj(ArrayList<Point> al)
          {
            rainCheckBox.setEnabled(true);
          }

          public void noTWSObj() 
          {
            twsCheckBox.setSelected(false);
            twsCheckBox.setEnabled(false);
          }
          public void no500mbObj() 
          {
            hgt500CheckBox.setSelected(false);
            hgt500CheckBox.setEnabled(false);
          }
          public void noPrmslObj() 
          {
            prmslCheckBox.setSelected(false);
            prmslCheckBox.setEnabled(false);
          }
          public void noTmpObj() 
          {
            temperatureCheckBox.setSelected(false);
            temperatureCheckBox.setEnabled(false);
          }
          public void noWaveObj() 
          {
            wavesCheckBox.setSelected(false);
            wavesCheckBox.setEnabled(false);
          }
          public void noRainObj() 
          {
            rainCheckBox.setSelected(false);
            rainCheckBox.setEnabled(false);
          }
        });

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
    this.setLayout(borderLayout1);
    this.setSize(new Dimension(620, 361));
    twsCheckBox.setText("tws");
    twsCheckBox.setEnabled(false);
    twsCheckBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            twsCheckBox_actionPerformed(e);
          }
        });
    prmslCheckBox.setText("prmsl");
    prmslCheckBox.setEnabled(false);
    prmslCheckBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            prmslCheckBox_actionPerformed(e);
          }
        });
    hgt500CheckBox.setText("500mb");
    hgt500CheckBox.setEnabled(false);
    hgt500CheckBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            hgt500CheckBox_actionPerformed(e);
          }
        });
    wavesCheckBox.setText("waves");
    wavesCheckBox.setEnabled(false);
    wavesCheckBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            wavesCheckBox_actionPerformed(e);
          }
        });
    temperatureCheckBox.setText("temperature");
    temperatureCheckBox.setEnabled(false);
    temperatureCheckBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            temperatureCheckBox_actionPerformed(e);
          }
        });
    rainCheckBox.setText("precipitation");
    rainCheckBox.setEnabled(false);
    rainCheckBox.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            rainCheckBox_actionPerformed(e);
          }
        });
    this.add(threeDPanel, BorderLayout.CENTER);
    zoompanel.add(twsCheckBox, null);
    zoompanel.add(prmslCheckBox, null);
    zoompanel.add(hgt500CheckBox, null);
    zoompanel.add(wavesCheckBox, null);
    zoompanel.add(temperatureCheckBox, null);
    zoompanel.add(rainCheckBox, null);
    this.add(zoompanel, BorderLayout.SOUTH);
  }

  public ThreeDPanel getThreeDPanel()
  {
    return threeDPanel;
  }
  
  private void twsCheckBox_actionPerformed(ActionEvent e)
  {
    WWContext.getInstance().fireTWSDisplayed(twsCheckBox.isSelected());
  }
  
  private void prmslCheckBox_actionPerformed(ActionEvent e)
  {
    WWContext.getInstance().firePRMSLDisplayed(prmslCheckBox.isSelected());
  }

  private void hgt500CheckBox_actionPerformed(ActionEvent e)
  {
    WWContext.getInstance().fire500MBDisplayed(hgt500CheckBox.isSelected());
  }

  private void wavesCheckBox_actionPerformed(ActionEvent e)
  {
    WWContext.getInstance().fireWAVESDisplayed(wavesCheckBox.isSelected());
  }

  private void temperatureCheckBox_actionPerformed(ActionEvent e)
  {
    WWContext.getInstance().fireTEMPDisplayed(temperatureCheckBox.isSelected());
  }

  private void rainCheckBox_actionPerformed(ActionEvent e)
  {
    WWContext.getInstance().fireRAINDisplayed(rainCheckBox.isSelected());
  }
}
