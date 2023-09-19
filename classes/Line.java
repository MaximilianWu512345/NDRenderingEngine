public class Line {

   private Point position;
   
   private Vector direction;
   
   public Line(Point p, Vector v) {
      position = p;
      direction = v;
   }
   
   public Line(Point a, Point b) {
      position = a;
      float[] temp = a.getCoords();
      for(int i = 0; i < temp.length; i++){
         temp[i] -= b.getCoords()[i];
      }
      direction = new Vector(temp);
   }
   
   public Point getIntersect(Line line) {
      float[] p1 = position.getCoords();
      float[] p2 = line.position.getCoords();
      float[] v1 = direction.getCoords();
      float[] v2 = line.direction.getCoords();
      float[] t = new float[p1.length];
      for(int i = 0; i<p1.length; i++){
         t[i] = (p2[i]-p1[i])/(v1[i]-v2[i]);
      }
      for(int i = 1; i<t.length; i++){
         if((t[i-1] - t[i])>0.00001){
            return null;
         }
      }
      return getPointOn(t[0]);
   }
   public Point getPosition(){
      return position;
   }
   public Vector getDirection(){
      return direction;
   }
   public Point getPointOn(float t){
      float[] nP = new float[position.getCoords().length];
      for(int i = 0; i<nP.length; i++){
         nP[i] = position.getCoords()[i]+t*direction.getCoords()[i];
      }
      return new Point(nP);
   }
}