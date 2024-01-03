import java.awt.Color;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Predicate;

public class CompressedTexture implements Texture {
   
   // 2d array of an array containing the 3 channels of ycc
   private HuffmanTree[][][] textures;
   // https://www.baeldung.com/cs/jpeg-compression
   // https://stackoverflow.com/questions/19459831/rgb-to-ycbcr-conversion-problems

   public CompressedTexture(Color[][] image) {
      YCC[][] colorSpace = ConvertImageColorSpace(image);
      YCC[][] downsample = DownsampleImage(colorSpace);
      YCCBlock[][] eights = DivideImageIntoEights(downsample);
      YCCBlock[][] dct = ForwardDCT(eights);
      YCCBlock[][] quant = Quantization(dct);
      HuffmanTree[][][] tex = Encode(quant);
      textures = tex;
   }
   
   public Color[][] decompress() {
      if (textures == null)
         return null;
      YCCBlock[][] decode = Decode(textures);
      YCCBlock[][] quant = ReverseQuantization(decode);
      YCCBlock[][] dct = ReverseDCT(quant);
      YCC[][] block = CombineImageFromEights(dct);
      Print(block);
      Color[][] rgb = ReverseImageColorSpace(block);
      return rgb;
   }
   
   public CompressedTexture(HuffmanTree[][][] tex) {
      textures = tex;
   }

   public int[] getBounds() {
      return null;
   }
   
   public Color getColor(Point p){
      return null;
   }
   
   public int getDimention() {
      return 0;
   }
   
   public boolean setColor(Point p, Color c){
      return false;
   }
   
   public static YCC ToYCC(Color rgb) {
      return CompressedTexture.ToYCC(new int[] { rgb.getRed(), rgb.getGreen(), rgb.getBlue() });
   }
   
   // also called YCbCr
   public static YCC ToYCC(int[] rgb) {
      double y = .299*rgb[0] + .587*rgb[1] + .114*rgb[2];
      double cb = 128 -.168736*rgb[0] -.331364*rgb[1] + .5*rgb[2];
      double cr = 128 +.5*rgb[0] - .418688*rgb[1] - .081312*rgb[2];
      return new YCC(y, cb, cr);
   }
   
   public static Color ToRGB(double[] ycc) {
      double r = ycc[0] + 1.402 * (ycc[2]-128);
      double g = ycc[0] - .34414 * (ycc[1]-128) -  .71414 * (ycc[2]-128);
      double b = ycc[0] + 1.772 * (ycc[1]-128);
      if (r < 0)
         r = 0;
      if (g < 0)
         g = 0;
      if (b < 0)
         b = 0;
      if (r > 255)
         r = 255;
      if (g > 255)
         g = 255;
      if (b > 255)
         b = 255;
      return new Color((int)Math.round(r), (int)Math.round(g), (int)Math.round(b));
   }
   
   public static YCC[][] ConvertImageColorSpace(Color[][] image) {
      YCC[][] imageYCC = new YCC[image.length][];
      for (int i = 0; i < image.length; i++) {
         imageYCC[i] = new YCC[image[i].length];
         for (int x = 0; x < image[i].length; x++) {
            imageYCC[i][x] = ToYCC(image[i][x]);
         }
      }
      return imageYCC;
   }
   
   public static Color[][] ReverseImageColorSpace(YCC[][] image) {
      Color[][] imageColor = new Color[image.length][];
      for (int i = 0; i < image.length; i++) {
         imageColor[i] = new Color[image[i].length];
         for (int x = 0; x < image[i].length; x++) {
            imageColor[i][x] = ToRGB(image[i][x].toArray());
         }
      }
      return imageColor;
   }
   
   public static YCC[][] DownsampleImage(YCC[][] list) {
      for (int i = 0; i < list.length; i += 2) {
         for (int x = 0; x < list[i].length; x += 2) {
            Average(new YCC[] {
               TryGet(list, i, x),
               TryGet(list, i, x + 1),
               TryGet(list, i + 1, x),
               TryGet(list,i + 1, x + 1)
               });
         }
      }
      return list;
   }
   
   public static void Average(YCC[] list) {
      double cb = 0;
      double cr = 0;
      for (int i = 0; i < list.length; i++) {
         if (list[i] == null)
            continue;
         cb += list[i].getChrominanceBlue();
         cr += list[i].getChrominanceRed();
      }
      cb = cb / list.length;
      cr = cr / list.length;
      for (YCC l : list) {
         if (l == null)
            continue;
         l.setChrominanceBlue(cb);
         l.setChrominanceRed(cr);
      }
   }
   
