package com.example.job_desc_backend.repository;

import com.example.job_desc_backend.model.ForgetPassword;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgetPasswordRepository extends MongoRepository<ForgetPassword,String> {
    ForgetPassword findByOtpAndUserId(Integer otp, String userId);
}
