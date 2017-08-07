package view.analyze;

import javafx.beans.property.SimpleStringProperty;

public class User {

    public SimpleStringProperty id;
    public SimpleStringProperty currentDate;
    public SimpleStringProperty dob;
    public SimpleStringProperty edu;

    public User(String id, String currentDate, String dob, String edu) {
        this.id = new SimpleStringProperty(id);
        this.currentDate = new SimpleStringProperty(currentDate);
        this.dob = new SimpleStringProperty(dob);
        this.edu = new SimpleStringProperty(edu);
    }

    public User(SimpleStringProperty id, SimpleStringProperty currentDate, SimpleStringProperty dob, SimpleStringProperty edu) {
        this.id = id;
        this.currentDate = currentDate;
        this.dob = dob;
        this.edu = edu;
    }

    public static String csvHeader() {
        return "id, currentDate, dob, edu";
    }

    public String csvLine() {
        return getId().trim() + ", " + getCurrentDate().trim() + ", " + getDob().trim() + ", " + getEdu().trim();
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getCurrentDate() {
        return currentDate.get();
    }

    public SimpleStringProperty currentDateProperty() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate.set(currentDate);
    }

    public String getDob() {
        return dob.get();
    }

    public SimpleStringProperty dobProperty() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob.set(dob);
    }

    public String getEdu() {
        return edu.get();
    }

    public SimpleStringProperty eduProperty() {
        return edu;
    }

    public void setEdu(String edu) {
        this.edu.set(edu);
    }

    public String toString(){
        return "User: " + this.csvLine() + " <<";
    }
}
