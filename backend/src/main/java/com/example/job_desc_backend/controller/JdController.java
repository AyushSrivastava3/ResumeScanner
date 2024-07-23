package com.example.job_desc_backend.controller;

import com.example.job_desc_backend.dto.Job_description_RequestDto;
import com.example.job_desc_backend.dto.Job_description_ResponseDto;
import com.example.job_desc_backend.model.Job_description;
import com.example.job_desc_backend.repository.JdRepository;
import com.example.job_desc_backend.service.JdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")

public class JdController {
    @Autowired
    JdService jdService;

    @PostMapping("/create")
    public ResponseEntity<?> createJobDescription(@RequestBody Job_description jobDescription) {
        try {
            Job_description createdJd = jdService.createJobDescription(jobDescription);
            return ResponseEntity.ok(createdJd);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

//    @GetMapping("/jd/today")
//    public List<Job_description> getJobDescriptionsAddedToday() {
//        return jdService.getJobDescriptionsAddedToday();
//    }
//
//    @GetMapping("/jd/last-week")
//    public List<Job_description> getJobDescriptionsAddedInLastWeek() {
//        return jdService.getJobDescriptionsAddedInLastWeek();
//    }
//
//    @GetMapping("/jd/date-range")
//    public List<Job_description> getJobDescriptionsWithinDateRange(
//            @RequestParam("startDate") String startDate,
//            @RequestParam("endDate") String endDate) {
//        LocalDateTime start = LocalDateTime.parse(startDate);
//        LocalDateTime end = LocalDateTime.parse(endDate);
//        return jdService.getJobDescriptionsWithinDateRange(start, end);
//    }



    @GetMapping("/jd/count")
    public Map<String, Long> getJobDescriptionsCount() {
        long total = jdService.getTotalJobDescriptions();
        long thisWeek = jdService.getJobDescriptionsAddedThisWeek().size();
        long today = jdService.getJobDescriptionsAddedToday().size();
        return Map.of("total", total, "thisWeek", thisWeek, "today", today);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editJobDescription(@RequestBody Job_description jobDescription) {
        try {
            Job_description updatedJd = jdService.editJobDescription(jobDescription);
            return ResponseEntity.ok(updatedJd);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteJd")
    public ResponseEntity<Void> deleteJobDescription(@RequestParam String id) {
        jdService.deleteJobDescription(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getJd")
    public Job_description getJd(@RequestParam String id){
        return jdService.getJd(id);
    }

    @GetMapping("/jd/getIds")
    public List<Job_description_ResponseDto> getAllIds(){
        return jdService.getAllJobDescriptions();
    }
}
