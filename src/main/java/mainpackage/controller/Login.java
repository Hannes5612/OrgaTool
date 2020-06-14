package mainpackage.controller;

import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.User;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Entry point of the application.
 * Handles the log in and sign up.
 */
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

    /**
     * Handle button presses.
     * Signup button press calls signup() method.
     * Login button press creates a databaseHandler and checks the user input for registered users. After a successful
     * chek the login() method gets called with the fetched user.
     */
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

            if (username.equals("") || password.equals("")) {               //Check for empty text fields
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter your credentials", ButtonType.OK);
                alert.showAndWait();
            } else {
                User loginUser = new User(username, password);              //Create an user with the given credentials
                ResultSet userRow = databaseHandler.getUser(loginUser);     //Catch the tables' row of the search result, given the users credentials
                int counter = 0;
                try {
                    while (userRow.next()) {                                //check whether userRow has values
                        counter++;
                        loginUser.setUserid(userRow.getInt("userid"));  //write userid to User
                    }
                    if (counter == 1) {
                        System.out.println("Login successful!");
                        login(loginUser);                                   //start login method
                    } else {

                        Alert usernamealert = new Alert(Alert.AlertType.ERROR, "Wrong username/password", ButtonType.OK);
                        usernamealert.showAndWait();
                    }

                } catch (SQLException e) {
                    Alert connectionalert = new Alert(Alert.AlertType.ERROR, "Connection failed", ButtonType.OK);
                    connectionalert.showAndWait();
                }
            }
        });


    }

    /**
     * Loads signup.xfml
     */

    private void signup() {
        System.out.println("Sign up clicked!");

        AnchorPane signup = null;
        try {
            signup = FXMLLoader.load(getClass().getResource("/view/Signup.fxml"));  //Load signup page
        } catch (IOException e) {
            e.printStackTrace();
        }
        rootPane.getChildren().setAll(signup);                                            //Show Signup page

    }

    /**
     * Closes current window after successful user check from database.
     * Opens the overview and passes the logged in user.
     * @param user user to create task with
     */

    private void login(User user) {


        loginLoginButton.getScene().getWindow().hide();                                   //Hide login screen
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/Overview.fxml"));

        try {
            loader.load();
        } catch (IOException e) {                                                         //Load overview screen
            e.printStackTrace();
        }

        Overview controller = loader.getController();
        controller.setUser(user);
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Overview");
        stage.setResizable(false);
        stage.showAndWait();

    }

}

