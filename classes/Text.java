import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Color;

/** More personalized and liberal version of a JButton */
public class Text extends JComponent {
   
/** Background color of this Button */
   protected Color color;
   
/** Text to be displayed inside this Button, aligns to be centered vertically and horizontally */
   protected String text;
   
/**
* Creates a new Text at location (x, y) with size (w, h), with a color of c and with a text of t.
* @param x the x-coord of the location of the component.
* @param y the y-coord of the location of the component.
* @param w the width of the component.
* @param h the height of the component.
* @param c the color of the component.
* @param t the text of the component.
*/
   public Text(int x, int y, int w, int h, Color c, String t) {
      this(x, y, w, h, c);
      this.setText(t);
   }
   
/**
* Creates a new Text at location (x, y) with size (w, h), with a color of c.
* @param x the x-coord of the location of the component.
* @param y the y-coord of the location of the component.
* @param w the width of the component.
* @param h the height of the component.
* @param c the color of the component.
*/
   public Text(int x, int y, int w, int h, Color c) {
      this(x, y, w, h);
      this.setColor(c);
   }
   
/**
* Creates a new Text at location (x, y) with size (w, h).
* @param x the x-coord of the location of the component.
* @param y the y-coord of the location of the component.
* @param w the width of the component.
* @param h the height of the component.
*/
   public Text(int x, int y, int w, int h) {
      super();
      this.setLocation(x, y);
      this.setSize(w, h);
   }
   
/** Sets color to c.
* @param c the color to set the text color to.
*/
   public void setColor(Color c) {
      color = c;
   }

/** Sets text to t.
* @param t the text to set the text to.
*/
   public void setText(String t) {
      text = t;
   }
   
/** Paints the text.
* @param g the graphics component used to paint the text.
*/
   protected void paintComponent(Graphics g) {
      g = g.create();
      g.setColor(color);
      if (text != null && text.length() > 0) {
         g.drawString(text, (int)this.getSize().getWidth() / 2 - text.length() - g.getFontMetrics().stringWidth(text) / 3, (int)this.getSize().getHeight() / 2);
      }
   }
   
/** Generic toString() method.
* @return String describing this Object.
*/
   public String toString() {
      String temp = "Text (Color color, String text) : [" + color + ", " + text + "]";
      return temp;
   }
   
}