   public static YCC TryGet(YCC[][] o, int r, int c) {
      if (r >= 0 && r < o.length && c >= 0 && c < o[r].length)
         return o[r][c];
      return null;
   }
   
   public static YCCBlock[][] DivideImageIntoEights(YCC[][] list) {
      YCCBlock[][] imageEights = new YCCBlock[list.length / 8 + (list.length % 8 > 0 ? 1 : 0)][];
      for (int i = 0; i < imageEights.length; i++) {
         imageEights[i] = new YCCBlock[list[i].length / 8 + (list[i].length % 8 > 0 ? 1 : 0)];
         for (int x = 0; x < imageEights[i].length; x++) {
            YCC[][] imageSixtyFour = new YCC[8][8];
            for (int e = 0; e < imageSixtyFour.length; e++) {
               for (int f = 0; f < imageSixtyFour[e].length; f++) {
                  imageSixtyFour[e][f] = TryGet(list, i * 8 + e, x * 8 + f);
               }
            }
            
            imageEights[i][x] = new YCCBlock(imageSixtyFour);
         }
      }
      return imageEights;
   }
   
   public static YCC[][] CombineImageFromEights(YCCBlock[][] list) {
      YCC[][] image = new YCC[list.length * 8][list[0].length * 8];
      for (int i = 0; i < list.length; i++) {
         for (int x = 0; x < list[i].length; x++) {
            YCC[][] eights = list[i][x].getYCCBlock();
            for (int e = 0; e < eights.length; e++) {
               for (int f = 0; f < eights[e].length; f++) {
                  image[i * 8 + e][x * 8 + f] = eights[e][f];
               }
            }
         }
      }
      return image;
   }
   
   // Discrete Cosine transform, 3 matrixes: { luminance, chrominance, chrominance };
   public static YCCBlock[][] ForwardDCT(YCCBlock[][] list) {
      for (int i = 0; i < list.length; i++) {
         for (int x = 0; x < list[i].length; x++) {
            Matrix[] temp = new Matrix[3];
            for (int c = 0; c < temp.length; c++) {
               temp[c] = new Matrix(DoubleToFloat(applyDCT(list[i][x].getYCCBlock(), c)));
            }
            list[i][x].setMatrixBlock(temp);
         }
      }
      return list;
   }
   
   public static YCCBlock[][] ReverseDCT(YCCBlock[][] list) {
      for (int i = 0; i < list.length; i++) {
         for (int x = 0; x < list[i].length; x++) {
            Matrix[] temp = list[i][x].getMatrixBlock();
            double[][][] ycc = new double[3][][];
            for (int c = 0; c < temp.length; c++) {
               ycc[c] = applyIDCT(FloatToDouble(temp[c].getData()));
            }
            YCC[][] block = new YCC[ycc[0].length][ycc[0][0].length];
            for (int f = 0; f < ycc[0].length; f++) {
               for (int g = 0; g < ycc[0][f].length; g++) {
                  block[f][g] = new YCC(ycc[0][f][g], ycc[1][f][g], ycc[2][f][g]);
               }
            }
            list[i][x].setYCCBlock(block);
         }
      }
      return list;
   }
   
   // https://stackoverflow.com/questions/4240490/problems-with-dct-and-idct-algorithm-in-java
   public static double[][] applyDCT(YCC[][] f, int type) {
      int N = 8;
      double[] c = new double[N];
      c[0] = 1/Math.sqrt(2.0);
      for (int i = 1; i < N; i++) {
         c[i] = 1;
      }
      double[][] F = new double[N][N];
      for (int u=0;u<N;u++) {
         for (int v=0;v<N;v++) {
            double sum = 0.0;
            for (int i=0;i<N;i++) {
               for (int j=0;j<N;j++) {
                  if (f[i][j] == null)
                     continue;
                  double channel = 1;
                  if (type == 0) {
                     channel = f[i][j].getLuminance();
                  }
                  else if (type == 1) {
                     channel = f[i][j].getChrominanceBlue();
                  }
                  else if (type == 2) {
                     channel = f[i][j].getChrominanceRed();
                  }
                  sum+=Math.cos(((2*i+1)/(2.0*N))*u*Math.PI)*Math.cos(((2*j+1)/(2.0*N))*v*Math.PI)*channel;
               }
            }
            sum*=((c[u]*c[v])/4.0);
            F[u][v]=sum;
         }
      }
      return F;
   }
   
