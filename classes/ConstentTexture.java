import java.awt.Color;
public class ConstentTexture implements Texture{
   private Color c;
   private int d = 0;
   public ConstentTexture(Color c, int d){
      this.c = c;
      this.d = d;
   }
   public int getDimention(){
      return d;
   }
   public int[] getBounds(){
      int[] bounds = new int[d];
      for(int i = 0; i<bounds.length; i++){
         bounds[i] = 2147483647;
      }
      return bounds;
   }
   public Color getColor(Point p){
      return c;
   }
   public boolean setColor(Point p, Color c){
      this.c = c;
      return true;       
   }
   public String toString(){
      return c.toString();
   }
}