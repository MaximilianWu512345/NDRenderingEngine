public class LinearEquation{
   private int numVar;
   private float[] paramiters;
   public LinearEquation(float[] p){
      paramiters = p;
      numVar = p.length-1;
   }
   public int getVarCount(){
      return numVar;
   }
   public float[] getParm(){
      return paramiters;
   }
   public float[] Evaluate(int[] var, float[] value){
      float[] result = new float[2];
      int term = -1;
      float sum = paramiters[paramiters.length];
      for(int i = 0; i<numVar; i++){
         boolean hasVal = false;
         for(int j = 0; j<var.length; j++){
            if(i == var[j]){
               hasVal = true;
               sum -= paramiters[i];
            }
         }
         if(paramiters[i] != 0 && !hasVal){
            term = i;
         }
      }
      sum = sum/paramiters[term];
      result[0] = sum;
      result[1] = term;
      return result;
   }
}