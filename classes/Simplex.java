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
         float[] temp2 = new float[vertex.length];
         for(int j = 0; j<vertex[0].length(); j++){
            temp2[j] = vertex[0].getCoords()[j];
         }
         for(int j = 0; j < temp.length; j++){
            temp2[j] -= vertex[i].getCoords()[j];
         }
         temp[i-1] = new Vector(temp2);
      }
      surface = new Plane(vertex[0], Vector.getOrthogonal(temp));
   }
   public Point[] getPoints(){
      return points;
   }
   /* algorthm from https://stackoverflow.com/questions/21819132/how-do-i-check-if-a-simplex-contains-the-origin
      by ellisbben
   */
   public boolean isWithin(Point p){
      int d = points[0].length();
      //for()
      return false;
   }
   public Color getColor(){
      return color;
   }
   public void setColor(Color c){
      color = c;
   }
   
   public void translate(float[] coords) {
      for (Point p : points)
         p.translate(coords);
   }
   
   public void rotate(int degrees) {
      for (Point p : points)
         p.rotate(degrees);
   }
}