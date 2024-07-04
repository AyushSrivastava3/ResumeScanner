package com.example.job_desc_backend.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document
public class ForgetPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private Integer otp;
    private Date expirationDate;
    private String userId;

}
