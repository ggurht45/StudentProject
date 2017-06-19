package view.analyze;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import view.LeapUIApp;

public class LoadGesturesScene extends Group {

    public LoadGesturesScene(LeapUIApp app) {
        super();

        //button to go back to main scene
        Button button2 = new Button("Go Back");
        button2.setOnAction(e -> app.window.setScene(app.scene));
        button2.setTranslateX(app.ScreenWidth * 1 / 5);
        button2.setTranslateY(app.ScreenHeight * 4 / 5);
        button2.setPrefHeight(50);
        button2.setFont(Font.font(app.STYLESHEET_MODENA, FontWeight.BOLD, 15));

        //Layout 2
        StackPane layout2 = new StackPane();
        layout2.getChildren().add(button2);

        this.getChildren().add(layout2);

    }


}
