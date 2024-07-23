package com.example.job_desc_backend.repository;

import com.example.job_desc_backend.model.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProfileRepository extends MongoRepository<Profile,String> {
    List<Profile> findByJdId(String jdId);
    @Query("{ 'uploadDate' : { $gte: ?0, $lt: ?1 } }")
    List<Profile> findProfilesWithinDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'uploadDate' : { $gte: ?0 } }")
    List<Profile> findProfilesAddedToday(LocalDateTime todayStart);
}
