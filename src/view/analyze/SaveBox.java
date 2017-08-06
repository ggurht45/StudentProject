package view.analyze;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class SaveBox {

    // todo put everything in fxml

    //Create variable
    public static boolean saved = false;
    public static String name = "";
    public static String comments = "";
    public static boolean passFail = true;
    public static String directory = "General";

    public static String display(String gestureName) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Saving Gesture");
        window.setMinWidth(350);


        //name the file
        Label nameLabel = new Label();
        nameLabel.setText(gestureName);
//        TextField nameTextfield = new TextField();

        //comments. todo: fix later. should not have comments like this.
        Label label = new Label();
        label.setText("Any comments:");
        TextField textField = new TextField();

        //save to directory
//        Label directorylabel = new Label();
//        directorylabel.setText("Output Folder");
//        TextField directoryTextField = new TextField();
//        directoryTextField.setText(initialDirectory);


        //Create two buttons
        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #669900; -jfx-button-type: RAISED");
        //Clicking will set answer and close window
        saveButton.setOnAction(e -> {
//            name = nameTextfield.getText();
            comments = textField.getText();
//            directory = directoryTextField.getText();
            saved = true;
            window.close();
        });


        Label resultLabel = new Label();
        resultLabel.setText("Result: ");

        //radio buttons for left right
        ToggleGroup lfGroup = new ToggleGroup();
        RadioButton leftRadio = new RadioButton("Yes");
        leftRadio.setToggleGroup(lfGroup);
        leftRadio.setOnAction(e -> passFail = true);
        RadioButton rightRadio = new RadioButton("No");
        rightRadio.setToggleGroup(lfGroup);
        rightRadio.setOnAction(e -> passFail = false);


        VBox layout = new VBox(10);

        //Add buttons
//        layout.getChildren().addAll(nameLabel, nameTextfield, label, textField, directorylabel, directoryTextField, leftRadio, rightRadio, saveButton);
        layout.getChildren().addAll(nameLabel, label, textField, resultLabel, leftRadio, rightRadio, saveButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();


        return comments;
    }
}