package com.faceRecog.manage.model;

import java.util.Date;
import java.util.List;

public class SignRecord {
    private String id;

    private Date signInOne;

    private Date signOutOne;

    private Date signInTwo;

    private Date signOutTwo;

    private Date signInThree;

    private Date signOutThree;

    private String empId;

    private Date createTime;

    private String status;

    private String signDate;
    
    private List<SignRecord> signList ;

    
    
    
    public List<SignRecord> getSignList() {
		return signList;
	}

	public void setSignList(List<SignRecord> signList) {
		this.signList = signList;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public Date getSignInOne() {
        return signInOne;
    }

    public void setSignInOne(Date signInOne) {
        this.signInOne = signInOne;
    }

    public Date getSignOutOne() {
        return signOutOne;
    }

    public void setSignOutOne(Date signOutOne) {
        this.signOutOne = signOutOne;
    }

    public Date getSignInTwo() {
        return signInTwo;
    }

    public void setSignInTwo(Date signInTwo) {
        this.signInTwo = signInTwo;
    }

    public Date getSignOutTwo() {
        return signOutTwo;
    }

    public void setSignOutTwo(Date signOutTwo) {
        this.signOutTwo = signOutTwo;
    }

    public Date getSignInThree() {
        return signInThree;
    }

    public void setSignInThree(Date signInThree) {
        this.signInThree = signInThree;
    }

    public Date getSignOutThree() {
        return signOutThree;
    }

    public void setSignOutThree(Date signOutThree) {
        this.signOutThree = signOutThree;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId == null ? null : empId.trim();
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

    public String getSignDate() {
        return signDate;
    }

    public void setSignDate(String signDate) {
        this.signDate = signDate == null ? null : signDate.trim();
    }

	@Override
	public String toString() {
		return "SignRecord [id=" + id + ", signInOne=" + signInOne + ", signOutOne=" + signOutOne + ", signInTwo="
				+ signInTwo + ", signOutTwo=" + signOutTwo + ", signInThree=" + signInThree + ", signOutThree="
				+ signOutThree + ", empId=" + empId + ", createTime=" + createTime + ", status=" + status
				+ ", signDate=" + signDate + "]";
	}
    
    
    
}