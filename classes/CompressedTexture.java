import java.awt.Color;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Comparator;

public class CompressedTexture implements Texture {
   
   private int[] bounds;
   
   private Color[] data;
   
   // https://www.baeldung.com/cs/jpeg-compression
   // https://stackoverflow.com/questions/19459831/rgb-to-ycbcr-conversion-problems

   public CompressedTexture(Color[][] image) {
      CompressedTexture.DivideImageIntoEights(CompressedTexture.DownsampleImage(CompressedTexture.ConvertImageColorSpace(image)));
   }
   
   public int getDimention(){
      return bounds.length;
   }
   
   public int[] getBounds(){
      return bounds;
   }
   
   public Color getColor(Point p){
      int mult = 1;
      float[] pos = p.getCoords();
      int index = (int)pos[0];
      for(int i = 1; i<pos.length; i++){
         mult *= bounds[0];
         index += pos[i]*mult;
      }
      if(index<data.length){
         return data[index];
      }
      return null;
   }
   
   public boolean setColor(Point p, Color c){
      if(p.length()==bounds.length){
         int mult = 1;
         float[] pos = p.getCoords();
         int index = (int)pos[0];
         for(int i = 1; i<pos.length; i++){
            if((int)pos[i] >=0 && (int)pos[i] <bounds[i]){
               mult *= bounds[0];
               index += pos[i]*mult;
            } else {
               return false;
            }
         }
         if(index<data.length){
            data[index] = c;
            return true;
         }
      }
      return false;
   }
   
   public static double[] ToYCC(Color rgb) {
      return CompressedTexture.ToYCC(new int[] { rgb.getRed(), rgb.getGreen(), rgb.getBlue() });
   }
   
   // also called YCbCr
   public static double[] ToYCC(int[] rgb) {
      double y = .299*rgb[0] + .587*rgb[1] + .114*rgb[2];
      double cb = 128 -.168736*rgb[0] -.331364*rgb[1] + .5*rgb[2];
      double cr = 128 +.5*rgb[0] - .418688*rgb[1] - .081312*rgb[2];
      return new double[] { y, cb, cr };
   }
   
   public static int[] ToRGB(double[] ycc) {
      double r = ycc[0] + 1.402 * (ycc[2]-128);
      double g = ycc[0] - .34414 * (ycc[1]-128) -  .71414 * (ycc[2]-128);
      double b = ycc[0] + 1.772 * (ycc[1]-128);
      return new int[] { (int)Math.round(r), (int)Math.round(g), (int)Math.round(b) };
   }
   
   public static double[][][] ConvertImageColorSpace(Color[][] image) {
      double[][][] imageYCC = new double[image.length][image[0].length][];
      for (int i = 0; i < image.length; i++) {
         for (int x = 0; x < image[i].length; x++) {
            imageYCC[i][x] = ToYCC(image[i][x]);
         }
      }
      return imageYCC;
   }
   
   public static double[][][] DownsampleImage(double[][][] list) {
      for (int i = 0; i < list.length; i += 2) {
         for (int x = 0; x < list[i].length; x += 2) {
            Average(new double[][] {
               TryGet(list, i, x),
               TryGet(list, i, x + 1),
               TryGet(list, i + 1, x),
               TryGet(list,i + 1, x + 1)
               });
         }
      }
      return list;
   }
   
   public static void Average(double[][] list) {
      double cb = 0;
      double cr = 0;
      for (int i = 0; i < list.length; i++) {
         cb += list[i][1];
         cr += list[i][2];
      }
      cb = cb / list.length;
      cr = cr / list.length;
      for (double[] l : list) {
         l[1] = cb;
         l[2] = cr;
      }
   }
   
   public static double[] TryGet(double[][][] o, int r, int c) {
      if (r >= 0 && r < o.length && c >= 0 && c < o[r].length)
         return o[r][c];
      return null;
   }
   
