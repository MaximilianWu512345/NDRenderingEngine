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
   public default byte[] toByteArray(){
      Color c = getColor(null);
      byte[] result = new byte[3];
      result[0] = (byte)c.getRed();
      result[1] = (byte)c.getGreen();
      result[2] = (byte)c.getBlue();
      return result;
   }
}