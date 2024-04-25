/** Mesh class */
import java.util.LinkedList;
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
   
   public float getDimension() {
      return dimension;
   }
   /** Returns the Simplex[] faces of this Mesh.
   * @return f the faces of this Mesh.
   */
   public Simplex[] getFaces() {
      return faces;
   }
   
   // Matrix m must be multipliable with each Simplex in faces, width must equal m.height
   public void transform(Matrix m) {
      for (Simplex s : faces) {
         Point[] points = s.getPoints();
         for (int i = 0; i < points.length; i++) {
            Matrix temp = m.mult(points[i].toMatrix());
            if (temp != null)
               points[i] = temp.toPoint();
            else
               System.out.println("Matrix Multiplication Failed: Mesh.transform(Matrix m): temp == null");
         }
         s.setPoints(points);
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
}