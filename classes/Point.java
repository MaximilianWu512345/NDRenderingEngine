/** Point class */
public class Point {

/** Coordinates of this Point. */
   private float[] coordinates;
   
/** Creates a new Point with coordinates of coords.
* @param p The coordinates of the Point.
*/
   public Point(float[] coords) {
      coordinates = coords;
   }
   
/** Returns an int length of this Point's coordinates.
* @return the length of this Point's coordinates.
*/
   public int length() {
      return coordinates.length;
   }
   
   public Matrix toMatrix() {
      float[][] array = new float[coordinates.length][];
      for (int i = 0; i < coordinates.length; i++) {
         array[i] = new float[] { coordinates[i] };
      }
      return new Matrix(array);
   }
   
/** Returns a float of the distance between this Point and point.
* @param point the point to check.
* @return the distance between this Point and point.
*/
   public float getDistance(Point point) {
      return getDistance(this, point);
   }

/** Returns a float of the distance between this Point and point.
* @param point the point to check.
* @return the distance between this Point and point.
*/
   public static float getDistance(Point one, Point two) {
      float sum = 0;
      for (int i = 0; i < one.coordinates.length && i < two.coordinates.length; i++) {
         sum += Math.abs(one.coordinates[i] - two.coordinates[i]);
      }
      return (float)Math.sqrt((double)sum);
   }
   
/** Returns a float[] of this Point's coordinates.
* @return this Point's coordinates.
*/
   public float[] getCoordinates() {
      return coordinates;
   }
   
/** Shortened version of getCoordinates().
* @return getCoordinates().
*/
   public float[] getCoords() {
      return getCoordinates();
   }
   
   // V1 * A = V2
   public Matrix rotate(Vector one, Vector two) {
      return null;
   }
   
/** Rotates this point's coordinates according to the degrees given. Optional java.awt.Point parameter to rotate around. Otherwise, default is the origin.
* @param rotation the amount of degrees to rotate.
* @param point the point to rotate around. Optional.
*/
   // around the origin if point == null
   // 2d rotation algorithim: https://academo.org/demos/rotation-about-point/#:~:text=If%20you%20wanted%20to%20rotate%20that%20point%20around,sin%20%CE%B8%20sin%20%CE%B8%20cos%20%CE%B8%29%20%28x%20y%29
   // 3d rotation algorithm: https://stackoverflow.com/questions/8602408/3d-rotation-around-the-origin
   public void rotate(int degrees, java.awt.Point point) {
      // 2d rotation
      float x;
      float y;
      if (point == null) {
         x = coordinates[1] - Engine.Instance.getWidth() / 2;
         y = coordinates.length > 2 ? coordinates[2] - Engine.Instance.getHeight() / 2 : 0;
      }
      else {
         x = coordinates[1] - (float)point.getX();
         y = coordinates.length > 2 ? coordinates[2] - (float)point.getY() : 0;
      }
      coordinates[1] = x * (float)Math.cos(degrees) - y * (float)Math.sin(degrees);
      if (coordinates.length > 2)
            coordinates[2] = y * (float)Math.cos(degrees) + x * (float)Math.sin(degrees);
   }
   
   public void translate(float[] coords) {
      for (int i = 0; i < coordinates.length && i < coords.length; i++) {
         coordinates[i] += coords[i];
      }
   }
   
/** Generic toString() method.
* @return String describing this Object.
*/
   public String toString() {
      String temp = "Point (int length, float[] coordinates): [" + (coordinates == null ? "" + 0 : coordinates.length) + ", {";
      if (coordinates != null && coordinates.length > 0) {
         for (int i = 0; i < coordinates.length - 1; i++) {
            temp += coordinates[i] + ", ";
         }
         temp += coordinates[coordinates.length - 1];
      }
      return temp + "}]";
   }
}