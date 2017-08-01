package view.analyze;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class InfoBox {

    //Create variable
    public static String name;
    public static boolean leftHand;

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
        saveButton.setStyle("-fx-background-color: #669900; -jfx-button-type: RAISED");

        //Clicking will set answer and close window
        saveButton.setOnAction(e -> {
            name = textField.getText();

            window.close();
        });


        //radio buttons for left right
        ToggleGroup lfGroup = new ToggleGroup();
        RadioButton leftRadio = new RadioButton("Left Hand");
        leftRadio.setToggleGroup(lfGroup);
        leftRadio.setOnAction(e -> leftHand = true);
        RadioButton rightRadio = new RadioButton("Right Hand");
        rightRadio.setToggleGroup(lfGroup);
        rightRadio.setOnAction(e -> leftHand = false);


        VBox layout = new VBox(10);

        //Add buttons
        layout.getChildren().addAll(label, textField, leftRadio, rightRadio, saveButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();


        return name;
    }
}