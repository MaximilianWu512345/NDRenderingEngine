import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComponent;
import java.util.Hashtable;
import java.util.Collection;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Canvas;
import java.awt.image.BufferedImage;

/** JPanel holding all Components */
public class EnginePanel extends JPanel {

/** Used to offset components so they can be displayed better on the JFrame.*/
   private static final int OFFSET = 1;
   
/** Enum of PanelTypes to index the dictComponents Hashtable */
   public enum PanelType {
      /** Indexes ButtonHelper */
      BUTTON,
      /** Indexes RenderHelper */
      RENDER
   }

/** Hashtable dictionary containing all components, indexed with PanelType. Might be redundant, could remove later. */
   protected Hashtable<PanelType, Component> dictComponents;

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
   private void initialize() {
      dictComponents = new Hashtable<PanelType, Component>();
   }
   
/** Adds and initializes components to the dictComponents hashtable,
* adds each Component's getComponents() method to the EnginePanel.
* @param width the width to be used for adding components.
* @param height the height to be used for adding components.
*/
   private void addComponents(int width, int height) {
      dictComponents.put(PanelType.RENDER, new RenderHelper(OFFSET, 0, width, height));
      dictComponents.put(PanelType.BUTTON, new ButtonHelper(OFFSET, height * 9 / 10, width, height / 10));
      for (Component c : dictComponents.values()) {
         JComponent[] array = c.getComponents();
         if (array != null) {
            for (JComponent x : array) {
               add(x);
            }
         }
      }
   }
   
   public void renderImage(Object object) {
      if (object instanceof BufferedImage)
         ((RenderHelper)dictComponents.get(PanelType.RENDER)).setImage((BufferedImage)object);
      else if (object instanceof Color[][])
         ((RenderHelper)dictComponents.get(PanelType.RENDER)).setColors((Color[][])object);
   }
   
   public void renderTriangle(Color c) {

   }
   

/** Paints EnginePanel and paints each component in dictComponents.
* @param g the Graphics instance to use to paint the components.
*/
   protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      for (Component c : dictComponents.values()) {
         g.setColor(Color.RED);
         g.drawRect(c.getX(), c.getY(), c.getWidth(), c.getHeight());
         c.paintComponent(g);
      }
   }
   
/** Container class to hold information about rendering. */
   protected class RenderHelper extends Component {
   
      private BufferedImage renderedImage;
      
      private Canvas imageObserver;
      
      /**  */
      private Color[][] colors;
      
      /**  */
      public RenderHelper(int x, int y, int w, int h) {
         super(x, y, w, h);
         initialize(w, h);
      }
      
      /**  */
      private void initialize(int w, int h) {
         colors = new Color[width][height];
         renderedImage = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
         imageObserver = new Canvas();
      }
      
      public Color[][] getColors() {
         return colors;
      }
      
      public void setColors(Color[][] c) {
         colors = c;
         updateImage();
      }
      
      public void setImage(BufferedImage image) {
         renderedImage = image;
         updateImage();
      }
      
      public void updateImage() {
         if (colors == null)
            return;
         for (int i = 0; i < colors.length; i++) {
            for (int x = 0; x < colors[i].length; x++) {
               if (colors[i][x] != null) {
                  renderedImage.setRGB(i, x, colors[i][x].getRGB());
               }
            }
         }
      }
      
      protected void paintComponent(Graphics g) {
         g.drawImage(renderedImage, x, y, imageObserver);
      }
   }
   
/** Container class to hold information about buttons. Might be obsolete, could remove later. */
   protected class ButtonHelper extends Component {
   
      /** Enum of ButtonTypes to index the dictButtons Hashtable. */
      public enum ButtonType {
         /** Indexes the left Button */
         LEFT,
         /** Indexes the right Button */
         RIGHT
      }
      
      /** Hashtable dictionary of Buttons, indexed with ButtonType. */
      protected Hashtable<ButtonType, Button> dictButtons;
   
      /** Creates new ButtonHelper with location (x, y) and size (w, h).
      * @param x the x-coord of the location.
      * @param y the y-coord of the location.
      * @param w the width of the location.
      * @param h the height of the location.
      */
      public ButtonHelper(int x, int y, int w, int h) {
         super(x, y, w, h);
         initialize();
         addButtons(width, height);
      }
      
      /** Initializes the dictButtons hashtable */
      private void initialize() {
         dictButtons = new Hashtable<ButtonType, Button>();
      }
      
      /** Adds and initializes Buttons to the dictButtons hashtable.
      * @param width the width to be used for adding Buttons.
      * @param height the height to be used for adding Buttons.
      */
      private void addButtons(int width, int height) {
         dictButtons.put(ButtonType.LEFT, new Button(x, y, width / 2, height, Color.red, "-"));
         dictButtons.put(ButtonType.RIGHT, new Button(x + width / 2, y, width / 2, height, Color.green, "+"));
         
      }
      
      /** Returns an array of every Button in dictButtons.
      * @return an array containing every Button in dictButtons.
      */
      public Button[] getComponents() {
         return dictButtons.values().toArray(new Button[dictButtons.values().size()]);
      }
      
      /** Returns dictButtons.
      * @return the hashtable of buttons, to be indexed with ButtonType.
      */
      public Hashtable<ButtonType, Button> getDictionary() {
         return dictButtons;
      }
   }
}