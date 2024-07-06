package com.example.job_desc_backend.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdmodel.PDAnnotation;
//import org.apache.pdfbox.pdmodel.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@Service
//public class PdfService {
//
//    public List<String> extractEmailsFromPdf(MultipartFile file) {
//        List<String> emailAddresses = new ArrayList<>();
//
//        try (PDDocument document = PDDocument.load(file.getInputStream())) {
//            // Extract emails from direct mailto: links in annotations
//            for (PDPage page : document.getPages()) {
//                for (PDAnnotation annotation : page.getAnnotations()) {
//                    if (annotation instanceof PDAnnotationLink) {
//                        PDAnnotationLink link = (PDAnnotationLink) annotation;
//                        if (link.getAction() instanceof PDActionURI) {
//                            PDActionURI uriAction = (PDActionURI) link.getAction();
//                            String uri = uriAction.getURI();
//                            if (uri != null && uri.startsWith("mailto:")) {
//                                String email = uri.substring(7);  // Strip off 'mailto:'
//                                emailAddresses.add(email);
//                            }
//                        }
//                    }
//                }
//            }
//
//            // Extract emails from plain text in the PDF content
//            PDFTextStripper stripper = new PDFTextStripper();
//            String text = stripper.getText(document);
//
//            List<String> extractedEmails = extractEmails(text);
//            emailAddresses.addAll(extractedEmails);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return emailAddresses;
//    }
//
//    private List<String> extractEmails(String text) {
//        List<String> emails = new ArrayList<>();
//        String emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}";
//
//        Pattern pattern = Pattern.compile(emailRegex);
//        Matcher matcher = pattern.matcher(text);
//
//        while (matcher.find()) {
//            emails.add(matcher.group());
//        }
//
//        return emails;
//    }
//}




@Service
public class PdfService {

    public String extractEmailFromPdf(MultipartFile file) {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            // Extract email from direct mailto: links in annotations
            for (PDPage page : document.getPages()) {
                for (PDAnnotation annotation : page.getAnnotations()) {
                    if (annotation instanceof PDAnnotationLink) {
                        PDAnnotationLink link = (PDAnnotationLink) annotation;
                        if (link.getAction() instanceof PDActionURI) {
                            PDActionURI uriAction = (PDActionURI) link.getAction();
                            String uri = uriAction.getURI();
                            if (uri != null && uri.startsWith("mailto:")) {
                                return uri.substring(7);  // Strip off 'mailto:'
                            }
                        }
                    }
                }
            }

            // Extract email from plain text in the PDF content
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            return extractEmail(text);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null; // Return null if no email address is found
    }

    private String extractEmail(String text) {
        String emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}";

        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group();
        }

        return null; // Return null if no email address is found
    }
}

