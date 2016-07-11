package betapanda.util.animation;

import awesome.math.Matrix;
import betapanda.util.Painter3D;

public class Bone {

	public Bone parent;
	public Bone[] children;
	public String name;
	
	public Matrix matrix_local;
	public Matrix matrix_world;
	public Matrix inverseMatrix;
	
	public Bone(String name, Matrix m, Bone parent){
		this.name =name;
		this.matrix_local =m;
		this.parent =parent;
		this.matrix_world =new Matrix();
		if(parent !=null)
			this.matrix_world.setMult(matrix_local, parent.matrix_world);
		else 
			this.matrix_world =matrix_local;
	}
	
	public void draw(float scale){
		if(parent ==null)return;
		
		Painter3D.setColor(0.941176471f, 0.447058824f, 0.0254403131f);
		Painter3D.setVertex(matrix_world.m[3] *scale, matrix_world.m[7] *scale, matrix_world.m[11] *scale, 0, 0);
		Painter3D.setVertex(parent.matrix_world.m[3] *scale, parent.matrix_world.m[7] *scale, parent.matrix_world.m[11] *scale, 0, 0);
		
	}

	public void updateMatrix() {
		if(parent!=null)
			matrix_world.setMult(parent.matrix_world, matrix_local);
		else
			matrix_world.set(matrix_local);
	}
}
