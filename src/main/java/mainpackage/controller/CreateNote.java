package mainpackage.controller;

import com.jfoenix.controls.*;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.Note;
import mainpackage.model.User;

public class CreateNote {

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private JFXTextField newNoteTitle;
    @FXML
    private JFXTextArea newNoteContent;
    @FXML
    private JFXButton newNoteCreateButton;

    private User user;

    @FXML
    void initialize() {

        newNoteCreateButton.setOnAction(e ->{
            String title = newNoteTitle.getText().trim();
            String content = newNoteContent.getText().trim();

            Note createdNote = new Note(title, content);

            DatabaseHandler databaseHandler = new DatabaseHandler();
            try {
                databaseHandler.createNote(createdNote);
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            } catch (SQLException throwables) {
                Alert error = new Alert(Alert.AlertType.ERROR,"Database connection failed \n Please check your connection or try again.");
                error.showAndWait();
            }

            newNoteCreateButton.getScene().getWindow().hide();

        });

    }
    @FXML
    void close(ActionEvent event) {
        newNoteTitle.getScene().getWindow().hide();
    }

    void setUser(User user){
        this.user = user;
    }
}
