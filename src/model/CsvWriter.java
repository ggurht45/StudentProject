package model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CsvWriter {

    //temporary for testing..
    public static String getCSVHeader(){
        return "filePath, comments, result";
    }

    public static String getCommaSeperatedToString(HandInfo h){
        return h.handFile + ", " + h.comments + ", " + h.result;
    }
    //END temporary for testing..



    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

    //CSV file header
    private static final String FILE_HEADER = getCSVHeader();//HandInfo.getCSVHeader();

    public static void writeCsvFile(String fileName, ArrayList<HandInfo> hands) {
        System.out.println("*** in writer.\n fileName: "+ fileName + "hands(0): " + hands.get(0) + "\n***");

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(fileName);
            fileWriter.append(FILE_HEADER.toString());      //Write the CSV file header
            fileWriter.append(NEW_LINE_SEPARATOR);          //Add a new line separator after the header

            //Write a new student object list to the CSV file
            for (HandInfo h : hands) {
                System.out.println("get comma line from handinfo");
                String s =getCommaSeperatedToString(h);// h.getCommaSeperatedToString();
                fileWriter.append(s);
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

            System.out.println("CSV file was created successfully");

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
}