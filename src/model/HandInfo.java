package model;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class HandInfo implements java.io.Serializable {
    public String handFile;
    public String comments;
    public String result;
    public int gestureType;

    public SimpleStringProperty handFile2;
    public SimpleStringProperty comments2;
    public SimpleStringProperty result2;
    public SimpleIntegerProperty gestureType2;

    public HandInfo(String hf, String cm, String res) {
        handFile = hf;
        comments = cm;
        result = res;
        gestureType = -1;

        //simpleProperties.. overengineered solution
        handFile2 = new SimpleStringProperty(hf);
        comments2 = new SimpleStringProperty(cm);
        result2 = new SimpleStringProperty(res);
        gestureType2 = new SimpleIntegerProperty(-1);
    }


    public String getHandFile() {
        return handFile;
    }

    public void setHandFile(String handFile) {
        this.handFile = handFile;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getGestureType() {
        return gestureType;
    }

    public void setGestureType(int gestureType) {
        this.gestureType = gestureType;
    }

    public String getHandFile2() {
        return handFile2.get();
    }

    public SimpleStringProperty handFile2Property() {
        return handFile2;
    }

    public void setHandFile2(String handFile2) {
        this.handFile2.set(handFile2);
    }

    public String getComments2() {
        return comments2.get();
    }

    public SimpleStringProperty comments2Property() {
        return comments2;
    }

    public void setComments2(String comments2) {
        this.comments2.set(comments2);
    }

    public String getResult2() {
        return result2.get();
    }

    public SimpleStringProperty result2Property() {
        return result2;
    }

    public void setResult2(String result2) {
        this.result2.set(result2);
    }

    public int getGestureType2() {
        return gestureType2.get();
    }

    public SimpleIntegerProperty gestureType2Property() {
        return gestureType2;
    }

    public void setGestureType2(int gestureType2) {
        this.gestureType2.set(gestureType2);
    }

    @Override
    public String toString() {
        String s = "Filename: " + handFile + "\n\tComments: " + comments + "\n\tResult: " + result;
        return s;
    }
}