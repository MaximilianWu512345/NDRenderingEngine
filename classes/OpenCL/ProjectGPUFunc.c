//https://community.amd.com/t5/archives-discussions/opencl-tutorial-help-cl-invalid-kernel-name/td-p/384785
#pragma OPENCL EXTENSION cl_khr_byte_addressable_store : enable
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
//TODO: check if some loops can be remmoved using get_global_id(1) or more
//TODO: finish last buffer
int floatCompare(const float a, const float b){
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
   return 7;
}
int floatCompareEps(const float a, const float b, const float epsilon){
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
   return 7;
}
void calcBaryCoords(float* pos, float* lpu, int triangleIndex, int dimention, float* out, float* sol, float* dat, int posStart, int outStart){
   int trueIndex = triangleIndex*(dimention+3*dimention*dimention);
   int arrPos = triangleIndex*dimention;
   for(int i = 0; i<dimention-1; i++){
      dat[i+ arrPos] = pos[i + posStart]-lpu[i+trueIndex];
   }
   //P
   for(int i = 0; i<dimention-1; i++){
      for(int j = 0; j<dimention-1; j++){
         if(floatCompare(lpu[trueIndex+dimention*(i+1)+dimention*dimention + j], 1) == 0){
            sol[i + arrPos] = dat[j+ arrPos];
         }
      }
   }
   //swap
   //L
   for(int i = 0; i<dimention; i++){
      dat[i + arrPos] = sol[i + triangleIndex*(dimention)] ;
      float sum = 0;
      for(int j = 0; j<i; j++){
         sum += lpu[trueIndex+dimention*(i+1) + j]*dat[j];
      }
      dat[i + arrPos] -= sum;
      dat[i + arrPos] /= lpu[trueIndex+dimention*(i+1) + i];
   }
   //swap
   //U
   for(int i = dimention-1; i>=0; i--){
      sol[i + arrPos] = dat[i + arrPos];
      float sum = 0;
      for(int j = i+1; j<dimention; j++){
         sum += lpu[trueIndex+dimention*(i+1) + 2*dimention*dimention + j]*sol[j];
      }
      sol[i + triangleIndex*(dimention)] -= sum;
      sol[i + triangleIndex*(dimention)] /= lpu[trueIndex+dimention*(i+1) + 2*dimention*dimention + i];
   }
   float sum = 0;
   for(int i = 0; i<dimention-1; i++){
      sum += sol[i + arrPos];
      out[i + outStart] = sol[i + arrPos];
   }
   out[dimention + outStart] = 1-sum;
}
void lpuBarycentricCoords(
float *data,
int dimention,
float *out, // must be zero to start, 3 times the size of data
int id
)
{
   int gid = id;
   int dataSize = dimention * (dimention + 1);
   int mSize = dimention*dimention;
   int outSize = mSize*3+dimention;
   int firstOut = gid*outSize;
   //getShift
   for(int i = 0; i<dimention; i++){
      out[firstOut+i] = -1*data[i];
   }
   //p, l, then u
   for(int i = 0; i<mSize; i += (dimention+1)){
      out[firstOut+i+dimention] = 1;
      out[firstOut+i+mSize + dimention] = 1;
   }
   for(int i = 0; i<mSize; i++){
      int c = i%dimention;
      int r = i/dimention;
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
      float temp;
      //swap u
      for(int j = 0; j<dimention; j++){
         temp = out[firstOut+targetIndex*dimention+j+mSize*2 + dimention];
         out[firstOut+targetIndex*dimention+j+mSize*2 + dimention] = out[firstOut+i*dimention+j+mSize*2 + dimention];
         out[firstOut+i*dimention+j+mSize*2 + dimention] = temp;
      }
      //swap p
      for(int j = 0; j<dimention; j++){
         temp = out[firstOut+targetIndex*dimention+j + dimention];
         out[firstOut+targetIndex*dimention+j+mSize*2 + dimention] = out[firstOut+i*dimention+j + dimention];
         out[firstOut+i*dimention+j + dimention] = temp;
      }
      //swap l
      out[firstOut+targetIndex*dimention+targetIndex+mSize + dimention] = 0;
      out[firstOut+i*dimention+i+mSize + dimention] = 0;
      
      for(int j = 0; j<dimention; j++){
         temp = out[firstOut+targetIndex*dimention+j+mSize + dimention];
         out[firstOut+targetIndex*dimention+j+mSize + dimention] = out[firstOut+i*dimention+j+mSize + dimention];
         out[firstOut+i*dimention+j+mSize + dimention] = temp;
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
__kernel void RaserizeStep1(
__global float *coords, //actual coords 
__global float *tcoords, //texture coords 
__global int *textureIndex, //texture to use 
__global uchar *textureColors, //all textures are in same array, each uchar is an rgb chanel 
__global int *textureSizes,  //texture sizes are all in the same array 
__global uchar *textureType, //type of texture 
__global float *lpuData, //data for lpu
__global uchar *out, // final result, each uchar is an rgb chanel
__global float *zBuff, //zbuffer
__global int *stencilBuff, //stencil buffer, for lighting
__global int *outDim, //dimentions of the result
__global float *fCoords, //flat coords
__global float *dat, //for lpu
__global float *sol, //for lpu
__global float *pixPos, //draw
__global float *found, //draw
__global float *texPos, //draw
__global int *texPosRound, //draw
__global float *temp, //draw
int tdim, //dimention of textures 19
int dimention, //dimention after slicing 
uchar DefR, //default red 
uchar DefG, //default green
uchar DefB, //default blue
int numSim, //number of simplexes
int numTextures //using dimention can help figure out each texture
){
   //printf("hello");
   //flaten
   int gid = get_global_id(0);
   for(int j = 0; j<dimention; j++){
      float dist = coords[gid*dimention*dimention+dimention*j+dimention-1];
      for(int k = 0; k<dimention-1; k++){
         fCoords[(dimention)*(dimention-1)*gid+(dimention-1)*j + k] = coords[gid*dimention*dimention+dimention*j+k]/dist;
      }
   }
   //printf("barry");
   //make matrixes
   lpuBarycentricCoords(fCoords, dimention, lpuData, gid);
   //printf("bye");
}

__kernel void RaserizeStep2(   
__global float *coords, //actual coords 
__global float *tcoords, //texture coords 
__global int *textureIndex, //texture to use 
__global uchar *textureColors, //all textures are in same array, each uchar is an rgb chanel 
__global int *textureSizes,  //texture sizes are all in the same array 
__global uchar *textureType, //type of texture 
__global float *lpuData, //data for lpu
__global uchar *out, // final result, each uchar is an rgb chanel
__global float *zBuff, //zbuffer
__global int *stencilBuff, //stencil buffer, for lighting
__global int *outDim, //dimentions of the result
__global float *fCoords, //flat coords
__global float *dat, //for lpu
__global float *sol, //for lpu
__global float *pixPos, //draw
__global float *found, //draw
__global float *texPos, //draw
__global int *texPosRound, //draw
__global float *temp, //draw
int tdim, //dimention of textures 19
int dimention, //dimention after slicing 
uchar DefR, //default red 
uchar DefG, //default green
uchar DefB, //default blue
int numSim, //number of simplexes
int numTextures //using dimention can help figure out each texture
){
   //printf("hello2");
   int gid = get_global_id(0);
   int arrStartSmall = gid*(dimention-1);
   int arrStartLarge = gid*(dimention);
   int arrStartTexture = gid*tdim;
   if(stencilBuff[gid/8] && (1<<(gid%8)) > 0){
      int pixPosInt = gid;
      for(int i = 0; i<(dimention-1); i++){
         pixPos[i+arrStartSmall] = (float)(pixPosInt%outDim[i]);
         pixPosInt /= outDim[i];
      }
      for(int i = 0; i<numSim; i++){
         calcBaryCoords(pixPos, lpuData, i, dimention, found, dat, sol, arrStartSmall, arrStartLarge);
         bool inSim = true;
         for(int j = 0; j<dimention; j++){
            if(found[j + arrStartLarge] < 0 || found[j + arrStartLarge]>1){
               inSim = false;
            }
         }
         if((zBuff[gid]<found[dimention-1 + arrStartLarge] || zBuff[gid]<0) && inSim){
            //get new color
            uchar r = DefR;
            uchar g = DefG;
            uchar b = DefB;
            int first = 0;
            for(int j = 0; j<textureIndex[i]; j++){
               int sum = 1;
               for(int k = 0; k<tdim; k++){
                  sum *= textureSizes[j*(tdim) + k];
               }
               first += sum;
            }
            first *= 3;
            if(textureType[textureIndex[i]] == 'c'){ // constant texture
               r = textureColors[first];
               g = textureColors[first+1];
               b = textureColors[first+2];
            } 
            else if (textureType[textureIndex[i]] == 'b') { //bit map
               int first = 0;
               for(int j = 0; j<(dimention-1); j++){
                  for(int k = 0; k<dimention; k++){
                     texPos[j + arrStartTexture] += tcoords[i*dimention*(dimention-1) + j*(dimention-1) + k]*found[j + arrStartLarge];
                  }
               }
               for(int j = 0; j<(dimention-1); j++){
                  texPosRound[j + arrStartTexture] = (int)texPos[j + arrStartTexture];
               }
               int mult = 1;
               for(int j = 0; j<(dimention-1); j++){
                  first += j*mult;
                  mult *= textureSizes[i*(dimention-1) + j];
               }
               r = textureColors[first];
               g = textureColors[first+1];
               b = textureColors[first+2];
            }
            //set new color
            out[pixPosInt*3] = r;
            out[pixPosInt*3] = g;
            out[pixPosInt*3] = b;
         }
      }
   }
   //printf("bye2");
}



