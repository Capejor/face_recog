package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.Role;
import com.faceRecog.manage.model.vo.RoleVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper {
    int deleteByPrimaryKey(String id);

    int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);

    //
    List<RoleVO> selectRole();

    int selectCountByRoleName(String roleName);

    int selectCountByRoleNameExceptOwn(@Param("roleName") String roleName,@Param("id") String id);
}