package betapanda.util.particle;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class ParticleSystem_Shotgun extends ParticleSystem{

	public particleFirshot[] particles =new particleFirshot[30];

//	private Shader shader;
	private int glVertexArray, glBuffer;
	
	public ParticleSystem_Shotgun() {
		
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
	
	@Override
	public void initialize() {
//	    float Longitude =(float)Math.PI*2.0f/15;
//	    float x0, y0;
//		shader.bind();
//	    for(int j =0; j <15; j ++){	
//		   	x0 =(float) (1*Math.sin((j)*Longitude));
//	    	y0 =(float) (1*Math.cos((j)*Longitude));
//	    	particles[j] =new particleFirshot(gun.mass.getX() +x0, gun.mass.getY() +y0, gun.mass.getZ(), 1, 0, 0);
//	    	GL20.glUniform3f(4+j, particles[j].mass.getX(), particles[j].mass.getY(), particles[j].mass.getZ());
//	    }
	}

	@Override
	public void logic() {
		for(Particle p: particles)if(p !=null){
			p.logic();
		}
		
	}

	@Override
	public void draw() {
		for(Particle p: particles)if(p !=null){
//			p.draw();
		}
		
	}

	@Override
	public void dispose() {
		for(int i =0; i <particles.length; i ++)
			particles[i] =null;
		
		
	}

	class particleFirshot extends Particle{

		public float lifeTime;
		
		public particleFirshot(float x, float y, float z, float xs, float ys, float zs) {
			this.mass.setX(x);
			this.mass.setY(y);
			this.mass.setZ(z);
			this.mass.setXspeed(xs);
			this.mass.setYspeed(ys);
			this.mass.setZspeed(zs);
		}
		
		@Override
		public void logic() {
			this.mass.stepMomentum();
			
		}
//
//		@Override
//		public void draw() {
//			// TODO Auto-generated method stub
//			
//		}

		@Override
		public float getLifeTime() {
			return lifeTime;
			
		}

		@Override
		public float getSize() {
			return 1;
			
		}

		@Override
		public void setup(int i) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
