import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;
import java.util.ArrayList;

/** More personalized and liberal version of a JButton */
public class Container extends JComponent implements MouseInputListener{
   
   protected Color defaultBackgroundColor = Color.white;
   
   protected Color borderColor = Color.black;
/** Background color of this Button */
   protected Color backgroundColor = Color.white;
   
/** Text to be displayed inside this Button, aligns to be centered vertically and horizontally */
   protected String text;
   
   protected boolean entered;
   
   protected boolean opened;
   
   protected int defaultX;
   
   protected int defaultY;
   
   protected int defaultWidth;
   
   protected int defaultHeight;
   
   protected ArrayList<Container> children;
   
   protected Container parent;
   
/**
* Creates a new Button at location (x, y) with size (w, h), with a color of c and with a text of t.
* @param x the x-coord of the location of the component.
* @param y the y-coord of the location of the component.
* @param w the width of the component.
* @param h the height of the component.
* @param c the color of the component.
* @param t the text of the component.
*/
   public Container(int x, int y, int w, int h, Color c, String t) {
      this(x, y, w, h);
      setColor(c);
      setText(t);
   }
   
   public Container(int x, int y, int w, int h, Color c) {
      this(x, y, w, h);
      setColor(c);
   }
   
   public Container(int x, int y, int w, int h, String t) {
      this(x, y, w, h);
      setText(t);
   }
   
   public Container(int x, int y, int w, int h) {
      children = new ArrayList<Container>();
      defaultX = x;
      defaultY = y;
      defaultWidth = w;
      defaultHeight = h;
      setLocation(x, y);
      setSize(w, h);
      addMouseListener(this);
      addMouseMotionListener(this);
   }
   
   public void add(Container c) {
      children.add(c);
      updateContainer();
   }

/** Sets color to c.
* @param c the color to set the background color to.
*/
   public void setColor(Color c) {
      backgroundColor = c;
   }

/** Sets text to t.
* @param t the text to set the button's text to.
*/
   public void setText(String t) {
      text = t;
   }
   
   public int getWidth() {
      if (opened)
         return (int)getSize().getWidth();
      return defaultWidth;
   }
   
   public int getHeight() {
      if (opened)
         return (int)getSize().getHeight();
      return defaultHeight;
   }
   
   public int getX() {
      if (opened)
         return (int)getLocation().getX();
      return defaultX;
   }
   
   public int getY() {
      if (opened)
         return (int)getLocation().getY();
      return defaultY;
   }
   
/** Paints the button. Halves the background color if the mouse has entered the button.
* @param g the graphics component used to paint the button.
*/
   protected void paintComponent(Graphics g) {
      g = g.create();
      if (entered) {
         g.setColor(defaultBackgroundColor.darker());
      }
      else
         g.setColor(defaultBackgroundColor);
      g.fillRect(defaultX - getX(), defaultY - getY(), defaultWidth, defaultHeight);
      g.setColor(borderColor);
      g.drawRect(defaultX - getX(), defaultY - getY(), defaultWidth, defaultHeight);
      if (text != null && text.length() > 0) {
         g.drawString(text, defaultWidth / 2 - text.length() - g.getFontMetrics().stringWidth(text) / 3, defaultHeight / 2);
      }
      if (opened) {
         g.setColor(backgroundColor);
         g.fillRect(0, defaultY + defaultHeight, getWidth(), getHeight());
         g.setColor(borderColor);
         g.drawRect(0, defaultY + defaultHeight, getWidth(), getHeight());
         for (Container c : children)
            c.paintComponent(g);
      }
   }
   
   public void updateContainer() {
      int maxWidth = defaultWidth > getWidth() ? defaultWidth : getWidth();
      for (Container c : children) {
         setSize(maxWidth > c.getWidth() ? maxWidth : c.getWidth(), defaultHeight + c.getHeight());
      }
   }
   
         /** Called when the button is clicked. */
   public void mouseClicked(MouseEvent e) {
   }
      
      /** Called when the mouse is pressed. */
   public void mousePressed(MouseEvent e) {
      opened = ! opened;
      if (parent != null) {
         parent.updateContainer();
         parent.repaint();
      }
      else
         repaint();
   }
      
      /** Called when the mouse is released. */
   public void mouseReleased(MouseEvent e) {
      
   }
      
      /** Called when the mouse is moved. */
   public void mouseMoved(MouseEvent e) {
      
   }
      
      /** Called when the mouse is dragged. */
   public void mouseDragged(MouseEvent e) {
      
   }
      
      /** Called when the mouse enters the button. Used to change button color. */
   public void mouseEntered(MouseEvent e) {
      entered = true;
      repaint();
   }
      
      /** Called when the mouse exits the button. Used to change button color. */
   public void mouseExited(MouseEvent e) {
      entered = false;
      repaint();
   }
   
}