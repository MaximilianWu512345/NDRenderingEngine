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
   
   public void renderImage(Color[][] colors) {
      panel.renderImage(colors);
   }
   
   public void renderTriangle(Color c) {
      panel.renderTriangle(c);
   }

/** Placeholder method to run TestDriver.
* @param args default args for main method.
*/
   public static void main(String[] args) {
      Engine engine = new Engine(1000, 1000);
      Color[][] array = new Color[500][500];
      for (int i = 0; i < array.length; i++) {
         for (int x = 0; x < array[i].length; x++) {
            if (i % 2 == 0 && x % 2 == 0)
               array[i][x] = Color.RED;
            else if (i % 2 == 1 && x % 2 == 1)
               array[i][x] = Color.BLUE;
            
         }
      }
      engine.renderImage(array);
   }
}