package mainpackage.threads;

import javafx.application.Platform;
import javafx.scene.control.Label;
import mainpackage.controller.Overview;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ClockThread extends Thread{
    Label timeLabel;
    Label dateLabel;

    private String time = "", month = "", day = "";

    public void setLabels(Label time, Label date){
        this.timeLabel = time;
        this.dateLabel = date;
    }
    @Override
    public void run() {
        try {
            while (true) {

                //Setting date format and variables:

                Calendar calendar = Calendar.getInstance();

                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                Date date = calendar.getTime();
                time = format.format(date);

                format = new SimpleDateFormat("EEE, MMMM dd yyyy");
                date = calendar.getTime();
                month = format.format(date);

                //Setting elements to pane:

                Platform.runLater(() -> {
                    dateLabel.setText(String.valueOf(month));
                    timeLabel.setText(time);

                });

                Thread.sleep(1000);
            }
        } catch (Exception e) { //Error check
            dateLabel.setText("");
            timeLabel.setText("Error occurred!!");
        }


    }
}
