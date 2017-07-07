package view.analyze;


import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import model.SerializedTargetHand;
import view.DebugHelper;
import view.LeapUIApp;
import view.ViewMath;
import view.anatomy.UIHand;
import view.anatomy.UIHand_Simple;
import view.anatomy.UIHand_SuperSimple;

import javax.swing.text.View;

public class LoadGesturesScene2 {
    private LeapUIApp app;
    private static UIHand uiHand1;
    private static Hand realTarget;
    private static UIHand uiHand2;
    private static UIHand uiHand3;
    private static Hand loadedHand;
    private static Group rootGroup;
    private static Scene scene;


    public LoadGesturesScene2(LeapUIApp app) {
        this.app = app;

//        DebugHelper.printInfoManyHands();
//        DebugHelper.runVectorDebugTests();


        uiHand1 = new UIHand_SuperSimple(Color.BLUE.darker(), true);
        uiHand2 = new UIHand_Simple(Color.GREEN, false);
        uiHand3 = new UIHand_Simple(Color.RED, true);


        realTarget = getHandFromString("targets/2017-06-12 12-13-58.hand"); //--normal. facing up.
        loadedHand = getHandFromString("targets/2017-06-12 12-21-01.hand"); //--to the right
//        loadedHand = getHandFromString("targets/2017-06-12 12-18-33.hand"); //--downwards
//        loadedHand = getHandFromString("dataOutput/1/typeA_2017-06-30 08-08-37.hand"); //-- (handshake position) facing downwards, palm to the right (roll left), pointing -z direction
//        DebugHelper.printHandInfo(loadedHand, "sanity check. loaded hand info");
//        DebugHelper.printVectorOrientationAngles(loadedHand.direction(), "direction of the loaded hand");
//        DebugHelper.printVectorOrientationAngles(loadedHand.palmNormal(), "palm Normal of the loaded hand");


        //print direction vectors orientation angles for sanity check
//        DebugHelper.printVectorOrientationAngles(realTarget.direction(), "Target Hand Direction");
//        DebugHelper.printVectorOrientationAngles(loadedHand.direction(), "Loaded Hand Direction");


        //uiHand1 setup
        uiHand1.setLoc(realTarget);
        uiHand1.setVisible(true);
        uiHand1.setTranslateX(-8);

        //uihand2 set up
        uiHand2.setLoc(loadedHand);
        uiHand2.setVisible(true);
        uiHand2.setTranslateX(8);

        //uihand3 set up
        uiHand3.setLoc(loadedHand);
        uiHand3.setVisible(true);
        uiHand3.setTranslateX(16);

        //button to fix orientation
        Button fixOrientationButton = new Button("Fix Orientation");
        fixOrientationButton.setOnAction(e -> {
            System.out.println("gonna fix orientation");
            //need parenthesis..
            ((UIHand_Simple) uiHand2).fixOrientation(loadedHand);

        });


        // The 3D camera; necessary for 3D display
        //weird things about camera: y increases downwards, z increases into the screen, x increases to the right
        //from google result: 'In JavaFX, the camera coordinate system is as follows: • X-axis pointing to the right • Y-axis pointing down • Z-axis pointing away from the viewer or into the screen.'
        //transforms get added lifo.
        PerspectiveCamera camera = new PerspectiveCamera(true);
        //z increases into the screen for javafx. unlike leap motion controller coordinate system
        //note, the order in which transforms are added matters. a lot. it seems to be the last one added is executed first. lifo. very weird
        //angles increase counter clockwise when looking down the negative of the axis. -90 x-axis makes sense now.
//        camera.getTransforms().addAll(new Rotate(-90, Rotate.X_AXIS), new Translate(0, 0, -50));
//        camera.getTransforms().addAll(new Translate(0, -70, 0), new Rotate(-90, Rotate.X_AXIS)); //note how we have to set the y axis now. y seems to increase downwards.
        camera.getTransforms().addAll(new Translate(0, -5, -50), new Rotate(-10, Rotate.X_AXIS));


        // The 3D display
        Group group3D = new Group();
        group3D.getChildren().add(camera);
        group3D.getChildren().addAll(uiHand1, uiHand2, uiHand3);
//        group3D.getChildren().add(uiHand2);
        SubScene sub3D = new SubScene(group3D, app.ScreenWidth, app.ScreenHeight, true, SceneAntialiasing.BALANCED); // "true" gives us a depth buffer
        sub3D.setFill(Color.LAVENDER);
        sub3D.setCamera(camera);

        // The 2D overlay
        Group group2D = new Group(fixOrientationButton);
        SubScene sub2D = new SubScene(group2D, app.ScreenWidth, app.ScreenHeight, false, SceneAntialiasing.BALANCED); // "false" because no depth in 2D


        //put 2D and 3D subScenes together; and make it into a scene
        rootGroup = new Group(sub3D, sub2D); // sub2D is second, as we want it overlaid, not underlaid
//        rootGroup = new Group(sub2D); // sub2D is second, as we want it overlaid, not underlaid
        scene = new Scene(rootGroup);

    }

    public Scene getScene() {
        return scene;
    }

    public Hand getHandFromString(String s) {
        Hand h = null;
        try {
            h = SerializedTargetHand.readFromFile(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return h;
    }

}




