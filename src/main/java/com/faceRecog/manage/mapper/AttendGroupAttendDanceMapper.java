package com.faceRecog.manage.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.faceRecog.manage.model.AttendGroupAttendDance;

public interface AttendGroupAttendDanceMapper {
    int deleteByPrimaryKey(String id);

    int insert(AttendGroupAttendDance record);

    int insertSelective(AttendGroupAttendDance record);

    AttendGroupAttendDance selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AttendGroupAttendDance record);

    int updateByPrimaryKey(AttendGroupAttendDance record);
    
    /**
     * 
    * @Title: insertBatchAttendDance 
    * @Description:  批量新增班次考勤组信息
    * @param agId
    * @param scheduleIds
    * @return int
    * @author xya
    * @date 2019年5月21日上午10:28:18
     */
    int insertBatchAttendDance(@Param("agId")String agId,@Param("scheduleIds")List<Map<String, Object>> scheduleIds);
   
    /**
     * 
     * 
    * @Title: selectAttendDanceByAgId 
    * @Description: 根据考勤组id查询考勤组班次信息 
    * @param agId
    * @return AttendGroupAttendDance
    * @author xya
    * @date 2019年5月21日上午10:33:17
     */
    AttendGroupAttendDance  selectAttendDanceByAgId(String agId);
    
    
    int deleteAttendGroupAttendDanceByAgId(String agId);
    
    
    List<Map<String, Object>>selectAttendDanceInfoByAgId(String agId);
}