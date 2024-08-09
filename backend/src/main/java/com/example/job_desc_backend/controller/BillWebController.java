package com.example.job_desc_backend.controller;



import com.example.job_desc_backend.model.BillEntity;
import com.example.job_desc_backend.service.ExportImportService;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.*;

import com.example.job_desc_backend.model.Billpdf;
import com.example.job_desc_backend.repository.BillMongoRepository;
import com.example.job_desc_backend.repository.BillPdfRepository;
import com.example.job_desc_backend.service.BillDataService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.poi.hwpf.model.FileInformationBlock.logger;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RestController
@RequestMapping("/api/billing")
public class BillWebController {

    @Autowired
    private BillDataService billService;

    @PostMapping
    public ResponseEntity<BillEntity> createBill(@RequestBody BillEntity bill) {
        BillEntity savedBill = billService.saveBill(bill);
        return ResponseEntity.ok(savedBill);
    }

    @GetMapping
    public ResponseEntity<List<BillEntity>> getAllBills() {
        List<BillEntity> bills = billService.getAllBills();
        return ResponseEntity.ok(bills);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillEntity> updateBill(@PathVariable String id, @RequestBody BillEntity updatedBill) {
        BillEntity existingBill = billService.getBillById(id);
        if (existingBill == null) {
            return ResponseEntity.notFound().build();
        }

        updatedBill.setId(id);
        BillEntity savedBill = billService.saveBill(updatedBill);
        return ResponseEntity.ok(savedBill);
    }



    @GetMapping("/{id}")
    public ResponseEntity<BillEntity> getBillId(@PathVariable String id) {
        Optional<BillEntity> billOpt = billRepository.findById(id);
        if (billOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(billOpt.get());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable String id) {
        billService.deleteBill(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getBillById")
    public ResponseEntity<BillEntity> getBillById(@RequestParam String id){
        BillEntity bill=billRepository.findById(id).get();
        return ResponseEntity.ok(bill);
    }

   @GetMapping("/export/bills")
    public ResponseEntity<byte[]> exportProfilesToExcel() {
        try {
            List<BillEntity> profiles = billRepository.findAll();
            ExportImportService<BillEntity> exporter = new ExportImportService<>();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            exporter.exportToExcel(profiles, baos);

            byte[] excelBytes = baos.toByteArray();
            System.out.println("Excel file size: " + excelBytes.length);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=profiles_export.xlsx");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(("Failed to create Excel file: " + e.getMessage()).getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/import/bills")
    public ResponseEntity<?> importProfilesFromCSV(@RequestParam("file") MultipartFile file) {
        try {
            ExportImportService<BillEntity> importer = new ExportImportService<>();
            List<BillEntity> profiles = importer.importFromCSV(file, BillEntity.class);
            billRepository.saveAll(profiles);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to import CSV file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }




    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Long>> getDashboardStats() {
        long totalBills = billRepository.count();
        long totalToBeClaimed = billRepository.countByClientReimbursed(false);
        long pendingReimbursements = billRepository.countByReimbursed(false);

        Map<String, Long> stats = new HashMap<>();
        stats.put("totalBills", totalBills);
        stats.put("totalToBeClaimed", totalToBeClaimed);
        stats.put("pendingReimbursements", pendingReimbursements);

        return ResponseEntity.ok(stats);
    }

    @Autowired
    BillPdfRepository billPdfRepository;
    @Autowired
    GridFsTemplate gridFsTemplate;



    @Autowired
    private BillMongoRepository billRepository;

    @PostMapping("/updateBill/{id}")
    public ResponseEntity<BillEntity> uploadBill(
            @PathVariable String id,
            @RequestPart("bill") BillEntity bill,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        // Verify if the bill with the given ID exists
        BillEntity existingBill = billService.getBillById(id);
        if (existingBill == null) {
            return ResponseEntity.notFound().build();
        }

        // If a file is provided, process and store it
        if (file != null && !file.isEmpty()) {
            // Store the file in GridFS and get the file ID
            ObjectId fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());

            // Create a Billpdf object and set its properties
            Billpdf billpdf = new Billpdf();
            billpdf.setFileId(fileId.toString());
            billpdf.setContentType(file.getContentType());
            billpdf.setFileName(file.getOriginalFilename());

            // Save the Billpdf object in the repository
            billPdfRepository.save(billpdf);

            // Set the file ID in the bill entity
            bill.setFileId(fileId.toString());
        }

        // Ensure the ID of the updated bill matches the existing one
        bill.setId(id);

        // Save the updated bill entity
        BillEntity savedBill = billRepository.save(bill);

        // Return the saved bill entity with a 200 OK response
        return ResponseEntity.ok(savedBill);
    }





    @GetMapping("/viewPdf/{fileId}")
    public ResponseEntity<InputStreamResource> viewBillPdf(@PathVariable String fileId) {
        // Retrieve the Billpdf document from the repository
        Optional<Billpdf> billPdfOpt = billPdfRepository.findByFileId(fileId);
        if (billPdfOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Billpdf billPdf = billPdfOpt.get();

        // Retrieve the file from GridFS using the stored fileId
        GridFSFile gridFsFile = gridFsTemplate.findOne(query(where("_id").is(new ObjectId(billPdf.getFileId()))));
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
            headers.setContentType(MediaType.parseMediaType(billPdf.getContentType()));
            headers.setContentDisposition(ContentDisposition.builder("inline").filename(billPdf.getFileName()).build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(inputStreamResource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
