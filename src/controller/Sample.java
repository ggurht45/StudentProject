package controller;

import java.io.IOException;
import com.leapmotion.leap.*;


class SampleListener extends Listener {
	
    public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

    public void onConnect(Controller controller) {
        System.out.println("Connected");

    }

    public void onDisconnect(Controller controller) {
        System.out.println("Disconnected");
    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }
    
    Comparer comparer = new Comparer();
    int x = 0;   
    Hand myHand = new Hand();
    public void onFrame(Controller controller) {
    	Frame frame = controller.frame();    	
    	if (x==0){    		
    		Hand hand = frame.hands().leftmost();        	
    		if (hand.isValid()) {
    			if (hand.fingers().count() == 5){
    				x += 1; myHand = hand;
    			}
    		}	
    	}
    	else {System.out.println(comparer.compare(frame.hands().get(0), myHand));}    	  
    }    
}

class Sample {
    public static void main(String[] args) {
        SampleListener listener = new SampleListener();
        Controller controller = new Controller();
        controller.addListener(listener);
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller.removeListener(listener);
    }
}
