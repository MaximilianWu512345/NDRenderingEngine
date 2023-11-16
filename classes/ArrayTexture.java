import java.awt.Color;
public class ArrayTexture implements Texture{
 private Color[] data;
 private int[] bounds;
 public ArrayTexture(Color[] data, int[] bounds){
   this.data = data;
   this.bounds = bounds;
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
      return false;
   }
   private int getIndex(Point p){
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
}