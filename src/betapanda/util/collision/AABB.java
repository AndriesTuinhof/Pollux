package betapanda.util.collision;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import awesome.graphics.Wavefront;
import betapanda.util.Painter3D;

public class AABB extends Collision{

	public float maxX, maxY, maxZ;
	public float minX, minY, minZ;
	public float x, y, z;
		
	public AABB(Wavefront wave){
		this.maxX =wave.maxX;
		this.maxY =wave.maxY;
		this.maxZ =wave.maxZ;
		this.minX =wave.minX;
		this.minY =wave.minY;
		this.minZ =wave.minZ;	
	}	
	
	public AABB(float maxX, float maxY, float maxZ, float minX, float minY, float minZ){
		this.shape ="AABB";
		this.maxX =maxX;
		this.maxY =maxY;
		this.maxZ =maxZ;
		this.minX =minX;
		this.minY =minY;
		this.minZ =minZ;
	}
	
	public void updateOrigin(float x, float y, float z){
		this.x =x;
		this.y =y;
		this.z =z;
		
	}

	public void updateMax(float maxX, float maxY, float maxZ){
		this.maxX =maxX;
		this.maxY =maxY;
		this.maxZ =maxZ;	
	}
	
	public void updateMin(float minX, float minY, float minZ){
		this.minX =minX;
		this.minY =minY;
		this.minZ =minZ;		
	}
	
	public void updateSize(float maxX, float maxY, float maxZ, float minX, float minY, float minZ){
		updateMax(maxX, maxY, maxZ);
		updateMin(minX, minY, minZ);
	}
	
	public boolean AABBinAABB(AABB b){
		
		return false;
	}
	
	private float check(float v, float min, float max){
		float out =0;
		if(v <min)
			out +=(min -v) *(min -v);
		
		if(v >max)
			out +=(v -max) *(v -max);			
		
		return out;
	}
	
	public boolean SphereInAABB(Sphere s){
		float sq =0;
		sq +=check(s.getX(), x +minX, x +maxX);
		sq +=check(s.getY(), y +minY, y +maxY);
		sq +=check(s.getZ(), z +minZ, z +maxZ);
		
		return sq <=(s.getRadius() *s.getRadius());
	}
	
	public boolean pointInAABB(float x, float y, float z){
		return x >(minX +this.x) && x <(maxX +this.x) && y >(minY +this.y) && y <(maxY +this.y) && z >(minZ +this.z) && z <(maxZ +this.z);
	}
	
	public boolean TriangleInAABB(Vector3f v1, Vector3f v2, Vector3f v3){
		float maxX =Math.max(v1.x, Math.max(v2.x, v3.x)), maxY =Math.max(v1.y, Math.max(v2.y, v3.y)), maxZ =Math.max(v1.z, Math.max(v2.z, v3.z));
		float minX =Math.min(v1.x, Math.min(v2.x, v3.x)), minY =Math.min(v1.y, Math.min(v2.y, v3.y)), minZ =Math.min(v1.z, Math.min(v2.z, v3.z));
		return !(this.x +this.maxX <minX || maxX <this.x +this.minX|| this.y +this.maxY <minY|| maxY <this.y +this.minY||this.z +this.maxZ <minZ|| maxZ <this.z +this.minZ);
		
	}
	
	public boolean TriangleInAABB(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3){
		float maxX =Math.max(x1, Math.max(x2, x3)) +0.05f, maxY =Math.max(y1, Math.max(y2, y3)) +0.05f, maxZ =Math.max(z1, Math.max(z2, z3)) +0.05f;
		float minX =Math.min(x1, Math.min(x2, x3)) -0.05f, minY =Math.min(y1, Math.min(y2, y3)) -0.05f, minZ =Math.min(z1, Math.min(z2, z3)) -0.05f;
		return !(this.x +this.maxX <minX || maxX <this.x +this.minX|| this.y +this.maxY <minY|| maxY <this.y +this.minY||this.z +this.maxZ <minZ|| maxZ <this.z +this.minZ);
		
	}
	
	public boolean AABBInAABB(AABB b){
		return !(this.x +this.maxX <b.x +b.minX || b.x +b.maxX <this.x +this.minX|| this.y +this.maxY <b.y +b.minY|| b.y +b.maxY <this.y +this.minY||this.z +this.maxZ <b.z +b.minZ|| b.z +b.maxZ <this.z +this.minZ);
	}
	

	@Override
	public void draw(){		//this is kinda fps consuming
		Painter3D.setBlankTexture();
		Painter3D.beginDrawing(true);
		Painter3D.setVertex(x+maxX, y+maxY, z+maxZ);
		Painter3D.setVertex(x+maxX, y+maxY, z+minZ);

		Painter3D.setVertex(x+maxX, y+minY, z+maxZ);
		Painter3D.setVertex(x+maxX, y+minY, z+minZ);

		Painter3D.setVertex(x+minX, y+maxY, z+maxZ);
		Painter3D.setVertex(x+minX, y+maxY, z+minZ);
		
		Painter3D.setVertex(x+minX, y+minY, z+maxZ);
		Painter3D.setVertex(x+minX, y+minY, z+minZ);

		
		
		Painter3D.setVertex(x+maxX, y+maxY, z+maxZ);
		Painter3D.setVertex(x+maxX, y+minY, z+maxZ);

		Painter3D.setVertex(x+minX, y+minY, z+maxZ);
		Painter3D.setVertex(x+minX, y+maxY, z+maxZ);

		Painter3D.setVertex(x+maxX, y+maxY, z+minZ);
		Painter3D.setVertex(x+maxX, y+minY, z+minZ);

		Painter3D.setVertex(x+minX, y+minY, z+minZ);
		Painter3D.setVertex(x+minX, y+maxY, z+minZ);


		Painter3D.setVertex(x+maxX, y+minY, z+maxZ);
		Painter3D.setVertex(x+minX, y+minY, z+maxZ);
		
		Painter3D.setVertex(x+maxX, y+maxY, z+maxZ);
		Painter3D.setVertex(x+minX, y+maxY, z+maxZ);
		
		Painter3D.setVertex(x+maxX, y+minY, z+minZ);
		Painter3D.setVertex(x+minX, y+minY, z+minZ);
		
		Painter3D.setVertex(x+maxX, y+maxY, z+minZ);
		Painter3D.setVertex(x+minX, y+maxY, z+minZ);
		
		Painter3D.stopDrawing(GL11.GL_LINES);
	}
	
	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getZ() {
		// TODO Auto-generated method stub
		return 0;
	}
}
