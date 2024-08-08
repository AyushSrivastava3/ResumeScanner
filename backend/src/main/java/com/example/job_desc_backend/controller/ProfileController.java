package com.example.job_desc_backend.controller;

import com.example.job_desc_backend.model.Profile;
import com.example.job_desc_backend.repository.ProfileRepository;
import com.example.job_desc_backend.service.ExportImportService;
import com.example.job_desc_backend.service.ProfileSectionService;
import com.example.job_desc_backend.service.ProfileService;
import com.example.job_desc_backend.service.ResumeService;
import com.example.job_desc_backend.utility.FileTextExtractor;
import com.itextpdf.io.source.ByteArrayOutputStream;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.job_desc_backend.service.ResumeService.extractSections;

@RestController
@CrossOrigin(origins = "*")
public class ProfileController {
    @Autowired
    ProfileService profileService;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    FileTextExtractor fileTextExtractor;
    @Autowired
    ResumeService resumeService;
    @Autowired
    ProfileSectionService profileSectionService;
    @PostMapping("/addProfile")
    public ResponseEntity<?> addProfile(@RequestPart("profile") Profile profile,
                                        @RequestPart(value = "attachment", required = false) MultipartFile attachment,
                                        @RequestParam(value = "saveSections", required = false, defaultValue = "false") boolean saveSections) throws IOException, TesseractException {
        try {
            if (attachment != null  && saveSections && profile.getId()!=null) {//&& !attachment.isEmpty() && saveSections
                String resumeText = fileTextExtractor.extractTextFromFile(attachment);
                Map<String, String> sections = extractSections(resumeText);

                // Save attachment
                resumeService.saveResume(attachment, true);
                
                //save extracted section to ProfileSectionRepository
                profileSectionService.saveSections(profile.getId(),sections);

                // Save extracted sections to profile
                profile.setExperienceSection(sections.getOrDefault("WORK EXPERIENCE", ""));
                profile.setEducationSection(sections.getOrDefault("EDUCATION", ""));
                profile.setSkillsSection(sections.getOrDefault("SKILLS", ""));
                profile.setProjectsSection(sections.getOrDefault("PROJECTS", ""));
                profile.setIntroductionSection(sections.getOrDefault("INTRODUCTION", ""));
                profile.setCertificationSection(sections.getOrDefault("CERTIFICATIONS", ""));
                profile.setExtracurricularSection(sections.getOrDefault("EXTRACURRICULAR", ""));
            } else if (attachment !=null && saveSections==false && profile.getId()!=null) {
                String resumeText = fileTextExtractor.extractTextFromFile(attachment);
                Map<String, String> sections = extractSections(resumeText);

                // Save attachment
                resumeService.saveResume(attachment, true);

                //save extracted section to ProfileSectionRepository
                profileSectionService.saveSections(profile.getId(),sections);
            } else if (saveSections && profile.getId()!=null && attachment==null) {
                Map<String, String> sections= profileSectionService.getSections(profile.getId());
                // Save extracted sections to profile
                profile.setExperienceSection(sections.getOrDefault("WORK EXPERIENCE", ""));
                profile.setEducationSection(sections.getOrDefault("EDUCATION", ""));
                profile.setSkillsSection(sections.getOrDefault("SKILLS", ""));
                profile.setProjectsSection(sections.getOrDefault("PROJECTS", ""));
                profile.setIntroductionSection(sections.getOrDefault("INTRODUCTION", ""));
                profile.setCertificationSection(sections.getOrDefault("CERTIFICATIONS", ""));
                profile.setExtracurricularSection(sections.getOrDefault("EXTRACURRICULAR", ""));
            }
            Profile createdProfile = profileService.addProfile(profile,attachment);
            return ResponseEntity.ok(createdProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PutMapping("/profile/{id}")

    public ResponseEntity<?> updateProfile(@PathVariable String id,
                                           @RequestPart("profile") Profile profileDetails,
                                           @RequestPart(value = "attachment", required = false) MultipartFile attachment) throws IOException {
        try {

            if (attachment != null) {
                // Extract sections from the resume
                String resumeText = fileTextExtractor.extractTextFromFile(attachment);
                Map<String, String> sections = extractSections(resumeText);

                // Save attachment
                resumeService.saveResume(attachment, true);

                // Save extracted sections to profile
                profileDetails.setExperienceSection(sections.getOrDefault("WORK EXPERIENCE", ""));
                profileDetails.setEducationSection(sections.getOrDefault("EDUCATION", ""));
                profileDetails.setSkillsSection(sections.getOrDefault("SKILLS", ""));
                profileDetails.setProjectsSection(sections.getOrDefault("PROJECTS", ""));
                profileDetails.setIntroductionSection(sections.getOrDefault("INTRODUCTION", ""));
                profileDetails.setCertificationSection(sections.getOrDefault("CERTIFICATIONS", ""));
                profileDetails.setExtracurricularSection(sections.getOrDefault("EXTRACURRICULAR", ""));
            }

            Profile updatedProfile = profileService.updateProfile(id, profileDetails);
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/jd/{jdId}")
    public List<Profile> getProfilesByJdId(@PathVariable String jdId) {
        return profileService.getProfilesByJdId(jdId);
    }

    @GetMapping("/getProfile")
    public Profile getProfileByProfileId(@RequestParam String id){
        return profileRepository.findById(id).get();
    }

    @GetMapping("/getAllProfile")
    public List<Profile> getAllProfile(){
        return profileRepository.findAll();
    }

    @GetMapping("/profile/count")
    public Map<String, Long> getProfilesCount() {
        long total = profileService.getTotalProfiles();
        long thisWeek = profileService.getProfilesAddedThisWeek().size();
        long today = profileService.getProfilesAddedToday().size();
        return Map.of("total", total, "thisWeek", thisWeek, "today", today);
    }

    @PostMapping("/extractResumeSections")
    public ResponseEntity<?> extractResumeSections(@RequestPart("attachment") MultipartFile attachment) throws IOException, TesseractException {
        if (attachment == null) {
            return ResponseEntity.badRequest().body("Attachment is required.");
        }

        String resumeText = fileTextExtractor.extractTextFromFile(attachment);
        Map<String, String> sections = extractSections(resumeText);

        return ResponseEntity.ok(sections);
    }

    @GetMapping("/export")
    public String exportProfilesToExcel() {
        try {
            List<Profile> profiles = profileRepository.findAll();
            ExportImportService<Profile> exporter = new ExportImportService<>();
            String filePath = "/Users/dq-mac-m2-1/Documents/profiles.xlsx"; // Change this to your desired path
            exporter.exportToExcel(profiles, filePath);
            return "Excel file created successfully at: " + filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to create Excel file: " + e.getMessage();
        }
    }


    @PostMapping("/import")
    public String importProfilesFromCSV(@RequestParam("file") MultipartFile file) {
        try {
            ExportImportService<Profile> importer = new ExportImportService<>();
            List<Profile> profiles = importer.importFromCSV(file, Profile.class);
            profileRepository.saveAll(profiles);
            return "CSV file imported successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to import CSV file: " + e.getMessage();
        }
    }

}
