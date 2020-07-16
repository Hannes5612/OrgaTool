package mainpackage.exceptions;

/**
 * Exception to be thrown when a not implemented state type shall be selected.
 */
public class UnsupportedStateType extends Exception {
    public UnsupportedStateType(String message) {
        super(message);
    }
}
