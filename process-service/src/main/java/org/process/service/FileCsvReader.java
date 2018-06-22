package org.process.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.process.model.CellInfo;
import org.process.model.RowFileInformation;

public class FileCsvReader implements FileReaderService {

	private String filename;
	private InputStream inputStream;
	private List<List<String>> values;
	private Map<String, Map<Integer, CellInfo>> columns = new HashMap<String, Map<Integer, CellInfo>>();
	private Map<String, List<RowFileInformation>> inputList = new HashMap<String, List<RowFileInformation>>();

	public FileCsvReader(String filename, InputStream inputStream) {
		super();
		this.filename = filename;
		this.inputStream = inputStream;
		read();
	}

	@Override
	public void read() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			values = br.lines().map(line -> Arrays.asList(line.split(","))).collect(Collectors.toList());
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block

		}
	}

	@Override
	public Map<String, Map<Integer, CellInfo>> getColumns() {

		AtomicInteger colummnId = new AtomicInteger(0);
		columns.put(filename, new HashMap<Integer, CellInfo>());

		List<String> columnsRow = values.remove(0);

		columnsRow.forEach(value -> {
			CellInfo cellInfo = new CellInfo(value);
			columns.get(filename).put(new Integer(colummnId.getAndIncrement()), cellInfo);
		});

		return columns;
	}

	@Override
	public Map<String, List<RowFileInformation>> getData() {
		inputList.put(filename, new ArrayList<RowFileInformation>());
		AtomicInteger atomicInt = new AtomicInteger(0);

		values.forEach(value -> {
			inputList.get(filename).add(getRowData(value, atomicInt.getAndIncrement()));
		});

		return inputList;
	}

	private RowFileInformation getRowData(List<String> value, int rowId) {
		List<CellInfo> rowInfo = new ArrayList<CellInfo>();

		value.forEach(eachValue -> {
			CellInfo cellInfo = new CellInfo(eachValue);
			rowInfo.add(cellInfo);

		});
		return new RowFileInformation(rowId, rowInfo);
	}
}
