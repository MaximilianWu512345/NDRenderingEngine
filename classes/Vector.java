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
   public float[] getCoordinates() {
      return coordinates;
   }
   
   public float[] getCoords() {
      return getCoordinates();
   }
   public Vector add(Vector v){
      float[] nC = new float[coordinates.length]; 
      for(int i = 0; i<nC.length;i++){
         nC[i] = coordinates[i] + v.getCoords()[i];
      }
      return new Vector(nC);
   }
   public Vector subtract(Vector v){
      float[] nC = new float[coordinates.length]; 
      for(int i = 0; i<nC.length;i++){
         nC[i] = coordinates[i] - v.getCoords()[i];
      }
      return new Vector(nC);
   }
   public Vector dot(Vector v){
      float[] nC = new float[coordinates.length]; 
      for(int i = 0; i<nC.length;i++){
         nC[i] = coordinates[i] * v.getCoords()[i];
      }
      return new Vector(nC);
   }
   public Vector getOrthogonal(Vector[] v){
      //Gram-Schmidt Orthogonalization? 
      return null;
   }
   public Vector project(Vector v){
      return null;
   }
   public float component(Vector v){
      return 0;
   }
}