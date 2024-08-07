package com.example.job_desc_backend.model;



import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "bills")
public class BillEntity {
    @Id
    private String id;
    private String reason;
    private double amount;
    private String date;
    private String category;

    private boolean reimbursed;
    private String submittedBy;
    private String comments;

    private boolean clientReimbursed;
    private String reimbursementDate;

    public boolean isClientReimbursed() {
        return clientReimbursed;
    }

    public void setClientReimbursed(boolean clientReimbursed) {
        this.clientReimbursed = clientReimbursed;
    }

    public String getReimbursementDate() {
        return reimbursementDate;
    }

    public void setReimbursementDate(String reimbursementDate) {
        this.reimbursementDate = reimbursementDate;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    private String fileId;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isReimbursed() {
        return reimbursed;
    }

    public void setReimbursed(boolean reimbursed) {
        this.reimbursed = reimbursed;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    public void setCreatedBy(String username) {
    }
}

