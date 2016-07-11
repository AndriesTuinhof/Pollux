package betapanda.gameplay;

import awesome.math.Matrix;
import awesome.util.Mass;
import betapanda.util.collision.Collision;

public abstract class Entity {

	public Mass mass =new Mass();
	public Matrix m =new Matrix();
	
	public float pitch, yaw, roll;
	
	public abstract void logic();
	public abstract void draw();
	public abstract void drawShadow();
	public abstract void dispose();
	public abstract Collision getCollision();
	
	public void setPosition(float x, float y, float z){
		this.mass.setX(x);
		this.mass.setY(y);
		this.mass.setZ(z);
	}
	public float getPitch() {
		return pitch;
	}
	public float getYaw() {
		return yaw;
	}
	public float getRoll() {
		return roll;
	}
	
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	public void setRoll(float roll) {
		this.roll = roll;
	}
}
