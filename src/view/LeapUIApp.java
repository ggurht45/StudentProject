// TODO maybe polygon plot

package view;

import java.io.IOException;
import java.util.ArrayList;

import controller.Comparer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.SerializedTargetHand;
import view.analyze.InfoBox;
import view.analyze.LoadGesturesScene;
import view.analyze.SaveBox;
import view.anatomy.UIHand;
//import view.anatomy.UIHand_Full;
import view.anatomy.UIHand_Simple;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.*;

import controller.Control;
import controller.Control2;
import controller.ControllerInterface;
import model.SerializedTargetHand;

// XXX to run: java -Djava.library.path="D:\Software\Leap SDK\LeapDeveloperKit_2.2.2+24469_win\LeapSDK\lib\x64" -classpath ".;D:\Software\Leap SDK\LeapDeveloperKit_2.2.2+24469_win\LeapSDK\lib\*" view.LeapUIApp

public class LeapUIApp extends Application {
    public static double ScreenWidth = 800;
    public static double ScreenHeight = 800;
    public static UIHand userHand;
    public static UIHand targetHand;
    public static AccuracyBar aBar;    // a bar to show the current accuracy of the user compared to the target
    public static SelectBar sBar;      // the buttons for selecting a target
    public static MoveBar mBar;        // the buttons for moving to next/prev a target
    public static Button testButton;   // a button to transition from free mode to training mode
    public static Text scoreText;      // displays the user's score at the end of a test
    public static Text timeText;       // displays the time a user took at the end of a test
    public static ControllerInterface control;        //the controller object
    public static Hand latestHand = null; // For recording target hands, disable in release version

    public static boolean AUTOMATIC_MODE = false; //developer mode is the one that shows the accuracy bar and the time.
    public Comparer comparer;
    public static Button loadButton;   // a button to load hand and show it using user hand
    public Stage window;
    public Scene scene, scene2;
    public static boolean leftHandSelected = true;

    public String userSpecifiedDirectory = "General"; //default directory to save data to


//	private Controller leapDevice; // XXX testing purposes only

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        window = stage;

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        ScreenWidth = bounds.getWidth();
        ScreenHeight = bounds.getHeight();
        stage.setMaximized(true);

        // The hand objects
        userHand = new UIHand_Simple(Color.GREEN.darker(), false);
        userHand.setVisible(false);
        targetHand = new UIHand_Simple(Color.DARKRED, true);
        targetHand.setVisible(false);
        // targetHand.setChildrenOpacity(0.5); // TODO once javafx implements 3D opacity, check that this works correctly before removing comment

        // The 3D camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(new Translate(0, -5, -50), new Rotate(-10, Rotate.X_AXIS));

        // The 3D display
        Group group3D = new Group();
        group3D.getChildren().add(camera);
        group3D.getChildren().add(userHand);
        group3D.getChildren().add(targetHand);
        SubScene sub3D = new SubScene(group3D, ScreenWidth, ScreenHeight, true, SceneAntialiasing.BALANCED); // "true" gives us a depth buffer
        sub3D.setFill(Color.LAVENDER);
        sub3D.setCamera(camera);

        // Set up 2D components
        aBar = new AccuracyBar(ScreenWidth / 4, ScreenHeight * 4 / 5, ScreenWidth / 2, 50);
        aBar.setVisible(false);
        sBar = new SelectBar(ScreenWidth / 4, ScreenHeight * 4 / 5, ScreenWidth / 2, 50);
        sBar.setVisible(false);
        mBar = new MoveBar(ScreenWidth / 4, ScreenHeight * 4 / 5, ScreenWidth / 2, 50);
        mBar.setVisible(false);

        testButton = new Button("Enter Test Mode") {
            @Override
            public void fire() {
                System.out.println("* entering test mode btn clicked");
                setVisible(false);
                userHand.setVisible(false);
                loadButton.setVisible(false);
                //makes a new thread, passing it a lambda function and then it calls start on that thread.
                new Thread(() -> control.enterTrainingMode()).start();
            }
        };
        testButton.setTranslateX(ScreenWidth * 4 / 5);
        testButton.setTranslateY(ScreenHeight * 4 / 5);
        testButton.setPrefHeight(50);
        testButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));

