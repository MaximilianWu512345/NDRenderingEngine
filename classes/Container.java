import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;
import java.util.ArrayList;

/** More personalized and liberal version of a JButton */
public class Container extends JComponent implements MouseInputListener{
   
   protected int fieldHeight = 20;
   
   protected int fieldWidthSpacing = 5;
   
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
      children = new ArrayList<Container>();
      fieldsInfo = new ArrayList<Integer>();
      setParent(parent);
      if (fields == null)
         fields = new ArrayList<DataField>();
      setFields(fields);
   }
   
   public Container(Container parent, DataField[] fields) {
      this(null, (ArrayList)null);
      for (DataField field : fields) {
         this.fields.add(field);
         this.fieldsInfo.add(0);
      }
   }
   
   public void initialize(int x, int y, int w, int h, Color c, String t) {
      setLocation(x, y);
      setSize(w, h);
      defaultHeight = h;
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
   
   public void setFields(ArrayList<DataField> fields) {
      this.fields = fields;
      for (DataField f : fields) {
         fieldsInfo.add(0);
      }
   }
   
   public void add(Container c) {
      if (c == null)
         return;
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
   
/** Paints the button. Halves the background color if the mouse has entered the button.
* @param g the graphics component used to paint the button.
*/
   protected void paintComponent(Graphics g) {
      g = g.create();
      g.setColor(defaultBackgroundColor);
      g.fillRect(0, 0, getWidth(), getHeight());
      g.setColor(borderColor);
      g.drawRect(0, 0, getWidth(), getHeight());
      if (text != null && text.length() > 0) {
         int i = g.getFontMetrics().stringWidth(text);
         g.setColor(textColor);
         g.drawString(text, getWidth() / 2 - i / 2, defaultHeight / 2);
      }
      for (int i = 0; i < fields.size(); i++) {
         DataField field = fields.get(i);
         Integer info = fieldsInfo.get(i);
         String fieldText = field.getName();
         String fieldValue = field.getValue();
         int fieldWidth = g.getFontMetrics().stringWidth(fieldText);
         int fieldTextHeight = g.getFontMetrics().getHeight() * 3 / 4;
         g.setColor(textColor);
         g.drawString(fieldText, 0, defaultHeight + i * fieldHeight);
         g.setColor(info > 0 ? defaultBackgroundColor.darker() : defaultBackgroundColor);
         int boxX = fieldWidth + fieldWidthSpacing;
         int boxY = defaultHeight + i * fieldHeight - fieldTextHeight;
         int boxWidth = getWidth() - fieldWidth - fieldWidthSpacing - 1;
         g.fillRect(boxX, boxY, boxWidth, fieldHeight);
         g.setColor(borderColor);
         g.drawRect(boxX, boxY, boxWidth, fieldHeight);
         g.setColor(textColor);
         g.drawString(fieldValue, boxX + fieldWidthSpacing, boxY + fieldTextHeight);
      }
   }
   
   public boolean inBox(int index, int x, int y) {
      if (index >= 0 && index < fields.size()) {
         DataField field = fields.get(index);
         String fieldText = field.getName();
         String fieldValue = field.getValue();
         int fieldWidth = getGraphics().getFontMetrics().stringWidth(fieldText);
         int fieldTextHeight = getGraphics().getFontMetrics().getHeight() * 3 / 4;
         int boxX = fieldWidth + fieldWidthSpacing;
         int boxY = defaultHeight + index * fieldHeight - fieldTextHeight;
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
            fieldsInfo.set(i, 1);
         }
         else {
            fieldsInfo.set(i, 0);
         }
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
   
}