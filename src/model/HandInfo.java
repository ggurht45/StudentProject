package model;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

//todo since not doing serializing now, need to clean up this code. dont need another class for properties. can just use this one.
public class HandInfo implements java.io.Serializable {
    public String name;
    public String handFile;
    public String comments;
    public String result;

    public HandInfo(String name, String hf, String cm, String res) {
        this.name = name;
        handFile = hf;
        comments = cm;
        result = res;
    }

    //temporary for testing..
    public static String getCSVHeader() {
        return "name, filePath, comments, result";
    }

    public String getCommaSeperatedToString() {
        return name.trim() + ", " + handFile.trim() + ", " + comments.trim() + ", " + result.trim();
    }

//    public String getHandFile() {
//        return handFile;
//    }
//
//    public void setHandFile(String handFile) {
//        this.handFile = handFile;
//    }
//
//    public String getComments() {
//        return comments;
//    }
//
//    public void setComments(String comments) {
//        this.comments = comments;
//    }
//
//    public String getResult() {
//        return result;
//    }
//
//    public void setResult(String result) {
//        this.result = result;
//    }

    @Override
    public String toString() {
        String s = "Filepath: " + handFile + "\n\tName: " + name + "\n\tComments: " + comments + "\n\tResult: " + result;
        return s;
    }
}