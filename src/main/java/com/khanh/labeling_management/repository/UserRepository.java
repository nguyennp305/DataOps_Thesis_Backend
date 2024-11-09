package com.khanh.labeling_management.repository;

import com.khanh.labeling_management.entity.information_management.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Boolean existsByUsername(String username);

    List<User> findAllByIdIn(List<String> ids);

    Optional<User> findByUsername(String username);

}
