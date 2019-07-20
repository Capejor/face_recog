package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.handler.SpringWebSocketHandler;
import com.faceRecog.manage.mapper.InstructionRecMapper;
import com.faceRecog.manage.mapper.UpgradeMapper;
import com.faceRecog.manage.model.InstructionRec;
import com.faceRecog.manage.model.Upgrade;
import com.faceRecog.manage.service.UpgradeService;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.constantUtils.CommonConstant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.socket.TextMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Capejor
 * @className: UpgradeServiceImpl
 * @Description: TODO
 * @date 2019-06-04 15:57
 */
@Service
public class UpgradeServiceImpl implements UpgradeService {

    @Autowired
    private UpgradeMapper upgradeMapper;

    @Autowired
    private SpringWebSocketHandler websocket;

    @Autowired
    private InstructionRecMapper instructionRecMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int uploadFile(String equipId, JSONObject jsonObject) throws Exception {
        JSONArray jsonArray = (JSONArray) jsonObject.get("fileList");
        String realName = jsonArray.getJSONObject(0).get("realName").toString();
        String suffix = jsonArray.getJSONObject(0).get("suffix").toString();
        String fileSize = jsonArray.getJSONObject(0).get("fileSize").toString();
        String saveUrl = jsonArray.getJSONObject(0).get("srcFilePath").toString();
        String fileName = jsonArray.getJSONObject(0).get("fileName").toString();
        Upgrade upgrade = new Upgrade();
        upgrade.setOriName(realName);
        upgrade.setSaveName(fileName);
        upgrade.setSuffix(suffix);
        upgrade.setSize(fileSize);
        upgrade.setSaveUrl(saveUrl);
        upgrade.setId(UUIDGenerator.getUUID());
        upgrade.setEquipId(equipId);
        upgrade.setCreateTime(new Date());
        upgrade.setCreateUserId("1");
        upgrade.setStatus("1");

       /* //根据sn查询设备id
        String equipId = equipmentMapper.selectEquipIdBySn(sn);*/
        //判断设备是否存在apk
        int equipCount = upgradeMapper.selectCountByEquip(equipId);
        int affectNum;
        if (equipCount == 0) {
            affectNum = upgradeMapper.insertSelective(upgrade);
            if (affectNum == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
        } else {
            affectNum = upgradeMapper.deleteByEquipId(equipId);
            if (affectNum < 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
            affectNum = upgradeMapper.insertSelective(upgrade);
            if (affectNum == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return -1;
            }
        }
        return affectNum;
    }

    @Override
    public int updateUpgrade(Upgrade upgrade) throws Exception {
        return upgradeMapper.updateByPrimaryKeySelective(upgrade);
    }

    @Override
    public List<Upgrade> selectUpgrade(String equipId) throws Exception {
        return upgradeMapper.selectUpgrade(equipId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateEquipUpgrade(String upgradeId, String percent, String downKb, String downloadStatus) throws Exception {
        int affectNum;
        Map<String, Object> map = new HashMap<>();
        map.put("upgradeId", upgradeId);
        map.put("percent", percent);
        map.put("downKb", downKb);
        map.put("downloadStatus", downloadStatus);
        affectNum = upgradeMapper.updateEquipUpgrade(map);
        if (affectNum < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        affectNum = upgradeMapper.deleteByPrimaryKey(upgradeId);
        if (affectNum < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        return affectNum;
    }

    /**
     * @Description 设备升级
     * @Author Capejor
     * @Date 2019-06-22 10:58
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int upgradeEquipment(String sn) throws Exception {
        int affectNum;
        // 新增指令发送记录 发送socket消息给设备端
        InstructionRec instructionRec = new InstructionRec();
        instructionRec.setId(UUIDGenerator.getUUID());
        instructionRec.setCreateTime(new Date());
        instructionRec.setStatus("-1");
        instructionRec.setSn(sn);
        instructionRec.setInstruction(CommonConstant.CHECK_UPGRADE.getValue());
        affectNum = instructionRecMapper.insertSelective(instructionRec);
        if (affectNum < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        }
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
        jsonObj.put("code", Integer.valueOf(CommonConstant.CHECK_UPGRADE.getValue()));
        jsonObj.put("id", instructionRec.getId());
        jsonObj.put("sendTime", new Date().getTime());
        //发送指令
        websocket.sendMessageToUser(sn, new TextMessage(jsonObj.toString()));
        if (affectNum < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return 1;
    }
}
