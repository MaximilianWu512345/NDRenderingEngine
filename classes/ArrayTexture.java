import java.awt.Color;
public class ArrayTexture implements Texture{
   private int[] bounds;
   private Color[] data;
   public ArrayTexture(int[] bounds, Color[] data){
      this.bounds = bounds;
      this.data = data;
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
   public Color[] getData() {
      return data;
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