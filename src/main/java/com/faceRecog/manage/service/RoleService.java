package com.faceRecog.manage.service;

import com.faceRecog.manage.model.Role;
import com.faceRecog.manage.model.vo.RoleVO;

import java.util.List;

public interface RoleService {

    int selectCountByRoleName(String roleName) throws Exception;

    int selectCountByRoleNameExceptOwn(String roleName,String id)throws Exception;

    int insertRole(Role role) throws Exception;

    int updateRole(Role role) throws Exception;

    List<RoleVO> selectRole() throws Exception;

    int selectCountByRoleId(String roleId) throws Exception;

    int deleteRole(String id) throws Exception;
}
