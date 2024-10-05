package com.example.job_desc_backend.repository;

import com.example.job_desc_backend.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {
    // You can define custom queries if needed
}
