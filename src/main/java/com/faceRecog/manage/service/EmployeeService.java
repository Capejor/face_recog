package com.faceRecog.manage.service;

import com.faceRecog.manage.model.Employee;
import com.faceRecog.manage.model.EmployeeInfo;
import com.faceRecog.manage.model.FaceFile;
import com.faceRecog.manage.model.serverVO.EmpServer;
import com.faceRecog.manage.model.vo.EmpVO;
import com.faceRecog.manage.util.Result;
import net.sf.json.JSONObject;


import java.util.List;
import java.util.Map;

public interface EmployeeService {


     //添加员工基本信息
    int insertEmployee(Employee employee, EmployeeInfo employeeInfo,String cardId) throws Exception;

    //设备端添加员工基本信息
    int insertEmpServer(Employee employee, EmployeeInfo employeeInfo, FaceFile faceFile, String cardId) throws Exception;

     //修改员工信息
    int updateEmployee(Employee employee, EmployeeInfo employeeInfo) throws Exception;

    //根部部门id查询员工
    List<EmpVO> selectEmployee(String deptId) throws Exception;

    //全公司模糊搜索员工信息
    List<EmpVO> selectAllEmpByParam(String searchParam)throws Exception;

    List<EmpVO> selectAllEmployee()throws Exception;

    //查询员工记录
    List<EmpVO> selectEmpRecord(Map<String, Object> map) throws Exception;

    //查询全公司员工记录
    List<EmpVO> selectAllEmpRecord(Map<String, Object> map) throws Exception;

    //权限分配
    int allocationAuth(String[] empIds, String authId,String[] eqIds) throws Exception;

    //修改权限分配
    int updateAuth(String[] empIds, String authId,String[] eqIds) throws Exception;

    //相同的员工与绑定设备只能拥有一个权限
    int selectAuthCount(String []empIds,String []eqIds) throws Exception;

    //int deleteAllocationAuth(String[] empIds,String[] equipIds) throws Exception;
    Result deleteAllocationAuth(JSONObject jsonObject) throws Exception;

    //调动员工部门
    int updateDeptByEmp(String[] ids, String deptId) throws Exception;

    //设备端 查询所有员工
    List<EmpServer> selectAllEmp() throws Exception;

    //导出员工信息
    List<EmpVO> exportEmployee()throws Exception;

    List<Map<String, Object>>selectAllEmpWorkSchedule()throws Exception;

    List<Map<String, Object>>selectAllEmpWorkPeriodSchedule()throws Exception;

    List<Map<String, Object>> selectAllEmpAttendInfo()throws Exception;
    
    int updateEmpFaceRegState(String empId,String state)throws Exception;

    int selectCountByIsDimiss(String empId) throws Exception;

}
