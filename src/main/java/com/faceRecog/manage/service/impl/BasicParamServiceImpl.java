package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.mapper.BasicParamMapper;
import com.faceRecog.manage.model.BasicParam;
import com.faceRecog.manage.service.BasicParamService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BasicParamServiceImpl implements BasicParamService {

    @Autowired
    private BasicParamMapper basicParamMapper;


    @Override
    public int insertBasicParam(BasicParam basicParam) throws Exception {
        return basicParamMapper.insertSelective(basicParam);
    }

    @Override
    public int updateBasicParam(BasicParam basicParam) throws Exception {
        return basicParamMapper.updateByPrimaryKeySelective(basicParam);
    }

    @Override
    public Map<String, Object> selectBasicByEquipId(String equipId) throws Exception {
        return basicParamMapper.selectBasicByEquipId(equipId);
    }
}
