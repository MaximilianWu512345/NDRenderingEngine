//steps to render:
//slicing
//1)lp
//2)adjust textures
//3)assemble triangles
//rastrize
//1)set up lpu
//2)barycentric coords
//3)lighting????
//    something about a stencil buffer...
__kernel void RaserizeStep1(
__global float *coords, //actual coords
__global float *tcoords, //texture coords
__global int dimention, //dimention after slicing
__global int *textureColors, //all textures are in same array
__global int *textureSizes,  //texture sizes are all in the same array
__global int numTextures, //using dimention can help figure out each texture
__global float *lpuData, //data for lpu, shift, l, p, u
__global int *out, // final result
__global int numSim, //number of simplexes
__global int *outDim //dimentions of the result
){
   //flaten
   int gid = get_global_id(0);
   float lpuTemp[numSim*(dimention + 3*dimention*dimention)];
   lpuData = &lpuTemp;
   float fCoords[numSim*(dimention-1)*dimention];
   for(int j = 0; j<dimention; j++){
      float dist = coords[gid*dimention*dimention+dimention*j+dimention-1];
      for(int k = 0; k<dimention-1; k--){
         fCoords[(dimention-1)*j + k] = coords[gid*dimention*dimention+dimention*j+k]/dist;
      }
   }
   //make matrixes
   lpuBarycentricCoords(fCoords&, dimention, lpuData, gid);
}

