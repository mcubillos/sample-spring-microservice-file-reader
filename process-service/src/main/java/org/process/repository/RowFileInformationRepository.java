package org.process.repository;

import org.process.model.RowFileInformation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface  RowFileInformationRepository extends MongoRepository<RowFileInformation, String> {

}