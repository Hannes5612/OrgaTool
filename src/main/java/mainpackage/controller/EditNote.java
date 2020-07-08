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
import mainpackage.database.DatabaseHandler;
import mainpackage.model.Note;
import mainpackage.model.User;

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

    private User user;
    private Note note;
    private int selectedIdx;

    public EditNote(Note note, int selectedIdx) {
        this.note = note;
        this.selectedIdx = selectedIdx;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resourcese) {

        newNoteTitle.setText(note.getTitle());
        newNoteContent.setText(note.getContent());
        int noteId = note.getId();

        closeEditNote.setOnAction(e -> {
            Stage stage = (Stage) closeEditNote.getScene().getWindow();
            stage.close();
        });

        newNoteEditButton.setOnAction(e -> {
            System.out.println("'Save' button pressed");
            if (newNoteTitle.getText() == null || newNoteTitle.getText().trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please enter a title for your note.", ButtonType.OK);
                alert.setTitle("MISSING TITLE");
                alert.setHeaderText("Your note has no title yet.");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
                alert.showAndWait();
            } else {
                if (selectedIdx != -1) {

                    String title = newNoteTitle.getText().trim();
                    String content = newNoteContent.getText().trim();
                    int state = note.getState();

                    editedNote = new Note(noteId, title, content);

                    DatabaseHandler databaseHandler = new DatabaseHandler();

                    try {
                        databaseHandler.editNote(noteId, editedNote, state);
                        ListManager.editNote(editedNote);
                        System.out.println("Note edited");
                    } catch (ClassNotFoundException classNotFoundException) {
                        classNotFoundException.printStackTrace();
                    } catch (SQLException throwables) {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Database connection failed \n Please check your connection or try again.");
                        error.showAndWait();
                    }
                }

                newNoteEditButton.getScene().getWindow().hide();
            }

        });

    }

    void setUser(User user) {
        this.user = user;
    }

    public static Note getEditedNote(){
        return editedNote;
    }

    private static Note editedNote;

}

