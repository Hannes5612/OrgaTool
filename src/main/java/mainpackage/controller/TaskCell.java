package mainpackage.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListCell;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mainpackage.ListManager;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.Note;
import mainpackage.model.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TaskCell extends JFXListCell<Task> {

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
        private JFXCheckBox cellCheckbox;
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

        private FXMLLoader fxmlLoader;

        private static final Logger logger = LogManager.getLogger(TaskCell.class);

        @FXML
        void initialize() {

                Tooltip.install(cellDeleteButton, new Tooltip("Delete task"));
                Tooltip.install(cellDeleteButton, new Tooltip("Edit task"));
                Tooltip.install(cellDeleteButton, new Tooltip("Archive task"));

                cellDeleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

                        final int selectedIdx = listViewProperty().get().getSelectionModel().getSelectedIndex();
                        final Task task = listViewProperty().get().getSelectionModel().getSelectedItem();

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + task.getTitle() + " ?", ButtonType.YES,  ButtonType.CANCEL);
                        alert.setTitle("Confirmation");
                        alert.setHeaderText("You are about to delete a task!");
                        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                        stage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
                        alert.showAndWait();

                        if (alert.getResult() == ButtonType.YES) {
                                if (selectedIdx != -1) {
                                        Task itemToRemove = listViewProperty().get().getSelectionModel().getSelectedItem();
                                        ListManager.deleteTask(itemToRemove.getId());
                                        //debugLogger.info(selectedIdx);

                                        DatabaseHandler databaseHandler = new DatabaseHandler();
                                        try {
                                                databaseHandler.deleteTask(task);
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

                cellEditButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

                        final int selectedIdx = listViewProperty().get().getSelectionModel().getSelectedIndex();
                        final Task task = listViewProperty().get().getSelectionModel().getSelectedItem();
                        System.out.println("Task at index " + selectedIdx + " selected.");

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EditTasks.fxml"));
                        loader.setController(new EditTask(task, selectedIdx));

                        try {
                                loader.load();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }

                        Parent root = loader.getRoot();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setResizable(false);
                        stage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
                        getListView().setDisable(true);
                        stage.showAndWait();
                        if(EditTask.getEditedTask() != null) { listViewProperty().get().getItems().set(selectedIdx, EditTask.getEditedTask()); }
                        getListView().setDisable(false);
                });

                // Archiving task when state = active/finished or reactivating task when state = archived
                cellArchiveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {

                        final int selectedIdx = listViewProperty().get().getSelectionModel().getSelectedIndex();
                        final Task task = listViewProperty().get().getSelectionModel().getSelectedItem();
                        int taskId = task.getId();
                        logger.debug("Task at index " + selectedIdx + " selected.");

                        if (task.getState() != 2) { // state = active/finished --> note will be archived
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
                        } else if (task.getState() == 2) { // state = archived --> note will be active
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

                cellCheckbox.setOnMouseClicked(event -> {

                        final Task task = listViewProperty().get().getSelectionModel().getSelectedItem();

                        if(cellCheckbox.isSelected()) { task.finish(); }
                        else { task.reactivate(); }
                        synchronizeDatabase(task);

                });

        }

        /**
         * New thread to synchronize the database with the updated task.
         */
        private void synchronizeDatabase(Task task) {

                DatabaseHandler databaseHandler = new DatabaseHandler();

                javafx.concurrent.Task<Void> taskThread = new javafx.concurrent.Task<>() {
                        @Override
                        public Void call() {
                                try {
                                        databaseHandler.editTask(task.getId(), task);
                                } catch (ClassNotFoundException | SQLException e) {
                                        e.printStackTrace();
                                }
                                return null;
                        }
                };

                taskThread.setOnFailed(e -> {
                        Alert error = new Alert(Alert.AlertType.ERROR, "Thread to synchronize the database with the updated task failed!");
                        error.showAndWait();
                        logger.error("Thread to synchronize the database with the updated task failed! " + e);
                });

                new Thread(taskThread).start();

        }

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
                                }
                        }

                        String color = task.getColor().substring(2,8);
                        cellColor.setStyle("-fx-background-color: #"+ color);
                        cellTaskLabel.setText(task.getTitle());
                        cellTaskDescription.setText(task.getContent());
                        cellPrioLabel.setText(task.getPriority());
                        cellDateLabel.setText(String.valueOf(task.getDueDate()));
                        cellCreatedLabel.setText(String.valueOf(task.getCreationDate()));
                        cellTaskDescription.setWrapText(true);
                        cellCheckbox.setSelected(task.getState() == 1);

                        setText(null);
                        setGraphic(rootAnchorPane);

                }
        }

}


