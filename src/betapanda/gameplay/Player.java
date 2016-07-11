package betapanda.gameplay;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import awesome.core.Input;
import awesome.core.Painter;
import awesome.core.PandaDisplay;
import awesome.core.ResourceLoader;
import awesome.core.Timing;
import awesome.core.Xbox;
import awesome.net.PacketWriter;
import awesome.util.FastMath;
import awesome.util.Mass;
import betapanda.PolluxGame;
import betapanda.core.Settings;
import betapanda.gameplay.entities.EntityGrenade;
import betapanda.gameplay.guns.Arms;
import betapanda.gameplay.world.World;
import betapanda.net.Network;
import betapanda.net.Packet;
import betapanda.net.User;
import betapanda.util.Camera;
import betapanda.util.Keyboard;
import betapanda.util.Painter3D;
import betapanda.util.Util;
import betapanda.util.animation.Model;
import betapanda.util.animation.Track;
import betapanda.util.collision.Collision;
import betapanda.util.collision.Sphere;

public class Player extends Entity{
	
	public Entity selected =null;

	public Model skeleton;
	public Track track;
	public static Feet feet;
	public Collision c;
	private PolluxGame game;
	public float vibration;
	public int money;
	public float overlayMoney;
	public Arms arms;
	public static float health=100;
	public static float playerDied =0;
	public static boolean isDead =false;
	public static EntityGrenade grenade;
	public float speedBoostTimer;
	public static float grenadeRegen = 1.0f;
	
	public int id;
		
