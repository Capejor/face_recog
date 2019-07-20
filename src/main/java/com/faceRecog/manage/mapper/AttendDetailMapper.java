package com.faceRecog.manage.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.faceRecog.manage.model.AttendDetail;

public interface AttendDetailMapper {
    int deleteByPrimaryKey(String id);

    int insert(AttendDetail record);

    int insertSelective(AttendDetail record);

    AttendDetail selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AttendDetail record);

    int updateByPrimaryKey(AttendDetail record);
    
    
    /**
     * 
    * @Title: deleteAttendDetailByAgId 
    * @Description: 根据考勤组id删除考勤明细 
    * @param agId
    * @return int
    * @author xya
    * @date 2019年5月18日下午3:24:59
     */
    int deleteAttendDetailByAgId(String agId);
    
    /**
     * 
    * @Title: selectAttendDetailByAgId 
    * @Description: 根据考勤组id查询考勤排班明细 
    * @param empId
    * @return List<Map<String,Object>>
    * @author xya
    * @date 2019年5月22日下午8:08:25
     */
    List<Map<String, Object>>selectAttendDetailByEmpId(@Param("empId")String empId,@Param("yearMonth")String yearMonth);
    
    /**
     * 
    * @Title: updateAttendDetailByEmpIdAndDateStr 
    * @Description: 根据员工id和日期修改员工考勤明细 
    * @param empId
    * @param dateStr
    * @return int
    * @author xya
    * @date 2019年5月28日下午4:12:05
     */
    int updateAttendDetailByEmpIdAndDateStr(@Param("empId")String empId,@Param("dateStr")String dateStr,@Param("attendId")String attendId);
}