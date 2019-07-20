package com.faceRecog.manage.model.vo;

import java.util.Date;

/**
 * @author Capejor
 * @className: AccountTopUpVO
 * @Description: TODO
 * @date 2019-05-17 19:41
 */
public class AccountTopUpVO {

    private String id;

    private String empId;

    private String empName;

    private String deptName;

    private Float balance;

    private Date alterTime;

    private Float originalSum;

    private Float alterSum;

    private String createUserId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public Date getAlterTime() {
        return alterTime;
    }

    public void setAlterTime(Date alterTime) {
        this.alterTime = alterTime;
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

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }
}
