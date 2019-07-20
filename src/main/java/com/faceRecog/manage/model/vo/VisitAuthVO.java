package com.faceRecog.manage.model.vo;

import java.util.Date;
import java.util.List;

/**
 * @author Capejor
 * @className: VisitAuthVO
 * @Description: TODO
 * @date 2019-05-17 10:49
 */
public class VisitAuthVO {

    private List<String> startTime;

    private List<String> endTime;

    private List<String> equipId;


    public List<String> getStartTime() {
        return startTime;
    }

    public void setStartTime(List<String> startTime) {
        this.startTime = startTime;
    }

    public List<String> getEndTime() {
        return endTime;
    }

    public void setEndTime(List<String> endTime) {
        this.endTime = endTime;
    }

    public List<String> getEquipId() {
        return equipId;
    }

    public void setEquipId(List<String> equipId) {
        this.equipId = equipId;
    }

}
