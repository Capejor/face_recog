package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.EmpCard;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.IdsMapper;

import java.util.List;

public interface EmpCardMapper {
    int deleteByPrimaryKey(String id);

    int insert(EmpCard record);

    int insertSelective(EmpCard record);

    EmpCard selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(EmpCard record);

    int updateByPrimaryKey(EmpCard record);

    List<String> selectCardIdByEmpId(@Param("empIds") String empIds[]);

    int deleteByCardId(String cardId);
}