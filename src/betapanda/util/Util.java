package betapanda.util;

import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class Util {
	
	public static int createRenderTexture(int width, int height, int internalFormat, int format, int attachment){
		int texture =glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, format, GL_FLOAT, (java.nio.FloatBuffer) null);
		glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, texture, 0);
		return texture;
	}

	public static void setTexture(int GL_Texture, int tex) {	
		GL13.glActiveTexture(GL_Texture);
		GL11.glBindTexture(GL_TEXTURE_2D, tex);
	}

	public static int addObject(Object[] a, Object o){
		for(int i =0; i <a.length; i ++)if(a[i] ==null){
			a[i] =o;
			return i;
		}
		new Exception("Array full :(").printStackTrace();
		return -1;
	}

	public static void removeObject(Object[] a, Object o){
		for(int i =0; i <a.length; i ++)if(a[i] ==o){
			a[i] =null;
			return;
		}
	}
	
}
