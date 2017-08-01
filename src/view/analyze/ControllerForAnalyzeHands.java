package view.analyze;

import com.jfoenix.controls.JFXTextField;
import com.leapmotion.leap.Hand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import model.CsvHelper;
import model.HandInfo;
import model.HandInfo2;
import model.SerializedTargetHand;
import view.LeapUIApp;
import view.anatomy.UIHand;
import view.anatomy.UIHand_Simple;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class ControllerForAnalyzeHands {
    private LeapUIApp app;
    private static ObservableList<TreeItem<HandInfo2>> treeItems;
    private static TreeItem<HandInfo2> root;
    private static String currentFolder;
    private static UIHand uiHand1;
    private static Hand lmHand1;
    private static UIHand uiHand2;
    private static Hand lmHand2;

    public void setMainApp(LeapUIApp app) {
        this.app = app;
        //make sure to save the data if the app is closed
        app.primaryStage.setOnCloseRequest(event -> {
            //save the data if need to
            savetableData2(treeItems, currentFolder);
        });
    }

    public void updateTable(){
        //update table to show new folder contents
        treeItems = getTreeItems(currentFolder);
        root.getChildren().setAll(treeItems);
        treeTableView.setRoot(root);
    }

    @FXML
    void sayHelloMaterial(ActionEvent event) {
        //get the input from the text field on the press of the button
        String txt = folderInputTextField.getText();


        //save data since it might have been changed; do this before updating name of folder
        savetableData2(treeItems, currentFolder);

        //validate that the input is a valid folder and update the currentfolder
        currentFolder = txt;

        //update table to show new folder contents
        treeItems = getTreeItems(txt);
        root.getChildren().setAll(treeItems);
        treeTableView.setRoot(root);

        //set text input to empty again
        folderInputTextField.setText("");
    }


    //helper method to get table data in arraylist format
    private static ArrayList<HandInfo> getHandInfoArrayList(ObservableList<TreeItem<HandInfo2>> items) {
        ArrayList<HandInfo> arr = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            HandInfo h = items.get(i).getValue().convertToHandInfo();
            arr.add(h);
        }
        return arr;
    }


//    private static void savetableData(ObservableList<TreeItem<HandInfo2>> items, String folder) {
//        //get handInfo Arraylist
//        ArrayList<HandInfo> arr = getHandInfoArrayList(items);
//        //save data to file
//        String path = SerializedTargetHand.getFolderPathHelperMethod(folder);
//        SerializedTargetHand.serialize(arr, path);
//    }

    //save to csv instead of .ser
    private static void savetableData2(ObservableList<TreeItem<HandInfo2>> items, String folder) {
        //get handInfo Arraylist
        ArrayList<HandInfo> arr = getHandInfoArrayList(items);
        //save data to file
        String fullFilePath = SerializedTargetHand.getCSVFilePathForFolder(folder);
        SerializedTargetHand.writeToCSV(fullFilePath, arr);
    }


    @FXML
    private VBox theVBox;

    @FXML
    private HBox container;

    @FXML
    private TreeTableView<HandInfo2> treeTableView;

    //name string
    @FXML
    private TreeTableColumn<HandInfo2, String> col0;

    //handfile string
    @FXML
    private TreeTableColumn<HandInfo2, String> col1;

    //handfile string
    @FXML
    private TreeTableColumn<HandInfo2, String> gestureCol;

    //comments
    @FXML
    private TreeTableColumn<HandInfo2, String> col2;

    //result
    @FXML
    private TreeTableColumn<HandInfo2, String> col3;

//    //Gesture Type
//    @FXML
//    private TreeTableColumn<HandInfo2, Number> col4;


    private static ObservableList<TreeItem<HandInfo2>> getTreeItems(String folder) {
        String path = SerializedTargetHand.getFolderPathHelperMethod(folder);
        ArrayList<HandInfo> handInfoArray_old = SerializedTargetHand.getAllHandsInfoInFolder(path);
        return getListOfTreeItems(handInfoArray_old);
    }

    //converts the derserialized class into something that can have simplestring properties etc.
    private static ObservableList<TreeItem<HandInfo2>> getListOfTreeItems(ArrayList<HandInfo> arr) {
        ObservableList<TreeItem<HandInfo2>> treeItems = FXCollections.observableArrayList();
        for (int i = 0; i < arr.size(); i++) {
            HandInfo2 hi = new HandInfo2(arr.get(i));
            TreeItem<HandInfo2> item = new TreeItem<HandInfo2>(hi);
            treeItems.add(item);
        }
        return treeItems;
    }


    //todo put this in a try catch, so if null pointer error happens we know where very quickly. and can make error code1
    private static String getFileStringPath(int rowIndex) {
        HandInfo2 h = treeItems.get(rowIndex).getValue();
        return h.getHandFile();
    }

    @FXML
    public void initialize() throws Exception {
        //set pref height and width of container?

        //create root, and add items to it
        root = new TreeItem<>(new HandInfo2("rootName", "rootFilename", "rootGestureType", "rootComments", "rootResult"));
        currentFolder = app.DEFAULT_FOLDER;
        treeItems = getTreeItems(currentFolder);
        root.getChildren().setAll(treeItems);

        //doing weird stuff with lambdas; much shorter
        col0.setCellValueFactory((TreeTableColumn.CellDataFeatures<HandInfo2, String> param) -> param.getValue().getValue().name2Property());
        col1.setCellValueFactory((TreeTableColumn.CellDataFeatures<HandInfo2, String> param) -> param.getValue().getValue().handFile2Property());
        gestureCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<HandInfo2, String> param) -> param.getValue().getValue().gestureType2Property());
        col2.setCellValueFactory((TreeTableColumn.CellDataFeatures<HandInfo2, String> param) -> param.getValue().getValue().comments2Property());
        col3.setCellValueFactory((TreeTableColumn.CellDataFeatures<HandInfo2, String> param) -> param.getValue().getValue().result2Property());


        //name column
        col0.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        col0.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<HandInfo2, String>>() {
            @Override
            public void handle(TreeTableColumn.CellEditEvent<HandInfo2, String> event) {
                TreeItem<HandInfo2> h = treeTableView.getTreeItem(event.getTreeTablePosition().getRow());
                h.getValue().setName2(event.getNewValue());
            }
        });


        //filepath column
        //specify that a textfield should show up, this is definitely needed
        col1.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        col1.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<HandInfo2, String>>() {
            @Override
            public void handle(TreeTableColumn.CellEditEvent<HandInfo2, String> event) {
                TreeItem<HandInfo2> h = treeTableView.getTreeItem(event.getTreeTablePosition().getRow());
                h.getValue().setHandFile2(event.getNewValue());
            }
        });


        //   comments section
        col2.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());
        col2.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<HandInfo2, String>>() {
            @Override
            public void handle(TreeTableColumn.CellEditEvent<HandInfo2, String> event) {
                TreeItem<HandInfo2> h = treeTableView.getTreeItem(event.getTreeTablePosition().getRow());
                h.getValue().setComments2(event.getNewValue());
            }
        });


        //setting up col2 to display choice of true/false
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("Passed");
        list.add("Failed");
        col3.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(list));

