package com.faceRecog.manage.service;

import java.util.Map;

import com.faceRecog.manage.model.BasicParam;

public interface BasicParamService {

    int insertBasicParam(BasicParam basicParam) throws Exception;

    int updateBasicParam(BasicParam basicParam) throws Exception;

    Map<String, Object> selectBasicByEquipId(String equipId) throws Exception;
}
