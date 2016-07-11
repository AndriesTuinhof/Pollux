package betapanda.util.collision;

import org.lwjgl.opengl.GL11;

import awesome.util.Mass;
import betapanda.util.Painter3D;

public class Point extends Collision{

	public Mass mass;
	
	public Point(){
		mass =new Mass();
		
	}
	
	public Point(Mass mass){
		this.mass =mass;
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void draw() {
		Painter3D.beginDrawing(true);
		Painter3D.setVertex(getX(), getY(), getZ(), 0, 0);
		Painter3D.stopDrawing(GL11.GL_POINT);
		
	}

	@Override
	public float getX() {
		return mass.getX();
	}

	@Override
	public float getY() {
		return mass.getY();
	}

	@Override
	public float getZ() {
		return mass.getZ();
	}
}
