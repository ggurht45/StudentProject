package view.analyze;


import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Vector;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import model.SerializedTargetHand;
import view.DebugHelper;
import view.LeapUIApp;
import view.ViewMath;
import view.anatomy.UIHand;
import view.anatomy.UIHand_Simple;
import view.anatomy.UIHand_SuperSimple;

import javax.swing.text.View;

public class TestScene3 {
    private LeapUIApp app;
    private static Scene scene;


    public TestScene3(LeapUIApp app) {
        this.app = app;


        //should create a very simple scene3

        //Button 2
        Button button2 = new Button("This sucks, go back to scene 2");
        button2.setOnAction(e -> app.window.setScene(app.scene2));


        StackPane layout2 = new StackPane();
        layout2.getChildren().add(button2);
        scene = new Scene(layout2, app.ScreenWidth, app.ScreenHeight);

    }

    public Scene getScene() {
        return scene;
    }


}




