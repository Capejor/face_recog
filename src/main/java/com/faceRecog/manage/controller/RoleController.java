package com.faceRecog.manage.controller;

import com.faceRecog.manage.model.Role;
import com.faceRecog.manage.model.vo.RoleVO;
import com.faceRecog.manage.service.RoleService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;


/**
 * @author Capejor
 * @className: RoleController
 * @Description: TODO
 * @date 2019-05-20 13:44
 */
@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;


    /**
     * @Description 添加角色
     * @Author Capejor
     * @Date 2019-05-20 13:35
     **/
    @RequestMapping("/insertRole")
    public Result insertRole(Role role) throws Exception {
        Result result;
        if (StrKit.isBlank(role.getRoleName())) {
            return Result.responseError("角色名不能为空");
        }
        if (StrKit.isBlank(role.getRemark())) {
            return Result.responseError("备注不能为空");
        }
        role.setId(UUIDGenerator.getUUID());
        role.setCreateTime(new Date());
        role.setCreateUserId("1");
        role.setStatus("1");
        int count = roleService.selectCountByRoleName(role.getRoleName());
        if (count > 0) {
            return Result.responseError("角色名已存在，请重新输入角色名称!");
        }
        int insertNum = roleService.insertRole(role);
        if (insertNum > 0) {
            result = Result.responseSuccess("角色添加成功");
        } else {
            result = Result.responseError("角色添加失败");
        }
        return result;
    }


    /**
     * @Description 修改角色，判断该角色是否被用户绑定
     * @Author Capejor
     * @Date 2019-06-20 19:15
     **/
    @RequestMapping("/isAllowUpdateRole")
    public Result isAllowUpdateRole(String roleId)throws Exception{
        Result result;
        int affectNum = roleService.selectCountByRoleId(roleId);
        if (affectNum > 0) {
            result= Result.responseError("该角色已绑定用户，是否修改？");
        }else {
            result= Result.responseSuccess("该角色未绑定用户，可修改。");
        }
        return result;
    }

    /**
     * @Description 修改角色
     * @Author Capejor
     * @Date 2019-05-20 13:46
     **/
    @RequestMapping("/updateRole")
    public Result updateRole(Role role) throws Exception {
        Result result;
        if (StrKit.isBlank(role.getRoleName())) {
            return Result.responseError("角色名不能为空");
        }
        if (StrKit.isBlank(role.getRemark())) {
            return Result.responseError("备注不能为空");
        }
        role.setUpdateTime(new Date());
        int count = roleService.selectCountByRoleNameExceptOwn(role.getRoleName(), role.getId());
        if (count > 0) {
            return Result.responseError("角色名已存在，请重新输入角色名称!");
        }
        int updateNum = roleService.updateRole(role);
        if (updateNum > 0) {
            result = Result.responseSuccess("角色修改成功");
        } else {
            result = Result.responseError("角色修改失败");
        }
        return result;
    }


    /**
     * @Description 查询角色
     * @Author Capejor
     * @Date 2019-05-20 14:02
     **/
    @RequestMapping("/selectRole")
    public Result selectRole() throws Exception {
        Result result;
        List<RoleVO> roleList = roleService.selectRole();
        if (roleList != null && roleList.size() > 0) {
            result = Result.responseSuccess("查询成功", roleList);
        } else {
            result = Result.responseError("无数据", roleList);
        }
        return result;
    }


    /**
     * @Description 删除角色
     * @Author Capejor
     * @Date 2019-05-20 14:36
     **/
    @RequestMapping("/deleteRole")
    public Result deleteRole(@RequestParam String id) throws Exception {
        Result result;
        // 判断该角色是否被用户绑定
        int userRoleCount = roleService.selectCountByRoleId(id);
        if (userRoleCount >0){
            return Result.responseError("该角色已绑定用户，不能删除！");
        }
        int deleteNum = roleService.deleteRole(id);
        if (deleteNum > 0) {
            result = Result.responseSuccess("角色删除成功");
        } else {
            result = Result.responseError("角色删除失败");
        }
        return result;
    }


}
