import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;
import java.util.ArrayList;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/** More personalized and liberal version of a JButton */
public class Container extends JComponent implements MouseInputListener, KeyListener {
   
   protected int fieldHeight = 20;
   
   protected int containerHeight = 20;
   
   protected int fieldWidthSpacing = 5;
   
   protected int heightSpacing = 5;
   
   protected Color defaultBackgroundColor = Color.white;
   
   protected Color borderColor = Color.black;
/** Background color of this Button */
   protected Color backgroundColor = Color.white;
   
   protected Color textColor = Color.black;
   
/** Text to be displayed inside this Button, aligns to be centered vertically and horizontally */
   protected String text;
   
   protected boolean entered;
   
   protected boolean opened;
   
   protected ArrayList<Container> children;
   
   protected ArrayList<DataField> fields;
   
   protected ArrayList<Integer> fieldsInfo;
   
   protected Container parent;
   
   protected int defaultHeight;

   public Container(Container parent, ArrayList<DataField> fields) {
      this(parent, fields == null ? (DataField[])null : fields.toArray(new DataField[0]));
   }
   
   public Container(Container parent, DataField[] fields) {
      children = new ArrayList<Container>();
      fieldsInfo = new ArrayList<Integer>();
      this.fields = new ArrayList<DataField>();
      for (DataField f : fields) {
         this.fields.add(f);
         fieldsInfo.add(0);
      }
      setParent(parent);
   }
   
   public void initialize(int x, int y, int w, int h, Color c, String t) {
      setLocation(x, y);
      setSize(w, h);
      defaultHeight = h;
      initialize(c, t);
      updateContainer();
   }
   
   public void initialize(Color c, String t) {
      if (c != null)
         backgroundColor = c;
      if (t != null)
         text = t;
   }
   
   public void initializeListeners() {
      addMouseListener(this);
      addMouseMotionListener(this);
   }
   
   public void setParent(Container parent) {
      if (parent == null)
         return;
      this.parent = parent;
      parent.add(this);
   }
   
   public void add(Container c) {
      if (c == null)
         return;
      children.add(c);
      c.initialize(getX(), getY() + getHeight(), getWidth(), containerHeight, null, null);
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
      return (int)getSize().getWidth();
   }
   
   public int getHeight() {
      return (int)getSize().getHeight();
   }
   
   public int getX() {
      return (int)getLocation().getX();
   }
   
   public int getY() {
      return (int)getLocation().getY();
   }
   
   public int getXParent() {
      if (parent == null)
         return getX();
      Container temp = parent;
      while (temp.parent != null) {
         temp = temp.parent;
      }
      return getX() - temp.getX();
   }
   
   public int getYParent() {
      if (parent == null)
         return getY();
      Container temp = parent;
      while (temp.parent != null) {
         temp = temp.parent;
      }
      return getY() - temp.getY();
   }
   
   protected void paintComponent(Graphics g) {
      paintComponent(g, parent == null ? 0 : getXParent(), parent == null ? 0 : getYParent());
   }
   
/** Paints the button. Halves the background color if the mouse has entered the button.
* @param g the graphics component used to paint the button.
*/
   protected void paintComponent(Graphics g, int x, int y) {
      g = g.create();
      g.setColor(defaultBackgroundColor);
      g.fillRect(x, y, getWidth(), getHeight());
      g.setColor(borderColor);
      g.drawRect(x, y, getWidth(), getHeight());
      int fieldTextHeight = g.getFontMetrics().getHeight() * 3 / 4;
      if (text != null && text.length() > 0) {
         int i = g.getFontMetrics().stringWidth(text);
         g.setColor(textColor);
         g.drawString(text, x + getWidth() / 2 - i / 2, y + fieldTextHeight);
      }
      for (int i = 0; i < fields.size(); i++) {
         DataField field = fields.get(i);
         Integer info = fieldsInfo.get(i);
         String fieldText = field.getName();
         String fieldValue = field.getValue();
         int fieldWidth = g.getFontMetrics().stringWidth(fieldText);
         g.setColor(textColor);
         g.drawString(fieldText, x, y + heightSpacing + fieldTextHeight * 2 + i * fieldHeight);
         g.setColor(info > 0 ? defaultBackgroundColor.darker() : defaultBackgroundColor);
         int boxX = x + fieldWidth + fieldWidthSpacing;
         int boxY = y + heightSpacing + fieldTextHeight + i * fieldHeight;
         int boxWidth = getWidth() - fieldWidth - fieldWidthSpacing - 1;
         g.fillRect(boxX, boxY, boxWidth, fieldHeight);
         g.setColor(borderColor);
         g.drawRect(boxX, boxY, boxWidth, fieldHeight);
         g.setColor(textColor);
         g.drawString(fieldValue, boxX + fieldWidthSpacing, boxY + fieldTextHeight);
      }
      for (int i = 0; i < children.size(); i++) {
         Container child = children.get(i);
         child.paintComponent(g);
      }
   }
   
