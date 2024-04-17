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
   public void rotate(int degrees, java.awt.Point point) {
   }
   
   public void translate(Vector other) {
      this.translate(other.getCoordinates());
   }
   
   public void translate(Point other) {
      this.translate(other.getCoordinates());
   }
   
   public void translate(float[] coords) {
      for (int i = 0; i < coordinates.length && i < coords.length; i++) {
         coordinates[i] += coords[i];
      }
   }
   
   public Point getInverse() {
      float[] coords = new float[coordinates.length];
      for (int i = 0; i < coords.length; i++) {
       coords[i] = -coordinates[i];
      }
      return new Point(coords);
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