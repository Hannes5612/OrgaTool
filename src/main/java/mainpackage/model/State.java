package mainpackage.model;

/**
 * 3 different states of notes/tasks: active, finished and archived
 */
public enum State {

    ACTIVE(0, "ACTIVE"),
    FINISHED(1, "FINISHED"),
    ARCHIVED(2, "ARCHIVED");

    private final int id;
    private final String state;

    /**
     * Creating state enum.
     *
     * @param id - integer 0, 1 or 2
     * @param state - state of the enum
     */
    State(final int id, final String state) {
        this.id = id;
        this.state = state;
    }

}
