package com.faceRecog.manage.service;

import com.faceRecog.manage.model.Period;

import java.util.List;
import java.util.Map;


public interface PeriodService {

    int insertPeriod(Period period) throws Exception;

    int updatePeriod(Period period) throws Exception;

    List<Map<String, Object>> selectPeriod() throws Exception;

    int deletePeriod(String ids[]) throws Exception;

    //添加周期班次
    int insertPeriodAttend(String periodId, String attendId, int dayNum);

    //删除周期班次
    int deletePeriodAttend(String id,String periodId) throws Exception;

    List<Map<String,Object>> selectPeriodAttend(String periodId)throws Exception;

    int selectCountByPeriodName(String periodName)throws Exception;

    int selectCountByPeriodNameExceptOwn(String id,String periodName)throws Exception;
}
