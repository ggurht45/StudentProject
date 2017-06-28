package view;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Matrix;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Cylinder;

import com.leapmotion.leap.Vector;

import java.math.BigDecimal;

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


    //note: by convention, counter-clockwise rotation is positive. 10 angle rotation means rotate in counterclockwise direction
    //when looking in the negative direction of the axis. (- z for example)
    public static void setRotationByVector(Node node, Vector direction) {
        //the "corrected" direction switches the zaxis to negative. because fingers point into the screen but z axis extends outward from the screen
        Vector correctedDirection = new Vector(direction.getX(), direction.getY(), -direction.getZ());

        //angle is the angle to the of the 'corrected' direction to the y-axis
        //not sure why its then multiplying it by 57ish.. oh! it converting from radians to degrees.
        //the angleTo method returns angle in radians. to convert it radians we must multiply that by 180 and then take
        //the result and divide it by Pi. mks
        double angle = correctedDirection.angleTo(Vector.yAxis()) * 180 / Math.PI;    //angle in degrees.

        //just takes a vector and converts it to point3d. the vector it converts is the cross the cross product to the y-axis
        //so this code.. kinda already does what i want. or does it? then how come i was able to show hand pointed to the right?
        //it makes sense why cross product should be the axis of rotation. oh, and the counter-clockwise rotation
        //gets taken care of by the fact that we have -z direction. try using the right hand rule to visualize it
        //axis can be located anywhere in space. but when an object rotates, this axis is can be pictured to be going through
        //the center of the object
        //it chooses the y-axis cuz that is the original "direction" of the cylinder. top/down. we are going from that axis to another axis
        //the "corrected" direction axis. getting the cross product. a cross b is NOT the same as  (b x a). its the opposite direction.
        Point3D axis = vectorToPoint(correctedDirection.cross(Vector.yAxis()));
        node.setRotate(angle);
        node.setRotationAxis(axis);


        //get a second "hand" set its direction approximately. also set h1. set both hands.
        //change into the second hand's direction
    }

    public static void setRotationByVector2(Node node, Vector direction, Vector orginalAxis) {
        Vector correctedDirection = new Vector(direction.getX(), direction.getY(), -direction.getZ());

        double angle = correctedDirection.angleTo(orginalAxis) * 180 / Math.PI;    //angle in degrees.

        Point3D axis = vectorToPoint(correctedDirection.cross(orginalAxis));
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

    public static void setGroup2(Group group, Vector middlePoint, Vector directionWithMagnitude, Vector axis) {
        setPositionByVector(group, middlePoint);
        setRotationByVector2(group, directionWithMagnitude, axis);
        double lengthOfBone = directionWithMagnitude.magnitude();
        group.setScaleY(lengthOfBone / positionScaleFactor);
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


    //helper static methods:
    public static void printHandInfo(Hand h, String msg) {
        System.out.println("--------------------------------------------" + msg);
        System.out.println("direction (rounded u.vec): " + roundVector(h.direction()));
//        System.out.println("opposite direction: " + roundVector(h.direction().opposite()));
        Matrix basis = h.basis();
        Vector xBasis = roundVector(basis.getXBasis());
        Vector yBasis = roundVector(basis.getYBasis());
        Vector zBasis = roundVector(basis.getZBasis());
        Vector origin = roundVector(basis.getOrigin());
        System.out.println("basis matrix: ");// + basis);
        System.out.println("(x-Basis): " + xBasis);
        System.out.println("(y-Basis): " + yBasis);
        System.out.println("(z-Basis): " + zBasis);
        System.out.println("(origin): " + origin);
        float pitch = h.direction().pitch();
        float yaw = h.direction().yaw();
        float roll = h.palmNormal().roll();
        pitch = roundFloat((float)Math.toDegrees(pitch));//casting to Float different from casting to float. primitive vs object types
        yaw = roundFloat((float) Math.toDegrees(yaw));
        roll = roundFloat((float) Math.toDegrees(roll));
        System.out.println("palmNormal: " + roundVector(h.palmNormal()));
        System.out.println("pitch: " + pitch);
        System.out.println("yaw: " + yaw);
        System.out.println("roll: " + roll);
        System.out.println("palmPosition: " + roundVector(h.palmPosition()));
        System.out.println("stabilizedPalmPosition: " + roundVector(h.stabilizedPalmPosition()));
        System.out.println("palmWidth: " + roundFloat(h.palmWidth()));
        System.out.println("wristPosition: " + roundVector(h.wristPosition()));
        System.out.println("-------------------------------------------- END");


        //notes about goal
        //i want no roll. high pitch. no yaw.
        //so. i need to be able to correct* for having roll. having yaw(fingers pointing to right),
        //having wrong pitch (fingers facing downwards or something)
    }

    public static Vector roundVector(Vector u) {
        float x = roundFloat(u.getX());
        float y = roundFloat(u.getY());
        float z = roundFloat(u.getZ());
        return new Vector(x, y, z);
    }

    public static float roundFloat(float d) {
        return roundFloat(d, 2);
    }

    public static float roundFloat(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

}
