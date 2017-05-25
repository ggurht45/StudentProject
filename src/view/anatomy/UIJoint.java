package view.anatomy;

import view.ViewMath;

import com.leapmotion.leap.Vector;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;

public class UIJoint extends Group {
	private Sphere sphere;
	
	public UIJoint(Color color, boolean wireframe) {
		super();
		sphere = new Sphere();
		sphere.setMaterial(new PhongMaterial(color));
		if (wireframe) sphere.setDrawMode(DrawMode.LINE);
		getChildren().add(sphere);
	}

	void setLoc(Vector position, double radius) {
		ViewMath.setPositionByVector(sphere, position);
		sphere.setRadius(radius/ViewMath.radiusScaleFactor);
	}

	void setChildrenOpacity(double opacity) {
		sphere.setOpacity(opacity);
	}

}
