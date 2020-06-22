package mainpackage.model;

public class EntryFactory {

    public static Entry createEntry(Entry.EntryTypes entry, int taskid, String name, String content, String priority, String color, java.sql.Date dueDate, java.sql.Date creationDate, int state) {
        switch(entry) {
            case TASK: return new Task(taskid, name, content, priority, color, dueDate, creationDate, state);
            // case NOTE: return new Note();
            default:
                // log.error("Illegal EntryType!");
                throw new IllegalArgumentException("Illegal EntryType!");
        }
    }

}
