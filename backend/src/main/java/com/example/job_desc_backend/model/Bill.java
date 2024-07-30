package com.example.job_desc_backend.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Bill")
public class Bill {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private String id;
        private String reason;
        private double amount;
        private String date;
        private String category;
        private boolean reimbursed;
        private String submittedBy;
        private String comments;
        private boolean clientReimbursed;
        private String reimbursementDate;

        private String fileId;
        private String createdBy;



}
