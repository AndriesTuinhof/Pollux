package betapanda.overlay;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import awesome.core.Painter;
import awesome.core.PandaDisplay;
import awesome.core.ResourceLoader;
import awesome.core.Timing;
import awesome.core.Xbox;
import awesome.math.Matrix;
import awesome.util.FastMath;
import betapanda.util.Model;
import betapanda.PolluxGame;
import betapanda.core.Shader;
import betapanda.gameplay.Player;
import betapanda.gameplay.entities.EntityPowerUp;
import betapanda.gameplay.guns.Weapon;
import betapanda.gameplay.world.World;
import betapanda.net.User;

public class Scoreboard {

	public boolean show =false, wasShowed =true;
	private float alpha;
	
	private PolluxGame game;
	private User[] users;
	public gunSelection selection;
	private int pointerTexture;// TODO: Release me!
	private int friendTexture;// TODO: Release me!
	private int bloodScreen;// TODO: Release me!
	private int powerupSphere;// TODO: Release me!
	private int diedTexture;// TODO: Release me!
	private int buttonYTexture;// TODO: Release me!
	private int xboxControls;// TODO: Release me!
	private String[] messages =new String[5];
	private float messageFade;
	
	private static long powerupAlertTime;
	private static String powerupString;
	public static long doubleDamageTime;
	public static long fireBulletTime;
	public static long speedBoostTime;
	private static long gameOverTime;
	private ArrayList<Image> images = new ArrayList<Image>(0);
	private ArrayList<Bar> bars = new ArrayList<Bar>(0);
	
