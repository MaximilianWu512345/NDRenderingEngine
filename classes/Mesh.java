public class Mesh{
   public Simplex[] faces;
   int dimension = 0;
   public Mesh(Simplex[] f, int d){
      faces = f;
      dimension = d;
   }
   public float getDimention(){
      return dimension;
   }
   
   public String toString() {
      String temp = "Mesh (int dimension, Simplex[] faces): { " + dimension + "\n\t";
      for (Simplex s : faces)
         temp += s + "\n\t";
      return temp + "}";
   }
}