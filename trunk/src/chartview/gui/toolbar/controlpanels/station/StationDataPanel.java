package chartview.gui.toolbar.controlpanels.station;

import chartview.util.WWGnlUtilities;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class StationDataPanel
  extends JPanel
{
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JLabel jLabel1 = new JLabel();
  private JLabel twdLabel = new JLabel();
  private JLabel jLabel3 = new JLabel();
  private JLabel twsLabel = new JLabel();
  private JLabel jLabel5 = new JLabel();
  private JLabel twaLabel = new JLabel();
  private JLabel jLabel2 = new JLabel();
  private JLabel bspLabel = new JLabel();

  public StationDataPanel()
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
    this.setLayout(gridBagLayout1);
    this.setSize(new Dimension(60, 149));
    jLabel1.setText("TWS");
    jLabel1.setFont(new Font("Tahoma", 1, 10));
    twsLabel.setText("00");
    jLabel3.setText("TWD");
    jLabel3.setFont(new Font("Tahoma", 1, 10));
    twdLabel.setText("00");
    jLabel5.setText("TWA");
    jLabel5.setFont(new Font("Tahoma", 1, 10));
    twaLabel.setText("00");
    jLabel2.setText("BSP");
    jLabel2.setFont(new Font("Tahoma", 1, 10));
    bspLabel.setText("00.00");
    this.add(jLabel1, 
             new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(twsLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(jLabel3, 
             new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(twdLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(jLabel5, 
             new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(twaLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(jLabel2, 
             new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(bspLabel, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
          new Insets(0, 0, 0, 0), 0, 0));
  }
  
  public void setTWS(float tws)
  {
    twsLabel.setText(WWGnlUtilities.DF2.format(tws));
  }
  
  public void setTWD(int twd)
  {
    twdLabel.setText(Integer.toString(twd));
  }

  public void setTWA(int twa)
  {
    if (twa < 0) twa += 360;
    // There is a minus sign on the side the wind comes from
    if (twa > 0 && twa < 180)
      twaLabel.setText(Integer.toString(twa) + " -");
    else if (twa > 180 && twa < 360)
      twaLabel.setText("- " + Integer.toString(360 - twa));
    else if (twa == 0)
      twaLabel.setText("0");
    else if (twa == 180)
      twaLabel.setText("180");
    else
      twaLabel.setText(Integer.toString(twa));
  }
  
  public void setBSP(float bsp)
  {
    bspLabel.setText(WWGnlUtilities.XX22.format(bsp));
  }
}
