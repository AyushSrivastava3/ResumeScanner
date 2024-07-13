package com.example.job_desc_backend.repository;

import com.example.job_desc_backend.model.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ResumeRepository extends MongoRepository<Resume, String> {
    Optional<Resume> findByFileId(String fileId);

    Optional<Resume> findByEmailId(String email);
}
