package controller;


import mainpackage.controller.Calendar;
import org.junit.Test;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CalendarTest {
    private final Calendar calendar = new Calendar();

    /**
     * Testing of the generation of the current month
     */
    @Test
    public void testCurrentMonth() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String currentMonth = new SimpleDateFormat("MMMM").format(cal.getTime());
        cal.add(java.util.Calendar.MONTH, 1);
        String nextMonth = new SimpleDateFormat("MMMM").format(cal.getTime());

        //positive and negative testing
        assertEquals(currentMonth, calendar.getCurrentMonth());
        assertNotEquals(nextMonth, calendar.getCurrentMonth());

    }

    /**
     * Testing of the generated year List with exemplary values
      */
    @Test
    public void testCurrentYear() {

        //positive and negative testing
        assertEquals("1981", calendar.yearList().get(calendar.yearList().size()-1));
        assertEquals("2030", calendar.yearList().get(0));
        assertEquals(50, calendar.yearList().size());
        assertNotEquals(100, calendar.yearList().size());
        assertNotEquals("1980", calendar.yearList().get(calendar.yearList().size()-1));

    }
}
