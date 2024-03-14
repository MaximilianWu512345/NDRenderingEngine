import static org.jocl.CL.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.jocl.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JComponent;
import java.util.Hashtable;
import java.util.Collection;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Canvas;
import java.awt.image.BufferedImage;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.JComboBox;

/** JPanel holding all Components */
public class EnginePanel extends JPanel {

/** Used to offset components so they can be displayed better on the JFrame.*/
   private static final int OFFSET = 1;
   
/** Enum of PanelTypes to index the dictComponents Hashtable */
   public enum PanelType {
      /** Indexes ButtonHelper */
      BUTTON,
      /** Indexes RenderHelper */
      RENDER,
      /** Indexes MenuHelper */
      MENU
   }
   
   public enum FileType {
      ANY,
      IMAGE,
      MESHANDTEXTURE,
      MESH,
      TEXTURE
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
      dictComponents.put(PanelType.MENU, new MenuHelper(0, 0, 0, 0));
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
   
   public Component getPanelComponent(PanelType t) {
      if (dictComponents.contains(t)) {
         return dictComponents.get(t);
      }
      return null;
   }
   
   
   public Color[][] getRenderImage() {
      return ((RenderHelper)dictComponents.get(PanelType.RENDER)).getColors();
   }
   
   public JMenuBar getMenuBar() {
      return ((MenuHelper)dictComponents.get(PanelType.MENU)).getMenuBar();
   }
   
   public void clearRenderImage() {
      ((RenderHelper)dictComponents.get(PanelType.RENDER)).clear();
      repaint();
   }
   
   public File askForFile(FileType type) {
      boolean askForFiles = true;
      if (askForFiles) {
         try {
            JFileChooser fileChooser = new JFileChooser();
            String directory = "target.txt";
            FileFilter filter = null;
            switch (type) {
               case IMAGE:
                  directory = "images/PlaceImagesHere.txt";
                  break;
               case MESHANDTEXTURE:
               case MESH:
                  directory = "meshes/PlaceMeshesHere.txt";
                  filter = 
                     new FileFilter()
                     {
                        public boolean accept(File f) { 
                           return f.getName().indexOf(".") == -1; }
                        public String getDescription() { 
                           return "Meshes"; }
                     };
                  break;
               case TEXTURE:
                  directory = "textures/PlaceTexturesHere.txt";
                  break;
            }
            File file = pickFile(fileChooser, directory, filter);
            if (file != null) {
               renderImage(ImageIO.read(file));
               repaint();
            }
            
         }
         catch (Exception e) {
            System.out.println(e);
         }
      }
      return null;
   }
   
      
   public static File pickFile(JFileChooser fileChooser, String directory, FileFilter filter)
   {
      File file = new File(directory);
      JFrame frame = new JFrame();
    // get the return value from choosing a file
      fileChooser.setCurrentDirectory(file);
      if (file != null) {
         fileChooser.setFileFilter(filter);
      }
      int returnVal = fileChooser.showOpenDialog(frame);
    
    // if the return value says the user picked a file 
      if (returnVal == JFileChooser.APPROVE_OPTION)
         file = fileChooser.getSelectedFile();
      else
         file = null;
      return file;
   }
   
