package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.mapper.EquipMapper;
import com.faceRecog.manage.model.Equip;
import com.faceRecog.manage.service.EquipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Capejor
 * @className: EquipServiceImpl
 * @Description: TODO
 * @date 2019-07-19 16:10
 */
@Service
public class EquipServiceImpl implements EquipService {

    @Autowired
    private EquipMapper equipMapper;


    @Override
    public int insertEquip(Equip equip) throws Exception {
        return equipMapper.insertSelective(equip);
    }

    @Override
    public int bindEquipment(String sn, String bindSn) throws Exception {
        return equipMapper.bindEquipment(sn,bindSn);
    }

    @Override
    public int updateRoomNum(String sn, String roomNum) throws Exception {
        return equipMapper.updateRoomNum(sn,roomNum);
    }

    @Override
    public List<Equip> selectEquipment(String roomNum) throws Exception {
        return equipMapper.selectEquipment(roomNum);
    }


}
