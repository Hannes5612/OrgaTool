package mainpackage.controller;

import javafx.scene.control.ListCell;
import mainpackage.exceptions.UnsupportedCellType;

/**
 * This class is the factory to return an object either from type NoteCell or TaskCell.
 */
public class CellFactory {

    /**
     * Create a cell for ListView.
     *
     * @param cellType is the type of cell to get created
     * @return Task or Note Cell
     * @throws UnsupportedCellType @param is not in the switch -> not implemented
     */
    public ListCell createCell(String cellType) throws UnsupportedCellType {
        switch (cellType) {
            case "note":
                return new NoteCell();
            case "task":
                return new TaskCell();
            default:
                throw new UnsupportedCellType("Cell type does not exist!");
        }
    }

}
