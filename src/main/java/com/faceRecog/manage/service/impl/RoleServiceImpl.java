package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.mapper.RoleMapper;
import com.faceRecog.manage.mapper.UserRoleMapper;
import com.faceRecog.manage.model.Role;
import com.faceRecog.manage.model.UserRole;
import com.faceRecog.manage.model.vo.RoleVO;
import com.faceRecog.manage.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Capejor
 * @className: RoleServiceImpl
 * @Description: TODO
 * @date 2019-05-20 13:46
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public int selectCountByRoleName(String roleName) throws Exception {
        return roleMapper.selectCountByRoleName(roleName);
    }

    @Override
    public int selectCountByRoleNameExceptOwn(String roleName, String id) throws Exception {
        return roleMapper.selectCountByRoleNameExceptOwn(roleName,id);
    }

    @Override
    public int insertRole(Role role) throws Exception {
        return roleMapper.insertSelective(role);
    }

    @Override
    public int updateRole(Role role) throws Exception {
        return roleMapper.updateByPrimaryKeySelective(role);
    }

    @Override
    public List<RoleVO> selectRole() throws Exception {
        return roleMapper.selectRole();
    }

    @Override
    public int selectCountByRoleId(String roleId) throws Exception {
        return userRoleMapper.selectCountByRoleId(roleId);
    }

    @Override
    public int deleteRole(String id) throws Exception {
        return roleMapper.deleteByPrimaryKey(id);
    }
}
