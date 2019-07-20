package com.faceRecog.manage.model;

public class AttendGroupAttendDance {
    private String id;

    private String adId;

    private String agId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId == null ? null : adId.trim();
    }

    public String getAgId() {
        return agId;
    }

    public void setAgId(String agId) {
        this.agId = agId == null ? null : agId.trim();
    }
}