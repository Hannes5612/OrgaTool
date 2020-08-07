package mainpackage.controller;

import com.jfoenix.controls.JFXListView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import mainpackage.model.Task;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Separate view on tasks for a single day.
 */
public class TaskViewCalendar {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private JFXListView<Task> taskViewCalendarList;

    @FXML
    void initialize() {
        // Placeholder when the user deletes all tasks.
        taskViewCalendarList.setPlaceholder(new Label("Deleted all tasks, please close!"));
    }

    // Sets the cells of the ListView nad inserts per cell one task.
    public void setTasks(ObservableList<Task> clickedTasks) {
        taskViewCalendarList.setCellFactory(Cell -> new TaskCell());
        taskViewCalendarList.setItems(clickedTasks);
    }

}
