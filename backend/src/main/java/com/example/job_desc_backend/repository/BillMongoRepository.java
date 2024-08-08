package com.example.job_desc_backend.repository;



import com.example.job_desc_backend.model.BillEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillMongoRepository extends MongoRepository<BillEntity, String> {
    long countByClientReimbursed(boolean clientReimbursed);

    long countByReimbursed(boolean reimbursed);

}
