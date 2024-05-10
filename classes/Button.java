import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;

/** More personalized and liberal version of a JButton */
public class Button extends JComponent {

/** Location of this Button */
   protected int x, y;
   
/** Size of this Button */
   protected int width, height;
   
/** Background color of this Button */
   protected Color backgroundColor;
   
/** Text to be displayed inside this Button, aligns to be centered vertically and horizontally */
   protected String text;
   
/** MouseListener to display graphical changes when mouse enters and exits button */
   protected Listener listener;
   
/** Helper boolean to store whether mouse has entered button */
   protected boolean entered;
   
/**
* Creates a new Button at location (x, y) with size (w, h), with a color of c and with a text of t.
* @param x the x-coord of the location of the component.
* @param y the y-coord of the location of the component.
* @param w the width of the component.
* @param h the height of the component.
* @param c the color of the component.
* @param t the text of the component.
*/
   public Button(int x, int y, int w, int h, Color c, String t) {
      this(x, y, w, h);
      setColor(c);
      setText(t);
   }
   
/**
* Creates a new Button at location (x, y) with size (w, h), with a color of c.
* @param x the x-coord of the location of the component.
* @param y the y-coord of the location of the component.
* @param w the width of the component.
* @param h the height of the component.
* @param c the color of the component.
*/
   public Button(int x, int y, int w, int h, Color c) {
      this(x, y, w, h);
      setColor(c);
   }
   
/**
* Creates a new Button at location (x, y) with size (w, h).
* @param x the x-coord of the location of the component.
* @param y the y-coord of the location of the component.
* @param w the width of the component.
* @param h the height of the component.
*/
   public Button(int x, int y, int w, int h) {
      super();
      setLocation(this.x = x, this.y = y);
      setSize(width = w, height = h);
      addMouseListener(listener = new Listener(this));
      addMouseMotionListener(listener);
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
   
/** Paints the button. Halves the background color if the mouse has entered the button.
* @param g the graphics component used to paint the button.
*/
   protected void paintComponent(Graphics g) {
      g = g.create();
      if (entered) {
         g.setColor(getGreyScale(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue()));
      }
      else
         g.setColor(backgroundColor);
      g.fillRect(0, 0, width, height);
      g.setColor(Color.black);
      g.drawRect(0, 0, width, height);
      if (text != null && text.length() > 0) {
         g.drawString(text, width / 2 - text.length() - g.getFontMetrics().stringWidth(text) / 3, height / 2);
      }
   }
   
/** Returns color used for when the mouse enters the button.
* @param red the red int of the color.
* @param green the green int of the color.
* @param blue the blue int of the color.
* @return a new color that has been greyscaled.
*/
   protected Color getGreyScale(int red, int green, int blue) {
      return new Color(red / 2, green / 2, blue / 2);
   }
   
/** Generic toString() method.
* @return String describing this Object.
*/
   public String toString() {
      String temp = "Button (int x, int y, int width, int height, Color backgroundColor, String text, Listener listener, boolean entered) : [\n\t" + x + "\n\t" + y + "\n\t" + width + "\n\t" + height + "\n\t" + backgroundColor + "\n\t" + text + "\n\t" + listener + "\n\t" + entered + "\n]";
      return temp;
   }
   
         /** Called when the button is clicked. */
   public void mouseClicked(MouseEvent e) {
   }
      
      /** Called when the mouse is pressed. */
   public void mousePressed(MouseEvent e) {
      
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
      this.repaint();
   }
      
      /** Called when the mouse exits the button. Used to change button color. */
   public void mouseExited(MouseEvent e) {
      entered = false;
      this.repaint();
   }
   
/** Custom MouseInputListener class for button */
   protected class Listener implements MouseInputListener {
      
      public Button owner;
      
      /** Creates new Listener */
      public Listener(Button owner) {
         this.owner = owner;
      }
      
      /** Called when the button is clicked. */
      public void mouseClicked(MouseEvent e) {
         owner.mouseClicked(e);
      }
      
      /** Called when the mouse is pressed. */
      public void mousePressed(MouseEvent e) {
         owner.mousePressed(e);
      }
      
      /** Called when the mouse is released. */
      public void mouseReleased(MouseEvent e) {
         owner.mouseReleased(e);
      }
      
      /** Called when the mouse is moved. */
      public void mouseMoved(MouseEvent e) {
         owner.mouseMoved(e);
      }
      
      /** Called when the mouse is dragged. */
      public void mouseDragged(MouseEvent e) {
         owner.mouseDragged(e);
      }
      
      /** Called when the mouse enters the button. Used to change button color. */
      public void mouseEntered(MouseEvent e) {
         owner.mouseEntered(e);
      }
      
      /** Called when the mouse exits the button. Used to change button color. */
      public void mouseExited(MouseEvent e) {
         owner.mouseExited(e);
      }
   }
   
}