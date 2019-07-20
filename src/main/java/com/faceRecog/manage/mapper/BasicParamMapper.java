package com.faceRecog.manage.mapper;

import java.util.Map;

import com.faceRecog.manage.model.BasicParam;

public interface BasicParamMapper {
    int deleteByPrimaryKey(String id);

    int insert(BasicParam record);

    int insertSelective(BasicParam record);

    BasicParam selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BasicParam record);

    int updateByPrimaryKey(BasicParam record);

    //根据 equipId 修改设备基本参数
    int updateBasicByEquipId(BasicParam basicParam);

    //根据 equipId 查询设备基本参数
    Map<String, Object> selectBasicByEquipId(String equipId);
}