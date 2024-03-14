import java.awt.Color;
import java.util.*;
import java.lang.*;
/** Camera interface */
public interface Camera{

/**
* Sets camera data.
* @param position the new position of the camera.
* @param v the new direction of the camera.
* @param w the new width of the camera.
* @param h the new height of the camera.
*/
   public void setData(Point position, Vector V, int width, int height);

   
   
/**
* Projects a Mesh and an int dimension to a Color[][].
* @param o the mesh.
* @param dimention the dimension.
* @return a projected Color[][]
*/

   public Texture Project(Mesh[] o) ;
   
/**
* Projects a Mesh and an int dimension to a Color[][].
* @param o the mesh.
* @param the color of the background, optionally null.
* @return a projected Color[][]
*/
   public Texture Project(Mesh[] o, Color triangleC, Color backgroundC);


}