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
      frame.setSize(width * 204 / 200, height * 209 / 200);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      EnginePanel panel = new EnginePanel(width, height);
      EnginePanelGUI gui = new EnginePanelGUI(frame, panel, true);
      frame.add(panel);
      frame.setVisible(true);
      // Create the engine and camera, start the program.
  /* max test code*/
      int dimention = 4;
      float[] camPosData = new float[dimention];
      Point screenPos = new Point(camPosData);
      Vector[] axis = new Vector[2];
      float[] axis1 = new float[dimention];
      float[] axis2 = new float[dimention];
      axis1[1] = 3f/900f;
      axis2[2] = 3f/900f;
      axis[0] = new Vector(axis1);
      axis[1] = new Vector(axis2);
      SubSpace screenDir = new SubSpace(axis);
      AffineSubSpace screen = new AffineSubSpace(screenDir, screenPos);
      Point camPos = new Point(new float[dimention]);
      camPos.getCoords()[0] = camPosData[0]-1;
      int[] pixBounds = new int[]{900, 900};
      Camera camera;
      camera = new CameraRastorizationV2(camPos, screen, pixBounds);
      ((CameraRastorizationV2)camera).GPUConnect();
      ((CameraRastorizationV2)camera).initCamGPUCon();
      panel.setCamera(camera);
      
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
      parr1[0] = new Point(new float[]{1,1,0,-0.5f});
      parr1[1] = new Point(new float[]{1,0,0,-0.5f});
      parr1[2] = new Point(new float[]{2,0,1,0.5f});
      parr1[3] = new Point(new float[]{2,0,1,-0.5f});
      manSimplex[0] = new Simplex(parr1);
      //scene.add(new Mesh(manSimplex, dimention));
      Mesh cube = ShapeLibrary.Generate4DTesseract(1);
      cube.translate(new Point(new float[]{0, -0.5f, -0.5f, -0.5f}));
      cube.transform(Matrix.GivensRot(dimention, 0.7f, 2, 3));
      scene.add(cube);
      
      for(int i = 0; i<cube.getFaces().length; i++){
         System.out.println(i+": " + cube.getFaces()[i].toString());
      }
      
      //System.out.println(cube);

      //render
      long timeStart = System.nanoTime();
      panel.setMeshes(scene.toArray(new Mesh[0]));
      panel.render();
      long timeEnd = (System.nanoTime()-timeStart);
      System.out.println( timeEnd + " nanoseconds taken to render the image, or " + (timeEnd/1000000000f) + " seconds");

      float[][] textData = new float[][]{new float[]{1, 3, 0, 0, 0.5f}, new float[]{3, 3, -4, 5, 8}, new float[]{7, 3, 0, 0, 0.5f}, new float[]{1, 3, 0, 0, 0.5f}, new float[]{1, 3, 0, 0, 0.5f}};
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