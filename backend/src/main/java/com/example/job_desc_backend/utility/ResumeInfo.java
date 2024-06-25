package com.example.job_desc_backend.utility;

import java.util.Map;

public class ResumeInfo {
    private String id;
    private String name;
    private int overallPercentage;
    private Map<String,Integer> percentages;
    private String uploadedDate;

    public ResumeInfo(String id, String name,int overallPercentage,Map<String,Integer> percentages,String uploadedDate) {
        this.id = id;
        this.name = name;
        this.overallPercentage=overallPercentage;
        this.percentages=percentages;
        this.uploadedDate=uploadedDate;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOverallPercentage() {
        return overallPercentage;
    }

    public void setOverallPercentage(int overallPercentage) {
        this.overallPercentage = overallPercentage;
    }

    public Map<String, Integer> getPercentages() {
        return percentages;
    }

    public void setPercentages(Map<String, Integer> percentages) {
        this.percentages = percentages;
    }

    public String getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(String uploadedDate) {
        this.uploadedDate = uploadedDate;
    }
}
