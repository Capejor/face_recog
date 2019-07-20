package com.faceRecog.manage.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class EmployeeInfo {
    private String id;

    private String empId;

    private String education;

    private String politics;

    private String isMarried;

    private String blood;

    private String emerPer;

    private String emerPhone;

    private String carNo;

    private String laborCon;

    private String laborConNo;

    private String remark;

    private String status;

    private String createUserId;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;


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

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education == null ? null : education.trim();
    }

    public String getPolitics() {
        return politics;
    }

    public void setPolitics(String politics) {
        this.politics = politics == null ? null : politics.trim();
    }

    public String getIsMarried() {
        return isMarried;
    }

    public void setIsMarried(String isMarried) {
        this.isMarried = isMarried == null ? null : isMarried.trim();
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood == null ? null : blood.trim();
    }

    public String getEmerPer() {
        return emerPer;
    }

    public void setEmerPer(String emerPer) {
        this.emerPer = emerPer == null ? null : emerPer.trim();
    }

    public String getEmerPhone() {
        return emerPhone;
    }

    public void setEmerPhone(String emerPhone) {
        this.emerPhone = emerPhone == null ? null : emerPhone.trim();
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo == null ? null : carNo.trim();
    }

    public String getLaborCon() {
        return laborCon;
    }

    public void setLaborCon(String laborCon) {
        this.laborCon = laborCon == null ? null : laborCon.trim();
    }

    public String getLaborConNo() {
        return laborConNo;
    }

    public void setLaborConNo(String laborConNo) {
        this.laborConNo = laborConNo == null ? null : laborConNo.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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

}