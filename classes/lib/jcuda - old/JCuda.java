import static jcuda.driver.JCudaDriver.cuCtxCreate;
import static jcuda.driver.JCudaDriver.cuCtxSynchronize;
import static jcuda.driver.JCudaDriver.cuDeviceGet;
import static jcuda.driver.JCudaDriver.cuInit;
import static jcuda.driver.JCudaDriver.cuLaunchKernel;
import static jcuda.driver.JCudaDriver.cuMemAlloc;
import static jcuda.driver.JCudaDriver.cuMemFree;
import static jcuda.driver.JCudaDriver.cuMemcpyDtoH;
import static jcuda.driver.JCudaDriver.cuMemcpyHtoD;
import static jcuda.driver.JCudaDriver.cuModuleGetFunction;
import static jcuda.driver.JCudaDriver.cuModuleLoadData;
import static jcuda.nvrtc.JNvrtc.nvrtcCompileProgram;
import static jcuda.nvrtc.JNvrtc.nvrtcCreateProgram;
import static jcuda.nvrtc.JNvrtc.nvrtcDestroyProgram;
import static jcuda.nvrtc.JNvrtc.nvrtcGetPTX;
import static jcuda.nvrtc.JNvrtc.nvrtcGetProgramLog;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.CUcontext;
import jcuda.driver.CUdevice;
import jcuda.driver.CUdeviceptr;
import jcuda.driver.CUfunction;
import jcuda.driver.CUmodule;
import jcuda.driver.JCudaDriver;
import jcuda.nvrtc.JNvrtc;
import jcuda.nvrtc.nvrtcProgram;
import jcuda.LibUtils;

import java.util.HashMap;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Scanner;

/**
 * An example showing how to use the NVRTC (NVIDIA Runtime Compiler) API
 * to compile CUDA kernel code at runtime.
 */
public class JCuda
{
   public static final String Directory_CU = "jcuda/cu";
   
   public static final String Directory_PTX = "jcuda/cu";
   
   public static HashMap<String, String> Map_Filename_PTX;
   
   public static void main(String[] args)
   {
      CompileAllFiles();  
   }
   
   
   // INSTALL NVRTC FIRST
   public static void CompileAllFiles() {
      Map_Filename_PTX = new HashMap<String, String>();
      File directory = new File(Directory_CU);
      File[] directoryFiles = directory.listFiles(
         new FilenameFilter() {
            public boolean accept(File dir, String name) {
               return name.endsWith(".cu");
            }});
      // Enable exceptions and omit all subsequent error checks
      JCudaDriver.setExceptionsEnabled(true);
      // JNvrtc.setExceptionsEnabled(true);
      // Initialize the driver and create a context for the first device.
      cuInit(0);
      CUdevice device = new CUdevice();
      cuDeviceGet(device, 0);
      CUcontext context = new CUcontext();
      cuCtxCreate(context, 0, device);
      for (File f : directoryFiles) {
         // Use the NVRTC to create a program by compiling the source code
         nvrtcProgram program = new nvrtcProgram();
         nvrtcCreateProgram(
            program, GetFileContents(f), null, 0, null, null);
         nvrtcCompileProgram(program, 0, null);
      // Print the compilation log (for the case there are any warnings)
         String programLog[] = new String[1];
         nvrtcGetProgramLog(program, programLog);
         System.out.println("Program compilation log:\n" + programLog[0]);
      // Obtain the PTX ("CUDA Assembler") code of the compiled program
         String[] ptx = new String[1];
         nvrtcGetPTX(program, ptx);
         nvrtcDestroyProgram(program);
         Map_Filename_PTX.put(f.getName(), ptx[0]);
         System.out.println(f.getName());
      }
   }
   
   public void LoadPTXFile(String fileName) {
      if (! Map_Filename_PTX.containsKey(fileName)) {
         return;
      }
      String ptx = Map_Filename_PTX.get(fileName);
         // Create a CUDA module from the PTX code
      CUmodule module = new CUmodule();
      cuModuleLoadData(module, ptx);
      // Obtain the function pointer to the "add" function from the module
      CUfunction function = new CUfunction();
      cuModuleGetFunction(function, module, "add");
      // Continue with some basic setup for the vector addition itself:
      // Allocate and fill the host input data
      int numElements = 256 * 100;
      float hostInputA[] = new float[numElements];
      float hostInputB[] = new float[numElements];
      for(int i = 0; i < numElements; i++)
      {
         hostInputA[i] = (float)i;
         hostInputB[i] = (float)i;
      }
      // Allocate the device input data, and copy the
      // host input data to the device
      CUdeviceptr deviceInputA = new CUdeviceptr();
      cuMemAlloc(deviceInputA, numElements * Sizeof.FLOAT);
      cuMemcpyHtoD(deviceInputA, Pointer.to(hostInputA),
            numElements * Sizeof.FLOAT);
      CUdeviceptr deviceInputB = new CUdeviceptr();
      cuMemAlloc(deviceInputB, numElements * Sizeof.FLOAT);
      cuMemcpyHtoD(deviceInputB, Pointer.to(hostInputB),
            numElements * Sizeof.FLOAT);
      // Allocate device output memory
      CUdeviceptr deviceOutput = new CUdeviceptr();
      cuMemAlloc(deviceOutput, numElements * Sizeof.FLOAT);
      // Set up the kernel parameters: A pointer to an array
      // of pointers which point to the actual values.
      Pointer kernelParameters = Pointer.to(
            Pointer.to(new int[]{numElements}),
            Pointer.to(deviceInputA),
            Pointer.to(deviceInputB),
            Pointer.to(deviceOutput)
            );
      
      
      // Call the kernel function, which was obtained from the
      // module that was compiled at runtime
      int blockSizeX = 256;
      int gridSizeX = (numElements + blockSizeX - 1) / blockSizeX;
      cuLaunchKernel(function,
            gridSizeX,  1, 1,      // Grid dimension
            blockSizeX, 1, 1,      // Block dimension
            0, null,               // Shared memory size and stream
            kernelParameters, null // Kernel- and extra parameters
            );
      cuCtxSynchronize();
      
      // Allocate host output memory and copy the device output
      // to the host.
      float hostOutput[] = new float[numElements];
      cuMemcpyDtoH(Pointer.to(hostOutput), deviceOutput,
            numElements * Sizeof.FLOAT);
      
      // Verify the result
      boolean passed = true;
      for(int i = 0; i < numElements; i++)
      {
         float expected = i+i;
         if (Math.abs(hostOutput[i] - expected) > 1e-5)
         {
            System.out.println(
                  "At index "+i+ " found "+hostOutput[i]+
                  " but expected "+expected);
            passed = false;
            break;
         }
      }
      System.out.println("Test "+(passed?"PASSED":"FAILED"));
      
      // Clean up.
      cuMemFree(deviceInputA);
      cuMemFree(deviceInputB);
      cuMemFree(deviceOutput);
   }
   
   public static String GetFileContents(File file) {
      try {
         Scanner reader = new Scanner(file);
         String s = "";
         while (reader.hasNextLine()) {
            s += reader.nextLine() + "\n";
         }
         reader.close();
         return s;
      }
      catch (Exception e) {
         System.out.println(e);
      }
      return null;
   }
}