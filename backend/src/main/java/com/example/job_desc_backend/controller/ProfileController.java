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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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

    @GetMapping("/profile/export")
    public ResponseEntity<byte[]> exportProfilesToExcel() {
        try {
            List<Profile> profiles = profileRepository.findAll();
            ExportImportService<Profile> exporter = new ExportImportService<>();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            exporter.exportToExcel(profiles, baos);

            byte[] excelBytes = baos.toByteArray();
            System.out.println("Excel file size: " + excelBytes.length);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=profiles_export.xlsx");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(("Failed to create Excel file: " + e.getMessage()).getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/profile/export/jdid")
    public ResponseEntity<byte[]> exportProfilesById(@RequestParam String jdid) {
        try {
            List<Profile> profiles = profileRepository.findByJdId(jdid);
            ExportImportService<Profile> exporter = new ExportImportService<>();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            exporter.exportToExcel(profiles, baos);

            byte[] excelBytes = baos.toByteArray();
            System.out.println("Excel file size: " + excelBytes.length);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=profiles_by_id_export.xlsx");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(("Failed to create Excel file: " + e.getMessage()).getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/profile/import")
    public ResponseEntity<?> importProfilesFromCSV(@RequestParam("file") MultipartFile file) {
        try {
            ExportImportService<Profile> importer = new ExportImportService<>();
            List<Profile> profiles = importer.importFromCSV(file, Profile.class);
            profileRepository.saveAll(profiles);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to import CSV file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }

    @GetMapping("/downloadProfile")
    public ResponseEntity<InputStreamResource> downloadProfile(@RequestParam("id") String profileId) throws IOException {
        ByteArrayInputStream bis = profileService.generateProfileDocument(profileId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=profile.docx");
        return new ResponseEntity<>(new InputStreamResource(bis), headers, HttpStatus.OK);
    }

}
