package mainpackage.model;

public enum State {

    ACTIVE(0, "ACTIVE"),
    FINISHED(1,"FINISHED"),
    ARCHIVED(2,"ARCHIVED");

    private final int id;
    private final String state;

    State (final int id, final String state) {
        this.id = id;
        this.state = state;
    }
}
