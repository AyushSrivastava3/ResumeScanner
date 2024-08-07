package com.example.job_desc_backend.service;

import com.example.job_desc_backend.model.Profile;
import com.example.job_desc_backend.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {
    @Autowired
    ProfileRepository profileRepository;
    public Profile addProfile(Profile profile, MultipartFile attachment) {
        if (attachment!=null) {
            profile.setAttachment(attachment.getOriginalFilename());
        }
        profile.setUploadDate(LocalDateTime.now());
        Profile savedPofile=profileRepository.save(profile);
        return savedPofile;
    }

//    public List<Profile> getProfilesAddedToday() {
//        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
//        return profileRepository.findProfilesAddedToday(todayStart);
//    }
//
//    public List<Profile> getProfilesAddedInLastWeek() {
//        LocalDateTime weekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);
//        LocalDateTime now = LocalDateTime.now();
//        return profileRepository.findProfilesWithinDateRange(weekAgo, now);
//    }
//
//    public List<Profile> getProfilesWithinDateRange(LocalDateTime startDate, LocalDateTime endDate) {
//        return profileRepository.findProfilesWithinDateRange(startDate, endDate);
//    }

    public long getTotalProfiles() {
        return profileRepository.count();
    }

    public List<Profile> getProfilesAddedToday() {
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
        return profileRepository.findProfilesAddedToday(todayStart);
    }

    public List<Profile> getProfilesAddedThisWeek() {
        LocalDateTime weekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);
        LocalDateTime now = LocalDateTime.now();
        return profileRepository.findProfilesWithinDateRange(weekAgo, now);
    }

    public Profile updateProfile(String id, Profile profileDetails) {
        Optional<Profile> optionalProfile = profileRepository.findById(id);
        if (optionalProfile.isPresent()) {
            Profile existingProfile = optionalProfile.get();
            existingProfile.setName(profileDetails.getName());
            existingProfile.setEmailId(profileDetails.getEmailId());
            existingProfile.setCurrent_location(profileDetails.getCurrent_location());
            existingProfile.setJdId(profileDetails.getJdId());
            existingProfile.setCurrent_ctc(profileDetails.getCurrent_ctc());
            existingProfile.setExpected_ctc(profileDetails.getExpected_ctc());
            existingProfile.setCall_back(profileDetails.isCall_back());
            existingProfile.setAttachment(profileDetails.getAttachment());
            existingProfile.setMobNo(profileDetails.getMobNo());
            existingProfile.setCurrentCompany(profileDetails.getCurrentCompany());
            existingProfile.setLinkedIn(profileDetails.getLinkedIn());
            existingProfile.setPreferred_Location(profileDetails.getPreferred_Location());
            existingProfile.setRelevantExp(profileDetails.getRelevantExp());
            existingProfile.setTotalExp(profileDetails.getTotalExp());
            existingProfile.setInterested_in_opportunity(profileDetails.isInterested_in_opportunity());
            existingProfile.setReady_to_relocate(profileDetails.isReady_to_relocate());
            existingProfile.setWillingToRelocate(profileDetails.isWillingToRelocate());
            return profileRepository.save(existingProfile);
        } else {
            throw new RuntimeException("Profile not found with id: " + id);
        }
    }

    public List<Profile> getProfilesByJdId(String jdId) {
        return profileRepository.findByJdId(jdId);
    }
}
