package com.faceRecog.manage.service;

import com.faceRecog.manage.model.Upgrade;
import net.sf.json.JSONObject;

import java.util.List;

public interface UpgradeService {

    //上传文件
    int uploadFile(String sn,JSONObject jsonObject) throws Exception;

    //修改版本号
    int updateUpgrade(Upgrade upgrade)throws Exception;

    //查询文件
    List<Upgrade> selectUpgrade(String equipId) throws Exception;

    //下载文件
    int updateEquipUpgrade(String upgradeId,String percent,String downKb,String downloadStatus)throws Exception;

    //升级设备
    int upgradeEquipment(String sn)throws Exception;


}
