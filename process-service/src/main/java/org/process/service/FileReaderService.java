package org.process.service;

import java.util.List;
import java.util.Map;

import org.process.model.CellInfo;
import org.process.model.RowFileInformation;

public interface FileReaderService {

	void read();

	Map<String, Map<Integer, CellInfo>> getColumns();

	Map<String, List<RowFileInformation>> getData();
}
