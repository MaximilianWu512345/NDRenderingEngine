/**
*repersents a vector of any dimention
*/
public class Vector {
   /**
   *value of vector
   */
   private float[] coordinates;
   /**
   *makes a vector out of a float[]
   *@param coords has a length of atleast 1
   */
   public Vector(float[] coords) {
      coordinates = coords;
   }
   /**
   *makes a vector out of a int[]
   *@param coords has a length of atleast 1
   */
   public Vector(int[] coords) {
      coordinates = new float[coords.length];
      for(int i = 0; i<coords.length; i++){
         coordinates[i] = coords[i];
      }
   }
   /**
   *returns the dimention of the vector
   *@return dimention of the vector
   */
   public int length() {
      return coordinates.length;
   }
   /**
   *gets distance between this and vector
   *@param vector has the same dimention as this
   *@return distance as a float
   */
   public float getDistance(Vector vector) {
      return getDistance(this, vector);
   }
   /**
   *gets distance between two vectors
   *@param one has the same dimention as two
   *@param two has the same dimention as one
   *@return distance as a float
   */
   public static float getDistance(Vector one, Vector two) {
      float sum = 0;
      for (int i = 0; i < one.coordinates.length && i < two.coordinates.length; i++) {
         sum += Math.abs(one.coordinates[i] - two.coordinates[i]);
      }
      return (float)Math.sqrt((double)sum);
   }
   /**
   *
   */
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
   public float dot(Vector v){
      float nC = 0; 
      for(int i = 0; i<coordinates.length;i++){
         nC += coordinates[i] * v.getCoords()[i];
      }
      return nC;
   }
   public static Vector getOrthogonal(Vector[] v){
      //just gonna assume unkowns are 1 so things stay simple
      Vector zeroVector = new Vector(new float[v[0].length()]);
      //add zero vector
      Vector[] vp = new Vector[v.length+1];
      for(int i = 0; i<v.length; i++){
         vp[i] = v[i];
      }
      vp[vp.length-1] = zeroVector;
      //get Augmented Matrix
      Matrix m = new Matrix(vp);
      Matrix aug = m.AugmentedMatrix(zeroVector);
      //System.out.println("aug");
      //System.out.println(aug);
      //get rref
      Matrix rref = aug.getRREF();
      //System.out.println("rref");
      //System.out.println(rref);
      //evaluate
      Vector[] d = rref.toVectors();
      float[] result = new float[v[0].length()];
      int[] var = new int[1];
      var[0] = (d.length-1);
      float[] value = new float[1];
      value[0] = 1; 
      for(int i = d.length-2; i>=0; i--){
         LinearEquation le = new LinearEquation(d[i].getCoords());
         /*
         System.out.println("Equation " + (i + 1));
         System.out.println(new Vector(value));
         System.out.println(new Vector(var));
         System.out.println(le);
         System.out.println(le.Evaluate(var, value)[0]);
         */
         float[] leRes = le.Evaluate(var, value);
         //add new vector values
         result[(int)leRes[1]] = leRes[0];
         int[] temp1 = new int[var.length+1];
         for(int j = 0; j<var.length; j++){
            temp1[j] = var[j];
         }
         temp1[temp1.length-1] = (int)leRes[1];
         var = temp1;
         float[] temp2 = new float[value.length+1];
         for(int j = 0; j<value.length; j++){
            temp2[j] = value[j];
         }
         temp2[temp2.length-1] = leRes[0];
         value = temp2;
      }
      result[result.length-1] = 1;
      return new Vector(result);
   }
   public Vector project(Vector v){
      return null;
   }
   public float component(Vector v){
      return 0;
   }
   public Vector scale(float s){
      float[] nC = new float[coordinates.length]; 
      for(int i = 0; i<nC.length;i++){
         nC[i] = coordinates[i]*s;
      }
      return new Vector(nC);
   }
   public String toString(){
      String ans = "[";
      for(int i = 0; i<coordinates.length; i++){
         ans += coordinates[i] + " ";
      }
      ans += "]";
      return ans;
   }
}