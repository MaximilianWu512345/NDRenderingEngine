import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;
import java.util.ArrayList;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/** Container for DataFields */
public class Container extends JComponent implements MouseInputListener, KeyListener {
   
      /** Height for DataFields */
   protected int fieldHeight = 20;
   
      /** Height for Container children */
   protected int containerHeight = 20;
   
      /** Spacing between DataField name and box */
   protected int fieldWidthSpacing = 5;
   
      /** Spacing between DataField */
   protected int heightSpacing = 5;
   
      /** Background Color */
   protected Color defaultBackgroundColor = Color.white;
   
      /** Border Color */
   protected Color borderColor = Color.black;
   
      /** Text Color */
   protected Color textColor = Color.black;
   
      /** Text to be displayed */
   protected String text;
   
      /** Helper boolean for painting */
   protected boolean entered;
   
      /** Helper boolean for checking if clicked */
   protected boolean opened;
   
      /** ArrayList of Container children */
   protected ArrayList<Container> children;
   
      /** ArrayList of DataFields */
   protected ArrayList<DataField> fields;
   
      /** ArrayList of information about the state of DataFields, 0 == default, 1 == entered, 2 == cliced */
   protected ArrayList<Integer> fieldsInfo;
   
      /** ArrayList of information of height of DataFields, used for size checking */
   protected ArrayList<Integer> fieldsHeight;
   
      /** Container parent */
   protected Container parent;
   
      /** Initial height when initialized */
   protected int defaultHeight;
   
      /** helper int to check current textIndex */
   protected int textIndex;
   
      /** Creates a new Container with a Container parent and DataField fields
      * @param parent parent of Container
      * @param fields fields of Container
      */
   public Container(Container parent, ArrayList<DataField> fields) {
      this(parent, fields == null ? (DataField[])null : fields.toArray(new DataField[0]));
   }
   
      /** Creates a new Container with a Container parent and DataField fields
      * @param parent parent of Container
      * @param fields fields of Container
      */
   public Container(Container parent, DataField[] fields) {
      children = new ArrayList<Container>();
      fieldsInfo = new ArrayList<Integer>();
      fieldsHeight = new ArrayList<Integer>();
      this.fields = new ArrayList<DataField>();
      for (DataField f : fields) {
         this.fields.add(f);
         fieldsInfo.add(0);
         fieldsHeight.add(fieldHeight);
      }
      setParent(parent);
   }
   
      /** Adds offset to this Container when drawn
      * @param x x coordinate offset
      * @param y y coordinate offset
      */
   public void addOffset(int x, int y) {
      setLocation(getLocation().x + x, getLocation().y + y);
      for (Container c : children)
         c.addOffset(x, y);
   }
   
      /** Initializes Container with location, size, color, and text
      * @param x x coordinate of location
      * @param y y coordinate of location
      * @param w width of size
      * @param h height of size
      * @param c background color of Container
      * @param t text of Container
      */
   public void initialize(int x, int y, int w, int h, Color c, String t) {
      setLocation(x, y);
      setSize(w, h);
      defaultHeight = h;
      initialize(c, t);
      updateContainer();
   }
   
      /** Initializes Container with color and text
      * @param c background color of Container
      * @param t text of Container
      */
   public void initialize(Color c, String t) {
      if (c != null)
         defaultBackgroundColor = c;
      if (t != null)
         text = t;
   }

      /** Initializes and adds Listeners, generally should only be used for parent Container
      */
   public void initializeListeners() {
      addMouseListener(this);
      addMouseMotionListener(this);
   }
   
   
      /** Sets parent of Container
      * @param parent parent of Container
      */
   public void setParent(Container parent) {
      if (parent == null)
         return;
      this.parent = parent;
      parent.add(this);
   }
   
   
      /** Adds Container child to Container
      * @param c Container child
      */
   public void add(Container c) {
      if (c == null)
         return;
      children.add(c);
      c.initialize(getX(), getY() + getHeight(), getWidth(), containerHeight, null, null);
   }

      /** Sets color of Container
      * @param c the color to set the background color to.
      */
   public void setColor(Color c) {
      defaultBackgroundColor = c;
   }

      /** Sets text of Container
      * @param t text of Container
      */
   public void setText(String t) {
      text = t;
   }
   
      /** Returns width of Container
      * @return width of Container
      */
   public int getWidth() {
      return (int)getSize().getWidth();
   }
   
      /** Returns height of Container
      * @return height of Container
      */
   public int getHeight() {
      return (int)getSize().getHeight();
   }
   
