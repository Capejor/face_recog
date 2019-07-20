package com.faceRecog.manage.controller;


import com.faceRecog.manage.model.User;
import com.faceRecog.manage.model.vo.UserVO;
import com.faceRecog.manage.service.UserService;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.SmsUtil;
import com.faceRecog.manage.util.StrKit;
import com.faceRecog.manage.util.UUIDGenerator;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Capejor
 * @className: UserController
 * @Description: TODO
 * @date 2019-05-20 10:33
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * @Description 用户注册
     * @Author Capejor
     * @Date 2019-05-20 11:00
     **/
    @RequestMapping("/insertUser")
    public Result insertUser(User user, @RequestParam String roleId) throws Exception {
        Result result;
        if (StrKit.isBlank(user.getUserName())) {
            return Result.responseError("用户名不能为空");
        }
        if (StrKit.isBlank(user.getPhone())) {
            return Result.responseError("用户手机号不能为空");
        }
        if (StrKit.isBlank(user.getPwd())) {
            return Result.responseError("密码不能为空");
        }
        if (StrKit.isBlank(roleId)) {
            return Result.responseError("角色id不能为空");
        }
        int userCount = userService.selectCountByUserName(user.getUserName());
        if (userCount > 0) {
            return Result.responseError("用户名已注册，请重新输入用户名!");
        }
        int phoneCount = userService.selectCountByPhone(user.getPhone());
        if (phoneCount > 0) {
            return Result.responseError("手机号已注册，请重新输入手机号!");
        }
        user.setId(UUIDGenerator.getUUID());
        user.setCreateTime(new Date());
        user.setCreateUserId("1");
        user.setStatus("1");
        int insertNum = userService.insertUser(user, roleId);
        if (insertNum > 0) {
            result = Result.responseSuccess("用户添加成功");
        } else {
            result = Result.responseError("用户添加失败");
        }
        return result;
    }


    /**
     * @Description 修改用户
     * @Author Capejor
     * @Date 2019-05-20 11:16
     **/
    @RequestMapping("/updateUser")
    public Result updateUser(User user, @RequestParam String roleId) throws Exception {
        Result result = null;
        int updateNum;
        if (StrKit.isBlank(user.getUserName())) {
            return Result.responseError("用户名不能为空");
        }
        if (StrKit.isBlank(user.getRemark())) {
            return Result.responseError("备注不能为空");
        }
        if (StrKit.isBlank(roleId)) {
            return Result.responseError("角色id不能为空");
        }
        user.setUpdateTime(new Date());
        //判断用户名是否注册 除去自己
        int userCount = userService.selectCountByUserNameExceptOwn(user.getUserName(), user.getId());
        //判断手机号是否注册 除去自己
        int phoneCount = userService.selectCountByPhoneExceptOwn(user.getPhone(), user.getId());
        if (userCount > 0 && phoneCount == 0) {
            return Result.responseError("用户名已注册，请重新输入用户名!");
        }
        if (userCount == 0 && phoneCount > 0) {
            return Result.responseError("手机号已注册，请重新输入手机号!");
        }
        if (userCount == 0 && phoneCount == 0) {
            // 判断该用户是否已经拥有该角色 用户绑定角色 删除角色
            //int userRolCount = userService.selectCountRoleId(user.getId());
            //updateNum = userService.updateUserAndInsert(user, roleId);
            updateNum = userService.updateUser(user, roleId);
            if (updateNum > 0) {
                result = Result.responseSuccess("用户修改成功");
            } else {
                result = Result.responseError("用户修改失败");
            }
        }
        if (userCount > 0 && phoneCount > 0) {
            return Result.responseError("用户名和手机号都已注册，很尴尬啊!");
        }
        return result;
    }


    /**
     * @Description 查询用户
     * @Author Capejor
     * @Date 2019-05-20 11:38
     **/
    @RequestMapping("/selectUser")
    public Result selectUser() throws Exception {
        Result result;
        List<UserVO> userList = userService.selectUser();
        if (userList != null && userList.size() > 0) {
            result = Result.responseSuccess("查询成功", userList);
        } else {
            result = Result.responseSuccess("无数据", userList);
        }
        return result;
    }


    /**
     * @Description 删除用户
     * @Author Capejor
     * @Date 2019-05-20 11:59
     **/
    @RequestMapping("/deleteUser")
    public Result deleteUser(@RequestParam String id) throws Exception {
        if ("1".equals(id)) {
            return Result.responseError("超级管理员admin，不能被删除！！！");
        }
        Result result;
        int deleteNum = userService.deleteUser(id);
        if (deleteNum > 0) {
            result = Result.responseSuccess("用户删除成功");
        } else {
            result = Result.responseError("用户删除失败");
        }
        return result;
    }


    /**
     * @Description 修改密码
     * @Author Capejor
     * @Date 2019-06-18 18:52
     **/
    @RequestMapping("/updatePassword")
    public Result updatePassword(String newPwd, String oldPwd) throws Exception {
        if (StrKit.isBlank(newPwd)) {
            return Result.responseError("新密码不能为空！");
        }
        if (StrKit.isBlank(oldPwd)) {
            return Result.responseError("旧密码不能为空！");
        }
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        return userService.updatePassword(user, newPwd, oldPwd);
    }

    
    
    /*@RequestMapping("/selectAllTable")
    public Result selectAllTable() throws Exception {
    	Result result=null;
    	List<Map<String, Object>> list=userService.selectAllTable();
    	result=Result.responseSuccess("查询成功",list);
        return result;
    }*/

    /**
     * @Description 找回密码
     * @Author Capejor
     * @Date 2019-06-19 16:52
     **/
  /*  @RequestMapping("/findBackPassword")
    public Result findBackPassword(String userName, String phone, String pwd) throws Exception {
        if (StrKit.isBlank(userName)) {
            return Result.responseError("用户名不能为空！");
        } else if (StrKit.isBlank(phone)) {
            return Result.responseError("手机号不能为空！");
        } else if (StrKit.isBlank(pwd)) {
            return Result.responseError("新密码不能为空！");
        }
        return userService.findBackPassword(userName, phone, pwd);
    }*/

    /**
     * @Description 找回密码
     * @Author Capejor
     * @Date 2019-06-19 16:52
     **/
    @RequestMapping("/findBackPassword")
    public Result findBackPassword(String code, String phone, String pwd) throws Exception {
        if (StrKit.isBlank(code)) {
            return Result.responseError("验证码不能为空！");
        } else if (StrKit.isBlank(phone)) {
            return Result.responseError("手机号不能为空！");
        } else if (StrKit.isBlank(pwd)) {
            return Result.responseError("新密码不能为空！");
        }
        return userService.findBackPassword(code, phone, pwd);
    }

    /**
     * @Description 获取短信验证码
     * @Author Capejor
     * @Date 2019-06-19 19:01
     **/
    @RequestMapping("/getSmsCode")
    public Result getSmsCode(String phone) throws Exception {
        int affectNum;
        if (StrKit.isBlank(phone)) {
            return Result.responseError("手机号不能为空!");
        }
        Integer sendCode = SmsUtil.getSmsCode(phone.trim());
        if (sendCode == null || sendCode == 0) {
            return Result.responseError("验证码获取失败！");
        }
        affectNum = userService.updateCodeByPhone(phone, sendCode.toString());
        if (affectNum < 0) {
            return Result.responseError("验证码获取失败！");
        } else {
            return Result.responseSuccess("获取成功！");
        }
    }

}
