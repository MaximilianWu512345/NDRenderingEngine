import java.awt.Color;

public interface Texture{
   public int[] getBounds();
   public Color getColor(Point p);
   public void setBounds(int[] b);
   public default boolean setColor(Point p, Color c){
      return false;
   }
   public default boolean placeMatters(){
      return true;
   }
   public default char[] toCharArray(){
      Color c = getColor(null);
      char[] result = new char[3];
      result[0] = (char)c.getRed();
      result[1] = (char)c.getGreen();
      result[2] = (char)c.getBlue();
      return result;
   }
}