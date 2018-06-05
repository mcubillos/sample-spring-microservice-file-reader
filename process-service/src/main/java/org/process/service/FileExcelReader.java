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
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.process.model.CellInfo;
import org.process.model.RowFileInformation;
import org.process.repository.RowFileInformationRepository;
import org.springframework.stereotype.Service;

@Service
public class FileExcelReader implements FileReaderService {

	private RowFileInformationRepository repositoryRows;
	
	private Workbook workbook;
	private List<String> sheets = new ArrayList<String>();
	private Map<Integer, CellInfo> columns = new HashMap<Integer, CellInfo>();
	private Map<String,List<RowFileInformation>> dataAllSheets = new HashMap<String, List<RowFileInformation>>();

	public FileExcelReader(RowFileInformationRepository repositoryRows) {
		super();
		this.repositoryRows = repositoryRows;
	}

	public List<String> getSheets(InputStream inputStream) {
		try {
			workbook = WorkbookFactory.create(inputStream);
			workbook.forEach(sheet -> {
				sheets.add(sheet.getSheetName());
			});
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sheets;
	}

	public Map<Integer, CellInfo> getColumns(InputStream inputStream) {
		try {
			workbook = WorkbookFactory.create(inputStream);
			workbook.forEach(sheet -> {
				columns.putAll(getColumnsBySheetName(sheet.getSheetName()));
			});
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return columns;
	}

	public Map<Integer, CellInfo> getColumnsBySheetName(String name) {
		Map<Integer, CellInfo> columnsBySheet = new HashMap<Integer, CellInfo>();

		workbook.getSheet(name).getRow(0).forEach(cell -> {
			CellInfo cellInfo = new CellInfo(readCellContent(cell));
			columnsBySheet.put(cell.getColumnIndex(), cellInfo);
		});

		return columnsBySheet;
	}

	public Map<String, List<RowFileInformation>> getData(String filename, InputStream inputStream) {
		
		try {
			workbook = WorkbookFactory.create(inputStream);
			workbook.forEach(sheet -> {
				dataAllSheets.put(sheet.getSheetName(), getFileDataBySheetName(filename, sheet.getSheetName()));
			});
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataAllSheets;
	}

	public List<RowFileInformation> getFileDataBySheetName(String fileName, String sheetName) {
		List<RowFileInformation> dataBySheet = new ArrayList<RowFileInformation>();

		workbook.getSheet(sheetName).forEach(row -> {
			dataBySheet.add(getRowData(fileName, sheetName, row.getRowNum()));
		});

		return dataBySheet;
	}

	public RowFileInformation getRowData(String fileName, String sheetName, int rowId) {
		List<CellInfo> rowInfo = new ArrayList<CellInfo>();
		
		workbook.getSheet(sheetName).getRow(rowId).forEach(cell -> {
			CellInfo cellInfo = new CellInfo(readCellContent(cell));
			rowInfo.add(cellInfo);
		});

		RowFileInformation rowFileInfo = new RowFileInformation(fileName, sheetName, rowId, rowInfo);
		repositoryRows.save(rowFileInfo);
		return rowFileInfo;
	}

	private String readCellContent(Cell cell) {
		String content;
		switch (cell.getCellTypeEnum()) {
		case STRING:
			content = cell.getStringCellValue();
			break;
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				content = cell.getDateCellValue() + "";
			} else {
				content = cell.getNumericCellValue() + "";
			}
			break;
		case BOOLEAN:
			content = cell.getBooleanCellValue() + "";
			break;
		case FORMULA:
			content = cell.getCellFormula() + "";
			break;
		default:
			content = "";
		}
		return content;
	}

}
