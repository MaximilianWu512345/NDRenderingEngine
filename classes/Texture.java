import java.awt.Color;
public interface Texture {
   public int getDimention();
   public int[] getBounds();
   public Color getColor(Point p);
   public default boolean setColor(Point p, Color c){
      return false;
   }
}