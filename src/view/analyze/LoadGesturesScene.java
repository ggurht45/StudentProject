package view.analyze;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import view.LeapUIApp;

public class LoadGesturesScene extends Group {

    public LoadGesturesScene(LeapUIApp app) {
        super();

                //Button 2
        Button button2 = new Button("This sucks, go back to scene 1");
        button2.setOnAction(e -> app.window.setScene(app.scene));

        //Layout 2
        StackPane layout2 = new StackPane();
        layout2.getChildren().add(button2);

        this.getChildren().add(layout2);

    }


}
