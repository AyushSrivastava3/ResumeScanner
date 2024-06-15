package com.example.job_desc_backend.repository;

import com.example.job_desc_backend.model.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResumeRepository extends MongoRepository<Resume, String> {
}
