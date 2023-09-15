import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLayeredPane;
import java.util.Hashtable;
import java.awt.Color;
import java.awt.Graphics;

public class Engine extends JFrame {

   protected static final String TITLE = "Max Wu's Concoction Machine";
   
   protected EnginePanel panel;

   public Engine(int width, int height) {
      super(TITLE);
      initialize(width, height);
      setSize(width * 407 / 400, height * 104 / 100);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setVisible(true);
   }
   
   private void initialize(int width, int height) {
      add(panel = new EnginePanel(width, height));
   }

   public static void main(String[] args) {
      TestDriver.main(args);
   }
}