   public boolean inBox(int index, int x, int y) {
      Graphics g = getGraphics();
      Container temp = parent;
      while (g == null && temp != null) {
         g = temp.getGraphics();
         temp = temp.parent;
      }
      if (g == null)
         return false;
      if (index >= 0 && index < fields.size()) {
         DataField field = fields.get(index);
         String fieldText = field.getName();
         String fieldValue = field.getValue();
         int fieldWidth = g.getFontMetrics().stringWidth(fieldText);
         int fieldTextHeight = g.getFontMetrics().getHeight() * 3 / 4;
         int offsetX = parent == null ? 0 : getXParent();
         int offsetY = parent == null ? 0 : getYParent();
         int boxX = offsetX + fieldWidth + fieldWidthSpacing;
         int boxY = offsetY + heightSpacing + fieldTextHeight + index * fieldHeight;
         int boxWidth = getWidth() - fieldWidth - fieldWidthSpacing - 1;
         if (x > boxX && x < boxX + boxWidth && y > boxY && y < boxY + fieldHeight)
            return true;
      }
      return false;
   }
   
   public void updateContainer() {
      int maxWidth = getWidth();
      int maxHeight = defaultHeight;
      for (DataField f : fields) {
         maxHeight += fieldHeight;
      }
      for (Container c : children) {
         if (c.getWidth() > maxWidth)
            maxWidth = c.getWidth();
         maxHeight += c.getHeight();
      }
      setSize(maxWidth, maxHeight);
      if (parent != null)
         parent.updateContainer();
   }

   public void keyPressed(KeyEvent e) {
      for (int i = 0; i < fields.size(); i++) {
         DataField field = fields.get(i);
         if (fieldsInfo.get(i) == 2 && field.canEdit()) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
               field.setValue();
            }
            else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
               String temp = field.getValue();
               if (temp.length() > 0) {
                  temp = temp.substring(0, temp.length() - 1);
                  field.setValue(temp);
               }
            }
            else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
               field.setValue(field.getValue() + " ");
            }
         }
      }
      for (Container child : children) {
         child.keyPressed(e);
      }
      repaint();
   }
      
   public void keyReleased(KeyEvent e) {
   }
      
   public void keyTyped(KeyEvent e) {
      for (int i = 0; i < fields.size(); i++) {
         DataField field = fields.get(i);
         if (fieldsInfo.get(i) == 2 && field.canEdit()) {
            if (Character.isLetterOrDigit(e.getKeyChar())) {
               field.setValue(field.getValue() + e.getKeyChar());
            }
         }
      }
      for (Container child : children) {
         child.keyTyped(e);
      }
      repaint();
   }
   
      /** Called when the button is clicked. */
   public void mouseClicked(MouseEvent e) {
   }
      
      /** Called when the mouse is pressed. */
   public void mousePressed(MouseEvent e) {
      for (int i = 0; i < fields.size(); i++) {
         DataField field = fields.get(i);
         if (inBox(i, e.getX(), e.getY())) {
            fieldsInfo.set(i, 2);
         }
         else {
            fieldsInfo.set(i, 0);
         }
      }
      opened = ! opened;
      for (Container child : children) {
         child.mousePressed(e);
      }
      updateContainer();
      if (parent != null)
         parent.updateContainer();
      repaint();
   }
      
      /** Called when the mouse is released. */
   public void mouseReleased(MouseEvent e) {
   
   }
      
      /** Called when the mouse is moved. */
   public void mouseMoved(MouseEvent e) {
      for (int i = 0; i < fields.size(); i++) {
         DataField field = fields.get(i);
         if (inBox(i, e.getX(), e.getY())) {
            if (fieldsInfo.get(i) == 0)
               fieldsInfo.set(i, 1);
         }
         else if (fieldsInfo.get(i) == 1) {
            fieldsInfo.set(i, 0);
         }
      }
      for (Container child : children) {
         child.mouseMoved(e);
      }
      repaint();
   }
      
      /** Called when the mouse is dragged. */
   public void mouseDragged(MouseEvent e) {
      
   }
      
      /** Called when the mouse enters the button. Used to change button color. */
   public void mouseEntered(MouseEvent e) {
   }
      
      /** Called when the mouse exits the button. Used to change button color. */
   public void mouseExited(MouseEvent e) {
   }
   
   public String toString() {
      String temp = getX() + " " + getY() + " " + getWidth() + " " + getHeight();
      for (Container c : children) {
         temp += "\n" + c;
      }
      return temp;
   }
}