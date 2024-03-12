/** Line class */
public class Line{

/** Position of the line. */
   private Point position;

/** Directino of the line. */   
   private Vector direction;

/**
* Creates a new Line.
* @param p the position of the line.
* @param v the direction of the line.
*/  
   public Line(Point p, Vector v) {
      position = p;
      direction = v;
   }

/**
* Creates a new Line of two points.
* @param a the first point
* @param b the second point
*/ 
   public Line(Point a, Point b) {
      position = a;
      float[] temp = new float[a.getCoords().length];
      for(int i = 0; i < temp.length; i++){
         temp[i] = b.getCoords()[i] - a.getCoords()[i];
      }
      direction = new Vector(temp);
   }

/**
* Returns a Point of the intersection of this and line.
* @param line the line to check for intersection.
* @return the Point of intersection.
*/
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
/**
* Returns the Point position of this Line.
* @return the position of this Line.
*/
   public Point getPosition(){
      return position;
   }
/**
* Returns the Vector direction of this Line.
* @return the direction of this Line.
*/
   public Vector getDirection(){
      return direction;
   }
   
/** Returns a Point on t.
* @param t the float to find the Point on.
* @return the Point on t.
*/
   public Point getPointOn(float t){
      float[] nP = new float[position.getCoords().length];
      for(int i = 0; i<nP.length; i++){
         nP[i] = position.getCoords()[i]+t*direction.getCoords()[i];
      }
      return new Point(nP);
   }
   
/** Generic toString() method.
* @return String describing this Object.
*/
   public String toString() {
      return "Line (Point position, Vector direction): [\n\t" + position + "\n\t" + direction + "\n]";
   }
}