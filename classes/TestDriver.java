
public class TestDriver {

   public static void main(String arg[]) {
         Point point = new Point(new float[] {10, 20});
      Point pointTwo = new Point(new float[] {20, 10});
      System.out.println(Point.getDistance(point, pointTwo));
   }
}