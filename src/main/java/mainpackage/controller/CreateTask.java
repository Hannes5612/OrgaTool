package mainpackage.controller;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
import java.util.Calendar;
import java.util.ResourceBundle;

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

    private static final Logger logger = LogManager.getLogger(Main.class.getName());

    private final Date today = new java.sql.Date(Calendar.getInstance().getTime().getTime());
    private LocalDate date;
    private boolean dateSet;

    public CreateTask() {}

    public CreateTask(LocalDate date) {
        this.date = date;
        dateSet = true;
    }

    @FXML
    void initialize() {

        newTaskDueDate.setValue(date);

        ObservableList<String> priorities = FXCollections.observableArrayList("High", "Medium", "Low");
        newTaskPriority.setItems(priorities);
        newTaskPriority.setValue("Medium");
        if (!dateSet) newTaskDueDate.setValue(LocalDate.now().plusDays(1));

        // Closing CreateTask window and saving new task (in database and ListManager)
        // only when title is set.
        newTaskCreateButton.setOnAction(e -> {
            String title = newTaskTitle.getText().trim();
            String content = newTaskContent.getText().trim();
            String prio = newTaskPriority.getValue().substring(0, 1);
            String color = String.valueOf(newTaskColor.getValue());
            Date duedate = Date.valueOf(newTaskDueDate.getValue());

            Task createdTask = new Task(ListManager.getCountingTaskID(), title, content, prio, color, duedate, today, 0);


            DatabaseHandler databaseHandler = new DatabaseHandler();
            try {
                databaseHandler.createTask(createdTask);

                ListManager.incrementCountingTaskId();
                ListManager.addTask(createdTask);
                logger.debug("Task created: " + createdTask);
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
                logger.error("ClassNotFoundException: " + classNotFoundException);
            } catch (SQLException throwables) {
                String msg = checkError(title, content);
                Alert error = new Alert(Alert.AlertType.ERROR, msg);
                error.showAndWait();
                logger.error("SQLException: " + throwables);
            } catch (UnsupportedStateType unsupportedStateType) {
                unsupportedStateType.printStackTrace();
                logger.error("Unsupported state type!");
            }
            newTaskCreateButton.getScene().getWindow().hide();
        });

    }

    private String checkError(String title, String content) {
        if (title.length() > 28) return "Title can't be longer than 45 chars.";
        if (content.length() > 1024) return "Content can't be longer than 1024 chars.";
        return "Connection failed.\nPlease check your connection or try again.";
    }

    @FXML
    void close(ActionEvent event) {
        newTaskTitle.getScene().getWindow().hide();
    }


}
