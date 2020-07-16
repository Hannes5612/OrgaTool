package controller;

import de.saxsys.javafx.test.JfxRunner;
import javafx.scene.control.ListCell;
import mainpackage.controller.CellFactory;
import mainpackage.controller.NoteCell;
import mainpackage.controller.TaskCell;
import mainpackage.exceptions.UnsupportedCellType;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(JfxRunner.class)
public class CellfactoryTest {

    private CellFactory cellFactory = new CellFactory();


    /**
     * Test for the creation of specific cells from the factory
     * @throws UnsupportedCellType
     */
    @Test
    public void testCellFactory() throws UnsupportedCellType {

        assertEquals(NoteCell.class, cellFactory.createCell("note").getClass());
        assertEquals(TaskCell.class, cellFactory.createCell("task").getClass());
        assertNotEquals(TaskCell.class, cellFactory.createCell("note").getClass());
        assertNotEquals(NoteCell.class, cellFactory.createCell("task").getClass());

    }

    /**
     * Test to intentionally throw the custom Exception
     * @throws UnsupportedCellType
     */
    @Test(expected = UnsupportedCellType.class)
    public void testCellFactoryException() throws UnsupportedCellType {
        ListCell listCell = cellFactory.createCell("picture");
        ListCell listCell2 = cellFactory.createCell("drawing");
    }
}
