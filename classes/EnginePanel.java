import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Collection;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/** JPanel holding all Components */
public class EnginePanel extends JPanel {

/** Used to offset components so they can be displayed better on the JFrame.*/
   public static final int OFFSET = 1;
   
   protected ArrayList<Component> components;
   
   protected RenderHelper renderHelper;
   
   protected Camera camera;
   
   protected ArrayList<Mesh> meshes;
   
/** Creates an EnginePanel of size (width, height) and calls initialize().
* @param width the width of the EnginePanel.
* @param height the height of the EnginePanel.
*/
   public EnginePanel(int width, int height) {
      super(null);
      initialize();
      setLayout(null);
      setSize(width, height);
      setVisible(true);
      addComponents(width, height);
   }
   
/** Initializes dictComponents to a new Hashtable. */
   protected void initialize() {
      components = new ArrayList<Component>();
      meshes = new ArrayList<Mesh>();
   }
   
/** Adds and initializes components to the dictComponents hashtable,
* adds each Component's getComponents() method to the EnginePanel.
* @param width the width to be used for adding components.
* @param height the height to be used for adding components.
*/
   protected void addComponents(int width, int height) {
      this.addComponent(renderHelper = new RenderHelper(OFFSET, 0, width, height));
   }
   
   public void setMeshes(Mesh[] meshes) {
      this.meshes.clear();
      for (Mesh m : meshes)
         this.meshes.add(m);
   }
   
   public void addMesh(Mesh mesh) {
      this.meshes.add(mesh);
   }
   
   public ArrayList<Mesh> getMeshes() {
      return this.meshes;
   }
   
   public void setCamera(Camera camera) {
      this.camera = camera;
   }
   
   public Camera getCamera() {
      return this.camera;
   }
   
   public void render() {
      this.renderImage(this.camera.Project(this.meshes.toArray(new Mesh[0])));
   }
   
   public void addComponent(Component c) {
      this.components.add(c);
      JComponent[] array = c.getComponents();
      if (array != null) {
         for (JComponent component : array) {
            this.add(component);
         }
      }
   }
   
   public Color[][] getRenderImage() {
      return renderHelper.getColors();
   }

   public void clearRenderImage() {
      renderHelper.clear();
      repaint();
   }
   
   public void renderImage(Object object) {
      if (object instanceof BufferedImage)
         renderHelper.setImage((BufferedImage)object);
      else if (object instanceof Color[][])
         renderHelper.setColors((Color[][])object);
      else if (object instanceof Point)
         renderHelper.setPoint((Point)object);
      else if (object instanceof Line)
         renderHelper.setLine((Line)object);
      else if (object instanceof Simplex)
         renderHelper.setSimplex((Simplex)object);
      else if (object instanceof Mesh) {
         renderHelper.setMesh((Mesh)object);
      }
      this.repaint();
   }

/** Paints EnginePanel and paints each component in dictComponents.
* @param g the Graphics instance to use to paint the components.
*/
   protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      for (Component c : components) {
         g.setColor(Color.RED);
         g.drawRect(c.getX(), c.getY(), c.getWidth(), c.getHeight());
         c.paintComponent(g);
      }
   }
   
