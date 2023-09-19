import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComponent;
import java.util.Hashtable;
import java.util.Collection;
import java.awt.Color;
import java.awt.Graphics;

public class EnginePanel extends JPanel {

   private static final int OFFSET = 1;
   
   public enum PanelType {
      BUTTON,
      RENDER
   }
   
   protected Hashtable<PanelType, Component> dictComponents;

   public EnginePanel(int width, int height) {
      super(null);
      initialize(width, height);
      setLayout(null);
      setSize(width, height);
      setVisible(true);
      addComponents(width, height);
   }
   
   private void initialize(int width, int height) {
      dictComponents = new Hashtable<PanelType, Component>();
   }
   
   private void addComponents(int width, int height) {
      dictComponents.put(PanelType.RENDER, new RenderHelper(OFFSET, 0, width, height));
      dictComponents.put(PanelType.BUTTON, new ButtonHelper(OFFSET, height * 9 / 10, width, height / 10));
      for (Component c : dictComponents.values()) {
         JComponent[] array = c.getComponents();
         if (array != null) {
            for (JComponent x : array) {
               add(x);
            }
         }
      }
   }
   
   protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      for (Component c : dictComponents.values()) {
         g.setColor(Color.RED);
         g.drawRect(c.getX(), c.getY(), c.getWidth(), c.getHeight());
      }
   }
   
   protected class RenderHelper extends Component {
   
      private Color[][] colors;
   
      public RenderHelper(int x, int y, int w, int h) {
         super(x, y, w, h);
         initialize();
      }
      
      private void initialize() {
         colors = new Color[width][height];
      }
      
      public Color[][] getColors() {
         return colors;
      }
      
   }
   
   protected class ButtonHelper extends Component {
   
      public enum ButtonType {
         LEFT,
         RIGHT
      }
      
      protected Hashtable<ButtonType, Button> dictButtons;
   
      public ButtonHelper(int x, int y, int w, int h) {
         super(x, y, w, h);
         initialize();
         addButtons(width, height);
      }
      
      private void initialize() {
         dictButtons = new Hashtable<ButtonType, Button>();
      }
      
      private void addButtons(int width, int height) {
         dictButtons.put(ButtonType.LEFT, new Button(x, y, width / 2, height, Color.red, "-"));
         dictButtons.put(ButtonType.RIGHT, new Button(x + width / 2, y, width / 2, height, Color.green, "+"));
         
      }
      
      public JComponent[] getComponents() {
         return dictButtons.values().toArray(new JComponent[dictButtons.values().size()]);
      }
      
      public Hashtable<ButtonType, Button> getDictionary() {
         return dictButtons;
      }
   }
}