package org.process.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.poi.EncryptedDocumentException;
import org.process.model.CSVFileInformation;
import org.process.model.ExcelFileInformation;
import org.process.model.RequestInfo;
import org.process.repository.RowFileInformationRepository;
import org.process.service.AsposeFileExcelReader;
import org.process.service.FileCsvReader;
import org.process.service.FileExcelReader;
import org.process.service.FileReaderService;
import org.process.client.StorageServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessController.class);
	
	@Autowired
	private FileReaderService fileReaderService;
	@Autowired
	private RowFileInformationRepository repositoryRows;
	@Autowired
	private StorageServiceClient storageServiceClient;
	
	@PostMapping("/processExcel")
	public ResponseEntity<ExcelFileInformation> porcessExcelFile(@RequestBody RequestInfo processRequest) throws IOException, URISyntaxException {
		if (processRequest == null || processRequest.getFilePath().isEmpty()) {
			LOGGER.info("Not file name found.");
			return new ResponseEntity<ExcelFileInformation>(HttpStatus.BAD_REQUEST);
		}
		LOGGER.info("Searching excel file ...");
		Resource file = storageServiceClient.getFile(processRequest.getFilePath());
		ExcelFileInformation excel = new ExcelFileInformation();

		try {
			fileReaderService = new FileExcelReader(repositoryRows);
			excel.setColumns(fileReaderService.getColumns(file.getInputStream()));
			excel.setData(fileReaderService.getData(file.getFilename(), file.getInputStream()));
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<ExcelFileInformation>(excel, HttpStatus.OK);
	}
	
	@PostMapping("/processExcelAspose")
	public ResponseEntity<ExcelFileInformation> porcessAsposeExcelFile(@RequestBody RequestInfo processRequest) throws IOException, URISyntaxException {
		if (processRequest == null || processRequest.getFilePath().isEmpty()) {
			LOGGER.info("Not file name found.");
			return new ResponseEntity<ExcelFileInformation>(HttpStatus.BAD_REQUEST);
		}
		LOGGER.info("Searching excel file ...");
		Resource file = storageServiceClient.getFile(processRequest.getFilePath());
		ExcelFileInformation excel = new ExcelFileInformation();

		try {
			fileReaderService = new AsposeFileExcelReader(repositoryRows);
			excel.setColumns(fileReaderService.getColumns(file.getInputStream()));
			excel.setData(fileReaderService.getData(file.getFilename(), file.getInputStream()));
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<ExcelFileInformation>(excel, HttpStatus.OK);
	}
	
	@PostMapping("/processCsv")
	public ResponseEntity<CSVFileInformation> porcessCsvFile(@RequestBody RequestInfo processRequest) throws IOException, URISyntaxException {
		if (processRequest == null || processRequest.getFilePath().isEmpty()) {
			LOGGER.info("Not file name found.");
			return new ResponseEntity<CSVFileInformation>(HttpStatus.BAD_REQUEST);
		}
		LOGGER.info("Searching csv file ...");
		Resource file = storageServiceClient.getFile(processRequest.getFilePath());
		CSVFileInformation csv = new CSVFileInformation();
		
		try {
			fileReaderService = new FileCsvReader(repositoryRows);
			csv.setColumns(fileReaderService.getColumns(file.getInputStream()));
			csv.setData(fileReaderService.getData(file.getFilename(), file.getInputStream()));
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ResponseEntity<CSVFileInformation>(csv, HttpStatus.OK);
	}
}