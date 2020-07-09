package mainpackage.controller;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mainpackage.ListManager;
import mainpackage.animation.FadeIn;
import mainpackage.exceptions.UnsupportedCellType;
import mainpackage.model.Note;
import mainpackage.model.Task;
import mainpackage.threads.ClockThread;
import mainpackage.threads.SaveThread;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ResourceBundle;

//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;

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
    private JFXListView<Note> noteListView;
    @FXML
    private ImageView overviewExport;
    @FXML
    private JFXTextField noteListSearchField;
    @FXML
    private JFXComboBox<String> sortNoteListDropdown;
    @FXML
    private JFXToggleButton toggleArchiveButton;
    @FXML
    private JFXListView<Task> taskListView;
    @FXML
    private JFXTextField taskListSearchField;
    @FXML
    private JFXComboBox<String> sortTaskListDropdown;

    private static final ListManager listManager = new ListManager();
    private final ObservableList<Task> usersTasks = FXCollections.observableArrayList();
    private final ObservableList<Task> usersTasksSearch = FXCollections.observableArrayList();
    private final ObservableList<Note> usersNotes = FXCollections.observableArrayList();
    private final ObservableList<Note> usersNotesSearch = FXCollections.observableArrayList();
    private final ClockThread clock = new ClockThread();
