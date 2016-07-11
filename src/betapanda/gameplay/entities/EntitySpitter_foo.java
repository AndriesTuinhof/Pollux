package betapanda.gameplay.entities;

import awesome.core.ResourceLoader;
import betapanda.gameplay.Entity;
import betapanda.gameplay.guns.Weapon;
import betapanda.util.animation.Model;
import betapanda.util.collision.Collision;
import betapanda.util.collision.Sphere;

public class EntitySpitter_foo extends Entity{
	
	public static Weapon[] guns =new Weapon[2];
	public float MaxHealth = 100, health = MaxHealth;
	private Model skel;
//	private Track track;
	private Collision c;
	
	public EntitySpitter_foo() {
		try {
			skel =new Model(ResourceLoader.openFile("Models/Monster/spitter.cha"));
//			track =new Track(ResourceLoader.openFile("Models/Monster/spitter.track"), skel);
			skel.setMatrix(m);
			c =new Sphere(mass, 0.45f);	
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void logic() {
		mass.stepMomentum();
		m.setIdentity();
		m.translate(mass.getX(), mass.getY(), mass.getZ());
		m.scale(0.5f, 0.5f, 0.5f);
		m.rotateZ(-yaw +180);
	}

	@Override
	public void draw() {
//		Util.setTexture(GL13.GL_TEXTURE0, World.players[playerIndex]);
		skel.draw();
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
