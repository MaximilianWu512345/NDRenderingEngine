import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicBorders.ButtonBorder;

public class Button extends JPanel {

   protected int x, y;
   
   protected int width, height;
   
   protected Color backgroundColor;
   
   protected Listener listener;
   
   protected byte switches;
   
   protected byte ENTERED = 1;
   
   public Button(int x, int y, int w, int h, Color c) {
      this(x, y, w, h);
      setColor(c);
   }
   
   public Button(int x, int y, int w, int h) {
      super();
      //setLocation(this.x = x, this.y = y);
      setSize(width = w, height = h);
      addMouseListener(listener = new Listener());
      addMouseMotionListener(listener);
      System.out.println(width + "" + height);
      System.out.println(getSize());
      System.out.println(getPreferredSize());
      System.out.println(getMaximumSize());
      setBorder(new ButtonBorder(Color.black, Color.black, Color.black, Color.black));
   }
   
   public void setColor(Color c) {
      backgroundColor = c;
   }
   
   protected void paintComponent(Graphics g) {
      g = g.create();
      if ((switches & (1 << ENTERED)) != 0) {
         int color = (int)(backgroundColor.getRed() + backgroundColor.getGreen() + backgroundColor.getBlue()) / 3;
         g.setColor(new Color(color, color, color));
      }
      else
         g.setColor(backgroundColor);
      g.fillRect(0, 0, width, height);
      g.setColor(Color.black);
      g.drawRect(0, 0, width, height);
   }
   
   
   protected class Listener implements MouseInputListener {
      
      public Listener() {
      
      }
      
      public void mouseClicked(MouseEvent e) {
         System.out.println(e.getX() + " "+ e.getY());
      }
      
      public void mousePressed(MouseEvent e) {
      
      }
      
      public void mouseReleased(MouseEvent e) {
      
      }
      
      public void mouseMoved(MouseEvent e) {
         switches |= (1 << ENTERED);
         paintComponent(getGraphics());
         if (e.getX() >= width || e.getY() >= height || e.getX() < 0 || e.getY() < 0) {
            switches &= ~(1 << ENTERED);
            paintComponent(getGraphics());
         }
      }
      
      public void mouseDragged(MouseEvent e) {
      
      }
      
      public void mouseEntered(MouseEvent e) {
      }
      
      public void mouseExited(MouseEvent e) { 
      }

   }
   
}