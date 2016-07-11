package betapanda.gameplay.guns;

import awesome.util.Mass;

public interface Weapon {
	//	protected int GUN_MaxAmmo, GUN_Ammo;
	//	protected float GUN_FireMinPower; 
	//	protected float GUN_FireMaxPower;
	//	protected float GUN_FireSpeed;
	//	protected float GUN_ReloadTime;
	
	public abstract void initilize();			//this is used for openGL mostly
	public abstract void logic();				//mostly no opengl
	public abstract void draw();				//only drawing stuff
	public abstract void dispose();				//activate when going out of the activity that contains this object

	public abstract int reload();				//activating reloading and returning how many
	public abstract float reloading();			//returns true if it's reloading
	public abstract int reloadingBullets();
	public abstract boolean reloadTrigger();
	
	public abstract Mass getMass();
	public abstract int getClipSize();			//returning the max ammo that the gun can contain one clip
	public abstract int getClipAmmo();		//returning the current ammo inside of the clip
	public abstract int getAmmo();				//returning the all the ammo
	public abstract int shoot();				//returning the amount of bullets shot per loop
	public abstract float getShootingTime();	//returning the minimum amount of damage it can do
	public abstract float getFireMinPower();	//returning the minimum amount of damage it can do
	public abstract float getFireMaxPower();	//returning the maximum amount of damage it can do
	public abstract float getFireSpeed();		//returning the speed of the bullets 
	public abstract float getReloadTime();		//returning the reloading time per bullet
	public abstract float getBulletSpread();	//returning the spread of the bullets 
	
	public abstract void setAmmo(int ammo);
	public abstract void setMaxAmmo(int ammo);
	public abstract void setCurrentAmmo(int ammo);
	public abstract void stopreloading();
	public abstract int getSoundIndex();
}
