package view.analyze;

import com.leapmotion.leap.Hand;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.SerializedTargetHand;
import view.LeapUIApp;
import view.anatomy.UIHand;
import view.anatomy.UIHand_Simple;


public class LoadGesturesScene extends Group {

    public static UIHand loadedHandUI;

    public LoadGesturesScene(LeapUIApp app) {
        super();

        //button to go back to main scene
        Button button2 = new Button("Go Back");
        button2.setOnAction(e -> app.window.setScene(app.scene));
        button2.setTranslateX(app.ScreenWidth * 1 / 5);
        button2.setTranslateY(app.ScreenHeight * 4 / 5);
        button2.setPrefHeight(50);
        button2.setFont(Font.font(app.STYLESHEET_MODENA, FontWeight.BOLD, 15));


        //set up the loadedHandUI
        loadedHandUI = new UIHand_Simple(Color.GREEN.darker(), false);
        loadedHandUI.setVisible(false);

        //button to load hand into the scene
        Button loadHandButton = new Button("Load Hand");
        loadHandButton.setOnAction(e -> {
            System.out.println("load hand button clicked");
            Hand h = getHandFromString("targets/2017-06-12 12-13-58.hand");
            System.out.println("seemed to have gotten hand, about to turn on visibility");
            loadedHandUI.setLoc(h);
            loadedHandUI.setVisible(true);

        });
        loadHandButton.setTranslateX(app.ScreenWidth * 2 / 5);
        loadHandButton.setTranslateY(app.ScreenHeight * 4 / 5);
        loadHandButton.setPrefHeight(50);
        loadHandButton.setFont(Font.font(app.STYLESHEET_MODENA, FontWeight.BOLD, 15));

        //Layout 2
        Group layout2 = new Group();
        layout2.getChildren().addAll(button2, loadHandButton, loadedHandUI);

        this.getChildren().add(layout2);


    }

    public Hand getHandFromString(String s) {
        Hand h = null;
        try {
            h = SerializedTargetHand.readFromFile(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return h;
    }
}
