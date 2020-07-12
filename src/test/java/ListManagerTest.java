import mainpackage.ListManager;
import mainpackage.model.Note;
import mainpackage.model.Task;
import mainpackage.model.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ListManagerTest {
    static ListManager listManager = new ListManager();

    /**
     * Before each ClassTestRun, fetch from DB and fill local lists
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @BeforeClass
    public static void update() throws SQLException, ClassNotFoundException {
        listManager.update();
    }

    /**
     * The user gets set before every test method
     */
    @Before
    public void setUser() {
        User user = new User();
        user.setUserid(1);
        ListManager.setUser(user);
    }

    /**
     * Check, whether the list gets filled by checking for the first item. Also tests the stream method.
     */
    @Test
    public void testListFilling() {
        List<Task> inputTasks = listManager.getTaskList().collect(Collectors.toList());
        List<Note> inputNotes = listManager.getNoteList().collect(Collectors.toList());
        assertNotNull(inputTasks.get(0));
        assertNotNull(inputNotes.get(0));
    }

    /**
     * Check, whether the lists in ListManager are getting cleared.
     */
    @Test
    public void testGetUserId() {
        assertNotEquals(22, ListManager.getUserId());
        assertEquals(1, ListManager.getUserId());
    }

    /**
     * Since the running auto increasing taskid is always chnaging only negative tests are possible
     */
    @Test
    public void testGetCountingTaskId() {
        assertNotEquals(1, ListManager.getCountingTaskID());
        assertNotEquals(2, ListManager.getCountingTaskID());
    }

    /**
     * Since the running auto increasing noteid is always chnaging only negative tests are possible
     */
    @Test
    public void testGetCountingNoteId() {
        assertNotEquals(1, ListManager.getCountingNoteId());
        assertNotEquals(2, ListManager.getCountingNoteId());
    }


    /**
     * Test, whether the incrementing of the iDs are working.
     */
    @Test
    public void testIncrementIds() {
        int maxNoteId = ListManager.getCountingNoteId();
        int maxTaskId = ListManager.getCountingTaskID();
        ListManager.incrementCountingNoteId();
        ListManager.incrementCountingTaskId();
        assertNotEquals(maxNoteId, ListManager.getCountingNoteId());
        assertNotEquals(maxTaskId, ListManager.getCountingTaskID());
        assertEquals(maxNoteId + 1, ListManager.getCountingNoteId());
        assertEquals(maxTaskId + 1, ListManager.getCountingTaskID());
    }

    /**
     * Test, whether inserting od new entries are working.
     */
    @Test
    public void testInsert() {
        int beforeTasks = (int) listManager.getTaskList().count();
        int beforeNotes = (int) listManager.getNoteList().count();
        Task task = new Task(911, "Title", "content", "H", "0x00000ff", new Date(321554543), new Date(321554543), 2);
        Note note = new Note(911, "2", "3");
        ListManager.addNote(note);
        ListManager.addTask(task);
        int afterTasks = (int) listManager.getTaskList().count();
        int afterNotes = (int) listManager.getNoteList().count();
        assertNotEquals(beforeNotes, afterNotes);
        assertNotEquals(beforeTasks, afterTasks);
        assertEquals(beforeNotes + 1, afterNotes);
        assertEquals(beforeTasks + 1, afterTasks);
    }

    /**
     * Test, whether the deletion of specific entries are working.
     */
    @Test
    public void testDelete() {
        int beforeTasks = (int) listManager.getTaskList().count();
        int beforeNotes = (int) listManager.getNoteList().count();
        Task task = new Task(911, "Title", "content", "H", "0x00000ff", new Date(321554543), new Date(321554543), 2);
        Note note = new Note(911, "2", "3");
        ListManager.addNote(note);
        ListManager.addTask(task);
        ListManager.deleteNote(note.getId());
        ListManager.deleteTask(task.getId());
        int afterTasks = (int) listManager.getTaskList().count();
        int afterNotes = (int) listManager.getNoteList().count();
        assertEquals(beforeNotes,afterNotes);
        assertEquals(beforeTasks,afterTasks);
    }

    /**
     * Test, whether on logout all data gets wiped.
     */
    @Test
    public void testWipe() {
        ListManager.wipe();
        List<Task> inputTasks = listManager.getTaskList().collect(Collectors.toList());
        List<Note> inputNotes = listManager.getNoteList().collect(Collectors.toList());
        assertEquals(0, inputTasks.size());
        assertEquals(0, inputNotes.size());
    }
}
