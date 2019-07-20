package com.faceRecog.manage.service.impl;

import com.faceRecog.manage.handler.SpringWebSocketHandler;
import com.faceRecog.manage.mapper.AccessAuthMapper;
import com.faceRecog.manage.mapper.EmpEquipmentMapper;
import com.faceRecog.manage.mapper.InstructionRecMapper;
import com.faceRecog.manage.model.AccessAuth;
import com.faceRecog.manage.model.InstructionRec;
import com.faceRecog.manage.model.serverVO.EmpAuthServer;
import com.faceRecog.manage.model.vo.AccessTimeVO;
import com.faceRecog.manage.model.vo.AuthVO;
import com.faceRecog.manage.service.AccessAuthService;
import com.faceRecog.manage.util.UUIDGenerator;
import com.faceRecog.manage.util.constantUtils.CommonConstant;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.socket.TextMessage;

import java.util.*;


@Service
public class AccessAuthServiceImpl implements AccessAuthService {

    @Autowired
    private AccessAuthMapper accessAuthMapper;

    @Autowired
    private EmpEquipmentMapper empEquipmentMapper;

    @Autowired
    private InstructionRecMapper instructionRecMapper;


    @Autowired
    private SpringWebSocketHandler websocket;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertAccAuth(AccessAuth accessAuth) throws Exception {
        return accessAuthMapper.insertSelective(accessAuth);
    }

    @Override
    public int updateAccAuth(AccessAuth accessAuth) throws Exception {
        String ids[] = {accessAuth.getId()};
        List<Map<String, Object>> empEqupmentLst = empEquipmentMapper.selectBatchEmpEquipmentByAuthId(ids);
        if (empEqupmentLst != null && empEqupmentLst.size() > 0) {
            for (Map<String, Object> empEquipmentMap : empEqupmentLst) {
                // 新增指令发送记录 发送socket消息给设备端
                InstructionRec instructionRec = new InstructionRec();
                instructionRec.setId(UUIDGenerator.getUUID());
                instructionRec.setCreateTime(new Date());
                instructionRec.setStatus("-1");
                instructionRec.setSn((String) empEquipmentMap.get("sn"));
                instructionRec.setInstruction(CommonConstant.UPDATE_PASS_AUTH.getValue());
                int affectNum = instructionRecMapper.insertSelective(instructionRec);
                if (affectNum < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return -1;
                }
                JSONObject jsonObjSend = new JSONObject();
                jsonObjSend.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
                jsonObjSend.put("code", Integer.valueOf(CommonConstant.UPDATE_PASS_AUTH.getValue()));
                jsonObjSend.put("id", instructionRec.getId());
                jsonObjSend.put("sendTime", new Date().getTime());
                //发送指令
                websocket.sendMessageToUser((String) empEquipmentMap.get("sn"), new TextMessage(jsonObjSend.toString()));
            }
        }
        return accessAuthMapper.updateByPrimaryKeySelective(accessAuth);
    }

    @Override
    public List<AccessAuth> selectAllAccessAuth() throws Exception {
        return accessAuthMapper.selectAllAccessAuth();
    }

    @Override
    public int deleteAccAuthById(String[] ids) throws Exception {
        List<Map<String, Object>> empEqupmentLst = empEquipmentMapper.selectBatchEmpEquipmentByAuthId(ids);
        if (empEqupmentLst != null && empEqupmentLst.size() > 0) {
            for (Map<String, Object> empEquipmentMap : empEqupmentLst) {
                // 新增指令发送记录 发送socket消息给设备端
                InstructionRec instructionRec = new InstructionRec();
                instructionRec.setId(UUIDGenerator.getUUID());
                instructionRec.setCreateTime(new Date());
                instructionRec.setStatus("-1");
                instructionRec.setSn((String) empEquipmentMap.get("sn"));
                instructionRec.setInstruction(CommonConstant.UPDATE_PASS_AUTH.getValue());
                int affectNum = instructionRecMapper.insertSelective(instructionRec);
                if (affectNum < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return -1;
                }
                JSONObject jsonObjSend = new JSONObject();
                jsonObjSend.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
                jsonObjSend.put("code", Integer.valueOf(CommonConstant.UPDATE_PASS_AUTH.getValue()));
                jsonObjSend.put("id", instructionRec.getId());
                jsonObjSend.put("sendTime", new Date().getTime());
                //发送指令
                websocket.sendMessageToUser((String) empEquipmentMap.get("sn"), new TextMessage(jsonObjSend.toString()));
            }
        }
        return accessAuthMapper.deleteByIds(ids);
    }

