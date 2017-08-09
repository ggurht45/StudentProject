// TODO maybe polygon plot

package view;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.jfoenix.controls.JFXButton;
import controller.Comparer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.SerializedTargetHand;
import view.analyze.*;
import view.anatomy.UIHand;
//import view.anatomy.UIHand_Full;
import view.anatomy.UIHand_Simple;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.*;

import controller.Control;
import controller.Control2;
import controller.ControllerInterface;

// XXX to run: java -Djava.library.path="D:\Software\Leap SDK\LeapDeveloperKit_2.2.2+24469_win\LeapSDK\lib\x64" -classpath ".;D:\Software\Leap SDK\LeapDeveloperKit_2.2.2+24469_win\LeapSDK\lib\*" view.LeapUIApp

public class LeapUIApp extends Application {
    public static String ALL_USERS_FILE = "dataOutput/AllUsers.csv";
    public static int currentGestureNumber = 1;
    public static boolean dataCollectionEnabled;
    public static Rotate rotateAroundY;
    public static ControllerForAnalyzeHands scene2Controller;
    public static HashMap<Hand, String> handToGestureType;
    public static String DEFAULT_FOLDER = "TestData";
    public static String userSpecifiedDirectory = DEFAULT_FOLDER; //updates as user names folders in savebox
    public static String ProjectDirectoryPath = System.getProperty("user.dir");
    public static String LeftGesturesFile = "dataOutput/LeftGestures.txt";
    public static String RightGesturesFile = "dataOutput/RightGestures.txt";
    public static String TestsFile = "dataOutput/AllTest.txt";
    public static String TargetHandsFile = "dataOutput/TargetHands.txt";
    public static String TargetsPath = "dataOutput/targets/";
    public static String Targets2Path = "dataOutput/targets2/";
    public static String DataOutputPath = "dataOutput/";

    public static double ScreenWidth = 800;
    public static double ScreenHeight = 800;
    public static UIHand userHand;
    public static UIHand targetHand;
    public static AccuracyBar aBar;    // a bar to show the current accuracy of the user compared to the target
    public static SelectBar sBar;      // the buttons for selecting a target
    //    public static MoveBar mBar;        // the buttons for moving to next/prev a target
    public static Button testButton;   // a button to transition from free mode to training mode
    public static Text scoreText;      // displays the user's score at the end of a test
    public static Text timeText;       // displays the time a user took at the end of a test
    public static ControllerInterface control;        //the controller object
    public static Hand latestHand = null; // For recording target hands, disable in release version

    public static boolean AUTOMATIC_MODE = false; //developer mode is the one that shows the accuracy bar and the time.
    public Comparer comparer;
    public static Button scene2Button;   // a button to load hand and show it using user hand
    public Stage primaryStage;
    public Scene scene, scene2;
    public static boolean leftHandSelected = true;


//	private Controller leapDevice; // XXX testing purposes only

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        ScreenWidth = bounds.getWidth();
        ScreenHeight = bounds.getHeight();
        stage.setMaximized(true);

        // The hand objects
        userHand = new UIHand_Simple(Color.BLUE.darker(), false);
        userHand.setVisible(false);
        targetHand = new UIHand_Simple(Color.GREEN.darker(), true);
        targetHand.setVisible(false);
        // targetHand.setChildrenOpacity(0.5); // TODO once javafx implements 3D opacity, check that this works correctly before removing comment

        // The 3D camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        rotateAroundY = new Rotate(0, Rotate.Y_AXIS);
        camera.getTransforms().addAll(rotateAroundY, new Translate(0, -5, -50), new Rotate(-10, Rotate.X_AXIS));

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
//        mBar = new MoveBar(ScreenWidth / 4, ScreenHeight * 4 / 5, ScreenWidth / 2, 50);
//        mBar.setVisible(false);

