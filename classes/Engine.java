import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;

/** JFrame engine that creates a graphical application. */
public class Engine {

/** Placeholder method to run TestDriver.
* @param args default args for main method.
*/
   public static void main(String[] args) {
      int width = 1000;
      int height = 1000;
      JFrame frame = new JFrame("Max Wu's Concoction Machine!");
      frame.setSize(width * 407 / 400, height * 104 / 100);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      EnginePanel panel = new EnginePanel(width, height);
      frame.add(panel);
      frame.setJMenuBar(panel.getMenuBar());
      frame.setVisible(true);
   }
   
   public static Color[] colors = { Color.RED, Color.BLUE, Color.BLACK, Color.WHITE, Color.GREEN, Color.YELLOW, Color.GRAY};
   
   public static Color randomColor() {
      return colors[(int)(Math.random() * colors.length)];
   }
   
}