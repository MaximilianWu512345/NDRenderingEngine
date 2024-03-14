public class AffineSubspace{
   protected SubSpace s;
   protected Point p;
   public AffineSubspace(SubSpace s, Point p){
      this.s = s;
      this.p = p;
   }
   public Point getPoint(){
      return p;
   }
   public SubSpace getSubSpace(){
      return s;
   }
}