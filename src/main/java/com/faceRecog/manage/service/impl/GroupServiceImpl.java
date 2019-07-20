package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.mapper.GroupMapper;
import com.faceRecog.manage.model.Group;
import com.faceRecog.manage.model.vo.GroupVO;
import com.faceRecog.manage.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupMapper groupMapper;



    @Override
    public int insertGroup(Group group) throws Exception {
        return groupMapper.insertSelective(group);
    }

    @Override
    public int updateGroup(Group group) throws Exception {
        return groupMapper.updateByPrimaryKeySelective(group);
    }

    @Override
    public List<GroupVO> selectAllGroup() throws Exception {
        return groupMapper.selectAllGroup();
    }


    @Override
    public int deleteGroup(String id) throws Exception {
        return groupMapper.deleteByPrimaryKey(id);
    }

    //判断分组名称是否重复
    @Override
    public int selectByGroupName(String groupName) throws Exception {
        return groupMapper.selectByGroupName(groupName);
    }

    //判断分组名称是否重复 除去自己
    @Override
    public int selectByNameExceptOwn(String groupName) throws Exception {
        return groupMapper.selectByNameExceptOwn(groupName);
    }

    @Override
    public Group selectOneById(String id) throws Exception {
        return groupMapper.selectByPrimaryKey(id);
    }


}
