package org.process.model;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection ="fileInformation")
public abstract class FileInformation extends BaseEntity{
	@Indexed(unique=true)
	private String fileName;
	private Map<String, Map<Integer, CellInfo>> columns;
	private Map<String, List<RowFileInformation>> data;
	
	public Map<String, Map<Integer, CellInfo>> getColumns() {
		return columns;
	}
	public void setColumns(Map<String, Map<Integer, CellInfo>> columns) {
		this.columns = columns;
	}
	public Map<String, List<RowFileInformation>> getData() {
		return data;
	}
	public void setData(Map<String, List<RowFileInformation>> data) {
		this.data = data;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	@Override
	public String toString() {
		return "FileInformation [columns=" + columns + ", data=" + data + ", fileName="+ fileName + "]";
	}
}
