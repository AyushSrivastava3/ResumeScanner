package com.example.job_desc_backend.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "Profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String jdId;
    private String name;
    private String mobNo;
    private String emailId;
    private String totalExp;
    private String relevantExp;
    private String currentCompany;
    private boolean willingToRelocate;
    private String current_ctc;
    private String expected_ctc;
    private String current_location;
    private List<String> preferred_Location;
    private boolean interested_in_opportunity;
    private boolean call_back;
    private boolean ready_to_relocate;
    private String linkedIn;
    private String attachment;
    private LocalDateTime uploadDate;
    private String createdBy;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    // New fields for extracted resume sections
    private String experienceSection;
    private String educationSection;
    private String skillsSection;
    private String projectsSection;
    private String introductionSection;
    private String certificationSection;
    private String extracurricularSection;

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
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



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJdId() {
        return jdId;
    }

    public void setJdId(String jdId) {
        this.jdId = jdId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobNo() {
        return mobNo;
    }

    public void setMobNo(String mobNo) {
        this.mobNo = mobNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getTotalExp() {
        return totalExp;
    }

    public void setTotalExp(String totalExp) {
        this.totalExp = totalExp;
    }

    public String getRelevantExp() {
        return relevantExp;
    }

    public void setRelevantExp(String relevantExp) {
        this.relevantExp = relevantExp;
    }

    public String getCurrentCompany() {
        return currentCompany;
    }

    public void setCurrentCompany(String currentCompany) {
        this.currentCompany = currentCompany;
    }

    public boolean isWillingToRelocate() {
        return willingToRelocate;
    }

    public void setWillingToRelocate(boolean willingToRelocate) {
        this.willingToRelocate = willingToRelocate;
    }

    public String getCurrent_ctc() {
        return current_ctc;
    }

    public void setCurrent_ctc(String current_ctc) {
        this.current_ctc = current_ctc;
    }

    public String getExpected_ctc() {
        return expected_ctc;
    }

    public void setExpected_ctc(String expected_ctc) {
        this.expected_ctc = expected_ctc;
    }

    public String getCurrent_location() {
        return current_location;
    }

    public void setCurrent_location(String current_location) {
        this.current_location = current_location;
    }

    public List<String> getPreferred_Location() {
        return preferred_Location;
    }

    public void setPreferred_Location(List<String> preferred_Location) {
        this.preferred_Location = preferred_Location;
    }

    public boolean isInterested_in_opportunity() {
        return interested_in_opportunity;
    }

    public void setInterested_in_opportunity(boolean interested_in_opportunity) {
        this.interested_in_opportunity = interested_in_opportunity;
    }

    public boolean isCall_back() {
        return call_back;
    }

    public void setCall_back(boolean call_back) {
        this.call_back = call_back;
    }

    public boolean isReady_to_relocate() {
        return ready_to_relocate;
    }

    public void setReady_to_relocate(boolean ready_to_relocate) {
        this.ready_to_relocate = ready_to_relocate;
    }

    public String getLinkedIn() {
        return linkedIn;
    }

    public void setLinkedIn(String linkedIn) {
        this.linkedIn = linkedIn;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}
