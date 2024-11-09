package com.khanh.labeling_management.repository;

import com.khanh.labeling_management.entity.information_management.Enterprise;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EnterpriseRepository extends MongoRepository<Enterprise, String> {

    List<Enterprise> findAllByIdIn(List<String> ids);
    Optional<Enterprise> findByName(String name);

    Boolean existsByName(String name);


}
