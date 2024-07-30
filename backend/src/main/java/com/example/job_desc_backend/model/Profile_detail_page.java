package com.example.job_desc_backend.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document
public class Profile_detail_page {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String resumeId;
    private String candidateName;
    private String uploadedDate;
    private String createdBy;
    private Map<String,Integer> profile_skill_map;

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(String uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public Map<String, Integer> getProfile_skill_map() {
        return profile_skill_map;
    }

    public void setProfile_skill_map(Map<String, Integer> profile_skill_map) {
        this.profile_skill_map = profile_skill_map;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResumeId() {
        return resumeId;
    }

    public void setResumeId(String resumeId) {
        this.resumeId = resumeId;
    }
}
