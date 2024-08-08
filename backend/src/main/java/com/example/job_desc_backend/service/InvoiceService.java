package com.example.job_desc_backend.service;

import com.example.job_desc_backend.model.Invoice;
import com.example.job_desc_backend.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private InvoiceRepository invoiceRepository;

    public List<Invoice> getInvoicesByYear(int year) {
        List<Invoice> allInvoices = invoiceRepository.findAll();
        return allInvoices.stream()
                .filter(invoice -> {
                    LocalDate date = LocalDate.parse(invoice.getRaisedOn(), DATE_FORMATTER);
                    return date.getYear() == year;
                })
                .collect(Collectors.toList());
    }

    public List<Invoice> getInvoicesByMonth(int year, int month) {
        List<Invoice> allInvoices = invoiceRepository.findAll();
        return allInvoices.stream()
                .filter(invoice -> {
                    LocalDate date = LocalDate.parse(invoice.getRaisedOn(), DATE_FORMATTER);
                    return date.getYear() == year && date.getMonthValue() == month;
                })
                .collect(Collectors.toList());
    }

    public List<Invoice> getPendingInvoices() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDateString = LocalDate.now().format(formatter);

        List<Invoice> invoices = invoiceRepository.findPendingInvoices(currentDateString);

        return invoices;
    }
}