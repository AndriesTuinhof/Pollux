package betapanda.gameplay.entities;

import awesome.core.Painter;
import betapanda.gameplay.Player;
import betapanda.gameplay.world.World;

public class EntitySpitter extends EntityMonster
{

	public EntitySpitter(float size, Player p) {
		super(size, p);
	}
	
	@Override
	public void draw() {
		animationIndex = 0;
//		track.tick(skel, ani);
		World.spitterTrack.tick(World.spitterSkel, 0, 0);
		m.scale(10, 10, 10);
		Painter.setTexture(World.crabTex[0]);
		World.spitterSkel.draw(m);
	}

}
