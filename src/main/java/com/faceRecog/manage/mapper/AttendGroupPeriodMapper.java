package com.faceRecog.manage.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.faceRecog.manage.model.AttendGroupPeriod;

public interface AttendGroupPeriodMapper {
    int deleteByPrimaryKey(String id);

    int insert(AttendGroupPeriod record);

    int insertSelective(AttendGroupPeriod record);

    AttendGroupPeriod selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AttendGroupPeriod record);

    int updateByPrimaryKey(AttendGroupPeriod record);
    
    /**
     * 
    * @Title: insertBatchAttendGroupPeriod 
    * @Description: 批量新增周期考勤组信息 
    * @param agId
    * @param periodIds
    * @return int
    * @author xya
    * @date 2019年5月21日上午10:30:56
     */
    int insertBatchAttendGroupPeriod(@Param("agId")String agId,@Param("periodIds")List<Map<String, Object>> periodIds);
    
    /**
     * 
    * @Title: selectAgPeriodByAgId 
    * @Description: 根据考勤组id查询考勤组周期排班信息 
    * @param agId
    * @return List<AttendGroupPeriod>
    * @author xya
    * @date 2019年5月21日上午10:32:50
     */
    AttendGroupPeriod selectAgPeriodByAgId(String agId);
    
    /**
     * 
    * @Title: deleteAttendGroupPeriod 
    * @Description: 根据考勤组id删除考勤组周期排班信息 
    * @param agId
    * @return int
    * @author xya
    * @date 2019年5月21日下午1:59:03
     */
    int deleteAttendGroupPeriod(String agId);
    
    /**
     * 
    * @Title: selectAgPeriodInfoByAgId 
    * @Description: 查询考勤组周期排班信息根据考勤组id 
    * @param agId
    * @return  List<Map<String, Object>>
    * @author xya
    * @date 2019年5月22日上午11:12:57
     */
    List<Map<String, Object>> selectAgPeriodInfoByAgId(String prId);
    
    /**
     * 
    * @Title: selectPeriodByEmpId 
    * @Description: 根据员工id查询固定班制员工的周期信息 
    * @param empId
    * @return AttendGroupPeriod
    * @author xya
    * @date 2019年5月29日上午10:20:57
     */
    AttendGroupPeriod selectPeriodByEmpId(String empId);
}