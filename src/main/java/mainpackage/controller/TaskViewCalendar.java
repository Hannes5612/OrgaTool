package mainpackage.controller;

import com.jfoenix.controls.JFXListView;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import mainpackage.model.Task;

public class TaskViewCalendar{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private JFXListView<Task> taskViewCalendarList;

    @FXML
    void initialize() {
        taskViewCalendarList.setPlaceholder(new Label("Deleted all tasks, please close!"));
    }

    //Sets the cells of the ListView, inserts per cell one task
    public void setTasks(ObservableList<Task> clickedTasks) {
        taskViewCalendarList.setCellFactory(Cell -> new TaskCell());
        taskViewCalendarList.setItems(clickedTasks);

    }

}
