package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.RoleAuth;

public interface RoleAuthMapper {
    int deleteByPrimaryKey(String id);

    int insert(RoleAuth record);

    int insertSelective(RoleAuth record);

    RoleAuth selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(RoleAuth record);

    int updateByPrimaryKey(RoleAuth record);
}