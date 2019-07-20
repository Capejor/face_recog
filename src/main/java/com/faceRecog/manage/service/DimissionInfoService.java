package com.faceRecog.manage.service;

import com.faceRecog.manage.model.DimissionInfo;
import com.faceRecog.manage.model.vo.DimissInfoVO;
import com.faceRecog.manage.util.Result;

import java.util.List;

public interface DimissionInfoService {

    //员工离职
    Result empToDimissById(DimissionInfo dimissionInfo, String[] empIds, String[] cardIds) throws Exception;

    //根据员工id查询离职日期
    String selectDimissTimeByEmpId(String empId) throws Exception;

    //员工复职
    Result dimissToEmpById(String empId, String employTime, String cardId) throws Exception;

    //指定部门复职
    Result dimissToEmpUpdateDept(String empId, String deptId, String cardId, String employTime) throws Exception;

    //批量删除离职员工
    int deleteEmployeeById(String[] empIds) throws Exception;

    //查询全公司离职员工
    List<DimissInfoVO> selectAllDimissInfo() throws Exception;

    //查询当前部门的离职员工
    List<DimissInfoVO> selectDimissByDeptId(String deptId) throws Exception;

    //全公司模糊搜索离职员工
    List<DimissInfoVO> selectAllByParams(String searchParam) throws Exception;




}
