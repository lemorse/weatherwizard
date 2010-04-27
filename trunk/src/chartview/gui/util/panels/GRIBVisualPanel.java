package chartview.gui.util.panels;

import chartview.ctx.ApplicationEventListener;
import chartview.ctx.WWContext;

import chartview.gui.util.transparent.TransparentPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import java.text.DecimalFormat;

public class GRIBVisualPanel extends TransparentPanel
{
  private float truewindspeed = 0f;
  private int truewinddir = 0;
  private float prmslValue = 0f;
  private float hgt500Value = 0f;
  private float waveHeightValue = 0f;
  private float tempValue = 0f;
  private float prateValue = 0f;              
  
  public GRIBVisualPanel()
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

  private void jbInit() throws Exception
  {
    this.setLayout( null );
    this.setOpaque(false);

    WWContext.getInstance().addApplicationListener(new ApplicationEventListener()
     {
        public void setGRIBWindValue(int twd, float tws)
        {
          truewindspeed = tws;
          truewinddir = twd;
          repaint();
        }

        public void setGRIBPRMSLValue(float prmsl)
        {
          prmslValue = prmsl;
          repaint();
        }

        public void setGRIB500HGTValue(float hgt500)
        {
          hgt500Value = hgt500;
          repaint();
        }

        public void setGRIBWaveHeight(float wh)
        {
          waveHeightValue = wh;
          repaint();
        }

        public void setGRIBTemp(float t)
        {
          tempValue = t;
          repaint();
        }

        public void setGRIBprate(float prate)
        {
          prateValue = prate;
          repaint();
        }

        public void setGRIBData(int twd, float tws, float prmsl, float hgt500, float wh, float t, float prate)
        {
          truewindspeed = tws;
          truewinddir = twd;
          prmslValue = prmsl;
          hgt500Value = hgt500;
          waveHeightValue = wh;
          tempValue = t;
          prateValue = prate;
          repaint();
        }
      });
  }

  private final static int BORDER_TICKNESS = 5;
  private final static int COLOR_OPACITY   = 200; // 0-255
  private final static int FONT_SIZE = 8;

  private final static DecimalFormat[] FMTS = { 
                                                new DecimalFormat("##0.0 'kts'"), 
                                                new DecimalFormat("##0.0 'mb'"), 
                                                new DecimalFormat("##0 'm'"), 
                                                new DecimalFormat("##0.0 'm'"), 
                                                new DecimalFormat("##0'°C'"), 
                                                new DecimalFormat("##0.00 'mm/h'"),
                                              };
  private final static DecimalFormat DIR_FMT = new DecimalFormat("##0'°'");
  private final static Color GRIB_DATA_TEXT_COLOR = Color.blue;
  
