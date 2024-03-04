import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.util.Hashtable;
import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import javax.swing.JMenuBar;

/** JFrame engine that creates a graphical application. */
public class Engine extends JFrame {

/** Holds most recently created Engine. */
   public static Engine Instance;
   
/** Holds most recently created Camera. */
   public static Camera Camera;
   
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
      this.setJMenuBar(panel.getMenuBar());
   }
   
   // Convenience method to render, currently takes Color[][], BufferedImage, Point, Vector, and Line.
   public void renderImage(Object object) {
      panel.renderImage(object);
      panel.repaint();
   }
  
   
   public void renderSimplex(Simplex s, Color c) {
      Simplex[] faces = new Simplex[] { s };
      Mesh obj1 = new Mesh(faces, 3);
      Mesh[] listObj = new Mesh[] { obj1 };
      Instance.renderImage(Camera.Project(listObj, 3, c, null));
   }
   
   public void renderSimplexes(Simplex[] s, Color c) {
      Mesh obj1 = new Mesh(s, 3);
      Mesh[] listObj = new Mesh[] { obj1 };
      Instance.renderImage(Camera.Project(listObj, 3, c, null));
   }
   
   public void renderMesh(Mesh m, Color c) {
      Mesh[] listObj = new Mesh[] { m };
      Instance.renderImage(Camera.Project(listObj, 3, c, null));
   }

/** Placeholder method to run TestDriver.
* @param args default args for main method.
*/
   public static void main(String[] args) {
      // Create the engine, start the program.
      
      CreateEngine();
      CreateCamera();
      /*
      Simplex[] simplexes = new Simplex[100];
      for (int i = 0; i < 100; i++) {
         Point[] points = new Point[3];
         for (int x = 0; x < 3; x++) {
            int z = (int)(Math.random() * 2) + 1;
            int ex = (int)(Math.random() * 1000);
            int y = (int)(Math.random() * 1000);
            points[x] = new Point(new float[] {z, ex, y});
         }
         Simplex s = new Simplex(points);
         simplexes[i] = s;
         Instance.renderSimplex(s, randomColor());
   
      }
      Instance.renderSimplex(simplexes[0], randomColor());
      */
      /*
      ArrayTexture t = new ArrayTexture(Utilities.TrimArray(Instance.panel.getRenderImage()));
      Utilities.SaveTexture("Test.png", t);
      */
      /*
      try {
         File file = new File("meshes/TESTS");
         file.createNewFile();
         FileOutputStream stream = new FileOutputStream(file);
         byte[] bytes = t.getTextures().encodedBytes;
         ArrayList<Byte> array = new ArrayList<Byte>();
         for (int i = 0; i < bytes.length; i++) {
            if (i > 0) {
               int add = 0;
               while (i + add < bytes.length - 1 && bytes[i - 1] == bytes[i + add])
                  add++;
               i += add;
            }
            array.add(bytes[i]);
         }
         System.out.println(array.size());
         byte[] compressed = new byte[array.size()];
         System.out.println(compressed.length);
         for (int i = 0; i < array.size(); i++) {
            compressed[i] = array.get(i);
         }
         stream.write(compressed);
      } catch (Exception e) {
         System.out.println(e);
      }
      */
      
      /*
      Color[][] de = t.decompress(true);
      System.out.println("JPEG decompression completed!");
      Instance.panel.clearRenderImage();
      Instance.renderImage(de);
      */
      /*
      System.out.println(t.getEncodedLength());
      float[] one = new float[10];
      float[] two = new float[10];
      float[] three = new float[10];
      for (int i = 0; i < one.length; i++) {
         one[i] = i;
         two[i] = i - 3;
      }
      OpenCL.RunFile("AddSum.c", "sampleKernel", 10, new Object[] { one, two }, new Object[] { three });
      for (float f : three)
         System.out.println(f);
      */
   }
   
   public static Color[] colors = { Color.RED, Color.BLUE, Color.BLACK, Color.WHITE, Color.GREEN, Color.YELLOW, Color.GRAY};
   
   public static Color randomColor() {
      return colors[(int)(Math.random() * colors.length)];
   }
   
   public static Engine CreateEngine() {
      return new Engine(1000, 1000);
   }
   
   public static Camera CreateCamera() {
      return Camera = new CameraRastorizationV1(new Point(/*camPos*/new float[3]), /*camDirection*/new Vector(new float[] {1,0,0}), 900, 900);
   }
   
}