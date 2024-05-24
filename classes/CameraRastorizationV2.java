import static org.jocl.CL.*;
import org.jocl.*;
import java.awt.Color;
import java.util.*;
import java.lang.*;
import java.io.File;
public class CameraRastorizationV2 implements Camera{
   protected Point c;
   protected AffineSubSpace s;
   protected Matrix m;
   Vector[] v; //could be removeable
   Vector[] u;//could be removeable
   Vector[] negu;//could be removeable
   Vector exten;//could be removeable
   Vector constShift; //could be removeable
   protected Vector sol;
   protected int[] bounds;
   protected final float EXISTS = 1;
   protected int g = 0;
   protected int ms = 0;
   protected int n = 0;
   public static final boolean useGPU = true;
   private static final String GPU_CODE_LOC = "OpenCL\\ProjectGPUFunc.c";
   private static final String GPU_KERNEL_LOC1 = "RaserizeStep1";
   private static final String GPU_KERNEL_LOC2 = "RaserizeStep2";
   private static String GPUCode;
   private static cl_command_queue commandQueue;
   private static cl_context context;
   private static cl_program program;
   private cl_kernel rasterS1;
   private cl_kernel rasterS2;
   private Pointer outPointer;
   private byte[] out;
   private static boolean hasConnected = false;
   private boolean hasBuilt = false;
   public CameraRastorizationV2 (Point c, AffineSubSpace s, int[] bounds){
      this.s = s;
      this.c = c;
      this.bounds = bounds;
   }
   
   public void translate(Point position) {
      c.translate(position);
      s.translate(position);
   }
   
   // translate camera so c is at (0, 0, 0, 0, ..., 0)
   // rotate point and vectos of s
   // translate camera so c is at it's original position
   public void rotate(float theta, int axis1, int axis2) {
      Point inverse = c.getInverse();
      s.translate(inverse);
      Matrix rotation = Matrix.GivensRot(getDimension(), theta, axis1, axis2);
      s.setPoint(rotation.mult(s.getPoint().toMatrix()).toPoint());
      for (int i = 0; i < s.getSubSpace().getDir().length; i++) {
         s.getSubSpace().getDir()[i] = s.getSubSpace().getDir()[i].rotBy(rotation);
      }
   }
   
