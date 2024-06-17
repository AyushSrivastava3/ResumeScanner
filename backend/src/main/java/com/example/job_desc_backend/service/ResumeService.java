package com.example.job_desc_backend.service;
import com.example.job_desc_backend.model.Resume;
import com.example.job_desc_backend.model.Skill;
import com.example.job_desc_backend.utility.SkillPattern;
import com.example.job_desc_backend.repository.ResumeRepository;
import com.example.job_desc_backend.utility.DatePatternMatcher;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class ResumeService {

    @Autowired
    private JdService jdService;

    public ResponseEntity<Map<String, Object>> uploadResume(MultipartFile file, String jdId) {
        try {
            List<Skill> mandatorySkills = jdService.getMandatorySkills(jdId);

            if (mandatorySkills == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Job Description not found"));
            }

            String resumeText = extractTextFromFile(file);

            if (resumeText == null) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(Map.of("error", "Unsupported file type"));
            }

            List<String> skillNames = new ArrayList<>();
            List<Integer> skillExperience=new ArrayList<>();
            Map<String, Integer> requiredExperienceMap = new HashMap<>();
            for (Skill skill : mandatorySkills) {
                String skillName = skill.getSkill();
                int skillExp=skill.getExperience();
                skillNames.add(skillName);
                skillExperience.add(skillExp);
                requiredExperienceMap.put(skillName.toLowerCase(), skill.getExperience());
            }

            Map<String, Object> skillAnalysis = calculateSkillAnalysis(resumeText, skillNames, requiredExperienceMap);

            return ResponseEntity.ok(skillAnalysis);
        } catch (IOException | TesseractException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to process the file: " + e.getMessage()));
        }
    }

    private String extractTextFromFile(MultipartFile file) throws IOException, TesseractException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IOException("File name is null");
        }

        if (fileName.endsWith(".pdf")) {
            PDDocument document = PDDocument.load(file.getInputStream());
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            if (text.trim().isEmpty()) {
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                ITesseract tesseract = new Tesseract();
                StringBuilder ocrText = new StringBuilder();

                for (int page = 0; page < document.getNumberOfPages(); ++page) {
                    BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300);
                    String result = tesseract.doOCR(bufferedImage);
                    ocrText.append(result);
                }
                document.close();
                return ocrText.toString();
            }

            document.close();
            return text;
        } else if (fileName.endsWith(".doc")) {
            HWPFDocument document = new HWPFDocument(file.getInputStream());
            WordExtractor extractor = new WordExtractor(document);
            String text = extractor.getText();
            document.close();
            return text;
        } else if (fileName.endsWith(".docx")) {
            XWPFDocument document = new XWPFDocument(file.getInputStream());
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            String text = extractor.getText();
            document.close();
            return text;
        } else {
            return null;
        }
    }

    private Map<String, Object> calculateSkillAnalysis(String resumeText, List<String> mandatorySkills, Map<String, Integer> requiredExperienceMap) {
        Map<String, Integer> skillDurations = new HashMap<>();
        Map<String, List<Map<String, Object>>> skillDetails = new HashMap<>();
        int totalExperienceMonths = 0;

        for (String skill : mandatorySkills) {
            skillDurations.put(skill.toLowerCase(), 0);
            skillDetails.put(skill.toLowerCase(), new ArrayList<>());
        }

        List<WorkExperience> experiences = extractWorkExperiences(resumeText);

        for (WorkExperience experience : experiences) {
            totalExperienceMonths += experience.durationInMonths;
            for (String skill : mandatorySkills) {
                String lowerSkill = skill.toLowerCase();

                Pattern pattern = SkillPattern.getSkillPattern(skill);
                Matcher matcher = pattern.matcher(experience.description.toLowerCase());

                if (matcher.find()) {
                    int currentDuration = skillDurations.get(lowerSkill);
                    skillDurations.put(lowerSkill, currentDuration + experience.durationInMonths);

                    Map<String, Object> detail = new HashMap<>();
                    detail.put("startDate", experience.startDateStr);
                    detail.put("endDate", experience.endDateStr);
                    detail.put("durationInMonths", experience.durationInMonths);

                    skillDetails.get(lowerSkill).add(detail);
                }
            }
        }

        Map<String, Double> skillPercentages = new HashMap<>();
        for (String skill : mandatorySkills) {
            String lowerSkill = skill.toLowerCase();
            int duration = skillDurations.get(lowerSkill);
            int totalExperienceYears = requiredExperienceMap.get(lowerSkill);
            double percentage = duration > totalExperienceYears * 12 ? 100
                    : (double) duration / (totalExperienceYears * 12) * 100;

            skillPercentages.put(lowerSkill, percentage);
        }

        Map<String, Object> result = new HashMap<>();
        for (String skill : mandatorySkills) {
            String lowerSkill = skill.toLowerCase();
            Map<String, Object> skillInfo = new HashMap<>();
            skillInfo.put("totalDuration", skillDurations.get(lowerSkill));
            skillInfo.put("requiredExperience", requiredExperienceMap.get(lowerSkill));
            skillInfo.put("percentage", skillPercentages.get(lowerSkill));
            skillInfo.put("details", skillDetails.get(lowerSkill));
            result.put(lowerSkill, skillInfo);
        }

        double overallPercentage = skillPercentages.values().stream().mapToDouble(Double::doubleValue).sum() / skillPercentages.size();
        result.put("overallPercentage", overallPercentage);

        return result;
    }

    private List<WorkExperience> extractWorkExperiences(String text) {
        List<WorkExperience> experiences = new ArrayList<>();
        Matcher matcher = DatePatternMatcher.getDateMatcher(text);

        while (matcher.find()) {
            String startDateStr = matcher.group(1);
            String endDateStr = matcher.group(2);

            int start = matcher.end();
            String description = extractExperienceDescription(text, start);

            LocalDate startDate = DatePatternMatcher.parseDate(startDateStr);
            LocalDate endDate = DatePatternMatcher.parseDate(endDateStr);
            int durationInMonths = DatePatternMatcher.calculateDurationInMonths(startDate, endDate);

            experiences.add(new WorkExperience(startDateStr, endDateStr, durationInMonths, description));
        }
        return experiences;
    }

    private String extractExperienceDescription(String text, int start) {
        String[] sectionHeaders = {"EDUCATION", "SKILLS", "COURSEWORK", "PROJECTS", "AREAS OF INTEREST"};
        String lowerText = text.toLowerCase();
        int end = lowerText.length();

        Matcher nextDateMatcher = DatePatternMatcher.getDateMatcher(lowerText.substring(start));
        if (nextDateMatcher.find()) {
            end = nextDateMatcher.start() + start;
        }

        for (String header : sectionHeaders) {
            Pattern headerPattern = Pattern.compile(
                    "(^|\\n)\\s*" + Pattern.quote(header.toLowerCase()) + "\\s*[:\\n\\r]",
                    Pattern.CASE_INSENSITIVE
            );
            Matcher headerMatcher = headerPattern.matcher(lowerText.substring(start, end));
            if (headerMatcher.find()) {
                end = headerMatcher.start() + start;
            }
        }

        return text.substring(start, end).trim();
    }

    private static class WorkExperience {
        String startDateStr;
        String endDateStr;
        int durationInMonths;
        String description;

        WorkExperience(String startDateStr, String endDateStr, int durationInMonths, String description) {
            this.startDateStr = startDateStr;
            this.endDateStr = endDateStr;
            this.durationInMonths = durationInMonths;
            this.description = description;
        }
    }



    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private ResumeRepository resumeRepository;

    public ResponseEntity<Map<String, Object>> saveResume(MultipartFile file) {
        try {
            // Save the file to MongoDB using GridFS
            ObjectId fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());

            // Save metadata to MongoDB
            Resume resume = new Resume();
            resume.setFileName(file.getOriginalFilename());
            resume.setContentType(file.getContentType());
            resume.setFileId(fileId.toString());  // Store the fileId
            resumeRepository.save(resume);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("fileId", fileId.toString());
            response.put("fileName", file.getOriginalFilename());
            response.put("contentType", file.getContentType());

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to process the file: " + e.getMessage()));
        }
    }

    public ResponseEntity<byte[]> downloadResume(String fileId) {
        Optional<Resume> resume = resumeRepository.findByFileId(fileId);
        if (resume.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Retrieve the file from GridFS using the stored fileId
        GridFSFile gridFsFile = gridFsTemplate.findOne(query(where("_id").is(new ObjectId(fileId))));
        if (gridFsFile == null) {
            return ResponseEntity.notFound().build();
        }

        GridFsResource resource = gridFsTemplate.getResource(gridFsFile);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] fileData = resource.getInputStream().readAllBytes();

            // Prepare HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(resume.get().getContentType()));
            headers.setContentDisposition(ContentDisposition.builder("inline").filename(resume.get().getFileName()).build());

            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<List<Resume>> getAllResumes() {
        List<Resume> resumes = resumeRepository.findAll();
        return ResponseEntity.ok(resumes);
    }
}
