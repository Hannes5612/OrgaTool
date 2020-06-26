package mainpackage.threads;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import mainpackage.ListManager;
import mainpackage.model.Note;
import mainpackage.model.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SaveThread extends Thread {
    private final ListManager listManager = new ListManager();
    //String dirName = System.getProperty("user.home");
    //String export = dirName + File.separator + "Export.txt";
    private final File file;

    public SaveThread(File file) {
        this.file = file;
    }

    private void createFile() throws IOException {
        FileWriter writer = new FileWriter(file,false );

        writer.write("Notes : \n\n");

        listManager.getNoteList().forEach(note -> {
            try {
                writer.write(String.valueOf(note)+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        writer.write("\n");
        writer.write("TASKS : \n\n");

        listManager.getTaskList().forEach(task->{
            try {
                writer.write(String.valueOf(task)+"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        writer.close();
    }
    @Override
    public void run(){
        try {
            createFile();
            System.out.println("File written to home dir");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
