package mainpackage.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListCell;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import mainpackage.database.DatabaseHandler;
import mainpackage.ListManager;
import mainpackage.model.Task;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class Cell extends JFXListCell<Task> {

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
        @FXML
        private ImageView cellDeleteButton;

        private  FXMLLoader fxmlLoader;

        @FXML
        void initialize() {
                cellDeleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

                        final int selectedIdx = listViewProperty().get().getSelectionModel().getSelectedIndex();
                        final Task task = listViewProperty().get().getSelectionModel().getSelectedItem();

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + task.getTitle() + " ?", ButtonType.YES,  ButtonType.CANCEL);
                        alert.setTitle("Confirmation");
                        alert.setHeaderText("You are about to delete a task!");
                        //Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                        //stage.getIcons().add(new Image("/icons/appicon_small.png")); // To add an icon
                        alert.showAndWait();

                        if (alert.getResult() == ButtonType.YES) {
                                if (selectedIdx != -1) {
                                        Task itemToRemove = listViewProperty().get().getSelectionModel().getSelectedItem();
                                        ListManager.deleteTask(itemToRemove.getId());
                                        //debugLogger.info(selectedIdx);

                                        DatabaseHandler databaseHandler = new DatabaseHandler();
                                        try {
                                                databaseHandler.deleteTask(task);
                                        } catch (SQLException throwables) {
                                                Alert error = new Alert(Alert.AlertType.ERROR,"Database connection failed \n Please check your connection or try again.");
                                                error.showAndWait();
                                        } catch (ClassNotFoundException e) {
                                                e.printStackTrace();
                                        }
                                        //debugLogger.info("Removed from Listview " + itemToRemove.getTaskName());
                                        listViewProperty().get().getItems().remove(selectedIdx);
                                }

                        }
                });

        }

        @Override
        protected void updateItem(Task task, boolean empty) {

                super.updateItem(task, empty);
                this.setStyle("-fx-padding: 0px;");
                if (empty || task == null) {
                        setText(null);
                        setGraphic(null);
                } else {
                        if (fxmlLoader == null) {
                                fxmlLoader = new FXMLLoader(getClass().getResource("/view/TaskCell.fxml"));
                                fxmlLoader.setController(this);
                                try {
                                        fxmlLoader.load();
                                } catch (IOException e) {
                                        e.printStackTrace();
                                }
                        }

                        String color = task.getColor().substring(2,8);
                        cellColor.setStyle("-fx-background-color: #"+ color);
                        cellTaskLabel.setText(task.getTitle());
                        cellTaskDescription.setText(task.getContent());
                        cellPrioLabel.setText(task.getPriority());
                        cellDateLabel.setText(String.valueOf(task.getDueDate()));
                        cellCreatedLabel.setText(String.valueOf(task.getCreationDate()));
                        cellCheckbox.setSelected(false);
                        cellTaskDescription.setWrapText(true);

                        setText(null);
                        setGraphic(rootAnchorPane);
                }
        }
    }


