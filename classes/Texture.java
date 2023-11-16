import java.awt.Color;
public interface Texture{
   public int[] getBounds();
   public Color getColor(Point p);
   public void setBounds(int[] b);
   public default boolean setColor(Point p, Color c){
      return false;
   }
}