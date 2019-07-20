package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.mapper.UserMapper;
import com.faceRecog.manage.mapper.UserRoleMapper;
import com.faceRecog.manage.model.User;
import com.faceRecog.manage.model.UserRole;
import com.faceRecog.manage.model.vo.UserVO;
import com.faceRecog.manage.service.UserService;
import com.faceRecog.manage.util.CommUtil;
import com.faceRecog.manage.util.PasswordHelper;
import com.faceRecog.manage.util.Result;
import com.faceRecog.manage.util.UUIDGenerator;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Map;

/**
 * @author Capejor
 * @className: UserServiceImpl
 * @Description: TODO
 * @date 2019-05-20 10:34
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public int selectCountByUserName(String userName) {
        return userMapper.selectCountByUserName(userName);
    }

    @Override
    public int selectCountByPhone(String phone) throws Exception {
        return userMapper.selectCountByPhone(phone);
    }

    @Override
    public int selectCountByUserNameExceptOwn(String userName, String id) throws Exception {
        return userMapper.selectCountByUserNameExceptOwn(userName, id);
    }

    @Override
    public int selectCountByPhoneExceptOwn(String phone, String id) throws Exception {
        return userMapper.selectCountByPhoneExceptOwn(phone, id);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(User user, String roleId) throws Exception {
        // 获取盐值
        String salt = CommUtil.randomString(6);
        // 进行MD5加密
        PasswordHelper passwordHelper = new PasswordHelper();
        String password_md5_sale = passwordHelper.encryptPassword(user.getPwd(), salt);
        user.setPwd(password_md5_sale);
        user.setSalt(salt);
        int userNum = userMapper.insertSelective(user);
        int userRoleNum;
        if (userNum > 0) {
            UserRole userRole = new UserRole();
            userRole.setId(UUIDGenerator.getUUID());
            userRole.setUserId(user.getId());
            userRole.setRoleId(roleId);
            userRoleNum = userRoleMapper.insertSelective(userRole);
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        return userRoleNum;
    }

    @Override
    public int updateUser(User user, String roleId) throws Exception {
        int userNum = userMapper.updateByPrimaryKeySelective(user);
        int userRoleNum;
        if (userNum > 0) {
            userRoleNum = userRoleMapper.updateByUserId(user.getId(), roleId);
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        return userRoleNum;
    }

   /* @Override
    public int updateUserAndInsert(User user, String roleId) throws Exception {
        int userNum = userMapper.updateByPrimaryKeySelective(user);
        int userRoleNum;
        if (userNum > 0) {
            UserRole userRole = new UserRole();
            userRole.setId(UUIDGenerator.getUUID());
            userRole.setUserId(user.getId());
            userRole.setRoleId(roleId);
            userRoleNum = userRoleMapper.insertSelective(userRole);
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        return userRoleNum;
    }*/

    @Override
    public List<UserVO> selectUser() throws Exception {
        return userMapper.selectUser();
    }

    @Override
    public int deleteUser(String id) throws Exception {
        return userMapper.deleteByPrimaryKey(id);
    }


    /*@Override
    public int selectCountByRoleId(String userId) throws Exception {
        return userRoleMapper.selectCountByRoleId(userId);
    }*/


    @Override
    public User selectByUserName(String userName) {
        return userMapper.selectByUserName(userName);
    }

    @Override
    public Result updatePassword(User user, String newPwd, String oldPwd) {
        int affectNum;
        Result result;
        // 原始密码加密
        PasswordHelper passwordHelper = new PasswordHelper();
        String oldPassword_md5 = passwordHelper.encryptPassword(oldPwd, user.getSalt());
        //判断原密码是否输入正确
        if (oldPassword_md5.equals(user.getPwd())) {
            // 获取盐值
            String salt = CommUtil.randomString(6);
            // 新密码加密
            String newPassword_md5 = passwordHelper.encryptPassword(newPwd, salt);
            affectNum = userMapper.updatePassword(user.getId(), newPassword_md5, salt);
            if (affectNum > 0) {
                result = Result.responseSuccess("修改成功,请重新登录！");
            } else {
                result = Result.responseError("修改失败！");
            }
        } else {
            result = Result.responseError("原密码输入错误！");
        }
        return result;
    }

    @Override
    public Result findBackPassword(String code, String phone, String pwd) throws Exception {
        Result result;
        int resultNum;
        //判断用户是否存在
        // resultNum = userMapper.selectCountByUserName(userName);
        //判断手机号是否注册
        resultNum = userMapper.selectCountByPhone(phone);
        if (resultNum == 0) {
            return Result.responseError("该手机号未注册！");
        }
        //resultNum = userMapper.selectCountByUserNameAndPhone(userName, phone);
        resultNum = userMapper.selectCountByPhoneAndCode(phone, code);
        if (resultNum == 0) {
            return Result.responseError("验证码错误！");
        }
        // 获取盐值
        String salt = CommUtil.randomString(6);
        // 新密码加密
        PasswordHelper passwordHelper = new PasswordHelper();
        String newPassword_md5 = passwordHelper.encryptPassword(pwd, salt);
        //resultNum = userMapper.findBackPassword(userName, phone, newPassword_md5, salt);
        resultNum = userMapper.findBackPassword(code, phone, newPassword_md5, salt);
        if (resultNum > 0) {
            result = Result.responseSuccess("密码找回成功,请记住！");
        } else {
            result = Result.responseError("密码找回失败！");
        }
        return result;

    }

    @Override
    public int updateCodeByPhone(String phone, String code) throws Exception {
        return userMapper.updateCodeByPhone(phone, code);
    }
}
