package mainpackage.controller;

import com.jfoenix.controls.JFXComboBox;

import java.net.URL;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import mainpackage.model.Task;

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


    private ArrayList<Task> usersTasks = new ArrayList<>();

    // YearCombo
    private final int currentYear = Year.now().getValue();
    private final ObservableList<String> years = FXCollections.observableArrayList();

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
                vPane.getStyleClass().add("calendar_pane");
                vPane.setMinWidth(50);
                vPane.setSpacing(5);
                vPane.setPadding(new Insets(0, 0, 0, 0));
                GridPane.setVgrow(vPane, Priority.ALWAYS);
                // Add it to the grid
                calendarGrid.add(vPane, j, i);
            }
        }
    }

    public void setTasks(ArrayList<Task> usersTasks) {
        this.usersTasks = usersTasks;
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
        }
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

        String style = "-fx-background-color: transparent";
        String hoveredStyle = "-fx-background-color: #607D8B";
        String noDay = "-fx-background-color: #e7e7e7";

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
                    int today = LocalDate.now().getDayOfMonth();
                    // Make a new day label
                    Label dayNumber = new Label(Integer.toString(lblCount));
                    dayNumber.setPadding(new Insets(5));
                    dayNumber.setStyle("-fx-text-fill:#757575");
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


    public void showDate(int dayNumber, Task task) {
        //Image img = new Image(getClass().getClassLoader().getResourceAsStream("academiccalendar/ui/icons/icon2.png"));
        //ImageView imgView = new ImageView();
        //imgView.setImage(img);
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
                    //eventLbl.setGraphic(imgView);
                    descLbl.setText(task.getName());
                    descLbl.setPadding(new Insets(5));
                    // Add label to calendar
                    day.getChildren().add(descLbl);
                }
            }
        }
    }


    @FXML
    void updateView(ActionEvent event) {
        loadSelectedMonth();
        setTasks(usersTasks);
    }

    @FXML
    void initialize() {

        drawCalendarGrid();
        monthCombo.setItems(months);
        monthCombo.setValue(currentMonth);
        yearCombo.setItems(yearList());
        yearCombo.setValue(String.valueOf(currentYear));
        loadSelectedMonth();

    }
}
