package mainpackage.model;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class EntryFactory {

    private static Logger log = LogManager.getLogger(EntryFactory.class);

    public static Entry createEntry(Entry.EntryTypes entry, int entryid, String title, String content, String priority, String color, java.sql.Date dueDate, java.sql.Date creationDate, int state) {
        switch(entry) {
            case TASK: return new Task(entryid, title, content, priority, color, dueDate, creationDate, state);
            case NOTE: return (Entry) new Note(entryid, title, content, creationDate, state);
            default:
                log.error("Illegal EntryType!");
                throw new IllegalArgumentException("Illegal EntryType!");
        }
    }

}
