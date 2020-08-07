package mainpackage;

import javafx.collections.FXCollections;
import mainpackage.database.DatabaseHandler;
import mainpackage.model.Note;
import mainpackage.model.Task;
import mainpackage.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

/**
 * Class manages the ObservableLists for Tasks and Notes
 * and synchronizes them with the database.
 */
public class ListManager {

    private static final Logger logger = LogManager.getLogger(Main.class.getName());
    private static User user = new User();
    private static int countingTaskID;
    private static int countingNoteId;

    // Synchronized Observable Lists
    private static final List<Task> taskList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private static final List<Note> noteList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

    // constructor
    public ListManager() {
    }

    // getters
    public static int getUserId() {
        return user.getUserid();
    }

    public static int getCountingTaskID() {
        return countingTaskID;
    }

    public static int getCountingNoteId() {
        return countingNoteId;
    }

    // Methods to increase the note and task id's for a correct creation
    public static void incrementCountingTaskId() {
        countingTaskID++;
    }

    public static void incrementCountingNoteId() {
        countingNoteId++;
    }

    /**
     * Run the databasehandlers of both notes and tasks.
     *
     * @throws SQLException - on a failed connection
     * @throws ClassNotFoundException - class not found
     */
    public void update() throws SQLException, ClassNotFoundException {
        updateNotes();
        updateTasks();
    }

    /**
     * Update the observable notelist from the database
     *
     * @throws SQLException - SQLException
     * @throws ClassNotFoundException - class not found
     */
    public void updateNotes() throws SQLException, ClassNotFoundException {
        noteList.clear();
        DatabaseHandler databaseHandler = new DatabaseHandler();
        ResultSet noteRow = databaseHandler.getNotes();
        logger.info("Notes from database fetched");

        // loading the ResultSet into the Observable List
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
        logger.info("Notes in local list loaded");
    }

    /**
     * Update the observable tasklist from the database
     *
     * @throws SQLException - SQLException
     * @throws ClassNotFoundException - class not found
     */
    public void updateTasks() throws SQLException, ClassNotFoundException {
        taskList.clear();
        DatabaseHandler databaseHandler = new DatabaseHandler();
        ResultSet taskRow = databaseHandler.getTasks(user);
        logger.info("Tasks from database fetched");


        // loading the ResultSet into the Observable List
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
            logger.info("Task loaded: " + task.toString());
            taskList.add(task);
        }
        logger.info("Tasks in local list loaded");
    }

    // static methods
    public static void setUser(User user) {
        ListManager.user = user;
    }

    public static void deleteTask(int taskId) {
        taskList.removeIf(task -> task.getId() == taskId);
    }

    /**
     * deleting note from list of notes
     *
     * @param noteId ID of note to be deleted
     */
    public static void deleteNote(int noteId) {
        noteList.removeIf(note -> note.getId() == noteId);
        logger.info("Deleted note from list of notes.");
    }

    /**
     * editing note in list of notes
     *
     * @param note edited note which will be shown in list of notes
     */
    public static void editNote(Note note) {

        int remove = 0;
        for (Note note1 : noteList) {
            if (note.getId() == note1.getId()) {
                noteList.set(remove, note);
                logger.info("Edited note in list of notes.");
            }
            remove++;
        }
    }

    public static void editTask(Task task) {

        int remove = 0;
        for (Task task1 : taskList) {
            if (task.getId() == task1.getId()) {
                taskList.set(remove, task);
            }
            remove++;
        }
    }

    public static void addTask(Task task) {
        taskList.add(task);
    }

    /**
     * adding note to list of notes
     *
     * @param note note to be added to list of notes
     */
    public static void addNote(Note note) {
        noteList.add(note);
    }


    /**
     * Synchronized method to parallel stream the noteLists items
     *
     * @return stream of notes
     */
    public synchronized Stream<Note> getNoteList() {
        return noteList.parallelStream();
    }

    /**
     * Synchronized method to parallel stream the taskLists items
     *
     * @return stream of tasks
     */
    public synchronized Stream<Task> getTaskList() {
        return taskList.parallelStream();
    }


    //Methods to access the latest Entry and fetch it
    public Note getLatestNote() {
        return noteList.get(noteList.size() - 1);
    }

    public Task getLatestTask() {
        return taskList.get(taskList.size() - 1);
    }

    /**
     * On logout, all data needs to be deleted, in case of exceptions
     */
    public static void wipe() {
        taskList.clear();
        noteList.clear();
        user = new User();
        countingNoteId = -1;
        countingTaskID = -1;

        logger.warn("All Lists and the user were wiped.");
    }

}
