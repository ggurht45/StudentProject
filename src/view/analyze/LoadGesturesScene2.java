package view.analyze;


import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import model.SerializedTargetHand;
import view.LeapUIApp;
import view.anatomy.UIHand;
import view.anatomy.UIHand_Simple;

import java.util.List;

public class LoadGesturesScene2 {
    private LeapUIApp app;
    private static UIHand uiHand1;
    private static Hand lh;
    private static UIHand uiHand2;
    private static Hand th;
    private static Group rootGroup;
    private static Scene scene;


    public LoadGesturesScene2(LeapUIApp app) {
        this.app = app;

//        Text label = new Text("hello world");
        Button label = new Button ("click me!");



        // The 3D camera; necessary for 3D display
//        PerspectiveCamera camera = new PerspectiveCamera(true);
//        camera.getTransforms().addAll(new Translate(0, -5, -50), new Rotate(-10, Rotate.X_AXIS));


        // The 3D display
//        Group group3D = new Group();
//        group3D.getChildren().add(camera);
//        group3D.getChildren().add(uiHand1);
//        group3D.getChildren().add(uiHand2);
//        SubScene sub3D = new SubScene(group3D, app.ScreenWidth, app.ScreenHeight, true, SceneAntialiasing.BALANCED); // "true" gives us a depth buffer
//        sub3D.setFill(Color.LAVENDER);
//        sub3D.setCamera(camera);

        // The 2D overlay
        Group group2D = new Group(label);
        SubScene sub2D = new SubScene(group2D, app.ScreenWidth, app.ScreenHeight, false, SceneAntialiasing.BALANCED); // "false" because no depth in 2D


        //put 2D and 3D subScenes together; and make it into a scene
//        rootGroup = new Group(sub3D, sub2D); // sub2D is second, as we want it overlaid, not underlaid
        rootGroup = new Group(sub2D); // sub2D is second, as we want it overlaid, not underlaid
        scene = new Scene(rootGroup);

    }

    public Scene getScene(){
        return scene;
    }

}




