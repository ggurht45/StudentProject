package view.anatomy;

import javafx.geometry.Point3D;
import javafx.scene.transform.Rotate;
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
        for (int i = 0; i < 4; ++i){
            ViewMath.setCylinderByEndpoints(knuckleSpans[i], knuckles[i], knuckles[i + 1]);
        }
        ViewMath.setCylinderByEndpoints(palmWrist, knuckles[0], pinkyBase);
        ViewMath.setPositionByVector(pinkyJoint, pinkyBase);


        System.out.println("***EE");
        System.out.println("layout: " + this.getLayoutX() + " " + this.getLayoutY() );
        System.out.println("translate: " + this.getTranslateX() + " " + this.getTranslateY() + " " + this.getTranslateZ() );
        System.out.println("this = simple hand after setLoc");
        System.out.println("this.getRotate(): " + this.getRotate());
        System.out.println("this.getRotationAxis(): " + this.getRotationAxis());
        System.out.println("***EE");

    }

    public void fixOrientation(Hand h){
        System.out.println("***EE2");
        System.out.println("layout: " + this.getLayoutX() + " " + this.getLayoutY() );
        System.out.println("translate: " + this.getTranslateX() + " " + this.getTranslateY() + " " + this.getTranslateZ() );
        System.out.println("this = simple hand after setLoc");
        System.out.println("this.getRotate(): " + this.getRotate());
        System.out.println("this.getRotationAxis(): " + this.getRotationAxis());
        System.out.println("h.direction(): " + h.direction());
        System.out.println("h.direction().opposite: " + h.direction().opposite());
        System.out.println("***EE2");
        Vector v = new Vector(0,150,0); //weird work around for setPosition method
        Vector d = h.direction().opposite();
        d.setZ(d.getZ()*-1);
        ViewMath.setGroup2(this, v, d.times(20), Vector.yAxis());
        System.out.println("***EE3");
        System.out.println("layout: " + this.getLayoutX() + " " + this.getLayoutY() );
        System.out.println("translate: " + this.getTranslateX() + " " + this.getTranslateY() + " " + this.getTranslateZ() );
        System.out.println("this = simple hand after setLoc");
        System.out.println("this.getRotate(): " + this.getRotate());
        System.out.println("this.getRotationAxis(): " + this.getRotationAxis());
        System.out.println("h.direction(): " + h.direction());
        System.out.println("h.direction().opposite: " + h.direction().opposite());
        System.out.println("direction at d: " + d);
        System.out.println("***EE3");
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
