package mainpackage.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mainpackage.ListManager;
import mainpackage.Main;
import mainpackage.animation.FadeIn;
import mainpackage.exceptions.UnsupportedStateType;
import mainpackage.model.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Calendarial view of the created tasks
 */
public class Calendar {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private GridPane calendarGrid;
    @FXML
    private JFXComboBox<String> monthCombo;
    @FXML
    private JFXComboBox<String> yearCombo;
    @FXML
    private JFXButton calendarBackButton;
    @FXML
    private HBox hPane;

    final Logger logger = LogManager.getLogger(Main.class.getName());


    private final ListManager listManager = new ListManager();

    //checks for an already opened detailedView
    private boolean isListOpen = false;

    //Executors for fetching the parallelStream synchronized
    private ExecutorService exec = Executors.newCachedThreadPool();

    // YearCombo
    private final int currentYear = Year.now().getValue();
    private final ObservableList<String> years = FXCollections.observableArrayList();

    // creates year list for year List View
    public ObservableList<String> yearList() {
        years.clear();
        for (int x = Year.now().getValue() + 10; x >= 1981; x--) {
            years.add(String.valueOf(x));
        }
        return (years);
    }

    // MonthCombo
    private final String currentMonth = getCurrentMonth();
    private final ObservableList<String> months = FXCollections.observableArrayList("January",
            "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December");

    /**
     * returns the current month
     */
    public String getCurrentMonth() {
        String[] monthName = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        java.util.Calendar cal = java.util.Calendar.getInstance();
        return monthName[cal.get(java.util.Calendar.MONTH)];
    }


