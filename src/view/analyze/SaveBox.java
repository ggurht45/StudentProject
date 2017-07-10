package view.analyze;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class SaveBox {

    //Create variable
    public static String comments;
    public static boolean passFail;

    public static String display(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(350);
        Label label = new Label();
        label.setText(message);

        TextField textField = new TextField();

        //Create two buttons
        Button saveButton = new Button("Save");

        //Clicking will set answer and close window
        saveButton.setOnAction(e -> {
            comments = textField.getText();
            window.close();
        });


        //radio buttons for left right
        ToggleGroup lfGroup = new ToggleGroup();
        RadioButton leftRadio = new RadioButton("Passed");
        leftRadio.setToggleGroup(lfGroup);
        leftRadio.setOnAction(e -> passFail = true);
        RadioButton rightRadio = new RadioButton("Failed");
        rightRadio.setToggleGroup(lfGroup);
        rightRadio.setOnAction(e -> passFail = false);


        VBox layout = new VBox(10);

        //Add buttons
        layout.getChildren().addAll(label, textField, leftRadio, rightRadio, saveButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();


        return comments;
    }
}