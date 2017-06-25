package view.anatomy;

import javafx.geometry.Point3D;
import javafx.scene.shape.Box;
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

public class UIHand_SuperSimple extends UIHand {

    private static float defaultWidth = 34f;
    private static float defaultHeight = 74f;
    private static float defaultDepth = 14f;
    private Box handRepresentation;

    public UIHand_SuperSimple(Color color, boolean wireframe) {
        super();
        handRepresentation = new Box(defaultWidth, defaultHeight, defaultDepth);

        //add children to UiHand group
        this.getChildren().add(handRepresentation);

    }

    @Override
    public void setLoc(Hand hand) {


    }

    @Override
    public void setDirectionTo(Hand hand) {
    }


    @Override
    public void setChildrenOpacity(double opacity) {
        for (Node child : getChildren()) child.setOpacity(opacity);
    }

}
