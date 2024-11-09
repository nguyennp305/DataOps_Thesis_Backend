package com.khanh.labeling_management.repository;

import com.khanh.labeling_management.entity.data_management.labeling_management.Data;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DataRepository extends MongoRepository<Data, String> {
}
