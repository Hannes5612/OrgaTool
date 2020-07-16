package mainpackage.exceptions;

/**
 * Exception to be thrown, when user ID is illegal
 */
public class IllegalIdentification extends Exception {
    public IllegalIdentification(String message) {
        super(message);
    }
}
