package com.faceRecog.manage.model;

import java.util.Date;

public class LinkRec {
    private String id;

    private String status;

    private Date createTime;

    private Date newLinkTime;

    private Date discTime;

    private String sn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
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

    public Date getNewLinkTime() {
        return newLinkTime;
    }

    public void setNewLinkTime(Date newLinkTime) {
        this.newLinkTime = newLinkTime;
    }

    public Date getDiscTime() {
        return discTime;
    }

    public void setDiscTime(Date discTime) {
        this.discTime = discTime;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn == null ? null : sn.trim();
    }
}