package com.example.job_desc_backend.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "BillPdf")
public class Billpdf {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private String Id;

        private String fileName;
        private String contentType;

        private String fileId;


}
