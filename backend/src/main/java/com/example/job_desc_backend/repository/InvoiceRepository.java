package com.example.job_desc_backend.repository;
import com.example.job_desc_backend.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
@Repository
public interface InvoiceRepository extends MongoRepository<Invoice,String> {
    @Query("{ 'status' : 'pending' }")
    List<Invoice> findPendingInvoices();

    List<Invoice> findByRaisedOnBetween(LocalDate start, LocalDate end);

    @Query("{ 'timeline': { $lt: ?0 }, 'raisedOn': { $lte: ?0 }, 'status': { $ne: 'completed' } }")
    List<Invoice> findPendingInvoices(String currentDate);
}