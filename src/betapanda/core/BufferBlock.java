package betapanda.core;

import static org.lwjgl.opengl.ARBUniformBufferObject.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.ARBUniformBufferObject.glBindBufferBase;
import static org.lwjgl.opengl.ARBUniformBufferObject.glGetUniformBlockIndex;
import static org.lwjgl.opengl.ARBUniformBufferObject.glUniformBlockBinding;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL15;

public abstract class BufferBlock{
	
	private String name;

	public int buffer;
	public FloatBuffer data;
	private static boolean debug =false;;
	
	public BufferBlock(String name){
		this.name =name;
		buffer =GL15.glGenBuffers();
		init();
	}
	
	public abstract void init();	//ment for seting up glbindbuffer and glbufferdata
	public abstract void updateVariables();	//ment for putting data in the buffer and afterwards updated to gpu by the method
	
	public void update(){
		updateVariables();
		updateToGPU();
	}
	
	public void updateToGPU(){
		GL15.glBindBuffer(GL_UNIFORM_BUFFER, buffer);
		GL15.glBufferSubData(GL_UNIFORM_BUFFER, 0, data);			
	}
	
	public void bindBuffer(Shader shader){
		GL15.glBindBuffer(GL_UNIFORM_BUFFER, buffer);
		int blockIndex =glGetUniformBlockIndex(shader.getProgram(), this.name);
		glUniformBlockBinding(shader.getProgram(), blockIndex, 1);
		glBindBufferBase(GL_UNIFORM_BUFFER, 1, buffer);
		
		if(debug)
			System.out.println("[BufferBlock: "+this.name+"] is binded to "+shader.name);
		
	}
	
	public String getName(){
		return this.name;
	}
	
	@Override
	public String toString(){
		return name;
	}
}