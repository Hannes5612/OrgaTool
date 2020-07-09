package mainpackage.controller;

import com.jfoenix.controls.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Ellipse;
import javafx.stage.Stage;
import mainpackage.animation.FadeIn;
import mainpackage.animation.Shake;
import mainpackage.database.DatabaseHandler;
import mainpackage.ListManager;
import mainpackage.model.User;

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
    @FXML
    private JFXSpinner loginSpinner;
    @FXML
    private Label loginMessage;
    @FXML
    private Ellipse loginBlueX;
    @FXML
    private Ellipse loginBlueY;
    @FXML
    private Ellipse loginBlueXY;


    /**
     * Handle button presses.
     * Signup button press calls signup() method.
     * Login button press creates a databaseHandler and checks the user input for registered users. After a successful
     * chek the login() method gets called with the fetched user.
     */
    @FXML
    void initialize() {

        //Hide error messages
        loginMessage.setVisible(false);
        //Hide login loading spinner
        loginSpinner.setVisible(false);

        //Trigger for sign up button
        loginSignupButton.setOnAction(event -> signup());

        //start login() on enter pressed in password field
        loginPassword.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                login();
            }
        });

        //Trigger for login button
        loginLoginButton.setOnAction(event -> login());
    }


    /**
     * Loads signup.xfml
     */

    private void signup() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setTitle("Signup");

        System.out.println("Sign up clicked!");

        AnchorPane signup = null;
        try {
            signup = FXMLLoader.load(getClass().getResource("/view/Signup.fxml"));  //Load signup page
        } catch (IOException e) {
            e.printStackTrace();
        }
        rootPane.getChildren().setAll(signup);                                            //Show Signup page
        new FadeIn(signup).play();

    }

    /**
     * Closes current window after successful user check from database.
     * Opens the overview and passes the logged in user.
     */

    private void overview() {


        AnchorPane login = null;
        try {
            login = FXMLLoader.load(getClass().getResource("/view/Overview.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        rootPane.getChildren().setAll(login);
        new FadeIn(login).play();

/*
        loginLoginButton.getScene().getWindow().close();                                   //Hide login screen
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/Overview.fxml"));

        try {
            loader.load();
        } catch (IOException e) {                                                         //Load overview screen
            e.printStackTrace();
        }

        Overview controller = loader.getController();
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Overview");
        stage.setResizable(false);
        stage.showAndWait();*/

    }

    private void login() {
        //hide error messages after retry
        loginMessage.setVisible(false);
        System.out.println("Login clicked, checking credentials!");

        //get user input
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText().trim();

        //create shake animation object
        Shake userNameShaker = new Shake(loginUsername);
        Shake passwordShaker = new Shake(loginPassword);

        //check for empty fields, if empty shake and display error
        if (username.equals("") || password.equals("")) {
            passwordShaker.shake();
            userNameShaker.shake();

            loginMessage.setVisible(true);
            loginMessage.setText("Enter something!");

            //if not empty create an user, show loading spinner and
        } else {

            User loginUser = new User(username, password);
            spin();

            //new task for extra thread, databasehandler to fetch user
            Task<ResultSet> task = new Task<>() {
                @Override
                public ResultSet call() {

                    final DatabaseHandler databaseHandler = new DatabaseHandler();
                    //Catch the tables' row of the search result, given the users credentials

                    return databaseHandler.getUser(loginUser);
                }
            };


            //if task succeeded take resultset and check wether it has values
            task.setOnSucceeded(e -> {
                ResultSet result = task.getValue();
                String resUName = "";
                String resPwd = "";
                int counter = 0;
                try {
                    while (result.next()) {
                        counter++;
                        loginUser.setUserid(result.getInt("userid"));
                        resUName = result.getString("username");
                        resPwd = result.getString("password");
                    }

                    //if a row exists, fetch userid and pass the user to the overview method to load nest scene
                    if (counter == 1 && loginUser.getUserName().equals(resUName) && loginUser.getPassword().equals(resPwd)) {
                        System.out.println("Login successful!");
                        ListManager.setUser(loginUser);

                        Task<Void> update = new Task<>() {
                            @Override
                            public Void call() {


                                try {
                                    new ListManager().update();
                                } catch (SQLException | ClassNotFoundException throwables) {
                                    throwables.printStackTrace();
                                }
                                return null;
                            }
                        };

                        update.setOnSucceeded(succ -> overview());

                        new Thread(update).start();


                        //if no row exists stop spinner, shake again and display error message
                    } else {
                        noSpin();
                        passwordShaker.shake();
                        userNameShaker.shake();
                        loginPassword.clear();

                        loginMessage.setVisible(true);
                        loginMessage.setText("Wrong username/password !");
                    }

                    //catch a SQLException in any case
                } catch (SQLException sqlException) {
                    noSpin();

                    Alert error = new Alert(Alert.AlertType.ERROR,"Database connection failed \n Please check your connection or try again.",ButtonType.OK);
                    error.showAndWait();
                }
            });



            //if task failed display connection error message
            task.setOnFailed(e -> {
                noSpin();
                Alert connectionalert = new Alert(Alert.AlertType.ERROR, "Connection failed!", ButtonType.OK);
                connectionalert.showAndWait();
            });

            //run database handler task
            Thread runTask = new Thread(task);
            runTask.start();


        }


    }

    private void spin() {
        loginUsername.setVisible(false);
        loginPassword.setVisible(false);
        loginSpinner.setVisible(true);
    }

    private void noSpin() {
        loginUsername.setVisible(true);
        loginPassword.setVisible(true);
        loginSpinner.setVisible(false);
    }

}