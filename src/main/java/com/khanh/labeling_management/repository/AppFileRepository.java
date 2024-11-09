package com.khanh.labeling_management.repository;

import com.khanh.labeling_management.entity.AppFile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.File;
import java.util.Optional;

public interface AppFileRepository extends MongoRepository<AppFile, String> {

    Optional<AppFile> findByName(String name);

}
