package mainpackage.model;

import mainpackage.database.DatabaseHandler;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EntryLists {

    private static Logger log = LogManager.getLogger(EntryLists.class);

    private static User user = new User();
    private final List<Task> taskList = new ArrayList<>();
    private final List<Note> noteList = new ArrayList<>();

    public EntryLists() {
    }

    public static int getUserId() {
        return user.getUserid();
    }

    public void update() throws SQLException {
        updateNotes();
        updateTasks();
    }

    public void updateNotes() throws SQLException {
        noteList.clear();
        DatabaseHandler databaseHandler = new DatabaseHandler();
        ResultSet noteRow = databaseHandler.getNotes();

        while (noteRow.next()) {
            int noteid = noteRow.getInt("notesid");
            String title = noteRow.getString("title");
            String content = noteRow.getString("content");
            java.sql.Date creationDate = noteRow.getDate("creationDate");
            int state = noteRow.getInt("state");

            Note note = new Note(noteid, title, content, creationDate, state);
            noteList.add(note);
        }
    }

    public void updateTasks() throws SQLException {
        taskList.clear();
        DatabaseHandler databaseHandler = new DatabaseHandler();
        ResultSet taskRow = databaseHandler.getTasks(user);
        while (taskRow.next()) {
            int id = taskRow.getInt("taskid");
            String title = taskRow.getString("title");
            String content = taskRow.getString("content");
            String priority = taskRow.getString("prio");
            String color = taskRow.getString("color");
            java.sql.Date dueDate = taskRow.getDate("dueDate");
            java.sql.Date creationDate = taskRow.getDate("creationDate");
            int state = taskRow.getInt("state");

            Task task = new Task(id, title, content, priority, color, dueDate, creationDate, state);
            log.info("Task created: " + task.toString());
            System.out.println("Task created: " + task.toString());
            taskList.add(task);
        }
    }

    public static void setUser(User user) {
        EntryLists.user = user;
    }

    public void addTask(Task task) {
        taskList.add(task);
    }

    public void addNote(Note note) {
        noteList.add(note);
    }

    public Stream<Note> getNoteList() {
        return this.noteList.stream();
    }

    public Stream<Task> getTaskList() {
        return this.taskList.stream();
    }

}
