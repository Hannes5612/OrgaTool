package mainpackage.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mainpackage.ListManager;
import mainpackage.Main;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.Note;
import mainpackage.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EditNote implements Initializable {

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private JFXTextField newNoteTitle;
    @FXML
    private JFXTextArea newNoteContent;
    @FXML
    private JFXButton newNoteEditButton;
    @FXML
    private JFXButton closeEditNote;

    private static final Logger logger = LogManager.getLogger(Main.class.getName());

    private User user;
    private Note note;
    private int selectedIdx;
    private static Note editedNote;

    public EditNote(Note note, int selectedIdx) {
        this.note = note;
        this.selectedIdx = selectedIdx;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resourcese) {

        editedNote = null;
        newNoteTitle.setText(note.getTitle());
        newNoteContent.setText(note.getContent());
        int noteId = note.getId();

        // closing EditNote window without saving edited note
        closeEditNote.setOnAction(e -> {
            closeEditNote.getScene().getWindow().hide();
            logger.debug("Cancelling note edit");
        });

        // closing EditNote window and saving edited note (in database and ListManager)
        // only when title is set
        newNoteEditButton.setOnAction(e -> {
            logger.debug("Saving note...");
            if (newNoteTitle.getText() == null || newNoteTitle.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please enter a title for your note.", ButtonType.OK);
                alert.setTitle("MISSING TITLE");
                alert.setHeaderText("Your note has no title yet.");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
                alert.showAndWait();
            } else {
                if (selectedIdx != -1) {
                    // getting values of edited note and storing it
                    String title = newNoteTitle.getText().trim();
                    String content = newNoteContent.getText().trim();
                    int state = note.getState();

                    editedNote = new Note(noteId, title, content);

                    DatabaseHandler databaseHandler = new DatabaseHandler();
                    try {
                        Note dbNote = editedNote;
                        databaseHandler.editNote(noteId, dbNote, state);
                        ListManager.editNote(editedNote);
                        logger.debug("Note edited: " + editedNote);
                    } catch (ClassNotFoundException classNotFoundException) {
                        classNotFoundException.printStackTrace();
                        logger.error("ClassNotFoundException: " + classNotFoundException);
                    } catch (SQLException throwables) {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Database connection failed \n Please check your connection or try again.");
                        error.showAndWait();
                        logger.error("SQLException: " + throwables);
                    }
                }
                newNoteEditButton.getScene().getWindow().hide();
            }
        });
    }

    public static Note getEditedNote() {
        return editedNote;
    }

}