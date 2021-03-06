package org.process.service;

import java.io.BufferedReader;
import java.io.IOException;
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
import org.process.repository.RowFileInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class FileCsvReader implements FileReaderService {

	@Autowired
	private RowFileInformationRepository repositoryRows;
	
	public FileCsvReader(RowFileInformationRepository repositoryRows) {
		super();
		this.repositoryRows = repositoryRows;
	}

	@Override
	public Map<Integer, CellInfo> getColumns(InputStream inputStream) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, List<RowFileInformation>> getData(String filename, InputStream inputStream) {
		Map<String, List<RowFileInformation>> inputList = new HashMap<String, List<RowFileInformation>>();
		inputList.put(filename, new ArrayList<RowFileInformation>());
		AtomicInteger atomicInt = new AtomicInteger(0);
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			// skip the header of the csv
			List<List<String>> values = br.lines().map(line -> Arrays.asList(line.split(",")))
					.collect(Collectors.toList());
			values.forEach(value -> {
				inputList.get(filename).add(getRowData(filename, value, atomicInt.getAndIncrement()));
			});
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputList;
	}
	
	public RowFileInformation getRowData(String fileName, List<String> value, int rowId) {
		
		List<CellInfo> rowInfo = new ArrayList<CellInfo>();
		
		value.forEach(eachValue -> {
			CellInfo cellInfo = new CellInfo(eachValue);
			rowInfo.add(cellInfo);
			
		});
		
		RowFileInformation rowFileInfo = new RowFileInformation(fileName, fileName, rowId, rowInfo);
		repositoryRows.save(rowFileInfo);
		return rowFileInfo;
	}

}
