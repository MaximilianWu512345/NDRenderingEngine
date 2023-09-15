
public class TestDriver {

   public static void main(String arg[]) {
         Point point = new Point(new float[] {10, 20});
      Point pointTwo = new Point(new float[] {20, 10});
      System.out.println(Point.getDistance(point, pointTwo));
      
      float[][] d = {{1,5,1}, {2,11,5}};
      Matrix m = new Matrix(d);
      System.out.println(m);
      System.out.println(m.getRREF());
      System.out.println("testing orthogonal stuff");
      Vector[] v = new Vector[3];
      float[] f = new float[1];
      f = new float[]{3,2,5,-1};
      v[0] = new Vector(f);
      f = new float[]{1,0,-7,0};
      v[1] = new Vector(f);
      f = new float[]{-2,3,2,6};
      v[2] = new Vector(f);
      System.out.println(Vector.getOrthogonal(v));
   }
}