package chartview.gui.util.dialog;


import chartview.gui.util.TableResizeValue;
import chartview.gui.util.param.ParamPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;


public class PatternEditorPanel
  extends JPanel
{
  private BorderLayout borderLayout1 = new BorderLayout();
  private FaxPatternEditTablePanel faxPatternEditTablePanel = new FaxPatternEditTablePanel();
  private GRIBPatternEditorPanel gribPatternEditorPanel = new GRIBPatternEditorPanel();

  private transient Object[][] faxData = null;
  private transient Object[][] gribData = null;
  private boolean grib = false;
  private JCheckBox fitColumnsCheckBox = new JCheckBox();

  public PatternEditorPanel(Object[][] f, Object[][] g)
  {
    faxData = f;
    gribData = g;
    faxPatternEditTablePanel.setData(faxData);
    gribPatternEditorPanel.setData((String)gribData[0][0],
                                   (String)gribData[0][1],
                                   (ParamPanel.DataDirectory)gribData[0][2],
                                   (String)gribData[0][3],
                                   (String)gribData[0][4],
                                   (String)gribData[0][5],
                                   (Boolean)gribData[0][6],
                                   (Boolean)gribData[0][7],
                                   (Boolean)gribData[0][8],
                                   (Boolean)gribData[0][9],
                                   (Boolean)gribData[0][10],
                                   (Boolean)gribData[0][11],
                                   (Boolean)gribData[0][12],
                                   (Boolean)gribData[0][13],
                                   (Boolean)gribData[0][14],
                                   (Boolean)gribData[0][15],
                                   (Boolean)gribData[0][16],
                                   (Boolean)gribData[0][17],
                                   (Boolean)gribData[0][18],
                                   (Boolean)gribData[0][19],
                                   (Boolean)gribData[0][20],
                                   (Boolean)gribData[0][21],
                                   (Boolean)gribData[0][22]); 
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
    this.setLayout(borderLayout1);
    this.setSize(new Dimension(460, 385));
    fitColumnsCheckBox.setText("Auto-resize Columns"); // TODO Localize
    fitColumnsCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          fitColumnsCheckBox_actionPerformed(e);
        }
      });
    this.add(faxPatternEditTablePanel, BorderLayout.NORTH);
    this.add(gribPatternEditorPanel, BorderLayout.SOUTH);
    this.add(fitColumnsCheckBox, BorderLayout.WEST);
    fitColumnsCheckBox.setSelected(faxPatternEditTablePanel.getTableResize() == TableResizeValue.ON);
  }

  public void setFaxData(Object[][] faxData)
  {
    faxPatternEditTablePanel.setData(faxData);
    this.faxData = faxData;
  }

  public Object[][] getFaxData()
  {
    faxData = faxPatternEditTablePanel.getData();
    return faxData;
  }

  public void setGribData(Object[][] gribData)
  {
    this.gribData = gribData;
    gribPatternEditorPanel.setData((String)gribData[0][0],
                                   (String)gribData[0][1],
                                   (ParamPanel.DataDirectory)gribData[0][2],
                                   (String)gribData[0][3],
                                   (String)gribData[0][4],
                                   (String)gribData[0][5],
                                   (Boolean)gribData[0][6],
                                   (Boolean)gribData[0][7],
                                   (Boolean)gribData[0][8],
                                   (Boolean)gribData[0][9],
                                   (Boolean)gribData[0][10],
                                   (Boolean)gribData[0][11],
                                   (Boolean)gribData[0][12],
                                   (Boolean)gribData[0][13],
                                   (Boolean)gribData[0][14],
                                   (Boolean)gribData[0][15],
                                   (Boolean)gribData[0][16],
                                   (Boolean)gribData[0][17],
                                   (Boolean)gribData[0][18],
                                   (Boolean)gribData[0][19],
                                   (Boolean)gribData[0][20],
                                   (Boolean)gribData[0][21],
                                   (Boolean)gribData[0][22]); 
  }

  public Object[][] getGribData()
  {
    gribData = new Object[][] { { gribPatternEditorPanel.getHint(),
                                  gribPatternEditorPanel.getRequest(),
                                  gribPatternEditorPanel.getDir(),
                                  gribPatternEditorPanel.getPrefix(),
                                  gribPatternEditorPanel.getPattern(),
                                  gribPatternEditorPanel.getExtension(),
                                  gribPatternEditorPanel.getPRMSLData(), // was JustWind
                                  gribPatternEditorPanel.get500MBData(),
                                  gribPatternEditorPanel.getWAVESData(),
                                  gribPatternEditorPanel.getTEMPData(),
                                  gribPatternEditorPanel.getPRATEData(),
                                  gribPatternEditorPanel.getTWS3D(),
                                  gribPatternEditorPanel.getPRMSL3D(),
                                  gribPatternEditorPanel.get500MB3D(),
                                  gribPatternEditorPanel.getWAVES3D(),
                                  gribPatternEditorPanel.getTEMP3D(),
                                  gribPatternEditorPanel.getPRATE3D(),
                                  gribPatternEditorPanel.getTWSContour(),
                                  gribPatternEditorPanel.getPRMSLContour(),
                                  gribPatternEditorPanel.get500MBContour(),
                                  gribPatternEditorPanel.getWAVESContour(),
                                  gribPatternEditorPanel.getTEMPContour(),
                                  gribPatternEditorPanel.getPRATEContour()
                                } 
                              };
    return gribData;
  }

  public void setGrib(boolean grib)
  {  
    this.grib = grib;
    gribPatternEditorPanel.setGribOption(grib);
  }

  public boolean isGrib()
  {
    grib = gribPatternEditorPanel.getGribOption();
    return grib;
  }

  private void fitColumnsCheckBox_actionPerformed(ActionEvent e)
  {
    faxPatternEditTablePanel.setTableResize(fitColumnsCheckBox.isSelected()? TableResizeValue.ON : TableResizeValue.OFF);
  }
}
