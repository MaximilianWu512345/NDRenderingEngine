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
   public subSpace getSubSpace(){
      return s;
   }
}