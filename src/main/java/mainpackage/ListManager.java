package mainpackage;

import javafx.collections.FXCollections;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.Note;
import mainpackage.model.Task;
import mainpackage.model.User;

//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

public class ListManager {

//    private static final Logger LOG = LogManager.getLogger(ListManager.class);

    private static User user = new User();
    private static int countingTaskID;
    private static int countingNoteId;
    private static final List<Task> taskList = FXCollections.observableArrayList();
    private static final List<Note> noteList = FXCollections.observableArrayList();

    public ListManager() {
    }

    public static int getUserId() {
        return user.getUserid();
    }

    public static int getCountingTaskID(){
        return countingTaskID;
    }

    public static int getCountingNoteId() { return countingNoteId; }

    public static void incrementCountingTaskId(){
        countingTaskID++;
    }

    public static void incrementCountingNoteId(){
        countingNoteId++;
    }

    public void update() throws SQLException, ClassNotFoundException {
        updateNotes();
        updateTasks();
    }

    public void updateNotes() throws SQLException, ClassNotFoundException {
        noteList.clear();
        DatabaseHandler databaseHandler = new DatabaseHandler();
        ResultSet noteRow = databaseHandler.getNotes();

        while (noteRow.next()) {
            int noteid = noteRow.getInt("notesid");
            String title = noteRow.getString("title");
            String content = noteRow.getString("content");
            java.sql.Date creationDate = noteRow.getDate("creationDate");
            int state = noteRow.getInt("state");

            countingNoteId = noteid + 1;
            Note note = new Note(noteid, title, content, creationDate, state);
            noteList.add(note);
        }
    }

    public void updateTasks() throws SQLException, ClassNotFoundException {
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

            countingTaskID = id + 1;
            Task task = new Task(id, title, content, priority, color, dueDate, creationDate, state);
           // LOG.info("Task loaded: " + task.toString());
            System.out.println("Task loaded: " + task.toString());
            taskList.add(task);
        }
    }

    public static void setUser(User user) {
        ListManager.user = user;
    }

    public static void deleteTask(int taskId){
        taskList.removeIf(task -> task.getId() == taskId);
    }
    public static void deleteNote(int noteId){
        noteList.removeIf(note -> note.getId() == noteId);
    }

    public static void archiveNote(Note note) {
        note.archive();
    }

    public static void reactivateNote(Note note) {
        note.reactivate();
    }

    public static void editNote(Note note) {

        int remove=0;
        for (Note note1 : noteList) {
            if(note.getId() == note1.getId()){
                noteList.set(remove, note);
            }
            remove++;
        }

    }

    public static void editTask(Task task) {

        int remove=0;
        for (Task task1 : taskList) {
            if(task.getId() == task1.getId()) {
                taskList.set(remove, task);
            }
            remove++;
        }

    }

    public static void addTask(Task task) {
        taskList.add(task);
    }

    public static void addNote(Note note) {
        noteList.add(note);
    }

    public Stream<Note> getNoteList() {
        return noteList.stream();
    }

    public Stream<Task> getTaskList() {
        return taskList.stream();
    }

    public Note getLatestNote(){
        return noteList.get(noteList.size() - 1);
    }


    public static void wipe(){
        taskList.clear();
        noteList.clear();
        user = new User();
        countingNoteId = -1;
        countingTaskID = -1;
    }

}
