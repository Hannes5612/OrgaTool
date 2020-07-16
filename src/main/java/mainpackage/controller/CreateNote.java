package mainpackage.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mainpackage.ListManager;
import mainpackage.Main;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.Note;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

/**
 * window to create a new note
 */
public class CreateNote {

    @FXML
    private JFXTextField newNoteTitle;
    @FXML
    private JFXTextArea newNoteContent;
    @FXML
    private JFXButton newNoteCreateButton;

    private static Note createdNote;

    private static final Logger logger = LogManager.getLogger(Main.class.getName());

    @FXML
    void initialize() {

        // closing CreateNote window and saving new note (in database and ListManager)
        // only when title is set
        newNoteCreateButton.setOnAction(e -> {
            logger.debug("Saving new note...");
            if (newNoteTitle.getText() == null || newNoteTitle.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please enter a title for your note.", ButtonType.OK);
                alert.setTitle("MISSING TITLE");
                alert.setHeaderText("Your note has no title yet.");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
                alert.showAndWait();
            } else {
                // getting values of new note and storing it
                String title = newNoteTitle.getText().trim();
                String content = newNoteContent.getText().trim();

                Note createdNote = new Note(ListManager.getCountingNoteId(), title, content);

                DatabaseHandler databaseHandler = new DatabaseHandler();
                try {
                    databaseHandler.createNote(createdNote);
                    ListManager.incrementCountingNoteId();
                    ListManager.addNote(createdNote);
                    logger.debug("Note created: " + createdNote);
                } catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                    logger.error("ClassNotFoundException: " + classNotFoundException);
                } catch (SQLException throwables) {
                    Alert error = new Alert(Alert.AlertType.ERROR, "Database connection failed \n Please check your connection or try again.");
                    error.showAndWait();
                    logger.error("SQLException: " + throwables);
                }
                newNoteCreateButton.getScene().getWindow().hide();
            }
        });
    }

    // closing CreateNote window without saving new note
    @FXML
    void close(ActionEvent event) {
        newNoteTitle.getScene().getWindow().hide();
    }
}
