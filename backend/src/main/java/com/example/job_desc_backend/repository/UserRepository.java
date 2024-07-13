package com.example.job_desc_backend.repository;

import com.example.job_desc_backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    User findByUsername(String user);

    User findByEmail(String email);

    void deleteByUsername(String name);
}
