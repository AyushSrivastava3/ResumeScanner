package com.example.job_desc_backend.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class Profile_detail_page_response_dto {
    private String resumeId;
    private String candidateName;
    private String uploadedDate;
    private Map<String,Integer> profile_skill_map;

    public String getResumeId() {
        return resumeId;
    }

    public void setResumeId(String resumeId) {
        this.resumeId = resumeId;
    }

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
}
