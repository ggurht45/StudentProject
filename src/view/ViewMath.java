package view;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Matrix;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Cylinder;

import com.leapmotion.leap.Vector;
import javafx.scene.transform.Rotate;
import model.SerializedTargetHand;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewMath {
    public static final int positionScaleFactor = 20;
    public static final int radiusScaleFactor = 35;
    public static final float webbingScaleFactor = 0.8f;


    //helper method to convert vector to 3dPoint
    public static Point3D vectorToPoint(Vector v) {
        return new Point3D(v.getX(), v.getY(), v.getZ());
    }


    //changes the node that was passed in. java is pass by value, but what gets passed in is remote control to the objects.
    //position is some point in space(the center point of the bone this uiNode object is positioned to
    //, this method sets the node to be at the position also, but scaled back by a factor so as to fit on the screen i guess.
    public static void setPositionByVector(Node node, Vector centerPositionOfBone) {
        node.setTranslateX(centerPositionOfBone.getX() / positionScaleFactor);
        node.setTranslateY((150 - centerPositionOfBone.getY()) / positionScaleFactor); // compensate for device Y being 0+, screen Y being -0+
        node.setTranslateZ(-centerPositionOfBone.getZ() / positionScaleFactor);
    }


    //This method rotates a given JavaFx node to point in the direction passed in
    public static void setRotationByVector(Node node, Vector direction) {
        //Correct the direction to correspond to JavaFx Coordinate system
        Vector correctedDirection = new Vector(direction.getX(), direction.getY(), -direction.getZ());

        //Find the angle of the direction to the y-axis; in degrees
        double angle = correctedDirection.angleTo(Vector.yAxis()) * 180 / Math.PI;

        //Find the axis of rotation by taking the cross product of the corrected direction with the y-axix
        Point3D axis = vectorToPoint(correctedDirection.cross(Vector.yAxis()));

        //Set the axis and angle of rotation on the Node object
        node.setRotate(angle);
        node.setRotationAxis(axis);
    }

    public static void setRotationByVector2(Node node, Vector direction, Vector orginalAxis) {
        Vector correctedDirection = new Vector(direction.getX(), -direction.getY(), -direction.getZ());

        double angle = correctedDirection.angleTo(orginalAxis) * 180 / Math.PI;    //angle in degrees.

        Point3D axis = vectorToPoint(orginalAxis.cross(correctedDirection));
//        Point3D axis = vectorToPoint(correctedDirection.cross(orginalAxis));
        node.setRotate(angle);
        node.setRotationAxis(axis);
    }

    //this method is used in the UIPalm class... only thing is that this sets the radius also.
    public static void setCylinder(Cylinder cylinder, Vector mid, Vector direction, float radius) {
        setPositionByVector(cylinder, mid);
        setRotationByVector(cylinder, direction);
        cylinder.setRadius(radius / radiusScaleFactor);
        cylinder.setHeight(direction.magnitude() / positionScaleFactor);
    }

    //even though this method doesnt return the changed node, since it is passed in the references to the real node objects, it does change
    //those objects through the references that were passed in. very javaish. not functional programming ideals of using pure functions.
    public static void setCylinder(Cylinder cylinder, Vector middlePoint, Vector directionWithMagnitude) {
        setPositionByVector(cylinder, middlePoint);
        setRotationByVector(cylinder, directionWithMagnitude);
        double lengthOfBone = directionWithMagnitude.magnitude();
        cylinder.setHeight(lengthOfBone / positionScaleFactor);
    }


    //even though this method doesnt return the changed node, since it is passed in the references to the real node objects, it does change
    //those objects through the references that were passed in. very javaish. not functional programming ideals of using pure functions.
    public static void setCylinder2(Vector middlePoint, Vector directionWithMagnitude) {
        System.out.println("hello from c2");
    }

    //work with whole group object rather than just one cylinder
    public static void setGroup(Group group, Vector middlePoint, Vector directionWithMagnitude) {
        setPositionByVector(group, middlePoint);
        setRotationByVector(group, directionWithMagnitude);
        double lengthOfBone = directionWithMagnitude.magnitude();
        group.setScaleY(lengthOfBone / positionScaleFactor);
    }

    //for setting any generic node, be it cylinder or box.
    public static void setGenericNode(Node node, Vector middlePoint, Vector directionWithMagnitude) {
        setPositionByVector(node, middlePoint);
        setRotationByVector(node, directionWithMagnitude);
        double lengthOfBone = directionWithMagnitude.magnitude();
        node.setScaleY(lengthOfBone / positionScaleFactor);
//        node.getTransforms().add(new Rotate(90, new Point3D(0,1,0))); //not really sure i understand this. but so happy it looks good now.
    }

    public static void straightenGroup(Group group) {
//		setPositionByVector(group, middlePoint);
//		setRotationByVector(group, directionWithMagnitude);
//		double lengthOfBone = directionWithMagnitude.magnitude();
//		group.setScaleY(lengthOfBone/positionScaleFactor);
    }


    // also very similar to the above methods. just finds the mid and direction by itself.
    //i should really refractor this code.
    public static void setCylinderByEndpoints(Cylinder cylinder, Vector start, Vector end) {
        Vector mid = start.plus(end).divide(2);
        Vector direction = start.minus(end);
        setPositionByVector(cylinder, mid);
        setRotationByVector(cylinder, direction);
        cylinder.setHeight(direction.magnitude() / positionScaleFactor);
    }

    public static Point3D getRotationAxis(double alf, double bet, double gam) {
        double A11 = Math.cos(alf) * Math.cos(gam);
        double A12 = Math.cos(bet) * Math.sin(alf) + Math.cos(alf) * Math.sin(bet) * Math.sin(gam);
        double A13 = Math.sin(alf) * Math.sin(bet) - Math.cos(alf) * Math.cos(bet) * Math.sin(gam);
        double A21 = -Math.cos(gam) * Math.sin(alf);
        double A22 = Math.cos(alf) * Math.cos(bet) - Math.sin(alf) * Math.sin(bet) * Math.sin(gam);
        double A23 = Math.cos(alf) * Math.sin(bet) + Math.cos(bet) * Math.sin(alf) * Math.sin(gam);
        double A31 = Math.sin(gam);
        double A32 = -Math.cos(gam) * Math.sin(bet);
        double A33 = Math.cos(bet) * Math.cos(gam);

        double d = Math.acos((A11 + A22 + A33 - 1d) / 2d);
        if (d != 0d) {
            double den = 2d * Math.sin(d);
            Point3D p = new Point3D((A32 - A23) / den, (A13 - A31) / den, (A21 - A12) / den);
            return p;
        } else {
            System.out.println("\n \n Error!! angle is zero?? \n \n");
            return new Point3D(0, 0, 0);
        }
    }

    public static double getRotationAngle(double alf, double bet, double gam) {
        double A11 = Math.cos(alf) * Math.cos(gam);
        double A12 = Math.cos(bet) * Math.sin(alf) + Math.cos(alf) * Math.sin(bet) * Math.sin(gam);
        double A13 = Math.sin(alf) * Math.sin(bet) - Math.cos(alf) * Math.cos(bet) * Math.sin(gam);
        double A21 = -Math.cos(gam) * Math.sin(alf);
        double A22 = Math.cos(alf) * Math.cos(bet) - Math.sin(alf) * Math.sin(bet) * Math.sin(gam);
        double A23 = Math.cos(alf) * Math.sin(bet) + Math.cos(bet) * Math.sin(alf) * Math.sin(gam);
        double A31 = Math.sin(gam);
        double A32 = -Math.cos(gam) * Math.sin(bet);
        double A33 = Math.cos(bet) * Math.cos(gam);

        double d = Math.acos((A11 + A22 + A33 - 1d) / 2d);
        return Math.toDegrees(d);
    }


    //note!! expects angle in radians!!
    //stackoverflow: alf is roll, bet is pitch and gam is yaw.
    public static void matrixRotateNode(Node n, double alf, double bet, double gam) {
        double A11 = Math.cos(alf) * Math.cos(gam);
        double A12 = Math.cos(bet) * Math.sin(alf) + Math.cos(alf) * Math.sin(bet) * Math.sin(gam);
        double A13 = Math.sin(alf) * Math.sin(bet) - Math.cos(alf) * Math.cos(bet) * Math.sin(gam);
        double A21 = -Math.cos(gam) * Math.sin(alf);
        double A22 = Math.cos(alf) * Math.cos(bet) - Math.sin(alf) * Math.sin(bet) * Math.sin(gam);
        double A23 = Math.cos(alf) * Math.sin(bet) + Math.cos(bet) * Math.sin(alf) * Math.sin(gam);
        double A31 = Math.sin(gam);
        double A32 = -Math.cos(gam) * Math.sin(bet);
        double A33 = Math.cos(bet) * Math.cos(gam);

        double d = Math.acos((A11 + A22 + A33 - 1d) / 2d);
        if (d != 0d) {
            double den = 2d * Math.sin(d);
            Point3D p = new Point3D((A32 - A23) / den, (A13 - A31) / den, (A21 - A12) / den);
            n.setRotationAxis(p);
            n.setRotate(Math.toDegrees(d));
        }
    }


}
