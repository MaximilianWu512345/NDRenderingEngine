public class AffineSubSpace{
   protected subSpace s;
   protected Point p;
   public AffineSubSpace(subSpace s, Point p){
      this.s = s;
      this.p = p;
   }
   public Point getPoint(){
      return p;
   }
   
   public void setPoint(Point p) {
      this.p = p;
   }
   public void translate(Point p) {
      this.p.translate(p);
   }
   
   public subSpace getSubSpace(){
      return s;
   }
}