import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.Hashtable;
import java.awt.Color;

public class Engine extends JFrame {

   protected static final String TITLE = "Max Wu's Concoction Machine";
   
   protected Hashtable<ButtonType, Button> dictButtons;
   
   public enum ButtonType {
      LEFT,
      RIGHT
   }

   public Engine(int width, int height) {
      super(TITLE);
      initialize();
      setSize(width, height);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setVisible(true);
      addButtons(width, height);
   }
   
   private void initialize() {
      dictButtons = new Hashtable<ButtonType, Button>();
   }
   
   private void addButtons(int width, int height) {
      dictButtons.put(ButtonType.LEFT, (Button)add(new Button(width / 2, height * 4 / 5, width / 2, height / 2, Color.green)));
   }
   
   public static void main(String[] args) {
      TestDriver.main(args);
   }
}