/** Container class to hold information about rendering. */
   protected class RenderHelper extends Component {
   
      private BufferedImage renderedImage;
      
      public static final int RESOLUTION = 5;
      
      /** Used for ImageObserver class, calls:
      * imageUpdate(Image img, int infoflags, int x, int y, int width, int height), 
      * This method is called when information about an image which was previously requested using an asynchronous interface becomes available.
      */
      private Canvas imageObserver;
      
      /**  */
      public RenderHelper(int x, int y, int w, int h) {
         super(x, y, w, h);
         initialize(w, h);
      }
      
      private void initialize(int w, int h) {
         renderedImage = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
         imageObserver = new Canvas();
         Color clear = new Color(0, 0, 0, 0);
         for (int i = 0; i < renderedImage.getWidth(); i++) {
            for (int x = 0; x < renderedImage.getHeight(); x++) {
               renderedImage.setRGB(i, x, clear.getRGB());
            }
         }
      }
   
      public Color[][] getColors() {
         Color[][] colors = new Color[renderedImage.getWidth()][renderedImage.getHeight()];
         for (int i = 0; i < renderedImage.getWidth(); i++) {
            for (int x = 0; x < renderedImage.getHeight(); x++) {
               colors[i][x] = new Color(renderedImage.getRGB(i, x), true);
            }
         }
         return colors;
      }
      
      public BufferedImage getImage() {
         return renderedImage;
      }
      
      public Canvas getCanvas() {
         return imageObserver;
      }
      
      public void clear() {
         initialize(super.width, super.height);
      }
      
      public void setColors(Color[][] colors) {
         for (int i = 0; i < colors.length && i < renderedImage.getWidth(); i++) {
            for (int x = 0; x < colors[i].length && x < renderedImage.getHeight(); x++) {
               if (colors[i][x] != null) {
                  renderedImage.setRGB(i, x, colors[i][x].getRGB());
               }
            }
         }
      }
      
     
      public void setImage(BufferedImage image) {
         for (int i = 0; i < image.getWidth(); i++) {
            for (int x = 0; x < image.getHeight(); x++) {
               renderedImage.setRGB(i, x, image.getRGB(i, x));
            }
         }
      }
      
      
      public void setPoint(Point point) {
         if (point == null || point.length() <= 0)
            return;
         int x = (int)point.getCoordinates()[0];
         int y = point.length() > 1 ? (int)point.getCoordinates()[1] : 0;
         int z = RESOLUTION;
         for (int a = x - z; a >= 0 && a <= x + z && a < renderedImage.getWidth(); a++) {
            for (int b = y - z; b >= 0 && b <= y + z && b < renderedImage.getHeight(); b++) {
               renderedImage.setRGB(a, b, Color.black.getRGB());
            }
         }
      }
      
      public void setLine(Line line) {
         if (line == null)
            return;
         if (line.getPosition() == null || line.getDirection() == null)
            return;
         float[] vectorCoords = line.getDirection().getCoordinates();
         float[] positionCoords = line.getPosition().getCoordinates();
         if (vectorCoords == null || vectorCoords.length < 2 || positionCoords == null || positionCoords.length < 2)
            return;
         setPoint(line.getPosition());
         int offsetX = Math.round(positionCoords[0]);
         int offsetY = Math.round(positionCoords[1]);
         float max = Math.abs(vectorCoords[0]) > Math.abs(vectorCoords[1]) ? Math.abs(vectorCoords[0]) : Math.abs(vectorCoords[1]);
         float x = vectorCoords[0] / max;
         float y = vectorCoords[1] / max;
         float addX = x;
         float addY = y;
         while (Math.abs(x) < Math.abs(vectorCoords[0]) || Math.abs(y) < Math.abs(vectorCoords[1])) {
            x += addX;
            y += addY;
            int newX = offsetX + (int)Math.floor((double)x);
            int newY = offsetY + (int)Math.floor((double)y);
            if (newX >= 0 && newY >= 0 && newX < renderedImage.getWidth() && newY < renderedImage.getHeight())
               renderedImage.setRGB(newX, newY, Color.black.getRGB());
            newX = offsetX + (int)Math.ceil((double)x);
            newY = offsetY + (int)Math.ceil((double)y);
            if (newX >= 0 && newY >= 0 && newX < renderedImage.getWidth() && newY < renderedImage.getHeight())
               renderedImage.setRGB(newX, newY, Color.black.getRGB());
         }
      }
      
      public void setSimplex(Simplex simplex) {
         if (simplex == null)
            return;
         Point[] points = simplex.getPoints();
         if (points == null)
            return;
         for (int i = 0; i < points.length; i++) {
            setPoint(points[i]);
            if (i != points.length - 1)
               setLine(new Line(points[i], points[i + 1]));
            else if (i != 0)
               setLine(new Line(points[i], points[0]));
         }
      }
      
      public void setMesh(Mesh mesh) {
         if (mesh == null || mesh.getFaces() == null || mesh.getFaces().length == 0)
            return;
         for (Simplex s : mesh.getFaces())
            setSimplex(s);
      }
      
      
      public void setCanvas(Canvas c) {
         imageObserver = c;
      }
      
      protected void paintComponent(Graphics g) {
         g.drawImage(renderedImage, x, y, imageObserver);
      }
      
   }
   


}