package view.analyze;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import model.SerializedTargetHand;
import view.LeapUIApp;

import java.util.ArrayList;


public class ControllerForSample{

    private LeapUIApp app;
    private ArrayList<String> tableList;

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


    //material button
    @FXML
    private JFXButton materialButton;

    @FXML
    void sayHelloMaterial(ActionEvent event) {
        System.out.println("hello from material button");
        tableList = SerializedTargetHand.getAllHands2Names("LeftGestures.txt");

        //add a button to the vbox
        System.out.println("children of vbox" + theVBox.getChildren());
        theVBox.getChildren().add(new JFXButton("yo"));//note interesting effect when you do setAll vs add
    }

    @FXML
    private JFXButton clickyButton;

    @FXML
    void clickyButtonAction(ActionEvent event) {
        System.out.println("clicky button clicked");

    }

    @FXML
    private VBox theVBox;

    @FXML
    private TreeTableView<String> treeTableView;

    @FXML
    private TreeTableColumn<String, String> ttCol;



    //fake data for tt in tab2
    TreeItem<String> item0 = new TreeItem<>("Daniel");
    TreeItem<String> item1 = new TreeItem<>("Joe");
    TreeItem<String> item2 = new TreeItem<>("Alice");
    TreeItem<String> item3 = new TreeItem<>("Bob");
    TreeItem<String> item4 = new TreeItem<>("Braxton");

    TreeItem<String> root = new TreeItem<>("root");


    @FXML
    public void initialize() throws Exception {

        //add a button to the vbox, after everything has been done in fxml.
        //do any java coding that you want to do
        theVBox.getChildren().add(new JFXButton("mango"));

        //lets add some thing we prepared in another fxml file
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pomo.fxml"));
        Parent button2 = fxmlLoader.load();
        theVBox.getChildren().add(button2);



        //add some data to the treetableView shown in tab2
        root.getChildren().setAll(item0, item1, item2, item3, item4);



        //do some weird stuff to col, but necessary
//        ttCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<String, String>, ObservableValue<String>>() {
//            @Override
//            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<String, String> param) {
//                return new SimpleStringProperty(param.getValue().getValue());
//            }
//        });


        //doing weird stuff with lambdas
        ttCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<String, String> param) -> new SimpleStringProperty(param.getValue().getValue()));



        treeTableView.setRoot(root);
    }




}