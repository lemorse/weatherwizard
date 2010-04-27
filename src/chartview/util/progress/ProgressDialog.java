package chartview.util.progress;

import chartview.ctx.WWContext;

import chartview.util.WWGnlUtilities;

import java.awt.BorderLayout;
import java.awt.Dialog;

import java.awt.Dimension;
import java.awt.Frame;

import java.awt.HeadlessException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ProgressDialog
  extends JDialog
  implements ChangeListener
{
  JLabel statusLabel = new JLabel();
  JProgressBar progressBar = new JProgressBar();
  ProgressMonitor monitor;
  boolean showButton = true;

  public ProgressDialog(Frame owner, ProgressMonitor monitor, boolean showButton)
    throws HeadlessException
  {
    super(owner, "Progress", true);
    this.showButton = showButton;
    init(monitor);
  }

  public ProgressDialog(Frame owner, ProgressMonitor monitor)
    throws HeadlessException
  {
    this(owner, monitor, true);
  }

  public ProgressDialog(Dialog owner, ProgressMonitor monitor)
    throws HeadlessException
  {
    super(owner);
    init(monitor);
  }

  public ProgressDialog(Dialog owner, ProgressMonitor monitor, boolean showButton)
    throws HeadlessException
  {
    super(owner);
    this.showButton = showButton;
    init(monitor);
  }

  private void init(ProgressMonitor monitor)
  {
    this.monitor = monitor;

    progressBar = new JProgressBar(0, monitor.getTotal());
    progressBar.setPreferredSize(new Dimension(300, 20));
    if (monitor.isIndeterminate())
      progressBar.setIndeterminate(true);
    else
      progressBar.setValue(monitor.getCurrent());
    statusLabel.setText(monitor.getStatus());

    JPanel contents = (JPanel) getContentPane();
    contents.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    contents.setLayout(new BorderLayout());
    contents.add(statusLabel, BorderLayout.NORTH);
    contents.add(progressBar, BorderLayout.CENTER);
    if (showButton)
    {
      JPanel bottomPanel = new JPanel();
      JButton interruptButton = new JButton(WWGnlUtilities.buildMessage("hide"));
      bottomPanel.add(interruptButton, null);
      contents.add(bottomPanel, BorderLayout.SOUTH);
      interruptButton.addActionListener(new ActionListener()
       {
          public void actionPerformed(ActionEvent e)
          {
  //        System.out.println("Canceling");
            WWContext.getInstance().fireInterruptProcess();
            dispose();
          }
        });
    }

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    monitor.addChangeListener(this);
  }

  public void stateChanged(final ChangeEvent ce)
  {
    // to ensure EDT thread
    if (!SwingUtilities.isEventDispatchThread())
    {
      SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            stateChanged(ce);
          }
        });
      return;
    }

    if (monitor.getCurrent() != monitor.getTotal()) // Not finished yet
    {
      statusLabel.setText(monitor.getStatus());
      if (!monitor.isIndeterminate())
        progressBar.setValue(monitor.getCurrent());
    }
    else
      dispose();
  }
}

