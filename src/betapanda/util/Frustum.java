package betapanda.util;

import betapanda.gameplay.world.Chunk;
import betapanda.util.math.vec3;

public class Frustum{
		
		private plane[] p =new plane[6];
		private Camera camera;
		
		public Frustum(Camera camera){
			this.camera =camera;
			for(int i =0; i <p.length; i++)
				p[i] =new plane();
		}

		float[] proj =new float[16];
		float[] modl =new float[16];
		float[] clip =new float[16];
		public void logic(){		
			this.camera.getProjectionMatrix().putTransposed(proj);
			this.camera.getModelViewMatrix().putTransposed(modl);

			clip[0]  =modl[ 0] *proj[ 0] +modl[ 1] *proj[ 4] +modl[ 2] *proj[ 8] +modl[ 3] *proj[12];
			clip[1]  =modl[ 0] *proj[ 1] +modl[ 1] *proj[ 5] +modl[ 2] *proj[ 9] +modl[ 3] *proj[13];
			clip[2]  =modl[ 0] *proj[ 2] +modl[ 1] *proj[ 6] +modl[ 2] *proj[10] +modl[ 3] *proj[14];
			clip[3]  =modl[ 0] *proj[ 3] +modl[ 1] *proj[ 7] +modl[ 2] *proj[11] +modl[ 3] *proj[15];
			
			clip[4]  =modl[ 4] *proj[ 0] +modl[ 5] *proj[ 4] +modl[ 6] *proj[ 8] +modl[ 7] *proj[12];
			clip[5]  =modl[ 4] *proj[ 1] +modl[ 5] *proj[ 5] +modl[ 6] *proj[ 9] +modl[ 7] *proj[13];
			clip[6]  =modl[ 4] *proj[ 2] +modl[ 5] *proj[ 6] +modl[ 6] *proj[10] +modl[ 7] *proj[14];
			clip[7]  =modl[ 4] *proj[ 3] +modl[ 5] *proj[ 7] +modl[ 6] *proj[11] +modl[ 7] *proj[15];
			
			clip[8]  =modl[ 8] *proj[ 0] +modl[ 9] *proj[ 4] +modl[10] *proj[ 8] +modl[11] *proj[12];
			clip[9]  =modl[ 8] *proj[ 1] +modl[ 9] *proj[ 5] +modl[10] *proj[ 9] +modl[11] *proj[13];
			clip[10] =modl[ 8] *proj[ 2] +modl[ 9] *proj[ 6] +modl[10] *proj[10] +modl[11] *proj[14];
			clip[11] =modl[ 8] *proj[ 3] +modl[ 9] *proj[ 7] +modl[10] *proj[11] +modl[11] *proj[15];
			
			clip[12] =modl[12] *proj[ 0] +modl[13] *proj[ 4] +modl[14] *proj[ 8] +modl[15] *proj[12];
			clip[13] =modl[12] *proj[ 1] +modl[13] *proj[ 5] +modl[14] *proj[ 9] +modl[15] *proj[13];
			clip[14] =modl[12] *proj[ 2] +modl[13] *proj[ 6] +modl[14] *proj[10] +modl[15] *proj[14];
			clip[15] =modl[12] *proj[ 3] +modl[13] *proj[ 7] +modl[14] *proj[11] +modl[15] *proj[15];
			
			
			p[3].n.x 	=clip[3]  -clip[0];
			p[3].n.y 	=clip[7]  -clip[4];
			p[3].n.z 	=clip[11] -clip[8];
			p[3].d 		=clip[15] -clip[12];

			p[2].n.x 	=clip[3]  +clip[0];
			p[2].n.y 	=clip[7]  +clip[4];
			p[2].n.z 	=clip[11] +clip[8];
			p[2].d 		=clip[15] +clip[12];

			p[5].n.x 	=clip[3]  -clip[1];
			p[5].n.y 	=clip[7]  -clip[5];
			p[5].n.z 	=clip[11] -clip[9];
			p[5].d 		=clip[15] -clip[13];

			p[4].n.x 	=clip[3]  +clip[1];
			p[4].n.y 	=clip[7]  +clip[5];
			p[4].n.z 	=clip[11] +clip[9];
			p[4].d 		=clip[15] +clip[13];

			p[1].n.x 	=clip[3]  -clip[2];
			p[1].n.y 	=clip[7]  -clip[6];
			p[1].n.z 	=clip[11] -clip[10];
			p[1].d 		=clip[15] -clip[14];

			p[0].n.x 	=clip[3]  +clip[2];
			p[0].n.y 	=clip[7]  +clip[6];
			p[0].n.z 	=clip[11] +clip[10];
			p[0].d 		=clip[15] +clip[14];
			
			// Normalize all plane normals
			for(int i = 0; i < 6; i++)
				p[i].normalize();
		}
		
		public boolean sphereInFrustum(float x, float y, float z, float radius){
            for (int i =0; i <6; i ++) {
            	if(p[i].n.x *x +p[i].n.y *y +p[i].n.z *z <= -radius)
            		return true;
            	
                
            }

            return true;
		}
		
		public boolean boxInFrustum(float x, float y, float z, float size){
		   for(int i =0; i <6; i++ ){
		      if(p[i].n.x * (x -size) + p[i].n.y * (y - size) + p[i].n.z * (z - size) + p[i].d > 0 )
		         continue;
		      if(p[i].n.x * (x +size) + p[i].n.y * (y - size) + p[i].n.z * (z - size) + p[i].d > 0 )
		         continue;
		      if(p[i].n.x * (x -size) + p[i].n.y * (y + size) + p[i].n.z * (z - size) + p[i].d > 0 )
		         continue;
		      if(p[i].n.x * (x +size) + p[i].n.y * (y + size) + p[i].n.z * (z - size) + p[i].d > 0 )
		         continue;
		      if(p[i].n.x * (x -size) + p[i].n.y * (y - size) + p[i].n.z * (z + size) + p[i].d > 0 )
		         continue;
		      if(p[i].n.x * (x +size) + p[i].n.y * (y - size) + p[i].n.z * (z + size) + p[i].d > 0 )
		         continue;
		      if(p[i].n.x * (x -size) + p[i].n.y * (y + size) + p[i].n.z * (z + size) + p[i].d > 0 )
		         continue;
		      if(p[i].n.x * (x +size) + p[i].n.y * (y + size) + p[i].n.z * (z + size) + p[i].d > 0 )
		         continue;
		      return false;
		   }
		   return true;
		}
		
		public boolean chunkInFrustum(Chunk c){
			
			return boxInFrustum(c.getXX() +Chunk.size /2, c.getYY() +Chunk.size /2, Chunk.size /2, Chunk.size /2);
		}
		
		public class plane{
			
			public vec3 n =new vec3();
			public float d;	//the distance from the frustum origin
			
			public void normalize(){
				float l =1f /(float)Math.sqrt(n.x *n.x +n.y *n.y +n.z *n.z);
				n.x *=l;
				n.y *=l;
				n.z *=l;
				d *=l;
			
			}
		}
	}