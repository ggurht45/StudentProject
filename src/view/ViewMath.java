package view;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Matrix;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Cylinder;

import com.leapmotion.leap.Vector;
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
        Vector d = h.direction();
        System.out.println("direction (rounded u.vec): " + roundVector(d));
        System.out.println("d normalized: " + roundVector(d.normalized()));
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
        float pitch = d.pitch();
        float yaw = d.yaw();
        float roll1 = d.roll();
        Vector pn = h.palmNormal();
        float roll2 = pn.roll();
        System.out.println("palmNormal: " + roundVector(pn));
        System.out.println("pitch, yaw, roll in radians");
        System.out.println("pitch: " + pitch);
        System.out.println("yaw: " + yaw);
        System.out.println("roll(d): " + roll1);
        System.out.println("roll(pn): " + roll2);

        pitch = roundFloat((float) Math.toDegrees(pitch));//casting to Float different from casting to float. primitive vs object types
        yaw = roundFloat((float) Math.toDegrees(yaw));
        roll1 = roundFloat((float) Math.toDegrees(roll1));
        roll2 = roundFloat((float) Math.toDegrees(roll2));
        System.out.println("pitch, yaw, roll in degrees");
        System.out.println("pitch: " + pitch);
        System.out.println("yaw: " + yaw);
        System.out.println("roll(d): " + roll1);
        System.out.println("roll(pn): " + roll2);

        //weight by magnitude of projections into the respective planes
        float pitchWeighted = getWeightedPYR(d, "pitch", true, true);
        float yawWeighted = getWeightedPYR(d, "yaw", true, true);
        float rollWeighted = getWeightedPYR(d, "roll", true, true);
        float rollWeightedPN = getWeightedPYR(pn, "roll", true, true);//roll for palm normal
        System.out.println("pitchWeighted: " + pitchWeighted);
        System.out.println("yawWeighted: " + yawWeighted);
        System.out.println("rollWeighted (d): " + rollWeighted);
        System.out.println("rollWeighted (pn): " + rollWeightedPN);

        System.out.println("palmPosition: " + roundVector(h.palmPosition()));
        System.out.println("stabilizedPalmPosition: " + roundVector(h.stabilizedPalmPosition()));
        System.out.println("palmWidth: " + roundFloat(h.palmWidth()));
        System.out.println("wristPosition: " + roundVector(h.wristPosition()));
        Vector negZaxis = Vector.zAxis().opposite(); //seems to give -0s shouldnt be a problem. but its kinda weird.
        System.out.println("learning about pitch, roll yaw: vector =" + negZaxis + " \t pitch: " + negZaxis.pitch() + " \t roll: " + negZaxis.roll() + " \t yaw: " + negZaxis.yaw());
        System.out.println("-------------------------------------------- END");
    }


    public static float getWeightedPYR(Vector d, String type, boolean useDegrees, boolean round) {
        d = d.normalized();

        //anglePYR is angle for pitch yaw or roll. depending on type passed in
        float anglePYR = d.pitch();

        Vector proj = d; //initially set proj to just d passed in

        //really set the projection
        if (type.equalsIgnoreCase("pitch")) {
            proj = getProjection(d, "YZ");
        } else if (type.equalsIgnoreCase("yaw")) {
            proj = getProjection(d, "XZ");
        } else if (type.equalsIgnoreCase("roll")) {
            proj = getProjection(d, "XY");
        } else {
            System.out.println("\n \n Wrong angle type passed in !!! \n \n ");
        }

        //weight the angle
        float w = proj.magnitude();
        anglePYR = anglePYR * w;//weight to multiply pitch/yaw/roll by. cuz direction is unit vector

        if (useDegrees) {
            anglePYR = (float) Math.toDegrees(anglePYR);//return rounded, and converted to degrees
        }//else doesnt do anything. pitch is already in radians

        if (round) {
            return roundFloat(anglePYR);
        } else {
            return anglePYR;
        }
    }


    //planes for angles pitch -> yz;    yaw -> xz;      roll -> xy
    public static Vector getProjection(Vector d, String plane) {
        Vector v = new Vector(d.getX(), d.getY(), d.getZ());
        if (plane.equalsIgnoreCase("YZ")) {
            v.setX(0);
        } else if (plane.equalsIgnoreCase("XZ")) {
            v.setY(0);
        } else if (plane.equalsIgnoreCase("XY")) {
            v.setZ(0);
        } else {
            System.out.println("\n \n Error! wrong plane specified \n \n");
        }
        System.out.println("getProjection(pitch:yz; yaw:xz; roll:xy): " + plane + " " + v + " mag (weight): " + v.magnitude());
        return v;
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


    public static void printInfoManyHands() {
        String str1 = "targets/2015-05-05 08-17-01.hand"; //normal facing up(1)
        String str2 = "targets/2017-06-12 12-13-58.hand"; //should be very close to the 0th hand. normal facing up(2)
        String str3 = "targets/2017-06-12 12-18-33.hand"; //palm facing downwards
        String str4 = "targets/2017-06-12 12-21-01.hand"; //palm facing downwards and fingers pointing to right
        String str5 = "targets/2017-06-12 12-23-19.hand"; //right hand palm, upwards
        String str6 = "targets/2017-06-12 12-30-56.hand"; //palm facing down again
        List<String> msges = new ArrayList<String>(Arrays.asList("normal facing up(1)", "normal facing up(2)", "facing down", "facing down, fingers to right", "right hand palm upright", "facing down 2"));
        List<String> files = new ArrayList<String>(Arrays.asList(str1, str2, str3, str4, str5, str6));

        for (int i = 0; i < msges.size(); i++) {
            Hand h = null;
            try {
                h = SerializedTargetHand.readFromFile(files.get(i));
                printHandInfo(h, msges.get(i));
                if (i == 3) {
                    System.out.println("** special pitch calculations **");
                    Vector d = h.direction();
                    float p = d.pitch();
                    Vector projectionOntoYZplane = new Vector(0, d.getY(), d.getZ());
                    System.out.println("dir: " + d + " \t prjYZ: " + projectionOntoYZplane + " \n p(radians): " + p + " \t (deg): " + Math.toDegrees(p));
                    Vector negZAxis = Vector.zAxis().opposite();
                    float angBtw = negZAxis.angleTo(projectionOntoYZplane);
                    System.out.println("negZAxis: " + negZAxis + " \t angBtw(-z, proj): " + angBtw + " \t angBtw(deg): " + Math.toDegrees(angBtw));

                    //multiply by percentage of magnitude.
                    float fullMag = d.magnitude();
                    float projYZmag = projectionOntoYZplane.magnitude();
                    System.out.println("dMagnitude: " + fullMag + " \t projMag" + projYZmag);
                    float angBtwWeighted = angBtw * projYZmag;
                    float angBtwDegW = (float) Math.toDegrees(angBtw) * projYZmag;
                    System.out.println("angW (rad): " + angBtwWeighted + " \t angW (deg): " + Math.toDegrees(angBtwWeighted) + " \t convertDeg 1st: " + angBtwDegW);
                    System.out.println("*** End special *** ");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
    public static void matrixRotateNode(Node n, double alf, double bet, double gam){
        double A11=Math.cos(alf)*Math.cos(gam);
        double A12=Math.cos(bet)*Math.sin(alf)+Math.cos(alf)*Math.sin(bet)*Math.sin(gam);
        double A13=Math.sin(alf)*Math.sin(bet)-Math.cos(alf)*Math.cos(bet)*Math.sin(gam);
        double A21=-Math.cos(gam)*Math.sin(alf);
        double A22=Math.cos(alf)*Math.cos(bet)-Math.sin(alf)*Math.sin(bet)*Math.sin(gam);
        double A23=Math.cos(alf)*Math.sin(bet)+Math.cos(bet)*Math.sin(alf)*Math.sin(gam);
        double A31=Math.sin(gam);
        double A32=-Math.cos(gam)*Math.sin(bet);
        double A33=Math.cos(bet)*Math.cos(gam);

        double d = Math.acos((A11+A22+A33-1d)/2d);
        if(d!=0d){
            double den=2d*Math.sin(d);
            Point3D p= new Point3D((A32-A23)/den,(A13-A31)/den,(A21-A12)/den);
            n.setRotationAxis(p);
            n.setRotate(Math.toDegrees(d));
        }
    }
}
