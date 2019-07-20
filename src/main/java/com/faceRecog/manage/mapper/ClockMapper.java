package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.Clock;
import org.apache.ibatis.annotations.Param;

public interface ClockMapper {
    int deleteByPrimaryKey(String id);

    int insert(Clock record);

    int insertSelective(Clock record);

    Clock selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Clock record);

    int updateByPrimaryKey(Clock record);

    Clock selectClockIn(@Param("empId") String empId);

    int selectClockOut(@Param("empId") String empId );

    String selectClockId(String empId);
}