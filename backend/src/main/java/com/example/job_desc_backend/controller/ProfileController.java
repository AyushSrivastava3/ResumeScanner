package com.example.job_desc_backend.controller;

import com.example.job_desc_backend.model.Profile;
import com.example.job_desc_backend.repository.ProfileRepository;
import com.example.job_desc_backend.service.ProfileService;
import com.example.job_desc_backend.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class ProfileController {
    @Autowired
    ProfileService profileService;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    ResumeService resumeService;
    @PostMapping("/addProfile")
    public ResponseEntity<?> addProfile(@RequestPart("profile") Profile profile,
                                        @RequestPart(value = "attachment", required = false) MultipartFile attachment) throws IOException {
        try {
            if (attachment != null) {
                resumeService.saveResume(attachment,true);
            }
            Profile createdProfile = profileService.addProfile(profile,attachment);
            return ResponseEntity.ok(createdProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

//    @GetMapping("/profiles/today")
//    public List<Profile> getProfilesAddedToday() {
//        return profileService.getProfilesAddedToday();
//    }
//
//    @GetMapping("/profiles/last-week")
//    public List<Profile> getProfilesAddedInLastWeek() {
//        return profileService.getProfilesAddedInLastWeek();
//    }

//    @GetMapping("/profiles/date-range")
//    public List<Profile> getProfilesWithinDateRange(
//            @RequestParam("startDate") String startDate,
//            @RequestParam("endDate") String endDate) {
//        LocalDateTime start = LocalDateTime.parse(startDate);
//        LocalDateTime end = LocalDateTime.parse(endDate);
//        return profileService.getProfilesWithinDateRange(start, end);
//    }

    @PutMapping("/profile/{id}")

    public ResponseEntity<?> updateProfile(@PathVariable String id,
                                           @RequestPart("profile") Profile profileDetails,
                                           @RequestPart(value = "attachment", required = false) MultipartFile attachment) throws IOException {
        try {
            if (attachment != null) {
                resumeService.saveResume(attachment,true);
            }
            Profile updatedProfile = profileService.updateProfile(id, profileDetails);
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
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
}
