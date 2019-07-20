package com.faceRecog.manage.controller;

import com.faceRecog.manage.model.DimissionInfo;
import com.faceRecog.manage.model.vo.DimissInfoVO;
import com.faceRecog.manage.service.DimissionInfoService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName: DimissionInfoController
 * @Description: 离职信息 前端控制器
 * @author: Capejor
 * @date: 2019年4月30日
 */
@RestController
public class DimissionInfoController {

    @Autowired
    private DimissionInfoService dimissionInfoService;


    /**
     * 员工批量离职
     */
    @RequestMapping("/empToDimiss")
    public Result empToDimiss(DimissionInfo dimissionInfo, @RequestParam("empIds[]") String[] empIds,
                              @RequestParam("cardIds[]") String[] cardIds) throws Exception {
        return dimissionInfoService.empToDimissById(dimissionInfo, empIds, cardIds);
    }


    /**
     * 员工复职
     *
     * @param empId
     */
    @RequestMapping("/dimissToEmp")
    public Result dimissToEmp(@RequestParam String empId, @RequestParam String employTime, @RequestParam String cardId) throws Exception {

        if (StrKit.isBlank(empId)) {
            return Result.responseError("员工id不能为空！");
        }
        //根据员工id查询离职日期
        String dimissDate = dimissionInfoService.selectDimissTimeByEmpId(empId);
        if (employTime.compareTo(dimissDate) < 0) {
            return Result.responseError("复职时间不能在离职时间之前");
        }
        if (StrKit.isBlank(employTime)) {
            return Result.responseError("复职时间必填！");
        }
        if (StrKit.isBlank(cardId)) {
            return Result.responseError("卡号id不能为空！");
        }

        /*if (affectNum > 0) {
            result = Result.responseSuccess("复职成功");
        } else {
            result = Result.responseError("复职失败");
        }*/
        return dimissionInfoService.dimissToEmpById(empId, employTime, cardId);
    }

    /**
     * 员工指定部门复职
     *
     * @param empId
     */
    @RequestMapping("/dimissToEmpUpdateDept")
    public Result dimissToEmpUpdateDept(@RequestParam String empId, @RequestParam String deptId, @RequestParam String cardId,
                                        @RequestParam String employTime) throws Exception {
        if (StrKit.isBlank(empId)) {
            return Result.responseError("员工id不能为空");
        }
        if (StrKit.isBlank(deptId)) {
            return Result.responseError("部门id不能为空");
        }
        if (StrKit.isBlank(cardId)) {
            return Result.responseError("卡片id不能为空");
        }
        if (StrKit.isBlank(employTime)) {
            return Result.responseError("复职时间不能为空");
        }
        //根据员工id查询离职日期
        String dimissDate = dimissionInfoService.selectDimissTimeByEmpId(empId);
        if (employTime.compareTo(dimissDate) < 0) {
            return Result.responseError("复职时间不能在离职时间之前");
        }
        if ("-1".equals(deptId)) {
            return Result.responseError("员工不能复职到全公司！！！");
        }
        if ("0".equals(deptId)) {
            return Result.responseError("员工不能复职到设备端录入部门！！！");
        }

       /* if (affectNum > 0) {
            result = Result.responseSuccess("指定部门复职成功");
        } else {
            result = Result.responseError("指定部门复职失败");
        }*/
        return dimissionInfoService.dimissToEmpUpdateDept(empId, deptId, cardId, employTime);
    }


    /**
     * @Description 查询当前部门的离职员工
     * @Author Capejor
     * @Date 2019-06-17 19:51
     **/
    @RequestMapping("/selectDimissByDeptId")
    public Result selectDimissByDeptId(@RequestParam String deptId) throws Exception {
        Result result;
        List<DimissInfoVO> dimissionInfoList;
        if ("-1".equals(deptId)) {
            dimissionInfoList = dimissionInfoService.selectAllDimissInfo();
        } else {
            dimissionInfoList = dimissionInfoService.selectDimissByDeptId(deptId);
        }
        if (dimissionInfoList != null && dimissionInfoList.size() > 0) {
            result = Result.responseSuccess("查询成功", dimissionInfoList);
        } else {
            result = Result.responseSuccess("部门无离职员工", dimissionInfoList);
        }
        return result;
    }


    /**
     * @Description 全公司模糊搜索离职员工
     * @Author Capejor
     * @Date 2019-06-17 19:52
     **/
    @RequestMapping("/selectAllByParams")
    public Result selectAllByParams(@RequestParam(required = false) String searchParam) throws Exception {
        Result result;
        List<DimissInfoVO> dimissionInfoList = new ArrayList<>();
        if (StrKit.isBlank(searchParam)) {
            return Result.responseSuccess("无数据", dimissionInfoList);
        } else {
            dimissionInfoList = dimissionInfoService.selectAllByParams(searchParam);
        }
        if (dimissionInfoList != null && dimissionInfoList.size() > 0) {
            result = Result.responseSuccess("查询成功", dimissionInfoList);
        } else {
            result = Result.responseSuccess("无数据", dimissionInfoList);
        }
        return result;
    }


    /**
     * 批量删除离职员工
     */
    @RequestMapping("/deleteEmployee")
    public Result deleteEmployee(@RequestParam("empIds[]") String[] empIds) throws Exception {
        Result result;
        int deleteResult = dimissionInfoService.deleteEmployeeById(empIds);
        if (deleteResult > 0) {
            result = Result.responseSuccess("删除成功");
        } else {
            result = Result.responseError("删除失败");
        }
        return result;
    }


    public static void main(String[] args) {
        String s1 = "2019-05-13";
        String s2 = "2019-06-18";
        if (s1.compareTo(s2) < 0) {
            System.out.println("时间小");
        } else {
            System.out.println("---");
        }
    }

}
