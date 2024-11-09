package com.khanh.labeling_management.repository;

import com.khanh.labeling_management.entity.data_management.labeling_management.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<Project, String> {
}
