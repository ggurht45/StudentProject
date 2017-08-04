package model;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Leap;
import view.LeapUIApp;


public class SerializedTargetHand {
    public static final int MAX_FRAMES = 10;


    public static void Save(Frame f) throws IOException {
        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        String fileName = LeapUIApp.TargetsPath + sdf.format(cal.getTime()) + ".hand";
        byte[] serializedFrame = f.serialize();
        Files.write(Paths.get(fileName), serializedFrame);
        PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(LeapUIApp.TargetHandsFile, true)));
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
        PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(LeapUIApp.TargetHandsFile, true)));
        printer.println(fileName);
        printer.close();
    }

    //use this function to save to a specific folder that may need to be created.
    public static void Save4(Frame f, String name, String outputFolder, String gestureType, String comments, boolean passFail) throws IOException {
//        System.out.println("inside save4");
        String result = (passFail ? "Passed" : "Failed");

        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

        String fileNameWithPath = outputFolder + sdf.format(cal.getTime());

        File fileDirectory = new File(fileNameWithPath + ".hand");
        fileDirectory.getParentFile().mkdirs();
//        System.out.println(fileDirectory);
//        System.out.println("full path: " + fileDirectory.getAbsolutePath());
//        System.out.println("getPath: " + fileDirectory.getPath());
        byte[] serializedFrame = f.serialize();
        Files.write(Paths.get(fileDirectory.getPath()), serializedFrame);

        //todo clean up these later. they are not needed anymore.
        //printer for comments
//        PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(fileNameWithPath + "_comments.txt", true)));
//        printer.println(comments);
//        printer.close();
//
//        //printer for pass/fail
//        PrintWriter printer2 = new PrintWriter(new BufferedWriter(new FileWriter(fileNameWithPath + "_result.txt", true)));
//        printer2.println(result);
//        printer2.close();
//
        //ideal printer
        PrintWriter printer3 = new PrintWriter(new BufferedWriter(new FileWriter(fileNameWithPath + "_Info.txt", true)));
        printer3.println(fileNameWithPath + ".hand information");
        printer3.println("\tName: " + name);
        printer3.println("\tGesture Type: " + gestureType);
        printer3.println("\tComments: " + comments);
        printer3.println("\tResult: " + result);
        printer3.println();
        printer3.close();


        //create handInfo object and serialize.
        HandInfo handInfo = new HandInfo(name, fileNameWithPath + ".hand", gestureType, comments, result);

        //check if a total list exists
        String fullFileName = outputFolder + "_allHandsOnDeck.csv";
        CsvHelper.writeHandInfoToFile(fullFileName, handInfo);


//        if (new File(fullFileName).isFile()) {
//            System.out.println("csv file exists, add on to it");
//            ArrayList<HandInfo> arraylist = readFromCSV(fullFileName);
//            arraylist.add(handInfo);
//            System.out.println("arraylist after new hand was added to it: " + arraylist);
//            //instead of serializing, use csv instead
//            writeToCSV(fullFileName, arraylist);
//
//        } else {
//            System.out.println("create csv file and add one item to it");
//            ArrayList<HandInfo> arraylist = new ArrayList<>();
//            arraylist.add(handInfo);
//            writeToCSV(fullFileName, arraylist);
//        }

//        printAllHandOnDeckArrayList(outputFolder);

    }

//    public static void serialize(ArrayList<HandInfo> ar, String fn) {
//        //check if fn isDirectory;
//        if ((new File(fn)).isDirectory()) {
//            fn = getAllHandsFileName(fn);
//        }
//        try {
//            FileOutputStream fos = new FileOutputStream(fn);
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//            oos.writeObject(ar);
//            oos.close();
//            fos.close();
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//    }
//
//    public static ArrayList<HandInfo> deserializeArrayList(String fn) {
//        ArrayList<HandInfo> arraylist = new ArrayList<>();
//        try {
//            FileInputStream fis = new FileInputStream(fn);
//            ObjectInputStream ois = new ObjectInputStream(fis);
//            arraylist = (ArrayList) ois.readObject();
//            ois.close();
//            fis.close();
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//            return null;
//        } catch (ClassNotFoundException c) {
//            System.out.println("Class not found");
//            c.printStackTrace();
//            return null;
//        }
//        return arraylist;
//    }


    //helper method
    public static String getFolderPathHelperMethod(String shortNameForFolder) {
        return "dataOutput/" + shortNameForFolder + "/";
    }

    //helper method to get the csv file associated with a certain folder
    public static String getCSVFilePathForFolder(String shortNameForFolder) {
        return getFolderPathHelperMethod(shortNameForFolder) + "_allHandsOnDeck.csv";
    }


