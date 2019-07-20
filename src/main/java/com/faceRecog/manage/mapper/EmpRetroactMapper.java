package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.EmpRetroact;

public interface EmpRetroactMapper {
    int deleteByPrimaryKey(String id);

    int insert(EmpRetroact record);

    int insertSelective(EmpRetroact record);

    EmpRetroact selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(EmpRetroact record);

    int updateByPrimaryKey(EmpRetroact record);
}