   public void renderImage(Object object) {
      if (object instanceof BufferedImage)
         ((RenderHelper)dictComponents.get(PanelType.RENDER)).setImage((BufferedImage)object);
      else if (object instanceof Color[][])
         ((RenderHelper)dictComponents.get(PanelType.RENDER)).setColors((Color[][])object);
      else if (object instanceof Point)
         ((RenderHelper)dictComponents.get(PanelType.RENDER)).setPoint((Point)object);
      else if (object instanceof Line)
         ((RenderHelper)dictComponents.get(PanelType.RENDER)).setLine((Line)object);
      else if (object instanceof Simplex)
         ((RenderHelper)dictComponents.get(PanelType.RENDER)).setSimplex((Simplex)object);
      else if (object instanceof Mesh) {
         ((RenderHelper)dictComponents.get(PanelType.RENDER)).setMesh((Mesh)object);
      }
      this.repaint();
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
   

/** Container class to hold information about buttons. Might be obsolete, could remove later. */
   protected class MenuHelper extends Component {
      
      protected JMenuBar menuBar;
      
      protected JMenu menuFile;
      protected JMenu menuMesh;
      protected JMenu menuTexture;
      
      protected JMenuItem menuItemSaveFile;
      protected JMenuItem menuItemSaveMesh;
      protected JMenuItem menuItemSaveTexture;
      protected JMenuItem menuItemLoadFile;
      protected JMenuItem menuItemLoadMesh;
      protected JMenuItem menuItemLoadTexture;
      /** Creates new MenuHelper with location (x, y) and size (w, h).
      * @param x the x-coord of the location.
      * @param y the y-coord of the location.
      * @param w the width of the location.
      * @param h the height of the location.
      */
      public MenuHelper(int x, int y, int w, int h) {
         super(x, y, w, h);
         initialize();
      }
      
      /** Initializes the dictButtons hashtable */
      private void initialize() {
         menuBar = new JMenuBar();
         menuFile = new JMenu("File");
         menuMesh = new JMenu("Mesh");
         menuTexture = new JMenu("Texture");
         menuBar.add(menuFile);
         menuBar.add(menuMesh);
         menuBar.add(menuTexture);
         menuItemSaveFile = new JMenuItem("Save Mesh & Texture");
         menuItemSaveMesh = new JMenuItem("Save Mesh");
         menuItemSaveTexture = new JMenuItem("Save Texture");
         menuItemLoadFile = new JMenuItem("Load File");
         menuItemLoadMesh = new JMenuItem("Load Mesh");
         menuItemLoadTexture = new JMenuItem("Load Texture");
         menuFile.add(menuItemSaveFile);
         menuFile.add(menuItemLoadFile);
         menuMesh.add(menuItemSaveMesh);
         menuMesh.add(menuItemLoadMesh);
         menuTexture.add(menuItemSaveTexture);
         menuTexture.add(menuItemLoadTexture);
         menuItemSaveFile.addActionListener(new Listener_SaveFile());
         menuItemSaveMesh.addActionListener(new Listener_SaveMesh());
         menuItemSaveTexture.addActionListener(new Listener_SaveTexture());
         menuItemLoadFile.addActionListener(new Listener_LoadFile());
         menuItemLoadMesh.addActionListener(new Listener_LoadMesh());
         menuItemLoadTexture.addActionListener(new Listener_LoadTexture());
      }
   
      public JMenuBar getMenuBar() {
         return menuBar;
      }
      
      protected class Listener_LoadFile implements ActionListener
      {	      
         public void actionPerformed(ActionEvent e)
         {
            askForFile(FileType.ANY);
         }
      }
      
      protected class Listener_LoadMesh implements ActionListener
      {	      
         public void actionPerformed(ActionEvent e)
         {
            askForFile(FileType.MESH);
         }
      }
      
      protected class Listener_LoadTexture implements ActionListener
      {	      
         public void actionPerformed(ActionEvent e)
         {
            askForFile(FileType.TEXTURE);
         }
      }
      
      protected class Listener_SaveFile implements ActionListener
      {
         public void actionPerformed(ActionEvent e)
         {
            String s = JOptionPane.showInputDialog("Name of File?");
            if(s!=null)
            {
               Utilities.SaveMesh(s, null, true);
            }
         }
      }
      
      protected class Listener_SaveMesh implements ActionListener
      {	      
         public void actionPerformed(ActionEvent e)
         {
            String s = JOptionPane.showInputDialog("Name of File?");
            if(s!=null)
            {
               Utilities.SaveMesh(s, null, false);
            }
         }
      }
      
      protected class Listener_SaveTexture implements ActionListener
      {	      
         public void actionPerformed(ActionEvent e)
         {
            String s = JOptionPane.showInputDialog("Name of File?");
            if(s!=null)
            {
               Utilities.SaveTexture(s, new ArrayTexture(getRenderImage()));
            }
         }
      }
      
   }
   
/** Container class to hold information about buttons. Might be obsolete, could remove later. */
   protected class ButtonHelper extends Component {
   
      protected JComboBox meshes;
   
      /** Creates new ButtonHelper with location (x, y) and size (w, h).
      * @param x the x-coord of the location.
      * @param y the y-coord of the location.
      * @param w the width of the location.
      * @param h the height of the location.
      */
      public ButtonHelper(int x, int y, int w, int h) {
         super(x, y, w, h);
         initialize();
      }
      
      /** Initializes the dictButtons hashtable */
      private void initialize() {
         String[] meshStrings = new String[] { "Meshes", "Hypercube", "Hypersphere" };
         meshes = new JComboBox(meshStrings);
         meshes.addActionListener(new Listener_Meshes());
         meshes.setLocation(this.getX(), this.getY());
         meshes.setSize(this.getWidth() / 10, this.getHeight() / 5);
      }
      
      /** Returns an array of every Button in dictButtons.
      * @return an array containing every Button in dictButtons.
      */
      public JComponent[] getComponents() {
         return new JComponent[] { meshes };
      }
      
      protected class Listener_Meshes implements ActionListener
      {	      
         public void actionPerformed(ActionEvent e)
         {
            String actionName = (String)((JComboBox)e.getSource()).getSelectedItem();
            System.out.println(actionName);
         }
      }
   
   }
}