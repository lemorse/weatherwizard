package chartview.util.progress;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ProgressUtil
{
  static class MonitorListener
    implements ChangeListener, ActionListener
  {
    ProgressMonitor monitor;
    Window owner;
    Timer timer;
    boolean showInterruptButton = true;

    public MonitorListener(Window owner, ProgressMonitor monitor, boolean showButton)
    {
      this.owner = owner;
      this.monitor = monitor;
      this.showInterruptButton = showButton;
    }

    public MonitorListener(Window owner, ProgressMonitor monitor)
    {
      this(owner, monitor, true);
    }

    public void stateChanged(ChangeEvent ce)
    {
      ProgressMonitor monitor = (ProgressMonitor) ce.getSource();
      if (monitor.getCurrent() != monitor.getTotal())
      {
        if (timer == null)
        {
          int millisecondToWait = 500;
          timer = new Timer(millisecondToWait, this);
          timer.setRepeats(false);
          timer.start();
        }
      }
      else
      {
        if (timer != null && timer.isRunning())
          timer.stop();
        monitor.removeChangeListener(this);
      }
    }

    public void actionPerformed(ActionEvent e)
    {
      monitor.removeChangeListener(this);
      ProgressDialog dlg = owner instanceof Frame ? 
                              new ProgressDialog((Frame) owner, monitor, showInterruptButton) : 
                              new ProgressDialog((Dialog) owner, monitor, showInterruptButton);
      dlg.pack();
      dlg.setLocationRelativeTo(null);
      dlg.setVisible(true);
    }
  }

  public static ProgressMonitor createModalProgressMonitor(Component owner, int total, boolean indeterminate)
  {
    return createModalProgressMonitor(owner, total, indeterminate, true);
  }
  
  public static ProgressMonitor createModalProgressMonitor(Component owner, int total, boolean indeterminate, boolean showInterrupt)
  {
    ProgressMonitor monitor = new ProgressMonitor(total, indeterminate);
    Window window = owner instanceof Window ? (Window) owner : SwingUtilities.getWindowAncestor(owner);
    monitor.addChangeListener(new MonitorListener(window, monitor, showInterrupt));
    return monitor;
  }
}

