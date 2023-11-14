/** Plane class */
import java.util.*;
public class Plane extends AffineSubSpace{
/** The position of this Plane. */
/** The norm of this Plane. */
   public Vector norm;
/** Creates a new Plane at a position of p and a norm of norm.
* @param p The position of the Plane.
* @param norm The norm of the Plane.
*/
   public Plane(Point p, Vector norm){
      this.p = p;
      this.norm = norm;
      int d = p.length();
      int pointer = 0;
      ArrayList<Vector> temp = new ArrayList<Vector>();
      for(int i = 0; i<d; i++){
         for(int j = 0; j<d; j++){
            if(i != j){
               Matrix rot = Matrix.GivensRot(d, 90, i, j);
               Vector res = norm.rotBy(rot);
               if(!res.equals(norm)){
                  temp.add(res);
               }
            }
         }
      }
      dir = new Vector[temp.size()];
      dir = temp.toArray(dir);
   }
   public Plane(Point p, Vector[] dir){
      super(p, dir);
   }
/** Returns the Point position of this Plane.
* @return the position of this Plane.
*/
   public Point getPosition(){
      return p;
   }
/** Returns the Vector norm of this Plane.
* @return the norm of this Plane.
*/
   public Vector getNorm(){
      return norm;
   }
/** Checks if a Point is on this Plane.
* @param c The point to check.
* @return whether or not the Point is on this Plane.
*/
   public boolean isOnPlane(Point c){
      float sum = 0;
      for(int i = 0; i<p.length(); i++){
         sum += (c.getCoords()[i] + p.getCoords()[i]) * norm.getCoords()[i];
      }
      return sum<0.00001 && sum>-0.00001;
   }
/** The distance between this Plane and the Point c.
* @param c The point to check.
* @return a float of the distance between this Plane and the Point c.
*/
   public float dist(Point c){
      float sum = 0;
      for(int i = 0; i<p.length(); i++){
         sum += (c.getCoords()[i] + p.getCoords()[i]) * norm.getCoords()[i];
      }
      return sum/norm.mag();
   }
/** Checks if a Line is intersecting this Plane.
* @param l The line to check.
* @return the Point where the line intersects.
*/
   public Point intersect(Line l){
      float t = 0;
      float num = 0;
      float dem = 0;
      for(int i = 0; i<l.getDirection().length(); i++){
         num += norm.getCoords()[i]*l.getPosition().getCoords()[i] + norm.getCoords()[i]*p.getCoords()[i];
         dem += l.getDirection().getCoords()[i]*norm.getCoords()[i];
      }
      t = num/dem;
      return l.getPointOn(t);
   }
   
/** Generic toString() method.
* @return String describing this Object.
*/
   public String toString() {
      return toString(0);
   }
   
/** Generic toString() method.
* @param extraTabs the amount of extra tabbing after each \n in the String.
* @return String describing this Object.
*/
   public String toString(int extraTabs) {
      String tabs = "\t";
      String lastTab = "";
      while (extraTabs > 0) {
         extraTabs--;
         tabs += "\t";
         lastTab += "\t";
      }
      String temp = lastTab + "Plane (Point p, Vector norm): [\n" + tabs + p + "\n" + tabs + norm + "\n" + lastTab + "]";
      return temp;
   }
}