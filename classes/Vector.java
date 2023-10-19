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
   *makes a Vector out of a pair of points
   *@param p1 is the initial point, must be the same length as p2
   *@param p2 is the terminal point, must be the same length as p1 
   */
   public Vector(Point p1, Point p2){
      coordinates = new float[p1.length()];
      for(int i = 0; i<p1.length(); i++){
         coordinates[i] = p2.getCoords()[i]-p1.getCoords()[i];
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
   *gets values of vector
   *@return float[] of values of vector
   */
   public float[] getCoordinates() {
      return coordinates;
   }
   /**
   *gets values of vector (alternate spelling)
   *@return float[] of values of vector
   */
   public float[] getCoords() {
      return getCoordinates();
   }
   /**
   *adds this vector and v
   *@param v has to have same length as this vector
   *@return vector with added values
   */
   public Vector add(Vector v){
      float[] nC = new float[coordinates.length]; 
      for(int i = 0; i<nC.length;i++){
         nC[i] = coordinates[i] + v.getCoords()[i];
      }
      return new Vector(nC);
   }
   /**
   *subtracts v from this vector
   *@param v has to have same length as this vector
   *@return vector with subtracted values values
   */
   public Vector subtract(Vector v){
      float[] nC = new float[coordinates.length]; 
      for(int i = 0; i<nC.length;i++){
         nC[i] = coordinates[i] - v.getCoords()[i];
      }
      return new Vector(nC);
   }
   /**
   *dots v and this vector
   *@param v has to have same length as this vector
   *@return float as dot product of v and this vector
   */
   public float dot(Vector v){
      float nC = 0; 
      for(int i = 0; i<coordinates.length;i++){
         nC += coordinates[i] * v.getCoords()[i];
      }
      return nC;
   }
   /**
   *dots v and this vector
   *@param v must contain vectors of length of length of v -1 and all vectors must not be coplaner or paralell with eachother
   *@return a orthogonal vector
   */
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
      //find missing variable
      for(int i = 0; i<d.length; i++){
         if(d[i].getCoords()[i] == 0){
            var[0] = i;
            break;
         }
      }
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
      result[var[0]] = value[0];
      return new Vector(result);
   }
   /**
   *scales this vector by s
   *@param s must be a float
   *@return this vector scaled
   */
   public Vector scale(float s){
      float[] nC = new float[coordinates.length]; 
      for(int i = 0; i<nC.length;i++){
         nC[i] = coordinates[i]*s;
      }
      return new Vector(nC);
   }
   /** rotates vector
   * @param rot must be a rotation Matrix
   * @return rotated vector
   */
   public Vector rotBy(Matrix rot){
      Vector[] temp = new Vector[1];
      temp[0] = this;
      Matrix v = new Matrix(temp);
      v = v.transpose();
      Matrix postRot = rot.mult(v).transpose();
      Vector[] result = postRot.toVectors();
      return result[0];
   }
/** Generic toString() method.
* @return String describing this Object.
*/
   public String toString(){
      String ans = "Vector (float[] coordinates): [{";
      if (coordinates != null && coordinates.length > 0) {
         for(int i = 0; i<coordinates.length - 1; i++){
            ans += coordinates[i] + ", ";
         }
         ans += coordinates[coordinates.length - 1] + "}]";
      }
      return ans;
   }
   
/** Returns the mag of this Vector.
* @return float of the mag of this Vector.
*/
   public float mag(){
      float sum = 0;
      for(int i = 0; i<coordinates.length; i++){
         sum += coordinates[i]*coordinates[i];
      }
      return (float)Math.sqrt(sum);
   }
   public Vector unitVector(){
      float[] nd = new float[coordinates.length];
      float div = mag();
      for(int i = 0; i<nd.length; i++){
         nd[i] = coordinates[i]/div;
      }
      return new Vector(nd);
   }
   public Vector clone(){
      float[] nd = new float[coordinates.length];
      for(int i = 0; i<nd.length; i++){
         nd[i] = coordinates[i];
      }
      return new Vector(nd);
   }
   public boolean equals(Vector v){
      for(int i = 0; i<coordinates.length; i++){
         if(v.getCoords()[i] != coordinates[i]){
            return false;
         }
      }
      return true;
   }
}