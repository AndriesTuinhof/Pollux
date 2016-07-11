package betapanda.util;

import org.lwjgl.opengl.GL20;

import awesome.core.Painter;
import awesome.graphics.Wavefront;
import awesome.math.Matrix;
import awesome.util.Mass;
import betapanda.core.Shader;

public class Model {

	public Shader shader;
	public Wavefront model;
	public Mass mass =new Mass();
	private Matrix m =new Matrix();
	public float rotateX, rotateY, rotateZ;
	public float scale =1;
	public int texture;
	
	public Model(){
		
	}
	
	public Model(Wavefront w){
		model =w;
		shader =Shader.getShader("wavefront", "matrixBlock");
	}
	
	public void logic(){
		mass.stepMomentum();
		m.setIdentity();
		m.translate(mass.getX(), mass.getY(), mass.getZ());
		m.scale(scale, scale, scale);
		m.rotateZ(rotateZ);
	}
	
	public void draw(){
		shader.bind();
		Painter.setTexture(texture);
		GL20.glUniformMatrix4fv(1, true, m.asBuffer());
		model.draw();
	}
	
	public void dispose(){
		
	}
}
