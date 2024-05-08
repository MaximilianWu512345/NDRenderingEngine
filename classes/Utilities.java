import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Color;
import java.io.FileOutputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Utilities {

   public static void SaveMesh(String fileName, Mesh mesh, boolean autoSaveTexture) {
      try {
         File file = new File("meshes/" + fileName);
         file.createNewFile();
         FileWriter writer = new FileWriter(file);
         writer.write(Utilities.MeshToFile(autoSaveTexture ? fileName : null, mesh));
         writer.close();
      }
      catch (Exception e) {
         System.out.println(e);
      }
   }
   
   public static Mesh LoadMesh(String fileName) {
      return LoadMesh(new File("meshes/" + fileName));
   }
   
   public static Mesh LoadMesh(File file) {
      try {
         Scanner reader = new Scanner(file);
         String text = "";
         while (reader.hasNextLine()) {
            text += reader.nextLine();
         }
         reader.close();
         return FileToMesh(text);
      }
      catch (Exception e) {
         System.out.println(e);
      }
      return null;
   }
   
   public static void SaveTexture(String fileName, Texture texture) {
      try {
         File file = new File("textures/" + fileName);
         file.createNewFile();
         Color[][] colors = TextureToColors(texture);
         BufferedImage temp = new BufferedImage(colors.length, colors[0].length, BufferedImage.TYPE_4BYTE_ABGR);
         for (int i = 0; i < colors.length && i < temp.getWidth(); i++) {
            for (int x = 0; x < colors[i].length && x < temp.getHeight(); x++) {
               if (colors[i][x] != null) {
                  temp.setRGB(i, x, colors[i][x].getRGB());
               }
            }
         }
         ImageIO.write(temp, "png", file);
      }
      catch (Exception e) {
         System.out.println(e);
      }
   }
   
   public static Texture LoadTexture(String fileName) {
      return LoadTexture(new File("textures/" + fileName));
   }
   
   public static Texture LoadTexture(File file) {
      try {
         if (file != null) {
            BufferedImage renderedImage = ImageIO.read(file);
            Color[][] colors = new Color[renderedImage.getWidth()][renderedImage.getHeight()];
            for (int i = 0; i < colors.length && i < renderedImage.getWidth(); i++) {
               for (int x = 0; x < colors[i].length && x < renderedImage.getHeight(); x++) {
                  if (colors[i][x] != null) {
                     renderedImage.setRGB(i, x, colors[i][x].getRGB());
                  }
               }
            }
            return new ArrayTexture(colors);
         }
      }
      catch (Exception e) {
         System.out.println(e);
      }
      return null;
   }
   
   public static String MeshToFile(String textureName, Mesh mesh) {
      if (mesh == null) {
         return "";
      }
      Simplex[] faces = mesh.getFaces();
      String temp = (int)mesh.getDimention() + " " + faces.length;
      for (int i = 0; i < faces.length; i++) {
         if (textureName != null) {
            SaveTexture(textureName + "$" + i + ".png", faces[i].getTexture());
            temp += " \"" + textureName + "$" + i + ".png\"";
         }
         else {
            temp += " \"\"";
         }
         temp += " " + faces[i].getPoints().length;
      }
      for (int i = 0; i < faces.length; i++) {
         for (int x = 0; x < faces[i].getPoints().length; x++) {
            temp += " " + faces[i].getPoints()[x].getCoordinates().length;
         }
      }
      for (int i = 0; i < faces.length; i++) {
         for (int x = 0; x < faces[i].getPoints().length; x++) {
            for (float f : faces[i].getPoints()[x].getCoordinates())
               temp += " " + f;
         }
      }
      return temp;
   }
   
   public static Mesh FileToMesh(String list) {
      if (list == null) {
         return null;
      }
      String[] temp = list.split(" ");
      Simplex[] simplexes = new Simplex[ToInt(temp[1])];
      int index = 2;
      for (int i = 0; i < simplexes.length; i++) {
         simplexes[i] = new Simplex(new Point[ToInt(temp[index + 1])]);
         simplexes[i].setTexture(LoadTexture(temp[index].replaceAll("\"", "")));
         index += 2;
      }
      for (int i = 0; i < simplexes.length; i++) {
         for (int x = 0; x < simplexes[i].getPoints().length; x++) {
            simplexes[i].getPoints()[x] = new Point(new float[ToInt(temp[index])]);
            index++;
         }
      }
      for (int i = 0; i < simplexes.length; i++) {
         for (int x = 0; x < simplexes[i].getPoints().length; x++) {
            for (int c = 0; c < simplexes[i].getPoints()[x].getCoordinates().length; c++) {
               simplexes[i].getPoints()[x].getCoordinates()[c] = ToFloat(temp[index]);
               index++;
            }
         }
         simplexes[i].setPoints(simplexes[i].getPoints());
      }
      return new Mesh(simplexes, ToInt(temp[0]));
   }
   
   public static int ToInt(String text) {
      return Integer.parseInt(text);
   }
   
   public static float ToFloat(String text) {
      return Float.parseFloat(text);
   }
   
   public static Color[][] TextureToColors(Texture texture) {
      Color[][] colors = new Color[texture.getBounds()[0]][texture.getBounds()[1]];
      for (int i = 0; i < texture.getBounds()[0]; i++) {
         for (int x = 0; x < texture.getBounds()[1]; x++) {
            colors[i][x] = texture.getColor(new Point(new float[] { i, x } ));
         }
      }
      return colors;
   }
      
   public static Color[][] TrimArray(Color[][] object) {
      ArrayList<ArrayList<Color>> array = new ArrayList<ArrayList<Color>>();
      Color clear = new Color(0, 0, 0, 0);
      for (int i = 0; i < object.length; i++) {
         for (int x = 0; x < object[i].length; x++) {
            if (object[i][x] != null && ! object[i][x].equals(clear)) {
               if (i >= array.size())
                  array.add(new ArrayList<Color>());
               array.get(i).add(object[i][x]);
            }
         }
      }
      Color[][] temp = new Color[array.size()][];
      for (int i = 0; i < array.size(); i++) {
         temp[i] = new Color[array.get(i).size()];
         for (int x = 0; x < array.get(i).size(); x++) {
            temp[i][x] = array.get(i).get(x);
         }
      }
      return temp;
   }
   
   public static void main(String[] args) {
      Simplex[] faces = new Simplex[] {
         new Simplex(new Point[] { new Point(new float[] { 1, 1 }) }),
         new Simplex(new Point[] { new Point(new float[] { 1, 1 }) }),
         new Simplex(new Point[] { new Point(new float[] { 1, 1 }) })
         };
      Mesh mesh = new Mesh(faces, 3);
      /*
      System.out.println(mesh);
      SaveMesh("Test", mesh, true);
      mesh = LoadMesh("Test");
      */
      float[][] data = new float[2][2];
      data[0][0] = 3;
      data[1][0] = 2;
      Matrix m = new Matrix(data);
      System.out.println(mesh);
      mesh.transform(m);
      System.out.println(mesh);
   }
   
}