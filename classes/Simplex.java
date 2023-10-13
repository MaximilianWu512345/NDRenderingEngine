import java.awt.Color;

/** Simplex class */
public class Simplex{

/** Points in this Simplex */
   private Point[] points;
   
/** Plane in this Simplex */
   private Plane surface;
   
/** Color of this Simplex */
   private Color color;
   
/** Creates a new Simplex with points of vertex.
* @param vertex the Point[] vertex to set points to.
*/
   public Simplex(Point[] vertex){
      setPoints(vertex);
      color = Color.RED;
   }
   
/** Sets points to be vertex.
* @param vertex the Point[] vertex to set points to.
*/
   public void setPoints(Point[] vertex){
      points = vertex;
      Vector[] temp = new Vector[vertex.length-1];
      for(int i = 1; i<vertex.length; i++){
         float[] temp2 = new float[vertex.length];
         for(int j = 0; j<vertex[0].length(); j++){
            temp2[j] = vertex[0].getCoords()[j];
         }
         for(int j = 0; j < temp.length; j++){
            temp2[j] -= vertex[i].getCoords()[j];
         }
         temp[i-1] = new Vector(temp2);
      }
      surface = new Plane(vertex[0], Vector.getOrthogonal(temp));
   }
   
/** Returns the points of this Simplex.
* @return Point[] of the points of this Simplex.
*/
   public Point[] getPoints(){
      return points;
   }
   
/** Returns whether or not Point p is within this Simplex.
* Algorithm from https://stackoverflow.com/questions/21819132/how-do-i-check-if-a-simplex-contains-the-origin by ellisbben
* @param p The point to check.
* @return boolean of whether the point is within this Simplex.
*/
   public boolean isWithin(Point p){
      //shift points
      Point[] ps = new Point[points.length];
      for(int i = 0; i<ps.length; i++){
         float[] shiftedPointData = new float[p.length()];
         for(int j = 0; j<shiftedPointData.length; j++){
            shiftedPointData[j] = points[i].getCoords()[j]-p.getCoords()[j];
         }
         ps[i] = new Point(shiftedPointData);
      }
      //set up vectors
      Vector[] v = new Vector[p.length()+1];
      for(int i = 0; i<v.length-1; i++){
         float[] vd = new float[ps.length];
         for(int j = 0; j<vd.length; j++){
            vd[j] = ps[j].getCoords()[i];
         }
         v[i] = new Vector(vd);
      }
      float[] t = new float[v.length];
      for(int i = 0; i<t.length; i++){
         t[i] = 1;
      }
      //equation solver
      v[v.length-1] = new Vector(t); 
      Matrix M = new Matrix(v);
      float[] argVal = new float[v.length];
      for(int i = 0; i<p.length(); i++){
         argVal[i] = 0;
      }
      argVal[argVal.length-1] = 1;
      Matrix aug = M.AugmentedMatrix(new Vector(argVal));
      Matrix rref = aug.getRREF();
      Vector[] d = rref.toVectors();
      int[] var = new int[0];
      float[] value = new float[0];
      float[] result = new float[v[0].length()];
      for(int i = d.length-1; i>=0; i--){
         LinearEquation le = new LinearEquation(d[i].getCoords());
         float[] leRes = le.Evaluate(var, value);
         //add new vector values
         result[(int)leRes[1]] = leRes[0];
         int[] temp1 = new int[var.length+1];
         for(int j = 0; j<var.length; j++){
            temp1[j] = var[j];
         }
         temp1[temp1.length-1] = (int)leRes[1];
         var = temp1;
         float[] temp2 = new float[value.length+1];
         for(int j = 0; j<value.length; j++){
            temp2[j] = value[j];
         }
         temp2[temp2.length-1] = leRes[0];
         value = temp2;
      
      }
      for(int i = 0; i<result.length; i++){
         if(result[i]<0){
            return false;
         }
      }
      return true;
   }
   
/** Returns the Color of this Simplex.
* @return Color of this Simplex.
*/
   public Color getColor(){
      return color;
   }
   
/** Sets the Color of this Simplex.
* @param c The color to set.
*/
   public void setColor(Color c){
      color = c;
   }
   
/** Translates all Points in this Simplex with coords.
* @param coords The float[] to translate Points in this Simplex with.
*/
   public void translate(float[] coords) {
      for (Point p : points)
         p.translate(coords);
      setPoints(points);
   }
   
/** Rotates all Points in this Simplex by degrees around the origin.
* @param degrees the amount to rotate by.
*/
   public void rotate(int degrees) {
      rotate(degrees, null);
   }
   
/** Rotates all Points in this Simplex by degrees, and optionally an origin.
* @param degrees the amount to rotate by.
* @param origin the java.awt.Point origin to rotate around, optional.
*/
   public void rotate(int degrees, java.awt.Point origin) {
      for (Point p : points)
         p.rotate(degrees, origin);
      setPoints(points);
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
      int count = extraTabs;
      while (extraTabs > 0) {
         extraTabs--;
         tabs += "\t";
         lastTab += "\t";
      }
      String temp = lastTab + "Simplex (Point[] points, Plane surface, Color color): [\n" + tabs +"{";
      if (points != null && points.length > 0) {
         for (int i = 0; i < points.length - 1; i++) {
            temp += "\n\t" + tabs + points[i] + ", ";
         }
         temp += "\n\t" + tabs + points[points.length - 1];
      }
      temp += "\n" + tabs + "}\n" + surface.toString(1 + count) + "\n" + tabs + color + "\n" + lastTab + "]";
      return temp;
   }
}