   public static double[][] applyIDCT(double[][] F) {
      int N = 8;
      double[] c = new double[N];
      c[0] = 1/Math.sqrt(2.0);
      for (int i = 1; i < N; i++) {
         c[i] = 1;
      }
      double[][] f = new double[N][N];
      for (int i=0;i<N;i++) {
         for (int j=0;j<N;j++) {
            double sum = 0.0;
            for (int u=0;u<N;u++) {
               for (int v=0;v<N;v++) {
                  sum+=(c[u]*c[v])/4.0*Math.cos(((2*i+1)/(2.0*N))*u*Math.PI)*Math.cos(((2*j+1)/(2.0*N))*v*Math.PI)*F[u][v];
               }
            }
            f[i][j]=Math.round(sum);
         }
      }
      return f;
   }
   
   public static YCCBlock[][] Quantization(YCCBlock[][] list) {
      for (YCCBlock[] blockList : list) {
         for (YCCBlock block : blockList) {
            for (int i = 0; i < block.getMatrixBlock().length; i++) {
               if (i == 0) {
                  float[][] data = PrecalculatedTables.LuminanceChannel;
                  for (int r = 0; r < data.length; r++) {
                     for (int c = 0; c < data[r].length; c++) {
                        block.getMatrixBlock()[i].getData()[r][c] = (float)(int)Math.round(block.getMatrixBlock()[i].getData()[r][c] / data[r][c]);
                     }
                  }
               }
               else {
                  float[][] data = PrecalculatedTables.ChrominanceChannel;
                  for (int r = 0; r < data.length; r++) {
                     for (int c = 0; c < data[r].length; c++) {
                        block.getMatrixBlock()[i].getData()[r][c] = (float)(int)Math.round(block.getMatrixBlock()[i].getData()[r][c] / data[r][c]);
                     }
                  }
               }
            }
         }
      }
      return list;
   }
   
   public static YCCBlock[][] ReverseQuantization(YCCBlock[][] list) {
      for (YCCBlock[] blockList : list) {
         for (YCCBlock block : blockList) {
            for (int i = 0; i < block.getMatrixBlock().length; i++) {
               if (i == 0) {
                  float[][] data = PrecalculatedTables.LuminanceChannel;
                  for (int r = 0; r < data.length; r++) {
                     for (int c = 0; c < data[r].length; c++) {
                        block.getMatrixBlock()[i].getData()[r][c] = block.getMatrixBlock()[i].getData()[r][c] * data[r][c];
                     }
                  }
               }
               else {
                  float[][] data = PrecalculatedTables.ChrominanceChannel;
                  for (int r = 0; r < data.length; r++) {
                     for (int c = 0; c < data[r].length; c++) {
                        block.getMatrixBlock()[i].getData()[r][c] = block.getMatrixBlock()[i].getData()[r][c] * data[r][c];
                     }
                  }
               }
            }
         }
      }
      return list;
   }

   // huffmanarray consisting of an array of 8x8 pixels, which each consist of a 3-length array of channels.
   // where each huffman tree is representative of a compressed matrix
   public static HuffmanTree[][][] Encode(YCCBlock[][] list) {
      HuffmanTree[][][] huffmanTrees = new HuffmanTree[list.length][][];
      for (int i = 0; i < list.length; i++) {
         HuffmanTree[][] trees = new HuffmanTree[list[i].length][];
         for (int x = 0; x < list[i].length; x++) {
            HuffmanTree[] channels = new HuffmanTree[list[i][x].getMatrixBlock().length];
            for (int c = 0; c < channels.length; c++) {
               float[] temp = ZigZagEncode(list[i][x].getMatrixBlock()[c]);
               ArrayList<HuffmanTreeNode> nodes = new ArrayList<HuffmanTreeNode>();
               for (int f = 0; f < temp.length; f++) {
                  int freq = 1;
                  while (f + 1 < temp.length && temp[f] == temp[f + 1]) {
                     f++;
                     freq++;
                  }
                  HuffmanTreeNode orig = null;
                  for (HuffmanTreeNode t : nodes) {
                     if (t.getValue().equals(temp[f])) {
                        orig = t;
                        break;
                     }
                  }
                  if (orig != null) {
                     orig.addFrequency(freq);
                  }
                  else
                     nodes.add(new HuffmanTreeNode(freq, temp[f]));
               }
               Float[] objArray = new Float[temp.length];
               for (int g = 0; g < temp.length; g++) {
                  objArray[g] = temp[g];
               }
               channels[c] = new HuffmanTree<Float>(nodes, objArray);
            }
            trees[x] = channels;
         }
         huffmanTrees[i] = trees;
      }
      return huffmanTrees;
   }
   
