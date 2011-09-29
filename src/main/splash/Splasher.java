package main.splash;

import chartview.ctx.ApplicationEventListener;
import chartview.ctx.WWContext;

import chartview.util.WWGnlUtilities;

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
    System.out.println(WWGnlUtilities.buildMessage("welcome"));
    SplashWindow.splash(Splasher.class.getResource("LogiSail.png"));
    SplashWindow.invokeMain("main.ChartAdjust", args);
    SplashWindow.disposeSplash();
  }  
}