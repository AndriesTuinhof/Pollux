package betapanda.gameplay.entities;

import awesome.core.Timing;
import awesome.math.Matrix;
import awesome.util.FastMath;
import betapanda.PolluxGame;
import betapanda.gameplay.Entity;
import betapanda.gameplay.Player;
import betapanda.gameplay.world.World;
import betapanda.util.collision.Collision;
import betapanda.util.collision.Sphere;

public class EntityMonster extends Entity {
	protected Matrix m =new Matrix();
	private Entity target;
	private Collision c;
	
	public float serverX, serverY, serverZ, serverYaw;
	public int animationIndex = 3;
	public float size;
	private Player player;
	private float averageDistance;
	private long lastDistanceAverage;
	private long lastHurtPlayerTime;
	
	public EntityMonster(float size, Player p) {
		try {
			this.size =size;
			c =new Sphere(mass, 1.30f *size);
			player = p;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setTarget(Entity e){
		this.target =e;
	}
	
	public Entity getTarget(){
		return this.target;
	}
	
	float ani;
	float age = 0;
	@Override
	public void logic() {
		if(animationIndex==3){
			age += Timing.delta();
			if(age>100){
				animationIndex = 0;
				age = 100;
			}
			mass.z = ((age/100.0f)*1.5f-1.5f)*size;
		}
		mass.stepMomentum();
		m.setIdentity();
		m.translate(mass.getX(), mass.getY(), mass.getZ());
		m.scale(size *0.04f, size *0.04f, size *0.04f);
		m.rotateZ(yaw -180);


		float xs =serverX -mass.getX();
		float ys =serverY -mass.getY();
		float d = FastMath.hypot(xs, ys);
		if(System.currentTimeMillis()-lastDistanceAverage>50){
			lastDistanceAverage = System.currentTimeMillis();
			averageDistance += d * 0.1;
			averageDistance /= 1.1;
		}

		xs = xs/d * (averageDistance*0.01f);
		ys = ys/d * (averageDistance*0.01f);
//		xs *=xs/d *Timing.delta() *0.2f*size;
//		ys *=ys/d *Timing.delta() *0.2f*size;
		
//		mass.x = serverX;
//		mass.y = serverY;
		

		
		
		float pVecX = mass.x-player.mass.x;
		float pVecY = mass.y-player.mass.y;
		float pLen = Math.max(0.1f, FastMath.hypot(pVecX, pVecY));
		float pushBack = 2.0f;
		if(PolluxGame.wave==0) pushBack = 5;
		if(pLen<pushBack*size){
			pVecX/=pLen;
			pVecY/=pLen;
			pVecX*= pushBack*size;
			pVecY*= pushBack*size;
			
			mass.x += pVecX*0.1*Timing.delta();
			mass.y += pVecY*0.1*Timing.delta();
		}
		
		if(pLen<2.0*size+3 && PolluxGame.wave >0 && animationIndex!=3){
			if(System.currentTimeMillis()-lastHurtPlayerTime>800)
			{
				lastHurtPlayerTime = System.currentTimeMillis();
				if(Math.random()<0.5f){
					Player.health-=50*size*(float)Math.random();
					PolluxGame.chromTimer = 1;
				}
			}
		}
		
//		if(Math.abs(d) <0.5f){
//			xs =ys =0;
//		}
		if(animationIndex==0) // Only walk about when on walk animation
		{
			mass.setXspeed(xs);
			mass.setYspeed(ys);	
		}
		float newYaw =((float)Math.toDegrees(-Math.atan2(mass.getXspeed(), mass.getYspeed())));
		if(Math.abs(newYaw -yaw) >0.1f && !Float.isNaN(newYaw))
		{
//			yaw += (serverYaw-yaw)*Timing.delta()*0.05f;
			float dif = (float)Math.toDegrees(Math.atan2(Math.sin(Math.toRadians(yaw-newYaw)), Math.cos(Math.toRadians(yaw-newYaw))));
			yaw-=dif*Timing.delta()*0.03;
		}
		

		// Animate
		if(animationIndex==3) ani+=Timing.delta()*0.007f;
		else 
			ani +=FastMath.hypot(mass.x-mass.lastx, mass.y-mass.lasty) *0.250f * (1f/size);
		ani =ani %1;
	}

	@Override
	public void draw() {

//		track.tick(skel, ani);
		World.crabTrack.tick(World.crabSkel, animationIndex, ani);
		
//		Util.setTexture(GL13.GL_TEXTURE0, World.crabTex);
		World.crabSkel.draw(m);
	}
	
	@Override
	public void drawShadow() {
		World.crabTrack.tick(World.crabSkel, animationIndex, ani);
		World.crabSkel.ShadowPass(m);
		
	}

	@Override
	public void dispose() {
		Collision.collisions.remove(c);
		
	}

	@Override
	public Collision getCollision() {
		return c;
	}

}
