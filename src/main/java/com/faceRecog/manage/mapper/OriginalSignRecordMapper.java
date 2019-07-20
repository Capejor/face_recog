package com.faceRecog.manage.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.faceRecog.manage.model.OriginalSignRecord;

public interface OriginalSignRecordMapper {
    int deleteByPrimaryKey(String id);

    int insert(OriginalSignRecord record);

    int insertSelective(OriginalSignRecord record);

    OriginalSignRecord selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(OriginalSignRecord record);

    int updateByPrimaryKey(OriginalSignRecord record);
    
    
    List<OriginalSignRecord>selectOriginalSignRecordByEmpId(String empId);
    
    /**
     * 
    * @Title: selectOriginalSignRecordByDateAndEmpId 
    * @Description: 根据各个班次班段时间查询班段内的打卡记录 
    * @param signMap
    * @return Map<String,Object>
    * @author xya
    * @date 2019年6月21日上午9:51:59
     */
    Map<String, Object> selectOriginalSignRecordByDateAndEmpId(@Param("signMap")Map<String, Object> signMap);
    
    /**
     * 
    * @Title: selectPageOrigSignInfo 
    * @Description: 根据查询参数查询原始打卡记录 
    * @param startDate 打卡开始时间
    * @param endDate 打卡结束时间
    * @param empIds 员工集合
    * @return List<OriginalSignRecord>
    * @author xya
    * @date 2019年6月21日上午9:54:52
     */
    List<OriginalSignRecord> selectPageOrigSignInfo(@Param("startDate")String startDate,
    		@Param("endDate")String endDate,@Param("empId")String empId);
}