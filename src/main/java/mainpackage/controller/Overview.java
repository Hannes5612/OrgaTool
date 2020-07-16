package mainpackage.controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Main view after log in.
 * Shows three different views of the created tasks.
 */

public class Overview {

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
    private final ObservableList<Task> usersTasks = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private final ObservableList<Task> usersTasksSearch = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private final ObservableList<Note> usersNotes = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private final ObservableList<Note> usersNotesSearch = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private ClockThread clock;
    private ExecutorService exec = Executors.newCachedThreadPool();

    @FXML
    synchronized void initialize() {

        // create executor that uses daemon threads:
        exec = Executors.newCachedThreadPool(runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });


        logger.info("Overview initializing");

        overviewCalendarImage.setOnMouseClicked(mouseEvent -> loadCalendar());
        overviewAddItemImage.setOnMouseClicked(mouseEvent -> loadAddTask());
        overviewAddNoteImage.setOnMouseClicked(mouseEvent -> loadAddNote());

        //Handler to export ones entries
        overviewExport.setOnMouseClicked(mouseEvent -> export());

        // ToggleButton to switch between archived and active notes/tasks
        toggleArchiveButton.selectedProperty().addListener(this::toggleSwitched);

        // ToDo: notes/tasks in eine Zeile

        //listener for changed search field
        noteListSearchField.textProperty().addListener(this::changedNotesSearchField);

        // sorting notes when DropDown value is changed
        sortNoteListDropdown.setOnAction(event -> sortNotes(sortNoteListDropdown.getValue()));

        // setting default sorting mechanism to "Sort by date (newest to oldest)"
        sortNoteListDropdown.setValue("Sort by date (newest to oldest)");

        //listener for changed search field
        taskListSearchField.textProperty().addListener(this::changedTaskSearchField);

        // sorting tasks when DropDown value is changed
        sortTaskListDropdown.setOnAction(event -> sortTasks(sortTaskListDropdown.getValue()));

        // setting default sorting mechanism to "Sort by date (newest to oldest)"
        sortTaskListDropdown.setValue("Sort by date (newest to oldest)");

        noteListView.setItems(usersNotes);
        taskListView.setItems(usersTasks);

        toggleActive();
        setListViews();

