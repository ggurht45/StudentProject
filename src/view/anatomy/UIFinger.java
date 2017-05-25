package view.anatomy;

import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Finger;

import javafx.scene.Group;
import javafx.scene.paint.Color;

public class UIFinger extends Group {
	private UIBone[] bones;
	private UIJoint[] joints;
	private static int numberOfBones = 4;	
	
	public UIFinger(Color color, boolean wireframe) {
		super();
		bones = new UIBone[numberOfBones];
		joints = new UIJoint[numberOfBones];
		for (int i = 0; i<numberOfBones; ++i) {bones[i] = new UIBone(color, wireframe); joints[i] = new UIJoint(color.brighter(), wireframe);}
		getChildren().addAll(bones);
		if (wireframe) getChildren().add(joints[numberOfBones-1]); 
		else getChildren().addAll(joints);
	}


	void setLoc(Finger f) {
		for (int i = 0; i<numberOfBones; ++i) {
			Bone bone = f.bone(Bone.Type.swigToEnum(i));
			bones[i].setLoc(bone.center(), bone.direction(), bone.width(), bone.length());
			joints[i].setLoc(bone.nextJoint(), bone.width());
		}		
	}


	void setChildrenOpacity(double opacity) {
		for (UIBone b : bones) b.setChildrenOpacity(opacity);
		for (UIJoint j : joints) j.setChildrenOpacity(opacity);
	}

}
