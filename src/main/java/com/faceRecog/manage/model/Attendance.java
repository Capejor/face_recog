package com.faceRecog.manage.model;

import java.util.Date;

public class Attendance {
    private String id;

    private String name;

    private String type;

    private String allowLate;

    private String allowEarly;

    private String workTime;

    private String afterOvertime;

    private Date createTime;

    private Date updateTime;

    private String createUserId;

    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getAllowLate() {
        return allowLate;
    }

    public void setAllowLate(String allowLate) {
        this.allowLate = allowLate == null ? null : allowLate.trim();
    }

    public String getAllowEarly() {
        return allowEarly;
    }

    public void setAllowEarly(String allowEarly) {
        this.allowEarly = allowEarly == null ? null : allowEarly.trim();
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime == null ? null : workTime.trim();
    }

    public String getAfterOvertime() {
        return afterOvertime;
    }

    public void setAfterOvertime(String afterOvertime) {
        this.afterOvertime = afterOvertime == null ? null : afterOvertime.trim();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
}