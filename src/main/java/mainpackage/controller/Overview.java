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

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.Task;
import mainpackage.model.User;

/**
 * Main view after log in. Shows three different views of the created tasks.
 */

public class Overview implements Runnable{

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
    private ImageView overviewCalendarImage;


    //Initializing variables

    private User loggedInUser;
    private ArrayList<Task> Tasks;
    //private ArrayList<Note> Notes;
    private Thread thread = null;
    private String time = "", month = "", day = "";
    private SimpleDateFormat format;
    private Date date;
    private Calendar calendar;

    @FXML
    void initialize() {
        overviewCalendarImage.setOnMouseClicked(mouseEvent -> {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/Calendar.fxml"));

            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Calendar");
            stage.showAndWait();

        });

        overviewAddItemImage.setOnMouseClicked(mouseEvent ->{

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/Create.fxml"));

            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Create controller = loader.getController();
            controller.setUser(loggedInUser);
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("New Task");
            stage.showAndWait();


        });

        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();

    }
    void setUser(User user){
        this.loggedInUser=user;
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
}
