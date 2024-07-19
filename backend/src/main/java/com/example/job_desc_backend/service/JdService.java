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

    public Job_description createJobDescription(Job_description jobDescription) {
        if (jdRepository.existsByTitleRegexIgnoreCase(jobDescription.getTitle())) {
            throw new RuntimeException("Job description title already exists");
        }
        return jdRepository.save(jobDescription);
    }

    public Job_description editJobDescription(Job_description jobDescription){
        Job_description newJd=jdRepository.findById(jobDescription.getId()).get();
        //newJd.setId(jobDescription.getId());
        if (!newJd.getTitle().equals(jobDescription.getTitle()) && jdRepository.existsByTitleRegexIgnoreCase(jobDescription.getTitle())) {
            throw new RuntimeException("Job description title already exists");
        }
        newJd.setTitle(jobDescription.getTitle());
        newJd.setLocation(jobDescription.getLocation());
        newJd.setExperienceLevel(jobDescription.getExperienceLevel());
        newJd.setMandatorySkills(jobDescription.getMandatorySkills());
        newJd.setOptionalSkills(jobDescription.getOptionalSkills());
        Job_description updatedJd=jdRepository.save(newJd);
        return updatedJd;
    }

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

    public void deleteJobDescription(String id) {
        Job_description jobDescription=jdRepository.findById(id).get();
        jdRepository.delete(jobDescription);
    }

    public Job_description getJd(String id) {
        Job_description jobDescription=jdRepository.findById(id).get();
        return jobDescription;
    }
}