package com.example.job_desc_backend.repository;

import com.example.job_desc_backend.model.Job_description;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JdRepository extends MongoRepository<Job_description,String> {
}
