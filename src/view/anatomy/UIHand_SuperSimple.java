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
        this.getChildren().addAll(hand, thumb, fingers);

    }

//    private void createHandGroup(){
//
//    }

    @Override
    public void setLoc(Hand hand) {
        System.out.println("in setLoc method of superSimple hand");
        //hand.direction is a unit vector
//        Vector direction = new Vector(0, 1, 0);
        Vector direction = new Vector(1,0,0);
//        Vector direction = new Vector(0,0,-1);
//        Vector direction = new Vector(1,0,-1);
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


        //fix rotate
//        this.hand.getTransforms().add(new Rotate(90, new Point3D(1,0,0)));
//        fingers.getTransforms().add(new Rotate(90, new Point3D(1,0,0)));
//        thumb.getTransforms().add(new Rotate(90, new Point3D(1,0,0)));

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


    }

    @Override
    public void setDirectionTo(Hand hand) {
    }


    @Override
    public void setChildrenOpacity(double opacity) {
        for (Node child : getChildren()) child.setOpacity(opacity);
    }

}
