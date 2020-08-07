package model;

import mainpackage.exceptions.UnsupportedStateType;
import mainpackage.model.Note;
import org.junit.Test;

import java.sql.Date;

import static org.junit.Assert.*;

public class NoteTest {

    Date date1 = new java.sql.Date(System.currentTimeMillis());
    Date date2 = new java.sql.Date(System.currentTimeMillis() + 2000);

    Note note1 = new Note(1, "Hello", "");
    Note note2 = new Note(2, null, null);
    Note note3 = new Note(3, "", "xy");
    Note note4 = new Note(4, "xxx", "yyy", date1, 0);
    Note note5 = new Note(5, "test", "hi", date2, 1);
    Note note6 = new Note(568377433, "hello", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", date1, 2);

    /**
     * Testing if note ID is set correctly.
     */
    @Test
    public void noteTestId() {
        assertEquals(1, note1.getId());
        assertEquals(2, note2.getId());
        assertEquals(568377433, note6.getId());
    }

    /**
     * Testing if note title is set correctly.
     */
    @Test
    public void noteTestTitle() {
        assertEquals("Hello", note1.getTitle());
        assertNull(note2.getTitle());
        assertEquals("", note3.getTitle());
        assertEquals("xxx", note4.getTitle());
    }

    /**
     * Testing if note content is set correctly.
     */
    @Test
    public void noteTestContent() {
        assertEquals("", note1.getContent());
        assertNull(note2.getContent());
        assertEquals("xy", note3.getContent());
        assertEquals("yyy", note4.getContent());
        assertEquals("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", note6.getContent());
    }

    /**
     * Testing if note creationDate is set correctly.
     */
    @Test
    public void noteTestDate() {
        assertEquals(date1, note4.getCreationDate());
        assertEquals(date2, note5.getCreationDate());
    }

    /**
     * Testing if note state is set correctly.
     */
    @Test
    public void noteTestState() throws UnsupportedStateType {
        assertEquals(0, note1.getState());
        assertEquals(0, note4.getState());
        assertEquals(1, note5.getState());
        assertEquals(2, note6.getState());
    }

}