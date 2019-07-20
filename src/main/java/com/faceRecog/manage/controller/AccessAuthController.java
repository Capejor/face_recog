package com.faceRecog.manage.controller;

import com.faceRecog.manage.model.AccessAuth;
import com.faceRecog.manage.model.vo.AccessTimeVO;
import com.faceRecog.manage.model.vo.AuthVO;
import com.faceRecog.manage.service.AccessAuthService;
import com.faceRecog.manage.service.EmployeeService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @ClassName: AccessAuthController
 * @Description: 门禁权限 前端控制器
 * @author: Capejor
 * @date: 2019年4月30日
 */
@RestController
public class AccessAuthController {

    @Autowired
    private AccessAuthService accessAuthService;

    @Autowired
    private EmployeeService employeeService;


    /**
     * @Description  添加门禁时段
     * @Author Capejor
     * @Date 2019-06-18 19:32
     **/
    @RequestMapping("/insertAccessAuth")
    public Result insertAccessAuth(AccessAuth accessAuth) throws Exception {
        Result result;
        if (StrKit.isBlank(accessAuth.getAuthName())) {
            result = Result.responseError("门禁名称不能为空");
            return result;
        }
        if (StrKit.isBlank(accessAuth.getTimeOne())) {
            result = Result.responseError("门禁时区一不能为空");
            return result;
        }
        int count = accessAuthService.selectCountByAuthName(accessAuth.getAuthName());
        if (count > 0) {
            return Result.responseError("门禁时段名称不能重复");
        }
        accessAuth.setId(UUIDGenerator.getUUID());
        accessAuth.setStatus("1");
        accessAuth.setCreateTime(new Date());
        accessAuth.setCreateUserId("1");
        int insertAuthResult = accessAuthService.insertAccAuth(accessAuth);
        if (insertAuthResult > 0) {
            result = Result.responseSuccess("添加成功");
        } else {
            result = Result.responseError("添加失败");
        }
        return result;
    }

    /**
     * @Description 修改门禁，判断门禁是否被占用
     * @Author Capejor
     * @Date 2019-06-18 9:31
     **/
    @RequestMapping("/isAllowUpdateAccessAuth")
    public Result isAllowUpdateAccessAuth(String authId) throws Exception{
        Result result;
        int affectNum = accessAuthService.selectCountById(authId);
        if (affectNum > 0) {
            result= Result.responseError("当前门禁已被使用，是否修改？");
        }else {
            result= Result.responseSuccess("当前门禁未被占用，可修改。");
        }
        return result;
    }

    /**
     * 修改门禁时段
     *
     * @param accessAuth
     * @return
     * @throws Exception
     */
    @RequestMapping("/updateAccessAuth")
    public Result updateAccessAuth(AccessAuth accessAuth) throws Exception {
        Result result;

        if (StrKit.isBlank(accessAuth.getAuthName())) {
            result = Result.responseError("门禁名称不能为空");
            return result;
        }
        if (StrKit.isBlank(accessAuth.getTimeOne())) {
            result = Result.responseError("门禁时区一不能为空");
            return result;
        }
        int count = accessAuthService.selectCountByAuthNameExceptOwn(accessAuth.getId(), accessAuth.getAuthName());
        if (count > 0) {
            return Result.responseError("门禁权限名称不能重复");
        }
        accessAuth.setUpdateTime(new Date());
        int updateAuthResult = accessAuthService.updateAccAuth(accessAuth);
        if (updateAuthResult > 0) {
            result = Result.responseSuccess("修改成功");
        } else {
            result = Result.responseError("修改失败");
        }
        return result;
    }


    /**
     * 查询门禁时段
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/selectAccessAuth")
    public Result selectAllAccessAuth() throws Exception {
        Result result;
        List<AccessAuth> authList = accessAuthService.selectAllAccessAuth();
        if (authList != null && authList.size() > 0) {
            result = Result.responseSuccess("查询成功", authList);
        } else {
            result = Result.responseSuccess("无数据", authList);
        }
        return result;
    }


    /**
     * 批量删除门禁时段
     *
     * @param ids
     * @return
     * @throws Exception
     */
    @RequestMapping("/deleteAccessAuth")
    public Result deleteAccAuthById(String[] ids) throws Exception {
        Result result;
        for (int i = 0; i < ids.length; i++) {
            int affectNum = accessAuthService.selectCountById(ids[i]);
            if (affectNum > 0) {
                return Result.responseError("当前门禁已被使用，不能删除!");
            }
        }

        int deleteResult = accessAuthService.deleteAccAuthById(ids);
        if (deleteResult > 0) {
            result = Result.responseSuccess("删除成功");
        } else {
            result = Result.responseError("删除失败");
        }
        return result;
    }

