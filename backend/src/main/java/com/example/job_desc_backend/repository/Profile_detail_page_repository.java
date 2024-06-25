package com.example.job_desc_backend.repository;

import com.example.job_desc_backend.model.Profile_detail_page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Profile_detail_page_repository extends MongoRepository<Profile_detail_page,String> {

    Profile_detail_page findByResumeId(String resumeId);
}
