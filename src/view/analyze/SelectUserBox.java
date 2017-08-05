package view.analyze;

import com.jfoenix.controls.JFXComboBox;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class SelectUserBox {


    public static String id;
    public static String currentDate;
    public static String dob;
    public static String edu;


    public static boolean display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Enter or Select User");
        window.setMinWidth(350);


        //drop down
        JFXComboBox<Label> jfxCombo = new JFXComboBox<>();
        jfxCombo.getItems().add(new Label("ID 1"));
        jfxCombo.getItems().add(new Label("ID 2"));
        jfxCombo.getItems().add(new Label("ID 3"));
        jfxCombo.getItems().add(new Label("ID 4"));
        jfxCombo.setPromptText("Select Previous User ID");


        Label idLabel = new Label();
        idLabel.setText("ID");
        TextField idField = new TextField();

        Label currentDateLabel = new Label();
        currentDateLabel.setText("Date");
        TextField currentDateField = new TextField();

        Label dobLabel = new Label();
        dobLabel.setText("Date of Birth");
        TextField dobField = new TextField();

        Label eduLabel = new Label();
        eduLabel.setText("Education");
        TextField eduField = new TextField();

        //Create two buttons
        Button enterTestingBtn = new Button("Enter Testing");
        enterTestingBtn.setStyle("-fx-background-color: #669900; -jfx-button-type: RAISED");

        //Clicking will set answer and close window
        enterTestingBtn.setOnAction(e -> {
            id = idField.getText();
            currentDate = currentDateField.getText();
            dob = dobField.getText();
            edu = eduField.getText();

            User u = new User(id, currentDate, dob, edu);
//            System.out.println("csv header of user class: " + User.csvHeader());
//            System.out.println("csv line of user: " + u.csvLine());
            window.close();
        });


        VBox layout = new VBox(10);

        //Add buttons
        layout.getChildren().addAll(jfxCombo, idLabel, idField, currentDateLabel, currentDateField, dobLabel, dobField, eduLabel, eduField, enterTestingBtn);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();


        return ((id == null) || (id == "")) ? false : true;
    }
}