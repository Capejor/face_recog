package com.faceRecog.manage.model;

public class TimeFrame {
    private String id;

    private String attendId;

    private String attendTime;

    private String closeTime;

    private String isOvertime;

    private String sort;

    private String inAcross;

    private String outAcross;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getAttendId() {
        return attendId;
    }

    public void setAttendId(String attendId) {
        this.attendId = attendId == null ? null : attendId.trim();
    }

    public String getAttendTime() {
        return attendTime;
    }

    public void setAttendTime(String attendTime) {
        this.attendTime = attendTime == null ? null : attendTime.trim();
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime == null ? null : closeTime.trim();
    }

    public String getIsOvertime() {
        return isOvertime;
    }

    public void setIsOvertime(String isOvertime) {
        this.isOvertime = isOvertime == null ? null : isOvertime.trim();
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort == null ? null : sort.trim();
    }

    public String getInAcross() {
        return inAcross;
    }

    public void setInAcross(String inAcross) {
        this.inAcross = inAcross == null ? null : inAcross.trim();
    }

    public String getOutAcross() {
        return outAcross;
    }

    public void setOutAcross(String outAcross) {
        this.outAcross = outAcross == null ? null : outAcross.trim();
    }
}