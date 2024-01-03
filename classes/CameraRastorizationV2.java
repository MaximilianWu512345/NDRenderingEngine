import java.awt.Color;
import java.util.*;
import java.lang.*;
public class CameraRastorizationV2 implements Camera{
   protected Point c;
   protected AffineSubspace s;
   protected Matrix m;
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
      g = s.getPoints().length-1;
      ms = this.s.getSubSpace().getDir().length;
      n = c.length();
      int numCol = 2*ms+2*g+3;
      int numRow = n+g+1;
      float[][] data = new float[numRow][numCol];
      int currentCol = 0;
      Point[] newData = s.getPoints();
      Vector[] v = new Vector[newData.length-1];
      Vector[] u = this.s.getSubSpace().getDir();
      Point p = newData[0];
      Vector exten = new Vector(c, this.s.getPoint());
      Vector constShift = new Vector(p,this.s.getPoint());
      for(int i = 1; i<newData.length; i++){
         v[i-1] = new Vector(p, newData[i]);
      }
      //u vectors positive
      int start = currentCol;
      while(currentCol-start < ms){
         //add current vector
         for(int i = 0; i<n; i++){
            data[i][currentCol] = u[currentCol-start].getCoords()[i];
         }
         currentCol++;
      }
      //extention vector
      for(int i = 0; i<n; i++){
         data[i][currentCol] = exten.getCoords()[i];
      }
      currentCol++;
      //u vectors negative
      start = currentCol;
      while(currentCol-start < ms){
         //add negative vector
         for(int i = 0; i<n; i++){
            data[i][currentCol] = -1*u[currentCol-start].getCoords()[i];
         }
         currentCol++;
      }
      //v vectors
      start = currentCol;
      while(currentCol-start < g){
         //add current vector
         for(int i = 0; i<n; i++){
            data[i][currentCol] = v[currentCol-start].getCoords()[i];
         }
         data[n+1][currentCol] = 1;
         currentCol++;
      }
      //alpha restrict (in simplex)
      start = currentCol;
      while(currentCol-start<g){
         data[n+1][currentCol] = -1;
         currentCol++;
      }
      //const
      for(int i = 0; i<n; i++){
         data[i][currentCol] = constShift.getCoords()[i];
      }
      data[numRow-1][currentCol] = 1;
      currentCol++;
      m = new Matrix(data);
      //remove reduntant bases later
      //generate solution
      float[] result = new float[numRow];
      result[n+1] = 1;
      result[numRow-1] = 1;
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
         if(allPoints.length>ms){
            //select ms+1 points to draw (triangles)
            int[] selectedPoints = new int[ms+1];
            int pointCount = current.getPoints().length;
            for(int i = 0; i<pointCount; i++){
               selectedPoints[i] = i;
            }
            boolean cont = true;
            while(cont){
               //put points in simplex
               Point[] neededPoints = new Point[ms+1];
               Point[] flatPoints = new Point[ms+1];
               for(int i = 0; i<selectedPoints.length; i++){
                  //has depth
                  neededPoints[i] = allPoints[selectedPoints[i]];
                  //check pos
                  float[] pixCoords = new float[allPoints[0].getCoords().length];
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
                  projBoundingBoxMax[i] = bounds[i];
                  projBoundingBoxMin[i] = bounds[i];
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
                  hasPix = pixPos == null;
               }
               selectedPoints = shiftSelected(selectedPoints, pointCount, selectedPoints.length-1);
               cont = selectedPoints == null;
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
            drawLoop:while(hasPix){
               Point pixPoint = new Point(pixPos);
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
      if(maxUnknowns >= numUnknowns){
         //solve System
         Matrix currentEq = new Matrix(data);
         //solve part
         Vector partSolution = currentEq.solve(sol);
         if(partSolution == null){
            return null;
         }
         //get rest
         float[] newPointData = new float[numUnknowns];
         for(int i = 0; i<numUnknowns; i++){
            newPointData[numUnknowns] = partSolution.getCoords()[i];
         }
         Vector mixedSolution = new Vector(newPointData);
         float[] mixedSolData = mixedSolution.getCoords();
         //fix format
         float[] fixedSolData = new float[ms+1];
         float[] corrispondVectors = new float[g];
         //camera vectors
         fixedSolData[ms] = mixedSolData[ms];
         for(int i = 0; i<ms; i++){
            fixedSolData[i] = (mixedSolData[i] + mixedSolData[i+ms+1])/mixedSolData[ms];
         }
         //simplex vectors
         for(int i = 2*ms+1; i<2*ms+g+1; i++){
            corrispondVectors[i-2*ms-1] = mixedSolData[i];
         }
         newPoints.add(new Point(fixedSolData));
         corrispond.add(new Point(corrispondVectors));
      } else {
         int[] selectedCol = new int[maxUnknowns];
         for(int i = 0; i<selectedCol.length; i++){
            selectedCol[i] = i;
         }
         boolean cont = true;
         while(cont){
            float[][] selectedData = new float[selectedCol.length][selectedCol.length];
            for(int i = 0; i<maxUnknowns; i++){
               int col = selectedCol[i];
            //add to matrix
               for(int j = 0; j<maxUnknowns; j++){
                  selectedData[j][i] = data[j][col];
               }
            }
         
            Matrix currentEq = new Matrix(selectedData);
         //solve part
            Vector partSolution = currentEq.solve(sol);
            if(partSolution == null){
               selectedCol = shiftSelected(selectedCol, maxUnknowns, selectedCol.length-1);
               cont = selectedCol == null;     
               continue;
            }
         //get rest
            float[] newPointData = new float[numUnknowns];
            for(int i = 0; i<selectedCol.length; i++){
               newPointData[selectedCol[i]] = partSolution.getCoords()[i];
            }
            Vector mixedSolution = new Vector(newPointData);
            float[] mixedSolData = mixedSolution.getCoords();
         //fix format
            float[] fixedSolData = new float[ms+1];
            float[] corrispondVectors = new float[g];
         //camera vectors
            fixedSolData[ms] = mixedSolData[ms];
            for(int i = 0; i<ms; i++){
               fixedSolData[i] = (mixedSolData[i] + mixedSolData[i+ms+1])/mixedSolData[ms];
            }
         //simplex vectors
            for(int i = 2*ms+1; i<2*ms+g+1; i++){
               corrispondVectors[i-2*ms-1] = mixedSolData[i];
            }
            newPoints.add(new Point(fixedSolData));
            corrispond.add(new Point(corrispondVectors));
         //change selection
            selectedCol = shiftSelected(selectedCol, maxUnknowns, selectedCol.length-1);
            cont = selectedCol == null;            
         }
      }
      Point[] tempPoints = new Point[newPoints.size()];
      tempPoints = newPoints.toArray(tempPoints);
      Simplex resultSimplex;
      if(tempPoints.length>0){
         resultSimplex = new Simplex(tempPoints);
      } else {
         return null;
      }
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
      if(selected[index] == (maximum-len+index)){
         if(index != 0){
            shiftSelected(selected, maximum, index-1);
            selected[index] = selected[index-1]+1;
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
}