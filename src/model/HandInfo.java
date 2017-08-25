package model;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

//todo since not doing serializing now, need to clean up this code. dont need another class for properties. can just use this one.
public class HandInfo implements java.io.Serializable {
    public String name;
    public String handFile;
    public String comments;
    public String result;

    public HandInfo(String n, String hf, String cm, String res) {
        name = n;
        handFile = hf;
        comments = cm;
        result = res;
    }

    //temporary for testing..
    public static String getCSVHeader() {
        return "gestureName, filePath, comments, result";
    }

    public String getCommaSeperatedToString() {
        if ((comments.trim()).equalsIgnoreCase("")){
            comments = "na";
        }
        //if comments = 2 result -> no.

//        if(name.trim().equalsIgnoreCase("gesture10Right")){
        return name.trim() + ", " + handFile.trim() + ", " + comments.trim() + ", " + result.trim();
//        }
//        return name.trim() + ", " + handFile.trim() + ",\t " + comments.trim() + ", " + result.trim();
    }

    @Override
    public String toString() {
        String s = "Filepath: " + handFile + "\n\tName: " + name + "\n\tComments: " + comments + "\n\tResult: " + result;
        return s;
    }
}