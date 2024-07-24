package com.example.job_desc_backend.repository;

import com.example.job_desc_backend.model.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends MongoRepository<Bill,String> {
    long countByClientReimbursed(boolean clientReimbursed);

    long countByReimbursed(boolean reimbursed);

}
