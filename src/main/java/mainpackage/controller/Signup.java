package mainpackage.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import javafx.concurrent.Task;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;
import mainpackage.Main;
import mainpackage.animation.FadeIn;
import mainpackage.animation.Shake;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Signup {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXTextField signupUsername;
    @FXML
    private JFXPasswordField signupPassword;
    @FXML
    private JFXButton signupSignupButton;
    @FXML
    private JFXSpinner signupSpinner;
    @FXML
    private Label signupMessage;
    @FXML
    private JFXButton signupGobackButton;

    private final Logger logger = LogManager.getLogger(Main.class.getName());


    /**
     * Signup handler to create an account
     */
    @FXML
    void initialize() {

        //Hide error messages
        signupMessage.setVisible(false);
        //Hide login loading spinner
        signupSpinner.setVisible(false);

        signupGobackButton.setOnAction(event -> goback());

        signupPassword.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                signup();
            }
        });


        signupSignupButton.setOnAction(e -> signup());


        logger.info("SignUp page loaded");
    }


    /**
     * Loads the login screen
     */

    private void goback() {

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

    /**
     * show the loading spinner
     */
    private void spin() {
        signupUsername.setVisible(false);
        signupPassword.setVisible(false);
        signupSpinner.setVisible(true);
    }

    /**
     * hide the loading spinner
     */
    private void noSpin() {
        signupUsername.setVisible(true);
        signupPassword.setVisible(true);
        signupSpinner.setVisible(false);
    }
    /**
     * signup the user with given credentials
     */
    private void signup() {
        //hide error messages after retry
        signupMessage.setVisible(false);
        signupMessage.setStyle("-fx-text-fill: red");


        //get user input
        String username = signupUsername.getText().trim();
        String password = signupPassword.getText().trim();

        //create shake animation object
        Shake userNameShaker = new Shake(signupUsername);
        Shake passwordShaker = new Shake(signupPassword);

        //check for empty fields, if empty shake and display error
        if (username.equals("") || password.equals("")) {
            passwordShaker.shake();
            userNameShaker.shake();

            signupMessage.setVisible(true);
            signupMessage.setText("Enter something!");

            //if not empty create an user, show loading spinner and
        } else {
            User signupUser = new User(username, password);
            spin();

            //new concurrentTask for extra thread, databasehandler to fetch user
            Task<Integer> task = new Task<>() {
                @Override
                public Integer call() {

                    //Catch the tables' row of the search result, given the users credentials
                    try {
                        DatabaseHandler databaseHandler = new DatabaseHandler();
                        databaseHandler.signupUser(signupUser);
                    } catch (SQLIntegrityConstraintViolationException integrity) {
                        logger.warn("User already exists.");
                        noSpin();
                        this.done();
                        return 0;
                    } catch (SQLException | ClassNotFoundException throwable) {

                        logger.error("Database connection failed: " + throwable);
                        noSpin();
                        this.done();
                        return 1;
                    }

                    return 2;
                }

            };

            //run database handler task
            new Thread(task).start();

            //if task succeeded take resultset and check wether it has values
            task.setOnSucceeded(e -> {
                signupMessage.setText("Account created, go back");
                logger.debug("New user was created.");

                // 0 if username is already given
                if (task.getValue() == 0) {
                    userNameShaker.shake();
                    signupPassword.clear();
                    signupMessage.setStyle("-fx-text-fill: red");
                    signupMessage.setVisible(true);
                    signupMessage.setText("User already exists.");
                }
                //1 if the connection failed
                if (task.getValue() == 1) {
                    Alert connectionalert = new Alert(Alert.AlertType.ERROR, "Connection failed!", ButtonType.OK);
                    connectionalert.showAndWait();
                }
                //2 if successful
                if (task.getValue() == 2) {
                    noSpin();
                    signupMessage.setStyle("-fx-text-fill: green");
                    signupMessage.setVisible(true);
                    signupMessage.setText("User created, go back now");

                }
            });


        }
    }
}
