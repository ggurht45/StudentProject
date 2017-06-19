package view.analyze;

import com.leapmotion.leap.Hand;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import model.SerializedTargetHand;
import view.LeapUIApp;
import view.anatomy.UIHand;
import view.anatomy.UIHand_Simple;


public class LoadGesturesScene{

    public static UIHand loadedHandUI;
    public static Group rootGroup;
    public static Scene scene;

    public LoadGesturesScene(LeapUIApp app) {

        //set up the loadedHandUI
        loadedHandUI = new UIHand_Simple(Color.GREEN.darker(), false);
        loadedHandUI.setVisible(false);

        //button to go back to main scene
        Button button2 = new Button("Go Back");
        button2.setOnAction(e -> app.window.setScene(app.scene));
        button2.setTranslateX(app.ScreenWidth * 1 / 5);
        button2.setTranslateY(app.ScreenHeight * 4 / 5);
        button2.setPrefHeight(50);
        button2.setFont(Font.font(app.STYLESHEET_MODENA, FontWeight.BOLD, 15));

        //button to load hand into the scene
        Button loadHandButton = new Button("Load Hand");
        loadHandButton.setOnAction(e -> {
            System.out.println("load hand button clicked");
            Hand h = getHandFromString("targets/2017-06-12 12-13-58.hand");
            System.out.println("seemed to have gotten hand, about to turn on visibility");
            loadedHandUI.setLoc(h);
            loadedHandUI.setVisible(true);

        });
        loadHandButton.setTranslateX(app.ScreenWidth * 2 / 5);
        loadHandButton.setTranslateY(app.ScreenHeight * 4 / 5);
        loadHandButton.setPrefHeight(50);
        loadHandButton.setFont(Font.font(app.STYLESHEET_MODENA, FontWeight.BOLD, 15));


        // The 3D camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(new Translate(0, -5, -50), new Rotate(-10, Rotate.X_AXIS));


        // The 3D display
        Group group3D = new Group();
        group3D.getChildren().add(camera);
        group3D.getChildren().add(loadedHandUI);
        SubScene sub3D = new SubScene(group3D, app.ScreenWidth, app.ScreenHeight, true, SceneAntialiasing.BALANCED); // "true" gives us a depth buffer
        sub3D.setFill(Color.LAVENDER);
        sub3D.setCamera(camera);

        // The 2D overlay
        Group group2D = new Group(button2, loadHandButton);
        SubScene sub2D = new SubScene(group2D, app.ScreenWidth, app.ScreenHeight, false, SceneAntialiasing.BALANCED); // "false" because no depth in 2D


        //put 2D and 3D subScenes together; and make it into a scene
        rootGroup = new Group(sub3D, sub2D); // sub2D is second, as we want it overlaid, not underlaid
        scene = new Scene(rootGroup);

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
