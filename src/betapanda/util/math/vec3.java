package betapanda.util.math;


public class vec3{
		
		public float x, y, z;
		
		public vec3(){
			
		}
		
		public vec3(float x, float y, float z){
			this.x =x;
			this.y =y;
			this.z =z;
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
		
		public void minus(vec3 v1){
			this.x -=v1.x;
			this.y -=v1.y;
			this.z -=v1.z;
		}
		
		public void add(vec3 v1){
			this.x +=v1.x;
			this.y +=v1.y;
			this.z +=v1.z;
		}
		
		public static vec3 add(vec3 left, vec3 right, vec3 dest) {
			if (dest == null) {
				return new vec3(left.x + right.x, left.y + right.y, left.z + right.z);
			}
			dest.set(left.x + right.x, left.y + right.y, left.z + right.z);
			return dest;
		}

		public static vec3 sub(vec3 left, vec3 right, vec3 dest) {
			if (dest == null) {
				return new vec3(left.x - right.x, left.y - right.y, left.z - right.z);
			}
			dest.set(left.x - right.x, left.y - right.y, left.z - right.z);
			return dest;
		}

		public static vec3 cross(vec3 left, vec3 right, vec3 dest) {
			if (dest == null) {
				dest = new vec3();
			}

			dest.x =left.y *right.z -right.y *left.z;
			dest.y =left.z *right.x -right.z *left.x;
			dest.z =left.x *right.y -right.x *left.y;
			return dest;
		}

		public static float dot(vec3 left, vec3 right) {
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