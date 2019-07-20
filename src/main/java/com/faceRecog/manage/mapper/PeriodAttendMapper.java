package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.Attendance;
import com.faceRecog.manage.model.PeriodAttend;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface PeriodAttendMapper {
    int deleteByPrimaryKey(String id);

    int insert(PeriodAttend record);

    int insertSelective(PeriodAttend record);

    PeriodAttend selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(PeriodAttend record);

    int updateByPrimaryKey(PeriodAttend record);

    //查询周期班次条数
    int selectCountByPeriodId(String periodId);

    //查询周期班次
    List<Map<String,Object>> selectPeriodAttend(String periodId);
    
    
    List<Map<String,Object>>selectPeriodAttendDetailByPrIdAndSortEq(@Param("prId")String prId,@Param("sort")Integer sort);
    
    List<Map<String,Object>> selectPeriodAttendDetailByPrIdAndSortCms(@Param("prId")String prId,
    		@Param("sort")Integer sort,@Param("dateStr")String dateStr);
    
    
    int deletePeriodAttendByPeriodId(String periodId);
    
    /**
     * 
    * @Title: selectPeriodAttendByAgId 
    * @Description: 根据考勤组id查询周期班次信息 
    * @param agId
    * @return PeriodAttend
    * @author xya
    * @date 2019年5月31日下午1:35:05
     */
    List<Attendance> selectPeriodAttendByAgId(String agId);

    int selectCountByAttIds(String id);
}