    /**
     * 配置权限分配
     *
     * @param empIds
     * @param authId
     * @return
     */
    @RequestMapping("/allocationAuth")
    public Result allocationAuth(@RequestParam("empIds[]") String[] empIds,
                                 @RequestParam String authId, @RequestParam("eqIds[]") String[] eqIds) throws Exception {
        Result result;
        int allocationResult = 0;

        int authCount = employeeService.selectAuthCount(empIds, eqIds);
        if (authCount >= 1) {
            //return Result.responseError("相同的员工与绑定设备只能拥有一个权限，请重新绑定设备！！！");
            allocationResult = employeeService.updateAuth(empIds, authId, eqIds);
        } else {
            allocationResult = employeeService.allocationAuth(empIds, authId, eqIds);
        }
        if (allocationResult > 0) {
            result = Result.responseSuccess("权限分配成功");
        } else {
            result = Result.responseError("权限分配失败");
        }
        return result;
    }


    /**
     * 删除权限分配
     *
     * @return
     */
    @RequestMapping("/deleteAuth")
    //public Result deleteAllocationAuth(@RequestParam("empIds[]") String[] empIds,@RequestParam("equipIds[]") String[] equipIds) throws Exception {
    //public Result deleteAllocationAuth(@RequestParam("authList") List<Map<String,Object>> authList) throws Exception {
    public Result deleteAllocationAuth(@RequestBody JSONObject jsonObject) throws Exception {
        Result result;
        result = employeeService.deleteAllocationAuth(jsonObject);
        return result;
    }

    /**
     * @Description 查询当前部门所有员工门禁权限
     * @Author Capejor
     * @Date 2019-06-17 20:49
     **/
    @RequestMapping("/selectAuthByDeptId")
    public Result selectAuthByDeptId(@RequestParam String deptId) throws Exception {
        Result result;
        List<AuthVO> authList;
        if ("-1".equals(deptId)) {
            authList = accessAuthService.selectAllEmpAuth();
        } else {
            authList = accessAuthService.selectAuthByDeptId(deptId);
        }
        if (authList != null && authList.size() > 0) {
            result = Result.responseSuccess("查询成功", authList);
        } else {
            result = Result.responseSuccess("无数据");
        }
        return result;
    }

    /**
     * @Description 模糊搜索全公司员工门禁权限
     * @Author Capejor
     * @Date 2019-06-17 20:48
     **/
    @RequestMapping("/selectEmpAuthByParams")
    public Result selectEmpAuthByParams(@RequestParam(required = false) String searchParam )throws Exception {
        Result result;
        List<AuthVO> authList = new ArrayList<>();
        if (StrKit.isBlank(searchParam)) {
            return Result.responseSuccess("无数据",authList);
        } else {
            authList = accessAuthService.selectEmpAuthByParams(searchParam);
        }
        if (authList != null && authList.size() > 0) {
            result = Result.responseSuccess("查询成功", authList);
        } else {
            result = Result.responseSuccess("无数据",authList);
        }
        return result;
    }



    /**
     * @Description 根据权限id查询门禁时段
     * @Author Capejor
     * @Date 2019-06-17 15:06
     **/
    @RequestMapping("/selectTimezoneById")
    public Result selectTimezoneById(String id) throws Exception {
        Result result;
        List<AccessTimeVO> timeVOS = accessAuthService.selectTimezoneById(id);
        if (timeVOS != null && timeVOS.size() > 0) {
            result = Result.responseSuccess("查询成功",timeVOS);
        } else {
            result = Result.responseError("无数据",timeVOS);
        }
        return result;
    }


    /**
     * @Description 启用禁用
     * @Author Capejor
     * @Date 2019-06-18 19:40
     **/
    @RequestMapping("/updateStatus")
    public Result updateStatus(@RequestParam String id,@RequestParam String status)throws Exception{
        Result result;
        int affectNum = accessAuthService.updateStatus(id,status);
        if (affectNum >0){
            result = Result.responseSuccess("操作成功！",true);
        }else {
            result = Result.responseError("操作失败！",false);
        }
        return result;
    }

}
