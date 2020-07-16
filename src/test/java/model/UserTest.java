package model;

import mainpackage.exceptions.IllegalIdentification;
import mainpackage.model.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UserTest {

    User user1 = new User("Test123", "Test123");
    User user2 = new User(null, null);
    User user3 = new User("", "");

    /**
     * testing if user's name is set correctly
     */
    @Test
    public void userNameTest() {
        assertEquals("Test123", user1.getUserName());
        assertNull(user2.getUserName());
        assertEquals("", user3.getUserName());
    }

    /**
     * testing if user's password is set correctly
     */
    @Test
    public void userPasswordTest() {
        assertEquals("Test123", user1.getPassword());
        assertNull(user2.getPassword());
        assertEquals("", user3.getPassword());
    }

    /**
     * testing if user's ID is set correctly
     *
     * @throws IllegalIdentification thrown when ID is invalid
     */
    @Test
    public void userIdTest() throws IllegalIdentification {
        user1.setUserid(1);
        user2.setUserid(435655765);
        assertEquals(1, user1.getUserid());
        assertEquals(435655765, user2.getUserid());
    }

    /**
     * Test to intentionally throw the custom Exception
     *
     * @throws IllegalIdentification thrown when ID is invalid
     */
    @Test(expected = IllegalIdentification.class)
    public void idExceptionTest() throws IllegalIdentification {
        User user4 = new User();
        user4.setUserid(-383);

        User user5 = new User();
        user5.setUserid(-487364974);
    }
}
