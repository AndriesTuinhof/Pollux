package betapanda.util.animation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import awesome.math.Matrix;
import betapanda.core.Shader;

public class Model {

	public Bone[] bones;
	public Mesh[] meshes;	
	public Binding[] bindings;
	
	private boolean isRigged =false;
	private static Shader wavefront, animation;
	private static Shader ShadowAnimation;
	
	private Matrix matrix;
	
	public Model(InputStream is){
		if(wavefront ==null){
			wavefront =Shader.getShader("wavefront", "matrixBlock");
		}
		if(animation ==null){
			animation =Shader.getShader("animation", "matrixBlock");
			ShadowAnimation =Shader.getShader("Shadow_animation", "matrixBlock");
		}
		try{
			BufferedReader br =new BufferedReader(new InputStreamReader(is));
			 
			String line ="";
			while((line =br.readLine()) !=null){
				if(line.startsWith("BONES")){
					int bonesCount =Integer.parseInt(line.split(" ")[1]);
					bones =new Bone[bonesCount];
					for(int i =0; i <bones.length; i++) bones[i] =readBone(br);
					
					isRigged =true;
				}
				if(line.startsWith("MESHES")){
					String[] data =line.split(" ");
					int meshesCount =Integer.parseInt(data[1]);
					meshes =new Mesh[meshesCount];
					for(int i =0; i <meshesCount; i++){
						
						data =(line =br.readLine().trim()).split(" ");
						String meshName =data[1];
						
						// Mesh Data
						float[] vertices =readFloatArray(br.readLine());
						float[] normals =readFloatArray(br.readLine());
						float[] uvs =readFloatArray(br.readLine());
						int polyCount =Integer.parseInt(br.readLine().trim().split(" ")[1]);
						int[] polyData =readIntArray(br.readLine());
						 
						line =br.readLine();
						data =line.trim().split(" ");
						int controllersCount =Integer.parseInt(data[1]);
						bindings =new Binding[controllersCount];
						
						for(int c =0; c <controllersCount; c ++){
							Matrix stupidMatrix =new Matrix();
							stupidMatrix.set(readFloatArray(br.readLine()));
							
							data =(line =br.readLine().trim()).split(" ");
							int bonesCount =Integer.parseInt(data[1]);
							String[] boneNames =br.readLine().trim().split(" ");
								
							Matrix[] inverseMatrices =new Matrix[bonesCount];
							for(int bone =0; bone <bonesCount; bone++){
								Matrix ma =new Matrix();
								ma.set(readFloatArray(br.readLine()));
								inverseMatrices[bone] =ma;
							}
							
							/*
							 * Vertex & Bone weights
							 */
							br.readLine();	
							float[] vertexBindings =readFloatArray(br.readLine());
							
							bindings[i] =new Binding(stupidMatrix, boneNames, inverseMatrices, vertexBindings, this);
						}
						meshes[i] =new Mesh(vertices, normals, uvs, polyCount, polyData, bindings[i]);
						meshes[i].name =meshName;
					}					
				}			 
			}
			tick();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Bone findBone(String name){
		for(int i =0; i< bones.length; i++)if(bones[i].name.toUpperCase().equals(name.toUpperCase())){
			return bones[i];
		}
		return null;
	}
	
	private float[] readFloatArray(String s){
		String[] data = s.trim().split(" ");
		float[] array = new float[data.length];
		for(int i=0;i<data.length;i++) array[i] =Float.parseFloat(data[i]);
		return array;
	}
	
	private int[] readIntArray(String s){
		String[] data = s.trim().split(" ");
		int[] array = new int[data.length];
		for(int i=0;i<data.length;i++) array[i] =Integer.parseInt(data[i]);
		return array;
	}
	
	private Bone readBone(BufferedReader br) throws IOException{
		String nameLine =br.readLine();
		String parentLine =br.readLine();
		String matrixLine =br.readLine();
		
		String Bone_Name =nameLine.trim();
		String[] data =Bone_Name.split(" "); 
		Bone_Name =data[0];

		String Bone_Parent =parentLine.trim();
		data =Bone_Parent.split(" "); 
		int parentID =Integer.parseInt(data[0]);
		Bone parent =null;
		if(parentID !=-1) parent =bones[parentID];
		
		Matrix m =new Matrix();
		float[] floats =readFloatArray(matrixLine);
		m.set(floats);	
		
		return new Bone(Bone_Name, m, parent);
	}
	
	public void upload(){
		for(int i =0; i <bindings.length; i++){//the number will prevent for sending matrices on the same numbers
			bindings[i].upload(0);
		}

	}

	public void draw(Matrix m){
		if(isRigged){
			animation.bind();
			upload();
		}
		else wavefront.bind();
		
		GL20.glUniformMatrix4fv(1, true, m.asBuffer());
		for(int i =0; i <meshes.length; i++)
			meshes[i].draw();
	}
	
	public void draw(){
		if(matrix ==null)
			return;
		if(isRigged){
			animation.bind();
			upload();
		}
		else wavefront.bind();
		
		GL20.glUniformMatrix4fv(1, true, matrix.asBuffer());
		for(int i =0; i <meshes.length; i++)
			meshes[i].draw();
	}
	
	public Matrix getMatrix() {
		return matrix;
	}

	public void setMatrix(Matrix matrix) {
		this.matrix = matrix;
	}

	public void ShadowPass(Matrix m){
		if(isRigged){
			ShadowAnimation.bind();
			upload();
		}
		else wavefront.bind();
	

		GL20.glUniformMatrix4fv(1, true, m.asBuffer());
		for(int i =0; i <meshes.length; i++)
			meshes[i].draw();	
	}
	
	public void ShadowPass(){
		if(isRigged){
			ShadowAnimation.bind();
			upload();
		}
	

		GL20.glUniformMatrix4fv(1, true, matrix.asBuffer());
		for(int i =0; i <meshes.length; i++)
			meshes[i].draw();	
	}
	
	public void tick() {
		for(Bone b: bones)
			b.updateMatrix();
		
	}
	
	
	public static class Mesh{

		private String name;
		private int glBuffer, glArrayObject;
		private int vertices =0;
		
		private float[] positions;
		private float[] normals;
		private float[] uvs;
		private int polyCount;
		private int[] polyData;
		
		private boolean isRigged =false;
		private Binding binding;
		
		public Mesh(float[] positions, float[] normals, float[] uvs, int polyCount, int[] polyData, Binding binding){
			this.positions =positions;
			this.normals =normals;
			this.uvs =uvs;
			this.polyCount =polyCount;
			this.polyData =polyData;	
			this.binding =binding;
			isRigged =(binding !=null);
			preRender();
			render();
		}
		
		
		public void preRender(){
			FloatBuffer buffer =BufferUtils.createFloatBuffer((isRigged ? (polyCount) *32 *3 :polyCount *8 *3));
			int readIndex =0;
			float[] floats =new float[4];
			for(int i =0; i <polyCount; i ++){
				int vertexCountForThisPolygon =polyData[readIndex++]; // Should be 3 (triangle)
				for(int vert =0; vert <vertexCountForThisPolygon; vert++){
					int posIndex =polyData[readIndex++]*3;
					int norIndex =polyData[readIndex++]*3;
					int uvIndex =polyData[readIndex++]*2;
					float x, y, z;
					
					x =positions[posIndex];
					y =positions[posIndex +1];
					z =positions[posIndex +2];
					binding.stupidMatrix.multiplyVector(x, y, z, 1, floats);
					buffer.put(floats[0]).put(floats[1]).put(floats[2]);
					
					x =normals[norIndex];
					y =normals[norIndex +1];
					z =normals[norIndex +2];
					buffer.put(x).put(y).put(z);
	
					x =uvs[uvIndex];
					y =uvs[uvIndex +1];
					buffer.put(x).put(1.0f-y);
					if(isRigged){
						int t =posIndex /3 * 8;
						buffer.put(binding.boneThings[t]).put(binding.boneThings[t +1]).put(binding.boneThings[t +2]).put(binding.boneThings[t +3]);
						buffer.put(binding.boneThings[t +4]).put(binding.boneThings[t +5]).put(binding.boneThings[t +6]).put(binding.boneThings[t +7]);
											
					}
					
					vertices++;
				}
			}
			buffer.flip();
			this.buffer =buffer;
		}
		
		private FloatBuffer buffer;
		public void render(){
			glBuffer =GL15.glGenBuffers();
			glArrayObject =GL30.glGenVertexArrays();
			
			GL30.glBindVertexArray(glArrayObject);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glBuffer);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
			
			int stride =(isRigged)? 64 :32;
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			GL20.glEnableVertexAttribArray(3);
			GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, stride, 0);
			GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, stride, 12);	//3 *4
			GL20.glVertexAttribPointer(3, 2, GL11.GL_FLOAT, false, stride, 24); //6 *4
			if(isRigged){
				GL20.glEnableVertexAttribArray(4);
				GL20.glEnableVertexAttribArray(5);
				GL20.glVertexAttribPointer(4, 4, GL11.GL_FLOAT, false, stride, 32); //10 *4		weight
				GL20.glVertexAttribPointer(5, 4, GL11.GL_FLOAT, false, stride, 48); //14 *4		indexes
			}
			GL30.glBindVertexArray(0);
		}
		
