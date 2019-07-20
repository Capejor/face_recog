package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.mapper.DepartmentMapper;
import com.faceRecog.manage.mapper.EmployeeMapper;
import com.faceRecog.manage.model.Department;
import com.faceRecog.manage.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 添加部门下级树节点
     *
     * @param department
     * @throws Exception
     */
    @Override
    public int insertDepartmentTree(Department department) throws Exception {
        return departmentMapper.insertSelective(department);
    }


    /**
     * 编辑部门
     *
     * @param department
     * @throws Exception
     */
    @Override
    public int updateDeptTree(Department department) throws Exception {
        return departmentMapper.updateByPrimaryKeySelective(department);
    }


    /**
     * 查询部门当前及下级部门
     *
     * @throws Exception
     */
    @Override
    public List<Map<String, Object>> selectDeptTree(String type) throws Exception {
        List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();
        //查询 当前部门信息及下级信息
        List<Map<String, Object>> nodeList = departmentMapper.selectTopDept();
        // nodeList = departmentMapper.selectOneById();
        if (nodeList != null && nodeList.size() > 0) {
            for (Map<String, Object> nodeMap : nodeList) {
                Map<String, Object> childMap = new HashMap<>();
                // 查询当前部门下的员工数
                if ("all".equals(type)) {
                    int count = employeeMapper.selectAllEmpCountByDeptId((String) nodeMap.get("id"));
                    childMap.put("id", nodeMap.get("id"));
                    childMap.put("label", nodeMap.get("deptName") + "(" + count + "人)");
                    childMap.put("parentId", nodeMap.get("parentId"));
                    setDeptTree(childMap, "all");
                    childList.add(childMap);
                }
                if ("emp".equals(type)) {
                    int count = employeeMapper.selectEmployeeCountByDeptId((String) nodeMap.get("id"));
                    childMap.put("id", nodeMap.get("id"));
                    childMap.put("label", nodeMap.get("deptName") + "(" + count + "人)");
                    childMap.put("parentId", nodeMap.get("parentId"));
                    setDeptTree(childMap, "emp");
                    childList.add(childMap);
                }
                if ("dimiss".equals(type)) {
                    int count = employeeMapper.selectDimissEmpCountByDeptId((String) nodeMap.get("id"));
                    childMap.put("id", nodeMap.get("id"));
                    childMap.put("label", nodeMap.get("deptName") + "(" + count + "人)");
                    childMap.put("parentId", nodeMap.get("parentId"));
                    setDeptTree(childMap, "dimiss");
                    childList.add(childMap);
                }
            }
        }
        return childList;
    }

    //递归
    public void setDeptTree(Map<String, Object> map, String type) {
        List<Map<String, Object>> childList = new ArrayList<>();
        map.put("children", childList);
        //
        String parentId = map.get("id").toString();
        List<Map<String, Object>> deptList = departmentMapper.selectOneById(parentId);
        if (deptList != null && deptList.size() > 0) {
            for (Map<String, Object> deptMap : deptList) {
                Map<String, Object> childMap = new HashMap<>();
                // 查询当前部门下的员工数
                if ("all".equals(type)) {
                    int count = employeeMapper.selectAllEmpCountByDeptId((String) deptMap.get("id"));
                    childMap.put("id", deptMap.get("id"));
                    childMap.put("label", deptMap.get("deptName") + "(" + count + "人)");
                    childMap.put("parentId", deptMap.get("parentId"));
                    setDeptTree(childMap, "all");
                    childList.add(childMap);
                }
                if ("emp".equals(type)) {
                    int count = employeeMapper.selectEmployeeCountByDeptId((String) deptMap.get("id"));
                    childMap.put("id", deptMap.get("id"));
                    childMap.put("label", deptMap.get("deptName") + "(" + count + "人)");
                    childMap.put("parentId", deptMap.get("parentId"));
                    setDeptTree(childMap, "emp");
                    childList.add(childMap);
                }
                if ("dimiss".equals(type)) {
                    int count = employeeMapper.selectDimissEmpCountByDeptId((String) deptMap.get("id"));
                    childMap.put("id", deptMap.get("id"));
                    childMap.put("label", deptMap.get("deptName") + "(" + count + "人)");
                    childMap.put("parentId", deptMap.get("parentId"));
                    setDeptTree(childMap, "dimiss");
                    childList.add(childMap);
                }
            }
        }
    }

    /**
     * @Description 查询部门员工权限树
     * @Author Capejor
     * @Date 2019-05-28 20:49
     **/
    @Override
    public List<Map<String, Object>> selectAuthDeptTree() throws Exception {
        List<Map<String, Object>> childList = new ArrayList<>();
        //查询 当前部门信息及下级信息
        List<Map<String, Object>> nodeList = departmentMapper.selectTopDept();
        if (nodeList != null && nodeList.size() > 0) {
            for (Map<String, Object> nodeMap : nodeList) {
                Map<String, Object> childMap = new HashMap<>();
                childMap.put("id", nodeMap.get("id"));
                childMap.put("label", nodeMap.get("deptName"));
                childMap.put("parentId", nodeMap.get("parentId"));
                setDeptAuthTree(childMap);
                childList.add(childMap);

            }
        }
        return childList;
    }
    //递归
    public void setDeptAuthTree(Map<String, Object> map) {
        List<Map<String, Object>> childList = new ArrayList<>();
        map.put("children", childList);
        String parentId = map.get("id").toString();
        List<Map<String, Object>> deptList = departmentMapper.selectOneById(parentId);
        if (deptList != null && deptList.size() > 0) {
            for (Map<String, Object> deptMap : deptList) {
                Map<String, Object> childMap = new HashMap<>();
                childMap.put("id", deptMap.get("id"));
                childMap.put("label", deptMap.get("deptName"));
                childMap.put("parentId", deptMap.get("parentId"));
                setDeptAuthTree(childMap);
                childList.add(childMap);

            }
        }
    }




    /**
     * 判断当前部门是否存在员工
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public int selectEmployeeCountByDeptId(String id) throws Exception {
        //查询当前部门及下级部门信息 获取id
        int count = employeeMapper.selectEmployeeCountByDeptId(id);

        return count;
    }


    /**
     * 删除部门树结构
     *
     * @param id
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDeptTree(String id) throws Exception {
        int affectNum = 0;

        //查询下级部门
        List<Department> deptList = departmentMapper.selectChildDeptById(id);
        if (deptList != null && deptList.size() > 0) {
            for (Department department : deptList) {
                affectNum = departmentMapper.deleteByPrimaryKey(department.getId());
                if (affectNum < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return -1;
                }
            }
        }
        affectNum = departmentMapper.deleteByPrimaryKey(id);//删除自己
        if (affectNum < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        return affectNum;
    }

    /**
     * 判断部门名称是否重复
     *
     * @param deptName
     * @return
     * @throws Exception
     */
    @Override
    public int selectByDeptNameAndParentId(String deptName, String parentId) throws Exception {
        return departmentMapper.selectByDeptNameAndParentId(deptName, parentId);
    }

    @Override
    public int selectByDeptNameAndParentIdExceptOwn(String id,String deptName, String parentId) throws Exception {
        return departmentMapper.selectByDeptNameAndParentIdExceptOwn(id,deptName, parentId);
    }

    /**
     * 返回当前添加部门信息
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public Department selectOneById(String id) throws Exception {
        return departmentMapper.selectByPrimaryKey(id);
    }

}


