package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.User;
import com.faceRecog.manage.model.vo.UserVO;
import org.apache.ibatis.annotations.Param;


import java.util.List;
import java.util.Map;

public interface UserMapper {
    int deleteByPrimaryKey(String id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //判断用户名是否注册
    int selectCountByUserName(String userName);

    //判断手机号是否注册
    int selectCountByPhone(String phone) throws Exception;

    //判断用户名是否注册  除去自己
    int selectCountByUserNameExceptOwn(@Param("userName") String userName, @Param("id") String id);

    //判断手机号是否注册  除去自己
    int selectCountByPhoneExceptOwn(@Param("phone") String phone, @Param("id") String id);

    //查询所有用户
    List<UserVO> selectUser();

    //根据用户名查询用户信息
    User selectByUserName(String userName);

    //修改密码
    int updatePassword(@Param("id") String id, @Param("pwd") String pwd, @Param("salt") String salt);
    
    String selectAllTables(String name);
    
   

    //判断用户手机号是否错误
    int selectCountByUserNameAndPhone(@Param("userName") String userName, @Param("phone") String phone);

    //找回密码
    //int findBackPassword(@Param("userName") String userName, @Param("phone") String phone, @Param("pwd") String pwd, @Param("salt") String salt);
    int findBackPassword(@Param("code") String code, @Param("phone") String phone, @Param("pwd") String pwd, @Param("salt") String salt);

    //保存验证码
    int updateCodeByPhone(@Param("phone") String phone,@Param("code") String code)throws Exception;

    //判断验证码是否错误
    int selectCountByPhoneAndCode(@Param("phone") String phone,@Param("code") String code);


}