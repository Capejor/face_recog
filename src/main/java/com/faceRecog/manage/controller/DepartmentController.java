package com.faceRecog.manage.controller;

import com.faceRecog.manage.model.Department;
import com.faceRecog.manage.service.DepartmentService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: DepartmentController
 * @Description: 部门 前端控制器
 * @author: Capejor
 * @date: 2019年4月30日
 */
@RestController
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;


    /**
     * 添加部门下级树节点
     *
     * @param department
     * @return
     * @throws Exception
     */
    @RequestMapping("/insertOrUpdateDeptTree")
    public Result insertOrUpdateDeptTree(Department department, @RequestParam String type) throws Exception {
        Result result = null;
        if ("insert".equals(type)) {
            if (StrKit.isBlank(department.getParentId())) {
                return Result.responseError("父级id不能为空");
            }
            if (StrKit.isBlank(department.getDeptName())) {
                return Result.responseError("部门名称不能为空");
            }
            //判断部门名称是否重复
            int countResult = departmentService.selectByDeptNameAndParentId(department.getDeptName(), department.getParentId());
            if (countResult > 0) {
                return Result.responseError("部门名称不能重复");
            }
            department.setId(UUIDGenerator.getUUID());
            department.setCreateTime(new Date());
            department.setCreateUserId("1");
            department.setStatus("1");
            int insertTreeResult = departmentService.insertDepartmentTree(department);
            //返回当前添加部门信息
            //Department dept = departmentService.selectOneById(department.getId());
            if (insertTreeResult > 0) {
                result = Result.responseSuccess("添加成功", department);
            } else {
                result = Result.responseError("添加失败");
            }
        } else if ("update".equals(type)) {
            department.setUpdateTime(new Date());
            if ("1".equals(department.getId())) {
                return Result.responseError("默认分组不能修改");
            }
            if ("0".equals(department.getId())) {
                return Result.responseError("设备端录入部门不能修改");
            }
            //判断部门名称是否重复
            int countResult = departmentService.selectByDeptNameAndParentIdExceptOwn(department.getId(),department.getDeptName(), department.getParentId());
            if (countResult > 0) {
                return Result.responseError("部门名称不能重复");
            }
            int updateResult = departmentService.updateDeptTree(department);
            //返回当前修改部门信息
            Department dept = departmentService.selectOneById(department.getId());
            if (updateResult > 0) {
                result = Result.responseSuccess("修改成功", dept);
            } else {
                result = Result.responseError("修改失败");
            }
        }
        return result;
    }


    /**
     * 查询部门当前及下级部门
     *
     * @return
     */
    @RequestMapping("/selectDeptTree")
    public Result selectDeptTree(@RequestParam String type) throws Exception {
        Result result;
        if (StrKit.isBlank(type)) {
            return Result.responseError("type不能为空");
        }
        List<Map<String, Object>> deptList = departmentService.selectDeptTree(type);
        if (StrKit.notNull(deptList)) {
            result = Result.responseSuccess("查询成功", deptList);
        } else {
            result = Result.responseSuccess("部门为空");
        }
        return result;
    }


    /**
     * @Description 查询部门员工权限树
     * @Author Capejor
     * @Date 2019-05-28 20:47
     **/
    @RequestMapping("/selectAuthDeptTree")
    public Result selectAuthDeptTree() throws Exception {
        Result result;
        List<Map<String, Object>> deptList = departmentService.selectAuthDeptTree();
        if (StrKit.notNull(deptList)) {
            result = Result.responseSuccess("查询成功", deptList);
        } else {
            result = Result.responseSuccess("部门为空");
        }
        return result;
    }



    /**
     * 判断当前部门是否存在员工
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping("/selectExistEmp")
    public Result selectExistEmp(@RequestParam String id) throws Exception {
        Result result;
        int count = departmentService.selectEmployeeCountByDeptId(id);
        if (count > 0) {
            result = Result.responseError("部门下有员工,不能删除");
        } else {
            result = Result.responseSuccess("部门下无员工,可删除");
        }
        return result;
    }


    /**
     * 删除当前树节点
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping("/deleteDeptTree")
    public Result deleteDeptTree(@RequestParam String id) throws Exception {
        Result result;
        if ("-1".equals(id)) {
            return Result.responseError("全公司不能删除");
        }
        if ("1".equals(id)) {
            return Result.responseError("默认分组不能删除");
        }
        if ("0".equals(id)) {
            return Result.responseError("设备端录入部门不能删除");
        }
        int deleteResult = departmentService.deleteDeptTree(id);
        if (deleteResult > 0) {
            result = Result.responseSuccess("删除成功");
        } else {
            result = Result.responseError("删除失败");
        }
        return result;
    }


}
