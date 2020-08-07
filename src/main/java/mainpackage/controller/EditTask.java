package mainpackage.controller;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mainpackage.ListManager;
import mainpackage.Main;
import mainpackage.database.DatabaseHandler;
import mainpackage.exceptions.UnsupportedStateType;
import mainpackage.model.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * window to edit an existing task
 */
public class EditTask implements Initializable {

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private JFXTextField newTaskTitle;
    @FXML
    private JFXTextArea newTaskContent;
    @FXML
    private JFXComboBox<String> newTaskPriority;
    @FXML
    private JFXColorPicker newTaskColor;
    @FXML
    private JFXDatePicker newTaskDueDate;
    @FXML
    private JFXButton newTaskEditButton;

    private static final Logger logger = LogManager.getLogger(Main.class.getName());

    private Task task;
    private int selectedIdx;

    public EditTask(Task task, int selectedIdx) {
        this.task = task;
        this.selectedIdx = selectedIdx;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resourcese) {

        editedTask = null;
        newTaskTitle.setText(task.getTitle());
        newTaskContent.setText(task.getContent());
        int taskId = task.getId();
        Date creationDate = task.getCreationDate();

        //newTaskDueDate.setValue((LocalDate) task.getDueDate());

        ObservableList<String> priorities = FXCollections.observableArrayList("High", "Medium", "Low");
        newTaskPriority.setItems(priorities);
        switch (task.getPriority()) {
            case "H":
                newTaskPriority.setValue("High");
                break;
            case "M":
                newTaskPriority.setValue("Medium");
                break;
            case "L":
                newTaskPriority.setValue("Low");
                break;
        }
        newTaskColor.setValue(Color.web(task.getColor()));
        newTaskDueDate.setValue(LocalDate.now().plusDays(1));
        newTaskDueDate.setValue(task.getDueDate().toLocalDate());

        newTaskEditButton.setOnAction(e -> {
            System.out.println("'Save' button pressed");
            if (newTaskTitle.getText() == null || newTaskTitle.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please enter a title for your task.", ButtonType.OK);
                alert.setTitle("MISSING TITLE");
                alert.setHeaderText("Your task has no title yet.");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
                alert.showAndWait();
            } else {
                if (selectedIdx != -1) {

                    String title = newTaskTitle.getText().trim();
                    String content = newTaskContent.getText().trim();
                    String priority = newTaskPriority.getValue().substring(0, 1);
                    String color = String.valueOf(newTaskColor.getValue());
                    Date dueDate = Date.valueOf(newTaskDueDate.getValue());

                    editedTask = new Task(taskId, title, content, priority, color, dueDate, creationDate, 0);
                    DatabaseHandler databaseHandler = new DatabaseHandler();

                    try {
                        databaseHandler.editTask(taskId, editedTask);
                        ListManager.editTask(editedTask);
                        logger.debug("Task edited: " + editedTask);
                    } catch (ClassNotFoundException classNotFoundException) {
                        classNotFoundException.printStackTrace();
                        logger.error("ClassNotFoundException: " + classNotFoundException);
                    } catch (SQLException throwables) {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Database connection failed \n Please check your connection or try again.");
                        error.showAndWait();
                        logger.error("SQLException: " + throwables);
                    } catch (UnsupportedStateType unsupportedStateType) {
                        unsupportedStateType.printStackTrace();
                        logger.error("Unsupported state type!");
                    }
                }
                newTaskEditButton.getScene().getWindow().hide();
            }

        });

    }

    @FXML
    void close(ActionEvent event) {
        newTaskTitle.getScene().getWindow().hide();
    }


    public static Task getEditedTask() {
        return editedTask;
    }

    private static Task editedTask;


}
