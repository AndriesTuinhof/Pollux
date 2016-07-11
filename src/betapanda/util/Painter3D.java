package betapanda.util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import awesome.math.Matrix;
import betapanda.core.Shader;

public class Painter3D {
	
	private static final int DRAWING_BUFFER_SIZE =400*6*8;
	private static int glVertexArray, glBuffer;
	private static FloatBuffer buffer;
	private static Shader shader;
	private static int WHITE_TEXTURE;
	private static float red, green, blue, alpha;
	private static Matrix emptymatrix =new Matrix();
	
	public static void create() {
		buffer =BufferUtils.createFloatBuffer(DRAWING_BUFFER_SIZE);
		shader = Shader.getShader("overlayMatrix", "matrixBlock");
		
		glBindVertexArray(glVertexArray =glGenVertexArrays());
		glBindBuffer(GL_ARRAY_BUFFER, glBuffer =glGenBuffers());
		glBufferData(GL_ARRAY_BUFFER, DRAWING_BUFFER_SIZE*4, GL_STREAM_DRAW);
		int stride =20;
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 0); // VERTEX
		glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 12); // UV
		glBindVertexArray(0);
		
		
		
		emptymatrix.setIdentity();
		WHITE_TEXTURE =glGenTextures();
		FloatBuffer buff =BufferUtils.createFloatBuffer(4);
		buff.put(1).put(1).put(1).put(1).flip();
		glBindTexture(GL_TEXTURE_2D, WHITE_TEXTURE);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1, 1, 0, GL_RGBA, GL_FLOAT, buff);
	}
	
	public static void destroy() {
		glDeleteVertexArrays(glVertexArray);
		glDeleteBuffers(glBuffer);
		glDeleteTextures(WHITE_TEXTURE);
	}
	
	public static void setBlankTexture() {
		glBindTexture(GL_TEXTURE_2D, WHITE_TEXTURE);
	}
	
	public static void setColor(float r, float g, float b, float a) {
		red = r;
		green = g;
		blue = b;
		alpha = a;
	}
	
	public static void setColor(float r, float g, float b) {
		setColor(r, g, b, 1);
	}
	
	private static void drawQuad(float x, float y, float z, float w, float h, boolean usesShader, Matrix m) {
		buffer.clear();
		
		vertex(x, y+h, z);
		buffer.put(1).put(0);
		
		vertex(x, y, z);
		buffer.put(0).put(0);
		
		vertex(x+w, y+h, z);
		buffer.put(1).put(1);


		vertex(x+w, y, z);
		buffer.put(0).put(1);
		
		vertex(x+w, y+h, z);
		buffer.put(1).put(1);		
		
		vertex(x, y, z);
		buffer.put(0).put(0);
		
		
		
		buffer.flip();
		
		if(usesShader) shader.bind();
		if(m !=null)GL20.glUniformMatrix4fv(1, true, m.asBuffer());
		else 
			GL20.glUniformMatrix4fv(1, true, emptymatrix.asBuffer());
		GL20.glUniform4f(2, red, green, blue, alpha);
		glBindVertexArray(glVertexArray);
		glBindBuffer(GL_ARRAY_BUFFER, glBuffer);
		glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
		glDrawArrays(GL_TRIANGLES, 0, 6);
		glBindVertexArray(0);
	}
	
	public static void drawQuad(float x, float y, float z, float w, float h, Matrix m) {
		drawQuad(x, y, z, w, h, true, m);
	}	

	public static void drawQuadNoShader(float x, float y, float z, float w, float h, Matrix m) {
		drawQuad(x, y, z, w, h, false, m);
	}
	
	public static void drawLine(float x, float y, float z, float x1, float y1, float z1, float t, Matrix m){
		lineThickness(t);
		beginDrawing(true);
		setVertex(x, y, z, 0, 0);
		setVertex(x1, y1, z1, 1, 1);
		stopDrawing(m, GL11.GL_LINES);
		lineThickness(1);
	}
	
	public static void drawLine(float x, float y, float z, float x1, float y1, float z1, float t){
		drawLine(x, y, z, x1, y1, z1, t, null);
	}
	
	public static void lineThickness(float t){
		GL11.glLineWidth(t);
	}
	
	private static void vertex(float x, float y, float z) {
		buffer.put(x).put(y).put(z);
	}
	
	private static void uv(float u, float t){
		buffer.put(u).put(t);
	}
	
	public static void setTexture(int tex) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
	}
	
	public static int WHITE_TEXTURE(){
		return WHITE_TEXTURE;
	}	
	
	public static int vertices =0;
	public static void beginDrawing(boolean useshader){
		buffer.clear();
		vertices =0;
		
		if(useshader)
			shader.bind();
	}
	
	public static void setVertex(float x, float y, float z){
		vertex(x, y, z);
		vertices++;
	}	
	
	public static void setVertex(float x, float y, float z, float u, float t){
		vertex(x, y, z);
		setUV(u, t);
		vertices++;
	}
	
	public static void setUV(float u, float t){
		uv(u, t);		
	}
		
	public static void stopDrawing(Matrix m, int gl){
		buffer.flip();

		if(m !=null)
			GL20.glUniformMatrix4fv(1, true, m.asBuffer());
		else 
			GL20.glUniformMatrix4fv(1, true, emptymatrix.asBuffer());
		GL20.glUniform4f(2, red, green, blue, alpha);
		
		glBindVertexArray(glVertexArray);
		glBindBuffer(GL_ARRAY_BUFFER, glBuffer);
		glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
		glDrawArrays(gl, 0, vertices);
		glBindVertexArray(0);
	}

	public static void stopDrawing(int gl){
		stopDrawing(null, gl);
	}
}
