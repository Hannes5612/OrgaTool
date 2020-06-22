package mainpackage.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;

import com.jfoenix.controls.JFXSpinner;
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
import javafx.scene.layout.*;
import javafx.stage.Stage;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.Task;
import mainpackage.model.User;

public class Calendar {

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
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
    @FXML
    private JFXSpinner calendarSpinner;


    private Calendar ownController;
    private Overview overviewController;
    private User user;
    private ArrayList<Task> usersTasks = new ArrayList<>();
    private TaskViewCalendar taskViewController;
    private boolean isListOpen = false;

    // YearCombo
    private final int currentYear = Year.now().getValue();
    private final ObservableList<String> years = FXCollections.observableArrayList();
    private final ObservableList<Task> clickedTasks = FXCollections.observableArrayList();

    // creates year list for year List View
    private ObservableList<String> yearList() {
        for (int x = Year.now().getValue(); x >= 1980; x--) {
            years.add(String.valueOf(x));
        }
        return (years);
    }

    // MonthCombo
    private final String currentMonth = getCurrentMonth();
    private final ObservableList<String> months = FXCollections.observableArrayList("January",
            "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December");

    // returns the current month
    private String getCurrentMonth() {
        String[] monthName = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        java.util.Calendar cal = java.util.Calendar.getInstance();
        return monthName[cal.get(java.util.Calendar.MONTH)];
    }

