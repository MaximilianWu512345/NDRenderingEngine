import java.awt.Color;
public class Simplex{
   private Point[] points;
   private Plane surface;
   private Color color;
   public Simplex(Point[] vertex){
      setPoints(vertex);
      color = Color.RED;
   }
   public void setPoints(Point[] vertex){
      points = vertex;
      Vector[] temp = new Vector[vertex.length-1];
      for(int i = 1; i<vertex.length; i++){
         float[] temp2 = vertex[0].getCoords();
         for(int j = 0; j < temp.length; j++){
            temp2[j] -= vertex[i].getCoords()[j];
         }
         temp[i] = new Vector(temp2);
      }
      surface = new Plane(vertex[0], Vector.getOrthogonal(temp));
   }
   public Point[] getPoints(){
      return points;
   }
   public boolean isWithin(Point p){
      
      return false;
   }
}