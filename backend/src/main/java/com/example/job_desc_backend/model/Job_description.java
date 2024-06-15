//package com.example.job_desc_backend.model;
//
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import lombok.*;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Document(collection = "Jd")
//public class Job_description {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private String id;
//
//    private String title;
//    private String description;
//    private String responsibilities;
//    private String qualifications;
//    private String location;
//    private String employmentType; // e.g., Full-time, Part-time, Contract, etc.
//    private String experienceLevel;
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getResponsibilities() {
//        return responsibilities;
//    }
//
//    public void setResponsibilities(String responsibilities) {
//        this.responsibilities = responsibilities;
//    }
//
//    public String getQualifications() {
//        return qualifications;
//    }
//
//    public void setQualifications(String qualifications) {
//        this.qualifications = qualifications;
//    }
//
//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }
//
//    public String getEmploymentType() {
//        return employmentType;
//    }
//
//    public void setEmploymentType(String employmentType) {
//        this.employmentType = employmentType;
//    }
//
//    public String getExperienceLevel() {
//        return experienceLevel;
//    }
//
//    public void setExperienceLevel(String experienceLevel) {
//        this.experienceLevel = experienceLevel;
//    }
//}




package com.example.job_desc_backend.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "Jd")
public class Job_description {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private String title;
    private String location;
    private String experienceLevel;

    private List<Skill> mandatorySkills;
    private List<Skill> optionalSkills;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public List<Skill> getMandatorySkills() {
        return mandatorySkills;
    }

    public void setMandatorySkills(List<Skill> mandatorySkills) {
        this.mandatorySkills = mandatorySkills;
    }

    public List<Skill> getOptionalSkills() {
        return optionalSkills;
    }

    public void setOptionalSkills(List<Skill> optionalSkills) {
        this.optionalSkills = optionalSkills;
    }


}
