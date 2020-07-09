package mainpackage.controller;

import com.jfoenix.controls.JFXListCell;
import mainpackage.exceptions.UnsupportedCellType;

public class CellFactory {

    public JFXListCell createCell(String cellType) throws UnsupportedCellType {
        switch (cellType){
            case "note": return new NoteCell();
            case "task": return new TaskCell();
            default: throw new UnsupportedCellType("Cell type does not exist!");
        }
    }

}
