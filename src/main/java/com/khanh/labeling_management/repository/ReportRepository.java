package com.khanh.labeling_management.repository;

import com.khanh.labeling_management.entity.Report;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportRepository extends MongoRepository<Report, String> {
}
