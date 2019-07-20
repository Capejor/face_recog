package com.faceRecog.manage.service;

import com.faceRecog.manage.model.User;
import com.faceRecog.manage.model.vo.UserVO;
import com.faceRecog.manage.util.Result;


import java.util.List;
import java.util.Map;

public interface UserService {

    int selectCountByUserName(String userName) throws Exception;

    int selectCountByPhone(String phone) throws Exception;

    int selectCountByUserNameExceptOwn(String userName,String id)throws Exception;

    int selectCountByPhoneExceptOwn(String phone,String id)throws Exception;

    int insertUser(User user, String roleId) throws Exception;

    int updateUser(User user, String roleId) throws Exception;

    //int updateUserAndInsert(User user, String roleId) throws Exception;

    List<UserVO> selectUser() throws Exception;

    int deleteUser(String id) throws Exception;

    //int selectCountByRoleId(String userId) throws Exception;

    User selectByUserName(String userName)throws Exception;

    Result updatePassword(User user, String newPwd, String oldPwd)throws Exception;

    Result findBackPassword(String code, String phone,String pwd)throws Exception;

    int updateCodeByPhone(String phone,String code)throws Exception;


}
