package org.process.model;

import java.util.List;

public class ExcelFileInformation extends FileInformation{
	private List<String> sheets;

	public List<String> getSheets() {
		return sheets;
	}

	public void setSheets(List<String> sheets) {
		this.sheets = sheets;
	}
	
	
}
