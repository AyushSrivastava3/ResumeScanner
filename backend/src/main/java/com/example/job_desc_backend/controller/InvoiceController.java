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
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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


}