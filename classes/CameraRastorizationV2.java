import java.awt.Color;
import java.util.*;
import java.lang.*;
public class CameraRastorizationV2 implements Camera{
   private Point c;
   private AffineSubspace s;
   private Matrix m;
   private int[] bounds;
   public CameraRastorizationV2 (Point c, AffineSubspace s, int[] bounds){
      this.s = s;
      this.c = c;
      this.bounds = bounds;
      reCalculateMatrix();
   }
   
/**
* Sets camera data.
* @param position the new position of the camera.
* @param v the new direction of the camera.
* @param w the new width of the camera.
* @param h the new height of the camera.
*/
   public void setData(Point position, AffineSubspace s){
      c = position;
      this.s = s;
      reCalculateMatrix();
   }
   public void reCalculateMatrix(){
      float[][] data = new float[c.length()+1][s.getSubSpace().getDir().length +1];
      Vector[] v = s.getSubSpace().getDir();
      for(int i = 0; i<v.length; i++){
         for(int j = 0; j<v[i].length(); j++){
            data[j][i] = v[i].getCoords()[j];
         }
      }
      for(int i = 0; i<data[data.getHeight()].length-1; i++){
         data[data.getHeight()][i] = 0
      }
      data[data.getHeight()][data.getWidth] = 1;
      m = new Matrix(data);
   }
   
   
/**
* Projects a Mesh and an int dimension to a Color[][].
* @param o the mesh.
* @param dimention the dimension.
* @return a projected Color[][]
*/
   public Texture Project(Mesh[] o, int dimension){
      return Project(o, dimension, Color.RED, Color.BLACK);
   }
   
/**
* Projects a Mesh and an int dimension to a Color[][].
* @param o the mesh.
* @param dimention the dimension.
* @param the color of the background, optionally null.
* @return a projected Color[][]
*/
   public Texture Project(Mesh[] o, int dimention, Color triangleC, Color backgroundC){
      //get all simplexes
      int numColors = 1;
      for(int i = 0; i<bounds.length; i++){
         numColors *= bounds[i];
      }
      Texture t = new ArrayTexture(new Color[numColors],bounds);
      LinkedList<Simplex> original = new LinkedList<Simplex>();
      for(Mesh obj: o){
         Simplex[] faces = obj.getFaces();
         for(Simplex face: faces){
            original.add(face);
         }
      }
      //calculate vertex positions
      LinkedList<Simplex> projected = new LinkedList<Simplex>();
      //for each simplex
      for(Simplex current: original){
         Point[] newPoints = new Point[current.getPoints().length];
         Point[] oldPoints = current.getPoints();
         //for each point
         for(Point p: oldPoints){
            //get lambda
            Vector l = new Vector(p,c);
            Vector tc = new Vector(s.getPoint(),c);
            float mag = l.mag();
            float lambda = (tc.dot(l))/(mag*mag);
            //set up matrix
            Vector z = l.scale(lambda).add(new Vector(s.getPoint().getCoords()));
            float[][] data = m.getData();
            for(int i = 0; i<z.length(); i++){
               data[i][data[i].length-1] = z.getCoords()[i];
            }
            m = new Matrix(data);
            float[] adjP = new float[p.length()+1];
            for(int i = 0; i<p.length(); i++){
               adjp[i] = p.getCoords()[i];
            }
            adjp[adjp.length-1] = 1;
            Vector pos = new Vector(adjp);
            Matrix aug = m.getAugmentedMatrix(pos);
            //get final point
            
            //save depth using lambda?
         }
         
         
      }
      //z-buffering
      //paint
      return null;
   }
}