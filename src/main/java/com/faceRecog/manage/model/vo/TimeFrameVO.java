package com.faceRecog.manage.model.vo;

import java.util.List;

public class TimeFrameVO {

    private String id;

    private String attendId;

    private List<String> attendTime;

    private List<String> closeTime;

    private List<String> isOvertime;

    private List<String> sort;
    
    private List<String> inAcorss;
    
    private List<String> outAcorss;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttendId() {
        return attendId;
    }

    public void setAttendId(String attendId) {
        this.attendId = attendId;
    }

    public List<String> getAttendTime() {
        return attendTime;
    }

    public void setAttendTime(List<String> attendTime) {
        this.attendTime = attendTime;
    }

    public List<String> getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(List<String> closeTime) {
        this.closeTime = closeTime;
    }

    public List<String> getIsOvertime() {
        return isOvertime;
    }

    public void setIsOvertime(List<String> isOvertime) {
        this.isOvertime = isOvertime;
    }

    public List<String> getSort() {
        return sort;
    }

    public void setSort(List<String> sort) {
        this.sort = sort;
    }

	public List<String> getInAcorss() {
		return inAcorss;
	}

	public void setInAcorss(List<String> inAcorss) {
		this.inAcorss = inAcorss;
	}

	public List<String> getOutAcorss() {
		return outAcorss;
	}

	public void setOutAcorss(List<String> outAcorss) {
		this.outAcorss = outAcorss;
	}

	
    
    
}
