package com.khanh.labeling_management.repository;

import com.khanh.labeling_management.entity.data_management.labeling_management.Dataset;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DatasetRepository extends MongoRepository<Dataset, String> {
}
