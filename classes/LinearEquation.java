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
      float sum = paramiters[paramiters.length-1];
      for(int i = 0; i<numVar; i++){
         boolean hasVal = false;
         for(int j = 0; j<var.length; j++){
            if(i == var[j]){
               hasVal = true;
               sum -= paramiters[i]*value[j];
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
   public String toString(){
      String s = "";
      for(int i = 0; i<numVar; i++){
         s += (paramiters[i] + "x" + i + " + ");
      }
      s += (paramiters[numVar] + " = 0");
      return s;
   }
}