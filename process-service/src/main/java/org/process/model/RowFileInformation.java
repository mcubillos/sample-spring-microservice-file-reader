package org.process.model;

import java.util.List;

public class RowFileInformation {

	private int rowId;
	private List<CellInfo> cells;

	public RowFileInformation(int rowId, List<CellInfo> cells) {
		super();
		this.rowId = rowId;
		this.cells = cells;
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
		return "RowFileInformation [rowId=" + rowId + ", cells=" + cells + "]";
	}
}
