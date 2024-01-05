import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Color;

public class Utilities {

   public static void SaveMesh(String fileName, Mesh mesh) {
      try {
         File file = new File("meshes/" + fileName);
         file.createNewFile();
         FileWriter writer = new FileWriter(file);
         ArrayList<String> list = new ArrayList<String>();
         Utilities.ConvertToFileFormat(list, mesh, "");
         for (String s : list) {
            writer.write(s + "\n");
         }
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
         ArrayList<String> list = new ArrayList<String>();
         while (reader.hasNextLine()) {
            list.add(reader.nextLine());
         }
         reader.close();
         return Utilities.ConvertFromFileFormatMesh(list);
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
         ArrayList<String> list = new ArrayList<String>();
         Utilities.ConvertToFileFormat(list, texture, "");
         for (String s : list) {
            writer.write(s + "\n");
         }
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
         ArrayList<String> list = new ArrayList<String>();
         while (reader.hasNextLine()) {
            list.add(reader.nextLine());
         }
         reader.close();
         return Utilities.ConvertFromFileFormatTexture(list);
      }
      catch (Exception e) {
         System.out.println(e);
      }
      return null;
   }
   
   public static void ConvertToFileFormat(ArrayList<String> list, Object o, String offset) {
      if (o instanceof Point) {
         list.add(offset + "Point " + ((Point)o).length());
         String temp = "";
         for (float f : ((Point)o).getCoordinates())
            temp += f + " ";
         list.add(offset + temp);
      }
      if (o instanceof Vector) {
         list.add(offset + "Vector " + ((Vector)o).length());
         String temp = "";
         for (float f : ((Vector)o).getCoordinates())
            temp += f + " ";
         list.add(offset + temp);
      }
      else if (o instanceof Plane) {
         list.add(offset + "Plane " + ((Plane)o).getDir().length);
         ConvertToFileFormat(list, ((Plane)o).getPos(), offset + " ");
         ConvertToFileFormat(list, ((Plane)o).getNorm(), offset + " ");
         for (Vector v : ((Plane)o).getDir())
            ConvertToFileFormat(list, v, offset + " ");
      }
      else if (o instanceof Simplex) {
         list.add(offset + "Simplex " + ((Simplex)o).getPoints().length);
         ConvertToFileFormat(list, ((Simplex)o).getSurface(), offset + " ");
         for (Point p : ((Simplex)o).getPoints())
            ConvertToFileFormat(list, p, offset + " ");
      }
      else if (o instanceof Mesh) {
         list.add(offset + "Mesh " + ((Mesh)o).getDimention());
         for (Simplex s : ((Mesh)o).getFaces())
            ConvertToFileFormat(list, s, offset + " ");
      }
      else if (o instanceof ArrayTexture) {
         list.add(offset + "ArrayTexture " + ((ArrayTexture)o).getDimention() + " " + ((ArrayTexture)o).getData().length);
         String temp = "";
         for (int i : ((ArrayTexture)o).getBounds())
            temp += i + " ";
         list.add(offset + temp);
         for (Color c : ((ArrayTexture)o).getData())
            ConvertToFileFormat(list, c, offset + " ");
      }
      else if (o instanceof Color) {
         list.add(offset + "Color " + ((Color)o).getRed() + " " + ((Color)o).getGreen() + " " + ((Color)o).getBlue() + " " + ((Color)o).getAlpha());
      }
   }
   
   public static Mesh ConvertFromFileFormatMesh(ArrayList<String> list) {
      return null;
   }
   
   public static Texture ConvertFromFileFormatTexture(ArrayList<String> list) {
      return null;
   }
   
   public static void main(String[] args) {
      Simplex[] faces = new Simplex[] { new Simplex(new Point[] { new Point(new float[] { 1, 1 }) }) };
      Mesh mesh = new Mesh(faces, 3);
      SaveMesh("Test", mesh);
   }
   
}