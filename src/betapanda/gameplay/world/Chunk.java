package betapanda.gameplay.world;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL15.*;

import betapanda.util.Model;
import betapanda.util.collision.Collision;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class Chunk {

	public static int size =16;
	private Octree EntitiyOctree;
	public List<Collision> EnviromentCollisions;
	
	private int x, y;
	public World world;
	public boolean isDirty =false, isGenerated =false, isPreRendered =false; 
	public chunkVBO model;
	public ArrayList<Model> models =new ArrayList<>(0);
	
	public grassFloor grass;
	
	public Chunk(int x, int y, World world){
		this.x =x;
		this.y =y;
		this.world =world;
		isDirty =true;
		EntitiyOctree =new Octree(0, getXX(), getYY(), size, size);
		EnviromentCollisions =new ArrayList<>();
		
		
		grass =new grassFloor(this);
		grass.initialize();
		
	}
	
	public int bufferBlocks =0;
	public FloatBuffer buffer;
	public int vertices;
	
	public Octree getOctree(){
		return EntitiyOctree;
	}
	
	public void logic(){
		for(Model m: models)if(m !=null)
			m.logic();
	}
	
	public void render(){
		if(isPreRendered)return;
		try{
			if(model ==null)
				model =new chunkVBO(buffer, vertices);
			else
				model.reBuffer(buffer, vertices);
				isPreRendered =true;
				this.buffer =null;
				this.vertices =0;
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void drawTerrain(){
//		if(model !=null){			
//			glUniform2f(1,  getXX() , getYY());
//			model.draw();
//		}
//		glActiveTexture(GL_TEXTURE0);
//		
//		EntitiyOctree.draw();
		
	}
	
	public void drawEnviroment(){
//		for(Model m: models)if(m !=null){
//			m.draw();
//		}
		grass.draw();
	}
	
	public void dispose(){
		model.dispose();
	}
	
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getXX(){
		return x *size;
	}
	public int getYY(){
		return y *size;
	}

	
	public class chunkVBO{
		
		private int vertices;
		
		private int glVertexArray, glBuffer;
		public boolean bufferd =false;
		
		public chunkVBO(FloatBuffer buffer, int vertices){
			this.vertices =vertices;
			glBindVertexArray(glVertexArray =glGenVertexArrays());
			glBindBuffer(GL_ARRAY_BUFFER, glBuffer =glGenBuffers());
			glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);
			glEnableVertexAttribArray(1);
			glEnableVertexAttribArray(2);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, 24, 0); 
			glVertexAttribPointer(2, 3, GL_FLOAT, false, 24, 12); 
			glBindVertexArray(0);
			bufferd =true;
		}
		
		public void reBuffer(FloatBuffer buffer, int vertices){
			if(buffer ==null)return;
			this.vertices =vertices;
			glBindVertexArray(glVertexArray);
			glBindBuffer(GL_ARRAY_BUFFER, glBuffer);
			glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);
			glEnableVertexAttribArray(1);
			glEnableVertexAttribArray(2);
			glVertexAttribPointer(1, 3, GL_FLOAT, false, 24, 0); 
			glVertexAttribPointer(2, 3, GL_FLOAT, false, 24, 12); 
			glBindVertexArray(0);
			bufferd =true;
		}
		
		public void draw(){
			if(!bufferd) return;			
			glBindVertexArray(glVertexArray);
			glDrawArrays(GL_TRIANGLES, 0, vertices);
			glBindVertexArray(0);		
		}
		
		public void dispose(){
			glDeleteVertexArrays(glVertexArray);
			glDeleteBuffers(glBuffer);			
		}
	}
}
