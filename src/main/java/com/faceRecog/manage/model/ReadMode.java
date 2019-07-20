package com.faceRecog.manage.model;

import java.util.Date;

public class ReadMode {
    private String id;

    private String eqId;

    private String serialPort;

    private String peripheralType;

    private Date createTime;

    private String readType;

    private Integer precisionNum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getEqId() {
        return eqId;
    }

    public void setEqId(String eqId) {
        this.eqId = eqId == null ? null : eqId.trim();
    }

    public String getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(String serialPort) {
        this.serialPort = serialPort == null ? null : serialPort.trim();
    }

    public String getPeripheralType() {
        return peripheralType;
    }

    public void setPeripheralType(String peripheralType) {
        this.peripheralType = peripheralType == null ? null : peripheralType.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getReadType() {
        return readType;
    }

    public void setReadType(String readType) {
        this.readType = readType == null ? null : readType.trim();
    }

    public Integer getPrecisionNum() {
        return precisionNum;
    }

    public void setPrecisionNum(Integer precisionNum) {
        this.precisionNum = precisionNum;
    }
}