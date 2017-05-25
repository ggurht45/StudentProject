package model;
/*
 * array[i][0(1, 2 respectively] is the x (y, z respectively) coordinate of the joint closer to the palm of the ((i%5)+1)th bone of the ((i/5)+1)th finger
 * array[j][3] is the width of the ((j%5)+1) bone of the ((j/5)+1) the finger; (if (j%5==4) it's the width of the (j%5)th finger as well.
 * array[26][0] is the score of the hand
 */


import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;


public class NewHand{
	
	private Hand hand;
	private double score;
	Float[][] array = new Float[26][4];
	
	public NewHand(Hand hand1, Double s){
		hand=hand1;
		score = s;
		toarray();
	}
	
	public NewHand(Frame frame, Double s){
		hand=frame.hand(0);
		score=s;
		toarray();
	}
	
	public NewHand(FrameWithScore framewithscore){
		hand=framewithscore.getFrame().hand(0);	
		score=framewithscore.getScore();
		toarray();
	}
	


	private void toarray(){
		int i = 0;
		for (Finger finger : hand.fingers()) {
			for(Bone.Type boneType : Bone.Type.values()) {
				Bone bone = finger.bone(boneType);
				Vector vector = bone.prevJoint();
					for(int j=0;j<3;j++){
						array[i][j]=vector.get(j);
					}
				array[i][3]=bone.width();
				i++;
				if (i%5==4){
				//if bone is the last bone on the finger add position of the fingertip	
					vector = bone.nextJoint();
							for(int j=0;j<3;j++){
								array[i][j]=vector.get(j);
							}
					array[i][3]=bone.width();	//same as array[i-1][3]
				i++;	
				}
			}
		}
		float s = (float) score;
		array[25][0]=s;
	}	
	
	public Float[][] getarray(){
		return array;
	}
	
}
