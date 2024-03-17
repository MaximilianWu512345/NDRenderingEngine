import java.util.ArrayList;
public class AffineSubSpace{
   protected Vector[] dir;
   protected Point p;
   public AffineSubSpace(Point p, Vector[] dir){
      this.p = p;
      this.dir = dir;
   }
   public AffineSubSpace(){
   
   }
   public boolean isOnSubspace(Point target){
      //guess what - more linear algebra!
      int size = 0;
      if(target.length()>=dir.length){
         size = target.length();
      } else if (dir.length > dir[0].length()){
         size = dir.length;
      } else {
         size = dir[0].length();
      }
      Vector[] coeffents = new Vector[size];
      for(int i = 0; i<size; i++){
         float[] rowData = new float[size];
         for(int j = 0; j<size; j++){
            if(i<dir[j].length()){
               rowData[j] = dir[i].getCoords()[j];
            } else {
               break;
            }
         }
         coeffents[i] = new Vector(rowData);
      }
      Matrix M = new Matrix(coeffents);
      float[] adjtarg = new float[size];
      for(int i = 0; i<size; i++){
         if(i<target.length()){
            adjtarg[i] = target.getCoords()[i] - p.getCoords()[i];
         } else {
            break;
         }
      }
      Matrix aug = M.AugmentedMatrix(new Vector(adjtarg));
      Matrix rref = aug.getRREF().getMatrix();
      Vector[] eq = rref.toVectors();
      for(Vector c:eq){
         if(c.getCoords()[c.length()] != 0){
            boolean sol = false;
            for(float x: c.getCoords()){
               if(x != 0){
                  sol = true;
                  break;
               }
            }
            if(!sol){
               return false;
            }
         }
      }
      return true;
   }
   public AffineSubSpace intersect(AffineSubSpace s){
      //get matrix
      this.simplify();
      s.simplify();
      Vector[] thisSubspaceMatrixData = new Vector[p.length()];
      for(int i = 0; i<thisSubspaceMatrixData.length; i++){
         float[] coordData = new float[dir.length+s.dir.length+2];
         for(int j = 0; j<dir.length; j++){
            coordData[j] = dir[j].getCoords()[i];
         }
         for(int j = 0; j<s.dir.length; j++){
            coordData[j + dir.length] = s.dir[j].getCoords()[i];
         }
         coordData[dir.length+s.dir.length] = p.getCoords()[i];
         coordData[dir.length+s.dir.length+1] = p.getCoords()[i];
         thisSubspaceMatrixData[i] = new Vector(coordData);
      }
      Matrix m = new Matrix(thisSubspaceMatrixData);
      Vector zero = new Vector(new float[p.length()]);
      Matrix aug = m.AugmentedMatrix(zero);
      Matrix rref = m.getRREF().getMatrix();
      //check for impossibility
      return null;
   }
   public Point getPos(){
      return p;
   }
   public Vector[] getDir(){
      return dir;
   }
   //https://dept.math.lsa.umich.edu/~speyer/LinearAlgebraVideos/Lecture3d.pdf
   public void simplify(){
      Matrix m = new Matrix((new Matrix(dir)).getTranspose());
      Matrix rref = m.getRREF().getMatrix();
      Vector[] dat = rref.toVectors();
      ArrayList<Vector> result = new ArrayList<Vector>();
      ArrayList<Integer> indexes = new ArrayList<Integer>();
      int j = 0;
      for(int i = 0; i<dat.length; i++){
         for(int k = j; k<dat[i].length(); k++){
            if(dat[i].getCoords()[k] != 0){
               j = k;
               indexes.add(k);
               break;
            }
         }
      }
      for(int i:indexes){
         if(result.indexOf(dir[i]) == -1){
            result.add(dir[i]);
         }
      }
      dir = new Vector[result.size()];
      dir = result.toArray(dir);
      
   }
}