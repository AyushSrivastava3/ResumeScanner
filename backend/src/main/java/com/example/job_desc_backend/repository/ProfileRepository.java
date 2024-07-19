package com.example.job_desc_backend.repository;

import com.example.job_desc_backend.model.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends MongoRepository<Profile,String> {
    List<Profile> findByJdId(String jdId);
}
