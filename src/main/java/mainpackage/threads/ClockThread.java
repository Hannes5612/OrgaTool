package mainpackage.threads;

import javafx.application.Platform;
import javafx.scene.control.Label;
import mainpackage.Main;
import mainpackage.controller.Overview;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ClockThread extends Thread{
    Label timeLabel;
    Label dateLabel;

    private final Logger logger = LogManager.getLogger(Main.class.getName());
    private String time = "", month = "", day = "";


    public ClockThread(Label timeLabel, Label dateLabel) {
        this.timeLabel = timeLabel;
        this.dateLabel = dateLabel;
    }

    /**
     * Runnable for the background Thread
     */
    @Override
    public void run() {
        logger.info("Clock running");
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

                //wait a second
                Thread.sleep(1000);
            }
        } catch (Exception e) { //Error check
            dateLabel.setText("");
            timeLabel.setText("Error occurred!!");
            logger.error("Clock thread has failed: " + e);
        }


    }
}
