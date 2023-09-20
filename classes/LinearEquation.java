/**
* repersents linear equations and evaluates for variables
*/
public class LinearEquation{
   /**
   *number of variables in equation
   */
   private int numVar;
   /**
   *coeffecents of variables and constant
   */
   private float[] paramiters;
   /**
   *creates a linear equation with last index of p contaning constant and the rest being coeffecents for vaiables
   *@param p has atleast 1 element
   */
   public LinearEquation(float[] p){
      paramiters = p;
      numVar = p.length-1;
   }
   /**
   *gets number of varibles
   *@return int with count of all varibles
   */
   public int getVarCount(){
      return numVar;
   }
   /**
   *gets paramiters
   *@return float[] with all paramiters
   */
   public float[] getParm(){
      return paramiters;
   }
   /**
   *Evaluates equation and solves value of missing varible given values for the rest of the varibles
   *@param var has same number of elements as value and has a length equivelent to the number of non zero elements in paramiters
   *@param value has same number of elements as var and has a length equivelent to the number of non zero elements in paramiters
   *@return float[] of length 2 where index 0 contains the value and index 1 contains the variable solved for, index is -1 if no variable found
   */
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