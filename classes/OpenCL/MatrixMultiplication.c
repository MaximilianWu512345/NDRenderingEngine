__kernel void matrixMultiplyKernel(
__global const float *data,
__global const float *otherData,
__global const int *width,
__global float *out
)
{
   int gid = get_global_id(0);
   int i = gid / width[0];
   int j = gid % width[0];
   float sum = 0;
   for(int k = 0; k < width[0]; k++){
      sum += otherData[k * width[0] + j] * data[i * width[0] + k];
   }
   out[gid] = sum;
}