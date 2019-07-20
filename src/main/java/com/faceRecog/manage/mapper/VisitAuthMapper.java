package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.VisitAuth;

public interface VisitAuthMapper {
    int deleteByPrimaryKey(String id);

    int insert(VisitAuth record);

    int insertSelective(VisitAuth record);

    VisitAuth selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(VisitAuth record);

    int updateByPrimaryKey(VisitAuth record);

    int deleteVisitAuthByVisitId(String visitId);


    int selectByVisitId(String visitId);


}