package model;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Controller;


public class SerializedTargetHand {
    public static final int MAX_FRAMES = 10;
    private static final String hands_file = "TargetHands.txt";
    private static String leftGestures_file = "LeftGestures.txt";
    private static String rightGestures_file = "RightGestures.txt";


    public static void Save(Frame f) throws IOException {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String fileName = "targets/" + sdf.format(cal.getTime()) + ".hand";
        byte[] serializedFrame = f.serialize();
        Files.write(Paths.get(fileName), serializedFrame);
        PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(hands_file, true)));
        printer.println(fileName);
        printer.close();
    }

    //use this function to save to a specific folder.
    public static void Save2(Frame f, String outFolder, String typeOfGesture) throws IOException {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        //store in dataOutput/<outFolder>/<typeOfGesture>_<DateTime>.hand
        String fileName = "dataOutput/" + outFolder + "/" + typeOfGesture + "_" + sdf.format(cal.getTime()) + ".hand";
        byte[] serializedFrame = f.serialize();
        Files.write(Paths.get(fileName), serializedFrame);
        PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(hands_file, true)));
        printer.println(fileName);
        printer.close();
    }

    //use this function to save to a specific folder that may need to be created.
    public static void Save4(Frame f, String fileName, String comments, boolean passFail) throws IOException {
//        System.out.println("inside save3");
//        String pf;
//        String indexFile;
//        if (passFail) {
//            pf = "pass";
////            indexFile = leftGestures_file;
//        } else {
//            pf = "fail";
////            indexFile = rightGestures_file;
//        }


        fileName = "gesture1";

        File fileDirectory = new File("dataOutput/alice3/" + fileName + ".hand");
        fileDirectory.getParentFile().mkdirs();
        System.out.println(fileDirectory);
        System.out.println("full path: " + fileDirectory.getAbsolutePath());
        System.out.println("getPath: " + fileDirectory.getPath());
        byte[] serializedFrame = f.serialize();
        Files.write(Paths.get(fileDirectory.getPath()), serializedFrame);


//        fileDirectory.getAbsolutePath();
//
//        String fileName = "dataOutput/" + outFolder + "/" + typeOfGesture + "_" + sdf.format(cal.getTime()) + ".hand";
//        byte[] serializedFrame = f.serialize();
//        Files.write(Paths.get(fileName), serializedFrame);


//        PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(hands_file, true)));
//        printer.println(fileName);
//        printer.close();
    }


    //save the 10 specific gestures; id = 1-10; side = l/r
    public static void Save3(Frame f, String gestureId, boolean leftHand) throws IOException {
        System.out.println("inside save3");
        String side;
        String indexFile;
        if (leftHand) {
            side = "Left";
            indexFile = leftGestures_file;
        } else {
            side = "Right";
            indexFile = rightGestures_file;
        }
        String fileName = "targets2/" + gestureId + side + ".hand";
        byte[] serializedFrame = f.serialize();
        Files.write(Paths.get(fileName), serializedFrame);
        PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(indexFile, true)));
        printer.println(fileName);
        printer.close();
        System.out.println("leaving save3");
    }

    //this method reads a .hand file. given a path string that tells where to find that .hand file
    public static Hand readFromFile(String s) throws FileNotFoundException, IOException {
        Controller controller = new Controller(); //An instance must exist
        byte[] frameBytes = Files.readAllBytes(Paths.get(s));

        Frame reconstructedFrame = new Frame();
        reconstructedFrame.deserialize(frameBytes);

        return reconstructedFrame.hands().leftmost();
    }

    public static ArrayList<Hand> getAllHands() throws Exception {

        File inFile = new File(hands_file);
        if (!inFile.exists())
            throw new Exception("In file not found :" + hands_file); // No input file

        BufferedReader br = new BufferedReader(new FileReader(inFile));

        ArrayList<Hand> hands = new ArrayList<Hand>();

        try {
            String line = br.readLine();
            while (line != null) {
                hands.add(readFromFile(line));
                line = br.readLine();
            }
        } finally {
            br.close();
        }
        return hands;

    }

    public static ArrayList<Hand> getAllHands2(String fileName) {
        try {
            File inFile = new File(fileName);
            if (!inFile.exists())
                throw new Exception("In file not found :" + fileName); // No input file

            BufferedReader br = new BufferedReader(new FileReader(inFile));

            ArrayList<Hand> hands = new ArrayList<Hand>();

            try {
                String line = br.readLine();
                while (line != null) {
                    hands.add(readFromFile(line));
                    line = br.readLine();
                }
            } finally {
                br.close();
            }
            return hands;
        } catch (Exception e) {
            System.out.println("error happened while trying to getAllHands2");
        }
        return null;
    }

    public static ArrayList<String> getAllHands2Names(String fileName) {
        try {
            File inFile = new File(fileName);
            if (!inFile.exists())
                throw new Exception("In file not found :" + fileName); // No input file

            BufferedReader br = new BufferedReader(new FileReader(inFile));

            ArrayList<String> handFiles = new ArrayList();

            try {
                String line = br.readLine();
                while (line != null) {
                    handFiles.add(line);
                    line = br.readLine();
                }
            } finally {
                br.close();
            }
            return handFiles;
        } catch (Exception e) {
            System.out.println("error happened while trying to getAllHands2Names");
        }
        return null;
    }

}