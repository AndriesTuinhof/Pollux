package betapanda.gameplay.entities;

import org.lwjgl.opengl.GL13;

import awesome.core.ResourceLoader;
import awesome.core.Timing;
import awesome.math.Matrix;
import betapanda.gameplay.Entity;
import betapanda.gameplay.world.World;
import betapanda.util.Util;
import betapanda.util.animation.Model;
import betapanda.util.animation.Track;
import betapanda.util.collision.Collision;

public class EntityCorpse extends Entity{
	
	private Matrix m =new Matrix();
	
	private Model skel;
	private Track track;
	public int tex;
	public float serverX, serverY, serverZ, serverYaw;
	public int animationIndex = 4;
	public float size;
	private World world;
	
	public EntityCorpse(float size, World w) {
		try {
			skel =new Model(ResourceLoader.openFile("Models/Monster/spider.cha"));
			track =new Track(ResourceLoader.openFile("Models/Monster/spider.track"), skel);
			skel.setMatrix(m);
			this.size =size;
			world = w;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	float ani;
	float age = 100;
	boolean dead = false;
	@Override
	public void logic() {
		if(animationIndex==4){
			age -= Timing.delta();
			if(age<0){
				animationIndex = 0;
				age = 0;
				dead = true;
				Util.removeObject(world.entities, this);
			}
			mass.z = ((age/100.0f)*1.5f-1.5f)*size;
		}
		mass.stepMomentum();
		m.setIdentity();
		m.translate(mass.getX(), mass.getY(), mass.getZ());
		m.scale(size *0.04f, size *0.04f, size *0.04f);
		m.rotateZ(yaw -180);
		

		ani +=Timing.delta() *0.008f;
		ani =ani %1;
//		track.tick(skel, ani);
		track.tick(skel, animationIndex, ani);
		

	}

	@Override
	public void draw() {
		if(dead) return;
		Util.setTexture(GL13.GL_TEXTURE0, World.crabTex[tex%World.crabTex.length]);
		skel.draw();
	}
	
	@Override
	public void drawShadow() {
		skel.ShadowPass();
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collision getCollision() {
		// TODO Auto-generated method stub
		return null;
	}

}
