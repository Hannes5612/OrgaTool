package mainpackage.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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

        newNoteEditButton.setOnAction(e -> {
            System.out.println("'Save' button pressed");
            if (newNoteTitle.getText() == null || newNoteTitle.getText().trim().isEmpty()) {
                CreateNote.missingTitleAlert();
            } else {
                if (selectedIdx != -1) {

                    String title = newNoteTitle.getText().trim();
                    String content = newNoteContent.getText().trim();

                    editedNote = new Note(title, content);
                    DatabaseHandler databaseHandler = new DatabaseHandler();

                    try {
                        databaseHandler.editNote(noteId, editedNote);
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

    @FXML
    void close(ActionEvent event) {
        newNoteTitle.getScene().getWindow().hide();
    }


    public static Note getEditedNote(){
        return editedNote;
    }

    private static Note editedNote;

}

