package mainpackage.controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mainpackage.ListManager;
import mainpackage.Main;
import mainpackage.animation.FadeIn;
import mainpackage.exceptions.UnsupportedCellType;
import mainpackage.model.Note;
import mainpackage.model.Task;
import mainpackage.threads.ClockThread;
import mainpackage.threads.SaveThread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main view after log in. Shows three different views of the created tasks.
 */

public class Overview {

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private ImageView overviewAddItemImage;
    @FXML
    private ImageView overviewAddNoteImage;
    @FXML
    private ImageView overviewCalendarImage;
    @FXML
    private ListView<Note> noteListView;
    @FXML
    private ImageView overviewExport;
    @FXML
    private JFXTextField noteListSearchField;
    @FXML
    private JFXComboBox<String> sortNoteListDropdown;
    @FXML
    private JFXToggleButton toggleArchiveButton;
    @FXML
    private ListView<Task> taskListView;
    @FXML
    private JFXTextField taskListSearchField;
    @FXML
    private JFXComboBox<String> sortTaskListDropdown;

    private static final Logger logger = LogManager.getLogger(Main.class.getName());
    private final ListManager listManager = new ListManager();
    private final ObservableList<Task> usersTasks       = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private final ObservableList<Task> usersTasksSearch = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private final ObservableList<Note> usersNotes       = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private final ObservableList<Note> usersNotesSearch = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private final ClockThread clock = new ClockThread();

