public class Matrix{
   private float[][] data;
   private int width;
   private int height;
   public Matrix(float[][] d){
      data = d;
      height = d.length;
      width = d[0].length;
   }
   public Matrix(Vector[] d){
      data = new float[d.length][d.length];
      for(int i = 0; i<d.length; i++){
         data[i] = d[i].getCoords();
      }
      height = d.length;
      width = d[0].length();
   }
   public float[][] getData(){
      return data;
   }
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
                  sum += m.data[k][i] + data[j][k];
               }
            }
         }
      }
      return null;
   }
   public Vector[] toVectors(){
      Vector[] result = new Vector[height];
      for(int i = 0; i<height; i++){
         result[i] = new Vector(data[i]);
      }
      return result;
   }
   /**
   * generates the augmented matrix
   * @param Vector v must have the same length as the height of the matrix
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
            float r = d[j].getCoords()[i]/d[i].getCoords()[i];
            d[j] = d[j].subtract(d[i].scale(r));
         }
      }
      //fix
      return new Matrix(d);
   }
   
   public void rotateMatrix(int dimension) {
   
   }
   
   
   public String toString(){
      String result = "";
      for(int i = 0; i<data.length; i++){
         for(int j = 0; j<data[i].length; j++){
            result += data[i][j] + " ";
         }
         result += "\n";
      }
      return result + "\n";
   }
   
}