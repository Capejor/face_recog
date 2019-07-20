package com.faceRecog.manage.model;

public class EmpRetroact {
    private String id;

    private String empId;

    private String retroactId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId == null ? null : empId.trim();
    }

    public String getRetroactId() {
        return retroactId;
    }

    public void setRetroactId(String retroactId) {
        this.retroactId = retroactId == null ? null : retroactId.trim();
    }
}