        //Initializing clock
        clock = new ClockThread(timeLabel, dateLabel);
        clock.setDaemon(true);
        clock.start();

    }

    /**
     * Sorting notes depending on selected String in sortNoteListDropdown (dropdown menu to sort notes in overview).
     *
     * @param choice - selected String in DropDown
     */
    private void sortNotes(String choice) {
        switch (choice) {
            case "Sort by date (newest to oldest)":
                usersNotes.sort((t1, t2) -> t2.getCreationDate().compareTo(t1.getCreationDate()));
                logger.info("Note list " + usersNotes.toString() + " sorted by creation dates in descending order.");
                break;
            case "Sort by date (oldest to newest)":
                usersNotes.sort(Comparator.comparing(Note::getCreationDate));
                logger.info("Note list " + usersNotes.toString() + " sorted by creation dates in ascending order.");
                break;
            case "Sort alphabetically (A-Z)":
                usersNotes.sort(Comparator.comparing(n -> n.getTitle().toUpperCase()));
                logger.info("Note list " + usersNotes.toString() + " sorted by title in ascending order.");
                break;
            case "Sort alphabetically (Z-A)":
                usersNotes.sort((n1, n2) -> n2.getTitle().toUpperCase().compareTo(n1.getTitle().toUpperCase()));
                logger.info("Note list " + usersNotes.toString() + " sorted by title in descending order.");
                break;
        }
    }

    /**
     * Sorting tasks depending on selected String in sortTaskListDropdown (dropdown menu to sort tasks in overview).
     *
     * @param choice - selected String in DropDown
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
            case "Sort by priority":
                sortTasksByPriority();
                break;
        }

    }

    private void sortTasksByPriority() {

        usersTasks.sort((t1, t2) -> {
            String firstPriority = t1.getPriority();
            String secondPriority = t2.getPriority();
            if(firstPriority.equals(secondPriority)) return 0;
            if(firstPriority.equals("H")) return -1;
            if(secondPriority.equals("H")) return 1;
            if(firstPriority.equals("M")) return -1;
            if(secondPriority.equals("M")) return 1;
            if(firstPriority.equals("L")) return -1;
            return 1;
        });

    }

    /**
     * Clearing list of user's notes/tasks and adding only archived notes/tasks.
     * Result: only archived notes/tasks are shown when toggleArchiveButton is selected.
     */
    private void toggleArchive() {

        javafx.concurrent.Task<List<Note>> getNotesTask = new javafx.concurrent.Task<>() {
            @Override
            public List<Note> call() {
                logger.info("Fetching archived notes.");
                return listManager.getNoteList()
                        .filter(n -> n.getState() == 2)
                        .collect(Collectors.toList());
            }
        };

        javafx.concurrent.Task<List<Task>> getTasksTask = new javafx.concurrent.Task<>() {
            @Override
            public List<Task> call() {
                logger.info("Fetching archived tasks.");
                return listManager.getTaskList()
                        .filter(n -> n.getState() == 2)
                        .collect(Collectors.toList());
            }
        };

        getNotesTask.setOnSucceeded(e -> {
            usersNotes.clear();
            usersNotes.setAll(getNotesTask.getValue());
            sortNotes(sortNoteListDropdown.getValue());
            logger.info("Archived notes are now displayed.");
        });
        getTasksTask.setOnSucceeded(e -> {
            usersTasks.clear();
            usersTasks.setAll(getTasksTask.getValue());
            sortTasks(sortTaskListDropdown.getValue());
            logger.info("Archived Tasks are now displayed.");
        });

        exec.submit(getTasksTask);
        exec.submit(getNotesTask);

    }

    /**
     * Clearing list of user's notes/tasks and adding only archived notes/tasks, perfectly synchronized access.
     * Result: only active notes/tasks are shown when toggleArchiveButton is not selected.
     */
    private void toggleActive() {

        javafx.concurrent.Task<List<Note>> getNotesTask = new javafx.concurrent.Task<>() {
            @Override
            public List<Note> call() {
                logger.info("Fetching active notes.");
                return listManager.getNoteList()
                        .filter(n -> n.getState() == 0)
                        .collect(Collectors.toList());
            }
        };

        javafx.concurrent.Task<List<Task>> getTasksTask = new javafx.concurrent.Task<>() {
            @Override
            public List<Task> call() {

                logger.info("Fetching active tasks.");
                return listManager.getTaskList()
                        .filter(n -> n.getState() == 0 || n.getState() == 1)
                        .collect(Collectors.toList());
            }
        };

        getTasksTask.setOnSucceeded(e -> {
            usersTasks.clear();
            usersTasks.setAll(getTasksTask.getValue());
            sortTasks(sortNoteListDropdown.getValue());
            logger.info("Active Tasks are now displayed.");
        });
        getNotesTask.setOnSucceeded(e -> {
            usersNotes.clear();
            usersNotes.setAll(getNotesTask.getValue());
            sortNotes(sortTaskListDropdown.getValue());
            logger.info("Active notes are now displayed.");
        });

        exec.submit(getTasksTask);
        exec.submit(getNotesTask);

    }

    /* ToDo: entfernen?
    private void sortTasksByPriority(String priority) {
        usersTasksSearch.setAll(searchTasksByPriority(priority, usersTasks));
        taskListView.setItems(usersTasksSearch);
    }
    */

    /**
     * Sorting list of user's tasks by date (descending).
     *
     * @param usersTasks - list of user's tasks
     */
    private void sortTasksDateDesc(ObservableList<Task> usersTasks) {
        usersTasks.sort((t1, t2) -> t2.getCreationDate().compareTo(t1.getCreationDate()));
        logger.info("Task list " + usersTasks.toString() + " sorted by creation dates in descending order.");
    }

    /**
     * Sorting list of user's tasks by date (ascending).
     *
     * @param usersTasks - list of user's tasks
     */
    private void sortTasksDateAsc(ObservableList<Task> usersTasks) {
        usersTasks.sort(Comparator.comparing(Task::getCreationDate));
        logger.info("Task list " + usersTasks.toString() + " sorted by creation dates in ascending order.");
    }

    /**
     * Sorting list of user's tasks alphabetically (ascending).
     *
     * @param usersTasks - list of user's tasks
     */
    private void sortTasksTitleAsc(ObservableList<Task> usersTasks) {
        usersTasks.sort(Comparator.comparing(n -> n.getTitle().toUpperCase()));
        logger.info("Task list " + usersTasks.toString() + " sorted by title in ascending order.");
    }

    /**
     * Sorting list of user's tasks alphabetically (descending).
     *
     * @param usersTasks - list of user's tasks
     */
    private void sortTasksTitleDesc(ObservableList<Task> usersTasks) {
        usersTasks.sort((n1, n2) -> n2.getTitle().toUpperCase().compareTo(n1.getTitle().toUpperCase()));
        logger.info("Task list " + usersTasks.toString() + " sorted by title in descending order.");
    }

    /**
     * Exporting notes and tasks into a .txt file on user's computer.
     */
    private void export() {

        //Create a file with assigned path
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialFileName("Orga-Exports.txt");
        File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());

        //If file and path was chosen, open a file saving thread with the respective thread
        if (file != null) {
            SaveThread save = new SaveThread(file);
            save.setDaemon(true);
            save.start();
        }

    }

    /**
     * Initializing the ListViews to display correct cells.
     */
    private void setListViews() {

        // Placeholder if user has no notes
        Label noNotes = new Label("No notes yet!");
        Label noTasks = new Label("No tasks yet!");
        noNotes.setFont(new Font(20));
        noTasks.setFont(new Font(20));
        taskListView.setPlaceholder(noTasks);
        noteListView.setPlaceholder(noNotes);


        CellFactory cellFactory = new CellFactory();
        taskListView.setCellFactory(ListCell -> {
            try {
                ListCell task = cellFactory.createCell("task");
                task.setWrapText(true);
                return task;
            } catch (UnsupportedCellType unsupportedCellType) {
                unsupportedCellType.printStackTrace();
                return new JFXListCell<>();
            }
        });

        noteListView.setCellFactory(ListCell -> {
            try {
                ListCell note = cellFactory.createCell("note");
                note.setWrapText(true);
                return note;
            } catch (UnsupportedCellType unsupportedCellType) {
                unsupportedCellType.printStackTrace();
                return new ListCell<>();
            }
        });

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
            toggleActive();
        } else {
            toggleArchive();
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
        if (!toggleArchiveButton.isSelected()) {
            toggleActive();
        } else {
            toggleArchive();
        }
        sortTasks(sortTaskListDropdown.getValue());
        overviewAddItemImage.setDisable(false);

    }

    private void loadCalendar() {

        Stage stage = (Stage) taskListView.getScene().getWindow();
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

        logger.info("Searching for the filter : " + filter + "in list " + list.toString());
        ArrayList<Note> searchResult = new ArrayList<>();
        if (!filter.isEmpty() && !filter.trim().equals("")) {
            logger.info("Searching for a note containing the filter: '" + filter + "'.");
            for (Note t : list) {
                if (t.getTitle().toLowerCase().contains(filter.toLowerCase()) || t.getContent().toLowerCase().contains(filter.toLowerCase()) || t.getCreationDate().toString().contains(filter.toLowerCase())) {
                    searchResult.add(t);
                }
            }
            return searchResult;
        } else if (searchResult.isEmpty()) {
            logger.info("No note found containing the filter: '" + filter + "'.");
        } else {
            searchResult.addAll(list);
        }
        return searchResult;

    }

    private ArrayList<Task> searchTasks(String filter, ObservableList<Task> list) {

        logger.info("Searching for the filter : " + filter + "in list " + list.toString());
        ArrayList<Task> searchResult = new ArrayList<>();
        if (!filter.isEmpty() && !filter.trim().equals("")) {
            logger.info("Searching for a task containing the filter: '" + filter + "'.");
            for (Task t : list) {
                if (t.getTitle().toLowerCase().contains(filter.toLowerCase()) || t.getContent().toLowerCase().contains(filter.toLowerCase()) || t.getCreationDate().toString().contains(filter.toLowerCase())) {
                    searchResult.add(t);
                }
            }
            return searchResult;
        } else if (searchResult.isEmpty()) {
            logger.info("No task found containing the filter: '" + filter + "'.");
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

        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setTitle("Login");
        rootPane.getChildren().setAll(login);
        new FadeIn(login).play();

    }

    private void changedTaskSearchField(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        logger.debug("Value Changed from: " + oldValue + " to " + newValue);

        if (!newValue.trim().isEmpty() && usersTasks.size() > 0) {
            usersTasksSearch.setAll(searchTasks(taskListSearchField.getText(), usersTasks));
            taskListView.setItems(usersTasksSearch);
        } else {
            taskListView.setItems(usersTasks);
        }
    }

    private void changedNotesSearchField(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        logger.debug("Value Changed from: " + oldValue + " to " + newValue);

        if (!newValue.trim().isEmpty() && usersNotes.size() > 0) {
            usersNotesSearch.setAll(searchNotes(noteListSearchField.getText(), usersNotes));
            noteListView.setItems(usersNotesSearch);
        } else {
            noteListView.setItems(usersNotes);
        }
    }

    private void toggleSwitched(Observable e) {
        if (toggleArchiveButton.isSelected()) {
            noteListSearchField.clear();
            taskListSearchField.clear();
            toggleArchive();
        } else {
            noteListSearchField.clear();
            taskListSearchField.clear();
            toggleActive();
        }
    }

}
