package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.VisitEquip;

public interface VisitEquipMapper {
    int deleteByPrimaryKey(String id);

    int insert(VisitEquip record);

    int insertSelective(VisitEquip record);

    VisitEquip selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(VisitEquip record);

    int updateByPrimaryKey(VisitEquip record);

    int deleteVisitEquipByVisitId(String visitId);
}