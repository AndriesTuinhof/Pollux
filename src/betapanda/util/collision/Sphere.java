package betapanda.util.collision;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import awesome.util.Mass;

public class Sphere extends Collision{

	private static vbo sphere;
	public Point p;
	private float radius;
	
	public Sphere() {
		this.shape ="SPHERE";
		p =new Point();
		Collision.collisions.add(this);
	}
	
	public Sphere(float x, float y, float z, float r){
		p =new Point();
		p.mass.setX(x);
		p.mass.setY(y);
		p.mass.setZ(z);
		this.radius =r;
		this.shape ="SPHERE";
		if(sphere ==null)render();
		Collision.collisions.add(this);
	}

	public Sphere(Mass mass, float r){
		this.p =new Point(mass);
		this.radius =r;
		this.shape ="SPHERE";
		if(sphere ==null)render();
		Collision.collisions.add(this);
		
	}
	
	public void render(){
		int segments =24;
		FloatBuffer b =BufferUtils.createFloatBuffer((segments) *(segments) *6 *2);
		float Latitude =(float)Math.PI/segments;
	    float Longitude =(float)Math.PI*2.0f/segments;
        float r1, z1, x0, y0;
        
	    for(int i =1; i <=segments; i++){            
        	for(int j =0; j <segments; j++){	
		        r1 =(float) Math.sin((i)*Latitude);
		        z1 =(float) Math.cos((i)*Latitude);
	            x0 =(float) (r1*Math.sin((j +1)*Longitude));
	            y0 =(float) (r1*Math.cos((j +1)*Longitude));
	            b.put(x0).put(y0).put(z1);
	            
		        r1 =(float) Math.sin((i)*Latitude);
		        z1 =(float) Math.cos((i)*Latitude);
	            x0 =(float) (r1*Math.sin((j)*Longitude));
	            y0 =(float) (r1*Math.cos((j)*Longitude));
	            
	            b.put(x0).put(y0).put(z1);	        	
	        }
	    }
	    
	    for (int i = 1; i < segments; i++){            
	        if(i!=1 && i!=segments){
	        	for (int j = 0; j < segments; j++){	

			        r1 =(float) Math.sin((i)*Latitude);
			        z1 =(float) Math.cos((i)*Latitude);
		            x0 =(float) (r1*Math.sin((j)*Longitude));
		            y0 =(float) (r1*Math.cos((j)*Longitude));
		            b.put(x0).put(y0).put(z1);
		            
			        r1 =(float) Math.sin((i-1)*Latitude);
			        z1 =(float) Math.cos((i-1)*Latitude);
		            x0 =(float) (r1*Math.sin((j)*Longitude));
		            y0 =(float) (r1*Math.cos((j)*Longitude));
		            
		            b.put(x0).put(y0).put(z1);
		        	
		        }
	        }
	    }
		
		b.flip();
		sphere =new vbo(b, segments *segments *4);
		setup();
	}
	
	public void setup(){
		this.matrix.setIdentity();
		this.matrix.translate(this.getX(), this.getY(), this.getZ());
		this.matrix.scale(getRadius(), getRadius(), getRadius());
	}
	
	public boolean sphereInSphere(Sphere s){
		float x =this.getX() -s.getX();
		float y =this.getY() -s.getY();
		float z =this.getZ() -s.getZ();
		float d =(float) Math.sqrt(x *x +y *y +z *z);
		return d <this.getRadius() +s.getRadius();
	}
	
	public float getX(){
		return p.mass.getX();
	}
	
	public float getY(){
		return p.mass.getY();
	}
	
	public float getZ(){
		return p.mass.getZ();
	}

	public float getRadius(){
		return radius;
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void draw() {
		setup();
		sphere.draw();
		
	}

	public Point getP() {
		return p;
	}

	public void setP(Point p) {
		this.p = p;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}
}
