package chartview.gui.util.dialog;

import chartview.gui.util.param.ParamPanel;

import chartview.util.WWGnlUtilities;

import coreutilities.Utilities;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class RoutingOutputFlavorPanel
  extends JPanel
{
  private GridBagLayout gridBagLayout1 = new GridBagLayout();
  
  private ButtonGroup groupOne = new ButtonGroup();
  private ButtonGroup groupTwo = new ButtonGroup();
  private JRadioButton csvRadioButton = new JRadioButton();
  private JRadioButton gpxRadioButton = new JRadioButton();
  private JRadioButton txtRadioButton = new JRadioButton();
  private JPanel rightPanel = new JPanel();
  private JRadioButton clipboardRadioButton = new JRadioButton();
  private JRadioButton saveFileRadioButton = new JRadioButton();
  private GridBagLayout gridBagLayout2 = new GridBagLayout();
  private JSeparator separator = new JSeparator();
  private JPanel filePanel = new JPanel();
  private JButton saveAsButton = new JButton();
  private JTextField fileNameTextField = new JTextField();

  public RoutingOutputFlavorPanel()
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
    this.setSize(new Dimension(518, 127));
    csvRadioButton.setText("CSV");
    csvRadioButton.setSelected(true);
    txtRadioButton.setText("Text");
    rightPanel.setLayout(gridBagLayout2);
    clipboardRadioButton.setText("Clipboard");
    clipboardRadioButton.setSelected(true);
    saveFileRadioButton.setText("Save File");
    saveFileRadioButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          saveFileRadioButton_actionPerformed(e);
        }
      });
    separator.setOrientation(SwingConstants.VERTICAL);
    saveAsButton.setText("Save as...");
    fileNameTextField.setPreferredSize(new Dimension(200, 19));
    gpxRadioButton.setText("GPX");
    this.add(csvRadioButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(gpxRadioButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(txtRadioButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(rightPanel, new GridBagConstraints(3, 1, 1, 3, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.NONE,
          new Insets(0, 10, 0, 0), 0, 0));
    this.add(separator, new GridBagConstraints(2, 1, 1, 3, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
          new Insets(0, 5, 0, 5), 0, 0));
    rightPanel.add(clipboardRadioButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    rightPanel.add(saveFileRadioButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 5, 0, 0), 0, 0));
    rightPanel.add(filePanel, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(5, 0, 0, 0), 0, 0));
    
    filePanel.add(fileNameTextField, null);
    filePanel.add(saveAsButton, null);
    fileNameTextField.setEnabled(false);
    saveAsButton.setEnabled(false);

    saveAsButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          saveAsButton_actionPerformed(e);
        }
      });
    groupOne.add(csvRadioButton);
    groupOne.add(gpxRadioButton);
    groupOne.add(txtRadioButton);
    
    groupTwo.add(clipboardRadioButton);
    groupTwo.add(saveFileRadioButton);
  }
  
  public int getSelectedOption()
  {
    int option = -1;
    if (csvRadioButton.isSelected())
      option = ParamPanel.RoutingOutputList.CSV;
    else if (txtRadioButton.isSelected())
      option = ParamPanel.RoutingOutputList.TXT;
    else if (gpxRadioButton.isSelected())
      option = ParamPanel.RoutingOutputList.GPX;
    return option;
  }
  
  public String getFileOutput()
  {
    String output = null;
    if (saveFileRadioButton.isSelected())
      output = fileNameTextField.getText();
    return output;   
  }

  private void saveFileRadioButton_actionPerformed(ActionEvent e)
  {
    fileNameTextField.setEnabled(saveFileRadioButton.isSelected());
    saveAsButton.setEnabled(saveFileRadioButton.isSelected());
  }

  private void saveAsButton_actionPerformed(ActionEvent e)
  {
    String extension = "csv";
    if (txtRadioButton.isSelected())
      extension = "txt";
    else if (gpxRadioButton.isSelected())
      extension = "gpx";
    String fileName = WWGnlUtilities.chooseFile(this, JFileChooser.FILES_ONLY, 
                                                new String[] { extension }, 
                                                "Routing output", 
                                                ".", 
                                                WWGnlUtilities.buildMessage("save-as-2"),
                                                "Routing output");
    if (fileName.trim().length() > 0)
      fileNameTextField.setText(Utilities.makeSureExtensionIsOK(fileName, "." + extension));
  }
}
