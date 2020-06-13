package mainpackage.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import mainpackage.model.User;

public class Overview {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane rootPane;

    private User loggedInUser;

    @FXML
    void sayHello(){
        System.out.println(loggedInUser);
    }

    @FXML
    void initialize() {


    }
    void setUser(User user){
        this.loggedInUser=user;
    }
}
