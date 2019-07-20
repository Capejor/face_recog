package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.mapper.GroupInfoMapper;
import com.faceRecog.manage.model.GroupInfo;
import com.faceRecog.manage.model.vo.GroupInfoVO;
import com.faceRecog.manage.service.GroupInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class GroupInfoServiceImpl implements GroupInfoService {

    @Autowired
    private GroupInfoMapper groupInfoMapper;

    @Override
    public int insertGroupInfo(GroupInfo groupInfo) throws Exception {
        return groupInfoMapper.insertSelective(groupInfo);
    }

    @Override
    public int updateGroupInfo(GroupInfo groupInfo) throws Exception {
        return groupInfoMapper.updateByPrimaryKeySelective(groupInfo);
    }

    @Override
    public List<GroupInfoVO> selectByGroupId(String groupId) throws Exception {
        return groupInfoMapper.selectByGroupId(groupId);
    }

    //批量删除
    @Override
    public int deleteGroupInfo(String []ids) throws Exception {
        return groupInfoMapper.deleteByList(ids);
    }

    //分组详情名称不能重复
    @Override
    public int selectByName(String name,String groupId) throws Exception {
        Map<String,Object> map  = new HashMap<>();
        map.put("name",name);
        map.put("groupId",groupId);
        return groupInfoMapper.selectByName(map);
    }

    //分组详情排序不能重复
    @Override
    public int selectBySort(int sort,String groupId) throws Exception {
        Map<String,Object> map  = new HashMap<>();
        map.put("sort",sort);
        map.put("groupId",groupId);
        return groupInfoMapper.selectBySort(map);
    }

    @Override
    public int selectByNameExceptOwn(String name,String groupId,String id) throws Exception {
        Map<String,Object> map  = new HashMap<>();
        map.put("name",name);
        map.put("groupId",groupId);
        map.put("id",id);
        return groupInfoMapper.selectByNameExceptOwn(map);
    }

    @Override
    public int selectBySortExceptOwn(int sort,String groupId,String id) throws Exception {
        Map<String,Object> map  = new HashMap<>();
        map.put("sort",sort);
        map.put("groupId",groupId);
        map.put("id",id);
        return groupInfoMapper.selectBySortExceptOwn(map);
    }
}
