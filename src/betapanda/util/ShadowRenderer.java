package betapanda.util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.opengl.GL11;

import awesome.math.Matrix;

public class ShadowRenderer {
	
	public ShadowRenderer(){
		shadowFrameBuffer =glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, shadowFrameBuffer);
		GL11.glViewport(0, 0, 2048, 2048);
		shadowDepthTexture =Util.createRenderTexture(2048, 2048, GL_DEPTH_COMPONENT24, GL_DEPTH_COMPONENT, GL_DEPTH_ATTACHMENT);
		glDrawBuffer(GL11.GL_NONE);
	}

	private Matrix modelViewProjection =new Matrix();
	private int shadowFrameBuffer;
	private int shadowDepthTexture;
	
	private Matrix proj =new Matrix(), view =new Matrix();
	public float x, y, z;
	public float rotate=45, height;
	public void shadowPass(){
		glBindFramebuffer(GL_FRAMEBUFFER, shadowFrameBuffer);
		glViewport(0, 0, 2048, 2048);
		glClear(GL_DEPTH_BUFFER_BIT);
		
		float s =30, d =200;
		proj.setIdentity();
		proj.loadOrtho(-s, s, -s, s, -d, d);
		view.setIdentity();
		view.rotateY(rotate);
		view.rotateZ(10);
		view.translate((int)x, (int)y,  height);
		modelViewProjection.setMult(proj, view);

		
	}
	
	public void shadowEndPass(){
		glDrawBuffer(GL_NONE);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		
	}
	
	public void dispose(){
		
	}
	
	public Matrix getModelviewProjectionMatrix(){
		return modelViewProjection;
	}

	public int getShadowTexture(){
		return shadowDepthTexture;
	}
}
