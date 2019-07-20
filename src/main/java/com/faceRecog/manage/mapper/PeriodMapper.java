package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.Period;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PeriodMapper {
    int deleteByPrimaryKey(String id);

    int insert(Period record);

    int insertSelective(Period record);

    Period selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Period record);

    int updateByPrimaryKey(Period record);


    List<Map<String,Object>> selectPeriod();

    int deletePeriod(@Param("ids") String ids[]);
    
    /**
     * 
    * @Title: selectPeriodInfoByAgId 
    * @Description: 根据考勤组id查询周期排班信息 
    * @param agId
    * @return List<Period>
    * @author xya
    * @date 2019年5月22日上午11:56:55
     */
    List<Period> selectPeriodInfoByAgId(String agId);
    
    
    Period selectPeriodByAgId(String agId);

    //查询当前周期的天数
    int selectDayNumByPeriodId(@Param("periodId") String periodId);

    //修改当前周期的天数
    int updateByDayNum(Map<String,Object> map);

    //判断周期名称是否重复
    int selectCountByPeriodName(String periodName);

    //判断周期名称是否重复 除去自己
    int selectCountByPeriodNameExceptOwn(@Param("id") String id,@Param("periodName") String periodName) ;


}