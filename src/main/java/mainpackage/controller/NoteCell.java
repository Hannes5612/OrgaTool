package mainpackage.controller;

import com.jfoenix.controls.JFXListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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

        Tooltip.install(noteCellDeleteButton, new Tooltip("Delete note"));
        Tooltip.install(noteCellEditButton, new Tooltip("Edit note"));
        Tooltip.install(noteCellArchiveButton, new Tooltip("Archive note"));

        noteCellDeleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            final int selectedIdx = listViewProperty().get().getSelectionModel().getSelectedIndex();
            final Note note = listViewProperty().get().getSelectionModel().getSelectedItem();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + note.getTitle() + "?", ButtonType.YES, ButtonType.CANCEL);
            alert.setTitle("DELETING NOTE");
            alert.setHeaderText("You are about to delete a note!");
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                if (selectedIdx != -1) {
                    Note itemToRemove = listViewProperty().get().getSelectionModel().getSelectedItem();
                    System.out.println("Note at index " + selectedIdx + " selected.");

                    DatabaseHandler databaseHandler = new DatabaseHandler();
                    try {
                        databaseHandler.deleteNote(note);
                        ListManager.deleteNote(itemToRemove.getId());
                    } catch (SQLException throwables) {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Database connection failed \n Please check your connection or try again.");
                        error.showAndWait();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    // debugLogger.info("Removed from Listview " + itemToRemove.getTitle());
                    listViewProperty().get().getItems().remove(selectedIdx);
                }

            }
        });


        noteCellEditButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            final int selectedIdx = listViewProperty().get().getSelectionModel().getSelectedIndex();
            final Note note = listViewProperty().get().getSelectionModel().getSelectedItem();
            System.out.println("Note at index " + selectedIdx + " selected.");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EditNotes.fxml"));
            loader.setController(new EditNote(note, selectedIdx));

            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initStyle(StageStyle.TRANSPARENT);
            noteCellEditButton.setDisable(true);
            stage.showAndWait();
            listViewProperty().get().getItems().set(selectedIdx, EditNote.getEditedNote());
            noteCellEditButton.setDisable(false);

        });

        noteCellArchiveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            final int selectedIdx = listViewProperty().get().getSelectionModel().getSelectedIndex();
            final Note note = listViewProperty().get().getSelectionModel().getSelectedItem();
            int noteId = note.getId();

            if (note.getState() == 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Archive " + note.getTitle() + "?", ButtonType.YES, ButtonType.CANCEL);
                alert.setTitle("ARCHIVING NOTE");
                alert.setHeaderText("You are about to archive a note!");
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {
                    if (selectedIdx != -1) {
                        System.out.println("Note at index " + selectedIdx + " selected.");
                        DatabaseHandler databaseHandler = new DatabaseHandler();
                        try {
                            databaseHandler.archiveNote(noteId, note);
                        } catch (SQLException throwables) {
                            Alert error = new Alert(Alert.AlertType.ERROR, "Database connection failed \n Please check your connection or try again.");
                            error.showAndWait();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        //debugLogger.info("Removed from Listview " + itemToRemove.getTaskName());
                        listViewProperty().get().getItems().remove(selectedIdx);
                    }
                }
            } else if (note.getState() == 2) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Reactivate " + note.getTitle() + "?", ButtonType.YES, ButtonType.CANCEL);
                alert.setTitle("REACTIVATING NOTE");
                alert.setHeaderText("You are about to reactivate a note!");
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {
                    if (selectedIdx != -1) {
                        System.out.println("Note at index " + selectedIdx + " selected.");
                        DatabaseHandler databaseHandler = new DatabaseHandler();
                        try {
                            databaseHandler.reactivateNote(noteId, note);
                        } catch (SQLException throwables) {
                            Alert error = new Alert(Alert.AlertType.ERROR, "Database connection failed \n Please check your connection or try again.");
                            error.showAndWait();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        //debugLogger.info("Removed from Listview " + itemToRemove.getTaskName());
                        listViewProperty().get().getItems().remove(selectedIdx);
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
