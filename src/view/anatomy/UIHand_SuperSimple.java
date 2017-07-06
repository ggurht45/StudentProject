package view.anatomy;

import com.leapmotion.leap.Vector;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.shape.Box;

import com.leapmotion.leap.Hand;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import view.ViewMath;

import javax.swing.text.View;

public class UIHand_SuperSimple extends UIHand {

    private static float defaultWidth = 5f;
    private static float defaultHeight = 10f;
    private static float defaultDepth = 2f;
    private Box hand;
    private Cylinder thumb;
    private Sphere fingers;
    private Group entireGroup;

    public UIHand_SuperSimple(Color color, boolean wireframe) {
        super();
        PhongMaterial dark = new PhongMaterial(color);
        PhongMaterial thumbColor = new PhongMaterial(Color.BURLYWOOD);
        PhongMaterial fingersColor = new PhongMaterial(Color.CADETBLUE);
        hand = new Box(defaultWidth, defaultHeight, defaultDepth);
        hand.setMaterial(dark);
        thumb = new Cylinder(2, 8);
        thumb.setMaterial(thumbColor);
        fingers = new Sphere(2);
        fingers.setMaterial(fingersColor);


        //need to fix these in setLoc
//        thumb.setTranslateX(4);
//        fingers.setTranslateY(-6); //java coordinate system, y increases downwards. z into the screen, and x to the right.

//        this.setScaleY(.5);

////        this.getTransforms().add(new Rotate(10, new Point3D(1,0,0)));
//        this.setRotate(20);

        //add children to UiHand group
        entireGroup = new Group();
        entireGroup.getChildren().addAll(hand, thumb, fingers);
        this.getChildren().add(entireGroup);

    }

//    private void createHandGroup(){
//
//    }

    //epiphany. to get to 45, 45 x,-z handshake position.
    //i must add a rotation transform at the end. that basically does a rotation around the y-axis. a 90 yaw. -> flipping -90 yaw.
    //why? because order is always pitch then yaw, then roll. so if you want to do yaw before pitch, it must come seperately.^^

    private void tryAgain(Hand hand) {
        //passed in parameters, AS SEEN FROM LM CS!
        float y1_original = -90;//-> this is the most tricky to think about. it sometimes reminds me of roll. but its a yaw i perform before the other two pitch and yaw. Note: by "before" i mean i will write it in code "after" the other two.
        float p_original = 10;
        float y2_original = -45;


        //fix incoming angles to correct coordinate system and assumpstions
        float y1 = y1_original * (-1.0f);
        float y2 = y2_original * (-1.0f);
        float p = 90.0f - p_original;

        System.out.println("***");
        System.out.println("groupHand position before setGroup called in setLoc in superSimple");
        System.out.println("layout: " + this.getLayoutX() + " " + this.getLayoutY());
        System.out.println("translate: " + this.getTranslateX() + " " + this.getTranslateY() + " " + this.getTranslateZ());
        System.out.println("this.getRotate(): " + this.getRotate());
        System.out.println("this.getRotationAxis(): " + this.getRotationAxis());


        //new approach, first hard set the the direction of the nodes in this hand.
        ViewMath.setGenericNode(this.hand, hand.stabilizedPalmPosition(), Vector.yAxis().times(20));
        ViewMath.setGenericNode(this.thumb, hand.stabilizedPalmPosition(), Vector.yAxis().times(20));
        ViewMath.setGenericNode(this.fingers, hand.stabilizedPalmPosition(), Vector.yAxis().times(20));


        //determine correct p,y,r from direction:
        Vector direction = new Vector(1, 0, -1);


        float roll = (float) Math.toRadians(0);
        //flip sign for pitch and yaw before passing them as arguments.
        //remember pitch downwards is -90 or whatever angle. and yaw to the right is +30. or whatever angle.
        //now after you have thought of the angles as above. remember to flip them, as told below.
        //so if you imagined the pitch and yaw angles to be -90, 30, respectively, then you should pass them as 90, -30;
        //i think this may be b/c of jcs vs lcs. leap coordinate sys vs javafx cs.
        float pitch = (float) Math.toRadians(p);
        float yaw = (float) Math.toRadians(y2);
        //stackoverflow: alf is roll, bet is pitch and gam is yaw.
        ViewMath.matrixRotateNode(this.entireGroup, roll, pitch, yaw);

        //fix translate
        fingers.getTransforms().add(new Translate(0, -6, 0)); // this transform happens first. transforms that get added last, are performed first
        thumb.getTransforms().add(new Translate(4, 0, 0));

        //add rotation around y-axis. yaw of 90 -> flip -> -90;
        //this works. looking down the opposite of the axis and +theta means to revolve counterclockwise around axis when looking down opposite end of axis
        this.entireGroup.getTransforms().add(new Rotate(y1, new Point3D(0, -1, 0))); //this is correct way in my current situation. to be inline with other code above.such as matrixRotateNode
        //this also works. note the axis being used is in javafx cs.
//        this.entireGroup.getTransforms().add(new Rotate(90, new Point3D(0, 1, 0)));


        System.out.println("groupHand position After setGroup called in setLoc in superSimple");
        System.out.println("layout: " + this.getLayoutX() + " " + this.getLayoutY());
        System.out.println("translate: " + this.getTranslateX() + " " + this.getTranslateY() + " " + this.getTranslateZ());
        System.out.println("this.getRotate(): " + this.getRotate());
        System.out.println("this.getRotationAxis(): " + this.getRotationAxis());
        System.out.println("***");


        System.out.println("***2");
        System.out.println("groupHand position After setGroup called in setLoc in superSimple");
        System.out.println("layout: " + this.entireGroup.getLayoutX() + " " + this.entireGroup.getLayoutY());
        System.out.println("translate: " + this.entireGroup.getTranslateX() + " " + this.entireGroup.getTranslateY() + " " + this.entireGroup.getTranslateZ());
        System.out.println("this.getRotate(): " + this.entireGroup.getRotate());
        System.out.println("this.getRotationAxis(): " + this.entireGroup.getRotationAxis());
        System.out.println("***2");


        //seems like the modifications that were performed on y1, y1, p are almost suited for undoing them.
        //only need to switch signs
        undoTransforms(y1_original, p_original, y2_original);


    }

