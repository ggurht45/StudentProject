package view.anatomy;

import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;
import view.DebugHelper;
import view.ViewMath;

import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;

public class UIHand_Simple extends UIHand {

    private static float fingerRadius = 14f;

    private Cylinder[][] fingerBones;
    private Sphere[][] fingerJoints;
    private Cylinder[] knuckleSpans;
    private Cylinder palmWrist;
    private Cylinder palmPinky;
    private Sphere pinkyJoint;

    public UIHand_Simple(Color color, boolean wireframe) {
        super();

        PhongMaterial dark = new PhongMaterial(color);
        PhongMaterial light = new PhongMaterial(color.brighter());

        fingerBones = new Cylinder[5][];
        for (int i = 0; i < 5; ++i) {
            fingerBones[i] = new Cylinder[3];
            for (int j = 0; j < 3; ++j) {
                fingerBones[i][j] = new Cylinder();
                fingerBones[i][j].setMaterial(dark);
                fingerBones[i][j].setRadius(fingerRadius / ViewMath.radiusScaleFactor);
                if (wireframe) fingerBones[i][j].setDrawMode(DrawMode.LINE);
            }
            getChildren().addAll(fingerBones[i]);
        }

        fingerJoints = new Sphere[5][];
        for (int i = 0; i < 5; ++i) {
            fingerJoints[i] = new Sphere[4];
            for (int j = 0; j < 4; ++j) {
                fingerJoints[i][j] = new Sphere();
                fingerJoints[i][j].setMaterial(light);
                fingerJoints[i][j].setRadius(fingerRadius / ViewMath.radiusScaleFactor);
                if (wireframe) fingerJoints[i][j].setDrawMode(DrawMode.LINE);
            }
            getChildren().addAll(fingerJoints[i]);
        }

        knuckleSpans = new Cylinder[4];
        for (int i = 0; i < 4; ++i) {
            knuckleSpans[i] = new Cylinder();
            knuckleSpans[i].setMaterial(dark);
            knuckleSpans[i].setRadius(fingerRadius / ViewMath.radiusScaleFactor);
            if (wireframe) knuckleSpans[i].setDrawMode(DrawMode.LINE);
        }
        getChildren().addAll(knuckleSpans);

        palmWrist = new Cylinder();
        palmWrist.setMaterial(dark);
        palmWrist.setRadius(fingerRadius / ViewMath.radiusScaleFactor);
        if (wireframe) palmWrist.setDrawMode(DrawMode.LINE);

        palmPinky = new Cylinder();
        palmPinky.setMaterial(dark);
        palmPinky.setRadius(fingerRadius / ViewMath.radiusScaleFactor);
        if (wireframe) palmPinky.setDrawMode(DrawMode.LINE);

        pinkyJoint = new Sphere();
        pinkyJoint.setMaterial(light);
        pinkyJoint.setRadius(fingerRadius / ViewMath.radiusScaleFactor);
        if (wireframe) pinkyJoint.setDrawMode(DrawMode.LINE);

        getChildren().addAll(palmWrist, palmPinky, pinkyJoint);

    }


    //this is the method that gets called from LeapUI, cuz the program uses Simple Hand rather than Full hand.
    //takes a hand object and sets some fingers and other stuff? yes.
    //name is very confusing. i guess it can mean set locations of different parts of the hand.
    @Override
    public void setLoc(Hand h) {
        Vector[] knuckles = new Vector[5];
        Vector pinkyBase = new Vector();

        //go through five fingers
        for (int i = 0; i < 5; ++i) {
            Finger finger = h.fingers().fingerType(Finger.Type.swigToEnum(i)).frontmost();
            //and each bone on the fingers
            for (int j = 0; j < 3; ++j) {
                Bone bone = finger.bone(Bone.Type.swigToEnum(j + 1));
                //this will take the "fingerBones[i][j]" item stored and update it with the data passed in
                // via the "bone" variable. bone comes from hand which was passed into this function.
                Cylinder uiBone = fingerBones[i][j];
                //updates uiBone based on bone passed in. even though this method doesn't seem to return anything, it does change the uiBone
                //bone.center() = the midpoint of the bone. bone.direction = normalized direction from base to tip of bone. thats why we have to
                //multiply it by the length of the bone.
                ViewMath.setCylinder(uiBone, bone.center(), bone.direction().times(bone.length()));

                Sphere uiJoint = fingerJoints[i][j + 1];
                Vector joint = bone.nextJoint();
                //probably updates uiJoint
                ViewMath.setPositionByVector(uiJoint, joint);

                if (j == 0) {
                    uiJoint = fingerJoints[i][0];
                    joint = bone.prevJoint();
                    ViewMath.setPositionByVector(uiJoint, joint);
                    knuckles[i] = joint;
                }

                if (finger.type() == Finger.Type.TYPE_PINKY) {
                    bone = finger.bone(Bone.Type.TYPE_METACARPAL);
                    ViewMath.setCylinder(palmPinky, bone.center(), bone.direction().times(bone.length()));
                    pinkyBase = bone.prevJoint();
                }
            }
        }

        //do something for the 4 knuckles ? why not five?
        for (int i = 0; i < 4; ++i) {
            ViewMath.setCylinderByEndpoints(knuckleSpans[i], knuckles[i], knuckles[i + 1]);
        }
        ViewMath.setCylinderByEndpoints(palmWrist, knuckles[0], pinkyBase);
        ViewMath.setPositionByVector(pinkyJoint, pinkyBase);

        tryAgain(h);
    }


