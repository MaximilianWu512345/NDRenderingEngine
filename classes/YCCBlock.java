public class YCCBlock {
   
   public static final int SIZE = 8;
   
   public YCC[][] yccBlock;
   
   // representative of 3 matrixes, for each channel
   public Matrix[] matrixBlock;
   
   public YCCBlock(YCC[][] block) {
      yccBlock = block;
   }
   
   public void setYCCBlock(YCC[][] block) {
      yccBlock = block;
   }
   
   public void setMatrixBlock(Matrix[] block) {
      matrixBlock = block;
   }
   
   public YCC[][] getYCCBlock() {
      return yccBlock;
   }
   
   public Matrix[] getMatrixBlock() {
      return matrixBlock;
   }
   
   public String toString() {
      String temp = "";
      if (yccBlock != null) {
         for (YCC[] list : yccBlock) {
            for (YCC o : list) {
               temp += o + "::: ";
            }
            temp += "\n";
         }
      }
      if (matrixBlock != null) {
         for (Matrix m : matrixBlock) {
            temp += m + "::: ";
         }
      }
      return temp;
   }
}