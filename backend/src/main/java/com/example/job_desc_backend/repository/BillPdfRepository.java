package com.example.job_desc_backend.repository;

import com.example.job_desc_backend.model.Billpdf;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillPdfRepository extends MongoRepository<Billpdf,String> {
    Optional<Billpdf> findByFileId(String fileId);

}
