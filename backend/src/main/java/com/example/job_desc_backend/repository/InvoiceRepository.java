package com.example.job_desc_backend.repository;
import com.example.job_desc_backend.model.Client;
import com.example.job_desc_backend.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
@Repository
public interface InvoiceRepository extends MongoRepository<Invoice,String> {
    @Query("{ 'status' : 'pending' }")
    List<Invoice> findPendingInvoices();

    List<Invoice> findByRaisedOnBetween(LocalDate start, LocalDate end);

    @Query("{ 'timeline': { $lt: ?0 }, 'raisedOn': { $lte: ?0 }, 'status': { $ne: 'completed' } }")
    List<Invoice> findPendingInvoices(String currentDate);

    @Query("{ 'createdDate' : { $gte: ?0, $lt: ?1 } }")
    List<Invoice> findInvoiceWithinDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'createdDate' : { $gte: ?0 } }")
    List<Invoice> findInvoiceAddedToday(LocalDateTime todayStart);

    @Query("{ 'raisedOn': { $gte: ?0-01-01T00:00:00.000Z, $lt: ?0-12-31T23:59:59.999Z } }")
    List<Invoice> findAllByRaisedOnYear(int year);

    @Query("{ 'raisedOn': { $gte: ?0-?1-01T00:00:00.000Z, $lt: ?0-?1-31T23:59:59.999Z } }")
    List<Invoice> findAllByRaisedOnMonth(int year, int month);
    @Query("{ 'raisedOn': { $gte: ?0, $lt: ?1 } }")
    List<Invoice> findByRaisedOnBetween(String startDate, String endDate);







}