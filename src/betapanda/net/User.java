package betapanda.net;

import betapanda.gameplay.entities.EntityCharacter;

public class User {

	public String name ="";
	public EntityCharacter entity;
	public int money =0;
	public float overlayMoney;

	public User(String name, EntityCharacter e){
		this.name =name;
		this.entity =e;
	}
}