package betapanda.overlay;

import java.awt.Color;

import org.w3c.dom.NamedNodeMap;

import awesome.core.Painter;
import awesome.core.PandaDisplay;
import awesome.core.ResourceLoader;
import betapanda.gameplay.Player;

public class Bar
{
	public float width, height, xpos, ypos, border;
	public Color emptyColor, fullColor, borderColor;
	private valueHook hook;
	public Bar(NamedNodeMap att)
	{
		xpos = Float.parseFloat(att.getNamedItem("xpos").getNodeValue());
		ypos = Float.parseFloat(att.getNamedItem("ypos").getNodeValue());
		width = Float.parseFloat(att.getNamedItem("width").getNodeValue());
		height = Float.parseFloat(att.getNamedItem("height").getNodeValue());
		emptyColor = Color.decode(att.getNamedItem("emptycolor").getNodeValue());
		fullColor = Color.decode(att.getNamedItem("fullcolor").getNodeValue());
		borderColor = Color.decode(att.getNamedItem("bordercolor").getNodeValue());
		border = Float.parseFloat(att.getNamedItem("border").getNodeValue());

		String binding = att.getNamedItem("binding").getNodeValue();
		if("player_stamina".equals(binding))
			hook = new valueHook(){public float value(){return Math.max(0, Player.feet.runningSteam /4);}};
		if("player_health".equals(binding))
			hook = new valueHook(){public float value(){return Math.max(0, Player.health /100);}};
	}
	
	public void draw()
	{
		if(hook==null) return;
		float x = rectify(xpos, PandaDisplay.getWidth());
		float y = rectify(ypos, PandaDisplay.getHeight());
		float w = rectify(width, PandaDisplay.getWidth());
		float h = rectify(height, PandaDisplay.getHeight());
		
		float r1=emptyColor.getRed()/255f, g1=emptyColor.getGreen()/255f, b1=emptyColor.getBlue()/255f;
		float r2=fullColor.getRed()/255f, g2=fullColor.getGreen()/255f, b2=fullColor.getBlue()/255f;
		// Sprint Bar
		Painter.setColor(borderColor.getRed()/255f, borderColor.getGreen()/255f, borderColor.getBlue()/255f);
		Painter.drawQuad(x, y, w, h);
		float p = 0.5f;
		float ip = 1.0f-p;
		// TODO: Blend between full and empty
		Painter.setColor(r1*ip+r2*p, g1*ip+g2*p, b1*ip+b2*p);
		Painter.drawQuad(x+border, y+border, w*hook.value()-border*2, h-border*2);
		
	}
	
	private static float rectify(float f, float m){
		float x = Math.abs(f); // Get REAL value
		if(x<=1 && x>=0) x *= m; // Check if percentage or absolute
		if(f<0) x = m-x; // Wrap if needed
		return x;
	}
	
	private interface valueHook {
		public float value();
	}
	
}
