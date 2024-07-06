package com.example.job_desc_backend.service;
import com.example.job_desc_backend.dto.Profile_detail_page_response_dto;
import com.example.job_desc_backend.model.Profile_detail_page;
import com.example.job_desc_backend.model.Resume;
import com.example.job_desc_backend.model.Skill;
import com.example.job_desc_backend.model.Skill_Experience;
import com.example.job_desc_backend.repository.Profile_detail_page_repository;
import com.example.job_desc_backend.repository.Skill_ExperienceRepository;
import com.example.job_desc_backend.utility.*;
import com.example.job_desc_backend.repository.ResumeRepository;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.core.io.ByteArrayResource;


//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.layout.element.Table;
//import com.itextpdf.layout.element.Cell;
////
//// import com.itextpdf.layout.property.UnitValue;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class ResumeService {

    @Autowired
    private JdService jdService;
    @Autowired
    Skill_ExperienceRepository skillExperienceRepository;
    @Autowired
    Profile_detail_page_repository profileDetailPageRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    PdfService pdfService;

    public ResponseEntity<Map<String, Object>> uploadResume(MultipartFile file,MultipartFile jdFile, String jdId) {
        try {
//
            List<Skill> mandatorySkills = new ArrayList<>();

            // Check if JD file is provided and extract skills from it
            if (jdFile != null && !jdFile.isEmpty()) {
                String jdText = extractTextFromFile(jdFile);
                if (jdText == null) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(Map.of("error", "Unsupported JD file type"));
                }
                mandatorySkills = extractSkillsFromJDText(jdText);
            }
            // Check if JD ID is provided and fetch skills using the ID
            else if (jdId != null) {
                mandatorySkills = jdService.getMandatorySkills(jdId);;
                if (mandatorySkills == null || mandatorySkills.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "JD not found with the provided ID"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Either JD file or JD ID must be provided"));
            }

            String resumeText = extractTextFromFile(file);

            if (resumeText == null) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(Map.of("error", "Unsupported file type"));
            }

            List<String> skillNames = new ArrayList<>();
            List<Integer> skillExperience = new ArrayList<>();
            Map<String, Integer> requiredExperienceMap = new HashMap<>();
            Map<String, List<String>> subSkillsMap = new HashMap<>();

            for (Skill skill : mandatorySkills) {
                String skillName = skill.getSkill();
                int skillExp = skill.getExperience();
                skillNames.add(skillName);
                skillExperience.add(skillExp);
                requiredExperienceMap.put(skillName.toLowerCase(), skill.getExperience());
                subSkillsMap.put(skillName.toLowerCase(), skill.getSubSkills());
            }


            // Fetch additional IT skills from ITSkillsUtility
            List<String> itSkills = ITSkillsUtility.getAllSkills();

            // Calculate IT skills analysis
            //Map<String, Object> itSkillAnalysis = calculateSkillAnalysis(resumeText, itSkills, new HashMap<>(), new HashMap<>());
            Map<String, Object> itSkillAnalysis = calculateITSkillAnalysis(resumeText, itSkills,skillNames);
            Map<String, Object> mandatorySkillAnalysis = calculateSkillAnalysis(resumeText, skillNames, requiredExperienceMap, subSkillsMap);
            Map<String, Object> result = new HashMap<>();
            result.put("mandatorySkills", mandatorySkillAnalysis);
            result.put("itSkills", itSkillAnalysis);

            return ResponseEntity.ok(result);
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

    private List<Skill> extractSkillsFromJDText(String jdText) {
        List<Skill> skills = new ArrayList<>();

        // Pattern to match lines with skill and experience
        Pattern pattern = Pattern.compile("([a-zA-Z]+)\\s+(\\d+)");
        Matcher matcher = pattern.matcher(jdText);

        while (matcher.find()) {
            String skillName = matcher.group(1);
            int experience = Integer.parseInt(matcher.group(2));

            Skill skill = new Skill();
            skill.setSkill(skillName);
            skill.setExperience(experience);
            skill.setSubSkills(new ArrayList<>()); // Since subSkills are not present

            skills.add(skill);
        }

        return skills;
    }

    private Map<String, Object> calculateSkillAnalysis(String resumeText, List<String> mandatorySkills, Map<String, Integer> requiredExperienceMap, Map<String, List<String>> subSkillsMap) {
        Map<String, Integer> skillDurations = new HashMap<>();
        Map<String, List<Map<String, Object>>> skillDetails = new HashMap<>();
        Map<String, List<String>> matchedSubSkills = new HashMap<>();
        int totalExperienceMonths = 0;

        for (String skill : mandatorySkills) {
            skillDurations.put(skill.toLowerCase(), 0);
            skillDetails.put(skill.toLowerCase(), new ArrayList<>());
            matchedSubSkills.put(skill.toLowerCase(), new ArrayList<>());
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

                List<String> subSkills = subSkillsMap.get(lowerSkill);
                if (subSkills != null) {
                    for (String subSkill : subSkills) {
                        if (experience.description.toLowerCase().contains(subSkill.toLowerCase())) {
                            matchedSubSkills.get(lowerSkill).add(subSkill);
                        }
                    }
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
            int skillDuration_year=skillDurations.get(lowerSkill)/12;
            int skillDuration_months=skillDurations.get(lowerSkill)%12;
            Map<String, Object> skillInfo = new HashMap<>();
            if (skillDuration_year==0 && skillDuration_months>0){
                skillInfo.put("totalDuration",skillDuration_months+" months");
            } else if (skillDuration_year==0 && skillDuration_months==0) {
                skillInfo.put("totalDuration", 0);
            }
            else {
                skillInfo.put("totalDuration", skillDuration_year+" years" +skillDuration_months+" months");
            }
            //skillInfo.put("totalDuration", skillDuration_year+" years" +skillDuration_months+" months");
            skillInfo.put("requiredExperience", requiredExperienceMap.get(lowerSkill));
            skillInfo.put("percentage", skillPercentages.get(lowerSkill));
            skillInfo.put("details", skillDetails.get(lowerSkill));
            skillInfo.put("matchedSubSkills", matchedSubSkills.get(lowerSkill));
            result.put(lowerSkill, skillInfo);
        }

        double overallPercentage = skillPercentages.values().stream().mapToDouble(Double::doubleValue).sum() / skillPercentages.size();
        result.put("overallPercentage", overallPercentage);

        return result;
    }


    private Map<String, Integer> calculateITSkillAnalysis(String resumeText, List<String> itSkills) {
        Map<String, Integer> skillDurations = new HashMap<>();
        Map<String, List<Map<String, Object>>> skillDetails = new HashMap<>();
        Map<String, List<String>> matchedSubSkills = new HashMap<>();
        int totalExperienceMonths = 0;

        // Initialize maps for IT skills
        for (String skill : itSkills) {
            String canonicalSkill = ITSkillsUtility.getCanonicalSkillName(skill);
            skillDurations.put(canonicalSkill.toLowerCase(), 0);
            skillDetails.put(canonicalSkill.toLowerCase(), new ArrayList<>());
            matchedSubSkills.put(canonicalSkill.toLowerCase(), new ArrayList<>());
        }

        // Extract work experiences from resume text
        List<WorkExperience> experiences = extractWorkExperiences(resumeText);

        // Process each work experience
        for (WorkExperience experience : experiences) {
            totalExperienceMonths += experience.durationInMonths;
            Set<String> skillsFoundInExperience = new HashSet<>();
            for (String skill : itSkills) {
                String lowerSkill = skill.toLowerCase();
                String canonicalSkill = ITSkillsUtility.getCanonicalSkillName(skill).toLowerCase();

                // Check if the skill is found in the experience description
                if (experience.description.toLowerCase().contains(lowerSkill) && !skillsFoundInExperience.contains(canonicalSkill)) {
                    skillsFoundInExperience.add(canonicalSkill);
                    int currentDuration = skillDurations.get(canonicalSkill);
                    skillDurations.put(canonicalSkill, currentDuration + experience.durationInMonths);

                    Map<String, Object> detail = new HashMap<>();
                    detail.put("startDate", experience.startDateStr);
                    detail.put("endDate", experience.endDateStr);
                    detail.put("durationInMonths", experience.durationInMonths);

                    skillDetails.get(canonicalSkill).add(detail);
                }
            }
        }

        // Calculate percentages for IT skills
        Map<String, Double> skillPercentages = new HashMap<>();
        for (String skill : itSkills) {
//            String can = skill.toLowerCase();
//            int duration = skillDurations.get(lowerSkill);
//            double percentage = (double) duration / totalExperienceMonths * 100;
//
//            skillPercentages.put(lowerSkill, percentage);

            String canonicalSkill = ITSkillsUtility.getCanonicalSkillName(skill).toLowerCase();
            int duration = skillDurations.get(canonicalSkill);
            double percentage = (double) duration / totalExperienceMonths * 100;

            skillPercentages.put(canonicalSkill, percentage);
        }

        // Prepare result map for IT skills analysis
        Map<String, Integer> result = new HashMap<>();
        for (String skill : itSkills) {



            String canonicalSkill = ITSkillsUtility.getCanonicalSkillName(skill).toLowerCase();
            int skillDurationYears = skillDurations.get(canonicalSkill) / 12;
            int skillDurationMonths = skillDurations.get(canonicalSkill) % 12;

            if (skillDurations.get(canonicalSkill) > 0) {
                Map<String, Object> skillInfo = new HashMap<>();
                if (skillDurationYears == 0 && skillDurationMonths > 0) {
                    skillInfo.put("totalDuration", skillDurationMonths + " months");
                } else if (skillDurationYears == 0 && skillDurationMonths == 0) {
                    skillInfo.put("totalDuration", 0);
                } else {
                    skillInfo.put("totalDuration", skillDurationYears + " years " + skillDurationMonths + " months");
                }
                skillInfo.put("details", skillDetails.get(canonicalSkill));
                result.put(canonicalSkill, skillDurations.get(canonicalSkill));
            }
        }

        return result;
    }

    private Map<String, Object> calculateITSkillAnalysis(String resumeText, List<String> itSkills, List<String> mandatorySkills) {
        Map<String, Integer> skillDurations = new HashMap<>();
        Map<String, List<Map<String, Object>>> skillDetails = new HashMap<>();
        Map<String, List<String>> matchedSubSkills = new HashMap<>();
        int totalExperienceMonths = 0;

        // Create a set of canonical mandatory skills to filter them out from IT skills
        Set<String> canonicalMandatorySkills = mandatorySkills.stream()
                .map(ITSkillsUtility::getCanonicalSkillName)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        // Initialize maps for IT skills, excluding mandatory skills
        for (String skill : itSkills) {
            String canonicalSkill = ITSkillsUtility.getCanonicalSkillName(skill);
            if (!canonicalMandatorySkills.contains(canonicalSkill.toLowerCase())) {
                skillDurations.put(canonicalSkill.toLowerCase(), 0);
                skillDetails.put(canonicalSkill.toLowerCase(), new ArrayList<>());
                matchedSubSkills.put(canonicalSkill.toLowerCase(), new ArrayList<>());
            }
        }

        // Extract work experiences from resume text
        List<WorkExperience> experiences = extractWorkExperiences(resumeText);

        // Process each work experience
        for (WorkExperience experience : experiences) {
            totalExperienceMonths += experience.durationInMonths;
            Set<String> skillsFoundInExperience = new HashSet<>();

            for (String skill : itSkills) {
                String lowerSkill = skill.toLowerCase();
                String canonicalSkill = ITSkillsUtility.getCanonicalSkillName(skill).toLowerCase();

                // Check if the skill is found in the experience description
                if (!canonicalMandatorySkills.contains(canonicalSkill) && experience.description.toLowerCase().contains(lowerSkill) && !skillsFoundInExperience.contains(canonicalSkill)) {
                    skillsFoundInExperience.add(canonicalSkill);
                    int currentDuration = skillDurations.get(canonicalSkill);
                    skillDurations.put(canonicalSkill, currentDuration + experience.durationInMonths);

                    Map<String, Object> detail = new HashMap<>();
                    detail.put("startDate", experience.startDateStr);
                    detail.put("endDate", experience.endDateStr);
                    detail.put("durationInMonths", experience.durationInMonths);

                    skillDetails.get(canonicalSkill).add(detail);
                }
            }
        }

        // Calculate percentages for IT skills
        Map<String, Double> skillPercentages = new HashMap<>();
        for (String skill : itSkills) {
            String canonicalSkill = ITSkillsUtility.getCanonicalSkillName(skill).toLowerCase();
            if (!canonicalMandatorySkills.contains(canonicalSkill)) {
                int duration = skillDurations.get(canonicalSkill);
                double percentage = (double) duration / totalExperienceMonths * 100;
                skillPercentages.put(canonicalSkill, percentage);
            }
        }

        // Prepare result map for IT skills analysis
        Map<String, Object> result = new HashMap<>();
        for (String skill : itSkills) {
            String canonicalSkill = ITSkillsUtility.getCanonicalSkillName(skill).toLowerCase();
            if (!canonicalMandatorySkills.contains(canonicalSkill)) {
                int skillDurationYears = skillDurations.get(canonicalSkill) / 12;
                int skillDurationMonths = skillDurations.get(canonicalSkill) % 12;

                if (skillDurations.get(canonicalSkill) > 0) {
                    Map<String, Object> skillInfo = new HashMap<>();
                    if (skillDurationYears == 0 && skillDurationMonths > 0) {
                        skillInfo.put("totalDuration", skillDurationMonths + " months");
                    } else if (skillDurationYears == 0 && skillDurationMonths == 0) {
                        skillInfo.put("totalDuration", 0);
                    } else {
                        skillInfo.put("totalDuration", skillDurationYears + " years " + skillDurationMonths + " months");
                    }
                    skillInfo.put("details", skillDetails.get(canonicalSkill));
                    result.put(canonicalSkill, skillInfo);
                }
            }
        }

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
        String[] sectionHeaders = {"EDUCATION", "SKILLS", "COURSEWORK", "PROJECTS","PROJECT", "AREAS OF INTEREST"};
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


//
//    public ResponseEntity<InputStreamResource> downloadResumeAnalysisAsPdf(MultipartFile file, MultipartFile jdFile, String jdId) {
//        ResponseEntity<Map<String, Object>> response = uploadResume(file, jdFile, jdId);
//
//        if (!response.getStatusCode().is2xxSuccessful()) {
//            return ResponseEntity.status(response.getStatusCode()).body(null);
//        }
//
//        Map<String, Object> responseData = response.getBody();
//
//        try {
//            // Create a PDF document
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
//            PdfDocument pdf = new PdfDocument(writer);
//            Document document = new Document(pdf);
//
//            // Add content to the PDF
//            document.add(new Paragraph("Resume Analysis Report"));
//
//            // Add mandatory skills analysis
//            Map<String, Object> mandatorySkills = (Map<String, Object>) responseData.get("mandatorySkills");
//            document.add(new Paragraph("Mandatory Skills Analysis"));
//
//            Table mandatorySkillsTable = new Table(UnitValue.createPercentArray(new float[]{3, 3, 3, 3}));
//            mandatorySkillsTable.setWidth(UnitValue.createPercentValue(100));
//            mandatorySkillsTable.addHeaderCell("Skill");
//            mandatorySkillsTable.addHeaderCell("Total Duration");
//            mandatorySkillsTable.addHeaderCell("Required Experience");
//            mandatorySkillsTable.addHeaderCell("Percentage");
//
//            for (Map.Entry<String, Object> entry : mandatorySkills.entrySet()) {
//                String skillName = entry.getKey();
//                Map<String, Object> skillInfo = (Map<String, Object>) entry.getValue();
//                mandatorySkillsTable.addCell(new Cell().add(new Paragraph(skillName)));
//                mandatorySkillsTable.addCell(new Cell().add(new Paragraph(skillInfo.get("totalDuration").toString())));
//                mandatorySkillsTable.addCell(new Cell().add(new Paragraph(skillInfo.get("requiredExperience").toString())));
//                mandatorySkillsTable.addCell(new Cell().add(new Paragraph(skillInfo.get("percentage").toString() + "%")));
//            }
//            document.add(mandatorySkillsTable);
//
//            // Add IT skills analysis
//            Map<String, Object> itSkills = (Map<String, Object>) responseData.get("itSkills");
//            document.add(new Paragraph("IT Skills Analysis"));
//
//            Table itSkillsTable = new Table(UnitValue.createPercentArray(new float[]{3, 3}));
//            itSkillsTable.setWidth(UnitValue.createPercentValue(100));
//            itSkillsTable.addHeaderCell("Skill");
//            itSkillsTable.addHeaderCell("Total Duration");
//
//            for (Map.Entry<String, Object> entry : itSkills.entrySet()) {
//                String skillName = entry.getKey();
//                Map<String, Object> skillInfo = (Map<String, Object>) entry.getValue();
//                itSkillsTable.addCell(new Cell().add(new Paragraph(skillName)));
//                itSkillsTable.addCell(new Cell().add(new Paragraph(skillInfo.get("totalDuration").toString())));
//            }
//            document.add(itSkillsTable);
//
//            // Close the document
//            document.close();
//
//            // Prepare response with the PDF file
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Content-Disposition", "inline; filename=resume_analysis.pdf");
//
//            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .contentType(MediaType.APPLICATION_PDF)
//                    .body(resource);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
















    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private ResumeRepository resumeRepository;

    public ResponseEntity<Map<String, Object>> saveResume(MultipartFile file) {
        try {
            // Save the file to MongoDB using GridFS
            ObjectId fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
            //Now i also wanted to create a profile_detail_page so i will create a profile_detail_object
            Profile_detail_page profileDetailPage=new Profile_detail_page();

            // Save metadata to MongoDB
            Resume resume = new Resume();
            resume.setFileName(file.getOriginalFilename());
            resume.setContentType(file.getContentType());
            resume.setFileId(fileId.toString());  // Store the fileId
            resume.setUploadDate(LocalDate.now());
//            Resume savedResume=resumeRepository.save(resume);
//            String savedResumeId= savedResume.getId();

            //Now here i am going write logic for matching it skills
            //first thing is to read the file
            String resumeText = extractTextFromFile(file);
            //Now we want the name of the candidate
            // Split the string into lines
            String[] lines = resumeText.split("\n");

            // Get the first line which is the name
            String name = lines[0];

            String candidateName=name;
            resume.setCandidateName(candidateName);
            Resume savedResume=resumeRepository.save(resume);
            String savedResumeCandidateName= savedResume.getCandidateName();
            String savedResumeId= savedResume.getId();
            //now we have to call the skills from utility class
            // Fetch additional IT skills from ITSkillsUtility
            List<String> itSkills = ITSkillsUtility.getAllSkills();
            Map<String, Integer> itSkillAnalysis = calculateITSkillAnalysis(resumeText, itSkills);
            //Now use this itSkillAnalysis to profile_detail_page
            profileDetailPage.setProfile_skill_map(itSkillAnalysis);
            profileDetailPage.setCandidateName(name);
            profileDetailPage.setResumeId(savedResumeId);
            profileDetailPage.setUploadedDate(String.valueOf(new Date()));
            profileDetailPageRepository.save(profileDetailPage);


            Skill_Experience skillExperience=new Skill_Experience();
            skillExperience.setResumeId(savedResumeId);
            skillExperience.setDate(new Date());
            for (Map.Entry<String, Integer> entry : itSkillAnalysis.entrySet()) {
                String skillName = entry.getKey();
                Integer experience = entry.getValue();

                //create a class of skill experience
//                Skill_Experience skillExperience=new Skill_Experience();
//                skillExperience.setResumeId(savedResumeId);
//                skillExperience.setDate(new Date());
                // Step 2: Set the skill attribute dynamically using reflection or method name construction
                switch (skillName.toLowerCase()) {
                    case "java":
                        skillExperience.setJava(experience);
                        break;
                    case "python":
                        skillExperience.setPython(experience);
                        break;
                    case "javascript":
                        skillExperience.setJavascript(experience);
                        break;
                    case "csharp":
                        //skillExperience.setCSharp(experience);
                        skillExperience.setcSharp(experience);
                        break;
                    case "cplusplus":
                        //skillExperience.setCPlusPlus(experience);
                        skillExperience.setcPlusPlus(experience);
                        break;
                    case "php":
                        skillExperience.setPhp(experience);
                        break;
                    case "swift":
                        skillExperience.setSwift(experience);
                        break;
                    case "kotlin":
                        skillExperience.setKotlin(experience);
                        break;
                    case "sql":
                        skillExperience.setSql(experience);
                        break;
                    case "html":
                        skillExperience.setHtml(experience);
                        break;
                    case "css":
                        skillExperience.setCss(experience);
                        break;
                    case "react":
                        skillExperience.setReact(experience);
                        break;
                    case "angular":
                        skillExperience.setAngular(experience);
                        break;
                    case "node.js":
                        skillExperience.setNodeJs(experience);
                        break;
                    case "spring":
                        skillExperience.setSpring(experience);
                        break;
                    case "django":
                        skillExperience.setDjango(experience);
                        break;
                    case "flask":
                        skillExperience.setFlask(experience);
                        break;
                    case "ruby":
                        skillExperience.setRuby(experience);
                        break;
                    case "rails":
                        skillExperience.setRails(experience);
                        break;
                    case "machine learning":
                        skillExperience.setMachineLearning(experience);
                        break;
                    case "data science":
                        skillExperience.setDataScience(experience);
                        break;
                    case "aws":
                        skillExperience.setAws(experience);
                        break;
                    case "azure":
                        skillExperience.setAzure(experience);
                        break;
                    case "docker":
                        skillExperience.setDocker(experience);
                        break;
                    case "kubernetes":
                        skillExperience.setKubernetes(experience);
                        break;
                    case "git":
                        skillExperience.setGit(experience);
                        break;
                    case "ci/cd":
                        skillExperience.setCiCd(experience);
                        break;
                    case "agile":
                        skillExperience.setAgile(experience);
                        break;
                    case "scrum":
                        skillExperience.setScrum(experience);
                        break;
                    case "jira":
                        skillExperience.setJira(experience);
                        break;
                    case "devops":
                        skillExperience.setDevOps(experience);
                        break;
                    case "hadoop":
                        skillExperience.setHadoop(experience);
                        break;
                    case "spark":
                        skillExperience.setSpark(experience);
                        break;
                    case "tableau":
                        skillExperience.setTableau(experience);
                        break;
                    case "powerBi":
                        //skillExperience.setPowerBi(experience);
                        skillExperience.setPowerBI(experience);
                        break;
                    case "tensorflow":
                        skillExperience.setTensorflow(experience);
                        break;
                    case "keras":
                        skillExperience.setKeras(experience);
                        break;
                    case "pandas":
                        skillExperience.setPandas(experience);
                        break;
                    case "numpy":
                        //skillExperience.setNumPy(experience);
                        skillExperience.setNumpy(experience);
                        break;
                    case "matplotlib":
                        skillExperience.setMatplotlib(experience);
                        break;
                    default:
                        // Handle unknown skills or skip them
                        System.err.println("Unhandled skill: " + skillName);
                        continue;  // Skip this skill
                }










            }
            skillExperienceRepository.save(skillExperience);


            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("fileId", fileId.toString());
            response.put("fileName", file.getOriginalFilename());
            response.put("contentType", file.getContentType());
            response.put("Candidatename",savedResume.getCandidateName());

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to process the file: " + e.getMessage()));
        } catch (TesseractException e) {
            throw new RuntimeException(e);
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

    public MultipartFile getResumeAsMultipartFile(String fileId) {
        Optional<Resume> resume = resumeRepository.findByFileId(fileId);
        if (resume.isEmpty()) {
            return null;
        }

        GridFSFile gridFsFile = gridFsTemplate.findOne(query(where("_id").is(new ObjectId(fileId))));
        if (gridFsFile == null) {
            return null;
        }

        GridFsResource resource = gridFsTemplate.getResource(gridFsFile);
        if (!resource.exists()) {
            return null;
        }

        try {
            byte[] fileData = resource.getInputStream().readAllBytes();
            return new CustomMultipartFile(fileData, resume.get().getFileName(), resume.get().getContentType());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }












    public ResponseEntity<InputStreamResource> viewResume(String fileId) {
        //String fileId=resumeRepository.findById(resumeId).get().getFileId();
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
            InputStreamResource inputStreamResource = new InputStreamResource(resource.getInputStream());

            // Prepare HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(resume.get().getContentType()));
            headers.setContentDisposition(ContentDisposition.builder("inline").filename(resume.get().getFileName()).build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(inputStreamResource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<List<Resume>> getAllResumes() {
        List<Resume> resumes = resumeRepository.findAll();
        return ResponseEntity.ok(resumes);
    }


    public ResponseEntity<byte[]> generateReport(MultipartFile file,MultipartFile jdFile, String jdId) {
        try {

            List<Skill> mandatorySkills = new ArrayList<>();

            // Check if JD file is provided and extract skills from it
            if (jdFile != null && !jdFile.isEmpty()) {
                String jdText = extractTextFromFile(jdFile);

                mandatorySkills = extractSkillsFromJDText(jdText);
            }
            // Check if JD ID is provided and fetch skills using the ID
            else if (jdId != null) {
                mandatorySkills = jdService.getMandatorySkills(jdId);;
                if (mandatorySkills == null || mandatorySkills.isEmpty()) {

                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
            }

            // Extract text from the file
            String resumeText = extractTextFromFile(file);
            //Extract candidate email id
            //String candidateEmailAddress = email;

            // Calculate skill analysis
            List<String> skillNames = new ArrayList<>();
            List<Integer> skillExperience = new ArrayList<>();
            Map<String, Integer> requiredExperienceMap = new HashMap<>();
            Map<String, List<String>> subSkillsMap = new HashMap<>();

            for (Skill skill : mandatorySkills) {
                skillNames.add(skill.getSkill());
                requiredExperienceMap.put(skill.getSkill().toLowerCase(), skill.getExperience());
                subSkillsMap.put(skill.getSkill().toLowerCase(), skill.getSubSkills());
            }

            Map<String, Object> skillAnalysis = calculateSkillAnalysis(resumeText, skillNames, requiredExperienceMap, subSkillsMap);

            // Generate PDF report
            byte[] pdfReport = generatePdfReport(skillAnalysis, requiredExperienceMap);
            //Generate email
//            candidateEmailAddress=pdfService.extractEmailFromPdf(file);
//            if (candidateEmailAddress!=null) {
//                sendEmailWithAttachment(candidateEmailAddress, "Subject: Report of your resume", "Please find the attached report.", pdfReport, file.getOriginalFilename() + "_Feedback.pdf");
//            }
            // Prepare response
            String resumeFileName = file.getOriginalFilename();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("inline").filename(resumeFileName+"_"+"Feedback.pdf").build());

            return new ResponseEntity<>(pdfReport, headers, HttpStatus.OK);

        } catch (IOException | TesseractException | DocumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public void sendEmailWithAttachment(String to, String subject, String text, byte[] attachment, String attachmentName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        InputStreamSource attachmentSource = new ByteArrayResource(attachment);
        helper.addAttachment(attachmentName, attachmentSource);
        mailSender.send(message);
    }


    private String generateFeedbackFileName(String resumeFileName) {
        if (resumeFileName != null && resumeFileName.contains(".")) {
            int lastDotIndex = resumeFileName.lastIndexOf(".");
            return resumeFileName.substring(0, lastDotIndex) + "_feedback" + resumeFileName.substring(lastDotIndex);
        } else {
            return "feedback_report.pdf";
        }
    }

    private byte[] generatePdfReport(Map<String, Object> skillAnalysis, Map<String, Integer> requiredExperienceMap) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);

        // Add header event
        HeaderFooterPageEvent event = new HeaderFooterPageEvent();
        writer.setPageEvent(event);

        document.open();

        document.add(new Paragraph("\n\n"));

        // Create table for skill analysis
        PdfPTable table = new PdfPTable(5); // 5 columns: Skill, Required Experience, Total Duration, Percentage, Experience Needed
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Define column widths
        float[] columnWidths = {2f, 2f, 2f, 2f, 2f};
        table.setWidths(columnWidths);

        // Define table header
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        PdfPCell cell;

        cell = new PdfPCell(new Paragraph("Skill", headerFont));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("Required Experience (years)", headerFont));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("Total Duration", headerFont));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("Percentage", headerFont));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Paragraph("Experience Needed (years)", headerFont));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        // Define table data
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        for (String skill : skillAnalysis.keySet()) {
            if (skill.equals("overallPercentage")) {
                continue;
            }

            Map<String, Object> skillInfo = (Map<String, Object>) skillAnalysis.get(skill);
            int requiredExperience = requiredExperienceMap.get(skill.toLowerCase());
            String totalDuration = skillInfo.get("totalDuration").toString();
            //int totalDuration_int=skillInfo.get("to")
            double percentage = (double) skillInfo.get("percentage");

            cell = new PdfPCell(new Paragraph(skill, dataFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph(String.valueOf(requiredExperience), dataFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph(totalDuration, dataFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Paragraph(String.format("%.2f%%", percentage), dataFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            //int experienceNeeded = requiredExperience - Integer.parseInt(totalDuration.split(" ")[0]) * 12;
            int experienceNeeded =  requiredExperience - Integer.parseInt(totalDuration.split(" ")[0]);
            cell = new PdfPCell(new Paragraph(experienceNeeded > 0 ? String.valueOf(experienceNeeded) : "0", dataFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        document.add(table);

        //double overallPercentage = (double) skillAnalysis.get("overallPercentage");
        Number numberOverallPercentage=(Number) skillAnalysis.get("overallPercentage");
        double overallPercentage=numberOverallPercentage.doubleValue();
        document.add(new Paragraph("\nOverall Percentage: " + String.format("%.2f%%", overallPercentage)));

        document.close();
        return byteArrayOutputStream.toByteArray();
    }




    public List<ResumeInfo> ReleventProfiles(String jdId) {
        List<Skill> mandatorySkills = jdService.getMandatorySkills(jdId);
        List<String> skillNames = new ArrayList<>();
        List<Integer> skillExperience = new ArrayList<>();
        Map<String, Integer> requiredExperienceMap = new HashMap<>();
        List<ResumeInfo> list=new ArrayList<>();

        for (Skill skill : mandatorySkills) {
            String skillName = skill.getSkill();
            String canonicalSkill = ITSkillsUtility.getCanonicalSkillName(skillName).toLowerCase();
            int skillExp = skill.getExperience();
            skillNames.add(canonicalSkill);
            skillExperience.add(skillExp);
            requiredExperienceMap.put(canonicalSkill, skill.getExperience());
            //subSkillsMap.put(skillName.toLowerCase(), skill.getSubSkills());
        }
        //Now get all resumes
        List<Resume> resumes = resumeRepository.findAll();
        List<String> resumeid = new ArrayList<>();
        //get all the resume id
        for (Resume resume : resumes) {
            resumeid.add(resume.getId());
        }
        //now check all the resume skill experience using resumeid

        List<String> relevantResumeIds = new ArrayList<>();
        List<String> resumeName = new ArrayList<>();
        for (String resumeId : resumeid) {
            Optional<Skill_Experience> optionalSkillExperience = skillExperienceRepository.findByResumeId(resumeId);
            int overAllPercentage=0;
            Map<String,Integer> skillPercentageMap=new HashMap<>();
            if (optionalSkillExperience.isPresent()) {
                Skill_Experience skillExperience_for_analysis = optionalSkillExperience.get();
                boolean isRelevant = true;
                int percentage=0;

                for (Map.Entry<String, Integer> entry : requiredExperienceMap.entrySet()) {
                    String skillName = entry.getKey();
                    int requiredExp = entry.getValue() * 12;

                    Integer candidateExp = getSkillExperience(skillExperience_for_analysis, skillName);
                    if (candidateExp!=null){
                        if (candidateExp>requiredExp){
                            percentage=100;
                        }
                        else {
                            percentage = (candidateExp * 100) / requiredExp;
                        }

                        skillPercentageMap.put(skillName,percentage);
                    }

                    if (candidateExp == null || candidateExp < requiredExp/2) {
                        isRelevant = false;
                        break;
                    }
                }

                if (isRelevant) {

                    int sum = 0;
                    for (int value : skillPercentageMap.values()) {
                        sum += value;
                    }

                    // Get the size of the map
                    int size = skillPercentageMap.size();
                    // Get the percentage now
                    overAllPercentage=sum/size;
                    //get the uploaded data of profile
                    String uploadedDate= String.valueOf(resumeRepository.findById(resumeId).get().getUploadDate());
                    String [] divideDate=uploadedDate.split("T");
                    String onlyDate=divideDate[0];

                    //resumeName.add(resumeRepository.findById(resumeId).get().getFileName());
                    String resumeFileId=resumeRepository.findById(resumeId).get().getFileId();
                    ResumeInfo resumeInfo=new ResumeInfo(resumeFileId,resumeRepository.findById(resumeId).get().getCandidateName(),overAllPercentage,skillPercentageMap,onlyDate);
                    list.add(resumeInfo);
                }
            }
        }
        return list;
    }

//    public List<ResumeInfo> ReleventProfiles(String jdId){
//        List<Skill> mandatorySkills = jdService.getMandatorySkills(jdId);
//        List<String> skillNames = new ArrayList<>();
//        List<Integer> skillExperience = new ArrayList<>();
//        Map<String, Integer> requiredExperienceMap = new HashMap<>();
//
//        for (Skill skill : mandatorySkills) {
//            String skillName = skill.getSkill();
//            String canonicalSkill = ITSkillsUtility.getCanonicalSkillName(skillName).toLowerCase();
//            int skillExp = skill.getExperience();
//            skillNames.add(canonicalSkill);
//            skillExperience.add(skillExp);
//            requiredExperienceMap.put(canonicalSkill, skill.getExperience());
//        }
//
//        // Now get all resumes
//        List<Resume> resumes = resumeRepository.findAll();
//        List<String> resumeid = new ArrayList<>();
//
//        // Get all the resume IDs
//        for (Resume resume : resumes){
//            resumeid.add(resume.getId());
//        }
//
//        // Now check all the resume skill experience using resumeId
//        List<ResumeInfo> relevantResumeInfos = new ArrayList<>();
//
//        for (String resumeId : resumeid) {
//            Optional<Skill_Experience> optionalSkillExperience = skillExperienceRepository.findByResumeId(resumeId);
//            if (optionalSkillExperience.isPresent()) {
//                Skill_Experience skillExperience_for_analysis = optionalSkillExperience.get();
//                boolean isRelevant = true;
//
//                for (Map.Entry<String, Integer> entry : requiredExperienceMap.entrySet()) {
//                    String skillName = entry.getKey();
//                    int requiredExp = entry.getValue() * 12;
//
//                    Integer candidateExp = getSkillExperience(skillExperience_for_analysis, skillName);
//
//                    if (candidateExp == null || candidateExp < requiredExp) {
//                        isRelevant = false;
//                        break;
//                    }
//                }
//
//                if (isRelevant) {
//                    Resume resume = resumeRepository.findById(resumeId).orElse(null);
//                    if (resume != null) {
//                        String fileName = resume.getFileName();
//                        relevantResumeInfos.add(new ResumeInfo(resumeId, fileName));
//                    }
//                }
//            }
//        }
//        return relevantResumeInfos;
//    }



    private Integer getSkillExperience(Skill_Experience skillExperience, String skillName) {
        switch (skillName.toLowerCase()) {
            case "java": return skillExperience.getJava();
            case "python": return skillExperience.getPython();
            case "javascript": return skillExperience.getJavascript();
            case "c#": return skillExperience.getcSharp();
            case "c++": return skillExperience.getcPlusPlus();
            case "php": return skillExperience.getPhp();
            case "swift": return skillExperience.getSwift();
            case "kotlin": return skillExperience.getKotlin();
            case "sql": return skillExperience.getSql();
            case "html": return skillExperience.getHtml();
            case "css": return skillExperience.getCss();
            case "react": return skillExperience.getReact();
            case "angular": return skillExperience.getAngular();
            case "nodejs": return skillExperience.getNodeJs();
            case "spring": return skillExperience.getSpring();
            case "django": return skillExperience.getDjango();
            case "flask": return skillExperience.getFlask();
            case "ruby": return skillExperience.getRuby();
            case "rails": return skillExperience.getRails();
            case "machine learning": return skillExperience.getMachineLearning();
            case "data science": return skillExperience.getDataScience();
            case "aws": return skillExperience.getAws();
            case "azure": return skillExperience.getAzure();
            case "docker": return skillExperience.getDocker();
            case "kubernetes": return skillExperience.getKubernetes();
            case "git": return skillExperience.getGit();
            case "ci/cd": return skillExperience.getCiCd();
            case "agile": return skillExperience.getAgile();
            case "scrum": return skillExperience.getScrum();
            case "jira": return skillExperience.getJira();
            case "devops": return skillExperience.getDevOps();
            case "hadoop": return skillExperience.getHadoop();
            case "spark": return skillExperience.getSpark();
            case "tableau": return skillExperience.getTableau();
            case "power bi": return skillExperience.getPowerBI();
            case "tensorflow": return skillExperience.getTensorflow();
            case "keras": return skillExperience.getKeras();
            case "pandas": return skillExperience.getPandas();
            case "numpy": return skillExperience.getNumpy();
            case "matplotlib": return skillExperience.getMatplotlib();
            default: return null;
        }
    }



    public Profile_detail_page_response_dto getProfileInformation(String fileId){
       String resumeId= resumeRepository.findByFileId(fileId).get().getId();
       Profile_detail_page profileDetailPage=profileDetailPageRepository.findByResumeId(resumeId);
       Profile_detail_page_response_dto profileDetailPageResponseDto=new Profile_detail_page_response_dto();
       profileDetailPageResponseDto.setResumeId(resumeId);
       profileDetailPageResponseDto.setCandidateName(profileDetailPage.getCandidateName());
       profileDetailPageResponseDto.setProfile_skill_map(profileDetailPage.getProfile_skill_map());
       profileDetailPageResponseDto.setUploadedDate(profileDetailPage.getUploadedDate());
       return profileDetailPageResponseDto;
    }

    public static String extractEmail(String text) {
        String emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group();
        }

        return null; // Return null if no email address is found
    }

    public Map<String, Object> generateReportData(MultipartFile file, MultipartFile jdFile, String jdId) throws IOException, TesseractException, DocumentException {
        List<Skill> mandatorySkills = new ArrayList<>();

        // Check if JD file is provided and extract skills from it
        if (jdFile != null && !jdFile.isEmpty()) {
            String jdText = extractTextFromFile(jdFile);
            mandatorySkills = extractSkillsFromJDText(jdText);
        }
        // Check if JD ID is provided and fetch skills using the ID
        else if (jdId != null) {
            mandatorySkills = jdService.getMandatorySkills(jdId);
            if (mandatorySkills == null || mandatorySkills.isEmpty()) {
                return null;
            }
        }

        // Extract text from the file
        String resumeText = extractTextFromFile(file);

        // Calculate skill analysis
        List<String> skillNames = new ArrayList<>();
        List<Integer> skillExperience = new ArrayList<>();
        Map<String, Integer> requiredExperienceMap = new HashMap<>();
        Map<String, List<String>> subSkillsMap = new HashMap<>();

        for (Skill skill : mandatorySkills) {
            skillNames.add(skill.getSkill());
            requiredExperienceMap.put(skill.getSkill().toLowerCase(), skill.getExperience());
            subSkillsMap.put(skill.getSkill().toLowerCase(), skill.getSubSkills());
        }

        Map<String, Object> skillAnalysis = calculateSkillAnalysis(resumeText, skillNames, requiredExperienceMap, subSkillsMap);

        // Generate PDF report
        byte[] pdfReport = generatePdfReport(skillAnalysis, requiredExperienceMap);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("pdfReport", pdfReport);
        response.put("fileName", file.getOriginalFilename());

        return response;
    }


}
