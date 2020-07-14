package mainpackage.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mainpackage.ListManager;
import mainpackage.Main;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.Note;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NoteCell extends ListCell<Note> {

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

    private static final Logger logger = LogManager.getLogger(Main.class);

    @FXML
    void initialize() {

        // Tooltips for note buttons
        Tooltip.install(noteCellDeleteButton, new Tooltip("Delete note"));
        Tooltip.install(noteCellEditButton, new Tooltip("Edit note"));
        Tooltip.install(noteCellArchiveButton, new Tooltip("Archive note"));

        // deleting note from database and ListView
        noteCellDeleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            // user selects note
            final int selectedIdx = listViewProperty().get().getSelectionModel().getSelectedIndex();
            final Note note = listViewProperty().get().getSelectionModel().getSelectedItem();
            logger.debug("Note at index " + selectedIdx + " selected.");

            // alert: delete note?
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + note.getTitle() + "?", ButtonType.YES, ButtonType.CANCEL);
            alert.setTitle("DELETING NOTE");
            alert.setHeaderText("You are about to delete a note!");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                if (selectedIdx != -1) {
                    Note itemToRemove = listViewProperty().get().getSelectionModel().getSelectedItem();

                    DatabaseHandler databaseHandler = new DatabaseHandler();
                    try {
                        databaseHandler.deleteNote(note);
                        ListManager.deleteNote(itemToRemove.getId());
                        logger.debug("Note at index " + selectedIdx + " deleted.");
                    } catch (SQLException throwables) {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Database connection failed \n Please check your connection or try again.");
                        error.showAndWait();
                        logger.error("SQLException: " + throwables);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        logger.error("ClassNotFoundException: " + e);
                    }
                    listViewProperty().get().getItems().remove(selectedIdx);
                    logger.info("Removed from ListView: Note '" + itemToRemove.getTitle() + "'");
                }
            }
        });

        // opening new window to edit note (with title + content of old note)
        // and updating note in ListView when note != null
        noteCellEditButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            final int selectedIdx = listViewProperty().get().getSelectionModel().getSelectedIndex();
            final Note note = listViewProperty().get().getSelectionModel().getSelectedItem();
            logger.debug("Note at index " + selectedIdx + " selected.");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EditNotes.fxml"));
            loader.setController(new EditNote(note, selectedIdx));

            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("IOException: " + e);
            }

            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
            getListView().setDisable(true);
            logger.info("Opened window to edit note " + note.getId() + ": '" + note.getTitle() + "'.");
            stage.showAndWait();
            if (EditNote.getEditedNote() != null)
                listViewProperty().get().getItems().set(selectedIdx, EditNote.getEditedNote());
            getListView().setDisable(false);
        });

        // Archiving note when state = active or reactivating note when state = archived.
        noteCellArchiveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            final int selectedIdx = listViewProperty().get().getSelectionModel().getSelectedIndex();
            final Note note = listViewProperty().get().getSelectionModel().getSelectedItem();
            int noteId = note.getId();
            logger.debug("Note at index " + selectedIdx + " selected.");

            if (note.getState() == 0) { // state = active --> note will be archived
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Archive " + note.getTitle() + "?", ButtonType.YES, ButtonType.CANCEL);
                alert.setTitle("ARCHIVING NOTE");
                alert.setHeaderText("You are about to archive a note!");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {
                    if (selectedIdx != -1) {
                        DatabaseHandler databaseHandler = new DatabaseHandler();
                        try {
                            databaseHandler.editNote(noteId, note, 2);
                            note.archive();
                            logger.debug("Note at index " + selectedIdx + " archived.");
                        } catch (SQLException throwables) {
                            Alert error = new Alert(Alert.AlertType.ERROR, "Database connection failed \n Please check your connection or try again.");
                            error.showAndWait();
                            logger.error("SQLException: " + throwables);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            logger.error("ClassNotFoundException: " + e);
                        }
                        listViewProperty().get().getItems().remove(selectedIdx);
                        logger.info("Removed from current ListView of active notes: Note '" + note.getTitle() + "'");
                    }
                }
            } else if (note.getState() == 2) { // state = archived --> note will be active
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Reactivate " + note.getTitle() + "?", ButtonType.YES, ButtonType.CANCEL);
                alert.setTitle("REACTIVATING NOTE");
                alert.setHeaderText("You are about to reactivate a note!");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {
                    if (selectedIdx != -1) {
                        DatabaseHandler databaseHandler = new DatabaseHandler();
                        try {
                            databaseHandler.editNote(noteId, note, 0);
                            note.reactivate();
                            logger.debug("Note at index " + selectedIdx + " reactivated.");
                        } catch (SQLException throwables) {
                            Alert error = new Alert(Alert.AlertType.ERROR, "Database connection failed \n Please check your connection or try again.");
                            error.showAndWait();
                            logger.error("SQLException: " + throwables);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            logger.error("ClassNotFoundException: " + e);
                        }
                        listViewProperty().get().getItems().remove(selectedIdx);
                        logger.info("Removed from current ListView of archived notes: Note '" + note.getTitle() + "'");
                    }
                }
            }
        });

    }

    @Override
    protected void updateItem(Note note, boolean empty) {
        super.updateItem(note, empty);

        this.setStyle("-fx-padding: 4px;");
        this.setStyle("-fx-background-color: white");
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
                    logger.error("IOException: " + e);
                }
            }

            cellNoteTitle.setText(note.getTitle());
            cellNoteDescription.setText(note.getContent());
            cellNoteDescription.setWrapText(true);
            cellNoteDescription.maxWidth(394);
            cellNoteDescription.minWidth(394);
            cellNoteDate.setText(String.valueOf(note.getCreationDate()));


            setText(null);
            setGraphic(rootPane);

        }
    }

}