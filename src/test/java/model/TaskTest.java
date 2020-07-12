package model;

import org.junit.Test;
import mainpackage.model.Task;

import java.sql.Date;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;



public class TaskTest {
    Task task1 = new Task(34,"Lernen","Ich muss lernen omg","H","#321123",new Date(Calendar.getInstance().getTime().getTime()+443332),new Date(Calendar.getInstance().getTime().getTime()),2);
    Task task2 = new Task(34,"Essen","Ich muss lernen omg","H","#321123",new Date(Calendar.getInstance().getTime().getTime()+443332),new Date(Calendar.getInstance().getTime().getTime()),2);
    Task task3 = new Task(34,"Gehen","Ich muss lernen omg","H","#321123",new Date(Calendar.getInstance().getTime().getTime()+443332),new Date(Calendar.getInstance().getTime().getTime()),2);
    Task task4 = new Task(34,"Kaffe","Ich muss lernen omg","H","#321123",new Date(Calendar.getInstance().getTime().getTime()+443332),new Date(Calendar.getInstance().getTime().getTime()),2);
    Task task5 = new Task(34,"Aufräumen","Ich muss lernen omg","H","#321123",new Date(Calendar.getInstance().getTime().getTime()+443332),new Date(Calendar.getInstance().getTime().getTime()),2);
    Task task6 = new Task(34,"Kochen","Ich muss lernen omg","H","#321123",new Date(Calendar.getInstance().getTime().getTime()+443332),new Date(Calendar.getInstance().getTime().getTime()),2);

    @Test
    public void taskTestTitle() {
        assertEquals("Lernen", task1.getTitle());
        assertEquals("Essen", task2.getTitle());
        assertEquals("Gehen", task3.getTitle());
        assertEquals("Kaffe", task4.getTitle());
        assertEquals("Aufräumen", task5.getTitle());
        assertEquals("Kochen", task6.getTitle());
    }
}
