package betapanda.net;

public class Packet {

	public static final int PING = 1;
	public static final int SERVER_CLIENT_HANDSHAKE = 2;
	public static final int SERVER_CLIENT_BROADCAST = 3;
	
	public static final int SERVER_CLIENT_PLAYERJOIN = 10;
	public static final int SERVER_CLIENT_PLAYERUPDATE = 11;
	public static final int SERVER_CLIENT_PLAYEREVENT = 12;
	public static final int SERVER_CLIENT_PLAYERLEAVE = 13;
	
	public static final int SERVER_CLIENT_ENEMYJOIN = 20;
	public static final int SERVER_CLIENT_ENEMYUPDATE = 21;
	public static final int SERVER_CLIENT_ENEMYEVENT = 22;
	public static final int SERVER_CLIENT_ENEMYLEAVE = 23;
	
	
	public static final int SERVER_GAMEPLAY_EVENT = 30;
	public static final int POWERUP_SPAWN = 31;
	public static final int POWERUP_DESPAWN = 32;
	public static final int POWERUP_PLAYER_TOUCHED = 33;
	public static final int POWERUP_SERVER_CONFIRM_TOUCH = 34;
	public static final int SERVER_GAME_OVER = 35;
	public static final int THROW_GRENADE = 36;
	
	public static final int PLAYER_DIED=41;
	public static final int PLAYER_REVIVED=42;
	public static final int SERVER_RESET = 43;
}
