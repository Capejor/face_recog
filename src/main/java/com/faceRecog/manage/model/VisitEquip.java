package com.faceRecog.manage.model;

public class VisitEquip {
    private String id;

    private String visitId;

    private String equipId;

    private String visitAuthId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId == null ? null : visitId.trim();
    }

    public String getEquipId() {
        return equipId;
    }

    public void setEquipId(String equipId) {
        this.equipId = equipId == null ? null : equipId.trim();
    }

    public String getVisitAuthId() {
        return visitAuthId;
    }

    public void setVisitAuthId(String visitAuthId) {
        this.visitAuthId = visitAuthId == null ? null : visitAuthId.trim();
    }
}