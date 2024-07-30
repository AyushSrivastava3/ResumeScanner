package com.example.job_desc_backend.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
@Getter
@Setter
@Document(collection = "invoices")
public class Invoice {

    @Id
    private String id;
    private String clientName;
    private String invoiceNumber;
    private double invoiceValue;
    private String currency;
    private String month;
    private String raisedOn;
    private String timeline;
    private LocalDate targetDate;
    private String createdBy;

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public String getFollowUpNotes() {
        return followUpNotes;
    }

    public void setFollowUpNotes(String followUpNotes) {
        this.followUpNotes = followUpNotes;
    }

    public LocalDate getLastFollowUp() {
        return lastFollowUp;
    }

    public void setLastFollowUp(LocalDate lastFollowUp) {
        this.lastFollowUp = lastFollowUp;
    }

    private LocalDate lastFollowUp;
    private String followUpNotes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public String getRaisedOn() {
        return raisedOn;
    }

    public void setRaisedOn(String raisedOn) {
        this.raisedOn = raisedOn;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getInvoiceValue() {
        return invoiceValue;
    }

    public void setInvoiceValue(double invoiceValue) {
        this.invoiceValue = invoiceValue;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}