package view.anatomy;

import javafx.scene.Group;
import javafx.scene.Node;

// TODO currently DEFUNCT, DO NOT USE. may be useful in future

public abstract class UIAnatomy extends Group {
	
	void setChildrenOpacity(double opacity) {
		for (Node node : getChildren()) node.setOpacity(opacity);
	}

}