//        commit the edit event
        col3.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<HandInfo2, String>>() {
            @Override
            public void handle(TreeTableColumn.CellEditEvent<HandInfo2, String> event) {
                TreeItem<HandInfo2> h = treeTableView.getTreeItem(event.getTreeTablePosition().getRow());
                h.getValue().setResult(event.getNewValue());
            }
        });


        treeTableView.setEditable(true);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);


        //uiHand1 setup
        uiHand1 = new UIHand_Simple(Color.GREEN.darker(), false);
        //set the inital hand to first row's hand file
        lmHand1 = SerializedTargetHand.getHandFromString(getFileStringPath(0));
        uiHand1.setLoc(lmHand1);
        uiHand1.setVisible(true);
        uiHand1.setTranslateX(4);

        //uiHand2 setup
        uiHand2 = new UIHand_Simple(Color.BLUE.darker(), true);
        lmHand2 = SerializedTargetHand.getHandFromString(LeapUIApp.Targets2Path + "gesture2Left.hand");
        uiHand2.setLoc(lmHand2);
        uiHand2.setVisible(true);
        uiHand2.setTranslateX(12);


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
    void rowClickedEvent(MouseEvent event) {
        TreeItem<HandInfo2> treeItem = treeTableView.getSelectionModel().getSelectedItem();
        //only update if actually clicked on a row containing the hand
        if (treeItem != null) {
            HandInfo2 h = treeItem.getValue();
            //update hand
            String file = h.getHandFile();
            lmHand1 = SerializedTargetHand.getHandFromString(file);
            uiHand1.setLoc(lmHand1);
        }
    }

//    @FXML
//    void writeToCSV(ActionEvent event) {
//        System.out.println("storing data to csv file");
//        ArrayList<HandInfo> data = getHandInfoArrayList(treeItems);
//        String path = SerializedTargetHand.getFolderPathHelperMethod(currentFolder);
//        SerializedTargetHand.storeToCSV(path, data);
//    }

    //todo get this work again
    @FXML
    void readFromCSV(ActionEvent event) {
        System.out.println("reading data from csv file");
        //get file from computer
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Please choose CSV file..");
        filechooser.setInitialDirectory(new File(app.ProjectDirectoryPath + "/dataOutput"));
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("CSV files (.csv)", "*.csv");
        filechooser.setSelectedExtensionFilter(filter);
        File file = filechooser.showOpenDialog(app.primaryStage);
        String filePath = file.getPath();
        System.out.println("filePath is: " + filePath);


//        String filename = "_allHandsOnDeck.csv";            //allow to be typed in from dialog
//        String path = SerializedTargetHand.getFolderPathHelperMethod(currentFolder);
//        String fullFileName = path + filename;
        ArrayList<HandInfo> data = SerializedTargetHand.readFromCSV(filePath);

        //update table
        treeItems = getListOfTreeItems(data);
        root.getChildren().setAll(treeItems);
        treeTableView.setRoot(root);

    }

    @FXML
    void saveToExternalCSVFile(ActionEvent event) {
        System.out.println("saving csv to external file");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save to external CSV file...");
        File file = fileChooser.showSaveDialog(app.primaryStage);
        ArrayList<HandInfo> hands = getHandInfoArrayList(treeItems);
        System.out.println("file being saved: " + file);
        if (file != null) {
            CsvHelper.writeCsvFile(file, hands);
        } else {
            System.out.println("the file specified is not null. can not write to this file");
        }
    }

    @FXML
    void goToMainScene(ActionEvent event) {
        //go to main scene
        app.primaryStage.setScene(app.scene);
    }

}


//-----------------------------------------------------------------------------------------------------------
//        -//look at later if need to
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pomo.fxml"));
//        Parent button2 = fxmlLoader.load();
//        theVBox.getChildren().add(button2);