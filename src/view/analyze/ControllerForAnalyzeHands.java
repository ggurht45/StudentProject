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
import model.SerializedTargetHand;
import view.LeapUIApp;

import java.util.ArrayList;


public class ControllerForAnalyzeHands {

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

        //print arraylist of handInfo objects
        String path = SerializedTargetHand.getFolderPathHelperMethod("Alex");
        System.out.println("path: " + path);
        System.out.println("arraylist: \n" + SerializedTargetHand.getAllHandsInfoInFolder(path));

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
    private TreeTableView<Person> treeTableView;

    @FXML
    private TreeTableColumn<Person, String> col1;

    @FXML
    private TreeTableColumn<Person, Boolean> col2;

    @FXML
    private TreeTableColumn<Person, Number> col3;


    //fake data for tt in tab2
    TreeItem<Person> item0 = new TreeItem<>(new Person("Daniel", true, 30));
    TreeItem<Person> item1 = new TreeItem<>(new Person("Joe", true, 31));
    TreeItem<Person> item2 = new TreeItem<>(new Person("Bob", true, 32));
    TreeItem<Person> item3 = new TreeItem<>(new Person("Alice", false, 33));
    TreeItem<Person> item4 = new TreeItem<>(new Person("Mat", false, 34));



    TreeItem<Person> root = new TreeItem<>(new Person("root", true, 0));


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
        col1.setCellValueFactory((TreeTableColumn.CellDataFeatures<Person, String> param) -> param.getValue().getValue().nameProperty);
        col2.setCellValueFactory((TreeTableColumn.CellDataFeatures<Person, Boolean> param) -> param.getValue().getValue().emailProperty);
        col3.setCellValueFactory((TreeTableColumn.CellDataFeatures<Person, Number> param) -> param.getValue().getValue().ageProperty);


        //gonna try to set up editable table cells.. hm. this seems unnecessary?
//        col1.setCellFactory(new Callback<TreeTableColumn<Person, String>, TreeTableCell<Person, String>>() {
//            @Override
//            public TreeTableCell<Person, String> call(TreeTableColumn<Person, String> param) {
//                return new TextFieldTreeTableCell<>();
//            }
//        });

        //specify that a textfield should show up, this is definitely needed
        col1.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

        //commit the edit event
        col1.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<Person, String>>() {
            @Override
            public void handle(TreeTableColumn.CellEditEvent<Person, String> event) {
                TreeItem<Person> currentEditingPerson = treeTableView.getTreeItem(event.getTreeTablePosition().getRow());
                currentEditingPerson.getValue().setNameProperty(event.getNewValue());
            }
        });


        //setting up col2 to display choice of true/false
        ObservableList<Boolean> list = FXCollections.observableArrayList();
        list.add(true);
        list.add(false);
        col2.setCellFactory(ChoiceBoxTreeTableCell.forTreeTableColumn(list));

        //commit the edit event
        col2.setOnEditCommit(new EventHandler<TreeTableColumn.CellEditEvent<Person, Boolean>>() {
            @Override
            public void handle(TreeTableColumn.CellEditEvent<Person, Boolean> event) {
                TreeItem<Person> currentEditingPerson = treeTableView.getTreeItem(event.getTreeTablePosition().getRow());
                currentEditingPerson.getValue().setEmailProperty(event.getNewValue());
            }
        });


        treeTableView.setEditable(true);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
    }


    class Person {
        SimpleStringProperty nameProperty;
        SimpleBooleanProperty emailProperty;
        SimpleIntegerProperty ageProperty;

        Person(String name, boolean email, int age) {
            this.nameProperty = new SimpleStringProperty(name);
            this.emailProperty = new SimpleBooleanProperty(email);
            this.ageProperty = new SimpleIntegerProperty(age);
        }

        public void setNameProperty(String nameProperty) {
            this.nameProperty.set(nameProperty);
        }

        public void setEmailProperty(boolean emailProperty) {
            this.emailProperty.set(emailProperty);
        }

        public void setAgeProperty(int ageProperty) {
            this.ageProperty.set(ageProperty);
        }
    }


    @FXML
    private JFXTextField folderInputTextField;


}