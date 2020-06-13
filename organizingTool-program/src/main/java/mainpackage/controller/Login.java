package mainpackage.controller;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.User;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField loginUsername;

    @FXML
    private PasswordField loginPassword;

    @FXML
    private Button loginLoginButton;

    @FXML
    private Button loginSignupButton;


    private DatabaseHandler databaseHandler;

    @FXML
    void initialize() {

        loginSignupButton.setOnAction(event -> {
            System.out.println("Signup clicked, changing screen");
            signup();
        });


        loginLoginButton.setOnAction(event -> {

            System.out.println("Login clicked, checking credentials!");
            databaseHandler = new DatabaseHandler();

            String username = loginUsername.getText().trim();
            String password = loginPassword.getText().trim();

            User loginUser = new User(username, password);
            ResultSet userRow = databaseHandler.getUser(loginUser);
            int counter = 0;
            try {
                while (userRow.next()) {
                    counter++;
                    loginUser.setUserid(userRow.getInt("userid"));
                }
                if (counter == 1) {
                    System.out.println("Login successful!");
                    login(loginUser);
                } else System.out.println("Wrong credentials");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });


    }



    private void signup() {
        System.out.println("Sign up clicked!");

        AnchorPane signup = null;
        try {
            signup = FXMLLoader.load(getClass().getResource("/view/Signup.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        rootPane.getChildren().setAll(signup);

    }

    private void login(User user) {


        loginLoginButton.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/Overview.fxml"));

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Overview controller = loader.getController();
        controller.setUser(user);
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Overview");
        stage.showAndWait();

    }

}

