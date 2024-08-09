package com.example.job_desc_backend.service;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.ZoneOffset;
import com.example.job_desc_backend.model.Profile;
import com.example.job_desc_backend.repository.ProfileRepository;
import com.itextpdf.io.source.ByteArrayOutputStream;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
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
//    public List<Profile> getProfilesAddedThisWeek() {
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

//    public List<Profile> getProfilesAddedToday() {
//        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
//        return profileRepository.findProfilesAddedToday(todayStart);
//    }
//
//    public List<Profile> getProfilesAddedThisWeek() {
//        LocalDateTime weekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);
//        LocalDateTime now = LocalDateTime.now();
//        return profileRepository.findProfilesWithinDateRange(weekAgo, now);
//    }


    public List<Profile> getProfilesAddedToday() {
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
        return profileRepository.findProfilesAddedToday(todayStart);
    }

    public List<Profile> getProfilesAddedThisWeek() {
        LocalDateTime weekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);
        LocalDateTime now = LocalDateTime.now();
        return profileRepository.findProfilesWithinDateRange(weekAgo, now);
    }



//    public List<Profile> getProfilesAddedToday() {
//        LocalDateTime todayStart = LocalDateTime.now(ZoneOffset.UTC).with(LocalTime.MIN);
//        return profileRepository.findProfilesAddedToday(todayStart);
//    }
//
//    public List<Profile> getProfilesAddedThisWeek() {
//        LocalDateTime weekAgo = LocalDateTime.now(ZoneOffset.UTC).minusWeeks(1).with(LocalTime.MIN);
//        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
//        return profileRepository.findProfilesWithinDateRange(weekAgo, now);
//    }



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

