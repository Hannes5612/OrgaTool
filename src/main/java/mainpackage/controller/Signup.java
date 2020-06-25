package mainpackage.controller;

import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Ellipse;
import mainpackage.animation.Shake;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.User;

public class Signup {

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXTextField signupUsername;
    @FXML
    private JFXPasswordField signupPassword;
    @FXML
    private JFXButton signupSignupButton;
    @FXML
    private Ellipse loginBlueX;
    @FXML
    private Ellipse loginBlueY;
    @FXML
    private Ellipse loginBlueXY;
    @FXML
    private JFXSpinner signupSpinner;
    @FXML
    private Label signupMessage;
    @FXML
    private JFXButton signupGobackButton;

    private DatabaseHandler databaseHandler = new DatabaseHandler();


    /**
     * Signup handler to create an account
     */
    @FXML
    void initialize() {
        //Hide error messages
        signupMessage.setVisible(false);
        //Hide login loading spinner
        signupSpinner.setVisible(false);

        signupGobackButton.setOnAction(event -> {
            System.out.println("Back clicked, showing Login screen");
            goback();
        });

        signupPassword.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                signup();
            }
        });


        signupSignupButton.setOnAction(e -> signup());
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

        rootPane.getChildren().setAll(login);
        new FadeIn(login).play();
    }

    private void spin() {
        signupUsername.setVisible(false);
        signupPassword.setVisible(false);
        signupSpinner.setVisible(true);
    }

    private void noSpin() {
        signupUsername.setVisible(true);
        signupPassword.setVisible(true);
        signupSpinner.setVisible(false);
    }

    private void signup() {
        System.out.println("Signup clicked, creating user.");
        //hide error messages after retry
        signupMessage.setVisible(false);
        signupMessage.setStyle("-fx-text-fill: red");

        System.out.println("Login clicked, checking credentials!");

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

            //new task for extra thread, databasehandler to fetch user
            Task<Integer> task = new Task<>() {
                @Override
                public Integer call() {

                    //Catch the tables' row of the search result, given the users credentials
                    try {
                        databaseHandler.signupUser(signupUser);
                    } catch (SQLIntegrityConstraintViolationException integrity) {
                        noSpin();
                        this.done();
                        return 0;
                    } catch (SQLException sqlException) {
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
                System.out.println(task.getValue());
                signupMessage.setText("Account created, go back");

                if (task.getValue() == 0) {
                    userNameShaker.shake();
                    signupPassword.clear();
                }
                if (task.getValue() == 1) {
                    Alert connectionalert = new Alert(Alert.AlertType.ERROR, "Connection failed!", ButtonType.OK);
                    connectionalert.showAndWait();
                }
                if (task.getValue() == 2) {
                    noSpin();
                    signupMessage.setStyle("-fx-text-fill: green");
                    signupMessage.setVisible(true);
                    signupMessage.setText("User created, go back now");

                }
            });


        }
    }

    /*
     Creates an user in the database with given credentials via databasehandler


    private void createUser(){

     DatabaseHandler databaseHandler = new DatabaseHandler();

     String username = signupUsername.getText().trim();
     String password = signupPassword.getText().trim();

     User signupUser = new User(username,password);



     databaseHandler.signupUser(signupUser);

     goback();
     }*/
}
