public class Line{
   private Point position;
   private Vector direciton;
   public line(Point p, Vector v){
      position = p;
      direction = v;
   }
   public line(Point a, Point b){
      position = a;
      float[] temp = new float[];
      //temp = a.getCords();
      /*
      for(int i = 0; i<temp.length; i++){
         temp[i] -= b.getCords()[i];
      }
      */
      direction = new Vector(temp);
   }
   public getIntersect(Line){
      
   }
}