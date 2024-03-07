import java.awt.Color;
public class ConstentTexture implements Texture{
   private Color data;
   private int[] bounds;
   public ConstentTexture(Color data, int[] bounds){
      this.data = data;
      this.bounds = bounds;
   }
   public int[] getBounds(){
      return bounds;
   }
   public Color getColor(Point p){
      return data;
   }
   public void setBounds(int[] b){
      bounds = b;
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
   public boolean placeMatters(){
      return false;
   }
}