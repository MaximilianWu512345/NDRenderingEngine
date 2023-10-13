import java.awt.Color;
import java.util.ArrayList;
import java.lang.*;
/** Camera class */
public class Camera{
/** Position of this Camera */
   public Point position;
/** Direction of this Camera */
   public Vector direction;
/** Width of this Camera */
   public int width;
/** Height of this Camera */
   public int height;
/** Background color of this Camera */
   public Color background;

   
/**
* Creates a new Camera.
* @param position the position of the camera.
* @param v the direction of the camera.
* @param w the width of the camera.
* @param h the height of the camera.
*/
   public Camera (Point position, Vector v, int w, int h){
      setData(position, v, w, h);
      background = Color.BLACK;
   }
   
/**
* Sets camera data.
* @param position the new position of the camera.
* @param v the new direction of the camera.
* @param w the new width of the camera.
* @param h the new height of the camera.
*/
   public void setData(Point position, Vector V, int width, int height){
      this.position = position;
      this.direction = V;
      this.width = width;
      this.height = height;
   }
   
/**
* Projects a Mesh and an int dimension to a Color[][].
* @param o the mesh.
* @param dimention the dimension.
* @return a projected Color[][]
*/
   public Color[][] Project(Mesh[] o, int dimension) {
      return Project(o, dimension, background);
   }
   
/**
* Projects a Mesh and an int dimension to a Color[][].
* @param o the mesh.
* @param dimention the dimension.
* @param the color of the background, optionally null.
* @return a projected Color[][]
*/
   public Color[][] Project(Mesh[] o, int dimention, Color backgroundColor) {
      long timeStart = System.nanoTime(); 
      //convert to lower dimention
      //loop through all meshes
      ArrayList<Simplex> simplexes = new ArrayList();
      for(int i = 0; i<o.length; i++){
         Mesh t = o[i];
         //loop through all simplexes
         if(t!=null){
            for(int j = 0; j<t.faces.length; j++){
            //add to list of simplexes
               simplexes.add(t.faces[j]);
            }
         }
      }
      //apply transformations so camera is straight
      int currentD = dimention;
      //loop through until at right dimention
      //resolve Intersections
      //compare every object
      //reorder Overlaps
      //compare every object
      while(currentD != 2){

      //find new coords
      //find projection plane
         float[] dir = new float[currentD];
         dir[0] = 1;
         Plane Screen = new Plane(new Point(dir), new Vector(dir));
      //loop though all simplexes
         ArrayList pojectedSimplexes = new ArrayList();
         for(int i = 0; i<simplexes.size(); i++){
            Simplex Current = simplexes.get(i);
         //loop through all vertexes
            Point[] newPoints = new Point[Current.getPoints().length];
            for(int j = 0; j<newPoints.length; j++){
               float[] origin = new float[currentD];
               Line projLine = new Line(new Point(origin), Current.getPoints()[j]);
               Point newPoint = Screen.intersect(projLine);
               newPoints[j] = Screen.intersect(projLine);
            }
            //shift points to lower dimention
            pojectedSimplexes.add(new Simplex(newPoints)); 
            
         }
         simplexes = pojectedSimplexes;
         currentD--;
      }
      //generate pixel grid
      Color[][] result = new Color[width][height];
      //set all pixels to backgorund color
      for(int i = 0; i<result.length; i++){
         for(int j = 0; j<result[i].length; j++){
            result[i][j] = backgroundColor;
         }
      }
      for(Simplex s: simplexes){
         //change to be faster later
         //add negitives later
         //shift simplexes to lower dimention
         Point[] simplexD = new Point[s.getPoints().length];
         for(int i = 0; i<s.getPoints().length; i++){
            float[] shiftedPoint = new float[s.getPoints()[i].length()-1];
            for(int j = 1; j<s.getPoints()[i].length(); j++){
               shiftedPoint[j-1] = s.getPoints()[i].getCoords()[j];
            }
            simplexD[i] = new Point(shiftedPoint);
         }
         s = new Simplex(simplexD);
         for(int i = 0; i<result.length; i++){
            for(int j = 0; j<result[i].length; j++){
               float[] d = new float[dimention-1];
               d[d.length-1] = i - result.length/2;
               d[d.length-2] = j - result[i].length/2;
               for(int k = 0; k<d.length-3; k++){
                  d[k] = 1;
               }
               
               Point p = new Point(d);
               
               if(s.isWithin(p)){
                  result[i][j] = s.getColor();
                  //System.out.println(p);
               }
            }
         }
      }
      System.out.println(timeStart-System.nanoTime());
      return result;
   }
   
/** Generic toString() method.
* @return String describing this Object.
*/
   public String toString() {
      String temp = "Camera (int width, int height, Color background, Point position, Vector direction): [\n\t" + width + "\n\t" + height + "\n\t" + background + "\n\t" + position + "\n\t" + direction + "\n]";
      return temp;
   }
}