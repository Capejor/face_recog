package com.faceRecog.manage.model.vo;

public class AttVO {
    private String id;

    private String name;

    private String type;

    private String workTime;

    //时段表
    private String attendTime;

    private String closeTime;

    private String LateTime;

    private String EarlyTime;

    private String isOvertime;

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

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public String getLateTime() {
        return LateTime;
    }

    public void setLateTime(String lateTime) {
        LateTime = lateTime;
    }

    public String getEarlyTime() {
        return EarlyTime;
    }

    public void setEarlyTime(String earlyTime) {
        EarlyTime = earlyTime;
    }

    public String getIsOvertime() {
        return isOvertime;
    }

    public void setIsOvertime(String isOvertime) {
        this.isOvertime = isOvertime;
    }

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
}
