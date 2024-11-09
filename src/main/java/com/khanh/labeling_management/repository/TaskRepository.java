package com.khanh.labeling_management.repository;

import com.khanh.labeling_management.entity.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, String> {
}
