package com.faceRecog.manage.model;

import java.math.BigDecimal;
import java.util.Date;

public class AttendCalculate {
    private String id;

    private String empId;

    private String signDate;

    private Integer overtimeNum;

    private BigDecimal overtimeTimes;

    private Integer delayNum;

    private BigDecimal delayTimes;

    private Integer earlyNum;

    private BigDecimal earlyTimes;

    private BigDecimal workTimes;

    private Date createTime;

    private String status;

    private Integer missSign;

    private Integer signInMiss;

    private Integer signOutMiss;

    private Integer isAbsenteeism;

    private Integer isRest;

    private Integer isAttendGroup;

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

    public String getSignDate() {
        return signDate;
    }

    public void setSignDate(String signDate) {
        this.signDate = signDate == null ? null : signDate.trim();
    }

    public Integer getOvertimeNum() {
        return overtimeNum;
    }

    public void setOvertimeNum(Integer overtimeNum) {
        this.overtimeNum = overtimeNum;
    }

    public BigDecimal getOvertimeTimes() {
        return overtimeTimes;
    }

    public void setOvertimeTimes(BigDecimal overtimeTimes) {
        this.overtimeTimes = overtimeTimes;
    }

    public Integer getDelayNum() {
        return delayNum;
    }

    public void setDelayNum(Integer delayNum) {
        this.delayNum = delayNum;
    }

    public BigDecimal getDelayTimes() {
        return delayTimes;
    }

    public void setDelayTimes(BigDecimal delayTimes) {
        this.delayTimes = delayTimes;
    }

    public Integer getEarlyNum() {
        return earlyNum;
    }

    public void setEarlyNum(Integer earlyNum) {
        this.earlyNum = earlyNum;
    }

    public BigDecimal getEarlyTimes() {
        return earlyTimes;
    }

    public void setEarlyTimes(BigDecimal earlyTimes) {
        this.earlyTimes = earlyTimes;
    }

    public BigDecimal getWorkTimes() {
        return workTimes;
    }

    public void setWorkTimes(BigDecimal workTimes) {
        this.workTimes = workTimes;
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

    public Integer getMissSign() {
        return missSign;
    }

    public void setMissSign(Integer missSign) {
        this.missSign = missSign;
    }

    public Integer getSignInMiss() {
        return signInMiss;
    }

    public void setSignInMiss(Integer signInMiss) {
        this.signInMiss = signInMiss;
    }

    public Integer getSignOutMiss() {
        return signOutMiss;
    }

    public void setSignOutMiss(Integer signOutMiss) {
        this.signOutMiss = signOutMiss;
    }

    public Integer getIsAbsenteeism() {
        return isAbsenteeism;
    }

    public void setIsAbsenteeism(Integer isAbsenteeism) {
        this.isAbsenteeism = isAbsenteeism;
    }

    public Integer getIsRest() {
        return isRest;
    }

    public void setIsRest(Integer isRest) {
        this.isRest = isRest;
    }

    public Integer getIsAttendGroup() {
        return isAttendGroup;
    }

    public void setIsAttendGroup(Integer isAttendGroup) {
        this.isAttendGroup = isAttendGroup;
    }
}