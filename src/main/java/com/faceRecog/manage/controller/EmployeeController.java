package com.faceRecog.manage.controller;

import com.faceRecog.manage.model.Employee;
import com.faceRecog.manage.model.EmployeeInfo;
import com.faceRecog.manage.model.vo.AuthVO;
import com.faceRecog.manage.model.vo.EmpVO;
import com.faceRecog.manage.service.EmployeeService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.excelUtils.ExportPOIUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


/**
 * @ClassName: EmployeeController
 * @Description: 员工模块 前端控制器
 * @author: Capejor
 * @date: 2019年4月30日
 */
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 添加员工基本信息
     *
     * @param employee
     */
    @RequestMapping("/insertEmployee")
    public Result insertEmployee(Employee employee, EmployeeInfo employeeInfo, @RequestParam String cardId) throws Exception {
        Result result;
        if (StrKit.isBlank(employee.getName())) {
            return Result.responseError("员工姓名不能为空");
        }
        if (StrKit.isBlank(employee.getPhone())) {
            return Result.responseError("员工电话不能为空");
        }
        if (StrKit.isBlank(employee.getDeptId())) {
            return Result.responseError("员工所在部门id不能为空");
        }
        if ("-1".equals(employee.getDeptId())) {
            return Result.responseError("全公司下不能添加，请选择具体部门添加！！！");
        }
        if (StrKit.isBlank(cardId)) {
            return Result.responseError("员工卡id不能为空");
        }
        if ("0".equals(employee.getDeptId())){
            return Result.responseError("不能在设备端录入部门添加员工！！！");
        }
        employee.setId(UUIDGenerator.getUUID());
        employee.setCreateTime(new Date());
        employee.setStatus("1");
        employee.setCreateUserId("1");
        employee.setIsDimiss("1");
        employee.setFaceReg("0");
        //详情
        if (StrKit.isBlank(employeeInfo.getEmerPhone())) {
            return Result.responseError("紧急联系人电话不能为空");
        }
        employeeInfo.setId(UUIDGenerator.getUUID());
        employeeInfo.setEmpId(employee.getId());
        employeeInfo.setCreateTime(new Date());
        employeeInfo.setStatus("1");
        employeeInfo.setCreateUserId(employee.getCreateUserId());
        int insertResult = employeeService.insertEmployee(employee, employeeInfo, cardId);
        if (insertResult > 0) {
            result = Result.responseSuccess("添加成功");
        } else {
            result = Result.responseError("添加失败");
        }
        return result;
    }

    /**
     * 修改员工信息
     *
     * @param employee
     */
    @RequestMapping("/updateEmployee")
    public Result updateEmployee(Employee employee, EmployeeInfo employeeInfo) throws Exception {
        Result result;
        if (StrKit.isBlank(employee.getName())) {
            return Result.responseError("员工姓名不能为空");
        }
        if (StrKit.isBlank(employee.getPhone())) {
            return Result.responseError("员工电话不能为空");
        }
        if (StrKit.isBlank(employeeInfo.getEmerPhone())) {
            return Result.responseError("紧急联系人电话不能为空");
        }
        employeeInfo.setEmpId(employee.getId());
        int updateResult = employeeService.updateEmployee(employee, employeeInfo);
        if (updateResult > 0) {
            result = Result.responseSuccess("修改成功");
        } else {
            result = Result.responseError("修改失败");
        }
        return result;
    }


    /**
     * @Description 模糊搜索全公司员工
     * @Author Capejor
     * @Date 2019-06-17 21:06
     **/
    @RequestMapping("/selectEmployee")
    public Result selectEmployee( @RequestParam(required = false) String searchParam) throws Exception {
        Result result;
        List<EmpVO> empVOList = new ArrayList<>();
        if (StrKit.isBlank(searchParam)) {
            return Result.responseSuccess("无数据",empVOList);
        } else {
            empVOList = employeeService.selectAllEmpByParam(searchParam);
        }
        if (empVOList != null && empVOList.size() > 0) {
            result = Result.responseSuccess("查询成功", empVOList);
        } else {
            result = Result.responseSuccess("无数据", empVOList);
        }
        return result;
    }

    /**
     * @Description 根部部门id查询员工
     * @Author Capejor
     * @Date 2019-06-17 19:10
     **/
    @RequestMapping("/selectByDeptId")
    public Result selectByDeptId(@RequestParam String deptId) throws Exception{
        Result result;
        List<EmpVO> empVOList;
        if ("-1".equals(deptId)) {
            empVOList = employeeService.selectAllEmployee();
        } else {
            empVOList = employeeService.selectEmployee(deptId);
        }
        if (empVOList != null && empVOList.size() > 0) {
            result = Result.responseSuccess("查询成功", empVOList);
        } else {
            result = Result.responseSuccess("当前部门无员工", empVOList);
        }
        return result;
    }
   /* @RequestMapping("/selectEmployee")
    public Result selectEmployee(//@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "500") int pageSize,
                                 @RequestParam String deptId, @RequestParam(required = false) @Param(value = "searchParam") String searchParam) throws Exception {
        Result result;
        //PageInfo<EmpVO> pageInfo;
        Map<String, Object> map = new HashMap<>();
        *//*map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);*//*
        map.put("deptId", deptId);
        map.put("searchParam", searchParam);
        List<EmpVO> empVOList;
        if ("-1".equals(deptId)) {
            empVOList = employeeService.selectAllEmployee(searchParam);
        } else {
            empVOList = employeeService.selectEmployee(map);
        }
        //if (pageInfo.getList() != null && pageInfo.getList().size() > 0) {
        if (empVOList != null && empVOList.size() > 0) {
            result = Result.responseSuccess("查询成功", empVOList);
        } else {
            result = Result.responseSuccess("当前部门无员工", empVOList);
        }
        return result;
    }*/


    /**
     * 查询员工记录
     *
     * @throws Exception
     */
    @RequestMapping("/selectEmpRecord")
    public Result selectEmpRecord(@RequestParam String deptId, @RequestParam(required = false) String startTime,
                                  @RequestParam(required = false) String endTime, @RequestParam(required = false) String empName) throws Exception {
        Result result;
        Map<String, Object> map = new HashMap<>();
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("deptId", deptId);
        map.put("empName", empName);
        List<EmpVO> empList;
        if ("-1".equals(deptId)) {
            empList = employeeService.selectAllEmpRecord(map);
        }else {
            empList = employeeService.selectEmpRecord(map);
        }
        if (empList != null && empList.size() > 0) {
            result = Result.responseSuccess("查询成功", empList);
        } else {
            result = Result.responseSuccess("当前部门无员工", empList);
        }
        return result;
    }



    /**
     * 调动员工
     *
     * @param ids
     * @param deptId
     */
    @RequestMapping("/updateDeptByEmp")
    public Result updateDeptByEmp(@RequestParam("ids[]") String[] ids, @RequestParam String deptId
                                     ) throws Exception {
        Result result;
            if (StrKit.isBlank(deptId)){
                return Result.responseError("部门id不能为空");
            }
            if ("-1".equals(deptId)) {
                return Result.responseError("员工不能调动到全公司！！！");
            }
            if ("0".equals(deptId)){
                return Result.responseError("员工不能调动到设备端录入部门！！！");
            }
            int updateResult = employeeService.updateDeptByEmp(ids, deptId);
            if (updateResult > 0) {
                result = Result.responseSuccess("调动部门成功");
            } else {
                result = Result.responseError("调动部门失败");
            }
        return result;
    }


    @RequestMapping("/exportEmployee")
    public void exportList(HttpServletResponse response) throws Exception {

        String fileName = "员工信息列表";

        List<EmpVO> groupList = employeeService.exportEmployee();

        // 列名
        String columnNames[] = {"员工id", "姓名", "性别", "学历", "身份证号", "民族", "籍贯", "电话", "地址",
                "入职时间", "岗位", "人脸是否注册", "部门名称", "分组名称", "政治面貌", "婚否", "血型",
                "紧急联系人", "紧急联系人电话", "车牌号", "劳动合同", "劳动合同号", "备注", "卡号", "照片路径"};
        // map中的key
        String keys[] = {"id", "name", "sex", "education", "idCard", "nation", "nativePlace", "phone", "address",
                "employTime", "jobPost", "faceReg", "deptName", "groupName", "politics", "isMarried", "blood",
                "emerPer", "emerPhone", "carNo", "laborCon", "laborConNo", "remark", "cardNum", "photoUrl"};
        try {
            ExportPOIUtils.start_download(response, fileName, groupList, columnNames, keys);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
