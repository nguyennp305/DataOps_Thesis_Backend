package com.khanh.labeling_management.repository;

import com.khanh.labeling_management.entity.information_management.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role,String> {

    Boolean existsByName(String name);

}
