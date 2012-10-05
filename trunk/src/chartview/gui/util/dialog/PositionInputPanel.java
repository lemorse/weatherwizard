package chartview.gui.util.dialog;

import chartview.util.WWGnlUtilities;
import chartview.ctx.WWContext;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.text.NumberFormatter;

import user.util.GeomUtil;


@SuppressWarnings("serial")
public class PositionInputPanel extends JPanel 
{
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JLabel jLabel1 = new JLabel();
  private JLabel jLabel2 = new JLabel();
  private JLabel jLabel3 = new JLabel();
  private JFormattedTextField LDeg = new JFormattedTextField("##0");
  private JLabel jLabel4 = new JLabel();
  private JFormattedTextField LMin = new JFormattedTextField("00.00");
  private JComboBox LSign = new JComboBox();
  private JComboBox GSign = new JComboBox();
  private JFormattedTextField GMin = new JFormattedTextField("##0.0");
  private JFormattedTextField GDeg = new JFormattedTextField("00.00");
  private JLabel jLabel5 = new JLabel();
  private JLabel jLabel6 = new JLabel();
  private JTextField headingTextField = new JTextField();
  private JLabel jLabel7 = new JLabel();

  public PositionInputPanel()
  {
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      WWContext.getInstance().fireExceptionLogging(e);
      e.printStackTrace();
    }

  }

  private void jbInit() throws Exception
  {
    this.setLayout(gridBagLayout1);
    this.setSize(new Dimension(285, 127));
    this.setPreferredSize(new Dimension(240, 100));
    this.setMinimumSize(new Dimension(240, 100));
    this.setBorder(BorderFactory.createTitledBorder(WWGnlUtilities.buildMessage("position-heading")));
    jLabel1.setText(WWGnlUtilities.buildMessage("assumed-position"));
    jLabel2.setText("L:");
    jLabel3.setText("G:");
    LDeg.setText("00");
    LDeg.setHorizontalAlignment(JTextField.RIGHT);
    LDeg.setPreferredSize(new Dimension(40, 20));
    LDeg.setMinimumSize(new Dimension(40, 20));
    jLabel4.setText("°");
    LMin.setText(((NumberFormatter)LMin.getFormatter()).getFormat().format(0.0));
    LMin.setHorizontalAlignment(JTextField.RIGHT);
    LMin.setPreferredSize(new Dimension(50, 20));
    LMin.setMinimumSize(new Dimension(50, 20));
    LSign.setPreferredSize(new Dimension(35, 20));
    LSign.setMinimumSize(new Dimension(35, 20));
    LSign.addItem("N");
    LSign.addItem("S");

    GSign.setPreferredSize(new Dimension(35, 20));
    GSign.setMinimumSize(new Dimension(35, 20));
    GSign.addItem("E");
    GSign.addItem("W");
    GMin.setText(((NumberFormatter)GMin.getFormatter()).getFormat().format(0.0));
    GMin.setHorizontalAlignment(JTextField.RIGHT);
    GMin.setPreferredSize(new Dimension(50, 20));
    GMin.setMinimumSize(new Dimension(50, 20));
    GDeg.setText("000");
    GDeg.setHorizontalAlignment(JTextField.RIGHT);
    GDeg.setPreferredSize(new Dimension(40, 20));
    GDeg.setMinimumSize(new Dimension(40, 20));
    jLabel5.setText("°");
    jLabel6.setText(WWGnlUtilities.buildMessage("true-heading"));
    headingTextField.setHorizontalAlignment(JTextField.RIGHT);
    headingTextField.setText("0");
    headingTextField.setToolTipText(WWGnlUtilities.buildMessage("heading-degrees"));
    jLabel7.setText("°");
    this.add(jLabel1, 
             new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, 
                                    new Insets(0, 0, 0, 10), 0, 0));
    this.add(jLabel2, 
             new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(jLabel3, 
             new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(LDeg, 
             new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(jLabel4, 
             new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(LMin, 
             new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(LSign, 
             new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(GSign, 
             new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(GMin, 
             new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(GDeg, 
             new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(jLabel5, 
             new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
                                    new Insets(0, 0, 0, 0), 0, 0));
    this.add(jLabel6, 
             new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, 
                                    new Insets(5, 0, 0, 0), 0, 0));
    this.add(headingTextField, 
             new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, 
                                    new Insets(5, 0, 0, 0), 0, 0));
    this.add(jLabel7, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, 
          new Insets(0, 0, 0, 0), 0, 0));
  }

  public double getL()
  {
    double l = 0.0;
    String d = LDeg.getText();
    String m = LMin.getText();
    l = GeomUtil.sexToDec(d, m);
    String ns = (String)LSign.getSelectedItem();
    if (ns.toUpperCase().equals("S"))
      l *= -1;
    return l;
  }
  
  public double getG()
  {
    double g = 0.0;
    String d = GDeg.getText();
    String m = GMin.getText();
    g = GeomUtil.sexToDec(d, m);
    String ew = (String)GSign.getSelectedItem();
    if (ew.toUpperCase().equals("W"))
      g *= -1;
    return g;
  }

  public void setData(double lat, double lng, int hdg)
  {
    int val = Math.abs((int)lat);
    double remainder = Math.abs(lat) - val;
    setLDeg(Integer.toString(val));
    setLMin(WWGnlUtilities.DF2.format(remainder * 60D));
    if (lat < 0) setLSign("S");
    else setLSign("N");
    
    val = Math.abs((int)lng);
    remainder = Math.abs(lng) - val;
    setGDeg(Integer.toString(val));
    setGMin(WWGnlUtilities.DF2.format(remainder * 60D));
    if (lng < 0) setGSign("W");
    else setGSign("E");
    
    setHeading(Integer.toString(hdg));
  }

  public void setLDeg(String str)
  {
    LDeg.setText(str);
  }
  public void setLMin(String str)
  {
    LMin.setText(str);
  }
  public void setGDeg(String str)
  {
    GDeg.setText(str);
  }
  public void setGMin(String str)
  {
    GMin.setText(str);
  }
  public void setLSign(String str)
  {
    LSign.setSelectedItem(str);
  }
  public void setGSign(String str)
  {
    GSign.setSelectedItem(str);
  }
  
  public void setHeading(String str)
  {
    headingTextField.setText(str);
  }
  public int getHeading()
  {
    int heading = 0;
    try { heading = Integer.parseInt(headingTextField.getText()); } catch (Exception ex) {}
    return heading;
  }
}