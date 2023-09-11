public class Line {

   private Point position;
   
   private Vector direction;
   
   public Line(Point p, Vector v) {
      position = p;
      direction = v;
   }
   
   public Line(Point a, Point b) {
      position = a;
      float[] temp = a.getCords();
      for(int i = 0; i < temp.length; i++){
         temp[i] -= b.getCords()[i];
      }
      direction = new Vector(temp);
   }
   
   public Line getIntersect(Line line) {
      return line;
   }
}