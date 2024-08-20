package com.example.job_desc_backend.repository;

import com.example.job_desc_backend.model.Client;
import com.example.job_desc_backend.model.Job_description;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClientRepository extends MongoRepository<Client,String> {
//    @Query("{ 'createdDate': { $gte: ?0, $lt: ?1 } }")
//    List<Client> findClientsCreatedToday(LocalDate startOfDay, LocalDate endOfDay);

    @Query("{ 'createdDate' : { $gte: ?0, $lt: ?1 } }")
    List<Client> findClientWithinDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'createdDate' : { $gte: ?0 } }")
    List<Client> findClientAddedToday(LocalDateTime todayStart);
}