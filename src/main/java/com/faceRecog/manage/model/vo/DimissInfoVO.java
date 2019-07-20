package com.faceRecog.manage.model.vo;

import java.util.Date;

public class DimissInfoVO {

    private String id;

    private String name;

    private String sex;

    private String idCard;

    private String phone;

    private String address;

    private String jobPost;

    private String deptName;

    private String dimDate;

    private String dimType;

    private String dimReason;

    private String employTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard == null ? null : idCard.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getJobPost() {
        return jobPost;
    }

    public void setJobPost(String jobPost) {
        this.jobPost = jobPost == null ? null : jobPost.trim();
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDimType() {
        return dimType;
    }

    public void setDimType(String dimType) {
        this.dimType = dimType;
    }

    public String getDimReason() {
        return dimReason;
    }

    public void setDimReason(String dimReason) {
        this.dimReason = dimReason;
    }

    public String getDimDate() {
        return dimDate;
    }

    public void setDimDate(String dimDate) {
        this.dimDate = dimDate;
    }

    public String getEmployTime() {
        return employTime;
    }

    public void setEmployTime(String employTime) {
        this.employTime = employTime;
    }
}