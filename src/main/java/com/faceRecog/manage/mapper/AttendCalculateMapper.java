package com.faceRecog.manage.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.faceRecog.manage.model.AttendCalculate;
import com.faceRecog.manage.model.vo.AttendCalculateVO;

public interface AttendCalculateMapper {
    int deleteByPrimaryKey(String id);

    int insert(AttendCalculate record);

    int insertSelective(AttendCalculate record);

    AttendCalculate selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AttendCalculate record);

    int updateByPrimaryKey(AttendCalculate record);
    
    
    /**
     * 
    * @Title: selectAttendCalculateInfoByEmpId 
    * @Description: 根据员工id和日期查询考勤计算信息 
    * @param empId
    * @param date
    * @return AttendCalculate
    * @author xya
    * @date 2019年5月16日下午4:59:36
     */
    AttendCalculate selectAttendCalculateInfoByEmpId(@Param("empId")String empId,@Param("date")String date);
    
    /**
     * 
    * @Title: updateAttendCalculateByEmpId 
    * @Description: 根据员工id和日期修改考勤计算信息 
    * @return int
    * @author xya
    * @date 2019年5月16日下午5:00:01
     */
    int updateAttendCalculateByEmpId(AttendCalculate attendCalculate);
    
    /**
     * 
    * @Title: selectPageAttendCalculate 
    * @Description: 查询考勤信息分页 
    * @param startDate
    * @param endDate
    * @param name
    * @param empId
    * @param deptId
    * @return List<Map<String,Object>>
    * @author xya
    * @date 2019年5月17日上午10:38:12
     */
    List<AttendCalculateVO>selectPageAttendCalculate(@Param("startDate")String startDate,@Param("endDate")String endDate,
    		@Param("name")String name,@Param("empId")String empId,@Param("deptId")String deptId);
    
    
    
   AttendCalculateVO selectPageAttendCalculateDetail(@Param("empId")String empId,@Param("date")String date);
   
   /**
    * 
   * @Title: selectSignMissAttendDetail 
   * @Description:查询员工月缺卡记录
   * @param dateStr
   * @param empId
   * @return List<Map<String,Object>>
   * @author xya
   * @date 2019年5月30日下午5:26:34
    */
   List<Map<String, Object>>selectSignMissAttendDetail(@Param("dateStr")String dateStr,@Param("empId")String empId);
   
   
   int deleteAttendCalcuateByEmpIdAnDate(@Param("empId")String empId,@Param("dateStr")String dateStr);
}