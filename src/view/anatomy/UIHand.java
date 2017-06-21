package view.anatomy;

import javafx.scene.Group;

import com.leapmotion.leap.Hand;

public abstract class UIHand extends Group {

	public abstract void setLoc(Hand hand);

	public abstract void setDirectionTo(Hand hand);
	
	public abstract void setChildrenOpacity(double opacity);
	
	
}
