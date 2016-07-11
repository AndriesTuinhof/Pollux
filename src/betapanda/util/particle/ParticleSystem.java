package betapanda.util.particle;

public abstract class ParticleSystem {

	public ParticleSystem(){
		ParticleManager.systems.add(this);
	}
	
	public abstract void initialize();
	public abstract void logic();
	public abstract void draw();
	public abstract void dispose();
	
}
