package chartview.gui.toolbar.controlpanels.station;

import chartview.util.WWGnlUtilities;

import java.awt.Color;
import java.awt.Dimension;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class BSPDisplay
  extends JPanel
{
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JLabel bspLabel = new JLabel();
  private float bsp = 0F;  

  public BSPDisplay()
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
    this.setSize(new Dimension(103, 42));
    this.setBackground(Color.black);
    bspLabel.setText("00.00");
    bspLabel.setForeground(Color.green);
    bspLabel.setFont(new Font("Courier New", Font.BOLD, 18)); // For default, at design time
    Font digiFont = null;
    try { digiFont = WWGnlUtilities.tryToLoadFont("ds-digi.ttf", this); }
    catch (Exception ex) { System.err.println(ex.getMessage()); }
    if (digiFont == null)
      digiFont = new Font("Courier New", Font.BOLD, 18);
    else
      digiFont = digiFont.deriveFont(Font.BOLD, 18);
    bspLabel.setFont(digiFont);
    this.add(bspLabel, 
             new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 
                                    GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                    new Insets(0, 0, 0, 0), 0, 0));
  }

  public void setBsp(float bsp)
  {
    this.bsp = bsp;
    bspLabel.setText(WWGnlUtilities.XXX12.format(bsp));
  }

  public float getBsp()
  {
    return bsp;
  }
}
