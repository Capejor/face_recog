package com.faceRecog.manage.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.faceRecog.manage.model.AttendGroup;

public interface AttendGroupMapper {
    int deleteByPrimaryKey(String id);

    int insert(AttendGroup record);

    int insertSelective(AttendGroup record);

    AttendGroup selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AttendGroup record);

    int updateByPrimaryKey(AttendGroup record);
    
    
    /**
     * 
    * @Title: selectAllAttendGroup 
    * @Description: 查询考勤组列表 
    * @return List<Map<String,Object>>
    * @author xya
    * @date 2019年5月20日上午9:23:17
     */
    List<Map<String, Object>>selectAllAttendGroup();
    
    
    List<Map<String, Object>>selectAttendGroupAttendDanceInfo(@Param("empId")String empId,@Param("dateStr")String dateStr);
    
    List<AttendGroup>selectFixedAttendGroup();
    
    /**
     * 
    * @Title: selectEmpAttendGroupType 
    * @Description: 根据员工id查询员工所属考勤组的班制类型 
    * @param empId
    * @return AttendGroup
    * @author xya
    * @date 2019年5月29日上午10:03:30
     */
    AttendGroup selectEmpAttendGroupType(String empId);
    
    /**
     * 
    * @Title: selectAttendGroupNameExist 
    * @Description: 根据考勤组名称查询考勤组名称是否被zhanyong 
    * @param attendGroupName
    * @param agId
    * @return int
    * @author xya
    * @date 2019年6月10日下午9:19:27
     */
    int selectAttendGroupNameExist(@Param("attendGroupName")String attendGroupName,@Param("agId")String agId);
}