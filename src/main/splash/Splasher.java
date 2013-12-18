package main.splash;

import chartview.ctx.ApplicationEventListener;
import chartview.ctx.WWContext;

import chartview.util.WWGnlUtilities;

import java.lang.reflect.Method;

import javax.swing.ImageIcon;

import main.help.AboutBox;

public class Splasher
{
  static
  {
    WWContext.getInstance().addApplicationListener(new ApplicationEventListener()
                                                   {
                                                     public void applicationLoaded()
                                                     {
                                                       SplashWindow.disposeSplash();
                                                     }
                                                   });
  }
  public static void main(String[] args)
  {
    boolean headlessMode = "true".equals(System.getProperty("headless", "false"));
    if (!headlessMode)
    {
      System.out.println(WWGnlUtilities.buildMessage("welcome"));
  //  SplashWindow.splash(Splasher.class.getResource("LogiSail.png"));
      SplashWindow.splash(AboutBox.class.getResource("wizard150.png"));
      SplashWindow.invokeMain("main.ChartAdjust", args);
      SplashWindow.disposeSplash();
    }
    else
    {
      try
      {
        Class<?> main = Class.forName("main.ChartAdjust");
        Method mainMethod = main.getMethod("main", String[].class);
        Object[] params = new Object[1];
        params[0] = args;
        mainMethod.invoke(null, params);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
        System.exit(1);
      }
    }
  }  
}