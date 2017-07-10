package model;


public class HandInfo implements java.io.Serializable {
    public String handFile;
    public String comments;
    public String result;

    public HandInfo(String hf, String cm, String res){
        handFile = hf;
        comments = cm;
        result = res;
    }

    @Override
    public String toString(){
        String s = "Filename: " + handFile + "\n\tComments: " + comments + "\n\tResult: " + result;
        return s;
    }
}