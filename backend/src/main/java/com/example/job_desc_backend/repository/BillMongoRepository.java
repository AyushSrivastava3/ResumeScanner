//package com.example.job_desc_backend.repository;
//
//import com.example.job_desc_backend.model.BillEntity;
//import org.springframework.data.mongodb.repository.MongoRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface BillMongoRepository extends MongoRepository<BillEntity,String> {
//    long countByClientReimbursed(boolean clientReimbursed);
//
//    long countByReimbursed(boolean reimbursed);
//
//
//
//    // New method to find all bills based on client reimbursement status
//    List<BillEntity> findByClientReimbursed(boolean clientReimbursed);
//
//    // New method to find all bills based on company reimbursement status
//    List<BillEntity> findByReimbursed(boolean reimbursed);
//
//
//}




package com.example.job_desc_backend.repository;



import com.example.job_desc_backend.model.BillEntity;
import com.example.job_desc_backend.model.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BillMongoRepository extends MongoRepository<BillEntity, String> {
    long countByClientReimbursed(boolean clientReimbursed);

    long countByReimbursed(boolean reimbursed);

    @Query("{ 'date' : { $gte: ?0, $lt: ?1 } }")
    List<BillEntity> findBillWithinDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'date' : { $gte: ?0 } }")
    List<BillEntity> findBillsAddedToday(LocalDateTime todayStart);

    // New method to find all bills based on client reimbursement status
    List<BillEntity> findByClientReimbursed(boolean clientReimbursed);

    // New method to find all bills based on company reimbursement status
    List<BillEntity> findByReimbursed(boolean reimbursed);

}