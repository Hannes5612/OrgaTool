package mainpackage.controller;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import mainpackage.ListManager;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.Task;
import mainpackage.model.User;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;

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

    private User user;
    private Task task;
    private int selectedIdx;

    public EditTask(Task task, int selectedIdx) {
        this.task = task;
        this.selectedIdx = selectedIdx;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resourcese) {

        newTaskTitle.setText(task.getTitle());
        newTaskContent.setText(task.getContent());
        int taskId = task.getId();
        Date creationDate = task.getCreationDate();

        newTaskEditButton.setOnAction(e -> {
            System.out.println("'Save' button pressed");
            if (newTaskTitle.getText() == null || newTaskTitle.getText().trim().isEmpty()) {
                CreateTask.missingTitleAlert();
            } else {
                if (selectedIdx != -1) {

                    String title = newTaskTitle.getText().trim();
                    String content = newTaskContent.getText().trim();
                    String priority = newTaskPriority.getValue().substring(0,1);
                    String color = String.valueOf(newTaskColor.getValue());
                    Date dueDate = Date.valueOf(newTaskDueDate.getValue());

                    editedTask = new Task(taskId, title, content, priority, color, dueDate, creationDate);
                    DatabaseHandler databaseHandler = new DatabaseHandler();

                    try {
                        databaseHandler.editTask(taskId, editedTask);
                        ListManager.editTask(editedTask);
                        System.out.println("Task edited");
                    } catch (ClassNotFoundException classNotFoundException) {
                        classNotFoundException.printStackTrace();
                    } catch (SQLException throwables) {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Database connection failed \n Please check your connection or try again.");
                        error.showAndWait();
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

    void setUser(User user) {
        this.user = user;
    }

    public static Task getEditedTask(){
        return editedTask;
    }

    private static Task editedTask;

}
