package mainpackage.toDoList;

/**
 * Class manages tasks created by the user.
 */
public class ToDoList {

    private int numberOfTasks = 0;

    public void createTask() {
        final String name = "Imput from user. (name)";
        final String content = "Input from user. (content)";
        final String color = "Input from user. (color)";
        final Date dueDate; // = ?
        final Date reminder; // = ?

        Task task = new Task("Test 1", "", numberOfTasks, color, dueDate, reminder);
    }

    public void deleteTask() {

    }

    public void listTasks() {

    }

    public void changeView() {

    }

}
