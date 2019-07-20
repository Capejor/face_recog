package com.faceRecog.manage.mapper;


import com.faceRecog.manage.model.GroupInfo;
import com.faceRecog.manage.model.vo.GroupInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GroupInfoMapper {
    int deleteByPrimaryKey(String id);

    int insert(GroupInfo record);

    int insertSelective(GroupInfo record);

    GroupInfo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(GroupInfo record);

    int updateByPrimaryKey(GroupInfo record);

    //根据分组id查询所有分组详情
    List<GroupInfoVO> selectByGroupId(String groupId);

    //分组详情名称不能重复
    int selectByName(Map<String,Object> map);

    //分组详情排序不能重复
    int selectBySort(Map<String,Object> map );

    //分组详情名称不能重复 除去自己
    int selectByNameExceptOwn(Map<String,Object> map);

    //分组详情排序不能重复 除去自己
    int selectBySortExceptOwn(Map<String,Object> map);


    //批量删除
    int deleteByList(@Param("ids") String []ids);
}