//        //load button, inspired from testButton
//        loadButton = new Button("Load Hand") {
//            @Override
//            public void fire() {
//                System.out.println("load hand button clicked");
//                Hand h = getHandFromString("targets/2017-06-12 12-13-58.hand");
//                userHand.setLoc(h);
//                userHand.setVisible(true);
//            }
//        };

        loadButton = new Button("Analyze Data");
        loadButton.setOnAction(e -> {
            System.out.println("load button clicked. going to scene2");
            window.setScene(scene2);
        });


        loadButton.setTranslateX(ScreenWidth * 1 / 5);
        loadButton.setTranslateY(ScreenHeight * 4 / 5);
        loadButton.setPrefHeight(50);
        loadButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));


        //going radio button approach
        ToggleGroup lfGroup = new ToggleGroup();
        RadioButton leftRadio = new RadioButton("Left Hand");
        leftRadio.setToggleGroup(lfGroup);
        leftRadio.setSelected(true);
        leftRadio.setTranslateX(ScreenWidth * 1 / 5);
        leftRadio.setTranslateY(ScreenHeight * 1 / 10);
        leftRadio.setOnAction(e -> {
            leftHandSelected = true;
//            System.out.println("left selected; leftHandSelected: " + leftHandSelected);
        });
        RadioButton rightRadio = new RadioButton("Right Hand");
        rightRadio.setToggleGroup(lfGroup);
        rightRadio.setTranslateX(ScreenWidth * 8 / 10);
        rightRadio.setTranslateY(ScreenHeight * 1 / 10);
        rightRadio.setOnAction(e -> {
            leftHandSelected = false;
//            System.out.println("right selected; leftHandSelected: " + leftHandSelected);
        });

        scoreText = new Text();
        scoreText.setFont(Font.font(STYLESHEET_MODENA, ScreenHeight / 4));
        scoreText.setY(ScreenHeight / 2);
        scoreText.setVisible(false);
        timeText = new Text();
        timeText.setFont(Font.font(STYLESHEET_MODENA, ScreenHeight / 16));
        timeText.setY(ScreenHeight * 5 / 8);
        timeText.setVisible(false);


        // The 2D overlay
        Group group2D = new Group(aBar, sBar, mBar, testButton, loadButton, scoreText, timeText, leftRadio, rightRadio);
        SubScene sub2D = new SubScene(group2D, ScreenWidth, ScreenHeight, false, SceneAntialiasing.BALANCED); // "false" because no depth in 2D
        Group root = new Group(sub3D, sub2D); // sub2D is second, as we want it overlaid, not underlaid
        scene = new Scene(root);


        LoadGesturesScene layout2 = new LoadGesturesScene(this);
        scene2 = layout2.scene;

        //create references for the 2 different controls
        Control ctrl1 = new Control();
        Control2 ctrl2 = new Control2();


        // TODO For recording target hands, disable in release version
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.G) {
                    try {
                        System.out.println("saving official gesture");
                        Frame f = latestHand.frame();

                        //for debugging
                        System.out.println("frame: \n" + f.toString());
                        FingerList fingersInFrame = f.fingers();
                        System.out.println("number of fingers: \n" + fingersInFrame.count());
                        System.out.println("extended fingers: \n" + fingersInFrame.extended().count());

                        //show alert box
                        InfoBox.display("Gesture Name", "Please name this gesture:");
                        System.out.println("name: " + InfoBox.name + " leftHand: " + InfoBox.leftHand);

                        if (InfoBox.name != null) {
                            SerializedTargetHand.Save3(f, InfoBox.name, InfoBox.leftHand);
                        } else {
                            System.out.println("aborting saving of gesture. no name was typed.");
                        }

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    try {
                        System.out.println("enter was pressed, saving hand.");
                        Frame f = (LoadGesturesScene.getHandFromString("targets/2015-05-05 08-17-01.hand")).frame(); //latestHand.frame();

                        //show alert box
                        SaveBox.display("Result and Comments", "Any comments:", userSpecifiedDirectory);
                        System.out.println("comments: " + SaveBox.comments + " passFail: " + SaveBox.passFail);

                        userSpecifiedDirectory = SaveBox.directory;
                        String dataOutputPath = "dataOutput/" + userSpecifiedDirectory + "/";

                        if (SaveBox.comments != null) {
                            SerializedTargetHand.Save4(f, dataOutputPath, SaveBox.comments, SaveBox.passFail);
                        } else {
                            SerializedTargetHand.Save4(f, dataOutputPath, "no comments", SaveBox.passFail);
                        }


                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (keyEvent.getCode().isDigitKey()) {
                    try {
                        if (keyEvent.getCode() == KeyCode.DIGIT0) {
                            System.out.println("0 KEY was pressed,gonna save into correct folder");
                            Frame f = latestHand.frame();
                            System.out.println("frame: \n" + f.toString());
                            //showImage();
                            FingerList fingersInFrame = f.fingers();
                            System.out.println("number of fingers: \n" + fingersInFrame.count());
                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
                            SerializedTargetHand.Save2(latestHand.frame(), "0", "typeA");
                        } else if (keyEvent.getCode() == KeyCode.DIGIT1) {
                            System.out.println("1 KEY was pressed,gonna save into correct folder");
                            Frame f = latestHand.frame();
                            System.out.println("frame: \n" + f.toString());
                            //showImage();
                            FingerList fingersInFrame = f.fingers();
                            System.out.println("number of fingers: \n" + fingersInFrame.count());
                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
                            SerializedTargetHand.Save2(latestHand.frame(), "1", "typeA");
                        } else if (keyEvent.getCode() == KeyCode.DIGIT2) {
                            System.out.println("2 KEY was pressed,gonna save into correct folder");
                            Frame f = latestHand.frame();
                            System.out.println("frame: \n" + f.toString());
                            //showImage();
                            FingerList fingersInFrame = f.fingers();
                            System.out.println("number of fingers: \n" + fingersInFrame.count());
                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
                            SerializedTargetHand.Save2(latestHand.frame(), "2", "typeA");
                        } else if (keyEvent.getCode() == KeyCode.DIGIT3) {
                            System.out.println("3 KEY was pressed,gonna save into correct folder");
                            Frame f = latestHand.frame();
                            System.out.println("frame: \n" + f.toString());
                            //showImage();
                            FingerList fingersInFrame = f.fingers();
                            System.out.println("number of fingers: \n" + fingersInFrame.count());
                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
                            SerializedTargetHand.Save2(latestHand.frame(), "3", "typeA");
                        } else if (keyEvent.getCode() == KeyCode.DIGIT4) {
                            System.out.println("4 KEY was pressed,gonna save into correct folder");
                            Frame f = latestHand.frame();
                            System.out.println("frame: \n" + f.toString());
                            //showImage();
                            FingerList fingersInFrame = f.fingers();
                            System.out.println("number of fingers: \n" + fingersInFrame.count());
                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
                            SerializedTargetHand.Save2(latestHand.frame(), "4", "typeA");
                        } else if (keyEvent.getCode() == KeyCode.DIGIT5) {
                            System.out.println("5 KEY was pressed,gonna save into correct folder");
                            Frame f = latestHand.frame();
                            System.out.println("frame: \n" + f.toString());
                            //showImage();
                            FingerList fingersInFrame = f.fingers();
                            System.out.println("number of fingers: \n" + fingersInFrame.count());
                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
                            SerializedTargetHand.Save2(latestHand.frame(), "5", "typeA");
                        } else if (keyEvent.getCode() == KeyCode.DIGIT6) {
                            System.out.println("6 KEY was pressed,gonna save into correct folder");
                            Frame f = latestHand.frame();
                            System.out.println("frame: \n" + f.toString());
                            //showImage();
                            FingerList fingersInFrame = f.fingers();
                            System.out.println("number of fingers: \n" + fingersInFrame.count());
                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
                            SerializedTargetHand.Save2(latestHand.frame(), "6", "typeA");
                        } else if (keyEvent.getCode() == KeyCode.DIGIT7) {
                            System.out.println("7 KEY was pressed,gonna save into correct folder");
                            Frame f = latestHand.frame();
                            System.out.println("frame: \n" + f.toString());
                            //showImage();
                            FingerList fingersInFrame = f.fingers();
                            System.out.println("number of fingers: \n" + fingersInFrame.count());
                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
                            SerializedTargetHand.Save2(latestHand.frame(), "7", "typeA");
                        } else if (keyEvent.getCode() == KeyCode.DIGIT8) {
                            System.out.println("8 KEY was pressed,gonna save into correct folder");
                            Frame f = latestHand.frame();
                            System.out.println("frame: \n" + f.toString());
                            //showImage();
                            FingerList fingersInFrame = f.fingers();
                            System.out.println("number of fingers: \n" + fingersInFrame.count());
                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
                            SerializedTargetHand.Save2(latestHand.frame(), "8", "typeA");
                        } else if (keyEvent.getCode() == KeyCode.DIGIT9) {
                            System.out.println("9 KEY was pressed,gonna save into correct folder");
                            Frame f = latestHand.frame();
                            System.out.println("frame: \n" + f.toString());
                            //showImage();
                            FingerList fingersInFrame = f.fingers();
                            System.out.println("number of fingers: \n" + fingersInFrame.count());
                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
                            SerializedTargetHand.Save2(latestHand.frame(), "9", "typeA");
                        } else {
                            System.out.println("hmm.. not sure which digit key was pressed");
                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (keyEvent.getCode() == KeyCode.D) {
                    try {
                        System.out.println("D was pressed, going into developer mode");
                        //try setting control dynamically
                        if (!AUTOMATIC_MODE) {
                            System.out.println("setting control to ctrl1 dynamically");
                            AUTOMATIC_MODE = true;
                            control = ctrl1;
                        } else {
                            System.out.println("setting control to ctrl2222 dynamically");
                            AUTOMATIC_MODE = false;
                            control = ctrl2;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (keyEvent.getCode() == KeyCode.M) {
                    new Thread(() -> {
                        try {
                            if (leftHandSelected) {
                                selectHand(SerializedTargetHand.getAllHands2("LeftGestures.txt"));
                            } else {
                                selectHand(SerializedTargetHand.getAllHands2("RightGestures.txt"));
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }).start();
                }
            }//END      public void handle(KeyEvent keyEvent) method
        });

        stage.setScene(scene);
        stage.show();

        //set control based on initial value of DEVELOPER_MODE
        if (AUTOMATIC_MODE) {
            System.out.println("Using control for automatic mode");
            control = ctrl1;
        } else {
            System.out.println("using control2 for manual mode");
            control = ctrl2;
        }


        //compare targetHand 0 against another one.
        String str1 = "targets/2015-05-05 08-17-01.hand";
        String str2 = "targets/2017-06-12 12-13-58.hand"; //should be very close to the 0th hand
        String str3 = "targets/2017-06-12 12-18-33.hand"; //palm facing downwards
        String str4 = "targets/2017-06-12 12-21-01.hand"; //palm facing downwards and fingers pointing to right
        String str5 = "targets/2017-06-12 12-23-19.hand"; //right hand palm, upwards
        String str6 = "targets/2017-06-12 12-30-56.hand"; //palm facing down again
        comparer = new Comparer();

        compareTwoHands(str1, str2);
        compareTwoHands(str1, str3);
        compareTwoHands(str1, str4);
        compareTwoHands(str1, str5);
        compareTwoHands(str1, str6);


        //scores:
        //(str1, str2) -> 0.8699791905046874
        //(str1, str3) -> 0.8804384491907629
        //(str1, str4) -> 0.15855860365038324
        //(str1, str5) -> 0.0
        //(str1, str6) -> 0.9003894774440483

        /*
        scores after wrist weight set to zero
        ************Score**********: 0.9515686165548964

        ************Score**********: 0.9367086427558178

        ************Score**********: 0.17667958692471275

        ************Score**********: 0.0

        ************Score**********: 0.944533897841223

         */

    }

//    public Hand getHandFromString(String s){
//        Hand h = null;
//        try{
//            h = SerializedTargetHand.readFromFile(s);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return h;
//    }


    public double compareTwoHands(String s1, String s2) {

        double score = 0.0;
        try {
            Hand h1 = SerializedTargetHand.readFromFile(s1);
            Hand h2 = SerializedTargetHand.readFromFile(s2);
            //display hands somehow.
            score = comparer.compare(h1, h2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("************Score**********: " + score + "\n");
        return score;
    }

    @Override
    public void stop() {
        // Any clean-up code goes here
        System.exit(0);
    }


    public static void startStaticTest(Hand h) {
        Platform.runLater(new StaticStartTask(h));
    }

    public static void endStaticTest(double finalscore, long finaltime, boolean success) {
        Platform.runLater(new StaticScoreTask(finalscore, finaltime, success));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Platform.runLater(new StaticEndTask());
    }

    public static void endStaticTest2() {
        System.out.println("endStaticTest2 called");
        Platform.runLater(new StaticEndTask());
    }

    public static void setUser(Hand hand, double accuracy) {
        Platform.runLater(new SetUserTask(hand, accuracy));
    }

    //this method returns a Hand object.
    public static Hand selectHand(ArrayList<Hand> arrayList) throws InterruptedException {
        Platform.runLater(() -> {
            userHand.setVisible(false);
            aBar.setVisible(false);//turn off the accuracy bar.
            sBar.setVisible(true);
        });
        Hand hand = sBar.select(arrayList); // Does not return until the user has selected a target
        mBar.setTargets(arrayList);
        Platform.runLater(() -> {
            sBar.setVisible(false);
            if (AUTOMATIC_MODE) {
                aBar.setVisible(true);
                mBar.setVisible(false);
            } else {
                aBar.setVisible(false);
                mBar.setVisible(true);
            }

        });
        return hand;
    }

    // TODO unsupported
    public static void startDynamicTest(Hand h) {
        throw new UnsupportedOperationException();
    }

    public static void endDynamicTest() {
        throw new UnsupportedOperationException();
    }


    // TASKS
    private static class SetUserTask extends Task<Void> {
        private Hand hand;
        private double accuracy;

        public SetUserTask(Hand h, double x) {
            hand = h;
            accuracy = x;
        }

        @Override
        protected Void call() throws Exception {
            if (hand == null) {
                userHand.setVisible(false);
                aBar.setAccuracy(0);
            } else {
                latestHand = hand; // TODO TESTING
                userHand.setLoc(hand);
                userHand.setVisible(true);
                aBar.setAccuracy(accuracy);
            }
            return null;
        }
    }

    private static class StaticStartTask extends Task<Void> {
        private Hand hand;

        public StaticStartTask(Hand h) {
            hand = h;
        }

        @Override
        protected Void call() throws Exception {
            if (hand != null) {
                targetHand.setLoc(hand);
                targetHand.setVisible(true);
            }
            return null;
        }
    }

    private static class StaticScoreTask extends Task<Void> {
        private int score;
        private float seconds;
        private boolean success;

        public StaticScoreTask(double finalscore, long finaltime, boolean testsuccess) {
            score = (int) (finalscore * 100);
            seconds = finaltime / 1000000f;
            success = testsuccess;
        }

        @Override
        protected Void call() throws Exception {
            targetHand.setVisible(false);
            userHand.setVisible(false);

            scoreText.setText(String.format("%d%%", score));
            scoreText.setX((ScreenWidth - scoreText.getBoundsInLocal().getWidth()) / 2);
            scoreText.setVisible(true);

            timeText.setText(success ? String.format("Time taken: %.3f seconds", seconds) : "You timed out");
            timeText.setX((ScreenWidth - timeText.getBoundsInLocal().getWidth()) / 2);
            timeText.setVisible(true);

            return null;
        }

    }

    private static class StaticEndTask extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            targetHand.setVisible(false);
            scoreText.setVisible(false);
            timeText.setVisible(false);
            aBar.setVisible(false);
            testButton.setVisible(true);
            loadButton.setCancelButton(true);
            return null;
        }

    }

    private class SelectBar extends Group {
        private Boolean confirmed;
        private ArrayList<Hand> targets = null;
        private int index;
        private Button prevButton;
        private Button confirmButton;
        private Button nextButton;

        public SelectBar(double x, double y, double width, double height) {
            super();
            confirmed = false;
            index = 0;

            prevButton = new Button("\u25c0 Previous") {
                @Override
                public void fire() {
                    prevHand();
                }
            };
            prevButton.setTranslateX(x);
            prevButton.setTranslateY(y);
            prevButton.setPrefSize(width / 4, height);
            prevButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));


            //things are starting to click. ^^
            confirmButton = new Button("Begin Test") {
                @Override
                public void fire() {
                    confirm();
                }
            };
            confirmButton.setTranslateX(x + width / 4);
            confirmButton.setTranslateY(y);
            confirmButton.setPrefSize(width / 2, height);
            confirmButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 16));

            nextButton = new Button("Next \u25b6") {
                @Override
                public void fire() {
                    nextHand();
                }
            };
            nextButton.setTranslateX(x + width * 3 / 4);
            nextButton.setTranslateY(y);
            nextButton.setPrefSize(width / 4, height);
            nextButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));

            getChildren().addAll(prevButton, confirmButton, nextButton);
        }


        //this is a synchronized method... nb
        public synchronized Hand select(ArrayList<Hand> arrayList) throws InterruptedException {
            confirmed = false;
            index = 0; //set to zero. does not change in this method.
            targets = arrayList;
            if (targets != null && targets.size() > 0) {
                //lambda annonymous function defined here. takes no arguments, and does the code int the brackets
                Platform.runLater(() -> {
                    targetHand.setLoc(targets.get(index)); //takes the 0th hand and assigns it using setLoc
                    targetHand.setVisible(true);
                });
                // **set local variables**
                //waits until user clicks a button to say "begin test" then we can be sure this hand has been selected.
                //still a little confused about the Platform.runLater but maybe debugging will help
                while (!confirmed) {
                    wait();
                }
                //the wait is over, selection was confirmed. can return the selected hand
                return targets.get(index);

            }
            //returning null cuz the arraylist passed in was null or empty
            else {
                return null;
            }
        }

        synchronized void confirm() {
            confirmed = true;
            targetHand.setVisible(false);
            notifyAll();
        }

        synchronized void prevHand() {
            if (targets != null && targets.size() > 0) {
                index = (--index + targets.size()) % targets.size();
                System.out.println(index);
                targetHand.setLoc(targets.get(index));
            }
        }

        synchronized void nextHand() {
            if (targets != null && targets.size() > 0) {
                index = ++index % targets.size();
                targetHand.setLoc(targets.get(index));
            }
        }

		/* also have keyboard listeners attached to scene to turn KeyCode.LEFT and .RIGHT into prevButton.click and nextButton.click respectively
         *
		 *     for Java wait/notify, wait() and notifyAll() must both be in synchronized blocks
		 *     
		 *     synchronized(sBar) { doStuff(); sBar.wait(); continueStuff(); }
		 *     synchronized(sBar) { doExtra(); sBar.notify(); }
		 *     
		 *     not sure if you need to do **sBar**.wait/notify(), may work to just do wait/notify. Probably not tho
		 */
    }


    // XXX testing only
//	private class LeapTimer extends AnimationTimer {
//		@Override
//		public void handle(long arg0) {
//			Frame currentFrame = leapDevice.frame();
//			setUser(currentFrame.hands().get(0), Math.random());
//			if (currentFrame.hands().count() > 1) startStaticTest(currentFrame.hands().get(1));
//			else endStaticTest();
//		}
//	 }


    private class MoveBar extends Group {
        //        private Boolean confirmed;
        private ArrayList<Hand> targets = null;
        private int index;
        private Button prevButton;
        private Button saveButton; //clicking on save button should allow an alert box to pop up. maybe allowing for message to be typed
        private Button nextButton;
        private Button endButton; //click on this to end the testing mode

        public MoveBar(double x, double y, double width, double height) {
            super();
//            confirmed = false;
            index = 0;

            prevButton = new Button("\u25c0 Previous") {
                @Override
                public void fire() {
                    prevHand();
                }
            };
            prevButton.setTranslateX(x);
            prevButton.setTranslateY(y);
            prevButton.setPrefSize(width / 4, height);
            prevButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));


            //things are starting to click. ^^
            saveButton = new Button("Save") {
                @Override
                public void fire() {
                    save();
                }
            };
            saveButton.setTranslateX(x + width / 4);
            saveButton.setTranslateY(y);
            saveButton.setPrefSize(width / 4, height);
            saveButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 16));

            endButton = new Button("End Testing") {
                @Override
                public void fire() {
                    endTesting();
                }
            };
            endButton.setTranslateX(x + width * 2 / 4);
            endButton.setTranslateY(y);
            endButton.setPrefSize(width / 4, height);
            endButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));

            nextButton = new Button("Next \u25b6") {
                @Override
                public void fire() {
                    nextHand();
                }
            };
            nextButton.setTranslateX(x + width * 3 / 4);
            nextButton.setTranslateY(y);
            nextButton.setPrefSize(width / 4, height);
            nextButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));

            getChildren().addAll(prevButton, saveButton, endButton, nextButton);
        }


        public void setTargets(ArrayList<Hand> targetHands) {
            targets = targetHands;
        }

        //probably dont need this method in moveBar
