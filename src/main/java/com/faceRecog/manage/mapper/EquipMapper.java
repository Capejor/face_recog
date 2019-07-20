package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.Equip;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EquipMapper {
    int deleteByPrimaryKey(String id);

    int insert(Equip record);

    int insertSelective(Equip record);

    Equip selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Equip record);

    int updateByPrimaryKey(Equip record);

    //绑定设备
    int bindEquipment(@Param("sn") String sn, @Param("bindSn") String bindSn);

    //修改房间号
    int updateRoomNum(@Param("sn") String sn, @Param("roomNum") String roomNum);


    List<Equip> selectEquipment(String roomNum) ;
}