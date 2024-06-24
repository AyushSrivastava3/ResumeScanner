package com.example.job_desc_backend.repository;

import com.example.job_desc_backend.model.Skill_Experience;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Skill_ExperienceRepository extends MongoRepository<Skill_Experience,String> {
    Optional<Skill_Experience> findByResumeId(String resumeId);
}
