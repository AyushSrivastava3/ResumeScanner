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
        private String id; // changed from Id to id

        private String fileName;
        private String contentType;
        private String fileId;

        // Getters and Setters
        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getFileName() {
                return fileName;
        }

        public void setFileName(String fileName) {
                this.fileName = fileName;
        }

        public String getContentType() {
                return contentType;
        }

        public void setContentType(String contentType) {
                this.contentType = contentType;
        }

        public String getFileId() {
                return fileId;
        }

        public void setFileId(String fileId) {
                this.fileId = fileId;
        }

    public void setCreatedBy(String username) {
    }
}
