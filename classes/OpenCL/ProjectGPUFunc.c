__kernel void lpuBarycentricCoords(
__global const float *data,
__global const int *dimention,
__global float *out // must be zero to start, 3 times the size of data
)
{
   int gid = get_global_id(0);
   int dataSize = dimention[0] * (dimention[0] + 1);
   int mSize = dimention[0]*dimention[0];
   int firstEntry = gid*dataSize;
   int outSize = mSize*3+dimention[0];
   int firstOut = gid*outSize;
   //getShift
   for(int i = 0; i<dimention[0]; i++){
      out[firstOut+i] = -1*data[firstEntry+1];
   }
   //p, l, then u
   for(int i = 0; i<mSize; i += dimention+1){
      out[firstOut+i+dimention] = 1;
      out[firstOut+i+mSize + dimention] = 1;
   }
   for(int i = 0; i<mSize; i++){
      int c = i%dimention;
      int r = i/dimention
      out[firstOut+i+mSize*2 + dimention] = data[firstEntry+dimention*(c+1)+r] + out[firstOut+r];
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
      for(int j = 0; j<dimention; j++){
         temp[i] = out[firstOut+targetIndex*dimention+j+mSize + dimention];
         out[firstOut+targetIndex*dimention+j+mSize + dimention] = out[firstOut+i*dimention+j+mSize + dimention];
         out[firstOut+i*dimention+j+mSize + dimention] = temp[i];
      }
      //eliminate
      for(int j = i+1; j<dimention; j++){
         float mult = out[firstOut+dimention*j+i+2*mSize + dimention]/out[firstOut+(i)*(dimention+1)+2*mSize + dimention];
         for(int k = 0; k<dimention; k++){
            out[firstOut+dimention*j+k+2*mSize + dimention] = out[firstOut+dimention*j+k+2*mSize + dimention]-(mult*out[firstOut+dimention*i+k+2*mSize + dimention])
         }
         
      }
   }
}
__kernel int floatCompare(const float a, const float b){
   float epsilon = 0.000001f;
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