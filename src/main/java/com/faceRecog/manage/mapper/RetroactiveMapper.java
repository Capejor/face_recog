package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.Retroactive;
import com.faceRecog.manage.model.vo.EmpRetVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RetroactiveMapper {
    int deleteByPrimaryKey(String id);

    int insert(Retroactive record);

    int insertSelective(Retroactive record);

    Retroactive selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Retroactive record);

    int updateByPrimaryKey(Retroactive record);

    //查询全公司员工的补签卡
    List<EmpRetVO> selectAllRetroactive();

    //查询当前部门员工的补签卡
    List<EmpRetVO> selectRetroactiveByDeptId(String deptId);

    //模糊搜索全公司员工补签卡
    List<EmpRetVO> selectByParams(Map<String,Object> map);

    //批量删除补签卡
    int deleteRetroactive(@Param("retIds")String[] retIds);
    

}