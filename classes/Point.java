public class Point {

   private float[] coordinates;
   
   public Point(float[] coords) {
      coordinates = coords;
   }
   
   public int length() {
      return coordinates.length;
   }

   public float getDistance(Point point) {
      return getDistance(this, point);
   }
   
   public float[] getCoordinates() {
      return coordinates;
   }
   
   public float[] getCoords() {
      return getCoordinates();
   }

   
   public static float getDistance(Point one, Point two) {
      float sum = 0;
      for (int i = 0; i < one.coordinates.length && i < two.coordinates.length; i++) {
         sum += Math.abs(one.coordinates[i] - two.coordinates[i]);
      }
      return (float)Math.sqrt((double)sum);
   }
}