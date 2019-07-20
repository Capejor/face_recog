package com.faceRecog.manage.service;

import com.faceRecog.manage.model.Equip;

import java.util.List;

public interface EquipService {

    int insertEquip(Equip equip) throws Exception;

    int bindEquipment(String sn,String bindSn)throws Exception;

    int updateRoomNum(String sn,String roomNum)throws Exception;

    List<Equip> selectEquipment(String roomNum)throws Exception;
}
