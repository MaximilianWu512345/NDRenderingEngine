import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Color;

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
      try {
         File file = new File("meshes/" + fileName);
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
         FileWriter writer = new FileWriter(file);
         writer.write(Utilities.TextureToFile(texture));
         writer.close();
      }
      catch (Exception e) {
         System.out.println(e);
      }
   }
   
   public static Texture LoadTexture(String fileName) {
      try {
         File file = new File("textures/" + fileName);
         Scanner reader = new Scanner(file);
         String text = "";
         text += reader.nextLine();
         reader.close();
         return Utilities.FileToTexture(text);
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
            SaveTexture(textureName + "$" + i, faces[i].getTexture());
            temp += " \"" + textureName + "$" + i + "\"";
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
         Texture texture = LoadTexture(temp[index]);
         simplexes[i] = new Simplex(new Point[ToInt(temp[index + 1])], true);
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
   
   public static String TextureToFile(Texture texture) {
      if (texture == null)
         return "";
      CompressedTexture compressedTexture = new CompressedTexture(TextureToColors(texture));
      HuffmanTree[][][] d3 = compressedTexture.getTextures();
      for (int i = 0; i < d3.length; i++) {
         HuffmanTree[][] d2 = d3[i];
         for (int x = 0; x < d2.length; x++) {
            HuffmanTree[] d1 = d2[x];
            for (int c = 0; c < d1.length; c++) {
               HuffmanTree tree = d1[c];
            }
         }
      }
      String temp = "";
      return temp;
   }
   
   public static Texture FileToTexture(String list) {
      return null;
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
   
   public static void main(String[] args) {
      Simplex[] faces = new Simplex[] { new Simplex(new Point[] { new Point(new float[] { 1, 1 }) }),
         new Simplex(new Point[] { new Point(new float[] { 1, 1 }) }),
         new Simplex(new Point[] { new Point(new float[] { 1, 1 }) })
         };
      Mesh mesh = new Mesh(faces, 3);
      System.out.println(mesh);
      SaveMesh("Test", mesh, true);
      mesh = LoadMesh("Test");
      System.out.println(mesh);
   }
   
}