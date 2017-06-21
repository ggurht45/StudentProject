package view.analyze;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LoadGesturesScene {

    private LeapUIApp app;
    private String handToLoadFile;
    public static UIHand loadedHandUI;
    public static Hand lh;
    public static UIHand targetHandUI;
    public static Hand th;
    public static Group rootGroup;
    public static Scene scene;

    public LoadGesturesScene(LeapUIApp app) {
        this.app = app;
        //set up the loadedHandUI and target hand
        loadedHandUI = new UIHand_Simple(Color.BLUE.darker(), false);
        loadedHandUI.setVisible(false);
        targetHandUI = new UIHand_Simple(Color.DARKRED, true);
        targetHandUI.setVisible(false);


        //button to go back to main scene
        Button button2 = new Button("Go Back");
        button2.setOnAction(e -> app.window.setScene(app.scene));
        button2.setTranslateX(app.ScreenWidth * 1 / 5);
        button2.setTranslateY(app.ScreenHeight * 4 / 5);
        button2.setPrefHeight(50);
        button2.setFont(Font.font(app.STYLESHEET_MODENA, FontWeight.BOLD, 15));

        //button to load hand into the scene
        Button loadHandButton = makeGenericButton("Load Hand", 0.4, 0.8);//new Button("Load Hand");
        loadHandButton.setOnAction(e -> {
            lh = getHandFromString(handToLoadFile);
            th = getHandFromString("targets/2015-05-05 08-17-01.hand");
            loadedHandUI.setLoc(lh);
            loadedHandUI.setVisible(true);
            targetHandUI.setLoc(th);
            targetHandUI.setVisible(true);
        });

        //make a compare button. that compares it against the appropriate hand... should be stored somehow.
        Button compareButton = makeGenericButton("Compare Hands", 0.5, 0.8);//new Button("Load Hand");
        compareButton.setOnAction(e -> {
//            fixHand();
            double score = compareTwoHands(lh, th);
            System.out.println("score after comparing h, th:" + score);

        });


        String str1 = "targets/2015-05-05 08-17-01.hand";
        String str2 = "targets/2017-06-12 12-13-58.hand"; //should be very close to the 0th hand
        String str3 = "targets/2017-06-12 12-18-33.hand"; //palm facing downwards
        String str4 = "targets/2017-06-12 12-21-01.hand"; //palm facing downwards and fingers pointing to right
        String str5 = "targets/2017-06-12 12-23-19.hand"; //right hand palm, upwards
        String str6 = "targets/2017-06-12 12-30-56.hand"; //palm facing down again
//        List<String> al = new ArrayList(Arrays.asList("normal facing up", "facing down", "facing down fingers to right", "right hand palm upright", "facing down 2"));

        //make a comboList
        String lhGesturesFile = "LeftGestures.txt";
        String rhGesturesFile = "RightGestures.txt";
        List<Hand> handList = SerializedTargetHand.getAllHands2(lhGesturesFile);
        List<String> fileList = SerializedTargetHand.getAllHands2Names(lhGesturesFile);

        ObservableList<String> options = FXCollections.observableArrayList(fileList);
        ComboBox comboBox = new ComboBox(options);
        //initially set up to load 0th item
        comboBox.setValue(fileList.get(0));
        handToLoadFile = fileList.get(0);
        comboBox.setOnAction(e -> {
            System.out.println("selected: " + comboBox.getValue());
            handToLoadFile = (String) comboBox.getValue();
        });
        comboBox.setTranslateX(app.ScreenWidth * (2 / 5));
        comboBox.setTranslateY(app.ScreenHeight * 4 / 5);


        // The 3D camera; necessary for 3D display
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(new Translate(0, -5, -50), new Rotate(-10, Rotate.X_AXIS));


        // The 3D display
        Group group3D = new Group();
        group3D.getChildren().add(camera);
        group3D.getChildren().add(targetHandUI);
        group3D.getChildren().add(loadedHandUI);
        SubScene sub3D = new SubScene(group3D, app.ScreenWidth, app.ScreenHeight, true, SceneAntialiasing.BALANCED); // "true" gives us a depth buffer
        sub3D.setFill(Color.LAVENDER);
        sub3D.setCamera(camera);

        // The 2D overlay
        Group group2D = new Group(comboBox, button2, loadHandButton, compareButton);
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

    private Button makeGenericButton(String name, double xTranslate, double yTranslate) {
        Button b = new Button(name);
        b.setTranslateX(app.ScreenWidth * xTranslate);
        b.setTranslateY(app.ScreenHeight * yTranslate);
        b.setPrefHeight(50);
        b.setFont(Font.font(app.STYLESHEET_MODENA, FontWeight.BOLD, 15));
        return b;
    }

    public double compareTwoHands(Hand h1, Hand h2) {
        double score = 0.0;
        try {
            score = app.comparer.compare(h1, h2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return score;
    }

    private void fixHand() {
        System.out.println("gonna fix angles before comparing");
        //compare lh th directions.
        Vector lhd = lh.direction();
        Vector thd = th.direction();
        System.out.println("lhdirection: " + lhd + " \t " + "thdirection: " + thd);

        Double d1 = loadedHandUI.getRotate();
        Point3D p1 = loadedHandUI.getRotationAxis();
        Double d2 = targetHandUI.getRotate();
        Point3D p2 = targetHandUI.getRotationAxis();
        System.out.println(d1 + " " + d2 + " \t" + p1 + " " + p2);


        //so these work, but the cumulative effect doesnt take hold.

        double rotationAngleX = thd.getX() * (-90.0);
        Point3D axisForX = new Point3D(0, 1, 0);

        double rotationAngleZ = thd.getZ() * (-90.0);
        Point3D axisForZ = new Point3D(1, 0, 0);

        double rotationAngleY = thd.getY() * (-90.0);
        Point3D axisForY = new Point3D(0, 0, 1);

//        double rotationAngleX = thd.getX()*(-90.0);
//        loadedHandUI.setRotationAxis(new Point3D(0, 1, 0));
//        loadedHandUI.setRotate(rotationAngleX);
//
//        double rotationAngleZ = thd.getZ()*(-90.0);
//        loadedHandUI.setRotationAxis(new Point3D(1, 0, 0));
//        loadedHandUI.setRotate(rotationAngleZ);
//
//
//        loadedHandUI.setRotationAxis(new Point3D(0, 0, 1));
//        double rotationAngleY = thd.getY()*(-90.0);
//        loadedHandUI.setRotate(rotationAngleY);


        //didnt work kinda close though. maybe try to make clones or copys of objects. needs more thought later
        //no doesnt work at all basically. cuz clicking on compare multiple times causes problems.
//        loadedHandUI.getTransforms().removeAll();
//        loadedHandUI.getTransforms().add(new Rotate(rotationAngleX, axisForX));
//        loadedHandUI.getTransforms().add(new Rotate(rotationAngleZ, axisForZ));
//        loadedHandUI.getTransforms().add(new Rotate(rotationAngleY, axisForY));


        //this stuff may also be useful later.
//        loadedHandUI.getLayoutX();
//        loadedHandUI.getLayoutY();
//        loadedHandUI.getLocalToParentTransform();

//        System.out.println("rotation angles: " + rotationAngleY + " " + rotationAngleX + " " + rotationAngleZ);


        loadedHandUI.setDirectionTo(th);
    }

}
