package com.faceRecog.manage.model;

import java.util.Date;

public class Account {
    private String id;

    private String empId;

    private Date alterTime;

    private String type;

    private Float originalSum;

    private Float alterSum;

    private Float balance;

    private String createUserId;

    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId == null ? null : empId.trim();
    }

    public Date getAlterTime() {
        return alterTime;
    }

    public void setAlterTime(Date alterTime) {
        this.alterTime = alterTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Float getOriginalSum() {
        return originalSum;
    }

    public void setOriginalSum(Float originalSum) {
        this.originalSum = originalSum;
    }

    public Float getAlterSum() {
        return alterSum;
    }

    public void setAlterSum(Float alterSum) {
        this.alterSum = alterSum;
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId == null ? null : createUserId.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
}