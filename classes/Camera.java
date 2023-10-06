import java.awt.Color;
import java.util.ArrayList;
public class Camera{
   public Point position;
   public Vector direction;
   public int width;
   public int height;
   public Color background;
   
   public Camera (Point position, Vector V, int width, int Height){
      setData(position, V, width, Height);
      background = Color.BLACK;
   }
   public void setData(Point position, Vector V, int width, int height){
      this.position = position;
      this.direction = V;
      this.width = width;
      this.height = height;
   }
   public Color[][] Project(Mesh[] o, int dimention){
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
      while(currentD != 2){
      //resolve Intersections
      //resolve Overlaps
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
            result[i][j] = background;
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
                  System.out.println(p);
               }
            }
         }
      }
      return result;
   }
}