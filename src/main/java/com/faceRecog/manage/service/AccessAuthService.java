package com.faceRecog.manage.service;

import com.faceRecog.manage.model.AccessAuth;
import com.faceRecog.manage.model.vo.AccessTimeVO;
import com.faceRecog.manage.model.vo.AuthVO;

import java.util.List;
import java.util.Map;


public interface AccessAuthService {

    int insertAccAuth(AccessAuth accessAuth) throws Exception;

    int updateAccAuth(AccessAuth accessAuth) throws Exception;

    List<AccessAuth> selectAllAccessAuth() throws Exception;

    int deleteAccAuthById(String[] ids) throws Exception;

    int selectCountById(String id) throws Exception;

    int selectCountByAuthName(String authName)throws Exception;

    int selectCountByAuthNameExceptOwn(String id,String authName)throws Exception;

    //查询全公司员工权限
    List<AuthVO> selectAllEmpAuth() throws Exception;

    //查询当前部门员工权限
    List<AuthVO> selectAuthByDeptId(String deptId) throws Exception;

    //查询全公司员工权限
    List<AuthVO> selectEmpAuthByParams(String searchParam) throws Exception;

    List<AccessTimeVO> selectTimezoneById(String id) throws Exception;

    //设备端 查询设备的员工门禁权限
    List<Map<String,Object>> selectEmpAuthBySn(String sn)throws Exception;


    //启用禁用
    int updateStatus(String id,String status)throws Exception;


}
