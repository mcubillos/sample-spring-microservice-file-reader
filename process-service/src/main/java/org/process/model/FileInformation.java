package org.process.model;

import java.util.List;
import java.util.Map;

public abstract class FileInformation {
	private Map<Integer, CellInfo> columns;
	private Map<String, List<RowFileInformation>> data;

	public Map<Integer, CellInfo> getColumns() {
		return columns;
	}
	public void setColumns(Map<Integer, CellInfo> columns) {
		this.columns = columns;
	}
	public Map<String, List<RowFileInformation>> getData() {
		return data;
	}
	public void setData(Map<String, List<RowFileInformation>> data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "FileInformation [columns=" + columns + ", data=" + data + "]";
	}
}
