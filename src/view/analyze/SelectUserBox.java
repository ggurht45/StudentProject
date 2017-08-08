package view.analyze;

import com.jfoenix.controls.JFXComboBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import model.CsvHelper;
import view.LeapUIApp;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectUserBox {


    public static String id;
    public static String currentDate;
    public static String dob;
    public static String edu;

    public static boolean successfulClose = false;
    public static User selectedUser = null;


    public static void display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Select or Create User");
        window.setMinWidth(350);


        //drop down
        JFXComboBox<Label> jfxCombo = new JFXComboBox<>();
        //get users from file and then populate this with their ids.
        ArrayList<User> users = CsvHelper.readUsersFromFile(LeapUIApp.ALL_USERS_FILE);
        HashMap<String, User> usersHashMap = new HashMap<>();
        for (User u : users) {
            String id = u.getId();
            jfxCombo.getItems().add(new Label(id));
            usersHashMap.put(id, u);
        }
        jfxCombo.setPromptText("Select Previous User ID");

        jfxCombo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String selectedId = jfxCombo.getValue().getText();
                System.out.println("selected: " + selectedId);
                selectedUser = usersHashMap.get(selectedId);
            }
        });


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


        HBox hBox = new HBox(15);

        //Create two buttons
        Button createNewUserBtn = new Button("Create New User");
        createNewUserBtn.setStyle("-fx-background-color: #669900; -jfx-button-type: RAISED");
        //Clicking will set answer and close window
        createNewUserBtn.setOnAction(e -> {
            id = idField.getText();
            currentDate = currentDateField.getText();
            dob = dobField.getText();
            edu = eduField.getText();

            //java is weird. checking string is empty is difficult
            if (id != null && !id.isEmpty()) {
                System.out.println("id is not empty...");
                successfulClose = true;
                //write to csv
                User u = new User(id, currentDate, dob, edu);
                CsvHelper.writeUserToFile(LeapUIApp.ALL_USERS_FILE, u);
                selectedUser = u;
            } else {
                successfulClose = false;
            }

            window.close();
        });

        //button to load user
        Button loadUserBtn = new Button("Load Selected User");
        loadUserBtn.setStyle("-fx-background-color: #669900; -jfx-button-type: RAISED");
        loadUserBtn.setOnAction(e -> {
            if (selectedUser != null) {
                successfulClose = true;
            } else {
                successfulClose = false;
            }
            window.close();
        });
        hBox.getChildren().addAll(createNewUserBtn, loadUserBtn);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 50, 50, 50));

        //Add buttons
        layout.getChildren().addAll(jfxCombo, idLabel, idField, currentDateLabel, currentDateField, dobLabel, dobField, eduLabel, eduField, hBox);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();


//        return ((id == null) || (id == "")) ? false : true;
    }
}