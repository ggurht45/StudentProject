package model;

import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Hand;

public class ModifiableHand extends Hand {

    private Hand hand;
    private int id;
    private int secretVariable;

    public ModifiableHand(Hand h) {
        hand = h;
        //go through and set variables.. so when this "extended" (manually in this case), hand is passed around, the methods work as expected.
        //and to be honest, it wont be that difficult. i just use the super.method() in the overridden methods.
//        longBoringMethod(h);
        id = h.id();
        System.out.println("handTest class id: " + this.id);
        secretVariable = 1394;
    }

    @Override
    public FingerList fingers() {
        return hand.fingers();
    }

    public int getSecretVariable() {
        return secretVariable;
    }

    public void setSecretVariable(int secretVariable) {
        this.secretVariable = secretVariable;
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }


    public void runTests() {
        System.out.println("------------- testing extended hand class -------------");
        //try to give ht all the methods that lm hand has.
        for (int i = 0; i < 5; ++i) {
            Finger fingerA = this.hand.fingers().fingerType(Finger.Type.swigToEnum(i)).frontmost();
            Finger fingerB = this.fingers().fingerType(Finger.Type.swigToEnum(i)).frontmost();
            System.out.println("hand finger " + i + ": " + fingerA);
            System.out.println("ht finger " + i + ": " + fingerB);
            System.out.println("fingerA = fingerB?  \t" + fingerA.equals(fingerB));
        }
        System.out.println("------------- END testing extended hand class -------------");

    }

}