    /**
     * Draws grid for the calendar view. Goes through every grid location of the
     * 7x5 GrindPane and inserts a VBox.
     */
    @FXML
    private void drawCalendarGrid() {
        // Go through each calendar grid location, or each "day" (7x5)
        int rows = 6;
        int cols = 7;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Add VBox and style it
                VBox vPane = new VBox();
                vPane.setMinWidth(50);
                vPane.setSpacing(5);
                vPane.setPadding(new Insets(0, 0, 0, 0));
                GridPane.setVgrow(vPane, Priority.ALWAYS);

                //Add a rightclick menu
                ContextMenu contextMenu = new ContextMenu();
                MenuItem edit = new MenuItem("New Task for this day");
                edit.setOnAction(click -> loadAddTask(vPane.getChildren().get(0)));

                // Add MenuItem to ContextMenu
                contextMenu.getItems().addAll(edit);

                //Eventhandler to click a day and get its tasks, prevent right klick to be detected
                vPane.setOnMouseClicked(e -> {
                    if (isListOpen && e.getButton() == MouseButton.PRIMARY) {
                        Alert openAlert = new Alert(Alert.AlertType.INFORMATION, "Close other TaskList window first to update calendar", ButtonType.OK);
                        openAlert.showAndWait();
                    } else {
                        if (vPane.getChildren().toArray().length != 0 && e.getButton() == MouseButton.PRIMARY)
                            showClickedDayTasks(vPane.getChildren().get(0));
                    }

                });


                //Add EventListener
                vPane.setOnContextMenuRequested(event -> contextMenu.show(vPane, event.getScreenX(), event.getScreenY()));
                // Add it to the grid
                calendarGrid.add(vPane, j, i);
            }
        }

        logger.debug("Calendar grid created.");
    }

    /**
     * Method for the right click context menu.
     * Opens a 'create Task' window to create a task a a specified day.
     *
     * @param node containing the days number
     */
    private void loadAddTask(Node node) {

        //Get day, month and year of the selected day pane.
        Label label = (Label) node;
        int day = Integer.parseInt(label.getText());
        NumberFormat formatter1 = new DecimalFormat("00");
        String inputString = formatter1.format(day) + "-" + formatter1.format(monthCombo.getSelectionModel().getSelectedIndex() + 1)
                + "-" + formatter1.format(2030 - yearCombo.getSelectionModel().getSelectedIndex());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate inputDate = LocalDate.parse(inputString, formatter);

        //Create new window, set Controller and show
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/CreateTask.fxml"));
        loader.setController(new CreateTask(inputDate));
        try {
            loader.load();
        } catch (IOException e) {
            logger.error("IOException: " + e);
        }
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initStyle(StageStyle.TRANSPARENT);
        logger.debug("Window to add a new task was opened");
        stage.showAndWait();

        logger.debug("Window to add a new task was closed");

        //Update the calendar view after inserting a task
        loadSelectedMonth();
        fillCalendarWithTasks();
    }


    /**
     * Checks for every task whether day, month and year fit to the currently selected month..
     * If a match was found, showDate gets called.
     */
    public void fillCalendarWithTasks() {

        //Runnable to get synchronized stream of tasks
        javafx.concurrent.Task<List<Task>> getTasksTask = new javafx.concurrent.Task<>() {
            @Override
            public List<Task> call() {
                return listManager.getTaskList()
                        .filter(n -> {
                            try {
                                return n.getState() == 0 || n.getState() == 1;
                            } catch (UnsupportedStateType unsupportedStateType) {
                                unsupportedStateType.printStackTrace();
                                logger.error("Unsupported state type!");
                            }
                            return false;
                        })
                        .collect(Collectors.toList());
            }
        };

        //on finish execute the check for each task whether month and year fit to the currently selected ones
        getTasksTask.setOnSucceeded(e -> {
            getTasksTask.getValue().forEach(task -> {
                String year = String.valueOf(task.getDueDate().toLocalDate().getYear());
                String month = task.getDueMonth();
                String yearSelection = yearCombo.getSelectionModel().getSelectedItem();
                String monthSelection = monthCombo.getSelectionModel().getSelectedItem();
                if (year.equals(yearSelection) && month.equals(monthSelection)) {
                    int day = task.getDueDate().toLocalDate().getDayOfMonth();
                    showDate(day, task);
                }
            });

            logger.debug("The calendar was filled with tasks");
        });

        //run javafx.concurrent.Task
        exec.submit(getTasksTask);
    }


    /**
     * Generates the day number labels. Iterates through the grid and determines whether a pane
     * is a day the selected month or if it should be empty.
     * Also sets colors and eventhandler.
     */
    private void loadSelectedMonth() {
        //Get current dates, determine where to begin drawing
        int year = Integer.parseInt(yearCombo.getSelectionModel().getSelectedItem());
        int month = monthCombo.getSelectionModel().getSelectedIndex();
        //create helper calendar
        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        //get the monts starting day (Mo-Sun) as int
        int firstDay = gc.get(java.util.Calendar.DAY_OF_WEEK);
        //get day amount of month
        int daysInMonth = gc.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
        //get offset for the grids start
        int offset = firstDay - 1;
        int gridCount = 1;
        int lblCount = 1;

        //style variables
        final String style = "-fx-background-color: #C5CAE9";
        final String hoveredStyle = "-fx-background-color: #95a1d2";
        final String noDay = "-fx-background-color: transparent";

        // Go through calendar grid
        for (Node node : calendarGrid.getChildren()) {
            VBox day = (VBox) node;
            //delete every child of the vbox and set the background to white
            day.getChildren().clear();
            day.setStyle("-fx-backgroud-color: white");
            // Start placing labels on the first day for the month
            if (gridCount < offset) {

                gridCount++;
                //"disable"  style and hover properties
                day.setStyle(noDay);
                day.setOnMouseEntered(e -> day.setStyle(noDay));
                day.setOnMouseExited(e -> day.setStyle(noDay));
            } else {
                // Don't place a label if we've reached maximum label for the month
                if (lblCount > daysInMonth) {
                    // Instead, again disable the styles and hover properties
                    day.setStyle(noDay);
                    day.setOnMouseEntered(e -> day.setStyle(noDay));
                    day.setOnMouseExited(e -> day.setStyle(noDay));

                    //we found a match, lets create the day labels and set the style properties
                } else {
                    //checking if day is the current day and set fancy style properties and finally add a day label
                    if (currentMonth.equals(monthCombo.getValue()) && currentYear == year && lblCount == LocalDate.now().getDayOfMonth()) {
                        Label dayNumber = new Label(Integer.toString(lblCount));
                        dayNumber.setPadding(new Insets(2));
                        dayNumber.setStyle("-fx-text-fill:#3F51B5");
                        day.setStyle(style + ";-fx-border-color: #3F51B5;-fx-border-width: 2px");
                        day.setOnMouseEntered(e -> {
                            day.setStyle(hoveredStyle + ";-fx-border-color: #3F51B5;-fx-border-width: 2px");
                            dayNumber.setStyle("-fx-text-fill: white");
                        });
                        day.setOnMouseExited(e -> {
                            day.setStyle(style + ";-fx-border-color: #3F51B5;-fx-border-width: 2px");
                            dayNumber.setStyle("-fx-text-fill: #3F51B5");
                        });
                        //add the day label!
                        day.getChildren().add(dayNumber);
                    } else {
                        // Make a new day label and style
                        Label dayNumber = new Label(Integer.toString(lblCount));
                        dayNumber.setPadding(new Insets(2));
                        dayNumber.setStyle("-fx-text-fill:#757575");
                        day.setStyle(style);
                        day.setOnMouseEntered(e -> {
                            day.setStyle(hoveredStyle);
                            dayNumber.setStyle("-fx-text-fill: white");
                        });
                        day.setOnMouseExited(e -> {
                            day.setStyle(style);
                            dayNumber.setStyle("-fx-text-fill: #757575");
                        });
                        //add the day label
                        day.getChildren().add(dayNumber);
                    }
                }
                // after adding a day label increment it for the next day
                lblCount++;
            }

        }


        logger.debug("The selected month layout was loaded");
    }


    /**
     * Iterates the GridPane to check for a day match and then insert a children label with
     * the tasks title.
     *
     * @param dayNumber of the day to add the tasks title as a label.
     * @param task      as the task to display on the given day.
     */
    public void showDate(int dayNumber, Task task) {
        for (Node node : calendarGrid.getChildren()) {
            // Get the current day
            VBox day = (VBox) node;
            // Don't look at any empty days (they at least must have a day label!)
            if (!day.getChildren().isEmpty()) {
                // Get the day label for that day
                Label lbl = (Label) day.getChildren().get(0);
                // Get the number
                int currentNumber = Integer.parseInt(lbl.getText());
                // Did we find a match?
                if (currentNumber == dayNumber) {
                    // Add an event label with the given description
                    Label descLbl = new Label(task.getTitle());    //(desc + time);
                    descLbl.setText("- " + task.getTitle());
                    descLbl.setPadding(new Insets(-4, 0, 0, 2));
                    // Add label to calendar, check for to many labels
                    if (day.getChildren().size() <= 2) day.getChildren().add(descLbl);
                    else if (day.getChildren().size() == 3) {
                        Label info = new Label();
                        info.setText("Click to show more");
                        info.setPadding(new Insets(-1, 0, 0, 2));
                        info.setStyle("-fx-text-fill: #3F51B5 ");
                        day.getChildren().add(info);
                        logger.debug("A Task label was added to the calendar :" + task);
                    }
                }
            }
        }
    }

    /**
     * Opens a new window with a task list of the clicked day
     *
     * @param clickedDayNode which is the day klicked
     */
    private void showClickedDayTasks(Node clickedDayNode) {

        //List for the days tasks
        ObservableList<Task> clickedTasks = FXCollections.observableArrayList();
        Label label = (Label) clickedDayNode;

        //get the clicked day as an int
        int dayClicked = Integer.parseInt(label.getText());

        //get selected month and year, equivalent to the tasks month and year
        String yearSelection = yearCombo.getSelectionModel().getSelectedItem();
        String monthSelection = monthCombo.getSelectionModel().getSelectedItem();

        //fetch current tasks synchronized
        javafx.concurrent.Task<List<Task>> getTasksTask = new javafx.concurrent.Task<>() {
            @Override
            public List<Task> call() {
                return listManager.getTaskList()
                        .filter(n -> {
                            try {
                                return n.getState() == 0 || n.getState() == 1;
                            } catch (UnsupportedStateType unsupportedStateType) {
                                unsupportedStateType.printStackTrace();
                                logger.error("Unsupported state type!");
                            }
                            return false;
                        })
                        .collect(Collectors.toList());
            }
        };

        //on runnable success check for matching days and add the assigned tasks to a new list
        getTasksTask.setOnSucceeded(e -> {
                    getTasksTask.getValue().forEach(task -> {
                        String year = String.valueOf(task.getDueDate().toLocalDate().getYear());
                        String month = task.getDueMonth();
                        int day = task.getDueDate().toLocalDate().getDayOfMonth();
                        if (year.equals(yearSelection) && month.equals(monthSelection) && dayClicked == day) {
                            clickedTasks.add(task);
                        }
                    });
                    //if the created list is not empty, show the new window
                    if (!clickedTasks.isEmpty()) {
                        isListOpen = true;
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/view/TaskViewCalendar.fxml"));

                        try {
                            loader.load();
                        } catch (IOException ex) {
                            logger.error("IOException: " + ex);
                        }

                        TaskViewCalendar controller = loader.getController();
                        controller.setTasks(clickedTasks);
                        Parent root = loader.getRoot();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.getIcons().add(new Image("icon/Logo organizingTool 75x75 blue.png"));
                        stage.setTitle("Tasks for " + dayClicked + " of " + monthSelection + " " + yearSelection);
                        stage.setResizable(true);
                        stage.setMinWidth(440);
                        logger.debug("A window with tasks for day" + dayClicked + "." + monthSelection + " was opened.");
                        stage.showAndWait();

                        //after closing the window, reload the month, in case the user has deleted or changed a task
                        loadSelectedMonth();
                        fillCalendarWithTasks();

                        isListOpen = false;
                    }
                }
        );

        //run the Task
        exec.submit(getTasksTask);
    }

    /**
     * Helper method to reload the month when a user selects a new month or year
     *
     * @param event changing dropdownList entiry
     */
    @FXML
    public void updateView(ActionEvent event) {
        loadSelectedMonth();
        fillCalendarWithTasks();
        logger.debug("New Month was completely loaded.");
    }

    /**
     * Loads the Overview Stage and shows it
     *
     * @param e Button click
     */
    private void backToOverview(ActionEvent e) {
        logger.debug("Sending back to overview pane");
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setTitle("Overview");

        AnchorPane overview = null;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/Overview.fxml"));
        try {
            overview = loader.load();
        } catch (IOException ex) {
            logger.error("IOException: " + ex);
        }

        rootPane.getChildren().setAll(overview);
        new FadeIn(overview).play();
    }

    @FXML
    void initialize() {

        // create executor that uses daemon threads:
        exec = Executors.newCachedThreadPool(runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            return t;
        });

        drawCalendarGrid();
        yearCombo.setItems(yearList());
        yearCombo.setValue(String.valueOf(currentYear));
        monthCombo.setItems(months);
        monthCombo.setValue(currentMonth);
        loadSelectedMonth();
        fillCalendarWithTasks();

        // Due to a javafx bug, this is neccessary to display the combobox list, otherwise it is "confused"
        // whether the list is already showing or not
        hPane.requestFocus();

        calendarBackButton.setOnAction(this::backToOverview);

    }
}
