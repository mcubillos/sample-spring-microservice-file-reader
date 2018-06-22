package org.process.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.process.model.CellInfo;
import org.process.model.RowFileInformation;
import com.aspose.cells.Cell;
import com.aspose.cells.CellValueType;
import com.aspose.cells.Cells;
import com.aspose.cells.DeleteOptions;
import com.aspose.cells.Row;
import com.aspose.cells.RowCollection;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;

public class AsposeFileExcelReader implements FileReaderService {

	private String filename;
	private InputStream inputStream;
	private Workbook workbook;
	private WorksheetCollection worksheetCollection;
	
	private List<CellInfo> rowInfo = null;
	private List<String> sheets = new ArrayList<String>();
	private Map<String, Map<Integer, CellInfo>> columns = new HashMap<String, Map<Integer, CellInfo>>();
	private Map<String, List<RowFileInformation>> dataAllSheets = new HashMap<String, List<RowFileInformation>>();

	public AsposeFileExcelReader(String filename, InputStream inputStream) {
		super();
		this.filename = filename;
		this.inputStream = inputStream;
		read();
	}

	@Override
	public void read() {
		try {
			workbook = new Workbook(inputStream);
			worksheetCollection = workbook.getWorksheets();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<String> getSheets() {
		try {
			Consumer<Worksheet> workSheetConsumer = new Consumer<Worksheet>() {
				@Override
				public void accept(Worksheet worksheet) {
					sheets.add(worksheet.getName());
				}
			};
			
			worksheetCollection.forEach(workSheetConsumer);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sheets;
	}

	@Override
	public Map<String, Map<Integer, CellInfo>> getColumns() {
		try {
			Consumer<Worksheet> workSheetConsumer = new Consumer<Worksheet>() {
				@Override
				public void accept(Worksheet worksheet) {
					columns.put(worksheet.getName(), getColumnNamesBySheet(worksheet));
				}
			};
			
			worksheetCollection.forEach(workSheetConsumer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return columns;
	}

	@Override
	public Map<String, List<RowFileInformation>> getData() {
		try {
			Consumer<Worksheet> workSheetConsumer = new Consumer<Worksheet>() {
				@Override
				public void accept(Worksheet worksheet) {
					dataAllSheets.put(worksheet.getName(), getFileDataBySheet(worksheet));
				}
			};
			
			worksheetCollection.forEach(workSheetConsumer);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dataAllSheets;
	}

	private void deleteEmptyCells(Cells cells) {
		//Create an instance of DeleteOptions class
		DeleteOptions options = new DeleteOptions();
		//Set UpdateReference property to true;
		options.setUpdateReference(true);

		//Delete all blank rows and columns
		cells.deleteBlankColumns(options);
		cells.deleteBlankRows(options);
	}
	
	private List<RowFileInformation> getFileDataBySheet(Worksheet worksheet) {
		List<RowFileInformation> dataBySheet = new ArrayList<RowFileInformation>();
		
		Cells cells = worksheet.getCells();
		deleteEmptyCells(cells);
		
		RowCollection rows = cells.getRows();
		rows.removeAt(0);
		
		Consumer<Cell> cellConsumer = new Consumer<Cell>() {
			@Override
			public void accept(Cell cell) {
				CellInfo cellInfo = new CellInfo(readCellContent(cell));
				rowInfo.add(cellInfo);
			}
		};
		
		Consumer<Row> rowConsumer = new Consumer<Row>() {
			@Override
			public void accept(Row row) {
				rowInfo = new ArrayList<CellInfo>();
				row.forEach(cellConsumer);

				RowFileInformation rowFileInfo = new RowFileInformation(row.getIndex(), rowInfo);
				dataBySheet.add(rowFileInfo);
			}
		};
		
		rows.forEach(rowConsumer);
		return dataBySheet;
	}
	
	private Map<Integer, CellInfo> getColumnNamesBySheet(Worksheet worksheet) {
		Map<Integer, CellInfo> columnsBySheet = new HashMap<Integer, CellInfo>();

		Cells cells = worksheet.getCells();
		deleteEmptyCells(cells);
		
		Row firstRow = cells.getRows().get(0);
		
		Consumer<Cell> cellConsumer = new Consumer<Cell>() {
			@Override
			public void accept(Cell cell) {
				CellInfo cellInfo = new CellInfo(readCellContent(cell));
				columnsBySheet.put(cell.getColumn(), cellInfo);
			}
		};

		firstRow.forEach(cellConsumer);
		return columnsBySheet;
	}

	private String readCellContent(Cell cell) {
		switch (cell.getType()) {
		case CellValueType.IS_STRING:
			return cell.getStringValue();
		case CellValueType.IS_NUMERIC:
			return cell.getFloatValue() + "";
		case CellValueType.IS_DATE_TIME:
			return cell.getDateTimeValue() + "";
		case CellValueType.IS_BOOL:
			return cell.getBoolValue() + "";
		default:
			if (cell.isFormula()) {
				return cell.getFormula() + "";
			}
			return "";
		}
	}
}
