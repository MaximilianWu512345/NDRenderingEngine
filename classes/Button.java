import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;

public class Button extends JComponent {

   protected int x, y;
   
   protected int width, height;
   
   protected Color backgroundColor;
   
   protected String text;
   
   protected Listener listener;
   
   protected byte switches;
   
   protected byte ENTERED = 1;
   
   public Button(int x, int y, int w, int h, Color c, String t) {
      this(x, y, w, h);
      setColor(c);
      setText(t);
   }
   
   public Button(int x, int y, int w, int h, Color c) {
      this(x, y, w, h);
      setColor(c);
   }
   
   public Button(int x, int y, int w, int h) {
      super();
      setLocation(this.x = x, this.y = y);
      setSize(width = w, height = h);
      addMouseListener(listener = new Listener());
      addMouseMotionListener(listener);
   }
   
   public void setColor(Color c) {
      backgroundColor = c;
   }
   
   public void setText(String t) {
      text = t;
   }
   
   protected void paintComponent(Graphics g) {
      g = g.create();
      if ((switches & (1 << ENTERED)) != 0) {
         g.setColor(getGreyScale(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue()));
      }
      else
         g.setColor(backgroundColor);
      g.fillRect(0, 0, width, height);
      g.setColor(Color.black);
      g.drawRect(0, 0, width, height);
      if (text != null && text.length() > 0) {
         g.drawString(text, width / 2 - text.length() * 3, height * 4 / 10);
      }
   }
   
   protected Color getGreyScale(int red, int green, int blue) {
      return new Color(red / 2, green / 2, blue / 2);
   }
   
   protected class Listener implements MouseInputListener {
   
      public Listener() { }
      
      public void mouseClicked(MouseEvent e) { }
      
      public void mousePressed(MouseEvent e) { }
      
      public void mouseReleased(MouseEvent e) { }
      
      public void mouseMoved(MouseEvent e) { }
      
      public void mouseDragged(MouseEvent e) { }
      
      public void mouseEntered(MouseEvent e) {
         switches |= (1 << ENTERED);
         paintComponent(getGraphics());
      }
      
      public void mouseExited(MouseEvent e) {
         switches &= ~(1 << ENTERED);
         paintComponent(getGraphics());
      }
   }
   
}