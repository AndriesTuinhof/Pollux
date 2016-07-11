package betapanda.util.collision;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import awesome.util.Mass;

public class Box extends Collision{

	public Point p;
	private float w, h, d;
	private static vbo box;
	
	public Box(float x, float y, float z, float w, float h, float d){
		this.shape ="BOX";
		p =new Point();
		p.mass.setX(x);
		p.mass.setY(y);
		p.mass.setZ(z);
		this.w =w;
		this.h =h;
		this.d =d;
		if(box ==null)
			render();
		Collision.collisions.add(this);
		setup();
			
	}
	
	public Box(Mass mass, float w, float h, float d){
		this.shape ="BOX";
		p =new Point(mass);
		this.w =w;
		this.h =h;
		this.d =d;
		if(box ==null)
			render();
		Collision.collisions.add(this);
		setup();
		
	}
	
	public void render(){
		FloatBuffer b =BufferUtils.createFloatBuffer(24 *3);
		b.put(0).put(0).put(0);
		b.put(1).put(0).put(0);
		
		b.put(1).put(0).put(0);
		b.put(1).put(1).put(0);
		
		b.put(0).put(0).put(0);
		b.put(0).put(1).put(0);
		
		b.put(0).put(1).put(0);
		b.put(1).put(1).put(0);
		

		b.put(0).put(0).put(1);
		b.put(1).put(0).put(1);
		
		b.put(1).put(0).put(1);
		b.put(1).put(1).put(1);
		
		b.put(0).put(0).put(1);
		b.put(0).put(1).put(1);
		
		b.put(0).put(1).put(1);
		b.put(1).put(1).put(1);
		

		b.put(0).put(0).put(0);
		b.put(0).put(0).put(1);
		
		b.put(1).put(0).put(0);
		b.put(1).put(0).put(1);
		
		b.put(0).put(1).put(0);
		b.put(0).put(1).put(1);
		
		b.put(1).put(1).put(0);
		b.put(1).put(1).put(1);
		
		b.flip();
		box =new vbo(b, 24);
	}
	
	public void setup(){
		this.matrix.setIdentity();
		this.matrix.translate(getX(), getY(), getZ());
		this.matrix.scale(getWidth(), getHeight(), getDepth());
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

	public float getWidth(){
		return w;
	}

	public float getHeight(){
		return h;
	}

	public float getDepth(){
		return d;
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void draw() {
		if(box !=null){
			setup();
			GL20.glUniformMatrix4fv(1, true, matrix.asBuffer());
			box.draw();
		}
		
	}

}