      /** Returns x coordinate of Container position
      * @return x coordinate of position
      */
   public int getX() {
      return (int)getLocation().getX();
   }
   
      /** Returns y coordinate of Container position
      * @return y coordinate of position
      */
   public int getY() {
      return (int)getLocation().getY();
   }
   
      /** Returns x coordinate of Container parent's position
      * @return x coordinate of parent's position
      */
   public int getXParent() {
      if (parent == null)
         return getX();
      Container temp = parent;
      while (temp.parent != null) {
         temp = temp.parent;
      }
      return getX() - temp.getX();
   }
   
      /** Returns y coordinate of Container parent's position
      * @return y coordinate of parent's position
      */
   public int getYParent() {
      if (parent == null)
         return getY();
      Container temp = parent;
      while (temp.parent != null) {
         temp = temp.parent;
      }
      return getY() - temp.getY();
   }
   
      /** Paints this Container, adds parent location as offset
      * @param g Graphics for painting
      */
   protected void paintComponent(Graphics g) {
      paintComponent(g, parent == null ? 0 : getXParent(), parent == null ? 0 : getYParent());
   }
   
   
      /** Paints this Container with position offset x and y
      * @param g Graphics for painting
      * @param x x coordinate position offset
      * @param y y coordinate position offset
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
         if (fieldValue != null && info == 2) {
            if (textIndex > fieldValue.length())
               textIndex = fieldValue.length();
            fieldValue = fieldValue.substring(0, textIndex) + "|" + fieldValue.substring(textIndex, fieldValue.length());
         }
         int fieldWidth = g.getFontMetrics().stringWidth(fieldText);
         int fieldHeight = 0;
         for (int f = 0; f < i; f++) {
            fieldHeight += fieldsHeight.get(f);
         }
         int currentFieldHeight = fieldsHeight.get(i);
         g.setColor(textColor);
         g.drawString(fieldText, x, y + heightSpacing + fieldTextHeight * 2 + fieldHeight);
         Color color = info > 0 ? defaultBackgroundColor.darker() : defaultBackgroundColor;
         color = info == 2 ? color.darker() : color;
         g.setColor(color);
         int boxX = x + fieldWidth + fieldWidthSpacing;
         int boxY = y + heightSpacing + fieldTextHeight + fieldHeight;
         int boxWidth = getWidth() - fieldWidth - fieldWidthSpacing - 1;
         g.fillRect(boxX, boxY, boxWidth, currentFieldHeight);
         g.setColor(borderColor);
         g.drawRect(boxX, boxY, boxWidth, currentFieldHeight);
         g.setColor(textColor);
         int splits = currentFieldHeight / this.fieldHeight;
         int begin = 0;
         int end = 0;
         for (int h = 0; h < splits; h++) {
            begin = end;
            if (h == splits - 1)
               end = fieldValue.length();
            else
               end += fieldValue.length() / splits;
            g.drawString(fieldValue.substring(begin, end), boxX + fieldWidthSpacing, boxY + fieldTextHeight * (h + 1));
         }
      }
      for (int i = 0; i < children.size(); i++) {
         Container child = children.get(i);
         child.paintComponent(g);
      }
   }
   
      /** Returns whether or not position x and y are inside of the DataField at index
      * @param index index of DataField to check
      * @param x x coordinate of position
      * @param y y coordinate of position
      * @return whether or not position is in DataField
      */
   public boolean inBox(int index, int x, int y) {
      Graphics g = getGraphics();
      if (g == null)
         return false;
      if (index >= 0 && index < fields.size()) {
         DataField field = fields.get(index);
         String fieldText = field.getName();
         String fieldValue = field.getValue();
         int fieldWidth = g.getFontMetrics().stringWidth(fieldText);
         int fieldHeight = 0;
         for (int f = 0; f < index; f++) {
            fieldHeight += fieldsHeight.get(f);
         }
         int currentFieldHeight = fieldsHeight.get(index);
         int fieldTextHeight = g.getFontMetrics().getHeight() * 3 / 4;
         int offsetX = parent == null ? 0 : getXParent();
         int offsetY = parent == null ? 0 : getYParent();
         int boxX = offsetX + fieldWidth + fieldWidthSpacing;
         int boxY = offsetY + heightSpacing + fieldTextHeight + fieldHeight;
         int boxWidth = getWidth() - fieldWidth - fieldWidthSpacing - 1;
         if (x > boxX && x < boxX + boxWidth && y > boxY && y < boxY + currentFieldHeight)
            return true;
      }
      return false;
   }
   
