package view.anatomy;

import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
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

import javax.swing.text.View;

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
    public void setLoc(Hand hand) {
        //System.out.println("* setLoc called from UIHand_Simple");
        Vector[] knuckles = new Vector[5];
        Vector pinkyBase = new Vector();

        //go through five fingers
        for (int i = 0; i < 5; ++i) {
            Finger finger = hand.fingers().fingerType(Finger.Type.swigToEnum(i)).frontmost();
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
    }


    private void tryAgain(Hand h){
        System.out.println("inside tryAgain");
        DebugHelper.printNodeInfo(this, "UIHand_simple before fixing orientation");

        //passed in parameters, AS SEEN FROM LM CS, based on the pictures. not the words!
        // also note the spinning around counter-clockwise axis is in lmcs is not following convention
        float p_original = 90; //-90 means rotate **clockwise** by 90 degrees around x-axis when looking down -xaxis. inside lmcs! picture lmdocs = correct
        float y_original = 0; //-90 means rotate counter-clockwise by 90 degrees around y-axis when looking down -yaxis. inside lmcs! picture lmdocs = correct
        float r_original = 0; //-90 means rotate counter-clockwise by 90 degrees around the z-axis when looking down -zaxis. inside lmcs! picture lmdocs = correct


        //fix incoming angles to correct coordinate system and assumptions. the cs the matrixRotateNode method seems to be using is Javafxc
        float p = p_original * (-1.0f);
        float y = y_original * (-1.0f);
        float r = r_original * (-1.0f);

        //to be passed into the method
        float pitch = (float) Math.toRadians(p);
        float roll = (float) Math.toRadians(r);
        float yaw = (float) Math.toRadians(y);

        //pitch happens before roll and yaw.
        //yaw happens before roll.
        //order of operations: pitch, yaw, roll.
        ViewMath.matrixRotateNode(this, roll, pitch, yaw);


//        this.getTransforms().add(0, new Rotate(90, new Point3D(101, 0, 0)));
        this.getTransforms().add(new Rotate(-90, new Point3D(0, 1, 0)));



    }

    public void fixOrientation(Hand h) {
        System.out.println("entered fixOrientation UIHand_Simple");


        tryAgain(h);

//        DebugHelper.printNodeInfo(this, "UIHand_simple before fixing orientation");
//
//        double r = 88;  //around z
//        double p_moreLikeYaw = -90; //rotation around x axis --> seems to be around y... --> nope here, it seems to be around x axix? check again to make sure... nono i was wrong. it does seem to be around y. looking like  a good sign ^^.
//        double y_moreLikePitch = 20;  //around y --> seems to be around x... --> here it seems to be around y. check again to make sure.
//
//        //stackoverflow: alf is roll, bet is pitch and gam is yaw.
//        //angles need to be given in radians.
//        ViewMath.matrixRotateNode(this, Math.toRadians(r), Math.toRadians(p_moreLikeYaw), Math.toRadians(y_moreLikePitch));
//
//        DebugHelper.printNodeInfo(this, "UIHand_simple After fixing orientation");
        System.out.println("leaving fixOrientation UIHand_Simple");
    }


    public void fixOrientationOld(Hand h) {
        System.out.println("fixOrientation simple hand");
//        ViewMath.printHandInfo(h, "fixOrientation Method");

//        System.out.println("***EE2");
//        System.out.println("layout: " + this.getLayoutX() + " " + this.getLayoutY() );
//        System.out.println("translate: " + this.getTranslateX() + " " + this.getTranslateY() + " " + this.getTranslateZ() );
//        System.out.println("this = simple hand after setLoc");
//        System.out.println("this.getRotate(): " + this.getRotate());
//        System.out.println("this.getRotationAxis(): " + this.getRotationAxis());
//        System.out.println("h.direction(): " + h.direction());
//        System.out.println("h.direction().opposite: " + h.direction().opposite());
//        System.out.println("***EE2");
//        Vector v = new Vector(0,150,0); //weird work around for setPosition method
//        Vector d = h.direction().opposite();
////        d.setZ(d.getZ()*-1);
////        d.setY(d.getY()*-1);


        System.out.println("num transforms: " + this.getTransforms().size());
        //try 3 transforms. look cam for inspiration. using pitch yaw roll.
//        float pitch = h.direction().pitch();
//        float yaw = h.direction().yaw();
//        float roll = h.palmNormal().roll();
//        pitch = (float)Math.toDegrees(pitch);//casting to Float different from casting to float. primitive vs object types
//        yaw = (float) Math.toDegrees(yaw);
//        roll = (float) Math.toDegrees(roll);
        Vector d = h.direction();
        Vector pn = h.palmNormal();
        float pitch = DebugHelper.getWeightedPYR(d, "pitch", true, false);
        float yaw = DebugHelper.getWeightedPYR(d, "yaw", true, false);
        float roll = DebugHelper.getWeightedPYR(d, "roll", true, false);
        float rollPN = DebugHelper.getWeightedPYR(pn, "roll", true, false);
        Rotate rPitch = new Rotate(pitch, Rotate.X_AXIS);
        Rotate rYaw = new Rotate(yaw, Rotate.Y_AXIS);
        Rotate rRoll = new Rotate(roll, Rotate.Z_AXIS);
        Rotate rRollPN = new Rotate(rollPN, Rotate.Z_AXIS);
        //maybe the problem is happening here. need to test this to make sure its behaving as i expect
//        this.getTransforms().addAll(rPitch, rYaw, rRollPN);

        Rotate swingToLeft = new Rotate(-68, Rotate.Z_AXIS); //by lm: should be 90. by javfx -90? ok need to use javafx. because Rotate.Z_Axis is pointing into the screen
        Rotate swivelAround = new Rotate(-66, Rotate.Y_AXIS); //by lm: should be 90. by javfx -90? sheesh. ok . need to use javafx again
        Rotate turnUp = new Rotate(15, Rotate.X_AXIS); // looking down neg of X-axis, 15 sounds good.



        /*
        right hand info
        pitch, yaw, roll in radians
        pitch: 1.1851473
        yaw: 1.4855914
        roll(d): 1.7781574
        roll(pn): 0.210175
        pitch, yaw, roll in degrees
        pitch: 67.9
        yaw: 85.12
        roll(d): 101.88
        roll(pn): 12.04
        getProjection(pitch:yz; yaw:xz; roll:xy): YZ (0, 0.205163, -0.0832915) mag (weight): 0.22142547
        getProjection(pitch:yz; yaw:xz; roll:xy): XZ (0.975177, 0, -0.0832915) mag (weight): 0.9787279
        getProjection(pitch:yz; yaw:xz; roll:xy): XY (0.975177, 0.205163, 0) mag (weight): 0.9965252
        getProjection(pitch:yz; yaw:xz; roll:xy): XY (0.208513, -0.977441, 0) mag (weight): 0.9994338
        pitchWeighted: 15.04
        yawWeighted: 66.46
        rollWeighted (d): 67.67
        rollWeighted (pn): -91.92

         */

//        double r = 90;  //around z
//        double p = -90; //rotation around x axis --> seems to be around y...
//        double y = 0;  //around y --> seems to be around x.

        //gonna try values.
        //        pitchWeighted: 15.04 --> use this for yaw
        //        yawWeighted: 66.46   --> use this for pitch, flip sign
        //        rollWeighted (d): 67.67
        //        rollWeighted (pn): -91.92 -->use this for roll, flip sign
//        double r = 90;  //around z
//        double p = -90; //rotation around x axis --> seems to be around y... yup. and requires -neg
//        double y = 90;  //around y --> seems to be around x.
















        /*
        facing down hand info
            palmNormal: (0.18, -0.94, 0.29)
            pitch, yaw, roll in radians
            pitch: -0.28922155
            yaw: 0.05520654
            roll(d): 0.18362421
            roll(pn): 0.19239147
            pitch, yaw, roll in degrees
            pitch: -16.57
            yaw: 3.16
            roll(d): 10.52
            roll(pn): 11.02
            getProjection(pitch:yz; yaw:xz; roll:xy): YZ (0, -0.284807, -0.957124) mag (weight): 0.9986002
            getProjection(pitch:yz; yaw:xz; roll:xy): XZ (0.0528933, 0, -0.957124) mag (weight): 0.9585849
            getProjection(pitch:yz; yaw:xz; roll:xy): XY (0.0528933, -0.284807, 0) mag (weight): 0.2896769
            getProjection(pitch:yz; yaw:xz; roll:xy): XY (0.183009, -0.939468, 0) mag (weight): 0.9571276
            pitchWeighted: -16.55
            yawWeighted: -15.88
            rollWeighted (d): -4.8
            rollWeighted (pn): -102.54

         */

//        double r = 90;  //around z
//        double p = -90; //rotation around x axis --> seems to be around y...
//        double y = 0;  //around y --> seems to be around x.

        //gonna try values.
        //        pitchWeighted: -16.55 --> use this for yaw
        //        yawWeighted: -15.88   --> use this for pitch, flip sign
        //        rollWeighted (d): -4.8
        //        rollWeighted (pn): -102.54 -->use this for roll, flip sign
        double r = 90;  //around z
        double p = -40; //rotation around x axis --> seems to be around y... --> nope here, it seems to be around x axix? check again to make sure... nono i was wrong. it does seem to be around y. looking like  a good sign ^^.
        double y = 0;  //around y --> seems to be around x... --> here it seems to be around y. check again to make sure.


        //stackoverflow: alf is roll, bet is pitch and gam is yaw.
        //angles need to be given in radians.

        ViewMath.matrixRotateNode(this, Math.toRadians(r), Math.toRadians(p), Math.toRadians(y));


        //stackoverflow: alf is roll, bet is pitch and gam is yaw.
//        Point3D axis  = ViewMath.getRotationAxis(r, p, y);
//        double angle  = ViewMath.getRotationAngle(r, p, y);
//        Rotate rotateTransform = new Rotate(angle, axis);
//        System.out.println("number of transforms: " + this.getTransforms().size());
//        this.getTransforms().addAll(rotateTransform);

//        this.getTransforms().addAll(swivelAround, swingToLeft); //order does matter. seems swivel happens first? no. the last transform added happens first
//        this.getTransforms().addAll(swingToLeft);
////        this.getTransforms().addAll(swivelAround); //order does matter. (roll after swivel)
//        this.getTransforms().removeAll();
//        this.getTransforms().addAll(swivelAround); //order does matter. (roll after swivel)
////        this.getTransforms().addAll(swingToLeft);

//        this.getTransforms().addAll(swingToLeft, swivelAround); //order does matter. (roll after swivel)
//        this.getTransforms().removeAll();
//        this.getTransforms().addAll(swingToLeft);


//        this.getTransforms().addAll(swingToLeft, swivelAround, turnUp); //order does matter.

        // 1. palm normal should always be -1 in z axis(lmotion). or 1 in javafx . not enough though..
        // cuz 2 axis can still change when palm is facing -z direction(lm) yaw, and roll.
        //so in addition to 1.


        System.out.println(this.getTransforms().toString());
//        ViewMath.setGroup2(this, v, d.times(20), Vector.yAxis());
//        System.out.println("***EE3");
//        System.out.println("layout: " + this.getLayoutX() + " " + this.getLayoutY() );
//        System.out.println("translate: " + this.getTranslateX() + " " + this.getTranslateY() + " " + this.getTranslateZ() );
//        System.out.println("this = simple hand after setLoc");
//        System.out.println("this.getRotate(): " + this.getRotate());
//        System.out.println("this.getRotationAxis(): " + this.getRotationAxis());
//        System.out.println("h.direction(): " + h.direction());
//        System.out.println("h.direction().opposite: " + h.direction().opposite());
//        System.out.println("direction at d: " + d);
//        System.out.println("***EE3");
    }

    @Override
    public void setDirectionTo(Hand hand) {
//        ViewMath.setPositionByVector(this, hand.palmNormal());
//        ViewMath.setRotationByVector(this, hand.palmNormal());


        //how to find angle between 2 3d vectors.
        double angle = this.getRotationAxis().angle(ViewMath.vectorToPoint(hand.palmNormal()));
        this.setRotate(angle);


//        this.getTransforms().add(new Rotate())
//        Vector correctedDirection new Vector(direction.getX(), direction.getY(), -direction.getZ());
//        double angle = correctedDirection.angleTo(Vector.yAxis()) * 180/Math.PI;
//        Point3D axis = vectorToPoint( correctedDirection.cross(Vector.yAxis()) );
//        node.setRotate(angle);
//        node.setRotationAxis(axis)= ;
    }


    @Override
    public void setChildrenOpacity(double opacity) {
        for (Node child : getChildren()) child.setOpacity(opacity);
    }

}
