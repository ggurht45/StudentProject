package view.analyze;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerForSample implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Sample.fxml view is now loaded!");
    }


    @FXML
    private void sayaHelloa(){
        System.out.println("yo yo wassup?!, the button has been clicked");
    }


}