    /**
     * Draws grid for the calendar view.
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

                vPane.setOnMouseClicked(e -> {

                    if (isListOpen){

                        Alert openAlert = new Alert(Alert.AlertType.INFORMATION, "Close other TaskList window first", ButtonType.OK);
                        openAlert.showAndWait();

                    }else{
                    clickedTasks.clear();

                    showTasks(vPane.getChildren().get(0));}

                });
                // Add it to the grid
                calendarGrid.add(vPane, j, i);
            }
        }
    }

    public void setTasks(User user) {
        calendarSpinner.setVisible(true);
        usersTasks.clear();
        this.user = user;

        DatabaseHandler databaseHandler = new DatabaseHandler();

        javafx.concurrent.Task<ResultSet> task = new javafx.concurrent.Task<>() {
            @Override
            public ResultSet call() {

                //Catch the tables' row of the search result, given the users credentials
                ResultSet taskRow = databaseHandler.getTasks(user);

                return taskRow;
            }
        };

        //run database handler task
        new Thread(task).start();

        task.setOnSucceeded(e -> {        try {

            ResultSet taskRow = task.getValue();

            while (taskRow.next()) {
                int taskid = taskRow.getInt("taskid");
                String name = taskRow.getString("title");
                String content = taskRow.getString("content");
                String prio = taskRow.getString("prio");
                String color = taskRow.getString("color");
                java.sql.Date due = taskRow.getDate("dueDate");
                java.sql.Date creation = taskRow.getDate("creationDate");
                int state = taskRow.getInt("state");

                // Change Task to Entry to avoid casting?
                Task userTask = (Task) EntryFactory.createEntry(Entry.EntryTypes.TASK, taskid, name, content, prio, color, due, creation, state);


                usersTasks.add(userTask);
            }

        } catch (SQLException ex) {
            Alert connectionalert = new Alert(Alert.AlertType.ERROR, "Connection failed", ButtonType.OK);
            connectionalert.showAndWait();
        }try {
            for (Task tasks : usersTasks) {
                String year = String.valueOf(tasks.getDueDate().toLocalDate().getYear());
                int monthInt = tasks.getDueDate().toLocalDate().getMonthValue();
                String month = "";
                switch (monthInt) {
                    case 1:
                        month = "January";
                        break;
                    case 2:
                        month = "February";
                        break;
                    case 3:
                        month = "March";
                        break;
                    case 4:
                        month = "April";
                        break;
                    case 5:
                        month = "May";
                        break;
                    case 6:
                        month = "June";
                        break;
                    case 7:
                        month = "July";
                        break;
                    case 8:
                        month = "August";
                        break;
                    case 9:
                        month = "September";
                        break;
                    case 10:
                        month = "October";
                        break;
                    case 11:
                        month = "November";
                        break;
                    case 12:
                        month = "December";
                        break;
                }
                String yearSelection = yearCombo.getSelectionModel().getSelectedItem();
                String monthSelection = monthCombo.getSelectionModel().getSelectedItem();
                if (year.equals(yearSelection) && month.equals(monthSelection)) {
                    int day = tasks.getDueDate().toLocalDate().getDayOfMonth();
                    showDate(day, tasks);
                }
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

            calendarSpinner.setVisible(false);
        });

        //if task failed display connection error message
        task.setOnFailed(e -> {
            Alert connectionalert = new Alert(Alert.AlertType.ERROR, "Connection failed!", ButtonType.OK);
            connectionalert.showAndWait();
        });
/*
        DatabaseHandler databaseHandler = new DatabaseHandler();
        ResultSet taskRow = databaseHandler.getTasks(user);

        try {
            while (taskRow.next()) {
                int taskid = taskRow.getInt("taskid");
                String name = taskRow.getString("title");
                String content = taskRow.getString("content");
                String prio = taskRow.getString("prio");
                String color = taskRow.getString("color");
                java.sql.Date due = taskRow.getDate("dueDate");
                java.sql.Date creation = taskRow.getDate("creationDate");
                int state = taskRow.getInt("state");

                // Change Task to Entry to avoid casting?
                Task task = (Task) EntryFactory.createEntry(Entry.EntryTypes.TASK, taskid, name, content, prio, color, due, creation, state);

                usersTasks.add(task);
            }

        } catch (SQLException e) {
            Alert connectionalert = new Alert(Alert.AlertType.ERROR, "Connection failed", ButtonType.OK);
            connectionalert.showAndWait();
        }


        try {
            for (Task task : usersTasks) {
                String year = String.valueOf(task.getDueDate().toLocalDate().getYear());
                int monthInt = task.getDueDate().toLocalDate().getMonthValue();
                String month = "";
                switch (monthInt) {
                    case 1:
                        month = "January";
                        break;
                    case 2:
                        month = "February";
                        break;
                    case 3:
                        month = "March";
                        break;
                    case 4:
                        month = "April";
                        break;
                    case 5:
                        month = "May";
                        break;
                    case 6:
                        month = "June";
                        break;
                    case 7:
                        month = "July";
                        break;
                    case 8:
                        month = "August";
                        break;
                    case 9:
                        month = "September";
                        break;
                    case 10:
                        month = "October";
                        break;
                    case 11:
                        month = "November";
                        break;
                    case 12:
                        month = "December";
                        break;
                }
                String yearSelection = yearCombo.getSelectionModel().getSelectedItem();
                String monthSelection = monthCombo.getSelectionModel().getSelectedItem();
                if (year.equals(yearSelection) && month.equals(monthSelection)) {
                    int day = task.getDueDate().toLocalDate().getDayOfMonth();
                    showDate(day, task);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }*/
    }

    private void loadSelectedMonth() {
        int year = Integer.parseInt(yearCombo.getSelectionModel().getSelectedItem());
        int month = monthCombo.getSelectionModel().getSelectedIndex();
        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        int firstDay = gc.get(java.util.Calendar.DAY_OF_WEEK);
        int daysInMonth = gc.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
        int offset = firstDay - 1;
        int gridCount = 1;
        int lblCount = 1;

        String style = "-fx-background-color: #C5CAE9";
        String hoveredStyle = "-fx-background-color: #95a1d2";
        String noDay = "-fx-background-color: transparent";

        // Go through calendar grid
        for (Node node : calendarGrid.getChildren()) {
            VBox day = (VBox) node;
            day.getChildren().clear();
            day.setStyle("-fx-backgroud-color: white");
            // Start placing labels on the first day for the month
            if (gridCount < offset) {
                gridCount++;
                // Darken color of the offset days
                day.setStyle(noDay);
                day.setOnMouseEntered(e -> day.setStyle(noDay));
                day.setOnMouseExited(e -> day.setStyle(noDay));
            } else {
                // Don't place a label if we've reached maximum label for the month
                if (lblCount > daysInMonth) {
                    // Instead, darken day color
                    day.setStyle(noDay);
                    day.setOnMouseEntered(e -> day.setStyle(noDay));
                    day.setOnMouseExited(e -> day.setStyle(noDay));
                } else {
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
                        day.getChildren().add(dayNumber);
                    } else {
                        // Make a new day label
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
                        day.getChildren().add(dayNumber);
                    }
                }
                lblCount++;
            }

        }
    }


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
                    Label descLbl = new Label(task.getName());    //(desc + time);
                    descLbl.setText(task.getName());
                    descLbl.setPadding(new Insets(2));
                    // Add label to calendar
                    day.getChildren().add(descLbl); //IMPLEMENT max 2 LABEL TODO
                }
            }
        }
    }

    private void showTasks(Node clickedDayNode) {
        Label label = (Label) clickedDayNode;
        int dayClicked = Integer.parseInt(label.getText());
        String yearSelection = yearCombo.getSelectionModel().getSelectedItem();
        String monthSelection = monthCombo.getSelectionModel().getSelectedItem();

        for (Task task : usersTasks) {
            String year = String.valueOf(task.getDueDate().toLocalDate().getYear());
            int monthInt = task.getDueDate().toLocalDate().getMonthValue();
            String month = "";
            switch (monthInt) {
                case 1:
                    month = "January";
                    break;
                case 2:
                    month = "February";
                    break;
                case 3:
                    month = "March";
                    break;
                case 4:
                    month = "April";
                    break;
                case 5:
                    month = "May";
                    break;
                case 6:
                    month = "June";
                    break;
                case 7:
                    month = "July";
                    break;
                case 8:
                    month = "August";
                    break;
                case 9:
                    month = "September";
                    break;
                case 10:
                    month = "October";
                    break;
                case 11:
                    month = "November";
                    break;
                case 12:
                    month = "December";
                    break;
            }
            int day = task.getDueDate().toLocalDate().getDayOfMonth();
            if (year.equals(yearSelection) && month.equals(monthSelection) && dayClicked == day) {
                clickedTasks.add(task);
            }
        }
        if (!clickedTasks.isEmpty()) {
            isListOpen = true;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/TaskViewCalendar.fxml"));

            try {
                loader.load();
            } catch (IOException e) {                                                         //Load overview screen
                e.printStackTrace();
            }

            TaskViewCalendar controller = loader.getController();
            taskViewController = controller;
            controller.setTasks(clickedTasks);
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Tasks for " + dayClicked + " of " + monthSelection + " " + yearSelection);
            stage.setResizable(true);
            stage.showAndWait();
            clickedTasks.clear();
            loadSelectedMonth();
            setTasks(user);
            updateOverview(usersTasks);
            isListOpen = false;
        }


    }

    @FXML
    void updateView(ActionEvent event) {
        loadSelectedMonth();
        setTasks(user);
    }

    void updateOverview(ArrayList arrayList) {
        overviewController.setUsersTasks(arrayList);
    }

    @FXML
    void initialize() {
        drawCalendarGrid();
        yearCombo.setItems(yearList());
        yearCombo.setValue(String.valueOf(currentYear));
        monthCombo.setItems(months);
        monthCombo.setValue(currentMonth);
        loadSelectedMonth();

        //Due to a jfx bug, this is neccessary to display the combobox list, otherwise it is "confused" whether the list s showing or not
        hPane.requestFocus();




        calendarBackButton.setOnAction(e->{
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setTitle("Overview");

            System.out.println("Back to overview");
            AnchorPane signup = null;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/Overview.fxml"));
            try {
                signup = loader.load(); //Load signup page
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Overview controller = loader.getController();
            controller.setOwnController(controller);
            controller.setUser(user);
            rootPane.getChildren().setAll(signup);
        });

    }

    public void setOwnController(Calendar controller) {
        this.ownController = controller;
    }


    public void setOverviewController(Overview controller) {
        this.overviewController = controller;
    }
}
