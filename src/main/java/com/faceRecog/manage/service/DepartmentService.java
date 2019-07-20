package com.faceRecog.manage.service;

import com.faceRecog.manage.model.Department;

import java.util.List;
import java.util.Map;

public interface DepartmentService {

    /**
     * 添加部门下级树节点
     *
     * @param department
     * @throws Exception
     */
    int insertDepartmentTree(Department department) throws Exception;


    /**
     * 编辑部门
     *
     * @param department
     * @throws Exception
     */
    int updateDeptTree(Department department) throws Exception;


    /**
     * 查询部门当前及下级部门
     *
     * @throws Exception
     */
    List<Map<String, Object>> selectDeptTree(String type) throws Exception;


    /**
     * 查询部门员工权限树
     *
     * @throws Exception
     */
    List<Map<String, Object>> selectAuthDeptTree() throws Exception;



    /**
     * 判断当前部门是否存在员工
     *
     * @param id
     * @return
     * @throws Exception
     */
    int selectEmployeeCountByDeptId(String id) throws Exception;


    /**
     * 删除部门树结构
     *
     * @param id
     * @throws Exception
     */
    int deleteDeptTree(String id) throws Exception;


    /**
     * 判断部门名称是否重复
     *
     * @param deptName
     * @return
     * @throws Exception
     */
    int selectByDeptNameAndParentId(String deptName, String parentId) throws Exception;


    /**
     * 判断部门名称是否重复 除去自己
     *
     * @return
     * @throws Exception
     */
    int selectByDeptNameAndParentIdExceptOwn(String id, String deptName, String parentId) throws Exception;


    /**
     * 返回当前添加部门信息
     *
     * @param id
     * @return
     * @throws Exception
     */
    Department selectOneById(String id) throws Exception;


}
