package com.faceRecog.manage.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.faceRecog.manage.model.SignRecord;

public interface SignRecordMapper {
    int deleteByPrimaryKey(String id);

    int insert(SignRecord record);

    int insertSelective(SignRecord record);

    SignRecord selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SignRecord record);

    int updateByPrimaryKey(SignRecord record);
    
    
    /**
     * 
    * @Title: selectSignRecord 
    * @Description: 新增签到信息 
    * @param empId
    * @return SignRecord
    * @author xya
    * @date 2019年5月11日下午2:43:58
     */
    SignRecord selectSignRecordByEmpId(@Param("empId")String empId,@Param("signDate")String signDate);
    
    /**
     * 
    * @Title: selectAttendGroupEmpSignInfoByAgId 
    * @Description: 根据考勤组id查询考勤组内员工的考勤打卡明细
    * @param agId
    * @return List<Map<String,Object>>
    * @author xya
    * @date 2019年5月16日下午3:52:34
     */
    Map<String, Object>selectAttendGroupEmpSignInfoByEmpId(@Param("empId")String empId,@Param("yesterDay")String yesterDay);
    
    
}