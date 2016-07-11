package betapanda.util.animation;

import awesome.math.Matrix;

public class FrameGroup{
	
	public String groupName ="";
	public int[] frames;
	private Track track;
	
	public FrameGroup(String line, String line2, Track track){
		String data[] =line.split(" ");
		this.groupName =data[1];
		this.frames =new int[Integer.parseInt(data[2])];
		this.track =track;
		String data2[] =line2.split(" ");
		for(int i =0; i <frames.length; i++)
			frames[i] =Integer.parseInt(data2[i]);
	}

	public int[] getKeyframes(){
		return frames;
	}
	
	Matrix fin =new Matrix();
	public void tick(Model s, float frame){
		int keyAA =(int) Math.floor(frame *frames.length) %frames.length;
		int keyBB =(int) Math.ceil(frame *frames.length) %frames.length;
//		int keyA =frames[keyAA] %track.getKeyFrames()[keyAA].matrices.length;		<--this is safer
//		int keyB =frames[keyBB] %track.getKeyFrames()[keyAA].matrices.length;
		int keyA =frames[keyAA];
		int keyB =frames[keyBB];
		float a =((frame *frames.length) %frames.length) -keyAA;
		float b =1f -a;
		for(BoneKeyframes key: track.getKeyFrames()) if(key!=null){
			Matrix m1 =key.getMatrix(keyA);
			Matrix m2 =key.getMatrix(keyB);
			if(m1==null && m2==null) continue;
			if(m1==null) m1 = m2;
			if(m2==null) m2 = m1;
			for(int i =0; i <16; i ++){
				fin.m[i] =m1.m[i] *b +m2.m[i] *a;
			}	
			
			if(key.targetBone!=null)key.targetBone.matrix_local.set(fin);
		}
		
		s.tick();
	}
	
	/*
	private Matrix[] getKeyframes(float frame){
		frame =Math.abs(frame);
		int keyAA =(int) Math.floor(frame *frames.length) %frames.length;
		int keyBB =(int) Math.ceil(frame *frames.length) %frames.length;
		int keyA =frames[keyAA] %track.getKeyFrames().length;
		int keyB =frames[keyBB] %track.getKeyFrames().length;
		float a =((frame *frames.length) %frames.length) -keyAA;
		float b =1f -a;
		Matrix[] m =new Matrix[track.getKeyFrames().length];
		for(int key =0; key <track.getKeyFrames().length; key++)if(track.getKeyFrames()[frames[keyAA]] !=null){
			m[key] =new Matrix();
			Matrix m1 =track.getKeyFrames()[frames[keyAA]].getMatrix(keyA);
			Matrix m2 =track.getKeyFrames()[frames[keyAA]].getMatrix(keyB);
			for(int i =0; i <16; i ++){
				m[key].m[i] =m1.m[i] *b +m2.m[i] *a;
			}	
		}
		return m;
	}
	*/
}