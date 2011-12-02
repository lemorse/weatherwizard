package chartview.gui;

import chartview.gui.toolbar.controlpanels.FaxPreviewPanel;

import chartview.util.WWGnlUtilities;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class ShiftTabPopup
  extends JPopupMenu
  implements ActionListener, PopupMenuListener
{
  JMenuItem shiftRight;
  JMenuItem shiftLeft;

  private static final String SHIFT_RIGHT = WWGnlUtilities.buildMessage("shift-right");
  private static final String SHIFT_LEFT  = WWGnlUtilities.buildMessage("shift-left");

  AdjustFrame parent;
  int tab = -1;
  
  public ShiftTabPopup(AdjustFrame caller, int tab)
  {
    super();
    parent = caller;
    this.tab = tab;
    this.add(shiftRight = new JMenuItem(SHIFT_RIGHT));
    shiftRight.setIcon(new ImageIcon(this.getClass().getResource("img/panright.gif")));
    shiftRight.addActionListener(this);
    this.add(shiftLeft = new JMenuItem(SHIFT_LEFT));
    shiftLeft.setIcon(new ImageIcon(this.getClass().getResource("img/panleft.gif")));
    shiftLeft.addActionListener(this);
  }

  public void enableShiftRight(boolean b)
  {
    shiftRight.setVisible(b);
  }
  public void enableShiftLeft(boolean b)
  {
    shiftLeft.setVisible(b);
  }

  public void actionPerformed(ActionEvent event)
  {
    if (event.getActionCommand().equals(SHIFT_RIGHT))
    {
      parent.shiftTabRight(tab);
    }
    else if (event.getActionCommand().equals(SHIFT_LEFT))
    {
      parent.shiftTabLeft(tab);
    }
    this.setVisible(false); // Shut popup when done.
  }

  public void popupMenuWillBecomeVisible(PopupMenuEvent e)
  {
  }

  public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
  {
  }

  public void popupMenuCanceled(PopupMenuEvent e)
  {
  }

  public void show(Component c, int x, int y)
  {
    super.show(c, x, y);
  }
}
