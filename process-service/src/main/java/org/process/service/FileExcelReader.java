package org.process.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.process.model.CellInfo;
import org.process.model.RowFileInformation;
import org.springframework.beans.factory.annotation.Autowired;

public class FileExcelReader implements FileReaderService {
	
	private String filename;
	private InputStream inputStream;
	private Workbook workbook;
	
	private List<String> sheets = new ArrayList<String>();
	private Map<String, Map<Integer, CellInfo>> columns = new HashMap<String, Map<Integer, CellInfo>>();
	private Map<String, List<RowFileInformation>> dataAllSheets = new HashMap<String, List<RowFileInformation>>();

	@Autowired
	public FileExcelReader(String filename, InputStream inputStream) {
		super();
		this.filename = filename;
		this.inputStream = inputStream;
		read();
	}
	
	@Override
	public void read(){
		try {
			workbook = WorkbookFactory.create(inputStream);

		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 

	public List<String> getSheets() {
		try {
			workbook.forEach(sheet -> {
				sheets.add(sheet.getSheetName());
			});

		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sheets;
	}

	public Map<String, Map<Integer, CellInfo>> getColumns() {
		try {
			workbook.forEach(sheet -> {
				columns.put(sheet.getSheetName(), getColumnsBySheet(sheet));
			});
			
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return columns;
	}

	public Map<String, List<RowFileInformation>> getData() {

		try {
			workbook.forEach(sheet -> {
				dataAllSheets.put(sheet.getSheetName(), getFileDataBySheet(sheet));
			});
			
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataAllSheets;
	}

	private Map<Integer, CellInfo> getColumnsBySheet(Sheet sheet) {
		Map<Integer, CellInfo> columnsBySheet = new HashMap<Integer, CellInfo>();

		sheet.getRow(0).forEach(cell -> {
			CellInfo cellInfo = new CellInfo(readCellContent(cell));
			columnsBySheet.put(cell.getColumnIndex(), cellInfo);
		});

		return columnsBySheet;
	}
	
	private List<RowFileInformation> getFileDataBySheet(Sheet sheet) {
		List<RowFileInformation> dataBySheet = new ArrayList<RowFileInformation>();

		sheet.forEach(row -> {
			dataBySheet.add(getRowData(row));
		});

		return dataBySheet;
	}

	private RowFileInformation getRowData(Row row) {
		List<CellInfo> rowInfo = new ArrayList<CellInfo>();

		row.forEach(cell -> {
			CellInfo cellInfo = new CellInfo(readCellContent(cell));
			rowInfo.add(cellInfo);
		});

		return new RowFileInformation(row.getRowNum(), rowInfo);
	}

	private String readCellContent(Cell cell) {
		switch (cell.getCellTypeEnum()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue() + "";
			} else {
				return cell.getNumericCellValue() + "";
			}
		case BOOLEAN:
			return cell.getBooleanCellValue() + "";
		case FORMULA:
			return cell.getCellFormula() + "";
		default:
			return "";
		}
	}
}
