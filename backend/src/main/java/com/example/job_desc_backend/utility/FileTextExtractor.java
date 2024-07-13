package com.example.job_desc_backend.utility;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.asprise.ocr.Ocr;

import net.sourceforge.tess4j.TesseractException;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.web.multipart.MultipartFile;

public class FileTextExtractor {


    public static String extractTextFromFile(MultipartFile file) throws IOException, TesseractException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IOException("File name is null");
        }

        if (fileName.endsWith(".pdf")) {
            return extractTextFromPdf(file);
        } else if (fileName.endsWith(".doc")) {
            return extractTextFromDoc(file);
        } else if (fileName.endsWith(".docx")) {
            return extractTextFromDocx(file);
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
            return extractTextFromImage(file);
        } else {
            throw new IOException("Unsupported file format");
        }
    }

    private static String extractTextFromPdf(MultipartFile file) throws IOException, TesseractException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            return text;
        }
    }

    private static String extractTextFromImage(MultipartFile file) throws IOException, TesseractException {
        File convertedFile=convert(file);
        Ocr.setUp();
        try {
            Ocr ocr=new Ocr();
            ocr.startEngine("eng",Ocr.SPEED_SLOW);
            URL imageUrl=convertedFile.toURI().toURL();
            String text=ocr.recognize(new URL[]{imageUrl},Ocr.RECOGNIZE_TYPE_TEXT,Ocr.OUTPUT_FORMAT_PLAINTEXT);
            ocr.stopEngine();
            return text;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String extractTextFromDoc(MultipartFile file) throws IOException {
        try (HWPFDocument document = new HWPFDocument(file.getInputStream())) {
            WordExtractor extractor = new WordExtractor(document);
            return extractor.getText();
        }
    }

    private static String extractTextFromDocx(MultipartFile file) throws IOException {
        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            return extractor.getText();
        }
    }

    public static File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