        testButton = new Button("Enter Test Mode") {
            @Override
            public void fire() {
//                System.out.println("* entering test mode btn clicked");
                SelectUserBox.display();
                System.out.println("successfully closed selectBox: " + SelectUserBox.successfulClose + " selectedUser: " + SelectUserBox.selectedUser);
                if (SelectUserBox.successfulClose) {
                    //do as before
                    DEFAULT_FOLDER = SelectUserBox.selectedUser.getId();
                    dataCollectionEnabled = true;
                    setVisible(false);
                    userHand.setVisible(false);
                    scene2Button.setVisible(false);
                    //makes a new thread, passing it a lambda function and then it calls start on that thread.
                    new Thread(() -> control.enterTrainingMode()).start();
                } else {
                    System.out.println("aborted going to test mode ");
                }


            }
        };
        testButton.setStyle("-fx-background-color: #669900; -jfx-button-type: RAISED");
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

        scene2Button = new JFXButton("Analyze Data");
        scene2Button.setStyle("-fx-background-color: #669900; -jfx-button-type: RAISED");
//        scene2Button.
        scene2Button.setOnAction(e -> {
//            System.out.println("going to analyzedata scene");
            scene2Controller.initializeTableWithData();
            primaryStage.setScene(scene2);
        });


        scene2Button.setTranslateX(ScreenWidth * 1 / 5);
        scene2Button.setTranslateY(ScreenHeight * 4 / 5);
        scene2Button.setPrefHeight(50);
        scene2Button.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));


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
        Group group2D = new Group(aBar, sBar, testButton, scene2Button, scoreText, timeText, leftRadio, rightRadio);
        SubScene sub2D = new SubScene(group2D, ScreenWidth, ScreenHeight, false, SceneAntialiasing.BALANCED); // "false" because no depth in 2D
        Group root = new Group(sub3D, sub2D); // sub2D is second, as we want it overlaid, not underlaid
        scene = new Scene(root);


//        LoadGesturesScene layout2 = new LoadGesturesScene(this);
//        scene2 = layout2.getScene();

