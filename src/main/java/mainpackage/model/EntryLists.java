package mainpackage.model;

import mainpackage.database.DatabaseHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class EntryLists {
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
            taskList.add(userTask);
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
