package model;


import java.io.*;
import java.nio.file.Files;
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
    public static void Save4(Frame f, String outputFolder, String comments, boolean passFail) throws IOException {
        System.out.println("inside save4");
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
        //printer for comments
        PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(fileNameWithPath + "_comments.txt", true)));
        printer.println(comments);
        printer.close();

        //printer for pass/fail
        PrintWriter printer2 = new PrintWriter(new BufferedWriter(new FileWriter(fileNameWithPath + "_result.txt", true)));
        printer2.println(result);
        printer2.close();

        //ideal printer
        PrintWriter printer3 = new PrintWriter(new BufferedWriter(new FileWriter(fileNameWithPath + "_Info.txt", true)));
        printer3.println(fileNameWithPath + ".hand information");
        printer3.println("\tComments: " + comments);
        printer3.println("\tResult: " + result);
        printer3.println();
        printer3.close();


        //create handInfo object and serialize.
        HandInfo handInfo = new HandInfo(fileNameWithPath + ".hand", comments, result);
//        try {
//            FileOutputStream fileOut = new FileOutputStream(fileNameWithPath + ".ser");
//
//            ObjectOutputStream out = new ObjectOutputStream(fileOut);
//            out.writeObject(handInfo);
//            out.close();
//            fileOut.close();
//            System.out.printf("Serialized handInfo");
//        } catch (IOException i) {
//            i.printStackTrace();
//        }

        //check if a total list exists
        if (new File(outputFolder + "_allHandsOnDeck.ser").isFile()) {
            System.out.println("arraylist of hands info file already exists! ");

            //try to get the all hands file and extract the arraylist and add onto it.
            ArrayList<HandInfo> arraylist;
            try {
                FileInputStream fis = new FileInputStream(outputFolder + "_allHandsOnDeck.ser");
                ObjectInputStream ois = new ObjectInputStream(fis);
                arraylist = (ArrayList) ois.readObject();
                System.out.println("got arraylist from serialized file, size: " + arraylist.size());
                ois.close();
                fis.close();
                //add the new HandInfo object to it.
                arraylist.add(handInfo);

                //then serialize it back and save it to the file again.
                serialize(arraylist, outputFolder + "_allHandsOnDeck.ser");

            } catch (IOException ioe) {
                ioe.printStackTrace();
                return;
            } catch (ClassNotFoundException c) {
                System.out.println("Class not found");
                c.printStackTrace();
                return;
            }


        } else {
            System.out.println("arraylist handsInfo file doesnt exist yet. lets create it");
            //create arraylist, add hand info objct to it, serialize arraylist
            ArrayList<HandInfo> al = new ArrayList<>();
            al.add(handInfo);

            try {
                FileOutputStream fos = new FileOutputStream(outputFolder + "_allHandsOnDeck.ser");
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(al);
                oos.close();
                fos.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        //print if arraylist size is three
//            if(arraylist.size()>2){
        printAllHandOnDeckArrayList(outputFolder);
//            }


        //deserializeArrayList
//        HandInfo checkHandInfo = null;
//        try {
//            FileInputStream fileIn = new FileInputStream(fileNameWithPath + ".ser");
//            ObjectInputStream in = new ObjectInputStream(fileIn);
//            checkHandInfo = (HandInfo) in.readObject();
//            in.close();
//            fileIn.close();
//        } catch (IOException i) {
//            i.printStackTrace();
//            return;
//        } catch (ClassNotFoundException c) {
//            System.out.println("HandInfo not found");
//            c.printStackTrace();
//            return;
//        }

    }

    public static void serialize(ArrayList<HandInfo> ar, String fn) {
        //check if fn isDirectory;
        if ((new File(fn)).isDirectory()) {
            fn = getAllHandsFileName(fn);
        }
        try {
            FileOutputStream fos = new FileOutputStream(fn);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(ar);
            oos.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static ArrayList<HandInfo> deserializeArrayList(String fn) {
        ArrayList<HandInfo> arraylist = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(fn);
            ObjectInputStream ois = new ObjectInputStream(fis);
            arraylist = (ArrayList) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
            return null;
        }
        return arraylist;
    }


    //helper method
    public static String getFolderPathHelperMethod(String shortNameForFolder) {
        return "dataOutput/" + shortNameForFolder + "/";
    }

    public static String getAllHandsFileName(String folderName) {
        return folderName + "_allHandsOnDeck.ser";
    }

    //clean up this method later
    public static ArrayList<HandInfo> getAllHandsInfoInFolder(String folderName) {
        return deserializeArrayList(folderName + "_allHandsOnDeck.ser");
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


    public static Hand getHandFromString(String s) {
        Hand h = null;
        try {
            h = SerializedTargetHand.readFromFile(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return h;
    }

    public static void storeToCSV(String path, ArrayList<HandInfo> hands) {
        System.out.println("serializedHand: saving data to csv");
        String fileName = System.getProperty("user.home") + "/student.csv";
        System.out.println("filename stared in home folder?: " + fileName);
        CsvHelper.writeCsvFile(path + "_allHandsOnDeck.csv", hands);

    }

    public static ArrayList<HandInfo> readFromCSV(String fullFileName) {
        System.out.println("serializedHand: reading from csv");
        return CsvHelper.readCsvFile(fullFileName);
    }

}
