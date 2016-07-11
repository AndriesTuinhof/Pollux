package betapanda.gameplay.guns;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import awesome.core.Input;
import awesome.core.Timing;
import awesome.core.Xbox;
import awesome.math.Matrix;
import awesome.net.PacketWriter;
import betapanda.PolluxGame;
import betapanda.core.Shader;
import betapanda.gameplay.Entity;
import betapanda.gameplay.Player;
import betapanda.gameplay.entities.EntityMonster;
import betapanda.gameplay.world.World;
import betapanda.net.Network;
import betapanda.net.Packet;
import betapanda.util.Keyboard;
import betapanda.util.Util;
import betapanda.util.collision.Sphere;
import betapanda.util.particle.Particle;
import betapanda.util.particle.ParticleManager;
import betapanda.util.particle.ParticleSystem;

public class Arms{
	
	public Weapon[] guns;
	private Player player;
	private PolluxGame game;
	public float gunVecX=0, gunVecY=1;
	public Matrix gunTrajection;// TODO: Remove!
	public gunParticleSystem system;
	public int currentGun;
	
	
	public Arms(Player p, PolluxGame g){
		player = p;
		game = g;
		
		guns =new Weapon[2];
		
		Weapon w=new Gun_SubMachine(p.skeleton.findBone("Bone_003_L_006").matrix_world, player.m);
		w.initilize();
		w.setAmmo(180);
		w.setCurrentAmmo(w.getClipSize());
		
		Util.addObject(guns, w);

		w=new Gun_Shotgun(p.skeleton.findBone("Bone_003_L_006").matrix_world, player.m);
		w.initilize();
		w.setAmmo(100);
		w.setCurrentAmmo(w.getClipSize());
		Util.addObject(guns, w);
		

		gunTrajection =new Matrix();
//		system =new gunParticleSystem();
	}
	
	public void logic(){
		if(getCurrentGun() ==null)
			return;
		guns[currentGun].logic();
		guns[currentGun].getMass().setX(player.mass.getX());
		guns[currentGun].getMass().setY(player.mass.getY());
		guns[currentGun].getMass().setZ(player.mass.getZ());
		
		boolean canFire =guns[currentGun].getClipAmmo()>0 &&guns[currentGun].reloading()<=0 && Xbox.getMainController().lTrigger()<0.1;
		boolean firePressed =Input.lDown() ||Xbox.getMainController().rTrigger()>0.5f;
		if(firePressed && canFire && player.arcTime <=0){
//			if(gun.reloading() >0 && gun.getCurrentAmmo() !=0)
//				gun.stopreloading();
			if(guns[currentGun].shoot() >0){
				game.getWorld().playSound(guns[currentGun].getSoundIndex(), false, 0.8f+(float)Math.random()*0.4f, 0.5f);
				
				
				if(Xbox.getMainController().rTrigger()>0.5){
					if(player.vibration <=0) Xbox.getMainController().vibrate(0.9f, 1.0f);
					player.vibration =18;
				}
				
				double angle = Math.atan2(gunVecX, gunVecY)+Math.random()*0.2-0.1;
				gunVecX = (float)Math.sin(angle);
				gunVecY = (float)Math.cos(angle);
				
				PacketWriter.createPacket(Packet.SERVER_CLIENT_PLAYEREVENT);
				PacketWriter.writeShort(1);
				PacketWriter.writeFloat(player.mass.getX());
				PacketWriter.writeFloat(player.mass.getY());
				PacketWriter.writeFloat(player.mass.getZ());
				PacketWriter.writeFloat((float)Math.toDegrees(angle));
				Network.sendPacket(PacketWriter.getPacketAsData());
				
				
				
				game.getWorld().bullet(player.mass.getX(), player.mass.getY(), player.mass.getZ()+1.6f, gunVecX, gunVecY, 0);
				Entity e;

				EntityMonster[] monsters =new EntityMonster[2];
				float[] dis =new float[2];
				int[] ids =new int[2];
				for(int i =0; i <game.getWorld().monsters.length; i ++)if((e= game.getWorld().monsters[i]) !=null){
					if(e instanceof EntityMonster == false) continue;	
					float xx =(e.mass.getX() -player.mass.getX());
					float yy =(e.mass.getY() -player.mass.getY());
					float f = xx *gunVecY - yy *gunVecX; // Distance from ray
					float f2 = yy*gunVecY + xx *gunVecX; // Distance infront of player (along ray)
					
					Sphere b =((Sphere)e.getCollision());
					if(Math.abs(f) < b.getRadius()+0.5 && f2>0){
						if(f2 <dis[0] || dis[0] ==0){
							dis[1] =dis[0];
							dis[0] =f2;
							ids[1] =ids[0];
							ids[0] =i;
							monsters[1] =monsters[0];
							monsters[0] =(EntityMonster) e;
						}
						else if(f2 <dis[1] || dis[1] ==0){
							dis[1] =f2;
							monsters[1] =(EntityMonster) e;		
							ids[1] =i;
						}
					}			
				}
				
				for(int mon =0; mon <monsters.length; mon++)if(monsters[mon] !=null){
					float xx =(monsters[mon].mass.getX() -player.mass.getX());
					float yy =(monsters[mon].mass.getY() -player.mass.getY());
					float rr =(float) ((Math.random() +1) /2);
					game.getWorld().blood(player.mass.getX() +xx *rr *2, player.mass.getY() +yy *rr *2, rr *360);
//					em.doBulletDamage(50f);
//
					PacketWriter.createPacket(Packet.SERVER_CLIENT_ENEMYEVENT);
					PacketWriter.writeInt(ids[mon]);
					PacketWriter.writeShort(1);
					float power = getCurrentGun().getFireMaxPower();
					if(monsters[mon].animationIndex==3) power *= 0.1f;
					PacketWriter.writeFloat(power -10 *dis[mon] /10f);
					Network.sendPacket(PacketWriter.getPacketAsData());
					
					monsters[mon].mass.xspeed = gunVecX*power*0.03f;
					monsters[mon].mass.yspeed = gunVecY*power*0.03f;
				}

			}
		}
		
		boolean canReload = guns[currentGun].getClipAmmo()<guns[currentGun].getClipSize() && guns[currentGun].reloading()<=0 && guns[currentGun].getAmmo()>0;
		boolean reloadPressed = Xbox.getMainController().buttonPressed(Xbox.btn_X) || Input.keyPressed(Keyboard.KEY_R) || Input.keyPressed(Keyboard.KEY_J);
		reloadPressed |= (firePressed&&guns[currentGun].getClipAmmo()<=0);
		if(canReload && reloadPressed)
		{
			guns[currentGun].reload();
			Xbox.getMainController().vibrate(0.0f, 0.7f);
			player.vibration =10;
		}
		
		if(guns[currentGun].reloadTrigger()){
//				if(gun.reloading()>0) return;
			game.getWorld().playSound(World.sndBulletload, false, 1, 0.4f);	
				Xbox.getMainController().vibrate(0.0f, 0.8f);
			player.vibration =10;
		}
		
		
		gunTrajection.setIdentity();
		gunTrajection.translate(guns[currentGun].getMass().getX(), guns[currentGun].getMass().getY(), guns[currentGun].getMass().getZ());
		gunTrajection.rotateZ(-player.yaw);
		
		
	}

