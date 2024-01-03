import java.awt.Color;

/** Simplex class */
public class Simplex{

/** Points in this Simplex */
   private Point[] points;
   
/** Corrisponding points on texture **/
   private Point[] texturePoints;
   
/** Plane in this Simplex */
   private Plane surface;
   
/** Color of this Simplex */
   private Texture t;
   
/** Creates a new Simplex with points of vertex.
* @param vertex the Point[] vertex to set points to.
*/
   public Simplex(Point[] vertex){
      setPoints(vertex);
      t = new ConstentTexture(Color.RED, new int[vertex.length]);
   }
   
/** Creates a new Simplex with points of vertex.
* @param vertex the Point[] vertex to set points to.
* @param c the color to set points to.
*/
   public Simplex(Point[] vertex, Color c){
      setPoints(vertex);
      t = new ConstentTexture(c, new int[vertex.length]);
   }
   
/** Sets points to be vertex.
* @param vertex the Point[] vertex to set points to.
*/
   public void setPoints(Point[] vertex){
      points = vertex;
      if(vertex.length == 0){
         surface = null;
         return;
      }
      Vector[] temp = new Vector[vertex.length-1];
      for(int i = 1; i<vertex.length; i++){
         float[] temp2 = new float[vertex.length];
         for(int j = 0; j<vertex[0].length(); j++){
            temp2[j] = vertex[0].getCoords()[j];
         }
         for(int j = 1; j < temp.length; j++){
            temp2[j-1] -= vertex[i].getCoords()[j];
         }
         temp[i-1] = new Vector(temp2);
      }
      
      surface = new Plane(vertex[0], temp);
   }
   
/** Returns the points of this Simplex.
* @return Point[] of the points of this Simplex.
*/
   public Point[] getPoints(){
      return points;
   }
   /** Returns the texture points of this simplex
   *@return Point[] of the texture poitns of this simplex
   */
   public Point[] getTexturePoints(){
      return texturePoints;
   }
   public void setTexturePoints(Point[] texturePoints){
      this.texturePoints = texturePoints;
   }
/** Returns whether or not Point p is within this Simplex.
* Algorithm from https://stackoverflow.com/questions/21819132/how-do-i-check-if-a-simplex-contains-the-origin by ellisbben
* @param p The point to check.
* @return boolean of whether the point is within this Simplex.
*/
   public Vector getBaryCentricCoords(Point p){
      //set up matrix
      int datWidth = points.length;
      int datHight = points[0].getCoords().length;
      float[][] matData = new float[datWidth][datHight];
      for(int i = 0; i<points.length; i++){
         float[] currentPoint = points[i].getCoords();
         for(int j = 0; j<currentPoint.length; j++){
            matData[i][j] = currentPoint[j];
         }
      }
      Matrix m = new Matrix(matData);
      //set up solution
      float[] sol = new float[datHight];
      for(int i = 0; i<sol.length-1; i++){
         sol[i] = p.getCoords()[i];
      }
      Vector solVec = new Vector(sol);
      Vector result = m.solve(solVec);
      return result;
   }
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
   
/** Returns the Texture of this Simplex.
* @return Texture of this Simplex.
*/
   public Texture getTexture(){
      return t;
   }
   
/** Sets the Texture of this Simplex.
* @param t The texture to set.
*/
   public void setTexture(Texture t){
      this.t = t;
   }
   /** gets color of this simplex at specifyed location definde by points of simplex
   *@param v The location to look at in terms of how much of each vertex, values between 1 and 0, must have length equal to the number of vertexes
   *@return Color The color at the location, return null if outside of texture bounds
   */
   public Color getColor(float[] v){
      int[] texBound = t.getBounds();
      Vector texLoc = new Vector(new float[texBound.length]);
      for(int i = 0; i<v.length; i++){
         texLoc.add(new Vector(texturePoints[i].getCoords()).scale(v[i]));
      }
      for(int i = 0; i<texLoc.length(); i++){
         if(texLoc.getCoords()[i] <= texBound[i]){
            return null;
         }
      }
      return t.getColor(new Point(texLoc.getCoords()));
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
   public float BoundingBoxDistance(){
      float[] max = new float[points[0].length()];
      float[] min = new float[points[0].length()];
      for(int i = 0; i<max.length; i++){
         max[i] = points[0].getCoords()[i];
         min[i] = points[0].getCoords()[i];
      }
      for(int i = 1; i<points.length; i++){
         Point p = points[i];
         for(int j = 0; j<max.length; j++){
            if(p.getCoords()[j]>max[j]){
               max[j] = p.getCoords()[j];
            } else if (p.getCoords()[j]<min[j]){
               min[j] = p.getCoords()[j];
            }
         }
      }
      float[] furthestPoint = new float[max.length];
      for(int i = 0; i<max.length; i++){
         float avg = (max[i]+min[i])/2f;
         if(avg < 0){
            furthestPoint[i] = min[i];
         } else if (avg > 0){
            furthestPoint[i] = max[i];
         } else {
            furthestPoint[i] = avg;
         }
      }
      int sum = 0;
      for(float c: furthestPoint){
         sum += c*c;
      }
      return (float) Math.sqrt(sum);
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
      temp += "\n" + tabs + "}\n" + surface.toString(1 + count) + "\n" + tabs + t + "\n" + lastTab + "]";
      return temp;
   }
   
}