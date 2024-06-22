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

import java.util.List;

@RestController
@CrossOrigin(origins = "*")

public class JdController {
    @Autowired
    JdService jdService;

    @PostMapping("/create")
    public Job_description createJobDescription(@RequestBody Job_description jobDescription) {
        return jdService.createJobDescription(jobDescription);
    }
    @PostMapping("/edit")
    public ResponseEntity<Job_description> editJobDescription(
            @RequestBody Job_description updatedJobDescription) {
        Job_description editedJobDescription = jdService.editJobDescription( updatedJobDescription);
        return new ResponseEntity<>(editedJobDescription, HttpStatus.OK);
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
