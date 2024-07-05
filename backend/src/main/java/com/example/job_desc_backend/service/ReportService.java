package com.example.job_desc_backend.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    ResumeService resumeService;



    public ResponseEntity<InputStreamResource> downloadResumeAnalysisAsPdf(MultipartFile file, MultipartFile jdFile, String jdId) {
        ResponseEntity<Map<String, Object>> response = resumeService.uploadResume(file, jdFile, jdId);

        if (!response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(response.getStatusCode()).body(null);
        }

        Map<String, Object> responseData = response.getBody();

        try {
            // Create a PDF document
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            // Set up styles for headers and content
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Font sectionHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
            Font overallPercentageFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.DARK_GRAY);

            // Add content to the PDF
            Paragraph title = new Paragraph("Resume Analysis Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(Chunk.NEWLINE); // Add space after title

            // Add overall percentage
            if (responseData.containsKey("overallPercentage")) {
                double overallPercentage = (double) responseData.get("overallPercentage");
                Paragraph overallPercentageParagraph = new Paragraph("Overall Percentage: " + String.format("%.2f", overallPercentage) + "%", overallPercentageFont);
                overallPercentageParagraph.setAlignment(Element.ALIGN_CENTER);
                document.add(overallPercentageParagraph);

                document.add(Chunk.NEWLINE); // Add space after overall percentage
            } else if (responseData.containsKey("mandatorySkills") && responseData.get("mandatorySkills") instanceof Map) {
                Map<String, Object> mandatorySkills = (Map<String, Object>) responseData.get("mandatorySkills");
                if (mandatorySkills.containsKey("overallPercentage")) {
                    double overallPercentage = (double) mandatorySkills.get("overallPercentage");
                    //Paragraph overallPercentageParagraph = new Paragraph("Overall Match Percentage: " + String.format("%.2f", overallPercentage) + "%", overallPercentageFont);
                    int formattedOverallPercentage = (int) Math.round(overallPercentage);

                    Paragraph overallPercentageParagraph = new Paragraph("Overall Percentage: " + formattedOverallPercentage + "%", overallPercentageFont);
                    overallPercentageParagraph.setAlignment(Element.ALIGN_CENTER);
                    document.add(overallPercentageParagraph);

                    document.add(Chunk.NEWLINE); // Add space after overall percentage
                }
            }

            // Add mandatory skills analysis
            Paragraph mandatorySkillsHeader = new Paragraph("Mandatory Skills Analysis", sectionHeaderFont);
            mandatorySkillsHeader.setAlignment(Element.ALIGN_CENTER);
            mandatorySkillsHeader.setSpacingAfter(20f);
            document.add(mandatorySkillsHeader);
            //document.add(Chunk.NEWLINE);

            PdfPTable mandatorySkillsTable = new PdfPTable(6);
            mandatorySkillsTable.setWidthPercentage(100);
            mandatorySkillsTable.addCell("Mandatory Skill");
            mandatorySkillsTable.addCell("Candidate Experience");
            mandatorySkillsTable.addCell("Required Experience (years)");
            mandatorySkillsTable.addCell("Percentage Match");
            mandatorySkillsTable.addCell("Details");
            mandatorySkillsTable.addCell("Matched Subskills");

            Object mandatorySkillsObj = responseData.get("mandatorySkills");
            if (mandatorySkillsObj instanceof Map) {
                Map<String, Object> mandatorySkills = (Map<String, Object>) mandatorySkillsObj;
                for (Map.Entry<String, Object> entry : mandatorySkills.entrySet()) {
                    String skillName = entry.getKey();
                    Object skillInfoObj = entry.getValue();

                    if (skillInfoObj instanceof Map) {
                        Map<String, Object> skillInfo = (Map<String, Object>) skillInfoObj;

                        mandatorySkillsTable.addCell(skillName);
                        mandatorySkillsTable.addCell(skillInfo.get("totalDuration").toString());
                        mandatorySkillsTable.addCell(skillInfo.get("requiredExperience").toString());
                        //mandatorySkillsTable.addCell(skillInfo.get("percentage").toString() + "%");
                        int percentageMatch = (int) Math.round((double) skillInfo.get("percentage"));
                        mandatorySkillsTable.addCell(percentageMatch + "%");

                        PdfPCell detailsCell = new PdfPCell();
                        List<Map<String, Object>> details = (List<Map<String, Object>>) skillInfo.get("details");
                        if (details != null) {
                            for (Map<String, Object> detail : details) {
                                String detailText = "From " + detail.get("startDate") + " to " + detail.get("endDate") + ": " + detail.get("durationInMonths") + " months";
                                detailsCell.addElement(new Paragraph(detailText));
                            }
                        } else {
                            detailsCell.addElement(new Paragraph("Details not available"));
                        }
                        mandatorySkillsTable.addCell(detailsCell);

                        PdfPCell subskillsCell = new PdfPCell();
                        List<String> matchedSubSkills = (List<String>) skillInfo.get("matchedSubSkills");
                        if (matchedSubSkills != null) {
                            for (String subskill : matchedSubSkills) {
                                subskillsCell.addElement(new Paragraph(subskill));
                            }
                        } else {
                            subskillsCell.addElement(new Paragraph("No subskills"));
                        }
                        mandatorySkillsTable.addCell(subskillsCell);
                    }
                }
            }
            document.add(mandatorySkillsTable);

            // Add IT skills analysis
            document.add(Chunk.NEWLINE); // Add space before IT skills header
            Paragraph itSkillsHeader = new Paragraph("Other Skills Analysis", sectionHeaderFont);
            itSkillsHeader.setAlignment(Element.ALIGN_CENTER);
            itSkillsHeader.setSpacingAfter(20f);
//          //itSkillsHeader.setSpacingAfter(10f);
            document.add(itSkillsHeader);
            //document.add(Chunk.NEWLINE);

            PdfPTable itSkillsTable = new PdfPTable(3);
            itSkillsTable.setWidthPercentage(100);
            itSkillsTable.addCell("Skill");
            itSkillsTable.addCell("Candidate Experience");
            itSkillsTable.addCell("Details");

            Object itSkillsObj = responseData.get("itSkills");
            if (itSkillsObj instanceof Map) {
                Map<String, Object> itSkills = (Map<String, Object>) itSkillsObj;
                for (Map.Entry<String, Object> entry : itSkills.entrySet()) {
                    String skillName = entry.getKey();
                    Object skillInfoObj = entry.getValue();

                    if (skillInfoObj instanceof Map) {
                        Map<String, Object> skillInfo = (Map<String, Object>) skillInfoObj;

                        itSkillsTable.addCell(skillName);
                        itSkillsTable.addCell(skillInfo.get("totalDuration").toString());

                        PdfPCell detailsCell = new PdfPCell();
                        List<Map<String, Object>> details = (List<Map<String, Object>>) skillInfo.get("details");
                        if (details != null) {
                            for (Map<String, Object> detail : details) {
                                String detailText = "From " + detail.get("startDate") + " to " + detail.get("endDate") + ": " + detail.get("durationInMonths") + " months";
                                detailsCell.addElement(new Paragraph(detailText));
                            }
                        } else {
                            detailsCell.addElement(new Paragraph("Details not available"));
                        }
                        itSkillsTable.addCell(detailsCell);
                    }
                }
            }
            document.add(itSkillsTable);

            // Close the document
            document.close();

            // Prepare response with the PDF file
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=resume_analysis.pdf");

            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (DocumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }
}

