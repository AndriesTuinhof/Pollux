package betapanda.util;


public class vec4{
		
		public float x, y, z, w;
		
		public vec4(){
			
		}
		
		public vec4(float x, float y, float z, float w){
			this.x =x;
			this.y =y;
			this.z =z;
			this.w =w;
			
		}
		
		public void setX(float x){
			this.x =x;
		}
		
		public void setY(float y){
			this.y =y;
		}
		
		public void setZ(float z){
			this.z =z;
		}
		
		public void set(float x, float y, float z){
			this.x =x;
			this.y =y;
			this.z =z;
		}


		public float getX(){
			return x;
		}

		public float getY(){
			return y;
		}
		
		public float getZ(){
			return z;
		}
		
		public void minus(vec4 v1){
			this.x -=v1.x;
			this.y -=v1.y;
			this.z -=v1.z;
		}
		
		public void add(vec4 v1){
			this.x +=v1.x;
			this.y +=v1.y;
			this.z +=v1.z;
		}
		
		public static vec4 cross(vec4 left, vec4 right, vec4 dest) {
			if (dest == null) {
				dest = new vec4();
			}

			dest.x =left.y *right.z -right.y *left.z;
			dest.y =left.z *right.x -right.z *left.x;
			dest.z =left.x *right.y -right.x *left.y;
			return dest;
		}

		public static float dot(vec4 left, vec4 right) {
			return (left.x * right.x + left.y * right.y + left.z * right.z);
		}
		
		public void normalize(){
			float l =(float) (1f /Math.sqrt(x * x + y * y + z * z));
			x *=l;
			y *=l;
			z *=l;
		}
		
		@Override
		public String toString(){
			return x+"  , "+y+"  , "+z;
		}
	}