__kernel void RaserizeStep2(
__global float *coords, //actual coords
__global float *tcoords, //texture coords
__global int dimention, //dimention after slicing
__global int *textureColors, //all textures are in same array
__global int *textureSizes,  //texture sizes are all in the same array
__global int numTextures, //using dimention can help figure out each texture
__global float *lpuData, //data for lpu
__global int *out, // final result
__global int numSim, //number of simplexes
__global int *outDim //dimentions of the result
){
   gid = get_global_id(0);
}
__kernel void lpuBarycentricCoords(
const float *data,
__global const int *dimention,
__global float *out, // must be zero to start, 3 times the size of data
int id
)
{
   int gid = id;
   int dataSize = dimention[0] * (dimention[0] + 1);
   int mSize = dimention[0]*dimention[0];
   int outSize = mSize*3+dimention[0];
   int firstOut = gid*outSize;
   //getShift
   for(int i = 0; i<dimention[0]; i++){
      out[firstOut+i] = -1*data[i];
   }
   //p, l, then u
   for(int i = 0; i<mSize; i += dimention+1){
      out[firstOut+i+dimention] = 1;
      out[firstOut+i+mSize + dimention] = 1;
   }
   for(int i = 0; i<mSize; i++){
      int c = i%dimention;
      int r = i/dimention
      out[firstOut+i+mSize*2 + dimention] = data[dimention*(c+1)+r] + out[firstOut+r];
   }
   //calculations
   for(int i = 0; i<dimention; i++){
      //pivot
      float val = out[firstOut+(i)*(dimention+1)+2*mSize + dimention];
      int targetIndex = i;
      for(int j = i+1; ((j<dimention) && (floatCompare(val, 0) == 0)); j++){
         val = fabs(out[firstOut+j*dimention+i+2*mSize + dimention]);
         targetIndex = j;
      }
      //no pivot found
      if((floatCompare(val, 0) == 0)){
         //set p to zero
         for(int j = 0; j<mSize; j++){
            out[firstOut+i+dimention] = 0;
         }
         return;
      }
      float temp[dimention];
      //swap u
      for(int j = 0; j<dimention; j++){
         temp[i] = out[firstOut+targetIndex*dimention+j+mSize*2 + dimention];
         out[firstOut+targetIndex*dimention+j+mSize*2 + dimention] = out[firstOut+i*dimention+j+mSize*2 + dimention];
         out[firstOut+i*dimention+j+mSize*2 + dimention] = temp[i];
      }
      //swap p
      for(int j = 0; j<dimention; j++){
         temp[i] = out[firstOut+targetIndex*dimention+j + dimention];
         out[firstOut+targetIndex*dimention+j+mSize*2 + dimention] = out[firstOut+i*dimention+j + dimention];
         out[firstOut+i*dimention+j + dimention] = temp[i];
      }
      //swap l
      out[firstOut+targetIndex*dimention+targetIndex+mSize + dimention] = 0;
      out[firstOut+i*dimention+i+mSize + dimention] = 0;
      
      for(int j = 0; j<dimention; j++){
         temp[i] = out[firstOut+targetIndex*dimention+j+mSize + dimention];
         out[firstOut+targetIndex*dimention+j+mSize + dimention] = out[firstOut+i*dimention+j+mSize + dimention];
         out[firstOut+i*dimention+j+mSize + dimention] = temp[i];
      }
      out[firstOut+targetIndex*dimention+targetIndex+mSize + dimention] = 1;
      out[firstOut+i*dimention+i+mSize + dimention] = 1;
      //eliminate
      for(int j = i+1; j<dimention; j++){
         float mult = out[firstOut+dimention*j+i+2*mSize + dimention]/out[firstOut+(i)*(dimention+1)+2*mSize + dimention];
         for(int k = 0; k<dimention; k++){
            out[firstOut+dimention*j+k+2*mSize + dimention] = out[firstOut+dimention*j+k+2*mSize + dimention]-(mult*out[firstOut+dimention*i+k+2*mSize + dimention]);
         }
         out[firstOut+j*dimention+i+mSize + dimention] = mult;
      }
   }
}
__kernal float* calcBaryCoords(float* pos, float* lpu, int triangleIndex, int dimention){
   float sol[dimention];
   float dat[dimention-1];
   int trueIndex = triangleIndex*(dimention+3*dimention*dimention);
   for(int i = 0; i<dimention-1; i++){
      dat[i] = pos[i]-lpu[i+trueIndex];
   }
   //P
   for(int i = 0; i<dimention-1; i++){
      for(int j = 0; j<dimention-1; j++){
         if(floatCompare(lpu[trueIndex+dimention*(i+1)+dimention*dimention + j], 1) == 0){
            sol[i] = dat[j];
            break;
         }
      }
   }
   dat = sol;
   for(int i = 0; i<dimention; i++){
      sol[i] = 0;
   }
   //L
   for(int i = 0; i<dimention; i++){
      sol[i] = dat[i];
      float sum = 0;
      for(int j = 0; j<i; j++){
         sum += lpu[trueIndex+dimention*(i+1) + j]*sol[j];
      }
      sol[i] -= sum;
      sol[i] /= lpu[trueIndex+dimention*(i+1) + i];
   }
   dat = sol;
   for(int i = 0; i<dimention; i++){
      sol[i] = 0;
   }
   //U
   for(int i = dimention-1; i>=0; i--){
      sol[i] = dat[i];
      float sum = 0;
      for(int j = i+1; j<dimention; j++){
         sum += lpu[trueIndex+dimention*(i+1) + 2*dimention*dimention + j]*sol[j];
      }
      sol[i] -= sum;
      sol[i] /= lpu[trueIndex+dimention*(i+1) + 2*dimention*dimention + i];
   }
   return &sol;
}
__kernel int floatCompare(const float a, const float b){
   float epsilon = 0.000001f;
   if(a == b){
      return 0;
   }
   if(a>b){
      if(b>=a-epsilon){
         return 0;
      }
      return 1;
   }
   if(b>a){
      if(b<=a+epsilon){
         return 0;
      }
      return -1;
   }
}
__kernel int floatCompare(const float a, const float b, const float epsilon){
   if(a == b){
      return 0;
   }
   if(a>b){
      if(b<=a-epsilon){
         return 0;
      }
      return 1;
   }
   if(b<a){
      if(b<=a+epsilon){
         return 0;
      }
      return -1;
   }
}
