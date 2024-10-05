package com.example.job_desc_backend.service;

import com.example.job_desc_backend.model.Client;
import com.example.job_desc_backend.model.Invoice;
import com.example.job_desc_backend.repository.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private InvoiceRepository invoiceRepository;

    public List<Invoice> getInvoicesByYear(int year) {
        String startDate = year + "-01-01";
        String endDate = (year + 1) + "-01-01"; // Start of next year to include all dates in the current year
        return invoiceRepository.findByRaisedOnBetween(startDate, endDate);
    }


    public List<Invoice> getInvoicesByMonth(int year, int month) {
        String startDate = String.format("%d-%02d-01", year, month);
        LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endDate = startLocalDate.withDayOfMonth(startLocalDate.lengthOfMonth()).plusDays(1).toString();
        return invoiceRepository.findByRaisedOnBetween(startDate, endDate);
    }


    public void deleteInvocieById(String id){
        if(invoiceRepository.existsById(id)){
            invoiceRepository.deleteById(id);
        }else {
            // throw new ClientNotFoundException("Client with id " + id + " not found");
        }
    }


    public List<Invoice> getPendingInvoices() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDateString = LocalDate.now().format(formatter);

        List<Invoice> invoices = invoiceRepository.findPendingInvoices(currentDateString);
        return invoices;
    }

    public List<Invoice> getPendingInvoicesByClientId(String clientId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDateString = LocalDate.now().format(formatter);

        List<Invoice> invoices = invoiceRepository.findPendingInvoices(currentDateString);

        List<Invoice> result= new ArrayList<>();
        for (Invoice currentInvoice : invoices){
            if(currentInvoice.getClientId().equals(clientId)){
                result.add(currentInvoice);
            }
        }
        return result;
    }
    public List<Invoice> getInvoiceCreatedToday() {
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
        return invoiceRepository.findInvoiceAddedToday(todayStart);
    }

    public List<Invoice> getInvoiceCreatedInWeek() {
        LocalDateTime weekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);
        LocalDateTime now = LocalDateTime.now();
        return invoiceRepository.findInvoiceWithinDateRange(weekAgo,now);

    }

    public List<Invoice> getInvoicesByClientId(String clientId){
        return invoiceRepository.findByClientId(clientId);
    }
}