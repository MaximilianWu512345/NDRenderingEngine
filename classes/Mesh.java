public class Mesh{
   public Simplex[] faces;
   int dimention = 0;
   public Mesh(Simplex[] f, int d){
      faces = f;
      dimention = d;
   }
   public float getDimention(){
      return dimention;
   }
}