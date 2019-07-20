package com.faceRecog.manage.service;

import com.faceRecog.manage.model.Group;
import com.faceRecog.manage.model.vo.GroupVO;

import java.util.List;

public interface GroupService {

    int insertGroup(Group group) throws Exception;

    int updateGroup(Group group) throws Exception;

    List<GroupVO> selectAllGroup()throws Exception;

    int deleteGroup(String id)throws Exception;

    //判断分组名称是否重复
    int selectByGroupName(String groupName) throws Exception;

    //判断分组名称是否重复 除去自己
    int selectByNameExceptOwn(String groupName) throws Exception;

    Group selectOneById(String id) throws Exception;




}
