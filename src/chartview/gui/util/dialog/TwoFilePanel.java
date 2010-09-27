package chartview.gui.util.dialog;

import chartview.util.WWGnlUtilities;

import coreutilities.Utilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class TwoFilePanel
  extends JPanel
{
  public final static String EVERY_THING = "EVERYTHING";
  public final static String JUST_FAXES  = "JUST_FAXES";
  public final static String JUST_GRIBS  = "JUST_GRIBS";
  
  private TwoFilePanel instance = this;
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JLabel leftLabel = new JLabel();
  private JLabel rightLabel = new JLabel();
  private JFileChooser leftChooser = new JFileChooser();
  private JFileChooser rightChooser = new JFileChooser();
  private JTextField regExprPatternTextField = new JTextField();
  private JLabel patternLabel = new JLabel();
  private JLabel regExpLabel = new JLabel();
  private JPanel gribOptionPanel = new JPanel();
  
  private ButtonGroup compositeGroup = new ButtonGroup();
  
  private JRadioButton allRadioButton = new JRadioButton();
  private JRadioButton justFaxesRadioButton = new JRadioButton();
  private JRadioButton justGRIBRadioButton = new JRadioButton();
  private JCheckBox pdfCheckBox = new JCheckBox();
  private JLabel pdfTitleLabel = new JLabel("A title for the pdf:");
  private JTextField pdfTitle = new JTextField();

  public TwoFilePanel()
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
    this.setSize(new Dimension(1002, 453));
    leftLabel.setText("Left Panel Label");
    leftLabel.setFont(new Font("Tahoma", 1, 11));
    rightLabel.setText("Right Panel Label");
    rightLabel.setFont(new Font("Tahoma", 1, 11));
    regExprPatternTextField.setText(".*");
    patternLabel.setText("Regular expression?");
    regExpLabel.setText("<html><u>" + WWGnlUtilities.buildMessage("help-reg-exp") + "</u></html>"); 
    regExpLabel.setForeground(Color.blue);
    regExpLabel.addMouseListener(new MouseAdapter()
        {
          public void mouseClicked(MouseEvent e)
          {
            regExpLabel.setForeground(WWGnlUtilities.PURPLE);
            regExpLabel.repaint();
            try { Utilities.openInBrowser("http://www.regular-expressions.info/tutorial.html"); }
            catch (Exception ex)
            { JOptionPane.showMessageDialog(instance, "Problem with RegExp help\n" + ex.toString(), "Regular Expressions", JOptionPane.WARNING_MESSAGE); }
          }
          public void mouseEntered(MouseEvent e)
          {
            regExpLabel.setForeground(WWGnlUtilities.PURPLE);
            regExpLabel.repaint();
          }
          public void mouseExited(MouseEvent e)
          {
            regExpLabel.setForeground(Color.blue);
            regExpLabel.repaint();
          }
    });

    compositeGroup.add(allRadioButton);
    compositeGroup.add(justFaxesRadioButton);
    compositeGroup.add(justGRIBRadioButton);
    
    allRadioButton.setText(WWGnlUtilities.buildMessage("everything")); 
    allRadioButton.setSelected(true);
    justFaxesRadioButton.setText(WWGnlUtilities.buildMessage("just-faxes"));
    justGRIBRadioButton.setText(WWGnlUtilities.buildMessage("just-gribs"));
    pdfCheckBox.setText("Generate PDF when done");
    pdfCheckBox.setSelected(false);
    pdfTitle.setEnabled(false);
    pdfTitleLabel.setEnabled(false);
    pdfCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          pdfTitle.setEnabled(pdfCheckBox.isSelected());
          pdfTitleLabel.setEnabled(pdfCheckBox.isSelected());
        }                                    
      });
    pdfTitle.setPreferredSize(new Dimension(200, 24));
    this.add(leftLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    this.add(rightLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    this.add(leftChooser, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    this.add(rightChooser, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    this.add(regExprPatternTextField, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    this.add(patternLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    this.add(regExpLabel, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
    gribOptionPanel.add(allRadioButton, null);
    gribOptionPanel.add(justFaxesRadioButton, null);
    gribOptionPanel.add(justGRIBRadioButton, null);
    gribOptionPanel.add(pdfCheckBox, null);
    gribOptionPanel.add(pdfTitleLabel, null);
    gribOptionPanel.add(pdfTitle, null);
    this.add(gribOptionPanel, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(10, 10, 0, 0), 0, 0));
  }

  public JLabel getLeftLabel()
  {
    return leftLabel;
  }

  public JLabel getRightLabel()
  {
    return rightLabel;
  }

  public JFileChooser getLeftChooser()
  {
    return leftChooser;
  }

  public JFileChooser getRightChooser()
  {
    return rightChooser;
  }

  public JTextField getRegExprPatternTextField()
  {
    return regExprPatternTextField;
  }

  public JLabel getPatternLabel()
  {
    return patternLabel;
  }
  
  public String getDisplayOption()
  {
    if (allRadioButton.isSelected())
      return EVERY_THING;
    else if (justFaxesRadioButton.isSelected())
      return JUST_FAXES;
    else if (justGRIBRadioButton.isSelected())
      return JUST_GRIBS;
    return null;
  }
  
  public String getPDFTitle()
  {
    if (pdfCheckBox.isSelected())
      return pdfTitle.getText();
    else
      return null;
  }
}