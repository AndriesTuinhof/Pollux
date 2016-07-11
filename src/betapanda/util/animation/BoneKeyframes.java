package betapanda.util.animation;

import awesome.math.Matrix;

/**
 * Contains all the keyframes/matrices for a single bone.
 *
 */
public class BoneKeyframes {

	public String targetName ="";	
	public Bone targetBone;
	public Matrix[] matrices;
	
	public BoneKeyframes(String bone, Bone targetBone, Matrix[] m){
		targetName =bone;
		this.targetBone =targetBone;
		matrices =m;
	}
	
	public BoneKeyframes(){
		
	}
	
	public Matrix getMatrix(int i){
		if(i<0) return null;
		if(i>=matrices.length) return null;
		return matrices[i];
	}
	
	public String name()
	{
		return targetName;
	}
}
