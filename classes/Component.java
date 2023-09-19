import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;

/** Container class to hold JComponents, acts like a JPanel that doesn't have to be added. */
public class Component {
   
/** Determines location of component. */
   protected int x, y;
   
/** Determines size of component. */
   protected int width, height;
   
/** Determines color of component. */
   protected Color color;

/**
* Creates a new Component at location (x, y) with size (w, h), with a color of c.
* @param x the x-coord of the location of the component.
* @param y the y-coord of the location of the component.
* @param w the width of the component.
* @param h the height of the component.
* @param c the color of the component.
*/
   public Component(int x, int y, int w, int h, Color c) {
      this(x, y, w, h);
      setColor(c);
   }

/**
* Creates a new Component at location (x, y) with size (w, h).
* @param x the x-coord of the location of the component.
* @param y the y-coord of the location of the component.
* @param w the width of the component.
* @param h the height of the component.
*/
   public Component(int x, int y, int w, int h) {
      setLocation(x, y);
      setSize(w, h);
   }

/**
* Changes size to be (w, h).
* @param w the width to set the component to.
* @param h the height to set the component to.
*/
   public void setSize(int w, int h) {
      width = w;
      height = h;
   }

/**
* Changes location to be (x, y).
* @param x the x-coord to set the location to.
* @param y the y-coord to set the location to.
*/
   public void setLocation(int x, int y) {
      this.x = x;
      this.y = y;
   }
   
/**
* Sets color to c.
* @param c the color to change the component to.
*/
   public void setColor(Color c) {
      color = c;
   }

/**
* Returns the x value of this object's location.
* @return an int of the x value of this object's location.
*/
   public int getX() {
      return x;
   }
   
/**
* Returns the y value of this object's location.
* @return an int of the y value of this object's location.
*/
   public int getY() {
      return y;
   }
   
/**
* Returns the width of this object's size.
* @return an int of the width of this object's size.
*/
   public int getWidth() {
      return width;
   }
   
/**
* Returns the height of this object's size.
* @return an int of the height of this object's size.
*/
   public int getHeight() {
      return height;
   }
   
/**
* Returns the Color of this object.
* @return the Color of this object.
*/
   public Color getColor() {
      return color;
   }

/**
* Returns an array of JComponents, representing components inside of extended classes of this container Component. Otherwise, defaults to null.
* @return an array of JComponents, representing components inside of extended classes of this container Component. Otherwise, defaults to null.
*/
   public JComponent[] getComponents() {
      return null;
   }
   
/**
* Paints components inside of extended classes of this container Component. Otherwise, does nothing.
* @param g uses graphics to paint components inside this component container class.
*/
   protected void paintComponent(Graphics g) { }
}