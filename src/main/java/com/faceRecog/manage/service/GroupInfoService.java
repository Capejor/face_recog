package com.faceRecog.manage.service;

import com.faceRecog.manage.model.GroupInfo;
import com.faceRecog.manage.model.vo.GroupInfoVO;

import java.util.List;

public interface GroupInfoService {

    int insertGroupInfo(GroupInfo groupInfo) throws Exception;

    int updateGroupInfo(GroupInfo groupInfo) throws Exception;

    List<GroupInfoVO> selectByGroupId(String groupId) throws Exception;

    int deleteGroupInfo(String []ids) throws Exception;

    //分组详情名称不能重复
    int selectByName(String name,String groupId) throws Exception;

    //分组详情排序不能重复
    int selectBySort(int sort,String groupId) throws Exception;

    //分组详情名称不能重复 除去自己
    int selectByNameExceptOwn(String name,String groupId,String id) throws Exception;

    //分组详情排序不能重复 除去自己
    int selectBySortExceptOwn(int sort,String groupId,String id) throws Exception;

}
