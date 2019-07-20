package com.faceRecog.manage.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.faceRecog.manage.model.AttendGroupLeader;

public interface AttendGroupLeaderMapper {
    int deleteByPrimaryKey(String id);

    int insert(AttendGroupLeader record);

    int insertSelective(AttendGroupLeader record);

    AttendGroupLeader selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AttendGroupLeader record);

    int updateByPrimaryKey(AttendGroupLeader record);
    
    
    int insertBatchAttendGroupLeader(@Param("agId")String agId,@Param("leaderIds")List<Map<String, Object>>leaderIds);
    
    /**
     * 
    * @Title: deleteAttendGroupLeaderByAgId 
    * @Description: 根据考情组id删除考情组负责人中间表数据 
    * @param agId
    * @return int
    * @author xya
    * @date 2019年5月20日下午8:16:14
     */
    int deleteAttendGroupLeaderByAgId(String agId);
    
    /**
     * 
    * @Title: selectAgLeaderByAgId 
    * @Description: 根据考勤组id查询考勤组负责人信息 
    * @param agId
    * @return AttendGroupLeader
    * @author xya
    * @date 2019年5月21日上午10:31:59
     */
    AttendGroupLeader selectAgLeaderByAgId(String agId);
    
    /**
     * 
    * @Title: deleteAttendGroupLeaderByEmpId 
    * @Description: 根据员工id删除考勤组负责人信息 
    * @param empId
    * @return int
    * @author xya
    * @date 2019年6月14日下午9:35:14
     */
    int deleteAttendGroupLeaderByEmpId(String empId);
}