package view.analyze;


import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import model.ModifiableHand;
import model.SerializedTargetHand;
import view.LeapUIApp;
import view.ViewMath;
import view.anatomy.UIHand;
import view.anatomy.UIHand_Simple;

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

        ViewMath.printInfoManyHands();

        //      ------------------------- understanding lm p r y
        ViewMath.printVectorOrientationAngles(new Vector(0, 0, -1.0f), "Negative Z Axis (lmotion)");
        System.out.println("Why is the roll 180?!! \n \n");
        // z-axis angles (especially roll is weird
        Vector almostNegZAxis = new Vector(0.05f, 0.05f, -0.9f);
        almostNegZAxis = almostNegZAxis.normalized();
        ViewMath.printVectorOrientationAngles(almostNegZAxis, "Aaaalmost Negative Z Axis (lmotion)");
        System.out.println("Why is the roll 135?!! ");

        Vector projectionXY = ViewMath.getProjection(almostNegZAxis, "XY");
        float angle =Vector.yAxis().angleTo(projectionXY);
        System.out.println("y axis in lm??!!: " + Vector.yAxis() + " \n" + "angle btw yaxis and projection: " + projectionXY + " angle(radians): " + angle  + " angle(deg): " + Math.toDegrees(angle));
        System.out.println("so, the Roll angle seems to be between the javafx y-axis (which points down) and projection onto the xy plane. because 135+45 = 180 \n");

        Vector test2 = new Vector(0.01f, 0.09f, -0.9f);
        test2 = test2.normalized();
        ViewMath.printVectorOrientationAngles(test2, "Aaaalmost Negative Z Axis, a more skewed projection (lmotion)");
        Vector ptest2 = ViewMath.getProjection(test2, "XY");
        float ang2 =Vector.yAxis().angleTo(ptest2);
        System.out.println("y axis in lm(more like javafx): " + Vector.yAxis() + " \n" + "angle btw yaxis and projection: " + ptest2 + " angle(radians): " + ang2  + " angle(deg): " + Math.toDegrees(ang2));
        System.out.println("\n");


        // y-axis
        ViewMath.printVectorOrientationAngles(new Vector(0, 1, -0.0f), "Positive Y Axis (lmotion)");






        // x-axis -- -0.0f?!!
        ViewMath.printVectorOrientationAngles(new Vector(1, 0, 0.0f), "Positive X Axis with POSITIVE 0 for zAxis (lm cs)", true);
        //what the fudge! there is such thing as a negative zero.
        ViewMath.printVectorOrientationAngles(new Vector(1, 0, -0.0f), "Positive X Axis with NEGATIVE -0! for zAxis (lm cs (supposedly))", true);

        Vector almostXAxis = new Vector(0.9f, 0.05f, -0.05f); //note the direction change in the z axis.
        almostXAxis = almostXAxis.normalized();
        ViewMath.printVectorOrientationAngles(almostXAxis, "Aaaalmost Positive X Axis, with slight z, y coordinates. (lmotion)", true);
        System.out.println("note the high pitch. this is what makes me think i need to multiply it by the weight of the projection? in addition, note the NEGATIVE z direction \n");

        ViewMath.printVectorOrientationAngles(new Vector(0.5f, 0, -0.5f), "X(-Z) plane 45 degree Axis (lm_cs)", true);

        Vector xz45deg = new Vector(0.5f, 0.03f, -0.5f).normalized();
        ViewMath.printVectorOrientationAngles(xz45deg, "X(-Z) plane 45 degree Axis with slight y direction. Note the pitch. (lm_cs)", true);

        Vector xyz45deg = new Vector(0.5f, 0.5f, -0.5f).normalized();
        ViewMath.printVectorOrientationAngles(xyz45deg, "XY(-Z) plane 45 degrees all. Note the pitch. (lm_cs)", true);

        Vector xy45deg = new Vector(0.5f, 0.5f, -0.0f).normalized();
        ViewMath.printVectorOrientationAngles(xy45deg, "xy 45 degrees (lm cs)", true);

        Vector xy_smallz45deg = new Vector(0.5f, 0.5f, -0.1f).normalized();
        ViewMath.printVectorOrientationAngles(xy_smallz45deg, "xy smallZ 45 degrees (lm cs)", true);










        uiHand1 = new UIHand_Simple(Color.BLUE.darker(), true);
        uiHand2 = new UIHand_Simple(Color.GREEN, false);
        uiHand3 = new UIHand_Simple(Color.RED, true);


        realTarget = getHandFromString("targets/2017-06-12 12-13-58.hand"); //--normal. facing up.
        loadedHand = getHandFromString("targets/2017-06-12 12-21-01.hand"); //--to the right
//        loadedHand = getHandFromString("targets/2017-06-12 12-18-33.hand"); //--downwards
//        loadedHand = getHandFromString("dataOutput/1/typeA_2017-06-30 08-08-37.hand"); //-- (handshake position) facing downwards, palm to the right (roll left), pointing -z direction
        ViewMath.printHandInfo(loadedHand, "sanity check. loaded hand info");
        ViewMath.printVectorOrientationAngles(loadedHand.direction(), "direction of the loaded hand");
        ViewMath.printVectorOrientationAngles(loadedHand.palmNormal(), "palm Normal of the loaded hand");


        //print direction vectors orientation angles for sanity check
        ViewMath.printVectorOrientationAngles(realTarget.direction(), "Target Hand Direction");
        ViewMath.printVectorOrientationAngles(loadedHand.direction(), "Loaded Hand Direction");



        //testing my hand class that i made
        ModifiableHand ht = new ModifiableHand(realTarget);
        ht.runTests();

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




