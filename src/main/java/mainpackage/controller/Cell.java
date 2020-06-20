package mainpackage.controller;

import com.jfoenix.controls.JFXCheckBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import mainpackage.model.Task;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class Cell  extends ListCell<Task> {

        @FXML
        private ResourceBundle resources;
        @FXML
        private URL location;
        @FXML
        private AnchorPane rootAnchorPane;
        @FXML
        private Label cellTaskLabel;
        @FXML
        private Label cellTaskDescription;
        @FXML
        private JFXCheckBox cellCheckbox;
        @FXML
        private Label cellDateLabel;
        @FXML
        private Label cellCreatedLabel;
        @FXML
        private Label cellPrioLabel;
        @FXML
        private AnchorPane cellColor;

        private  FXMLLoader fxmlLoader;

        @FXML
        void initialize() {


        }

        @Override
        protected void updateItem(Task myTask, boolean empty) {
                super.updateItem(myTask, empty);

                if (empty || myTask == null) {
                        setText(null);
                        setGraphic(null);
                } else {
                        if (fxmlLoader == null) {
                                fxmlLoader = new FXMLLoader(getClass().getResource("/view/Cell.fxml"));
                                fxmlLoader.setController(this);
                                try {
                                        fxmlLoader.load();
                                } catch (IOException e) {
                                        e.printStackTrace();
                                }
                        }

                        String color = myTask.getColor().substring(2,8);
                        cellColor.setStyle("-fx-background-color: #"+ color);
                        cellTaskLabel.setText(myTask.getName());
                        cellTaskDescription.setText(myTask.getContent());
                        cellPrioLabel.setText(myTask.getPriority());
                        cellDateLabel.setText(String.valueOf(myTask.getDueDate()));
                        cellCreatedLabel.setText(String.valueOf(myTask.getCreationDate()));
                        cellCheckbox.setSelected(false);

                        setText(null);
                        setGraphic(rootAnchorPane);
                }


        }
    }


