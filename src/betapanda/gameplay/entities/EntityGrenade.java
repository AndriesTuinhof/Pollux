package betapanda.gameplay.entities;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import awesome.core.Timing;
import awesome.graphics.Wavefront;
import awesome.math.Matrix;
import awesome.util.FastMath;
import betapanda.PolluxGame;
import betapanda.gameplay.Entity;
import betapanda.gameplay.Player;
import betapanda.gameplay.world.World;
import betapanda.util.Painter3D;
import betapanda.util.collision.Collision;

public class EntityGrenade extends Entity {
	
	private Matrix m =new Matrix();	

	private float originX, originY;
	private float targetX, targetY;

	public static Wavefront grenadeModel;
	public static Wavefront explosionModel1;
	public static Wavefront explosionModel2;
	public static Wavefront explosionModel3;

	public static int grenadeTex;
	public static int explosionTex;
	
	public Matrix exp1 =new Matrix(), exp2 =new Matrix(), exp3 =new Matrix();
	
	public EntityGrenade(float x, float y, float tx, float ty) {
		mass.x = x;
		mass.y = y;
		mass.z = 1;
		
		targetX = tx;
		targetY = ty;
		
		originX = mass.x;
		originY = mass.y;
		
		
		PolluxGame.world.playSound(World.sndWhoosh, false, 1, 1);
	}

	private float lifeTime = 0;
	public boolean exploded = false;
//	float fs =0, fs1 =0;
//	float smokeAlpha =1;
	
	public void logic() {
		lifeTime += Timing.delta()*0.02f;
		if(lifeTime>1.3 && !exploded){
			PolluxGame.world.playSound(World.sndBlast, false, 1.5f, 1);
			exploded = true;
		}
		float alongPath = Math.min(1, lifeTime);
		mass.x = originX+(targetX-originX)*alongPath;
		mass.y = originY+(targetY-originY)*alongPath;
		mass.z = FastMath.sin(alongPath*180)*3;
		m.setIdentity();
		m.translate(mass.getX(), mass.getY(), mass.getZ());
		m.rotate(alongPath*360, alongPath*400, alongPath*600);
		m.scale(5,5,5);

//		if(lifeTime <1.7f)
		{
//			if(lifeTime >1.4f)
//				fs += 0.1f *Timing.delta();
//			if(lifeTime >1.55f)
//				fs1 +=(1f -fs) *0.1f *Timing.delta();
		}
//
//		if(lifeTime >1.7f)
//			fs -=0.1f *Timing.delta();
//		if(lifeTime >1.5f){
//			fs1 +=0.005f *Timing.delta();
//			smokeAlpha -=0.005f *Timing.delta();
//		}
		
//		System.out.println(""+fs+"   "+fs1);
		
//		fs =Math.max(Math.min(fs, 1), 0);
//		fs1 =Math.max(Math.min(fs1, 1), 0);
		
		float xl = Math.max(0, lifeTime-1.2f);//fs *16f *2f;
		// Ring
		exp1.setIdentity();
		exp1.translate(mass.getX(), mass.getY(), mass.getZ());
		exp1.scale(xl*30, xl*30, xl*30);
		exp1.rotateZ(xl*5);

		// Fire
		exp2.setIdentity();
		exp2.translate(mass.getX(), mass.getY(), mass.getZ());
		exp2.scale(xl*30, xl*30, xl*30);
		exp2.rotateZ(-xl*100);

		// Smoke
		exp3.setIdentity();
		exp3.translate(mass.getX(), mass.getY(), mass.getZ());
		exp3.scale(xl*18, xl*18, xl*18);
		exp3.rotateZ((float)xl );
		
	}

	@Override
	public void draw() {
		float xl = Math.max(0, lifeTime-1.2f);
		GL11.glDisable(GL11.GL_CULL_FACE);
		World.wavefrontShader.bind();
		GL20.glUniformMatrix4fv(1, true, m.asBuffer());
		Painter3D.setTexture(grenadeTex);
		if(lifeTime<1.5) grenadeModel.draw();

		World.expShader.bind();
		Painter3D.setTexture(explosionTex);

		// Ring
		GL20.glUniformMatrix4fv(1, true, exp1.asBuffer());
		GL20.glUniform1f(2, 1-xl/2f);
		explosionModel1.draw();

		// Flames
		GL20.glUniformMatrix4fv(1, true, exp2.asBuffer());
		GL20.glUniform1f(2, 1-xl);
		explosionModel2.draw();

		// Smoke
		GL20.glUniformMatrix4fv(1, true, exp3.asBuffer());
		GL20.glUniform1f(2, 1-xl/6);
		explosionModel3.draw();

		GL11.glEnable(GL11.GL_CULL_FACE);
	}
	
	@Override
	public void drawShadow() {
		
	}

	@Override
	public void dispose() {
		Player.grenade =null;
		
	}

	@Override
	public Collision getCollision() {
		return null;
	}

}
