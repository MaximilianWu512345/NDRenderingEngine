/** Mesh class */
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
}