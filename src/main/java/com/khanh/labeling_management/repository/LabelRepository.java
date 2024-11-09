package com.khanh.labeling_management.repository;

import com.khanh.labeling_management.entity.data_management.labeling_management.Label;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LabelRepository extends MongoRepository<Label, String> {
    Boolean existsByName(String name);

    Label findByName(String name);

    Optional<Label> getByName(String name);
}
