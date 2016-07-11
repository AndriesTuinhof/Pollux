package betapanda.util.collision;


import org.lwjgl.opengl.GL20;

import betapanda.core.Shader;

public class CollisionDebugger {
	
	public static Shader shader;
	
	public static void setup(){
		shader =Shader.getShader("collision", "matrixBlock");
	}
	
	public static void draw(){
		Collision co;
		for(int i =0; i <Collision.collisions.size(); i++)if((co =Collision.collisions.get(i)) !=null){
			if(co.isRemove())
				Collision.collisions.remove(i);
		}
		shader.bind();
		for(Collision c:Collision.collisions)if(c !=null){
			GL20.glUniformMatrix4fv(1, true, c.matrix.asBuffer());
			GL20.glUniform3f(2, c.color.getRed(), c.color.getGreen(), c.color.getBlue());
			c.draw();
		}
	}
	
	public void dispose(){
		
	}
}