//    public ByteArrayInputStream generateProfileDocument(String profileId) throws IOException {
//        Profile profile = profileRepository.findById(profileId).orElse(null);
//        if (profile == null) {
//            throw new IllegalArgumentException("Profile not found");
//        }
//
//        try (XWPFDocument doc = new XWPFDocument()) {
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//
//            // Add profile details to the document
//            addProfileDetails(doc, profile);
//
//            doc.write(out);
//            return new ByteArrayInputStream(out.toByteArray());
//        }
//    }
//
//    private void addProfileDetails(XWPFDocument doc, Profile profile) {
//        XWPFParagraph title = doc.createParagraph();
//        title.createRun().setText("Profile Details");
//
//        addParagraph(doc, "Job Description ID: " + profile.getJdId());
//        addParagraph(doc, "Name: " + profile.getName());
//        addParagraph(doc, "Mobile Number: " + profile.getMobNo());
//        addParagraph(doc, "Email ID: " + profile.getEmailId());
//        addParagraph(doc, "Total Experience: " + profile.getTotalExp());
//        addParagraph(doc, "Relevant Experience: " + profile.getRelevantExp());
//        addParagraph(doc, "Current Company: " + profile.getCurrentCompany());
//        addParagraph(doc, "Willing to Relocate: " + (profile.isWillingToRelocate() ? "Yes" : "No"));
//        addParagraph(doc, "Current CTC: " + profile.getCurrent_ctc());
//        addParagraph(doc, "Expected CTC: " + profile.getExpected_ctc());
//        addParagraph(doc, "Current Location: " + profile.getCurrent_location());
//        addParagraph(doc, "Preferred Locations: " + String.join(", ", profile.getPreferred_Location()));
//        addParagraph(doc, "Interested in Opportunity: " + (profile.isInterested_in_opportunity() ? "Yes" : "No"));
//        addParagraph(doc, "Call Back: " + (profile.isCall_back() ? "Yes" : "No"));
//        addParagraph(doc, "Ready to Relocate: " + (profile.isReady_to_relocate() ? "Yes" : "No"));
//        addParagraph(doc, "LinkedIn: " + profile.getLinkedIn());
//        addParagraph(doc, "Attachment: " + profile.getAttachment());
//        addParagraph(doc, "Uploaded Date: " + profile.getUploadDate());
//        addParagraph(doc, "Created By: " + profile.getCreatedBy());
//        addParagraph(doc, "Experience Section: " + profile.getExperienceSection());
//        addParagraph(doc, "Education Section: " + profile.getEducationSection());
//        addParagraph(doc, "Skills Section: " + profile.getSkillsSection());
//        addParagraph(doc, "Projects Section: " + profile.getProjectsSection());
//        addParagraph(doc, "Introduction Section: " + profile.getIntroductionSection());
//        addParagraph(doc, "Certification Section: " + profile.getCertificationSection());
//        addParagraph(doc, "Extracurricular Section: " + profile.getExtracurricularSection());
//    }
//
//    private void addParagraph(XWPFDocument doc, String text) {
//        XWPFParagraph paragraph = doc.createParagraph();
//        paragraph.createRun().setText(text);
//    }

    public ByteArrayInputStream generateProfileDocument(String profileId) throws IOException {
        Profile profile = profileRepository.findById(profileId).orElse(null);
        if (profile == null) {
            throw new IllegalArgumentException("Profile not found for id: " + profileId);
        }

        try (XWPFDocument doc = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Add profile details to the document with styling
            addProfileDetails(doc, profile);

            doc.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            System.err.println("Error generating document: " + e.getMessage());
            throw e; // Re-throw the exception to be handled by the controller
        }
    }

    private void addProfileDetails(XWPFDocument doc, Profile profile) {
        // Add Title
        XWPFParagraph title = doc.createParagraph();
        XWPFRun titleRun = title.createRun();
        titleRun.setText("Profile Details");
        titleRun.setBold(true);
        titleRun.setFontSize(20); // Large font size for the title
        titleRun.setUnderline(UnderlinePatterns.SINGLE);

        // Add profile details with basic styling
        addStyledParagraph(doc, "Job Description ID: " + (profile.getJdId() != null ? profile.getJdId() : "N/A"));
        addStyledParagraph(doc, "Name: " + (profile.getName() != null ? profile.getName() : "N/A"));
        addStyledParagraph(doc, "Mobile Number: " + (profile.getMobNo() != null ? profile.getMobNo() : "N/A"));
        addStyledParagraph(doc, "Email ID: " + (profile.getEmailId() != null ? profile.getEmailId() : "N/A"));
        addStyledParagraph(doc, "Total Experience: " + (profile.getTotalExp() != null ? profile.getTotalExp() : "N/A"));
        addStyledParagraph(doc, "Relevant Experience: " + (profile.getRelevantExp() != null ? profile.getRelevantExp() : "N/A"));
        addStyledParagraph(doc, "Current Company: " + (profile.getCurrentCompany() != null ? profile.getCurrentCompany() : "N/A"));
        addStyledParagraph(doc, "Willing to Relocate: " + (profile.isWillingToRelocate() ? "Yes" : "No"));
        addStyledParagraph(doc, "Current CTC: " + (profile.getCurrent_ctc() != null ? profile.getCurrent_ctc() : "N/A"));
        addStyledParagraph(doc, "Expected CTC: " + (profile.getExpected_ctc() != null ? profile.getExpected_ctc() : "N/A"));
        addStyledParagraph(doc, "Current Location: " + (profile.getCurrent_location() != null ? profile.getCurrent_location() : "N/A"));
        addStyledParagraph(doc, "Preferred Locations: " + (profile.getPreferred_Location() != null ? String.join(", ", profile.getPreferred_Location()) : "N/A"));
        addStyledParagraph(doc, "Interested in Opportunity: " + (profile.isInterested_in_opportunity() ? "Yes" : "No"));
        addStyledParagraph(doc, "Call Back: " + (profile.isCall_back() ? "Yes" : "No"));
        addStyledParagraph(doc, "Ready to Relocate: " + (profile.isReady_to_relocate() ? "Yes" : "No"));
        addStyledParagraph(doc, "LinkedIn: " + (profile.getLinkedIn() != null ? profile.getLinkedIn() : "N/A"));
        addStyledParagraph(doc, "Attachment: " + (profile.getAttachment() != null ? profile.getAttachment() : "N/A"));
        addStyledParagraph(doc, "Uploaded Date: " + (profile.getUploadDate() != null ? profile.getUploadDate() : "N/A"));
        addStyledParagraph(doc, "Created By: " + (profile.getCreatedBy() != null ? profile.getCreatedBy() : "N/A"));
        addStyledParagraph(doc, "Experience Section: " + (profile.getExperienceSection() != null ? profile.getExperienceSection() : "N/A"));
        addStyledParagraph(doc, "Education Section: " + (profile.getEducationSection() != null ? profile.getEducationSection() : "N/A"));
        addStyledParagraph(doc, "Skills Section: " + (profile.getSkillsSection() != null ? profile.getSkillsSection() : "N/A"));
        addStyledParagraph(doc, "Projects Section: " + (profile.getProjectsSection() != null ? profile.getProjectsSection() : "N/A"));
        addStyledParagraph(doc, "Introduction Section: " + (profile.getIntroductionSection() != null ? profile.getIntroductionSection() : "N/A"));
        addStyledParagraph(doc, "Certification Section: " + (profile.getCertificationSection() != null ? profile.getCertificationSection() : "N/A"));
        addStyledParagraph(doc, "Extracurricular Section: " + (profile.getExtracurricularSection() != null ? profile.getExtracurricularSection() : "N/A"));
    }

    private void addStyledParagraph(XWPFDocument doc, String text) {
        XWPFParagraph paragraph = doc.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontSize(12); // Default font size
        // Optionally, you can add more styling here
    }

}
