package mainpackage.controller;


import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.User;

public class Signup {

    @FXML
    private ResourceBundle resources;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private URL location;

    @FXML
    private TextField signupUsername;

    @FXML
    private PasswordField signupPassword;

    @FXML
    private Button signupGobackButton;

    @FXML
    private Button signupSignupButton;

    @FXML
    void initialize() {

        signupGobackButton.setOnAction(event -> {
            System.out.println("Back clicked, showing Login screen");
            goback();
        });
        signupSignupButton.setOnAction(event -> {
            System.out.println("Signup clicked, creating user.");
            createUser();
        });

    }

    private void goback() {

        AnchorPane login = null;
        try {
            login = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        rootPane.getChildren().setAll(login);

    }


    private void createUser(){

        DatabaseHandler databaseHandler = new DatabaseHandler();

        String username = signupUsername.getText().trim();
        String password = signupPassword.getText().trim();

        User signupUser = new User(username,password);



        databaseHandler.signupUser(signupUser);

        goback();
    }
}
