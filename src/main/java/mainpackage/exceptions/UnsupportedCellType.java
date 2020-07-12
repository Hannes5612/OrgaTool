package mainpackage.exceptions;

/**
 * Exception to be thrown, whe a not implemented cellType shall be created
 */
public class UnsupportedCellType extends Exception{
    public UnsupportedCellType(String message){
        super (message);
    }
}