    private void undoTransforms(float firstYaw, float pitch, float lastYaw) {
        //Note the negative -1 in the y-axis. this is important. remember "looking down the opposite direction of the axis" so if -1, is given, we would be looking in the positive y direction (javafxcs) or in other words upwards direction

        pitch = 90.0f - pitch;
        //undo the first yaw that was performed. first meaning when the hand originally in the vertical position and before any pitch or yaws took place.
        this.getTransforms().add(new Rotate(firstYaw, new Point3D(0, -1, 0))); //happens third in relation to these transforms.
        //undo the pitch
        this.getTransforms().add(new Rotate(pitch, new Point3D(1, 0, 0))); //happens second
        //undo the last yaw that was performed
        this.getTransforms().add(new Rotate(lastYaw, new Point3D(0, -1, 0)));//happens first

    }


    @Override
    public void setLoc(Hand hand) {
        System.out.println("in setLoc method of superSimple hand");
        tryAgain(hand);









        /*
        //hand.direction is a unit vector
//        Vector direction = new Vector(0, 1, 0);
//        Vector direction = new Vector(1,0,0);
//        Vector direction = new Vector(0,0,-1);
        Vector direction = new Vector(1,0,-1);
        direction = direction.normalized();

        //old approach
        //        ViewMath.setGroup(this, hand.stabilizedPalmPosition(), hand.direction().times(20));

        //position of group before setGroup is called
//        System.out.println("***");
//        System.out.println("groupHand position before setGroup called in setLoc in superSimple");
//        System.out.println("layout: " + this.getLayoutX() + " " + this.getLayoutY() );
//        System.out.println("translate: " + this.getTranslateX() + " " + this.getTranslateY() + " " + this.getTranslateZ() );

        //new approach
        ViewMath.setGenericNode(this.hand, hand.stabilizedPalmPosition(), direction.times(20));
        ViewMath.setGenericNode(this.thumb, hand.stabilizedPalmPosition(), direction.times(20));
        ViewMath.setGenericNode(this.fingers, hand.stabilizedPalmPosition(), direction.times(20));



        //fix translate
        fingers.getTransforms().add(new Translate(0, -6, 0)); // this transform happens first. transforms that get added last, are performed first
        thumb.getTransforms().add(new Translate(4, 0, 0));
        //this below does not work. its hard setting the translate property.
//        fingers.setTranslateY(transY-6); //java coordinate system, y increases downwards. z into the screen, and x to the right.


        //these below were helpful. i should make a debug method for them

//        System.out.println("groupHand position After setGroup called in setLoc in superSimple");
//        System.out.println("layout: " + this.getLayoutX() + " " + this.getLayoutY() );
//        System.out.println("translate: " + this.getTranslateX() + " " + this.getTranslateY() + " " + this.getTranslateZ() );
//        System.out.println("this.getRotate(): " + this.getRotate());
//        System.out.println("this.getRotationAxis(): " + this.getRotationAxis());
//        System.out.println("***");

//        ViewMath.straightenGroup(this);

*/
    }

    @Override
    public void setDirectionTo(Hand hand) {
    }


    @Override
    public void setChildrenOpacity(double opacity) {
        for (Node child : getChildren()) child.setOpacity(opacity);
    }

}
