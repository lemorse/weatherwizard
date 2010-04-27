package chartview.gui.right;

import java.awt.Dimension;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;

import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import java.net.URL;

import javax.swing.ImageIcon;

public class DraggableFlag
     extends ImageIcon
  implements DragGestureListener, 
             DragSourceListener
{
  DragSource dragSource;

  public DraggableFlag(URL resource)
  {
    super(resource);
//  super();
//  this.setIcon(new ImageIcon(resource));
//  this.setImage(new ImageIcon(this.getClass().getResource("greenflag.png")).getImage());
    dragSource = new DragSource();
    dragSource.createDefaultDragGestureRecognizer(this.component, DnDConstants.ACTION_COPY_OR_MOVE, this);
    Dimension compSize = this.component.getSize();
    System.out.println("Constructor");
    System.out.println("Size:" + compSize.width + " x " + compSize.height);
  }

  public void dragGestureRecognized(DragGestureEvent evt)
  {
    Transferable t = new StringSelection("aString");
    dragSource.startDrag(evt, DragSource.DefaultCopyDrop, t, this);
    System.out.println("dragGestureRecognized");
//  System.out.println("At x:" + evt.getLocation().x + ", y:" + evt.getLocation().y);
  }

  public void dragEnter(DragSourceDragEvent evt)
  {
    // Called when the user is dragging this drag source and enters
    // the drop target.
    System.out.println("DragEnter");
    System.out.println("At x:" + evt.getLocation().x + ", y:" + evt.getLocation().y);
  }

  public void dragOver(DragSourceDragEvent evt)
  {
    // Called when the user is dragging this drag source and moves
    // over the drop target.
    System.out.println("dragOver");
    System.out.println("At x:" + evt.getLocation().x + ", y:" + evt.getLocation().y);
  }

  public void dragExit(DragSourceEvent evt)
  {
    // Called when the user is dragging this drag source and leaves
    // the drop target.
    System.out.println("dragExit");
    System.out.println("At x:" + evt.getLocation().x + ", y:" + evt.getLocation().y);
  }

  public void dropActionChanged(DragSourceDragEvent evt)
  {
    // Called when the user changes the drag action between copy or move.
    System.out.println("dropActionChanged");
    System.out.println("At x:" + evt.getLocation().x + ", y:" + evt.getLocation().y);
  }

  public void dragDropEnd(DragSourceDropEvent evt)
  {
    // Called when the user finishes or cancels the drag operation.
    System.out.println("dragDropEnd");
    System.out.println("At x:" + evt.getLocation().x + ", y:" + evt.getLocation().y);
  }
}
