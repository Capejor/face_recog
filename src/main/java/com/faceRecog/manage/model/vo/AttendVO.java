package com.faceRecog.manage.model.vo;


public class AttendVO {

    private String id;

    private String name;

    private String type;

    private String allowLate;

    private String allowEarly;

    private String workTime;

    private String afterOvertime;

   /* private Date createTime;

    private Date updateTime;

    private String createUserId;

    private String status;*/

    //时段表
    private String attendTime;

    private String closeTime;

    private String isOvertime;

    private String sort;


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
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAllowLate() {
        return allowLate;
    }

    public void setAllowLate(String allowLate) {
        this.allowLate = allowLate;
    }

    public String getAllowEarly() {
        return allowEarly;
    }

    public void setAllowEarly(String allowEarly) {
        this.allowEarly = allowEarly;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }


   /* public Date getCreateTime() {
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
        this.createUserId = createUserId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }*/


    public String getAttendTime() {
        return attendTime;
    }

    public void setAttendTime(String attendTime) {
        this.attendTime = attendTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getIsOvertime() {
        return isOvertime;
    }

    public void setIsOvertime(String isOvertime) {
        this.isOvertime = isOvertime;
    }

    public String getAfterOvertime() {
        return afterOvertime;
    }

    public void setAfterOvertime(String afterOvertime) {
        this.afterOvertime = afterOvertime;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
