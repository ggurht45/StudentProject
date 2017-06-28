package view.anatomy;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.shape.Box;

import com.leapmotion.leap.Hand;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import view.ViewMath;

public class UIHand_SuperSimple extends UIHand {

    private static float defaultWidth = 5f;
    private static float defaultHeight = 10f;
    private static float defaultDepth = 2f;
    private Box hand;
    private Cylinder thumb;

    public UIHand_SuperSimple(Color color, boolean wireframe) {
        super();
        PhongMaterial dark = new PhongMaterial(color);
        PhongMaterial thumbColor = new PhongMaterial(Color.BURLYWOOD);
        hand = new Box(defaultWidth, defaultHeight, defaultDepth);
        hand.setMaterial(dark);
        thumb = new Cylinder(2, 8);
        thumb.setMaterial(thumbColor);
        thumb.setTranslateX(4);

//        this.setScaleY(.5);

////        this.getTransforms().add(new Rotate(10, new Point3D(1,0,0)));
//        this.setRotate(20);

        //add children to UiHand group
        this.getChildren().addAll(hand, thumb);

    }

//    private void createHandGroup(){
//
//    }

    @Override
    public void setLoc(Hand hand) {
        System.out.println("in setLoc method of superSimple hand");
//        Group g = this;


        //hand.direction is a unit vector
        System.out.println(hand.id());
//        System.out.println(hand.stabilizedPalmPosition());
//        System.out.println( hand.direction().times(20));
//        Cylinder uiBone = thumb;
//        System.out.println(thumb);
//        ViewMath.setCylinder2(hand.stabilizedPalmPosition(), hand.direction().times(20));
        ViewMath.setGroup(this, hand.stabilizedPalmPosition(), hand.direction().times(20));

        System.out.println("this.getRotate(): " + this.getRotate());
        System.out.println("this.getRotationAxis(): " + this.getRotationAxis());





    }

    @Override
    public void setDirectionTo(Hand hand) {
    }


    @Override
    public void setChildrenOpacity(double opacity) {
        for (Node child : getChildren()) child.setOpacity(opacity);
    }

}
