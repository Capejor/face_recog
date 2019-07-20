package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.ReadMode;

public interface ReadModeMapper {
    int deleteByPrimaryKey(String id);

    int insert(ReadMode record);

    int insertSelective(ReadMode record);

    ReadMode selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ReadMode record);

    int updateByPrimaryKey(ReadMode record);
    
    
    
    ReadMode selectReadModeByEqId(String eqId);
}