package betapanda.gameplay.guns;

import org.lwjgl.opengl.GL20;

import awesome.core.Painter;
import awesome.core.ResourceLoader;
import awesome.core.Timing;
import awesome.graphics.Wavefront;
import awesome.math.Matrix;
import awesome.util.Mass;
import betapanda.core.Shader;
import betapanda.gameplay.world.World;

public class Gun_SubMachine implements Weapon{

	private Shader shader;
	private Wavefront model;
	private Matrix m =new Matrix();
	private Mass mass =new Mass();
	
	private int ammo, currentAmmo, maxAmmo =30, clipSize =30;
	private float reloading =0;
	private float shootTime;
	private boolean reloadTriggered, isReloading;
	
	private Matrix ma, m1;
	private int texture;
	
	public Gun_SubMachine(Matrix m, Matrix m1) {
		this.ma =m;
		this.m1 =m1;
	}
	
	@Override
	public void initilize() {
		shader =Shader.getShader("wavefront", "matrixBlock");
		model =ResourceLoader.getWavefront("Models/Guns/thompson.obj");
		texture =ResourceLoader.getTexture("Models/Guns/thompson.png", false);
		
	}

	@Override
	public void logic() {
		m.setIdentity();
		m.setMult(m1, ma);
		m.translate(0, 0.1f, 0.3f);
		float s =4.14f;
		m.scale(s, s, s);
		m.rotateZ(30 +180);
		m.rotateX(130);
//		m.rotateX(-50 +90);
//		m.rotateY(20);
		
		if(shootTime >0)
			shootTime -=Timing.delta() /60f;
		
		if(reloading >0 && isReloading){
			reloading -=Timing.delta() /60f;
		}
		else
			reloadTriggered =false;
		
		if(reloading <0 && isReloading){
			reloadTriggered =true;
			int a =Math.max(getClipAmmo() -clipSize, Math.max(getClipSize() -getClipAmmo(), 0));
			currentAmmo +=a;
			ammo -=a;		
			isReloading =false;
		}
		
		
	}

	@Override
	public void draw() {
		shader.bind();
		Painter.setTexture(texture);
		GL20.glUniformMatrix4fv(1, true, m.asBuffer());
		model.draw();
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public int reload() {		
		int a =getClipSize() -getClipAmmo();
		if(getAmmo() -a <0)
			a =Math.max(a, 0);
		if(isReloading || getClipAmmo() >= clipSize)
			return a;
		if(a !=0){
			isReloading =true;
			reloading =getReloadTime();
		}
		return a;
	}

	@Override
	public float reloading() {
		return isReloading ?reloading :0;
	}
	
	@Override
	public boolean reloadTrigger() {
		return reloadTriggered;
	}

	@Override
	public int reloadingBullets() {
		return reloading() >0 ? 1: 0;
	}

	@Override
	public Mass getMass() {
		return mass;
	}

	@Override
	public int getClipSize() {
		return maxAmmo;
	}

	@Override
	public int getClipAmmo() {
		return currentAmmo;
	}
	
	@Override
	public int getAmmo() {
		return ammo;
	}
	
	@Override
	public void setAmmo(int ammo) {
		this.ammo =ammo;
	}

	@Override
	public int shoot() {
		int b =(currentAmmo -1 >=0)? 1: 0; 
		if(b >0){
			if(shootTime <=0){
				shootTime =getShootingTime();
				
				currentAmmo --;
			}
			else{
				b =0;
			}
		}
		return b;
	}

	@Override
	public float getShootingTime() { //damamge(newton?) per bullet
		return 0.1f;
	}

	@Override
	public float getFireMinPower() { //damamge(newton?) per bullet
		return 28;
	}

	@Override
	public float getFireMaxPower() { //damamge(newton?) per bullet
		return 28;
	}

	@Override
	public float getFireSpeed() {	//speed(m/s) per bullet
		return 1000;
	}

	@Override
	public float getReloadTime() {	//time(sec) per bullet
		return 1.7f;
	}

	@Override
	public float getBulletSpread() {
		return 0.6f;
	}

	@Override
	public void setMaxAmmo(int ammo) {
		maxAmmo =ammo;
		
	}

	@Override
	public void setCurrentAmmo(int ammo) {
		currentAmmo =ammo;
		
	}

	@Override
	public void stopreloading() {
		reloading =0;
		
	}

	@Override
	public int getSoundIndex() {
		return World.sndGun1;
	}

	@Override
	public String toString(){
		return "SubMachine gun";
	}

}
