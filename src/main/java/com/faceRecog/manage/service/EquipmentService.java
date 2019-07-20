package com.faceRecog.manage.service;

import com.faceRecog.manage.model.Equipment;
import com.faceRecog.manage.model.User;
import com.faceRecog.manage.util.Result;

import java.util.List;
import java.util.Map;

public interface EquipmentService {

    int insertEquipment(Equipment equipment) throws Exception;

    int updateEquipment(Equipment equipment) throws Exception;

    List<Map<String, Object>> selectEquipment()throws Exception;

    Result deleteEquipment(User user, String[] ids, String password) throws Exception;

    Equipment selectEquipmentBySn(String sn) throws Exception;

    Equipment selectEquipmentByPrimaryKey(String id) throws Exception;

    int selectEqNickNameExistByPrimaryKey(String id,String eqName) throws Exception;

    int selectEqNickNameExistBySn(String sn,String eqName) throws Exception;

    int updateEquipmentInfoBySn(Equipment equipment)throws Exception;

    int selectCountByEqName(String eqName)throws Exception;

    int judgeEquipmentRegister(String sn)throws Exception;
    
    List<Map<String, Object>>selectAllOnLineEquipmentInfo()throws Exception;

    //根据sn查询设备id
    String selectEquipIdBySn(String sn)throws Exception;



}
