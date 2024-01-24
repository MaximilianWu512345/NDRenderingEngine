import java.util.ArrayList;
import java.math.*;
/**
* a matrix from linear algebra
*/
public class Matrix{
   /**
   *elements of the matrix
   */
   private float[][] data;
   /**
   *width of the matrix
   */
   private int width;
   /**
   *height of the matrix
   */
   private int height;
   /**
   * generates a matrix with elements of d
   * @param d must be a rectangular 2d array
   */
   public Matrix(float[][] d){
      data = d;
      height = d.length;
      width = d[0].length;
   }
   /**
   *gets the hight of this matrix
   *@return the ight of this matrix as an int
   */
   public int getHeight(){
      return height;
   }
   
   /**
   *gets the width of this matrix
   *@return the ight of this matrix as an int
   */
   public int getWidth(){
      return width;
   }
   /**
   * generates a matrix with elements of d
   * @param d must be a rectangular 2d array
   */
   public Matrix(ArrayList<ArrayList<Float>> d){
      data = new float[d.size()][d.get(0).size()];
      for (int i = 0; i < d.size(); i++) {
         for (int x = 0; x < d.get(i).size(); x++) {
            data[i][x] = d.get(i).get(x);
         }
      }
      height = d.size();
      width = d.get(0).size();
   }
   /**
   * generates a matrix out of vectors
   * @param d must contain vectors of the same length
   */
   public Matrix(Vector[] d){
      data = new float[d.length][d.length];
      for(int i = 0; i<d.length; i++){
         data[i] = d[i].getCoords();
      }
      height = d.length;
      width = d[0].length();
   }
   /**
   * returns data within matrix
   * @return rectangular float[][]
   */
   public float[][] getData(){
      return data;
   }
   /**
   * Multiplys this matrix with m
   * @param m must be multipliable with this matrix
   * @return a Matrix or null when multiplication fails
   */
   public Matrix mult(Matrix m){
      int nWidth = m.width;
      int nHeight = height;
      if(m.height == width){
         float[][] newData = new float[m.width][height];
         for(int i = 0; i<height; i++){
            for(int j = 0; j<m.width; j++){
               float sum = 0;
               for(int k = 0; k<width; k++){
                  // check if this line is correct later
                  sum += m.data[k][i] * data[j][k];
               }
               newData[j][i] = sum;
            }
         }
         return new Matrix(newData);
      }
      return null;
   }
   /**
   * Divides this matrix by m
   * @param m must be dividable with this matrix
   * @return a Matrix
   */
   public Matrix div(float m){
      float[][] newData = new float[width][height];
      for(int i = 0; i<height; i++){
         for(int j = 0; j<width; j++){
            newData[i][j] = data[i][j]/m;
         }
      }
      return new Matrix(newData);
   }
   /**
   * genrates a array of vectors with a vector for each row of the matrix
   * @return a vector[]
   */
   public Vector[] toVectors(){
      Vector[] result = new Vector[height];
      for(int i = 0; i<height; i++){
         result[i] = new Vector(data[i]);
      }
      return result;
   }
   /**
   * generates the augmented matrix
   * @param v must have the same length as the height of the matrix
   * @return a Matrix
   */
   public Matrix AugmentedMatrix(Vector v){
      Vector[] oldv = toVectors();
      Vector[] newv = new Vector[oldv.length];
      for(int i = 0; i<newv.length; i++){
         float[] coord = new float[oldv[i].length()+1];
         for(int j = 0; j<coord.length-1; j++){
            coord[j] = oldv[i].getCoords()[j];
         }
         coord[coord.length-1] = v.getCoords()[i];
         newv[i] = new Vector(coord);
      }
      return new Matrix(newv);
   }
   /**
   * gets reduced row echelon form
   * @return a Matrix
   */
   public Matrix getRREF(){
      //algorithem from https://www.codewithc.com/gauss-elimination-method-algorithm-flowchart/
      Vector[] d = this.toVectors();
      for(int i = 0; i<d.length-1; i++){
         if(d[i].getCoords()[i] == 0){
            //pivot
            for(int j = i+1; j<d.length; j++){
               if(d[j].getCoords()[i] != 0){
                  Vector temp = d[i];
                  d[i] = d[j];
                  d[j] = temp;
                  break;
               }
            }
         }
         //Elimination
         for(int j = i+1; j<d.length; j++){
            if(d[i].getCoords()[i] != 0){
               float r = d[j].getCoords()[i]/d[i].getCoords()[i];
               d[j] = d[j].subtract(d[i].scale(r));
            }
         }
      }
      //fix
      return new Matrix(d);
   }
   private int getRREFPivotCount(){
      //algorithem from https://www.codewithc.com/gauss-elimination-method-algorithm-flowchart/
      int count = 0;
      Vector[] d = this.toVectors();
      for(int i = 0; i<d.length-1; i++){
         if(d[i].getCoords()[i] == 0){
            //pivot
            
            for(int j = i+1; j<d.length; j++){
               if(d[j].getCoords()[i] != 0){
                  Vector temp = d[i];
                  d[i] = d[j];
                  d[j] = temp;
                  count++;
                  break;
               }
            }
         }
      }
      //fix
      return count;
   }
   
