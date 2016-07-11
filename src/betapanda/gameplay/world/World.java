package betapanda.gameplay.world;

import java.io.File;
import java.util.ArrayList;

import org.lwjgl.openal.AL10;
import org.lwjgl.opengl.GL11;

import awesome.core.Painter;
import awesome.core.ResourceLoader;
import awesome.core.Timing;
import awesome.graphics.Wavefront;
import awesome.math.Matrix;
import awesome.util.FastMath;
import betapanda.PolluxGame;
import betapanda.core.Shader;
import betapanda.core.collada.Collada;
import betapanda.core.collada.RenderableMesh;
import betapanda.gameplay.Entity;
import betapanda.gameplay.entities.EntityGrenade;
import betapanda.gameplay.entities.EntityMonster;
import betapanda.gameplay.entities.EntityPowerUp;
import betapanda.overlay.Scoreboard;
import betapanda.util.Painter3D;
import betapanda.util.animation.Model;
import betapanda.util.animation.Track;
import betapanda.util.collision.Box;
import betapanda.util.collision.Collision;
import betapanda.util.collision.CollisionCollider;
import betapanda.util.collision.Sphere;
import betapanda.util.math.vec3;

public class World {

	public float centerX =0, centerY =0, centerZ =0;
	public vec3 spawnPoint =new vec3();
	public float maxTime =86400, time =maxTime *0.31f, timeSpeedMultiplier =0;//60 *60;

	public Chunk[] chunks =new Chunk[(radiusOfChunks +radiusOfChunks +2) *(radiusOfChunks +radiusOfChunks +2)];
	private ArrayList<Float[]> tracers = new ArrayList<Float[]>();
	public Entity[] entities =new Entity[128];
	public EntityMonster[] monsters =new EntityMonster[64];
	public static EntityPowerUp[] powerUPS =new EntityPowerUp[64];
	
	public static int[] players =new int[4];
	public static int[] enviromentTextures;

	public int bloodTexture;
	public vec3[] bloodstains =new vec3[25];
	public int bloodtimer;
	
	private Shader worldShader, shaderShadow, newShader;
	public int sizeX, sizeY;
	public static int groundTexture;
	
	public int sndSplat, sndCoinDrop, sndCashIn, mscHurry, srcSource1, srcSource2, srcMusic, sndFlesh;
	public static int sndBulletload, sndShotgun1, sndGun1, sndPowerup, sndGameOver;
	public static int sndWhoosh, sndBlast;
	private int[] sndSources = new int[32];
	private int sndSrcPointer = 0;
	
	public Wavefront[] waves; // TODO: Make these private, and implement Andries...
	public int[] textures;
	private int tracerTex;
	private Matrix tracerMatrix = new Matrix();
	
	public static Shader wavefrontShader;
	public static Shader expShader;
	

	public static Model crabSkel;
	public static Track crabTrack;
	public static int[] crabTex=new int[6];
	
	public static Model spitterSkel;
	public static Track spitterTrack;
	public static int[] spitterTex=new int[1];
	public static Collada collada;
	public static RenderableMesh[] testMeshes;
	