	public Scoreboard(PolluxGame game, User[] users) {
		this.game =game;
		this.users =users;
		selection =new gunSelection();
		pointerTexture = ResourceLoader.getTexture("Textures/pointer.png", false);
		friendTexture = ResourceLoader.getTexture("Textures/friend.png", false);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		bloodScreen = ResourceLoader.getTexture("Textures/bloody_screen.png", false);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		powerupSphere = ResourceLoader.getTexture("Textures/powerup.png", false);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		diedTexture = ResourceLoader.getTexture("Textures/sad.png", false);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		buttonYTexture = ResourceLoader.getTexture("Textures/button_y.png", false);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		

		xboxControls = ResourceLoader.getTexture("Textures/xbox.png", false);
		
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder(); 
			Document doc = db.parse(ResourceLoader.openFile("config/hud.xml"));

			Node n = doc.getDocumentElement().getFirstChild();
			do
			{
				if(n.getNodeName()=="image") images.add(new Image(n.getAttributes()));
				if(n.getNodeName()=="bar") bars.add(new Bar(n.getAttributes()));
				n = n.getNextSibling();
			}
			while(n!=null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public static void alertPowerup(String s)
	{
		powerupString = s;
		powerupAlertTime = System.currentTimeMillis();
	}
	
	public float gunShowTimer;
	public void logic(){
		if(show && !wasShowed){
			alpha -=0.1f *Timing.delta();
			if(alpha <=0)
				wasShowed =show;
		}
		
		if(!show && wasShowed){
			alpha +=0.1f *Timing.delta();
			if(alpha >=1)
				wasShowed =show;				
		}

		alpha =Math.max(0, Math.min(alpha, 1));
		selection.logic();
		
		if(gunShowTimer >0)
			gunShowTimer -=Timing.delta() /60f;
		if(Xbox.getMainController().buttonPressed(Xbox.btn_rightShoulder)){
			game.getPlayer().arms.currentGun --;
			gunShowTimer =2;
		}
		if(Xbox.getMainController().buttonPressed(Xbox.btn_leftShoulder)){
			game.getPlayer().arms.currentGun ++;	
			gunShowTimer =2;		
		}
		int a =game.getPlayer().arms.currentGun;
		game.getPlayer().arms.currentGun =Math.max(Math.min(game.getPlayer().arms.guns.length -1, a), 0);
		
		if(messageFade >0)
			messageFade -=Timing.delta() /60f;
		
	}
	
	float scale =1.2f;
	public void draw(){
		Painter.setBlankTexture();
		powerupSpheres();
		Painter.setBlankTexture();
		drawMoneys();
		
//		showControls();

		Painter.setColor(1, 1, 0);
		if(System.currentTimeMillis()-doubleDamageTime<30000){
			int s = 30-(int)(System.currentTimeMillis()-doubleDamageTime)/1000;
			Painter.drawString("Double Damage for "+s+" seconds!", 150, 150, 30);
		}
		if(System.currentTimeMillis()-fireBulletTime<30000){
			int s = 30-(int)(System.currentTimeMillis()-fireBulletTime)/1000;
			Painter.drawString("Fire bullets for "+s+" seconds!", 150, 150+30, 30);
		}
		if(System.currentTimeMillis()-speedBoostTime<10000){
			int s = 10-(int)(System.currentTimeMillis()-speedBoostTime)/1000;
			Painter.drawString("Speed boost for "+s+" seconds!", 150, 150+60, 30);
		}
		Painter.setColor(1, 1, 1);
		int ammo =game.getPlayer().arms.getCurrentGun().getClipAmmo();
		int maxAmmo =game.getPlayer().arms.getCurrentGun().getAmmo();
		Painter.drawString(ammo+"/"+maxAmmo, PandaDisplay.getWidth() -250 *scale, 50 *scale, 32);
		if(PolluxGame.wave>0)
			Painter.drawString("Wave "+PolluxGame.wave, PandaDisplay.getWidth() -330 -20 *game.timer, 100, 50);
		else Painter.drawString("Shoot the bug to begin...", PandaDisplay.getWidth() -500 -20 *game.timer, 100, 32);
		
		
//		drawDebug();
		
		drawPowerupAlert();
		
		if(gunShowTimer >0){	
			Painter.setWhite(gunShowTimer);			
			Weapon we =game.getPlayer().arms.getCurrentGun();
			Painter.drawString(we.toString()+"  "+we.getClipAmmo()+"/"+we.getAmmo(), 15, 40, 20);
		}
		
		Painter.setWhite();
		if(waveCompleteTime >0 && System.currentTimeMillis() -waveCompleteTime < 5000){
			if(PolluxGame.wave==0){
				controlSlider+=Timing.delta()*16;	
				Painter.drawStringCenter("Game started, now survive?!", PandaDisplay.getWidth() /2f, PandaDisplay.getHeight() /3f *2f, 40);
			}
			else{
				Painter.drawStringCenter("Wave "+finishedWave+" complete, get ready!", PandaDisplay.getWidth() /2f, PandaDisplay.getHeight() /3f *2f, 40);
			}
		}
		
		float w = PandaDisplay.getWidth();
		float h = PandaDisplay.getHeight();
		
		// Images
		Painter.setColor(1,1,1);
		for(Image i: images) if(i!=null) i.draw();
		// Bars
		Painter.setBlankTexture();
		for(Bar b: bars) if(b!=null) b.draw();
		
		Painter.setBlankTexture();
		// Grenade Bar
		Painter.setColor(0.2f,0.2f,0.2f);
		Painter.drawQuad(110, h-195, 165, 20);
		Painter.setColor(0.5f,0.5f,0.5f);
		Painter.drawQuad(115, h-190, 155*Math.max(Player.grenadeRegen, 0), 10);
		if(Player.grenadeRegen>=1){
			Painter.setColor(1,1,0, (System.currentTimeMillis()%1000)/1500f+0.25f);
			Painter.drawQuad(115, h-190, 155*Math.max(Player.grenadeRegen, 0), 10);
		}
		
		bloodyScreen();
		
		if(System.currentTimeMillis()-gameOverTime<5000)
		{
			
			Painter.setColor(0, 0, 0, 0.3f);
			Painter.drawQuad(0, 0, w, h);

			Painter.setColor(1, 1, 1);
			Painter.drawStringCenter("GAME OVER!", w*0.5f, h*0.7f, 100);
			return;
		}
		

		friendIndicator();
		diedFriendIndicator();
		
		if(Player.isDead){
			Painter.setWhite();
			Painter.drawStringCenter("You are dead.", PandaDisplay.getWidth() /2f, PandaDisplay.getHeight() /3f *2f+20, 50);
			Painter.drawStringCenter("Perhaps your friend could revive you...", PandaDisplay.getWidth() /2f, PandaDisplay.getHeight() /3f *2f -40, 30);
		}
		

		
	}
	
	private float controlSlider = 0;
	private void showControls() {
		if(PolluxGame.wave>=1){
			controlSlider = 0;
			return;
		}
		Painter.setColor(1, 1, 1);
		Painter.setTexture(xboxControls);
		Painter.drawQuad(PandaDisplay.getWidth()-800+controlSlider, PandaDisplay.getHeight()-630, 750, 750);
	}

	private void drawPowerupAlert() {
		if(System.currentTimeMillis()-powerupAlertTime>4000) return;
		if(powerupString==null) return;
		Painter.setColor(1, 1, 00);
		float w = PandaDisplay.getWidth();
		float h = PandaDisplay.getHeight();
		Painter.drawStringCenter(powerupString, w*0.5f, h*0.8f, 48);
		Painter.setColor(1, 1, 1);
	}

	private void drawDebug() {
		Painter.setColor(1, 1, 1);
		Painter.drawString("Health: "+Player.health, 100, 100, 12);
		if(game.getPlayer().speedBoostTimer >=0)
			Painter.drawString("Speed boost: "+Math.floor(game.getPlayer().speedBoostTimer *10f) /10f+"s", 100, 130, 12);
		selection.draw();
		
		if(messageFade >0){
			Painter.setWhite(messageFade);
			for(int i = 0; i<messages.length; i++)if(messages[i] !=null)
				Painter.drawString(messages[i], 5, 105 -20 *i, 15);
		}
		
	}

	private void drawMoneys() {
		float width =200 *scale;
		float w =width;//PandaDisplay.getWidth();	
		float h =PandaDisplay.getHeight() -PandaDisplay.getHeight() *-0.1f *alpha;	
		float y =(h /1.7f) -30;
		
		Painter.setColor(0.2f, 0.2f, 0.2f, 0.8f *alpha);
		Painter.drawQuad(w -width,  y, width, 30 *scale);
		Painter.setColor(0.3f, 0.3f, 0.3f, 0.5f *alpha);
		int a =0;
		int teamMoneys =0;

		Painter.setColor(0.3f, 0.3f, 0.3f, 0.5f *alpha);
		Painter.drawQuad(w -width,  y-32 *a-(30 *scale), width, 30 *scale);
//		Painter.drawString(users[i].entity.mass.getX()+" "+ users[i].entity.mass.getY(), 5, h -140 -(32 *i)-12, 10);	
//		Painter.setWhite(alpha);
		Painter.setColor(1f, 1f, 0.4f, alpha);
		
		Painter.setBlankTexture();
		Player p = game.getPlayer();
		float moneyDif = (p.money-p.overlayMoney);
		float increasedMoney = p.overlayMoney+moneyDif*Timing.delta()*0.1f;
//		if((int)p.overlayMoney<(int)(increasedMoney)) game.getWorld().playSound(game.getWorld().sndCoinDrop, false, 1, 1.0f);
		p.overlayMoney = increasedMoney;

		Painter.setBlankTexture();
		Painter.drawString("You: "+(int)Math.round(p.overlayMoney), 10, y-32 *a-30, 12 *(1.5f +moneyDif /50f));
		teamMoneys += game.getPlayer().money;

		Painter.setBlankTexture();
		for(int i =0; i <users.length; i ++)if(users[i] !=null){
			moneyDif = (users[i].money-users[i].overlayMoney);
			increasedMoney = users[i].overlayMoney+moneyDif*Timing.delta()*0.1f;
			users[i].overlayMoney = increasedMoney;
			Painter.setColor(0.3f, 0.3f, 0.3f, 0.5f *alpha);
			Painter.drawQuad(w -width,  y-32 *(a +1)-(30 *scale), width, 30 *scale);
//			Painter.drawString(users[i].entity.mass.getX()+" "+ users[i].entity.mass.getY(), 5, h -140 -(32 *i)-12, 10);	
			Painter.setWhite(alpha);
			Painter.drawString(users[i].name+": "+users[i].money, 10, y-32 *(a +1)-30, 12 *(1.4f +moneyDif /50f));
			teamMoneys +=increasedMoney;
			a ++;
		}

		Painter.setColor(1, 1, 1, 1 *alpha);
		Painter.drawStringCenter("Team: "+teamMoneys, w /2f,  y +15, 24);
	}

	public void addMessage(String s){
		for(int i=0; i<messages.length-1; i++){
			messages[i] =messages[i+1];
		}
		messages[messages.length-1] =s;
		messageFade =15f;
			
	}
	
	private void bloodyScreen()
	{
		Painter.setTexture(bloodScreen);
		
		float a = Math.max(0, Math.min(1, (100-Player.health)/100f ));
		if(a<0.05) return;
		Painter.setColor(1, 0.3f, 0.3f, a);
		Painter.drawQuad(0, 0, PandaDisplay.getWidth(), PandaDisplay.getHeight());
	}
	
	private float[] indicatorTemp = new float[4];
	private float[] pCol ={	28 /255f,112 /255f,167 /255f,
							242 /255f,201 /255f,78 /255f,
							166 /255f,28 /255f,122 /255f,
							0.5f,0.5f,0.2f};
	private void friendIndicator()
	{
		float w = PandaDisplay.getWidth();
		float h = PandaDisplay.getHeight();
		float dia = (float)Math.hypot(w, h);
		
		for(User u: game.getUsers()) if(u!=null && !u.entity.isDead){
			Matrix camera = game.getCamera().getmodelviewProjectionMatrix();
			camera.multiplyVector(u.entity.mass.x, u.entity.mass.y, u.entity.mass.z+2, 1.0f, indicatorTemp);
			float r = (float)Math.toDegrees(Math.atan2(indicatorTemp[0],indicatorTemp[1]));
			float sz = dia*0.02f;
			float s = FastMath.sin(r);
			float c = -FastMath.cos(r);
			float x = PandaDisplay.getWidth()/2 + s*500;
			float y = PandaDisplay.getHeight()/2 - c*500;
			
			
			x = indicatorTemp[0]/indicatorTemp[2]*w/2f+w/2f;
			y = indicatorTemp[1]/indicatorTemp[2]*h/2f+h/2f;
			x = x*Math.signum(indicatorTemp[2]);
			y = y*Math.signum(indicatorTemp[2]);
			
			float al= 0;
			float aaa = FastMath.hypot(Math.abs(w/2f-x)/w, Math.abs(h/2f-y)/h)*7-3.0f;
			
			if(x>w-80) x = w-80;
			if(y>h-80) y = h-80;
			if(x<80) x = 80;
			if(y<80) y = 80;
			
			al = Math.max(0, Math.min(1,aaa));
			float red =pCol[u.entity.playerIndex *3];
			float green =pCol[u.entity.playerIndex *3 +1];
			float blue =pCol[u.entity.playerIndex *3 +2];
			Painter.setColor(red, green, blue, al *0.5f);
			Painter.setTexture(pointerTexture);
			Painter.beginDrawing();
			Painter.setVertex( x + (-sz)*c + (sz)*s, y+ (-sz)*s + (-sz)*c, 0, 0);
			Painter.setVertex( x + (sz)*c + (sz)*s, y+ (sz)*s + (-sz)*c, 1, 0);
			Painter.setVertex( x + (sz)*c + (-sz)*s, y+ (sz)*s + (sz)*c, 1, 1);
			
			Painter.setVertex( x + (-sz)*c + (sz)*s, y+ (-sz)*s + (-sz)*c, 0, 0);
			Painter.setVertex( x + (sz)*c + (-sz)*s, y+ (sz)*s + (sz)*c, 1, 1);
			Painter.setVertex( x + (-sz)*c + (-sz)*s, y+ (-sz)*s + (sz)*c, 0, 1);
			Painter.stopDrawing(GL11.GL_TRIANGLES);
			
			Painter.setColor(0,0,0, al);
			Painter.setTexture(friendTexture);
			Painter.drawQuad(x-sz/2f, y-sz/2f, sz, sz);
		}
	}
	
	private void diedFriendIndicator()
	{
		float w = PandaDisplay.getWidth();
		float h = PandaDisplay.getHeight();
		float dia = (float)Math.hypot(w, h);
		
		for(User u: game.getUsers()) if(u!=null && u.entity.isDead){
			Matrix camera = game.getCamera().getmodelviewProjectionMatrix();
			camera.multiplyVector(u.entity.mass.x, u.entity.mass.y, u.entity.mass.z+2, 1.0f, indicatorTemp);
			float r = (float)Math.toDegrees(Math.atan2(indicatorTemp[0],indicatorTemp[1]));
			float sz = dia*0.02f;
			float s = FastMath.sin(r);
			float c = -FastMath.cos(r);
			float x = PandaDisplay.getWidth()/2 + s*500;
			float y = PandaDisplay.getHeight()/2 - c*500;
			
			
			x = indicatorTemp[0]/indicatorTemp[2]*w/2f+w/2f;
			y = indicatorTemp[1]/indicatorTemp[2]*h/2f+h/2f;
			x = x*Math.signum(indicatorTemp[2]);
			y = y*Math.signum(indicatorTemp[2]);
			
			float al= 0;
			float aaa = FastMath.hypot(Math.abs(w/2f-x)/w, Math.abs(h/2f-y)/h)*7-3.0f;
			
			if(x>w-80) x = w-80;
			if(y>h-80) y = h-80;
			if(x<80) x = 80;
			if(y<80) y = 80;
			
			al =1f; Math.max(0, Math.min(1,aaa));
			float red =pCol[u.entity.playerIndex *3];
			float green =pCol[u.entity.playerIndex *3 +1];
			float blue =pCol[u.entity.playerIndex *3 +2];
			Painter.setColor(red, green, blue, al);
			Painter.setTexture(pointerTexture);
			Painter.beginDrawing();
			Painter.setVertex( x + (-sz)*c + (sz)*s, y+ (-sz)*s + (-sz)*c, 0, 0);
			Painter.setVertex( x + (sz)*c + (sz)*s, y+ (sz)*s + (-sz)*c, 1, 0);
			Painter.setVertex( x + (sz)*c + (-sz)*s, y+ (sz)*s + (sz)*c, 1, 1);
			
			Painter.setVertex( x + (-sz)*c + (sz)*s, y+ (-sz)*s + (-sz)*c, 0, 0);
			Painter.setVertex( x + (sz)*c + (-sz)*s, y+ (sz)*s + (sz)*c, 1, 1);
			Painter.setVertex( x + (-sz)*c + (-sz)*s, y+ (-sz)*s + (sz)*c, 0, 1);
			Painter.stopDrawing(GL11.GL_TRIANGLES);
			
			Painter.setColor(0,0,0, al);
			Painter.setTexture(diedTexture);
			Painter.drawQuad(x-sz/2f, y-sz/2f, sz, sz);
			
			Painter.setColor(1,1,1, al);
			Painter.setTexture(buttonYTexture);
			Painter.drawQuad(x-sz/2f, y-sz/2f+40, sz, sz);
		}
	}
	
	private void powerupSpheres()
	{
		float w = PandaDisplay.getWidth();
		float h = PandaDisplay.getHeight();
		float dia = (float)Math.hypot(w, h)*0.03f;
		
		for(int i=0;i<World.powerUPS.length;i++) if(World.powerUPS[i]!=null){
			EntityPowerUp u = World.powerUPS[i];
			Matrix camera = game.getCamera().getmodelviewProjectionMatrix();
			camera.multiplyVector(u.mass.x, u.mass.y, u.mass.z+1, 1.0f, indicatorTemp);
			float r = (float)Math.toDegrees(Math.atan2(indicatorTemp[0],indicatorTemp[1]));
			float sz = dia;
			float s = FastMath.sin(r);
			float c = -FastMath.cos(r);
			float x = PandaDisplay.getWidth()/2 + s*500;
			float y = PandaDisplay.getHeight()/2 - c*500;
			
			
			x = indicatorTemp[0]/indicatorTemp[2]*w/2f+w/2f;
			y = indicatorTemp[1]/indicatorTemp[2]*h/2f+h/2f;
			x = x*Math.signum(indicatorTemp[2]);
			y = y*Math.signum(indicatorTemp[2]);
			
			
			if(x>w-80) x = w-80;
			if(y>h-80) y = h-80;
			if(x<80) x = 80;
			if(y<80) y = 80;
			
			c = 1;
			s = 0;
			
			Painter.setColor(1,1,1);
			Painter.setTexture(powerupSphere);
			Painter.beginDrawing();
			Painter.setVertex( x + (-sz)*c + (sz)*s, y+ (-sz)*s + (-sz)*c, 0, 0);
			Painter.setVertex( x + (sz)*c + (sz)*s, y+ (sz)*s + (-sz)*c, 1, 0);
			Painter.setVertex( x + (sz)*c + (-sz)*s, y+ (sz)*s + (sz)*c, 1, 1);
			
			Painter.setVertex( x + (-sz)*c + (sz)*s, y+ (-sz)*s + (-sz)*c, 0, 0);
			Painter.setVertex( x + (sz)*c + (-sz)*s, y+ (sz)*s + (sz)*c, 1, 1);
			Painter.setVertex( x + (-sz)*c + (-sz)*s, y+ (-sz)*s + (sz)*c, 0, 1);
			Painter.stopDrawing(GL11.GL_TRIANGLES);
		}
	}
	
	
	public void dipose(){
		
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show =show;
	}
	
	public class gunSelection{
		
		float x, y, width =64, height;
		public boolean show =true, wasShowed =true;
		private float alpha;
		
		private Shader gunSelection;
		private ArrayList<File> objects =new ArrayList<>(0);
		private int selected =0;
		
		public Model newObject;
		private float scale =4;
		
		public gunSelection(){
			gunSelection =Shader.getShader("gunSelection");
			
			File[] f =new File(ResourceLoader.getPath()+"Models/environment").listFiles();
					
			for(File file: f)if(f !=null)
				objects.add(file);
			
			objects.add(new File("Chunk"));
			
		}

		float minSpeed =0.1f, speed =0.1f;
		public void logic(){
			try{
				if(alpha ==1 || alpha ==0)
					speed =minSpeed;
				
				if(show && !wasShowed){
					alpha -=speed *Timing.delta();
					if(alpha <=0){
						wasShowed =show;
						alpha =0;
					}
				}
				
				if(!show && wasShowed){
					alpha +=speed *Timing.delta();
					if(alpha >=1){
						wasShowed =show;	
						alpha =1;		
					}
				}
				
	
				float mx =PandaDisplay.getWidth(), my =0;
				float nx =PandaDisplay.getWidth() /2f, ny =PandaDisplay.getHeight() /2f;
				x =mx +(nx -mx) *alpha -width /2f;
				y =my +(ny -my) *alpha -height /2f;
				width =300 *alpha;
				height =500 *alpha;
	
				
				if(Xbox.getMainController().buttonPressed(Xbox.btn_back)){
//					toggle();
				
				}
	
				scale=Math.max(scale, 0);
				
				selected =Math.max(Math.min(selected, objects.size() -1), 0);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		float fs, rot, rrot;
		public void draw(){
			float w =PandaDisplay.getWidth();
			gunSelection.bind();
			GL20.glUniform1f(2, 100);
			GL20.glUniform1f(3, 0);
			GL20.glUniform2f(4, x, y);
			Painter.setWhite();
			Painter.drawQuadNoShader(w -width, 0, width, height);
			File f;
			for(int i =0; i <objects.size(); i++)if((f =objects.get(i)) !=null){
				Painter.setColor(0, 0, 0, alpha);
				if(selected ==i)
					Painter.setColor(0.6f, 0.6f, 0.1f, alpha);
				Painter.drawStringCenter(f.getName(), w -width /2f, height -i *20 -20, 20 *alpha);
			}
			
			Painter.setWhite(alpha);
			Painter.drawString("scale: "+scale, PandaDisplay.getWidth() -160, 500, 15);
		}
		
		public void toggle(){
			if((alpha >0 && alpha <1))
				speed =minSpeed *10f;
			else
				show =!show;
			
		}
		
		public boolean showing(){
			return !show && !wasShowed;
		}
	}

	private long waveCompleteTime;
	private long finishedWave;
	public void review(int wave){
		waveCompleteTime =System.currentTimeMillis();
		finishedWave = wave;
	}

	public static void alertGameOver()
	{
		gameOverTime = System.currentTimeMillis();
		doubleDamageTime = 0;
	}
}
