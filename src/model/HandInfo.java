package model;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

//todo since not doing serializing now, need to clean up this code. dont need another class for properties. can just use this one.
public class HandInfo implements java.io.Serializable {
    public String name;
    public String handFile;
    public String gestureType;
    public String comments;
    public String result;

    public HandInfo(String n, String hf, String gt, String cm, String res) {
        name = n;
        handFile = hf;
        gestureType = gt;
        comments = cm;
        result = res;
    }

    //temporary for testing..
    public static String getCSVHeader() {
        return "name, filePath, gesturetype, comments, result";
    }

    public String getCommaSeperatedToString() {
        return name.trim() + ", " + handFile.trim() + ", " + gestureType.trim() + ", " + comments.trim() + ", " + result.trim();
    }

    @Override
    public String toString() {
        String s = "Filepath: " + handFile + "\n\tName: " + name + "\n\tGesture Type: " + gestureType + "\n\tComments: " + comments + "\n\tResult: " + result;
        return s;
    }
}