public class Camera{
   public Point position;
   public Vector direction;
   public float width;
   public float height;
   
   public Camera (Point position, Vector V, float width, float Height){
      setData(position, V, width, Height);
   }
   public setData(Point position, Vector V, float width, float Height){
      this.position = position;
      this.direction = V;
      this.width;
      this.height;
   }
   public Mesh[] Project(Mesh[] o){
      //convert to lower dimention
      //loop through all meshes
      Arraylist<Simplex> simplexes = new Arraylist();
      for(int i = 0; i<o.length; i++){
         Mesh t = o[i];
         //loop through all simplexes
         for(int j = 0; j<mesh.faces.length; j++){
            //add to list of simplexes
            simplexes.add(mesh.faces[j]);
         }
      }
      //resolve Intersections
      //resolve Overlaps
      //find new coords
      //loop though all simplexes
      for(int i = 0; i<simplexes.length; i++){
         
         //loop through all vertexes
         for(int j = 0; j<sim)
      }
      //is right dimention?
      //if not make new camera and scene
      //repeat
      return null;
   }
}