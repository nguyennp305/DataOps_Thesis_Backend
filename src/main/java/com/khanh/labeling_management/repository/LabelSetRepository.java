package com.khanh.labeling_management.repository;

import com.khanh.labeling_management.entity.data_management.labeling_management.LabelSet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LabelSetRepository extends MongoRepository<LabelSet, String> {
}
