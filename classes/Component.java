import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;

public class Component {
   
   protected int x, y;
   
   protected int width, height;
   
   protected Color color;

/**
* Creates a new Component at location (x, y) with size (w, h), with a color of c.
*/
   public Component(int x, int y, int w, int h, Color c) {
      this(x, y, w, h);
      setColor(c);
   }

/**
* Creates a new Component at location (x, y) with size (w, h).
*/
   public Component(int x, int y, int w, int h) {
      setLocation(x, y);
      setSize(w, h);
   }

/**
* Changes size to be (w, h).
*/
   public void setSize(int w, int h) {
      width = w;
      height = h;
   }

/**
* Changes location to be (x, y).
*/
   public void setLocation(int x, int y) {
      this.x = x;
      this.y = y;
   }
   
/**
* Sets color to c.
*/
   public void setColor(Color c) {
      color = c;
   }

/**
* Returns the x value of this object's location.
* @return   an int of the x value of this object's location.
*/
   public int getX() {
      return x;
   }
   
/**
* Returns the y value of this object's location.
* @return   an int of the y value of this object's location.
*/
   public int getY() {
      return y;
   }
   
/**
* Returns the width of this object's size.
* @return   an int of the width of this object's size.
*/
   public int getWidth() {
      return width;
   }
   
/**
* Returns the height of this object's size.
* @return   an int of the height of this object's size.
*/
   public int getHeight() {
      return height;
   }
   
/**
* Returns the Color of this object.
* @return   the Color of this object.
*/
   public Color getColor() {
      return color;
   }

/**
* Returns an array of JComponents, representing components inside of extended classes of this container Component. Otherwise, defaults to null.
* @return   an array of JComponents, representing components inside of extended classes of this container Component. Otherwise, defaults to null.
*/
   public JComponent[] getComponents() {
      return null;
   }
   
/**
* Paints components inside of extended classes of this container Component. Otherwise, does nothing.
*/
   protected void paintComponent(Graphics g) { }
}