		public void draw(){
			GL30.glBindVertexArray(glArrayObject);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertices);
			GL30.glBindVertexArray(0);
		}
		
		@Override
		public String toString(){
			return name;
		}
	}

	public static class Binding{
		
		public Matrix stupidMatrix;
		public String[] names;
		public Matrix[] inverseMatrices;
		public float[] boneThings;
		public Bone[] bones;
		
		public Binding(Matrix m, String[] names, Matrix[] matrices, float[] boneBinding, Model skeleton){
			this.stupidMatrix =m;
			this.names =names;
			this.boneThings =boneBinding;
			this.bones =new Bone[matrices.length];
			this.inverseMatrices =new Matrix[matrices.length];
						
			for(int b =0; b <this.bones.length; b ++){
				initBone(names[b], b, matrices, skeleton);
			}
		}
		

		private void initBone(String name, int b, Matrix[] matrices, Model skeleton){
			for(int i =0; i <skeleton.bones.length; i ++){
				if(name.toUpperCase().equals(skeleton.bones[i].name.toUpperCase())){
					this.bones[b] =skeleton.bones[i];
					this.inverseMatrices[b] =matrices[i];
					return;
				}
			}
			
		}
		
		Matrix m =new Matrix();
		public void upload(int bone){
			for(int i =0; i <bones.length; i++)if(bones[i] !=null){
				m.empty();
				m.setMult(bones[i].matrix_world, inverseMatrices[i]);
				GL20.glUniformMatrix4fv(12 +bone +i, true, m.asBuffer()); 
			}
		}
		
		
	}
}
