package betapanda;

import static org.lwjgl.opengl.ARBUniformBufferObject.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import awesome.core.Input;
import awesome.core.Painter;
import awesome.core.PandaDisplay;
import awesome.core.Timing;
import awesome.core.Xbox;
import awesome.net.PacketReader;
import awesome.net.PacketWriter;
import awesome.util.Keyboard;
import betapanda.core.BufferBlock;
import betapanda.core.Shader;
import betapanda.gameplay.Player;
import betapanda.gameplay.entities.EntityCharacter;
import betapanda.gameplay.entities.EntityCorpse;
import betapanda.gameplay.entities.EntityGrenade;
import betapanda.gameplay.entities.EntityMonster;
import betapanda.gameplay.entities.EntityPowerUp;
import betapanda.gameplay.entities.EntitySpitter;
import betapanda.gameplay.world.World;
import betapanda.net.GameplayEvent;
import betapanda.net.Network;
import betapanda.net.Packet;
import betapanda.net.PowerupType;
import betapanda.net.User;
import betapanda.overlay.Scoreboard;
import betapanda.util.Camera;
import betapanda.util.Frustum;
import betapanda.util.Painter3D;
import betapanda.util.ShadowRenderer;
import betapanda.util.Util;
import betapanda.util.collision.Collision;
import betapanda.util.collision.CollisionDebugger;
import betapanda.util.particle.ParticleManager;

public class PolluxGame
{

	private int gameFrameBuffer;
	private int gameColorBuffer;
	private int gameDepthBuffer;
	private int gameNormalBuffer;

	private Player player;
	private Camera camera;
	public static World world;
	public static Frustum frustum;
	private ShadowRenderer shadowRenderer;
	
	private Scoreboard scoreBoard;
	public User[] users =new User[4];
	
	private float time;
	private Shader shader;
	public static int wave;
	public float timer;
	public static float chromTimer;
	private static float chromTimertimer;


	void initialize() {
		Shader.addBufferBlock(new matrixBlock());
		shader =Shader.getShader("screenBuffer", "matrixBlock");
		updateBuffers();
		ParticleManager.setup();
		camera =new Camera();
		frustum =new Frustum(camera);
		player =new Player(this);
		world =new World();
		player.mass.setX(world.spawnPoint.x);
		player.mass.setY(world.spawnPoint.y);
		player.mass.setZ(world.spawnPoint.z);
		CollisionDebugger.setup();
		Painter3D.create();
		scoreBoard =new Scoreboard(this, users);
		shadowRenderer =new ShadowRenderer();
	}
	
	public Player getPlayer() {
		return player;
	}

	public Camera getCamera() {
		return camera;
	}

	public World getWorld() {
		return world;
	}

	public Frustum getFrustum() {
		return frustum;
	}
	
	public User[] getUsers(){
		return users;
	}
	
	private boolean devInfo = false, drawCollisionMeshes =false;	
	private float sunX =0, sunY =0, sunZ =0, sunA;
	
	void logic(){
		if(PandaDisplay.wasResized())
			updateBuffers();
		
		if(Input.keyPressed(Keyboard.KEY_F10))
			devInfo =!devInfo;
		if(Input.keyPressed(Keyboard.KEY_F11))
			drawCollisionMeshes =!drawCollisionMeshes;
		if(Input.keyPressed(Keyboard.KEY_TAB))
			scoreBoard.setShow(!scoreBoard.isShow());
		
		if(chromTimer >0)
			chromTimer -=Timing.delta() /60f;		
		if(Player.isDead){
			if(chromTimer <=0){
				if(chromTimertimer <=0){
					chromTimer =1;
					chromTimertimer =3;
				}
				if(chromTimertimer >0){
					chromTimertimer -=Timing.delta() /60f;					
				}
			}
		}
		
		time +=Timing.delta() /60f;
		time =time %360f;
		
		float t =(float) (world.time /world.maxTime);
		float sun =t *360f;
		float rot =(t >0.25f && t <0.75f) ? sun +180: sun;
		
		sunX =(float) Math.sin(Math.toRadians(rot));
		sunY =(float) Math.cos(Math.toRadians(rot));
		sunA =1f -(float) (Math.sin(t *Math.PI *2 +Math.PI) +1)*0.5f;	//TODO:needs an good old fix
		
		shadowRenderer.x =-world.centerX;
		shadowRenderer.y =-world.centerY;
		shadowRenderer.rotate =rot;
		
		
		if(time >0)
			time -=Timing.delta() /60f;
		if(time <0)
			time =0;
		
		scoreBoard.logic();
		
		
		
		player.logic();
		camera.updateMatrix();
		world.logic();
		frustum.logic();
		ParticleManager.logic();
		
		handlePackets();
		
		developerButtons();
		
		
	}
 
