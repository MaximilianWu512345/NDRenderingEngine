/** Mesh class */
import java.util.LinkedList;
import java.util.HashSet;
public class Mesh{
/** Direction of this Camera */
   public Simplex[] faces;
/** Direction of this Camera */
   int dimension = 0;
   /** Generates a Mesh with elements of f and a dimension of d.
   * @param f the Simplex[] faces of the mesh.
   * @param d the dimension of the mesh.
   */
   public Mesh(Simplex[] f, int d){
      faces = f;
      dimension = d;
   }
   /** Returns the int dimension of this Mesh.
   * @return the dimension of this Mesh.
   */
   public float getDimention(){
      return dimension;
   }
   /** Returns the Simplex[] faces of this Mesh.
   * @return f the faces of this Mesh.
   */
   public Simplex[] getFaces() {
      return faces;
   }
   public Point[] getAllPoints(){   
      HashSet<Point> set = new HashSet<Point>();
      for (Simplex s : faces) {
         Point[] points = s.getPoints();
         for (int i = 0; i < points.length; i++) {
            set.add(points[i]);
         }
      }
      Point[] result = new Point[set.size()];
      int index = 0;
      for(Point p: set){
         result[index] = p;
         index++;
      }
      return result;
   }
   // Matrix m must be multipliable with each Simplex in faces, width must equal m.height
   public void transform(Matrix m) {
      Point[] points = getAllPoints();
      for (int i = 0; i < points.length; i++) {
         Matrix temp = m.mult(points[i].toMatrix());
         if (temp != null)
            points[i].setCoords(temp.toPoint().getCoords());
         else
            System.out.println("Matrix Multiplication Failed: Mesh.transform(Matrix m): temp == null");
      }
   }
   
/** Generic toString() method.
* @return String describing this Object.
*/
   public String toString() {
      String temp = "Mesh (int dimension, Simplex[] faces): [\n\t" + dimension + "\n";
      if (faces != null && faces.length > 0) {
         for (int i = 0; i < faces.length - 1; i++) {
            temp += faces[i].toString(1) + "\n";
         }
         temp += faces[faces.length - 1].toString(1) + "\n";
      }
      return temp + "]";
   }
   
   //conves hull generator
   public Mesh(Point[] p){
      setConvexHull(toVectors(p));
   }
   public Mesh(Vector[] v){
      setConvexHull(v);
   }
   protected Vector[] toVectors(Point[] p){
      Vector[] v = new Vector[p.length];
      for(int i = 0; i<p.length; i++){
         v[i] = new Vector(p[i].getCoords());
      }
      return v;
   }
   protected void setConvexHull(Vector[] v){
      if(v.length == 0){
         return;
      }
      int d = v[0].length();
      if(v.length == 1){
         faces = new Simplex[]{new Simplex(new Point[]{new Point(v[0].getCoords())})};
         return;
      }
      faces = quickHull(v, d);   
   }
   protected Simplex[] quickHull(Vector[] v, int d){
      LinkedList<Simplex> hull = new LinkedList<Simplex>();
      //get inital dividing simplex
      
      Simplex[] result = new Simplex[hull.size()];
      result = hull.toArray(result);
      return result;
   }
   public void translate(Point p){
   Point[] points = getAllPoints();
      for(Point sp: points){
         sp.translate(p.getCoords());
      }
   }
}