	public Player(PolluxGame game){
		this.game =game;
		feet =new Feet(game.getCamera());
//		setPosition(15, 0, 3);
		mass.setWeight(72 *10f);
		
		c =new Sphere(mass, 1);
//			c =new Cilinder(mass, 0.9f, 0, 1.8f);
		
//		system.initialize();
		try {	
			skeleton =new Model(ResourceLoader.openFile("Models/player/player.cha"));
			track =new Track(ResourceLoader.openFile("Models/player/player.track"), skeleton);
			skeleton.setMatrix(m);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		arms = new Arms(this, game);
	}
	
	private float cameraYaw =0, cameraPitch =-65;
	private float d, dd =12, ddMax =80, ddMin =1;

	private void mouseLook(){
		float dx =Input.getMouseDeltaX();
		float dy =Input.getMouseDeltaY();
		
		if(!Input.rDown()){
			dx =0;
			dy =0;
		}
		
		if(playerDied >0){
			isDead =true;
		}
		
		if(playerDied <0){
			playerDied =0;
			isDead =false;
		}
			

		int d =(int) Input.getMouseWheelDelta();
		if(d !=0)this.d +=d;
		
		float speed =this.d *0.1f *Timing.delta();
		dd -=speed;
		this.d -=speed;
		
		dd =Math.max(Math.min(ddMax, dd), ddMin);	
		
		cameraYaw -=dx *Settings.mouse_sensitivity;
		cameraPitch +=dy *Settings.mouse_sensitivity;
		while(cameraYaw >180)cameraYaw -=360;
		while(cameraYaw <-180)cameraYaw +=360;
		cameraPitch =Math.max(Math.min(89.999f, cameraPitch), -89.999f);	
		cameraLook();
	}
	
	private void cameraLook(){
		game.getCamera().sin =(float) Math.sin(Math.toRadians((game.getCamera().yaw =cameraYaw)));
		game.getCamera().cos =(float) Math.cos(Math.toRadians(game.getCamera().yaw));
		game.getCamera().tan =(float) Math.cos(Math.toRadians((game.getCamera().pitch =cameraPitch)));
		
		// TODO: Perhaps let the camera choose where it wants to be!
		Mass c = game.getCamera().mass;
		float ofsx = +game.getCamera().sin *dd *game.getCamera().tan;
		float ofsy = -game.getCamera().cos *dd *game.getCamera().tan;
		c.x += (mass.x-(c.x-ofsx))*0.15*Timing.delta();
		c.y += (mass.y-(c.y-ofsy))*0.15*Timing.delta();
//		game.getCamera().mass.setX(mass.getX() +game.getCamera().sin *dd *game.getCamera().tan);
//		game.getCamera().mass.setY(mass.getY() -game.getCamera().cos *dd *game.getCamera().tan);
		game.getCamera().mass.setZ((float) (mass.getZ() +-dd*Math.sin(Math.toRadians(game.getCamera().pitch)) +1.8f));
		
	}
	
	public float arcX, arcY, arcZ =1, arcTime, arcTimeMax =2f;
	public float length =16;
	
	public boolean onGround, dontRegen;
	long lastPosSyncTime;
	public void logic(){	
		if(grenade !=null)
			grenade.logic();	
		
		grenadeRegen+=Timing.delta()*0.003;
		if(grenadeRegen>1) grenadeRegen = 1;

		if(System.currentTimeMillis() -lastPosSyncTime >160){
			lastPosSyncTime =System.currentTimeMillis();
			PacketWriter.createPacket(Packet.SERVER_CLIENT_PLAYERUPDATE);
			PacketWriter.writeFloat(mass.getX());
			PacketWriter.writeFloat(mass.getY());
			PacketWriter.writeFloat(mass.getZ());
			PacketWriter.writeFloat(yaw);
			Network.sendPacket(PacketWriter.getPacketAsData());
		}
		
		
		
		if(isDead){
			health=-100;
			return;
		}
		health = Math.min(100, health+=Timing.delta()*0.1);
		
		if(health <0){
			isDead =true;
			PacketWriter.createPacket(Packet.PLAYER_DIED);
			Network.sendPacket(PacketWriter.getPacketAsData());
			return;
		}
		
		
		
		
		feet.logic();
		mouseLook();
		mass.stepMomentum();
		
		game.getWorld().centerX =mass.getX();
		game.getWorld().centerY =mass.getY();
		game.getWorld().centerZ =mass.getZ();

		
		if(mass.getZ() <0){
			mass.z =0.001f;
			mass.zspeed =0;
		}
		
		if(Xbox.getMainController().buttonPressed(Xbox.btn_Y) || Input.keyPressed(Keyboard.KEY_Y)){
			User[] users = game.users;
			for(int i=0;i<users.length;i++) if(users[i]!=null && users[i].entity.isDead){
				if(FastMath.hypot(mass.x-users[i].entity.mass.x, mass.y-users[i].entity.mass.y)>2) continue;
				PacketWriter.createPacket(Packet.PLAYER_REVIVED);
				PacketWriter.writeInt(i);
				byte[] pack = PacketWriter.getPacketAsData();
				Network.sendPacket(pack);
				System.out.println("I ("+id+") revive player "+i);
			}
		}
		
		if(Xbox.getMainController().lTrigger() >0 && grenadeRegen>=1){
			arcTime +=Timing.delta() /60f;
			arcTime =Math.min(arcTime, arcTimeMax);
			if(Xbox.getMainController().rTrigger()>0.2 && arcTime >0.1f){
				grenade =new EntityGrenade(mass.getX(), mass.getY(), mass.getX()+arcX, mass.getY()+arcY);
				arcTime =-1;
				PacketWriter.createPacket(Packet.THROW_GRENADE);
				PacketWriter.writeFloat(mass.getX()+arcX).writeFloat(mass.getY()+arcY);
				Network.sendPacket(PacketWriter.getPacketAsData());
				grenadeRegen = 0;
			}
			if(arcTime >arcTimeMax){
				//kaboom
			}

		}
		else if(arcTime >0){
			//start throwing those babies
			arcTime -=Timing.delta() /10f;
			if(arcTime <0.00000000000001f)
				arcTime =0;
		}
		
		if(arcTime <0){
			arcTime +=Timing.delta() /60f;
			if(arcTime >0)
				arcTime =0;
		}
		
		{
			float dir =yaw;
			float mag = Xbox.getMainController().rThumbMagnitude() *length;
			mag =Math.min(Math.max(mag, 1), arcTime /arcTimeMax *length);
			arcX =(float)Math.sin(Math.toRadians(dir)) *mag;
			arcY =(float)Math.cos(Math.toRadians(dir)) *mag;
		}
		
		if(vibration >0){ // TODO: Implement this is the Xbox class
			vibration -=Timing.delta();
			if(vibration <= 0) Xbox.getMainController().vibrate(0, 0);
		}
		
		float facingVecX = FastMath.sin(yaw), facingVecY = FastMath.cos(yaw);
		if(!Xbox.getMainController().isConnected())
		{
			facingVecX =((Input.getMouseX() /PandaDisplay.getWidth()) *2)-1f;
			facingVecY =((Input.getMouseY() /PandaDisplay.getHeight()) *2)-1f;
			float r =(float) (1f /Math.sqrt(facingVecX *facingVecX +facingVecY *facingVecY));
			facingVecX *=r;
			facingVecY *=r;
		}

		float newYaw =((float)Math.toDegrees(Math.atan2(facingVecX, facingVecY)));

		if(Xbox.getMainController().rThumbMagnitude() >0 || Xbox.getMainController().lThumbMagnitude() >0){
			float mag = Xbox.getMainController().rThumbMagnitude();
			float dir = Xbox.getMainController().rThumbDirection();
			if(mag==0){
				mag = Xbox.getMainController().lThumbMagnitude();
				dir = Xbox.getMainController().lThumbDirection();
			}
			facingVecX =(float)Math.sin(Math.toRadians(dir));
			facingVecY =(float)Math.cos(Math.toRadians(dir));
			newYaw = dir;
		}
		
		if(Math.abs(newYaw -yaw) >0.1f && !Float.isNaN(newYaw)){

			float dif = (float)Math.toDegrees(Math.atan2(Math.sin(Math.toRadians(yaw-newYaw)), Math.cos(Math.toRadians(yaw-newYaw))));
			yaw-=dif*Timing.delta()*0.2;

		}
		
		arms.gunVecX = facingVecX;
		arms.gunVecY = facingVecY;

		
		// Weapon logic
		arms.logic();

		
		m.setIdentity();
		m.translate(mass.getX(), mass.getY(), mass.getZ());
		m.scale(0.5f, 0.5f, 0.5f);
		m.rotateZ(-yaw -180);
		
	}	
	
	private void drawArc(){
		if(!(arcTime >0)) return;
		
		float xSpeed = arcX;
		float ySpeed = arcY;

		Painter3D.lineThickness(4);
		Painter3D.beginDrawing(true);
		Painter3D.setColor(1, 0.3f, 0);
		Painter.setBlankTexture();
		float step =0.1f;
		for(float i =0; i <1; i +=step){
			float z =(float) (Math.sin(i *Math.PI) *length)*0.2f+1;
			float z1 =(float) (Math.sin((i +step) *Math.PI) *length)*0.2f+1;
			

			Painter3D.setVertex(mass.getX() +xSpeed *i, mass.getY() +ySpeed *i, z, 0, 0);
			Painter3D.setVertex(mass.getX() +xSpeed *(i +step), mass.getY() +ySpeed *(i +step), z1, 0, 0);
		}

		Painter3D.stopDrawing(GL11.GL_LINES);
		// OUTER TARGET CIRCLE
		Painter3D.beginDrawing(true);
		Painter3D.setColor(1, 0, 0);
		Painter.setBlankTexture();
		int r =32;
		float size =1;
		for(int cx =0; cx <r; cx ++){
			for(int cy =0; cy <r; cy ++){
				float fcos1 =(float)Math.cos(Math.toRadians((cx +1) /(float)r *360f));
				float fsin1 =(float)Math.sin(Math.toRadians((cx +1) /(float)r *360f));
				float fcos =(float)Math.cos(Math.toRadians((cx) /(float)r *360f));
				float fsin =(float)Math.sin(Math.toRadians((cx) /(float)r *360f));
				Painter3D.setVertex(mass.getX() +arcX +fcos *size, mass.getY() +arcY +fsin *size, arcZ, 0, 0);
				Painter3D.setVertex(mass.getX() +arcX +fcos1 *size, mass.getY() +arcY +fsin1 *size, arcZ, 0, 0);
			}
		}
		Painter3D.stopDrawing(GL11.GL_LINES);

		
		// INNER TARGET CIRCLE
		Painter3D.beginDrawing(true);
		Painter3D.setColor(1, 0, 0);

		size =0.2f;
		
		for(int cx =0; cx <r; cx ++){
			for(int cy =0; cy <r; cy ++){
				float fcos1 =(float)Math.cos(Math.toRadians((cx +1) /(float)r *360f));
				float fsin1 =(float)Math.sin(Math.toRadians((cx +1) /(float)r *360f));
				float fcos =(float)Math.cos(Math.toRadians((cx) /(float)r *360f));
				float fsin =(float)Math.sin(Math.toRadians((cx) /(float)r *360f));
				Painter3D.setVertex(mass.getX() +arcX +fcos *size, mass.getY() +arcY +fsin *size, arcZ, 0, 0);
				Painter3D.setVertex(mass.getX() +arcX +fcos1 *size, mass.getY() +arcY +fsin1 *size, arcZ, 0, 0);
			}
		}
		Painter3D.stopDrawing(GL11.GL_LINES);
		
		Painter3D.beginDrawing(true);
		Painter3D.setColor(0.5f, 0.7f, 0.1f);

		size =arcTime /arcTimeMax *length;
		
		// MAX RANGE CIRCLE
		for(int cx =0; cx <r; cx ++){
			for(int cy =0; cy <r; cy ++){
				float fcos1 =(float)Math.cos(Math.toRadians((cx +1) /(float)r *360f));
				float fsin1 =(float)Math.sin(Math.toRadians((cx +1) /(float)r *360f));
				float fcos =(float)Math.cos(Math.toRadians((cx) /(float)r *360f));
				float fsin =(float)Math.sin(Math.toRadians((cx) /(float)r *360f));
				Painter3D.setVertex(mass.getX() +fcos *size, mass.getY() +fsin *size, arcZ, 0, 0);
				Painter3D.setVertex(mass.getX() +fcos1 *size, mass.getY() +fsin1 *size, arcZ, 0, 0);
			}
		}
		Painter3D.stopDrawing(GL11.GL_LINES);
	}
	
	public void draw(){
		if(grenade !=null)
			grenade.draw();
		Painter.setBlankTexture();	
		arms.draw();
		Util.setTexture(GL13.GL_TEXTURE0, World.players[id]);
		skeleton.draw();
		
		drawArc();
		
		if(Xbox.getMainController().rThumbMagnitude()==0 || !Xbox.getMainController().isConnected()) return;
		
		
		
		
		float l =6, length =l;//gun.getFireMaxPower() /gun.getBulletType().getFriction();
		float w =l *(float) Math.sin(Math.toRadians(0.8f));
		float f =(float) Math.sqrt(l *l +w *w) /length; 
		l /=f;
		w /=f;
		float x =0, y =0, z =0.01f;
		length +=0.2f;

		
		Painter3D.setBlankTexture();
		Painter3D.setColor(235 /255f, 82 /255f, 64 /255f, 0.4f);
		Painter3D.beginDrawing(true);
		Painter3D.setVertex(x, y, z, 0, 0);
		Painter3D.setVertex(x, y +l, z, 0, 0);
		Painter3D.setVertex(x -w, y +l, z, 0, 0);
		Painter3D.setVertex(x, y, z, 0, 0);
		Painter3D.setVertex(x +w, y +l, z, 0, 0);
		Painter3D.setVertex(x, y +l, z, 0, 0);
		Painter3D.setVertex(x, y +l, z, 0, 0);
		Painter3D.setVertex(x, y +length, z, 0, 0);
		Painter3D.setVertex(x-w, y +l, z, 0, 0);
		Painter3D.setVertex(x, y +l, z, 0, 0);
		Painter3D.setVertex(x+w, y +l, z, 0, 0);
		Painter3D.setVertex(x, y +length, z, 0, 0);
		Painter3D.stopDrawing(arms.gunTrajection, GL11.GL_TRIANGLES);

		
	}

	public void drawShadow(){
		skeleton.ShadowPass();
		
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Collision getCollision() {
		return c;
	}
	
	public class Feet {

		public float runningSteam =0;
		private final float GROUND_FRICTION =0.1f;		
		public Camera camera;
		
		public Feet(Camera camera){
			this.camera =camera;
		}

		float vecX =0, vecY =0, vecZ =0;
		public boolean fly =false, running =false;
		public void logic() {
			if(Input.keyPressed(Keyboard.KEY_F1))
				fly =!fly;
			vecX =0; vecY =0; vecZ =0;
			float gravity =(9.806f/60f);
//
			if(!fly)mass.setZspeed(mass.getZspeed() - gravity *Timing.delta());

			float speed = 6f;//5.2f /3.6f /60 *mass.getWeight();
			float b =Timing.delta() /60f;
			
			
			if(Input.keyDown(Keyboard.KEY_LSHIFT) ||Xbox.getMainController().buttonDown(Xbox.btn_leftThumb) && runningSteam >=0)
				running =true;
			
			if(running)
				runningSteam -=b;
			else
				runningSteam +=b;
			
			if(runningSteam >4)
				runningSteam =4;
			
			if(runningSteam <0 && running){
				running =false;
				runningSteam =-0.5f;
			}
			
			if(running && runningSteam >0)
				speed *=2;

			
			if(speedBoostTimer>0){
				speed =12f *2;
				speedBoostTimer -=Timing.delta() /60f;
			}
			
			float friction =GROUND_FRICTION;
			mass.setXspeed(mass.getXspeed() -mass.getXspeed() *friction *Timing.delta());
			mass.setYspeed(mass.getYspeed() -mass.getYspeed() *friction *Timing.delta());
			if(fly)mass.setZspeed(mass.getZspeed() - mass.getZspeed() *friction *Timing.delta());
			
			if(Input.keyDown(Keyboard.KEY_W)){
				vecX -=camera.sin;
				vecY +=camera.cos;
			}	
			if(Input.keyDown(Keyboard.KEY_S)){
				vecX +=camera.sin;
				vecY -=camera.cos;
			}
			
			if(Input.keyDown(Keyboard.KEY_D)){
				vecX +=camera.cos;
				vecY +=camera.sin;
				
			}
			if(Input.keyDown(Keyboard.KEY_A)){
				vecX -=camera.cos;
				vecY -=camera.sin;
			}	
			if(!fly){
//				if(Input.keyPressed(Keyboard.KEY_SPACE) || Xbox.buttonPressed(Xbox.btn_A)){
//					vecZ =37f;
//				}
			}
			else{
				if(Input.keyDown(Keyboard.KEY_SPACE))
					vecZ =1f;
				if(Input.keyDown(Keyboard.KEY_LCONTROL))
					vecZ =-1f;
			}
			
			if(Xbox.getMainController().lThumbMagnitude()>0.0){
				vecX =(float)Math.sin(Math.toRadians(Xbox.getMainController().lThumbDirection()));
				vecY =(float)Math.cos(Math.toRadians(Xbox.getMainController().lThumbDirection()));
				speed *=Xbox.getMainController().lThumbMagnitude();
			}
			
			float len =(float)Math.hypot(vecX, vecY);
			if(len>0){
				mass.xspeed =mass.getXspeed() +vecX /len *speed *Timing.delta();
				mass.yspeed =mass.getYspeed() +vecY /len *speed *Timing.delta();
			}
			else
				running =false;
			
			mass.zspeed =mass.zspeed +vecZ *speed *Timing.delta();
			
			animation();
		}
		
		float ani;
		public void animation(){
			int animationIndex =2;
//			float time =0;
			float d =(float)Math.hypot(mass.lastx-mass.x, mass.lasty-mass.y);
			
			float a =(float)Math.toDegrees(Math.atan2(mass.lastx-mass.x, mass.lasty-mass.y));
			float dif =(float)Math.toDegrees(Math.atan2(Math.sin(Math.toRadians(yaw-a)), Math.cos(Math.toRadians(yaw-a))));
			if(Math.abs(dif)<90) d = -d;
			
			ani +=d *0.6f;
			ani =ani %1;
			if(ani<0) ani++;
			track.tick(skeleton, animationIndex, ani);
		}
	}


}
