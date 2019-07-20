package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.TimeFrame;
import org.apache.ibatis.annotations.Param;


public interface TimeFrameMapper {
    int deleteByPrimaryKey(String id);

    int insert(TimeFrame record);

    int insertSelective(TimeFrame record);

    TimeFrame selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(TimeFrame record);

    int updateByPrimaryKey(TimeFrame record);

    //根据 考勤id 删除时段
    int deleteByAttendId(@Param("attendId") String attendId);


}