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


public class Control extends Controller implements ControllerInterface {
    //put fields up here

    //one second seems to have 6 zeros.
    static int dataLimit = 10000; //0.01s; time limit between data points
    static int displayLimit = 10000; //0.01s; framelimit
    static int testLimit = 30000000; // 30s; maximum time allowed for test
    static double testThreshold = 0.9; // minimum score to end test; max score is 1

    private long dataStamp;
    private long displayStamp;
    private boolean selecting = false;

    FrameStore model;
    Comparer comparer = new Comparer();
    private ControlListener controlListener;
    public Hand testHand = new Hand();
    public boolean freeMode = true;            //is only true during freemode. what is free mode??

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
    public Control() {
        super();
        controlListener = new ControlListener(this);
        addListener(controlListener);
    }

    public void enterTrainingMode() {
        System.out.println("enterTrainingMode called inside Control");
        selecting = true;
        select();                    //is this method the selection process?
        freeMode = false;
        selecting = false;
    }

    public void select() { // grab hand and start test. *what does "grab" hand mean?
        Hand h = new Hand();
        try {
            ArrayList<Hand> arrayHands;
            if(LeapUIApp.leftHandSelected){
                arrayHands = SerializedTargetHand.getAllHands2("LeftGestures.txt");
            }else{
                arrayHands = SerializedTargetHand.getAllHands2("RightGestures.txt");
            }
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
        System.out.println("staticStart called");
        testHand = storedHand;
        LeapUIApp.startStaticTest(storedHand);
        FrameStore.staticStart();
    }

    public void staticEnd() {
        System.out.println("staticEnd called");
        LeapUIApp.endStaticTest(finalscore, finaltime, success); // returns after 10s
        try {
            FrameStore.staticEnd();
        } catch (IOException e) {
            e.printStackTrace();
        }
        maxscore = 0;
        timeTracked = false;
        freeMode = true;
    }


    //method that executes on each incoming frame received by the controller
    public void receive(Frame inFrame) {
        long timestamp = inFrame.timestamp();
        //selecting seems like such an annoying variable. i dont see the use for it.

        //if selecting is false (as it will be initially), it will check freeMode variable. which is intially set to true. once selecting becomes true and the selection proess
        //  has FINISHED, only then does the freeMode variable become false as does the selecting variable. see enterTrainingMode method above
        //so basically, initially the condition that runs is the 2nd condition. then the 1st condition runs. then the 3rd condition will run. after
        //both 'selecting' and 'freeMode' are set to false.
        if (selecting) {
            System.out.println("Recieve: 'selecting' is true. waiting for user to select hand");
            //do nothing with frame received, because in the process of selecting hand
        } else if (freeMode) {
            //so free mode seems to be when in between the staticEnd and the beginning of selection process.
            System.out.println("Recieve: 'freeMode' is true. whatever that means");
            if ((timestamp - displayStamp) > displayLimit) { //this will only fire after the set amount of time has passed
                //the purpose this if clause seems to serve is to set the hands or their accuracy to null or 0.
                System.out.println("Recieve: 'freeMode' is true. time > displayLimit case true");
                if (inFrame.hands().count() == 0) {
                    System.out.println("Recieve: 'freeMode' is true. time > displayLimit, hands = 0");
                    LeapUIApp.setUser(null, 0);
                } else {
                    System.out.println("Recieve: 'freeMode' is true. time > displayLimit, hands accur =0");
                    for (Hand h : inFrame.hands()) {
                        LeapUIApp.setUser(h, 0);
                    }
                }
                displayStamp = timestamp;
            }
        } else { // training mode
            System.out.println("finally entered training mode. ");
            if (timeTracked) {
                System.out.println("timeTracked is true");
                if ((timestamp - testTime) > testLimit) { //as timestamp will increase, the difference will get larger. until this hits
                    System.out.println("timeout after 30 secs");
                    finalscore = maxscore;
                    finaltime = testLimit;
                    success = false;
                    staticEnd();
                }
            } else {
                System.out.println("timeTracked is false, about to set timeTracked to true");
                //if timeTracked is set to false, it will always be be set to True. cuz of the else. but the process gets started
                //by this condition. because after timeTracked is set to true, the first if clause always runs.
                testTime = timestamp; //testtime is set to the time of the first timestamp.
                timeTracked = true;
            }

            //the sequence of updating display to show current hand position; changes based on the displayLimit
            if ((timestamp - displayStamp) > displayLimit) {
                if (inFrame.hands().count() == 0) {
                    LeapUIApp.setUser(null, 0);
                } else {
                    for (Hand h : inFrame.hands()) {
                        double x = comparer.compare(h, testHand);
                        if (x > 0) {
                            LeapUIApp.setUser(h, x);
                        }
                    }
                }
                displayStamp = timestamp;
            }

            if ((timestamp - dataStamp) > dataLimit) {
                double y = 0; // max hand score this frame
                for (Hand h : inFrame.hands()) {
                    double x = comparer.compare(h, testHand);
                    if (x > 0) {
                        //creates a new "frameWithScore" object and passes it to be stored in the "max score frames" array
                        FrameStore.pass(new FrameWithScore(inFrame, x));
                        dataStamp = timestamp; //updates the dataStamp to reset the collection time logic
                        maxscore = Math.max(maxscore, x);
                        y = x; //why is y just set to x. shouldnt it only be set to x, if x is bigger?
                    }
                }

                // hands invalid or below threshold; countdown restarts
                if (y <= testThreshold) {
                    countdown = false;
                } else if (!countdown) {
                    countdown = true;
                    countdownStart = timestamp;
                }

                // user has spent enough time in successful position
                else {
                    if (timestamp - countdownStart > countdownTime) {
                        success = true;
                        finalscore = y;
                        finaltime = timestamp - testTime;
                        staticEnd();
                    }
                }
            }
        } // end of training code
    }
}

class ControlListener extends Listener {
    Control control;

    public ControlListener(Control control) {
        super();
        this.control = control;
    }

    @Override
    public void onFrame(Controller controller) {    // action upon receiving frame
        Frame inFrame = controller.frame();        //incoming frame
        control.receive(inFrame);                    //execute this method on the incoming frame
    }
}