   public Matrix invert() {
      return Matrix.invert(this);      
   }
   
   public static Matrix invert(Matrix m) {
      return Matrix.invert(m.data);
   }
   
   /** Returns an inverted form of this Matrix.
   * https://byjus.com/maths/inverse-matrix/
   * https://www.mathsisfun.com/algebra/matrix-determinant.html
   * @return inverted matrix.
   */
   public static Matrix invert(float[][] data) {
      // convert to cofactor matrix first, transpose the cofactor matrix, then divide it by the determinent of data.
      System.out.println(Matrix.getCoFactor(data));
      return new Matrix(Matrix.getTranspose(Matrix.getCoFactor(data))).div(Matrix.getDeterminant(data));
   }
   
   public float[][] getMinorsMatrix(int skipRow, int skipCol) {
      return Matrix.getMinorsMatrix(this, skipRow, skipCol);
   }
   
   public static float[][] getMinorsMatrix(Matrix m, int skipRow, int skipCol) {
      return Matrix.getMinorsMatrix(m.data, skipRow, skipCol);
   }
   
   public static float[][] getMinorsMatrix(float[][] m, int skipRow, int skipCol) {
      int indexRow = 0;
      int indexCol = 0;
      float[][] temp = new float[m.length - 1][m[0].length - 1];
      for (int i = 0; i < m.length; i++) {
         for (int x = 0; x < m[i].length; x++) {
            if (i == skipRow || x == skipCol)
               continue;
            temp[indexRow][indexCol++] = m[i][x];
            indexRow += indexCol / (m[i].length - 1);
            indexCol %= (m[i].length - 1);
         }
      }
      return temp;
   }
   
   public float getDeterminant() {
      return Matrix.getDeterminant(this);
   }
   
   public static float getDeterminant(Matrix m) {
      return Matrix.getDeterminant(m.data);
   }
   
   // Gaussian method.
   public static float getDeterminant(float[][] m) {
      int sign = (new Matrix(m)).getRREFPivotCount();
      float[][] RREF = new Matrix(m).getRREF().data;
      int sum = 1;
      for (int i = 0; i < RREF.length && i < RREF[0].length; i++) {
         sum *= RREF[i][i];
      }
      return sum * ((sign) % 2 == 0 ? 1 : -1);
   }
   
   public float[][] getCoFactor() {
      return Matrix.getCoFactor(this);
   }
   
   public static float[][] getCoFactor(Matrix m) {
      return Matrix.getCoFactor(m.data);
   }
   
