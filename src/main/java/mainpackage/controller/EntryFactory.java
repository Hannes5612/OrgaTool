package mainpackage.controller;

import mainpackage.model.Task;
import mainpackage.model.Note;

public class EntryFactory {

    public static Entry createEntry(Entry.EntryTypes entry, String name, String content, String priority, String color, java.sql.Date dueDate, java.sql.Date creationDate) {
        switch(entry) {
            case TASK: return new Task(name, content, priority, color, dueDate, creationDate);
            // case NOTE: return new Note();
            default: throw new IllegalArgumentException("Illegal EntryType!");
        }
    }

}
