package chartview.gui.util.dialog;

import chartview.util.WWGnlUtilities;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class FileFilterPanel
  extends JPanel
{
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  private JLabel topLabel = new JLabel();
  private JTextField filterTextField = new JTextField();
  private JRadioButton containsRadioButton = new JRadioButton();
  private JRadioButton regExpRadioButton = new JRadioButton();
  private ButtonGroup group = new ButtonGroup();

  public FileFilterPanel()
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
    topLabel.setText(WWGnlUtilities.buildMessage("select-contains"));
    containsRadioButton.setText(WWGnlUtilities.buildMessage("contains"));
    regExpRadioButton.setText(WWGnlUtilities.buildMessage("reg-expr"));
    regExpRadioButton.setToolTipText(WWGnlUtilities.buildMessage("reg-expr-tt"));
    regExpRadioButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            regExpRadioButton_actionPerformed(e);
          }
        });
    group.add(containsRadioButton);
    group.add(regExpRadioButton);
    containsRadioButton.setSelected(true);

    containsRadioButton.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
            containsRadioButton_actionPerformed(e);
          }
        });
    this.add(topLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(filterTextField, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(containsRadioButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(regExpRadioButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
  }
  
  public String getFilter()
  { return filterTextField.getText(); }

  private void containsRadioButton_actionPerformed(ActionEvent e)
  {
    topLabel.setText(WWGnlUtilities.buildMessage("select-contains"));
  }

  private void regExpRadioButton_actionPerformed(ActionEvent e)
  {
    topLabel.setText(WWGnlUtilities.buildMessage("select-matches"));
  }
  
  public boolean isRegExpr()
  {
    return regExpRadioButton.isSelected();
  }
}
