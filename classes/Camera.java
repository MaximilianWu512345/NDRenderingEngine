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
         for(int j = 0; j<t.faces.length; j++){
            //add to list of simplexes
            simplexes.add(t.faces[j]);
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
               Line projLine = new Line(Screen.getPosition(), Current.getPoints()[j]);
               Point newPoint = Screen.intersect(projLine);
               newPoints[j] = Screen.intersect(projLine);
            }
            //shift points to lower dimention
            Point[] temp = new Point[newPoints.length];
            for(int j = 0; j<newPoints.length; j++){
               float[] d = new float[currentD];
               for(int k = 0; k<d.length-1; k++){
                  d[k] = newPoints[j].getCoords()[k];
               }
               d[d.length-1] = dir[0];
               temp[j] = new Point(d);
            }
            newPoints = temp;
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
         for(int i = 0; i<result.length; i++){
            for(int j = 0; j<result[i].length; j++){
               float[] d = new float[dimention];
               d[0] = i;
               d[1] = j;
               for(int k = 2; k<dimention; k++){
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