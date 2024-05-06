import java.awt.Color;

/** Simplex class */
public class Simplex {

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
      t = new ConstentTexture(Color.RED, new int[vertex.length-1]);
   }
   
/** Creates a new Simplex with points of vertex.
* @param vertex the Point[] vertex to set points to.
* @param c the color to set points to.
*/
   public Simplex(Point[] vertex, Color c){
      setPoints(vertex);
      t = new ConstentTexture(c, new int[vertex.length-1]);
      Point[] textPoints = new Point[vertex.length-1];
      for(int i = 0; i<textPoints.length; i++){
         textPoints[i] = new Point(new float[vertex[0].length()]);
      }
      setTexturePoints(textPoints);

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
      if(vertex.length > vertex[0].getCoords().length){
         surface = null;
         return;
      }
      Vector[] temp = new Vector[vertex.length-1];
      for(int i = 1; i<vertex.length; i++){
         float[] temp2 = new float[vertex[0].length()];
         for(int j = 0; j<vertex[0].length(); j++){
            temp2[j] = vertex[0].getCoords()[j];
         }
         for(int j = 1; j < temp.length; j++){
            temp2[j - 1] -= vertex[i].getCoords()[j-1];
         }
         temp[i-1] = new Vector(temp2);
      }
      
      surface = new Plane(vertex[0], temp);
      Point[] textPoints = new Point[vertex.length];
      for(int i = 0; i<textPoints.length; i++){
         textPoints[i] = new Point(new float[vertex[0].length()]);
      }
      setTexturePoints(textPoints);
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
   private Matrix lBaryMatrix;
   private Matrix pBaryMatrix;
   private Matrix uBaryMatrix;
   private Vector shift;
   /** sets up Simplex for barycentric corodinate calculations
   */
   public void initBaryCalc(){
      if(points.length != (points[0].length()+1)){
         return;
      }
      shift = new Vector(points[points.length-1].getCoords());
      float[][] mdata = new float[points.length-1][points.length-1];
      for(int i = 0; i<mdata.length; i++){
         for(int j = 0; j<mdata[i].length; j++){
            mdata[i][j] = points[j].getCoords()[i]-points[points.length-1].getCoords()[i];
         }
      }
      //lpu decomp
      Matrix m = new Matrix(mdata);
      Matrix[] decomp = m.LPUDecomp();
      lBaryMatrix = decomp[0];
      pBaryMatrix = decomp[1];
      uBaryMatrix = decomp[2];
      System.out.println(shift);
      System.out.println(lBaryMatrix);
      System.out.println(pBaryMatrix);
      System.out.println(uBaryMatrix);
   }
   /**
   *
   */
   public boolean canGenBarycentricCoords(){
      return true;
   }
   /** gets the point in barycentric coordinates
   *  @param p the Point to translate
   *  @return a vector of the amount of each point of this simplex, filled with zeros if not possible
   */
   public Vector getBarycentricCoords(Point p){
      //set up matrix
      if(lBaryMatrix == null){
         initBaryCalc();
      }
      if((points.length != (points[0].length()+1) || lBaryMatrix == null)){
         return null;
      }
      //set up solution
      float[] dat = new Vector(points[points.length-1], p).getCoords();
      float[] sol = new float[points.length];
      //p
      for(int i = 0; i<sol.length-1; i++){
         for(int j = 0; j<dat.length; j++){
            if(Float.compare(pBaryMatrix.getData()[i][j], 1) == 0){
               sol[i] = dat[j];
               break;
            }
         }
      }
      dat = sol;
      sol = new float[points.length];
      //L
      for(int i = 0; i<lBaryMatrix.getHeight(); i++){
         sol[i] = dat[i];
         float sum = 0;
         for(int j = 0; j<i; j++){
            sum += lBaryMatrix.getData()[i][j]*sol[j];
         }
         sol[i] -= sum;
         sol[i] /= lBaryMatrix.getData()[i][i];
      }
      dat = sol;
      sol = new float[points.length];
      //U
      for(int i = uBaryMatrix.getHeight()-1; i>=0; i--){
         sol[i] = dat[i];
         float sum = 0;
         for(int j = i+1; j<uBaryMatrix.getWidth(); j++){
            sum += uBaryMatrix.getData()[i][j]*sol[j];
         }
         sol[i] -= sum;
         sol[i] /= uBaryMatrix.getData()[i][i];
      }
      sol[sol.length-1] = 1;
      float sum = 0;
      for(int i = 0; i<sol.length-1; i++){
         sum += sol[i];
      }
      sol[sol.length-1] -= sum;
      Vector solVec = new Vector(sol);
      return solVec;
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
      Matrix rref = aug.getRREF().getMatrix();
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
   
   public Plane getSurface() {
      return surface;
   }
   
   /** gets color of this simplex at specifyed location definde by points of simplex
   *@param v The location to look at in terms of how much of each vertex, values between 1 and 0, must have length equal to the number of vertexes
   *@return Color The color at the location, return null if outside of texture bounds
   */
   public Color getColor(float[] v){
      if(!t.placeMatters()){
         return t.getColor(null);
      }
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
   
      // min = [0], max = [1];
      public Point[] BoundingBox(){
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
      Point[] box = new Point[2];
      box[0] = new Point(min);
      box[1] = new Point(max);
      return box;
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
      String temp = lastTab + "Simplex (Point[] points, Plane surface, Color color): [\n" + tabs + "{";
      if (points != null && points.length > 0) {
         for (int i = 0; i < points.length - 1; i++) {
            temp += "\n\t" + tabs + points[i] + ", ";
         }
         temp += "\n\t" + tabs + points[points.length - 1];
      }
      temp += "\n" + tabs + "}\n" + (surface == null ? tabs + null : surface.toString(1 + count)) + "\n" + tabs + t + "\n" + lastTab + "]";
      if(surface != null){
         temp += "\n" + tabs + "}\n" + surface.toString(1 + count) + "\n" + tabs + t + "\n" + lastTab + "]";
      } else {
         temp += "\n" + tabs + "}\nnull\n" + tabs + t + "\n" + lastTab + "]";
      }

      return temp;
   }
   
}