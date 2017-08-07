package controller;

import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Finger.Type;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Hand;

import java.util.HashMap;

public class Comparer2 {

    //returns a number between 0,1. cos(ang) ang is less than 90.
    private double compareAngles(float angle1, float angle2) {
        double differenceBtwAngles = Math.abs(angle1 - angle2);
        double tmp = Math.min(differenceBtwAngles, Math.PI / 4); //pi is 180 degrees. so tmp can be at most 45
        return Math.cos(2 * tmp); //if tmp is exactly 45, will return 0. cos(90) = 0.
    }


    public static HashMap<String, Finger> getFingerHashMap(FingerList fingerList) {
        HashMap<String, Finger> fingerMap = new HashMap<>();
        fingerMap.put("thumb", fingerList.fingerType(Type.TYPE_THUMB).get(0));
        fingerMap.put("index", fingerList.fingerType(Type.TYPE_INDEX).get(0));
        fingerMap.put("middle", fingerList.fingerType(Type.TYPE_MIDDLE).get(0));
        fingerMap.put("ring", fingerList.fingerType(Type.TYPE_RING).get(0));
        fingerMap.put("pinky", fingerList.fingerType(Type.TYPE_PINKY).get(0));
        return fingerMap;
    }

    public static HashMap<String, String> getFingerPoseMap(boolean leftHanded, int gestureNumber) {
        HashMap<String, String> fingerPoseMap = new HashMap<>();
        if (leftHanded) {
            switch (gestureNumber) {
                case 3:
                    fingerPoseMap.put("index", "straight");
                    fingerPoseMap.put("pinky", "straight");
                    fingerPoseMap.put("middle", "curved");
                    fingerPoseMap.put("ring", "curved");
                    fingerPoseMap.put("thumb", "curved");
                    break;
                default:
                    System.out.println("no default fingerPoses");
                    //nothing here
            }


            return fingerPoseMap;
        } else return new HashMap<String, String>();
    }

    private static double gradeFinger(String fingerType, Finger f, String pose) {
        //straight pose
        if(pose.equals("straight")){
            return .5;
        }

        //curved pose
        if(pose.equals("curved")){
            return .75;
        }

        //special case for thumb


        return 0;
    }

    private static HashMap<String, Double> getFingersGradedMap(HashMap<String, Finger> fingerMap, HashMap<String, String> fingerPoseMap) {
        if (fingerMap.size() != 5 || fingerPoseMap.size() != 5) {
            System.out.println("ERROR: EXPECTED SIZE TO BE FIVE!");
        }
        HashMap<String, Double> grades = new HashMap<>();
        for (String key : fingerMap.keySet()) {
            Finger f = fingerMap.get(key);
            String pose = fingerPoseMap.get(key);
            double grade = gradeFinger(key, f, pose);
            grades.put(key, grade);
        }
        return grades;
    }

    public double compare(Hand h1, Hand h2, String gestureType) {

        FingerList fingerList = h1.fingers();
        if (fingerList.count() == 5) {
            //get all the fingers into a hashmap
            HashMap<String, Finger> fingerMap = getFingerHashMap(fingerList);

            //deconstruct from gestureType what kind of gesture we are dealing with
            //assume gesture 3, left hand. rock sign
            boolean leftHanded = true;
            int gestureNumber = 3;

            //want the pinky and index to be straight, middle and ring to be fully curved. (thumb touching middle)
            HashMap<String, String> fingerPoseMap = getFingerPoseMap(leftHanded, gestureNumber);
            HashMap<String, Double> fingersGradedMap = getFingersGradedMap(fingerMap, fingerPoseMap);
            System.out.println(fingersGradedMap);


            return 0;
        }
        System.out.println("number of fingers in hand is not 5");
        return -1;
    }

}
