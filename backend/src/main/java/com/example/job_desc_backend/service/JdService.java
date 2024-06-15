package com.example.job_desc_backend.service;

import com.example.job_desc_backend.dto.Job_description_RequestDto;
import com.example.job_desc_backend.dto.Job_description_ResponseDto;
import com.example.job_desc_backend.model.Job_description;
import com.example.job_desc_backend.model.Skill;
import com.example.job_desc_backend.repository.JdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JdService {

    @Autowired
    private JdRepository jdRepository;


//    public Job_description createJobDescription(Job_description_RequestDto jobDescriptionRequestDto) {
//        Job_description jobDescription=new Job_description();
//        jobDescription.setTitle(jobDescriptionRequestDto.getTitle());
//        jobDescription.setDescription(jobDescriptionRequestDto.getDescription());
//        jobDescription.setResponsibilities(jobDescriptionRequestDto.getResponsibilities());
//        jobDescription.setQualifications(jobDescriptionRequestDto.getQualifications());
//        jobDescription.setLocation(jobDescriptionRequestDto.getLocation());
//        jobDescription.setEmploymentType(jobDescriptionRequestDto.getEmploymentType());
//        jobDescription.setExperienceLevel(jobDescriptionRequestDto.getExperienceLevel());
//
//
//        return jdRepository.save(jobDescription);
//    }

    public Job_description createJobDescription(Job_description jobDescription) {
        return jdRepository.save(jobDescription);
    }

//    public List<String> getAllIds(){
//        List<String> jds=new ArrayList<>();
//        List<Job_description> allJd=jdRepository.findAll();
//        for (Job_description jobDescription:allJd){
//            jds.add(jobDescription.getId());
//        }
//        return jds;
//    }

    public List<Job_description_ResponseDto> getAllJobDescriptions() {
        List<Job_description> allJd = jdRepository.findAll();
        List<Job_description_ResponseDto> jds = new ArrayList<>();
        for (Job_description jobDescription : allJd) {
            Job_description_ResponseDto dto = new Job_description_ResponseDto();
            dto.setId(jobDescription.getId());
            dto.setTitle(jobDescription.getTitle());
            jds.add(dto);
        }
        return jds;
    }



    public List<Skill> getMandatorySkills(String jdId) {
        Optional<Job_description> jobDescriptionOptional = jdRepository.findById(jdId);
        if (jobDescriptionOptional.isPresent()) {
            Job_description jobDescription = jobDescriptionOptional.get();
            return jobDescription.getMandatorySkills(); // Assuming getDescription() returns the job description content as String
        }
        return null; // or throw an exception, handle as per your application's requirement
    }
}