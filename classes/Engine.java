import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.util.ArrayList;

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
      // Create the engine and camera, start the program.
  /* max test code*/
      int dimention = 3;
      Point screenPos = new Point(new float[dimention]);
      screenPos.getCoords()[0] = 1;
      Vector[] axis = new Vector[2];
      float[] axis1 = new float[dimention];
      float[] axis2 = new float[dimention];
      axis1[1] = 15f/900f;
      axis2[2] = 10f/900f;
      axis[0] = new Vector(axis1);
      axis[1] = new Vector(axis2);
      subSpace screenDir = new subSpace(axis);
      AffineSubSpace screen = new AffineSubSpace(screenDir, screenPos);
      Point camPos = new Point(new float[dimention]);
      int[] pixBounds = new int[]{900, 900};
      Camera camera;
      camera = new CameraRastorizationV2(camPos, screen, pixBounds);
      
      //generate scene
      ArrayList<Mesh> scene = new ArrayList<Mesh>();
      //generate mesh
      /*
      int numTestSimplexes = 1;
      int numTestMeshes = 1;
      
      for(int i = 0; i<numTestMeshes; i++){
         Simplex[] tempSimplex = new Simplex[numTestSimplexes];
         for(int j = 0; j< numTestSimplexes; j++){
            tempSimplex[j] = generateRandomSimplex(dimention, 10);
            System.out.println(tempSimplex[j]);
         }
         scene.add(new Mesh(tempSimplex, dimention));
      }
      */
      //manuel Input
      Simplex[] manSimplex = new Simplex[1];
      Point[] parr1 = new Point[dimention];
      parr1[0] = new Point(new float[]{3,3,3});
      parr1[1] = new Point(new float[]{7,0,4});
      parr1[2] = new Point(new float[]{4,-3,-3});
      manSimplex[0] = new Simplex(parr1);
      scene.add(new Mesh(manSimplex, dimention));
     
  
      //set up
      Mesh[] sceneArr = new Mesh[scene.size()];
      sceneArr = scene.toArray(sceneArr);
      //render
      long timeStart = System.nanoTime();
      Texture realPixels = camera.Project(sceneArr);
      long timeEnd = (System.nanoTime()-timeStart);
      System.out.println( timeEnd + " nanoseconds taken to render the image, or " + (timeEnd/1000000000f) + " seconds");
      int[] b = realPixels.getBounds();
      Color[][] pixelArray = new Color[b[0]][b[1]];
      for(int i = 0; i<pixelArray.length; i++){
         for(int j = 0; j<pixelArray[i].length; j++){
            pixelArray[i][j] = realPixels.getColor(new Point(new float[]{i, j}));
         }
      }

      panel.renderImage(pixelArray);
      frame.revalidate();
      frame.repaint();
   }
   
   public static Color[] colors = { Color.RED, Color.BLUE, Color.BLACK, Color.WHITE, Color.GREEN, Color.YELLOW, Color.GRAY};
   
   public static Color randomColor() {
      return colors[(int)(Math.random() * colors.length)];
   }
   
   public static Simplex generateRandomSimplex(int dimention, float bounds){
      Point[] data = new Point[dimention];
      for(int i = 0; i<dimention; i++){
         float[] coord = new float[dimention];
         for(int j = 0; j<dimention; j++){
            coord[j] = (float)(Math.random()*2*bounds)-bounds;
         }
         data[i] = new Point(coord);
      }
      return new Simplex(data);
   
   }

}