	private int[] quitCombination = new int[]{
		Xbox.btn_leftThumb, Xbox.btn_rightThumb,Xbox.btn_leftThumb,
		Xbox.btn_leftThumb, Xbox.btn_rightThumb
	};
	private int buttonTracker = 0;
	private void developerButtons() {
		int b = Xbox.getMainController().fetchLastButtonPressed();
		if(b<0) return;
		if(buttonTracker<quitCombination.length){
			if(b==quitCombination[buttonTracker])
			{
				buttonTracker++;
				return;
			}
			else if(b>=0) buttonTracker = 0;
			return;
		}

		if(b==Xbox.btn_start) RedshawEngine.running = false;
		if(b==Xbox.btn_A) player.arms.getCurrentGun().setCurrentAmmo(9999);
		if(b==Xbox.btn_X) player.speedBoostTimer =40f;
			

		buttonTracker = 0;
	}

	
	void draw()
	{
		GL11.glClearColor(74 /255f, 193 /255f, 221 /255f, 1f);
		Painter.setBlankTexture();
		GL11.glEnable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);

		
		shadowRenderer.shadowPass();
		world.shadowDraw();
		player.drawShadow();

		shadowRenderer.shadowEndPass();

		glActiveTexture(GL_TEXTURE0);
		loadGamePass();
		glEnable(GL_CULL_FACE);
		//DRAW
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL_TEXTURE_2D, shadowRenderer.getShadowTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		world.draw();
		player.draw();

		glActiveTexture(GL_TEXTURE0);
		if(scoreBoard.selection.showing() && scoreBoard.selection.newObject !=null){
			scoreBoard.selection.newObject.logic();
			scoreBoard.selection.newObject.draw();
		}
		

		Painter.setBlankTexture();
		if(drawCollisionMeshes) CollisionDebugger.draw();
		ParticleManager.draw();


		glActiveTexture(GL_TEXTURE0);
		GL11.glDisable(GL_DEPTH_TEST);
		
		Painter.setBlankTexture();
//		float w =PandaDisplay.getWidth();	
		float h =PandaDisplay.getHeight();	
		


		
		
		//STOP DRAWING
		glDisable(GL_CULL_FACE);
		loadScreenPass();
		postRendering();

		Painter.setColor(1, 1, 1);

