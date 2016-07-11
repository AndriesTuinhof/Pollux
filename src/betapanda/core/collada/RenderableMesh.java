package betapanda.core.collada;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import awesome.math.Matrix;

public class RenderableMesh
{
	
	private int glBuffer, glArrayObject;
	private float[] data;
	private Matrix matrix = new Matrix();
	
	// Loader
	public RenderableMesh(float[] data, float[] matrix2)
	{
		this.data = data;
		this.matrix.set(matrix2);
		glBuffer = GL15.glGenBuffers();
		glArrayObject = GL30.glGenVertexArrays();
		render();
	}
	
	// Render
	private void render()
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		
		buffer.flip();
		GL30.glBindVertexArray(glArrayObject);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glBuffer);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 32, 0);
		GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 32, 12);
		GL20.glVertexAttribPointer(3, 2, GL11.GL_FLOAT, false, 32, 24);
		GL30.glBindVertexArray(0);
	}
	
	
	// Draw
	public void draw()
	{
		GL20.glUniformMatrix4fv(1, true, matrix.asBuffer());
		GL30.glBindVertexArray(glArrayObject);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, data.length/8*3);
		GL30.glBindVertexArray(0);
	}
	
	public void draw(int instances)
	{
		GL30.glBindVertexArray(glArrayObject);
		GL31.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, data.length/8*3, instances);
		GL30.glBindVertexArray(0);
	}
	
	// Free
	public void destroy()
	{
		GL30.glDeleteVertexArrays(glArrayObject);
		GL15.glDeleteBuffers(glBuffer);
	}
}
