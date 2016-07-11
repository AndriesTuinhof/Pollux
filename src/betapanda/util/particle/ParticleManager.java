package betapanda.util.particle;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class ParticleManager {
	
	protected static ArrayList<ParticleSystem> systems;
	protected static int glVertexArray, glBuffer;	//protected or private, or make a get don't make a setter
	
	public static void setup(){
		systems =new ArrayList<>(0);		
		
		glBuffer =GL15.glGenBuffers();
		glVertexArray =GL30.glGenVertexArrays();
		FloatBuffer buffer =BufferUtils.createFloatBuffer(12);
		float s = 1f;
		buffer.put(-s).put(-s);
		buffer.put(s).put(-s);
		buffer.put(s).put(s);
		buffer.put(-s).put(-s);
		buffer.put(s).put(s);
		buffer.put(-s).put(s);
		
		buffer.flip();
		GL30.glBindVertexArray(glVertexArray);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glBuffer);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 8, 0);
		GL30.glBindVertexArray(0);
		
	}
	
	public static void logic(){
		for(ParticleSystem p: systems)if(p !=null){
			p.logic();
		}
	}
	
	public static void draw(){
		for(ParticleSystem p: systems)if(p !=null){
			p.draw();
		}
	}
	
	public static void dispose(){
		for(ParticleSystem p: systems)if(p !=null){
			p.dispose();
		}
	}
	
	public static void addSystem(ParticleSystem p){
		systems.add(p);
	}
	
	public static void removeSystem(ParticleSystem p){
		systems.remove(p);
	}
}
