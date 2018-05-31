package org.process.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "upload-service")
public interface StorageServiceClient {
	@RequestMapping(method = RequestMethod.GET, value = "/files/{filename:.+}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Resource getFile(String filename);
}
