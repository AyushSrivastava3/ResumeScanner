package com.example.job_desc_backend.repository;

import com.example.job_desc_backend.model.Job_description;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JdRepository extends MongoRepository<Job_description,String> {
    //boolean existsByTitle(String title);
    boolean existsByTitleRegexIgnoreCase(String title);
    @Query("{ 'createdDate' : { $gte: ?0, $lt: ?1 } }")
    List<Job_description> findJobDescriptionsWithinDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'createdDate' : { $gte: ?0 } }")
    List<Job_description> findJobDescriptionsAddedToday(LocalDateTime todayStart);
}