      /** Returns Graphics of Container
      * @return Graphics to paint
      */
   public Graphics getGraphics() {
      Graphics g = super.getGraphics();
      Container temp = parent;
      while (g == null && temp != null) {
         g = temp.getGraphics();
         temp = temp.parent;
      }
      return g;
   }
   
      /** Returns whether or not any fields are selected
      * @return boolean if fields are selected
      */
   public boolean isSelected() {
      for (Integer i : fieldsInfo) {
         if (i == 2)
            return true;
      }
      for (Container c : children) {
         if (c.isSelected())
            return true;
      }
      return false;
   }
   
      /** Updates Container size and width depending on DataFields and Container children
      */
   public void updateContainer() {
      int maxWidth = getWidth();
      int maxHeight = defaultHeight;
      for (int i = 0; i < fieldsHeight.size(); i++) {
         maxHeight += fieldsHeight.get(i);
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
   
      /** Checks and updates height based on width at DataField at index
      * @param index index of DataField to check
      */
   public void checkFieldWidth(int index) {
      Graphics g = getGraphics();
      if (g == null)
         return;
      DataField field = fields.get(index);
      String fieldText = field.getName();
      String fieldValue = field.getValue();
      int fieldX = g.getFontMetrics().stringWidth(fieldText);
      int fieldWidth = g.getFontMetrics().stringWidth(fieldValue);
      int boxWidth = getWidth() - fieldX - fieldWidthSpacing - 1;
      if (fieldWidth > boxWidth) {
         fieldsHeight.set(index, (fieldWidth / boxWidth + 1) * fieldHeight);
         updateContainer();
      }
   }
   
      /** KeyEvent upon pressing keyboard
      * @param e Pressed KeyEvent
      */
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
                  if (textIndex != 0) {
                     temp = temp.substring(0, textIndex - 1) + temp.substring(textIndex, temp.length());
                     field.setValue(temp);
                     textIndex = Math.max(0, textIndex - 1);
                     checkFieldWidth(i);
                  }
               }
            }
            else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
               field.setValue(field.getValue() + " ");
               textIndex++;
               checkFieldWidth(i);
            }
            else if (e.getKeyCode() == KeyEvent.VK_PERIOD) {
               field.setValue(field.getValue() + ".");
               textIndex++;
               checkFieldWidth(i);
            }
            else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
               textIndex = Math.max(0, textIndex - 1);
            }
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
               textIndex = Math.min(field.getValue().length(), textIndex + 1);
            }
         }
      }
      for (Container child : children) {
         child.keyPressed(e);
      }
      repaint();
   }

      /** KeyEvent upon releasing keyboard
      * @param e Released KeyEvent
      */
   public void keyReleased(KeyEvent e) {
   }

      /** KeyEvent upon typing keyboard
      * @param e Typed KeyEvent
      */
   public void keyTyped(KeyEvent e) {
      for (int i = 0; i < fields.size(); i++) {
         DataField field = fields.get(i);
         if (fieldsInfo.get(i) == 2 && field.canEdit()) {
            if (Character.isDigit(e.getKeyChar())) {
               field.setValue(field.getValue() + e.getKeyChar());
               textIndex++;
               checkFieldWidth(i);
            }
         }
      }
      for (Container child : children) {
         child.keyTyped(e);
      }
      repaint();
   }
   
      /** MouseEvent upon clicking
      * @param e Clicked MouseEvent
      */
   public void mouseClicked(MouseEvent e) {
   }
      
      /** MouseEvent upon pressing
      * @param e Pressed MouseEvent
      */
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
      
      /** MouseEvent upon releasing
      * @param e Released MouseEvent
      */
   public void mouseReleased(MouseEvent e) {
   
   }
      
      /** MouseEvent upon moving
      * @param e Moved MouseEvent
      */
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
      
      /** MouseEvent upon dragging
      * @param e Dragged MouseEvent
      */
   public void mouseDragged(MouseEvent e) {
      
   }
      
      /** MouseEvent upon entering
      * @param e Entered MouseEvent
      */
   public void mouseEntered(MouseEvent e) {
   }
      
   
      /** MouseEvent upon exiting
      * @param e Exiting MouseEvent
      */
   public void mouseExited(MouseEvent e) {
   }
   
      /** Returns String representation of Container: position, size, Container children
      * @return Container toString
      */
   public String toString() {
      String temp = getX() + " " + getY() + " " + getWidth() + " " + getHeight();
      for (Container c : children) {
         temp += "\n" + c;
      }
      return temp;
   }
}