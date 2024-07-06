package com.example.job_desc_backend.controller;

import com.example.job_desc_backend.service.PdfService;
import com.example.job_desc_backend.service.ResumeService;
import com.itextpdf.text.DocumentException;
import jakarta.mail.MessagingException;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
public class ReportController {
    @Autowired
    ResumeService resumeService;
    @Autowired
    PdfService pdfService;
    @PostMapping("/sendReportEmail")
    public ResponseEntity<String> sendReportEmail(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "jdFile", required = false) MultipartFile jdFile,
            @RequestParam(value = "jdId", required = false) String jdId,
            @RequestParam(value = "email", required = false) String email) {
        try {
            Map<String, Object> reportData = resumeService.generateReportData(file, jdFile, jdId);
            if (reportData == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job description not found");
            }

            byte[] pdfReport = (byte[]) reportData.get("pdfReport");
            String resumeFileName = (String) reportData.get("fileName");

            // Extract candidate email address if possible
            String candidateEmailAddress = pdfService.extractEmailFromPdf(file);
            if (candidateEmailAddress == null || candidateEmailAddress.isEmpty()) {
                if (email == null || email.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to extract email from the resume. Please provide an email address.");
                } else {
                    candidateEmailAddress = email;
                }
            }

            resumeService.sendEmailWithAttachment(candidateEmailAddress, "Subject: Report of your resume", "Please find the attached report.", pdfReport, resumeFileName + "_Feedback.pdf");

            return ResponseEntity.ok("Email sent successfully");

        } catch (IOException | TesseractException | DocumentException | MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }
    }

}
