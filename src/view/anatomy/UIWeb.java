package view.anatomy;

import view.ViewMath;

import com.leapmotion.leap.Vector;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

public class UIWeb extends Group {
	private Cylinder[] cylinders;
//	private Sphere[] spheres;
	private static int density = 20;
	private static float inverseDensity = 1.0f/density;
	
	public UIWeb(Color color) {
		super();
		cylinders = new Cylinder[density];
		for (int i=0; i<density; ++i) {cylinders[i] = new Cylinder(); cylinders[i].setMaterial(new PhongMaterial(color));}
//		spheres = new Sphere[density];
//		for (int i=0; i<density; ++i) {spheres[i] = new Sphere(); spheres[i].setMaterial(new PhongMaterial(color));}
		getChildren().addAll(cylinders);
//		getChildren().addAll(spheres);
	}
	
	void setLoc(Vector fromMid, Vector fromDir, Vector toMid, Vector toDir, float radius) {
		for (int i = 0; i<density; ++i) {
			Vector mid = fromMid.times(i+1).plus(toMid.times(density-i-1)).times(inverseDensity);
			Vector direction = fromDir.times(i+1).plus(toDir.times(density-i-1)).times(inverseDensity);
			ViewMath.setCylinder(cylinders[i], mid, direction, radius);
//			ViewMath.setPositionByVector(spheres[i], mid.minus(direction.times(0.5f)));
//			spheres[i].setRadius(radius/ViewMath.radiusScaleFactor);
		}
	}
	void setChildrenOpacity(double opacity) {
		for (Cylinder c : cylinders) c.setOpacity(opacity);
	}
}
