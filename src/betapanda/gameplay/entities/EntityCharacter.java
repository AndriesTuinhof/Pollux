package betapanda.gameplay.entities;

import org.lwjgl.opengl.GL13;

import awesome.core.ResourceLoader;
import awesome.core.Timing;
import awesome.util.Mass;
import betapanda.gameplay.Entity;
import betapanda.gameplay.guns.Gun_Shotgun;
import betapanda.gameplay.guns.Gun_SubMachine;
import betapanda.gameplay.guns.Weapon;
import betapanda.gameplay.world.World;
import betapanda.util.Util;
import betapanda.util.animation.Model;
import betapanda.util.animation.Track;
import betapanda.util.collision.Collision;
import betapanda.util.collision.Sphere;

public class EntityCharacter extends Entity{
	
	public static Weapon[] guns =new Weapon[2];
	public float MaxHealth = 100, health = MaxHealth;
	private Model skel;
	private Track track;
	private Collision c;
	public Mass serverLocation = new Mass();
	public float serverYaw;
	public int playerIndex;
	public int weaponId =0;
	public boolean isDead;
	public EntityGrenade grenade;
	
	public EntityCharacter(int id) {
		try {
			skel =new Model(ResourceLoader.openFile("Models/player/player.cha"));
			track =new Track(ResourceLoader.openFile("Models/player/player.track"), skel);
			skel.setMatrix(m);
			c =new Sphere(mass, 0.45f);	
			this.playerIndex =id;
			if(guns[0] ==null){
				guns[0] =new Gun_SubMachine(skel.findBone("Bone_003_L_006").matrix_world, m);
				guns[0].initilize();
			}

			if(guns[1] ==null){
				guns[1]=new Gun_Shotgun(skel.findBone("Bone_003_L_006").matrix_world, m);
				guns[1].initilize();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private float f = 0;
	@Override
	public void logic() {
		if(grenade!=null) grenade.logic();
		mass.stepMomentum();
		m.setIdentity();
		m.translate(mass.getX(), mass.getY(), mass.getZ());
		m.scale(0.5f, 0.5f, 0.5f);
		m.rotateZ(-yaw +180);
		
		guns[weaponId].logic();
		
		mass.x+=(serverLocation.x-mass.x)*Timing.delta()*0.05f;
		mass.y+=(serverLocation.y-mass.y)*Timing.delta()*0.05f;
		mass.z+=(serverLocation.z-mass.z)*Timing.delta()*0.05f;
		

		float moved = (float)Math.hypot(mass.lastx-mass.x, mass.lasty-mass.y);
		
		float a = (float)Math.toDegrees(Math.atan2(mass.lastx-mass.x, mass.lasty-mass.y));
		float dif = (float)Math.toDegrees(Math.atan2(Math.sin(Math.toRadians(yaw-a)), Math.cos(Math.toRadians(yaw-a))));
		if(Math.abs(dif)<90) moved = -moved;
		
		f +=moved *0.6f;
		f =f %1;
		if(f<0) f++;
		track.tick(skel, 2, f);

		float dif2 = (float)Math.toDegrees(Math.atan2(Math.sin(Math.toRadians(yaw-serverYaw)), Math.cos(Math.toRadians(yaw-serverYaw))));
		yaw-=dif2*Timing.delta()*0.1;
	}

	@Override
	public void draw() {
		if(grenade!=null) grenade.draw();
		Util.setTexture(GL13.GL_TEXTURE0, World.players[playerIndex]);
		skel.draw();
		guns[weaponId].draw();
	}

	@Override
	public void drawShadow() {
		skel.ShadowPass();
		
	}

	@Override
	public Collision getCollision() {
		return c;
	}

	@Override
	public void dispose() {
		
	}

}
