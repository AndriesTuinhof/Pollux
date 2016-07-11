package betapanda.net;

public class PowerupType {
	public static final int MONEY_BOOST = 1;
	public static final int SPEED_BOOST = 2;
	public static final int MAX_AMMO = 3;
	public static final int BOMB = 4;
	public static final int DOUBLE_DAMAGE = 5;
	public static final int FIRE_BULLETS = 6;
	public static final int BUG_SPRAY = 7;
	
	
	public static String getString(int breed) {
		if(breed == MONEY_BOOST) return "money boost";
		if(breed == SPEED_BOOST) return "speed boost";
		if(breed == MAX_AMMO) return "ammo refill";
		if(breed == BOMB) return "a bomb";
		if(breed == DOUBLE_DAMAGE) return "double damage for 30 seconds";
		if(breed == FIRE_BULLETS) return "fire bullets";
		if(breed == BUG_SPRAY) return "insect repellant";
		
		return "Gigantic Lemons";
	}
}
