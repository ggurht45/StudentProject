package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvHelper {

    //temporary for testing..
    public static String getCSVHeader() {
        return "filePath, comments, result";
    }

    public static String getCommaSeperatedToString(HandInfo h) {
        return h.handFile + ", " + h.comments + ", " + h.result;
    }
    //END temporary for testing..


    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

    //CSV file header
    private static final String FILE_HEADER = getCSVHeader();//HandInfo.getCSVHeader();

    public static void writeCsvFile(String fileName, ArrayList<HandInfo> hands) {
        System.out.println("*** in writer.\n fileName: " + fileName + "\nhands(0): " + hands.get(0) + "\n***");

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(fileName);
            fileWriter.append(FILE_HEADER.toString());      //Write the CSV file header
            fileWriter.append(NEW_LINE_SEPARATOR);          //Add a new line separator after the header

            //Write a new student object list to the CSV file
            for (HandInfo h : hands) {
                String s = getCommaSeperatedToString(h);// h.getCommaSeperatedToString();
                fileWriter.append(s);
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }
    }


    //Student attributes index
    private static final int HANDINFO_FILENAME_IDX = 0;
    private static final int HANDINFO_COMMENTS_IDX = 1;
    private static final int HANDINFO_RESULTS_IDX = 2;

    public static ArrayList<HandInfo> readCsvFile(String fileName) {

        BufferedReader fileReader = null;
        ArrayList hands = null;

        try {
            //initial stuff
            String line = "";
            fileReader = new BufferedReader(new FileReader(fileName));            //Create the file reader
            hands = new ArrayList();                                              //list of hands
            fileReader.readLine();                                                //Read the CSV file header to skip it

            //Read the file line by line starting from the second line
            while ((line = fileReader.readLine()) != null) {
                String[] tokens = line.split(COMMA_DELIMITER);//Get all tokens available in line
                if (tokens.length > 0) {
                    //Create a new student object and fill his  data
                    HandInfo h = new HandInfo(tokens[HANDINFO_FILENAME_IDX], tokens[HANDINFO_COMMENTS_IDX], tokens[HANDINFO_RESULTS_IDX]);
                    hands.add(h);
                }
            }
            return hands;       //return arraylist of hands
        } catch (Exception e) {
            System.out.println("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
            } catch (IOException e) {
                System.out.println("Error while closing fileReader !!!");
                e.printStackTrace();
            }
        }

        return hands;
    }
}