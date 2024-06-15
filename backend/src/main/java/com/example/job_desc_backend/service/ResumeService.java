package com.example.job_desc_backend.service;
import com.example.job_desc_backend.model.Resume;
import com.example.job_desc_backend.model.Skill;
import com.example.job_desc_backend.repository.ResumeRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            // Extract skill names from mandatorySkills
            List<String> skillNames = new ArrayList<>();
            for (Skill skill : mandatorySkills) {
                skillNames.add(skill.getSkill());
            }

            Map<String, Integer> requiredExperienceMap = new HashMap<>();
            for (Skill skill : mandatorySkills) {
                skillNames.add(skill.getSkill());
                requiredExperienceMap.put(skill.getSkill().toLowerCase(), skill.getExperience());
            }

            // Calculate skill durations and percentages
            Map<String, Object> skillAnalysis = calculateSkillAnalysis(resumeText, skillNames,requiredExperienceMap);

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

            // If text extraction is unsuccessful, try OCR
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
            skillDurations.put(skill.toLowerCase(), 0); // Lowercase for case insensitive matching
            skillDetails.put(skill.toLowerCase(), new ArrayList<>());
        }


        List<WorkExperience> experiences = extractWorkExperiences(resumeText);

        // Calculate total duration and individual skill durations
        for (WorkExperience experience : experiences) {
            totalExperienceMonths += experience.durationInMonths;
            for (String skill : mandatorySkills) {
                String lowerSkill = skill.toLowerCase();
                Pattern pattern = Pattern.compile("\\b" + Pattern.quote(lowerSkill) + "\\b", Pattern.CASE_INSENSITIVE);
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

        // Calculate percentages for each skill
        Map<String, Double> skillPercentages = new HashMap<>();
        for (String skill : mandatorySkills) {
            double percentage;
            String lowerSkill = skill.toLowerCase();
            int duration = skillDurations.get(lowerSkill);
            int totalExperienceYears=requiredExperienceMap.get(lowerSkill);
            if(duration>totalExperienceYears*12){
                percentage=100;

            }
            else {
               int totalRequiredMonth=totalExperienceYears*12;
               percentage=(double) duration/(totalRequiredMonth)*100;
            }

            //double percentage = totalExperienceMonths > 0 ? (double) duration / (totalExperienceMonths) * 100 : 0;
            skillPercentages.put(lowerSkill, percentage);
        }

        // Combine skillDurations, skillDetails, and skillPercentages into the result map
        Map<String, Object> result = new HashMap<>();
        for (String skill : mandatorySkills) {
            String lowerSkill = skill.toLowerCase();
            Map<String, Object> skillInfo = new HashMap<>();
            skillInfo.put("totalDuration", skillDurations.get(lowerSkill));
            skillInfo.put("percentage", skillPercentages.get(lowerSkill));
            skillInfo.put("details", skillDetails.get(lowerSkill));
            result.put(lowerSkill, skillInfo);
        }

        // Add overall percentage

        double sum = 0.0;
        for (double percentage : skillPercentages.values()) {
            sum += percentage;
        }

        double overallPercentage = sum / skillPercentages.size();
        //double overallPercentage = totalExperienceMonths > 0 ? (double) totalExperienceMonths / experiences.size() : 0;
        result.put("overallPercentage", overallPercentage);

        return result;
    }

    private List<WorkExperience> extractWorkExperiences(String text) {
        List<WorkExperience> experiences = new ArrayList<>();
        Pattern datePattern = Pattern.compile(
                "\\b((?:\\w+ \\d{4})|(?:\\w+-\\d{4})|(?:\\d{1,2}/\\d{4})|(?:\\d{1,2}/\\d{1,2}/\\d{2,4})|(?:\\w{3}/\\d{4}))" +
                        "\\s*(?:to|–|-)\\s*" +
                        "((?:\\w+ \\d{4})|(?:\\w+-\\d{4})|(?:\\d{1,2}/\\d{4})|(?:\\d{1,2}/\\d{1,2}/\\d{2,4})|(?:Present)|(?:Till Date)|(?:Current)|(?:CURRENT))\\b",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = datePattern.matcher(text);

        while (matcher.find()) {
            String startDateStr = matcher.group(1);
            String endDateStr = matcher.group(2);

            int start = matcher.end();
            String description = extractExperienceDescription(text, start);

            int durationInMonths = calculateDurationInMonthsFromString(startDateStr, endDateStr);
            experiences.add(new WorkExperience(startDateStr, endDateStr, durationInMonths, description));
        }
        return experiences;
    }

    private int calculateDurationInMonthsFromString(String startDateStr, String endDateStr) {
        // Function to convert month name to month number
        Map<String, Integer> monthMap = new HashMap<>();
        String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String[] abbreviations = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
        String[] abbreviations1 = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String[] numericAbbreviations = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        for (int i = 0; i < months.length; i++) {
            monthMap.put(months[i].toLowerCase(), i + 1); // Ensure lowercase for case insensitive matching
            monthMap.put(abbreviations[i].toLowerCase(), i + 1);
            monthMap.put(abbreviations1[i].toLowerCase(), i + 1);
            monthMap.put(numericAbbreviations[i], i + 1); // Add
            monthMap.put(numericAbbreviations[i], i + 1); // Add numeric abbreviations to the map
        }

        LocalDate startDate = parseDate(startDateStr, monthMap);
        LocalDate endDate = parseDate(endDateStr, monthMap);

        // Calculate duration in months
        int durationInMonths = (int) ChronoUnit.MONTHS.between(startDate, endDate);
        return durationInMonths;
    }

    private LocalDate parseDate(String dateStr, Map<String, Integer> monthMap) {
        if ("Present".equalsIgnoreCase(dateStr) || "Till Date".equalsIgnoreCase(dateStr) || "Current".equalsIgnoreCase(dateStr) || "CURRENT".equalsIgnoreCase(dateStr)) {
            return LocalDate.now();
        }

        DateTimeFormatter formatter;
        if (dateStr.contains("-")) {
            formatter = DateTimeFormatter.ofPattern("MMM-yyyy", Locale.ENGLISH);
        } else if (dateStr.matches("\\d{1,2}/\\d{4}")) {
            formatter = DateTimeFormatter.ofPattern("M/yyyy");
        } else if (dateStr.matches("\\d{1,2}/\\d{1,2}/\\d{2,4}")) {
            formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        } else if (dateStr.matches("\\w{3}/\\d{4}")) {
            formatter = DateTimeFormatter.ofPattern("MMM/yyyy", Locale.ENGLISH);
        } else {
            formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        }

        try {
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            // Fallback to manual parsing
            String[] parts = dateStr.split("[\\s/-]");
            int month = monthMap.getOrDefault(parts[0].toLowerCase(), 1);
            int year = Integer.parseInt(parts[parts.length - 1]);
            return LocalDate.of(year, month, 1);
        }
    }

    private String extractExperienceDescription(String text, int start) {
        String[] sectionHeaders = {"EDUCATION", "SKILLS", "COURSEWORK", "PROJECTS", "AREAS OF INTEREST"};
        String lowerText = text.toLowerCase();
        int end = lowerText.length();
        Matcher nextDateMatcher = Pattern.compile("\\b((?:\\w+ \\d{4})|(?:\\w+-\\d{4})|(?:\\d{1,2}/\\d{4})|(?:\\d{1,2}/\\d{1,2}/\\d{2,4})|(?:\\w{3}/\\d{4}))\\b", Pattern.CASE_INSENSITIVE).matcher(lowerText.substring(start));
        if (nextDateMatcher.find()) {
            end = nextDateMatcher.start() + start;
        }

        for (String header : sectionHeaders) {
            Pattern headerPattern = Pattern.compile("\\b" + Pattern.quote(header.toLowerCase()) + "\\b[\\s:]*", Pattern.CASE_INSENSITIVE);
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
            String fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType()).toString();

            // Save metadata to MongoDB
            Resume resume = new Resume();
            resume.setFileName(file.getOriginalFilename());
            resume.setContentType(file.getContentType());
            resumeRepository.save(resume);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("fileId", fileId);
            response.put("fileName", file.getOriginalFilename());
            response.put("contentType", file.getContentType());

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to process the file: " + e.getMessage()));
        }
    }


    public ResponseEntity<byte[]> downloadResume(String resumeId) {
        Optional<Resume> resume = resumeRepository.findById(resumeId);
        if (resume == null) {
            return ResponseEntity.notFound().build();
        }

        // Retrieve the file from GridFS
        GridFsResource resource = gridFsTemplate.getResource(resume.get().getId());
        if (resource == null) {
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
}
