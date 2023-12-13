import java.awt.Color;
public class zBufferArrayTexture implements Texture{
   protected PixelContainer[] data;
   protected int[] bounds;
   public zBufferArrayTexture(Color[] data, int[] bounds){
      this.data = new PixelContainer[data.length];
      for(int i = 0; i<data.length; i++){
         this.data[i] = new PixelContainer(data[i],-1f);
      }
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
      return data[index].c;
   }
   public void setBounds(int[] b){
      bounds = b;
   }
   public boolean setColor(Point p, Color c){
      int index = getIndex(p);
      if(index == -1){
         return false;
      }
      if(data[index].z == -1 || data[index].z > p.getCoords()[p.length()-1]){
      data[index] = new PixelContainer(c, p.getCoords()[p.length()-1]);
      return true;
      }
      return false;
   }
   protected int getIndex(Point p){
      int mult = 1;
      int result = 0;
      for(int i = 0; i<p.length()-1; i++){
         int val = (int)p.getCoords()[i];
         if(val>bounds[i]){
            return -1;
         }
         result += mult*val;
         mult *= bounds[i];
      }
      return result;
   }
   protected class PixelContainer{
      public PixelContainer(Color c, float z){
         this.c = c;
         this.z = z;
      }
      public Color c;
      public float z;
   }
   public ArrayTexture getArrayTexture(){
      Color[] colorData = new Color[data.length];
      for(int i = 0; i<colorData.length; i++){
         colorData[i] = data[i].c;
      }
      return new ArrayTexture(colorData, bounds);
   }
}