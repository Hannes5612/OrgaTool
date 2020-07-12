package mainpackage.threads;

import mainpackage.ListManager;
import mainpackage.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class SaveThread extends Thread {
    private final ListManager listManager = new ListManager();
    private final File file;
    Logger logger = LogManager.getLogger(Main.class.getName());

    //constructor
    public SaveThread(File file) {
        this.file = file;
    }

    /**
     * Method for creating the File with a save location popup window.
     * @throws IOException
     */
    private void createFile() throws IOException {
        FileWriter writer = new FileWriter(file,false );

        writer.write("NOTES: \n\n");

        listManager.getNoteList().forEach(note -> {
            try {
                writer.write(note+"\n");
            } catch (IOException e) {
                logger.error("Note could not be written in File: " + e);
            }
        });
        logger.debug("Notes written in File");

        writer.write("\n");
        writer.write("TASKS: \n\n");

        listManager.getTaskList().forEach(task->{
            try {
                writer.write(task+"\n");
            } catch (IOException e) {
                logger.error("Task could not be written in File: " + e);
            }
        });


        logger.debug("Tasks written in File");
        writer.close();
    }
    @Override
    public void run(){
        try {
            createFile();
            logger.debug("File written: " + file);
        } catch (IOException e) {
            logger.error("File could not be written : " + e);
        }
    }


}
