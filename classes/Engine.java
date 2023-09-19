import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLayeredPane;
import java.util.Hashtable;
import java.awt.Color;
import java.awt.Graphics;

/** JFrame engine that creates a graphical application. */
public class Engine extends JFrame {

/** Title of JFrame when program is run. */
   protected static final String TITLE = "Max Wu's Concoction Machine";
   
/** JPanel for engine. */
   protected EnginePanel panel;

/** Creates a new Engine JFrame with size (width, height), and calls initialize(width, height).
* @param width the width of the engine JFrame.
* @param height the height of the engine JFrame.
*/
   public Engine(int width, int height) {
      super(TITLE);
      initialize(width, height);
      setSize(width * 407 / 400, height * 104 / 100);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setVisible(true);
   }
   
/** Creates JPanel for engine with (width, height).
* @param width the width of the engine JPanel.
* @param height the height of the engine JPanel.
*/
   private void initialize(int width, int height) {
      add(panel = new EnginePanel(width, height));
   }

/** Placeholder method to run TestDriver.
* @param args default args for main method.
*/
   public static void main(String[] args) {
      TestDriver.main(args);
   }
}