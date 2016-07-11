package betapanda.util.particle;

import awesome.util.Mass;

public abstract class Particle {

	public Mass mass =new Mass();
	
	public abstract void logic();
	public abstract void setup(int i);
	public abstract float getLifeTime();
	public abstract float getSize();
}