    @Override
    public int selectCountById(String id) throws Exception {
        return empEquipmentMapper.selectCountById(id);
    }

    @Override
    public int selectCountByAuthName(String authName) throws Exception {
        return accessAuthMapper.selectCountByAuthName(authName);
    }

    @Override
    public int selectCountByAuthNameExceptOwn(String id, String authName) throws Exception {
        return accessAuthMapper.selectCountByAuthNameExceptOwn(id, authName);
    }


    @Override
    public List<AuthVO> selectAllEmpAuth() throws Exception {
        return accessAuthMapper.selectAllEmpAuth();
    }

    @Override
    public List<AuthVO> selectAuthByDeptId(String deptId) throws Exception {
        return accessAuthMapper.selectAuthByDeptId(deptId);
    }

    @Override
    public List<AuthVO> selectEmpAuthByParams(String searchParam) throws Exception {
        return accessAuthMapper.selectEmpAuthByParams(searchParam);
    }


    @Override
    public List<AccessTimeVO> selectTimezoneById(String id) throws Exception {
        return accessAuthMapper.selectTimezoneById(id);
    }

    /**
     * @Description TODO
     * @Author Capejor
     * @Date 2019-06-18 18:50
     **/
    @Override
    public List<Map<String, Object>> selectEmpAuthBySn(String sn) throws Exception {
        List<EmpAuthServer> authVOList = accessAuthMapper.selectEmpAuthBySn(sn);
        List<Map<String, Object>> empAuthList = new ArrayList<>();
        for (EmpAuthServer empAuth : authVOList) {
            Map<String, Object> empAuthMap = new HashMap<>();
            List<String> timeList = new ArrayList<>();
            timeList.add(empAuth.getTimeOne());
            timeList.add(empAuth.getTimeTwo());
            timeList.add(empAuth.getTimeThree());
            empAuthMap.put("id", empAuth.getId());
            empAuthMap.put("name", empAuth.getName());
            empAuthMap.put("sn", empAuth.getSn());
            empAuthMap.put("timeZone", timeList);
            empAuthList.add(empAuthMap);
        }
        return empAuthList;
    }

    /**
     * @Description 启用禁用
     * @Author Capejor
     * @Date 2019-06-18 19:43
     **/
    @Override
    public int updateStatus(String id, String status) throws Exception {
        String ids[] = {id};
        List<Map<String, Object>> empEqupmentLst = empEquipmentMapper.selectBatchEmpEquipmentByAuthId(ids);
        if (empEqupmentLst != null && empEqupmentLst.size() > 0) {
            for (Map<String, Object> empEquipmentMap : empEqupmentLst) {
                // 新增指令发送记录 发送socket消息给设备端
                InstructionRec instructionRec = new InstructionRec();
                instructionRec.setId(UUIDGenerator.getUUID());
                instructionRec.setCreateTime(new Date());
                instructionRec.setStatus("-1");
                instructionRec.setSn((String) empEquipmentMap.get("sn"));
                instructionRec.setInstruction(CommonConstant.UPDATE_PASS_AUTH.getValue());
                int affectNum = instructionRecMapper.insertSelective(instructionRec);
                if (affectNum < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return -1;
                }
                JSONObject jsonObjSend = new JSONObject();
                jsonObjSend.put("type", Integer.valueOf(CommonConstant.TYPE.getValue()));
                jsonObjSend.put("code", Integer.valueOf(CommonConstant.UPDATE_PASS_AUTH.getValue()));
                jsonObjSend.put("id", instructionRec.getId());
                jsonObjSend.put("sendTime", new Date().getTime());
                //发送指令
                websocket.sendMessageToUser((String) empEquipmentMap.get("sn"), new TextMessage(jsonObjSend.toString()));
            }
        }
        return accessAuthMapper.updateStatus(id, status);
    }
}
