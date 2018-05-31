package org.process.model;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class RowFileInformation {
	@Id
    private ObjectId id;
	
	private String fileName;
	private String sheet;
	private int rowId;
	private List<CellInfo> cells;
	
	
	public RowFileInformation(String fileName, String sheet, int rowId, List<CellInfo> cells) {
		super();
		this.fileName = fileName;
		this.sheet = sheet;
		this.rowId = rowId;
		this.cells = cells;
	}
	
	public String getId() {
		return id.toString();
	}
	public void setId(String id) {
		this.id = new ObjectId(id);
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getSheet() {
		return sheet;
	}
	public void setSheet(String sheet) {
		this.sheet = sheet;
	}
	public int getRowId() {
		return rowId;
	}
	public void setRowId(int rowId) {
		this.rowId = rowId;
	}
	public List<CellInfo> getCells() {
		return cells;
	}
	public void setCells(List<CellInfo> cells) {
		this.cells = cells;
	}
	@Override
	public String toString() {
		return "RowFileInformation [id=" + id + ", fileName=" + fileName + ", sheet=" + sheet + ", rowId=" + rowId
				+ ", cells=" + cells + "]";
	}

}
