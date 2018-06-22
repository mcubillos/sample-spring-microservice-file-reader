package org.process.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.spreadsheet.KCell;
import org.pentaho.di.trans.steps.excelinput.poi.PoiSheet;
import org.pentaho.di.trans.steps.excelinput.poi.PoiWorkbook;
import org.process.model.CellInfo;
import org.process.model.RowFileInformation;

public class PDIFileExcelReader implements FileReaderService {

	private String filename;
	private InputStream inputStream;
	private PoiWorkbook workbook;
	private List<CellInfo> rowInfo = null;
	private List<String> sheets = new ArrayList<String>();
	private Map<String, Map<Integer, CellInfo>> columns = new HashMap<String, Map<Integer, CellInfo>>();
	private Map<String, List<RowFileInformation>> dataAllSheets = new HashMap<String, List<RowFileInformation>>();
	
	public PDIFileExcelReader(String filename, InputStream inputStream) {
		this.filename = filename;
		this.inputStream = inputStream;
		read();
	}	

	@Override
	public void read() {
		try {
			workbook = new PoiWorkbook(inputStream, filename);
		} catch (KettleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<String> getSheets() {
		sheets =  Arrays.asList(workbook.getSheetNames());
		return sheets;
	}

	@Override
	public Map<String, Map<Integer, CellInfo>> getColumns() {
		try {
			Consumer<String> workSheetConsumer = new Consumer<String>() {
				@Override
				public void accept(String sheetName) {
					PoiSheet worksheet = (PoiSheet) workbook.getSheet(sheetName);
					columns.put(worksheet.getName(), getColumnNamesBySheet(worksheet));
				}
			};
			
			sheets.forEach(workSheetConsumer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return columns;
	}

	@Override
	public Map<String, List<RowFileInformation>> getData() {
		Consumer<String> workSheetConsumer = new Consumer<String>() {
			@Override
			public void accept(String sheetName) {
				PoiSheet worksheet = (PoiSheet) workbook.getSheet(sheetName);
				dataAllSheets.put(worksheet.getName(), getFileDataBySheet(worksheet));
			}
		};
		
		sheets.forEach(workSheetConsumer);
		return dataAllSheets;
	}

	private Map<Integer, CellInfo> getColumnNamesBySheet(PoiSheet worksheet) {
		Map<Integer, CellInfo> columnsBySheet = new HashMap<Integer, CellInfo>();
		
		KCell[] firstRow = worksheet.getRow(0);
		
		for(int i = 0; i < firstRow.length; i++) {
			CellInfo cellInfo = new CellInfo(readCellContent(firstRow[i]));
			columnsBySheet.put(i, cellInfo);
		}
		
		return columnsBySheet;
	}
	
	private List<RowFileInformation> getFileDataBySheet(PoiSheet worksheet) {
		List<RowFileInformation> dataBySheet = new ArrayList<RowFileInformation>();
		
		Consumer<KCell> cellConsumer = new Consumer<KCell>() {
			@Override
			public void accept(KCell cell) {
				CellInfo cellInfo = new CellInfo(readCellContent(cell));
				rowInfo.add(cellInfo);
			}
		};
		
		for(int i = 1; i < worksheet.getRows(); i++) {
			List<KCell> row = Arrays.asList(worksheet.getRow(i));
			rowInfo = new ArrayList<CellInfo>();
			row.forEach(cellConsumer);

			RowFileInformation rowFileInfo = new RowFileInformation(i, rowInfo);
			dataBySheet.add(rowFileInfo);
		}
		
		return dataBySheet;
	}
	
	private String readCellContent(KCell cell) {
		if(cell == null) {
			return "";
		}
		switch (cell.getType()) {
		case LABEL:
			return (String) cell.getValue();
		case NUMBER:
			return cell.getValue() + "";
		case DATE:
			return cell.getValue() + "";
		case BOOLEAN:
			return cell.getValue() + "";
		default:
			return "";
		}
	}
}
