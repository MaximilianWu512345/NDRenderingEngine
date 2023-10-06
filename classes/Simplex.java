import java.awt.Color;
public class Simplex{
   private Point[] points;
   private Plane surface;
   private Color color;
   public Simplex(Point[] vertex){
      setPoints(vertex);
      color = Color.RED;
   }
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
   public Point[] getPoints(){
      return points;
   }
   /* algorthm from https://stackoverflow.com/questions/21819132/how-do-i-check-if-a-simplex-contains-the-origin
      by ellisbben
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
   
   public Color getColor(){
      return color;
   }
   
   public void setColor(Color c){
      color = c;
   }
   
   public void translate(float[] coords) {
      for (Point p : points)
         p.translate(coords);
   }
   
   public void rotate(int degrees) {
      for (Point p : points)
         // alternatively, new java.awt.Point(x, y)
         p.rotate(degrees, null);
   }
   
   public String toString() {
      String temp = "Simplex (Point[] points, Plane surface, Color color): {\n\t{";
      if (points != null && points.length > 0) {
         for (int i = 0; i < points.length - 1; i++) {
            temp += points[i] + ", ";
         }
         temp += points[points.length - 1];
      }
      temp += "}\n\t" + surface + "\n\t" + color + "\n}";
      return temp;
   }
}