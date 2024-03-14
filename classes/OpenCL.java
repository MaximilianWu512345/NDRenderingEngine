import static org.jocl.CL.*;

import org.jocl.*;

import java.util.Arrays;
import java.util.HashMap;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Scanner;

public class OpenCL {

   public static final String Directory = "OpenCL/";
   
   public static HashMap<String, String> Map_FileName_File;

   public static cl_command_queue CommandQueue;
   public static cl_context Context;
  
   public static cl_command_queue getCommandQueue() {
      CheckForInit();
      return CommandQueue;
   }
   
   public static cl_context GetContext() {
      CheckForInit();
      return Context;
   }
  
   public static void CheckForInit() {
      if (Map_FileName_File == null)
         init();
   }
   
   public static void init() {
      Map_FileName_File = new HashMap<String, String>();
      File directory = new File(Directory);
      File[] directoryFiles = directory.listFiles(
         new FilenameFilter() {
            public boolean accept(File dir, String name) {
               return name.endsWith(".c");
            }});
      for (File f : directoryFiles) {
         Map_FileName_File.put(f.getName(), GetFileContents(f));
      }
      // The platform, device type and device number
      // that will be used
      final int platformIndex = 0;
      final long deviceType = CL_DEVICE_TYPE_ALL;
      final int deviceIndex = 0;
   
      // Enable exceptions and subsequently omit error checks in this sample
      CL.setExceptionsEnabled(true);
   
      // Obtain the number of platforms
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
   
      // Create a context for the selected device
      Context = clCreateContext(
         contextProperties, 1, new cl_device_id[]{device}, 
         null, null, null);
      
      // Create a command-queue for the selected device
      cl_queue_properties properties = new cl_queue_properties();
      CommandQueue = clCreateCommandQueueWithProperties(
         Context, device, properties, null);
   }
   
   public static String GetFileContents(File file) {
      try {
         Scanner reader = new Scanner(file);
         String s = "";
         while (reader.hasNextLine()) {
            s += reader.nextLine();
         }
         reader.close();
         return s;
      }
      catch (Exception e) {
         System.out.println(e);
      }
      return null;
   }
   
   public static String ReadFile(String fileName) {
      CheckForInit();
      if (Map_FileName_File.containsKey(fileName))
         return Map_FileName_File.get(fileName);
      System.out.println("File not found: " + fileName);
      return "";
   }
   
   public static long SetupData(Pointer[] pointers, cl_mem[] memory, int index, long memType, Object o) {
      if (o instanceof byte[]) {
         pointers[index] = Pointer.to((byte[])o);
         Pointer p = memType == CL_MEM_READ_WRITE ? null : pointers[index];
         memory[index] = clCreateBuffer(Context, memType, Sizeof.cl_char * ((byte[])o).length, p, null);
         return Sizeof.cl_char * ((byte[])o).length;
      }
      if (o instanceof char[]) {
         pointers[index] = Pointer.to((char[])o);
         Pointer p = memType == CL_MEM_READ_WRITE ? null : pointers[index];
         memory[index] = clCreateBuffer(Context, memType, Sizeof.cl_char * ((char[])o).length, p, null);
         return Sizeof.cl_char * ((char[])o).length;
      }
      if (o instanceof short[]) {
         pointers[index] = Pointer.to((short[])o);
         Pointer p = memType == CL_MEM_READ_WRITE ? null : pointers[index];
         memory[index] = clCreateBuffer(Context, memType, Sizeof.cl_short * ((short[])o).length, p, null);
         return Sizeof.cl_short * ((short[])o).length;
      }
      if (o instanceof int[]) {
         pointers[index] = Pointer.to((int[])o);
         Pointer p = memType == CL_MEM_READ_WRITE ? null : pointers[index];
         memory[index] = clCreateBuffer(Context, memType, Sizeof.cl_int * ((int[])o).length, p, null);
         return Sizeof.cl_int * ((int[])o).length;
      }
      if (o instanceof float[]) {
         pointers[index] = Pointer.to((float[])o);
         Pointer p = memType == CL_MEM_READ_WRITE ? null : pointers[index];
         memory[index] = clCreateBuffer(Context, memType, Sizeof.cl_float * ((float[])o).length, p, null);
         return Sizeof.cl_float * ((float[])o).length;
      }
      if (o instanceof long[]) {
         pointers[index] = Pointer.to((long[])o);
         Pointer p = memType == CL_MEM_READ_WRITE ? null : pointers[index];
         memory[index] = clCreateBuffer(Context, memType, Sizeof.cl_long * ((long[])o).length, p, null);
         return Sizeof.cl_long * ((long[])o).length;
      }
      if (o instanceof double[]) {
         pointers[index] = Pointer.to((double[])o);
         Pointer p = memType == CL_MEM_READ_WRITE ? null : pointers[index];
         memory[index] = clCreateBuffer(Context, memType, Sizeof.cl_double * ((double[])o).length, p, null);
         return Sizeof.cl_double * ((double[])o).length;
      }
      /*
      if (o instanceof Buffer) {
      
      }
      if (o instanceof NativePointerObject) {
      
      }
      if (o instanceof NativePointerObject...) {
      
      }
      */
      return 0;
   }
   
   public static void RunFile(String fileName, String kernelName, int globalWorkSize, Object[] readArgs, Object[] writeArgs)
   {
      String programSource = ReadFile(fileName);
      // Create input- and output data
      Pointer[] pointers = new Pointer[readArgs.length + writeArgs.length];
      cl_mem[] memory = new cl_mem[pointers.length];
      long[] memSize = new long[pointers.length];
      for (int i = 0; i < pointers.length; i++) {
         if (i >= readArgs.length)
            memSize[i] = SetupData(pointers, memory, i, CL_MEM_READ_WRITE, writeArgs[i - readArgs.length]);
         else
            memSize[i] = SetupData(pointers, memory, i, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, readArgs[i]);
      }
      // Create the program from the source code
      cl_program program = clCreateProgramWithSource(Context,
         1, new String[]{ programSource }, null, null);
      
      // Build the program
      clBuildProgram(program, 0, null, null, null, null);
      
      // Create the kernel
      cl_kernel kernel = clCreateKernel(program, kernelName, null);
      
      // Set the arguments for the kernel
      int a = 0;
      for (cl_mem m : memory) {
         clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(m));
      }
      
      // Set the work-item dimensions
      long global_work_size[] = new long[]{globalWorkSize};
      
      // Execute the kernel
      clEnqueueNDRangeKernel(CommandQueue, kernel, 1, null,
         global_work_size, null, 0, null, null);
      
      for (int i = readArgs.length; i < pointers.length; i++) {
         clEnqueueReadBuffer(CommandQueue, memory[i], CL_TRUE, 0,
            memSize[i], pointers[i], 0, null, null);
      }
      // Release kernel, program, and memory objects
      for (cl_mem m : memory)
         clReleaseMemObject(m);
      clReleaseKernel(kernel);
      clReleaseProgram(program);
      /* - Will crash program when running twice
      clReleaseCommandQueue(CommandQueue);
      clReleaseContext(Context);
      */
   }
   
   public static cl_kernel GetKernel(String fileName, String kernelName) {
      CheckForInit();
      String programSource = ReadFile(fileName);
      cl_program program = clCreateProgramWithSource(Context,
            1, new String[]{ programSource }, null, null);
   
        // Build the program
      System.out.println("Building program...");
      clBuildProgram(program, 0, null, null, null, null);
   
        // Create the kernel
      System.out.println("Creating kernel...");
      return clCreateKernel(program, kernelName, null);
   }
}