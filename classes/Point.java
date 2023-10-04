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
   
   // around the origin
   // 2d rotation algorithim: https://academo.org/demos/rotation-about-point/#:~:text=If%20you%20wanted%20to%20rotate%20that%20point%20around,sin%20%CE%B8%20sin%20%CE%B8%20cos%20%CE%B8%29%20%28x%20y%29
   // 3d rotation algorithm: https://stackoverflow.com/questions/8602408/3d-rotation-around-the-origin
   public void rotate(int degrees) {
      // 2d rotation
      float x = coordinates[0] - Engine.WIDTH / 2;
      float y = coordinates.length > 1 ? coordinates[1] - Engine.HEIGHT / 2 : 0;
      coordinates[0] = x * (float)Math.cos(degrees) - y * (float)Math.sin(degrees);
      if (coordinates.length > 1)
         coordinates[1] = y * (float)Math.cos(degrees) + x * (float)Math.sin(degrees);
   }
   
   public void translate(float[] coords) {
      for (int i = 0; i < coordinates.length && i < coords.length; i++) {
         coordinates[i] += coords[i];
      }
   }
   
   public static float getDistance(Point one, Point two) {
      float sum = 0;
      for (int i = 0; i < one.coordinates.length && i < two.coordinates.length; i++) {
         sum += Math.abs(one.coordinates[i] - two.coordinates[i]);
      }
      return (float)Math.sqrt((double)sum);
   }
   
   public String toString() {
      String toString = "Point (Length, float[]): ( " + (coordinates == null ? "" + 0 : coordinates.length) + ", float[]: { ";
      if (coordinates != null && coordinates.length > 0) {
         for (int i = 0; i < coordinates.length - 1; i++) {
            toString += coordinates[i] + ", ";
         }
         toString += coordinates[coordinates.length - 1];
      }
      return toString + " } )";
   }
}