//    private static final Logger log = LogManager.getLogger(Overview.class);

    @FXML
    void initialize() {

        //listManager.getNoteList().forEach(usersNotes::add);
        listManager.getTaskList().forEach(usersTasks::add);
        //setLists();

        overviewCalendarImage.setOnMouseClicked(mouseEvent -> loadCalendar());
        overviewAddItemImage.setOnMouseClicked(mouseEvent -> loadAddTask());
        overviewAddNoteImage.setOnMouseClicked(mouseEvent -> loadAddNote());
        overviewExport.setOnMouseClicked(mouseEvent -> export());

        toggleArchiveButton.selectedProperty().addListener((arg0, arg1, arg2) -> {
            if(toggleArchiveButton.isSelected()) {
                noteListSearchField.clear();
                toggleArchive();
            }
            else {
                noteListSearchField.clear();
                toggleActive();
            }
        });

        noteListSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            //debugLogger.debug("Value Changed from: " + oldValue + " to " + newValue);

            if (!newValue.trim().isEmpty() && usersNotes.size() > 0) {
                usersNotesSearch.setAll(search(noteListSearchField.getText(), usersNotes));
                noteListView.setItems(usersNotesSearch);
            } else {
                noteListView.setItems(usersNotes);
            }

            //debugLogger.debug("Search");
            //debugLogger.info("TaskListView Size: " + todolistTaskList.getItems().size());
            //debugLogger.info("TaskList Size: " + taskListView.size());
            //debugLogger.info("tasks Arraylist Size: " + tasks.getTasks().size());
        });

        sortNoteListDropdown.setOnAction(event -> sortNotes(sortNoteListDropdown.getValue()));

        sortNoteListDropdown.setValue("Sort by date (newest to oldest)");

        //Initializing clock
        clock.setLabels(timeLabel, dateLabel);
        clock.setDaemon(true);
        clock.start();

        setNotes();
        sortNotes(sortNoteListDropdown.getValue());
        setTasks();
    }

    /**
     * Sorting notes depending on selected String in sortNoteListDropdown (dropdown menu to sort notes in overview)
     * @param choice selected String in DropDown
     */
    private void sortNotes(String choice) {
        switch (choice) {
            case "Sort by date (newest to oldest)":
                sortDateDesc(usersNotes);
                break;
            case "Sort by date (oldest to newest)":
                sortDateAsc(usersNotes);
                break;
            case "Sort alphabetically (A-Z)":
                sortTitleAsc(usersNotes);
                break;
            case "Sort alphabetically (Z-A)":
                sortTitleDesc(usersNotes);
                break;
        }
    }


    /**
     * Clearing list of user's note and adding only archived notes.
     * Result: only archived notes are shown when toggleArchiveButton is selected
     */
    private void toggleArchive() {
        usersNotes.clear();
        listManager.getNoteList().forEach((n) -> {
            if (n.getState() == 2) {
                usersNotes.add(n);
            }
        });
        sortNotes(sortNoteListDropdown.getValue());
    }

    /**
     * Clearing list of user's note and adding only archived notes.
     * Result: only active notes are shown when toggleArchiveButton is not selected
     */
    private void toggleActive() {
        usersNotes.clear();
        listManager.getNoteList().forEach((n) -> {
            if (n.getState() == 0) {
                usersNotes.add(n);
            }
        });
        sortNotes(sortNoteListDropdown.getValue());
    }

    /**
     * Sorting list of user's notes by date (descending)
     * @param usersNotes list of user's notes
     */
    private void sortDateDesc(ObservableList<Note> usersNotes) {
        usersNotes.sort((t1, t2) -> t2.getCreationDate().compareTo(t1.getCreationDate()));
        //debugLogger.info("List " + list.toString() + "  sorted by takdates in descending order.");
    }

    /**
     * Sorting list of user's notes by date (ascending)
     * @param usersNotes list of user's notes
     */
    private void sortDateAsc(ObservableList<Note> usersNotes) {
        usersNotes.sort(Comparator.comparing(Note::getCreationDate));
        //debugLogger.info("List " + list.toString() + "  sorted by takdates in descending order.");

    }

    /**
     * Sorting list of user's notes alphabetically (ascending)
     * @param usersNotes list of user's notes
     */
    private void sortTitleAsc(ObservableList<Note> usersNotes) {
        usersNotes.sort(Comparator.comparing(n -> n.getTitle().toUpperCase()));
        //debugLogger.info("List " + list.toString() + "  sorted by title in ascending order.");
    }

    /**
     * Sorting list of user's notes alphabetically (descending)
     * @param usersNotes list of user's notes
     */
    private void sortTitleDesc(ObservableList<Note> usersNotes) {
        usersNotes.sort((n1, n2) -> n2.getTitle().toUpperCase().compareTo(n1.getTitle().toUpperCase()));
        //debugLogger.info("List " + list.toString() + "  sorted by title in descending order.");
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

    public void setNotes() {

        // Placeholder if user has no notes
        Label noNotes = new Label("No notes yet!");
        noNotes.setFont(new Font(20));
        noteListView.setPlaceholder(noNotes);

        CellFactory cellFactory = new CellFactory();
        usersNotes.clear();
        // listManager.getNoteList().forEach(usersNotes::add);
        listManager.getNoteList().forEach((n) -> {
            if (n.getState() == 0) {
                usersNotes.add(n);
            }
        });
        noteListView.setCellFactory(NoteCell -> {
            try {
                return cellFactory.createCell("note");
            } catch (UnsupportedCellType unsupportedCellType) {
                unsupportedCellType.printStackTrace();
                return new JFXListCell<>();
            }
        });
        noteListView.setItems(usersNotes);
    }

    public void setTasks() {

        // Placeholder if user has no tasks
        Label noTasks = new Label("No tasks yet!");
        noTasks.setFont(new Font(20));
        taskListView.setPlaceholder(noTasks);

        CellFactory cellFactory = new CellFactory();
        usersTasks.clear();
        listManager.getTaskList().forEach((n) -> {
            if (n.getState() == 0 || n.getState() == 1) {
                usersTasks.add(n);
            }
        });
        taskListView.setCellFactory(TaskCell -> {
            try {
                return cellFactory.createCell("task");
            } catch (UnsupportedCellType unsupportedCellType) {
                unsupportedCellType.printStackTrace();
                return new JFXListCell<>();
            }
        });
        taskListView.setItems(usersTasks);

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
        overviewAddItemImage.setDisable(false);

    }

    private void loadCalendar() {

        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setTitle("Calendar");

        System.out.println("Signup clicked, changing screen");
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


    private ArrayList<Note> search(String filter, ObservableList<Note> list) {

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

    @FXML
    void reload(ActionEvent event) {

        // listManager.getTaskList().forEach(System.out::println);
        listManager.getNoteList().forEach(System.out::println);

        System.out.println("------------------------");
        //setLists();
        //usersTasks.clear();
        //setUser(loggedInUser);

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
