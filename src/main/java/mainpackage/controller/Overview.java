package mainpackage.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
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

    private Overview ownController;
    private EntryLists entryLists = new EntryLists();
    private List<Task> usersTasks = new ArrayList<>();
    private List<Note> usersNotes = new ArrayList<>();

    private static Logger log = LogManager.getLogger(Overview.class);

    @FXML
    void initialize() {

        setLists();

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
        stage.setTitle("New Note");
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.showAndWait();
        setLists();

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
        stage.setTitle("New Task");
        stage.initStyle(StageStyle.TRANSPARENT);
        overviewAddItemImage.setDisable(true);
        stage.showAndWait();
        overviewAddItemImage.setDisable(false);
        setLists();


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

    void setLists() {

        overviewSpinner.setVisible(true);
        usersTasks.clear();
        usersNotes.clear();

        javafx.concurrent.Task<Void> thread = new javafx.concurrent.Task<>() {
            @Override
            public Void call() throws SQLException {
                entryLists.update();
                return null;
            }
        };

        new Thread(thread).start();

        thread.setOnSucceeded(e -> {

            entryLists.getNoteList().forEach(note -> usersNotes.add(note));
            entryLists.getTaskList().forEach(task -> usersTasks.add(task));


            overviewSpinner.setVisible(false);
        });
    }


    @FXML
    void reload(ActionEvent event) {
        for (Task usersTask : usersTasks) {
            System.out.println(usersTask);
        }
        System.out.println("_____________________");


        for (Note userNote : usersNotes) {
            System.out.println(userNote);
        }
        System.out.println("_____________________");
        setLists();
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

    public void setOwnController(Overview controller) {
        this.ownController = controller;
    }
}
