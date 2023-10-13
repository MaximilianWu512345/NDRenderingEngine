import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.imageio.ImageIO;
import java.util.Hashtable;
import java.awt.Color;
import java.awt.Graphics;
import java.io.File;

/** JFrame engine that creates a graphical application. */
public class Engine extends JFrame {

/** Holds most recently created Engine. */
   public static Engine Instance;
   
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
      Instance = this;
      add(panel = new EnginePanel(width, height));
   }
   
   // Convenience method to render, currently takes Color[][], BufferedImage, Point, Vector, and Line.
   public void renderImage(Object object) {
      panel.renderImage(object);
      panel.repaint();
   }
   
   // For future implementation.
   public void renderTriangle(Color c) {
      panel.renderTriangle(c);
   }

/** Placeholder method to run TestDriver.
* @param args default args for main method.
*/
   public static void main(String[] args) {
      // Create the engine, start the program.
      Engine engine = new Engine(1000, 1000);
      
      // Ask for img files to open and display until user clicks cancel.
      boolean askForFiles = false;
      if (askForFiles) {
         try {
            JFileChooser fileChooser = new JFileChooser();
            File file = pickFile(fileChooser);
            if (file != null) {
               engine.renderImage(ImageIO.read(file));
               engine.repaint();
               while (file != null) {
                  file = pickFile(fileChooser);
                  if (file != null) {
                     engine.renderImage(ImageIO.read(file));
                     engine.repaint();
                  }
               }
            }
         }
         catch (Exception e) {
            System.out.println(e);
         }
      }
   }
   
   public static File pickFile(JFileChooser fileChooser)
   {
      File file = new File("images/PlaceImagesHere.txt");
      JFrame frame = new JFrame();
    // get the return value from choosing a file
      fileChooser.setCurrentDirectory(file);
      int returnVal = fileChooser.showOpenDialog(frame);
    
    // if the return value says the user picked a file 
      if (returnVal == JFileChooser.APPROVE_OPTION)
         file = fileChooser.getSelectedFile();
      else
         file = null;
      return file;
   }
}