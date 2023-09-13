import java.awt.Color;
import java.awt.Graphics;

public class Component {
   
   protected int x, y;
   
   protected int width, height;
   
   protected Color color;

   public Component(int x, int y, int w, int h, Color c) {
      this(x, y, w, h);
      setColor(c);
   }
   
   public Component(int x, int y, int w, int h) {
      setLocation(x, y);
      setSize(w, h);
   }
   
   protected void setSize(int w, int h) {
      width = w;
      height = h;
   }
   
   protected void setLocation(int x, int y) {
      this.x = x;
      this.y = y;
   }
   
   protected void setColor(Color c) {
      color = c;
   }
   
   protected void paintComponent(Graphics g) { }
}