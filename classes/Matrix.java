public class Matrix{
   private float[][] data;
   private int width;
   private int height;
   public Matirx(float[][] d){
      data = d;
      height = d.length;
      width = d[0].length;
   }
   public float[][] getData(){
      return data;
   }
   public Matrix mult(Matrix m){
      
   }
}