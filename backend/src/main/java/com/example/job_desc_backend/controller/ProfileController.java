package com.example.job_desc_backend.controller;

import com.example.job_desc_backend.model.Profile;
import com.example.job_desc_backend.repository.ProfileRepository;
import com.example.job_desc_backend.service.ProfileService;
import com.example.job_desc_backend.service.ResumeService;
import com.example.job_desc_backend.utility.FileTextExtractor;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @PostMapping("/addProfile")
    public ResponseEntity<?> addProfile(@RequestPart("profile") Profile profile,
                                        @RequestPart(value = "attachment", required = false) MultipartFile attachment,
                                        @RequestParam(value = "saveSections", required = false, defaultValue = "false") boolean saveSections) throws IOException, TesseractException {
        try {
            if (attachment != null  && saveSections ) {//&& !attachment.isEmpty() && saveSections
                String resumeText = fileTextExtractor.extractTextFromFile(attachment);
                Map<String, String> sections = extractSections(resumeText);

                // Save attachment
                resumeService.saveResume(attachment, true);

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

}
