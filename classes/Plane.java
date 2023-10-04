public class Plane{
   public Point p;
   public Vector norm;
   public Plane(Point p, Vector norm){
      this.p = p;
      this.norm = norm;
   }
   public Point getPosition(){
      return p;
   }
   public Vector getNorm(){
      return norm;
   }
   public boolean isOnPlane(Point c){
      float sum = 0;
      for(int i = 0; i<p.length(); i++){
         sum += (c.getCoords()[i] + p.getCoords()[i]) * norm.getCoords()[i];
      }
      return sum<0.00001 && sum>-0.00001;
   }
   public float dist(Point c){
      float sum = 0;
      for(int i = 0; i<p.length(); i++){
         sum += (c.getCoords()[i] + p.getCoords()[i]) * norm.getCoords()[i];
      }
      return sum/norm.mag();
   }
   public Point intersect(Line l){
      float t = 0;
      float num = 0;
      float dem = 0;
      for(int i = 0; i<l.getDirection().length(); i++){
         num -= norm.getCoords()[i]*l.getPosition().getCoords()[i] + norm.getCoords()[i]*p.getCoords()[i];
         dem += l.getDirection().getCoords()[i]*norm.getCoords()[i];
      }
      t = num/dem;
      return l.getPointOn(t);
   }
   
   public String toString() {
      return "Plane (Point p, Vector norm): {\n\t" + p + "\n\t" + norm + "\n}";
   }
}