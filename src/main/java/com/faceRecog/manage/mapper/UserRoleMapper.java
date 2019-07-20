package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.UserRole;
import org.apache.ibatis.annotations.Param;

public interface UserRoleMapper {
    int deleteByPrimaryKey(String id);

    int insert(UserRole record);

    int insertSelective(UserRole record);

    UserRole selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UserRole record);

    int updateByPrimaryKey(UserRole record);

    int selectCountByRoleId(@Param("roleId") String roleId);

    // 编辑用户角色
    int updateByUserId(@Param("userId") String userId, @Param("roleId") String roleId);




}