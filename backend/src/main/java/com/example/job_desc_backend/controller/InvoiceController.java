package com.example.job_desc_backend.controller;

import com.example.job_desc_backend.model.Client;
import com.example.job_desc_backend.model.Invoice;
import com.example.job_desc_backend.repository.ClientRepository;
import com.example.job_desc_backend.repository.InvoiceRepository;
import com.example.job_desc_backend.service.InvoiceService;
//import org.apache.regexp.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

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

    @GetMapping("/pending")
    public List<Invoice> getPendingInvoices() {
        return invoiceRepository.findPendingInvoices();
    }

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
        return invoiceService.getInvoicesByMonth(year,month);
    }
}