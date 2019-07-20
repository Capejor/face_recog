package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.EmployeeInfo;

public interface EmployeeInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(EmployeeInfo record);

    int insertSelective(EmployeeInfo record);

    EmployeeInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(EmployeeInfo record);

    int updateByPrimaryKey(EmployeeInfo record);


    //根据 empId 修改员工详情
    int updateByEmpId(EmployeeInfo employeeInfo);


}