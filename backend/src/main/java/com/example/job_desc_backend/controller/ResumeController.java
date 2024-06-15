package com.example.job_desc_backend.controller;

import com.example.job_desc_backend.service.JdService;
import com.example.job_desc_backend.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;

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
                                                            @RequestParam("jdId") String jdId) {
        return resumeService.uploadResume(file, jdId);
    }

//    @PostMapping("/save")
//    public ResponseEntity<Map<String, Object>> saveResume(@RequestParam("file") MultipartFile file) {
//        return resumeService.saveResume(file);
//    }
//
//    @GetMapping("/resume/{resumeId}")
//    public ResponseEntity<byte[]> downloadResume(@PathVariable String resumeId) {
//        return resumeService.downloadResume(resumeId);
//    }
}




