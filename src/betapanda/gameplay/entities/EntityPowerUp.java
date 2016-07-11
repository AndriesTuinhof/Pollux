package betapanda.gameplay.entities;

import awesome.net.PacketWriter;
import betapanda.gameplay.Entity;
import betapanda.gameplay.Player;
import betapanda.net.Network;
import betapanda.net.Packet;
import betapanda.util.collision.Collision;

public class EntityPowerUp extends Entity{

	private Player player;
	private int type, id;
	private boolean sent =false;
	public EntityPowerUp(float x, float y, float z, int id, int type, Player player) {
		mass.x =x;
		mass.y =y;
		mass.z =z;
		this.id =id;
		this.type =type;
//		system =new ParticleSystem_groundMount(this.mass);
//		system.initialize();
		this.player =player;
	}
	
	long sentTimeout = 0;
	@Override
	public void logic() {
		if(System.currentTimeMillis()-sentTimeout>3000)
		{
			sentTimeout = System.currentTimeMillis();
			sent = false;// Allow re-sending of touch
		}
		
		float d =(float) Math.hypot(mass.x -player.mass.x, mass.y -player.mass.y);
		
		if(d >=2 || sent)
			return;
		
		sent =true;
		PacketWriter.createPacket(Packet.POWERUP_PLAYER_TOUCHED);
		PacketWriter.writeInt(id);
		byte[] pack =PacketWriter.getPacketAsData();
		Network.sendPacket(pack);
		
		
		
	}
	
	public int getType()
	{
		return type;
	}

	@Override
	public void draw() {
//		Painter3D.setColor(0, 0, 1);
//		if(type==1) Painter3D.setColor(1, 1, 0);
//		Painter3D.drawQuad(mass.x, mass.y, mass.z +0.5f, 1, 1, null);
		
	}

	@Override
	public void drawShadow() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collision getCollision() {
		return null;
	}

}
