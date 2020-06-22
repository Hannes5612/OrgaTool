package mainpackage.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.Note;
import mainpackage.model.Task;
import mainpackage.model.User;
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


    //Initializing variables
    private Overview ownController;
    private User loggedInUser;
    private Thread thread = null;
    private String time = "", month = "", day = "";
    private SimpleDateFormat format;
    private Date date;
    private Calendar calendar;
    private DatabaseHandler databaseHandler = new DatabaseHandler();
    private ArrayList<Task> usersTasks = new ArrayList<>();
    private ArrayList<Note> usersNotes = new ArrayList<>();

    private static Logger log = LogManager.getLogger(Overview.class);

    @FXML
    void initialize() {

        overviewCalendarImage.setOnMouseClicked(mouseEvent -> {

            overviewSpinner.setVisible(true);

            usersTasks.clear();
            setUser(loggedInUser);


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
            mainpackage.controller.Calendar controller = loader.getController();
            controller.setTasks(loggedInUser);
            controller.setOwnController(controller);
            controller.setOverviewController(ownController);
            rootPane.getChildren().clear();
            rootPane.getChildren().setAll(calendar);

            //Old method to open a new window.

            /*
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/Calendar.fxml"));

            try {
                loader.load();
            } catch (IOException e) {                                                         //Load overview screen
                e.printStackTrace();
            }
            mainpackage.controller.Calendar controller = loader.getController();
            controller.setTasks(loggedInUser);
            controller.setOwnController(controller);
            controller.setOverviewController(ownController);
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Calendar");
            stage.setResizable(true);
            stage.showAndWait();
*/

        });

        overviewAddItemImage.setOnMouseClicked(mouseEvent -> {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/CreateTask.fxml"));

            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            CreateTask controller = loader.getController();
            controller.setUser(loggedInUser);
            controller.setOverviewController(ownController);
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("New Task");
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.showAndWait();


        });

        overviewAddNoteImage.setOnMouseClicked(mouseEvent -> {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/CreateNotes.fxml"));

            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            CreateNote controller = loader.getController();
            controller.setUser(loggedInUser);
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("New Note");
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.showAndWait();

        });

        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();

    }

    void setUser(User user) { //TODO move dbhandler in new task

        overviewSpinner.setVisible(true);
        usersTasks.clear();
        this.loggedInUser = user;


        javafx.concurrent.Task<ResultSet> taskDB = new javafx.concurrent.Task<>() {
            @Override
            public ResultSet call() {
                //Catch the tables' row of the search result, given the users credentials
                ResultSet taskRow = databaseHandler.getTasks(loggedInUser);
                return taskRow;
            }
        };

        javafx.concurrent.Task<ResultSet> noteDB = new javafx.concurrent.Task<>() {
            @Override
            public ResultSet call() {
                //Catch the tables' row of the search result, given the users credentials
                ResultSet noteRow = databaseHandler.getNotes(loggedInUser);
                return noteRow;
            }
        };

        //run database handler tasks
        new Thread(taskDB).start();
        new Thread(noteDB).start();



        taskDB.setOnSucceeded(e -> {
            ResultSet taskRow = taskDB.getValue();
            try {
                while (taskRow.next()) {
                    int taskid = taskRow.getInt("taskid");
                    String name = taskRow.getString("title");
                    String content = taskRow.getString("content");
                    String prio = taskRow.getString("prio");
                    String color = taskRow.getString("color");
                    java.sql.Date due = taskRow.getDate("dueDate");
                    java.sql.Date creation = taskRow.getDate("creationDate");
                    int state = taskRow.getInt("state");

                    // Change Task to Entry to avoid casting?
                    Task task = (Task) EntryFactory.createEntry(Entry.EntryTypes.TASK, taskid, name, content, prio, color, due, creation, state);
                    log.info("Task created."); // Replace print by logger.

                    usersTasks.add(task);
                    overviewSpinner.setVisible(false);
                }
            } catch (SQLException ex) {
                Alert connectionalert = new Alert(Alert.AlertType.ERROR, "Connection failed", ButtonType.OK);
                connectionalert.showAndWait();

            }
        });

        noteDB.setOnSucceeded(e -> {

            ResultSet noteRow = noteDB.getValue();
            try {
                while (noteRow.next()) {
                    int noteid = noteRow.getInt("taskid");
                    String title = noteRow.getString("title");
                    String content = noteRow.getString("content");
                    java.sql.Date creationDate = noteRow.getDate("creationDate");
                    int state = noteRow.getInt("state");

                    Note note = new Note(noteid, title, content, creationDate, state);
                    log.info("Note created");

                    usersNotes.add(note);
                    overviewSpinner.setVisible(false);
                }

            } catch (SQLException ex) {
                Alert connectionalert = new Alert(Alert.AlertType.ERROR, "Connection failed", ButtonType.OK);
                connectionalert.showAndWait();
            }
        });
    }


    @FXML
    void reload(ActionEvent event) {
        for (int i = 0; i < usersTasks.size(); i++) {
            System.out.println(usersTasks.get(i));
        }
        //usersTasks.clear();
        //setUser(loggedInUser);
    }

    @Override
    public void run() {
        try {
            while (true) {

                //Setting date format and variables:

                calendar = Calendar.getInstance();

                format = new SimpleDateFormat("HH:mm:ss");
                date = calendar.getTime();
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

    public void setUsersTasks(ArrayList usersTasks) {
        this.usersTasks = usersTasks;
    }

    public void setOwnController(Overview controller) {
        this.ownController = controller;
    }
}
