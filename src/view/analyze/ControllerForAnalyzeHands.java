package view.analyze;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import model.HandInfo;
import model.HandInfo2;
import model.SerializedTargetHand;
import view.LeapUIApp;

import java.util.ArrayList;


public class ControllerForAnalyzeHands {
    private LeapUIApp app;
    private ArrayList<HandInfo2> handInfos2;

    public void setMainApp(LeapUIApp app) {
        this.app = app;
    }

    @FXML
    void sayHelloMaterial(ActionEvent event) {
        //get the input from the text field on the press of the button
        System.out.println("textfield: " + folderInputTextField.getText());
    }


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
        //create root, and add items to it
        TreeItem<HandInfo2> root = new TreeItem<>(new HandInfo2("rootFilename", "rootComments", "rootResult"));
        ArrayList<TreeItem<HandInfo2>> treeItems = getTreeItems("Alex");
        root.getChildren().setAll(treeItems);

//        -//look at
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pomo.fxml"));
//        Parent button2 = fxmlLoader.load();
//        theVBox.getChildren().add(button2);

        //do some weird stuff to col, but necessary; a shorter version is below
//        ttCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<String, String>, ObservableValue<String>>() {
//            @Override
//            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<String, String> param) {
//                return new SimpleStringProperty(param.getValue().getValue());
//            }
//        });

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
//        //commit the edit event
//        col2.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<HandInfo2, String>>() {
//            @Override
//            public void handle(TreeTableColumn.CellEditEvent<HandInfo2, String> event) {
//                TreeItem<HandInfo2> currentEditingPerson = treeTableView.getTreeItem(event.getTreeTablePosition().getRow());
//                currentEditingPerson.getValue().setComments(event.getNewValue());
//            }
//        });


//        treeTableView.setEditable(true);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
    }

    @FXML
    private JFXTextField folderInputTextField;

    @FXML
    private AnchorPane centerPane;
}