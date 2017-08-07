package controller;

import com.leapmotion.leap.Bone;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Finger.Type;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Hand;
import model.SerializedTargetHand;

import java.util.ArrayList;
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
                case 10:
                    fingerPoseMap.put("index", "straight");
                    fingerPoseMap.put("pinky", "straight");
                    fingerPoseMap.put("middle", "curved");
                    fingerPoseMap.put("ring", "curved");
                    fingerPoseMap.put("thumb", "thumb");
                    break;
                default:
                    System.out.println("no default fingerPoses");
                    //nothing here
            }


            return fingerPoseMap;
        } else return new HashMap<String, String>();
    }

    private static HashMap<String, Bone> getHashMapOfBonesFromFinger(Finger f) {
        HashMap<String, Bone> h = new HashMap<>();
        h.put("distal", f.bone(Bone.Type.TYPE_DISTAL));                     //closest to tip
        h.put("intermediate", f.bone(Bone.Type.TYPE_INTERMEDIATE));         //2nd closest to tip
        h.put("proximal", f.bone(Bone.Type.TYPE_PROXIMAL));                 //3rd to tip
        h.put("metacarpal", f.bone(Bone.Type.TYPE_METACARPAL));             //closest to palm
        return h;
    }

    private static double angleBetweenBones(Bone a, Bone b) {
        double angle = a.direction().angleTo(b.direction());
        angle = Math.toDegrees(angle);
        if (angle > 90) {
            System.out.println("setting greater angle (" + angle + ") to 90 for angle btw bone: " + a + ", and bone: " + b);
            angle = 90;
        }
        //nb. not else
        if (angle < 0) {
            System.out.println("setting lesser angle (" + angle + ") to 0 for angle btw bone: " + a + ", and bone: " + b);
            angle = 0;
        }
        return angle;
    }

    private static double getSumOfThreeAnglesBetweenFingerBones(Finger f) {
        //bone variables for easy access
        HashMap<String, Bone> b = getHashMapOfBonesFromFinger(f);
        Bone d = b.get("distal");
        Bone i = b.get("intermediate");
        Bone p = b.get("proximal");
        Bone m = b.get("metacarpal");

        //find angle between bones; using their direction vector; we want the these angles to be as close to zero as possible(for straight fingers);
        // NOTE: this is different from compareAngles method in Comparer class.
        double angle1 = angleBetweenBones(m, p);
        double angle2 = angleBetweenBones(p, i);
        double angle3 = angleBetweenBones(i, d);

        //return addition of angles
        return angle1 + angle2 + angle3;
    }

    private static double calculateStraightnessOfFinger(Finger f) {
        double sumOfAngles = getSumOfThreeAnglesBetweenFingerBones(f);
        //best case = 0; worst case is: 90+90+90 = 270.
        double score = sumOfAngles / 270;           //closer to 0 means a better score. (ie. weird scale)
        return 1 - score;                          //conventional scale: 0 = bad, 1 = good.
    }

    private static double calculateCurvednessOfFinger(Finger f) {
        double sumOfAngles = getSumOfThreeAnglesBetweenFingerBones(f);
        //best case is: 90+90+90 = 270; worst case = 0;
        return sumOfAngles / 270;           //closer to 1 means a better score
    }

    private static double gradeFinger(String fingerType, Finger f, String pose) {
        if (pose.equals("straight")) {
            return calculateStraightnessOfFinger(f);
        } else if (pose.equals("curved")) {
            return calculateCurvednessOfFinger(f);
        } else if (pose.equals("thumb")) {
            return 1; //best possible value for now
        }
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

    private static double cumulativeGrade(HashMap<String, Double> fingersGradeMap) {
        double grade = 0;
        for (String key : fingersGradeMap.keySet()) {
            grade = grade + fingersGradeMap.get(key);
        }
        grade = grade / 5.0;//five fingers
        return grade;
    }

    public static double compare(Hand h1, Hand h2, String gestureType) {

        FingerList fingerList = h1.fingers();
        if (fingerList.count() == 5) {
            //get all the fingers into a hashmap
            HashMap<String, Finger> fingerMap = getFingerHashMap(fingerList);

            //deconstruct from gestureType what kind of gesture we are dealing with
            //assume gesture 10, left hand. rock sign
            boolean leftHanded = true;
            int gestureNumber = 10;

            //want the pinky and index to be straight, middle and ring to be fully curved. (thumb touching middle)
            HashMap<String, String> fingerPoseMap = getFingerPoseMap(leftHanded, gestureNumber);
            HashMap<String, Double> fingersGradedMap = getFingersGradedMap(fingerMap, fingerPoseMap);
//            System.out.println(fingersGradedMap);

            double totalGrade = cumulativeGrade(fingersGradedMap);


            //straight -- angles between each consecutive bone in finger must be 0 degrees. (or 180, check that later)
            //curved -- max "" 90 degrees, min "" 45. maybe 30 for bone nearest wrist. think about thumb
            //thumb -- tip of thumb has to be within the (max min) x,y,z of any center point of any bone in the finger. calculate max and min xyz values.


            return totalGrade;
        }
        System.out.println("number of fingers in hand is not 5");
        return -1;
    }

    private static HashMap<String, Hand> getTestHands() {
        HashMap<String, Hand> testHands = new HashMap<>();
        HashMap<String, String> nicknamePaths = new HashMap<>();

        //can easily add here
        nicknamePaths.put("besttry", "dataOutput/TestData/2017-08-07 14-18-05.hand");
        nicknamePaths.put("closedfist", "dataOutput/TestData/2017-08-07 14-17-52.hand");
        nicknamePaths.put("openPalm", "dataOutput/TestData/2017-08-07 14-18-20.hand");
        nicknamePaths.put("oneFingerOff", "dataOutput/TestData/2017-08-07 14-18-42.hand");
        nicknamePaths.put("oneFingerOff2", "dataOutput/TestData/2017-08-07 14-19-30.hand");

        for (String nickname : nicknamePaths.keySet()) {
            String path = nicknamePaths.get(nickname);
            Hand h = SerializedTargetHand.getHandFromString(path);
            testHands.put(nickname, h);
        }
        return testHands;
    }

    //using strings cuz java doesn't have tuples; and im too lazy to write class
    private static HashMap<String, String> getGradesForTestHands(HashMap<String, Hand> testHands, Hand target) {
        HashMap<String, String> gradesMap = new HashMap<>();

        for (String nickname : testHands.keySet()) {
            Hand h = testHands.get(nickname);
            double grade1 = Comparer.compareStatic(h, target);
            double grade2 = compare(h, h, "gestureTypeNotImplemented yet");
            String stringGrade = "Grade1: " + grade1 + ", Grade2: " + grade2 + "\n";
            gradesMap.put(nickname, stringGrade);
        }
        return gradesMap;
    }

    public static void main(String[] args) {
        System.out.println("testing main method");
        String path = "dataOutput/targets2/gesture10Left.hand";
        Hand target = SerializedTargetHand.getHandFromString(path);

        //get test hands
        HashMap<String, Hand> hands = getTestHands();

        //get grades for all test hands; using strings cuz java doesn't have tuples
        HashMap<String, String> grades = getGradesForTestHands(hands, target);

        System.out.println(grades);
    }

}
