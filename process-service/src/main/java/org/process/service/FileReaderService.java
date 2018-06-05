package org.process.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.process.model.CellInfo;
import org.process.model.RowFileInformation;

public interface FileReaderService {

	 Map<Integer, CellInfo> getColumns(InputStream inputStream);
	 
	 Map<String, List<RowFileInformation>> getData(String filename, InputStream inputStream);
}
