package org.process.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.process.model.CellInfo;
import org.process.model.RowFileInformation;
import org.process.repository.RowFileInformationRepository;

import com.aspose.cells.Cell;
import com.aspose.cells.CellValueType;
import com.aspose.cells.Cells;
import com.aspose.cells.Range;
import com.aspose.cells.Row;
import com.aspose.cells.RowCollection;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.aspose.cells.WorksheetCollection;

public class AsposeFileExcelReader implements FileReaderService {

	private RowFileInformationRepository repositoryRows;

	private Workbook workbook;
	private List<String> sheets = new ArrayList<String>();
	private Map<Integer, CellInfo> columns = new HashMap<Integer, CellInfo>();
	private Map<String, List<RowFileInformation>> dataAllSheets = new HashMap<String, List<RowFileInformation>>();

	public AsposeFileExcelReader(RowFileInformationRepository repositoryRows) {
		super();
		this.repositoryRows = repositoryRows;
	}

	public List<String> getSheets(InputStream inputStream) {
		try {
			workbook = new Workbook(inputStream);
			WorksheetCollection worksheetCollection = workbook.getWorksheets();

			for (int j = 0; j < worksheetCollection.getCount(); j++) {
				sheets.add(worksheetCollection.get(j).getName());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sheets;
	}

	@Override
	public Map<Integer, CellInfo> getColumns(InputStream inputStream) {
		try {
			workbook = new Workbook(inputStream);
			WorksheetCollection worksheetCollection = workbook.getWorksheets();

			for (int j = 0; j < worksheetCollection.getCount(); j++) {
				columns.putAll(getColumnNamesBySheetName(worksheetCollection.get(j).getName()));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return columns;
	}

	@Override
	public Map<String, List<RowFileInformation>> getData(String filename, InputStream inputStream) {
		try {
			workbook = new Workbook(inputStream);
			WorksheetCollection worksheetCollection = workbook.getWorksheets();

			for (int j = 0; j < worksheetCollection.getCount(); j++) {
				dataAllSheets.put(worksheetCollection.get(j).getName(), getFileDataBySheetName(filename, worksheetCollection.get(j).getName()));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dataAllSheets;
	}

	public List<RowFileInformation> getFileDataBySheetName(String fileName, String sheetName) {
		List<RowFileInformation> dataBySheet = new ArrayList<RowFileInformation>();

		Worksheet worksheet = workbook.getWorksheets().get(sheetName);
		Cells cells = worksheet.getCells();

		// Access the Maximum Display Range
		Range range = worksheet.getCells().getMaxDisplayRange();
		int tcols = range.getColumnCount();
		
		RowCollection rows = cells.getRows();
		
		for (int i = 0 ; i < rows.getCount() ; i++)
		{
			List<CellInfo> rowInfo = new ArrayList<CellInfo>();
			
			for (int j = 0 ; j < tcols ; j++)
			{
				CellInfo cellInfo = new CellInfo(readCellContent(cells.get(i,j)));
				rowInfo.add(cellInfo);
			}
			RowFileInformation rowFileInfo = new RowFileInformation(fileName, sheetName, i, rowInfo);
			repositoryRows.save(rowFileInfo);
			dataBySheet.add(rowFileInfo);
		}

		return dataBySheet;
	}
	
	public Map<Integer, CellInfo> getColumnNamesBySheetName(String name) {
		Map<Integer, CellInfo> columnsBySheet = new HashMap<Integer, CellInfo>();

		Worksheet worksheet = workbook.getWorksheets().get(name);
		Cells cells = worksheet.getCells();

		// Access the Maximum Display Range
		Range range = worksheet.getCells().getMaxDisplayRange();
		int tcols = range.getColumnCount();

		Row firstRow = cells.getRows().get(0);

		for (int j = 0; j < tcols; j++) {
			Cell cell = firstRow.get(j);
			CellInfo cellInfo = new CellInfo(readCellContent(cell));
			columnsBySheet.put(cell.getColumn(), cellInfo);
		}

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
