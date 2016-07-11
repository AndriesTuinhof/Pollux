package betapanda.util.collision;

import java.util.ArrayList;

import org.lwjgl.util.Color;

import awesome.math.Matrix;

public abstract class Collision {
	
	public static ArrayList<Collision> collisions =new ArrayList<>();
	
	public Color color =new Color(1, 1, 1);
	public Matrix matrix =new Matrix();
	public String shape;
	private boolean remove;
	
	public abstract boolean isActive();
	public abstract void draw();
	
	public abstract float getX();
	public abstract float getY();
	public abstract float getZ();
	
	public void remove(){
		remove =true;
	}
	
	public boolean isRemove(){
		return remove;
	}
}
