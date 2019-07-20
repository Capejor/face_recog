package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.Group;
import com.faceRecog.manage.model.vo.GroupVO;


import java.util.List;


public interface GroupMapper {
    int deleteByPrimaryKey(String id);

    int insert(Group record);

    int insertSelective(Group record);

    Group selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Group record);

    int updateByPrimaryKey(Group record);

    //查询所有分组
    List<GroupVO> selectAllGroup();

    //判断分组名称是否重复
    int selectByGroupName(String groupName);

    //判断分组名称是否重复 除去自己
    int selectByNameExceptOwn(String groupName);
}