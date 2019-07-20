package com.faceRecog.manage.mapper;

import com.faceRecog.manage.model.Upgrade;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UpgradeMapper {
    int deleteByPrimaryKey(String id);

    int insert(Upgrade record);

    int insertSelective(Upgrade record);

    Upgrade selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Upgrade record);

    int updateByPrimaryKey(Upgrade record);


    List<Upgrade> selectUpgrade(@Param("equipId") String equipId);

    //升级文件
    int updateEquipUpgrade(Map<String,Object> map) ;

    //判断设备是否存在apk
    int selectCountByEquip(String equipId);

    //根据设备id删除apk
    int deleteByEquipId(String equipId);
}