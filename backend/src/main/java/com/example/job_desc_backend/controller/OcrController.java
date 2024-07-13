package com.example.job_desc_backend.controller;
//import com.asprise.ocr.Ocr;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//
//@RestController
//public class OcrController {
//
//    @PostMapping("/api/ocr")
//
//    public String DoOCR(@RequestParam("Image") MultipartFile image) throws IOException {
//
//        File file=convert(image);
//        Ocr.setUp();
//        try {
//            Ocr ocr=new Ocr();
//            ocr.startEngine("eng",Ocr.SPEED_FAST);
//            URL imageUrl=file.toURI().toURL();
//            String text=ocr.recognize(new URL[]{imageUrl},Ocr.RECOGNIZE_TYPE_TEXT,Ocr.OUTPUT_FORMAT_PLAINTEXT);
//            ocr.stopEngine();
//            return text;
//        } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
//        }
//
//
//    }
//
//    public static File convert(MultipartFile file) throws IOException {
//        File convFile = new File(file.getOriginalFilename());
//        convFile.createNewFile();
//        FileOutputStream fos = new FileOutputStream(convFile);
//        fos.write(file.getBytes());
//        fos.close();
//        return convFile;
//    }
//
//}
//



import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
public class OcrController {

    @PostMapping("/api/ocr")
    public ResponseEntity<String> performOcr(@RequestParam("image") MultipartFile image) {
        try {
            File file = convert(image);
            BufferedImage bufferedImage = ImageIO.read(file);
            bufferedImage = preprocessImage(bufferedImage);

            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath("/Users/dq-mac-m2-1/Downloads/tesseract-ocr-tesseract-e3f272b/tessdata"); // Set the path to your tessdata directory
            tesseract.setLanguage("eng");
            String text = tesseract.doOCR(bufferedImage);

            file.delete();
            return new ResponseEntity<>(text, HttpStatus.OK);
        } catch (IOException | TesseractException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    private BufferedImage preprocessImage(BufferedImage original) {
        BufferedImage grayscaleImage = new BufferedImage(
                original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        grayscaleImage.getGraphics().drawImage(original, 0, 0, null);
        return grayscaleImage;
    }
}