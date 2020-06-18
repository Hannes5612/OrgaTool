package mainpackage.controller;

import com.jfoenix.controls.*;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.Task;
import mainpackage.model.User;

public class Create {

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private JFXButton newTaskOnlyNoteButton;
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

    private User user;
    private Date today = new java.sql.Date(Calendar.getInstance().getTime().getTime());

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

            Task createdTask = new Task(title,content,prio,color,duedate,today);

            DatabaseHandler databaseHandler = new DatabaseHandler();
            databaseHandler.createTask(createdTask,user,"Task");

            newTaskCreateButton.getScene().getWindow().hide();

        });

    }

    void setUser(User user){
        this.user = user;
    }
}
