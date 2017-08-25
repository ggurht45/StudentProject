package controller;

import com.leapmotion.leap.*;
import com.leapmotion.leap.Finger.Type;
import model.HandInfo;
import model.SerializedTargetHand;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static java.io.File.separator;

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

    public static HashMap<String, String> getFingerPoseMap(String gestureType) {
        HashMap<String, String> fingerPoseMap = new HashMap<>();
//        if (leftHanded) { //left right shouldn't matter since both hands follow same gesture signatures.
        switch (gestureType) {
            case "gesture1Left":
            case "gesture1Right":
                fingerPoseMap.put("index", "straight");
                fingerPoseMap.put("middle", "curved");
                fingerPoseMap.put("ring", "curved");
                fingerPoseMap.put("pinky", "curved");
                fingerPoseMap.put("thumb", "thumb");
                break;
            case "gesture2Left":
            case "gesture2Right":
                fingerPoseMap.put("index", "straight");
                fingerPoseMap.put("middle", "straight");
                fingerPoseMap.put("ring", "straight");
                fingerPoseMap.put("pinky", "curved");
                fingerPoseMap.put("thumb", "pinky");
                break;
            case "gesture3Left":
            case "gesture3Right":
                fingerPoseMap.put("index", "straight");
                fingerPoseMap.put("middle", "curved");
                fingerPoseMap.put("ring", "straight");
                fingerPoseMap.put("pinky", "straight");
                fingerPoseMap.put("thumb", "middle");
                break;
            case "gesture4Left":
            case "gesture4Right":
                fingerPoseMap.put("index", "curved");
                fingerPoseMap.put("middle", "curved");
                fingerPoseMap.put("ring", "curved");
                fingerPoseMap.put("pinky", "straight");
                fingerPoseMap.put("thumb", "thumb");
                break;
            case "gesture5Left":
            case "gesture5Right":
                fingerPoseMap.put("index", "curved");
                fingerPoseMap.put("middle", "straight");
                fingerPoseMap.put("ring", "straight");
                fingerPoseMap.put("pinky", "straight");
                fingerPoseMap.put("thumb", "index");
                break;
            case "gesture6Left":
            case "gesture6Right":
                fingerPoseMap.put("index", "straight");
                fingerPoseMap.put("middle", "straight");
                fingerPoseMap.put("ring", "curved");
                fingerPoseMap.put("pinky", "curved");
                fingerPoseMap.put("thumb", "ring");
                break;
            case "gesture7Left":
            case "gesture7Right":
                fingerPoseMap.put("index", "curved");
                fingerPoseMap.put("middle", "curved");
                fingerPoseMap.put("ring", "curved");
                fingerPoseMap.put("pinky", "straight");
                fingerPoseMap.put("thumb", "middle");
                break;
            case "gesture8Left":
            case "gesture8Right":
                fingerPoseMap.put("index", "straight");
                fingerPoseMap.put("middle", "curved");
                fingerPoseMap.put("ring", "curved");
                fingerPoseMap.put("pinky", "curved");
                fingerPoseMap.put("thumb", "middle");
                break;
            case "gesture9Left":
            case "gesture9Right":
                fingerPoseMap.put("index", "straight");
                fingerPoseMap.put("middle", "straight");
                fingerPoseMap.put("ring", "curved");
                fingerPoseMap.put("pinky", "straight");
                fingerPoseMap.put("thumb", "ring");
                break;
            case "gesture10Left":
            case "gesture10Right":
                fingerPoseMap.put("index", "straight");
                fingerPoseMap.put("middle", "curved");
                fingerPoseMap.put("ring", "curved");
                fingerPoseMap.put("pinky", "straight");
                fingerPoseMap.put("thumb", "middle");
                break;
            default:
                System.out.println("no default fingerPoses");
                //nothing here
        }


        return fingerPoseMap;
//        } else return new HashMap<String, String>();
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
//            System.out.println("setting greater angle (" + angle + ") to 90 for angle btw bone: " + a + ", and bone: " + b);
            angle = 90;
        }
        //nb. not else
        if (angle < 0) {
//            System.out.println("setting lesser angle (" + angle + ") to 0 for angle btw bone: " + a + ", and bone: " + b);
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
        sumOfAngles = sumOfAngles - 30;
        double score = sumOfAngles / 270;           //closer to 0 means a better score. (ie. weird scale)
        score = 1 - score;                          //conventional scale: 0 = bad, 1 = good.

        if (score < 0) {
            score = 0;
        }
        if (score > 1) {
            score = 1;
        }
        return score;
    }

    private static double calculateCurvednessOfFinger(Finger f) {
        double sumOfAngles = getSumOfThreeAnglesBetweenFingerBones(f);

        //best case is: 90+90+90 = 270; adjusted bestcase = 210; worst case = 0;
        double score = sumOfAngles / 210;           //closer to 1 means a better score

        if (score < 0) {
            score = 0;
        }
        if (score > 1) {
            score = 1;
        }
        return score;
    }

    private static double getThumbScore(Finger thumb, Finger f) {

        //bones in thumb and finger
        HashMap<String, Bone> thumbMap = getHashMapOfBonesFromFinger(thumb);
        HashMap<String, Bone> fingerMap = getHashMapOfBonesFromFinger(f);

        //get center point of thumb's tip bone
        Vector thumbTip = thumbMap.get("distal").center();

        //finger bones
        Bone d = fingerMap.get("distal");
        Bone i = fingerMap.get("intermediate");
        Bone p = fingerMap.get("proximal");

        //finger bones center points
        Vector fingerCenter1 = d.center();
        Vector fingerCenter2 = i.center();
        Vector fingerCenter3 = p.center();

        //length of bones
        float smallestBoneLength = (Math.min(Math.min(d.length(), i.length()), p.length()));


        //find distances to tip bones of finger
        float d1 = thumbTip.distanceTo(fingerCenter1);
        float d2 = thumbTip.distanceTo(fingerCenter2);
        float d3 = thumbTip.distanceTo(fingerCenter3);
        double minDistance = (double) (Math.min(Math.min(d1, d2), d3));


        //scale and score
        double distanceScaledByBoneLength = minDistance / (smallestBoneLength * 5);
        double score = 1 - distanceScaledByBoneLength;


        if (score < 0) {
            score = 0;
        }
        if (score > 1) {
            score = 1;
        }
        return score;
    }


    private static double gradeFinger(HashMap<String, Finger> fingerMap, Finger f, String pose) {
        if (pose.equals("straight")) {
            return calculateStraightnessOfFinger(f);
        } else if (pose.equals("curved")) {
            return calculateCurvednessOfFinger(f);
        } else if (pose.equals("thumb")) {
            return calculateStraightnessOfFinger(f);
        }
        //these conditions deal with thumb touching other fingers
        else {
            Finger theFingerThumbTouches = fingerMap.get(pose);
            return getThumbScore(f, theFingerThumbTouches);
        }

////
////        else if (pose.equals("thumb")) {
////            return calculateStraightnessOfFinger(f);
////        }
    }

    private static HashMap<String, Double> getFingersGradedMap(HashMap<String, Finger> fingerMap, HashMap<String, String> fingerPoseMap) {
        if (fingerMap.size() != 5 || fingerPoseMap.size() != 5) {
            System.out.println("ERROR: EXPECTED SIZE TO BE FIVE!");
        }
        HashMap<String, Double> grades = new HashMap<>();
        for (String fingerName : fingerMap.keySet()) {
            Finger f = fingerMap.get(fingerName);
            String pose = fingerPoseMap.get(fingerName);
            double grade = gradeFinger(fingerMap, f, pose);
            grades.put(fingerName, grade);
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

    // left/right doesnt matter
    //straight -- angles between each consecutive bone in finger must be 0 degrees. (or 180, check that later)
    //curved -- max "" 90 degrees, min "" 45. maybe 30 for bone nearest wrist. think about thumb
    //thumb -- tip of thumb has to be within the (max min) x,y,z of any center point of any bone in the finger. calculate max and min xyz values.
    public static int compare(Hand h, String gestureType) {
        FingerList fingerList = h.fingers();

        //make sure you have five fingers
        if (fingerList.count() == 5) {
            //get all the fingers into a hashmap, named by their common name
            HashMap<String, Finger> fingerMap = getFingerHashMap(fingerList);

            //want the pinky and index to be straight, middle and ring to be fully curved. (thumb touching middle)
            HashMap<String, String> fingerPoseMap = getFingerPoseMap(gestureType);
            HashMap<String, Double> fingersGradedMap = getFingersGradedMap(fingerMap, fingerPoseMap);

            double totalGrade = cumulativeGrade(fingersGradedMap);

            int score = (int) (totalGrade * 100.0);
            return score;
        }
        System.out.println("number of fingers in hand is not 5");
        return -1;
    }


    private static HashMap<String, ArrayList<String>> outputFolderScores(HashMap<String, ArrayList<String>> ghashmap, String f) {
        // get all Hand info objects from file.
        String fullfilename = SerializedTargetHand.getCSVFilePathForFolder(f);
        ArrayList<HandInfo> hands = SerializedTargetHand.readFromCSV(fullfilename);

        //print folder name
        System.out.println("Folder: " + f);

        //print header
        String header = HandInfo.getCSVHeader() + ", AngleScore, ComponentsScore";
        System.out.println(header);

        //print data
        for (HandInfo h : hands) {
//        ArrayList<HandInfo> handsTesting = new ArrayList<>();
//        handsTesting.add(hands.get(7));
//        for (HandInfo h : handsTesting) {

            String gestureType = h.name;

            //leap motion hand
            Hand lmh = SerializedTargetHand.getHandFromString(h.handFile);

            //get correct target to compare against
            String targetPath = "dataOutput/targets2/" + gestureType + ".hand";
            Hand correctTarget = SerializedTargetHand.getHandFromString(targetPath);

            //get scores
            int s1 = Comparer.compareStatic(lmh, correctTarget);
            int s2 = compare(lmh, gestureType);
            String line = h.getCommaSeperatedToString() + ", " + s1 + ", " + s2;

            //add to ghashmap
            if (ghashmap.containsKey(gestureType)) {
                ghashmap.get(gestureType).add(line);
            } else {
                ArrayList<String> a = new ArrayList<>();
                a.add(line);
                ghashmap.put(gestureType, a);
            }

            System.out.println(line);
        }
        System.out.println();
        return ghashmap;
    }

    public static void main(String[] args) throws Exception {
        //put run output to file
        PrintStream out = new PrintStream(new FileOutputStream("outputByFolderName.txt"));
        PrintStream out2 = new PrintStream(new FileOutputStream("outputByGestureType.txt"));
        System.setOut(out);

        //initialize hashmap for gesturetypes
        HashMap<String, ArrayList<String>> ghashmap = new HashMap<>();


        //my testing
        ghashmap = outputFolderScores(ghashmap, "General");
        //Nik's data
        ghashmap = outputFolderScores(ghashmap, "Alex");
        ghashmap = outputFolderScores(ghashmap, "Jacqueline");
        ghashmap = outputFolderScores(ghashmap, "Stefan");
        //my data collection
        ghashmap = outputFolderScores(ghashmap, "test1");
        ghashmap = outputFolderScores(ghashmap, "test2");
        ghashmap = outputFolderScores(ghashmap, "test3");
        ghashmap = outputFolderScores(ghashmap, "test4");
        ghashmap = outputFolderScores(ghashmap, "test5");
        ghashmap = outputFolderScores(ghashmap, "test6");
        ghashmap = outputFolderScores(ghashmap, "test7");
        ghashmap = outputFolderScores(ghashmap, "test8");


        //print by gesture types
        System.setOut(out2);
        List<String> gestureTypesInOrder = Arrays.asList("gesture1Left", "gesture2Left", "gesture3Left", "gesture4Left", "gesture5Left", "gesture6Left", "gesture7Left", "gesture8Left", "gesture9Left", "gesture10Left", "gesture1Right", "gesture2Right", "gesture3Right", "gesture4Right", "gesture5Right", "gesture6Right", "gesture7Right", "gesture8Right", "gesture9Right", "gesture10Right");
        for (String gestureType : gestureTypesInOrder) {
            System.out.println(gestureType);
            //print all lines in list
            for(String line: ghashmap.get(gestureType)){
                System.out.println(line);
            }
            System.out.println();
        }
        System.out.println();


    }

}
