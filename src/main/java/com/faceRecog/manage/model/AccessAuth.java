package com.faceRecog.manage.model;


import java.util.Date;

public class AccessAuth {
    private String id;

    private String authName;

    private String timeOne;

    private String timeTwo;

    private String timeThree;

    private String status;

    private Date createTime;

    private Date updateTime;

    private String createUserId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName == null ? null : authName.trim();
    }

    public String getTimeOne() {
        return timeOne;
    }

    public void setTimeOne(String timeOne) {
        this.timeOne = timeOne == null ? null : timeOne.trim();
    }

    public String getTimeTwo() {
        return timeTwo;
    }

    public void setTimeTwo(String timeTwo) {
        this.timeTwo = timeTwo == null ? null : timeTwo.trim();
    }

    public String getTimeThree() {
        return timeThree;
    }

    public void setTimeThree(String timeThree) {
        this.timeThree = timeThree == null ? null : timeThree.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId == null ? null : createUserId.trim();
    }
}