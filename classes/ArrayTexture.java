import java.awt.Color;
public class ArrayTexture implements Texture{

 protected Color[] data;
 protected int[] bounds;
 public ArrayTexture(Color[] data, int[] bounds){
   this.data = data;
   this.bounds = bounds;
 }

   public ArrayTexture(Color[][] data){
      bounds = new int[2];
      bounds[0] = data[0].length;
      bounds[1] = data.length;
      this.data = new Color[(bounds[0]*bounds[1])];
      for(int i = 0; i<data.length; i++){
         for(int j = 0; j<data[i].length; j++){
            this.data[i*bounds[0] + j] = data[i][j];
         }
      }
   }
   public int getDimention(){
      return bounds.length;
   }
   public int[] getBounds(){
      return bounds;
   }
   public Color getColor(Point p){

      int index = getIndex(p);
      if(index == -1){
         return null;
      }
      return data[index];
   }
   public void setBounds(int[] b){
      bounds = b;
   }
   public boolean setColor(Point p, Color c){
      int index = getIndex(p);
      if(index == -1){
         return false;
      }
      data[index] = c;
      return true;
   }
   protected int getIndex(Point p){
      int mult = 1;
      int result = 0;
      for(int i = 0; i<p.length(); i++){
         int val = (int)p.getCoords()[i];
         if(val>bounds[i]){
            return -1;
         }
         result += mult*val;
         mult *= bounds[i];
      }
      return result;
   }

   public boolean setColor(Point p, Color c){
      if(p.length()==bounds.length){
         int mult = 1;
         float[] pos = p.getCoords();
         int index = (int)pos[0];
         for(int i = 1; i<pos.length; i++){
            if((int)pos[i] >=0 && (int)pos[i] <bounds[i]){
            mult *= bounds[0];
            index += pos[i]*mult;
            } else {
               return false;
            }
         }
         if(index<data.length){
            data[index] = c;
            return true;
         }
      }
      return false;
   }
}