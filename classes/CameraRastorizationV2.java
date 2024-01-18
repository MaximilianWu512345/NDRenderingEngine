import java.awt.Color;
import java.util.*;
import java.lang.*;
public class CameraRastorizationV2 implements Camera{
   protected Point c;
   protected AffineSubspace s;
   protected Matrix m;
   Vector[] v;
   Vector[] u;
   Vector[] negu;
   Vector exten;
   Vector constShift;
   protected Vector sol;
   protected int[] bounds;
   protected final float EXISTS = 1;
   protected int g = 0;
   protected int ms = 0;
   protected int n = 0;
   public CameraRastorizationV2 (Point c, AffineSubspace s, int[] bounds){
      this.s = s;
      this.c = c;
      this.bounds = bounds;
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
   }
   public void reCalculateMatrix(Simplex s){
      g = s.getPoints().length;
      ms = this.s.getSubSpace().getDir().length;
      n = c.length();
      int numCol = 2*ms+g+2;
      int numRow = n+2;
      float[][] data = new float[numRow][numCol];
      int currentCol = 0;
      Point[] newData = s.getPoints();
      Vector[] v = new Vector[newData.length];
      Vector[] u = this.s.getSubSpace().getDir();
      Point p = newData[0];
      Vector exten = new Vector(c, this.s.getPoint());
      Vector constShift = new Vector(p.getCoords());
      for(int i = 0; i<newData.length; i++){
         v[i] = new Vector(newData[i].getCoords());
      }
      float[] temp;
      //u vectors positive
      int start = currentCol;
      while(currentCol-start < ms){
         //add current vector
         temp = new float[numRow];
         for(int i = 0; i<n; i++){
            data[i][currentCol] = u[currentCol-start].getCoords()[i];
            temp[i] = u[currentCol-start].getCoords()[i];
         }
         u[currentCol-start] = new Vector(temp);
         currentCol++;
      }
      //extention vector
      temp = new float[numRow];
      for(int i = 0; i<n; i++){
         data[i][currentCol] = exten.getCoords()[i];
         temp[i] = exten.getCoords()[i];
      }
      currentCol++;
      //u vectors negative
      start = currentCol;
      while(currentCol-start < ms){
         temp = new float[numRow];
         //add negative vector
         for(int i = 0; i<n; i++){
            data[i][currentCol] = -1*u[currentCol-start].getCoords()[i];
            temp[i] = -1*u[currentCol-start].getCoords()[i];
         }
         negu[currentCol-start] = new Vector(temp);
         currentCol++;
      }
      //v vectors
      start = currentCol;
      while(currentCol-start < g){
         temp = new float[numRow];
         //add current vector
         for(int i = 0; i<n; i++){
            data[i][currentCol] = v[currentCol-start].getCoords()[i];
            temp[i] = v[currentCol-start].getCoords()[i];
         }
         data[n][currentCol] = 1;
         temp[n] = 1;
         v[currentCol-start] = new Vector(temp);
         currentCol++;
      }
      //const
      temp = new float[numRow];
      for(int i = 0; i<n; i++){
         data[i][currentCol] = constShift.getCoords()[i];
         temp[i] = constShift.getCoords()[i];
      }
      data[numRow-1][currentCol] = 1;
      temp[numRow-1] = 1;
      constShift = new Vector(temp);
      currentCol++;
      m = new Matrix(data);
      //remove reduntant bases later
      //generate solution
      float[] result = new float[numRow];
      
      result[numRow-1] = 1;
      result[numRow-2] = 1;
      sol = new Vector(result);
      
   }

      
   
   
/**
* Projects a Mesh and an int dimension to a Color[][].
* @param o the mesh.
* @param dimention the dimension.
* @return a projected Color[][]
*/
   public Texture Project(Mesh[] o){
      return Project(o, Color.RED, Color.BLACK);
   }
   
/**
* Projects a Mesh and an int dimension to a Color[][].
* @param o the mesh.
* @param dimention the dimension.
* @param the color of the background, optionally null.
* @return a projected Color[][]
*/
   public Texture Project(Mesh[] o, Color triangleC, Color backgroundC){
      //get all simplexes
      int numColors = 1;
      for(int i = 0; i<bounds.length; i++){
         numColors *= bounds[i];
      }
      Color[] pix = new Color[numColors];
      for(int i = 0; i<numColors; i++){
         pix[i] = backgroundC;
      }
      LinkedList<Simplex> original = new LinkedList<Simplex>();
      for(Mesh obj: o){
         Simplex[] faces = obj.getFaces();
         for(Simplex face: faces){
            original.add(face);
         }
      }
      //cull by bounding box?
      
      LinkedList<Simplex> projected = new LinkedList<Simplex>();
      //for each simplex, project points
      for(Simplex current: original){
         //null check add
         Simplex tempFace = projectSimplex(current);
         if(tempFace != null){
            projected.add(tempFace); 
         }
      }
      //z-buffering and painting
      zBufferArrayTexture zBuff = new zBufferArrayTexture(pix,bounds);
      for(Simplex current: projected){
         Point[] allPoints = current.getPoints();
         System.out.println(current);
         if(allPoints.length>ms){
            //select ms+1 points to draw (triangles)
            int[] selectedPoints = new int[ms+1];
            int pointCount = current.getPoints().length;
            for(int i = 0; i<selectedPoints.length; i++){
               selectedPoints[i] = i;
            }
            boolean cont = true;
            System.out.println(current);
            while(cont){
               System.out.println("drawing triangle");
               //put points in simplex
               Point[] neededPoints = new Point[ms+1];
               Point[] flatPoints = new Point[ms+1];
               for(int i = 0; i<selectedPoints.length; i++){
                  //has depth
                  neededPoints[i] = allPoints[selectedPoints[i]];
                  //check pos
                  float[] pixCoords = new float[allPoints[0].getCoords().length-1];
                  for(int j = 0; j<pixCoords.length; j++){
                     pixCoords[j] = allPoints[selectedPoints[i]].getCoords()[j];
                  }
                  flatPoints[i] = new Point(pixCoords);
               }
               Simplex currentPart = new Simplex(neededPoints);
               Simplex flatCurrentPart = new Simplex(flatPoints);
               //restirctions
               float[] projBoundingBoxMax = new float[ms];
               float[] projBoundingBoxMin = new float[ms];
               for(int i = 0; i<bounds.length; i++){
                  projBoundingBoxMax[i] = bounds[i]/2;
                  projBoundingBoxMin[i] = -bounds[i]/2;
               }
               boolean hasPix = true;
               float[] pixPos = new float[ms];
               for(int i = 0; i<pixPos.length; i++){
                  pixPos[i] = projBoundingBoxMin[i];
               }
               drawLoop:while(hasPix){
                  Point pixPoint = new Point(pixPos);
                  //draw pixel
                  Vector bary = flatCurrentPart.getBaryCentricCoords(pixPoint);
                  //is in triangle
                  if(bary == null){
                     pixPos = incrementArray(pixPos, projBoundingBoxMax, projBoundingBoxMin, pixPos.length-1);
                     hasPix = pixPos != null;
                     continue drawLoop;
                  }
                  for(int i = 0; i<bary.length(); i++){
                     if(bary.getCoords()[i] < 0 || bary.getCoords()[i] > 1){
                        continue drawLoop;
                     }
                  }
                  //get color
                  Color pixColor = currentPart.getColor(bary.getCoords());
                  //get depth
                  Vector actualPoint = new Vector(new float[bary.length()]);
                  for(int i = 0; i<currentPart.getPoints().length; i++){
                     actualPoint.add((new Vector(currentPart.getPoints()[i].getCoords())).scale(bary.getCoords()[i]));
                  }
                  Point zbuffPoint = new Point(actualPoint.getCoords());
                  zBuff.setColor(zbuffPoint, pixColor);
                  //next pixel
                  pixPos = incrementArray(pixPos, projBoundingBoxMax, projBoundingBoxMin, pixPos.length-1);
                  hasPix = pixPos != null;
               }
               selectedPoints = shiftSelected(selectedPoints, pointCount, selectedPoints.length-1);
               cont = selectedPoints != null;
               
            }
            
         } else if (allPoints.length == 0){
            //no points to draw
            
         }else {
            //less points (e.g. line or point)
            Point[] neededPoints = new Point[allPoints.length];
            Point[] flatPoints = new Point[allPoints.length];
            for(int i = 0; i<allPoints.length; i++){
                  //has depth
               neededPoints[i] = allPoints[i];
                  //check pos
               float[] pixCoords = new float[allPoints[0].getCoords().length];
               for(int j = 0; j<pixCoords.length; j++){
                  pixCoords[j] = allPoints[i].getCoords()[j];
               }
               flatPoints[i] = new Point(pixCoords);
            }
            Simplex currentPart = new Simplex(neededPoints);
            Simplex flatCurrentPart = new Simplex(flatPoints);
            float[] projBoundingBoxMax = new float[ms];
            float[] projBoundingBoxMin = new float[ms];
            for(int i = 0; i<bounds.length; i++){
               projBoundingBoxMax[i] = bounds[i];
               projBoundingBoxMin[i] = bounds[i];
            }
            boolean hasPix = true;
            float[] pixPos = new float[ms];
            for(int i = 0; i<pixPos.length; i++){
               pixPos[i] = projBoundingBoxMin[i];
            }
            Point pixPoint = new Point(pixPos);
            drawLoop:while(hasPix){
                  //draw pixel
               Vector bary = flatCurrentPart.getBaryCentricCoords(pixPoint);
                  //is in triangle
               if(bary == null){
                  continue drawLoop;
               }
               for(int i = 0; i<bary.length(); i++){
                  if(bary.getCoords()[i] < 0 || bary.getCoords()[i] > 1){
                     continue drawLoop;
                  }
               }
                  //get color
               Color pixColor = currentPart.getColor(bary.getCoords());
                  //get depth
               Vector actualPoint = new Vector(new float[bary.length()]);
               for(int i = 0; i<currentPart.getPoints().length; i++){
                  actualPoint.add((new Vector(currentPart.getPoints()[i].getCoords())).scale(bary.getCoords()[i]));
               }
               Point zbuffPoint = new Point(actualPoint.getCoords());
               zBuff.setColor(zbuffPoint, pixColor);
               pixPos = incrementArray(pixPos, projBoundingBoxMax, projBoundingBoxMin, pixPos.length-1);
               hasPix = pixPos != null;
            }
         }
      }
      return zBuff.getArrayTexture();
   }
   protected Simplex projectSimplex(Simplex s){
      ArrayList<Point> newPoints = new ArrayList<Point>();
      ArrayList<Point> corrispond = new ArrayList<Point>();
      //get simplex slice
      reCalculateMatrix(s);
      System.out.println(m);
      System.out.println(sol);
      
      //get basic fesable solution https://en.wikipedia.org/wiki/Basic_feasible_solution
      int numUnknowns = m.getWidth();
      int maxUnknowns = m.getHeight();
      //just in case, prove this is correct later
      if(numUnknowns == 0){
         return null;
      }
      float[][] data = m.getData();
      //todo - symplify matrix
      //if is already square
      boolean moreBasis = true;
      while(moreBasis){
         
      }
      
      Point[] tempPoints = new Point[newPoints.size()];
      tempPoints = newPoints.toArray(tempPoints);
      Simplex resultSimplex;
      if(tempPoints.length>0){
         resultSimplex = new Simplex(tempPoints);
      } else {
         return null;
      }
      System.out.println(resultSimplex);
      //set texture
      resultSimplex.setTexture(s.getTexture());
      LinkedList<Point> shiftedTexturePoints = new LinkedList<Point>();
      for(int i = 0; i<tempPoints.length; i++){
         Vector target = new Vector(corrispond.get(i).getCoords());
         Vector newTextPoint = new Vector(new float[s.getTexture().getBounds().length]);
         for(int j = 0; j<g; j++){
            newTextPoint.add(new Vector(s.getTexturePoints()[j].getCoords()).scale(target.getCoords()[j]));
         }
         shiftedTexturePoints.add(new Point(newTextPoint.getCoords()));
      }
      tempPoints = new Point[shiftedTexturePoints.size()];
      tempPoints = shiftedTexturePoints.toArray(tempPoints);
      resultSimplex.setTexturePoints(tempPoints);
      return resultSimplex;
   }
   protected int[] shiftSelected(int[] selected, int maximum, int index){
      selected[index]++;
      int len = selected.length;
      if(selected[index] == (maximum-len+index+1)){
         if(index != 0){
            selected = shiftSelected(selected, maximum, index-1);
            if(selected != null){
               selected[index] = selected[index-1]+1;
            }
         } else {
            return null;
         }
      }  
      return selected;
   }
   protected float[] incrementArray(float[] arr, float[] max, float[] min, int index){
      if(index == -1){
         return arr;
      }
      arr[index]++;
      if(arr[index]>= max[index]){
         if(index == 0){
            return null;
         }
         arr[index] = min[index];
         arr = incrementArray(arr, max, min, index-1);
      }
      return arr;
   }
   protected static ArrayList<Vector> LPMaximums(){
      ArrayList<Vector> result = new ArrayList<Vector>();
      
      return result;
   }
}