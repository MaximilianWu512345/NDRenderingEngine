
public class ShapeLibrary {
   
   // https://en.wikipedia.org/wiki/Polygon_triangulation
   // https://stackoverflow.com/questions/71256623/algorithm-to-dynamically-generate-m-face-list-for-n-dimensional-hypercube
   public static Mesh GenerateHypercube(int dimension, float size) {
      /*
      Simplex[] faces = new Simplex[dimension * 2];
      for (int i = 0; i < faces.length; i++) {
         faces[i] = GenerateSimplex(dimension - 1, size);
      }*/
      Simplex[] faces = new Simplex[1];
      faces[0] = GenerateSimplex(dimension, size);
      return new Mesh(faces, dimension);
   }
   public static Mesh Generate4DTesseract(float size){
      Point[] points = GenerateAllPoints(4, size);
      Simplex[] sim = new Simplex[40];
      int index = 0;
      //f1 face
      sim[index] = new Simplex(new Point[]{points[0], points[1], points[2], points[4]});
      index++;
      sim[index] = new Simplex(new Point[]{points[1], points[2], points[4], points[7]});
      index++;
      sim[index] = new Simplex(new Point[]{points[3], points[1], points[2], points[7]});
      index++;
      sim[index] = new Simplex(new Point[]{points[5], points[1], points[4], points[7]});
      index++;
      sim[index] = new Simplex(new Point[]{points[6], points[7], points[2], points[3]});
      index++;
      //b1 face
      sim[index] = new Simplex(new Point[]{points[8], points[9], points[10], points[12]});
      index++;
      sim[index] = new Simplex(new Point[]{points[9], points[10], points[12], points[15]});
      index++;
      sim[index] = new Simplex(new Point[]{points[11], points[9], points[10], points[15]});
      index++;
      sim[index] = new Simplex(new Point[]{points[13], points[9], points[12], points[15]});
      index++;
      sim[index] = new Simplex(new Point[]{points[14], points[15], points[10], points[11]});
      index++;
      //f2 face
      sim[index] = new Simplex(new Point[]{points[0], points[1], points[2], points[8]});
      index++;
      sim[index] = new Simplex(new Point[]{points[1], points[2], points[8], points[11]});
      index++;
      sim[index] = new Simplex(new Point[]{points[3], points[1], points[2], points[11]});
      index++;
      sim[index] = new Simplex(new Point[]{points[9], points[1], points[8], points[11]});
      index++;
      sim[index] = new Simplex(new Point[]{points[10], points[11], points[2], points[3]});
      index++;
      //b2 face
      sim[index] = new Simplex(new Point[]{points[4], points[5], points[6], points[12]});
      index++;
      sim[index] = new Simplex(new Point[]{points[5], points[6], points[12], points[15]});
      index++;
      sim[index] = new Simplex(new Point[]{points[7], points[5], points[6], points[15]});
      index++;
      sim[index] = new Simplex(new Point[]{points[13], points[5], points[12], points[15]});
      index++;
      sim[index] = new Simplex(new Point[]{points[14], points[15], points[6], points[7]});
      index++;
      //f3 face
      sim[index] = new Simplex(new Point[]{points[0], points[1], points[4], points[8]});
      index++;
      sim[index] = new Simplex(new Point[]{points[1], points[4], points[8], points[13]});
      index++;
      sim[index] = new Simplex(new Point[]{points[5], points[1], points[4], points[13]});
      index++;
      sim[index] = new Simplex(new Point[]{points[9], points[1], points[8], points[13]});
      index++;
      sim[index] = new Simplex(new Point[]{points[12], points[13], points[4], points[5]});
      index++;
      //b3 face
      sim[index] = new Simplex(new Point[]{points[2], points[3], points[6], points[10]});
      index++;
      sim[index] = new Simplex(new Point[]{points[3], points[6], points[10], points[15]});
      index++;
      sim[index] = new Simplex(new Point[]{points[7], points[3], points[6], points[15]});
      index++;
      sim[index] = new Simplex(new Point[]{points[11], points[3], points[10], points[15]});
      index++;
      sim[index] = new Simplex(new Point[]{points[6], points[15], points[6], points[7]});
      index++;
      //f4 face
      sim[index] = new Simplex(new Point[]{points[0], points[2], points[4], points[8]});
      index++;
      sim[index] = new Simplex(new Point[]{points[2], points[4], points[8], points[14]});
      index++;
      sim[index] = new Simplex(new Point[]{points[6], points[2], points[4], points[14]});
      index++;
      sim[index] = new Simplex(new Point[]{points[10], points[2], points[8], points[14]});
      index++;
      sim[index] = new Simplex(new Point[]{points[12], points[14], points[4], points[6]});
      index++;
      //b4 face
      sim[index] = new Simplex(new Point[]{points[1], points[3], points[5], points[9]});
      index++;
      sim[index] = new Simplex(new Point[]{points[3], points[5], points[9], points[15]});
      index++;
      sim[index] = new Simplex(new Point[]{points[7], points[3], points[5], points[15]});
      index++;
      sim[index] = new Simplex(new Point[]{points[11], points[3], points[9], points[15]});
      index++;
      sim[index] = new Simplex(new Point[]{points[13], points[15], points[5], points[7]});
      index++;
      return new Mesh(sim, 4);
   }
   public static Point[] GenerateAllPoints(int dimension, float size) {
      Point[] temp = new Point[(int)Math.pow(2, dimension)];
      for (int i = 0; i < temp.length; i++) {
         float[] data = new float[dimension == 0 ? 1 : dimension];
         int amount = i;
         if (amount != 0) {
            for (int x = 0; x < data.length; x++) {
               int sub = (int)Math.pow(2, data.length - 1 - x);
               if (amount >= sub) {
                  amount -= sub;
                  data[x] = size;
               }
            }
         }
         temp[i] = new Point(data);
      }
      return temp;
   }
   
   public static Simplex GenerateSimplex(int dimension, float size) {
      return new Simplex(GenerateAllPoints(dimension, size));
   }
   
   // https://mathworld.wolfram.com/HyperspherePointPicking.html
   //(x1)^2 + (x2)^2 + (x3)^2 + ... + (xn)^2 = r^2
   public static Mesh GenerateHypersphere(int dimension, float radius, int numPoints) {
      Point[] points = new Point[numPoints];
      for (int index = 0; index < points.length; index++) {
         float[] randVar = new float[dimension];
         for (int i = 0; i < randVar.length; i++) {
            randVar[i] = GenerateGaussianRandom();
         }
         points[index] = new Point(new Vector(randVar).unitVector().scale(radius).getCoordinates());
      }
      Simplex[] faces = new Simplex[1];
      faces[0] = new Simplex(points);
      return new Mesh(faces, dimension);
   }
   
   // https://stackoverflow.com/questions/218060/random-gaussian-variables
   public static float GenerateGaussianRandom() {
      double u1 = 1.0 - Math.random();
      double u2 = 1.0 - Math.random();
      float randStdNormal = (float)(Math.sqrt(-2.0 * Math.log(u1)) * Math.sin(2.0 * Math.PI * u2)); //random normal(0,1)
      return randStdNormal;
   }
   
   public static float[] copy(float[] f) {
      float[] temp = new float[f.length];
      for (int i = 0; i < f.length; i++) {
         temp[i] = f[i];
      }
      return temp;
   }
   
   public static void main(String[] args) {
      System.out.println(GenerateHypercube(2, 2));
   }
}