	public World(){
		try {
			wavefrontShader = Shader.getShader("wavefront", "matrixBlock");
			worldShader =Shader.getShader("mc", "matrixBlock");
			expShader =Shader.getShader("exp", "matrixBlock");
			newShader =Shader.getShader("world", "matrixBlock");
			shaderShadow =Shader.getShader("mcShadow", "matrixBlock");
			
			groundTexture =ResourceLoader.getTexture("Textures/ground.png", false);
			bloodTexture =ResourceLoader.getTexture("Textures/Decals/blood1.png", false);
			tracerTex =ResourceLoader.getTexture("Textures/Decals/bullet.png", false);
			
			sndSplat = ResourceLoader.getAudio("Sounds/splat.ogg");// TODO: Release
			sndCashIn = ResourceLoader.getAudio("Sounds/coin_drop_stereo3.ogg");// TODO: Release
			sndCoinDrop = ResourceLoader.getAudio("Sounds/coin_drop_stereo.ogg");// TODO: Release
			sndFlesh = ResourceLoader.getAudio("Sounds/flesh.ogg");// TODO: Release
			mscHurry = ResourceLoader.getAudio("Sounds/music_monkey.ogg");// TODO: Release
			sndShotgun1 = ResourceLoader.getAudio("Sounds/shotgun1.ogg");// TODO: Release
			sndGun1 = ResourceLoader.getAudio("Sounds/gun_fire1.ogg");// TODO: Release
			sndBulletload = ResourceLoader.getAudio("Sounds/gun_cock.ogg");// TODO: Release
			sndPowerup = ResourceLoader.getAudio("Sounds/powerup.ogg");// TODO: Release
			sndGameOver = ResourceLoader.getAudio("Sounds/gameover.ogg");// TODO: Release
			sndWhoosh = ResourceLoader.getAudio("Sounds/whoosh.ogg");// TODO: Release
			sndBlast = ResourceLoader.getAudio("Sounds/blast.ogg");// TODO: Release
			for(int i=0;i<sndSources.length;i++) sndSources[i] = AL10.alGenSources();// TODO: Release!
			srcMusic = AL10.alGenSources();// TODO: Release
			
			for(int i =0; i <players.length; i++)
				players[i] =ResourceLoader.getTexture("Textures/Player/player_texture"+(1 +i)+".png", true);

			File[] fil =new File(ResourceLoader.getPath()+"Models/environment/textures").listFiles();
			enviromentTextures =new int[fil.length];


			crabSkel =new Model(ResourceLoader.openFile("Models/Monster/spider.cha"));
			crabTrack =new Track(ResourceLoader.openFile("Models/Monster/spider.track"), crabSkel);
			for(int i=0;i<crabTex.length;i++)
				crabTex[i] =ResourceLoader.getTexture("Models/Monster/mon1_t"+i+".png", true);
			
			spitterSkel =new Model(ResourceLoader.openFile("Models/Monster/spitter.cha"));
			spitterTrack =new Track(ResourceLoader.openFile("Models/Monster/spitter.track"), spitterSkel);
			for(int i=0;i<spitterTex.length;i++)
				spitterTex[i] =ResourceLoader.getTexture("Models/Monster/spitter.png", true);
			
			AL10.alSourcei(srcMusic, AL10.AL_LOOPING, AL10.AL_TRUE);
			AL10.alSourcei(srcMusic, AL10.AL_BUFFER, mscHurry);
			AL10.alSourcef(srcMusic, AL10.AL_PITCH, 1);
			AL10.alSourcef(srcMusic, AL10.AL_GAIN, 0.5f);
			AL10.alSourcePlay(srcMusic);


			EntityGrenade.grenadeModel =ResourceLoader.getWavefront("Models/grenade.obj");
			EntityGrenade.explosionModel1 =ResourceLoader.getWavefront("Models/effects/explosion_lower.obj");
			EntityGrenade.explosionModel2 =ResourceLoader.getWavefront("Models/effects/explosion_upper.obj");
			EntityGrenade.explosionModel3 =ResourceLoader.getWavefront("Models/effects/explosion_smoke.obj");			
			EntityGrenade.grenadeTex =ResourceLoader.getTexture("Models/grenade.png", true);
			EntityGrenade.explosionTex =ResourceLoader.getTexture("Models/effects/explosion.png", true);
			// TODO: Load
			collada = new Collada();
			collada.loadScene();
			testMeshes = new RenderableMesh[collada.nodes.length];
			for(int i=0;i<testMeshes.length;i++) if(collada.nodes[i].instanceGeometry!=null && !collada.nodes[i].id.startsWith("col_"))
			{
				testMeshes[i] = new RenderableMesh(collada.nodes[i].instanceGeometry.renderedData, collada.nodes[i].matrix);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void playSound(int sound, boolean looping, float pitch, float gain){
		if((sndSrcPointer+=1)>=sndSources.length) sndSrcPointer = 0;
		int src = sndSources[sndSrcPointer];

		AL10.alSourceStop(src);
		AL10.alSourcei(src, AL10.AL_LOOPING, AL10.AL_FALSE);
		AL10.alSourcei(src, AL10.AL_BUFFER, sound);
		AL10.alSourcef(src, AL10.AL_PITCH, pitch);
		AL10.alSourcef(src, AL10.AL_GAIN, gain);
		AL10.alSourcePlay(src);
	}

	public static int radiusOfChunks =5;	
	public int playerX =1, playerY =1;
	public void logic(){		
		time +=Timing.delta() /60f *timeSpeedMultiplier;
		
		if(time >=maxTime)
			time =0;
		
		for(Entity e:entities)if(e !=null){
			e.logic();
		}
		
		for(Chunk c: chunks)if(c !=null)
			c.logic();
		
		Entity e;
		for(int i =0; i <monsters.length; i++)if((e =monsters[i]) !=null){
			e.logic();
		}

		for(int i =0; i <powerUPS.length; i++)if((e =powerUPS[i]) !=null){
			e.logic();
		}
		
		Float[] f;
		for(int i =0; i <tracers.size(); i++)if((f =tracers.get(i)) !=null){
			if(f[6] >=3)
				tracers.remove(i);
			f[6] +=Timing.delta()*0.2f;
		}

		int px =(int)Math.floor(centerX /Chunk.size);
		int py =(int)Math.floor(centerY /Chunk.size);
		if(px ==playerX && py ==playerY)return;
		
		playerX =px;
		playerY =py;
		
		for(int x =px -radiusOfChunks; x <px +radiusOfChunks -1; x++){		//there is a minus 1 on it other wise
			for(int y =py -radiusOfChunks; y <py +radiusOfChunks -1; y++){	// there is one chunk on the +side more
				Chunk c =getChunk(x, y);
				
				if(c ==null){
					addChunk(new Chunk(x, y, this));
				}
				
			}
		}
	}
	
	public void draw(){
		worldShader.bind();
		
//		for(int i =0; i <chunks.length; i++)if(chunks[i] !=null){
//			chunks[i].drawTerrain();
//		}
		Painter3D.setColor(1, 1, 1);
		newShader.bind();
		Painter.setTexture(groundTexture);
		Painter3D.drawQuadNoShader(centerX -200, centerY -200, -0.001f, 400, 400, null);
		Painter.setBlankTexture();
		for(int i =0; i <chunks.length; i++)if(chunks[i] !=null){
			chunks[i].drawEnviroment();
			if(!insideRadius(chunks[i], radiusOfChunks))
				chunks[i] =null;
		}
		
		
		GL11.glDepthMask(false);
		Painter3D.setTexture(bloodTexture);
		Painter3D.setColor(0.7f, 0.2f, 0.2f, 1);	
		
		Matrix m =new Matrix();
		for(int i =0; i <bloodstains.length; i ++)if(bloodstains[i] !=null){
			m.setIdentity();
			m.translate(bloodstains[i].x, bloodstains[i].y, 0);
			m.rotateZ(bloodstains[i].z);
			Painter3D.drawQuad(0, 0, 0, 1, 1, m);
		}

		GL11.glDepthMask(true);
		
		Painter.setBlankTexture();
		for(Entity e: entities) if(e!=null) e.draw();
		wavefrontShader.bind();
		Painter.setTexture(groundTexture);
		for(int i=0;i<testMeshes.length;i++) if(testMeshes[i]!=null) testMeshes[i].draw();
		
		for(int i=0;i<monsters.length;i++)
		{
			Entity e = monsters[i];
			if(e==null) continue;
			Painter.setTexture(crabTex[i%crabTex.length]);
			if(PolluxGame.frustum.sphereInFrustum(e.mass.x, e.mass.y, e.mass.z, ((EntityMonster)e).size))e.draw();
		}
		for(Entity e: powerUPS) if(e!=null) e.draw();
//		Painter.setBlankTexture();

		// Render bullet trace lines
		Float[] f;
		float[] tDir;
		Painter.setTexture(tracerTex);
		for(int i =0; i <tracers.size(); i++)if((f =tracers.get(i)) !=null){
			Painter3D.setColor(1.0f, 1.0f, 0.5f, 1.0f);
			float thickness = 1.0f;
			long now = System.currentTimeMillis();
			if(now-Scoreboard.fireBulletTime<30000 || now-Scoreboard.doubleDamageTime<30000)
			{
				if(now-Scoreboard.fireBulletTime<30000) Painter3D.setColor(1.0f, 0.8f, 0.5f, 0.7f);
				thickness = 7.0f;
			}
			tracerMatrix.setIdentity();
			tracerMatrix.translate(f[0], f[1], f[2]);
			tracerMatrix.rotateZ(-90+(float)Math.toDegrees(Math.atan2(f[4]-f[1],f[3]-f[0])));
			tDir = new float[]{f[3]-f[0], f[4]-f[1],f[5]-f[2]};
			Painter3D.drawLine(f[0]+tDir[0]*f[6]*0.1f, f[1]+tDir[1]*f[6]*0.1f, f[2]+tDir[2]*f[6]*0.1f, f[3], f[4] , f[5], thickness);
			Painter3D.drawQuad(-1.0f,-1.0f+f[6]*10+FastMath.random(2),0, 2, 2, tracerMatrix);
		}
		
	}

	public boolean insideRadius(Chunk c, int radius){
		int px =(int)Math.floor(centerX /Chunk.size);
		int py =(int)Math.floor(centerY /Chunk.size);
		if(c.getX() >=px -radius && c.getY() >=py -radius && c.getX() <=px +radius && c.getY() <=py +radius)
			return true;
		
		return false;
	}
	
	public void shadowDraw(){
		shaderShadow.bind();
		
		for(int i =0; i <chunks.length; i++)if(chunks[i] !=null){
			chunks[i].drawTerrain();
		}
		
		for(Entity e:entities)if(e !=null){
			e.drawShadow();
		}

		for(EntityMonster e:monsters)if(e !=null){
			e.drawShadow();
		}
		
	}
	
	public void bullet(float rx, float ry, float rz, float tx, float ty, float tz){
		float l = 80;
		tracers.add(new Float[]{rx +tx, ry +ty, rz +tz, rx +tx *l, ry +ty *l, rz +tz *l, 0f});
	}
	
	public void blood(float x, float y, float r){
		bloodstains[bloodtimer] =new vec3(x, y, r);
		
		if(++bloodtimer>=bloodstains.length) bloodtimer = 0;
	}
	
	public Chunk getChunk(int x, int y){
		int i =getIndex(x, y);
		if(i <0)return null;
		
		return chunks[i];
	}
	
	public int getIndex(int x, int y){
		for(int i =0; i <chunks.length; i++)if(chunks[i] !=null)if(chunks[i].getX() ==x && chunks[i].getY() ==y){
			return i;
		}
		return -1;
	}
	
	public void addChunk(Chunk c){
		for(int i =0; i <chunks.length; i++) if(chunks[i] ==null){
			chunks[i] =c;
			return;
		}
		
		System.out.println("[Client] Error something wrong with the Chunk array");
	}

	public void collisionTest(Collision c) {
		if(c ==null)
			return;
		if(c instanceof Sphere){
			Sphere s =(Sphere)c;
			
			Collision b;
			for(Entity e: entities)if(e!=null && (b =e.getCollision()) !=null && b !=c){
				if(b instanceof Box)
					CollisionCollider.sphereCollideAABB(s, (Box)b);
				if(b instanceof Sphere)
					CollisionCollider.sphereCollideSphere(s, (Sphere)b);
			}
			
			for(Chunk cc: chunks) if(cc!=null)
			{
				for(Collision co :cc.EnviromentCollisions)if(co !=null && c !=co){
					if(co instanceof Box)
						CollisionCollider.sphereCollideAABB(s, (Box)co);
					if(co instanceof Sphere)
						CollisionCollider.sphereCollideSphere(s, (Sphere)co);						
				}
			}
		}
		
	}

}
