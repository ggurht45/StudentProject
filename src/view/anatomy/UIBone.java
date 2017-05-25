package view.anatomy;

import view.ViewMath;

import com.leapmotion.leap.Vector;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;

public class UIBone extends Group {
	private Cylinder cylinder;
	private static int positionScaleFactor = ViewMath.positionScaleFactor;
	private static int radiusScaleFactor = ViewMath.radiusScaleFactor;
	
	public UIBone(Color color, boolean wireframe) {
		super();
		cylinder = new Cylinder();
		cylinder.setMaterial(new PhongMaterial(color));
		if (wireframe) cylinder.setDrawMode(DrawMode.LINE);
		getChildren().add(cylinder);
	}

	void setLoc(Vector position, Vector direction, double radius, double length) {
		ViewMath.setPositionByVector(cylinder, position);
		ViewMath.setRotationByVector(cylinder, direction);
		cylinder.setRadius(radius/radiusScaleFactor);
		cylinder.setHeight(length/positionScaleFactor);
		System.out.println(radius);

	}

	void setChildrenOpacity(double opacity) {
		cylinder.setOpacity(opacity);
	}

}