   public static double[][][][] DivideImageIntoEights(double[][][] list) {
      double[][][][] imageEights = new double[list.length / 8 + list.length % 8 > 0 ? 1 : 0][][][];
      int eightsIndex = 0;
      for (int i = 0; i < list.length; i += 8) {
         for (int x = 0; x < list[i].length; x += 8) {
            double[][][] imageSixtyFour = new double[8][8][];
            for (int e = 0; e < 8; e++) {
               for (int f = 0; f < 8; f++) {
                  imageSixtyFour[e][f] = TryGet(list, i + e, x + f);
               }
            }
            imageEights[eightsIndex++] = imageSixtyFour;
         }
      }
      return imageEights;
   }
   
   // Discrete Cosine transform, 3 matrixes: { luminance, chrominance, chrominance };
   public static Matrix[][] ForwardDCT(double[][][][] list) {
      Matrix[][] temp = new Matrix[list.length][3];
      for (int i = 0; i < temp.length; i++) {
         for (int x = 0; x < list[i].length; x++) {
            temp[i][x] = new Matrix(DoubleToFloat(applyDCT(list[i], x)));
         }
      }
      return temp;
   }
   
   // https://stackoverflow.com/questions/4240490/problems-with-dct-and-idct-algorithm-in-java
   public static double[][] applyDCT(double[][][] f, int index) {
      int N = 2;
      double[] c = new double[N];
      c[0] = 1/Math.sqrt(2.0);
      for (int i = 1; 1 < N; i++) {
         c[i] = 1;
      }
      double[][] F = new double[N][N];
      for (int u=0;u<N;u++) {
         for (int v=0;v<N;v++) {
            double sum = 0.0;
            for (int i=0;i<N;i++) {
               for (int j=0;j<N;j++) {
                  sum+=Math.cos(((2*i+1)/(2.0*N))*u*Math.PI)*Math.cos(((2*j+1)/(2.0*N))*v*Math.PI)*f[i][j][index];
               }
            }
            sum*=((c[u]*c[v])/4.0);
            F[u][v]=sum;
         }
      }
      return F;
   }
   
   public static Matrix[][] Quantization(Matrix[][] list) {
      for (Matrix[] matrix : list) {
         for (int i = 0; i < matrix.length; i++) {
            if (i == 0)
               matrix[i] = matrix[i].mult(PrecalculatedTables.LuminanceChannel.invert());
            else
               matrix[i] = matrix[i].mult(PrecalculatedTables.ChrominanceChannel.invert());
         }
      }
      return list;
   }

   // huffmanarray consisting of an array of 8x8 pixels, which each consist of a 3-length array of channels.
   // where each huffman tree is representative of a compressed matrix
   public static HuffmanTree[][] Encode(Matrix[][] list) {
      HuffmanTree[][] huffmanTrees = new HuffmanTree[list.length][list[0].length];
      for (int i = 0; i < list.length; i++) {
         HuffmanTree[] trees = new HuffmanTree[list[i].length];
         for (int x = 0; x < list[i].length; x++) {
            float[] temp = ZigZagEncode(list[i][x]);
            ArrayList<HuffmanTreeNode> nodes = new ArrayList<HuffmanTreeNode>();
            for (int f = 0; f < temp.length; f++) {
               int freq = 1;
               while (f + 1 < temp.length && temp[f] == temp[f + 1]) {
                  f++;
                  freq++;
               }
               nodes.add(new HuffmanTreeNode(freq, f));
            }
            trees[x] = new HuffmanTree(nodes);
         }
         huffmanTrees[i] = trees;
      }
      return huffmanTrees;
   }
   
   public static float[] ZigZagEncode(Matrix matrix) {
      float[] temp = new float[matrix.getData().length * matrix.getData()[0].length];
      int index = 0;
      int i = 0;
      int x = 0;
      boolean run = true;
      temp[index++] = matrix.getData()[i][x];
      while (run) {
         if (x + 1 < matrix.getData()[i].length) {
            x++;
            temp[index++] = matrix.getData()[i][x];
         }
         else {
            if (i + 1 < matrix.getData().length) {
               i++;
               temp[index++] = matrix.getData()[i][x];
            }
         }
         while (i + 1 < matrix.getData().length && x - 1 >= 0) {
            i++;
            x--;
            temp[index++] = matrix.getData()[i][x];
         }
         if (i + 1 < matrix.getData().length) {
            i++;
            temp[index++] = matrix.getData()[i][x];
         }
         else {
            if (x + 1 < matrix.getData()[i].length) {
               x++;
               temp[index++] = matrix.getData()[i][x];
            }
         }
         while (i - 1 >= 0 && x + 1 < matrix.getData()[i - 1].length) {
            i--;
            x++;
            temp[index++] = matrix.getData()[i][x];
         }
         if (index >= temp.length)
            run = false;
      }
      return temp;
   }
   
