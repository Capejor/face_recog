package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.Attendance;
import com.faceRecog.manage.model.vo.AttendVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AttendanceMapper {
    int deleteByPrimaryKey(String id);

    int insert(Attendance record);

    int insertSelective(Attendance record);

    Attendance selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Attendance record);

    int updateByPrimaryKey(Attendance record);


    //查询所有
    List<AttendVO> selectAllAttend();

    int deleteAttendance(@Param("id") String id);

    //考勤名称不能重复
    int selectCountByAttName(String attName);

    //考勤名称不能重复 除去自己
    int selectCountByAttNameExceptOwn(@Param("id") String id,@Param("attName") String attName);
    
    
    Map<String, Object> selectAttendInfoByAtId(String adId);
    
    /**
     * 
    * @Title: selectFixedAttendEmpInfo 
    * @Description: 查询固定班员工考勤信息 
    * @return List<Map<String,Object>>
    * @author xya
    * @date 2019年6月25日下午5:18:37
     */
    List<Map<String, Object>> selectFixedAttendEmpInfo();
    /**
     * 
    * @Title: selectSchedulAttendEmpInfo 
    * @Description: 查询排班制员工考勤 
    * @return List<Map<String,Object>>
    * @author xya
    * @date 2019年6月25日下午5:18:57
     */
    List<Map<String, Object>> selectSchedulAttendEmpInfo();
    /**
     * 
    * @Title: selectNoAttendEmpInfo 
    * @Description: 查询无考勤组人员信息
    * @return List<Map<String,Object>>
    * @author xya
    * @date 2019年6月25日下午5:19:17
     */
    List<Map<String, Object>>  selectNoAttendEmpInfo();




}