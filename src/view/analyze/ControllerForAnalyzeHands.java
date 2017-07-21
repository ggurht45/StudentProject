package view.analyze;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTreeTableCell;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import model.HandInfo;
import model.HandInfo2;
import model.SerializedTargetHand;
import view.LeapUIApp;

import java.util.ArrayList;


public class ControllerForAnalyzeHands {

    private LeapUIApp app;
    private ArrayList<String> tableList;
    private ArrayList<HandInfo2> handInfos2;

    @FXML
    private Label theSpecialLabel;

    @FXML
    private Button mySpecialButtonYea;

    @FXML
    void sayaHelloa(ActionEvent event) {
        System.out.println("button was clicked. btw, did some design in scenebuilder bro");
    }

    public void setMainApp(LeapUIApp app) {
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

        //get the input from the text field on the press of the button
        System.out.println("textfield: " + folderInputTextField.getText());


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


    //fake data for tt in tab2
//    TreeItem<Person> item0 = new TreeItem<>(new Person("Daniel", true, 30));
//    TreeItem<Person> item1 = new TreeItem<>(new Person("Joe", true, 31));
//    TreeItem<Person> item2 = new TreeItem<>(new Person("Bob", true, 32));
//    TreeItem<Person> item3 = new TreeItem<>(new Person("Alice", false, 33));
//    TreeItem<Person> item4 = new TreeItem<>(new Person("Mat", false, 34));


    private static ArrayList<TreeItem<HandInfo2>> getTreeItems(String folder) {
        String path = SerializedTargetHand.getFolderPathHelperMethod("Alex");
        ArrayList<HandInfo> handInfoArray_old = SerializedTargetHand.getAllHandsInfoInFolder(path);
        System.out.println("old handInfoArray: " + handInfoArray_old);

        ArrayList<HandInfo2> handInfoArray = new ArrayList<>();
        HandInfo2 hi1 = new HandInfo2("handfile1", "comments1", "result1");
        HandInfo2 hi2 = new HandInfo2("handfile2", "comments2", "result2");
        HandInfo2 hi3 = new HandInfo2("handfile3", "comments3", "result3");
        handInfoArray.add(hi1);
        handInfoArray.add(hi2);
        handInfoArray.add(hi3);
        System.out.println("handinfoarray: " + handInfoArray);
        return getTreeItemsFromHandInfos(handInfoArray);
    }

    private static ArrayList<TreeItem<HandInfo2>> getTreeItemsFromHandInfos(ArrayList<HandInfo2> arr) {
        ArrayList<TreeItem<HandInfo2>> treeItems = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            HandInfo2 hi = arr.get(i);
            TreeItem<HandInfo2> item = new TreeItem<HandInfo2>(hi);
            if (i == 0) {
                System.out.println("handInfo: " + hi);
                System.out.println("item: " + item);
            }
            treeItems.add(item);
        }
        return treeItems;
    }

    @FXML
    public void initialize() throws Exception {
        //get hand info and store it in the array.default = alex for now
//        String path = SerializedTargetHand.getFolderPathHelperMethod("Alex");
//        System.out.println("path: " + path);
//        handInfos = SerializedTargetHand.getAllHandsInfoInFolder(path);
//        System.out.println("arraylist of handsInfos : \n" + handInfos);


        //create root, and add items to it
        TreeItem<HandInfo2> root = new TreeItem<>(new HandInfo2("rootFilename", "rootComments", "rootResult"));
        ArrayList<TreeItem<HandInfo2>> treeItems = getTreeItems("Alex");
        root.getChildren().setAll(treeItems);


//        //add a button to the vbox, after everything has been done in fxml.
//        //do any java coding that you want to do
//        theVBox.getChildren().add(new JFXButton("mango"));
//
//        //lets add some thing we prepared in another fxml file
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pomo.fxml"));
//        Parent button2 = fxmlLoader.load();
//        theVBox.getChildren().add(button2);


        //do some weird stuff to col, but necessary
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


        //gonna try to set up editable table cells.. hm. this seems unnecessary?
//        col1.setCellFactory(new Callback<TreeTableColumn<Person, String>, TreeTableCell<Person, String>>() {
//            @Override
//            public TreeTableCell<Person, String> call(TreeTableColumn<Person, String> param) {
//                return new TextFieldTreeTableCell<>();
//            }
//        });

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


//    class Person {
//        SimpleStringProperty nameProperty;
//        SimpleBooleanProperty emailProperty;
//        SimpleIntegerProperty ageProperty;
//
//        Person(String name, boolean email, int age) {
//            this.nameProperty = new SimpleStringProperty(name);
//            this.emailProperty = new SimpleBooleanProperty(email);
//            this.ageProperty = new SimpleIntegerProperty(age);
//        }
//
//        public void setNameProperty(String nameProperty) {
//            this.nameProperty.set(nameProperty);
//        }
//
//        public void setEmailProperty(boolean emailProperty) {
//            this.emailProperty.set(emailProperty);
//        }
//
//        public void setAgeProperty(int ageProperty) {
//            this.ageProperty.set(ageProperty);
//        }
//    }


    @FXML
    private JFXTextField folderInputTextField;


}