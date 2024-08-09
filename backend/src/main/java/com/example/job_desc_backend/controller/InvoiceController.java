package com.example.job_desc_backend.controller;

import com.example.job_desc_backend.model.Client;
import com.example.job_desc_backend.model.Invoice;
import com.example.job_desc_backend.repository.ClientRepository;
import com.example.job_desc_backend.repository.InvoiceRepository;
import com.example.job_desc_backend.service.ExportImportService;
import com.example.job_desc_backend.service.InvoiceService;
//import org.apache.regexp.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/clients")
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return ResponseEntity.ok(savedInvoice);
    }

//    @GetMapping("/pending")
//    public List<Invoice> getPendingInvoices() {
//        return invoiceRepository.findPendingInvoices();
//    }

    @GetMapping("/all")
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @GetMapping("/byYear")
    public List<Invoice> getInvoicesByYear(@RequestParam int year) {
        return invoiceService.getInvoicesByYear(year);
    }

    @GetMapping("/byMonth")
    public List<Invoice> getInvoicesByMonth(@RequestParam int year, @RequestParam int month) {
        return invoiceService.getInvoicesByMonth(year, month);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable String id) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        return invoice.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // New endpoint to update a specific invoice by its ID
    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable String id, @RequestBody Invoice invoiceDetails) {
        Optional<Invoice> optionalInvoice = invoiceRepository.findById(id);

        if (optionalInvoice.isPresent()) {
            Invoice invoice = optionalInvoice.get();
            invoice.setInvoiceNumber(invoiceDetails.getInvoiceNumber());
            invoice.setClientName(invoiceDetails.getClientName());
            invoice.setInvoiceValue(invoiceDetails.getInvoiceValue());
            invoice.setCurrency(invoiceDetails.getCurrency());
            invoice.setMonth(invoiceDetails.getMonth());
            invoice.setRaisedOn(invoiceDetails.getRaisedOn());
            invoice.setTimeline(invoiceDetails.getTimeline());
            Invoice updatedInvoice = invoiceRepository.save(invoice);
            return ResponseEntity.ok(updatedInvoice);
        }else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/pending")
    public ResponseEntity<List<Invoice>> getPendingInvoices() {
        List<Invoice> invoices = invoiceService.getPendingInvoices();

        if (invoices.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(invoices);
        }
    }

    @GetMapping("/export/invoices")
    public ResponseEntity<byte[]> exportInvoicesToExcel() {
        try {
            List<Invoice> invoices = invoiceRepository.findAll();
            ExportImportService<Invoice> exporter = new ExportImportService<>();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            exporter.exportToExcel(invoices, baos);

            byte[] excelBytes = baos.toByteArray();
            System.out.println("Excel file size: " + excelBytes.length);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clients_export.xlsx");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(("Failed to create Excel file: " + e.getMessage()).getBytes(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/import/invoices")
    public ResponseEntity<?> importInvoicesFromCSV(@RequestParam("file") MultipartFile file) {
        try {
            ExportImportService<Invoice> importer = new ExportImportService<>();
            List<Invoice> invoices = importer.importFromCSV(file, Invoice.class);
            invoiceRepository.saveAll(invoices);
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to import CSV file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }
    }

}