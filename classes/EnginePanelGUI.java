import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.util.*;
import javax.swing.filechooser.FileFilter;
import javax.imageio.ImageIO;

public class EnginePanelGUI {

   public enum FileType {
      ANY,
      IMAGE,
      MESHANDTEXTURE,
      MESH,
      TEXTURE
   }
   
   public EnginePanelGUI(JFrame frame, EnginePanel owner, boolean enableJMenuBar) {
      engineFrame = frame;
      enginePanel = owner;
      frame.setSize(frame.getWidth() * 6 / 5, frame.getHeight());
      enginePanel.setSize(enginePanel.getWidth() * 6 / 5, enginePanel.getHeight());
      enginePanel.addComponent(menuHelper = new MenuHelper(0, 0, 0, 0));
      enginePanel.addComponent(buttonHelper = new ButtonHelper(enginePanel.getWidth() * 5 / 6, 0, enginePanel.getWidth() / 6, enginePanel.getHeight()));
      if (enableJMenuBar) {
         frame.setJMenuBar(this.getMenuBar());
         frame.setSize(frame.getWidth(), frame.getHeight() + 25);
      }
   }
   
   protected JFrame engineFrame;
   
   protected EnginePanel enginePanel;
   
   protected MenuHelper menuHelper;
   
   protected ButtonHelper buttonHelper;

   public JMenuBar getMenuBar() {
      return menuHelper.getMenuBar();
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
               enginePanel.renderImage(ImageIO.read(file));
               enginePanel.repaint();
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
               Utilities.SaveTexture(s, new ArrayTexture(enginePanel.getRenderImage()));
            }
         }
      }
      
   }
   
/** Container class to hold information about buttons. Might be obsolete, could remove later. */
   protected class ButtonHelper extends Component {
      
      protected ArrayList<Button> buttons;
   
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
      
      private void initialize() {
         buttons = new ArrayList<Button>();
         buttons.add(new Button_NewMesh(this.getX(), this.getY(), this.getWidth(), this.getHeight() / 20, Color.white, "+"));
         buttons.add(new Button_Render(this.getX(), this.getY() + this.getHeight() * 19 / 20, this.getWidth(), this.getHeight() / 20, Color.white, "Render Mesh"));
      }
      
      public Mesh createMesh(String type) {
         switch (type) {
            case "Default":
               return new Mesh(new Simplex[0], 0);
            case "Hypercube":
               return null;
            case "Hypersphere":
               return null;
            default:
               return null;
         }
      }
      
      public void updateForMesh() {
         ArrayList<Mesh> meshes = enginePanel.getMeshes();
         if (meshes.size() > 0) {
            enginePanel.remove(buttons.get(0));
         }
         enginePanel.repaint();
      }
      
      /** Returns an array of every Button in dictButtons.
      * @return an array containing every Button in dictButtons.
      */
      public JComponent[] getComponents() {
         JComponent[] temp = new JComponent[buttons.size()];
         for (int i = 0; i < buttons.size(); i++) {
            temp[i] = buttons.get(i);
         }
         return temp;
      }
      
      protected void paintComponent(Graphics g) {
         ArrayList<Mesh> meshes = enginePanel.getMeshes();
      }
      
      protected class Button_Render extends Button {
         
         public Button_Render(int x, int y, int width, int height, Color color, String text) {
            super(x, y, width, height, color, text);
         }
         
         public void mousePressed(MouseEvent e) {
            enginePanel.render();
         }
      }
      
      protected class Button_NewMesh extends Button {
      
         public JFrame frame;
         
         public Button_NewMesh(int x, int y, int width, int height, Color color, String text) {
            super(x, y, width, height, color, text);
            String[] meshStrings = new String[] { "Default", "Hypercube", "Hypersphere" };
            JList list = new JList(meshStrings);
            list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            list.setLayoutOrientation(JList.VERTICAL);
            list.setVisibleRowCount(-1);
            JScrollPane listScroller = new JScrollPane(list);
            listScroller.setPreferredSize(new Dimension(250, 250));
            listScroller.setVisible(true);
            frame = new JFrame("New Mesh");
            frame.setSize(listScroller.getPreferredSize());
            frame.setLocation(getX() + getWidth() - (int)frame.getSize().getWidth(), getY());
            frame.setContentPane(listScroller);
            MouseListener mouseListener = 
               new MouseAdapter() {
                  public void mouseClicked(MouseEvent e) {
                     if (e.getClickCount() == 2) {
                        enginePanel.addMesh(createMesh((String)list.getSelectedValue()));
                        updateForMesh();
                        frame.setVisible(false);
                     }
                  }
               };
            list.addMouseListener(mouseListener);
         }
         
         public void mousePressed(MouseEvent e) {
            frame.setLocation((int)engineFrame.getLocation().getX() + getX() + getWidth() - (int)frame.getSize().getWidth(), (int)engineFrame.getLocation().getY() + getY());
            frame.setVisible(true);
         }
      }
      
   
   
   }
   
}