   public static float[][] getCoFactor(float[][] m) {
      float[][] temp = new float[m.length][m[0].length];
      for (int i = 0; i < m.length; i++) {
         for (int x = 0; x < m[i].length; x++) {
            float det = getDeterminant(getMinorsMatrix(m, i, x)) * ((i + x) % 2 == 0 ? 1 : -1);
            if (det != 0) {
               temp[i][x] = det;
            }
         }
      }
      return temp;
   }
   
   public float[][] getTranspose() {
      return Matrix.getTranspose(this);
   }
   
   public static float[][] getTranspose(Matrix m) {
      return Matrix.getTranspose(m.data);
   }
   
   public static float[][] getTranspose(float[][] m) {
      float[][] temp = new float[m[0].length][m.length];
      for (int i = 0; i < m.length; i++) {
         for (int x = 0; x < m[i].length; x++) {
            temp[x][i] = m[i][x];
         }
      }
      return temp;
   }
   
/** Generic toString() method.
* @return String describing this Object.
*/
   public String toString(){
   //https://stackoverflow.com/questions/41340566/java-include-decimal-and-preceding-digits-when-setting-length-of-double-to-be
      String result = "Matrix (int width, int height, float[][] data): [\n\t" + width + "\n\t" + height + "\n\t{\n";
      for(int i = 0; i<data.length; i++){
         result += "\t\t";
         for(int j = 0; j<data[i].length; j++){
            String val = (new BigDecimal(data[i][j]).setScale(6, BigDecimal.ROUND_HALF_UP)).toString();
            if(val.charAt(0) != (int)('-')){
               result += " ";
            }
            result +=  val + " ";
         }
         result += "\n";
      }
      return result + "\t}\n]";
   }
   public static Matrix GivensRot(int d, float theta, int axis1, int axis2){
      float[][] rotData = new float[d][d];
      if(axis1 > axis2){
         int temp = axis2;
         axis2 = axis1;
         axis1 = temp;
      }
      for(int i = 0; i<d; i++){
         if(i!=axis1 || i!=axis2){
            rotData[i][i] = 1;
         }
      }
      rotData[axis1][axis1] = (float) Math.cos(theta);
      rotData[axis2][axis2] = (float) Math.cos(theta);
      rotData[axis1][axis2] = (float) Math.sin(theta);
      rotData[axis2][axis1] = (-1f)*(float) Math.sin(theta);
      return new Matrix(rotData);
   }
   public Matrix transpose(){
      float[][] temp = new float[height][width];
      for(int i = 0; i<width; i++){
         for(int j = 0; j<height; j++){
            temp[i][j] = data[j][i];
         }
      }
      return null;
   }
   //returns null if there is no solution or infitite solutions
   public Vector solve(Vector v){
      Matrix aug = AugmentedMatrix(v);
      Matrix rref = aug.getRREF();
      Vector[] allEq = rref.toVectors();
      Vector result = null;
      //check for multiple or impossible
      if(rref.height<(rref.width-1)){
         return null;
      }
      for(int i = 0; i<allEq.length; i++){
         if(allEq[i].getCoords()[i] == 0){
            return null;
         }
      }
      //actually solve
      float[] resultData = new float[rref.width-1];
      for(int i = allEq.length-1; i>=1; i--){
         float mult = 1/allEq[i].getCoords()[i];
         allEq[i] = allEq[i].scale(1/allEq[i].getCoords()[i]);
         resultData[i] = allEq[i].getCoords()[rref.width-1];
         //subtract from above vectors
         for(int j = i-1; j>=0; j--){
            if(allEq[j].getCoords()[i] != 0){
               float scale = allEq[j].getCoords()[i]/allEq[i].getCoords()[i];
               allEq[j] = allEq[j].add(allEq[i].scale(-1*scale));
            }
         }
      }
      allEq[0] = allEq[0].scale(1/allEq[0].getCoords()[0]);
      resultData[0] = allEq[0].getCoords()[rref.width-1];
      result = new Vector(resultData);
      return result;
   }
}