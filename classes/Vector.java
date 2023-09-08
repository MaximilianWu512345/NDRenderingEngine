public class Vector {

   private float[] coordinates;
   
   public Vector(float[] coords) {
      coordinates = coords;
   }
   
   public int length() {
      return coordinates.length;
   }

   public float getDistance(Vector vector) {
      return getDistance(this, vector);
   }
   
   public static float getDistance(Vector one, Vector two) {
      float sum = 0;
      for (int i = 0; i < one.coordinates.length && i < two.coordinates.length; i++) {
         sum += Math.abs(one.coordinates[i] - two.coordinates[i]);
      }
      return (float)Math.sqrt((double)sum);
   }
}