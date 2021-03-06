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


public class Control extends Controller {
	
	public Control() {
		super();
		controlListener = new ControlListener(this);
		addListener(controlListener);		
	}
	
	static int dataLimit = 10000; //0.01s; time limit between data points
	static int displayLimit = 10000; //0.01s; framelimit
	static int testLimit = 30000000; // 30s; maximum time allowed for test
	static double testThreshold = 0.9; // minimum score to end test; max score is 1
	FrameStore model;
	Comparer comparer = new Comparer();
	private ControlListener controlListener;
	public Hand testHand = new Hand();
	public boolean freeMode = true;			//is only true during freemode
		
	private boolean countdown = false;
	private long countdownStart;
	static double countdownTime = 1000000; // 1s; time user must maintain hand for 
	private double finalscore=0;
	private long finaltime;
	private boolean success;
	private double maxscore=0;
			
	public void select() { // grab hand and start test
		Hand h = new Hand();
		try {
			ArrayList<Hand> arrayHands = SerializedTargetHand.getAllHands();
			h = LeapUIApp.selectHand(arrayHands);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (h.isValid()) staticStart(h); 
	}
	
	private long testTime = 0;
	private boolean timeTracked = false; 
	public void staticStart(Hand storedHand){
		System.out.println("test start");
		testHand = storedHand; 
		LeapUIApp.startStaticTest(storedHand);
		FrameStore.staticStart(); 
	}

	public void staticEnd() {		
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

	private long dataStamp;
	private long displayStamp;
	private boolean selecting = false;	
	public void enterTrainingMode(){
		selecting = true;
		select();
		freeMode=false;
		selecting = false;
	}		
	
	public void receive(Frame inFrame) {
		long timestamp = inFrame.timestamp();		
		if (selecting){}
		else if(freeMode) {
			if ((timestamp - displayStamp) > displayLimit){
				if (inFrame.hands().count() == 0) LeapUIApp.setUser(null, 0);
				else for (Hand h : inFrame.hands()) {
					LeapUIApp.setUser(h,0);
				}
				displayStamp = timestamp;	
			}			
		} else { // training mode
			if (timeTracked) {
				if ((timestamp - testTime) > testLimit){
					System.out.println("timeout");
					finalscore=maxscore; 
					finaltime=testLimit; 
					success=false; 
					staticEnd();
				}
			}
			else {testTime = timestamp; timeTracked = true;}
			
			if ((timestamp - displayStamp) > displayLimit){
				if (inFrame.hands().count() == 0) LeapUIApp.setUser(null, 0);
				else for (Hand h : inFrame.hands()) {
					double x = comparer.compare(h, testHand);
					if (x > 0) {
						LeapUIApp.setUser(h, x);
					}
				}
				displayStamp = timestamp;	
			}			
			
			if ((timestamp - dataStamp) > dataLimit) { 
				double y = 0; // max hand score this frame
				for (Hand h : inFrame.hands()) {
					double x = comparer.compare(h, testHand);					
					if (x > 0) {
						FrameStore.pass(new FrameWithScore(inFrame,x));
						dataStamp = timestamp;
						maxscore= Math.max(maxscore, x);
						y = x; 
					}					
				} 
				if (y <= testThreshold){// hands invalid or below threshold; countdown restarts
					countdown = false;
				}				
				else if (!countdown){
					countdown = true;
					countdownStart = timestamp;
				}	
				// user has spent enough time in successful position
				else if (timestamp - countdownStart > countdownTime){
					success=true;
					finalscore=y;
					finaltime= timestamp - testTime;
					staticEnd(); 
				}								
			}				
		} // end of training code	
	}
}

class ControlListener extends Listener{
	Control control;

	public ControlListener(Control control) {
		super();
		this.control = control;
	}

	@Override
	public void onFrame(Controller controller) { // action upon receiving frame
		Frame inFrame = controller.frame();
		control.receive(inFrame);
	}
}
