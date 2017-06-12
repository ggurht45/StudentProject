package controller;

import java.io.IOException;
import java.util.ArrayList;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;

import model.FrameStore;
import model.FrameWithScore;
import model.SerializedTargetHand;
import view.LeapUIApp;


public class Control2 extends Controller implements ControllerInterface {
    //put fields up here

    static int dataLimit = 10000; //0.01s; time limit between data points
    static int displayLimit = 10000; //0.01s; framelimit
    static int testLimit = 30000000; // 30s; maximum time allowed for test
    static double testThreshold = 0.9; // minimum score to end test; max score is 1

    private long dataStamp;
    private long displayStamp;
    private boolean selecting = false;

    FrameStore model;
    Comparer comparer = new Comparer();
    private ControlListener2 controlListener;
    public Hand testHand = new Hand();
    public boolean freeMode = true;            //is only true during freemode

    private boolean countdown = false;
    private long countdownStart;
    static double countdownTime = 1000000; // 1s; time user must maintain hand for
    private double finalscore = 0;
    private long finaltime;
    private boolean success;
    private double maxscore = 0;

    private long testTime = 0;
    private boolean timeTracked = false;


    //put methods here
    public Control2() {
        super();
        controlListener = new ControlListener2(this);
        addListener(controlListener);
    }

    public void enterTrainingMode() {
        System.out.println("enterTrainingMode called inside Control2");
        selecting = true;
        select();                    //is this method the selection process?
        freeMode = false;
        selecting = false;
    }

    public void select() { // grab hand and start test. *what does "grab" hand mean?
        Hand h = new Hand();
        try {
            ArrayList<Hand> arrayHands = SerializedTargetHand.getAllHands();
            h = LeapUIApp.selectHand(arrayHands);
        } catch (Exception e) {
            System.out.println("** Error happened while trying to select hand.");
            e.printStackTrace();
        }
        if (h.isValid()) {
            staticStart(h);
        }
    }


    public void staticStart(Hand storedHand) {
        System.out.println("test start");
        testHand = storedHand;
        LeapUIApp.startStaticTest(storedHand);
        FrameStore.staticStart();
    }

    public void staticEnd() {
        System.out.println("static end called from manual control2");
        LeapUIApp.endStaticTest2();
        try {
            FrameStore.staticEnd();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //these variables are probably unnecessary
        maxscore = 0;
        timeTracked = false;
        freeMode = true;
    }




    //method that executes on each incoming frame received by the controller
    public void receive(Frame inFrame) {
        long timestamp = inFrame.timestamp();
        if (selecting) {
            //do nothing with frame received, because in the process of selecting hand
        } else if (freeMode) {
            if ((timestamp - displayStamp) > displayLimit) {
                if (inFrame.hands().count() == 0) LeapUIApp.setUser(null, 0);
                else for (Hand h : inFrame.hands()) {
                    LeapUIApp.setUser(h, 0);
                }
                displayStamp = timestamp;
            }
        } else { // training mode
            System.out.println("entered training mode in control2");
//            if (timeTracked) {
//                if ((timestamp - testTime) > testLimit) {
//                    System.out.println("timeout");
//                    finalscore = maxscore;
//                    finaltime = testLimit;
//                    success = false;
//                    staticEnd();
//                }
//            } else {//if timeTracked is set to false, it will always be be set to True. cuz of the else.
//                testTime = timestamp;
//                timeTracked = true;
//            }
//
//            if ((timestamp - displayStamp) > displayLimit) {
//                if (inFrame.hands().count() == 0) LeapUIApp.setUser(null, 0);
//                else for (Hand h : inFrame.hands()) {
//                    double x = comparer.compare(h, testHand);
//                    if (x > 0) {
//                        LeapUIApp.setUser(h, x);
//                    }
//                }
//                displayStamp = timestamp;
//            }
//
//            if ((timestamp - dataStamp) > dataLimit) {
//                double y = 0; // max hand score this frame
//                for (Hand h : inFrame.hands()) {
//                    double x = comparer.compare(h, testHand);
//                    if (x > 0) {
//                        FrameStore.pass(new FrameWithScore(inFrame, x));
//                        dataStamp = timestamp;
//                        maxscore = Math.max(maxscore, x);
//                        y = x;
//                    }
//                }
//                if (y <= testThreshold) {// hands invalid or below threshold; countdown restarts
//                    countdown = false;
//                } else if (!countdown) {
//                    countdown = true;
//                    countdownStart = timestamp;
//                }
//                // user has spent enough time in successful position
//                else if (timestamp - countdownStart > countdownTime) {
//                    success = true;
//                    finalscore = y;
//                    finaltime = timestamp - testTime;
//                    staticEnd();
//                }
//            }
        } // end of training code
    }
}

class ControlListener2 extends Listener {
    Control2 control;

    public ControlListener2(Control2 control) {
        super();
        this.control = control;
    }

    @Override
    public void onFrame(Controller controller) {    // action upon receiving frame
        Frame inFrame = controller.frame();        //incoming frame
        control.receive(inFrame);                    //execute this method on the incoming frame
    }
}
