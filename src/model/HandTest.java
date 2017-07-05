package model;

import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Hand;

public class HandTest extends Hand{

    private Hand hand;
    private int id;
    private int secretVariable;

    public HandTest(Hand h){
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

}
