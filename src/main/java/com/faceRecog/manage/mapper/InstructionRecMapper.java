package com.faceRecog.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.faceRecog.manage.model.InstructionRec;

public interface InstructionRecMapper {
    int deleteByPrimaryKey(String id);

    int insert(InstructionRec record);

    int insertSelective(InstructionRec record);

    InstructionRec selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(InstructionRec record);

    int updateByPrimaryKey(InstructionRec record);
    
    
    int deleteInstructionRec();
    
    
    int  updateInstructionRecStatus(String id);
    
    
    int insertBatch(@Param("ids")List<String> ids);
}