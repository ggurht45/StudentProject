package view.anatomy;

import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Hand;

import javafx.scene.paint.Color;

public class UIHand_Full extends UIHand {
	
	private UIFinger[] fingers;
	private UIPalm palm;
	private static int numberOfFingers = 5;
	
	private static Finger.Type thumbType = Finger.Type.TYPE_THUMB;
	private static Finger.Type indexType = Finger.Type.TYPE_INDEX;
	private static Finger.Type pinkyType = Finger.Type.TYPE_PINKY;
	private static Bone.Type metacarpal = Bone.Type.TYPE_METACARPAL;
	private static Bone.Type proximal = Bone.Type.TYPE_PROXIMAL;
	
	public UIHand_Full(Color color, boolean wireframe) {
		super();
		fingers = new UIFinger[numberOfFingers];
		for (int i = 0; i<numberOfFingers; ++i) fingers[i] = new UIFinger(color, wireframe);
		palm = new UIPalm(color, wireframe);
		getChildren().addAll(fingers);
		getChildren().add(palm);
	}
	
	public void setLoc(Hand hand) {
		FingerList fingerList = hand.fingers();
		for (int i = 0; i<numberOfFingers; ++i) fingers[i].setLoc(fingerList.get(i));
		palm.setLoc(fingerList.fingerType(thumbType).get(0).bone(proximal),
					fingerList.fingerType(indexType).get(0).bone(metacarpal),
					fingerList.fingerType(pinkyType).get(0).bone(metacarpal));
	}
	
	public void setChildrenOpacity(double opacity) {
		for (UIFinger f : fingers) f.setChildrenOpacity(opacity);
		palm.setChildrenOpacity(opacity);
	}
	
}
