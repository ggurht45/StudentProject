package view.anatomy;

import view.ViewMath;

import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Vector;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;

public class UIPalm extends Group {
	private static float webbingScaleFactor = ViewMath.webbingScaleFactor;
	
	private Sphere indexSphere;
	private Sphere pinkySphere;
	private Cylinder indexToThumb; // webbing
	private Cylinder indexToPinky; // wrist
	private Cylinder thumbToIndex; //
	private Cylinder thumbToPinky; //
	private UIWeb webbing;

	public UIPalm(Color color, boolean wireframe) {
		super();
		indexSphere = new Sphere();
		pinkySphere = new Sphere();
		indexToThumb = new Cylinder();
		indexToPinky = new Cylinder();
		thumbToIndex = new Cylinder();
		thumbToPinky = new Cylinder();
		webbing = new UIWeb(color);
		getChildren().addAll(indexSphere, pinkySphere, indexToThumb, indexToPinky, thumbToIndex, thumbToPinky);
		for (Node node : getChildren()) ((Shape3D) node).setMaterial(new PhongMaterial(color));
		if (wireframe) for (Node node : getChildren()) ((Shape3D) node).setDrawMode(DrawMode.LINE);
		if (!wireframe) getChildren().add(webbing);
	}

	void setLoc(Bone thumb, Bone index, Bone pinky) {
		
		ViewMath.setPositionByVector(indexSphere, index.prevJoint());
		ViewMath.setPositionByVector(pinkySphere, pinky.prevJoint());
		indexSphere.setRadius(index.width()/ViewMath.radiusScaleFactor);
		pinkySphere.setRadius(index.width()/ViewMath.radiusScaleFactor);
		
		Vector mid0 = index.nextJoint().plus(thumb.nextJoint()).times(0.5f);
		Vector direction0 = index.nextJoint().minus(thumb.nextJoint());
		ViewMath.setCylinder(indexToThumb, mid0, direction0, thumb.width()*webbingScaleFactor);
		
		Vector mid1 = index.prevJoint().plus(pinky.prevJoint()).times(0.5f);
		Vector direction1 = index.prevJoint().minus(pinky.prevJoint());
		ViewMath.setCylinder(indexToPinky, mid1, direction1, index.width());
		
		Vector mid2 = thumb.prevJoint().plus(index.prevJoint()).times(0.5f);
		Vector direction2 = thumb.prevJoint().minus(index.prevJoint());
		ViewMath.setCylinder(thumbToIndex, mid2, direction2, index.width());
		
		Vector mid3 = thumb.prevJoint().plus(pinky.prevJoint()).times(0.5f);
		Vector direction3 = thumb.prevJoint().minus(pinky.prevJoint());
		ViewMath.setCylinder(thumbToPinky, mid3, direction3, index.width());
		
		// thumb/index.direction() is normalised to 1, so we must multiply it by .length() to use the direction for the length
		webbing.setLoc(thumb.center(), thumb.direction().times(thumb.length()), index.center(), index.direction().times(index.length()), index.width()*webbingScaleFactor);
	}

	void setChildrenOpacity(double opacity) {
		indexSphere.setOpacity(opacity);
		pinkySphere.setOpacity(opacity);
		indexToThumb.setOpacity(opacity);
		indexToPinky.setOpacity(opacity);
		thumbToIndex.setOpacity(opacity);
		thumbToPinky.setOpacity(opacity);
		webbing.setChildrenOpacity(opacity);
	}

}
