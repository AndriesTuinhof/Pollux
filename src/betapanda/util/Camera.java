package betapanda.util;

import awesome.core.PandaDisplay;
import awesome.math.Matrix;
import awesome.util.Mass;

public class Camera {

	public Mass mass =new Mass();
	public float sin, cos, tan;
	public float pitch, yaw;	
	private float FOV =90;	//standard
	
	private float near =0.1f, far =400;
	private boolean cameraIsOrtho =false;
	private float[] ortho =new float[]{-1, 1, -1, 1};

	private Matrix modelviewMatrix =new Matrix();
	private Matrix projectionMatrix =new Matrix();
	private Matrix modelviewProjectionMatrix =new Matrix();
	
	public void updateMatrix(){
		projectionMatrix.setIdentity();

		if(!cameraIsOrtho)
			projectionMatrix.setPerspective(FOV, (float)PandaDisplay.getWidth() /(float)PandaDisplay.getHeight(), near, far);
		else
			projectionMatrix.loadOrtho(ortho[0], ortho[1], -ortho[2], ortho[3], near, far);
		
		modelviewMatrix.setIdentity();
		modelviewMatrix.rotateX(-90);
		modelviewMatrix.rotateX(-pitch);
		modelviewMatrix.rotateZ(-yaw);
		modelviewMatrix.translate(-mass.getX(), -mass.getY(), -mass.getZ());
		modelviewProjectionMatrix.setMult(projectionMatrix, modelviewMatrix);
	
	}
	
	public Matrix getModelViewMatrix(){
		return modelviewMatrix;
	}

	public Matrix getProjectionMatrix(){
		return projectionMatrix;
	}

	public Matrix getmodelviewProjectionMatrix(){
		return modelviewProjectionMatrix;
	}

	public float getSin() {
		return sin;
	}

	public void setSin(float sin) {
		this.sin = sin;
	}

	public float getCos() {
		return cos;
	}

	public void setCos(float cos) {
		this.cos = cos;
	}

	public float getTan() {
		return tan;
	}

	public void setTan(float tan) {
		this.tan = tan;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getFOV() {
		return FOV;
	}

	public void setFOV(float fOV) {
		FOV = fOV;
	}

	public float getNear() {
		return near;
	}

	public void setNear(float near) {
		this.near = near;
	}

	public float getFar() {
		return far;
	}

	public void setFar(float far) {
		this.far = far;
	}

	public boolean isCameraIsOrtho() {
		return cameraIsOrtho;
	}

	public void setCameraIsOrtho(boolean cameraIsOrtho) {
		this.cameraIsOrtho = cameraIsOrtho;
	}

	public float[] getOrtho() {
		return ortho;
	}

	public void setOrtho(float[] ortho) {
		this.ortho = ortho;
	}

	public Mass getMass() {
		return mass;
	}
}
