import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLayeredPane;
import java.util.Hashtable;
import java.awt.Color;
import java.awt.Graphics;

public class EnginePanel extends JPanel {
   
   public enum PanelType {
      BUTTON,
      RENDER
   }
   
   protected Hashtable<PanelType, Component> dictComponents;

   public EnginePanel(int width, int height) {
      super(null);
      initialize(width, height);
      setSize(width, height);
      setVisible(true);
      addComponents(width, height);
   }
   
   private void initialize(int width, int height) {
      dictComponents = new Hashtable<PanelType, Component>();
   }
   
   private void addComponents(int width, int height) {
      dictComponents.put(PanelType.RENDER, new RenderHelper(0, 0, width, height));
      dictComponents.put(PanelType.BUTTON, new ButtonHelper(0, height * 9 / 10, width, height / 10));
   }
   
   protected void paintComponent(Graphics g) {
      for (Component component : dictComponents.values()) {
         component.paintComponent(g);
      }
      g.drawRect(0, 0, 100, 100);
   }
   
   protected class RenderHelper extends Component {
   
      public RenderHelper(int x, int y, int w, int h) {
         super(x, y, w, h);
      }
   }
   
   protected class ButtonHelper extends Component {
   
      public enum ButtonType {
         LEFT,
         RIGHT
      }
      
      protected Hashtable<ButtonType, Button> dictButtons;
      
      protected int x, y;
      
      protected int width, height;
   
      public ButtonHelper(int x, int y, int w, int h) {
         super(x, y, w, h);
         initialize();
         addButtons(width = w, height = h);
      }
      
      private void initialize() {
         dictButtons = new Hashtable<ButtonType, Button>();
      }
      
      protected void paintComponent(Graphics g) {
         g.setColor(Color.green);
         g.fillRect(0, 0, width, height);
      }
      
      private void addButtons(int width, int height) {
         dictButtons.put(ButtonType.LEFT, (Button)add(new Button(width / 2 - width / 10, 0, width * 9 / 10, 0, Color.red, "-")));
         dictButtons.put(ButtonType.RIGHT, (Button)add(new Button(width / 2, 0, width * 9 / 10, 0, Color.green, "+")));
      }
   }
}