   public static Matrix ZigZagDecode(float[] temp) {
      float[][] data = new float[(int)Math.sqrt(temp.length)][(int)Math.sqrt(temp.length)];
      int index = 0;
      int i = 0;
      int x = 0;
      boolean run = true;
      data[i][x] = temp[index++];
      while (run) {
         if (x + 1 < data[i].length) {
            x++;
            data[i][x] = temp[index++];
         }
         else {
            if (i + 1 < data.length) {
               i++;
               data[i][x] = temp[index++];
            }
         }
         while (i + 1 < data.length && x - 1 >= 0) {
            i++;
            x--;
            data[i][x] = temp[index++];
         }
         if (i + 1 < data.length) {
            i++;
            data[i][x] = temp[index++];
         }
         else {
            if (x + 1 < data[i].length) {
               x++;
               data[i][x] = temp[index++];
            }
         }
         while (i - 1 >= 0 && x + 1 < data[i - 1].length) {
            i--;
            x++;
            data[i][x] = temp[index++];
         }
         if (index >= temp.length)
            run = false;
      }
      return new Matrix(data);
   }
   
   public static String Trim(String s) {
      if (s.charAt(0) == ' ')
         s = s.substring(1, s.length());
      if (s.charAt(s.length() - 1) == ' ')
         s = s.substring(0, s.length() - 1);
      return s;
   }
   
   public static class PrecalculatedTables {
      
      public static Matrix LuminanceChannel = new Matrix(new float[][]
         {
            { 6, 4, 4, 6, 10, 16, 20, 24 },
            { 5, 5, 6, 8, 10, 23, 24, 22 },
            { 6, 5, 6, 10, 16, 23, 28, 22 },
            { 6, 7, 9, 12, 20, 35, 32, 25 },
            { 7, 9, 15, 22, 27, 44, 41, 31 },
            { 10, 14, 22, 26, 32, 42, 45, 37 },
            { 20, 26, 31, 35, 41, 48, 48, 40 },
            { 29, 37, 38, 39, 45, 40, 41, 40 }
         }
      );
      
      public static Matrix ChrominanceChannel = new Matrix(new float[][]
         {
            { 10, 8, 9, 9, 9, 8, 10, 9 },
            { 10, 8, 9, 9, 9, 8, 10, 9 },
            { 9, 9, 10, 10, 10, 11, 12, 17 },
            { 13, 12, 12, 12, 12, 20, 16, 16 },
            { 14, 17, 18, 20, 23, 23, 22, 20 },
            { 25, 25, 25, 25, 25, 25, 25, 25},
            { 25, 25, 25, 25, 25, 25, 25, 25},
            { 25, 25, 25, 25, 25, 25, 25, 25},
         }
      );
   }
   
   
   public static float[][] DoubleToFloat(double[][] list) {
      float[][] temp = new float[list.length][];
      for (int i = 0; i < list.length; i++) {
         temp[i] = new float[list[i].length];
         for (int x = 0; x < list[i].length; x++) {
            temp[i][x] = (float)list[i][x];
         }
      }
      return temp;
   }
   
   public static void main(String[] args) {
      var v = ZigZagEncode(new Matrix(new float[][] {
         {1, 2, 3, 4, 5, 6, 7, 8 },
         {1, 2, 3, 4, 5, 6, 7, 8 },
         {1, 2, 3, 4, 5, 6, 7, 8 },
         {1, 2, 3, 4, 5, 6, 7, 8 },
         {1, 2, 3, 4, 5, 6, 7, 8 },
         {1, 2, 3, 4, 5, 6, 7, 8 },
         {1, 2, 3, 4, 5, 6, 7, 8 },
         {1, 2, 3, 4, 5, 6, 7, 8 },
         }));
      for (float f : v) {
         System.out.print(f + " ");
      }
      System.out.println(ZigZagDecode(v));
   }
}