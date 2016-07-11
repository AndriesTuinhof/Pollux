package betapanda.util;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

public class Vbo{

	private int vertices;
	private int glVertexArray, glBuffer;
	
	public Vbo(FloatBuffer buffer, int vertices){
		this.vertices =vertices;
		glBindVertexArray(glVertexArray =glGenVertexArrays());
		glBindBuffer(GL_ARRAY_BUFFER, glBuffer =glGenBuffers());
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 12, 0); 
		glBindVertexArray(0);
	}
	
	public void draw(){		
		glBindVertexArray(glVertexArray);
		glDrawArrays(GL11.GL_LINES, 0, vertices);
		glBindVertexArray(0);
	}
	
	public void dispose(){
		glDeleteVertexArrays(glVertexArray);
		glDeleteBuffers(glBuffer);	
	}
}