   public int getDimension() {
      return c.getCoordinates().length;
   }
   
/**
* Sets camera data.
* @param position the new position of the camera.
* @param v the new direction of the camera.
* @param w the new width of the camera.
* @param h the new height of the camera.
*/
   public void setData(Point position, AffineSubSpace s){
      c = position;
      this.s = s;
   }
   /*implement later*/
   public void setData(Point position, Vector V, int width, int height){
   
   }
   public void reCalculateMatrix(Simplex s){
      g = s.getPoints().length;
      ms = this.s.getSubSpace().getDir().length;
      n = c.length();
      int numCol = 2*ms+g+1;
      int numRow = n+1;
      float[][] data = new float[numRow][numCol];
      int currentCol = 0;
      Point[] newData = s.getPoints();
      v = new Vector[newData.length];
      u = new Vector[this.s.getSubSpace().getDir().length];
      for (int i = 0; i < u.length; i++) {
         u[i] = this.s.getSubSpace().getDir()[i];
      }
      negu = new Vector[u.length];
      exten = new Vector(this.s.getPoint(), c);
      constShift = new Vector(c.getCoords());
      for(int i = 0; i<newData.length; i++){
         v[i] = new Vector(newData[i].getCoords());
      }
      float[] temp;
      int start = 0;
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
      //u vectors
      start = currentCol;
      while(currentCol-start < ms){
         //add current vector
         temp = new float[numRow];
         for(int i = 0; i<n; i++){
            data[i][currentCol] = -1*u[currentCol-start].getCoords()[i];
            temp[i] = u[currentCol-start].getCoords()[i];
         }
         u[currentCol-start] = new Vector(temp);
         currentCol++;
      }
      //neg u vectors
      start = currentCol;
      while(currentCol-start < ms){
         //add current vector
         temp = new float[numRow];
         for(int i = 0; i<n; i++){
            data[i][currentCol] = u[currentCol-start].getCoords()[i];
            temp[i] = -1*u[currentCol-start].getCoords()[i];
         }
         negu[currentCol-start] = new Vector(temp);
         currentCol++;
      }
      //extention vector
      temp = new float[numRow];
      for(int i = 0; i<n; i++){
         data[i][currentCol] = exten.getCoords()[i];
         temp[i] = exten.getCoords()[i];
      }
      exten = new Vector(temp);
      m = new Matrix(data);
      //remove reduntant bases later
      //generate solution
      float[] result = new float[numRow];
      
      result[numRow-1] = 1;
      for(int i = 0; i<constShift.length(); i++){
         result[i] = -1*constShift.getCoords()[i];
      }
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
      //System.out.println("resulting " + projected.size() + " simplexes:");
      /*for(Simplex current: projected){
         System.out.println(current);
      }*/
      //z-buffering and painting
      zBufferArrayTexture zBuff = new zBufferArrayTexture(pix,bounds);
      if(useGPU){
         if(!hasConnected){
            GPUConnect();
         }
         //first make kernals
         if(!hasBuilt){
            initCamGPUCon();
         }
         //set arguments
         LinkedList<Simplex> sList = new LinkedList<Simplex>();
         for(Simplex current: projected){
            int[] selectedPoints = new int[ms+1];
            int pointCount = current.getPoints().length;
            for(int i = 0; i<selectedPoints.length; i++){
               selectedPoints[i] = i;
            }
            boolean cont = true;
            while(cont){
               Point[] coords = current.getPoints();
               Point[] tcoords = current.getTexturePoints();
               Texture texture = current.getTexture();
               Point[] newCoords = new Point[selectedPoints.length];
               Point[] newtCoords = new Point[selectedPoints.length];
               for(int i = 0; i< newCoords.length; i++){
                  newCoords[i] = coords[selectedPoints[i]];
                  newtCoords[i] = tcoords[selectedPoints[i]];
               }
               Simplex newS = new Simplex(newCoords);
               newS.setTexture(texture);
               newS.setTexturePoints(newtCoords);
               sList.add(newS);
               selectedPoints = shiftSelected(selectedPoints, pointCount, selectedPoints.length-1);
               cont = selectedPoints != null;
            }
         }
         Simplex[] sim = new Simplex[sList.size()];
         sim = sList.toArray(sim);
         cl_mem memory[] = setMemoryBuffRaster(sim, backgroundC, triangleC, new cl_kernel[]{rasterS1, rasterS2});
         for(int i = 0; i<memory.length; i++){
            clSetKernelArg(rasterS1, i, Sizeof.cl_mem, Pointer.to(memory[i]));
            clSetKernelArg(rasterS2, i, Sizeof.cl_mem, Pointer.to(memory[i]));
         }
         int numPixels = 1;
         for(int b: bounds){
            numPixels *= b;
         }
         //run step 1
         long global_work_size[] = new long[]{sim.length};
         long local_work_size[] = new long[]{1};
         clEnqueueNDRangeKernel(commandQueue, rasterS1, 1, null, global_work_size, local_work_size, 0, null, null);
         //System.out.println(clFinish(commandQueue));
         //run step 2
         global_work_size = new long[]{numPixels};
         local_work_size = new long[]{1};
         clEnqueueNDRangeKernel(commandQueue, rasterS2, 1, null, global_work_size, local_work_size, 0, null, null);
         //System.out.println(clFinish(commandQueue));
         //shadows if we get to it
         //read results
         clEnqueueReadBuffer(commandQueue, memory[7], CL_TRUE, 0, out.length*Sizeof.cl_uchar, outPointer, 0, null, null);
         //System.out.println(clFinish(commandQueue));
         Color[] c = new Color[out.length/3];
         for(int i = 0; i<c.length; i++){
            c[i] = new Color(Byte.toUnsignedInt(out[i*3]), Byte.toUnsignedInt(out[i*3+1]), Byte.toUnsignedInt(out[i*3+2]));
            if(c[i].equals(new Color(255, 0, 0))){
               //System.out.println(i);
            }
         }
         for (cl_mem m : memory)
            clReleaseMemObject(m);
         return new ArrayTexture(c, bounds);
      } else {
         for(Simplex current: projected){
            Point[] allPoints = current.getPoints();
         //System.out.println(current);
            if(allPoints.length>ms){
            //select ms+1 points to draw (triangles)
               int[] selectedPoints = new int[ms+1];
               int pointCount = current.getPoints().length;
               for(int i = 0; i<selectedPoints.length; i++){
                  selectedPoints[i] = i;
               }
               boolean cont = true;
            //System.out.println(current);
               while(cont){
               //System.out.println("drawing triangle");
               //put points in simplex
                  Point[] neededPoints = new Point[ms+1];
                  Point[] flatPoints = new Point[ms+1];
                  for(int i = 0; i<selectedPoints.length; i++){
                  //has depth
                     neededPoints[i] = allPoints[selectedPoints[i]];
                  //check pos
                     float[] pixCoords = new float[allPoints[0].getCoords().length-1];
                     for(int j = 0; j<pixCoords.length; j++){
                        pixCoords[j] = allPoints[selectedPoints[i]].getCoords()[j]/allPoints[selectedPoints[i]].getCoords()[pixCoords.length] ;
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
                     Vector bary = flatCurrentPart.getBarycentricCoords(pixPoint);
                  //is in triangle
                     if(bary == null){
                        pixPos = incrementArray(pixPos, projBoundingBoxMax, projBoundingBoxMin, pixPos.length-1);
                        hasPix = pixPos != null;
                        continue drawLoop;
                     }
                     for(int i = 0; i<bary.length(); i++){
                        if(bary.getCoords()[i] < 0 || bary.getCoords()[i] > 1){
                           pixPos = incrementArray(pixPos, projBoundingBoxMax, projBoundingBoxMin, pixPos.length-1);
                           hasPix = pixPos != null;
                           continue drawLoop;
                        }
                     }
                  //get color
                  
                     Color pixColor = currentPart.getColor(bary.getCoords());
                     if (pixColor == null){
                        pixColor = triangleC;
                     }
                  //get depth
                     float[] actualPointDat = new float[bary.length()];
                     for(int i = 0; i<actualPointDat.length-1; i++){
                        actualPointDat[i] = pixPos[i]-projBoundingBoxMin[i];
                     }
                     for(int i = 0; i<currentPart.getPoints().length; i++){
                        actualPointDat[actualPointDat.length-1] += currentPart.getPoints()[i].getCoords()[actualPointDat.length-1]*bary.getCoords()[i];
                     }
                  
                     Point zbuffPoint = new Point(actualPointDat);
                  //System.out.println("drawing pixel " + zbuffPoint.toString() + "pix Point" + (new Point(pixPos)).toString() + " color " + pixColor.toString());
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
                  Vector bary = flatCurrentPart.getBarycentricCoords(pixPoint);
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
                  //System.out.println("drawing pixel " + zbuffPoint.toString() + "pix Point" + (new Point(pixPos)).toString() + " color " + pixColor.toString());
/* old stuff??
                  float[] zBuffPos = new float[actualPoint.length()];
                  for(int i = 0; i<zBuffPos.length-1; i++){
                     zBuffPos[i] = pixPos[i]-projBoundingBoxMin[i];
                  }
                  zBuffPos[zBuffPos.length-1] = actualPoint.getCoords()[zBuffPos.length-1];
                  Point zbuffPoint = new Point(zBuffPos);
                  //System.out.println("drawing pixel " + zbuffPoint.toString() + " color " + pixColor.toString());
*/
                  zBuff.setColor(zbuffPoint, pixColor);
                  pixPos = incrementArray(pixPos, projBoundingBoxMax, projBoundingBoxMin, pixPos.length-1);
                  hasPix = pixPos != null;
               }
            }
         }
      }
      System.out.println("drawing done");
      return zBuff.getArrayTexture();
   }
   protected Simplex projectSimplex(Simplex s){
      ArrayList<Point> newPoints = new ArrayList<Point>();
      ArrayList<Point> corrispond = new ArrayList<Point>();
      //get simplex slice
      reCalculateMatrix(s);
      //System.out.println(m);
      //System.out.println(sol);
      
      //get basic fesable solution https://en.wikipedia.org/wiki/Basic_feasible_solution
      int numUnknowns = m.getWidth();
      int maxUnknowns = m.getHeight();
      //just in case, prove this is correct later
      if(numUnknowns == 0){
         return null;
      }
      //getPoints
      int[] col = new int[g];
      for(int i = 0; i<g; i++){
         col[i] = i;
      }
      //reformat
      Point[] rawPoints = m.LPMaximum(col, sol);
      
      LinkedList<Point> tempRepPoints = new LinkedList<Point>();
      for(int i = 0; i<rawPoints.length; i++){
         float[] data = new float[ms+1];
         for(int j = 0; j<ms; j++){
            data[j] = rawPoints[i].getCoords()[g+j] - rawPoints[i].getCoords()[g+j+ms];
         }
         data[ms] = rawPoints[i].getCoords()[2*ms+g];
         Point toCheck = new Point(data);
         if(!tempRepPoints.contains(toCheck)){
            tempRepPoints.add(toCheck);
         }
      }
      Point[] tempPoints = new Point[tempRepPoints.size()];
      tempPoints = tempRepPoints.toArray(tempPoints);
      Simplex resultSimplex;
      if(tempPoints.length>0){
         resultSimplex = new Simplex(tempPoints);
      } else {
         return null;
      }
      //System.out.println(resultSimplex);
      //set texture
      resultSimplex.setTexture(s.getTexture());
      if(!s.getTexture().placeMatters()){
         resultSimplex.setTexturePoints(tempPoints);
         return resultSimplex;
      }
      Point[] texturePoints = new Point[tempPoints.length];
      Point[] oldTextPoints = s.getTexturePoints();
      for(int i = 0; i<tempPoints.length; i++){
         Vector newPos = new Vector(new float[oldTextPoints[0].length()]);
         for(int j = 0; j<g; j++){
            newPos = newPos.add((new Vector(oldTextPoints[j].getCoords()).scale(rawPoints[i].getCoords()[j])));
         }
      }
      resultSimplex.setTexturePoints(texturePoints);
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
   protected boolean[] shiftBool(boolean[] arr, int index){
      if(index == -1){
         return arr;
      }
      arr[index] = !arr[index];
      if(arr[index] == false){
         if(index == 0){
            return null;
         }
         arr = shiftBool(arr, index-1);
      }
      return arr;
   }
   public static void endConnection(){
      clReleaseProgram(program);
      clReleaseCommandQueue(commandQueue);
      clReleaseContext(context);
   }
   public void close(){   
      clReleaseKernel(rasterS1);
      clReleaseKernel(rasterS2);
   }
   // https://www.codeproject.com/Articles/86551/Part-1-Programming-your-Graphics-Card-GPU-with-Jav
   //static stuff that we only need one of
   public static void GPUConnect(){ 
      GPUCode = OpenCL.GetFileContents(new File(GPU_CODE_LOC));
      
      final int platformIndex = 0;
      final long deviceType = CL_DEVICE_TYPE_GPU;
      final int deviceIndex = 0;
      
      CL.setExceptionsEnabled(true);
      int numPlatformsArray[] = new int[1];
      clGetPlatformIDs(0, null, numPlatformsArray);
      int numPlatforms = numPlatformsArray[0];
   
      // Obtain a platform ID
      cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
      clGetPlatformIDs(platforms.length, platforms, null);
      cl_platform_id platform = platforms[platformIndex];
   
      // Initialize the context properties
      cl_context_properties contextProperties = new cl_context_properties();
      contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
      
      // Obtain the number of devices for the platform
      int numDevicesArray[] = new int[1];
      clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
      int numDevices = numDevicesArray[0];
      
      // Obtain a device ID 
      cl_device_id devices[] = new cl_device_id[numDevices];
      clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
      cl_device_id device = devices[deviceIndex];
      
      //get context
      context = clCreateContext(contextProperties, 1, new cl_device_id[]{device}, null, null, null);         
      
      commandQueue = clCreateCommandQueue(context, devices[0], 0, null);   
      program = clCreateProgramWithSource(context, 1, new String[]{ GPUCode }, null, null);
      clBuildProgram(program, 1, new cl_device_id[]{device}, null, null, null);
      System.out.println(obtainBuildLogs(program, new cl_device_id[]{device}));
      hasConnected = true;
   }
   //non static stuff
   public void initCamGPUCon(){
      cl_kernel temp = clCreateKernel(program, "RaserizeStep1", null);
      rasterS1 = clCreateKernel(program, GPU_KERNEL_LOC1, null);
      rasterS2 = clCreateKernel(program, GPU_KERNEL_LOC2, null);
      hasBuilt = true;
   }
   //gets the memory array
   private cl_mem[] setMemoryBuffRaster(Simplex[] sim, Color background, Color def, cl_kernel[] kernels){
      int numBytes = 0;
      cl_mem[] result = new cl_mem[19];
      int tdim = sim[0].getTexturePoints()[0].length();//input index 25
      int dim = sim[0].getPoints().length;//input index 20
      int numFloat = sim.length*dim*dim;
      float[] coords = new float[numFloat];//input index 0
      int index = 0;
      for(Simplex current: sim){
         for(Point p: current.getPoints()){
            for(float f: p.getCoords()){
               coords[index] = f;
               index++;
            }
         }
      }
      result[0] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float*numFloat, Pointer.to(coords), null);
      numBytes += Sizeof.cl_float*numFloat;
      numFloat = sim.length*dim*tdim;
      float[] tcoords = new float[numFloat];//input index 1
      index = 0;
      for(Simplex current: sim){
         for(Point p: current.getTexturePoints()){
            for(float f: p.getCoords()){
               tcoords[index] = f;
               index++;
            }
         }
      }
      result[1] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float*numFloat, Pointer.to(new int[]{dim}), null);
      numBytes += Sizeof.cl_float*numFloat;
      int[] textureIndex = new int[sim.length]; //input index 2
      HashMap<Texture, Integer> texMap = new HashMap<Texture, Integer>();
      LinkedList<Texture> allTextures = new LinkedList<Texture>();
      index = 0;
      int simIndex = 0;
      for(Simplex current: sim){
         if(texMap.get(current.getTexture()) == null){
            allTextures.add(current.getTexture());
            texMap.put(current.getTexture(), index);
            index++;
         }
         textureIndex[simIndex] =  texMap.get(current.getTexture()).intValue();
         simIndex++;
      }
      result[2] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int*textureIndex.length, Pointer.to(textureIndex), null);
      numBytes += Sizeof.cl_int*textureIndex.length;
      char[][] TextureData = new char[1][allTextures.size()];
      index = 0;
      int textSize = 0;
      for(Texture t: allTextures){
         TextureData[index] = t.toCharArray();
         textSize += TextureData[index].length;
         index++;
      }
      char[] textureColors = new char[textSize]; //input index 3
      index = 0;
      for(char[] line: TextureData){
         for(char c: line){
            textureColors[index] = c;
            index++;
         }
      }
      result[3] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_uchar*textureColors.length, Pointer.to(textureColors), null);
      numBytes += Sizeof.cl_uchar*textureColors.length;
      int[] textureSizes = new int[allTextures.size()*(tdim)]; //input index 4
      index = 0;
      for(Texture t: allTextures){
         for(int v: t.getBounds()){
            textureSizes[index] = v;
            index++;
         }
      }
      result[4] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int*textureSizes.length, Pointer.to(textureSizes), null);
      numBytes += Sizeof.cl_int*textureSizes.length;
      char[] textureType = new char[allTextures.size()]; //input index 5
      index = 0;
      for(Texture t: allTextures){
         if(t instanceof ArrayTexture){
            textureType[index] = 'b';
         } else if(t instanceof ConstentTexture){
            textureType[index] = 'c';
         }
         index++;
      }
      result[5] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_uchar*textureType.length, Pointer.to(textureType), null);
      numBytes += Sizeof.cl_uchar*textureType.length;
      float[] lpuData = new float[sim.length*((dim-1)*(dim-1)*3 + (dim-1))]; //input index 6
      result[6] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float*lpuData.length, Pointer.to(lpuData), null);
      numBytes += Sizeof.cl_float*lpuData.length;
      int numPixels = 1;
      for(int b: bounds){
         numPixels *= b;
      }
      out = new byte[numPixels*3]; // input index 7
      for(int i = 0; i<numPixels; i++){
         out[i*3] = (byte)background.getRed();
         out[i*3+1] = (byte)background.getGreen();
         out[i*3+2] = (byte)background.getBlue();
      }
      outPointer = Pointer.to(out);
      result[7] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_uchar*out.length, outPointer, null);
      numBytes += Sizeof.cl_uchar*out.length;
      float[] zBuff = new float[numPixels]; // input index 8
      for(int i = 0; i<numPixels; i++){
         zBuff[i] = -1;
      }
      result[8] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float*zBuff.length, Pointer.to(zBuff), null);
      numBytes += Sizeof.cl_float*zBuff.length;
      int[] stencilBuff = new int[numPixels]; // input index 9
      result[9] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int*stencilBuff.length, Pointer.to(stencilBuff), null);
      numBytes += Sizeof.cl_int*stencilBuff.length;
      result[10] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int*bounds.length, Pointer.to(bounds), null);
      numBytes += Sizeof.cl_int*bounds.length;
      float[] fCoords = new float[sim.length*(dim-1)*dim];
      result[11] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float*fCoords.length, Pointer.to(fCoords), null);
      numBytes += Sizeof.cl_float*fCoords.length;
      float[] dat = new float[numPixels*(dim)];
      result[12] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float*dat.length, Pointer.to(dat), null);
      numBytes += Sizeof.cl_float*dat.length;
      float[] sol = new float[numPixels*(dim)];
      result[13] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float*sol.length, Pointer.to(sol), null);
      numBytes += Sizeof.cl_float*sol.length;
      float[] pixPos = new float[numPixels*(dim-1)];
      result[14] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int*pixPos.length, Pointer.to(pixPos), null);
      numBytes += Sizeof.cl_float*pixPos.length;
      float[] found = new float[numPixels*(dim)];
      result[15] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float*found.length, Pointer.to(found), null);
      numBytes += Sizeof.cl_float*found.length;
      float[] texPos = new float[numPixels*(tdim)];
      result[16] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float*texPos.length, Pointer.to(texPos), null);
      numBytes += Sizeof.cl_float*texPos.length;
      int[] texPosRound = new int[numPixels*(tdim)];
      result[17] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int*texPosRound.length, Pointer.to(texPosRound), null);
      numBytes += Sizeof.cl_int*texPosRound.length;
      float[] temp = new float[numPixels*(dim)];
      result[18] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float*texPosRound.length, Pointer.to(temp), null);
      numBytes += Sizeof.cl_float*texPosRound.length;
      //set constants 19-end
      //use https://stackoverflow.com/questions/13672575/pass-int-as-kernel-argument-in-jocl
      index = 19;
      for(cl_kernel kern: kernels){
         clSetKernelArg(kern, index, Sizeof.cl_int, Pointer.to(new int[]{tdim}));
      }
      numBytes += Sizeof.cl_int;
      index++;
      for(cl_kernel kern: kernels){
         clSetKernelArg(kern, index, Sizeof.cl_int, Pointer.to(new int[]{dim}));
      }
      numBytes += Sizeof.cl_int;
      index++;
      for(cl_kernel kern: kernels){
         clSetKernelArg(kern, index, Sizeof.cl_uchar, Pointer.to(new char[]{(char)def.getRed()}));
      }
      numBytes += Sizeof.cl_uchar;
      index++;
      for(cl_kernel kern: kernels){
         clSetKernelArg(kern, index, Sizeof.cl_uchar, Pointer.to(new char[]{(char)def.getGreen()}));
      }
      numBytes += Sizeof.cl_uchar;
      index++;
      for(cl_kernel kern: kernels){
         clSetKernelArg(kern, index, Sizeof.cl_uchar, Pointer.to(new char[]{(char)def.getBlue()}));
      }
      numBytes += Sizeof.cl_uchar;
      index++;
      for(cl_kernel kern: kernels){
         clSetKernelArg(kern, index, Sizeof.cl_int, Pointer.to(new int[]{sim.length}));
      }
      numBytes += Sizeof.cl_int;
      index++;
      for(cl_kernel kern: kernels){
         clSetKernelArg(kern, index, Sizeof.cl_int, Pointer.to(new int[]{allTextures.size()}));
      }
      numBytes += Sizeof.cl_int;
      System.out.println(numBytes + " bytes of vram needed");
      return result;
   }
   //code taken from:https://www.tabnine.com/code/java/methods/org.jocl.CL/clGetProgramBuildInfo
   public static String obtainBuildLogs(cl_program program, cl_device_id[] devices)
   {
      StringBuffer sb = new StringBuffer();
      for(int i = 0; i < devices.length; i++)
      {
         sb.append("Build log for device " + i + ":\n");
         long logSize[] = new long[1];
         clGetProgramBuildInfo(program, devices[i],
             CL_PROGRAM_BUILD_LOG, 0, null, logSize);
         byte logData[] = new byte[(int) logSize[0]];
         clGetProgramBuildInfo(program, devices[i],
             CL_PROGRAM_BUILD_LOG, logSize[0], Pointer.to(logData), null);
         sb.append(new String(logData, 0, logData.length - 1));
         sb.append("\n");
      }
      return sb.toString();
   }
}