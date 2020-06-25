package mainpackage.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mainpackage.ListManager;
import mainpackage.model.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/**
 * Main view after log in. Shows three different views of the created tasks.
 */

public class Overview implements Runnable {

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
    private JFXSpinner overviewSpinner;


    private String time = "", month = "", day = "";

    private ListManager listManager = new ListManager();
    private List<Task> usersTasks = FXCollections.observableArrayList();
    private List<Note> usersNotes = FXCollections.observableArrayList();

    private static Logger log = LogManager.getLogger(Overview.class);

    @FXML
    void initialize() {

        listManager.getNoteList().forEach(note -> usersNotes.add(note));
        listManager.getTaskList().forEach(task -> usersTasks.add(task));
        overviewSpinner.setVisible(false);
        //setLists();

        overviewCalendarImage.setOnMouseClicked(mouseEvent -> loadCalendar());
        overviewAddItemImage.setOnMouseClicked(mouseEvent -> loadAddTask());
        overviewAddNoteImage.setOnMouseClicked(mouseEvent -> loadAddNote());

        //Initializing clock variables
        Thread clock = new Thread(this);
        clock.setDaemon(true);
        clock.start();

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

        overviewSpinner.setVisible(true);

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
    }

    //Deprecated
    void setLists() {

        overviewSpinner.setVisible(true);
        usersTasks.clear();
        usersNotes.clear();

        javafx.concurrent.Task<Void> thread = new javafx.concurrent.Task<>() {
            @Override
            public Void call() throws SQLException {
                listManager.update();
                return null;
            }
        };

        new Thread(thread).start();

        thread.setOnSucceeded(e -> {

            listManager.getNoteList().forEach(note -> usersNotes.add(note));
            listManager.getTaskList().forEach(task -> usersTasks.add(task));

            overviewSpinner.setVisible(false);
        });
    }


    @FXML
    void reload(ActionEvent event) {

        listManager.getTaskList().forEach(System.out::println);

        System.out.println("------------------------");

        for (Note userNote : usersNotes) {
            System.out.println(userNote);
        }
        System.out.println("_____________________");
        //setLists();
        //usersTasks.clear();
        //setUser(loggedInUser);
    }

    @Override
    public void run() {
        try {
            while (true) {

                //Setting date format and variables:

                Calendar calendar = Calendar.getInstance();

                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                Date date = calendar.getTime();
                time = format.format(date);

                format = new SimpleDateFormat("EEE, MMMM dd yyyy");
                date = calendar.getTime();
                month = format.format(date);

                //Setting elements to pane:

                Platform.runLater(() -> {
                    dateLabel.setText(String.valueOf(month));
                    timeLabel.setText(time);

                });

                Thread.sleep(1000);
            }
        } catch (Exception e) { //Error check
            dateLabel.setText("");
            timeLabel.setText("Error occurred!!");
        }


    }

}