    @FXML
    synchronized void initialize() {

        logger.info("Overview initializing");
        //listManager.getNoteList().forEach(usersNotes::add);
        //listManager.getTaskList().forEach(usersTasks::add);
        //setLists();

        overviewCalendarImage.setOnMouseClicked(mouseEvent -> loadCalendar());
        overviewAddItemImage.setOnMouseClicked(mouseEvent -> loadAddTask());
        overviewAddNoteImage.setOnMouseClicked(mouseEvent -> loadAddNote());
        overviewExport.setOnMouseClicked(mouseEvent -> export());

        // ToggleButton to switch between archived and active notes/tasks
        toggleArchiveButton.selectedProperty().addListener((arg0, arg1, arg2) -> {
            if(toggleArchiveButton.isSelected()) {
                noteListSearchField.clear();
                taskListSearchField.clear();
                toggleArchive();
            }
            else {
                noteListSearchField.clear();
                taskListSearchField.clear();
                toggleActive();
            }
        });

        noteListSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            //debugLogger.debug("Value Changed from: " + oldValue + " to " + newValue);

            if (!newValue.trim().isEmpty() && usersNotes.size() > 0) {
                usersNotesSearch.setAll(searchNotes(noteListSearchField.getText(), usersNotes));
                noteListView.setItems(usersNotesSearch);
            } else {
                noteListView.setItems(usersNotes);
            }

            //debugLogger.debug("Search");
            //debugLogger.info("TaskListView Size: " + todolistTaskList.getItems().size());
            //debugLogger.info("TaskList Size: " + taskListView.size());
            //debugLogger.info("tasks Arraylist Size: " + tasks.getTasks().size());
        });

        // sorting notes when DropDown value is changed
        sortNoteListDropdown.setOnAction(event -> sortNotes(sortNoteListDropdown.getValue()));

        // setting default sorting mechanism to "Sort by date (newest to oldest)"
        sortNoteListDropdown.setValue("Sort by date (newest to oldest)");

        taskListSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            //debugLogger.debug("Value Changed from: " + oldValue + " to " + newValue);

            if (!newValue.trim().isEmpty() && usersTasks.size() > 0) {
                usersTasksSearch.setAll(searchTasks(taskListSearchField.getText(), usersTasks));
                taskListView.setItems(usersTasksSearch);
            } else {
                taskListView.setItems(usersTasks);
            }

            //debugLogger.debug("Search");
            //debugLogger.info("TaskListView Size: " + todolistTaskList.getItems().size());
            //debugLogger.info("TaskList Size: " + taskListView.size());
            //debugLogger.info("tasks Arraylist Size: " + tasks.getTasks().size());
        });

        // sorting tasks when DropDown value is changed
        sortTaskListDropdown.setOnAction(event -> sortTasks(sortTaskListDropdown.getValue()));

        // setting default sorting mechanism to "Sort by date (newest to oldest)"
        sortTaskListDropdown.setValue("Sort by date (newest to oldest)");

        //Initializing clock
        clock.setLabels(timeLabel, dateLabel);
        clock.setDaemon(true);
        clock.start();

        ExecutorService ex = Executors.newCachedThreadPool();
        ex.execute(this::setNotes);
        ex.execute(this::setTasks);
        ex.shutdown();

        sortNotes(sortNoteListDropdown.getValue());
        sortTasks(sortTaskListDropdown.getValue());
    }

    /**
     * Sorting notes depending on selected String in sortNoteListDropdown (dropdown menu to sort notes in overview)
     * @param choice selected String in DropDown
     */
    private void sortNotes(String choice) {
        switch (choice) {
            case "Sort by date (newest to oldest)":
                sortNotesDateDesc(usersNotes);
                break;
            case "Sort by date (oldest to newest)":
                sortNotesDateAsc(usersNotes);
                break;
            case "Sort alphabetically (A-Z)":
                sortNotesTitleAsc(usersNotes);
                break;
            case "Sort alphabetically (Z-A)":
                sortNotesTitleDesc(usersNotes);
                break;
        }
    }

    /**
     * Sorting tasks depending on selected String in sortTaskListDropdown (dropdown menu to sort tasks in overview)
     * @param choice selected String in DropDown
     */
    private void sortTasks(String choice) {
        switch (choice) {
            case "Sort by date (newest to oldest)":
                sortTasksDateDesc(usersTasks);
                break;
            case "Sort by date (oldest to newest)":
                sortTasksDateAsc(usersTasks);
                break;
            case "Sort alphabetically (A-Z)":
                sortTasksTitleAsc(usersTasks);
                break;
            case "Sort alphabetically (Z-A)":
                sortTasksTitleDesc(usersTasks);
                break;
        }
    }


    /**
     * Clearing list of user's notes/tasks and adding only archived notes/tasks.
     * Result: only archived notes/tasks are shown when toggleArchiveButton is selected.
     */
    private synchronized void toggleArchive() {
        usersNotes.clear();
        usersTasks.clear();
        //Unload NotesList/TasksList from ListView to disable update properties from wrong thread
        noteListView.setItems(FXCollections.observableArrayList());
        listManager.getNoteList().filter(n -> n.getState()==2).forEach(usersNotes::add);
        taskListView.setItems(FXCollections.observableArrayList());
        listManager.getTaskList().filter(n -> n.getState()==2).forEach(usersTasks::add);
        //Reload NotesList/TasksList in ListView to update it in FX-Thread
        Platform.runLater(() -> noteListView.setItems(usersNotes));
        sortNotes(sortNoteListDropdown.getValue());
        Platform.runLater(() -> taskListView.setItems(usersTasks));
        sortTasks(sortTaskListDropdown.getValue());
        logger.info("Archived notes/tasks are now displayed.");
    }

    /**
     * Clearing list of user's notes/tasks and adding only archived notes/tasks.
     * Result: only active notes/tasks are shown when toggleArchiveButton is not selected.
     */
    private void toggleActive() {
        usersNotes.clear();
        usersTasks.clear();
        //Unload NotesList/TasksList from ListView to disable update properties from wrong thread
        noteListView.setItems(FXCollections.observableArrayList());
        taskListView.setItems(FXCollections.observableArrayList());
        listManager.getNoteList().filter(n -> n.getState()==0).forEach(usersNotes::add);
        listManager.getTaskList().filter(n -> n.getState()==0 || n.getState()==1).forEach(usersTasks::add);
        //Reload NotesList/TasksList in ListView to update it in FX-Thread
        Platform.runLater(() -> noteListView.setItems(usersNotes));
        sortNotes(sortNoteListDropdown.getValue());
        Platform.runLater(() -> taskListView.setItems(usersTasks));
        sortTasks(sortTaskListDropdown.getValue());
        logger.info("Active notes are now displayed.");
    }

    /**
     * Sorting list of user's notes by date (descending)
     * @param usersNotes list of user's notes
     */
    private void sortNotesDateDesc(ObservableList<Note> usersNotes) {
            usersNotes.sort((t1, t2) -> t2.getCreationDate().compareTo(t1.getCreationDate()));
            logger.info("Note list " + usersNotes.toString() + " sorted by creation dates in descending order.");
    }

    /**
     * Sorting list of user's notes by date (ascending)
     * @param usersNotes list of user's notes
     */
    private void sortNotesDateAsc(ObservableList<Note> usersNotes) {
        usersNotes.sort(Comparator.comparing(Note::getCreationDate));
        logger.info("Note list " + usersNotes.toString() + " sorted by creation dates in ascending order.");
    }

    /**
     * Sorting list of user's notes alphabetically (ascending)
     * @param usersNotes list of user's notes
     */
    private void sortNotesTitleAsc(ObservableList<Note> usersNotes) {
        usersNotes.sort(Comparator.comparing(n -> n.getTitle().toUpperCase()));
        logger.info("Note list " + usersNotes.toString() + " sorted by title in ascending order.");
    }

    /**
     * Sorting list of user's notes alphabetically (descending)
     * @param usersNotes list of user's notes
     */
    private void sortNotesTitleDesc(ObservableList<Note> usersNotes) {
        usersNotes.sort((n1, n2) -> n2.getTitle().toUpperCase().compareTo(n1.getTitle().toUpperCase()));
        logger.info("Note list " + usersNotes.toString() + " sorted by title in descending order.");
    }

    /**
     * Sorting list of user's tasks by date (descending)
     * @param usersTasks list of user's tasks
     */
    private void sortTasksDateDesc(ObservableList<Task> usersTasks) {
        usersTasks.sort((t1, t2) -> t2.getCreationDate().compareTo(t1.getCreationDate()));
        logger.info("Task list " + usersTasks.toString() + " sorted by creation dates in descending order.");
    }

    /**
     * Sorting list of user's tasks by date (ascending)
     * @param usersTasks list of user's tasks
     */
    private void sortTasksDateAsc(ObservableList<Task> usersTasks) {
        usersTasks.sort(Comparator.comparing(Task::getCreationDate));
        logger.info("Task list " + usersTasks.toString() + " sorted by creation dates in ascending order.");
    }

    /**
     * Sorting list of user's tasks alphabetically (ascending)
     * @param usersTasks list of user's tasks
     */
    private void sortTasksTitleAsc(ObservableList<Task> usersTasks) {
        usersTasks.sort(Comparator.comparing(n -> n.getTitle().toUpperCase()));
        logger.info("Task list " + usersTasks.toString() + " sorted by title in ascending order.");
    }

    /**
     * Sorting list of user's tasks alphabetically (descending)
     * @param usersTasks list of user's tasks
     */
    private void sortTasksTitleDesc(ObservableList<Task> usersTasks) {
        usersTasks.sort((n1, n2) -> n2.getTitle().toUpperCase().compareTo(n1.getTitle().toUpperCase()));
        logger.info("Task list " + usersTasks.toString() + " sorted by title in descending order.");
    }

    /**
     * Exporting notes and tasks into a .txt file on user's computer
     */
    private void export() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("Orga-Exports.txt");
        File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
        if (file != null) {
            SaveThread save = new SaveThread(file);
            save.setDaemon(true);
            save.start();
        }
    }

    public synchronized void setNotes() {

        // Placeholder if user has no notes
        Label noNotes = new Label("No notes yet!");
        noNotes.setFont(new Font(20));
        noteListView.setPlaceholder(noNotes);

        usersNotes.clear();
        listManager.getNoteList().filter(t->t.getState()==0).forEach(usersNotes::add);
        CellFactory cellFactory = new CellFactory();
        noteListView.setCellFactory(NoteCell -> {
            try {
                return cellFactory.createCell("note");
            } catch (UnsupportedCellType unsupportedCellType) {
                unsupportedCellType.printStackTrace();
                return new JFXListCell<>();
            }
        });
        noteListView.setItems(usersNotes);
        logger.info("Notes loaded to overview listview");
    }

    public synchronized void setTasks() {

        // Placeholder if user has no tasks
        Label noTasks = new Label("No tasks yet!");
        noTasks.setFont(new Font(20));
        taskListView.setPlaceholder(noTasks);

        usersTasks.clear();
        listManager.getTaskList().filter(t->t.getState()==0||t.getState()==1).forEach(usersTasks::add);
        CellFactory cellFactory = new CellFactory();
        taskListView.setCellFactory(TaskCell -> {
           try {
               return cellFactory.createCell("task");
           } catch (UnsupportedCellType unsupportedCellType) {
               unsupportedCellType.printStackTrace();
               return new JFXListCell<>();
           }
        });
        taskListView.setItems(usersTasks);

        logger.info("Tasks loaded to overview listview");

    }

    private void loadAddNote() {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/CreateNotes.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
        overviewAddNoteImage.setDisable(true);
        stage.showAndWait();
        if (!toggleArchiveButton.isSelected()) {
            usersNotes.add(listManager.getLatestNote());
        }
        sortNotes(sortNoteListDropdown.getValue());
        overviewAddNoteImage.setDisable(false);

    }

    private void loadAddTask() {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/CreateTask.fxml"));
        loader.setController(new CreateTask());

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
        overviewAddItemImage.setDisable(true);
        stage.showAndWait();
        if(!toggleArchiveButton.isSelected()) {
            usersTasks.add(listManager.getLatestTask());
        }
        sortTasks(sortTaskListDropdown.getValue());
        overviewAddItemImage.setDisable(false);

    }

    private void loadCalendar() {

        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setTitle("Calendar");

        AnchorPane calendar = null;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/Calendar.fxml"));
        try {
            calendar = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new FadeIn(rootPane).play();
        rootPane.getChildren().clear();
        rootPane.getChildren().setAll(calendar);

    }

    private ArrayList<Note> searchNotes(String filter, ObservableList<Note> list) {

        //debugLogger.info("Searching for the filter : " + filter + "in list " + list.toString());
        ArrayList<Note> searchResult = new ArrayList<>();
            if (!filter.isEmpty() && !filter.trim().equals("")) {
                //debugLogger.info("Searching for a task containing the filter: '" + filter + "'.");
                for (Note t : list) {
                    if (t.getTitle().toLowerCase().contains(filter.toLowerCase()) || t.getContent().toLowerCase().contains(filter.toLowerCase()) || t.getCreationDate().toString().contains(filter.toLowerCase())) {
                        searchResult.add(t);
                    }
                }
                return searchResult;
            } else if (searchResult.isEmpty()) {
                // debugLogger.info("No task found containing the filter: '" + filter + "'.");
            } else {
                searchResult.addAll(list);
            }
            return searchResult;

    }

    private ArrayList<Task> searchTasks(String filter, ObservableList<Task> list) {

        //debugLogger.info("Searching for the filter : " + filter + "in list " + list.toString());
        ArrayList<Task> searchResult = new ArrayList<>();
        if (!filter.isEmpty() && !filter.trim().equals("")) {
            //debugLogger.info("Searching for a task containing the filter: '" + filter + "'.");
            for (Task t : list) {
                if (t.getTitle().toLowerCase().contains(filter.toLowerCase()) || t.getContent().toLowerCase().contains(filter.toLowerCase()) || t.getCreationDate().toString().contains(filter.toLowerCase())) {
                    searchResult.add(t);
                }
            }
            return searchResult;
        } else if (searchResult.isEmpty()) {
            // debugLogger.info("No task found containing the filter: '" + filter + "'.");
        } else {
            searchResult.addAll(list);
        }
        return searchResult;

    }

    @FXML
    void logout(ActionEvent event) {

        ListManager.wipe();

        AnchorPane login = null;
        try {
            login = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        rootPane.getChildren().setAll(login);
        new FadeIn(login).play();

    }
}
