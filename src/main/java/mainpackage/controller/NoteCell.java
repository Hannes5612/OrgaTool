package mainpackage.controller;

import com.jfoenix.controls.JFXListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import mainpackage.ListManager;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.Note;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NoteCell extends JFXListCell<Note> {

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label cellNoteTitle;
    @FXML
    private Label cellNoteDescription;
    @FXML
    private Label cellNoteDate;
    @FXML
    private ImageView noteCellDeleteButton;
    @FXML
    private ImageView noteCellEditButton;
    @FXML
    private ImageView noteCellArchiveButton;

    private FXMLLoader fxmlLoader;

    @FXML
    void initialize() {
        noteCellDeleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            final int selectedIdx = listViewProperty().get().getSelectionModel().getSelectedIndex();
            final Note note = listViewProperty().get().getSelectionModel().getSelectedItem();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + note.getTitle() + "?", ButtonType.YES,  ButtonType.CANCEL);
            alert.setTitle("Confirmation");
            alert.setHeaderText("You are about to delete a note!");
            //Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            //stage.getIcons().add(new Image("/icons/appicon_small.png")); // To add an icon
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                if (selectedIdx != -1) {
                    Note itemToRemove = listViewProperty().get().getSelectionModel().getSelectedItem();
                    //debugLogger.info(selectedIdx);

                    DatabaseHandler databaseHandler = new DatabaseHandler();
                    try {
                        databaseHandler.deleteNote(note);
                        ListManager.deleteNote(itemToRemove.getId());
                    } catch (SQLException throwables) {
                        Alert error = new Alert(Alert.AlertType.ERROR,"Database connection failed \n Please check your connection or try again.");
                        error.showAndWait();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    //debugLogger.info("Removed from Listview " + itemToRemove.getTaskName());
                    listViewProperty().get().getItems().remove(selectedIdx);
                }

            }
        });

    }

    @Override
    protected void updateItem(Note note, boolean empty) {

        super.updateItem(note, empty);
        this.setStyle("-fx-padding: 0px;");
        if (empty || note == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (fxmlLoader == null) {
                fxmlLoader = new FXMLLoader(getClass().getResource("/view/NoteCells.fxml"));
                fxmlLoader.setController(this);
                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            cellNoteTitle.setText(note.getTitle());
            cellNoteDescription.setText(note.getContent());
            cellNoteDescription.setWrapText(true);
            cellNoteDate.setText(String.valueOf(note.getCreationDate()));

            setText(null);
            setGraphic(rootPane);
        }
    }
}
