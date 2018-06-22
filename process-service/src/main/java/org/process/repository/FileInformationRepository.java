package org.process.repository;

import org.process.model.FileInformation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface  FileInformationRepository extends MongoRepository<FileInformation, String> {

}