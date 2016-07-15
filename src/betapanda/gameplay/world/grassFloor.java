package betapanda.gameplay.world;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import awesome.core.ResourceLoader;
import awesome.core.Timing;
import awesome.graphics.Wavefront;
import awesome.math.noise.SimplexNoise;
import betapanda.core.Shader;
import betapanda.util.vec4;

public class grassFloor {

	private static Shader shader;
	private static Wavefront grass, flower, bush;
	private static int textureGrass[]=new int[3], textureFlower, textureBush;
	
	private int grassInstances, flowerInstances, bushInstances;
	private vec4[] grassEnviroment;
	private vec4[] flowerEnviroment;
//	private vec4[] bushEnviroment;
	
	public grassFloor(Chunk c){
//		float s =0.1f;
		grassEnviroment =new vec4[Chunk.size *Chunk.size];
		flowerEnviroment =new vec4[Chunk.size *Chunk.size];
//		bushEnviroment =new vec4[Chunk.size *Chunk.size];
		float a;
		for(int x =0; x <Chunk.size; x ++){
			for(int y =0; y <Chunk.size; y ++){
				a =(SimplexNoise.noise(c.getXX() +x, c.getYY() +y) +1) /2;
				float w =2 +a *0.2f +0.6f;
				if((a >0.1f && a <0.3f)){					
					grassEnviroment[grassInstances] =new vec4(c.getXX() +x, c.getYY() +y, -0.001f, w *2f);
					grassInstances ++;
				}
				else if(a >0.1f && a <0.2f){	
					flowerEnviroment[flowerInstances] =new vec4(c.getXX() +x, c.getYY() +y, -0.0001f, w *2f);
					flowerInstances ++;
					
				}
//				else if(Math.random() <0.0625f){
//					bushEnviroment[bushInstances] =new vec4(c.getXX() +x, c.getYY() +y, -0.0001f, w *5f);
//					bushInstances ++;
//					
//				}
			}
		}
	}
	
	public void initialize(){
		try{
			if(shader ==null)
				shader =Shader.getShader("grass", "matrixBlock");
			if(grass ==null){
				grass =new Wavefront(ResourceLoader.openFile("Models/environment/grass1.obj"));
				textureGrass[0] =ResourceLoader.getTexture("Models/environment/textures/grass1.png", false);
				textureGrass[1] =ResourceLoader.getTexture("Models/environment/textures/grass2.png", false);
				textureGrass[2] =ResourceLoader.getTexture("Models/environment/textures/grass3.png", false);
			}

			if(flower ==null){
				flower =new Wavefront(ResourceLoader.openFile("Models/environment/ivy1.obj"));
				textureFlower =ResourceLoader.getTexture("Models/environment/textures/ivy1.png", false);
			}
			if(bush ==null){
//				bush=new Wavefront(ResourceLoader.openFile("Models/environment/object0.obj"));
//				textureBush=ResourceLoader.getTexture("Models/environment/textures/texture0.png", true);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	float time;
	boolean uploaded = false;
	public void draw(){
		glDisable(GL_CULL_FACE);
		shader.bind();
		GL20.glUniform1f(5, time+=Timing.delta()*0.04f);
		glActiveTexture(GL_TEXTURE0);
		
		// First Texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureGrass[0]);
		for(int i =0; i <grassInstances/3; i+=1)if(grassEnviroment[i*3+0] !=null){
			GL20.glUniform4f(6 +i, grassEnviroment[i*3+0].x, grassEnviroment[i*3+0].y, grassEnviroment[i*3+0].z, grassEnviroment[i*3+0].w);
		}
		grass.draw(grassInstances/3);
		
		// Second Texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureGrass[1]);
		for(int i =0; i <grassInstances/3; i+=1)if(grassEnviroment[i*3+1] !=null){
			GL20.glUniform4f(6 +i, grassEnviroment[i*3+1].x, grassEnviroment[i*3+1].y, grassEnviroment[i*3+1].z, grassEnviroment[i*3+1].w);
		}
		grass.draw(grassInstances/3);
		
		// Third Texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureGrass[2]);
		for(int i =0; i <grassInstances/3; i+=1)if(grassEnviroment[i*3+2] !=null){
			GL20.glUniform4f(6 +i, grassEnviroment[i*3+2].x, grassEnviroment[i*3+2].y, grassEnviroment[i*3+2].z, grassEnviroment[i*3+2].w);
		}
		grass.draw(grassInstances/3);

		shader.bind();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureFlower);
		for(int i =0; i <flowerInstances; i ++)if(flowerEnviroment[i] !=null)
			GL20.glUniform4f(6 +i, flowerEnviroment[i].x, flowerEnviroment[i].y, flowerEnviroment[i].z, flowerEnviroment[i].w);
		flower.draw(flowerInstances);
		

//		GL20.glUniform1f(5, time+=Timing.delta()*0.025f);
//		shader.bind();
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureBush);
//		for(int i =0; i <bushInstances; i ++)if(bushEnviroment[i] !=null)
//			GL20.glUniform4f(6 +i, bushEnviroment[i].x, bushEnviroment[i].y, bushEnviroment[i].z, bushEnviroment[i].w);
//		flower.draw(bushInstances);
//
//		glEnable(GL_CULL_FACE);
	}
	
}
