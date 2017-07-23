package view.analyze;

import com.jfoenix.controls.JFXTextField;
import com.leapmotion.leap.Hand;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import model.HandInfo;
import model.HandInfo2;
import model.SerializedTargetHand;
import view.LeapUIApp;
import view.anatomy.UIHand;
import view.anatomy.UIHand_Simple;
import view.anatomy.UIHand_SuperSimple;

import java.util.ArrayList;


public class ControllerForAnalyzeHands {
    private LeapUIApp app;
    private ArrayList<HandInfo2> handInfos2;
    private static UIHand uiHand1;
    private static Hand lmHand1;
    private static UIHand uiHand2;
    private static Hand lmHand2;

    public void setMainApp(LeapUIApp app) {
        this.app = app;
    }

    @FXML
    void sayHelloMaterial(ActionEvent event) {
        //get the input from the text field on the press of the button
        System.out.println("textfield: " + folderInputTextField.getText());
    }


    @FXML
    private VBox theVBox;

    @FXML
    private HBox container;

    @FXML
    private TreeTableView<HandInfo2> treeTableView;

    //handfile string
    @FXML
    private TreeTableColumn<HandInfo2, String> col1;

    //comments
    @FXML
    private TreeTableColumn<HandInfo2, String> col2;

    //result
    @FXML
    private TreeTableColumn<HandInfo2, String> col3;

//    //Gesture Type
//    @FXML
//    private TreeTableColumn<HandInfo2, Number> col4;


    private static ArrayList<TreeItem<HandInfo2>> getTreeItems(String folder) {
        String path = SerializedTargetHand.getFolderPathHelperMethod("Alex");
        ArrayList<HandInfo> handInfoArray_old = SerializedTargetHand.getAllHandsInfoInFolder(path);
        return getTreeItemsFromHandInfos(handInfoArray_old);
    }

    //converts the derserialized class into something that can have simplestring properties etc.
    private static ArrayList<TreeItem<HandInfo2>> getTreeItemsFromHandInfos(ArrayList<HandInfo> arr) {
        ArrayList<TreeItem<HandInfo2>> treeItems = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            HandInfo2 hi = new HandInfo2(arr.get(i));
            TreeItem<HandInfo2> item = new TreeItem<HandInfo2>(hi);
            treeItems.add(item);
        }
        return treeItems;
    }

    @FXML
    public void initialize() throws Exception {

        //set pref height and width of container?

        //uiHand1 setup
        uiHand1 = new UIHand_Simple(Color.GREEN.darker(), false);
        lmHand1 = SerializedTargetHand.getHandFromString("targets2/gesture2Left.hand");
        uiHand1.setLoc(lmHand1);
        uiHand1.setVisible(true);
        uiHand1.setTranslateX(4);

        //uiHand2 setup
        uiHand2 = new UIHand_Simple(Color.BLUE.darker(), true);
        lmHand2 = SerializedTargetHand.getHandFromString("dataOutput/Alex/2017-07-10 12-26-16.hand");
        uiHand2.setLoc(lmHand2);
        uiHand2.setVisible(true);
        uiHand2.setTranslateX(12);

        //create root, and add items to it
        TreeItem<HandInfo2> root = new TreeItem<>(new HandInfo2("rootFilename", "rootComments", "rootResult"));
        ArrayList<TreeItem<HandInfo2>> treeItems = getTreeItems("Alex");
        root.getChildren().setAll(treeItems);

        //doing weird stuff with lambdas; much shorter
        col1.setCellValueFactory((TreeTableColumn.CellDataFeatures<HandInfo2, String> param) -> param.getValue().getValue().handFile2Property());
        col2.setCellValueFactory((TreeTableColumn.CellDataFeatures<HandInfo2, String> param) -> param.getValue().getValue().comments2Property());
        col3.setCellValueFactory((TreeTableColumn.CellDataFeatures<HandInfo2, String> param) -> param.getValue().getValue().result2Property());


//        //specify that a textfield should show up, this is definitely needed
//        col1.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
//
//        //commit the edit event
//        col1.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<HandInfo2, String>>() {
//            @Override
//            public void handle(TreeTableColumn.CellEditEvent<HandInfo2, String> event) {
//                TreeItem<HandInfo2> currentEditingPerson = treeTableView.getTreeItem(event.getTreeTablePosition().getRow());
//                currentEditingPerson.getValue().setHandFile2(event.getNewValue());
//            }
//        });
//
//
//        //setting up col2 to display choice of true/false
//        ObservableList<String> list = FXCollections.observableArrayList();
//        list.add("Passed");
//        list.add("Failed");
//        col2.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(list));
//
        //commit the edit event
        col2.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<HandInfo2, String>>() {
            @Override
            public void handle(TreeTableColumn.CellEditEvent<HandInfo2, String> event) {
                TreeItem<HandInfo2> currentEditingPerson = treeTableView.getTreeItem(event.getTreeTablePosition().getRow());
                currentEditingPerson.getValue().setComments(event.getNewValue());
            }
        });


//        treeTableView.setEditable(true);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);

        //camera and stuff
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(new Translate(0, -15, -50), new Rotate(-20, Rotate.X_AXIS));

        // The 3D display
        Group group3D = new Group();
        group3D.getChildren().add(camera);
        group3D.getChildren().addAll(uiHand1, uiHand2);
        SubScene sub3D = new SubScene(group3D, app.ScreenWidth, app.ScreenHeight, true, SceneAntialiasing.BALANCED); // "true" gives us a depth buffer
        sub3D.setFill(Color.LAVENDER);
        sub3D.setCamera(camera);

        // The 2D overlay
        Group group2D = new Group(theVBox);
        SubScene sub2D = new SubScene(group2D, app.ScreenWidth, app.ScreenHeight, false, SceneAntialiasing.BALANCED); // "false" because no depth in 2D


        //put 2D and 3D subScenes together; and make it into a scene
        container.getChildren().setAll(new Group(sub3D, sub2D));//new Group(sub3D, sub2D); // sub2D is second, as we want it overlaid, not underlaid
    }

    @FXML
    private JFXTextField folderInputTextField;

    @FXML
    private AnchorPane centerPane;

    @FXML
    void mouseClickedEvent(MouseEvent event) {
//        System.out.println("mouse wa/s clicked in the table: " + event);

        TreeItem<HandInfo2> treeItem = treeTableView.getSelectionModel().getSelectedItem();
        HandInfo2 h = treeItem.getValue();
//        System.out.println(h); //prints the object associated with this row in the table

        //update hand1
        String file =  h.getHandFile();
//        System.out.println("new file location:" + file);
        lmHand1 = SerializedTargetHand.getHandFromString(file);
        uiHand1.setLoc(lmHand1);
    }
}


//-----------------------------------------------------------------------------------------------------------
//        -//look at later if need to
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pomo.fxml"));
//        Parent button2 = fxmlLoader.load();
//        theVBox.getChildren().add(button2);