//        LoadGesturesScene2 rotateDemoLayout = new LoadGesturesScene2(this);
////        scene2 = rotateDemoLayout.getScene();

        try {
            //this uses the static load method. which is not what we want if we ever once in our life time want to access the
            //controller associated with this fxml template file.
//            Parent root = FXMLLoader.load(getClass().getResource("/view/analyze/analyzeHandsScene.fxml")); //Note the way to find resources manually.

            //create an instance of the fxmlloader, this instance will be used to get controller objects for the fxml templates
            //note, this is a special kind of loader that has a Specific kind fxml file attached to it
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("analyzeHandsScene.fxml"));
            //note, this loader uses the "instance" load method, rather than the static load method.
            Parent rootNode = fxmlLoader.load();
            scene2 = new Scene(rootNode, ScreenWidth, ScreenHeight);

            //get the controller file for the fxml file attached to the loader
            scene2Controller = (ControllerForAnalyzeHands) fxmlLoader.getController();
            scene2Controller.setMainApp(this);

        } catch (Exception e) {
            e.printStackTrace();
        }


        //create references for the 2 different controls
        Control ctrl1 = new Control();
        Control2 ctrl2 = new Control2();


        // TODO For recording target hands, disable in release version
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.G) {
                    try {
//                        System.out.println("saving official gesture");
                        Frame f = latestHand.frame();

                        //for debugging
//                        System.out.println("frame: \n" + f.toString());
                        FingerList fingersInFrame = f.fingers();
//                        System.out.println("number of fingers: \n" + fingersInFrame.count());
//                        System.out.println("extended fingers: \n" + fingersInFrame.extended().count());

                        //show alert box
                        InfoBox.display("Gesture Name", "Please name this gesture:");
//                        System.out.println("name: " + InfoBox.name + " leftHand: " + InfoBox.leftHand);

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
                if (keyEvent.getCode() == KeyCode.LEFT) {
                    sBar.prevHand();
                }
                if (keyEvent.getCode() == KeyCode.RIGHT) {
                    sBar.nextHand();
                }
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    saveHandDataOfficial();
                }
//                if (keyEvent.getCode().isDigitKey()) {
//                    try {
//                        if (keyEvent.getCode() == KeyCode.DIGIT0) {
//                            System.out.println("0 KEY was pressed,gonna save into correct folder");
//                            Frame f = latestHand.frame();
//                            System.out.println("frame: \n" + f.toString());
//                            //showImage();
//                            FingerList fingersInFrame = f.fingers();
//                            System.out.println("number of fingers: \n" + fingersInFrame.count());
//                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
//                            SerializedTargetHand.Save2(latestHand.frame(), "0", "typeA");
//                        } else if (keyEvent.getCode() == KeyCode.DIGIT1) {
//                            System.out.println("1 KEY was pressed,gonna save into correct folder");
//                            Frame f = latestHand.frame();
//                            System.out.println("frame: \n" + f.toString());
//                            //showImage();
//                            FingerList fingersInFrame = f.fingers();
//                            System.out.println("number of fingers: \n" + fingersInFrame.count());
//                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
//                            SerializedTargetHand.Save2(latestHand.frame(), "1", "typeA");
//                        } else if (keyEvent.getCode() == KeyCode.DIGIT2) {
//                            System.out.println("2 KEY was pressed,gonna save into correct folder");
//                            Frame f = latestHand.frame();
//                            System.out.println("frame: \n" + f.toString());
//                            //showImage();
//                            FingerList fingersInFrame = f.fingers();
//                            System.out.println("number of fingers: \n" + fingersInFrame.count());
//                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
//                            SerializedTargetHand.Save2(latestHand.frame(), "2", "typeA");
//                        } else if (keyEvent.getCode() == KeyCode.DIGIT3) {
//                            System.out.println("3 KEY was pressed,gonna save into correct folder");
//                            Frame f = latestHand.frame();
//                            System.out.println("frame: \n" + f.toString());
//                            //showImage();
//                            FingerList fingersInFrame = f.fingers();
//                            System.out.println("number of fingers: \n" + fingersInFrame.count());
//                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
//                            SerializedTargetHand.Save2(latestHand.frame(), "3", "typeA");
//                        } else if (keyEvent.getCode() == KeyCode.DIGIT4) {
//                            System.out.println("4 KEY was pressed,gonna save into correct folder");
//                            Frame f = latestHand.frame();
//                            System.out.println("frame: \n" + f.toString());
//                            //showImage();
//                            FingerList fingersInFrame = f.fingers();
//                            System.out.println("number of fingers: \n" + fingersInFrame.count());
//                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
//                            SerializedTargetHand.Save2(latestHand.frame(), "4", "typeA");
//                        } else if (keyEvent.getCode() == KeyCode.DIGIT5) {
//                            System.out.println("5 KEY was pressed,gonna save into correct folder");
//                            Frame f = latestHand.frame();
//                            System.out.println("frame: \n" + f.toString());
//                            //showImage();
//                            FingerList fingersInFrame = f.fingers();
//                            System.out.println("number of fingers: \n" + fingersInFrame.count());
//                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
//                            SerializedTargetHand.Save2(latestHand.frame(), "5", "typeA");
//                        } else if (keyEvent.getCode() == KeyCode.DIGIT6) {
//                            System.out.println("6 KEY was pressed,gonna save into correct folder");
//                            Frame f = latestHand.frame();
//                            System.out.println("frame: \n" + f.toString());
//                            //showImage();
//                            FingerList fingersInFrame = f.fingers();
//                            System.out.println("number of fingers: \n" + fingersInFrame.count());
//                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
//                            SerializedTargetHand.Save2(latestHand.frame(), "6", "typeA");
//                        } else if (keyEvent.getCode() == KeyCode.DIGIT7) {
//                            System.out.println("7 KEY was pressed,gonna save into correct folder");
//                            Frame f = latestHand.frame();
//                            System.out.println("frame: \n" + f.toString());
//                            //showImage();
//                            FingerList fingersInFrame = f.fingers();
//                            System.out.println("number of fingers: \n" + fingersInFrame.count());
//                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
//                            SerializedTargetHand.Save2(latestHand.frame(), "7", "typeA");
//                        } else if (keyEvent.getCode() == KeyCode.DIGIT8) {
//                            System.out.println("8 KEY was pressed,gonna save into correct folder");
//                            Frame f = latestHand.frame();
//                            System.out.println("frame: \n" + f.toString());
//                            //showImage();
//                            FingerList fingersInFrame = f.fingers();
//                            System.out.println("number of fingers: \n" + fingersInFrame.count());
//                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
//                            SerializedTargetHand.Save2(latestHand.frame(), "8", "typeA");
//                        } else if (keyEvent.getCode() == KeyCode.DIGIT9) {
//                            System.out.println("9 KEY was pressed,gonna save into correct folder");
//                            Frame f = latestHand.frame();
//                            System.out.println("frame: \n" + f.toString());
//                            //showImage();
//                            FingerList fingersInFrame = f.fingers();
//                            System.out.println("number of fingers: \n" + fingersInFrame.count());
//                            System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
//                            SerializedTargetHand.Save2(latestHand.frame(), "9", "typeA");
//                        } else {
//                            System.out.println("hmm.. not sure which digit key was pressed");
//                        }
//
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//                if (keyEvent.getCode() == KeyCode.D) {
//                    try {
//                        System.out.println("D was pressed, going into developer mode");
//                        //try setting control dynamically
//                        if (!AUTOMATIC_MODE) {
//                            System.out.println("setting control to ctrl1 dynamically");
//                            AUTOMATIC_MODE = true;
//                            control = ctrl1;
//                        } else {
//                            System.out.println("setting control to ctrl2222 dynamically");
//                            AUTOMATIC_MODE = false;
//                            control = ctrl2;
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (keyEvent.getCode() == KeyCode.M) {
//                    new Thread(() -> {
//                        try {
//                            if (leftHandSelected) {
//                                selectHand(SerializedTargetHand.getAllHands2(LeftGesturesFile));
//                            } else {
//                                selectHand(SerializedTargetHand.getAllHands2(RightGesturesFile));
//                            }
//                        } catch (Exception e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }).start();
//                }
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
        String str1 = LeapUIApp.TargetsPath + "2015-05-05 08-17-01.hand";
        String str2 = LeapUIApp.TargetsPath + "2017-06-12 12-13-58.hand"; //should be very close to the 0th hand
        String str3 = LeapUIApp.TargetsPath + "2017-06-12 12-18-33.hand"; //palm facing downwards
        String str4 = LeapUIApp.TargetsPath + "2017-06-12 12-21-01.hand"; //palm facing downwards and fingers pointing to right
        String str5 = LeapUIApp.TargetsPath + "2017-06-12 12-23-19.hand"; //right hand palm, upwards
        String str6 = LeapUIApp.TargetsPath + "2017-06-12 12-30-56.hand"; //palm facing down again
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


    public static void saveHandDataOfficial() {
        try {
//            System.out.println("enter was pressed, saving hand, dataCollectionEnabled: " + dataCollectionEnabled);
            if (dataCollectionEnabled) {

                //following line is for TESTING. dont forget to uncomment it later
                Frame f = (SerializedTargetHand.getHandFromString(LeapUIApp.DataOutputPath + "General/defaultTestingHand.hand")).frame();
//                Frame f = latestHand.frame();

                //name the gesture appropriately; format: gesture2Left
                String gestureName = leftHandSelected ? "gesture" + currentGestureNumber + "Left" : "gesture" + currentGestureNumber + "Right";

                //show alert box
                SaveBox.display(gestureName);//"Result and Comments", "Any comments:", userSpecifiedDirectory);
//            System.out.println("comments: " + SaveBox.comments + " passFail: " + SaveBox.passFail);

                System.out.println("defaultFolderset(savedataoffical): " + DEFAULT_FOLDER);
                userSpecifiedDirectory = DEFAULT_FOLDER;//SaveBox.directory;
                String dataOutputPath = "dataOutput/" + userSpecifiedDirectory + "/";

                if (SaveBox.saved) {
                    SerializedTargetHand.Save4(f, gestureName, dataOutputPath, SaveBox.comments, SaveBox.passFail);
                } else {
                    System.out.println("INFO... NoT saving since save box was not saved or closed properly");
                }


            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


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

//        System.out.println("************Score**********: " + score + "\n");
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
//        System.out.println("endStaticTest2 called");
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
//        mBar.setTargets(arrayList);   //todo clean up this later. i removed movebar. still need to clean up though
        Platform.runLater(() -> {
            sBar.setVisible(false);
            if (AUTOMATIC_MODE) {
                aBar.setVisible(true);
//                mBar.setVisible(false);
            } else {
                aBar.setVisible(false);
//                mBar.setVisible(true);
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
            scene2Button.setVisible(true);
            return null;
        }

    }

    private class SelectBar extends Group {
        private Boolean confirmed;
        private ArrayList<Hand> targets = null;
        private int index;
        private Button prevButton;
        private Button saveButton;
        private Button endButton;
        private Button confirmButton;
        private Button nextButton;
        private Button rotateButton;
        private Timeline timeline;

        public SelectBar(double x, double y, double width, double height) {
            super();
            confirmed = false;
            index = 0;
            currentGestureNumber = index + 1;
            double tmpVar = 4.3;

            //set up rotation timeline
            Timeline timeline = new Timeline(
                    new KeyFrame(
                            Duration.seconds(0),
                            new KeyValue(rotateAroundY.angleProperty(), 0)
                    ),
                    new KeyFrame(
                            Duration.seconds(7),
                            new KeyValue(rotateAroundY.angleProperty(), -360)
                    )
            );
            timeline.setCycleCount(1);


            timeline.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String time = sdf.format(cal.getTime());
                    System.out.println("animation finished: " + time);
                    userHand.setVisible(true);
                }
            });


            prevButton = new Button("\u25c0 Previous") {
                @Override
                public void fire() {
                    System.out.println("previous button clicked");
                    prevHand();
                }
            };
            prevButton.setStyle("-fx-background-color: #669900; -jfx-button-type: RAISED");
            prevButton.setTranslateX(x);
            prevButton.setTranslateY(y);
            prevButton.setPrefSize(width / tmpVar, height);
            prevButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));


            //things are starting to click. ^^
            //todo this save button needs to do the same job as enter
            saveButton = new Button("Save") {
                @Override
                public void fire() {
                    save();
                }
            };
            saveButton.setStyle("-fx-background-color: #669900; -jfx-button-type: RAISED");
            saveButton.setTranslateX(x + width / 4);
            saveButton.setTranslateY(y);
            saveButton.setPrefSize(width / tmpVar, height);
            saveButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 16));

            endButton = new Button("End Testing") {
                @Override
                public void fire() {
                    endTesting();
                }
            };
            endButton.setStyle("-fx-background-color: #669900; -jfx-button-type: RAISED");
            endButton.setTranslateX(x + width * 2 / 4);
            endButton.setTranslateY(y);
            endButton.setPrefSize(width / tmpVar, height);
            endButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));

            nextButton = new Button("Next \u25b6") {
                @Override
                public void fire() {
                    System.out.println("going to next hand");
                    nextHand();
                }
            };
            nextButton.setStyle("-fx-background-color: #669900; -jfx-button-type: RAISED");
            nextButton.setTranslateX(x + width * 3 / 4);
            nextButton.setTranslateY(y);
            nextButton.setPrefSize(width / tmpVar, height);
            nextButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));

            //rotate button
            rotateButton = new Button("Rotate") {
                @Override
                public void fire() {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String time = sdf.format(cal.getTime());

                    System.out.println("animation started: " + time);

                    userHand.setVisible(false);
//                    System.out.println("rotate button clicked");
                    //play time line
                    timeline.play();
//                    userHand.setVisible(true);
                }
            };
            rotateButton.setStyle("-fx-background-color: #669900; -jfx-button-type: RAISED");
            rotateButton.setTranslateX(x + (width * 1 / 2) - (width / 8)); //center the button
            rotateButton.setTranslateY(y - (height + 15));
            rotateButton.setPrefSize(width / tmpVar, height);
            rotateButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));

            getChildren().addAll(prevButton, saveButton, endButton, nextButton, rotateButton);
        }


        synchronized void save() {
            //todo, i think this should be wired to pressing enter.
            try {
                //save data
                System.out.println("saving data 3432flsk31s ************");
                saveHandDataOfficial();
//                Frame f = latestHand.frame();
//                System.out.println("frame: \n" + f.toString());
//                //showImage();
//                FingerList fingersInFrame = f.fingers();
//                System.out.println("number of fingers: \n" + fingersInFrame.count());
//                System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
//
//                //create a new kind of save function that is based on save3 and allows u to save into the folder called joe. u can type and make
//                //folders as u go. as long as they have directories to be saved in. and the saving process should note the gesture type also. so
//                //when it needs to be compared it can be appropriately compared. maybe that can be saved in files, serialized data?
//
//
//                SerializedTargetHand.Save2(f, "General", "typeX");
                System.out.println("saving data END ************");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        synchronized void endTesting() {
//            System.out.println("ending test, going back to main page 232391ksd");
            dataCollectionEnabled = false;
            currentGestureNumber = 1;
            control.staticEnd();
            this.setVisible(false);
//            System.out.println("ending test mode END");
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
                //todo this is where the gestureType should be updated
                Hand selectedHand = targets.get(index);
//                System.out.println("selected hand, the corresponding gestureType is: " + handToGestureType.get(selectedHand));
                return targets.get(index);

            }
            //returning null cuz the arraylist passed in was null or empty
            else {
                System.out.println("ERROR, the arraylist passed into this select hand method was empty or null. ");
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
                currentGestureNumber = index + 1;
//                System.out.println(index);
                System.out.println("new index of (prev) hand in Array: " + index);
                targetHand.setLoc(targets.get(index));
            }
        }

        synchronized void nextHand() {
            if (targets != null && targets.size() > 0) {
                index = ++index % targets.size();
                currentGestureNumber = index + 1;
                System.out.println("new index of (next) hand in Array: " + index);
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

//
//    private class MoveBar extends Group {
//        //        private Boolean confirmed;
//        private ArrayList<Hand> targets = null;
//        private int index;
//        private Button prevButton;
//        private Button saveButton; //clicking on save button should allow an alert box to pop up. maybe allowing for message to be typed
//        private Button nextButton;
//        private Button endButton; //click on this to end the testing mode
//
//        public MoveBar(double x, double y, double width, double height) {
//            super();
////            confirmed = false;
//            index = 0;
//
//            prevButton = new Button("\u25c0 Previous") {
//                @Override
//                public void fire() {
//                    prevHand();
//                }
//            };
//            prevButton.setTranslateX(x);
//            prevButton.setTranslateY(y);
//            prevButton.setPrefSize(width / 4, height);
//            prevButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));
//
//
////            //things are starting to click. ^^
////            //todo this save button needs to do the same job as enter
////            saveButton = new Button("Save") {
////                @Override
////                public void fire() {
////                    save();
////                }
////            };
////            saveButton.setTranslateX(x + width / 4);
////            saveButton.setTranslateY(y);
////            saveButton.setPrefSize(width / 4, height);
////            saveButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 16));
////
////            endButton = new Button("End Testing") {
////                @Override
////                public void fire() {
////                    endTesting();
////                }
////            };
////            endButton.setTranslateX(x + width * 2 / 4);
////            endButton.setTranslateY(y);
////            endButton.setPrefSize(width / 4, height);
////            endButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));
//
//            nextButton = new Button("Next \u25b6") {
//                @Override
//                public void fire() {
//                    nextHand();
//                }
//            };
//            nextButton.setTranslateX(x + width * 3 / 4);
//            nextButton.setTranslateY(y);
//            nextButton.setPrefSize(width / 4, height);
//            nextButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));
//
//            getChildren().addAll(prevButton, nextButton);
//        }
//
//
//        public void setTargets(ArrayList<Hand> targetHands) {
//            targets = targetHands;
//        }
//
//        //probably dont need this method in moveBar
////        public synchronized Hand select(ArrayList<Hand> arrayList) throws InterruptedException {
////            confirmed = false;
////            index = 0; //set to zero. does not change in this method.
////            targets = arrayList;
////            if (targets != null && targets.size() > 0) {
////                //lambda annonymous function defined here. takes no arguments, and does the code int the brackets
////                Platform.runLater(() -> {
////                    targetHand.setLoc(targets.get(index)); //takes the 0th hand and assigns it using setLoc
////                    targetHand.setVisible(true);
////                });
////                // **set local variables**
////                //waits until user clicks a button to say "begin test" then we can be sure this hand has been selected.
////                //still a little confused about the Platform.runLater but maybe debugging will help
////                while (!confirmed) {
////                    wait();
////                }
////                //the wait is over, selection was confirmed. can return the selected hand
////                return targets.get(index);
////
////            }
////            //returning null cuz the arraylist passed in was null or empty
////            else {
////                return null;
////            }
////        }
////
////        synchronized void save() {
////            //todo, i think this should be wired to pressing enter.
////            try {
////                //save data
////                System.out.println("saving data 3sk3232fsadf ************");
////                Frame f = latestHand.frame();
////                System.out.println("frame: \n" + f.toString());
////                //showImage();
////                FingerList fingersInFrame = f.fingers();
////                System.out.println("number of fingers: \n" + fingersInFrame.count());
////                System.out.println("extended fingers: \n" + fingersInFrame.extended().count());
////
////                //create a new kind of save function that is based on save3 and allows u to save into the folder called joe. u can type and make
////                //folders as u go. as long as they have directories to be saved in. and the saving process should note the gesture type also. so
////                //when it needs to be compared it can be appropriately compared. maybe that can be saved in files, serialized data?
////
////
////                SerializedTargetHand.Save2(f, "General", "typeX");
////                System.out.println("saving data END ************");
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////
////        }
////
////        synchronized void endTesting() {
////            System.out.println("ending test mode");
////            control.staticEnd();
////            this.setVisible(false);
////            System.out.println("ending test mode END");
////        }
//
//        synchronized void prevHand() {
//            if (targets != null && targets.size() > 0) {
//                index = (--index + targets.size()) % targets.size();
//                System.out.println(index);
//                targetHand.setLoc(targets.get(index));
//            }
//        }
//
//        synchronized void nextHand() {
//            if (targets != null && targets.size() > 0) {
//                index = ++index % targets.size();
//                targetHand.setLoc(targets.get(index));
//            }
//        }
//
//		/* also have keyboard listeners attached to scene to turn KeyCode.LEFT and .RIGHT into prevButton.click and nextButton.click respectively
//         *
//		 *     for Java wait/notify, wait() and notifyAll() must both be in synchronized blocks
//		 *
//		 *     synchronized(sBar) { doStuff(); sBar.wait(); continueStuff(); }
//		 *     synchronized(sBar) { doExtra(); sBar.notify(); }
//		 *
//		 *     not sure if you need to do **sBar**.wait/notify(), may work to just do wait/notify. Probably not tho
//		 */
//    }


}