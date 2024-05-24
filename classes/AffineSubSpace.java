public class AffineSubSpace{
   protected Point p;
   protected SubSpace s;
   public AffineSubSpace(SubSpace s, Point p){
      this.p = p;
      this.s = s;
   }
   public Point getPoint(){
      return p;
   }
   public void setPoint(Point p){
      this.p = p;
   }
   public SubSpace getSubSpace(){
      return s;
   }
   public void setSubSpace(SubSpace s){
      this.s = s;
   }
   public void translate(Point p){
      this.p.translate(p);
   }
}