//    public static String getAllHandsFileName(String folderName) {
//        return folderName + "_allHandsOnDeck.ser";
//    }


    public static ArrayList<HandInfo> getAllHandsInfoInFolder(String folderName) {
        //return hands built from csv file instead of .ser
        String fullName = folderName + "_allHandsOnDeck.csv";
        return readFromCSV(fullName);
    }

    public static void printAllHandOnDeckArrayList(String outputFolder) {
        ArrayList<HandInfo> arraylist = getAllHandsInfoInFolder(outputFolder);
        //print arraylist
        System.out.println("------------AllHandsOnDeck arraylist---------");
        for (int i = 0; i < arraylist.size(); i++) {
            System.out.println(arraylist.get(i).toString());
        }
        System.out.println("END------------AllHandsOnDeck arraylist---------");
    }

    //save the 10 specific gestures; id = 1-10; side = l/r
    public static void Save3(Frame f, String gestureId, boolean leftHand) throws IOException {
        System.out.println("inside save3");
        String side;
        String indexFile;
        if (leftHand) {
            side = "Left";
            indexFile = LeapUIApp.LeftGesturesFile;
        } else {
            side = "Right";
            indexFile = LeapUIApp.RightGesturesFile;
        }
        String fileName = LeapUIApp.Targets2Path + gestureId + side + ".hand";
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

        File inFile = new File(LeapUIApp.TargetHandsFile);
        if (!inFile.exists())
            throw new Exception("In file not found :" + LeapUIApp.TargetHandsFile); // No input file

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

    public static String extractGestureTypeFromFilePath(String path) {
        String[] tokens = path.split("/");//todo maybe this wont work on other OS's, maybe use a better thing than /
        String lastToken = tokens[tokens.length - 1];

        tokens = lastToken.split(Pattern.quote(".")); //assume gestureType can't have "." in it
        String gestureType = tokens[0];
//        System.out.println("extracted gestureType: " + gestureType);

        return gestureType;
    }

    public static ArrayList<Hand> getAllHands2(String fileName) {
        //also create a hashmap that is stored in main app, that links each hand to a gesture type.
        LeapUIApp.handToGestureType = new HashMap<>();
        try {
            File inFile = new File(fileName);
            if (!inFile.exists())
                throw new Exception("In file not found :" + fileName); // No input file

            BufferedReader br = new BufferedReader(new FileReader(inFile));

            ArrayList<Hand> hands = new ArrayList<>();

            try {
                String line = br.readLine();
                while (line != null) {
                    Hand h = readFromFile(line);
                    String gestureType = extractGestureTypeFromFilePath(line);
                    LeapUIApp.handToGestureType.put(h, gestureType);
                    hands.add(h);
                    line = br.readLine();
                }
//                System.out.println("created hashmap of hand->gestureType" + LeapUIApp.handToGestureType);
            } finally {
                br.close();
            }
            return hands;
        } catch (Exception e) {
            System.out.println("error happened while trying to getAllHands2");
            e.printStackTrace();
            System.out.println("returning null for Arraylist of hands!!!!!!!!!!!!!");
            return null;
        }

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


    public static Hand getHandFromString(String s) {
        Hand h = null;
        try {
            s = s.trim(); //todo temporary fix. fix this in handInfo. whenever returning string properties, trim them

            h = SerializedTargetHand.readFromFile(s);
        } catch (Exception e) {
            System.out.println("ERROR, failed to load hand from this path>>>>" + s + "<<<<");
            e.printStackTrace();
        }
        return h;
    }

    public static void writeToCSV(String fullFilePath, ArrayList<HandInfo> hands) {
//        System.out.println("NEED TO CHECK THIS OVER LATER");
        //todo need to check over this later??
        CsvHelper.writeCsvFile(fullFilePath, hands);

    }

    public static ArrayList<HandInfo> readFromCSV(String fullFileName) {
//        System.out.println("serializedHand: reading from csv");
        return CsvHelper.readCsvFile(fullFileName);
    }

}
