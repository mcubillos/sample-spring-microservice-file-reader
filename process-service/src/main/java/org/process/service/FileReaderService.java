package org.process.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.process.model.CellInfo;
import org.process.model.RowFileInformation;

public interface FileReaderService {

	 Map<Integer, CellInfo> getColumns(File file);
	 
	 Map<String, List<RowFileInformation>> getData(File file);
}
