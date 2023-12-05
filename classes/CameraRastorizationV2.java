import java.awt.Color;
import java.util.*;
import java.lang.*;
public class CameraRastorizationV2 implements Camera{
   private Point c;
   private AffineSubspace s;
   private Matrix m;
   private Vector sol;
   private int[] bounds;
   private final float EXISTS = 1;
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
      int g = c.length()-1;
      int ms = this.s.getSubSpace().getDir().length;
      int n = c.length();
      int numCol = 2*ms+2*g+2;
      int numRow = n+g+1;
      float[][] data = new float[numCol][numRow];
      int currentCol = 0;
      Point[] newData = s.getPoints();
      Vector[] v = new Vector[newData.length-1];
      Vector[] u = this.s.getSubSpace().getDir();
      Point p = newData[0];
      Vector exten = new Vector(c, this.s.getPoint());
      Vector constShift = new Vector(p,this.s.getPoint());
      for(int i = 1; i<newData.length; i++){
         v[i-1] = new Vector(p, this.s.getPoint());
      }
      //u vectors positive
      int start = currentCol;
      while(currentCol-start < ms){
         //add current vector
         for(int i = 0; i<n; i++){
            data[currentCol][i] = u[currentCol-start].getCoords()[i];
         }
         currentCol++;
      }
      //extention vector
      for(int i = 0; i<n; i++){
         data[currentCol][i] = exten.getCoords()[i];
      }
      currentCol++;
      //u vectors negative
      start = currentCol;
      while(currentCol-start < ms){
         //add negative vector
         for(int i = 0; i<n; i++){
            data[currentCol][i] = -1*u[currentCol-start].getCoords()[i];
         }
         currentCol++;
      }
      //v vectors
      start = currentCol;
      while(currentCol-start < g){
         //add current vector
         for(int i = 0; i<n; i++){
            data[currentCol][i] = v[currentCol-start].getCoords()[i];
         }
         data[currentCol][n+(start-currentCol)] = 1;
         currentCol++;
      }
      //alpha restrict (in simplex)
      start = currentCol;
      while(currentCol-start<g){
         data[currentCol][n+(start-currentCol)] = 1;
         currentCol++;
      }
      //const
      for(int i = 0; i<n; i++){
         data[currentCol][i] = constShift.getCoords()[i];
      }
      data[currentCol][numRow-1] = 1;
      currentCol++;
      m = new Matrix(data);
      //remove reduntant bases later
      //generate solution
      float[] result = new float[numRow];
      for(int i = n; i<numRow; i++){
         result[i] = 1;
      }
      sol = new Vector(result);
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
      //cull by bounding box?
      
      //calculate vertex positions, let 1 be exists, let -1 be doesn't exist
      LinkedList<Simplex> projected = new LinkedList<Simplex>();
      //for each simplex, project points
      for(Simplex current: original){
         projected.add(projectSimplex(current)); 
      }
      //z-buffering
      //paint
      return null;
   }
   private Simplex projectSimplex(Simplex s){
      ArrayList<Point> newPoints = new ArrayList<Point>();
      //get simplex slice
      reCalculateMatrix(s);
   
      //get basic fesable solution https://en.wikipedia.org/wiki/Basic_feasible_solution
      int numUnknowns = m.getWidth();
      int maxUnknowns = m.getHeight();
      //just in case, prove this is correct later
      if(numUnknowns == 0){
         return null;
      }
      //todo - symplify matrix
      //if is already square
      if(maxUnknowns >= numUnknowns){
         //solve System
         return null;
      }
      int[] selectedCol = new int[numUnknowns];
      for(int i = 0; i<selectedCol.length; i++){
         selectedCol[i] = i;
      }
      boolean cont = true;
      float[][] data = m.getData();
      while(cont){
         float[][] selectedData = new float[selectedCol.length][selectedCol.length];
         for(int i = 0; i<numUnknowns; i++){
            int col = selectedCol[i];
            //add to matrix
            for(int j = 0; j<maxUnknowns; j++){
               selectedData[i][maxUnknowns] = data[col][maxUnknowns];
            }
         }
         
         Matrix currentEq = new Matrix(selectedData);
         //solve
         Vector mixedSolution = currentEq.solve(sol);
         //fix format
         
         //change selection
         selectedCol = shiftSelected(selectedCol, maxUnknowns, selectedCol.length-1);
         cont = selectedCol == null;            
      }
      
      
      return null;
      //just in case 
      /*
      Matrix aug = m.AugmentedMatrix(sol);
      Matrix rref = aug.getRREF();
      Vector[] allEq = rref.toVectors();
      //remove zero vectors and check for impossible
      LinkedList<Vector> fixedEq = new LinkedList<Vector>();
      for(Vector eq: allEq){
         float[] eqCoeff = eq.getCoords();
         float sum = 0;
         boolean isZero = true;
         for(float coeff: eqCoeff){
            sum += coeff;
            isZero = isZero && (coeff==0f);
         }
         if(!isZero) {
            if(sum == eqCoeff[eqCoeff.length-1]){
               return null;
            }  
            fixedEq.add(eq);
         }
      }
      Vector[] temp = new Vector[fixedEq.size()];
      temp = fixedEq.toArray(temp);
      //seperate m and b
      float[][] fixedData = new float[fixedEq.size()-1][m.getWidth()];
      float[] fixedRes = new float[m.getWidth()];
      {// just limiting scope to make things easier
         int j = 0;
         for(Vector eq: fixedEq){
            for(int i = 0; i<eq.length(); i++){
               if(i+1 = eq.length()){
                  fixedRes[j] = eq.toCoords()[i];
               } else {
                  fixedData[j][i] = eq.toCoords()[i];
               }
            }
            j++;
         }
      }
      Matrix mf = new Matrix(fixedData);
      Vector bf = new Vector(fixedRes);
      */
   }
   private int[] shiftSelected(int[] selected, int maximum, int index){
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
}