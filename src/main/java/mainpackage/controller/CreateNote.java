package mainpackage.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import mainpackage.ListManager;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.Note;
import mainpackage.model.User;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

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

        newNoteCreateButton.setOnAction(e -> {
            if (newNoteTitle.getText() == null || newNoteTitle.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please enter a title for your note.", ButtonType.OK);
                alert.setTitle("MISSING TITLE");
                alert.setHeaderText("Your note has no title yet.");
                alert.showAndWait();
            } else {
                String title = newNoteTitle.getText().trim();
                String content = newNoteContent.getText().trim();

                Note createdNote = new Note(title, content);

                DatabaseHandler databaseHandler = new DatabaseHandler();
                try {
                    databaseHandler.createNote(createdNote);
                    ListManager.incrementCountingNoteId();
                    ListManager.addNote(createdNote);
                } catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                } catch (SQLException throwables) {
                    Alert error = new Alert(Alert.AlertType.ERROR, "Database connection failed \n Please check your connection or try again.");
                    error.showAndWait();
                }

                newNoteCreateButton.getScene().getWindow().hide();
            }

        });

    }

    @FXML
    void close(ActionEvent event) {
        newNoteTitle.getScene().getWindow().hide();
    }

    void setUser(User user) {
        this.user = user;
    }
}
