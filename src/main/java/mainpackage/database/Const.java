package mainpackage.database;

/**
 * SQL configuration of the applications database tables and rows.
 */
public class Const {
    protected static final String USER_TABLE = "users";
    protected static final String TASK_TABLE = "tasks";
    protected static final String NOTE_TABLE = "notes";

    protected static final String USER_ID = "userid";
    protected static final String USER_USERNAME = "username";
    protected static final String USER_PASSWORD = "password";

    protected static final String TASK_ID = "taskid";
    protected static final String TASK_USER = "userid";
    protected static final String TASK_TYPE = "type";
    protected static final String TASK_TITLE = "title";
    protected static final String TASK_CONTENT = "content";
    protected static final String TASK_PRIO = "prio";
    protected static final String TASK_COLOR = "color";
    protected static final String TASK_DUEDATE = "dueDate";
    protected static final String TASK_CREATIONDATE = "creationDate";
    protected static final String TASK_STATE = "state";

    protected static final String NOTE_ID = "notesid";
    protected static final String NOTE_USER = "userid";
    protected static final String NOTE_TITLE = "title";
    protected static final String NOTE_CONTENT = "content";
    protected static final String NOTE_DATE = "creationDate";
    protected static final String NOTE_STATE = "state";

}