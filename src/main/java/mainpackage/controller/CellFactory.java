package mainpackage.controller;

import com.jfoenix.controls.JFXListCell;
import mainpackage.exceptions.UnsupportedCellType;

public class CellFactory {

    /**
     * Create a cell for ListView.
     * @param cellType is the type of cell to get created
     * @return Task or Note Cell
     * @throws UnsupportedCellType @param is not in the switch -> not implemented
     */
    public JFXListCell createCell(String cellType) throws UnsupportedCellType {
        switch (cellType){
            case "note": return new NoteCell();
            case "task": return new TaskCell();
            default: throw new UnsupportedCellType("Cell type does not exist!");
        }
    }

}
