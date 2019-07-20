package com.faceRecog.manage.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class DimissionInfo {
    private String id;

    private String empId;

    private String dimDate;

    private String dimType;

    private String dimReason;

    private String createUserId;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

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

    public String getDimDate() {
        return dimDate;
    }

    public void setDimDate(String dimDate) {
        this.dimDate = dimDate == null ? null : dimDate.trim();
    }

    public String getDimType() {
        return dimType;
    }

    public void setDimType(String dimType) {
        this.dimType = dimType == null ? null : dimType.trim();
    }

    public String getDimReason() {
        return dimReason;
    }

    public void setDimReason(String dimReason) {
        this.dimReason = dimReason == null ? null : dimReason.trim();
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId == null ? null : createUserId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
}