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
            temp += " " + f;
         list.add(offset + temp);
      }
      else if (o instanceof Vector) {
         list.add(offset + "Vector " + ((Vector)o).length());
         String temp = "";
         for (float f : ((Vector)o).getCoordinates())
            temp += f + " ";
         list.add(offset + temp);
      }
      else if (o instanceof Plane) {
         list.add(offset + "Plane");
         ConvertToFileFormat(list, ((Plane)o).getPos(), offset + " ");
         ConvertToFileFormat(list, ((Plane)o).getNorm(), offset + " ");
      }
      else if (o instanceof Simplex) {
         list.add(offset + "Simplex " + ((Simplex)o).getPoints().length);
         for (Point p : ((Simplex)o).getPoints())
            ConvertToFileFormat(list, p, offset + " ");
      }
      else if (o instanceof Mesh) {
         list.add(offset + "Mesh " + ((Mesh)o).getDimention() + " " + ((Mesh)o).getFaces().length);
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
      else
         list.add(offset + null);
   }
   
   public static Mesh ConvertFromFileFormatMesh(ArrayList<String> list) {
      return (Mesh)ConvertFromFileFormat(list);
   }
   
   public static Object ConvertFromFileFormat(ArrayList<String> list) {
      if (list.size() == 0)
         return null;
      String[] head = RemoveWhiteSpace(list.remove(0));
      String[] components = head[0].split(" ");
      if (components[0].equalsIgnoreCase("Mesh")) {
         int dimension = (int)Float.parseFloat(components[1]);
         Simplex[] temp = new Simplex[Integer.parseInt(components[2])];
         for (int i = 0; i < temp.length; i++) {
            temp[i] = (Simplex)ConvertFromFileFormat(GetFromFileFormat(list));
         }
         return new Mesh(temp, dimension);
      }
      else if (components[0].equalsIgnoreCase("Simplex")) {
         Point[] temp = new Point[Integer.parseInt(components[1])];
         for (int i = 0; i < temp.length; i++) {
            temp[i] = (Point)ConvertFromFileFormat(GetFromFileFormat(list));
         }
         return new Simplex(temp);
      }
      else if (components[0].equalsIgnoreCase("Plane")) {
         Point pos = (Point)ConvertFromFileFormat(GetFromFileFormat(list));
         Vector norm = (Vector)ConvertFromFileFormat(GetFromFileFormat(list));
         return new Plane(pos, norm);
      }
      else if (components[0].equalsIgnoreCase("Point")) {
         return new Point((float[])ConvertFromFileFormat(GetFromFileFormat(list)));
      }
      else {
         try {
            float[] temp = new float[components.length];
            for (int i = 0; i < temp.length; i++) {
               temp[i] = Float.parseFloat(components[i]);
            }
            return temp;
         }
         catch (Exception e) {
         
         }
      }
      return null;
   }
   
   public static Texture ConvertFromFileFormatTexture(ArrayList<String> list) {
      return null;
   }
   
   public static ArrayList<String> GetFromFileFormat(ArrayList<String> list) {
      ArrayList<String> temp = new ArrayList<String>();
      if (list.size() == 0)
         return temp;
      String[] head = RemoveWhiteSpace(list.get(0));
      temp.add(list.remove(0));
      while (list.size() > 0 && ! RemoveWhiteSpace(list.get(0))[1].equals(head[1])) {
         temp.add(list.remove(0));
      }
      return temp;
   }
   
   public static String[] RemoveWhiteSpace(String s) {
      int index = 0;
      while (index < s.length() && s.substring(index, index + 1).equals(" ")) {
         index++;
      }
      s = s.substring(index);
      return new String[] { s, "" + index };
   }
   
   public static void main(String[] args) {
      Simplex[] faces = new Simplex[] { new Simplex(new Point[] { new Point(new float[] { 1, 1 }) }) };
      Mesh mesh = new Mesh(faces, 3);
      System.out.println(mesh);
      SaveMesh("Test", mesh);
      mesh = LoadMesh("Test");
      System.out.println(mesh);
   }
   
}