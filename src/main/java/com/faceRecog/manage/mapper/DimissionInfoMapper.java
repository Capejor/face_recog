package com.faceRecog.manage.mapper;


import com.faceRecog.manage.model.DimissionInfo;
import com.faceRecog.manage.model.vo.DimissInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DimissionInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(DimissionInfo record);

    int insertSelective(DimissionInfo record);

    DimissionInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(DimissionInfo record);

    int updateByPrimaryKey(DimissionInfo record);


    //员工离职
    int empToDimissById(String id);

    //员工表 isDimiss 置为 1
    int dimissToEmpById(Map<String,Object> map);

    //指定部门复职 isDimiss 置为 1
    int dimissToEmpUpdateDept(Map<String,Object> map);

    //根据员工id查询离职日期
    String selectDimissTimeByEmpId(String empId) throws Exception;

    //删除离职员工
    int deleteByEmpId(String empId);

    //查询全公司离职员工
    List<DimissInfoVO> selectAllDimissInfo();

    //查询当前部门的离职员工
    List<DimissInfoVO> selectDimissByDeptId(String deptId);

    //查询全公司离职员工
    List<DimissInfoVO> selectAllByParams(@Param("searchParam") String searchParam);








}