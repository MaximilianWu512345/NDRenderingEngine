
public class ShapeLibrary {
   
   public ShapeLibrary() {
   
   }
   
   // https://en.wikipedia.org/wiki/Polygon_triangulation
   // https://stackoverflow.com/questions/71256623/algorithm-to-dynamically-generate-m-face-list-for-n-dimensional-hypercube
   public static Mesh GenerateHypercube(int dimension, float size) {
      Simplex[] faces = new Simplex[dimension * 2];
      for (int i = 0; i < faces.length; i++) {
         faces[i] = GenerateSimplex(dimension - 1, size);
      }
      return new Mesh(faces, dimension);
   }
   
   public static Point[] GenerateAllPoints(int dimension, float size) {
      Point[] temp = new Point[(int)Math.pow(2, dimension)];
      for (int i = 0; i < temp.length; i++) {
         float[] data = new float[dimension];
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
      System.out.println(GenerateHypersphere(2, 5, 100));
   }
}