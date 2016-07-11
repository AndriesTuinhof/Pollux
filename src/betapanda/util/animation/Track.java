package betapanda.util.animation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import awesome.math.Matrix;

public class Track {

	private BoneKeyframes[] keyframes;
	private Model model;
	private int animations =1;
	private ArrayList<FrameGroup> groups =new ArrayList<>();
	
	public Track(InputStream is, Model skeleton){
		try{
			this.model =skeleton;
			 BufferedReader br =new BufferedReader(new InputStreamReader(is));
			 
			 String line ="";
			 while((line =br.readLine()) !=null){
				 if(line.startsWith("GROUPS")){
					 int groupsTotal =Integer.parseInt(line.trim().split(" ")[1]);
					 for(int i =0; i <groupsTotal; i++){
						 FrameGroup group =new FrameGroup(br.readLine().trim(), br.readLine().trim(), this);
						 groups.add(group);
					 }
				 }
				 if(line.startsWith("ANIMATIONS")){
					 int keyframesCount =Integer.parseInt(line.split(" ")[1]);
					 keyframes =new BoneKeyframes[keyframesCount];
					 
					 for(int i =0; i <keyframes.length; i ++){
						 keyframes[i] =parseKeyframe(br.readLine().trim(), br.readLine().trim()); 
					 }
					 animations =keyframes[0].matrices.length;
				 }
				 
			 }
			}
			catch(Exception e){
				e.printStackTrace();
			}
	}
	
	
	public BoneKeyframes[] getKeyFrames(){
		return keyframes;
	}
	

	Matrix fin =new Matrix();
	public void tick(Model s, float frame){
		int keyA =(int) Math.floor(frame *animations) %animations;
		int keyB =(int) Math.ceil(frame *animations) %animations;
		float a =((frame *animations) %animations) -keyA;
		float b =1f -a;
		for(BoneKeyframes key: keyframes) if(key !=null){
			Matrix m1 =key.getMatrix(keyA);
			Matrix m2 =key.getMatrix(keyB);
			for(int i =0; i <16; i ++){
				fin.m[i] =m1.m[i] *b +m2.m[i] *a;
			}	
			key.targetBone.matrix_local.set(fin);
		}
		
		s.tick();
	}
	
	public void tick(Model s, int group, float frame){
		if(group >groups.size()){
			System.out.println("[Track] This group doesn't exist.");
			return;
		}
		groups.get(group).tick(model, frame);
	}
	
	private BoneKeyframes parseKeyframe(String name, String matrices){
		String[] data =matrices.split(" ");
		Matrix[] Bone_matrices =new Matrix[data.length /16];
		for(int m =0; m <Bone_matrices.length; m++){
			Matrix matrix =new Matrix();
			for(int i =0; i <16; i ++){
				matrix.m[i] =Float.parseFloat(data[m*16 +i]);
			}
			Bone_matrices[m] =matrix;
		}
		Bone b =null;
		for(int i =0; i <model.bones.length; i++)if(model.bones[i] !=null){
			if(model.bones[i].name.equals(name))
				b =model.bones[i];
		}
		BoneKeyframes key =new BoneKeyframes(name, b, Bone_matrices);
		return key;
	}

	/*
	private int animation(){
		return animations;
	}
	
	private Matrix[] getKeyframes(float f){
		int ff =(int) Math.floor(f);
		int fc =(int) Math.ceil(f);
		float a =f -ff;
		float b =fc -f;
		Matrix m[] =new Matrix[keyframes.length];
		for(int key =0; key <keyframes.length; key++)if(keyframes[key] !=null){
			m[key] =new Matrix();
			Matrix m1 =keyframes[key].getMatrix(ff);
			Matrix m2 =keyframes[key].getMatrix(fc);
			for(int i =0; i <16; i ++){
				m[key].m[i] =m1.m[i] *a +m2.m[i] *b;
			}			
		}
		return m;
	}
	
	private void tick(Matrix[] m1, Matrix[] m2, float f, Model s){	
		f =Math.min(f, 1f);
		float b =1f -f;
		for(int key =0; key <getKeyFrames().length; key++) if(getKeyFrames()[key] !=null){
			for(int i =0; i <16; i ++){
				fin.m[i] =m1[key].m[i] *b +m2[key].m[i] *f;
			}	
			
			getKeyFrames()[key].targetBone.matrix_local.set(fin);
		}
		
		s.tick();
		
	}
	
	private void tick(int group1, float f1, int group2, float f2, float f, Model s){	
		tick(groups.get(group1).getKeyframes(f1), groups.get(group2).getKeyframes(f2), f, s);
		
	}
	
	private int getGroup(String name){
		for(int i =0; i <groups.size(); i++)if(groups.get(i) !=null)if(groups.get(i).groupName.toUpperCase().equals(name.toUpperCase()))
			return i;
		return -1;
	}
	
	private FrameGroup getGroup(int i){
		return groups.get(i);
	}
	*/
}