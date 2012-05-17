package chartview.gui.util.dialog;


import chartview.gui.util.TableResizeValue;
import chartview.gui.util.param.ParamPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class PatternEditorPanel
  extends JPanel
{
  private FaxPatternEditTablePanel faxPatternEditTablePanel  = new FaxPatternEditTablePanel();
  private GRIBPatternEditorPanel gribPatternEditorPanel      = new GRIBPatternEditorPanel();
  private ChartDimensionInputPanel chartDimensionEditorPanel = new ChartDimensionInputPanel();

  private transient Object[][] faxData = null;
  private transient Object[][] gribData = null;
  private boolean grib = false;
  private JCheckBox fitColumnsCheckBox = new JCheckBox();
  private GridBagLayout gridBagLayout1 = new GridBagLayout();

  public PatternEditorPanel(int projection,
                            double northBoundary, 
                            double southBoundary, 
                            double eastBoundary, 
                            double westBoundary, 
                            int chartWidth, 
                            int chartHeight, 
                            int xOffset, 
                            int yOffset, 
                            Object[][] f, 
                            Object[][] g)
  {
    chartDimensionEditorPanel.setProjection(projection);
    chartDimensionEditorPanel.setTopLat(northBoundary);
    chartDimensionEditorPanel.setBottomLat(southBoundary);
    chartDimensionEditorPanel.setLeftLong(westBoundary);
    chartDimensionEditorPanel.setRightLong(eastBoundary);
    chartDimensionEditorPanel.setChartWidth(chartWidth);
    chartDimensionEditorPanel.setChartHeight(chartHeight);
    chartDimensionEditorPanel.setXOffset(xOffset);
    chartDimensionEditorPanel.setYOffset(yOffset);
    
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
    this.setLayout(gridBagLayout1);
    this.setSize(new Dimension(595, 605));
    this.setPreferredSize(new Dimension(595, 605));
    fitColumnsCheckBox.setText("Auto-resize Columns"); // TODO Localize
    fitColumnsCheckBox.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          fitColumnsCheckBox_actionPerformed(e);
        }
      });
    this.add(chartDimensionEditorPanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(faxPatternEditTablePanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(fitColumnsCheckBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
          new Insets(0, 0, 0, 0), 0, 0));
    this.add(gribPatternEditorPanel, new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
          new Insets(0, 0, 0, 0), -80, 0));
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

  public double getTopLat()
  {
    return chartDimensionEditorPanel.getTopLat();
  }
  
  public double getBottomLat()
  {
    return chartDimensionEditorPanel.getBottomLat();
  }
  
  public double getLeftLong()
  {
    return chartDimensionEditorPanel.getLeftLong();
  }
  
  public double getRightLong()
  {
    return chartDimensionEditorPanel.getRightLong();
  }
  
  public int getProjection()
  {
    return chartDimensionEditorPanel.getProjection();
  }
  
  public int getChartWidth()
  {
    return chartDimensionEditorPanel.getChartWidth();
  }
      
  public int getChartHeight()
  {
    return chartDimensionEditorPanel.getChartHeight();
  }
  
  public int getXOffset()
  {
    return chartDimensionEditorPanel.getXOffset();
  }
      
  public int getYOffset()
  {
    return chartDimensionEditorPanel.getYOffset();
  }
      
  private void fitColumnsCheckBox_actionPerformed(ActionEvent e)
  {
    faxPatternEditTablePanel.setTableResize(fitColumnsCheckBox.isSelected()? TableResizeValue.ON : TableResizeValue.OFF);
  }
}
