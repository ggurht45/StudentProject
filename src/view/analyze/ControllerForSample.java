package view.analyze;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import view.LeapUIApp;


public class ControllerForSample{

    private LeapUIApp app;

    @FXML
    private Label theSpecialLabel;

    @FXML
    private Button mySpecialButtonYea;

    @FXML
    void sayaHelloa(ActionEvent event) {
        System.out.println("button was clicked. btw, did some design in scenebuilder bro");
    }

    public void setMainApp(LeapUIApp app){
        this.app = app;
    }



}