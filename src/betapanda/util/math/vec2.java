package betapanda.util.math;

public class vec2{
		
		public float x, y;
		
		public vec2(){
			
		}
		
		public vec2(float x, float y){
			this.x =x;
			this.y =y;
		}
		
		public void setX(float x){
			this.x =x;
		}
		
		public void setY(float y){
			this.y =y;
		}
		
		public void set(float x, float y){
			setX(x);
			setY(y);
		}


		public float getX(){
			return x;
		}

		public float getY(){
			return y;
		}
		
		public void minus(vec2 v1){
			this.x -=v1.x;
			this.y -=v1.y;
		}
		
		public void add(vec2 v1){
			this.x +=v1.x;
			this.y +=v1.y;
		}
		
		public void normalize(){
			float l =(float) (1f /Math.sqrt(x * x + y * y));
			x *=l;
			y *=l;
		}
		
		@Override
		public String toString(){
			return x+"  , "+y;
		}
	}