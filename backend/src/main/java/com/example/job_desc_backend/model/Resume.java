package com.example.job_desc_backend.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "resumes")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String candidateName;
    private String emailId;
    private String fileName;
    private String contentType;
    private String fileId;
    private LocalDate uploadDate;
    // New fields to store section data
    @Lob
    private String experienceSection;
    @Lob
    private String educationSection;
    @Lob
    private String skillsSection;
    @Lob
    private String projectsSection;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    private String createdBy;

    @Lob
    private String IntroductionSection;
    @Lob
    private String CertificationSection;
    @Lob
    private String ExtracurricularSection;

    public String getCertificationSection() {
        return CertificationSection;
    }

    public void setCertificationSection(String certificationSection) {
        CertificationSection = certificationSection;
    }

    public String getExtracurricularSection() {
        return ExtracurricularSection;
    }

    public void setExtracurricularSection(String extracurricularSection) {
        ExtracurricularSection = extracurricularSection;
    }

    public String getIntroductionSection() {
        return IntroductionSection;
    }

    public void setIntroductionSection(String introductionSection) {
        IntroductionSection = introductionSection;
    }

    public String getExperienceSection() {
        return experienceSection;
    }

    public void setExperienceSection(String experienceSection) {
        this.experienceSection = experienceSection;
    }

    public String getEducationSection() {
        return educationSection;
    }

    public void setEducationSection(String educationSection) {
        this.educationSection = educationSection;
    }

    public String getSkillsSection() {
        return skillsSection;
    }

    public void setSkillsSection(String skillsSection) {
        this.skillsSection = skillsSection;
    }

    public String getProjectsSection() {
        return projectsSection;
    }

    public void setProjectsSection(String projectsSection) {
        this.projectsSection = projectsSection;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    // Getters and setters
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

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}
