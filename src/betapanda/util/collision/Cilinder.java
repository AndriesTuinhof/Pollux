package betapanda.util.collision;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import awesome.util.Mass;

public class Cilinder extends Sphere{

	public Point p;
	public float zOffset, height, radius;
	private static vbo cilinder;
	
	public Cilinder(float x, float y, float z, float radius, float zOffset, float height) {
		this.shape ="CILINDER";
		p =new Point();
		p.mass.setX(x);
		p.mass.setY(y);
		p.mass.setZ(z);
		this.radius =radius;
		this.zOffset =zOffset;
		this.height =height;
		if(cilinder ==null)
			render();
		Collision.collisions.add(this);
		
	}	
	
	public Cilinder() {
		this.shape ="CILINDER";
		p =new Point();
		Collision.collisions.add(this);
	}
	
	public Cilinder(Mass mass, float radius, float zOffset, float height) {
		this.shape ="CILINDER";
		p =new Point(mass);
		this.radius =radius;
		this.zOffset =zOffset;
		this.height =height;
		if(cilinder ==null)
			render();
		Collision.collisions.add(this);
		
	}	
	
	public void render(){
		int segments =24;
		FloatBuffer b =BufferUtils.createFloatBuffer((segments +1) *8 *3);		
//		float Latitude =(float)Math.PI /segments;
	    float Longitude =(float)Math.PI *2.0f /segments;
        float r1, x0, y0, x1, y1;    
    	for(int j =0; j <segments; j++){	
	        r1 =1;//(float) Math.sin((1)*Latitude);
            x1 =x0 =(float) (r1 *Math.sin((j)*Longitude));
            y1 =y0 =(float) (r1 *Math.cos((j)*Longitude));
            b.put(x0).put(y0).put(1);
            x0 =(float) (r1 *Math.sin((j +1)*Longitude));
            y0 =(float) (r1 *Math.cos((j +1)*Longitude));
            b.put(x1).put(y1).put(1);
            
            b.put(x0).put(y0).put(0);
            b.put(x1).put(y1).put(0);
            b.put(x0).put(y0).put(1);
            b.put(x1).put(y1).put(1);
            
            b.put(x0).put(y0).put(1);
            b.put(x0).put(y0).put(0);
            System.out.println(x0+"  "+y0);
    	}
		
		b.flip();
		cilinder =new vbo(b, 8 *segments);
	}
	
	public void setup(){
		this.matrix.setIdentity();
		this.matrix.translate(getX(), getY(), getZ() +zOffset);
		this.matrix.scale(getRadius(), getRadius(), height);
	}
	
	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void draw() {
		if(cilinder !=null){
			setup();
			GL20.glUniformMatrix4fv(1, true, matrix.asBuffer());
			cilinder.draw();
		}
		
	}

	@Override
	public float getX() {
		return p.getX();
	}

	@Override
	public float getY() {
		return p.getY();
	}

	@Override
	public float getZ() {
		return p.getZ();
	}
	
	public float getRadius(){
		return radius;
	}
	
	public float getHeight(){
		return height;
	}

	public float getzOffset() {
		return zOffset;
	}

	public void setzOffset(float zOffset) {
		this.zOffset = zOffset;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public Point getP() {
		return p;
	}

	public void setP(Point p) {
		this.p = p;
	}

}