  public void paintComponent(Graphics g)
  {
    int xOffset = BORDER_TICKNESS, 
        yOffset = BORDER_TICKNESS;
    int w = this.getWidth() - (2 * BORDER_TICKNESS);
    int h = (this.getHeight() - (2 * BORDER_TICKNESS)) / 2;
    super.paintComponent(g);
    
    Font origFont = g.getFont();
    Font smallFont = new Font(origFont.getName(), origFont.getStyle(), FONT_SIZE);
    g.setFont(smallFont);

    for (int i=0; i<6; i++)
    {
      int displayValue = 0;
      float currValue = 0;
      String displayString = "";
      Color startColor = null; // bottom (low)
      Color endColor   = null; // top    (high)
      switch (i)
      {
        case 0: // TWS
          currValue = truewindspeed;
          displayValue = (int) (currValue * ((float)h / 70f)); // [0, 70]
          startColor = new Color(193, 216, 217, COLOR_OPACITY);
          endColor   = new Color(  0,   0, 128, COLOR_OPACITY);
          break;
        case 1: // PRMSL
          currValue = prmslValue / 100f;
//        System.out.println("PRMSL:" + prmslValue);
          displayValue = (int) ((currValue - 950f) * ((float)h / 100f)); // [950, 1050]
          startColor = new Color(255,   0,   0, COLOR_OPACITY);
          endColor   = new Color(255, 128, 192, COLOR_OPACITY);
          break;
        case 2: // HGT500
          currValue = hgt500Value;
          displayValue = (int) ((currValue - 4500f) * ((float)h / 1500f)); // [4500, 6000]
//        System.out.println("HGT500:" + hgt500Value);
          endColor   = new Color(  0, 255, 255, COLOR_OPACITY);
          startColor = new Color(  0,   0, 255, COLOR_OPACITY);
          break;
        case 3: // WAVES
//        System.out.println("Waves Height:" + waveHeightValue);
          currValue = waveHeightValue / 100f;
          displayValue = (int) (currValue * ((float)h / 10f)); // [0, 10]
          startColor = new Color(128, 255, 128, COLOR_OPACITY);
          endColor   = new Color(  0, 128,   0, COLOR_OPACITY);
          break;
        case 4: // TEMP
          startColor = new Color(128, 255, 255, COLOR_OPACITY);
          endColor   = new Color(255,   0,   0, COLOR_OPACITY);
          currValue = tempValue - 273f;
//        System.out.println("TEMP:" + tempValue);
          displayValue = (int) ((currValue + 50f) * ((float)h / 100f)); // [-50, 50]
          break;
        case 5: // PRATE
//        System.out.println("PRATE:" + prateValue);
          currValue = prateValue * 3600f;
          displayValue = (int) (currValue * ((float)h / 20f)); // [0, 20]
          startColor = new Color(255, 255, 255, COLOR_OPACITY);
          endColor   = new Color(  0,   0,   0, COLOR_OPACITY);
          break;
        default:
          break;
      }
      if (displayValue == 0F)
        continue;
      
      GradientPaint gradient = new GradientPaint(0, this.getHeight(), startColor, 0, 0, endColor); // vertical, upside down
      ((Graphics2D)g).setPaint(gradient);
      g.fillRect(xOffset + (i * (w / 6)), h - yOffset - displayValue, w / 6, displayValue);
      g.setColor(GRIB_DATA_TEXT_COLOR);
      displayString = FMTS[i].format(currValue);
      int l = g.getFontMetrics(smallFont).stringWidth(displayString);
      int x = xOffset + (i * (w / 6)) + (w / (2 * 6)) - (l / 2);
      int y = h - yOffset - 5;
      g.drawString(displayString, x, y);
    }

    // Wind dir - Display
    int displayCenterX = this.getWidth() / 4;
    int displayCenterY = 3 * this.getHeight() / 4;
    int displayRadius = Math.min(this.getWidth() / 4, this.getHeight() / 4) - BORDER_TICKNESS;
    int displayBackGroundOpacity = 125;
    // Display shadow
    g.setColor(new Color(Color.gray.getRed(), Color.gray.getGreen(), Color.gray.getBlue(), displayBackGroundOpacity));
    g.fillOval(displayCenterX - displayRadius + 3,
               displayCenterY - displayRadius + 3,
               2 * displayRadius,
               2 * displayRadius);
    // Display
    g.setColor(new Color(0, 0, 0, displayBackGroundOpacity));
    g.fillOval(displayCenterX - displayRadius,
               displayCenterY - displayRadius,
               2 * displayRadius,
               2 * displayRadius);
    g.setColor(new Color(255, 255, 255, displayBackGroundOpacity));
    g.fillOval(displayCenterX - (displayRadius - 4),
               displayCenterY - (displayRadius - 4),
               2 * (displayRadius - 4),
               2 * (displayRadius - 4));
    // Rose
    int externalCircleRadius = (int)((double)displayRadius * 0.85);
    int internalCircleRadius = (int)((double)displayRadius * 0.25);
    g.setColor(new Color(0, 0, 0, displayBackGroundOpacity));
    for (int d=0; d<360; d+=90)
    {
      Point one   = new Point(displayCenterX, 
                              displayCenterY);
      Point two   = new Point((int)(displayCenterX + (Math.sin(Math.toRadians(d - 45)) * internalCircleRadius)), 
                              (int)(displayCenterY + (Math.cos(Math.toRadians(d - 45)) * internalCircleRadius)));
      Point three   = new Point((int)(displayCenterX + (Math.sin(Math.toRadians(d)) * externalCircleRadius)), 
                                (int)(displayCenterY  + (Math.cos(Math.toRadians(d)) * externalCircleRadius)));
      g.fillPolygon(new Polygon(new int[] {one.x, two.x, three.x},
                                new int[] {one.y, two.y, three.y},
                                3));      
    }
    g.setColor(Color.white);
    int fontSize = h / 10;
    Font cardFont = new Font(origFont.getName(), origFont.getStyle(), fontSize);
    g.setFont(cardFont);
    String card = "N";
    int l = g.getFontMetrics(cardFont).stringWidth(card);
    int x = displayCenterX - (l / 2);
    int y = displayCenterY - externalCircleRadius + (fontSize / 2);
    g.drawString(card, x, y);
    card = "S";
    l = g.getFontMetrics(cardFont).stringWidth(card);
    x = displayCenterX - (l / 2);
    y = displayCenterY + externalCircleRadius + (fontSize / 2);
    g.drawString(card, x, y);
    card = "W";
    l = g.getFontMetrics(cardFont).stringWidth(card);
    x = displayCenterX - externalCircleRadius - (l / 2);
    y = displayCenterY + (fontSize / 2);
    g.drawString(card, x, y);
    card = "E";
    l = g.getFontMetrics(cardFont).stringWidth(card);
    x = displayCenterX + externalCircleRadius - (l / 2);
    y = displayCenterY + (fontSize / 2);
    g.drawString(card, x, y);
    
    // Wind dir - Hand
    int handEndX = displayCenterX + (int)((displayRadius - 8) * Math.sin(Math.toRadians(truewinddir)));
    int handEndY = displayCenterY - (int)((displayRadius - 8) * Math.cos(Math.toRadians(truewinddir)));;
    internalCircleRadius = (int)((double)displayRadius * 0.10);
    Point pOne = new Point((int)(displayCenterX + (Math.sin(Math.toRadians(truewinddir - 70)) * internalCircleRadius)), 
                           (int)(displayCenterY - (Math.cos(Math.toRadians(truewinddir - 70)) * internalCircleRadius)));
    Point pTwo = new Point((int)(displayCenterX + (Math.sin(Math.toRadians(truewinddir + 70)) * internalCircleRadius)), 
                           (int)(displayCenterY - (Math.cos(Math.toRadians(truewinddir + 70)) * internalCircleRadius)));    
    // Hand Shadow
    g.setColor(Color.gray);
//  g.drawLine(displayCenterX + 3, displayCenterY + 3, handEndX + 3, handEndY + 3);    
    g.fillPolygon(new Polygon(new int[] { pOne.x + 3, handEndX + 3, pTwo.x + 3, displayCenterX + 3 },
                              new int[] { pOne.y + 3, handEndY + 3, pTwo.y + 3, displayCenterY + 3 },
                              4));
    // Hand
    g.setColor(new Color(Color.blue.getRed(), Color.blue.getGreen(), Color.blue.getBlue(), COLOR_OPACITY));
//  g.drawLine(displayCenterX, displayCenterY, handEndX, handEndY);    
    g.fillPolygon(new Polygon(new int[] { pOne.x, handEndX, pTwo.x, displayCenterX },
                              new int[] { pOne.y, handEndY, pTwo.y, displayCenterY },
                              4));
    // Center
    g.setColor(new Color(0, 0, 0, displayBackGroundOpacity));
    g.fillOval(displayCenterX - 4,
               displayCenterY - 4,
               2 * 4,
               2 * 4);

    // Text data
    Font dataFont = new Font("Courier", origFont.getStyle(), 10);
    g.setFont(dataFont);
    g.setColor(GRIB_DATA_TEXT_COLOR);

    int startX = (w / 2) + BORDER_TICKNESS;
    int startY = (this.getHeight() / 2) + FONT_SIZE;
    g.drawString("TWS    " + FMTS[0].format(truewindspeed), startX, startY);
    startY += (FONT_SIZE * 1.2);
    g.drawString("TWD    " + DIR_FMT.format(truewinddir), startX, startY);
    startY += (FONT_SIZE * 1.2);
    g.drawString("PRMSL  " + FMTS[1].format(prmslValue / 100f), startX, startY);
    startY += (FONT_SIZE * 1.2);
    g.drawString("500HGT " + FMTS[2].format(hgt500Value), startX, startY);
    startY += (FONT_SIZE * 1.2);
    g.drawString("WAVES  " + FMTS[3].format(waveHeightValue / 100f), startX, startY);
    startY += (FONT_SIZE * 1.2);
    g.drawString("TEMP   " + FMTS[4].format(tempValue - 273f), startX, startY);
    startY += (FONT_SIZE * 1.2);
    g.drawString("PRATE  " + FMTS[5].format(prateValue * 3600f), startX, startY);
    startY += (FONT_SIZE * 1.2);

    g.setFont(origFont);
  }
  
  public static void main1(String[] args)
  {
    float f = 12.3654654f;
    for (int i=0; i<FMTS.length; i++)
      System.out.println(FMTS[i].format(f));
  }

}
