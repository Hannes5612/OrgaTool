package mainpackage.controller;

import com.jfoenix.controls.JFXCheckBox;
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
import mainpackage.database.DatabaseHandler;
import mainpackage.model.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TaskCell extends ListCell<Task> {

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private Label cellTaskLabel;
    @FXML
    private Label cellTaskDescription;
    @FXML
    private Label cellDateLabel;
    @FXML
    private Label cellCreatedLabel;
    @FXML
    private Label cellPrioLabel;
    @FXML
    private AnchorPane cellColor;
    @FXML
    private ImageView cellDeleteButton;
    @FXML
    private ImageView cellEditButton;
    @FXML
    private ImageView cellArchiveButton;
    @FXML
    private JFXCheckBox cellCheckbox;
    @FXML
    private Label checkboxLabel;

    private FXMLLoader fxmlLoader;

    private static final Logger logger = LogManager.getLogger(TaskCell.class);

    @FXML
    void initialize() {

        Tooltip.install(cellDeleteButton, new Tooltip("Delete task"));
        Tooltip.install(cellEditButton, new Tooltip("Edit task"));
        Tooltip.install(cellArchiveButton, new Tooltip("Archive task"));

        // deleting task from database and ListView
        cellDeleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            // user selects task
            final int selectedIdx = listViewProperty().get().getSelectionModel().getSelectedIndex();
            final Task task = listViewProperty().get().getSelectionModel().getSelectedItem();
            logger.debug("Task at index " + selectedIdx + " selected.");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + task.getTitle() + " ?", ButtonType.YES, ButtonType.CANCEL);
            alert.setTitle("DELETING TASK");
            alert.setHeaderText("You are about to delete a task!");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                if (selectedIdx != -1) {
                    Task itemToRemove = listViewProperty().get().getSelectionModel().getSelectedItem();

                    DatabaseHandler databaseHandler = new DatabaseHandler();
                    try {
                        databaseHandler.deleteTask(task);
                        ListManager.deleteTask(itemToRemove.getId());
                        logger.debug("Task at index " + selectedIdx + " deleted.");
                    } catch (SQLException throwables) {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Database connection failed \n Please check your connection or try again.");
                        error.showAndWait();
                        logger.error("SQLException: " + throwables);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        logger.error("ClassNotFoundException: " + e);
                    }
                    listViewProperty().get().getItems().remove(selectedIdx);
                    logger.info("Removed from ListView: Task '" + itemToRemove.getTitle() + "'");
                }

            }
        });

        // opening new window to edit task (with title + content of old task)
        // and updating task in ListView when task != null
        cellEditButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            final int selectedIdx = listViewProperty().get().getSelectionModel().getSelectedIndex();
            final Task task = listViewProperty().get().getSelectionModel().getSelectedItem();
            logger.debug("Task at index " + selectedIdx + " selected.");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EditTasks.fxml"));
            loader.setController(new EditTask(task, selectedIdx));

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
            logger.info("Opened window to edit task " + task.getId() + ": '" + task.getTitle() + "'.");
            stage.showAndWait();
            if (EditTask.getEditedTask() != null) {
                listViewProperty().get().getItems().set(selectedIdx, EditTask.getEditedTask());
            }
            getListView().setDisable(false);
        });

        // Archiving task when state = active/finished or reactivating task when state = archived.
        cellArchiveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

            final int selectedIdx = listViewProperty().get().getSelectionModel().getSelectedIndex();
            final Task task = listViewProperty().get().getSelectionModel().getSelectedItem();
            int taskId = task.getId();
            logger.debug("Task at index " + selectedIdx + " selected.");

            if (task.getState() != 2) { // state = active/finished --> task will be archived
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Archive " + task.getTitle() + "?", ButtonType.YES, ButtonType.CANCEL);
                alert.setTitle("ARCHIVING TASK");
                alert.setHeaderText("You are about to archive a task!");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {
                    if (selectedIdx != -1) {
                        DatabaseHandler databaseHandler = new DatabaseHandler();
                        try {
                            task.archive();
                            databaseHandler.editTask(taskId, task);
                            logger.debug("Task at index " + selectedIdx + " archived.");
                        } catch (SQLException throwables) {
                            Alert error = new Alert(Alert.AlertType.ERROR, "Database connection failed \n Please check your connection or try again.");
                            error.showAndWait();
                            logger.error("SQLException: " + throwables);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            logger.error("ClassNotFoundException: " + e);
                        }
                        listViewProperty().get().getItems().remove(selectedIdx);
                        logger.info("Removed from current ListView of active/finished tasks: Task '" + task.getTitle() + "'");
                    }
                }
            } else if (task.getState() == 2) { // state = archived --> task will be active
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Reactivate " + task.getTitle() + "?", ButtonType.YES, ButtonType.CANCEL);
                alert.setTitle("REACTIVATING TASK");
                alert.setHeaderText("You are about to reactivate a task!");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {
                    if (selectedIdx != -1) {
                        DatabaseHandler databaseHandler = new DatabaseHandler();
                        try {
                            task.reactivate();
                            databaseHandler.editTask(taskId, task);
                            logger.debug("Task at index " + selectedIdx + " reactivated.");
                        } catch (SQLException throwables) {
                            Alert error = new Alert(Alert.AlertType.ERROR, "Database connection failed \n Please check your connection or try again.");
                            error.showAndWait();
                            logger.error("SQLException: " + throwables);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            logger.error("ClassNotFoundException: " + e);
                        }
                        listViewProperty().get().getItems().remove(selectedIdx);
                        logger.info("Removed from current ListView of archived tasks: Task '" + task.getTitle() + "'");
                    }
                }
            }
        });

        checkboxLabel.setOnMouseClicked(e -> {

            final Task task = listViewProperty().get().getSelectionModel().getSelectedItem();

            if (task.getState() == 2) {
                return;
            }
            if (task.getState() == 0) {
                cellCheckbox.setSelected(true);
                task.finish();
            } else {
                cellCheckbox.setSelected(false);
                task.reactivate();
            }

            synchronizeDatabase(task);

            logger.info("Checkbox clicked of task: '" + task.getTitle() + "'");

        });

    }

    /**
     * New thread to synchronize the database with the updated task after clicking the checkbox.
     * @param task - Selected task with updated state.
     */
    private void synchronizeDatabase(Task task) {

        final int selectedIdx = listViewProperty().get().getSelectionModel().getSelectedIndex();

        DatabaseHandler databaseHandler = new DatabaseHandler();

        javafx.concurrent.Task<Void> taskThread = new javafx.concurrent.Task<>() {
            @Override
            public Void call() {
                try {
                    databaseHandler.editTask(task.getId(), task);
                    logger.debug("Task at index " + selectedIdx + " edited.");
                } catch (SQLException throwables) {
                    Alert error = new Alert(Alert.AlertType.ERROR, "Database connection failed \n Please check your connection or try again.");
                    error.showAndWait();
                    logger.error("SQLException: " + throwables);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    logger.error("ClassNotFoundException: " + e);
                }
                return null;
            }
        };

        taskThread.setOnFailed(e -> {
            Alert error = new Alert(Alert.AlertType.ERROR, "Thread to synchronize the database with the updated task failed! Task: '" + task.getTitle() + "'");
            error.showAndWait();
            logger.error("Thread to synchronize the database with the updated task failed! " + e);
        });

        new Thread(taskThread).start();

    }

    /**
     * The method is called every time the cells in the ListView were updated.
     * It adds click triggers to the cells and loads their fxml resource.
     * The cells will be reloaded with their (updated) tasks.
     *
     * @param task - task objects of the taskList
     * @param empty - boolean of the superclass
     */
    @Override
    protected void updateItem(Task task, boolean empty) {

        super.updateItem(task, empty);
        this.setStyle("-fx-padding: 4px;");
        this.setStyle("-fx-background-color: white");
        if (empty || task == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (fxmlLoader == null) {
                fxmlLoader = new FXMLLoader(getClass().getResource("/view/TaskCell.fxml"));
                fxmlLoader.setController(this);
                try {
                    fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("IOException: " + e);
                }
            }

            String color = task.getColor().substring(2, 8);
            cellColor.setStyle("-fx-background-color: #" + color);
            cellTaskLabel.setText(task.getTitle());
            cellTaskDescription.setText(task.getContent());
            cellPrioLabel.setText(task.getPriority());
            cellDateLabel.setText(String.valueOf(task.getDueDate()));
            cellCreatedLabel.setText(String.valueOf(task.getCreationDate()));
            cellTaskDescription.setWrapText(true);
            if (task.getState() == 2) {
                cellCheckbox.setVisible(false);
            } // Deactivate checkbox for archived tasks.
            else {
                cellCheckbox.setVisible(true);
                cellCheckbox.setSelected(task.getState() == 1);
            }

            setText(null);
            setGraphic(rootAnchorPane);

        }

    }

}


