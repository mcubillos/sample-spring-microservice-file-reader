package org.upload.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.upload.model.RequestInfo;
import org.upload.service.StorageService;


@RestController
public class UploadController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);
	
	@Autowired
	private StorageService storageService;
	
	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@RequestBody RequestInfo uploadRequest) throws IOException, URISyntaxException {
		
		if (uploadRequest == null || uploadRequest.getFilePath().isEmpty()) {
			return new ResponseEntity<String>("You need a file for this operation", HttpStatus.BAD_REQUEST);
		}
		
		LOGGER.info("Uploading file with name " + uploadRequest.getFilePath());
		storageService.storeByFilePath(uploadRequest.getFilePath());
		return new ResponseEntity<String>("File saved", HttpStatus.OK);
	}
	
	@GetMapping("/files/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
