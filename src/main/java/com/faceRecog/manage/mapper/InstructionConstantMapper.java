package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.InstructionConstant;

public interface InstructionConstantMapper {
    int deleteByPrimaryKey(String id);

    int insert(InstructionConstant record);

    int insertSelective(InstructionConstant record);

    InstructionConstant selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(InstructionConstant record);

    int updateByPrimaryKey(InstructionConstant record);
    
    
    
}