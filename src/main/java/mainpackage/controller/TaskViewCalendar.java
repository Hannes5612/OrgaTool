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

    private ObservableList<Task> clickedTasks = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        taskViewCalendarList.setPlaceholder(new Label("Deleted all tasks, please close!"));
    }

    public void setTasks(ObservableList<Task> clickedTasks) {
        this.clickedTasks = clickedTasks;
        taskViewCalendarList.setCellFactory(Cell -> new Cell());
        taskViewCalendarList.setItems(clickedTasks);

    }


}