   public static YCCBlock[][] Decode(HuffmanTree[][][] huffmanTrees) {
      YCCBlock[][] temp = new YCCBlock[huffmanTrees.length][];
      for (int i = 0; i < huffmanTrees.length; i++) {
         temp[i] = new YCCBlock[huffmanTrees[i].length];
         for (int x = 0; x < huffmanTrees[i].length; x++) {
            temp[i][x] = new YCCBlock(null);
            Matrix[] matrixes = new Matrix[huffmanTrees[i][x].length];
            for (int c = 0; c < matrixes.length; c++) {
               String[] decode = huffmanTrees[i][x][c].getDecodedString().split(" ");
               float[] decodeFloat = new float[decode.length];
               for (int f = 0; f < decodeFloat.length; f++) {
                  decodeFloat[f] = Float.parseFloat(decode[f]);
               }
               matrixes[c] = ZigZagDecode(decodeFloat);
            }
            temp[i][x].setMatrixBlock(matrixes);
         }
      }
      return temp;
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
      
      public static float[][] LuminanceChannel = new float[][]
         {
            { 6, 4, 4, 6, 10, 16, 20, 24 },
            { 5, 5, 6, 8, 10, 23, 24, 22 },
            { 6, 5, 6, 10, 16, 23, 28, 22 },
            { 6, 7, 9, 12, 20, 35, 32, 25 },
            { 7, 9, 15, 22, 27, 44, 41, 31 },
            { 10, 14, 22, 26, 32, 42, 45, 37 },
            { 20, 26, 31, 35, 41, 48, 48, 40 },
            { 29, 37, 38, 39, 45, 40, 41, 40 }
         };
      
      public static float[][] ChrominanceChannel = new float[][]
         {
            { 10, 8, 9, 9, 9, 8, 10, 9 },
            { 10, 8, 9, 9, 9, 8, 10, 9 },
            { 9, 9, 10, 10, 10, 11, 12, 17 },
            { 13, 12, 12, 12, 12, 20, 16, 16 },
            { 14, 17, 18, 20, 23, 23, 22, 20 },
            { 25, 25, 25, 25, 25, 25, 25, 25},
            { 25, 25, 25, 25, 25, 25, 25, 25},
            { 25, 25, 25, 25, 25, 25, 25, 25}
         };
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
   
   public static double[][] FloatToDouble(float[][] list) {
      double[][] temp = new double[list.length][];
      for (int i = 0; i < list.length; i++) {
         temp[i] = new double[list[i].length];
         for (int x = 0; x < list[i].length; x++) {
            temp[i][x] = (double)list[i][x];
         }
      }
      return temp;
   }
   
   public static CompressedTexture CreateTexture(Color[][] image) {
      YCC[][] colorSpace = ConvertImageColorSpace(image);
      YCC[][] downsample = DownsampleImage(colorSpace);
      YCCBlock[][] eights = DivideImageIntoEights(downsample);
      YCCBlock[][] dct = ForwardDCT(eights);
      YCCBlock[][] quant = Quantization(dct);
      HuffmanTree[][][] tex = Encode(quant);
      return new CompressedTexture(tex);
   }
   
   public static void Print(float[] array) {
      for (float o : array) {
         System.out.print(o + " ");
      }
      System.out.println("");
   }
   
   public static void Print(Object[] array) {
      for (Object o : array) {
         System.out.print(o + " ");
      }
   }
   
   public static void Print(Object[][] array) {
      for (Object[] list : array) {
         Print(list);
         System.out.println("");
      }
   }
   
   public static void PrintMatrix(YCCBlock[][] array) {
      for (YCCBlock[] list : array) {
         for (YCCBlock o : list)
            System.out.print(o.toString() + " ");
         System.out.println("");
      }
   }
   
   public static void PrintTree(HuffmanTree[][][] array) {
      for (HuffmanTree[][] l : array) {
         for (HuffmanTree[] trees : l) {
            for (HuffmanTree tree : trees) {
               System.out.println(tree.getEncodedString());
               System.out.println(tree.getDecodedString());
            }
         }
      }
   }
   
   public static void main(String[] args) {
      /*CompressedTexture t = new CompressedTexture(v);
      Print(v);
      Print(t.decompress());*/
   }
}