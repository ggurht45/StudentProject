package model;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class HandInfo implements java.io.Serializable {
    public String handFile;
    public String comments;
    public String result;

    public HandInfo(String hf, String cm, String res) {
        handFile = hf;
        comments = cm;
        result = res;
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
        String s = "Filename: " + handFile + "\n\tComments: " + comments + "\n\tResult: " + result;
        return s;
    }
}