	public void draw() {
		if(guns[currentGun] !=null)
			guns[currentGun].draw();

	}
	
	public Weapon getCurrentGun(){
		return guns[currentGun];
	}
	

	class gunParticleSystem extends ParticleSystem{
		
		public particleFirshot[] particles =new particleFirshot[30];

		private Shader shader;
		private int glVertexArray, glBuffer;
		private Matrix m;
		
		public gunParticleSystem() {
			shader =Shader.getShader("particle", "matrixBlock");
			m =new Matrix();
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
		}
		
		float beginX, beginY, beginZ;
		public void shoot(float x, float y, float z){
			beginX =x;
			beginY =y;
			beginZ =z;
			
		    float Longitude =(float)Math.PI *2.0f /15;
		    float x0, y0;
		    for(int j =0; j <5; j ++){	
			   	x0 =(float) (1 *Math.sin((j)*Longitude));
		    	y0 =(float) (1 *Math.cos((j)*Longitude));
		    	particles[j] =new particleFirshot(x +x0, y +y0, z +1.2f, gunVecX, gunVecY, 0);
		    }
		}

		@Override
		public void logic() {
			for(int i =0; i <particles.length; i ++)if(particles[i] !=null){
				particles[i].logic();
				
			}
			m.setIdentity();
			m.translate(beginX, beginY, beginZ);
			
		}

		@Override
		public void draw() {
			shader.bind();
			GL20.glUniform4f(3, 0.1f, 0.1f, 0.1f, 1);
			GL20.glUniformMatrix4fv(4, true, m.asBuffer());
			int a =0;
		    for(int i =0; i <5; i ++)if(particles[i] !=null){
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

			public float lifeTime =1;
			
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
				lifeTime -=Timing.delta() /60f;
			}

			@Override
			public void setup(int i) {
				GL20.glUniform3f(5 +i, mass.getX(), mass.getY(), mass.getZ());
				System.out.println(mass.getX()+"  "+mass.getY()+"  "+mass.getZ());
				
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


	public void maxAmmoForAll()
	{
		for(Weapon w: guns) if(w!=null){
			w.setAmmo(w.getClipSize()*20);
			w.setCurrentAmmo(w.getClipSize()*2);
		}
	}

	public void resetNewGame() {
		maxAmmoForAll();
	}
}
