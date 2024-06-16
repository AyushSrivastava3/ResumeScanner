package com.example.job_desc_backend.controller;

import com.example.job_desc_backend.dto.Job_description_RequestDto;
import com.example.job_desc_backend.dto.Job_description_ResponseDto;
import com.example.job_desc_backend.model.Job_description;
import com.example.job_desc_backend.repository.JdRepository;
import com.example.job_desc_backend.service.JdService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/jd/getIds")
    public List<Job_description_ResponseDto> getAllIds(){
        return jdService.getAllJobDescriptions();
    }
}
