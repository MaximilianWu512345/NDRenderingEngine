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

      /*
      temp = a.getCords();
      

      for(int i = 0; i<temp.length; i++){
         temp[i] -= b.getCords()[i];
      }
      */
      direction = new Vector(temp);
   }

   public point getIntersect(Line){
      float[] p1 = new float[];
      float[] v1 = new float[];
      float[] p2 = new float[];
      float[] v2 = new float[];
      float[] t = new float[];
      /*set array values*/
      for(int i = 0; i<p.length; i++){
        t[i] = (p2[i]-p1[i])/(v1[i]-v2[i]) 
      } 
      return null;
      /**/
      
   }
   public getPoint(){
      return position;
   }
   public getVector(){
      return direction;
   }

}