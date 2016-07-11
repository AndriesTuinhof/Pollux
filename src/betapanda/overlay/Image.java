package betapanda.overlay;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import org.w3c.dom.NamedNodeMap;

import awesome.core.Painter;
import awesome.core.PandaDisplay;
import awesome.core.ResourceLoader;

public class Image
{
	public int texture;
	public float width, height, xpos, ypos;
	public Image(NamedNodeMap att)
	{
		xpos = Float.parseFloat(att.getNamedItem("xpos").getNodeValue());
		ypos = Float.parseFloat(att.getNamedItem("ypos").getNodeValue());
		width = Float.parseFloat(att.getNamedItem("width").getNodeValue());
		height = Float.parseFloat(att.getNamedItem("height").getNodeValue());
		
		texture = ResourceLoader.getTexture(att.getNamedItem("res").getNodeValue(), false);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	}
	
	public void draw()
	{
		Painter.setTexture(texture);
		float x = rectify(xpos, PandaDisplay.getWidth());
		float y = rectify(ypos, PandaDisplay.getHeight());
		float w = rectify(width, PandaDisplay.getWidth());
		float h = rectify(height, PandaDisplay.getHeight());
		Painter.drawQuad(x, y, w, h);
		
	}
	
	private static float rectify(float f, float m){
		float x = Math.abs(f); // Get REAL value
		if(x<=1 && x>=0) x *= m; // Check if percentage or absolute
		if(f<0) x = m-x; // Wrap if needed
		return x;
	}
	
}
