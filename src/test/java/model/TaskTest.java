package model;

import mainpackage.exceptions.UnsupportedStateType;
import org.junit.Test;
import mainpackage.model.Task;

import java.sql.Date;

import static org.junit.Assert.*;


public class TaskTest {

    Date date1 = new java.sql.Date(System.currentTimeMillis());
    Date date2 = new java.sql.Date(System.currentTimeMillis() + 42);
    Date date3 = new java.sql.Date(System.currentTimeMillis() + 1234);
    Date date4 = new java.sql.Date(System.currentTimeMillis() + 1337);
    Date date5 = new java.sql.Date(System.currentTimeMillis() + 5678);
    Date date6 = new java.sql.Date(System.currentTimeMillis() + 31415926);

    Task task1 = new Task(1,        "Lernen",         "Ich muss SE-Projekt machen, omg!",    "H", "#b2149a", date1, date6, 0);
    Task task2 = new Task(2,        null,             "Ich muss Mathe lernen, omg!",         "M", "#1355c2", date2, date5, 1);
    Task task3 = new Task(3,        "Kaffee trinken", "Ich muss WebDev lernen, omg!",        "L", "#ff6a00", date3, date4, 2);
    Task task4 = new Task(42,       "Cardistry",      "Ich muss Rechnernetze lernen, würg!", "H", "#ff0000", date4, date3, 0);
    Task task5 = new Task(1337,     "Weiter lernen",  "Ich muss Datenbanken lernen, omg!",   "H", "#24822d", date5, date2, 1);
    Task task6 = new Task(31415926, "Schlafen",       null,                                  "M", "#551cb5", date6, date1, 2);

    /**
     * Testing if task ID is set correctly.
     */
    @Test
    public void taskTestId() {
        assertEquals(1,    task1.getId());
        assertEquals(2,    task2.getId());
        assertEquals(3,    task3.getId());
        assertEquals(42,   task4.getId());
        assertEquals(1337, task5.getId());
        assertNotEquals(271828,    task6.getId());
    }

    /**
     * Testing if task title is set correctly.
     */
    @Test
    public void taskTestTitle() {
        assertEquals("Lernen",         task1.getTitle());
        assertNull(task2.getTitle());
        assertEquals("Kaffee trinken", task3.getTitle());
        assertEquals("Cardistry",      task4.getTitle());
        assertNotEquals("Pause",               task5.getTitle());
        assertEquals("Schlafen",       task6.getTitle());
    }

    /**
     * Testing if task content is set correctly.
     */
    @Test
    public void taskTestContent() {
        assertEquals("Ich muss SE-Projekt machen, omg!",    task1.getContent());
        assertEquals("Ich muss Mathe lernen, omg!",         task2.getContent());
        assertNotEquals("Ich kann Pause machen!",                   task3.getContent());
        assertEquals("Ich muss Rechnernetze lernen, würg!", task4.getContent());
        assertEquals("Ich muss Datenbanken lernen, omg!",   task5.getContent());
        assertNull(task6.getContent());
    }

    /**
     * Testing if task priority is set correctly.
     */
    @Test
    public void taskTestPriority() {
        assertEquals("H", task1.getPriority());
        assertEquals("M", task2.getPriority());
        assertEquals("L", task3.getPriority());
        assertEquals("H", task4.getPriority());
        assertEquals("H", task5.getPriority());
        assertNotEquals("L",      task6.getPriority());
    }

    /**
     * Testing if task color is set correctly.
     */
    @Test
    public void taskTestColor() {
        assertEquals("#b2149a", task1.getColor());
        assertEquals("#1355c2", task2.getColor());
        assertEquals("#ff6a00", task3.getColor());
        assertEquals("#ff0000", task4.getColor());
        assertEquals("#24822d", task5.getColor());
        assertNotEquals("#000000",      task6.getColor());
    }

    /**
     * Testing if task dueDate and creationDate is set correctly.
     */
    @Test
    public void taskTestDate() {
        assertEquals(date1,    task1.getDueDate());
        assertEquals(date2,    task2.getDueDate());
        assertEquals(date3,    task3.getDueDate());
        assertEquals(date4,    task4.getDueDate());
        assertEquals(date5,    task5.getDueDate());
        assertNotEquals(date1, task6.getDueDate());

        assertEquals(date6,    task1.getCreationDate());
        assertEquals(date5,    task2.getCreationDate());
        assertEquals(date4,    task3.getCreationDate());
        assertEquals(date3,    task4.getCreationDate());
        assertEquals(date2,    task5.getCreationDate());
        assertNotEquals(date6, task6.getCreationDate());
    }

    /**
     * Testing if task state is set correctly.
     */
    @Test
    public void taskTestState() throws UnsupportedStateType {
        assertEquals(0, task1.getState());
        assertEquals(1, task2.getState());
        assertEquals(2, task3.getState());
        assertNotEquals(3,      task4.getState());
    }

}
