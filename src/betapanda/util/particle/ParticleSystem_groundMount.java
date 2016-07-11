package betapanda.util.particle;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import awesome.core.Timing;
import awesome.math.Matrix;
import awesome.util.Mass;
import betapanda.core.Shader;

public class ParticleSystem_groundMount extends ParticleSystem{
	
	public particleFirshot[] particles =new particleFirshot[30];

	private Shader shader;
	private int glVertexArray, glBuffer;
	private Matrix m;
	private Mass mass;
	
	public ParticleSystem_groundMount(Mass mass){
		shader =Shader.getShader("particleGround", "matrixBlock");
		m =new Matrix();
		this.mass =mass;
	}
	
	@Override
	public void initialize() {			
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
		ParticleManager.addSystem(this);
		
	    float Longitude =(float)Math.PI *2.0f /15;
	    float x0, y0;
	    for(int j =0; j <30; j ++){	
		   	x0 =(float) (1 *Math.sin((j)*Longitude));
	    	y0 =(float) (1 *Math.cos((j)*Longitude));
	    	particles[j] =new particleFirshot(mass.x +x0, mass.y +y0, mass.z, 0, 0, 0.5f);
	    }
	}

	@Override
	public void logic() {
		for(int i =0; i <particles.length; i ++)if(particles[i] !=null){
			particles[i].logic();
			
		}
		m.setIdentity();
		m.translate(mass.x, mass.y, mass.z);
		
	}

	@Override
	public void draw() {
		shader.bind();
		GL20.glUniform4f(2, 120 /255f, 72 /255f, 0f, 1);
		GL20.glUniform4f(3, 0.1f, 0.1f, 0.1f, 1);
		GL20.glUniformMatrix4fv(4, true, m.asBuffer());
		int a =0;
//		if(particles[0] !=null)
//		System.out.println(""+particles[0].mass.getZ());
	    for(int i =0; i <particles.length; i ++)if(particles[i] !=null){
			if(particles[i].getLifeTime() >0){	
				GL20.glUniform3f(5 +a, particles[i].mass.getX(), particles[i].mass.getY(), particles[i].mass.getZ());
				a ++;
			}
	    }
		
	    
		GL30.glBindVertexArray(glVertexArray);
		GL31.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, 6, a);
		GL30.glBindVertexArray(0);
		
	}

	@Override
	public void dispose() {
		for(int i =0; i <particles.length; i ++)
			particles[i] =null;
		
		
	}

	class particleFirshot extends Particle{

		public float lifeTime =2;
		
		public particleFirshot(float x, float y, float z, float xs, float ys, float zs) {
			this.mass.setX(x);
			this.mass.setY(y);
			this.mass.setZ(z);
			this.mass.setXspeed(xs);
			this.mass.setYspeed(ys);
			this.mass.setZspeed(zs);
		}
		
		float friction;
		@Override
		public void logic() {
			this.mass.stepMomentum();
//			lifeTime -=Timing.delta() /60f;

			float gravity =(9.806f/60f);
			mass.zspeed -=gravity *Timing.delta() /friction;
			
			float friction =0.1f;
			mass.setXspeed(mass.getXspeed() -mass.getXspeed() *friction *Timing.delta());
			mass.setYspeed(mass.getYspeed() -mass.getYspeed() *friction *Timing.delta());
			
		}

		@Override
		public void setup(int i) {
			GL20.glUniform3f(5 +i, mass.getX(), mass.getY(), mass.getZ());
			
		}

		@Override
		public float getLifeTime() {
			return lifeTime;
			
		}

		@Override
		public float getSize() {
			return 1;
			
		}		
	}
}
