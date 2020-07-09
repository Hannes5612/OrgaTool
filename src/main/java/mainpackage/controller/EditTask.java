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
import mainpackage.database.DatabaseHandler;
import mainpackage.model.Task;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
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

        //newTaskDueDate.setValue((LocalDate) task.getDueDate());

        ObservableList<String> priorities = FXCollections.observableArrayList("High","Medium","Low");
        newTaskPriority.setItems(priorities);
        switch(task.getPriority()) {
            case "H": newTaskPriority.setValue("High");   break;
            case "M": newTaskPriority.setValue("Medium"); break;
            case "L": newTaskPriority.setValue("Low");    break;
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


    public static Task getEditedTask(){
        return editedTask;
    }

    private static Task editedTask;

}