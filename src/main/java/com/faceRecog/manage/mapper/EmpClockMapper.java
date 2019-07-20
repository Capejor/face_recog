package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.EmpClock;

public interface EmpClockMapper {
    int deleteByPrimaryKey(String id);

    int insert(EmpClock record);

    int insertSelective(EmpClock record);

    EmpClock selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(EmpClock record);

    int updateByPrimaryKey(EmpClock record);
}