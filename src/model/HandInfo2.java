package model;


import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class HandInfo2 implements java.io.Serializable {
    public String name;
    public String handFile;
    public String gestureType;
    public String comments;
    public String result;

    public SimpleStringProperty name2;
    public SimpleStringProperty handFile2;
    public SimpleStringProperty gestureType2;
    public SimpleStringProperty comments2;
    public SimpleStringProperty result2;

    public HandInfo2(HandInfo hf){
        this(hf.name, hf.handFile, hf.gestureType, hf.comments, hf.result);
    }

    public HandInfo2(String n, String hf, String gt, String cm, String res) {
        name = n;
        handFile = hf;
        gestureType = gt;
        comments = cm;
        result = res;


        //simpleProperties.. overengineered solution
        name2 = new SimpleStringProperty(n);
        handFile2 = new SimpleStringProperty(hf);
        comments2 = new SimpleStringProperty(cm);
        result2 = new SimpleStringProperty(res);
        gestureType2 = new SimpleStringProperty(gt);
    }

    //convert handinfo2 into handinfo
    public HandInfo convertToHandInfo(){
        //convert the properties to strings and construct handinfo object
        return new HandInfo(this.getName2(), this.getHandFile2(), this.getGestureType2(), this.getComments2(), this.getResult2());
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName2() {
        return name2.get();
    }

    public SimpleStringProperty name2Property() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2.set(name2);
    }

    public String getGestureType() {
        return gestureType;
    }

    public void setGestureType(String gestureType) {
        this.gestureType = gestureType;
    }

    public String getGestureType2() {
        return gestureType2.get();
    }

    public SimpleStringProperty gestureType2Property() {
        return gestureType2;
    }

    public void setGestureType2(String gestureType2) {
        this.gestureType2.set(gestureType2);
    }

    @Override
    public String toString() {
        //todo fix this string.
        String s = "HI2 toString\nFilename: " + handFile + "\n\tComments: " + comments + "\n\tResult: " + result;
        return s;
    }
}