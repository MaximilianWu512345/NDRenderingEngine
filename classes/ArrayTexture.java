import java.awt.Color;
public class ArrayTexture implements Texture{
   private int[] bounds;
   private Color[] data;
   public ArrayTexture(int[] bounds, Color[] data){
      this.bounds = bounds;
      this.data = data;
   }
   public int getDimention(){
      return bounds.length;
   }
   public int[] getBounds(){
      return bounds;
   }
   public Color getColor(Point p){
   
      int mult = 1;
      float[] pos = p.getCoords();
      int index = (int)pos[0];
      for(int i = 1; i<pos.length; i++){
         mult *= bounds[0];
         index += pos[i]*mult;
      }
      if(index<data.length){
         return data[index];
      }
      return null;
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