package com.example.job_desc_backend.controller;

import com.example.job_desc_backend.model.Bill;
import com.example.job_desc_backend.model.Billpdf;
import com.example.job_desc_backend.repository.BillPdfRepository;
import com.example.job_desc_backend.repository.BillRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class BillController {
    @Autowired
    BillRepository billRepository;
    @Autowired
    BillPdfRepository billPdfRepository;
    @Autowired
    GridFsTemplate gridFsTemplate;
    @PostMapping("/upload/Bill")
    private Bill uploadbill(@RequestPart("bill") Bill bill, @RequestPart("file") MultipartFile file) throws IOException {
        ObjectId fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
        Billpdf billpdf=new Billpdf();
        billpdf.setFileId(String.valueOf(fileId));
        billpdf.setContentType(file.getContentType());
        billpdf.setFileName(file.getOriginalFilename());
        billPdfRepository.save(billpdf);
        bill.setFileId(String.valueOf(fileId));
        return billRepository.save(bill);
    }


    @RestController
    public class DashboardController {

        @Autowired
        private BillRepository billRepository;

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
    }

}