    //the yaw that happens on the plane of xz.
    private float getfirstYaw(Hand h) {
        float angleAmount = (float) Math.toDegrees(h.direction().angleTo(Vector.forward()));
        if (h.direction().getX() > 0.0f) {
            return (-1.0f * angleAmount); //returning a -negative angle to "undo" the positive yaw noticed in hand
        } else {
            return angleAmount; //return a positive angle to "undo" a negative yaw noticed in the hand
        }
    }

    private float getPitch(Hand h) {
        float angleAmount = (float) Math.toDegrees(h.direction().angleTo(Vector.yAxis()));
        if (h.direction().getZ() > 0.0f) {
            return (-1.0f * angleAmount); //returning a -negative angle to "undo" the positive pitch noticed in hand
        } else {
            //99 % of the time, pitch will be a positive angle, so this case will run most of the time.
            return angleAmount; //return a positive angle to "undo" a negative pitch noticed in the hand
        }
    }


    //can in some regards, be thought of as the roll component also.
    private float getfinalYaw_relatedToRoll(Hand h) {

        //even though im getting roll, this will be treated as finalYaw.
        //if fingers pointing to the -z direction, then roll will be determined by the amount of projection of the pn onto XY plane
        //if -- pointing to x direcion, roll -- onto YZ plane
        //if fingers pointing somewhere in the middle, then need to weight both projections appropriately and add them together.
        //regardless, i need to weigh the projections against the full pn and only "roll" by the appropriate amount. (*)
        //

        Vector d = h.direction();
        Vector pn = h.palmNormal();

        //get projection of pn to yz plane
        Vector projToYZ = DebugHelper.getProjection(pn, "YZ");
        Vector projToXY = DebugHelper.getProjection(pn, "XY");


        //check how much of the pn are these projections
//        float percentProjYZ = projToYZ.magnitude() / pn.magnitude();
//        float percentProjXY = projToXY.magnitude() / pn.magnitude();

        float percentOfPN_onZ = pn.getZ();
        float percentOfPN_onX = pn.getX();


        //need to review this later
        float angleBtwProjYZ_yAxis = 180.0f - (float) Math.toDegrees(projToYZ.angleTo(Vector.yAxis()));
        float angleBtwProjXY_yAxis = 180.0f - (float) Math.toDegrees(projToXY.angleTo(Vector.yAxis()));
//        float combinedAngles = angleBtwProjXY_yAxis + angleBtwProjYZ_yAxis;

        //note, i think the absolute values need to be added so the opposite directions dont cancel each other out
        //think -z, x or -x, z 45 deg
        float angleYZ_yaxis_weighted = angleBtwProjYZ_yAxis * percentOfPN_onZ;
        float angleXY_yaxis_weighted = angleBtwProjXY_yAxis * percentOfPN_onX;
        float combinedAngles_weighted = angleYZ_yaxis_weighted + angleXY_yaxis_weighted;


        //find the direction of roll/yaw2 by using the cross product. no, unnecessary.
        return -1.0f * combinedAngles_weighted;
    }

    private void tryAgain(Hand h) {
        //try to get ypy angles from just looking at the hand info
        float y1_angle_yawOnXZPlane = getfirstYaw(h);
        float p_angle = getPitch(h);
        float y2_angle_rollCousin = getfinalYaw_relatedToRoll(h);


        //the yaw that happens on the plane of xz.
        float firstYaw_original = y1_angle_yawOnXZPlane; //-45 means *clockwise* when looking down the negative y-axis in java_cs.(remember negative y-axis in javacs points upwards)
        //passed in parameters, AS SEEN FROM LM CS, based on the pictures. not the words!
        // also note the spinning around counter-clockwise axis is in lmcs is not following convention
        float p_original = p_angle; //-90 means rotate **clockwise** by 90 degrees around x-axis when looking down -xaxis. inside lmcs! picture lmdocs = correct
        float y_original = y2_angle_rollCousin; //-90 means rotate counter-clockwise by 90 degrees around y-axis when looking down -yaxis. inside lmcs! picture lmdocs = correct
        float r_original = 0; //-90 means rotate counter-clockwise by 90 degrees around the z-axis when looking down -zaxis. inside lmcs! picture lmdocs = correct


        //fix incoming angles (that were deteremined using the lmcs) to correct coordinate system and assumptions.
        // the cs the matrixRotateNode method seems to be using is Javafxc
        float p = p_original * (-1.0f);
        float y = y_original * (-1.0f);
        float r = r_original * (-1.0f);
        float fy = firstYaw_original;// dont need to multiply by -1 because the method that will use this is already using java_cs.

        //to be passed into the method
        float pitch = (float) Math.toRadians(p);
        float yaw = (float) Math.toRadians(y);
        float roll = (float) Math.toRadians(r);
        float firstYaw = fy; // dont need to convert to radians for Rotate transform java class.

        //order of operations: pitch, yaw, roll.
        ViewMath.matrixRotateNode(this, roll, pitch, yaw);

        //add a yaw to perform before the matrixRotateNode sets the axis and angle.
        if (this.getTransforms().size() == 0) {
            this.getTransforms().add(new Rotate(firstYaw, new Point3D(0, 1, 0)));
        } else {
            this.getTransforms().set(0, new Rotate(firstYaw, new Point3D(0, 1, 0)));
        }
    }

    public void fixOrientation(Hand h) {
        tryAgain(h);
    }


    @Override
    public void setDirectionTo(Hand hand) {
        System.out.println("setDirectionTo Method, i think this method isn't used anywhere");
    }


    @Override
    public void setChildrenOpacity(double opacity) {
        for (Node child : getChildren()) child.setOpacity(opacity);
    }

}
