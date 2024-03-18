public class MatrixInformation {
   protected Matrix matrix;
   protected int pivotCount;
   
   public MatrixInformation(Matrix m, int pV) {
      setMatrix(m);
      setPivotCount(pV);
   }
   
   public MatrixInformation(Matrix m) {
      setMatrix(m);
   }
   
   public MatrixInformation(int pV) {
      setPivotCount(pV);
   }
   
   public void setMatrix(Matrix m) {
      this.matrix = m;
   }
   
   public void setPivotCount(int pV) {
      pivotCount = pV;
   }
   
   public Matrix getMatrix() {
      return matrix;
   }
   
   public int getPivotCount() {
      return pivotCount;
   }
}