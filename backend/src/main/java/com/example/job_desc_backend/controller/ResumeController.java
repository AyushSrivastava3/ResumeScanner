package com.example.job_desc_backend.controller;

import com.example.job_desc_backend.dto.Profile_detail_page_response_dto;
import com.example.job_desc_backend.model.Resume;
import com.example.job_desc_backend.service.JdService;
import com.example.job_desc_backend.service.ResumeService;
import com.example.job_desc_backend.utility.ResumeInfo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")

public class ResumeController {

    @Autowired
    JdService jdService;
    @Autowired
    ResumeService resumeService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadResume(@RequestParam("file") MultipartFile file,
                                                            @RequestParam(value = "jdFile", required = false) MultipartFile jdFile,
                                                            @RequestParam(value = "jdId", required = false) String jdId) {
        return resumeService.uploadResume(file,jdFile, jdId);
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveResume(@RequestParam("file") MultipartFile file) {
        return resumeService.saveResume(file);
    }

    @GetMapping("/getResume")
    public ResponseEntity<byte[]> downloadResume(@RequestParam("resumeId") String resumeId) {
        return resumeService.downloadResume(resumeId);
    }

    @GetMapping("/viewResume")
    public ResponseEntity<InputStreamResource> viewResume(@RequestParam("resumeId") String resumeId) {
        return resumeService.viewResume(resumeId);
    }

    @GetMapping("/allResume")
    public ResponseEntity<List<Resume>> getAllResumes() {
        return resumeService.getAllResumes();
    }

    @PostMapping("/generateReport")
    public ResponseEntity<byte[]> generateReport(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("jdId") String jdId) {
        return resumeService.generateReport(file, jdId);
    }

    @GetMapping("/getReleventProfiles")
    public ResponseEntity<List<ResumeInfo>> getReleventProfiles(@RequestParam String jdId) {
        List<ResumeInfo> relevantProfiles = resumeService.ReleventProfiles(jdId);
        return ResponseEntity.ok(relevantProfiles);
    }

    @GetMapping("/getProfileInformation")
    public Profile_detail_page_response_dto getProfileInformation(@RequestParam String fileId){
       return resumeService.getProfileInformation(fileId);
    }

    @GetMapping("/getResumeFile")
    public MultipartFile getResumeAsMultipartFile(@RequestParam String fileId){
        return resumeService.getResumeAsMultipartFile(fileId);
    }

}




