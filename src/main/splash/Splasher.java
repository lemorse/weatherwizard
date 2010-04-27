package main.splash;

import chartview.util.WWGnlUtilities;

public class Splasher
{
  public static void main(String[] args)
  {
    System.out.println(WWGnlUtilities.buildMessage("welcome"));
    SplashWindow.splash(Splasher.class.getResource("LogiSail.png"));
    SplashWindow.invokeMain("main.ChartAdjust", args);
    SplashWindow.disposeSplash();
  }  
}