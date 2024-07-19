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
import java.util.List;

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

    @PutMapping("/{id}")

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
}
