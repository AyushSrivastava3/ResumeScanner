package com.example.job_desc_backend.service;

import com.example.job_desc_backend.model.ProfileSection;
import com.example.job_desc_backend.repository.ProfileSectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProfileSectionService {
    @Autowired
    private ProfileSectionRepository profileSectionRepository;

    public void saveSections(String profileId, Map<String, String> sections) {
        ProfileSection resumeSection = new ProfileSection();
        resumeSection.setProfileId(profileId);
        resumeSection.setSections(sections);
        profileSectionRepository.save(resumeSection);
    }

    public Map<String, String> getSections(String profileId) {
       ProfileSection profileSection=profileSectionRepository.findByProfileId(profileId);
       return profileSection.getSections();
    }
}
