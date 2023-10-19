public class subSpace{
   protected Vector[] dir;
   protected Point p;
   public subSpace(Point p, Vector[] dir){
      this.p = p;
      this.dir = dir;
   }
   public subSpace(){
   
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
      Matrix rref = aug.getRREF();
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
   public subSpace intersect(subSpace s){
      return null;
   }
   public Point getPos(){
      return p;
   }
   public Vector[] getDir(){
      return dir;
   }
}