		if(devInfo){
			Painter.setColor(1, 1, 1);
			Painter.drawString("FPS: "+(int)Math.round(Timing.getFPS()), 5, h -30, 20);
			Painter.drawString(" (" + (Math.round(1000f /Timing.getFPS() *100f) /100f)+"ms)", 5, h -50, 20);
			Painter.drawString(Math.floor(player.mass.getX() *10f) /10f+" "+ Math.floor(player.mass.getY() *10f) /10f, 5, h -90, 14);
			Painter.drawString(Network.ping+" ms", 5, h -104, 12);
		}
		
		
		scoreBoard.draw();
	}
	
	
	private void updateBuffers(){
		glDeleteFramebuffers(gameFrameBuffer);
		glDeleteTextures(gameColorBuffer);
		glDeleteTextures(gameDepthBuffer);
		glDeleteTextures(gameNormalBuffer);
		int width = PandaDisplay.getWidth();
		int height = PandaDisplay.getHeight();
				
		//THE RENDER FBO
		gameFrameBuffer =glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, gameFrameBuffer);
		gameColorBuffer =Util.createRenderTexture(width, height, GL_RGBA, GL_RGBA, GL_COLOR_ATTACHMENT0);
		gameNormalBuffer =Util.createRenderTexture(width, height, GL_RGBA, GL_RGBA, GL_COLOR_ATTACHMENT1);
		gameDepthBuffer =Util.createRenderTexture(width, height, GL_DEPTH_COMPONENT, GL_DEPTH_COMPONENT, GL_DEPTH_ATTACHMENT);

		IntBuffer bb =BufferUtils.createIntBuffer(2);
		bb.put(GL_COLOR_ATTACHMENT0).put(GL_COLOR_ATTACHMENT1);
		bb.flip();
		GL20.glDrawBuffers(bb);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
		
	private void loadGamePass() {
		glBindFramebuffer(GL_FRAMEBUFFER, gameFrameBuffer);
		glViewport(0, 0, PandaDisplay.getWidth(), PandaDisplay.getHeight());
		glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
	}
	
	private void loadScreenPass(){
		GL11.glDisable(GL_DEPTH_TEST);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, PandaDisplay.getWidth(), PandaDisplay.getHeight());
		glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
	}
	
	private void postRendering(){
		glDisable(GL_DEPTH_TEST);
		
		shader.bind();
		
		glActiveTexture(GL_TEXTURE0+2);
		glBindTexture(GL_TEXTURE_2D, gameNormalBuffer);
		glActiveTexture(GL_TEXTURE0+1);
		glBindTexture(GL_TEXTURE_2D, gameDepthBuffer);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, gameColorBuffer);
		
		GL20.glUniform1f(1, time);
		GL20.glUniform1f(2, chromTimer);
		
		Painter.setColor(1, 1, 1);
		Painter.drawQuadNoShader(0, 0, PandaDisplay.getWidth(), PandaDisplay.getHeight());
	}

	private void handlePackets(){
		byte[] packet;
		while((packet =Network.fetchPacket()) !=null){
			PacketReader.setPacket(packet);
			switch(PacketReader.packetID()){	
				case Packet.SERVER_CLIENT_HANDSHAKE:
					wave =0;
					// In event of server reconnect, wipe monsters/players and get new ones
					int playerID =PacketReader.readInt();
					player.id =playerID;
					for(int i=0;i<users.length;i++) users[i] = null;
					for(int i=0;i<world.monsters.length;i++) if(world.monsters[i]!=null){
						world.monsters[i].dispose();
						world.monsters[i] =null;
					}
					for(int i=0;i<World.powerUPS.length;i++) World.powerUPS[i] = null;
					
					for(int id =PacketReader.readInt(); id !=-1; id =PacketReader.readInt()){
						
						String name ="Player "+id;//PacketReader.readString();
	 					float x =PacketReader.readFloat();
	 					float y =PacketReader.readFloat();
	 					float z =PacketReader.readFloat();
	 					boolean dead =PacketReader.readByte() ==1;
						
						EntityCharacter e =new EntityCharacter(id);
						e.mass.setX(x);
						e.mass.setY(y);
						e.mass.setZ(z);
						e.isDead =dead;
						Util.addObject(world.entities, e);
						users[id] =new User(name, e);
					}
					
					for(int id =PacketReader.readInt(); id !=-1; id =PacketReader.readInt()){
						float x =PacketReader.readFloat();
						float y =PacketReader.readFloat();
						float z =PacketReader.readFloat();
						float yaw =PacketReader.readFloat();
						float size =PacketReader.readFloat();
						EntityMonster e;
						if(Xbox.getMainController().lTrigger()>0.5) e =new EntitySpitter(size, player);
						else
							e =new EntityMonster(size, player);
						e.mass.setX(x);
						e.mass.setY(y);
						e.mass.setZ(z);
						e.setYaw(yaw);
						world.monsters[id] = e;
						
					}
					for(int id =PacketReader.readInt(); id !=-1; id =PacketReader.readInt()){
						float x =PacketReader.readFloat();
						float y =PacketReader.readFloat();
						float z =PacketReader.readFloat();
						int type =PacketReader.readInt();
						EntityPowerUp power =new EntityPowerUp(x, y, z, id, type, player);
						World.powerUPS[id] = power;
					}
					
					int wave =PacketReader.readInt();
					PolluxGame.wave =wave;
					break;
					
				case Packet.SERVER_CLIENT_PLAYERJOIN:{
					 
					int id =PacketReader.readInt();
					String name ="player "+id;
					float x =PacketReader.readFloat();
					float y =PacketReader.readFloat();
					float z =PacketReader.readFloat();
					EntityCharacter e =new EntityCharacter(id);
					e.mass.setX(x);
					e.mass.setY(y);
					e.mass.setZ(z);
					Util.addObject(world.entities, e);
					users[id] =new User(name, e);
				}
				break;
				
				case Packet.THROW_GRENADE:
					int pid = PacketReader.readInt();
					float tx = PacketReader.readFloat();
					float ty = PacketReader.readFloat();
					scoreBoard.addMessage("Someone ELSE threw a grenade");
					users[pid].entity.grenade = new EntityGrenade(users[pid].entity.mass.x, users[pid].entity.mass.y, tx, ty);
					break;
					
				case Packet.SERVER_CLIENT_PLAYERUPDATE:{
					int id =PacketReader.readInt();
					if(users[id]==null) break;
					float x =PacketReader.readFloat();
					float y =PacketReader.readFloat();
					float z =PacketReader.readFloat();
					float yaw =PacketReader.readFloat();
					User u = users[id];
					u.entity.serverLocation.setX(x);
					u.entity.serverLocation.setY(y);
					u.entity.serverLocation.setZ(z);
					u.entity.serverYaw =yaw;
				}
				break;
				case Packet.SERVER_CLIENT_PLAYEREVENT:{
					int id =PacketReader.readInt();
					int i =PacketReader.readShort();
					if(i ==1){
						User u = users[id];
						if(u==null) continue;
						
						float x =PacketReader.readFloat();
						float y =PacketReader.readFloat();
						float z =PacketReader.readFloat();
						float yaw =90 -PacketReader.readFloat();
						
						u.entity.serverLocation.x = x;
						u.entity.serverLocation.y = y;
						u.entity.serverLocation.z = z;
						float dis = 1+0.2f*(float)Math.hypot(player.mass.x-u.entity.mass.x, player.mass.y-u.entity.mass.y);
//						
						u.entity.serverYaw = yaw;
						world.bullet(u.entity.mass.x, u.entity.mass.y, u.entity.mass.z+1.6f, (float)Math.cos(Math.toRadians(yaw)), (float)Math.sin(Math.toRadians(yaw)), 0);
						world.playSound(World.sndGun1, false, 0.9f+(float)Math.random()*0.2f, 0.2f/dis);
					}
				}
				break;
				case Packet.SERVER_CLIENT_PLAYERLEAVE:{
					int id =PacketReader.readInt();
					Util.removeObject(world.entities, users[id].entity);
					Collision.collisions.remove(users[id].entity.getCollision());
					users[id] =null;
				}
				break;
				
				case Packet.SERVER_RESET:{
					PolluxGame.wave =0;
					for(User u: users) if(u!=null) u.entity.isDead = false;
					player.mass.x = 0;
					player.mass.y = 0;
					player.mass.z = 0;
					world.playerX =-1;
					world.playerY =-1;
					Player.health = 100;
					Player.isDead = false;
					player.arms.resetNewGame();
				}
				break;
				
				case Packet.SERVER_CLIENT_ENEMYJOIN:{
					int id =PacketReader.readInt();
					float x =PacketReader.readFloat();
					float y =PacketReader.readFloat();
					float z =PacketReader.readFloat();
					float yaw =PacketReader.readFloat();
					float size =PacketReader.readFloat();
					EntityMonster e =new EntityMonster(size, player);
					e.mass.setX(x);
					e.mass.setY(y);
					e.mass.setZ(z);
					e.yaw =yaw;
					world.monsters[id] =e;
				}
				break;
				case Packet.SERVER_CLIENT_ENEMYUPDATE:{
					for(int id =PacketReader.readInt(); id !=-1; id =PacketReader.readInt()){ // Check this out Andries :D
						float x =PacketReader.readFloat();
						float y =PacketReader.readFloat();
						float z =PacketReader.readFloat();
						float yaw =PacketReader.readFloat();
						EntityMonster e =world.monsters[id];
						if(e ==null)
							continue;
						e.serverX =x;
						e.serverY =y;
						e.serverZ =z;
						e.serverYaw = yaw;
					}
				}
				break;
				case Packet.SERVER_CLIENT_ENEMYEVENT:{
					int enemyID = PacketReader.readInt();
					int eventType = PacketReader.readShort();
					if(world.monsters[enemyID]==null) break;
					if(eventType==1)
					{
//						System.out.println("Enemy "+enemyID+" was hurt");
						float distance = (float)Math.hypot(world.monsters[enemyID].mass.x-player.mass.x, world.monsters[enemyID].mass.y-player.mass.y);
						distance = 1+Math.max(0, distance/10f);
						world.playSound(world.sndSplat, false, 0.8f+(float)Math.random()*0.7f, 1.0f/distance);
					}
				}
				break;
				case Packet.SERVER_CLIENT_ENEMYLEAVE:{
					int id =PacketReader.readInt();
					if(id <0)return;
					EntityMonster c =world.monsters[id];
					if(c !=null)
					{
						world.playSound(world.sndSplat, false, 0.2f+(float)Math.random()*0.3f, 1.0f);
						Collision.collisions.remove(c.getCollision());
						world.monsters[id] =null;
						EntityCorpse e =new EntityCorpse(c.size, world);
						e.tex = id;
						e.mass.setX(c.mass.x);
						e.mass.setY(c.mass.y);
						e.mass.setZ(c.mass.z);
						e.yaw = c.yaw;
						Util.addObject(world.entities, e);
						
					}
				}
				break;
				
				case Packet.PING:{
					PacketWriter.createPacket(Packet.PING);
					PacketWriter.writeLong(PacketReader.readLong());
					Network.sendPacket(PacketWriter.getPacketAsData());
					Network.ping = PacketReader.readInt();
				}
				break;
				
				case Packet.SERVER_GAME_OVER:{
					Scoreboard.alertGameOver();
					world.playSound(World.sndGameOver, false, 1, 1);
				}
				break;
				
				case Packet.SERVER_GAMEPLAY_EVENT:{
					int type = PacketReader.readInt();
					if(type==GameplayEvent.SYNC_SCORES){
						int playerIndex;
						while((playerIndex = PacketReader.readInt())>=0){
							if(playerIndex<0 || playerIndex >users.length) continue;
							int score = PacketReader.readInt();
							int oldScore = 0;
							if(users[playerIndex] !=null){
								oldScore = users[playerIndex].money;
								users[playerIndex].money =score;
							}
							else{
								oldScore = player.money;
								player.money =score;
							}
							if(score>oldScore) world.playSound(world.sndCashIn, false, 0.8f+(float)Math.random()*0.4f, 1.0f);
						}
					}
					// Change of wave
					if(type==GameplayEvent.WAVE_CHANGE)
					{
						int wave1 =PacketReader.readInt();
						PolluxGame.wave =wave1;
						timer =10f;
					}
					if(type ==GameplayEvent.WAVE_REVIEW){
						scoreBoard.review(PacketReader.readInt());
					}
				}
				break;
				case Packet.POWERUP_SPAWN:{
					int id =PacketReader.readInt();
					float x =PacketReader.readFloat();
					float y =PacketReader.readFloat();
					float z =PacketReader.readFloat();
					int type =PacketReader.readInt();
					EntityPowerUp power =new EntityPowerUp(x, y, z, id, type, player);		
					World.powerUPS[id] =power;
				}
				break;
				case Packet.POWERUP_DESPAWN:{
					int id =PacketReader.readInt();
					if(id <0 || World.powerUPS[id] ==null) break;
					World.powerUPS[id].dispose();
					World.powerUPS[id] =null;
					world.playSound(World.sndPowerup, false, 1, 0.7f);
				}
				break;
				case Packet.SERVER_CLIENT_BROADCAST:{
					scoreBoard.addMessage(PacketReader.readString());
				}
				break;
				
				case Packet.POWERUP_SERVER_CONFIRM_TOUCH:{
					int player = PacketReader.readInt();
					int breed = PacketReader.readInt();
					String s =(player ==getPlayer().id)? "You grabbed ": "Your friend grabbed ";
					Scoreboard.alertPowerup(s+PowerupType.getString(breed));
					if(breed==PowerupType.DOUBLE_DAMAGE) Scoreboard.doubleDamageTime = System.currentTimeMillis();
					if(breed==PowerupType.FIRE_BULLETS) Scoreboard.fireBulletTime = System.currentTimeMillis();
					if(breed==PowerupType.SPEED_BOOST){ getPlayer().speedBoostTimer = 10; Scoreboard.speedBoostTime=System.currentTimeMillis(); }
					if(breed==PowerupType.MAX_AMMO) getPlayer().arms.maxAmmoForAll();
				}
				break;
				
				case Packet.PLAYER_DIED:{
					int index = PacketReader.readInt();
					if(player.id==index) break;
					User u = users[index];
					if(u==null){
						System.out.println("Can't set other player to dead!");
						break;
					}
					u.entity.isDead = true;
				}
				break;
				
				case Packet.PLAYER_REVIVED:{
					int index = PacketReader.readInt();
					if(player.id==index)
					{
						Player.isDead = false;
						Player.health = 100;
						chromTimer =(float) (1);
						break;
					}
					User u = users[index];
					if(u==null){
						System.out.println("Can't set other player to dead!");
						break;
					}
					u.entity.isDead = false;
				}
				break;
				
			}
		}
	}
	
	private class matrixBlock extends BufferBlock{

		public matrixBlock() {
			super("matrixBlock");
		}

		@Override
		public void init() {
			int d =16 +16 +16 +16 +4;
			this.data =BufferUtils.createFloatBuffer(d);
			GL15.glBindBuffer(GL_UNIFORM_BUFFER, buffer);
			GL15.glBufferData(GL_UNIFORM_BUFFER, d *4, GL15.GL_DYNAMIC_DRAW);
			
		}

		@Override
		public void updateVariables() {
			data.clear();
			camera.getModelViewMatrix().putTransposed(data);
			camera.getProjectionMatrix().putTransposed(data);
			camera.getmodelviewProjectionMatrix().putTransposed(data);
			shadowRenderer.getModelviewProjectionMatrix().putTransposed(data);
			data.put(sunX).put(sunY).put(sunZ).put(sunA);

			data.flip();
		}
		
	}
}
