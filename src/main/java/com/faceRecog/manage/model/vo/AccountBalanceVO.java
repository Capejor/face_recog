package com.faceRecog.manage.model.vo;

/**
 * @author Capejor
 * @className: AccountBalanceVO
 * @Description: TODO
 * @date 2019-05-17 19:34
 */
public class AccountBalanceVO {

    private String empId;

    private String empName;

    private String cardNum;

    private String deptName;

    private Float balance;

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

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
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
}
