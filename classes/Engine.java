import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.imageio.ImageIO;
import java.util.Hashtable;
import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import com.aparapi.Kernel;

/** JFrame engine that creates a graphical application. */
public class Engine extends JFrame {

/** Holds most recently created Engine. */
   public static Engine Instance;
   
/** Holds most recently created Camera. */
   public static Camera camera;
   
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
   
   public void renderSimplex(Simplex s, Color c) {
      Simplex[] faces = new Simplex[] { s };
      Mesh obj1 = new Mesh(faces, 3);
      Mesh[] listObj = new Mesh[] { obj1 };
      Instance.renderImage(camera.Project(listObj, c, null));
   }
   
   public void renderSimplexes(Simplex[] s, Color c) {
      Mesh obj1 = new Mesh(s, 3);
      Mesh[] listObj = new Mesh[] { obj1 };
      Instance.renderImage(camera.Project(listObj, c, null));
   }
   
   public static class Squarer extends Kernel{
      int[] in;
      int[] out;
      @Override public void run(){
         int gid = getGlobalId(0);
         out[gid] = in[gid] * in[gid];
      }
   }

/** Placeholder method to run TestDriver.
* @param args default args for main method.
*/
   public static void main(String[] args) {
      // Create the engine and camera, start the program.
      int dimention = 5;
      CreateEngine();
      Point screenPos = new Point(new float[dimention]);
      screenPos.getCoords()[0] = 1;
      Vector[] axis = new Vector[2];
      float[] axis1 = new float[dimention];
      float[] axis2 = new float[dimention];
      axis1[1] = 1;
      axis2[2] = 2;
      axis[0] = new Vector(axis1);
      axis[1] = new Vector(axis2);
      SubSpace screenDir = new SubSpace(axis);
      AffineSubspace screen = new AffineSubspace(screenDir, screenPos);
      Point camPos = new Point(new float[dimention]);
      int[] pixBounds = new int[]{900, 900};
      camera = new CameraRastorizationV2(camPos, screen, pixBounds);
      
      //generate simplex
      Mesh[] scene = null;
      //render
      Instance.renderImage(camera.Project(scene));
   }
   
   public static Color[] colors = { Color.RED, Color.BLUE, Color.BLACK, Color.WHITE, Color.GREEN, Color.YELLOW, Color.GRAY};
   
   public static Color randomColor() {
      return colors[(int)(Math.random() * colors.length)];
   }
   
   public static Engine CreateEngine() {
      return new Engine(1000, 1000);
   }
   
   public static Camera CreateCamera() {
      //return Camera = new CameraRastorizationV2(new Point(/*camPos*/new float[3]), /*camDirection*/new Vector(new float[] {1,0,0}), 900, 900);
      return null;
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