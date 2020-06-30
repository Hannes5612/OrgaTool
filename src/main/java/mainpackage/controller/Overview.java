package mainpackage.controller;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import mainpackage.ListManager;
import mainpackage.exceptions.UnsupportedCellType;
import mainpackage.model.Note;
import mainpackage.model.Task;
import mainpackage.threads.ClockThread;
import mainpackage.threads.SaveThread;

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


    private static final ListManager listManager = new ListManager();
    private final ObservableList<Task> usersTasks = FXCollections.observableArrayList();
    private final ObservableList<Note> usersNotes = FXCollections.observableArrayList();
    private final ObservableList<Note> usersNotesSearch = FXCollections.observableArrayList();
    private final ClockThread clock = new ClockThread();
    private static final Logger log = LogManager.getLogger(Overview.class);

    @FXML
    void initialize() {

        //listManager.getNoteList().forEach(usersNotes::add);
        listManager.getTaskList().forEach(usersTasks::add);
        //setLists();

        overviewCalendarImage.setOnMouseClicked(mouseEvent -> loadCalendar());
        overviewAddItemImage.setOnMouseClicked(mouseEvent -> loadAddTask());
        overviewAddNoteImage.setOnMouseClicked(mouseEvent -> loadAddNote());
        overviewExport.setOnMouseClicked(mouseEvent -> export());

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


        //Initializing clock
        clock.setLabels(timeLabel, dateLabel);
        clock.setDaemon(true);
        clock.start();

        setNotes();
    }

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
        listManager.getNoteList().forEach(usersNotes::add);
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
        stage.initStyle(StageStyle.TRANSPARENT);
        overviewAddNoteImage.setDisable(true);
        stage.showAndWait();
        overviewAddNoteImage.setDisable(false);

    }

    private void loadAddTask() {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/CreateTask.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initStyle(StageStyle.TRANSPARENT);
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
        rootPane.getChildren().clear();
        rootPane.getChildren().setAll(calendar);
        new FadeIn(calendar).play();
    }

    //Deprecated
    void setLists() {

        usersTasks.clear();
        usersNotes.clear();

        javafx.concurrent.Task<Void> thread = new javafx.concurrent.Task<>() {
            @Override
            public Void call() throws SQLException, ClassNotFoundException {
                listManager.update();
                return null;
            }
        };

        new Thread(thread).start();

        thread.setOnSucceeded(e -> {

            listManager.getNoteList().forEach(usersNotes::add);
            listManager.getTaskList().forEach(usersTasks::add);

        });
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
