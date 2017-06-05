// TODO maybe polygon plot

package view;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
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
import model.SerializedTargetHand;
import view.anatomy.UIHand;
//import view.anatomy.UIHand_Full;
import view.anatomy.UIHand_Simple;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.*;

import controller.Control;

// XXX to run: java -Djava.library.path="D:\Software\Leap SDK\LeapDeveloperKit_2.2.2+24469_win\LeapSDK\lib\x64" -classpath ".;D:\Software\Leap SDK\LeapDeveloperKit_2.2.2+24469_win\LeapSDK\lib\*" view.LeapUIApp

public class LeapUIApp extends Application {
	private static double ScreenWidth = 800;
	private static double ScreenHeight = 800;
	private static UIHand userHand;
	private static UIHand targetHand;
	private static AccuracyBar aBar;    // a bar to show the current accuracy of the user compared to the target 
	private static SelectBar sBar;      // the buttons for selecting a target
	private static Button testButton;   // a button to transition from free mode to training mode
	private static Text scoreText;      // displays the user's score at the end of a test
	private static Text timeText;       // displays the time a user took at the end of a test
	private static Control control;
	private static Hand latestHand = null; // For recording target hands, disable in release version
//	private Controller leapDevice; // XXX testing purposes only
	
	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage stage) throws Exception {
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
		aBar = new AccuracyBar(ScreenWidth/4, ScreenHeight*4/5, ScreenWidth/2, 50);
		aBar.setVisible(false);
		sBar = new SelectBar(ScreenWidth/4, ScreenHeight*4/5, ScreenWidth/2, 50);
		sBar.setVisible(false);
		
		testButton = new Button("Enter Test Mode") {
			@Override public void fire() {
				setVisible(false);
				userHand.setVisible(false);
				new Thread( () -> control.enterTrainingMode() ).start();
			} 
		};
		testButton.setTranslateX(ScreenWidth*4/5);
		testButton.setTranslateY(ScreenHeight*4/5);
		testButton.setPrefHeight(50);
		testButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));
		
		scoreText = new Text();
		scoreText.setFont(Font.font(STYLESHEET_MODENA, ScreenHeight/4));
		scoreText.setY(ScreenHeight/2);
		scoreText.setVisible(false);
		timeText = new Text();
		timeText.setFont(Font.font(STYLESHEET_MODENA, ScreenHeight/16));
		timeText.setY(ScreenHeight*5/8);
		timeText.setVisible(false);
		
		// The 2D overlay
		Group group2D = new Group(aBar, sBar, testButton, scoreText, timeText);
		SubScene sub2D = new SubScene(group2D, ScreenWidth, ScreenHeight, false, SceneAntialiasing.BALANCED); // "false" because no depth in 2D
		Group root = new Group(sub3D, sub2D); // sub2D is second, as we want it overlaid, not underlaid
		Scene scene = new Scene(root);
		
		// TODO For recording target hands, disable in release version
		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyEvent) {
				if (keyEvent.getCode() == KeyCode.ENTER)
					try {
						System.out.println("enter was pressed, saving target hand.");
						Frame f = latestHand.frame();
						System.out.println("frame: \n" + f.toString());//enter was pressed, saving target hand.");
						//showImage();
						FingerList fingersInFrame = f.fingers();
						System.out.println("number of fingers: \n" + fingersInFrame.count());
						System.out.println("extended fingers: \n" + fingersInFrame.extended().count());


						SerializedTargetHand.Save(latestHand.frame());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				if (keyEvent.getCode() == KeyCode.M)
						new Thread( () -> {try {
							selectHand(SerializedTargetHand.getAllHands());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}}).start();
			}
		});
		
		stage.setScene(scene);
        stage.show();
        control = new Control();
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
	
	public static void setUser(Hand hand, double accuracy) {
		Platform.runLater(new SetUserTask(hand, accuracy));
	}
	
	public static Hand selectHand(ArrayList<Hand> arrayList) throws InterruptedException {
		Platform.runLater( () -> {
			userHand.setVisible(false);
			aBar.setVisible(false);
			sBar.setVisible(true);
		});
		Hand hand = sBar.select(arrayList); // Does not return until the user has selected a target
		Platform.runLater( () -> {
			sBar.setVisible(false);
			aBar.setVisible(true);
		});
		return hand;
	}

	// TODO unsupported
	public static void startDynamicTest(Hand h) {throw new UnsupportedOperationException();}
	public static void endDynamicTest() {throw new UnsupportedOperationException();}
	
	
	// TASKS
	private static class SetUserTask extends Task<Void> {
		private Hand hand;
		private double accuracy;
		public SetUserTask(Hand h, double x) {hand = h; accuracy = x;}
		
		@Override
		protected Void call() throws Exception {
			if (hand == null) {
				userHand.setVisible(false);
				aBar.setAccuracy(0);
			}
			else {
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
		public StaticStartTask(Hand h) {hand = h;}
		
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
			score = (int)(finalscore*100); seconds = finaltime/1000000f; success = testsuccess;
		}

		@Override
		protected Void call() throws Exception {
			targetHand.setVisible(false);
			userHand.setVisible(false);
			
			scoreText.setText(String.format("%d%%", score));
			scoreText.setX((ScreenWidth - scoreText.getBoundsInLocal().getWidth())/2);
			scoreText.setVisible(true);
			
			timeText.setText( success ? String.format("Time taken: %.3f seconds", seconds) : "You timed out");
			timeText.setX((ScreenWidth - timeText.getBoundsInLocal().getWidth())/2);
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
			
			prevButton = new Button("\u25c0 Previous"){ @Override public void fire() {prevHand();} };
			prevButton.setTranslateX(x);
			prevButton.setTranslateY(y);
			prevButton.setPrefSize(width/4, height);
			prevButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));
			
			confirmButton = new Button("Begin Test"){ @Override public void fire() {confirm();} };
			confirmButton.setTranslateX(x + width/4);
			confirmButton.setTranslateY(y);
			confirmButton.setPrefSize(width/2, height);
			confirmButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 16));
			
			nextButton = new Button("Next \u25b6"){ @Override public void fire() {nextHand();} };
			nextButton.setTranslateX(x + width*3/4);
			nextButton.setTranslateY(y);
			nextButton.setPrefSize(width/4, height);
			nextButton.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 15));
			
			getChildren().addAll(prevButton, confirmButton, nextButton);
		}
		
		public synchronized Hand select(ArrayList<Hand> arrayList) throws InterruptedException {
			confirmed = false;
			index = 0;
			targets = arrayList;
			if (targets != null && targets.size() > 0) {
			Platform.runLater( () -> {targetHand.setLoc(targets.get(index)); targetHand.setVisible(true);} ); // **set local variables**
				while (!confirmed) wait();
				return targets.get(index);
			} else return null;
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

}