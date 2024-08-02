package com.example.job_desc_backend.repository;

import com.example.job_desc_backend.model.ProfileSection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface ProfileSectionRepository extends MongoRepository<ProfileSection,String> {
    ProfileSection findByProfileId(String profileId);
}
