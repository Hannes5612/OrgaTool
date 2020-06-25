package mainpackage.controller;

import com.jfoenix.controls.*;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import mainpackage.database.DatabaseHandler;
import mainpackage.ListManager;
import mainpackage.model.Task;

public class CreateTask {

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
    private JFXButton newTaskCreateButton;

    private final Date today = new java.sql.Date(Calendar.getInstance().getTime().getTime());

    @FXML
    void initialize() {
        ObservableList<String> priorities = FXCollections.observableArrayList("High","Medium","Low");
        newTaskPriority.setItems(priorities);
        newTaskPriority.setValue("Medium");
        newTaskDueDate.setValue(LocalDate.now().plusDays(1));

        newTaskCreateButton.setOnAction(e ->{
            String title = newTaskTitle.getText().trim();
            String content = newTaskContent.getText().trim();
            String prio = newTaskPriority.getValue().substring(0,1);
            String color = String.valueOf(newTaskColor.getValue());
            Date duedate = Date.valueOf(newTaskDueDate.getValue());

            Task createdTask = new Task(ListManager.getCountingTaskID(),title,content,prio,color,duedate,today);


            DatabaseHandler databaseHandler = new DatabaseHandler();
            try {
                databaseHandler.createTask(createdTask);

                ListManager.incrementCountingTaskId();
                ListManager.addTask(createdTask);

                newTaskCreateButton.getScene().getWindow().hide();
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            } catch (SQLException throwables) {
                String msg = checkError(title,content);
                Alert error = new Alert(Alert.AlertType.ERROR,msg);
                error.showAndWait();
            }



        });

    }

    private String checkError(String title, String content) {
        if (title.length()>45) return "Title can't be longer than 45 chars." ;
        if (content.length()>1024) return "Contenc can't be longer than 1024 chars.";
        return "Connection failed.\nPlease check your connection or try again.";
    }

    @FXML
    void close(ActionEvent event) {
        newTaskTitle.getScene().getWindow().hide();

    }

}