//        public synchronized Hand select(ArrayList<Hand> arrayList) throws InterruptedException {
//            confirmed = false;
//            index = 0; //set to zero. does not change in this method.
//            targets = arrayList;
//            if (targets != null && targets.size() > 0) {
//                //lambda annonymous function defined here. takes no arguments, and does the code int the brackets
//                Platform.runLater(() -> {
//                    targetHand.setLoc(targets.get(index)); //takes the 0th hand and assigns it using setLoc
//                    targetHand.setVisible(true);
//                });
//                // **set local variables**
//                //waits until user clicks a button to say "begin test" then we can be sure this hand has been selected.
//                //still a little confused about the Platform.runLater but maybe debugging will help
//                while (!confirmed) {
//                    wait();
//                }
//                //the wait is over, selection was confirmed. can return the selected hand
//                return targets.get(index);
//
//            }
//            //returning null cuz the arraylist passed in was null or empty
//            else {
//                return null;
//            }
//        }

        synchronized void save() {
            try {
                //save data
                System.out.println("saving data ************");
                Frame f = latestHand.frame();
                System.out.println("frame: \n" + f.toString());
                //showImage();
                FingerList fingersInFrame = f.fingers();
                System.out.println("number of fingers: \n" + fingersInFrame.count());
                System.out.println("extended fingers: \n" + fingersInFrame.extended().count());

                //create a new kind of save function that is based on save3 and allows u to save into the folder called joe. u can type and make
                //folders as u go. as long as they have directories to be saved in. and the saving process should note the gesture type also. so
                //when it needs to be compared it can be appropriately compared. maybe that can be saved in files, serialized data?


                SerializedTargetHand.Save2(latestHand.frame(), "General", "typeX");
                System.out.println("saving data END ************");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        synchronized void endTesting() {
            System.out.println("ending test mode");
            control.staticEnd();
            this.setVisible(false);
            System.out.println("ending test mode END");
        }

        synchronized void prevHand() {
            if (targets != null && targets.size() > 0) {
                index = (--index + targets.size()) % targets.size();
                System.out.println(index);
                targetHand.setLoc(targets.get(index));
            }
        }

        synchronized void nextHand() {
            if (targets != null && targets.size() > 0) {
                index = ++index % targets.size();
                targetHand.setLoc(targets.get(index));
            }
        }

		/* also have keyboard listeners attached to scene to turn KeyCode.LEFT and .RIGHT into prevButton.click and nextButton.click respectively
         *
		 *     for Java wait/notify, wait() and notifyAll() must both be in synchronized blocks
		 *
		 *     synchronized(sBar) { doStuff(); sBar.wait(); continueStuff(); }
		 *     synchronized(sBar) { doExtra(); sBar.notify(); }
		 *
		 *     not sure if you need to do **sBar**.wait/notify(), may work to just do wait/notify. Probably not tho
		 */
    }


}