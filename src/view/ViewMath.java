package view;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.shape.Cylinder;

import com.leapmotion.leap.Vector;

public class ViewMath {
	public static final int positionScaleFactor = 20;
	public static final int radiusScaleFactor = 35;
	public static final float webbingScaleFactor = 0.8f;
	
	public static Point3D vectorToPoint(Vector v) {
		return new Point3D( v.getX(), v.getY(), v.getZ() );
	}
	
	public static void setPositionByVector(Node node, Vector position) {
		node.setTranslateX(position.getX()/positionScaleFactor);
		node.setTranslateY((150 - position.getY())/positionScaleFactor); // compensate for device Y being 0+, screen Y being -0+
		node.setTranslateZ(-position.getZ()/positionScaleFactor);
	}
	
	public static void setRotationByVector(Node node, Vector direction) {
		Vector correctedDirection = new Vector(direction.getX(), direction.getY(), -direction.getZ());
		double angle = correctedDirection.angleTo(Vector.yAxis()) * 180/Math.PI;
		Point3D axis = vectorToPoint( correctedDirection.cross(Vector.yAxis()) );
		node.setRotate(angle);
		node.setRotationAxis(axis);
	}
	
	public static void setCylinder(Cylinder cylinder, Vector mid, Vector direction, float radius) {
		setPositionByVector(cylinder, mid);
		setRotationByVector(cylinder, direction);
		cylinder.setRadius(radius/radiusScaleFactor);
		cylinder.setHeight(direction.magnitude()/positionScaleFactor);
	}

	public static void setCylinder(Cylinder cylinder, Vector mid, Vector direction) {
		setPositionByVector(cylinder, mid);
		setRotationByVector(cylinder, direction);
		cylinder.setHeight(direction.magnitude()/positionScaleFactor);
	}
	
	public static void setCylinderByEndpoints(Cylinder cylinder, Vector start, Vector end) {
		Vector mid = start.plus(end).divide(2);
		Vector direction = start.minus(end);
		setPositionByVector(cylinder, mid);
		setRotationByVector(cylinder, direction);
		cylinder.setHeight(direction.magnitude()/positionScaleFactor);
	}

}
